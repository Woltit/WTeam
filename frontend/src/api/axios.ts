import axios from 'axios';

import { translations } from '../contexts/LanguageContext';
import type { Language } from '../contexts/LanguageContext';

let store: any;
export const injectStore = (_store: any) => {
    store = _store;
};

const API_BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

const api = axios.create({ 
    baseURL: API_BASE,
    withCredentials: true
});

api.interceptors.request.use(config => {
    const accessToken = store?.getState().auth.accessToken;
    if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
});

let refreshPromise: Promise<any> | null = null;

api.interceptors.response.use(
    response => response,
    async error => {
        const original = error.config;

        if (error.response?.status !== 401 || original._retry || original.url === "/auth/refresh") {
            return Promise.reject(error);
        }

        original._retry = true;

        if (!refreshPromise) {
            refreshPromise = axios.post(`${API_BASE}/auth/refresh`, {}, { withCredentials: true })
                .then(res => {
                    store?.dispatch({
                        type: "auth/tokensRefreshed",
                        payload: { accessToken: res.data.accessToken }
                    })
                    return res.data.accessToken;
                })
                .finally(() => { refreshPromise = null; });
        }

        try {
            const newToken = await refreshPromise;
            original.headers.Authorization = `Bearer ${newToken}`;
            return api(original);
        } catch {
            store?.dispatch({ type: 'auth/logout' });
            window.location.href = '/login';
            return Promise.reject(error);
        }
    }
);

const translateError = (backendMessage: string): string => {
    if (!backendMessage) return backendMessage;
    const msg = backendMessage.toLowerCase();
    
    const lang = (localStorage.getItem('rentgo-language') as Language) || 'ua';
    const dict = translations[lang] || translations['ua'];

    if (msg.includes('bad credentials')) return dict['errors.badCredentials'] || backendMessage;
    if (msg.includes('user not found')) return dict['errors.userNotFound'] || backendMessage;
    if (msg.includes('email already exists')) return dict['errors.emailTaken'] || backendMessage;
    if (msg.includes('access is denied')) return dict['errors.accessDenied'] || backendMessage;
    if (msg.includes('incomplete or not verified')) return dict['errors.profileIncomplete'] || backendMessage;
    if (msg.includes('item with id') && msg.includes('not found')) return dict['errors.itemNotFound'] || backendMessage;
    if (msg.includes('booking') && msg.includes('not found')) return dict['errors.bookingNotFound'] || backendMessage;
    if (msg.includes('jwt') || msg.includes('token')) return dict['errors.sessionExpired'] || backendMessage;

    return backendMessage;
};

api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.data?.message) {
            error.response.data.message = translateError(error.response.data.message);
        }
        return Promise.reject(error);
    }
);

export default api;
