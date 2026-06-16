import { useEffect, useState, useCallback } from 'react';
import { createPortal } from 'react-dom';
import { Link } from 'react-router-dom';
import { X } from 'lucide-react';
import { ReviewModal } from '../components/ReviewModal';
import bookingsApi from '../api/bookings';
import itemsApi from '../api/items';
import paymentsApi from '../api/payments';
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
    const [activeSubTab, setActiveSubTab] = useState<'all' | 'pending' | 'active' | 'upcoming' | 'completed' | 'cancelled'>('all');
    const [bookings, setBookings] = useState<IBookingWithItem[]>([]);
    const [page, setPage] = useState(0);
    const [reviewingBookingId, setReviewingBookingId] = useState<number | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [cancellingBookingId, setCancellingBookingId] = useState<number | null>(null);
    const [cancelReason, setCancelReason] = useState('');
    const [cancelling, setCancelling] = useState(false);

    const fetchBookings = useCallback(() => {
        setLoading(true);
        const fetchMethod = activeTab === 'renter' 
            ? bookingsApi.getMyBookings(0, 100)
            : bookingsApi.getOwnerBookings(0, 100);

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
            })
            .catch(() => setError(t('bookings.loadError')))
            .finally(() => setLoading(false));
    }, [activeTab, t]);

    useEffect(() => {
        fetchBookings();
    }, [fetchBookings]);

    const handleTabChange = (tab: 'renter' | 'owner') => {
        setActiveTab(tab);
        setActiveSubTab('all');
        setPage(0);
        setBookings([]);
    };

    const handleSubTabChange = (subTab: 'all' | 'pending' | 'active' | 'upcoming' | 'completed' | 'cancelled') => {
        setActiveSubTab(subTab);
        setPage(0);
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

    const handlePay = async (bookingId: number) => {
        try {
            const { data, signature } = await paymentsApi.createPaymentCheckout(bookingId);
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'https://www.liqpay.ua/api/3/checkout';
            
            const dataInput = document.createElement('input');
            dataInput.type = 'hidden';
            dataInput.name = 'data';
            dataInput.value = data;
            
            const signatureInput = document.createElement('input');
            signatureInput.type = 'hidden';
            signatureInput.name = 'signature';
            signatureInput.value = signature;
            
            form.appendChild(dataInput);
            form.appendChild(signatureInput);
            document.body.appendChild(form);
            form.submit();
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

    const filterBooking = (item: IBookingWithItem) => {
        const status = item.booking.status;
        const startDate = item.booking.startDate;

        if (activeSubTab === 'pending') {
            return status === 'PENDING';
        }
        if (activeSubTab === 'active') {
            return ['APPROVED', 'PAID', 'IN_PROGRESS', 'DISPUTE'].includes(status) && startDate <= todayStr;
        }
        if (activeSubTab === 'upcoming') {
            return ['APPROVED', 'PAID', 'IN_PROGRESS'].includes(status) && startDate > todayStr;
        }
        if (activeSubTab === 'completed') {
            return status === 'COMPLETED';
        }
        if (activeSubTab === 'cancelled') {
            return ['CANCELLED', 'REJECTED'].includes(status);
        }
        return true;
    };

    const filteredBookings = bookings.filter(filterBooking);

    if (activeSubTab === 'upcoming') {
        filteredBookings.sort((a, b) => a.booking.startDate.localeCompare(b.booking.startDate));
    }

    const countPending = bookings.filter(b => b.booking.status === 'PENDING').length;
    const countActive = bookings.filter(b => ['APPROVED', 'PAID', 'IN_PROGRESS', 'DISPUTE'].includes(b.booking.status) && b.booking.startDate <= todayStr).length;
    const countUpcoming = bookings.filter(b => ['APPROVED', 'PAID', 'IN_PROGRESS'].includes(b.booking.status) && b.booking.startDate > todayStr).length;
    const countCompleted = bookings.filter(b => b.booking.status === 'COMPLETED').length;
    const countCancelled = bookings.filter(b => ['CANCELLED', 'REJECTED'].includes(b.booking.status)).length;
    const countAll = bookings.length;

    const itemsPerPage = 6;
    const paginatedBookings = filteredBookings.slice(page * itemsPerPage, (page + 1) * itemsPerPage);
    const totalPages = Math.ceil(filteredBookings.length / itemsPerPage);

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('bookings.title')}</h1>
                <p className="page-subtitle">{t('bookings.subtitle')}</p>
                
                <div className="tab-container">
                    <button
                        onClick={() => handleTabChange('renter')}
                        className={`tab-btn ${activeTab === 'renter' ? 'active' : ''}`}
                    >
                        {t('bookings.tabRentsLabel')}
                    </button>
                    <button
                        onClick={() => handleTabChange('owner')}
                        className={`tab-btn ${activeTab === 'owner' ? 'active' : ''}`}
                    >
                        {t('bookings.tabOffersLabel')}
                    </button>
                </div>

                <div className="subtab-container">
                    <button
                        onClick={() => handleSubTabChange('all')}
                        className={`subtab-btn ${activeSubTab === 'all' ? 'active' : ''}`}
                    >
                        {t('bookings.subTabAll')}
                        <span className="subtab-badge">{countAll}</span>
                    </button>
                    <button
                        onClick={() => handleSubTabChange('pending')}
                        className={`subtab-btn ${activeSubTab === 'pending' ? 'active' : ''}`}
                    >
                        {t('bookings.subTabPending')}
                        <span className="subtab-badge">{countPending}</span>
                    </button>
                    <button
                        onClick={() => handleSubTabChange('active')}
                        className={`subtab-btn ${activeSubTab === 'active' ? 'active' : ''}`}
                    >
                        {t('bookings.subTabActive')}
                        <span className="subtab-badge">{countActive}</span>
                    </button>
                    <button
                        onClick={() => handleSubTabChange('upcoming')}
                        className={`subtab-btn ${activeSubTab === 'upcoming' ? 'active' : ''}`}
                    >
                        {t('bookings.subTabUpcoming')}
                        <span className="subtab-badge">{countUpcoming}</span>
                    </button>
                    <button
                        onClick={() => handleSubTabChange('completed')}
                        className={`subtab-btn ${activeSubTab === 'completed' ? 'active' : ''}`}
                    >
                        {t('bookings.subTabCompleted')}
                        <span className="subtab-badge">{countCompleted}</span>
                    </button>
                    <button
                        onClick={() => handleSubTabChange('cancelled')}
                        className={`subtab-btn ${activeSubTab === 'cancelled' ? 'active' : ''}`}
                    >
                        {t('bookings.subTabCancelled')}
                        <span className="subtab-badge">{countCancelled}</span>
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
                    {filteredBookings.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-icon">📅</div>
                            <p>
                                {bookings.length === 0 
                                    ? (activeTab === 'renter' ? t('bookings.noRentsPrompt') : t('bookings.noOffersPrompt'))
                                    : activeTab === 'renter'
                                    ? (activeSubTab === 'pending' ? t('bookings.noPendingRentsPrompt') :
                                       activeSubTab === 'active' ? t('bookings.noActiveRentsPrompt') :
                                       activeSubTab === 'upcoming' ? t('bookings.noUpcomingRentsPrompt') :
                                       activeSubTab === 'completed' ? t('bookings.noCompletedRentsPrompt') :
                                       t('bookings.noCancelledRentsPrompt'))
                                    : (activeSubTab === 'pending' ? t('bookings.noPendingOffersPrompt') :
                                       activeSubTab === 'active' ? t('bookings.noActiveOffersPrompt') :
                                       activeSubTab === 'upcoming' ? t('bookings.noUpcomingOffersPrompt') :
                                       activeSubTab === 'completed' ? t('bookings.noCompletedOffersPrompt') :
                                       t('bookings.noCancelledOffersPrompt'))
                                }
                            </p>
                            {activeTab === 'renter' && bookings.length === 0 && (
                                <Link to="/" className="btn btn-primary" style={{ marginTop: '1rem' }}>
                                    {t('bookings.goToCatalog')}
                                </Link>
                            )}
                        </div>
                    ) : (
                        <div className="items-grid">
                            {paginatedBookings.map(({ booking, item }) => (
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
                                                    <div className="flex flex-col gap-2 w-full">
                                                        {booking.status === 'APPROVED' && (
                                                            <button 
                                                                className="btn btn-success btn-sm w-full justify-center" 
                                                                onClick={() => handlePay(booking.id)}
                                                            >
                                                                Оплатити (LiqPay)
                                                            </button>
                                                        )}
                                                        <button 
                                                            className="btn btn-danger btn-sm w-full justify-center" 
                                                            onClick={() => setCancellingBookingId(booking.id)}
                                                        >
                                                            {t('bookings.actionCancel')}
                                                        </button>
                                                    </div>
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
                    className="modal-backdrop"
                >
                    <div className="modal-content">
                        <button
                            onClick={() => { setCancellingBookingId(null); setCancelReason(''); }}
                            className="modal-close-btn"
                            aria-label={t('review.close')}
                        >
                            <X className="w-6 h-6" />
                        </button>

                        <h2 className="modal-title">
                            {t('bookings.cancelTitle')}
                        </h2>

                        <form onSubmit={handleCancelSubmit}>
                            <div style={{ marginBottom: '1.5rem' }}>
                                <label className="modal-label">
                                    {t('bookings.cancelReasonLabel')}
                                </label>
                                <textarea
                                    className="modal-textarea"
                                    rows={4}
                                    value={cancelReason}
                                    onChange={(e) => setCancelReason(e.target.value)}
                                    placeholder={t('bookings.cancelPlaceholder')}
                                />
                            </div>

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    onClick={() => { setCancellingBookingId(null); setCancelReason(''); }}
                                    disabled={cancelling}
                                    className="btn btn-outline flex-1"
                                >
                                    {t('bookings.cancelBack')}
                                </button>
                                <button
                                    type="submit"
                                    disabled={cancelling}
                                    className="btn btn-danger flex-1"
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
