import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import itemsApi from '../api/items';
import { useAuth } from '../contexts/AuthContext';
import { ItemForm } from './CreateItemPage';
import type { ItemRequest } from '../types/item';
import type { ItemResponse } from '../types/item';
import { useLanguage } from '../contexts/LanguageContext';

const EditItemPage = () => {
    const { itemId } = useParams<{ itemId: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [item, setItem] = useState<ItemResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { t } = useLanguage();

    useEffect(() => {
        if (!itemId) return;
        itemsApi.getItemById(Number(itemId))
            .then(data => {
                if (user && data.ownerId !== user.id) {
                    navigate('/');
                    return;
                }
                setItem(data);
            })
            .catch(() => setError(t('itemDetail.notFound')))
            .finally(() => setLoading(false));
    }, [itemId, user, navigate, t]);

    const handleSubmit = async (data: ItemRequest, images: File[], mainImageIndex: number) => {
        await itemsApi.updateItem(Number(itemId), data);
        if (images && images.length > 0) {
            for (let i = 0; i < images.length; i++) {
                // Since the user selected a new image as main, we pass true if it is the mainImageIndex.
                // Our backend service correctly removes isMain from existing images if isMain=true is passed.
                await itemsApi.uploadItemImage(Number(itemId), images[i], i === mainImageIndex);
            }
        }
        navigate(`/items/${itemId}`);
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;
    if (error || !item) return <div className="page"><div className="alert alert-error">{error || t('itemDetail.notFound')}</div></div>;

    const initial: Partial<ItemRequest> = {
        categoryId: item.categoryId,
        title: item.title,
        description: item.description ?? '',
        tags: item.tags ?? [],
        condition: item.condition,
        pricePerDay: item.pricePerDay as unknown as number,
        pricePerWeek: item.pricePerWeek as unknown as number | null,
        depositAmount: item.depositAmount as unknown as number,
        city: item.city,
        address: item.address,
        latitude: item.latitude as unknown as number,
        longitude: item.longitude as unknown as number,
    };

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('itemForm.editTitle')}</h1>
                <p className="page-subtitle">{t('itemForm.editSubtitle', { title: item.title })}</p>
            </div>
            <div className="form-card">
                <ItemForm initial={initial} onSubmit={handleSubmit} submitLabel={t('itemForm.submitEdit')} />
            </div>
        </div>
    );
};

export default EditItemPage;
