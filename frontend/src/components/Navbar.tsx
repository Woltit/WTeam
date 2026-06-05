import { Link, NavLink, useNavigate } from 'react-router';
import { useAuth } from '../contexts/AuthContext';

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
                    Browse
                </NavLink>
                {user && (
                    <>
                        <NavLink to="/items/create" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            List Item
                        </NavLink>
                        <NavLink to="/profile" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            Profile
                        </NavLink>
                    </>
                )}
                {isAdmin && (
                    <NavLink to="/admin" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                        Admin
                    </NavLink>
                )}
            </div>

            <div className="navbar-actions">
                {user ? (
                    <div className="navbar-user">
                        <span className="navbar-user-name">
                            {user.profile.firstName} {user.profile.lastName}
                        </span>
                        <button className="btn btn-outline btn-sm" onClick={handleLogout}>
                            Logout
                        </button>
                    </div>
                ) : (
                    <div className="navbar-auth">
                        <Link to="/login" className="btn btn-outline btn-sm">Login</Link>
                        <Link to="/register" className="btn btn-primary btn-sm">Sign Up</Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
