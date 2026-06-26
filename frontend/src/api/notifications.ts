import api from './axios';
import type { NotificationResponse, PageResponse } from '../types/notification';

const getMyNotifications = async (page = 0, size = 20): Promise<PageResponse<NotificationResponse>> => {
    const response = await api.get<PageResponse<NotificationResponse>>('/notifications/me', {
        params: { page, size },
    });
    return response.data;
};

const markAsRead = async (notificationId: number): Promise<void> => {
    await api.patch(`/notifications/${notificationId}/read`);
};

export default { getMyNotifications, markAsRead };
