import { defineStore } from 'pinia';
import AuthService from '@/services/auth.service';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null, // User profile object
        token: localStorage.getItem('token') || null,
        roles: [], // E.g., ['ROLE_ADMIN', 'ROLE_CUSTOMER']
        loading: false,
        error: null
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
        isAdmin: (state) => state.roles.includes('ROLE_ADMIN'),
        isStaff: (state) => state.roles.includes('ROLE_STAFF'),
        isCustomer: (state) => state.roles.includes('ROLE_CUSTOMER'),
        currentUser: (state) => state.user,
    },

    actions: {
        async login(credentials) {
            this.loading = true;
            this.error = null;
            try {
                const authData = await AuthService.login(credentials);
                // authData = { accessToken, refreshToken, email, fullName, role }
                this.token = authData.accessToken;
                this.user = {
                    email: authData.email,
                    fullName: authData.fullName,
                };
                // role từ BE là chuỗi: "ROLE_ADMIN" / "ROLE_STAFF" / "ROLE_CUSTOMER"
                if (authData.role) {
                    this.roles = [authData.role];
                }
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || 'Đăng nhập thất bại';
                throw err;
            } finally {
                this.loading = false;
            }
        },

        async register(userData) {
            this.loading = true;
            try {
                await AuthService.register(userData);
            } catch (err) {
                this.error = err.response?.data?.message || 'Registration failed';
                throw err;
            } finally {
                this.loading = false;
            }
        },

        async logout() {
            try {
                await AuthService.logout();
            } catch (err) {
                console.error('Logout error', err);
            } finally {
                this.user = null;
                this.token = null;
                this.roles = [];
                // AuthService.logout cleans localStorage
            }
        },

        async fetchProfile() {
            if (!this.token) return;
            try {
                const userData = await AuthService.getCurrentUser();
                // userData = { email, fullName, role }
                this.user = userData;
                if (userData?.role) {
                    this.roles = [userData.role];
                }
            } catch (err) {
                // Nếu lấy profile thất bại (ví dụ 401), logout
                this.logout();
            }
        }
    }
});
