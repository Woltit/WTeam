CREATE TYPE device_type AS ENUM ('WEB', 'ANDROID', 'IOS');

CREATE TABLE user_device_tokens (
    id          BIGSERIAL   PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users(id)
        ON DELETE CASCADE,
    token       TEXT        NOT NULL,
    device_type device_type NOT NULL
        DEFAULT 'WEB',
    created_at  TIMESTAMP   NOT NULL
        DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL
        DEFAULT NOW()
);

CREATE INDEX idx_user_device_tokens_user_id ON user_device_tokens(user_id);