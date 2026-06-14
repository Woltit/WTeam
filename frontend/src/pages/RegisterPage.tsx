import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { GOOGLE_OAUTH_URL } from "../constants";
import { getApiErrorMessage } from "../utils/apiError";
import ThemeToggle from "../components/ThemeToggle";

function RegisterPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [checkPassword, setCheckPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { register } = useAuth();

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    if (password !== checkPassword) {
        setError("Паролі не збігаються.");
        return;
    }
    if (password.length < 8) {
        setError("Пароль має містити від 8 до 20 символів.");
        return;
    }
    if (password.length > 20) {
        setError("Пароль має містити від 8 до 20 символів.");
        return;
    }

    setLoading(true);
    try {
        await register({ email, password, checkPassword });
        navigate("/");
    } catch (err: unknown) {
        setError(
        getApiErrorMessage(
            err,
            "Не вдалося зареєструватися. Спробуйте ще раз.",
        ),
        );
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
        <h1 className="auth-title">Реєстрація</h1>
        <p className="auth-sub">Створіть акаунт і почніть орендувати</p>

        {error && <div className="alert alert-error">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email
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
              Пароль
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              placeholder="8–20 символів"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="checkPassword">
              Підтвердіть пароль
            </label>
            <input
              id="checkPassword"
              type="password"
              className="form-input"
              placeholder="повторіть пароль"
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
            {loading ? <span className="spinner-sm" /> : "Зареєструватися"}
          </button>
        </form>

        <div className="auth-divider">
          <span>або</span>
        </div>

        <a
          href={GOOGLE_OAUTH_URL}
          className="btn btn-outline btn-full btn-google"
        >
          <span className="google-icon">G</span> Продовжити з Google
        </a>

        <p className="auth-footer">
          Вже є акаунт? <Link to="/login">Увійти</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
