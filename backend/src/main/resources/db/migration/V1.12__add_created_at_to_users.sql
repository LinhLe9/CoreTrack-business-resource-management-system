-- Add created_at column to users table
ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add index for better performance
CREATE INDEX idx_users_created_at ON users(created_at); 