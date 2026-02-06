package service.cinema.be.infrastructure.constant;

public class MappingConstants {

    private MappingConstants() {
        // Prevent instantiation
    }

    /* ============================================
     * BASE PATHS
     * ============================================ */
    
    /** API Version Prefix */
    public static final String API_VERSION_PREFIX = "/api/v1";

    /* ============================================
     * PUBLIC ENDPOINTS (No Authentication)
     * ============================================ */
    
    /** Authentication endpoints */
    public static final String API_AUTH_PREFIX = API_VERSION_PREFIX + "/auth";
    public static final String API_AUTH_LOGIN = API_AUTH_PREFIX + "/login";
    public static final String API_AUTH_REGISTER = API_AUTH_PREFIX + "/register";
    public static final String API_AUTH_REFRESH = API_AUTH_PREFIX + "/refresh";
    public static final String API_AUTH_LOGOUT = API_AUTH_PREFIX + "/logout";
    public static final String API_AUTH_ME = API_AUTH_PREFIX + "/me";

    /** OAuth2 endpoints */
    public static final String API_OAUTH2_PREFIX = API_VERSION_PREFIX + "/oauth2";
    public static final String API_OAUTH2_AUTHORIZE = API_OAUTH2_PREFIX + "/authorize";

    /** Public content endpoints (no auth required) */
    public static final String API_PUBLIC_PREFIX = API_VERSION_PREFIX + "/public";
    public static final String API_PUBLIC_MOVIES = API_PUBLIC_PREFIX + "/movies";
    public static final String API_PUBLIC_SHOWTIMES = API_PUBLIC_PREFIX + "/showtimes";
    public static final String API_PUBLIC_PROMOTIONS = API_PUBLIC_PREFIX + "/promotions";
    public static final String API_PUBLIC_NEWS = API_PUBLIC_PREFIX + "/news";
    public static final String API_PUBLIC_BANNERS = API_PUBLIC_PREFIX + "/banners";
    public static final String API_PUBLIC_FOOD = API_PUBLIC_PREFIX + "/food";

    /* ============================================
     * CUSTOMER ENDPOINTS (CUSTOMER + ADMIN)
     * ============================================ */
    
    public static final String API_CUSTOMER_PREFIX = API_VERSION_PREFIX + "/customer";
    public static final String API_CUSTOMER_BOOKINGS = API_CUSTOMER_PREFIX + "/bookings";
    public static final String API_CUSTOMER_REVIEWS = API_CUSTOMER_PREFIX + "/reviews";
    public static final String API_CUSTOMER_PROFILE = API_CUSTOMER_PREFIX + "/profile";
    public static final String API_CUSTOMER_TICKETS = API_CUSTOMER_PREFIX + "/tickets";

    /* ============================================
     * STAFF ENDPOINTS (STAFF + ADMIN)
     * ============================================ */
    
    public static final String API_STAFF_PREFIX = API_VERSION_PREFIX + "/staff";
    
    // Booking Management (Staff)
    public static final String API_STAFF_BOOKINGS = API_STAFF_PREFIX + "/bookings";
    
    // Movie Management
    public static final String API_STAFF_MOVIES = API_STAFF_PREFIX + "/movies";
    public static final String API_STAFF_GENRES = API_STAFF_PREFIX + "/genres";
    public static final String API_STAFF_AGE_RATINGS = API_STAFF_PREFIX + "/age-ratings";
    
    // Showtime Management
    public static final String API_STAFF_SHOWTIMES = API_STAFF_PREFIX + "/showtimes";
    
    // Room & Seat Management
    public static final String API_STAFF_ROOMS = API_STAFF_PREFIX + "/rooms";
    public static final String API_STAFF_ROOM_TYPES = API_STAFF_PREFIX + "/room-types";
    public static final String API_STAFF_SEAT_TYPES = API_STAFF_PREFIX + "/seat-types";
    public static final String API_STAFF_SEATS = API_STAFF_PREFIX + "/seats";
    
    // Food & Beverage Management
    public static final String API_STAFF_FOOD = API_STAFF_PREFIX + "/food";
    public static final String API_STAFF_FOOD_CATEGORIES = API_STAFF_PREFIX + "/food-categories";
    
    // Promotion Management
    public static final String API_STAFF_PROMOTIONS = API_STAFF_PREFIX + "/promotions";
    
    // Content Management
    public static final String API_STAFF_NEWS = API_STAFF_PREFIX + "/news";
    public static final String API_STAFF_NEWS_CATEGORIES = API_STAFF_PREFIX + "/news-categories";
    public static final String API_STAFF_BANNERS = API_STAFF_PREFIX + "/banners";
    
    // Reports
    public static final String API_STAFF_REPORTS = API_STAFF_PREFIX + "/reports";
    public static final String API_STAFF_REPORTS_REVENUE = API_STAFF_REPORTS + "/revenue";
    public static final String API_STAFF_REPORTS_BOOKINGS = API_STAFF_REPORTS + "/bookings";
    public static final String API_STAFF_REPORTS_MOVIES = API_STAFF_REPORTS + "/movies";

    /* ============================================
     * ADMIN ENDPOINTS (ADMIN ONLY)
     * ============================================ */
    
    public static final String API_ADMIN_PREFIX = API_VERSION_PREFIX + "/admin";
    
    // User Management
    public static final String API_ADMIN_USERS = API_ADMIN_PREFIX + "/users";
    public static final String API_ADMIN_USERS_STAFF = API_ADMIN_USERS + "/staff";
    public static final String API_ADMIN_USERS_CUSTOMERS = API_ADMIN_USERS + "/customers";
    
    // Role Management
    public static final String API_ADMIN_ROLES = API_ADMIN_PREFIX + "/roles";
    
    // Admin Reports
    public static final String API_ADMIN_REPORTS = API_ADMIN_PREFIX + "/reports";
    public static final String API_ADMIN_REPORTS_CUSTOMERS = API_ADMIN_REPORTS + "/customers";
    public static final String API_ADMIN_REPORTS_STAFF = API_ADMIN_REPORTS + "/staff";
    public static final String API_ADMIN_REPORTS_SYSTEM = API_ADMIN_REPORTS + "/system";

    /* ============================================
     * COMMON ENDPOINTS (All Authenticated Users)
     * ============================================ */
    
    public static final String API_COMMON = API_VERSION_PREFIX + "/common";
    public static final String API_COMMON_UPLOAD = API_COMMON + "/upload";
    public static final String API_COMMON_HEALTH = API_COMMON + "/health";

    /* ============================================
     * LEGACY SUPPORT (Deprecated)
     * ============================================ */
    
    /** @deprecated Use API_PUBLIC_PREFIX instead */
    @Deprecated
    public static final String API_PERMITALL_PREFIX = API_VERSION_PREFIX + "/permitall";

}
