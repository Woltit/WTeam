-- Relax the booking dates check constraint to allow same-day bookings (start_date == end_date)
ALTER TABLE bookings DROP CONSTRAINT IF EXISTS chk_booking_dates;
ALTER TABLE bookings ADD CONSTRAINT chk_booking_dates CHECK (end_date >= start_date);
