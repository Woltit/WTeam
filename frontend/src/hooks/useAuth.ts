import { useSelector, useDispatch } from 'react-redux';
import type { RootState, AppDispatch } from '../store/store';
import { loginUser, registerUser, logoutUser } from '../store/slices/authSlice';
import type { LoginRequest, RegisterRequest } from '../types/auth';

export const useAuth = () => {
    const dispatch = useDispatch<AppDispatch>();
    const { user, accessToken, isAuthenticated, isLoading } = useSelector((state: RootState) => state.auth);

    const isAdmin = user?.role === 'ADMIN';

    const login = async (data: LoginRequest) => {
        await dispatch(loginUser(data)).unwrap();
    }

    const register = async (data: RegisterRequest) => {
        await dispatch(registerUser(data)).unwrap();
    }

    const logout = async () => {
        await dispatch(logoutUser());
    }

    return {
        user,
        accessToken,
        isAdmin,
        isAuthenticated,
        isLoading,
        login,
        register,
        logout
    };
};
