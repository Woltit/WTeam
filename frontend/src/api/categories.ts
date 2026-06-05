import api from './axios';
import type { CategoryRequest, CategoryResponse } from '../types/category';

const getCategories = async () => {
    const response = await api.get<CategoryResponse[]>('/categories');
    return response.data;
};

const createCategory = async (data: CategoryRequest) => {
    const response = await api.post<CategoryResponse>('/categories', data);
    return response.data;
};

const updateCategory = async (categoryId: number, data: CategoryRequest) => {
    const response = await api.put<CategoryResponse>(`/categories/${categoryId}`, data);
    return response.data;
};

const deleteCategory = async (categoryId: number) => {
    await api.delete(`/categories/${categoryId}`);
};

export default { getCategories, createCategory, updateCategory, deleteCategory };
