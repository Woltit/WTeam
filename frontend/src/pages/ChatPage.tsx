import { useEffect, useRef, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import chatApi from '../api/chat';
import { useAuth } from '../hooks/useAuth';
import type { ChatRoomResponse, MessageResponse } from '../types/chat';
import { useLanguage } from '../contexts/LanguageContext';

const ChatPage = () => {
    const { roomId } = useParams<{ roomId: string }>();
    const { user, accessToken: token } = useAuth();
    const [room, setRoom] = useState<ChatRoomResponse | null>(null);
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [text, setText] = useState('');
    const [sending, setSending] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [connected, setConnected] = useState(false);
    const bottomRef = useRef<HTMLDivElement>(null);
    const stompRef = useRef<import('@stomp/stompjs').Client | null>(null);
    const { language, t } = useLanguage();

    const id = Number(roomId);

    useEffect(() => {
        if (!id) return;
        Promise.all([
            chatApi.getMyRooms().then(rooms => rooms.find(r => r.id === id) ?? null),
            chatApi.getMessages(id),
        ])
            .then(([foundRoom, msgs]) => {
                setRoom(foundRoom);
                setMessages(msgs);
                chatApi.markAsRead(id).catch(() => {});
            })
            .catch(() => setError(t('chats.loadChatError')))
            .finally(() => setLoading(false));
    }, [id, t]);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    useEffect(() => {
        if (!token || !id) return;

        let client: import('@stomp/stompjs').Client;

        const WS_URL = 'http://localhost:8080/api/v1/ws';

        Promise.all([
            import('@stomp/stompjs'),
            import('sockjs-client'),
        ]).then(([{ Client }, { default: SockJS }]) => {
            client = new Client({
                webSocketFactory: () => new SockJS(WS_URL) as WebSocket,
                connectHeaders: { Authorization: `Bearer ${token}` },
                reconnectDelay: 5000,
                onConnect: () => {
                    setConnected(true);
                    client.subscribe(`/topic/chat-rooms/${id}`, frame => {
                        const msg: MessageResponse = JSON.parse(frame.body);
                        setMessages(prev => {
                            if (prev.some(m => m.id === msg.id)) return prev;
                            return [...prev, msg];
                        });
                        chatApi.markAsRead(id).catch(() => {});
                    });
                },
                onDisconnect: () => setConnected(false),
                onStompError: () => setConnected(false),
            });

            client.activate();
            stompRef.current = client;
        });

        return () => {
            stompRef.current?.deactivate();
        };
    }, [token, id]);

    const handleSend = async (e: React.SyntheticEvent<HTMLFormElement>) => {
        e.preventDefault();
        const trimmed = text.trim();
        if (!trimmed || sending) return;

        setSending(true);
        try {
            if (connected && stompRef.current?.connected) {
                stompRef.current.publish({
                    destination: `/app/chat-rooms/${id}/send`,
                    body: JSON.stringify({ messageText: trimmed }),
                });
                setText('');
            } else {
                const msg = await chatApi.sendMessage(id, trimmed);
                setMessages(prev => [...prev, msg]);
                setText('');
            }
        } catch {
            setError(t('chats.sendError'));
        } finally {
            setSending(false);
        }
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;
    if (error && !room) return <div className="page"><div className="alert alert-error">{error}</div></div>;

    return (
        <div className="page chat-page">
            <div className="chat-header">
                <Link to="/chats" className="btn btn-outline btn-sm">{t('browse.prev')}</Link>
                <div className="chat-header-info">
                    <div className="chat-header-name">{room?.otherUserName ?? t('chats.defaultName')}</div>
                    <div className="chat-header-sub">{room?.itemTitle}</div>
                </div>
                <span className={`chat-status-dot ${connected ? 'connected' : ''}`} title={connected ? t('chats.connected') : t('chats.offline')} />
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            <div className="chat-messages">
                {messages.length === 0 && (
                    <div className="chat-empty">{t('chats.emptyChat')}</div>
                )}
                {messages.map(msg => {
                    const isMine = msg.senderId === user?.id;
                    return (
                        <div key={msg.id} className={`chat-bubble-wrap ${isMine ? 'mine' : 'theirs'}`}>
                            {!isMine && <div className="chat-sender">{msg.senderName}</div>}
                            <div className={`chat-bubble ${isMine ? 'bubble-mine' : 'bubble-theirs'}`}>
                                {msg.messageText}
                            </div>
                            <div className="chat-time">
                                {new Date(msg.createdAt.endsWith('Z') ? msg.createdAt : `${msg.createdAt}Z`).toLocaleTimeString(language === 'ua' ? 'uk-UA' : 'en-US', { hour: '2-digit', minute: '2-digit' })}
                            </div>
                        </div>
                    );
                })}
                <div ref={bottomRef} />
            </div>

            <form className="chat-input-row" onSubmit={handleSend}>
                <input
                    className="form-input chat-input"
                    placeholder={t('chats.placeholder')}
                    value={text}
                    onChange={e => setText(e.target.value)}
                    disabled={sending}
                    maxLength={2000}
                />
                <button className="btn btn-primary" type="submit" disabled={sending || !text.trim()}>
                    {sending ? <span className="spinner-sm" /> : t('chats.send')}
                </button>
            </form>
        </div>
    );
};

export default ChatPage;
