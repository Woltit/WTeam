import {createSlice, type PayloadAction}  from '@reduxjs/toolkit';
import type { UserResponse } from '../../types/user';

interface AuthState {
    user: UserResponse | null
    token: string | null
    refreshToken: string | null
    isLoading: boolean
}

const initialState: AuthState = {
    user: null,
    token: localStorage.getItem('token'),
    refreshToken: localStorage.getItem('refreshToken'),
    isLoading: false
}

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setCredentials (state, action: PayloadAction<{ user: UserResponse; token: string; refreshToken: string }>) {
            const { user, token, refreshToken } = action.payload;
            state.user = user;
            state.token = token;
            state.refreshToken = refreshToken;
            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);
        },

        logout (state) {
            state.user = null;
            state.token = null;
            state.refreshToken = null;
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
        }
        
    }
})

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;