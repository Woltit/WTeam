import api from './axios';

export interface UserReviewResponse {
    id: number;
    targetUserId: number;
    reviewerId: number;
    bookingId: number;
    targetRole: 'RENTER' | 'OWNER';
    rating: number;
    comment: string;
    status: 'PENDING' | 'PUBLISHED';
    createdAt: string;
}

const getUserReviews = async (userId: number): Promise<UserReviewResponse[]> => {
    const response = await api.get<UserReviewResponse[]>(`/users/${userId}/reviews`);
    return response.data;
};

export default { getUserReviews };
