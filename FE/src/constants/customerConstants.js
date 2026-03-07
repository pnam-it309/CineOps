import { API_VERSION_PREFIX } from './baseApi';

const CUSTOMER_PREFIX = `${API_VERSION_PREFIX}/customer`;

export const CUSTOMER_API = {
  PREFIX: CUSTOMER_PREFIX,
  BOOKINGS: `${CUSTOMER_PREFIX}/bookings`,
  REVIEWS: `${CUSTOMER_PREFIX}/reviews`,
  PROFILE: `${CUSTOMER_PREFIX}/profile`,
  TICKETS: `${CUSTOMER_PREFIX}/tickets`,
};

export const CUSTOMER_ROUTES = {
  path: '/customer',
  name: 'Customer',
  children: {
    PROFILE: { path: 'profile', name: 'CustomerProfile' },
    TICKETS: { path: 'tickets', name: 'CustomerTickets' },
    BOOKINGS: { path: 'bookings', name: 'CustomerBookings' }
  }
};
