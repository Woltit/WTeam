-- 1. Створення ENUM типів
CREATE TYPE role AS ENUM ('ADMIN', 'MODER', 'USER');
CREATE TYPE item_condition AS ENUM ('IDEAL', 'GOOD', 'NORM', 'BAD', 'NEEDS_REPAIRING');
CREATE TYPE renting_status AS ENUM ('ACTIVE', 'RENTED', 'INACTIVE', 'DELETED');
CREATE TYPE verification_status AS ENUM ('PENDING', 'VERIFIED', 'REJECTED');
CREATE TYPE booking_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'PAID', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'DISPUTE');
CREATE TYPE transaction_type AS ENUM ('RENT_PAYMENT', 'DEPOSIT_HOLD', 'DEPOSIT_REFUND', 'COMPENSATION');

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255),
                       role role NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_profiles (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                               last_name VARCHAR(100) NOT NULL,
                               first_name VARCHAR(100) NOT NULL,
                               middle_name VARCHAR(100),
                               birth_date DATE,
                               phone_number VARCHAR(15),
                               bio TEXT,
--                                passport_id VARCHAR(50) UNIQUE ,
                               verification_status verification_status DEFAULT 'PENDING',
                               created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_reviews (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              reviewer_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                              trust_rating SMALLINT NOT NULL CHECK (trust_rating >= 1 AND trust_rating <= 5),
                              comment TEXT,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
);

CREATE TABLE items (
                       id BIGSERIAL PRIMARY KEY,
                       owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
                       title VARCHAR(100) NOT NULL,
                       description TEXT,
                       condition item_condition NOT NULL,
                       price_per_day DECIMAL(10, 2) NOT NULL,
                       deposit_amount_per_day DECIMAL(10, 2) NOT NULL,
                       status renting_status NOT NULL,
                       city VARCHAR(100) NOT NULL,
                       address TEXT NOT NULL,
                       is_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE item_images (
                             id BIGSERIAL PRIMARY KEY,
                             item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
                             image_url TEXT NOT NULL,
                             is_main BOOLEAN DEFAULT FALSE,
                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE item_reviews (
                              id BIGSERIAL PRIMARY KEY,
                              item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
                              renter_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                              rating SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                              comment TEXT,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,
                          item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
                          renter_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                          start_date DATE NOT NULL,
                          end_date DATE NOT NULL,
                          total_price DECIMAL(10, 2) NOT NULL,
                          deposit_total DECIMAL(10, 2) NOT NULL,
                          status booking_status NOT NULL DEFAULT 'PENDING',
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE RESTRICT,
                              amount DECIMAL(10, 2) NOT NULL,
                              type transaction_type NOT NULL,
                              external_tx_id VARCHAR(255),
                              status VARCHAR(50),
                              created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_rooms (
                            id BIGSERIAL PRIMARY KEY,
                            booking_id BIGINT UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
                            created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE messages (
                          id BIGSERIAL PRIMARY KEY,
                          room_id BIGINT NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
                          sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          message_text TEXT NOT NULL,
                          is_read BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);