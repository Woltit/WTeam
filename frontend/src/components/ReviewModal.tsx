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
        } catch (err: any) {
            const msg =
                err.response?.data?.detailedMessage
                || err.response?.data?.message
                || err.response?.data?.error
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
            className="fixed top-0 left-0 right-0 bottom-0 w-screen h-screen z-[9999] flex items-center justify-center bg-black/70 backdrop-blur-sm"
        >
            <div className="bg-slate-800 rounded-2xl p-8 shadow-2xl max-w-lg w-full mx-4 relative">
                {/* ── Close Button ─────────────────────────── */}
                <button
                    onClick={onClose}
                    className="absolute top-5 right-5 text-slate-400 hover:text-white transition-colors cursor-pointer"
                    aria-label={t('review.close')}
                >
                    <X className="w-6 h-6" />
                </button>

                {/* ── Title ───────────────────────────────── */}
                <h2 className="text-2xl font-bold text-white mb-8">
                    {type === 'item' ? t('review.rateItem') : t('review.rateRenter')}
                </h2>

                {/* ── Error Alert ─────────────────────────── */}
                {error && (
                    <div className="bg-red-500/10 border border-red-500/40 text-red-400 px-4 py-3 rounded-xl mb-6 text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    {/* ── Star Rating ─────────────────────── */}
                    <div className="mb-6">
                        <label className="block text-sm font-medium text-slate-300 mb-3">
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
                    <div className="mb-6">
                        <label className="block text-sm font-medium text-slate-300 mb-3">
                            {t('review.commentLabel')}
                        </label>
                        <textarea
                            className="w-full bg-slate-900 border border-slate-700 rounded-xl p-4 text-white placeholder-slate-500 focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none resize-none transition-all"
                            rows={4}
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            placeholder={t('review.placeholder')}
                        />
                    </div>

                    {/* ── Action Buttons ──────────────────── */}
                    <div className="flex gap-4 mt-6">
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={isSubmitting}
                            className="flex-1 border border-slate-600 text-slate-300 hover:bg-slate-700 rounded-xl py-3 font-semibold text-sm transition-colors cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {t('review.cancel')}
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 bg-indigo-600 text-white hover:bg-indigo-500 rounded-xl py-3 font-semibold text-sm shadow-lg transition-colors cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
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
