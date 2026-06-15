import api from './axios';

export interface CategoryStat {
    categoryName: string;
    itemCount: number;
}

export interface AdminStatsResponse {
    totalUsers: number;
    activeBookings: number;
    completedBookings: number;
    totalItems: number;
    topCategories: CategoryStat[];
}

const getStats = async (): Promise<AdminStatsResponse> => {
    const response = await api.get<AdminStatsResponse>('/admin/stats');
    return response.data;
};

export default { getStats };
