-- Add created_by column to users table
ALTER TABLE users ADD COLUMN created_by BIGINT;

-- Add foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_created_by 
    FOREIGN KEY (created_by) REFERENCES users(id);

-- Add index for better performance
CREATE INDEX idx_users_created_by ON users(created_by); 