-- ================================================================
-- V12: Розширені тестові дані для демонстрації/перевірки
-- ----------------------------------------------------------------
-- Тестові облікові записи (пароль для всіх: Test1234!):
--   owner1@test.com   — власник (verified), речі: велосипед, автобокс
--   owner2@test.com   — власник (verified), речі: газонокосарка, проєктор
--   renter2@test.com  — орендар (verified)
--   renter3@test.com  — орендар (unverified)
-- (адмін admin@gmail.com / 12345678 та renter@test.com / Test1234! — з V2/V5)
--
-- Покриває: профілі з рейтингами, категорії, речі з фото,
-- бронювання у статусах COMPLETED / PAID / PENDING / CANCELLED,
-- платежі (SUCCESS, PENDING), двосторонні відгуки, чат, AI-сесію.
-- ================================================================

DO $$
DECLARE
    pwd          TEXT := '$2a$10$48pgVvcaR3gFkkN0X9fTgemsLl5rMjIV1ggsv9UUJrZHvEKW3T7n2'; -- Test1234!
    u_owner1     BIGINT;
    u_owner2     BIGINT;
    u_renter2    BIGINT;
    u_renter3    BIGINT;
    c_sports     BIGINT;
    c_home       BIGINT;
    c_auto       BIGINT;
    c_electro    BIGINT;
    i_bike       BIGINT;
    i_mower      BIGINT;
    i_roofbox    BIGINT;
    i_projector  BIGINT;
    b_completed  BIGINT;
    b_paid       BIGINT;
    b_pending    BIGINT;
    b_cancelled  BIGINT;
    room_proj    BIGINT;
