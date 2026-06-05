import { Navigate, type ReactNode } from 'react-router';
import { useAuth } from '../contexts/AuthContext';

interface Props {
    children: ReactNode;
    requireAdmin?: boolean;
}

const ProtectedRoute = ({ children, requireAdmin = false }: Props) => {
    const { token, isAdmin, isLoading } = useAuth();

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
