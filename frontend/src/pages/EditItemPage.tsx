import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import itemsApi from '../api/items';
import { useAuth } from '../contexts/AuthContext';
import { ItemForm } from './CreateItemPage';
import type { ItemRequest } from '../types/item';
import type { ItemResponse } from '../types/item';

const EditItemPage = () => {
    const { itemId } = useParams<{ itemId: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [item, setItem] = useState<ItemResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

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
            .catch(() => setError('Item not found.'))
            .finally(() => setLoading(false));
    }, [itemId, user, navigate]);

    const handleSubmit = async (data: ItemRequest) => {
        await itemsApi.updateItem(Number(itemId), data);
        navigate(`/items/${itemId}`);
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;
    if (error || !item) return <div className="page"><div className="alert alert-error">{error || 'Item not found.'}</div></div>;

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
                <h1 className="page-title">Edit Listing</h1>
                <p className="page-subtitle">Update the details for "{item.title}".</p>
            </div>
            <div className="form-card">
                <ItemForm initial={initial} onSubmit={handleSubmit} submitLabel="Save Changes" />
            </div>
        </div>
    );
};

export default EditItemPage;