BEGIN
    -- Запобіжник від повторного запуску
    IF EXISTS (SELECT 1 FROM users WHERE email = 'owner1@test.com') THEN
        RAISE NOTICE 'V12 test data already present, skipping';
        RETURN;
    END IF;

    -- ---------------------------------------------------------
    -- 1. Користувачі + профілі
    -- ---------------------------------------------------------
    INSERT INTO users (email, password, auth_provider, role, is_active)
        VALUES ('owner1@test.com', pwd, 'LOCAL', 'USER', TRUE) RETURNING id INTO u_owner1;
    INSERT INTO user_profiles (user_id, last_name, first_name, phone_number, birth_date, bio,
                               verification_status, renter_trust_score, owner_trust_score, total_successful_rents)
        VALUES (u_owner1, 'Коваленко', 'Олена', '+380671112233', DATE '1990-04-12',
                'Здаю спортивне спорядження та техніку для подорожей.',
                'VERIFIED', 4.80, 4.90, 12);

    INSERT INTO users (email, password, auth_provider, role, is_active)
        VALUES ('owner2@test.com', pwd, 'LOCAL', 'USER', TRUE) RETURNING id INTO u_owner2;
    INSERT INTO user_profiles (user_id, last_name, first_name, phone_number, birth_date, bio,
                               verification_status, renter_trust_score, owner_trust_score, total_successful_rents)
        VALUES (u_owner2, 'Шевченко', 'Ігор', '+380672223344', DATE '1985-09-30',
                'Інструменти та техніка для дому й саду.',
                'VERIFIED', 4.50, 4.70, 8);

    INSERT INTO users (email, password, auth_provider, role, is_active)
        VALUES ('renter2@test.com', pwd, 'LOCAL', 'USER', TRUE) RETURNING id INTO u_renter2;
    INSERT INTO user_profiles (user_id, last_name, first_name, phone_number, birth_date, bio,
                               verification_status, renter_trust_score, owner_trust_score, total_successful_rents)
        VALUES (u_renter2, 'Бондаренко', 'Марія', '+380673334455', DATE '1995-01-20',
                'Активно орендую речі для подорожей та хобі.',
                'VERIFIED', 4.60, 0.00, 5);

    INSERT INTO users (email, password, auth_provider, role, is_active)
        VALUES ('renter3@test.com', pwd, 'LOCAL', 'USER', TRUE) RETURNING id INTO u_renter3;
    INSERT INTO user_profiles (user_id, last_name, first_name, phone_number, birth_date,
                               verification_status, renter_trust_score, owner_trust_score, total_successful_rents)
        VALUES (u_renter3, 'Мельник', 'Андрій', '+380674445566', DATE '2000-07-07',
                'UNVERIFIED', 0.00, 0.00, 0);

    -- ---------------------------------------------------------
    -- 2. Категорії (додаткові; tourism/tools/electronics — з V5)
    -- ---------------------------------------------------------
    INSERT INTO categories (name, slug) VALUES ('Спорт', 'sports')          ON CONFLICT (slug) DO NOTHING;
    INSERT INTO categories (name, slug) VALUES ('Дім і сад', 'home-garden') ON CONFLICT (slug) DO NOTHING;
    INSERT INTO categories (name, slug) VALUES ('Авто', 'auto')             ON CONFLICT (slug) DO NOTHING;
    SELECT id INTO c_sports  FROM categories WHERE slug = 'sports';
    SELECT id INTO c_home    FROM categories WHERE slug = 'home-garden';
    SELECT id INTO c_auto    FROM categories WHERE slug = 'auto';
    SELECT id INTO c_electro FROM categories WHERE slug = 'electronics';

    -- ---------------------------------------------------------
    -- 3. Речі + зображення
    -- ---------------------------------------------------------
    INSERT INTO items (owner_id, category_id, title, description, tags, condition,
                       price_per_day, price_per_week, deposit_amount, status, city, address,
                       is_verified, rating, total_reviews)
        VALUES (u_owner1, c_sports, 'Гірський велосипед Merida',
                'Алюмінієва рама 27.5", 21 швидкість. Підходить для міста і трейлів.',
                ARRAY['велосипед','спорт','подорожі'], 'GOOD',
                120.00, 700.00, 800.00, 'AVAILABLE', 'Київ', 'вул. Велика Васильківська 10',
                TRUE, 5.00, 1)
        RETURNING id INTO i_bike;
    INSERT INTO item_images (item_id, image_url, is_main) VALUES
        (i_bike, 'https://res.cloudinary.com/demo/image/upload/sample_bike_main.jpg', TRUE),
        (i_bike, 'https://res.cloudinary.com/demo/image/upload/sample_bike_side.jpg', FALSE);

    INSERT INTO items (owner_id, category_id, title, description, tags, condition,
                       price_per_day, price_per_week, deposit_amount, status, city, address, is_verified)
        VALUES (u_owner2, c_home, 'Газонокосарка Bosch Rotak',
                'Електрична газонокосарка, ширина скошування 34 см, легка в керуванні.',
                ARRAY['сад','газон','інструмент'], 'IDEAL',
                90.00, 500.00, 400.00, 'AVAILABLE', 'Львів', 'вул. Личаківська 25', TRUE)
        RETURNING id INTO i_mower;
    INSERT INTO item_images (item_id, image_url, is_main) VALUES
        (i_mower, 'https://res.cloudinary.com/demo/image/upload/sample_mower_main.jpg', TRUE);

    INSERT INTO items (owner_id, category_id, title, description, tags, condition,
                       price_per_day, price_per_week, deposit_amount, status, city, address, is_verified)
        VALUES (u_owner1, c_auto, 'Автобокс на дах Thule 420 л',
                'Місткий автобокс для подорожей, кріплення в комплекті.',
                ARRAY['авто','подорожі','багаж'], 'GOOD',
                110.00, 600.00, 700.00, 'AVAILABLE', 'Київ', 'вул. Велика Васильківська 10', FALSE)
        RETURNING id INTO i_roofbox;

    INSERT INTO items (owner_id, category_id, title, description, tags, condition,
                       price_per_day, price_per_week, deposit_amount, status, city, address, is_verified)
        VALUES (u_owner2, c_electro, 'Проєктор Epson Full HD',
                'Full HD проєктор для домашнього кінотеатру та презентацій.',
                ARRAY['електроніка','кіно','презентація'], 'GOOD',
                160.00, 900.00, 1200.00, 'AVAILABLE', 'Одеса', 'вул. Дерибасівська 5', TRUE)
        RETURNING id INTO i_projector;
    INSERT INTO item_images (item_id, image_url, is_main) VALUES
        (i_projector, 'https://res.cloudinary.com/demo/image/upload/sample_projector_main.jpg', TRUE);

    -- ---------------------------------------------------------
    -- 4. Бронювання (різні статуси)
    -- ---------------------------------------------------------
    -- COMPLETED: Марія орендувала велосипед Олени (підстава для відгуків)
    INSERT INTO bookings (item_id, renter_id, start_date, end_date,
                          total_price, deposit_total, price_per_day_snapshot, status)
        VALUES (i_bike, u_renter2, CURRENT_DATE - 14, CURRENT_DATE - 11,
                360.00, 800.00, 120.00, 'COMPLETED')
        RETURNING id INTO b_completed;

    -- PAID: Марія орендує проєктор Ігоря (підстава для платежу)
    INSERT INTO bookings (item_id, renter_id, start_date, end_date,
                          total_price, deposit_total, price_per_day_snapshot, status)
        VALUES (i_projector, u_renter2, CURRENT_DATE + 2, CURRENT_DATE + 4,
                320.00, 1200.00, 160.00, 'PAID')
        RETURNING id INTO b_paid;

    -- PENDING: Андрій хоче газонокосарку Ігоря
    INSERT INTO bookings (item_id, renter_id, start_date, end_date,
                          total_price, deposit_total, price_per_day_snapshot, status)
        VALUES (i_mower, u_renter3, CURRENT_DATE + 5, CURRENT_DATE + 7,
                180.00, 400.00, 90.00, 'PENDING')
        RETURNING id INTO b_pending;

    -- CANCELLED: Андрій скасував бронювання автобокса
    INSERT INTO bookings (item_id, renter_id, start_date, end_date,
                          total_price, deposit_total, price_per_day_snapshot, status, cancellation_reason)
        VALUES (i_roofbox, u_renter3, CURRENT_DATE + 10, CURRENT_DATE + 12,
                220.00, 700.00, 110.00, 'CANCELLED', 'Змінилися плани')
        RETURNING id INTO b_cancelled;

    -- ---------------------------------------------------------
    -- 5. Платежі
    -- ---------------------------------------------------------
    INSERT INTO payments (booking_id, amount, currency, status, provider_transaction_id)
        VALUES (b_paid, 320.00, 'UAH', 'SUCCESS', 'liqpay_test_tx_1001');
    INSERT INTO payments (booking_id, amount, currency, status)
        VALUES (b_pending, 180.00, 'UAH', 'PENDING');

    -- ---------------------------------------------------------
    -- 6. Відгуки (для завершеного бронювання) — опубліковані
    -- ---------------------------------------------------------
    -- Орендар (Марія) -> річ (велосипед)
    INSERT INTO item_reviews (item_id, reviewer_id, booking_id, rating, comment, status)
        VALUES (i_bike, u_renter2, b_completed, 5,
                'Чудовий велосипед, усе працює ідеально. Власниця дуже привітна!', 'PUBLISHED');
    -- Орендар (Марія) -> власник (Олена)
    INSERT INTO user_reviews (target_user_id, reviewer_id, booking_id, rating, comment, target_role, status)
        VALUES (u_owner1, u_renter2, b_completed, 5,
                'Швидка комунікація, річ відповідає опису. Рекомендую.', 'OWNER', 'PUBLISHED');
    -- Власник (Олена) -> орендар (Марія)
    INSERT INTO user_reviews (target_user_id, reviewer_id, booking_id, rating, comment, target_role, status)
        VALUES (u_renter2, u_owner1, b_completed, 5,
                'Відповідальна орендарка, повернула вчасно й у гарному стані.', 'RENTER', 'PUBLISHED');

    -- ---------------------------------------------------------
    -- 7. Чат + повідомлення (по бронюванню проєктора; кімната прив'язана до booking_id)
    -- ---------------------------------------------------------
    INSERT INTO chat_rooms (booking_id) VALUES (b_paid)
        ON CONFLICT (booking_id) DO NOTHING;
    SELECT id INTO room_proj FROM chat_rooms WHERE booking_id = b_paid;
    INSERT INTO messages (room_id, sender_id, message_text, is_read) VALUES
        (room_proj, u_renter2, 'Доброго дня! Чи доступний проєктор на ці вихідні?', TRUE),
        (room_proj, u_owner2,  'Так, доступний. Можу передати у пʼятницю ввечері.', FALSE);

    -- ---------------------------------------------------------
    -- 8. AI-сесія (приклад рекомендації)
    -- ---------------------------------------------------------
    INSERT INTO ai_sessions (user_id, user_query, ai_response, recommended_item_ids)
        VALUES (u_renter2, 'Їду на 3 дні в гори, що порадите взяти?',
                'Рекомендую гірський велосипед для пересування та автобокс на дах для спорядження.',
                ARRAY[i_bike, i_roofbox]);

    RAISE NOTICE 'V12 test data seeded successfully';
END $$;
