import { getUnreadCount } from './notificationService';

class NotificationPollingService {
  private pollingInterval: NodeJS.Timeout | null = null;
  private isPolling = false;
  private onNotificationCallback: (() => void) | null = null;
  private lastUnreadCount = 0;
  private isAuthenticated = false;

  constructor() {
    // Don't start polling immediately - wait for authentication
    console.log('NotificationPollingService initialized - waiting for authentication');
  }

  private checkAuthentication() {
    if (typeof window === 'undefined') {
      console.log('checkAuthentication - Not client-side');
      return;
    }
    
    try {
      const token = localStorage.getItem('token');
      console.log('checkAuthentication - Token found:', !!token);
      if (token && !this.isPolling) {
        this.isAuthenticated = true;
        console.log('Starting polling after authentication check');
        this.startPolling();
      } else {
        console.log('No token or already polling');
      }
    } catch (error) {
      console.log('No authentication token found:', error);
    }
  }

  startPolling() {
    // Check if user is authenticated before starting
    if (typeof window === 'undefined') {
      console.log('startPolling - Not client-side');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      console.log('startPolling - No token found, skipping polling');
      return;
    }

    if (this.isPolling) {
      console.log('startPolling - Already polling');
      return;
    }
    
    console.log('Starting notification polling...');
    this.isPolling = true;
    this.pollingInterval = setInterval(async () => {
      // Double-check we're still on client-side and authenticated
      if (typeof window === 'undefined') {
        console.log('Polling interval - Not client-side, stopping');
        this.stopPolling();
        return;
      }

      const currentToken = localStorage.getItem('token');
      if (!currentToken) {
        console.log('Polling interval - No token found, stopping polling');
        this.stopPolling();
        return;
      }
      
      try {
        console.log('Polling - Making API call...');
        const currentUnreadCount = await getUnreadCount();
        
        // If unread count changed, trigger callback
        if (currentUnreadCount !== this.lastUnreadCount) {
          console.log('Unread count changed:', this.lastUnreadCount, '->', currentUnreadCount);
          this.lastUnreadCount = currentUnreadCount;
          
          if (this.onNotificationCallback) {
            this.onNotificationCallback();
          }
        }
      } catch (error: any) {
        // Only log error if it's not a 401 (unauthorized)
        if (error.response?.status !== 401) {
          console.error('Error polling notifications:', error);
        } else {
          console.log('Polling - 401 error, stopping polling');
          this.stopPolling();
        }
      }
    }, 10000); // Poll every 10 seconds
  }

  stopPolling() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
    this.isPolling = false;
    console.log('Polling stopped');
  }

  setNotificationCallback(callback: () => void) {
    this.onNotificationCallback = callback;
  }

  // For compatibility with existing code
  connect() {
    console.log('Connect called - checking authentication');
    this.checkAuthentication();
  }

  // Method to start polling after login
  startAfterLogin() {
    console.log('startAfterLogin called - starting polling');
    this.isAuthenticated = true;
    this.startPolling();
  }

  disconnect() {
    this.stopPolling();
  }

  reconnect() {
    console.log('Reconnect called');
    this.stopPolling();
    this.startPolling();
  }
}

// Create singleton instance
const notificationService = new NotificationPollingService();

export default notificationService; 