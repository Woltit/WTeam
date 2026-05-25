-- ================================================
-- RentGo — повна міграція БД
-- ================================================

-- ================================================
-- 1. Створення ENUM типів
-- ================================================
CREATE TYPE role                    AS ENUM
    ('ADMIN', 'MODER', 'USER');
CREATE TYPE item_condition          AS ENUM
    ('IDEAL', 'GOOD', 'NORM', 'BAD', 'NEEDS_REPAIRING');
CREATE TYPE renting_status          AS ENUM
    ('ACTIVE', 'RENTED', 'INACTIVE', 'DELETED');
CREATE TYPE verification_status     AS ENUM
    ('UNVERIFIED', 'PENDING', 'VERIFIED', 'REJECTED');
CREATE TYPE booking_status          AS ENUM
    ('PENDING', 'APPROVED', 'REJECTED', 'PAID', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'DISPUTE');
CREATE TYPE transaction_type        AS ENUM
    ('RENT_PAYMENT', 'DEPOSIT_HOLD', 'DEPOSIT_REFUND', 'COMPENSATION');
CREATE TYPE transaction_status      AS ENUM
    ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED');
CREATE TYPE document_type           AS ENUM
    ('PASSPORT', 'ID_CARD');
CREATE TYPE notification_channel    AS ENUM
    ('EMAIL', 'PUSH', 'IN_APP');
CREATE TYPE notification_type       AS ENUM (
    'BOOKING_REQUEST', 'BOOKING_APPROVED', 'BOOKING_REJECTED',
    'BOOKING_CANCELLED', 'PAYMENT_RECEIVED', 'REVIEW_LEFT',
    'VERIFICATION_APPROVED', 'VERIFICATION_REJECTED', 'DISPUTE_OPENED'
);
CREATE TYPE delivery_method         AS ENUM
    ('SELF_PICKUP', 'DELIVERY');
CREATE TYPE delivery_status         AS ENUM
    ('PENDING', 'SENT', 'DELIVERED', 'RETURNED');
CREATE TYPE dispute_status          AS ENUM
    ('OPEN', 'UNDER_REVIEW', 'RESOLVED', 'CLOSED');
CREATE TYPE dispute_reason          AS ENUM
    ('ITEM_DAMAGED', 'ITEM_NOT_RETURNED', 'PAYMENT_ISSUE', 'OTHER');
CREATE TYPE auth_provider          AS ENUM
    ('LOCAL', 'GOOGLE', 'APPLE');

