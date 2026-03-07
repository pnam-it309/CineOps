import { API_VERSION_PREFIX } from './baseApi';

const STAFF_PREFIX = `${API_VERSION_PREFIX}/staff`;

export const STAFF_API = {
  PREFIX: STAFF_PREFIX,
  BOOKINGS: `${STAFF_PREFIX}/bookings`,
  MOVIES: `${STAFF_PREFIX}/movies`,
  GENRES: `${STAFF_PREFIX}/genres`,
  AGE_RATINGS: `${STAFF_PREFIX}/age-ratings`,
  SHOWTIMES: `${STAFF_PREFIX}/showtimes`,
  ROOMS: `${STAFF_PREFIX}/rooms`,
  ROOM_TYPES: `${STAFF_PREFIX}/room-types`,
  SEAT_TYPES: `${STAFF_PREFIX}/seat-types`,
  SEATS: `${STAFF_PREFIX}/seats`,
  FOOD: `${STAFF_PREFIX}/food`,
  FOOD_CATEGORIES: `${STAFF_PREFIX}/food-categories`,
  PROMOTIONS: `${STAFF_PREFIX}/promotions`,
  NEWS: `${STAFF_PREFIX}/news`,
  NEWS_CATEGORIES: `${STAFF_PREFIX}/news-categories`,
  BANNERS: `${STAFF_PREFIX}/banners`,
  REPORTS: `${STAFF_PREFIX}/reports`,
  REPORTS_REVENUE: `${STAFF_PREFIX}/reports/revenue`,
  REPORTS_BOOKINGS: `${STAFF_PREFIX}/reports/bookings`,
  REPORTS_MOVIES: `${STAFF_PREFIX}/reports/movies`,
};

export const STAFF_ROUTES = {
  path: '/staff',
  name: 'Staff',
  children: {
    DASHBOARD: { path: 'dashboard', name: 'StaffDashboard' },
    SALES: { path: 'sales', name: 'StaffSales' },
    CHECKIN: { path: 'checkin', name: 'StaffCheckin' },
    INVOICES: { path: 'invoices', name: 'StaffInvoices' },
    SHIFT: { path: 'shift', name: 'StaffShift' }
  }
};
