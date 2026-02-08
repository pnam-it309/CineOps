<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookingStore } from '@/stores/booking';
import { mockMovies, mockShowtimes } from '@/mock/movies';
import { formatCurrency } from '@/utils/formatters';
import { SEAT_TYPES, SEAT_PRICES } from '@/utils/constants';

const route = useRoute();
const router = useRouter();
const bookingStore = useBookingStore();

// State
const showtimeId = ref(null);
const movie = ref(null);
const showtime = ref(null);

// Seat matrix generation (Mock 8x12)
const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
const cols = 12;

const generateSeats = () => {
  const seats = [];
  rows.forEach((row, rowIndex) => {
    for (let col = 1; col <= cols; col++) {
      // Mock some sold seats
      const isSold = Math.random() < 0.2; 
      // Determine seat type using constants
      let type = SEAT_TYPES.STANDARD;
      
      if (rowIndex >= 6) {
        type = SEAT_TYPES.COUPLE;
      } else if (rowIndex >= 4) {
        type = SEAT_TYPES.VIP;
      }
      
      // Use seat prices from constants
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
    return; // Should show warning
  }
  router.push({ name: 'SelectFood' });
};

onMounted(() => {
  showtimeId.value = parseInt(route.params.showtimeId);
  showtime.value = mockShowtimes.find(s => s.id === showtimeId.value);
  movie.value = mockMovies.find(m => m.id === showtime.value?.movieId);
  
  if (showtime.value) {
    bookingStore.setBasicInfo({
      movieId: movie.value.id,
      movieTitle: movie.value.title,
      cinemaId: showtime.value.cinemaId,
      cinemaName: 'CineOps Central', // Mock name
      showtimeId: showtime.value.id,
      showtimeDate: 'Feb 7, 2026', // Mock date
      showtimeTime: showtime.value.time
    });
  }
  
  allSeats.value = generateSeats();
});
</script>

<template>
  <div class="seat-selection container py-5">
    <div class="row">
      <!-- Seat Map -->
      <div class="col-lg-8 mb-4">
        <div class="card border-0 shadow-sm rounded-4 p-4 p-md-5">
          <div class="screen-indicator mb-5">
            <div class="screen-box mx-auto mb-2"></div>
            <p class="text-center text-secondary small">MÀN HÌNH</p>
          </div>

          <div class="seat-grid d-flex flex-column gap-2 mb-5">
            <div v-for="row in rows" :key="row" class="d-flex justify-content-center gap-1 gap-md-2 align-items-center">
              <div class="row-label me-2 text-secondary small fw-bold">{{ row }}</div>
              <div 
                v-for="seat in allSeats.filter(s => s.row === row)" 
                :key="seat.id"
                class="seat rounded-1"
                :class="[
                  seat.type,
                  seat.isSold ? 'sold' : (isSelected(seat.id) ? 'selected' : 'available')
                ]"
                @click="handleSeatClick(seat)"
              ></div>
              <div class="row-label ms-2 text-secondary small fw-bold">{{ row }}</div>
            </div>
          </div>

          <!-- Legend -->
          <div class="d-flex justify-content-center gap-4 flex-wrap border-top pt-4">
            <div class="d-flex align-items-center gap-2">
              <div class="seat available sm"></div>
              <span class="small text-secondary">Ghế trống</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat selected sm"></div>
              <span class="small text-secondary">Đang chọn</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat sold sm"></div>
              <span class="small text-secondary">Đã bán</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat vip available sm"></div>
              <span class="small text-secondary">VIP (+20k)</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <div class="seat couple available sm"></div>
              <span class="small text-secondary">Ghế đôi</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Reservation Summary -->
      <div class="col-lg-4">
        <div class="card border-0 shadow-lg rounded-4 overflow-hidden sticky-top" style="top: 100px;">
          <div class="bg-dark p-4 text-white">
            <h4 class="fw-bold mb-1">{{ movie?.title }}</h4>
            <p class="opacity-75 small mb-0">{{ bookingStore.cinemaName }} • {{ bookingStore.showtimeTime }}</p>
          </div>
          
          <div class="p-4 bg-white">
            <div class="mb-4">
              <label class="small text-secondary fw-bold text-uppercase mb-2 d-block">Ghế đang chọn</label>
              <div v-if="bookingStore.selectedSeats.length > 0" class="d-flex flex-wrap gap-2">
                <span 
                  v-for="seat in bookingStore.selectedSeats" 
                  :key="seat.id"
                  class="badge bg-primary rounded-pill px-3 py-2"
                >
                  {{ seat.id }}
                </span>
              </div>
              <p v-else class="text-secondary small italic mb-0">Chưa có ghế nào được chọn.</p>
            </div>

            <div class="border-top mb-4"></div>

            <div class="d-flex justify-content-between align-items-center mb-4">
              <span class="fw-bold">Tổng cộng</span>
              <span class="fs-4 fw-bold text-primary">{{ bookingStore.seatsTotal.toLocaleString() }}đ</span>
            </div>

            <button 
              class="btn btn-primary w-100 py-3 rounded-3 fw-bold shadow-sm"
              :disabled="bookingStore.selectedSeats.length === 0"
              @click="goToNextStep"
            >
              Tiếp tục chọn bắp nước
            </button>
            <p class="text-center mt-3 mb-0">
              <router-link :to="{ name: 'SelectShowtime', params: { movieId: movie?.id }}" class="text-secondary small">
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
.screen-box {
  width: 80%;
  height: 6px;
  background: #e0e0e0;
  border-radius: 50%;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.seat {
  width: 25px;
  height: 25px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.seat.sm {
  width: 15px;
  height: 15px;
  cursor: default;
}

.seat.available {
  background: #f1f3f5;
  border: 1px solid #dee2e6;
}

.seat.available:hover {
  background: #e9ecef;
  transform: scale(1.1);
}

.seat.selected {
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(13, 110, 253, 0.4);
}

.seat.sold {
  background: #dee2e6;
  border: none;
  cursor: not-allowed;
  opacity: 0.5;
}

.seat.vip {
  border-color: #ffc107;
}

.seat.couple {
  width: 55px;
  border-color: #f06292;
}

.row-label {
  width: 20px;
}

@media (max-width: 576px) {
  .seat {
    width: 20px;
    height: 20px;
  }
  .seat.couple {
    width: 45px;
  }
}
</style>
