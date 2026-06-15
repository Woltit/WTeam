import api from './axios';

export type BookingStatus =
    | 'PENDING'
    | 'APPROVED'
    | 'REJECTED'
    | 'PAID'
    | 'IN_PROGRESS'
    | 'COMPLETED'
    | 'CANCELLED'
    | 'DISPUTE';

export interface UnavailableDateRange {
    startDate: string;
    endDate: string;
}

export interface BookingResponse {
    id: number;
    itemId: number;
    renterId: number;
    startDate: string;
    endDate: string;
    totalPrice: number;
    depositTotal: number;
    pricePerDaySnapshot: number;
    status: BookingStatus;
    cancellationReason?: string;
}

interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

const getAllBookings = async (page = 0, size = 20) => {
    const response = await api.get<Page<BookingResponse>>('/bookings', { params: { page, size } });
    return response.data;
};

const getMyBookings = async (page = 0, size = 20) => {
    const response = await api.get<Page<BookingResponse>>('/bookings/my', { params: { page, size } });
    return response.data;
};

const getOwnerBookings = async (page = 0, size = 20) => {
    const response = await api.get<Page<BookingResponse>>('/bookings/owner', { params: { page, size } });
    return response.data;
};

const createBooking = async (itemId: number, startDate: string, endDate: string) => {
    const response = await api.post<BookingResponse>('/bookings', { itemId, startDate, endDate });
    return response.data;
};

const updateBookingStatus = async (
    bookingId: number,
    status: BookingStatus,
    cancellationReason?: string,
) => {
    const response = await api.patch<BookingResponse>(`/bookings/${bookingId}/status`, {
        status,
        cancellationReason: cancellationReason ?? null,
    });
    return response.data;
};

const getUnavailableDates = async (itemId: number) => {
    const response = await api.get<UnavailableDateRange[]>(`/bookings/items/${itemId}/unavailable-dates`);
    return response.data;
};

const approveBooking = async (bookingId: number) => {
    const response = await api.patch<BookingResponse>(`/bookings/${bookingId}/approve`);
    return response.data;
};

const rejectBooking = async (bookingId: number) => {
    const response = await api.patch<BookingResponse>(`/bookings/${bookingId}/reject`);
    return response.data;
};

const cancelBooking = async (bookingId: number, cancellationReason?: string) => {
    const response = await api.patch<BookingResponse>(`/bookings/${bookingId}/cancel`, { cancellationReason });
    return response.data;
};

const completeBooking = async (bookingId: number) => {
    const response = await api.patch<BookingResponse>(`/bookings/${bookingId}/complete`);
    return response.data;
};

export default {
    getAllBookings,
    getMyBookings,
    getOwnerBookings,
    createBooking,
    updateBookingStatus,
    getUnavailableDates,
    approveBooking,
    rejectBooking,
    cancelBooking,
    completeBooking,
};
