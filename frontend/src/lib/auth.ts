// Utility functions for authentication

const isClient = typeof window !== 'undefined';

export const logout = (message?: string) => {
  if (!isClient) return;
  
  console.log('logout function called with message:', message);
  console.log('Current pathname:', window.location.pathname);
  
  // Check if we're already on home page to avoid infinite redirects
  if (window.location.pathname === '/') {
    console.log('Already on home page, skipping redirect');
    return;
  }
  
  // Clear all auth-related data
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  sessionStorage.removeItem('token');
  sessionStorage.removeItem('user');
  
  // Clear any other auth-related items
  localStorage.removeItem('refreshToken');
  sessionStorage.removeItem('refreshToken');
  
  // Show message if provided
  if (message) {
    console.log('Dispatching session expired event with message:', message);
    // You can also dispatch a custom event for toast notification
    try {
      window.dispatchEvent(new CustomEvent('sessionExpired', { detail: { message } }));
    } catch (error) {
      console.error('Error dispatching session expired event:', error);
    }
  }
  
  // Redirect to home page
  console.log('Redirecting to home page...');
  window.location.href = '/';
};

export const isAuthenticated = (): boolean => {
  if (!isClient) return false;
  
  try {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (!token) return false;
    
    // Basic JWT validation - check if token is not expired
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      
      if (payload.exp && payload.exp < currentTime) {
        // Token is expired, clear it and return false
        console.log('Token is expired, clearing...');
        clearAuthData();
        return false;
      }
      
      return true;
    } catch (error) {
      console.error('Error parsing JWT token:', error);
      // Invalid token format, clear it
      clearAuthData();
      return false;
    }
  } catch (error) {
    console.error('Error checking authentication:', error);
    return false;
  }
};

export const getToken = (): string | null => {
  if (!isClient) return null;
  
  try {
    return localStorage.getItem('token') || sessionStorage.getItem('token');
  } catch (error) {
    console.error('Error getting token:', error);
    return null;
  }
};

export const setToken = (token: string): void => {
  if (!isClient) return;
  
  try {
    localStorage.setItem('token', token);
  } catch (error) {
    console.error('Error setting token:', error);
  }
};

export const clearAuthData = (): void => {
  if (!isClient) return;
  
  try {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('refreshToken');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    sessionStorage.removeItem('refreshToken');
  } catch (error) {
    console.error('Error clearing auth data:', error);
  }
};

export const checkTokenExpiration = (): boolean => {
  if (!isClient) return false;
  
  try {
    const token = getToken();
    if (!token) return false;
    
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Math.floor(Date.now() / 1000);
    
    // Check if token expires within the next 5 minutes
    const timeUntilExpiry = payload.exp - currentTime;
    const fiveMinutes = 5 * 60;
    
    if (timeUntilExpiry <= fiveMinutes && timeUntilExpiry > 0) {
      console.log('Token will expire soon, warning user...');
      return true; // Token will expire soon
    }
    
    return false;
  } catch (error) {
    console.error('Error checking token expiration:', error);
    return false;
  }
}; 