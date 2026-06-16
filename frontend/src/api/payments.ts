import api from './axios';

export interface LiqPayCheckoutResponse {
    data: string;
    signature: string;
}

const paymentsApi = {
    createPaymentCheckout: async (bookingId: number): Promise<LiqPayCheckoutResponse> => {
        const response = await api.post<LiqPayCheckoutResponse>(`/payments/create?bookingId=${bookingId}`);
        return response.data;
    },
    
    verifyPaymentStatus: async (bookingId: number): Promise<void> => {
        await api.post(`/payments/verify?bookingId=${bookingId}`);
    },

    callbackStub: async (paymentId: number, success: boolean): Promise<void> => {
        await api.post(`/payments/callback-stub?paymentId=${paymentId}&success=${success}`);
    }
};

export default paymentsApi;
