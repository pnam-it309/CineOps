<script setup>
import { ref, computed } from 'vue';
import { ShoppingCart, ArrowRight, ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { mockMovies } from '@/assets/mock';

// Mock Data
const showtimes = [
  { id: 1, time: '14:30', room: 'Hall 1' },
  { id: 2, time: '17:00', room: 'Hall 2' },
  { id: 3, time: '19:45', room: 'Hall 1' }
];

const combos = [
  { id: 1, name: 'Solo Combo', price: 95000, desc: '1 Popcorn L + 1 Coke XL' },
  { id: 2, name: 'Couple Combo', price: 175000, desc: '1 Popcorn L + 2 Coke L' }
];

// State
const selectedMovie = ref(mockMovies[0]);
const selectedShowtime = ref(showtimes[0]);
const selectedSeats = ref(['H1', 'H2']);
const selectedCombos = ref([]);

// Step Logic (Single Screen)
const totalPrice = computed(() => {
  const ticketPrice = selectedSeats.value.length * 95000;
  const foodPrice = selectedCombos.value.reduce((acc, c) => acc + (c.price * c.qty), 0);
  return ticketPrice + foodPrice;
});

const handleAddCombo = (combo) => {
  const existing = selectedCombos.value.find(c => c.id === combo.id);
  if (existing) {
    existing.qty++;
  } else {
    selectedCombos.value.push({ ...combo, qty: 1 });
  }
};

// Pagination for Concessions
const concessionPageSize = 4;
const concessionPage = ref(1);

const totalConcessionPages = computed(() => Math.ceil(combos.length / concessionPageSize));

const paginatedCombos = computed(() => {
  const start = (concessionPage.value - 1) * concessionPageSize;
  const end = start + concessionPageSize;
  const currentItems = combos.slice(start, end);

  // Fill with empty slots to always have 4
  const slotsNeeded = concessionPageSize - currentItems.length;
  const slots = [...currentItems];
  for (let i = 0; i < slotsNeeded; i++) {
    slots.push({ id: `empty-${i}`, isPlaceholder: true });
  }
  return slots;
});

const handleConcessionPageChange = (delta) => {
  const newPage = concessionPage.value + delta;
  if (newPage >= 1 && newPage <= totalConcessionPages.value) {
    concessionPage.value = newPage;
  }
};

const handleCheckout = () => {
  ElMessage.success('Đơn hàng đã hoàn tất! Đang in vé...');
  // Reset
  selectedSeats.value = [];
  selectedCombos.value = [];
};
</script>

<template>
  <div class="shared-sales w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <!-- Header: Gọn hơn -->
    <div class="d-flex justify-content-between align-items-center mb-1 flex-shrink-0">
      <h2 class="fs-6 fw-bold mb-0">Quầy bán vé</h2>
      <div class="text-secondary small" style="font-size: 0.7rem;">Giao diện POS tích hợp</div>
    </div>

    <!-- Main Content Grid -->
    <div class="row g-2 flex-grow-1 overflow-hidden no-scroll">
      <!-- Left: Selection Panel -->
      <div class="col-lg-8 h-100 d-flex flex-column gap-2 overflow-hidden">

        <!-- Section 1: Movie & Showtime (Fix Height) -->
        <div class="filter-card p-3 mb-2 flex-shrink-0">
          <div class="row g-2">
            <div class="col-7">
              <label class="x-small fw-bold text-secondary text-uppercase mb-1 d-block scale-text" style="font-size: 11px;">1. Chọn phim</label>
              <el-select v-model="selectedMovie" value-key="id" class="w-100" size="default">
                <el-option v-for="m in mockMovies" :key="m.id" :label="m.title" :value="m" />
              </el-select>
            </div>
            <div class="col-5">
              <label class="x-small fw-bold text-secondary text-uppercase mb-1 d-block scale-text" style="font-size: 11px;">2. Suất chiếu</label>
              <el-select v-model="selectedShowtime" value-key="id" class="w-100" size="default">
                <el-option v-for="s in showtimes" :key="s.id" :label="`${s.time} - ${s.room}`" :value="s" />
              </el-select>
            </div>
          </div>
        </div>

        <!-- Section 2: Seats (UI label 3) -->
        <el-card shadow="never"
          class="border-0 shadow-sm rounded-4 d-flex flex-column overflow-hidden compact-card flex-shrink-0">
          <label class="x-small fw-bold text-secondary text-uppercase mb-1 d-block scale-text">3. Chọn chỗ ngồi</label>
          <div class="text-center mb-1">
            <div
              class="bg-secondary bg-opacity-10 py-1 rounded-pill x-small w-50 mx-auto text-secondary fw-bold scale-text">
              MÀN HÌNH</div>
          </div>
          <div class="d-flex align-items-center justify-content-center overflow-hidden py-1">
            <div class="d-flex flex-wrap justify-content-center gap-1" style="max-width: 400px;">
              <div v-for="i in 40" :key="i"
                class="rounded-1 d-flex align-items-center justify-content-center cursor-pointer border seat-box"
                :class="i % 7 === 0 ? 'bg-secondary bg-opacity-25 border-0 text-white text-opacity-0' : (i < 5 ? 'bg-primary text-white border-primary' : 'bg-light')">
                {{ i }}
              </div>
            </div>
          </div>
          <div class="d-flex justify-content-center gap-3 x-small text-secondary mt-1 scale-text">
            <div class="d-flex align-items-center gap-1"><span class="d-inline-block bg-primary rounded-1"
                style="width: 8px; height: 8px;"></span> Đang chọn</div>
            <div class="d-flex align-items-center gap-1"><span class="d-inline-block bg-light border rounded-1"
                style="width: 8px; height: 8px;"></span> Trống</div>
            <div class="d-flex align-items-center gap-1"><span
                class="d-inline-block bg-secondary bg-opacity-25 rounded-1" style="width: 8px; height: 8px;"></span>
              Đã bán</div>
          </div>
        </el-card>

        <!-- Section 3: Concessions (UI label 4) -->
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 compact-card flex-shrink-0">
          <div class="d-flex justify-content-between align-items-center mb-1">
            <label class="x-small fw-bold text-secondary text-uppercase scale-text mb-0">4. Bắp nước & Combo</label>
            <div v-if="totalConcessionPages > 1" class="d-flex gap-1">
              <el-button :icon="ArrowLeft" size="small" circle @click="handleConcessionPageChange(-1)"
                :disabled="concessionPage === 1" />
              <el-button :icon="ArrowRight" size="small" circle @click="handleConcessionPageChange(1)"
                :disabled="concessionPage === totalConcessionPages" />
            </div>
          </div>
          <div class="row g-2">
            <div v-for="c in paginatedCombos" :key="c.id" class="col-6">
              <!-- Actual Item -->
              <div v-if="!c.isPlaceholder"
                class="p-2 border rounded-3 d-flex justify-content-between align-items-center hover-bg concession-item"
                @click="handleAddCombo(c)">
                <div class="overflow-hidden">
                  <div class="fw-bold small text-truncate">{{ c.name }}</div>
                  <div class="text-secondary x-small text-truncate mb-1">{{ c.desc }}</div>
                  <div class="text-primary fw-bold">{{ c.price.toLocaleString() }}đ</div>
                </div>
                <el-button :icon="ShoppingCart" size="default" circle />
              </div>
              <!-- Placeholder Item -->
              <div v-else
                class="p-2 border rounded-3 border-dashed d-flex align-items-center justify-content-center text-secondary x-small opacity-25 concession-item">
                Khuyến mãi sắp có
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Right: Order Summary & Payment -->
      <div class="col-lg-4 h-100 overflow-hidden d-flex flex-column">
        <el-card shadow="never"
          class="border-0 shadow-sm rounded-4 d-flex flex-column p-0 overflow-hidden compact-card mb-auto">
          <div class="bg-light p-2 px-3 border-bottom flex-shrink-0">
            <h6 class="fw-bold mb-0 x-small scale-text">Chi tiết đơn hàng</h6>
          </div>

          <div class="p-2 d-flex flex-column overflow-hidden">
            <div class="mb-1">
              <div class="fw-bold x-small text-truncate">{{ selectedMovie.title }}</div>
              <div class="x-small text-secondary" style="font-size: 0.65rem;">{{ selectedShowtime.time }} | {{
                selectedShowtime.room }}</div>
            </div>

            <div class="border-top pt-1 mb-1  overflow-hidden">
              <div class="d-flex justify-content-between x-small mb-1 scale-text">
                <span>Vé xem phim ({{ selectedSeats.length }})</span>
                <span class="fw-bold">{{ (selectedSeats.length * 95000).toLocaleString() }}đ</span>
              </div>
              <div class="item-list-container no-scrollbar" style="min-height: 230px;">
                <div v-for="c in selectedCombos" :key="c.id"
                  class="d-flex justify-content-between x-small mb-1 scale-text">
                  <span class="text-truncate" style="max-width: 120px;">{{ c.name }} x{{ c.qty }}</span>
                  <span class="fw-bold">{{ (c.price * c.qty).toLocaleString() }}đ</span>
                </div>
              </div>
            </div>

            <div class="border-top border-dark border-1 pt-2 flex-shrink-0 mt-2">
              <div class="d-flex justify-content-between align-items-center">
                <span class="fw-bold x-small scale-text">TỔNG CỘNG</span>
                <span class="fs-6 fw-bold text-primary">{{ totalPrice.toLocaleString() }}đ</span>
              </div>
            </div>
          </div>

          <div class="p-2 px-3 bg-light border-top flex-shrink-0">
            <label
              class="x-small fw-bold text-secondary text-uppercase mb-1 d-block text-center scale-text">Thanh toán</label>
            <div class="d-flex gap-2 mb-1">
              <el-button type="primary" class="flex-grow-1" size="small" plain>TIỀN MẶT</el-button>
              <el-button class="flex-grow-1" size="small" plain>THẺ / CHUYỂN KHOẢN</el-button>
            </div>
            <el-button type="success" class="btn-premium-primary w-100 py-2 fw-bold" @click="handleCheckout">
              XÁC NHẬN THANH TOÁN
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.hover-bg {
  cursor: pointer;
  transition: background 0.2s;
}

.hover-bg:hover {
  background-color: #f8f9fa;
}

.shared-sales {
  height: 100%;
  max-height: calc(100vh - 84px);
  position: relative;
  overflow: hidden !important;
}

:deep(.el-card) {
  display: flex !important;
  flex-direction: column !important;
  overflow: hidden !important;
  border-radius: 12px !important;
  border: 1px solid #e2e8f0 !important;
}

.concession-item {
  height: 75px;
}

:deep(.el-card__body) {
  flex: 1 !important;
  padding: 12px 15px !important;
  overflow: hidden !important;
  display: flex !important;
  flex-direction: column !important;
}

.seat-box {
  width: 40px;
  height: 40px;
  font-size: 0.75rem;
}

.x-small {
  font-size: 0.7rem;
}

.no-scroll {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
}

.no-scroll::-webkit-scrollbar,
.shared-sales::-webkit-scrollbar,
.el-card__body::-webkit-scrollbar,
.item-list-container::-webkit-scrollbar {
  display: none !important;
}

.flex-shrink-0 {
  flex-shrink: 0 !important;
}

.scale-text {
  transform-origin: left top;
  white-space: nowrap;
}

.item-list-container {
  max-height: 60px;
  overflow: hidden;
}

@media screen and (max-height: 700px) {
  .seat-box {
    width: 26px;
    height: 26px;
  }

  .fs-6 {
    font-size: 0.9rem !important;
  }
}
</style>
