import { API_VERSION_PREFIX } from './baseApi';

const ADMIN_PREFIX = `${API_VERSION_PREFIX}/admin`;

export const ADMIN_API = {
  PREFIX: ADMIN_PREFIX,
  
  // User Management
  USERS: `${ADMIN_PREFIX}/users`,
  USERS_STAFF: `${ADMIN_PREFIX}/users/staff`,
  USERS_CUSTOMERS: `${ADMIN_PREFIX}/users/customers`,
  
  // Role Management
  ROLES: `${ADMIN_PREFIX}/roles`,
  
  // Admin Reports
  REPORTS: `${ADMIN_PREFIX}/reports`,
  REPORTS_CUSTOMERS: `${ADMIN_PREFIX}/reports/customers`,
  REPORTS_STAFF: `${ADMIN_PREFIX}/reports/staff`,
  REPORTS_SYSTEM: `${ADMIN_PREFIX}/reports/system`,
  
  // Seat Management
  GHE: `${ADMIN_PREFIX}/ghe`,
  GHE_GENERATE: `${ADMIN_PREFIX}/ghe/generate`,
  LOAI_GHE: `${ADMIN_PREFIX}/ghe/loai-ghe`,
  PHONG_CHIEU_DROP: `${ADMIN_PREFIX}/ghe/phong-chieu`,
  
  // Showtime Management
  SUAT_CHIEU: `${ADMIN_PREFIX}/suat-chieu`,
  
  // Phim dropdown
  PHIM_DROP: `${ADMIN_PREFIX}/phim`,
};

export const ADMIN_ROUTES = {
  path: '/admin',
  name: 'Admin',
  children: {
    DASHBOARD: { path: 'dashboard', name: 'AdminDashboard' },
    STATISTICS: { path: 'statistics', name: 'AdminStatistics' },
    POS: { path: 'pos', name: 'AdminPOS' },
    CHECKIN: { path: 'checkin', name: 'AdminCheckin' },
    INVOICES: { path: 'invoices', name: 'AdminInvoices' },
    INVOICES_DETAIL: { path: 'invoices/:id', name: 'AdminInvoiceDetail' },
    TICKETS: { path: 'tickets', name: 'AdminTickets' },
    SEATS: { 
      path: 'seats', 
      name: 'AdminSeats',
      children: {
        LIST: { path: 'list', name: 'AdminSeatsList' },
        LAYOUT: { path: 'layout', name: 'AdminSeatsLayout' },
        CONFIG: { path: 'config', name: 'AdminSeatsConfig' }
      }
    },
    MOVIES_SCHEDULE: { path: 'movies', name: 'AdminMovies' },
    MOVIES_ADD: { path: 'movies/add', name: 'AdminMoviesAdd' },
    MOVIES_EDIT: { path: 'movies/edit/:id', name: 'AdminMoviesEdit' },
    SHOWTIMES: { path: 'showtimes', name: 'AdminShowtimes' },
    SCHEDULE: { 
      path: 'schedule', 
      name: 'AdminSchedule',
      children: {
        VISUAL: { path: 'visual', name: 'AdminScheduleVisual' }
      }
    },
    FOOD: { path: 'food', name: 'AdminFood' },
    VOUCHERS: { path: 'vouchers', name: 'AdminVouchers' },
    VOUCHERS_ADD: { path: 'vouchers/add', name: 'AdminVouchersAdd' },
    VOUCHERS_EDIT: { path: 'vouchers/edit/:id', name: 'AdminVouchersEdit' },
    CUSTOMERS: { path: 'customers', name: 'AdminCustomers' },
    STAFF: { path: 'staff', name: 'AdminStaff' },
    STAFF_ADD: { path: 'staff/add', name: 'AdminStaffAdd' },
    STAFF_EDIT: { path: 'staff/edit/:id', name: 'AdminStaffEdit' }
  }
};
