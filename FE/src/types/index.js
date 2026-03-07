/**
 * JSDoc type definitions for CineOps
 * Hỗ trợ nhắc lệnh (IntelliSense) cho dự án thuần Javascript.
 */

/**
 * @typedef {Object} Movie
 * @property {number} id
 * @property {string} title
 * @property {string} [vietnameseTitle]
 * @property {string} genre
 * @property {number} duration - Thời lượng phim (phút)
 * @property {string} rating - Phân loại độ tuổi (P, K, T13, T16, T18, C)
 * @property {number} imdbRating
 * @property {string} releaseDate - Chuỗi ISO Date
 * @property {string} poster - URL ảnh
 * @property {string} backdrop - URL ảnh
 * @property {string} trailer - URL YouTube
 * @property {string} synopsis
 * @property {string} director
 * @property {string[]} cast
 * @property {'now-showing'|'coming-soon'|'ended'} status
 */

/**
 * @typedef {Object} Showtime
 * @property {number} id
 * @property {number} movieId
 * @property {number} cinemaId
 * @property {number} roomId
 * @property {string} date - Chuỗi ISO Date
 * @property {string} time - Kiểu (HH:mm)
 * @property {'2D'|'3D'|'IMAX'|'4DX'} format
 * @property {number} price - Giá vé cơ sở
 * @property {number} availableSeats
 */

/**
 * @typedef {Object} Seat
 * @property {string} id - Mã ghế (VD: "A1", "B5")
 * @property {string} row - Hàng
 * @property {number} number - Số ghế
 * @property {'standard'|'vip'|'couple'} type
 * @property {'available'|'selected'|'sold'|'reserved'|'unavailable'} status
 * @property {number} price
 */

/**
 * @typedef {Object} FoodItem
 * @property {number} id
 * @property {string} name
 * @property {string} description
 * @property {number} price
 * @property {string} image
 * @property {string} [category]
 * @property {boolean} [available]
 */

/**
 * @typedef {Object} User
 * @property {number} id
 * @property {string} username
 * @property {string} email
 * @property {string} [fullName]
 * @property {string} [phone]
 * @property {string} [avatar]
 * @property {string[]} roles
 * @property {boolean} [isActive]
 * @property {string} createdAt
 */

/**
 * @typedef {Object} Promotion
 * @property {number} id
 * @property {string} title
 * @property {string} description
 * @property {string} image
 * @property {string} code - Mã Voucher
 * @property {number} [discountPercent]
 * @property {number} [discountAmount]
 * @property {string} validUntil
 * @property {boolean} [isActive]
 */

/**
 * @typedef {Object} ApiResponse
 * @template T
 * @property {boolean} success
 * @property {T} [data]
 * @property {string} [message]
 * @property {number} [code]
 */

export {};
