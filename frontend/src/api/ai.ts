import api from './axios';
import type { AiQueryResponse } from '../types/chat';

const recommend = async (query: string): Promise<AiQueryResponse> => {
    const res = await api.post<AiQueryResponse>('/ai/recommend', { query });
    return res.data;
};

export default { recommend };
