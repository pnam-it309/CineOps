import { createRouter, createWebHistory } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { useAuthStore } from '@/stores/auth';

// Layouts
const AdminLayout = () => import('@/layout/AdminLayout.vue');
const StaffLayout = () => import('@/layout/StaffLayout.vue');
const MainLayout = () => import('@/layout/MainLayout.vue');

// Pages
const Home = () => import('@/pages/HomePage.vue');
const Login = () => import('@/pages/LoginPage.vue');

const routes = [
    // PUBLIC / CUSTOMER LAYOUT
    {
        path: '/',
        component: MainLayout,
        children: [
            {
                path: '',
                name: ROUTES_CONSTANTS.HOME.name,
                component: Home,
            },
            {
                path: ROUTES_CONSTANTS.LOGIN.path,
                name: ROUTES_CONSTANTS.LOGIN.name,
                component: Login,
                meta: { guestOnly: true }
            },
            {
                path: ROUTES_CONSTANTS.CUSTOMER.path, // /customer
                children: [
                    {
                        path: ROUTES_CONSTANTS.CUSTOMER.children.PROFILE.path,
                        name: ROUTES_CONSTANTS.CUSTOMER.children.PROFILE.name,
                        component: () => import('@/pages/customer/CustomerProfile.vue'),
                        meta: { requiresAuth: true } // Assuming any logged in user is a 'customer' effectively
                    },
                    {
                        path: ROUTES_CONSTANTS.CUSTOMER.children.TICKETS.path,
                        name: ROUTES_CONSTANTS.CUSTOMER.children.TICKETS.name,
                        component: () => import('@/pages/customer/CustomerTickets.vue'),
                        meta: { requiresAuth: true }
                    }
                ]
            },
            // Error pages within Main Layout? Or standalone? Let's keep them here for header/footer consistency
            {
                path: ROUTES_CONSTANTS.ERROR.UNAUTHORIZED.path,
                name: ROUTES_CONSTANTS.ERROR.UNAUTHORIZED.name,
                component: () => import('@/pages/401/Unauthorized.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ERROR.FORBIDDEN.path,
                name: ROUTES_CONSTANTS.ERROR.FORBIDDEN.name,
                component: () => import('@/pages/403/Forbidden.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ERROR.NOT_FOUND.path,
                name: ROUTES_CONSTANTS.ERROR.NOT_FOUND.name,
                component: () => import('@/pages/404/NotFound.vue'),
            }
        ]
    },

    // ADMIN ROUTES
    {
        path: ROUTES_CONSTANTS.ADMIN.path, // /admin
        component: AdminLayout,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
        children: [
            {
                path: '', // Default to dashboard
                redirect: { name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name,
                component: () => import('@/pages/admin/AdminDashboard.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.USERS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.USERS.name,
                component: () => import('@/pages/admin/AdminUsers.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.ROLES.path,
                name: ROUTES_CONSTANTS.ADMIN.children.ROLES.name,
                component: () => import('@/pages/admin/AdminRoles.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.REPORTS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.REPORTS.name,
                component: () => import('@/pages/admin/AdminReports.vue'),
            }
        ]
    },

    // STAFF ROUTES
    {
        path: ROUTES_CONSTANTS.STAFF.path,
        component: StaffLayout,
        meta: { requiresAuth: true, roles: ['ROLE_STAFF', 'ROLE_ADMIN'] }, // Admin usually can see staff stuff too
        children: [
             {
                path: '',
                redirect: { name: ROUTES_CONSTANTS.STAFF.children.BOOKINGS.name }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.BOOKINGS.path,
                name: ROUTES_CONSTANTS.STAFF.children.BOOKINGS.name,
                component: () => import('@/pages/staff/StaffBookings.vue'),
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.MOVIES.path,
                name: ROUTES_CONSTANTS.STAFF.children.MOVIES.name,
                component: () => import('@/pages/staff/StaffMovies.vue'),
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.SHOWTIMES.path,
                name: ROUTES_CONSTANTS.STAFF.children.SHOWTIMES.name,
                component: () => import('@/pages/staff/StaffShowtimes.vue'),
            }
        ]
    },

    // Catch All
    {
        path: ROUTES_CONSTANTS.ERROR.CATCH_ALL.path,
        redirect: ROUTES_CONSTANTS.ERROR.NOT_FOUND.path,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// Navigation Guard
router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();
    
    // 1. Initialize auth if needed (check token and fetch profile)
    // We might want to do this only once? Or authStore handles it?
    // For simplicity, verify authentication state:
    if (localStorage.getItem('token') && !authStore.user) {
         try {
             // Attempt to restore session
            await authStore.fetchProfile();
         } catch(e) {
             // Failed to restore -> Token invalid
         }
    }

    const isAuthenticated = authStore.isAuthenticated;
    const userRoles = authStore.roles;

    // 2. Check requiresAuth
    if (to.meta.requiresAuth) {
        if (!isAuthenticated) {
            return next({ name: ROUTES_CONSTANTS.LOGIN.name });
        }

        // 3. Check Role Permissions
        if (to.meta.roles) {
            const hasRole = to.meta.roles.some(role => userRoles.includes(role));
            if (!hasRole) {
                return next({ name: ROUTES_CONSTANTS.ERROR.FORBIDDEN.name });
            }
        }
    }

    // 4. Check guestOnly (e.g. Login page)
    if (to.meta.guestOnly && isAuthenticated) {
        return next({ name: ROUTES_CONSTANTS.HOME.name });
    }

    next();
});

export default router;
