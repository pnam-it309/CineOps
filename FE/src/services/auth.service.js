import axios from '@/services/axios';
import {
    API_AUTH_LOGIN,
    API_AUTH_REGISTER,
    API_AUTH_REFRESH,
    API_AUTH_LOGOUT,
    API_AUTH_ME,
} from '@/constants/apiEndpoints';

class AuthService {
    /**
     * Login user
     * @param {Object} credentials { username, password }
     * @returns {Promise}
     */
    login(credentials) {
        // --- MOCK LOGIN FOR TESTING ---
        const { username, password } = credentials;
        if (password === '123') {
            let mockRole = null;
            let mockToken = null;

            if (username === 'admin') {
                mockRole = 'ROLE_ADMIN';
                mockToken = 'MOCK_TOKEN_ADMIN';
            } else if (username === 'staff') {
                mockRole = 'ROLE_STAFF';
                mockToken = 'MOCK_TOKEN_STAFF';
            } else if (username === 'customer') {
                mockRole = 'ROLE_CUSTOMER';
                mockToken = 'MOCK_TOKEN_CUSTOMER';
            }

            if (mockRole) {
                localStorage.setItem('token', mockToken);
                return Promise.resolve({
                    accessToken: mockToken,
                    tokenType: 'Bearer',
                    message: 'Mock Login Successful'
                });
            }
        }
        // -----------------------------

        return axios.post(API_AUTH_LOGIN, credentials)
            .then(response => {
                if (response.data.accessToken) {
                    localStorage.setItem('token', response.data.accessToken);
                }
                return response.data;
            });
    }

    /**
     * Register new user
     * @param {Object} userData 
     * @returns {Promise}
     */
    register(userData) {
        return axios.post(API_AUTH_REGISTER, userData);
    }

    /**
     * Logout user
     * @returns {Promise}
     */
    logout() {
        // Check for mock token
        const token = localStorage.getItem('token');
        if (token && token.startsWith('MOCK_TOKEN_')) {
            localStorage.removeItem('token');
            return Promise.resolve();
        }

        return axios.post(API_AUTH_LOGOUT).finally(() => {
            localStorage.removeItem('token');
        });
    }

    /**
     * Get current user profile
     * @returns {Promise}
     */
    getCurrentUser() {
        // --- MOCK PROFILE FOR TESTING ---
        const token = localStorage.getItem('token');
        if (token === 'MOCK_TOKEN_ADMIN') {
            return Promise.resolve({
                data: {
                    id: 1,
                    username: 'admin',
                    email: 'admin@cineops.com',
                    roles: [{ name: 'ROLE_ADMIN' }]
                }
            });
        }
        if (token === 'MOCK_TOKEN_STAFF') {
            return Promise.resolve({
                data: {
                    id: 2,
                    username: 'staff',
                    email: 'staff@cineops.com',
                    roles: [{ name: 'ROLE_STAFF' }] // Staff often has limited admin access or just staff
                }
            });
        }
        if (token === 'MOCK_TOKEN_CUSTOMER') {
            return Promise.resolve({
                data: {
                    id: 3,
                    username: 'customer',
                    email: 'customer@cineops.com',
                    roles: [{ name: 'ROLE_CUSTOMER' }]
                }
            });
        }
        // -------------------------------

        return axios.get(API_AUTH_ME);
    }

    /**
     * Refresh token
     * @returns {Promise}
     */
    refreshToken() {
        // Implementation depends on backend requirements (e.g. sending refresh token)
        return axios.post(API_AUTH_REFRESH);
    }
}

export default new AuthService();
