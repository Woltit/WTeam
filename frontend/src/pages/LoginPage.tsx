import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { useAuth } from '../contexts/AuthContext';
import { GOOGLE_OAUTH_URL } from '../constants';
import { getApiErrorMessage } from '../utils/apiError';
import ThemeToggle from '../components/ThemeToggle';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

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
            setError(getApiErrorMessage(err, 'Помилка входу. Перевірте email та пароль.'));
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
                <h1 className="auth-title">Вхід</h1>
                <p className="auth-sub">Увійдіть, щоб орендувати або здавати речі</p>

                {error && <div className="alert alert-error">{error}</div>}

                <form className="auth-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label" htmlFor="email">Email</label>
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
                        <label className="form-label" htmlFor="password">Пароль</label>
                        <input
                            id="password"
                            type="password"
                            className="form-input"
                            placeholder="••••••••"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                        {loading ? <span className="spinner-sm" /> : 'Увійти'}
                    </button>
                </form>

                <div className="auth-divider"><span>або</span></div>

                <a href={GOOGLE_OAUTH_URL} className="btn btn-outline btn-full btn-google">
                    <span className="google-icon">G</span> Увійти через Google
                </a>

                <p className="auth-footer">
                    Немає акаунту? <Link to="/register">Зареєструватися</Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;
