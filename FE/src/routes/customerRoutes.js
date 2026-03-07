import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const CustomerLayout = () => import('@/layout/CustomerLayout.vue');

// Eager-load các trang được truy cập nhiều nhất
const Home       = () => import('@/pages/customer/HomePage.vue');
const Login      = () => import('@/pages/auth/LoginPage.vue');
const Showtimes  = () => import('@/pages/customer/ShowtimesPage.vue');
const Promotions = () => import('@/pages/customer/PromotionsPage.vue');
const CinemaInfo = () => import('@/pages/customer/CinemaInfoPage.vue');

export const customerRoutes = {
    path: '/',
    component: CustomerLayout,
    children: [
        // ── Trang chính ──────────────────────────────────────
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

        // ── Auth ─────────────────────────────────────────────
        {
            path: ROUTES_CONSTANTS.LOGIN.path,
            name: ROUTES_CONSTANTS.LOGIN.name,
            component: Login,
            meta: { guestOnly: true }
        },
        {
            path: ROUTES_CONSTANTS.REGISTER.path,
            name: ROUTES_CONSTANTS.REGISTER.name,
            component: () => import('@/pages/auth/RegisterPage.vue'),
            meta: { guestOnly: true }
        },
        {
            path: ROUTES_CONSTANTS.FORGOT_PASSWORD.path,
            name: ROUTES_CONSTANTS.FORGOT_PASSWORD.name,
            component: () => import('@/pages/auth/ForgotPasswordPage.vue'),
            meta: { guestOnly: true }
        },

        // ── Đặt vé ───────────────────────────────────────────
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

        // ── Tin tức ──────────────────────────────────────────
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

        // ── Tài khoản khách hàng ─────────────────────────────
        {
            path: ROUTES_CONSTANTS.CUSTOMER.path,
            children: [
                {
                    path: '',
                    redirect: '/'
                },
                {
                    path: ROUTES_CONSTANTS.CUSTOMER.children.PROFILE.path,
                    name: ROUTES_CONSTANTS.CUSTOMER.children.PROFILE.name,
                    component: () => import('@/pages/customer/CustomerProfile.vue'),
                    meta: { requiresAuth: true }
                },
                {
                    path: ROUTES_CONSTANTS.CUSTOMER.children.TICKETS.path,
                    name: ROUTES_CONSTANTS.CUSTOMER.children.TICKETS.name,
                    component: () => import('@/pages/customer/CustomerTickets.vue'),
                    meta: { requiresAuth: true }
                }
            ]
        },

        // ── Error pages ──────────────────────────────────────
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
};
