import { createClient } from '@supabase/supabase-js';

// Supabase configuration
const supabaseUrl = 'https://vnhabbwfymisxmrezqrh.supabase.co';
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZuaGFiYndmeW1pc3htcmV6cXJoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTM0NTA4ODAsImV4cCI6MjA2OTAyNjg4MH0.w-GK9rDgD6CTVqgJkzURMyBDYHTBY7LsM_SjGD2bU1w';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
