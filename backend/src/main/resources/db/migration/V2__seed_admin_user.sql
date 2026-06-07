-- Seed default admin account (email: admin@gmail.com, password: 12345678)
INSERT INTO users (id, email, password, auth_provider, role, is_active, created_at, updated_at)
SELECT
    COALESCE((SELECT MAX(id) FROM users), 0) + 1,
    'admin@gmail.com',
    '$2b$10$31SfGMf.SdzHbgH9hZb7mODzUeoRIcEayXzB9RWiPOeLpf8MWhO7i',
    'LOCAL',
    'ADMIN',
    TRUE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@gmail.com');

INSERT INTO user_profiles (
    id,
    user_id,
    last_name,
    first_name,
    verification_status,
    renter_trust_score,
    owner_trust_score,
    total_successful_rents,
    created_at,
    updated_at
)
SELECT
    COALESCE((SELECT MAX(id) FROM user_profiles), 0) + 1,
    u.id,
    'Адмін',
    'Системний',
    'VERIFIED',
    0.0,  -- значення для renter_trust_score
    0.0,  -- значення для owner_trust_score
    0,    -- значення для total_successful_rents
    NOW(),
    NOW()
FROM users u
WHERE u.email = 'admin@gmail.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_profiles up WHERE up.user_id = u.id
);

-- Keep sequences in sync (BIGSERIAL / JPA sequences)
DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM pg_class WHERE relname = 'users_id_seq') THEN
            PERFORM setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users), true);
        END IF;
        IF EXISTS (SELECT 1 FROM pg_class WHERE relname = 'user_profiles_id_seq') THEN
            PERFORM setval('user_profiles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_profiles), true);
        END IF;
    END $$;