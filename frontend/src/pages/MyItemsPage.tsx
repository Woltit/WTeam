import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import itemsApi from '../api/items';
import type { ItemResponse } from '../types/item';
import { useLanguage } from '../contexts/LanguageContext';

const conditionClass: Record<string, string> = {
    IDEAL: 'badge-success',
    GOOD: 'badge-success',
    NORM: 'badge-warning',
    BAD: 'badge-warning',
    NEEDS_REPAIRING: 'badge-error',
};

const MyItemsPage = () => {
    const { t } = useLanguage();
    const [items, setItems] = useState<ItemResponse[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        setLoading(true);
        itemsApi.getMyItems(page, 12)
            .then(data => {
                setItems(data.content);
                setTotalPages(data.totalPages);
            })
            .catch(() => setError(t('browse.loadingError')))
            .finally(() => setLoading(false));
    }, [page, t]);

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('myItems.title')}</h1>
            </div>

            {loading && (
                <div className="page-loader">
                    <div className="spinner" />
                </div>
            )}

            {error && <div className="alert alert-error container">{error}</div>}

            {!loading && !error && (
                <>
                    {items.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-icon">📦</div>
                            <p>{t('browse.noItems')}</p>
                            <Link to="/items/create" className="btn btn-primary" style={{ marginTop: '1rem' }}>
                                {t('nav.addListing')}
                            </Link>
                        </div>
                    ) : (
                        <div className="items-grid container">
                            {items.map(item => (
                                <Link to={`/items/${item.id}`} key={item.id} className="item-card">
                                    <div className="item-card-img-placeholder" style={{ padding: 0, overflow: 'hidden' }}>
                                        {item.images && item.images.length > 0 ? (
                                            <img 
                                                src={item.images.find(img => img.isMain)?.imageUrl || item.images[0].imageUrl} 
                                                alt={item.title} 
                                                style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
                                            />
                                        ) : (
                                            <span className="item-card-icon">📦</span>
                                        )}
                                    </div>
                                    <div className="item-card-body">
                                        <div className="item-card-top" style={{ justifyContent: 'space-between', display: 'flex' }}>
                                            <span className={`badge ${conditionClass[item.condition]}`}>
                                                {t('condition.' + item.condition)}
                                            </span>
                                            {item.isVerified ? (
                                                <span className="badge badge-success">{t('itemDetail.verified')}</span>
                                            ) : (
                                                <span className="badge badge-warning">{t('verificationStatus.PENDING')}</span>
                                            )}
                                        </div>
                                        <h2 className="item-card-title">{item.title}</h2>
                                        <p className="item-card-location">📍 {item.city}</p>
                                        <div className="item-card-price">
                                            <span className="price-main">₴{item.pricePerDay}<span className="price-unit">{t('browse.priceUnitDay')}</span></span>
                                            {item.pricePerWeek && (
                                                <span className="price-secondary">₴{item.pricePerWeek}{t('browse.priceUnitWeek')}</span>
                                            )}
                                        </div>
                                    </div>
                                </Link>
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
        </div>
    );
};

export default MyItemsPage;
