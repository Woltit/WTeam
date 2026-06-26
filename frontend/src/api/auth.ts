import api from './axios';
import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';

const login = async (data: LoginRequest) => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
}

const register = async (data: RegisterRequest) => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
};

let refreshPromise: Promise<AuthResponse> | null = null;

const refresh = async () => {
    if (!refreshPromise) {
        refreshPromise = api.post<AuthResponse>('/auth/refresh')
            .then(res => res.data)
            .finally(() => {
                refreshPromise = null;
            });
    }
    return refreshPromise;
};

const logout = async () => {
    await api.post('/auth/logout');
}

export default {
    login,
    register,
    refresh,
    logout,
};
