
import { Route, Routes } from 'react-router';
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

function App() {
    return (
        <Routes>
            <Route path="/oauth2/redirect" element={<OAuth2RedirectPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route element={<Layout />}>
                <Route path="/" element={<BrowsePage />} />
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
            </Route>
        </Routes>
    );

}

export default App;
