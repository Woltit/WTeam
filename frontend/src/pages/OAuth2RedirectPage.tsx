import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import usersApi from "../api/users";

const OAuth2RedirectPage = () => {
  const [searchParams] = useSearchParams();
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const accessToken = searchParams.get("accessToken");
    if (!accessToken) {
      setError("Токен авторизації не знайдено.");
      return;
    }

    dispatch({ type: "auth/tokensRefreshed", payload: { accessToken } });

    window.history.replaceState({}, "", "/oauth2/redirect");

    usersApi
      .getMe()
      .then(() => {
        navigate("/");
      })
      .catch(() => {
        dispatch({ type: "auth/logout" });
        setError("Не вдалося завантажити профіль. Спробуйте увійти знову.");
      });
  }, [searchParams, navigate, dispatch]);

  if (error) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <div className="alert alert-error">{error}</div>
          <a href="/login" className="btn btn-primary">
            Повернутися до входу
          </a>
        </div>
      </div>
    );
  }

  return (
    <div className="page-loader">
      <div className="spinner" />
      <p className="loader-text">Завершуємо вхід через Google…</p>
    </div>
  );
};

export default OAuth2RedirectPage;
