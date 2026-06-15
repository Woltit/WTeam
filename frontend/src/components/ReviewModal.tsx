import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import api from '../api/axios';
import { Star, X } from 'lucide-react';
import { useLanguage } from '../contexts/LanguageContext';

interface ReviewModalProps {
    isOpen: boolean;
    onClose: () => void;
    bookingId: number;
    type: 'item' | 'user';
    onSuccess: () => void;
}

export const ReviewModal: React.FC<ReviewModalProps> = ({ isOpen, onClose, bookingId, type, onSuccess }) => {
    const { t } = useLanguage();
    const [rating, setRating] = useState<number>(0);
    const [hoverRating, setHoverRating] = useState<number>(0);
    const [comment, setComment] = useState<string>('');
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    // Lock body scroll when modal is open
    useEffect(() => {
        if (isOpen) {
            document.body.style.overflow = 'hidden';
        }
        return () => {
            document.body.style.overflow = '';
        };
    }, [isOpen]);

    if (!isOpen) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (rating === 0) {
            setError(t('review.selectRatingError'));
            return;
        }

        setIsSubmitting(true);
        setError('');

        try {
            const endpoint = type === 'item' 
                ? `/bookings/${bookingId}/reviews/item`
                : `/bookings/${bookingId}/reviews/user`;
            
            await api.post(endpoint, {
                rating,
                comment,
            });
            onSuccess();
            onClose();
        } catch (err: unknown) {
            const resData = (err as { response?: { data?: { detailedMessage?: string; message?: string; error?: string } } })?.response?.data;
            const msg =
                resData?.detailedMessage
                || resData?.message
                || resData?.error
                || t('review.error');
            setError(msg);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleBackdropClick = (e: React.MouseEvent<HTMLDivElement>) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    const activeRating = hoverRating || rating;

    const modal = (
        <div
            onClick={handleBackdropClick}
            className="modal-backdrop"
        >
            <div className="modal-content">
                {/* ── Close Button ─────────────────────────── */}
                <button
                    onClick={onClose}
                    className="modal-close-btn"
                    aria-label={t('review.close')}
                >
                    <X className="w-6 h-6" />
                </button>

                {/* ── Title ───────────────────────────────── */}
                <h2 className="modal-title">
                    {type === 'item' ? t('review.rateItem') : t('review.rateRenter')}
                </h2>

                {/* ── Error Alert ─────────────────────────── */}
                {error && (
                    <div className="alert alert-error" style={{ marginBottom: '1.5rem' }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    {/* ── Star Rating ─────────────────────── */}
                    <div style={{ marginBottom: '1.5rem' }}>
                        <label className="modal-label">
                            {type === 'item' ? t('review.itemRating') : t('review.renterRating')}
                        </label>
                        <div className="flex gap-1">
                            {[1, 2, 3, 4, 5].map((star) => {
                                const isFilled = star <= activeRating;
                                return (
                                    <button
                                        key={star}
                                        type="button"
                                        onMouseEnter={() => setHoverRating(star)}
                                        onMouseLeave={() => setHoverRating(0)}
                                        onClick={() => setRating(star)}
                                        className="p-1 cursor-pointer focus:outline-none"
                                    >
                                        <Star
                                            className={`w-8 h-8 transition-all duration-200 ${
                                                isFilled
                                                    ? 'fill-yellow-400 text-yellow-400 drop-shadow-md scale-110'
                                                    : 'text-slate-500 stroke-[1.5px]'
                                            }`}
                                        />
                                    </button>
                                );
                            })}
                        </div>
                    </div>

                    {/* ── Comment ─────────────────────────── */}
                    <div style={{ marginBottom: '1.5rem' }}>
                        <label className="modal-label">
                            {t('review.commentLabel')}
                        </label>
                        <textarea
                            className="modal-textarea"
                            rows={4}
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            placeholder={t('review.placeholder')}
                        />
                    </div>

                    {/* ── Action Buttons ──────────────────── */}
                    <div className="modal-actions">
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={isSubmitting}
                            className="btn btn-outline flex-1"
                        >
                            {t('review.cancel')}
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="btn btn-primary flex-1"
                        >
                            {isSubmitting ? t('review.submitting') : t('review.submit')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );

    return createPortal(modal, document.body);
};
