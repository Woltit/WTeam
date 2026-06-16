CREATE SEQUENCE IF NOT EXISTS item_images_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS item_images (
    id BIGINT NOT NULL DEFAULT nextval('item_images_id_seq'),
    item_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    is_main BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_item_images PRIMARY KEY (id),
    CONSTRAINT fk_item_images_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);
