import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { GOOGLE_OAUTH_URL } from "../constants";
import { getApiErrorMessage } from "../utils/apiError";
import ThemeToggle from "../components/ThemeToggle";
import { useLanguage } from "../contexts/LanguageContext";

function RegisterPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [checkPassword, setCheckPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { t } = useLanguage();

  const navigate = useNavigate();
  const { register } = useAuth();

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    if (password !== checkPassword) {
        setError(t('register.passwordMismatch'));
        return;
    }
    if (password.length < 8 || password.length > 20) {
        setError(t('register.passwordLength'));
        return;
    }

    setLoading(true);
    try {
        await register({ email, password, checkPassword });
        navigate("/");
    } catch (err: unknown) {
        setError(getApiErrorMessage(err, t('register.error'), t('errors.serverDown')));
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
        <h1 className="auth-title">{t('register.title')}</h1>
        <p className="auth-sub">{t('register.subtitle')}</p>

        {error && <div className="alert alert-error">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              {t('login.email')}
            </label>
            <input
              id="email"
              type="email"
              className="form-input"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="password">
              {t('login.password')}
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              placeholder={t('register.passwordPlaceholder')}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="checkPassword">
              {t('register.confirmPassword')}
            </label>
            <input
              id="checkPassword"
              type="password"
              className="form-input"
              placeholder={t('register.confirmPasswordPlaceholder')}
              value={checkPassword}
              onChange={(e) => setCheckPassword(e.target.value)}
              required
            />
          </div>
          <button
            type="submit"
            className="btn btn-primary btn-full"
            disabled={loading}
          >
            {loading ? <span className="spinner-sm" /> : t('register.submit')}
          </button>
        </form>

        <div className="auth-divider">
          <span>{t('login.or')}</span>
        </div>

        <a
          href={GOOGLE_OAUTH_URL}
          className="btn btn-outline btn-full btn-google"
        >
          <span className="google-icon">G</span> {t('register.googleSubmit')}
        </a>

        <p className="auth-footer">
          {t('register.hasAccount')} <Link to="/login">{t('register.loginLink')}</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
