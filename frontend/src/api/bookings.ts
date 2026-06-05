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

export default { getAllBookings, updateBookingStatus };
