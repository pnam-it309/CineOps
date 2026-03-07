import { API_VERSION_PREFIX } from './baseApi';

const AUTH_PREFIX = `${API_VERSION_PREFIX}/auth`;
const OAUTH2_PREFIX = `${API_VERSION_PREFIX}/oauth2`;

export const AUTH_API = {
  PREFIX: AUTH_PREFIX,
  LOGIN: `${AUTH_PREFIX}/login`,
  REGISTER: `${AUTH_PREFIX}/register`,
  REFRESH: `${AUTH_PREFIX}/refresh`,
  LOGOUT: `${AUTH_PREFIX}/logout`,
  ME: `${AUTH_PREFIX}/me`,
  
  OAUTH2_AUTHORIZE: `${OAUTH2_PREFIX}/authorize`,
};
