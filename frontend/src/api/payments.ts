import api from './axios';

export interface StripeCheckoutResponse {
    url: string;
}

const paymentsApi = {
    createPaymentCheckout: async (bookingId: number): Promise<StripeCheckoutResponse> => {
        const response = await api.post<StripeCheckoutResponse>(`/payments/create?bookingId=${bookingId}`);
        return response.data;
    },
    
    verifyPaymentStatus: async (bookingId: number): Promise<void> => {
        await api.post(`/payments/verify?bookingId=${bookingId}`);
    },

    callbackStub: async (paymentId: number, success: boolean): Promise<void> => {
        await api.post(`/payments/stub`, { paymentId, success });
    }
};

export default paymentsApi;
