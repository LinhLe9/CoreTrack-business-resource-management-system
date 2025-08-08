/**
 * Utility functions for handling BigDecimal values from backend
 */

/**
 * Safely parse a BigDecimal string to number
 * @param value - The BigDecimal string from backend
 * @returns Parsed number or 0 if invalid
 */
export const parseBigDecimal = (value: string | null | undefined): number => {
  if (!value) return 0;
  const parsed = parseFloat(value);
  return isNaN(parsed) ? 0 : parsed;
};

/**
 * Format a BigDecimal string to display with 2 decimal places
 * @param value - The BigDecimal string from backend
 * @returns Formatted string or 'N/A' if null/undefined
 */
export const formatBigDecimal = (value: string | null | undefined): string => {
  console.log('=== formatBigDecimal DEBUG ===');
  console.log('Input value:', value);
  console.log('Input type:', typeof value);
  console.log('Is null:', value === null);
  console.log('Is undefined:', value === undefined);
  console.log('Is empty string:', value === '');
  console.log('Is "0":', value === '0');
  console.log('Is "0.00":', value === '0.00');
  console.log('=============================');
  
  if (!value) return 'N/A';
  const parsed = parseFloat(value);
  console.log('Parsed value:', parsed);
  console.log('Is NaN:', isNaN(parsed));
  const result = isNaN(parsed) ? 'N/A' : parsed.toFixed(2);
  console.log('Final result:', result);
  console.log('=============================');
  return result;
};

/**
 * Convert number to string for sending to backend as BigDecimal
 * @param value - The number to convert
 * @returns String representation for BigDecimal
 */
export const toBigDecimalString = (value: number): string => {
  return value.toFixed(2);
};



// Helper function to ensure error is always a string
export const getErrorMessage = (error: any): string => {
  if (typeof error === 'string') return error;
  if (error?.message) return error.message;
  if (error?.response?.data?.message) return error.response.data.message;
  if (error?.response?.data) {
    const data = error.response.data;
    return typeof data === 'string' ? data : JSON.stringify(data);
  }
  return 'An error occurred';
}; 