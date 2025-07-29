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
  if (!value) return 'N/A';
  const parsed = parseFloat(value);
  return isNaN(parsed) ? 'N/A' : parsed.toFixed(2);
};

/**
 * Convert number to string for sending to backend as BigDecimal
 * @param value - The number to convert
 * @returns String representation for BigDecimal
 */
export const toBigDecimalString = (value: number): string => {
  return value.toFixed(2);
}; 