'use client';

import React, { useState, useEffect } from 'react';
import { Bell, X, Check, Trash2 } from 'lucide-react';
import { getUserNotifications, getUnreadCount, markAsRead, markAllAsRead, deleteNotification, NotificationResponse } from '@/services/notificationService';
import notificationPollingService from '@/services/websocketService';
import { formatDistanceToNow } from 'date-fns';

interface NotificationBellProps {
  className?: string;
}

export default function NotificationBell({ className = '' }: NotificationBellProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [dropdownPosition, setDropdownPosition] = useState({ top: 0, right: 0 });

  // Calculate dropdown position
  const calculateDropdownPosition = () => {
    if (typeof window === 'undefined') return;
    
    const button = document.querySelector('[data-notification-button]') as HTMLElement;
    if (button) {
      const rect = button.getBoundingClientRect();
      setDropdownPosition({
        top: rect.bottom + 8, // 8px gap
        right: window.innerWidth - rect.right
      });
    }
  };

  const handleToggleDropdown = () => {
    if (!isOpen) {
      calculateDropdownPosition();
    }
    setIsOpen(!isOpen);
  };

  // Fetch unread count
  const fetchUnreadCount = async () => {
    // Only fetch on client-side and when authenticated
    if (typeof window === 'undefined') return;
    
    try {
      const token = localStorage.getItem('token');
      if (!token) return; // Don't fetch if not authenticated
      
      const count = await getUnreadCount();
      setUnreadCount(count);
    } catch (error) {
      console.error('Error fetching unread count:', error);
    }
  };

  // Fetch notifications
  const fetchNotifications = async () => {
    // Only fetch on client-side and when authenticated
    if (typeof window === 'undefined') return;
    
    try {
      const token = localStorage.getItem('token');
      if (!token) return; // Don't fetch if not authenticated
      
      setLoading(true);
      const response = await getUserNotifications(page, 10);
      setNotifications(response.content);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  // Mark notification as read
  const handleMarkAsRead = async (notificationId: number) => {
    try {
      await markAsRead(notificationId);
      setNotifications(prev => 
        prev.map(notif => 
          notif.id === notificationId 
            ? { ...notif, isRead: true, readAt: new Date().toISOString() }
            : notif
        )
      );
      fetchUnreadCount();
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  // Mark all as read
  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      setNotifications(prev => 
        prev.map(notif => ({ ...notif, isRead: true, readAt: new Date().toISOString() }))
      );
      fetchUnreadCount();
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  // Delete notification
  const handleDeleteNotification = async (notificationId: number) => {
    try {
      await deleteNotification(notificationId);
      setNotifications(prev => prev.filter(notif => notif.id !== notificationId));
      fetchUnreadCount();
    } catch (error) {
      console.error('Error deleting notification:', error);
    }
  };

  // Get notification icon based on type
  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'INVENTORY_OUT_OF_STOCK':
        return '🔴';
      case 'INVENTORY_LOW_STOCK':
        return '🟡';
      case 'INVENTORY_OVER_STOCK':
        return '🟠';
      default:
        return '📢';
    }
  };

  // Get notification color based on type
  const getNotificationColor = (type: string) => {
    switch (type) {
      case 'INVENTORY_OUT_OF_STOCK':
        return 'border-red-200 bg-red-50';
      case 'INVENTORY_LOW_STOCK':
        return 'border-yellow-200 bg-yellow-50';
      case 'INVENTORY_OVER_STOCK':
        return 'border-orange-200 bg-orange-50';
      default:
        return 'border-blue-200 bg-blue-50';
    }
  };

  useEffect(() => {
    // Only run on client-side
    if (typeof window === 'undefined') {
      console.log('NotificationBell useEffect - Not client-side');
      return;
    }
    
    // Check if user is authenticated
    const token = localStorage.getItem('token');
    console.log('NotificationBell useEffect - Token found:', !!token);
    console.log('NotificationBell useEffect - Token value:', token ? token.substring(0, 20) + '...' : 'null');
    
    if (token) {
      console.log('Starting notification polling with token');
      fetchUnreadCount();
      
      // Set up polling callback for real-time notifications
      notificationPollingService.setNotificationCallback(() => {
        console.log('Notification callback triggered');
        fetchUnreadCount();
        if (isOpen) {
          fetchNotifications();
        }
      });

      // Start polling after login
      notificationPollingService.startAfterLogin();

      // Polling fallback (every 30 seconds)
      const interval = setInterval(() => {
        // Double-check we're still on client-side
        if (typeof window === 'undefined') {
          console.log('Fallback polling - Not client-side, clearing interval');
          clearInterval(interval);
          return;
        }
        console.log('Fallback polling triggered');
        fetchUnreadCount();
      }, 30000);
      
      return () => {
        console.log('NotificationBell useEffect cleanup - clearing interval');
        clearInterval(interval);
      };
    } else {
      console.log('No token found, skipping notification setup');
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      fetchNotifications();
    }
  }, [isOpen, page]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      if (isOpen && !target.closest('[data-notification-button]') && !target.closest('[data-notification-dropdown]')) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      // Add event listener with a small delay to avoid immediate closing
      const timeoutId = setTimeout(() => {
        document.addEventListener('mousedown', handleClickOutside);
      }, 100);

      return () => {
        clearTimeout(timeoutId);
        document.removeEventListener('mousedown', handleClickOutside);
      };
    }
  }, [isOpen]);

  return (
    <div className={`relative ${className}`}>
      {/* Bell Icon with Badge */}
      <button
        onClick={handleToggleDropdown}
        className="relative p-2 text-gray-600 hover:text-gray-900 transition-colors rounded-lg hover:bg-gray-100"
        style={{ display: 'flex', alignItems: 'center', gap: '4px' }}
        data-notification-button
      >
        <Bell size={20} />
        {unreadCount > 0 && (
          <span 
            style={{ 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center',
              backgroundColor: '#ef4444', // red-500
              color: 'white',
              fontSize: '12px',
              borderRadius: '50%',
              height: '20px',
              width: '20px',
              minWidth: '20px',
              fontWeight: '500'
            }}
          >
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {/* Notification Dropdown */}
      {isOpen && (
        <div 
          className="fixed top-16 right-4 w-96 bg-white border border-gray-200 rounded-lg shadow-xl z-[9999] max-h-96 overflow-y-auto"
          style={{ 
            position: 'fixed',
            top: dropdownPosition.top,
            right: dropdownPosition.right,
            zIndex: 9999,
            backgroundColor: 'white',
            border: '1px solid #e5e7eb',
            borderRadius: '8px',
            boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25), 0 0 0 1px rgba(0, 0, 0, 0.05)',
            maxHeight: '384px',
            overflowY: 'auto'
          }}
          data-notification-dropdown
        >
          {/* Header */}
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'space-between', 
            padding: '16px',
            borderBottom: '1px solid #e5e7eb'
          }}>
            <h3 style={{ 
              fontSize: '18px', 
              fontWeight: 'bold', 
              color: '#111827',
              margin: 0
            }}>
              Notifications
            </h3>
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '8px'
            }}>
              {unreadCount > 0 && (
                <button
                  onClick={handleMarkAllAsRead}
                  style={{ 
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px',
                    fontSize: '12px',
                    color: '#2563eb',
                    fontWeight: '500',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    padding: '4px 8px',
                    borderRadius: '4px'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.color = '#1d4ed8';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.color = '#2563eb';
                  }}
                >
                  <Check size={12} />
                  <span>Mark all read</span>
                </button>
              )}
              <button
                onClick={() => setIsOpen(false)}
                style={{ 
                  color: '#9ca3af',
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  padding: '4px'
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.color = '#6b7280';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.color = '#9ca3af';
                }}
              >
                <X size={16} />
              </button>
            </div>
          </div>
          
          {/* Separator line */}
          <div style={{ 
            borderBottom: '1px solid #e5e7eb',
            height: '1px'
          }}></div>

          {/* Notifications List */}
          <div className="p-2">
            {loading ? (
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
              </div>
            ) : notifications.length === 0 ? (
              <div className="text-center py-8 text-gray-500">
                No notifications
              </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                 {notifications.map((notification, index) => (
                   <React.Fragment key={notification.id}>
                                           <div
                        className={`rounded-lg border ${getNotificationColor(notification.type)} ${
                          !notification.isRead ? 'ring-2 ring-blue-200' : ''
                        }`}
                        style={{ 
                          padding: '12px 16px',
                          margin: '0 8px'
                        }}
                      >
                     {/* First row: Icon + Title + Buttons */}
                     <div style={{ 
                       display: 'flex', 
                       alignItems: 'center', 
                       justifyContent: 'space-between',
                       marginBottom: '8px'
                     }}>
                    <div style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          gap: '6px'
                        }}>
                          <span style={{ fontSize: '14px' }}>{getNotificationIcon(notification.type)}</span>
                          <h4 style={{ 
                            fontSize: '12px', 
                            fontWeight: '500', 
                            color: '#111827',
                            margin: 0
                          }}>
                            {notification.title}
                          </h4>
                        </div>
                       <div style={{ 
                         display: 'flex', 
                         alignItems: 'center', 
                         gap: '4px'
                       }}>
                         {!notification.isRead && (
                           <button
                             onClick={() => handleMarkAsRead(notification.id)}
                             style={{ 
                               color: '#9ca3af',
                               background: 'none',
                               border: 'none',
                               cursor: 'pointer',
                               padding: '4px',
                               borderRadius: '4px'
                             }}
                             onMouseEnter={(e) => {
                               e.currentTarget.style.color = '#16a34a';
                             }}
                             onMouseLeave={(e) => {
                               e.currentTarget.style.color = '#9ca3af';
                             }}
                             title="Mark as read"
                           >
                             <Check size={14} />
                           </button>
                         )}
                         <button
                           onClick={() => handleDeleteNotification(notification.id)}
                           style={{ 
                             color: '#9ca3af',
                             background: 'none',
                             border: 'none',
                             cursor: 'pointer',
                             padding: '4px',
                             borderRadius: '4px'
                           }}
                           onMouseEnter={(e) => {
                             e.currentTarget.style.color = '#dc2626';
                           }}
                           onMouseLeave={(e) => {
                             e.currentTarget.style.color = '#9ca3af';
                           }}
                           title="Delete"
                         >
                           <Trash2 size={14} />
                         </button>
                       </div>
                     </div>
                     
                     {/* Second row: Product/Material info */}
                      {notification.productName && (
                        <p style={{ 
                          fontSize: '12px', 
                          color: '#6b7280', 
                          margin: '0 0 6px 0'
                        }}>
                          Product: {notification.productName} ({notification.productSku})
                        </p>
                      )}
                      {notification.materialName && (
                        <p style={{ 
                          fontSize: '12px', 
                          color: '#6b7280', 
                          margin: '0 0 6px 0'
                        }}>
                          Material: {notification.materialName} ({notification.materialSku})
                        </p>
                      )}
                      
                      {/* Third row: Message */}
                      <p style={{ 
                        fontSize: '14px', 
                        color: '#4b5563', 
                        margin: '0 0 6px 0',
                        lineHeight: '1.4'
                      }}>
                        {notification.message}
                      </p>
                      
                      {/* Fourth row: Timestamp */}
                      <p style={{ 
                        fontSize: '12px', 
                        color: '#9ca3af',
                        margin: 0
                      }}>
                      {formatDistanceToNow(new Date(notification.createdAt), { addSuffix: true })}
                     </p>
                   </div>
                   {index < notifications.length - 1 && (
                     <div style={{ 
                       borderBottom: '1px solid #e5e7eb',
                       height: '1px',
                       margin: '0 8px'
                     }}></div>
                   )}
                 </React.Fragment>
               ))}
             </div>
            )}
          </div>

          {/* Separator line before pagination */}
           {notifications.length > 0 && (
             <div style={{ 
               borderBottom: '1px solid #e5e7eb',
               height: '1px',
               margin: '0 16px'
             }}></div>
           )}
           
           {/* Pagination */}
           {notifications.length > 0 && (
             <div style={{ 
               display: 'flex', 
               alignItems: 'center', 
               justifyContent: 'center',
               padding: '16px',
               gap: '16px'
             }}>
               <button
                 onClick={() => setPage(Math.max(0, page - 1))}
                 disabled={page === 0}
                 style={{ 
                   fontSize: '14px',
                   color: page === 0 ? '#9ca3af' : '#2563eb',
                   background: 'none',
                   border: 'none',
                   cursor: page === 0 ? 'not-allowed' : 'pointer',
                   padding: '4px 8px',
                   borderRadius: '4px'
                 }}
                 onMouseEnter={(e) => {
                   if (page !== 0) {
                     e.currentTarget.style.color = '#1d4ed8';
                   }
                 }}
                 onMouseLeave={(e) => {
                   if (page !== 0) {
                     e.currentTarget.style.color = '#2563eb';
                   }
                 }}
               >
                 Previous
               </button>
               <span style={{ 
                 fontSize: '14px', 
                 color: '#6b7280',
                 fontWeight: '500'
               }}>
                 Page {page + 1}
               </span>
               <button
                 onClick={() => setPage(page + 1)}
                 disabled={notifications.length < 10}
                 style={{ 
                   fontSize: '14px',
                   color: notifications.length < 10 ? '#9ca3af' : '#2563eb',
                   background: 'none',
                   border: 'none',
                   cursor: notifications.length < 10 ? 'not-allowed' : 'pointer',
                   padding: '4px 8px',
                   borderRadius: '4px'
                 }}
                 onMouseEnter={(e) => {
                   if (notifications.length >= 10) {
                     e.currentTarget.style.color = '#1d4ed8';
                   }
                 }}
                 onMouseLeave={(e) => {
                   if (notifications.length >= 10) {
                     e.currentTarget.style.color = '#2563eb';
                   }
                 }}
               >
                 Next
               </button>
             </div>
           )}
        </div>
      )}
    </div>
  );
} 