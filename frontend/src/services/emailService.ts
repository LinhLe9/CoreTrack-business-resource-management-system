import apiClient from '../lib/axios';

export interface EmailAlertConfig {
  lowStockEnabled: boolean;
  overStockEnabled: boolean;
  outOfStockEnabled: boolean;
  ticketStatusChangeEnabled: boolean;
  recipientEmails: string[] | null;
}

// Email Alert Configuration
export const getEmailAlertConfig = async (): Promise<EmailAlertConfig> => {
  const response = await apiClient.get('/email/config');
  return response.data;
};

export const updateEmailAlertConfig = async (config: EmailAlertConfig): Promise<EmailAlertConfig> => {
  const response = await apiClient.put('/email/config', config);
  return response.data;
};

// Test Email
export const sendTestEmail = async (toEmail: string): Promise<void> => {
  const payload = { toEmail };
  await apiClient.post('/email/test', payload);
};

export const emailService = {
  getEmailAlertConfig,
  updateEmailAlertConfig,
  sendTestEmail,
}; 