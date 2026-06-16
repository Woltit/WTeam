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
                await itemsApi.uploadItemImage(Number(itemId), images[i], i === mainImageIndex);
            }
        }
        navigate(`/items/${itemId}`);
    };

    const handleDeleteExistingImage = async (imageId: number) => {
        if (!confirm(t('itemForm.confirmDeleteImage') || 'Are you sure you want to delete this image?')) return;
        try {
            await itemsApi.deleteItemImage(imageId);
            setItem(prev => prev ? { ...prev, images: prev.images?.filter(i => i.id !== imageId) } : prev);
        } catch (err) {
            console.error('Failed to delete image', err);
            alert(t('itemForm.deleteImageFailed') || 'Failed to delete image');
        }
    };

    const handleSetMainImage = async (imageId: number) => {
        try {
            await itemsApi.setMainItemImage(imageId);
            setItem(prev => prev ? {
                ...prev,
                images: prev.images?.map(img => ({
                    ...img,
                    isMain: img.id === imageId
                }))
            } : prev);
        } catch (err) {
            console.error('Failed to set main image', err);
            alert(t('itemForm.actionError') || 'Error setting main image');
        }
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
                <ItemForm 
                    initial={initial} 
                    existingImages={item.images}
                    onDeleteExistingImage={handleDeleteExistingImage}
                    onSetMainExistingImage={handleSetMainImage}
                    onSubmit={handleSubmit} 
                    submitLabel={t('itemForm.submitEdit')} 
                />
            </div>
        </div>
    );
};

export default EditItemPage;
