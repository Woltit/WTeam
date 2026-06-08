import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import chatApi from '../api/chat';
import type { ChatRoomResponse } from '../types/chat';

const ChatsPage = () => {
    const [rooms, setRooms] = useState<ChatRoomResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        chatApi.getMyRooms()
            .then(setRooms)
            .catch(() => setError('Не вдалося завантажити чати.'))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">Мої чати</h1>
                <p className="page-subtitle">Переписка з орендодавцями та орендарями</p>
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            {rooms.length === 0 && !error ? (
                <div className="empty-state">
                    <div className="empty-icon">💬</div>
                    <p>У вас ще немає активних чатів.</p>
                    <p style={{ fontSize: '0.875rem' }}>Зробіть бронювання, щоб почати спілкування з власником.</p>
                    <Link to="/" className="btn btn-primary" style={{ marginTop: '1rem' }}>
                        Переглянути оголошення
                    </Link>
                </div>
            ) : (
                <div className="chat-list">
                    {rooms.map(room => (
                        <Link key={room.id} to={`/chats/${room.id}`} className="chat-list-item">
                            <div className="chat-list-avatar">
                                {room.otherUserName?.[0]?.toUpperCase() ?? '?'}
                            </div>
                            <div className="chat-list-body">
                                <div className="chat-list-name">{room.otherUserName}</div>
                                <div className="chat-list-sub">{room.itemTitle}</div>
                            </div>
                            <div className="chat-list-arrow">›</div>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ChatsPage;
