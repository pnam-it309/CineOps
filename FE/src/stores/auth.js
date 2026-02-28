import { defineStore } from 'pinia';
import AuthService from '@/services/auth.service';

// Promise chung cho việc khôi phục session - dùng chung giữa các lần gọi
let restorePromise = null;

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null, // User profile object
        token: sessionStorage.getItem('token') || null,
        roles: [], // E.g., ['ROLE_ADMIN', 'ROLE_CUSTOMER']
        loading: false,
        error: null,
        isRefreshing: false
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
            this.loading = true;
            // Clear state immediately for best UI response
            this.user = null;
            this.token = null;
            this.roles = [];
            restorePromise = null;
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('refreshToken');

            try {
                await AuthService.logout();
            } catch (err) {
                console.error('Logout error', err);
            } finally {
                this.loading = false;
            }
        },
        
        async refresh() {
            if (this.isRefreshing) return;
            this.isRefreshing = true;
            try {
                const authData = await AuthService.refreshToken();
                this.token = authData.accessToken;
                this.user = {
                    email: authData.email,
                    fullName: authData.fullName,
                };
                if (authData.role) {
                    this.roles = [authData.role];
                }
                return true;
            } catch (err) {
                // Không gọi logout() ở đây - chỉ return false
                // Để caller quyết định có cần logout hay không
                console.warn('Refresh token failed:', err?.message || err);
                return false;
            } finally {
                this.isRefreshing = false;
            }
        },
        
        /**
         * Khôi phục session khi reload trang hoặc khi store mất user.
         * Dùng promise chung để tránh gọi nhiều lần cùng lúc khi click nhanh.
         */
        async restoreSession() {
            // Không có token → không cần khôi phục
            if (!sessionStorage.getItem('token')) return false;
            
            // Đã có user → không cần khôi phục
            if (this.user) return true;
            
            // Nếu đang khôi phục → chờ kết quả (tránh gọi API trùng)
            if (restorePromise) {
                return restorePromise;
            }
            
            // Bắt đầu khôi phục
            restorePromise = this._doRestore();
            try {
                return await restorePromise;
            } finally {
                restorePromise = null;
            }
        },
        
        async _doRestore() {
            // Bước 1: Thử dùng access token hiện tại để lấy profile
            try {
                await this.fetchProfile();
                return true;
            } catch (e) {
                // Access token hết hạn, thử refresh
            }
            
            // Bước 2: Thử refresh token
            try {
                const success = await this.refresh();
                if (success) {
                    await this.fetchProfile();
                    return true;
                }
            } catch (e) {
                // Refresh cũng thất bại
            }
            
            return false;
        },
        
        async fetchProfile() {
            if (!this.token && !sessionStorage.getItem('token')) return;
            // Đồng bộ token từ sessionStorage nếu store bị mất
            if (!this.token && sessionStorage.getItem('token')) {
                this.token = sessionStorage.getItem('token');
            }
            const userData = await AuthService.getCurrentUser();
            // userData = { email, fullName, role }
            this.user = userData;
            if (userData?.role) {
                this.roles = [userData.role];
            }
        }
    }
});
