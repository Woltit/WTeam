-- Dev seed: тестові дані для локальної розробки
-- renter@test.com / Test1234!
INSERT INTO users (email, password, auth_provider, role, is_active, created_at, updated_at)
SELECT 'renter@test.com',
       '$2a$10$48pgVvcaR3gFkkN0X9fTgemsLl5rMjIV1ggsv9UUJrZHvEKW3T7n2',
       'LOCAL', 'USER', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'renter@test.com');

INSERT INTO user_profiles (user_id, first_name, last_name, verification_status,
                           renter_trust_score, owner_trust_score, total_successful_rents,
                           created_at, updated_at)
SELECT u.id, 'Тест', 'Орендар', 'UNVERIFIED', 0.0, 0.0, 0, NOW(), NOW()
FROM users u
WHERE u.email = 'renter@test.com'
  AND NOT EXISTS (SELECT 1 FROM user_profiles up WHERE up.user_id = u.id);

-- Категорії
INSERT INTO categories (id, name, slug) VALUES
    (1, 'Туризм',       'tourism'),
    (2, 'Інструменти',  'tools'),
    (3, 'Електроніка',  'electronics')
ON CONFLICT (slug) DO NOTHING;

-- Речі (власник = admin, id=1)
INSERT INTO items (id, owner_id, category_id, title, description, condition,
                   price_per_day, deposit_amount, status, city, address, is_verified,
                   created_at, updated_at)
VALUES
    (1, 1, 1, 'Намет туристичний 2-місний',
     'Легкий намет для кемпінгу, витримує дощ', 'GOOD',
     150.00, 500.00, 'AVAILABLE', 'Київ', 'вул. Хрещатик 1', TRUE, NOW(), NOW()),
    (2, 1, 2, 'Дриль Bosch Professional',
     'Потужний дриль з набором свердел', 'IDEAL',
     80.00, 300.00, 'AVAILABLE', 'Київ', 'вул. Хрещатик 1', TRUE, NOW(), NOW()),
    (3, 1, 3, 'Камера Sony A6000',
     'Дзеркальна камера, обєктив 18-55mm в комплекті', 'GOOD',
     200.00, 1000.00, 'AVAILABLE', 'Львів', 'пл. Ринок 1', TRUE, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Бронювання (orендар = renter@test.com, id=2)

INSERT INTO bookings (item_id, renter_id, start_date, end_date,
                      total_price, deposit_total, price_per_day_snapshot,
                      status, created_at, updated_at)
SELECT 1, u.id, CURRENT_DATE + 1, CURRENT_DATE + 4,
       450.00, 500.00, 150.00, 'APPROVED', NOW(), NOW()
FROM users u
WHERE u.email = 'renter@test.com'
  AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.item_id = 1 AND b.renter_id = u.id);

-- Синхронізація sequences
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories), true);
SELECT setval('items_id_seq',      (SELECT MAX(id) FROM items),      true);
SELECT setval('bookings_id_seq',   (SELECT MAX(id) FROM bookings),   true);
SELECT setval('users_id_seq',      (SELECT MAX(id) FROM users),      true);
SELECT setval('user_profiles_id_seq', (SELECT MAX(id) FROM user_profiles), true);
