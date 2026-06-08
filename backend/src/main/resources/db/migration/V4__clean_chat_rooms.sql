ALTER TABLE chat_rooms
    DROP COLUMN IF EXISTS item_id,
    DROP COLUMN IF EXISTS renter_id;
