import { API_VERSION_PREFIX } from './baseApi';

const PUBLIC_PREFIX = `${API_VERSION_PREFIX}/public`;

export const PUBLIC_API = {
  PREFIX: PUBLIC_PREFIX,
  MOVIES: `${PUBLIC_PREFIX}/movies`,
  SHOWTIMES: `${PUBLIC_PREFIX}/showtimes`,
  PROMOTIONS: `${PUBLIC_PREFIX}/promotions`,
  NEWS: `${PUBLIC_PREFIX}/news`,
  BANNERS: `${PUBLIC_PREFIX}/banners`,
  FOOD: `${PUBLIC_PREFIX}/food`,
};

export const PUBLIC_ROUTES = {
  HOME: { path: '/', name: 'Home' },
  MOVIE_DETAILS: { path: '/movie/:id', name: 'MovieDetails' },
  SHOWTIMES: { path: '/showtimes', name: 'Showtimes' },
  PROMOTIONS: { path: '/promotions', name: 'Promotions' },
  CINEMAS: { path: '/cinemas', name: 'CinemaInfo' },
  LOGIN: { path: '/login', name: 'Login' },
  REGISTER: { path: '/register', name: 'Register' },
  FORGOT_PASSWORD: { path: '/forgot-password', name: 'ForgotPassword' },
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
  ERROR: {
    UNAUTHORIZED: { path: '/401', name: 'Unauthorized' },
    FORBIDDEN: { path: '/403', name: 'Forbidden' },
    NOT_FOUND: { path: '/404', name: 'NotFound' },
    CATCH_ALL: { path: '/:pathMatch(.*)*', name: 'not-found-redirect' }
  }
};
