import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router';
import usersApi from '../api/users';

const OAuth2RedirectPage = () => {
    const [searchParams] = useSearchParams();
    const [error, setError] = useState('');

    useEffect(() => {
        const token = searchParams.get('token');
        if (!token) {
            setError('Токен авторизації не знайдено.');
            return;
        }

        localStorage.setItem('token', token);
        window.history.replaceState({}, '', '/oauth2/redirect');

        usersApi.getMe()
            .then(() => { window.location.replace('/'); })
            .catch(() => {
                localStorage.removeItem('token');
                setError('Не вдалося завантажити профіль. Спробуйте увійти знову.');
            });
    }, [searchParams]);

    if (error) {
        return (
            <div className="auth-page">
                <div className="auth-card">
                    <div className="alert alert-error">{error}</div>
                    <a href="/login" className="btn btn-primary">Повернутися до входу</a>
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
