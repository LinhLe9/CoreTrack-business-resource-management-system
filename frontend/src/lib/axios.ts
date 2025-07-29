import axios from 'axios';
import { logout } from './auth';

const isClient = typeof window !== 'undefined';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api',  
  withCredentials: true, 
});

// Flag to prevent infinite logout loops
let isLoggingOut = false;

// Function to reset the logout flag
export const resetLogoutFlag = () => {
  isLoggingOut = false;
};

api.interceptors.request.use((config) => {
  if (isClient) {
    try {
      const token = localStorage.getItem("token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch (error) {
      console.error('Error setting authorization header:', error);
    }
  }
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