-- ================================================
-- USERS
-- ================================================
CREATE TABLE users (
    id              BIGSERIAL       PRIMARY KEY,
    email           VARCHAR(255)    UNIQUE NOT NULL,
    password        VARCHAR(255),
    auth_provider   auth_provider   NOT NULL
        DEFAULT 'LOCAL',
    role            role            NOT NULL
        DEFAULT 'USER',
    is_active       BOOLEAN         NOT NULL
        DEFAULT TRUE,
    blocked_at      TIMESTAMP,
    blocked_by_id   BIGINT,
    block_reason    TEXT,
    created_at      TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- USER PROFILES
-- ================================================
CREATE TABLE user_profiles (
    id                      BIGSERIAL       PRIMARY KEY,
    user_id                 BIGINT          UNIQUE REFERENCES users(id)
        ON DELETE CASCADE,
    last_name               VARCHAR(100)    NOT NULL,
    first_name              VARCHAR(100)    NOT NULL,
    middle_name             VARCHAR(100),
    birth_date              DATE,
    phone_number            VARCHAR(20),
    bio                     TEXT,
    avatar_url              TEXT,
    verification_status verification_status
        NOT NULL DEFAULT 'UNVERIFIED',
    renter_trust_score      DECIMAL(3, 2)
        DEFAULT 0.00,
    owner_trust_score       DECIMAL(3, 2)
        DEFAULT 0.00,
    total_successful_rents  INT
        DEFAULT 0,
    created_at              TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- ВЕРИФІКАЦІЯ ДОКУМЕНТІВ
-- ================================================
CREATE TABLE user_verification_requests (
    id                          BIGSERIAL               PRIMARY KEY,
    user_id                     BIGINT                  NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    document_type               document_type           NOT NULL,
    document_image_url          TEXT                    NOT NULL,
    reviewed_by                 BIGINT                  REFERENCES users(id) -- адмін
        ON DELETE SET NULL,
    rejection_reason            TEXT,
    status                      verification_status     NOT NULL
        DEFAULT 'PENDING',
    created_at                  TIMESTAMP               NOT NULL
        DEFAULT NOW(),
    updated_at                  TIMESTAMP               NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- REFRESH TOKENS
-- ================================================
CREATE TABLE refresh_tokens (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    token_hash  VARCHAR(255)    NOT NULL UNIQUE,
    expires_at  TIMESTAMP       NOT NULL,
    revoked_at  TIMESTAMP,
    created_at  TIMESTAMP       NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- CATEGORIES
-- ================================================
CREATE TABLE categories (
    id          BIGSERIAL       PRIMARY KEY,
    parent_id   BIGINT          REFERENCES categories(id)
        ON DELETE SET NULL,
    name        VARCHAR(255)    NOT NULL,
    slug        VARCHAR(100)    UNIQUE NOT NULL,
    icon_url    TEXT
);

-- ================================================
-- ITEMS
-- ================================================
CREATE TABLE items (
    id                      BIGSERIAL       PRIMARY KEY,
    owner_id                BIGINT          NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    category_id             BIGINT          NOT NULL REFERENCES categories(id)
        ON DELETE RESTRICT,
    title                   VARCHAR(100)    NOT NULL,
    description             TEXT,
    tags                    TEXT[],
    condition               item_condition  NOT NULL,
    price_per_day           DECIMAL(10, 2)  NOT NULL,
    price_per_week          DECIMAL(10, 2),
    deposit_amount          DECIMAL(10, 2)  NOT NULL,
    status                  renting_status  NOT NULL,
    city                    VARCHAR(100)    NOT NULL,
    address                 TEXT            NOT NULL,
    latitude                DECIMAL(9, 6),
    longitude               DECIMAL(9, 6),
    is_verified             BOOLEAN         NOT NULL
        DEFAULT FALSE,
    created_at              TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- ITEM IMAGES
-- ================================================
CREATE TABLE item_images (
    id          BIGSERIAL   PRIMARY KEY,
    item_id     BIGINT      NOT NULL REFERENCES items(id)
        ON DELETE CASCADE,
    image_url   TEXT        NOT NULL,
    is_main     BOOLEAN
        DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- BOOKINGS
-- ================================================
CREATE TABLE bookings (
    id                      BIGSERIAL       PRIMARY KEY,
    item_id                 BIGINT          NOT NULL REFERENCES items(id)
        ON DELETE RESTRICT,
    renter_id               BIGINT          NOT NULL REFERENCES users(id)
        ON DELETE RESTRICT,
    start_date              DATE            NOT NULL,
    end_date                DATE            NOT NULL,
    total_price             DECIMAL(10, 2)  NOT NULL,
    deposit_total           DECIMAL(10, 2)  NOT NULL,
    price_per_day_snapshot  DECIMAL(10, 2)  NOT NULL,
    status                  booking_status  NOT NULL
        DEFAULT 'PENDING',
    cancellation_reason     TEXT,
    created_at              TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    CONSTRAINT chk_booking_dates CHECK (end_date > start_date)
);

-- Частковий індекс для захисту від подвійного бронювання
CREATE INDEX idx_bookings_item_dates ON bookings(item_id, start_date, end_date)
    WHERE status NOT IN ('REJECTED', 'CANCELLED');


-- ================================================
-- BOOKING DELIVERIES
-- ================================================
CREATE TABLE booking_deliveries (
    id                      BIGSERIAL       PRIMARY KEY,
    booking_id              BIGINT          UNIQUE NOT NULL REFERENCES bookings(id)
        ON DELETE CASCADE,
    method                  delivery_method NOT NULL, -- якщо доставка
    tracking_number         VARCHAR(100),           -- ТТН Нової Пошти
    delivery_address        TEXT,
    estimated_delivery_date DATE,
    status                  delivery_status,
    created_at              TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- TRANSACTIONS
-- ================================================
CREATE TABLE transactions (
    id              BIGSERIAL           PRIMARY KEY,
    booking_id      BIGINT              NOT NULL REFERENCES bookings(id)
        ON DELETE RESTRICT,
    amount          DECIMAL(10, 2)      NOT NULL,
    type            transaction_type    NOT NULL,
    external_tx_id  VARCHAR(255),
    status          transaction_status  NOT NULL
        DEFAULT 'PENDING',
    created_at      TIMESTAMP           NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- USER REVIEWS
-- ================================================
CREATE TABLE user_reviews (
    id              BIGSERIAL   PRIMARY KEY,
    user_id         BIGINT      NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    reviewer_id     BIGINT      REFERENCES users(id)
        ON DELETE SET NULL,
    booking_id      BIGINT      REFERENCES bookings(id)
        ON DELETE SET NULL,
    trust_rating    SMALLINT    NOT NULL
        CHECK (trust_rating >= 1 AND trust_rating <= 5),
    comment         TEXT,
    created_at      TIMESTAMP   NOT NULL
        DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL
        DEFAULT NOW(),
    CONSTRAINT uq_user_review_per_booking UNIQUE (booking_id, reviewer_id)
);

-- ================================================
-- ITEM REVIEWS
-- ================================================
CREATE TABLE item_reviews (
    id          BIGSERIAL   PRIMARY KEY,
    item_id     BIGINT      NOT NULL REFERENCES items(id)
        ON DELETE CASCADE,
    renter_id   BIGINT      REFERENCES users(id)
        ON DELETE SET NULL,
    booking_id  BIGINT      REFERENCES bookings(id)
        ON DELETE SET NULL,
    rating      SMALLINT    NOT NULL
        CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    created_at  TIMESTAMP   NOT NULL
        DEFAULT NOW(),
    -- один відгук на річ від одного бронювання
    CONSTRAINT uq_item_review_per_booking
        UNIQUE (booking_id, renter_id)
);

-- ================================================
-- CHAT ROOMS
-- ================================================
CREATE TABLE chat_rooms (
    id          BIGSERIAL   PRIMARY KEY,
    booking_id  BIGINT      UNIQUE REFERENCES bookings(id)
        ON DELETE CASCADE,
    created_at  TIMESTAMP   NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- MESSAGES
-- ================================================
CREATE TABLE messages (
    id              BIGSERIAL PRIMARY KEY,
    room_id         BIGINT NOT NULL REFERENCES chat_rooms(id)
        ON DELETE CASCADE,
    sender_id       BIGINT NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    message_text    TEXT NOT NULL,
    is_read         BOOLEAN
        DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- NOTIFICATIONS
-- ================================================
CREATE TABLE notifications (
    id                  BIGSERIAL               PRIMARY KEY,
    user_id             BIGINT                  NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    type                notification_type       NOT NULL,
    channel             notification_channel    NOT NULL
        DEFAULT 'IN_APP',
    title               VARCHAR(255)            NOT NULL,
    body                TEXT,
    is_read             BOOLEAN
        DEFAULT FALSE,
    related_booking_id  BIGINT                  REFERENCES bookings(id)
        ON DELETE SET NULL,
    created_at          TIMESTAMP               NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- DISPUTES
-- ================================================
CREATE TABLE disputes (
    id              BIGSERIAL       PRIMARY KEY,
    booking_id      BIGINT          NOT NULL REFERENCES bookings(id)
        ON DELETE RESTRICT,
    initiator_id    BIGINT          NOT NULL REFERENCES users(id)
        ON DELETE RESTRICT,
    reason          dispute_reason  NOT NULL,
    description     TEXT            NOT NULL,
    resolution      TEXT,
    resolved_by     BIGINT          REFERENCES users(id)
        ON DELETE SET NULL,  -- адмін
    status          dispute_status NOT NULL
        DEFAULT 'OPEN',
    created_at      TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL
        DEFAULT NOW(),
    CONSTRAINT uq_one_dispute_per_booking
        UNIQUE (booking_id)
);

-- ================================================
-- AI SESSIONS
-- ================================================
CREATE TABLE ai_sessions (
    id                      BIGSERIAL   PRIMARY KEY,
    user_id                 BIGINT      REFERENCES users(id)
        ON DELETE SET NULL,
    user_query              TEXT        NOT NULL,
    ai_response             TEXT        NOT NULL,
    recommended_item_ids    BIGINT[],   -- масив id з items
    created_at              TIMESTAMP   NOT NULL
        DEFAULT NOW()
);

-- ================================================
-- ІНДЕКСИ
-- ================================================

-- Users
CREATE INDEX idx_users_is_active             ON users(is_active);

-- Items
CREATE INDEX idx_items_owner                 ON items(owner_id);
CREATE INDEX idx_items_category              ON items(category_id);
CREATE INDEX idx_items_status                ON items(status);
CREATE INDEX idx_items_city                  ON items(city);
CREATE INDEX idx_items_location              ON items(latitude, longitude)
    WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Bookings
CREATE INDEX idx_bookings_renter             ON bookings(renter_id);
CREATE INDEX idx_bookings_status             ON bookings(status);

-- Transactions
CREATE INDEX idx_transactions_booking        ON transactions(booking_id);

-- Messages
CREATE INDEX idx_messages_room               ON messages(room_id);
CREATE INDEX idx_messages_unread             ON messages(room_id, is_read)
    WHERE is_read = FALSE;

-- Notifications
CREATE INDEX idx_notifications_user_unread   ON notifications(user_id, is_read)
    WHERE is_read = FALSE;

-- Refresh tokens
CREATE INDEX idx_refresh_tokens_user         ON refresh_tokens(user_id);

-- Verification requests
CREATE INDEX idx_verification_req_status     ON user_verification_requests(status)
    WHERE status = 'PENDING';

-- Reviews
CREATE INDEX idx_item_reviews_item           ON item_reviews(item_id);
CREATE INDEX idx_user_reviews_user           ON user_reviews(user_id);