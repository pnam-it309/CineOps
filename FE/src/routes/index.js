import { createRouter, createWebHistory } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { useAuthStore } from '@/stores/auth';

// Layouts
const AdminLayout = () => import('@/layout/AdminLayout.vue');
const StaffLayout = () => import('@/layout/StaffLayout.vue');
const CustomerLayout = () => import('@/layout/CustomerLayout.vue');

// Pages
const Home = () => import('@/pages/customer/HomePage.vue');
const Login = () => import('@/pages/LoginPage.vue');
const OAuthCallback = () => import('@/pages/OAuthCallbackPage.vue');
const Showtimes = () => import('@/pages/customer/ShowtimesPage.vue');
const Promotions = () => import('@/pages/customer/PromotionsPage.vue');
const CinemaInfo = () => import('@/pages/customer/CinemaInfoPage.vue');

const routes = [
    // PUBLIC / CUSTOMER LAYOUT
    {
        path: '/',
        component: CustomerLayout,
        children: [
            {
                path: '',
                name: ROUTES_CONSTANTS.HOME.name,
                component: Home,
            },
            {
                path: ROUTES_CONSTANTS.MOVIE_DETAILS.path,
                name: ROUTES_CONSTANTS.MOVIE_DETAILS.name,
                component: () => import('@/pages/customer/MovieDetails.vue'),
            },
            {
                path: ROUTES_CONSTANTS.SHOWTIMES.path,
                name: ROUTES_CONSTANTS.SHOWTIMES.name,
                component: Showtimes,
            },
            {
                path: ROUTES_CONSTANTS.PROMOTIONS.path,
                name: ROUTES_CONSTANTS.PROMOTIONS.name,
                component: Promotions,
            },
            {
                path: ROUTES_CONSTANTS.CINEMAS.path,
                name: ROUTES_CONSTANTS.CINEMAS.name,
                component: CinemaInfo,
            },
            {
                path: ROUTES_CONSTANTS.LOGIN.path,
                name: ROUTES_CONSTANTS.LOGIN.name,
                component: Login,
                meta: { guestOnly: true }
            },
            {
                path: ROUTES_CONSTANTS.REGISTER.path,
                name: ROUTES_CONSTANTS.REGISTER.name,
                component: () => import('@/pages/RegisterPage.vue'),
                meta: { guestOnly: true }
            },
            {
                path: ROUTES_CONSTANTS.FORGOT_PASSWORD.path,
                name: ROUTES_CONSTANTS.FORGOT_PASSWORD.name,
                component: () => import('@/pages/ForgotPasswordPage.vue'),
                meta: { guestOnly: true }
            },
            {
                path: ROUTES_CONSTANTS.BOOKING.path,
                name: ROUTES_CONSTANTS.BOOKING.name,
                children: [
                    {
                        path: ROUTES_CONSTANTS.BOOKING.children.STEP1.path,
                        name: ROUTES_CONSTANTS.BOOKING.children.STEP1.name,
                        component: () => import('@/pages/booking/SelectShowtime.vue'),
                    },
                    {
                        path: ROUTES_CONSTANTS.BOOKING.children.STEP2.path,
                        name: ROUTES_CONSTANTS.BOOKING.children.STEP2.name,
                        component: () => import('@/pages/booking/SelectSeats.vue'),
                    },
                    {
                        path: ROUTES_CONSTANTS.BOOKING.children.STEP3.path,
                        name: ROUTES_CONSTANTS.BOOKING.children.STEP3.name,
                        component: () => import('@/pages/booking/SelectFood.vue'),
                    },
                    {
                        path: ROUTES_CONSTANTS.BOOKING.children.STEP4.path,
                        name: ROUTES_CONSTANTS.BOOKING.children.STEP4.name,
                        component: () => import('@/pages/booking/PaymentPage.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: ROUTES_CONSTANTS.BOOKING.children.RESULT.path,
                        name: ROUTES_CONSTANTS.BOOKING.children.RESULT.name,
                        component: () => import('@/pages/booking/BookingResult.vue'),
                        meta: { requiresAuth: true }
                    }
                ]
            },
            {
                path: ROUTES_CONSTANTS.NEWS.path,
                children: [
                    {
                        path: ROUTES_CONSTANTS.NEWS.children.LIST.path,
                        name: ROUTES_CONSTANTS.NEWS.children.LIST.name,
                        component: () => import('@/pages/news/NewsList.vue'),
                    },
                    {
                        path: ROUTES_CONSTANTS.NEWS.children.DETAIL.path,
                        name: ROUTES_CONSTANTS.NEWS.children.DETAIL.name,
                        component: () => import('@/pages/news/NewsDetail.vue'),
                    }
                ]
            },
            {
                path: ROUTES_CONSTANTS.CUSTOMER.path, // /customer
                children: [
                    {
                        path: '', // /customer -> redirect to homepage for now
                        redirect: '/'
                    },
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
                path: ROUTES_CONSTANTS.ADMIN.children.STATISTICS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STATISTICS.name,
                component: () => import('@/pages/admin/AdminReports.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.POS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.POS.name,
                component: () => import('@/pages/staff/StaffSales.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CHECKIN.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CHECKIN.name,
                component: () => import('@/pages/staff/StaffCheckin.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.INVOICES.path,
                name: ROUTES_CONSTANTS.ADMIN.children.INVOICES.name,
                component: () => import('@/pages/staff/StaffInvoices.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.TICKETS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.TICKETS.name,
                component: () => import('@/pages/admin/AdminReports.vue'), // Using reports as placeholder for tickets
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SEATS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SEATS.name,
                component: () => import('@/pages/admin/AdminSeats.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE.name,
                component: () => import('@/pages/admin/AdminMovies.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.name,
                component: () => import('@/pages/admin/AdminShowtimes.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.FOOD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.FOOD.name,
                component: () => import('@/pages/admin/AdminFood.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS.name,
                component: () => import('@/pages/admin/AdminVouchers.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS.name,
                component: () => import('@/pages/admin/AdminCustomers.vue'),
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STAFF.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
                component: () => import('@/pages/admin/AdminStaff.vue'),
            },
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
                redirect: { name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.path,
                name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name,
                component: () => import('@/pages/staff/StaffDashboard.vue'),
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.SALES.path,
                name: ROUTES_CONSTANTS.STAFF.children.SALES.name,
                component: () => import('@/pages/staff/StaffSales.vue'),
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.CHECKIN.path,
                name: ROUTES_CONSTANTS.STAFF.children.CHECKIN.name,
                component: () => import('@/pages/staff/StaffCheckin.vue'),
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.INVOICES.path,
                name: ROUTES_CONSTANTS.STAFF.children.INVOICES.name,
                component: () => import('@/pages/staff/StaffInvoices.vue'),
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.SHIFT.path,
                name: ROUTES_CONSTANTS.STAFF.children.SHIFT.name,
                component: () => import('@/pages/staff/StaffShift.vue'),
            },
        ]
    },

    // OAuth2 Callback (outside layout, no auth required)
    {
        path: '/oauth2/redirect',
        name: 'OAuthCallback',
        component: OAuthCallback,
        meta: { guestOnly: false } // allow even if authenticated to handle re-login
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
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition;
        } else {
            return { top: 0, behavior: 'smooth' };
        }
    },
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
        if (authStore.isAdmin) {
            return next({ name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name });
        } else if (authStore.isStaff) {
            return next({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name });
        }
        return next({ name: ROUTES_CONSTANTS.HOME.name });
    }

    next();
});

export default router;
