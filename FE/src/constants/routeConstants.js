export const ROUTES_CONSTANTS = {
  // Public / Common
  HOME: {
    path: '/',
    name: 'Home'
  },
  LOGIN: {
    path: '/login',
    name: 'Login'
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
      USERS: { path: 'users', name: 'AdminUsers' },
      ROLES: { path: 'roles', name: 'AdminRoles' },
      REPORTS: { path: 'reports', name: 'AdminReports' }
    }
  },

  // STAFF (For future implementation based on Backend)
  STAFF: {
    path: '/staff',
    name: 'Staff',
    children: {
      BOOKINGS: { path: 'bookings', name: 'StaffBookings' },
      MOVIES: { path: 'movies', name: 'StaffMovies' },
      SHOWTIMES: { path: 'showtimes', name: 'StaffShowtimes' },
      ROOMS: { path: 'rooms', name: 'StaffRooms' },
      FOOD: { path: 'food', name: 'StaffFood' }
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
