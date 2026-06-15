-- Create Enum Types
DO $$ BEGIN
    CREATE TYPE target_role AS ENUM ('RENTER', 'OWNER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE review_status AS ENUM ('PENDING', 'PUBLISHED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 1. Alter item_reviews
DO $$ BEGIN
    ALTER TABLE item_reviews RENAME COLUMN renter_id TO reviewer_id;
EXCEPTION
    WHEN undefined_column THEN null;
END $$;

DO $$ BEGIN
    ALTER TABLE item_reviews ADD COLUMN status review_status NOT NULL DEFAULT 'PENDING';
EXCEPTION
    WHEN duplicate_column THEN null;
END $$;

ALTER TABLE item_reviews DROP CONSTRAINT IF EXISTS uq_item_review_per_booking;
ALTER TABLE item_reviews ADD CONSTRAINT uq_item_review_per_booking UNIQUE (booking_id, reviewer_id);

-- 2. Alter user_reviews
DO $$ BEGIN
    ALTER TABLE user_reviews RENAME COLUMN user_id TO target_user_id;
EXCEPTION
    WHEN undefined_column THEN null;
END $$;

DO $$ BEGIN
    ALTER TABLE user_reviews RENAME COLUMN trust_rating TO rating;
EXCEPTION
    WHEN undefined_column THEN null;
END $$;

DO $$ BEGIN
    ALTER TABLE user_reviews ADD COLUMN target_role target_role;
EXCEPTION
    WHEN duplicate_column THEN null;
END $$;

DO $$ BEGIN
    ALTER TABLE user_reviews ADD COLUMN status review_status NOT NULL DEFAULT 'PENDING';
EXCEPTION
    WHEN duplicate_column THEN null;
END $$;

ALTER TABLE user_reviews DROP CONSTRAINT IF EXISTS uq_user_review_per_booking;
ALTER TABLE user_reviews ADD CONSTRAINT uq_user_review_per_booking UNIQUE (booking_id, reviewer_id, target_user_id);

-- 3. Alter items
DO $$ BEGIN
    ALTER TABLE items ADD COLUMN rating DECIMAL(3, 2) DEFAULT 0.00;
EXCEPTION
    WHEN duplicate_column THEN null;
END $$;

DO $$ BEGIN
    ALTER TABLE items ADD COLUMN total_reviews INT DEFAULT 0;
EXCEPTION
    WHEN duplicate_column THEN null;
END $$;