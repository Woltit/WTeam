import { useEffect, useState } from 'react';
import notificationsApi from '../api/notifications';
import type { NotificationResponse } from '../types/notification';
import { useLanguage } from '../contexts/LanguageContext';

const NotificationsPage = () => {
    const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { t } = useLanguage();

    useEffect(() => {
        notificationsApi.getMyNotifications()
            .then(page => setNotifications(page.content))
            .catch(() => setError(t('notifications.loadError')))
            .finally(() => setLoading(false));
    }, [t]);

    const handleMarkAsRead = async (notificationId: number) => {
        try {
            await notificationsApi.markAsRead(notificationId);
            setNotifications(prev =>
                prev.map(n => n.id === notificationId ? { ...n, isRead: true } : n)
            );
        } catch {
            setError(t('notifications.markReadError'));
        }
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('notifications.title')}</h1>
                <p className="page-subtitle">{t('notifications.subtitle')}</p>
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            {notifications.length === 0 && !error ? (
                <div className="empty-state">
                    <div className="empty-icon">🔔</div>
                    <p>{t('notifications.empty')}</p>
                    <p style={{ fontSize: '0.875rem' }}>{t('notifications.emptyHint')}</p>
                </div>
            ) : (
                <div className="chat-list">
                    {notifications.map(notification => (
                        <div
                            key={notification.id}
                            className={`chat-list-item ${notification.isRead ? '' : 'notification-unread'}`}
                            style={{ cursor: 'default' }}
                        >
                            <div className="chat-list-avatar">📧</div>
                            <div className="chat-list-body">
                                <div className="chat-list-name">{notification.title}</div>
                                <div className="chat-list-sub">{notification.body}</div>
                                <div className="chat-list-sub" style={{ marginTop: '0.25rem' }}>
                                    {new Date(notification.createdAt).toLocaleString()}
                                </div>
                            </div>
                            {!notification.isRead && (
                                <button
                                    type="button"
                                    className="btn btn-outline btn-sm"
                                    onClick={() => handleMarkAsRead(notification.id)}
                                >
                                    {t('notifications.markRead')}
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default NotificationsPage;
