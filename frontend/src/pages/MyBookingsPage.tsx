import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import bookingsApi from '../api/bookings';
import itemsApi from '../api/items';
import type { BookingResponse, BookingStatus } from '../api/bookings';
import type { ItemResponse } from '../types/item';

interface BookingWithItem {
    booking: BookingResponse;
    item: ItemResponse | null;  
}

const MyBookingsPage = () => {
    const [bookings, setBookings] = useState<BookingWithItem[]>([])
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        setLoading(true);
        bookingsApi.getMyBookings(page, 10)
            .then(async (data: any) => {
                const bookingsWithItems = await Promise.all(
                    data.content.map(async (booking: any) => {
                        try {
                            const item = await itemsApi.getItemById(booking.itemId);
                            return { booking, item };
                        } catch (err) {
                            return { booking, item: null };
                        }
                    })
                );
                setBookings(bookingsWithItems);
                setTotalPages(data.totalPages);
            })
            .catch(() => setError('Failed to load items.'))
            .finally(() => setLoading(false));
    }, [page]); 

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Мої бронювання</h1>
            
            {loading && (
                <div className="page-loader">
                    <div className="spinner" />
                </div>
            )}

            {error && <div className="alert alert-error container">{error}</div>}
            
            {!loading && !error && bookings.length === 0 && (
                <p>У вас немає активних бронювань.</p>
            )}
            {!loading && !error && bookings.length > 0 && (
                <div>
                    {/* Тут ми будемо виводити картки бронювань */}
                    <p>Знайдено бронювань: {bookings.length}</p>
                </div>
            )}
        </div>
    );
}   

export default MyBookingsPage;