import api from './axios';
import type { UserResponse, Role, BlockUserRequest } from '../types/user';

interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

const getMe = async () => {
    const response = await api.get<UserResponse>('/users/me');
    return response.data;
};

const getAllUsers = async (page = 0, size = 20, isActive?: boolean, role?: Role) => {
    const response = await api.get<Page<UserResponse>>('/users', {
        params: { page, size, isActive, role },
    });
    return response.data;
};

const getUserById = async (userId: number) => {
    const response = await api.get<UserResponse>(`/users/${userId}`);
    return response.data;
};

const searchUserByEmail = async (email: string) => {
    const response = await api.get<UserResponse>('/users/search', { params: { email } });
    return response.data;
};

const activateUser = async (userId: number) => {
    await api.post(`/users/${userId}/activate`);
};

const blockUser = async (userId: number, data: BlockUserRequest) => {
    await api.post(`/users/${userId}/block`, data);
};

const updateRole = async (userId: number, role: Role) => {
    await api.patch(`/users/${userId}/role`, null, { params: { role } });
};

const deleteUser = async (userId: number) => {
    await api.delete(`/users/${userId}`);
};

export default { getMe, getAllUsers, getUserById, searchUserByEmail, activateUser, blockUser, updateRole, deleteUser };
