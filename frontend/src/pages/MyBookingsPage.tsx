import { useEffect, useState, useCallback } from 'react';
import { createPortal } from 'react-dom';
import { Link } from 'react-router-dom';
import { X } from 'lucide-react';
import { ReviewModal } from '../components/ReviewModal';
import bookingsApi from '../api/bookings';
import itemsApi from '../api/items';
import type { BookingResponse, BookingStatus } from '../api/bookings';
import type { ItemResponse } from '../types/item';
import { useLanguage } from '../contexts/LanguageContext';

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

// statusLabels mapping has been moved to LanguageContext.tsx

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
    const { language, t } = useLanguage();
    const [activeTab, setActiveTab] = useState<'renter' | 'owner'>('renter');
    const [bookings, setBookings] = useState<IBookingWithItem[]>([]);
    const [page, setPage] = useState(0);
    const [reviewingBookingId, setReviewingBookingId] = useState<number | null>(null);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [cancellingBookingId, setCancellingBookingId] = useState<number | null>(null);
    const [cancelReason, setCancelReason] = useState('');
    const [cancelling, setCancelling] = useState(false);

    const fetchBookings = useCallback(() => {
        setLoading(true);
        const fetchMethod = activeTab === 'renter' 
            ? bookingsApi.getMyBookings(page, 6)
            : bookingsApi.getOwnerBookings(page, 6);

        fetchMethod
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
            .catch(() => setError(t('bookings.loadError')))
            .finally(() => setLoading(false));
    }, [activeTab, page, t]);

    useEffect(() => {
        fetchBookings();
    }, [fetchBookings]);

    const handleTabChange = (tab: 'renter' | 'owner') => {
        setActiveTab(tab);
        setPage(0);
        setBookings([]);
    };

    const handleApprove = async (bookingId: number) => {
        try {
            await bookingsApi.approveBooking(bookingId);
            alert(t('bookings.alertApproveSuccess'));
            fetchBookings();
        } catch {
            alert(t('bookings.actionError'));
        }
    };

    const handleReject = async (bookingId: number) => {
        if (!confirm(t('bookings.actionReject') + '?')) return;
        try {
            await bookingsApi.rejectBooking(bookingId);
            alert(t('bookings.alertRejectSuccess'));
            fetchBookings();
        } catch {
            alert(t('bookings.actionError'));
        }
    };

    const handleComplete = async (bookingId: number) => {
        if (!confirm(t('bookings.actionComplete') + '?')) return;
        try {
            await bookingsApi.completeBooking(bookingId);
            alert(t('bookings.alertCompleteSuccess'));
            fetchBookings();
        } catch {
            alert(t('bookings.actionError'));
        }
    };

    const handleCancelSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (cancellingBookingId === null) return;
        setCancelling(true);
        try {
            await bookingsApi.cancelBooking(cancellingBookingId, cancelReason);
            alert(t('bookings.alertCancelSuccess'));
            setCancellingBookingId(null);
            setCancelReason('');
            fetchBookings();
        } catch {
            alert(t('bookings.actionError'));
        } finally {
            setCancelling(false);
        }
    };

    const formatDate = (dateString: string) => {
        if (!dateString) return '';
        const parts = dateString.split('-');
        if (parts.length !== 3) return dateString;
        const [y, m, d] = parts;
        if (language === 'ua') {
            return `${d}.${m}.${y}`;
        }
        return `${m}/${d}/${y}`;
    };

    const todayStr = (() => {
        const d = new Date();
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${y}-${m}-${day}`;
    })();

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('bookings.title')}</h1>
                <p className="page-subtitle">{t('bookings.subtitle')}</p>
                
                <div className="flex bg-slate-800 p-1 rounded-xl w-fit mt-6">
                    <button
                        onClick={() => handleTabChange('renter')}
                        className={`px-6 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                            activeTab === 'renter'
                                ? 'bg-indigo-600 text-white shadow-md'
                                : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
                        }`}
                    >
                        {t('bookings.tabRentsLabel')}
                    </button>
                    <button
                        onClick={() => handleTabChange('owner')}
                        className={`px-6 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                            activeTab === 'owner'
                                ? 'bg-indigo-600 text-white shadow-md'
                                : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
                        }`}
                    >
                        {t('bookings.tabOffersLabel')}
                    </button>
                </div>
            </div>

            {loading && (
                <div className="page-loader">
                    <div className="spinner" />
                    <span className="loader-text">{t('bookings.loading')}</span>
                </div>
            )}

            {error && <div className="alert alert-error container">{error}</div>}

            {!loading && !error && (
                <>
                    {bookings.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-icon">📅</div>
                            <p>
                                {activeTab === 'renter' 
                                    ? t('bookings.noRentsPrompt')
                                    : t('bookings.noOffersPrompt')}
                            </p>
                            {activeTab === 'renter' && (
                                <Link to="/" className="btn btn-primary" style={{ marginTop: '1rem' }}>
                                    {t('bookings.goToCatalog')}
                                </Link>
                            )}
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
                                                {t('bookingStatus.' + booking.status)}
                                            </span>
                                            <span style={{ fontSize: '0.8rem', color: 'var(--text)' }}>
                                                #{booking.id}
                                            </span>
                                        </div>

                                        <h2 className="item-card-title" style={{ marginTop: '0.5rem', marginBottom: '0.25rem' }}>
                                            {item ? item.title : t('bookings.itemPlaceholder', { id: booking.itemId })}
                                        </h2>

                                        {item && (
                                            <p className="item-card-location">📍 {item.city}</p>
                                        )}

                                        <div style={{ fontSize: '0.85rem', color: 'var(--text)', marginTop: '0.5rem', marginBottom: '0.5rem' }}>
                                            <div><strong>{t('bookings.period')}</strong> {formatDate(booking.startDate)} — {formatDate(booking.endDate)}</div>
                                            {booking.depositTotal > 0 && (
                                                <div style={{ marginTop: '0.25rem' }}><strong>{t('bookings.depositLabel')}</strong> ₴{booking.depositTotal}</div>
                                            )}
                                            {booking.status === 'CANCELLED' && booking.cancellationReason && (
                                                <div className="mt-2 text-xs text-red-400 bg-red-950/20 border border-red-900/30 p-2.5 rounded-lg">
                                                    <strong>{language === 'ua' ? 'Причина скасування: ' : 'Cancellation reason: '}</strong>
                                                    {booking.cancellationReason}
                                                </div>
                                            )}
                                        </div>

                                        <div className="item-card-price" style={{ borderTop: '1px solid var(--border)', paddingTop: '0.75rem', marginTop: 'auto' }}>
                                            <span className="price-main">
                                                ₴{booking.totalPrice}
                                                <span className="price-unit" style={{ fontSize: '0.75rem', color: 'var(--text)', fontWeight: 'normal' }}>{t('bookings.totalUnit')}</span>
                                            </span>
                                        </div>

                                        {item && (
                                            <div className="flex flex-col gap-2 mt-3 w-full">
                                                <div className="flex flex-row items-center gap-2 w-full">
                                                    <Link to={`/items/${item.id}`} className="btn btn-primary btn-sm flex-1 text-center justify-center">
                                                        {t('bookings.itemDetails')}
                                                    </Link>
                                                    {booking.status === 'COMPLETED' && (
                                                        <button 
                                                            className="btn btn-primary btn-sm flex-1 justify-center" 
                                                            onClick={() => setReviewingBookingId(booking.id)}
                                                        >
                                                            {t('bookings.actionReview')}
                                                        </button>
                                                    )}
                                                </div>

                                                {activeTab === 'renter' && (booking.status === 'PENDING' || booking.status === 'APPROVED' || booking.status === 'IN_PROGRESS') && (
                                                    <button 
                                                        className="btn btn-danger btn-sm w-full justify-center" 
                                                        onClick={() => setCancellingBookingId(booking.id)}
                                                    >
                                                        {t('bookings.actionCancel')}
                                                    </button>
                                                )}

                                                {activeTab === 'owner' && booking.status === 'PENDING' && (
                                                    <div className="flex flex-row items-center gap-2 w-full">
                                                        <button 
                                                            className="btn btn-success btn-sm flex-1 justify-center" 
                                                            onClick={() => handleApprove(booking.id)}
                                                        >
                                                            {t('bookings.actionApprove')}
                                                        </button>
                                                        <button 
                                                            className="btn btn-danger btn-sm flex-1 justify-center" 
                                                            onClick={() => handleReject(booking.id)}
                                                        >
                                                            {t('bookings.actionReject')}
                                                        </button>
                                                    </div>
                                                )}

                                                {activeTab === 'owner' && (booking.status === 'APPROVED' || booking.status === 'IN_PROGRESS') && (
                                                    <div className="flex flex-row items-center gap-2 w-full">
                                                        {booking.startDate <= todayStr && (
                                                            <button 
                                                                className="btn btn-success btn-sm flex-1 justify-center" 
                                                                onClick={() => handleComplete(booking.id)}
                                                            >
                                                                {t('bookings.actionComplete')}
                                                            </button>
                                                        )}
                                                        <button 
                                                            className="btn btn-danger btn-sm flex-1 justify-center" 
                                                            onClick={() => setCancellingBookingId(booking.id)}
                                                        >
                                                            {t('bookings.actionCancel')}
                                                        </button>
                                                    </div>
                                                )}
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
                                {t('browse.prev')}
                            </button>
                            <span className="pagination-info">{t('browse.pageInfo', { page: page + 1, total: totalPages })}</span>
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                disabled={page >= totalPages - 1}
                            >
                                {t('browse.next')}
                            </button>
                        </div>
                    )}
                </>
            )}

            {reviewingBookingId !== null && (
                                                <ReviewModal
                                                    isOpen={true}
                                                    onClose={() => setReviewingBookingId(null)}
                                                    bookingId={reviewingBookingId}
                                                    type={activeTab === 'renter' ? 'item' : 'user'}
                                                    onSuccess={() => {
                                                        fetchBookings();
                                                    }}
                                                />
                                            )}

                                            {cancellingBookingId !== null && createPortal(
                                                <div
                                                    onClick={(e) => { if (e.target === e.currentTarget) { setCancellingBookingId(null); setCancelReason(''); } }}
                                                    className="fixed top-0 left-0 right-0 bottom-0 w-screen h-screen z-[9999] flex items-center justify-center bg-black/70 backdrop-blur-sm"
                                                >
                                                    <div className="bg-slate-800 rounded-2xl p-8 shadow-2xl max-w-lg w-full mx-4 relative text-slate-100">
                                                        <button
                                                            onClick={() => { setCancellingBookingId(null); setCancelReason(''); }}
                                                            className="absolute top-5 right-5 text-slate-400 hover:text-white transition-colors cursor-pointer"
                                                            aria-label={t('review.close')}
                                                        >
                                                            <X className="w-6 h-6" />
                                                        </button>

                                                        <h2 className="text-2xl font-bold text-white mb-6">
                                                            {t('bookings.cancelTitle')}
                                                        </h2>

                                                        <form onSubmit={handleCancelSubmit}>
                                                            <div className="mb-6">
                                                                <label className="block text-sm font-medium text-slate-300 mb-3">
                                                                    {t('bookings.cancelReasonLabel')}
                                                                </label>
                                                                <textarea
                                                                    className="w-full bg-slate-900 border border-slate-700 rounded-xl p-4 text-white placeholder-slate-500 focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none resize-none transition-all"
                                                                    rows={4}
                                                                    value={cancelReason}
                                                                    onChange={(e) => setCancelReason(e.target.value)}
                                                                    placeholder={t('bookings.cancelPlaceholder')}
                                                                />
                                                            </div>

                                                            <div className="flex gap-4 mt-6">
                                                                <button
                                                                    type="button"
                                                                    onClick={() => { setCancellingBookingId(null); setCancelReason(''); }}
                                                                    disabled={cancelling}
                                                                    className="flex-1 border border-slate-600 text-slate-300 hover:bg-slate-700 rounded-xl py-3 font-semibold text-sm transition-colors cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                                                                >
                                                                    {t('bookings.cancelBack')}
                                                                </button>
                                                                <button
                                                                    type="submit"
                                                                    disabled={cancelling}
                                                                    className="flex-1 bg-red-600 text-white hover:bg-red-500 rounded-xl py-3 font-semibold text-sm shadow-lg transition-colors cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                                                                >
                                                                    {cancelling ? t('review.submitting') : t('bookings.cancelConfirm')}
                                                                </button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>,
                                                document.body
                                            )}
                                        </div>
                                    );
                                };

                                export default MyBookingsPage;