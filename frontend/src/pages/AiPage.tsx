import { useState } from 'react';
import { Link } from 'react-router-dom';
import aiApi from '../api/ai';
import itemsApi from '../api/items';
import type { AiQueryResponse } from '../types/chat';
import type { ItemResponse } from '../types/item';

const AiPage = () => {
    const [query, setQuery] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [result, setResult] = useState<AiQueryResponse | null>(null);
    const [recommendedItems, setRecommendedItems] = useState<ItemResponse[]>([]);

    const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
        e.preventDefault();
        const q = query.trim();
        if (!q || loading) return;

        setLoading(true);
        setError('');
        setResult(null);
        setRecommendedItems([]);

        try {
            const res = await aiApi.recommend(q);
            setResult(res);

            if (res.recommendedItemIds.length > 0) {
                const items = await Promise.all(
                    res.recommendedItemIds.map(id => itemsApi.getItemById(id).catch(() => null))
                );
                setRecommendedItems(items.filter(Boolean) as ItemResponse[]);
            }
        } catch {
            setError('Не вдалося отримати відповідь від AI. Спробуйте ще раз.');
        } finally {
            setLoading(false);
        }
    };

    const examples = [
        'Що взяти в похід на вихідні?',
        'Потрібен інструмент для ремонту квартири',
        'Шукаю камеру для фотосесії',
    ];

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">AI Помічник</h1>
                <p className="page-subtitle">Опишіть що вам потрібно — AI підбере відповідні речі для оренди</p>
            </div>

            <div className="ai-card">
                <form onSubmit={handleSubmit} className="ai-form">
                    <div className="form-group">
                        <textarea
                            className="form-input form-textarea ai-textarea"
                            placeholder="Наприклад: Планую похід у Карпати на 3 дні, що взяти?"
                            value={query}
                            onChange={e => setQuery(e.target.value)}
                            rows={3}
                            maxLength={1000}
                            disabled={loading}
                        />
                    </div>
                    <div className="ai-form-footer">
                        <div className="ai-examples">
                            {examples.map(ex => (
                                <button
                                    key={ex}
                                    type="button"
                                    className="ai-example-chip"
                                    onClick={() => setQuery(ex)}
                                    disabled={loading}
                                >
                                    {ex}
                                </button>
                            ))}
                        </div>
                        <button className="btn btn-primary" type="submit" disabled={loading || !query.trim()}>
                            {loading ? <><span className="spinner-sm" /> Думаю...</> : '✨ Знайти'}
                        </button>
                    </div>
                </form>
            </div>

            {error && <div className="alert alert-error" style={{ marginTop: '1.5rem' }}>{error}</div>}

            {result && (
                <div className="ai-result">
                    <div className="ai-response-card">
                        <div className="ai-response-label">Відповідь AI</div>
                        <p className="ai-response-text">{result.aiResponse}</p>
                    </div>

                    {recommendedItems.length > 0 && (
                        <div>
                            <h2 className="section-heading" style={{ marginBottom: '1rem' }}>
                                Рекомендовані речі
                            </h2>
                            <div className="items-grid">
                                {recommendedItems.map(item => (
                                    <Link key={item.id} to={`/items/${item.id}`} className="item-card">
                                        <div className="item-card-img-placeholder">
                                            <span className="item-card-icon">📦</span>
                                        </div>
                                        <div className="item-card-body">
                                            <div className="item-card-title">{item.title}</div>
                                            <div className="item-card-location">📍 {item.city}</div>
                                            <div className="item-card-price">
                                                <span className="price-main">₴{item.pricePerDay}</span>
                                                <span className="price-unit">/ день</span>
                                            </div>
                                        </div>
                                    </Link>
                                ))}
                            </div>
                        </div>
                    )}

                    {result.recommendedItemIds.length === 0 && (
                        <div className="empty-state" style={{ padding: '2rem' }}>
                            <div className="empty-icon">🔍</div>
                            <p>AI не знайшов підходящих речей за вашим запитом.</p>
                            <p style={{ fontSize: '0.875rem' }}>Спробуйте переформулювати або перегляньте каталог.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default AiPage;
