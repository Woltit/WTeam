import { useEffect, useState, type FormEvent } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import itemsApi from '../api/items';
import bookingsApi, { type UnavailableDateRange } from '../api/bookings';
import chatApi from '../api/chat';
import reviewsApi from '../api/reviews';
import type { UserReviewResponse } from '../api/reviews';
import { useAuth } from '../hooks/useAuth';
import type { ItemResponse } from '../types/item';
import AvailabilityCalendar from '../components/AvailabilityCalendar';
import { useLanguage } from '../contexts/LanguageContext';

const ItemDetailPage = () => {
    const { t } = useLanguage();
    const { itemId } = useParams<{ itemId: string }>();
    const navigate = useNavigate();
    const { user, accessToken: token } = useAuth();
    const [item, setItem] = useState<ItemResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [ownerReviews, setOwnerReviews] = useState<UserReviewResponse[]>([]);
    const [deleting, setDeleting] = useState(false);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [booking, setBooking] = useState(false);
    const [bookingError, setBookingError] = useState('');
    const [unavailableDates, setUnavailableDates] = useState<UnavailableDateRange[]>([]);
    const [lightboxOpen, setLightboxOpen] = useState(false);
    const [lightboxIndex, setLightboxIndex] = useState(0);

    useEffect(() => {
        if (!itemId) return;
        setLoading(true);
        itemsApi.getItemById(Number(itemId))
            .then(async (fetchedItem) => {
                setItem(fetchedItem);
                try {
                    const revs = await reviewsApi.getUserReviews(fetchedItem.ownerId);
                    // Filter to only show reviews left for them AS an owner
                    setOwnerReviews(revs.filter(r => r.targetRole === 'OWNER'));
                } catch {
                    // Ignore review fetch errors
                }
            })
            .catch(() => setError(t('itemDetail.notFound')))
            .finally(() => setLoading(false));
        bookingsApi.getUnavailableDates(Number(itemId))
            .then(setUnavailableDates)
            .catch(() => {});
    }, [itemId, t]);

    const handleDelete = async () => {
        if (!item || !confirm(t('itemDetail.deleteConfirm'))) return;
        setDeleting(true);
        try {
            await itemsApi.deleteItem(item.id);
            navigate('/');
        } catch {
            setError(t('itemDetail.deleteError'));
            setDeleting(false);
        }
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;
    if (error || !item) return <div className="page"><div className="alert alert-error">{error || t('itemDetail.notFound')}</div></div>;

    const isOwner = user?.id === item.ownerId;

    const handleBooking = async (e: FormEvent) => {
        e.preventDefault();
        setBookingError('');
        if (!startDate || !endDate) { setBookingError(t('itemDetail.selectDatesError')); return; }
        setBooking(true);
        try {
            const b = await bookingsApi.createBooking(item.id, startDate, endDate);
            const room = await chatApi.getOrCreateRoom(b.id);
            navigate(`/chats/${room.id}`);
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
            setBookingError(msg ?? t('itemDetail.bookError'));
        } finally {
            setBooking(false);
        }
    };

    return (
        <div className="page">
            <div className="item-detail">
                {item.images && item.images.length > 0 ? (
                    <div className="item-detail-img-gallery" style={{ display: 'flex', flexDirection: 'column', paddingBottom: '1rem' }}>
                        <div style={{ width: '100%', cursor: 'pointer' }} onClick={() => { 
                            const mainImg = item.images!.find(i => i.isMain) || item.images![0];
                            setLightboxIndex(item.images!.indexOf(mainImg)); 
                            setLightboxOpen(true); 
                        }}>
                            <img 
                                src={(item.images.find(i => i.isMain) || item.images[0]).imageUrl} 
                                alt="main item" 
                                style={{ width: '100%', height: '400px', objectFit: 'cover', borderRadius: '12px' }} 
                            />
                        </div>
                        {item.images.length > 1 && (
                            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', flexWrap: 'wrap' }}>
                                {item.images.map((img, idx) => (
                                    <img 
                                        key={img.id} 
                                        src={img.imageUrl} 
                                        alt="item thumb" 
                                        onClick={() => { setLightboxIndex(idx); setLightboxOpen(true); }}
                                        style={{ 
                                            height: '80px', width: '80px', objectFit: 'cover', borderRadius: '8px', 
                                            cursor: 'pointer', border: '1px solid #ddd'
                                        }} 
                                    />
                                ))}
                            </div>
                        )}
                    </div>
                ) : (
                    <div className="item-detail-img-placeholder">
                        <span className="item-detail-icon">📦</span>
                    </div>
                )}

                <div className="item-detail-content">
                    <div className="item-detail-header">
                        <div className="item-detail-badges">
                            <span className="badge badge-accent">{item.status.replace('_', ' ')}</span>
                            <span className="badge badge-neutral">{t('condition.' + item.condition)}</span>
                            {item.isVerified && <span className="badge badge-success">✓ {t('itemDetail.verified')}</span>}
                        </div>
                        <h1 className="item-detail-title">{item.title}</h1>
                        <p className="item-detail-location">📍 {item.city}, {item.address}</p>
                    </div>

                    <div className="item-detail-pricing">
                        <div className="price-block">
                            <span className="price-value">₴{item.pricePerDay}</span>
                            <span className="price-label">{t('itemDetail.perDay')}</span>
                        </div>
                        {item.pricePerWeek && (
                            <div className="price-block">
                                <span className="price-value">₴{item.pricePerWeek}</span>
                                <span className="price-label">{t('itemDetail.perWeek')}</span>
                            </div>
                        )}
                        <div className="price-block">
                            <span className="price-value">₴{item.depositAmount}</span>
                            <span className="price-label">{t('itemDetail.deposit')}</span>
                        </div>
                    </div>

                    {item.description && (
                        <div className="item-detail-section">
                            <h2 className="section-heading">{t('itemDetail.description')}</h2>
                            <p className="item-detail-desc">{item.description}</p>
                        </div>
                    )}

                    {item.tags?.length > 0 && (
                        <div className="item-detail-section">
                            <h2 className="section-heading">{t('itemDetail.tags')}</h2>
                            <div className="tag-list">
                                {item.tags.map(tag => <span key={tag} className="tag">{tag}</span>)}
                            </div>
                        </div>
                    )}

                    <div className="item-detail-section">
                        <h2 className="section-heading">{t('itemDetail.owner')}</h2>
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
                                    <span className="trust-score">⭐ {t('itemDetail.trust')} {item.ownerProfile.ownerTrustScore}</span>
                                )}
                                {item.ownerProfile.totalSuccessfulRents != null && (
                                    <span className="rents-count">{t('itemDetail.successfulRents', { count: item.ownerProfile.totalSuccessfulRents })}</span>
                                )}
                            </div>
                        </div>
                    </div>

                    {ownerReviews.length > 0 && (
                        <div className="item-detail-section">
                            <h2 className="section-heading">{t('itemDetail.ownerReviews')}</h2>
                            <div className="flex flex-col gap-4">
                                {ownerReviews.map(review => (
                                    <div key={review.id} className="bg-slate-800 p-4 rounded-xl border border-slate-700">
                                        <div className="flex items-center gap-2 mb-2">
                                            <div className="flex">
                                                {[1, 2, 3, 4, 5].map(star => (
                                                    <span key={star} className={`text-lg ${star <= review.rating ? 'text-yellow-400' : 'text-slate-600'}`}>★</span>
                                                ))}
                                            </div>
                                            <span className="text-slate-400 text-sm ml-2">
                                                {new Date(review.createdAt).toLocaleDateString(t('nav.catalog') === 'Каталог' ? 'uk-UA' : 'en-US')}
                                            </span>
                                        </div>
                                        {review.comment && (
                                            <p className="text-slate-300 text-sm leading-relaxed">{review.comment}</p>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    <div className="item-detail-actions">
                        {isOwner ? (
                            <>
                                <Link to={`/items/${item.id}/edit`} className="btn btn-outline">{t('itemDetail.editBtn')}</Link>
                                <button className="btn btn-danger" onClick={handleDelete} disabled={deleting}>
                                    {deleting ? <span className="spinner-sm" /> : t('itemDetail.deleteBtn')}
                                </button>
                            </>
                        ) : token ? (
                            <div className="rent-cta" style={{ width: '100%' }}>
                                <p className="rent-cta-text" style={{ fontWeight: 600, fontSize: '1rem' }}>{t('itemDetail.bookTitle')}</p>
                                {bookingError && <div className="alert alert-error" style={{ marginBottom: 0 }}>{bookingError}</div>}
                                
                                <AvailabilityCalendar
                                    unavailableDates={unavailableDates}
                                    onRangeSelect={(start, end) => {
                                        setStartDate(start);
                                        setEndDate(end);
                                    }}
                                    pricePerDay={item.pricePerDay}
                                    depositAmount={item.depositAmount}
                                />

                                <form onSubmit={handleBooking} className="booking-form" style={{ marginTop: '1rem' }}>
                                    <button
                                        type="submit"
                                        className="btn btn-primary btn-full"
                                        disabled={booking || !startDate || !endDate}
                                    >
                                        {booking
                                            ? <><span className="spinner-sm" /> {t('itemDetail.bookingInProgress')}</>
                                            : startDate && endDate
                                            ? t('itemDetail.bookAndMessage')
                                            : startDate
                                            ? t('cal.selectEnd', { start: startDate })
                                            : t('cal.selectStart')}
                                    </button>
                                </form>
                            </div>
                        ) : (
                            <div className="rent-cta">
                                <p className="rent-cta-text">{t('itemDetail.loginToBook')}</p>
                                <Link to="/login" className="btn btn-primary">{t('nav.login')}</Link>
                                <Link to="/register" className="btn btn-outline">{t('nav.register')}</Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>
            {lightboxOpen && item?.images && item.images.length > 0 && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.9)', zIndex: 9999, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <button onClick={() => setLightboxOpen(false)} style={{ position: 'absolute', top: '20px', right: '20px', background: 'none', border: 'none', color: 'white', fontSize: '2rem', cursor: 'pointer' }}>✖</button>
                    {item.images.length > 1 && (
                        <button 
                            onClick={() => setLightboxIndex(prev => prev > 0 ? prev - 1 : item.images!.length - 1)} 
                            style={{ position: 'absolute', left: '20px', background: 'none', border: 'none', color: 'white', fontSize: '4rem', cursor: 'pointer' }}>
                            ‹
                        </button>
                    )}
                    <img src={item.images[lightboxIndex].imageUrl} style={{ maxHeight: '90vh', maxWidth: '90vw', objectFit: 'contain' }} alt="lightbox" />
                    {item.images.length > 1 && (
                        <button 
                            onClick={() => setLightboxIndex(prev => prev < item.images!.length - 1 ? prev + 1 : 0)} 
                            style={{ position: 'absolute', right: '20px', background: 'none', border: 'none', color: 'white', fontSize: '4rem', cursor: 'pointer' }}>
                            ›
                        </button>
                    )}
                </div>
            )}
        </div>
    );
};

export default ItemDetailPage;
