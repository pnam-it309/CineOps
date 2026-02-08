/**
 * Format Vietnamese currency (VND)
 * @param {number} amount - Amount in VND
 * @returns {string} Formatted currency string
 */
export function formatCurrency(amount) {
  if (typeof amount !== 'number') return '0₫';
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount);
}

/**
 * Format date in Vietnamese locale
 * @param {Date|string} date - Date to format
 * @param {string} format - Format type: 'short', 'medium', 'long', 'full'
 * @returns {string} Formatted date string
 */
export function formatDate(date, format = 'medium') {
  if (!date) return '';
  
  const d = typeof date === 'string' ? new Date(date) : date;
  
  const options = {
    short: { month: 'short', day: 'numeric', year: 'numeric' },
    medium: { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' },
    long: { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' },
    full: { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' }
  };

  return d.toLocaleDateString('vi-VN', options[format] || options.medium);
}

/**
 * Format time (HH:mm)
 * @param {Date|string} date - Date/time to format
 * @returns {string} Formatted time string
 */
export function formatTime(date) {
  if (!date) return '';
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

/**
 * Format movie duration
 * @param {number} minutes - Duration in minutes
 * @returns {string} Formatted duration (e.g., "2h 30m" or "2 giờ 30 phút")
 */
export function formatDuration(minutes, locale = 'vi') {
  if (typeof minutes !== 'number' || minutes < 0) return '';
  
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  
  if (locale === 'vi') {
    if (hours === 0) return `${mins} phút`;
    if (mins === 0) return `${hours} giờ`;
    return `${hours} giờ ${mins} phút`;
  }
  
  // English format
  if (hours === 0) return `${mins}m`;
  if (mins === 0) return `${hours}h`;
  return `${hours}h ${mins}m`;
}

/**
 * Format Vietnamese phone number
 * @param {string} phone - Phone number
 * @returns {string} Formatted phone number
 */
export function formatPhoneNumber(phone) {
  if (!phone) return '';
  
  // Remove all non-digits
  const cleaned = phone.replace(/\D/g, '');
  
  // Format as XXX-XXX-XXXX or 0XXX-XXX-XXX
  if (cleaned.length === 10) {
    return cleaned.replace(/(\d{4})(\d{3})(\d{3})/, '$1-$2-$3');
  }
  
  return phone;
}

/**
 * Truncate text with ellipsis
 * @param {string} text - Text to truncate
 * @param {number} maxLength - Maximum length
 * @returns {string} Truncated text
 */
export function truncate(text, maxLength = 100) {
  if (!text || text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}

/**
 * Format file size
 * @param {number} bytes - Size in bytes
 * @returns {string} Formatted size
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}
