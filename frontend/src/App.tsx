import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import BrowsePage from './pages/BrowsePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ItemDetailPage from './pages/ItemDetailPage';
import CreateItemPage from './pages/CreateItemPage';
import EditItemPage from './pages/EditItemPage';
import ProfilePage from './pages/ProfilePage';
import AdminPage from './pages/AdminPage';
import OAuth2RedirectPage from './pages/OAuth2RedirectPage';
import ChatsPage from './pages/ChatsPage';
import ChatPage from './pages/ChatPage';
import AiPage from './pages/AiPage';
import MyBookingsPage from './pages/MyBookingsPage';
import MyItemsPage from './pages/MyItemsPage';
import PayStubPage from './pages/PayStubPage';
import DocsPage from './pages/DocsPage';
import { Toaster } from 'react-hot-toast';
import { useDispatch } from 'react-redux';
import { useEffect } from 'react';
import { initAuth } from './store/slices/authSlice';
import { type AppDispatch } from './store/store';

function App() {
    const dispatch = useDispatch<AppDispatch>();
    useEffect(() => {
        dispatch(initAuth());
    }, [dispatch]);

    return (
        <>
            <Toaster position="top-center" />
            <Routes>
                <Route path="/oauth2/redirect" element={<OAuth2RedirectPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route element={<Layout />}>
                <Route path="/" element={<BrowsePage />} />
                <Route path="/docs" element={<DocsPage />} />
                <Route path="/items/:itemId" element={<ItemDetailPage />} />
                <Route
                    path="/items/create"
                    element={
                        <ProtectedRoute>
                            <CreateItemPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/items/:itemId/edit"
                    element={
                        <ProtectedRoute>
                            <EditItemPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <ProfilePage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/my-bookings"
                    element={
                        <ProtectedRoute>
                            <MyBookingsPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/my-items"
                    element={
                        <ProtectedRoute>
                            <MyItemsPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <ProtectedRoute requireAdmin>
                            <AdminPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/chats"
                    element={
                        <ProtectedRoute>
                            <ChatsPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/chats/:roomId"
                    element={
                        <ProtectedRoute>
                            <ChatPage />
                        </ProtectedRoute>
                    }
                />
                <Route path="/ai" element={<AiPage />} />
                <Route path="/pay-stub/:paymentId" element={<PayStubPage />} />
            </Route>
        </Routes>
        </>
    );

}

export default App;
