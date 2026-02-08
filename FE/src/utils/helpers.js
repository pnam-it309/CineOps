/**
 * Miscellaneous helper functions
 */

/**
 * Generate cinema seat matrix
 * @param {number} rows - Number of rows
 * @param {number} cols - Number of columns per row
 * @param {object} options - Configuration options
 * @returns {Array} Seat matrix
 */
export function generateSeatMatrix(rows = 10, cols = 12, options = {}) {
  const {
    vipRows = [7, 8, 9], // Rows that are VIP
    coupleSeats = [], // Array of [row, col] for couple seats
    unavailableSeats = [] // Array of [row, col] for unavailable seats
  } = options;

  const matrix = [];
  const rowLabels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

  for (let row = 0; row < rows; row++) {
    const rowSeats = [];
    const rowLabel = rowLabels[row];

    for (let col = 0; col < cols; col++) {
      const seatNumber = col + 1;
      const seatId = `${rowLabel}${seatNumber}`;
      
      // Determine seat type
      let type = 'standard';
      if (vipRows.includes(row)) {
        type = 'vip';
      }
      if (coupleSeats.some(([r, c]) => r === row && c === col)) {
        type = 'couple';
      }

      // Determine availability
      const isUnavailable = unavailableSeats.some(([r, c]) => r === row && c === col);

      rowSeats.push({
        id: seatId,
        row: rowLabel,
        number: seatNumber,
        type,
        status: isUnavailable ? 'unavailable' : 'available',
        price: getSeatPrice(type)
      });
    }

    matrix.push(rowSeats);
  }

  return matrix;
}

/**
 * Get seat price by type
 * @param {string} type - Seat type
 * @returns {number} Price in VND
 */
function getSeatPrice(type) {
  const prices = {
    standard: 85000,
    vip: 125000,
    couple: 200000
  };
  return prices[type] || prices.standard;
}

/**
 * Calculate total from items array
 * @param {Array} items - Array of items with price and quantity
 * @returns {number} Total amount
 */
export function calculateTotal(items) {
  if (!Array.isArray(items)) return 0;
  
  return items.reduce((total, item) => {
    const price = item.price || 0;
    const quantity = item.quantity || 1;
    return total + (price * quantity);
  }, 0);
}

/**
 * Group array of objects by a key
 * @param {Array} array - Array to group
 * @param {string} key - Key to group by
 * @returns {Object} Grouped object
 */
export function groupBy(array, key) {
  if (!Array.isArray(array)) return {};
  
  return array.reduce((result, item) => {
    const groupKey = item[key];
    if (!result[groupKey]) {
      result[groupKey] = [];
    }
    result[groupKey].push(item);
    return result;
  }, {});
}

/**
 * Deep clone an object
 * @param {*} obj - Object to clone
 * @returns {*} Cloned object
 */
export function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj));
}

/**
 * Debounce function
 * @param {Function} func - Function to debounce
 * @param {number} wait - Wait time in ms
 * @returns {Function} Debounced function
 */
export function debounce(func, wait = 300) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * Generate random ID
 * @param {number} length - Length of ID
 * @returns {string} Random ID
 */
export function generateId(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * Sleep/delay function
 * @param {number} ms - Milliseconds to sleep
 * @returns {Promise}
 */
export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Check if object is empty
 * @param {Object} obj - Object to check
 * @returns {boolean}
 */
export function isEmpty(obj) {
  if (obj == null) return true;
  if (Array.isArray(obj) || typeof obj === 'string') return obj.length === 0;
  if (typeof obj === 'object') return Object.keys(obj).length === 0;
  return false;
}

/**
 * Sort array of objects by key
 * @param {Array} array - Array to sort
 * @param {string} key - Key to sort by
 * @param {string} order - 'asc' or 'desc'
 * @returns {Array} Sorted array
 */
export function sortBy(array, key, order = 'asc') {
  if (!Array.isArray(array)) return [];
  
  return [...array].sort((a, b) => {
    const aValue = a[key];
    const bValue = b[key];
    
    if (aValue < bValue) return order === 'asc' ? -1 : 1;
    if (aValue > bValue) return order === 'asc' ? 1 : -1;
    return 0;
  });
}

/**
 * Remove duplicates from array
 * @param {Array} array - Array with duplicates
 * @param {string} key - Key to check for uniqueness (optional)
 * @returns {Array} Array without duplicates
 */
export function unique(array, key = null) {
  if (!Array.isArray(array)) return [];
  
  if (key) {
    const seen = new Set();
    return array.filter(item => {
      const value = item[key];
      if (seen.has(value)) return false;
      seen.add(value);
      return true;
    });
  }
  
  return [...new Set(array)];
}
