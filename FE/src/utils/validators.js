/**
 * Check if value is required (not empty)
 * @param {*} value - Value to check
 * @returns {boolean}
 */
export function isRequired(value) {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  if (Array.isArray(value)) return value.length > 0;
  return true;
}

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean}
 */
export function isValidEmail(email) {
  if (!email) return false;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate Vietnamese phone number
 * Accepts formats: 0XXXXXXXXX (10 digits) or +84XXXXXXXXX
 * @param {string} phone - Phone number to validate
 * @returns {boolean}
 */
export function isValidPhone(phone) {
  if (!phone) return false;
  
  // Remove all spaces and dashes
  const cleaned = phone.replace(/[\s-]/g, '');
  
  // Vietnamese phone: starts with 0 and has 10 digits, or +84 with 9 digits
  const phoneRegex = /^(0[3|5|7|8|9])\d{8}$/;
  const intlPhoneRegex = /^\+84[3|5|7|8|9]\d{8}$/;
  
  return phoneRegex.test(cleaned) || intlPhoneRegex.test(cleaned);
}

/**
 * Validate password strength
 * Requirements: At least 8 characters, 1 uppercase, 1 lowercase, 1 number
 * @param {string} password - Password to validate
 * @returns {object} { isValid: boolean, errors: string[] }
 */
export function validatePassword(password) {
  const errors = [];
  
  if (!password || password.length < 8) {
    errors.push('Mật khẩu phải có ít nhất 8 ký tự');
  }
  
  if (!/[A-Z]/.test(password)) {
    errors.push('Mật khẩu phải có ít nhất 1 chữ hoa');
  }
  
  if (!/[a-z]/.test(password)) {
    errors.push('Mật khẩu phải có ít nhất 1 chữ thường');
  }
  
  if (!/[0-9]/.test(password)) {
    errors.push('Mật khẩu phải có ít nhất 1 chữ số');
  }
  
  return {
    isValid: errors.length === 0,
    errors
  };
}

/**
 * Simple password validation (just length check)
 * @param {string} password - Password to validate
 * @returns {boolean}
 */
export function isValidPassword(password) {
  return password && password.length >= 8;
}

/**
 * Validate Vietnamese ID card number (CCCD/CMND)
 * @param {string} idNumber - ID number to validate
 * @returns {boolean}
 */
export function isValidIdCard(idNumber) {
  if (!idNumber) return false;
  
  // CMND: 9 or 12 digits, CCCD: 12 digits
  const cleaned = idNumber.replace(/\s/g, '');
  return /^\d{9}$/.test(cleaned) || /^\d{12}$/.test(cleaned);
}

/**
 * Validate date is in the future
 * @param {Date|string} date - Date to validate
 * @returns {boolean}
 */
export function isFutureDate(date) {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d > new Date();
}

/**
 * Validate date is in the past
 * @param {Date|string} date - Date to validate
 * @returns {boolean}
 */
export function isPastDate(date) {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d < new Date();
}

/**
 * Validate minimum length
 * @param {string} value - Value to validate
 * @param {number} minLength - Minimum length
 * @returns {boolean}
 */
export function hasMinLength(value, minLength) {
  return value && value.length >= minLength;
}

/**
 * Validate maximum length
 * @param {string} value - Value to validate
 * @param {number} maxLength - Maximum length
 * @returns {boolean}
 */
export function hasMaxLength(value, maxLength) {
  return !value || value.length <= maxLength;
}
