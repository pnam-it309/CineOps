import axios from 'axios';
import router from '@/routes';

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8888/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10s timeout
});

// Request Interceptor
instance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response Interceptor
instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Token expired or invalid
            localStorage.removeItem('token');
            router.push({ name: 'Login' });
        }
        return Promise.reject(error);
    }
);

export default instance;
