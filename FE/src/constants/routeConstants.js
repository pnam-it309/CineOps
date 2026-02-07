export const ROUTES_CONSTANTS = {
  // Public / Common
  HOME: {
    path: '/',
    name: 'Home'
  },
  MOVIE_DETAILS: {
    path: '/movie/:id',
    name: 'MovieDetails'
  },
  LOGIN: {
    path: '/login',
    name: 'Login'
  },
  REGISTER: {
    path: '/register',
    name: 'Register'
  },
  FORGOT_PASSWORD: {
    path: '/forgot-password',
    name: 'ForgotPassword'
  },
  BOOKING: {
    path: '/booking',
    name: 'Booking',
    children: {
      STEP1: { path: 'step1/:movieId?', name: 'SelectShowtime' },
      STEP2: { path: 'step2/:showtimeId', name: 'SelectSeats' },
      STEP3: { path: 'step3', name: 'SelectFood' },
      STEP4: { path: 'step4', name: 'Payment' },
      RESULT: { path: 'result', name: 'BookingResult' }
    }
  },
  NEWS: {
    path: '/news',
    name: 'News',
    children: {
      LIST: { path: '', name: 'NewsList' },
      DETAIL: { path: ':id', name: 'NewsDetail' }
    }
  },

  // ERROR PAGES
  ERROR: {
    UNAUTHORIZED: {
      path: '/401',
      name: 'Unauthorized'
    },
    FORBIDDEN: {
      path: '/403',
      name: 'Forbidden'
    },
    NOT_FOUND: {
      path: '/404',
      name: 'NotFound'
    },
    CATCH_ALL: {
      path: '/:pathMatch(.*)*',
      name: 'not-found-redirect'
    }
  },

  // ADMIN (For future implementation based on Backend)
  ADMIN: {
    path: '/admin',
    name: 'Admin',
    children: {
      DASHBOARD: { path: 'dashboard', name: 'AdminDashboard' },
      MOVIES: { path: 'movies', name: 'AdminMovies' },
      ROOMS: { path: 'rooms', name: 'AdminRooms' },
      SHOWTIMES: { path: 'showtimes', name: 'AdminShowtimes' },
      PRICING: { path: 'pricing', name: 'AdminPricing' },
      FOOD: { path: 'food', name: 'AdminFood' },
      VOUCHERS: { path: 'vouchers', name: 'AdminVouchers' },
      BANNERS: { path: 'banners', name: 'AdminBanners' },
      STAFF: { path: 'staff', name: 'AdminStaff' },
      CUSTOMERS: { path: 'customers', name: 'AdminCustomers' },
      REPORTS: { path: 'reports', name: 'AdminReports' },
      REVIEWS: { path: 'reviews', name: 'AdminReviews' }
    }
  },

  // STAFF (For future implementation based on Backend)
  STAFF: {
    path: '/staff',
    name: 'Staff',
    children: {
      DASHBOARD: { path: 'dashboard', name: 'StaffDashboard' },
      SALES: { path: 'sales', name: 'StaffSales' },
      CHECKIN: { path: 'checkin', name: 'StaffCheckin' },
      INVOICES: { path: 'invoices', name: 'StaffInvoices' },
      SHIFT: { path: 'shift', name: 'StaffShift' }
    }
  },

  // CUSTOMER (For authenticated user pages)
  CUSTOMER: {
    path: '/customer',
    name: 'Customer',
    children: {
      PROFILE: { path: 'profile', name: 'CustomerProfile' },
      TICKETS: { path: 'tickets', name: 'CustomerTickets' },
      BOOKINGS: { path: 'bookings', name: 'CustomerBookings' }
    }
  }
};
