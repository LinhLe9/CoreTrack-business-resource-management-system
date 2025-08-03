import apiClient from '../lib/axios';

export interface NotificationResponse {
  id: number;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  readAt: string | null;
  productInventoryId: number | null;
  productName: string | null;
  productSku: string | null;
  productImageUrl: string | null;
  materialInventoryId: number | null;
  materialName: string | null;
  materialSku: string | null;
  materialImageUrl: string | null;
}

export interface NotificationPage {
  content: NotificationResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// Get user notifications with pagination
export const getUserNotifications = async (page: number = 0, size: number = 20): Promise<NotificationPage> => {
  const response = await apiClient.get(`/notifications?page=${page}&size=${size}`);
  return response.data;
};

// Get unread notifications count
export const getUnreadCount = async (): Promise<number> => {
  console.log('getUnreadCount - Starting API call');
  console.log('getUnreadCount - isClient:', typeof window !== 'undefined');
  console.log('getUnreadCount - apiClient instance:', apiClient);
  console.log('getUnreadCount - apiClient.defaults:', apiClient.defaults);
  console.log('getUnreadCount - Client instance ID:', apiClient.defaults.headers.common['X-Client-Instance']);
  
  const response = await apiClient.get('/notifications/unread-count');
  console.log('getUnreadCount - Response received:', response.status);
  return response.data;
};

// Mark notification as read
export const markAsRead = async (notificationId: number): Promise<void> => {
  await apiClient.post(`/notifications/${notificationId}/mark-read`);
};

// Mark all notifications as read
export const markAllAsRead = async (): Promise<void> => {
  await apiClient.post('/notifications/mark-all-read');
};

// Delete notification
export const deleteNotification = async (notificationId: number): Promise<void> => {
  await apiClient.delete(`/notifications/${notificationId}`);
};

// Get notifications by type
export const getNotificationsByType = async (type: string): Promise<NotificationResponse[]> => {
  const response = await apiClient.get(`/notifications/by-type?type=${type}`);
  return response.data;
}; 