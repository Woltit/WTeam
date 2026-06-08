import { Link, NavLink, useNavigate } from 'react-router';
import { useAuth } from '../contexts/AuthContext';
import ThemeToggle from './ThemeToggle';

const Navbar = () => {
    const { user, isAdmin, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <Link to="/" className="navbar-brand">
                <span className="navbar-logo">⬡</span>
                RentGo
            </Link>

            <div className="navbar-links">
                <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                    Каталог
                </NavLink>
                <NavLink to="/ai" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                    ✨ AI Помічник
                </NavLink>
                {user && (
                    <>
                        <NavLink to="/chats" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            Чати
                        </NavLink>
                        <NavLink to="/items/create" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            Додати оголошення
                        </NavLink>
                        <NavLink to="/profile" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            Профіль
                        </NavLink>
                    </>
                )}
                {isAdmin && (
                    <NavLink to="/admin" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                        Адмін
                    </NavLink>
                )}
            </div>

            <div className="navbar-actions">
                <ThemeToggle />
                {user ? (
                    <div className="navbar-user">
                        <span className="navbar-user-name">
                            {user.profile?.firstName || user.profile?.lastName
                                ? `${user.profile.firstName ?? ''} ${user.profile.lastName ?? ''}`.trim()
                                : user.email}
                        </span>
                        <button className="btn btn-outline btn-sm" onClick={handleLogout}>
                            Вийти
                        </button>
                    </div>
                ) : (
                    <div className="navbar-auth">
                        <Link to="/login" className="btn btn-outline btn-sm">Увійти</Link>
                        <Link to="/register" className="btn btn-primary btn-sm">Реєстрація</Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
