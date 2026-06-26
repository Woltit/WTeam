export type NotificationType =
    | 'BOOKING_REQUEST'
    | 'BOOKING_APPROVED'
    | 'BOOKING_REJECTED'
    | 'BOOKING_CANCELLED'
    | 'PAYMENT_RECEIVED'
    | 'REVIEW_LEFT'
    | 'VERIFICATION_APPROVED'
    | 'VERIFICATION_REJECTED'
    | 'DISPUTE_OPENED';

export interface NotificationResponse {
    id: number;
    type: NotificationType;
    title: string;
    body: string;
    isRead: boolean;
    createdAt: string;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}
