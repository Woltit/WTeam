import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useLanguage } from '../contexts/LanguageContext';
import ThemeToggle from './ThemeToggle';

const Navbar = () => {
    const { user, isAdmin, logout } = useAuth();
    const { language, setLanguage, t } = useLanguage();
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
                    {t('nav.catalog')}
                </NavLink>
                <NavLink to="/ai" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                    {t('nav.aiHelper')}
                </NavLink>
                <NavLink to="/docs" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                    {language === 'ua' ? 'Документація' : 'Docs'}
                </NavLink>
                {user && (
                    <>
                        <NavLink to="/chats" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            {t('nav.chats')}
                        </NavLink>
                        <NavLink to="/my-bookings" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            {t('nav.myBookings')}
                        </NavLink>
                        <NavLink to="/my-items" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            {t('nav.myItems')}
                        </NavLink>
                        <NavLink to="/items/create" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            {t('nav.addListing')}
                        </NavLink>
                        <NavLink to="/profile" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            {t('nav.profile')}
                        </NavLink>
                    </>
                )}
                {isAdmin && (
                    <NavLink to="/admin" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                        {t('nav.admin')}
                    </NavLink>
                )}
            </div>

            <div className="navbar-actions">
                <div className="lang-switcher">
                    <button 
                        className={`lang-btn ${language === 'ua' ? 'active' : ''}`} 
                        onClick={() => setLanguage('ua')}
                    >
                        UA
                    </button>
                    <span className="lang-separator">|</span>
                    <button 
                        className={`lang-btn ${language === 'en' ? 'active' : ''}`} 
                        onClick={() => setLanguage('en')}
                    >
                        EN
                    </button>
                </div>
                <ThemeToggle />
                {user ? (
                    <div className="navbar-user">
                        <span className="navbar-user-name">
                            {user.profile?.firstName || user.profile?.lastName
                                ? `${user.profile.firstName ?? ''} ${user.profile.lastName ?? ''}`.trim()
                                : user.email}
                        </span>
                        <button className="btn btn-outline btn-sm" onClick={handleLogout}>
                            {t('nav.logout')}
                        </button>
                    </div>
                ) : (
                    <div className="navbar-auth">
                        <Link to="/login" className="btn btn-outline btn-sm">{t('nav.login')}</Link>
                        <Link to="/register" className="btn btn-primary btn-sm">{t('nav.register')}</Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
