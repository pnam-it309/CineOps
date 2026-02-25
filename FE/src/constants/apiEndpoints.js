/**
 * Mapped from BE MappingConstants.java
 */

/* ============================================
 * BASE PATHS
 * ============================================ */
export const API_VERSION_PREFIX = "/api/v1";

/* ============================================
 * PUBLIC ENDPOINTS (No Authentication)
 * ============================================ */

/** Authentication endpoints */
export const API_AUTH_PREFIX = `${API_VERSION_PREFIX}/auth`;
export const API_AUTH_LOGIN = `${API_AUTH_PREFIX}/login`;
export const API_AUTH_REGISTER = `${API_AUTH_PREFIX}/register`;
export const API_AUTH_REFRESH = `${API_AUTH_PREFIX}/refresh`;
export const API_AUTH_LOGOUT = `${API_AUTH_PREFIX}/logout`;
export const API_AUTH_ME = `${API_AUTH_PREFIX}/me`;

/** OAuth2 endpoints */
export const API_OAUTH2_PREFIX = `${API_VERSION_PREFIX}/oauth2`;
export const API_OAUTH2_AUTHORIZE = `${API_OAUTH2_PREFIX}/authorize`;

/** Public content endpoints (no auth required) */
export const API_PUBLIC_PREFIX = `${API_VERSION_PREFIX}/public`;
export const API_PUBLIC_MOVIES = `${API_PUBLIC_PREFIX}/movies`;
export const API_PUBLIC_SHOWTIMES = `${API_PUBLIC_PREFIX}/showtimes`;
export const API_PUBLIC_PROMOTIONS = `${API_PUBLIC_PREFIX}/promotions`;
export const API_PUBLIC_NEWS = `${API_PUBLIC_PREFIX}/news`;
export const API_PUBLIC_BANNERS = `${API_PUBLIC_PREFIX}/banners`;
export const API_PUBLIC_FOOD = `${API_PUBLIC_PREFIX}/food`;

/* ============================================
 * CUSTOMER ENDPOINTS (CUSTOMER + ADMIN)
 * ============================================ */

export const API_CUSTOMER_PREFIX = `${API_VERSION_PREFIX}/customer`;
export const API_CUSTOMER_BOOKINGS = `${API_CUSTOMER_PREFIX}/bookings`;
export const API_CUSTOMER_REVIEWS = `${API_CUSTOMER_PREFIX}/reviews`;
export const API_CUSTOMER_PROFILE = `${API_CUSTOMER_PREFIX}/profile`;
export const API_CUSTOMER_TICKETS = `${API_CUSTOMER_PREFIX}/tickets`;

/* ============================================
 * STAFF ENDPOINTS (STAFF + ADMIN)
 * ============================================ */

export const API_STAFF_PREFIX = `${API_VERSION_PREFIX}/staff`;

// Booking Management (Staff)
export const API_STAFF_BOOKINGS = `${API_STAFF_PREFIX}/bookings`;

// Movie Management
export const API_STAFF_MOVIES = `${API_STAFF_PREFIX}/movies`;
export const API_STAFF_GENRES = `${API_STAFF_PREFIX}/genres`;
export const API_STAFF_AGE_RATINGS = `${API_STAFF_PREFIX}/age-ratings`;

// Showtime Management
export const API_STAFF_SHOWTIMES = `${API_STAFF_PREFIX}/showtimes`;

// Room & Seat Management
export const API_STAFF_ROOMS = `${API_STAFF_PREFIX}/rooms`;
export const API_STAFF_ROOM_TYPES = `${API_STAFF_PREFIX}/room-types`;
export const API_STAFF_SEAT_TYPES = `${API_STAFF_PREFIX}/seat-types`;
export const API_STAFF_SEATS = `${API_STAFF_PREFIX}/seats`;

// Food & Beverage Management
export const API_STAFF_FOOD = `${API_STAFF_PREFIX}/food`;
export const API_STAFF_FOOD_CATEGORIES = `${API_STAFF_PREFIX}/food-categories`;

// Promotion Management
export const API_STAFF_PROMOTIONS = `${API_STAFF_PREFIX}/promotions`;

// Content Management
export const API_STAFF_NEWS = `${API_STAFF_PREFIX}/news`;
export const API_STAFF_NEWS_CATEGORIES = `${API_STAFF_PREFIX}/news-categories`;
export const API_STAFF_BANNERS = `${API_STAFF_PREFIX}/banners`;

// Reports
export const API_STAFF_REPORTS = `${API_STAFF_PREFIX}/reports`;
export const API_STAFF_REPORTS_REVENUE = `${API_STAFF_REPORTS}/revenue`;
export const API_STAFF_REPORTS_BOOKINGS = `${API_STAFF_REPORTS}/bookings`;
export const API_STAFF_REPORTS_MOVIES = `${API_STAFF_REPORTS}/movies`;

/* ============================================
 * ADMIN ENDPOINTS (ADMIN ONLY)
 * ============================================ */

export const API_ADMIN_PREFIX = `${API_VERSION_PREFIX}/admin`;

// User Management
export const API_ADMIN_USERS = `${API_ADMIN_PREFIX}/users`;
export const API_ADMIN_USERS_STAFF = `${API_ADMIN_USERS}/staff`;
export const API_ADMIN_USERS_CUSTOMERS = `${API_ADMIN_USERS}/customers`;

// Role Management
export const API_ADMIN_ROLES = `${API_ADMIN_PREFIX}/roles`;

// Admin Reports
export const API_ADMIN_REPORTS = `${API_ADMIN_PREFIX}/reports`;
export const API_ADMIN_REPORTS_CUSTOMERS = `${API_ADMIN_REPORTS}/customers`;
export const API_ADMIN_REPORTS_STAFF = `${API_ADMIN_REPORTS}/staff`;
export const API_ADMIN_REPORTS_SYSTEM = `${API_ADMIN_REPORTS}/system`;

// Seat Management
export const API_ADMIN_GHE = `${API_ADMIN_PREFIX}/ghe`;
export const API_ADMIN_LOAI_GHE = `${API_ADMIN_GHE}/loai-ghe`;
export const API_ADMIN_PHONG_CHIEU_DROP = `${API_ADMIN_GHE}/phong-chieu`;

// Showtime Management
export const API_ADMIN_SUAT_CHIEU = `${API_ADMIN_PREFIX}/suat-chieu`;

// Phim dropdown (public)
export const API_PHIM_DROP = `${API_VERSION_PREFIX}/admin/phim`;

/* ============================================
 * COMMON ENDPOINTS (All Authenticated Users)
 * ============================================ */

export const API_COMMON = `${API_VERSION_PREFIX}/common`;
export const API_COMMON_UPLOAD = `${API_COMMON}/upload`;
export const API_COMMON_HEALTH = `${API_COMMON}/health`;
