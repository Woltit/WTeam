-- Create Enum Types
CREATE TYPE target_role AS ENUM ('RENTER', 'OWNER');
CREATE TYPE review_status AS ENUM ('PENDING', 'PUBLISHED');

-- 1. Alter item_reviews
ALTER TABLE item_reviews
    RENAME COLUMN renter_id TO reviewer_id;

ALTER TABLE item_reviews
    ADD COLUMN status review_status NOT NULL DEFAULT 'PENDING';

ALTER TABLE item_reviews DROP CONSTRAINT IF EXISTS uq_item_review_per_booking;
ALTER TABLE item_reviews ADD CONSTRAINT uq_item_review_per_booking UNIQUE (booking_id, reviewer_id);

-- 2. Alter user_reviews
ALTER TABLE user_reviews
    RENAME COLUMN user_id TO target_user_id;

ALTER TABLE user_reviews
    RENAME COLUMN trust_rating TO rating;

ALTER TABLE user_reviews
    ADD COLUMN target_role target_role;

ALTER TABLE user_reviews
    ADD COLUMN status review_status NOT NULL DEFAULT 'PENDING';

ALTER TABLE user_reviews DROP CONSTRAINT IF EXISTS uq_user_review_per_booking;
ALTER TABLE user_reviews ADD CONSTRAINT uq_user_review_per_booking UNIQUE (booking_id, reviewer_id, target_user_id);

-- 3. Alter items
ALTER TABLE items
    ADD COLUMN rating DECIMAL(3, 2) DEFAULT 0.00,
    ADD COLUMN total_reviews INT DEFAULT 0;