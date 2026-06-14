import axios from 'axios';
import type { AuthResponse } from '../types/auth';

const API_BASE = 'http://localhost:8080/api/v1';

const api = axios.create({ baseURL: API_BASE });

api.interceptors.request.use(config => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
});

let refreshPromise: Promise<string> | null = null;

api.interceptors.response.use(
    response => response,
    async error => {
        const original = error.config;
        if (error.response?.status !== 401 || original._retry) {
            return Promise.reject(error);
        }

        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
            return Promise.reject(error);
        }

        original._retry = true;

        if (!refreshPromise) {
            refreshPromise = axios
                .post<AuthResponse>(`${API_BASE}/auth/refresh`, { refreshToken })
                .then(res => {
                    localStorage.setItem('accessToken', res.data.accessToken);
                    localStorage.setItem('refreshToken', res.data.refreshToken);
                    return res.data.accessToken;
                })
                .finally(() => { refreshPromise = null; });
        }

        try {
            const newToken = await refreshPromise;
            original.headers.Authorization = `Bearer ${newToken}`;
            return api(original);
        } catch {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            return Promise.reject(error);
        }
    }
);

export default api;
