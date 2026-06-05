import api from './axios';
import type { ItemRequest, ItemResponse } from '../types/item';

interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

const getAvailableItems = async (page = 0, size = 12) => {
    const response = await api.get<Page<ItemResponse>>('/items/available', { params: { page, size } });
    return response.data;
};

const getItemById = async (itemId: number) => {
    const response = await api.get<ItemResponse>(`/items/${itemId}`);
    return response.data;
};

const getAllItems = async (page = 0, size = 20) => {
    const response = await api.get<Page<ItemResponse>>('/items', { params: { page, size } });
    return response.data;
};

const createItem = async (data: ItemRequest) => {
    const response = await api.post<ItemResponse>('/items', data);
    return response.data;
};

const updateItem = async (itemId: number, data: ItemRequest) => {
    const response = await api.put<ItemResponse>(`/items/${itemId}`, data);
    return response.data;
};

const deleteItem = async (itemId: number) => {
    await api.delete(`/items/${itemId}`);
};

const setItemVerified = async (itemId: number, verified: boolean) => {
    const response = await api.patch<ItemResponse>(`/items/${itemId}/verification`, null, {
        params: { verified },
    });
    return response.data;
};

export default { getAvailableItems, getItemById, getAllItems, createItem, updateItem, deleteItem, setItemVerified };
