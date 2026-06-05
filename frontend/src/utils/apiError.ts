import axios from 'axios';

export function getApiErrorMessage(err: unknown, fallback: string): string {
    if (!axios.isAxiosError(err)) {
        return fallback;
    }

    if (!err.response) {
        return 'Сервер недоступний. Переконайтеся, що бекенд запущено (порт 8080).';
    }

    const data = err.response.data as { message?: string; detailedMessage?: string } | undefined;
    const message = data?.message;
    const details = data?.detailedMessage;

    if (message && details && message !== details) {
        return `${message}: ${details}`;
    }
    return message ?? details ?? fallback;
}
