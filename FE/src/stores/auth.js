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
                const response = await AuthService.login(credentials);
                // Assumption: response contains accessToken. User data might be separate or in token.
                // We'll set the token, then fetch user details immediately.
                this.token = response.accessToken; 
                // Wait for the token to be set in localStorage (AuthService does it, but we sync state here)
                // Actually AuthService.login sets localStorage. We just double check via our state.
                
                await this.fetchProfile();
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || 'Login failed';
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
                const response = await AuthService.getCurrentUser();
                this.user = response.data; // Assuming /me returns the user object
                // Extract roles. Adjust based on your Backend response structure. 
                // Common pattern: user.roles = [{name: 'ROLE_ADMIN'}, ...] or just ['ROLE_ADMIN']
                // Let's assume response.data.roles is an array of strings or objects.
                if (this.user.roles) {
                    this.roles = this.user.roles.map(r => typeof r === 'string' ? r : r.name);
                }
            } catch (err) {
                // If fetching profile fails (e.g. 401), logout
                this.logout();
            }
        }
    }
});
