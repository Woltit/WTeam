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

const getMyItems = async (page = 0, size = 12) => {
    const response = await api.get<Page<ItemResponse>>('/items/my', { params: { page, size } });
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

const uploadItemImage = async (itemId: number, file: File, isMain: boolean = false) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('isMain', isMain.toString());
    const response = await api.post(`/items/${itemId}/images`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
};

const deleteItemImage = async (imageId: number) => {
    await api.delete(`/items/images/${imageId}`);
};

const setItemVerified = async (itemId: number, verified: boolean) => {
    const response = await api.patch<ItemResponse>(`/items/${itemId}/verification`, null, {
        params: { verified },
    });
    return response.data;
};

export default { getAvailableItems, getMyItems, getItemById, getAllItems, createItem, updateItem, deleteItem, setItemVerified, uploadItemImage, deleteItemImage };
