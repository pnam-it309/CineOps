import { PUBLIC_ROUTES } from './publicConstants';
import { ADMIN_ROUTES } from './adminConstants';
import { STAFF_ROUTES } from './staffConstants';
import { CUSTOMER_ROUTES } from './customerConstants';

export const ROUTES_CONSTANTS = {
  ...PUBLIC_ROUTES,
  ADMIN: ADMIN_ROUTES,
  STAFF: STAFF_ROUTES,
  CUSTOMER: CUSTOMER_ROUTES
};

