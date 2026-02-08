/**
 * TypeScript type definitions for CineOps
 * Provides IntelliSense support even in JavaScript projects
 */

/**
 * @typedef {Object} Movie
 * @property {number} id
 * @property {string} title
 * @property {string} [vietnameseTitle]
 * @property {string} genre
 * @property {number} duration - Duration in minutes
 * @property {string} rating - Age rating (P, K, T13, T16, T18, C)
 * @property {number} imdbRating
 * @property {string} releaseDate - ISO date string
 * @property {string} poster - Poster image URL
 * @property {string} backdrop - Backdrop image URL
 * @property {string} trailer - YouTube trailer URL
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
 * @property {string} date - ISO date string
 * @property {string} time - Time string (HH:mm)
 * @property {'2D'|'3D'|'IMAX'|'4DX'} format
 * @property {number} price - Base ticket price
 * @property {number} availableSeats
 */

/**
 * @typedef {Object} Seat
 * @property {string} id - Seat identifier (e.g., "A1", "B5")
 * @property {string} row - Row letter
 * @property {number} number - Seat number in row
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
 * @property {string} image - Image URL
 * @property {string} [category] - e.g., 'combo', 'popcorn', 'drink'
 * @property {boolean} [available]
 */

/**
 * @typedef {Object} Booking
 * @property {number} [id]
 * @property {number} userId
 * @property {number} showtimeId
 * @property {Seat[]} seats
 * @property {Array<{item: FoodItem, quantity: number}>} food
 * @property {number} totalAmount
 * @property {'pending'|'confirmed'|'cancelled'|'completed'} status
 * @property {string} paymentMethod
 * @property {string} [voucherCode]
 * @property {string} createdAt - ISO date string
 * @property {string} [qrCode]
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
 * @property {string} createdAt - ISO date string
 */

/**
 * @typedef {Object} Cinema
 * @property {number} id
 * @property {string} name
 * @property {string} location
 * @property {string} [address]
 * @property {string} [phone]
 * @property {Room[]} [rooms]
 */

/**
 * @typedef {Object} Room
 * @property {number} id
 * @property {string} name
 * @property {number} cinemaId
 * @property {number} totalSeats
 * @property {Seat[][]} seatLayout
 */

/**
 * @typedef {Object} Promotion
 * @property {number} id
 * @property {string} title
 * @property {string} description
 * @property {string} image
 * @property {string} code - Voucher code
 * @property {number} [discountPercent]
 * @property {number} [discountAmount]
 * @property {string} validUntil - ISO date string
 * @property {boolean} [isActive]
 */

/**
 * @typedef {Object} NewsArticle
 * @property {number} id
 * @property {string} title
 * @property {string} category - e.g., 'Promotion', 'Event', 'News'
 * @property {string} date - Display date string
 * @property {string} summary
 * @property {string} [content] - HTML content
 * @property {string} image
 * @property {string} [author]
 * @property {Array<{id: number, title: string}>} [relatedNews]
 */

/**
 * @typedef {Object} ApiResponse
 * @template T
 * @property {boolean} success
 * @property {T} [data]
 * @property {string} [message]
 * @property {number} [code]
 */

/**
 * @typedef {Object} PaginatedResponse
 * @template T
 * @property {T[]} items
 * @property {number} total
 * @property {number} page
 * @property {number} pageSize
 * @property {number} totalPages
 */

export {};
