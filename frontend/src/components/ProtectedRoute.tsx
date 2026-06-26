import { type ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface Props {
    children: ReactNode;
    requireAdmin?: boolean;
}

const ProtectedRoute = ({ children, requireAdmin = false }: Props) => {
    const { accessToken: token, isAdmin, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="page-loader">
                <div className="spinner" />
            </div>
        );
    }

    if (!token) return <Navigate to="/login" replace />;
    if (requireAdmin && !isAdmin) return <Navigate to="/" replace />;

    return <>{children}</>;
};

export default ProtectedRoute;
