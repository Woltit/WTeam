import { createSlice, createAsyncThunk, type PayloadAction } from '@reduxjs/toolkit';
import type { UserResponse } from '../../types/user';
import type { LoginRequest, RegisterRequest } from '../../types/auth';
import authApi from '../../api/auth';
import usersApi from '../../api/users';

interface AuthState {
    user: UserResponse | null;
    accessToken: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}

const initialState: AuthState = {
    user: null,
    accessToken: null,
    isAuthenticated: false,
    isLoading: true
};

export const loginUser = createAsyncThunk(
    "auth/login",
    async (credentials: LoginRequest) => {
        const res = await authApi.login(credentials);
        const me = await usersApi.getMe();
        return { user: me, accessToken: res.accessToken };
    }
)

export const registerUser = createAsyncThunk(
    "auth/register",
    async (credentials: RegisterRequest) => {
        const res = await authApi.register(credentials);
        const me = await usersApi.getMe();
        return { user: me, accessToken: res.accessToken };
    }
)

export const initAuth = createAsyncThunk(
    "auth/init",
    async (_, { rejectWithValue }) => {
        try {
            const res = await authApi.refresh();
            const me = await usersApi.getMe();
            return { user: me, accessToken: res.accessToken };
        } catch (error) {
            return rejectWithValue('Session expired or no cookie found');
        }
    }
);

export const logoutUser = createAsyncThunk(
    "auth/logoutAction",
    async (_, { dispatch }) => {
        try {
            await authApi.logout();
        } catch (error) {
            console.error("Logout failed on backend", error);
        } finally {
            dispatch(logout());
        }
    }
);

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logout(state) {
            state.user = null;
            state.isAuthenticated = false;
        },
        tokensRefreshed(state, action: PayloadAction<{accessToken: string}>) {
            state.accessToken = action.payload.accessToken;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(loginUser.pending, (state) => {
                state.isLoading = true;
            })
            .addCase(loginUser.fulfilled, (state, action) => {
                state.user = action.payload.user;
                state.accessToken = action.payload.accessToken;
                state.isAuthenticated = true;
                state.isLoading = false;
            })
            .addCase(loginUser.rejected, (state) => {
                state.isLoading = false;
            })
            .addCase(registerUser.fulfilled, (state, action) => {
                state.user = action.payload.user;
                state.accessToken = action.payload.accessToken;
                state.isAuthenticated = true;
                state.isLoading = false;
            })
            .addCase(registerUser.rejected, (state) => {
                state.isLoading = false;
            })
            .addCase(initAuth.pending, (state) => {
                state.isLoading = true;
            })
            .addCase(initAuth.fulfilled, (state, action) => {
                state.user = action.payload.user;
                state.accessToken = action.payload.accessToken;
                state.isAuthenticated = true;
                state.isLoading = false;
            })
            .addCase(initAuth.rejected, (state) => {
                state.isLoading = false;
            })
    }
});

export const { logout, tokensRefreshed } = authSlice.actions;
export default authSlice.reducer;
