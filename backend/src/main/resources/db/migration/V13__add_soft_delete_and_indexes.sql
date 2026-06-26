-- V13__add_soft_delete_and_indexes.sql

-- 1. Add Soft Delete (is_deleted) to critical tables
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE items ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

-- 2. Create performance indexes
-- User lookup by email is very common during login/auth
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Token lookup is common during auth filter chain
CREATE INDEX IF NOT EXISTS idx_user_device_tokens_token ON user_device_tokens(token);

-- Booking lookups by status are very common (e.g. active bookings)
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);
