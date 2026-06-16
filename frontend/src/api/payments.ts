import api from './axios';

export interface PaymentUrlResponse {
    payUrl: string;
}

const paymentsApi = {
    createPaymentUrl: async (bookingId: number): Promise<string> => {
        const response = await api.post<PaymentUrlResponse>(`/payments/create?bookingId=${bookingId}`);
        return response.data.payUrl;
    },
    
    callbackStub: async (paymentId: number, success: boolean): Promise<void> => {
        await api.post(`/payments/callback-stub?paymentId=${paymentId}&success=${success}`);
    }
};

export default paymentsApi;
