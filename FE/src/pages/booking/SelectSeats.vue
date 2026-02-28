<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookingStore } from '@/stores/booking';
// import { mockMovies, mockShowtimes } from '@/mock';
const mockMovies = ref([]);
const mockShowtimes = ref([]);

import { formatCurrency } from '@/utils/formatters';
import { SEAT_TYPES, SEAT_PRICES } from '@/utils/constants';

const route = useRoute();
const router = useRouter();
const bookingStore = useBookingStore();

// State
const showtimeId = ref(null);
const movie = ref({ title: 'Đang tải...' });
const showtime = ref({});

// Seat matrix generation (Local)
const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
const cols = 12;

const generateSeats = () => {
  const seats = [];
  rows.forEach((row, rowIndex) => {
    for (let col = 1; col <= cols; col++) {
      const isSold = Math.random() < 0.2;
      let type = SEAT_TYPES.STANDARD;
      
      if (rowIndex >= 6) {
        type = SEAT_TYPES.COUPLE;
      } else if (rowIndex >= 4) {
        type = SEAT_TYPES.VIP;
      }
      
      const price = SEAT_PRICES[type];

      seats.push({
        id: `${row}${col}`,
        row,
        col,
        type,
        price,
        isSold
      });
    }
  });
  return seats;
};

const allSeats = ref([]);

const isSelected = (seatId) => {
  return bookingStore.selectedSeats.some(s => s.id === seatId);
};

const handleSeatClick = (seat) => {
  if (seat.isSold) return;
  bookingStore.toggleSeat(seat);
};

const goToNextStep = () => {
  if (bookingStore.selectedSeats.length === 0) {
    return;
  }
  router.push({ name: 'SelectFood' });
};

onMounted(() => {
  showtimeId.value = parseInt(route.params.showtimeId);
  showtime.value = mockShowtimes.value?.find(s => s.id === showtimeId.value) || {};
  movie.value = mockMovies.value?.find(m => m.id === showtime.value?.movieId) || { title: 'Đang tải...' };
  
  if (showtime.value?.id) {
    bookingStore.setBasicInfo({
      movieId: movie.value.id,
      movieTitle: movie.value.title,
      cinemaId: showtime.value.cinemaId,
      cinemaName: 'CineOps Central',
      showtimeId: showtime.value.id,
      showtimeDate: 'Feb 7, 2026',
      showtimeTime: showtime.value.time
    });
  }
  
  allSeats.value = generateSeats();
});
</script>

<template>
  <div class="seat-selection container py-5">
    <div class="row g-4">
      <!-- Seat Map -->
      <div class="col-lg-8">
        <div class="seat-panel">
          <div class="screen-indicator mb-5">
            <div class="screen-box mx-auto mb-3">
              <div class="screen-glow"></div>
            </div>
            <p class="text-center text-secondary small fw-bold tracking-wider">MÀN HÌNH</p>
          </div>

          <div class="seat-grid d-flex flex-column gap-2 mb-5">
            <div v-for="row in rows" :key="row" class="d-flex justify-content-center gap-1 gap-md-2 align-items-center">
              <div class="row-label me-2 text-secondary small fw-bold">{{ row }}</div>
              <div 
                v-for="seat in allSeats.filter(s => s.row === row)" 
                :key="seat.id"
                class="seat"
                :class="[
                  seat.type,
                  seat.isSold ? 'occupied' : (isSelected(seat.id) ? 'selected' : '')
                ]"
                @click="handleSeatClick(seat)"
              ></div>
              <div class="row-label ms-2 text-secondary small fw-bold">{{ row }}</div>
            </div>
          </div>

          <!-- Legend -->
          <div class="seat-legend glass p-3 rounded-3 d-flex justify-content-center gap-4 flex-wrap">
            <div class="d-flex align-items-center gap-2">
              <div class="seat standard sm"></div>
              <span class="small text-white">Ghế trống</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat selected sm"></div>
              <span class="small text-white">Đang chọn</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat occupied sm"></div>
              <span class="small text-white">Đã bán</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat vip sm"></div>
              <span class="small text-white">VIP (+20k)</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat couple sm"></div>
              <span class="small text-white">Ghế đôi</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Reservation Summary -->
      <div class="col-lg-4">
        <div class="summary-panel glass-strong sticky-top" style="top: 100px;">
          <div class="gradient-header p-4 text-white position-relative overflow-hidden">
            <div class="header-glow"></div>
            <h4 class="fw-bold mb-1 position-relative">{{ movie?.title }}</h4>
            <p class="opacity-75 small mb-0 position-relative">{{ bookingStore.cinemaName }} • {{ bookingStore.showtimeTime }}</p>
          </div>
          
          <div class="p-4">
            <div class="mb-4">
              <label class="small text-secondary fw-bold text-uppercase mb-3 d-block tracking-wider">Ghế đang chọn</label>
              <div v-if="bookingStore.selectedSeats.length > 0" class="d-flex flex-wrap gap-2">
                <span 
                  v-for="seat in bookingStore.selectedSeats" 
                  :key="seat.id"
                  class="seat-badge"
                >
                  {{ seat.id }}
                </span>
              </div>
              <p v-else class="text-secondary small italic mb-0">Chưa có ghế nào được chọn.</p>
            </div>

            <div class="divider mb-4"></div>

            <div class="d-flex justify-content-between align-items-center mb-4">
              <span class="fw-bold text-white">Tổng cộng</span>
              <span class="fs-3 fw-bold text-gradient">{{ bookingStore.seatsTotal.toLocaleString() }}đ</span>
            </div>

            <button 
              class="btn btn-gradient w-100 py-3 rounded-3 fw-bold shadow-lg"
              :disabled="bookingStore.selectedSeats.length === 0"
              @click="goToNextStep"
            >
              Tiếp tục chọn bắp nước
            </button>
            <p class="text-center mt-3 mb-0">
              <router-link :to="{ name: 'SelectShowtime', params: { movieId: movie?.id }}" class="text-secondary small hover-underline">
                Quay lại xem lịch chiếu
              </router-link>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.seat-selection {
  min-height: 100vh;
  background: var(--dark-bg-primary);
}

/* Screen Indicator */
.screen-box {
  width: 80%;
  max-width: 500px;
  height: 8px;
  background: linear-gradient(90deg, transparent 0%, var(--brand-gradient), transparent 100%);
  border-radius: 50%;
  position: relative;
}

.screen-glow {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 30px;
  background: var(--brand-gradient);
  opacity: 0.3;
  filter: blur(20px);
  animation: screen-pulse 3s ease-in-out infinite;
}

@keyframes screen-pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.5; }
}

/* Gradient Header */
.gradient-header {
  background: var(--brand-gradient);
  border-radius: 16px 16px 0 0;
}

.header-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
}

.summary-panel {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

/* Seat Badge */
.seat-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  background: var(--brand-gradient);
  border-radius: 8px;
  color: white;
  font-weight: 600;
  font-size: 0.875rem;
  box-shadow: 0 2px 8px rgba(227, 30, 36, 0.3);
}

/* Divider */
.divider {
  height: 1px;
  background: var(--border-color);
}

/* Utilities */
.tracking-wider {
  letter-spacing: 0.05em;
}

.hover-underline {
  text-decoration: none;
  transition: all var(--transition-fast);
}

.hover-underline:hover {
  text-decoration: underline !important;
}

/* Responsive */
@media (max-width: 576px) {
  .seat {
    width: 24px !important;
    height: 24px !important;
  }
  .seat.couple {
    width: 52px !important;
  }
}
</style>
