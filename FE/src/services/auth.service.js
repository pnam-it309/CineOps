import axios from '@/services/axios';
import baseAxios from 'axios';
import {
    API_AUTH_LOGIN,
    API_AUTH_REGISTER,
    API_AUTH_LOGOUT,
    API_AUTH_ME,
    API_AUTH_REFRESH,
} from '@/constants/apiEndpoints';

class AuthService {
    /**
     * Login với email + password
     * BE trả về ApiResponse<AuthResponse> = { status, message, data: { accessToken, refreshToken, email, fullName, role } }
     */
    login(credentials) {
        return axios.post(API_AUTH_LOGIN, credentials)
            .then(response => {
                // BE bọc trong ApiResponse → lấy response.data.data
                const authData = response.data?.data || response.data;
                if (authData?.accessToken) {
                    sessionStorage.setItem('token', authData.accessToken);
                }
                if (authData?.refreshToken) {
                    sessionStorage.setItem('refreshToken', authData.refreshToken);
                }
                return authData;
            });
    }

    /**
     * Register new user
     */
    register(userData) {
        return axios.post(API_AUTH_REGISTER, userData);
    }

    /**
     * Logout user
     */
    logout() {
        return axios.post(API_AUTH_LOGOUT).finally(() => {
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('refreshToken');
        });
    }

    /**
     * Refresh access token bằng refreshToken
     */
    refreshToken() {
        const refreshToken = sessionStorage.getItem('refreshToken');
        if (!refreshToken) return Promise.reject('No refresh token');

        // Phải dùng baseAxios (axios gốc) để tránh bị interceptor gắn header Authorization token cũ (hết hạn)
        // và tránh bị loop khi refresh thất bại
        const url = (import.meta.env.VITE_API_URL || 'http://localhost:8888') + API_AUTH_REFRESH;
        
        return baseAxios.post(url, { refreshToken })
            .then(response => {
                const authData = response.data?.data || response.data;
                if (authData?.accessToken) {
                    sessionStorage.setItem('token', authData.accessToken);
                }
                if (authData?.refreshToken) {
                    sessionStorage.setItem('refreshToken', authData.refreshToken);
                }
                return authData;
            });
    }

    /**
     * GET /api/v1/auth/me - lấy thông tin user hiện tại
     */
    getCurrentUser() {
        return axios.get(API_AUTH_ME)
            .then(response => {
                // trả về response.data.data
                return response.data?.data || response.data;
            });
    }
}

export default new AuthService();
