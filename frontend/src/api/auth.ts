import api from './axios';
import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';

const login = async (data: LoginRequest) => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
}

const register = async (data: RegisterRequest) => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
}

export default {
    login,
    register
}