import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import bookingsApi from '../api/bookings';
import itemsApi from '../api/items';
import type { BookingResponse, BookingStatus } from '../api/bookings';
import type { ItemResponse } from '../types/item';

interface IBookingWithItem {
    booking: BookingResponse;
    item: ItemResponse | null;  
}

interface IPageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
}

const statusLabels: Record<BookingStatus, string> = {
    PENDING: 'Очікує підтвердження',
    APPROVED: 'Підтверджено',
    REJECTED: 'Відхилено',
    PAID: 'Оплачено',
    IN_PROGRESS: 'В оренді',
    COMPLETED: 'Завершено',
    CANCELLED: 'Скасовано',
    DISPUTE: 'Суперечка',
};

const statusClasses: Record<BookingStatus, string> = {
    PENDING: 'badge-warning',
    APPROVED: 'badge-accent',
    REJECTED: 'badge-error',
    PAID: 'badge-success',
    IN_PROGRESS: 'badge-accent',
    COMPLETED: 'badge-neutral',
    CANCELLED: 'badge-error',
    DISPUTE: 'badge-error',
};

const MyBookingsPage = () => {
    const [bookings, setBookings] = useState<IBookingWithItem[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        setLoading(true);
        bookingsApi.getMyBookings(page, 6)
            .then(async (data: IPageResponse<BookingResponse>) => {
                const bookingsWithItems = await Promise.all(
                    data.content.map(async (booking: BookingResponse) => {
                        try {
                            const item = await itemsApi.getItemById(booking.itemId);
                            return { booking, item };
                        } catch {
                            return { booking, item: null };
                        }
                    })
                );
                setBookings(bookingsWithItems);
                setTotalPages(data.totalPages);
            })
            .catch(() => setError('Не вдалося завантажити бронювання.'))
            .finally(() => setLoading(false));
    }, [page]);

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('uk-UA', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">Мої бронювання</h1>
                <p className="page-subtitle">Перегляд статусу та управління вашими замовленнями</p>
            </div>

            {loading && (
                <div className="page-loader">
                    <div className="spinner" />
                    <span className="loader-text">Завантаження бронювань...</span>
                </div>
            )}

            {error && <div className="alert alert-error container">{error}</div>}

            {!loading && !error && (
                <>
                    {bookings.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-icon">📅</div>
                            <p>У вас ще немає жодних бронювань. Перейдіть до каталогу, щоб орендувати щось.</p>
                            <Link to="/" className="btn btn-primary" style={{ marginTop: '1rem' }}>
                                Перейти до каталогу
                            </Link>
                        </div>
                    ) : (
                        <div className="items-grid">
                            {bookings.map(({ booking, item }) => (
                                <div key={booking.id} className="item-card">
                                    <div className="item-card-img-placeholder">
                                        <span className="item-card-icon">📦</span>
                                    </div>
                                    <div className="item-card-body">
                                        <div className="item-card-top" style={{ justifyContent: 'space-between', display: 'flex', width: '100%' }}>
                                            <span className={`badge ${statusClasses[booking.status] || 'badge-neutral'}`}>
                                                {statusLabels[booking.status] || booking.status}
                                            </span>
                                            <span style={{ fontSize: '0.8rem', color: 'var(--text)' }}>
                                                #{booking.id}
                                            </span>
                                        </div>

                                        <h2 className="item-card-title" style={{ marginTop: '0.5rem', marginBottom: '0.25rem' }}>
                                            {item ? item.title : `Товар #${booking.itemId}`}
                                        </h2>

                                        {item && (
                                            <p className="item-card-location">📍 {item.city}</p>
                                        )}

                                        <div style={{ fontSize: '0.85rem', color: 'var(--text)', marginTop: '0.5rem', marginBottom: '0.5rem' }}>
                                            <div><strong>Період:</strong> {formatDate(booking.startDate)} — {formatDate(booking.endDate)}</div>
                                            {booking.depositTotal > 0 && (
                                                <div style={{ marginTop: '0.25rem' }}><strong>Застава:</strong> ₴{booking.depositTotal}</div>
                                            )}
                                        </div>

                                        <div className="item-card-price" style={{ borderTop: '1px solid var(--border)', paddingTop: '0.75rem', marginTop: 'auto' }}>
                                            <span className="price-main">
                                                ₴{booking.totalPrice}
                                                <span className="price-unit" style={{ fontSize: '0.75rem', color: 'var(--text)', fontWeight: 'normal' }}> загалом</span>
                                            </span>
                                        </div>

                                        {item && (
                                            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.75rem' }}>
                                                <Link to={`/items/${item.id}`} className="btn btn-outline btn-sm" style={{ flex: 1 }}>
                                                    Деталі товару
                                                </Link>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {totalPages > 1 && (
                        <div className="pagination">
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(p => Math.max(0, p - 1))}
                                disabled={page === 0}
                            >
                                ← Назад
                            </button>
                            <span className="pagination-info">Сторінка {page + 1} з {totalPages}</span>
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                disabled={page >= totalPages - 1}
                            >
                                Вперед →
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default MyBookingsPage;