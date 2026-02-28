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
        if (error.response) {
            if (error.response.status === 401) {
                // Token expired or invalid -> Redirect to Login or Unauthorized
                localStorage.removeItem('token');
                router.push({ name: ROUTES_CONSTANTS.ERROR.UNAUTHORIZED.name }); 
            } else if (error.response.status === 403) {
                // Forbidden -> Redirect to Forbidden page
                router.push({ name: ROUTES_CONSTANTS.ERROR.FORBIDDEN.name });
            } else if (error.response.status === 404) {
                 // Resource not found
            }
        }
        return Promise.reject(error);
    }
);

export default instance;
