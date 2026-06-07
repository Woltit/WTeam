import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import itemsApi from '../api/items';
import type { ItemResponse } from '../types/item';

const conditionLabel: Record<string, string> = {
    IDEAL: 'Ideal',
    GOOD: 'Good',
    NORM: 'Normal',
    BAD: 'Fair',
    NEEDS_REPAIRING: 'Needs Repair',
};

const conditionClass: Record<string, string> = {
    IDEAL: 'badge-success',
    GOOD: 'badge-success',
    NORM: 'badge-warning',
    BAD: 'badge-warning',
    NEEDS_REPAIRING: 'badge-error',
};

const BrowsePage = () => {
    const [items, setItems] = useState<ItemResponse[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [search, setSearch] = useState('');
    const [cityFilter, setCityFilter] = useState('');

    useEffect(() => {
        setLoading(true);
        itemsApi.getAvailableItems(page, 12)
            .then(data => {
                setItems(data.content);
                setTotalPages(data.totalPages);
            })
            .catch(() => setError('Failed to load items.'))
            .finally(() => setLoading(false));
    }, [page]);

    const filtered = items.filter(item => {
        const q = search.toLowerCase().trim();
        const matchesSearch = !q
            || item.title.toLowerCase().includes(q)
            || item.description?.toLowerCase().includes(q)
            || item.tags?.some(t => t.toLowerCase().includes(q));
        const matchesCity = !cityFilter || item.city.toLowerCase().includes(cityFilter.toLowerCase());
        return matchesSearch && matchesCity;
    });

    return (
        <div className="page">
            <div className="page-hero">
                <h1 className="hero-title">Rent anything,<br /><span className="hero-accent">from anyone.</span></h1>
                <p className="hero-sub">Browse thousands of items available for rent near you.</p>
            </div>

            <div className="browse-filters container">
                <input
                    className="form-input"
                    placeholder="Пошук за назвою, описом або тегами…"
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />
                <input
                    className="form-input"
                    placeholder="Місто…"
                    value={cityFilter}
                    onChange={e => setCityFilter(e.target.value)}
                />
            </div>

            {loading && (
                <div className="page-loader">
                    <div className="spinner" />
                </div>
            )}

            {error && <div className="alert alert-error container">{error}</div>}

            {!loading && !error && (
                <>
                    {filtered.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-icon">📦</div>
                            <p>{items.length === 0 ? 'No items available right now. Check back soon!' : 'Нічого не знайдено за вашим запитом.'}</p>
                        </div>
                    ) : (
                        <div className="items-grid">
                            {filtered.map(item => (
                                <Link to={`/items/${item.id}`} key={item.id} className="item-card">
                                    <div className="item-card-img-placeholder">
                                        <span className="item-card-icon">📦</span>
                                    </div>
                                    <div className="item-card-body">
                                        <div className="item-card-top">
                                            <span className={`badge ${conditionClass[item.condition]}`}>
                                                {conditionLabel[item.condition]}
                                            </span>
                                        </div>
                                        <h2 className="item-card-title">{item.title}</h2>
                                        <p className="item-card-location">📍 {item.city}</p>
                                        <div className="item-card-price">
                                            <span className="price-main">₴{item.pricePerDay}<span className="price-unit">/day</span></span>
                                            {item.pricePerWeek && (
                                                <span className="price-secondary">₴{item.pricePerWeek}/week</span>
                                            )}
                                        </div>
                                        <div className="item-card-owner">
                                            <div className="owner-avatar">
                                                {item.ownerProfile.avatarUrl
                                                    ? <img src={item.ownerProfile.avatarUrl} alt="owner" />
                                                    : <span>{item.ownerProfile.firstName?.[0]}{item.ownerProfile.lastName?.[0]}</span>
                                                }
                                            </div>
                                            <span className="owner-name">
                                                {item.ownerProfile.firstName} {item.ownerProfile.lastName}
                                            </span>
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
                                ← Prev
                            </button>
                            <span className="pagination-info">Page {page + 1} of {totalPages}</span>
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                disabled={page >= totalPages - 1}
                            >
                                Next →
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default BrowsePage;
