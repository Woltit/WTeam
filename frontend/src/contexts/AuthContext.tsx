import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import authApi from '../api/auth';
import usersApi from '../api/users';
import type { LoginRequest, RegisterRequest } from '../types/auth';
import type { UserResponse } from '../types/user';

interface AuthContextValue {
    user: UserResponse | null;
    token: string | null;
    isAdmin: boolean;
    isLoading: boolean;
    login: (data: LoginRequest) => Promise<void>;
    register: (data: RegisterRequest) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
    const [isLoading, setIsLoading] = useState(true);

    const logout = useCallback(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        setToken(null);
        setUser(null);
    }, []);

    useEffect(() => {
        const stored = localStorage.getItem('token');
        if (stored) {
            setToken(stored);
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
        localStorage.setItem('token', res.token);
        localStorage.setItem('refreshToken', res.refreshToken);
        setToken(res.token);
        const me = await usersApi.getMe();
        setUser(me);
    };

    const register = async (data: RegisterRequest) => {
        const res = await authApi.register(data);
        localStorage.setItem('token', res.token);
        localStorage.setItem('refreshToken', res.refreshToken);
        setToken(res.token);
        const me = await usersApi.getMe();
        setUser(me);
    };

    const isAdmin = user?.role === 'ADMIN';

    return (
        <AuthContext.Provider value={{ user, token, isAdmin, isLoading, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
};
