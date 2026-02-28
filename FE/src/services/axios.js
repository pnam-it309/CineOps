import axios from 'axios';
import router from '@/routes';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8888',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10s timeout
});

// Request Interceptor - luôn lấy token mới nhất từ sessionStorage
instance.interceptors.request.use(
    (config) => {
        const token = sessionStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Queue for requests while refreshing
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach((prom) => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// Response Interceptor
instance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Chỉ xử lý 401 và chưa retry
        if (error.response?.status === 401 && !originalRequest._retry) {
            // Nếu chính request refresh bị 401 → session chết thật
            if (originalRequest.url?.includes('/auth/refresh')) {
                sessionStorage.removeItem('token');
                sessionStorage.removeItem('refreshToken');
                // Chỉ redirect nếu đang ở trang yêu cầu auth
                const currentRoute = router.currentRoute?.value;
                if (currentRoute?.meta?.requiresAuth) {
                    router.push({ name: ROUTES_CONSTANTS.LOGIN.name });
                }
                return Promise.reject(error);
            }

            // Nếu đang refresh → xếp hàng chờ
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                })
                    .then((token) => {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        return instance(originalRequest);
                    })
                    .catch((err) => Promise.reject(err));
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                const refreshToken = sessionStorage.getItem('refreshToken');
                if (!refreshToken) throw new Error('No refresh token');

                const response = await axios.post(
                    (import.meta.env.VITE_API_URL || 'http://localhost:8888') + '/api/v1/auth/refresh',
                    { refreshToken }
                );

                const newToken = response.data?.data?.accessToken || response.data?.accessToken;
                const newRefreshToken = response.data?.data?.refreshToken || response.data?.refreshToken;
                if (newToken) {
                    sessionStorage.setItem('token', newToken);
                    if (newRefreshToken) {
                        sessionStorage.setItem('refreshToken', newRefreshToken);
                    }
                    instance.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
                    processQueue(null, newToken);
                    originalRequest.headers.Authorization = `Bearer ${newToken}`;
                    return instance(originalRequest);
                }
                throw new Error('No access token in refresh response');
            } catch (refreshError) {
                processQueue(refreshError, null);
                sessionStorage.removeItem('token');
                sessionStorage.removeItem('refreshToken');
                // Chỉ redirect nếu đang ở trang yêu cầu auth
                const currentRoute = router.currentRoute?.value;
                if (currentRoute?.meta?.requiresAuth) {
                    router.push({ name: ROUTES_CONSTANTS.LOGIN.name });
                }
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        if (error.response?.status === 403) {
            router.push({ name: ROUTES_CONSTANTS.ERROR.FORBIDDEN.name });
        }

        return Promise.reject(error);
    }
);

export default instance;
