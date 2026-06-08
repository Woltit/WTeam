import api from './axios';
import type { ChatRoomResponse, MessageResponse } from '../types/chat';

const getOrCreateRoom = async (bookingId: number): Promise<ChatRoomResponse> => {
    const res = await api.post<ChatRoomResponse>(`/chat-rooms/booking/${bookingId}`);
    return res.data;
};

const getMyRooms = async (): Promise<ChatRoomResponse[]> => {
    const res = await api.get<ChatRoomResponse[]>('/chat-rooms');
    return res.data;
};

const getMessages = async (roomId: number): Promise<MessageResponse[]> => {
    const res = await api.get<MessageResponse[]>(`/chat-rooms/${roomId}/messages`);
    return res.data;
};

const sendMessage = async (roomId: number, messageText: string): Promise<MessageResponse> => {
    const res = await api.post<MessageResponse>(`/chat-rooms/${roomId}/messages`, { messageText });
    return res.data;
};

const markAsRead = async (roomId: number): Promise<void> => {
    await api.patch(`/chat-rooms/${roomId}/read`);
};

export default { getOrCreateRoom, getMyRooms, getMessages, sendMessage, markAsRead };
