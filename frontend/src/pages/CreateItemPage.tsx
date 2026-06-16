import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import itemsApi from '../api/items';
import categoriesApi from '../api/categories';
import type { ItemRequest, ItemCondition } from '../types/item';
import type { CategoryResponse } from '../types/category';
import { useLanguage } from '../contexts/LanguageContext';

const CONDITIONS: ItemCondition[] = ['IDEAL', 'GOOD', 'NORM', 'BAD', 'NEEDS_REPAIRING'];

interface ItemFormProps {
    initial?: Partial<ItemRequest>;
    onSubmit: (data: ItemRequest) => Promise<void>;
    submitLabel: string;
}

export const ItemForm = ({ initial = {}, onSubmit, submitLabel }: ItemFormProps) => {
    const { t } = useLanguage();
    const [categories, setCategories] = useState<CategoryResponse[]>([]);
    const [form, setForm] = useState<Omit<ItemRequest, 'tags' | 'pricePerDay' | 'pricePerWeek' | 'depositAmount' | 'categoryId'> & { 
        tags: string;
        pricePerDay: number | '';
        pricePerWeek: number | '';
        depositAmount: number | '';
        categoryId: number | '';
    }>({
        categoryId: initial.categoryId || '',
        title: initial.title ?? '',
        description: initial.description ?? '',
        tags: (initial.tags ?? []).join(', '),
        condition: initial.condition ?? 'GOOD',
        pricePerDay: initial.pricePerDay ?? '',
        pricePerWeek: initial.pricePerWeek ?? '',
        depositAmount: initial.depositAmount ?? '',
        city: initial.city ?? '',
        address: initial.address ?? '',
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

    const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await onSubmit({
                ...form,
                categoryId: Number(form.categoryId),
                pricePerDay: Number(form.pricePerDay),
                pricePerWeek: form.pricePerWeek === '' ? null : Number(form.pricePerWeek),
                depositAmount: Number(form.depositAmount),
                tags: form.tags ? form.tags.split(',').map(t => t.trim()).filter(Boolean) : [],
            } as any);
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
            setError(msg ?? t('itemForm.somethingWentWrong'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="item-form" onSubmit={handleSubmit}>
            {error && <div className="alert alert-error">{error}</div>}

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-title">{t('itemForm.title')} *</label>
                    <input id="if-title" className="form-input" value={form.title}
                        onChange={e => set('title', e.target.value)} required placeholder={t('itemForm.titlePlaceholder')} />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-category">{t('itemForm.category')} *</label>
                    <select id="if-category" className="form-input" value={form.categoryId}
                        onChange={e => set('categoryId', Number(e.target.value))} required>
                        <option value="" disabled>{t('itemForm.selectCategory')}</option>
                        {flatCategories(categories).map(c => (
                            <option key={c.id} value={c.id}>{c.name}</option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="form-group">
                <label className="form-label" htmlFor="if-desc">{t('itemForm.description')}</label>
                <textarea id="if-desc" className="form-input form-textarea" rows={4}
                    value={form.description ?? ''} onChange={e => set('description', e.target.value)} 
                    placeholder={t('itemForm.descPlaceholder')} />
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-condition">{t('itemForm.condition')} *</label>
                    <select id="if-condition" className="form-input" value={form.condition}
                        onChange={e => set('condition', e.target.value as ItemCondition)} required>
                        {CONDITIONS.map(c => (
                            <option key={c} value={c}>{t('condition.' + c)}</option>
                        ))}
                    </select>
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-tags">{t('itemForm.tagsLabel')}</label>
                    <input id="if-tags" className="form-input" value={form.tags}
                        onChange={e => set('tags', e.target.value)} placeholder={t('itemForm.tagsPlaceholder')} />
                </div>
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-ppd">{t('itemForm.price')} *</label>
                    <input id="if-ppd" type="number" min="0.01" step="0.01" className="form-input"
                        value={form.pricePerDay} onChange={e => set('pricePerDay', e.target.value === '' ? '' : Number(e.target.value))} required placeholder="0" />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-ppw">{t('itemForm.priceWeek')} *</label>
                    <input id="if-ppw" type="number" min="0.01" step="0.01" className="form-input"
                        value={form.pricePerWeek} onChange={e => set('pricePerWeek', e.target.value === '' ? '' : Number(e.target.value))} required placeholder="0" />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-deposit">{t('itemForm.deposit')} *</label>
                    <input id="if-deposit" type="number" min="0" step="0.01" className="form-input"
                        value={form.depositAmount} onChange={e => set('depositAmount', e.target.value === '' ? '' : Number(e.target.value))} required placeholder="0" />
                </div>
            </div>

            <div className="form-row">
                <div className="form-group">
                    <label className="form-label" htmlFor="if-city">{t('itemForm.city')} *</label>
                    <input id="if-city" className="form-input" value={form.city}
                        onChange={e => set('city', e.target.value)} required placeholder={t('itemForm.cityPlaceholder')} />
                </div>
                <div className="form-group">
                    <label className="form-label" htmlFor="if-address">{t('itemForm.address')} *</label>
                    <input id="if-address" className="form-input" value={form.address}
                        onChange={e => set('address', e.target.value)} required placeholder={t('itemForm.addressPlaceholder')} />
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
    const { t } = useLanguage();

    const handleSubmit = async (data: ItemRequest) => {
        const item = await itemsApi.createItem(data);
        navigate(`/items/${item.id}`);
    };

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('itemForm.createTitle')}</h1>
                <p className="page-subtitle">{t('itemForm.createSubtitle')}</p>
            </div>
            <div className="form-card">
                <ItemForm onSubmit={handleSubmit} submitLabel={t('itemForm.submitCreate')} />
            </div>
        </div>
    );
};

export default CreateItemPage;
