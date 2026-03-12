import { createRouter, createWebHistory } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { useAuthStore } from '@/stores/auth';

import { customerRoutes } from './customerRoutes';
import { adminRoutes,printRoutes }    from './adminRoutes';
import { staffRoutes }    from './staffRoutes';
import { sharedStandaloneRoutes } from './sharedStandaloneRoutes';

// OAuth2 callback — nằm ngoài layout, không cần auth
const OAuthCallback = () => import('@/pages/auth/OAuthCallbackPage.vue');

const routes = [
    customerRoutes,
    adminRoutes,
    printRoutes,
    staffRoutes,
    sharedStandaloneRoutes,

    // OAuth2 Callback
    {
        path: '/oauth2/redirect',
        name: 'OAuthCallback',
        component: OAuthCallback,
        meta: { guestOnly: false }
    },

    // Catch-all → 404
    {
        path: ROUTES_CONSTANTS.ERROR.CATCH_ALL.path,
        redirect: ROUTES_CONSTANTS.ERROR.NOT_FOUND.path,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        return savedPosition ?? { top: 0, behavior: 'smooth' };
    },
});

// ── Navigation Guard ──────────────────────────────────────────────────────────
router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();

    // 1. Đồng bộ state nếu token bị xóa (bởi interceptor / logout)
    if (!sessionStorage.getItem('token') && authStore.token) {
        authStore.user  = null;
        authStore.token = null;
        authStore.roles = [];
    }

    // 2. Khôi phục session nếu có token nhưng chưa có user (F5 / navigate nhanh)
    if (sessionStorage.getItem('token') && !authStore.user) {
        await authStore.restoreSession();
    }

    const isAuthenticated = authStore.isAuthenticated;
    const userRoles       = authStore.roles;

    // 3. Kiểm tra requiresAuth
    if (to.meta.requiresAuth) {
        if (!isAuthenticated) {
            return next({ name: ROUTES_CONSTANTS.LOGIN.name });
        }

        // 4. Kiểm tra quyền (roles)
        if (to.meta.roles) {
            const hasRole = to.meta.roles.some(role => userRoles.includes(role));
            if (!hasRole) {
                return next({ name: ROUTES_CONSTANTS.ERROR.FORBIDDEN.name });
            }
        }
    }

    // 5. Redirect nếu đã đăng nhập mà vào trang guestOnly (login/register…)
    if (to.meta.guestOnly && isAuthenticated) {
        if (authStore.isAdmin)  return next({ name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name });
        if (authStore.isStaff)  return next({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name });
        return next({ name: ROUTES_CONSTANTS.HOME.name });
    }

    next();
});

export default router;
