import api from './axios';
import type { UserProfileResponse, PublicProfileResponse, VerificationStatus } from '../types/user';

interface UserProfileRequest {
    lastName: string;
    firstName: string;
    middleName?: string | null;
    birthDate: string;
    phoneNumber: string;
    bio?: string | null;
}

interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

const getMyProfile = async () => {
    const response = await api.get<UserProfileResponse>('/profile');
    return response.data;
};

const updateMyProfile = async (data: UserProfileRequest) => {
    const response = await api.put<UserProfileResponse>('/profile', data);
    return response.data;
};

const getPublicProfile = async (userId: number) => {
    const response = await api.get<PublicProfileResponse>(`/profile/public/${userId}`);
    return response.data;
};

const uploadAvatar = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    await api.post('/profile/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    });
};

const getPendingProfiles = async (page = 0, size = 20) => {
    const response = await api.get<Page<UserProfileResponse>>('/profile/pending', { params: { page, size } });
    return response.data;
};

const updateVerificationStatus = async (userId: number, status: VerificationStatus) => {
    const response = await api.patch<UserProfileResponse>(`/profile/${userId}/verification-status`, null, {
        params: { status },
    });
    return response.data;
};

export default { getMyProfile, updateMyProfile, getPublicProfile, uploadAvatar, getPendingProfiles, updateVerificationStatus };
