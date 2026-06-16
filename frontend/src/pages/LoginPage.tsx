
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { GOOGLE_OAUTH_URL } from '../constants';
import { getApiErrorMessage } from '../utils/apiError';
import ThemeToggle from '../components/ThemeToggle';
import { useLanguage } from '../contexts/LanguageContext';

import { Eye, EyeOff } from 'lucide-react';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { t } = useLanguage();

    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await login({ email, password });
            navigate('/');
        } catch (err: unknown) {
            setError(getApiErrorMessage(err, t('login.error'), t('errors.serverDown')));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-theme-toggle">
                <ThemeToggle />
            </div>
            <div className="auth-card">
                <Link to="/" className="auth-brand">
                    <span className="navbar-logo">⬡</span> RentGo
                </Link>
                <h1 className="auth-title">{t('login.title')}</h1>
                <p className="auth-sub">{t('login.subtitle')}</p>

                {error && <div className="alert alert-error">{error}</div>}

                <form className="auth-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label" htmlFor="email">{t('login.email')}</label>
                        <input
                             id="email"
                             type="email"
                             className="form-input"
                             placeholder="you@example.com"
                             value={email}
                             onChange={e => setEmail(e.target.value)}
                             required
                         />
                    </div>
                    <div className="form-group">
                        <label className="form-label" htmlFor="password">{t('login.password')}</label>
                        <div style={{ position: 'relative' }}>
                            <input
                                 id="password"
                                 type={showPassword ? "text" : "password"}
                                 className="form-input"
                                 style={{ paddingRight: '2.5rem' }}
                                 placeholder="••••••••"
                                 value={password}
                                 onChange={e => setPassword(e.target.value)}
                                 required
                             />
                             <button
                                 type="button"
                                 onClick={() => setShowPassword(!showPassword)}
                                 style={{
                                     position: 'absolute',
                                     right: '0.75rem',
                                     top: '50%',
                                     transform: 'translateY(-50%)',
                                     background: 'none',
                                     border: 'none',
                                     cursor: 'pointer',
                                     color: 'var(--text-muted)'
                                 }}
                             >
                                 {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                             </button>
                        </div>
                    </div>
                    <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                        {loading ? <span className="spinner-sm" /> : t('login.submit')}
                    </button>
                </form>

                <div className="auth-divider"><span>{t('login.or')}</span></div>

                <a href={GOOGLE_OAUTH_URL} className="btn btn-outline btn-full btn-google">
                    <span className="google-icon">G</span> {t('login.googleSubmit')}
                </a>

                <p className="auth-footer">
                    {t('login.noAccount')} <Link to="/register">{t('login.registerLink')}</Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;

