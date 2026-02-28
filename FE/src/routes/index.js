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
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN'], title: 'Quản trị' },
        children: [
            {
                path: '', // Default to dashboard
                redirect: { name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name,
                component: () => import('@/pages/admin/AdminDashboard.vue'),
                meta: { title: 'Tổng quan' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STATISTICS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STATISTICS.name,
                component: () => import('@/pages/admin/AdminReports.vue'),
                meta: { title: 'Thống kê' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.POS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.POS.name,
                component: () => import('@/pages/admin/AdminSales.vue'),
                meta: { title: 'Bán vé tại quầy' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CHECKIN.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CHECKIN.name,
                component: () => import('@/pages/admin/AdminCheckin.vue'),
                meta: { title: 'Check-in vé' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.INVOICES.path,
                name: ROUTES_CONSTANTS.ADMIN.children.INVOICES.name,
                component: () => import('@/pages/admin/AdminInvoices.vue'),
                meta: { title: 'Quản lý hóa đơn' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.TICKETS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.TICKETS.name,
                component: () => import('@/pages/admin/AdminReports.vue'), // Using reports as placeholder for tickets
                meta: { title: 'Quản lý vé' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SEATS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SEATS.name,
                component: () => import('@/pages/admin/ghe/AdminSeats.vue'),
                meta: { title: 'Quản lý ghế' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE.name,
                component: () => import('@/pages/admin/AdminMovies.vue'),
                meta: { title: 'Phim & Lịch chiếu' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.name,
                component: () => import('@/pages/admin/suatchieu/AdminShowtimes.vue'),
                meta: { title: 'Suất chiếu' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.FOOD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.FOOD.name,
                component: () => import('@/pages/admin/AdminFood.vue'),
                meta: { title: 'Sản phẩm & Combo' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS.name,
                component: () => import('@/pages/admin/AdminVouchers.vue'),
                meta: { title: 'Phiếu giảm giá' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS.name,
                component: () => import('@/pages/admin/AdminCustomers.vue'),
                meta: { title: 'Khách hàng' }
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STAFF.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
                component: () => import('@/pages/admin/AdminStaff.vue'),
                meta: { title: 'Nhân viên' }
            },
        ]
    },

    // STAFF ROUTES
    {
        path: ROUTES_CONSTANTS.STAFF.path,
        component: StaffLayout,
        meta: { requiresAuth: true, roles: ['ROLE_STAFF', 'ROLE_ADMIN'], title: 'Nhân viên' },
        children: [
             {
                path: '',
                redirect: { name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.path,
                name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name,
                component: () => import('@/pages/staff/StaffDashboard.vue'),
                meta: { title: 'Tổng quan POS' }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.SALES.path,
                name: ROUTES_CONSTANTS.STAFF.children.SALES.name,
                component: () => import('@/pages/staff/StaffSales.vue'),
                meta: { title: 'Quầy bán vé' }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.CHECKIN.path,
                name: ROUTES_CONSTANTS.STAFF.children.CHECKIN.name,
                component: () => import('@/pages/staff/StaffCheckin.vue'),
                meta: { title: 'Soát vé' }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.INVOICES.path,
                name: ROUTES_CONSTANTS.STAFF.children.INVOICES.name,
                component: () => import('@/pages/staff/StaffInvoices.vue'),
                meta: { title: 'Hóa đơn ngày' }
            },
            {
                path: ROUTES_CONSTANTS.STAFF.children.SHIFT.path,
                name: ROUTES_CONSTANTS.STAFF.children.SHIFT.name,
                component: () => import('@/pages/staff/StaffShift.vue'),
                meta: { title: 'Bàn giao ca' }
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
    
    // 1. Đồng bộ state nếu token bị xóa (bởi interceptor hoặc logout)
    if (!sessionStorage.getItem('token') && authStore.token) {
        authStore.user = null;
        authStore.token = null;
        authStore.roles = [];
    }

    // 2. Khôi phục session nếu có token nhưng chưa có user (F5 hoặc click nhanh)
    // restoreSession() đã có cơ chế chống gọi trùng (shared promise)
    if (sessionStorage.getItem('token') && !authStore.user) {
        await authStore.restoreSession();
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
