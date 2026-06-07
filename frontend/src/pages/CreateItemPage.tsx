import { useEffect, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router';
import itemsApi from '../api/items';
import categoriesApi from '../api/categories';
import type { ItemRequest, ItemCondition } from '../types/item';
import type { CategoryResponse } from '../types/category';

const CONDITIONS: ItemCondition[] = ['IDEAL', 'GOOD', 'NORM', 'BAD', 'NEEDS_REPAIRING'];
const CONDITION_LABELS: Record<ItemCondition, string> = {
    IDEAL: 'Ideal', GOOD: 'Good', NORM: 'Normal', BAD: 'Fair', NEEDS_REPAIRING: 'Needs Repair',
};

interface ItemFormProps {
    initial?: Partial<ItemRequest>;
    onSubmit: (data: ItemRequest) => Promise<void>;
    submitLabel: string;
}

export const ItemForm = ({ initial = {}, onSubmit, submitLabel }: ItemFormProps) => {
    const [categories, setCategories] = useState<CategoryResponse[]>([]);
    const [form, setForm] = useState<Omit<ItemRequest, 'tags'> & { tags: string }>({
        categoryId: initial.categoryId ?? 0,
        title: initial.title ?? '',
        description: initial.description ?? '',
        tags: (initial.tags ?? []).join(', '),
        condition: initial.condition ?? 'GOOD',
        pricePerDay: initial.pricePerDay ?? 0,
        pricePerWeek: initial.pricePerWeek ?? null,
        depositAmount: initial.depositAmount ?? 0,
        city: initial.city ?? '',
        address: initial.address ?? '',
        latitude: initial.latitude ?? 0,
        longitude: initial.longitude ?? 0,
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        categoriesApi.getCategories().then(setCategories).catch(() => {});
    }, []);

    const flatCategories = (cats: CategoryResponse[]): CategoryResponse[] =>
        cats.flatMap(c => [c, ...flatCategories(c.subcategories)]);

    const set = (field: string, value: unknown) =>
        setForm(f => ({ ...f, [field]: value }));

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await onSubmit({
                ...form,
                tags: form.tags ? form.tags.split(',').map(t => t.trim()).filter(Boolean) : [],
            });
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
            setError(msg ?? 'Something went wrong.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="item-form" onSubmit={handleSubmit}>
            {error && <div className="alert alert-error">{error}</div>}

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-title">Title *</label>
                    <input id="if-title" className="form-input" value={form.title}
                        onChange={e => set('title', e.target.value)} required placeholder="e.g. Electric Drill" />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-category">Category *</label>
                    <select id="if-category" className="form-input" value={form.categoryId}
                        onChange={e => set('categoryId', Number(e.target.value))} required>
                        <option value={0} disabled>Select category</option>
                        {flatCategories(categories).map(c => (
                            <option key={c.id} value={c.id}>{c.name}</option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="form-group">
                <label className="form-label" htmlFor="if-desc">Description *</label>
                <textarea id="if-desc" className="form-input form-textarea" rows={4}
                    value={form.description ?? ''} onChange={e => set('description', e.target.value)} required
                    placeholder="Describe the item, its features, and any usage notes..." />
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-condition">Condition *</label>
                    <select id="if-condition" className="form-input" value={form.condition}
                        onChange={e => set('condition', e.target.value as ItemCondition)} required>
                        {CONDITIONS.map(c => (
                            <option key={c} value={c}>{CONDITION_LABELS[c]}</option>
                        ))}
                    </select>
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-tags">Tags (comma-separated)</label>
                    <input id="if-tags" className="form-input" value={form.tags}
                        onChange={e => set('tags', e.target.value)} placeholder="tool, drill, DIY" />
                </div>
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-ppd">Price per Day (₴) *</label>
                    <input id="if-ppd" type="number" min="0.01" step="0.01" className="form-input"
                        value={form.pricePerDay} onChange={e => set('pricePerDay', Number(e.target.value))} required />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-ppw">Price per Week (₴)</label>
                    <input id="if-ppw" type="number" min="0.01" step="0.01" className="form-input"
                        value={form.pricePerWeek ?? ''} onChange={e => set('pricePerWeek', e.target.value ? Number(e.target.value) : null)} />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-deposit">Deposit (₴) *</label>
                    <input id="if-deposit" type="number" min="0" step="0.01" className="form-input"
                        value={form.depositAmount} onChange={e => set('depositAmount', Number(e.target.value))} required />
                </div>
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-city">City *</label>
                    <input id="if-city" className="form-input" value={form.city}
                        onChange={e => set('city', e.target.value)} required placeholder="Kyiv" />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-address">Address *</label>
                    <input id="if-address" className="form-input" value={form.address}
                        onChange={e => set('address', e.target.value)} required placeholder="Khreshchatyk 1" />
                </div>
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-lat">Latitude</label>
                    <input id="if-lat" type="number" step="any" className="form-input"
                        value={form.latitude ?? ''} onChange={e => set('latitude', Number(e.target.value))} />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-lon">Longitude</label>
                    <input id="if-lon" type="number" step="any" className="form-input"
                        value={form.longitude ?? ''} onChange={e => set('longitude', Number(e.target.value))} />
                </div>
            </div>

            <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? <span className="spinner-sm" /> : submitLabel}
            </button>
        </form>
    );
};

const CreateItemPage = () => {
    const navigate = useNavigate();

    const handleSubmit = async (data: ItemRequest) => {
        const item = await itemsApi.createItem(data);
        navigate(`/items/${item.id}`);
    };

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">List a New Item</h1>
                <p className="page-subtitle">Fill in the details below to make your item available for rent.</p>
            </div>
            <div className="form-card">
                <ItemForm onSubmit={handleSubmit} submitLabel="Create Listing" />
            </div>
        </div>
    );
};

export default CreateItemPage;
