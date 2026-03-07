import { AUTH_API } from './authConstants';
import { PUBLIC_API } from './publicConstants';
import { CUSTOMER_API } from './customerConstants';
import { STAFF_API } from './staffConstants';
import { ADMIN_API } from './adminConstants';
import { API_VERSION_PREFIX } from './baseApi';

/* ============================================
 * BASE PATHS
 * ============================================ */
export { API_VERSION_PREFIX };

/* ============================================
 * RE-EXPORTS FOR COMPATIBILITY
 * ============================================ */

// Auth
export const API_AUTH_PREFIX = AUTH_API.PREFIX;
export const API_AUTH_LOGIN = AUTH_API.LOGIN;
export const API_AUTH_REGISTER = AUTH_API.REGISTER;
export const API_AUTH_REFRESH = AUTH_API.REFRESH;
export const API_AUTH_LOGOUT = AUTH_API.LOGOUT;
export const API_AUTH_ME = AUTH_API.ME;
export const API_OAUTH2_PREFIX = "/api/v1/oauth2"; // Inline or from auth
export const API_OAUTH2_AUTHORIZE = AUTH_API.OAUTH2_AUTHORIZE;

// Public
export const API_PUBLIC_PREFIX = PUBLIC_API.PREFIX;
export const API_PUBLIC_MOVIES = PUBLIC_API.MOVIES;
export const API_PUBLIC_SHOWTIMES = PUBLIC_API.SHOWTIMES;
export const API_PUBLIC_PROMOTIONS = PUBLIC_API.PROMOTIONS;
export const API_PUBLIC_NEWS = PUBLIC_API.NEWS;
export const API_PUBLIC_BANNERS = PUBLIC_API.BANNERS;
export const API_PUBLIC_FOOD = PUBLIC_API.FOOD;

// Customer
export const API_CUSTOMER_PREFIX = CUSTOMER_API.PREFIX;
export const API_CUSTOMER_BOOKINGS = CUSTOMER_API.BOOKINGS;
export const API_CUSTOMER_REVIEWS = CUSTOMER_API.REVIEWS;
export const API_CUSTOMER_PROFILE = CUSTOMER_API.PROFILE;
export const API_CUSTOMER_TICKETS = CUSTOMER_API.TICKETS;

// Staff
export const API_STAFF_PREFIX = STAFF_API.PREFIX;
export const API_STAFF_BOOKINGS = STAFF_API.BOOKINGS;
export const API_STAFF_MOVIES = STAFF_API.MOVIES;
export const API_STAFF_GENRES = STAFF_API.GENRES;
export const API_STAFF_AGE_RATINGS = STAFF_API.AGE_RATINGS;
export const API_STAFF_SHOWTIMES = STAFF_API.SHOWTIMES;
export const API_STAFF_ROOMS = STAFF_API.ROOMS;
export const API_STAFF_ROOM_TYPES = STAFF_API.ROOM_TYPES;
export const API_STAFF_SEAT_TYPES = STAFF_API.SEAT_TYPES;
export const API_STAFF_SEATS = STAFF_API.SEATS;
export const API_STAFF_FOOD = STAFF_API.FOOD;
export const API_STAFF_FOOD_CATEGORIES = STAFF_API.FOOD_CATEGORIES;
export const API_STAFF_PROMOTIONS = STAFF_API.PROMOTIONS;
export const API_STAFF_NEWS = STAFF_API.NEWS;
export const API_STAFF_NEWS_CATEGORIES = STAFF_API.NEWS_CATEGORIES;
export const API_STAFF_BANNERS = STAFF_API.BANNERS;
export const API_STAFF_REPORTS = STAFF_API.REPORTS;
export const API_STAFF_REPORTS_REVENUE = STAFF_API.REPORTS_REVENUE;
export const API_STAFF_REPORTS_BOOKINGS = STAFF_API.REPORTS_BOOKINGS;
export const API_STAFF_REPORTS_MOVIES = STAFF_API.REPORTS_MOVIES;

// Admin
export const API_ADMIN_PREFIX = ADMIN_API.PREFIX;
export const API_ADMIN_USERS = ADMIN_API.USERS;
export const API_ADMIN_USERS_STAFF = ADMIN_API.USERS_STAFF;
export const API_ADMIN_USERS_CUSTOMERS = ADMIN_API.USERS_CUSTOMERS;
export const API_ADMIN_ROLES = ADMIN_API.ROLES;
export const API_ADMIN_REPORTS = ADMIN_API.REPORTS;
export const API_ADMIN_REPORTS_CUSTOMERS = ADMIN_API.REPORTS_CUSTOMERS;
export const API_ADMIN_REPORTS_STAFF = ADMIN_API.REPORTS_STAFF;
export const API_ADMIN_REPORTS_SYSTEM = ADMIN_API.REPORTS_SYSTEM;
export const API_ADMIN_GHE = ADMIN_API.GHE;
export const API_ADMIN_GHE_GENERATE = ADMIN_API.GHE_GENERATE;
export const API_ADMIN_LOAI_GHE = ADMIN_API.LOAI_GHE;
export const API_ADMIN_PHONG_CHIEU_DROP = ADMIN_API.PHONG_CHIEU_DROP;
export const API_ADMIN_SUAT_CHIEU = ADMIN_API.SUAT_CHIEU;
export const API_PHIM_DROP = ADMIN_API.PHIM_DROP;

/* ============================================
 * COMMON ENDPOINTS (All Authenticated Users)
 * ============================================ */
export const API_COMMON = `${API_VERSION_PREFIX}/common`;
export const API_COMMON_UPLOAD = `${API_COMMON}/upload`;
export const API_COMMON_HEALTH = `${API_COMMON}/health`;

