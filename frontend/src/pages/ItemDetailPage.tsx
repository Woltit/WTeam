import { useEffect, useState, type FormEvent } from 'react';
import { useParams, useNavigate, Link } from 'react-router';
import itemsApi from '../api/items';
import bookingsApi, { type UnavailableDateRange } from '../api/bookings';
import chatApi from '../api/chat';
import { useAuth } from '../contexts/AuthContext';
import type { ItemResponse } from '../types/item';
import AvailabilityCalendar from '../components/AvailabilityCalendar';

const conditionLabel: Record<string, string> = {
    IDEAL: 'Ideal', GOOD: 'Good', NORM: 'Normal', BAD: 'Fair', NEEDS_REPAIRING: 'Needs Repair',
};

const ItemDetailPage = () => {
    const { itemId } = useParams<{ itemId: string }>();
    const navigate = useNavigate();
    const { user, token } = useAuth();
    const [item, setItem] = useState<ItemResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [deleting, setDeleting] = useState(false);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [booking, setBooking] = useState(false);
    const [bookingError, setBookingError] = useState('');
    const [unavailableDates, setUnavailableDates] = useState<UnavailableDateRange[]>([]);

    useEffect(() => {
        if (!itemId) return;
        const id = Number(itemId);
        itemsApi.getItemById(id)
            .then(setItem)
            .catch(() => setError('Item not found.'))
            .finally(() => setLoading(false));
        bookingsApi.getUnavailableDates(id)
            .then(setUnavailableDates)
            .catch(() => {});
    }, [itemId]);

    const handleDelete = async () => {
        if (!item || !confirm('Delete this item? This cannot be undone.')) return;
        setDeleting(true);
        try {
            await itemsApi.deleteItem(item.id);
            navigate('/');
        } catch {
            setError('Failed to delete item.');
            setDeleting(false);
        }
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;
    if (error || !item) return <div className="page"><div className="alert alert-error">{error || 'Item not found.'}</div></div>;

    const isOwner = user?.id === item.ownerId;

    const handleBooking = async (e: FormEvent) => {
        e.preventDefault();
        setBookingError('');
        if (!startDate || !endDate) { setBookingError('Оберіть дати оренди в календарі.'); return; }
        setBooking(true);
        try {
            const b = await bookingsApi.createBooking(item.id, startDate, endDate);
            const room = await chatApi.getOrCreateRoom(b.id);
            navigate(`/chats/${room.id}`);
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
            setBookingError(msg ?? 'Не вдалося створити бронювання. Спробуйте ще раз.');
        } finally {
            setBooking(false);
        }
    };

    return (
        <div className="page">
            <div className="item-detail">
                <div className="item-detail-img-placeholder">
                    <span className="item-detail-icon">📦</span>
                </div>

                <div className="item-detail-content">
                    <div className="item-detail-header">
                        <div className="item-detail-badges">
                            <span className="badge badge-accent">{item.status.replace('_', ' ')}</span>
                            <span className="badge badge-neutral">{conditionLabel[item.condition]}</span>
                            {item.isVerified && <span className="badge badge-success">✓ Verified</span>}
                        </div>
                        <h1 className="item-detail-title">{item.title}</h1>
                        <p className="item-detail-location">📍 {item.city}, {item.address}</p>
                    </div>

                    <div className="item-detail-pricing">
                        <div className="price-block">
                            <span className="price-value">₴{item.pricePerDay}</span>
                            <span className="price-label">per day</span>
                        </div>
                        {item.pricePerWeek && (
                            <div className="price-block">
                                <span className="price-value">₴{item.pricePerWeek}</span>
                                <span className="price-label">per week</span>
                            </div>
                        )}
                        <div className="price-block">
                            <span className="price-value">₴{item.depositAmount}</span>
                            <span className="price-label">deposit</span>
                        </div>
                    </div>

                    {item.description && (
                        <div className="item-detail-section">
                            <h2 className="section-heading">Description</h2>
                            <p className="item-detail-desc">{item.description}</p>
                        </div>
                    )}

                    {item.tags?.length > 0 && (
                        <div className="item-detail-section">
                            <h2 className="section-heading">Tags</h2>
                            <div className="tag-list">
                                {item.tags.map(tag => <span key={tag} className="tag">{tag}</span>)}
                            </div>
                        </div>
                    )}

                    <div className="item-detail-section">
                        <h2 className="section-heading">Owner</h2>
                        <div className="owner-card">
                            <div className="owner-avatar owner-avatar-lg">
                                {item.ownerProfile.avatarUrl
                                    ? <img src={item.ownerProfile.avatarUrl} alt="owner" />
                                    : <span>{item.ownerProfile.firstName?.[0]}{item.ownerProfile.lastName?.[0]}</span>
                                }
                            </div>
                            <div className="owner-info">
                                <span className="owner-name-lg">{item.ownerProfile.firstName} {item.ownerProfile.lastName}</span>
                                {item.ownerProfile.renterTrustScore != null && (
                                    <span className="trust-score">⭐ Trust {item.ownerProfile.ownerTrustScore}</span>
                                )}
                                {item.ownerProfile.totalSuccessfulRents != null && (
                                    <span className="rents-count">{item.ownerProfile.totalSuccessfulRents} successful rents</span>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="item-detail-section">
                        <h2 className="section-heading">Календар доступності</h2>
                        <AvailabilityCalendar
                            unavailableDates={unavailableDates}
                            onRangeSelect={(s, e) => {
                                setStartDate(s);
                                setEndDate(e);
                                setBookingError('');
                            }}
                            pricePerDay={item.pricePerDay}
                            depositAmount={item.depositAmount}
                        />
                    </div>

                    <div className="item-detail-actions">
                        {isOwner ? (
                            <>
                                <Link to={`/items/${item.id}/edit`} className="btn btn-outline">Edit Item</Link>
                                <button className="btn btn-danger" onClick={handleDelete} disabled={deleting}>
                                    {deleting ? <span className="spinner-sm" /> : 'Delete Item'}
                                </button>
                            </>
                        ) : token ? (
                            <div className="rent-cta" style={{ width: '100%' }}>
                                <p className="rent-cta-text" style={{ fontWeight: 600, fontSize: '1rem' }}>Орендувати цей товар</p>
                                {bookingError && <div className="alert alert-error" style={{ marginBottom: 0 }}>{bookingError}</div>}
                                <form onSubmit={handleBooking} className="booking-form">
                                    <button
                                        type="submit"
                                        className="btn btn-primary btn-full"
                                        disabled={booking || !startDate || !endDate}
                                    >
                                        {booking
                                            ? <><span className="spinner-sm" /> Бронювання...</>
                                            : startDate && endDate
                                            ? 'Забронювати та написати власнику'
                                            : 'Оберіть дати в календарі'}
                                    </button>
                                </form>
                            </div>
                        ) : (
                            <div className="rent-cta">
                                <p className="rent-cta-text">Увійдіть, щоб орендувати цей товар</p>
                                <Link to="/login" className="btn btn-primary">Увійти</Link>
                                <Link to="/register" className="btn btn-outline">Зареєструватися</Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ItemDetailPage;
