/**
 * Application-wide constants
 */

// Seat Types
export const SEAT_TYPES = {
  STANDARD: 'standard',
  VIP: 'vip',
  COUPLE: 'couple'
};

// Seat Status
export const SEAT_STATUS = {
  AVAILABLE: 'available',
  SELECTED: 'selected',
  SOLD: 'sold',
  RESERVED: 'reserved'
};

// Seat Pricing (VND)
export const SEAT_PRICES = {
  [SEAT_TYPES.STANDARD]: 85000,
  [SEAT_TYPES.VIP]: 125000,
  [SEAT_TYPES.COUPLE]: 200000
};

// Movie Ratings (Vietnam Classification)
export const MOVIE_RATINGS = {
  P: { label: 'P', description: 'Phổ biến - Mọi lứa tuổi', color: 'success' },
  K: { label: 'K', description: 'Dưới 13 tuổi có PHCM', color: 'info' },
  T13: { label: 'T13', description: '13 tuổi trở lên', color: 'warning' },
  T16: { label: 'T16', description: '16 tuổi trở lên', color: 'warning' },
  T18: { label: 'T18', description: '18 tuổi trở lên', color: 'danger' },
  C: { label: 'C', description: 'Cấm chiếu', color: 'dark' }
};

// Payment Methods
export const PAYMENT_METHODS = {
  VNPAY: { id: 'vnpay', name: 'VNPay', icon: 'wallet' },
  CARD: { id: 'card', name: 'Thẻ tín dụng/ghi nợ', icon: 'credit-card' },
  MOMO: { id: 'momo', name: 'MoMo', icon: 'mobile' },
  CASH: { id: 'cash', name: 'Tiền mặt', icon: 'money-bill' }
};

// Booking Status
export const BOOKING_STATUS = {
  PENDING: 'pending',
  CONFIRMED: 'confirmed',
  CANCELLED: 'cancelled',
  COMPLETED: 'completed'
};

// User Roles
export const USER_ROLES = {
  ADMIN: 'ROLE_ADMIN',
  STAFF: 'ROLE_STAFF',
  CUSTOMER: 'ROLE_CUSTOMER'
};

// Movie Status
export const MOVIE_STATUS = {
  NOW_SHOWING: 'now-showing',
  COMING_SOON: 'coming-soon',
  ENDED: 'ended'
};

// Format Types
export const FORMAT_TYPES = {
  '2D': '2D',
  '3D': '3D',
  IMAX: 'IMAX',
  '4DX': '4DX'
};

// Days of Week (Vietnamese)
export const DAYS_OF_WEEK = [
  'Chủ Nhật',
  'Thứ Hai',
  'Thứ Ba',
  'Thứ Tư',
  'Thứ Năm',
  'Thứ Sáu',
  'Thứ Bảy'
];

// Pagination
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100]
};

// API Endpoints (if needed)
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'auth_token',
  USER_PREFERENCES: 'user_preferences',
  BOOKING_DRAFT: 'booking_draft',
  THEME: 'theme'
};

// Date/Time Formats
export const DATE_FORMATS = {
  SHORT: 'short',
  MEDIUM: 'medium',
  LONG: 'long',
  FULL: 'full'
};

// File Upload
export const FILE_UPLOAD = {
  MAX_SIZE: 5 * 1024 * 1024, // 5MB
  ALLOWED_IMAGE_TYPES: ['image/jpeg', 'image/png', 'image/webp'],
  ALLOWED_VIDEO_TYPES: ['video/mp4', 'video/webm']
};
