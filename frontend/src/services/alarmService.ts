import apiClient from '../lib/axios';
import { AlarmResponse, AlarmQueryParams, ProductAlarm, MaterialAlarm } from '../types/alarm';

// Get product alarms
export const getProductAlarms = async (params: AlarmQueryParams = {}): Promise<AlarmResponse> => {
  const response = await apiClient.get('/product-inventory/alarm/filter', { params });
  return response.data;
};

// Get material alarms
export const getMaterialAlarms = async (params: AlarmQueryParams = {}): Promise<AlarmResponse> => {
  const response = await apiClient.get('/material-inventory/alarm/filter', { params });
  return response.data;
};

// Resolve alarm - TODO: Implement when backend endpoint is available
export const resolveAlarm = async (alarmId: number, alarmType: 'product' | 'material'): Promise<void> => {
  // TODO: Implement when backend endpoint is available
  console.warn('Resolve alarm endpoint not implemented yet');
  throw new Error('Resolve alarm endpoint not implemented yet');
};

// Get alarm statistics - TODO: Implement when backend endpoint is available
export const getAlarmStatistics = async (): Promise<{
  totalProductAlarms: number;
  totalMaterialAlarms: number;
  criticalAlarms: number;
  unresolvedAlarms: number;
}> => {
  // TODO: Implement when backend endpoint is available
  console.warn('Alarm statistics endpoint not implemented yet');
  throw new Error('Alarm statistics endpoint not implemented yet');
}; 