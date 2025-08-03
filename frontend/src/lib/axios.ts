import axios from 'axios';
import { logout } from './auth';

const isClient = typeof window !== 'undefined';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api',  
  withCredentials: true, 
});

// Add unique identifier to track this instance
api.defaults.headers.common['X-Client-Instance'] = 'frontend-client';
console.log('Creating axios instance with ID:', api.defaults.headers.common['X-Client-Instance']);

// Flag to prevent infinite logout loops
let isLoggingOut = false;

// Function to reset the logout flag
export const resetLogoutFlag = () => {
  isLoggingOut = false;
};

api.interceptors.request.use((config) => {
  console.log('=== REQUEST INTERCEPTOR START ===');
  console.log('isClient:', isClient);
  console.log('Request URL:', config.url);
  console.log('Request method:', config.method);
  console.log('Client instance ID:', config.headers['X-Client-Instance']);
  console.log('Request headers before:', config.headers);
  
  if (isClient) {
    try {
      const token = localStorage.getItem("token");
      console.log('Token found:', !!token);
      console.log('Token value:', token ? token.substring(0, 20) + '...' : 'null');
      
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('Authorization header set to:', `Bearer ${token.substring(0, 20)}...`);
      } else {
        console.log('No token found in localStorage');
      }
    } catch (error) {
      console.error('Error setting authorization header:', error);
    }
  } else {
    console.log('Not client-side - skipping token setup');
  }
  
  console.log('Request headers after:', config.headers);
  console.log('=== REQUEST INTERCEPTOR END ===');
  return config;
});

// Response interceptor to handle token expiration
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Check if this is not already a login/logout request to avoid infinite loops
    const isAuthRequest = error.config?.url?.includes('/login') || 
                         error.config?.url?.includes('/logout') ||
                         error.config?.url?.includes('/register');
    
    if (!isAuthRequest && !isLoggingOut && isClient) {
      // Handle 401 (Unauthorized) or 403 (Forbidden) errors
      if (error.response?.status === 401 || error.response?.status === 403) {
        console.log('Token expired - status code:', error.response?.status);
        console.log('Current pathname:', window.location.pathname);
        console.log('isLoggingOut flag:', isLoggingOut);
        isLoggingOut = true;
        logout('Your session has expired. Please login again.');
        return Promise.reject(error);
      }
      
      // Handle JWT expired errors from response body
      const errorMessage = error.response?.data?.message || error.response?.data?.error || '';
      const isJwtExpired = errorMessage.toLowerCase().includes('jwt expired') || 
                           errorMessage.toLowerCase().includes('token expired') ||
                           errorMessage.toLowerCase().includes('expiredjwt') ||
                           errorMessage.toLowerCase().includes('expired jwt');
      
      if (isJwtExpired) {
        console.log('JWT expired detected from response:', errorMessage);
        isLoggingOut = true;
        logout('Your session has expired. Please login again.');
        return Promise.reject(error);
      }
      
      // Handle other authentication errors
      if (error.response?.status === 400 && errorMessage.toLowerCase().includes('invalid token')) {
        console.log('Invalid token detected:', errorMessage);
        isLoggingOut = true;
        logout('Invalid session. Please login again.');
        return Promise.reject(error);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;