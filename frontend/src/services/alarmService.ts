import apiClient from '../lib/axios';
import { AlarmResponse, AlarmQueryParams, ProductAlarm, MaterialAlarm } from '../types/alarm';

// Get product alarms
export const getProductAlarms = async (params: AlarmQueryParams = {}): Promise<AlarmResponse> => {
  const response = await apiClient.get('/alarms/products', { params });
  return response.data;
};

// Get material alarms
export const getMaterialAlarms = async (params: AlarmQueryParams = {}): Promise<AlarmResponse> => {
  const response = await apiClient.get('/alarms/materials', { params });
  return response.data;
};

// Resolve alarm
export const resolveAlarm = async (alarmId: number, alarmType: 'product' | 'material'): Promise<void> => {
  await apiClient.put(`/alarms/${alarmType}/${alarmId}/resolve`);
};

// Get alarm statistics
export const getAlarmStatistics = async (): Promise<{
  totalProductAlarms: number;
  totalMaterialAlarms: number;
  criticalAlarms: number;
  unresolvedAlarms: number;
}> => {
  const response = await apiClient.get('/alarms/statistics');
  return response.data;
}; 