import axios from '@/services/axios';
import {
    API_AUTH_LOGIN,
    API_AUTH_REGISTER,
    API_AUTH_LOGOUT,
    API_AUTH_ME,
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
                    localStorage.setItem('token', authData.accessToken);
                }
                if (authData?.refreshToken) {
                    localStorage.setItem('refreshToken', authData.refreshToken);
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
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
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
