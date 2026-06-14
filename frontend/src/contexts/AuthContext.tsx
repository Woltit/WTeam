import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import authApi from '../api/auth';
import usersApi from '../api/users';
import type { LoginRequest, RegisterRequest } from '../types/auth';
import type { UserResponse } from '../types/user';

interface AuthContextValue {
    user: UserResponse | null;
    accessToken: string | null;
    isAdmin: boolean;
    isLoading: boolean;
    login: (data: LoginRequest) => Promise<void>;
    register: (data: RegisterRequest) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem('accessToken'));
    const [isLoading, setIsLoading] = useState(true);

    const logout = useCallback(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setAccessToken(null);
        setUser(null);
    }, []);

    useEffect(() => {
        const stored = localStorage.getItem('accessToken');
        if (stored) {
            setAccessToken(stored);
            usersApi.getMe()
                .then(setUser)
                .catch(() => logout())
                .finally(() => setIsLoading(false));
        } else {
            setIsLoading(false);
        }
    }, [logout]);

    const login = async (data: LoginRequest) => {
        const res = await authApi.login(data);
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        setAccessToken(res.accessToken);
        const me = await usersApi.getMe();
        setUser(me);
    };

    const register = async (data: RegisterRequest) => {
        const res = await authApi.register(data);
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        setAccessToken(res.accessToken);
        const me = await usersApi.getMe();
        setUser(me);
    };

    const isAdmin = user?.role === 'ADMIN';

    return (
        <AuthContext.Provider value={{ user, accessToken, isAdmin, isLoading, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
};
