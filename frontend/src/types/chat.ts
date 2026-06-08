export interface ChatRoomResponse {
    id: number;
    bookingId: number;
    itemTitle: string;
    otherUserId: number;
    otherUserName: string;
    createdAt: string;
}

export interface MessageResponse {
    id: number;
    senderId: number;
    senderName: string;
    messageText: string;
    isRead: boolean;
    createdAt: string;
}

export interface AiQueryResponse {
    sessionId: number;
    aiResponse: string;
    recommendedItemIds: number[];
}
