-- Dev seed: тестові дані для локальної розробки
-- admin@gmail.com / 12345678
INSERT INTO users (id, email, password, auth_provider, role, is_active, created_at, updated_at)
SELECT COALESCE((SELECT MAX(id) FROM users), 0) + 1,
       'admin@gmail.com',
       '$2b$10$31SfGMf.SdzHbgH9hZb7mODzUeoRIcEayXzB9RWiPOeLpf8MWhO7i',
       'LOCAL', 'ADMIN', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@gmail.com');

INSERT INTO user_profiles (id, user_id, first_name, last_name, verification_status,
                           renter_trust_score, owner_trust_score, total_successful_rents,
                           created_at, updated_at)
SELECT COALESCE((SELECT MAX(id) FROM user_profiles), 0) + 1,
       u.id, 'Системний', 'Адмін', 'VERIFIED', 0.0, 0.0, 0, NOW(), NOW()
FROM users u
WHERE u.email = 'admin@gmail.com'
  AND NOT EXISTS (SELECT 1 FROM user_profiles up WHERE up.user_id = u.id);

-- renter@test.com / Test1234!
INSERT INTO users (id, email, password, auth_provider, role, is_active, created_at, updated_at)
SELECT COALESCE((SELECT MAX(id) FROM users), 0) + 1,
       'renter@test.com',
       '$2a$10$48pgVvcaR3gFkkN0X9fTgemsLl5rMjIV1ggsv9UUJrZHvEKW3T7n2',
       'LOCAL', 'USER', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'renter@test.com');

INSERT INTO user_profiles (id, user_id, first_name, last_name, verification_status,
                           renter_trust_score, owner_trust_score, total_successful_rents,
                           created_at, updated_at)
SELECT COALESCE((SELECT MAX(id) FROM user_profiles), 0) + 1,
       u.id, 'Тест', 'Орендар', 'UNVERIFIED', 0.0, 0.0, 0, NOW(), NOW()
FROM users u
WHERE u.email = 'renter@test.com'
  AND NOT EXISTS (SELECT 1 FROM user_profiles up WHERE up.user_id = u.id);

-- Категорії
INSERT INTO categories (id, name, slug) VALUES
                                            (1, 'Туризм',       'tourism'),
                                            (2, 'Інструменти',  'tools'),
                                            (3, 'Електроніка',  'electronics')
ON CONFLICT (slug) DO NOTHING;

-- Речі (власник = admin@gmail.com)
INSERT INTO items (id, owner_id, category_id, title, description, condition,
                   price_per_day, deposit_amount, status, city, address, is_verified,
                   created_at, updated_at)
SELECT v.id, admin.id, v.category_id, v.title, v.description, v.condition,
       v.price_per_day, v.deposit_amount, v.status, v.city, v.address, TRUE, NOW(), NOW()
FROM users admin
         CROSS JOIN (VALUES
                         (1, 1, 'Намет туристичний 2-місний',
                          'Легкий намет для кемпінгу, витримує дощ', 'GOOD'::item_condition,
                          150.00, 500.00, 'AVAILABLE'::renting_status, 'Київ', 'вул. Хрещатик 1'),
                         (2, 2, 'Дриль Bosch Professional',
                          'Потужний дриль з набором свердел', 'IDEAL'::item_condition,
                          80.00, 300.00, 'AVAILABLE'::renting_status, 'Київ', 'вул. Хрещатик 1'),
                         (3, 3, 'Камера Sony A6000',
                          'Дзеркальна камера, обєктив 18-55mm в комплекті', 'GOOD'::item_condition,
                          200.00, 1000.00, 'AVAILABLE'::renting_status, 'Львів', 'пл. Ринок 1')
) AS v(id, category_id, title, description, condition, price_per_day, deposit_amount, status, city, address)
WHERE admin.email = 'admin@gmail.com'
ON CONFLICT (id) DO NOTHING;

-- Бронювання (орендар = renter@test.com)
INSERT INTO bookings (id, item_id, renter_id, start_date, end_date,
                      total_price, deposit_total, price_per_day_snapshot,
                      status, created_at, updated_at)
SELECT COALESCE((SELECT MAX(id) FROM bookings), 0) + 1,
       i.id, u.id, CURRENT_DATE + 1, CURRENT_DATE + 4,
       450.00, 500.00, 150.00, 'APPROVED', NOW(), NOW()
FROM users u
         JOIN items i ON i.title = 'Намет туристичний 2-місний'
WHERE u.email = 'renter@test.com'
  AND NOT EXISTS (
    SELECT 1 FROM bookings b WHERE b.item_id = i.id AND b.renter_id = u.id
);

-- Синхронізація sequences
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories), true);
SELECT setval('items_id_seq',      (SELECT MAX(id) FROM items),      true);
SELECT setval('bookings_id_seq',   (SELECT MAX(id) FROM bookings),   true);
SELECT setval('users_id_seq',      (SELECT MAX(id) FROM users),      true);
SELECT setval('user_profiles_id_seq', (SELECT MAX(id) FROM user_profiles), true);
