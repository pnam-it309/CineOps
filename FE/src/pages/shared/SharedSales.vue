<script setup>
import { ref, computed } from 'vue';
import { ShoppingCart, ArrowRight, ArrowLeft } from '@element-plus/icons-vue';
import notification from '@/utils/notifications';

// Mock Data (Local)
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
const activeStep = ref(1);
const selectedMovie = ref(null);
const selectedShowtime = ref(showtimes[0]);
const selectedSeats = ref([]);
const selectedCombos = ref([]);
const paymentMethod = ref('cash'); // 'cash', 'card', 'qr'

// Colors & Design
const BRAND_RED = '#E11D48'; // Modern Vibrant Red
const BRAND_DARK = '#0F172A';
const GLOW_ACCENT = 'rgba(225, 29, 72, 0.4)';

const steps = [
  { id: 1, name: 'Phim', icon: 'bi-film' },
  { id: 2, name: 'Ghế', icon: 'bi-chair' },
  { id: 3, name: 'Bắp Nước', icon: 'bi-cup-straw' },
  { id: 4, name: 'Thanh Toán', icon: 'bi-credit-card' }
];

const mockMovies = ref([
  { id: 1, title: 'Dune: Hành Tinh Cát 2', rating: 'T16', duration: '166 phút', poster: 'https://image.tmdb.org/t/p/w500/8b8R8l3899v9D0Xn9QDs799v9D0.jpg' },
  { id: 2, title: 'Kung Fu Panda 4', rating: 'P', duration: '94 phút', poster: 'https://image.tmdb.org/t/p/w500/kDp1vUBiRSToYvRSZ70rNvT3I0L.jpg' }
]);

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
  notification.success('Đơn hàng đã hoàn tất! Đang in vé...');
  // Reset
  selectedSeats.value = [];
  selectedCombos.value = [];
};
</script>

<template>
  <div class="pos-container-premium w-100 d-flex flex-column overflow-hidden" style="height: calc(100vh - 120px);">
    <!-- Minimalist POS Navigation Bar -->
    <nav class="pos-navbar d-flex justify-content-between align-items-center px-4 bg-white border-bottom shadow-sm">
      <div class="nav-left d-flex align-items-center gap-4">
        <div class="pos-logo d-flex align-items-center gap-2">
          <div class="logo-circle bg-danger-gradient rounded-circle shadow-sm">
            <i class="bi bi-lightning-fill text-white"></i>
          </div>
          <span class="fw-bold text-dark fs-6 tracking-tight">CineOps <span class="text-danger">POS</span></span>
        </div>
        
        <!-- Logic Stepper -->
        <div class="pos-stepper d-flex align-items-center gap-1 ms-4">
          <div v-for="(step, idx) in steps" :key="step.id" class="d-flex align-items-center">
            <div class="step-pill d-flex align-items-center gap-2 px-3 py-1 rounded-pill"
                 :class="{ 'active': activeStep === step.id, 'completed': activeStep > step.id }">
              <div class="step-num">{{ idx + 1 }}</div>
              <span class="step-lbl extra-small fw-bold text-uppercase">{{ step.name }}</span>
            </div>
            <div v-if="idx < steps.length - 1" class="step-path mx-1"></div>
          </div>
        </div>
      </div>

      <div class="nav-right d-flex align-items-center gap-4">
        <div class="staff-card d-flex align-items-center gap-2 px-3 py-1 bg-light rounded-pill border">
          <i class="bi bi-person-circle text-primary"></i>
          <span class="extra-small fw-bold text-dark">NV. NGUYỄN VĂN A</span>
        </div>
        <div class="pos-clock text-dark fw-bold small">
          <i class="bi bi-clock-fill text-danger me-1"></i> 22:15
        </div>
      </div>
    </nav>

    <!-- Unified Workspace -->
    <div class="pos-workspace d-flex flex-row flex-grow-1 overflow-hidden">
      <!-- Selection Flow -->
      <main class="pos-main-flow flex-grow-1 p-4 bg-light-subtle overflow-y-auto custom-scrollbar">
        <div class="pos-sections-stack d-flex flex-column gap-4 pb-5">
          
          <!-- STEP 1: Movie -->
          <section class="flow-card p-4 rounded-4 bg-white shadow-sm border">
            <div class="d-flex align-items-center gap-3 mb-4">
              <div class="icon-blob bg-danger-soft"><i class="bi bi-film text-danger"></i></div>
              <h3 class="fs-6 mb-0 fw-bold">Chọn Phim & Suất Chiếu</h3>
            </div>
            <div class="row g-4">
              <div class="col-md-7">
                <el-select v-model="selectedMovie" value-key="id" placeholder="Tìm kiếm phim..." class="w-100 pos-select" size="large" filterable>
                  <el-option v-for="m in mockMovies" :key="m.id" :label="m.title" :value="m">
                    <div class="d-flex align-items-center gap-3">
                       <img :src="m.poster" class="thumb-mini-poster rounded-1 shadow-sm">
                       <span class="fw-medium">{{ m.title }}</span>
                    </div>
                  </el-option>
                </el-select>
              </div>
              <div class="col-md-5">
                <el-select v-model="selectedShowtime" value-key="id" class="w-100 pos-select" size="large">
                  <el-option v-for="s in showtimes" :key="s.id" :label="`${s.time} - ${s.room}`" :value="s" />
                </el-select>
              </div>
            </div>
          </section>

          <!-- STEP 2: Seats -->
          <section class="flow-card p-4 rounded-4 bg-white shadow-sm border">
            <div class="d-flex align-items-center gap-3 mb-4">
              <div class="icon-blob bg-primary-soft"><i class="bi bi-grid-3x3-gap text-primary"></i></div>
              <h3 class="fs-6 mb-0 fw-bold">Sơ đồ chỗ ngồi</h3>
              <div class="ms-auto x-small fw-bold text-muted border px-2 py-1 rounded bg-light">PHÒNG: {{ selectedShowtime?.room }}</div>
            </div>

            <div class="pos-seat-map py-4">
              <div class="map-screen mx-auto mb-5">
                <div class="screen-surface shadow-sm"></div>
                <div class="screen-label x-small fw-bold text-muted mt-2 tracking-widest">MÀN HÌNH</div>
              </div>

              <div class="pos-seat-grid mx-auto mb-4">
                <div v-for="i in 40" :key="i"
                  class="pos-seat"
                  :class="{ 
                    'selected': i < 3, 
                    'occupied': i % 7 === 0,
                    'available': i >= 3 && i % 7 !== 0 
                  }">
                  {{ i }}
                </div>
              </div>

              <div class="pos-legend d-flex justify-content-center gap-4">
                <div class="legend-pill selected"><span></span> Chọn</div>
                <div class="legend-pill available"><span></span> Trống</div>
                <div class="legend-pill occupied"><span></span> Hết</div>
              </div>
            </div>
          </section>

          <!-- STEP 3: Snacks -->
          <section class="flow-card p-4 rounded-4 bg-white shadow-sm border">
            <div class="d-flex justify-content-between align-items-center mb-4">
              <div class="d-flex align-items-center gap-3">
                <div class="icon-blob bg-warning-soft"><i class="bi bi-egg-fried text-warning"></i></div>
                <h3 class="fs-6 mb-0 fw-bold">Dịch vụ bắp nước</h3>
              </div>
              <div style="width: 250px;">
                <el-input placeholder="Tên sản phẩm..." suffix-icon="Search" size="small" />
              </div>
            </div>
            
            <div class="row g-3">
              <div v-for="c in combos" :key="c.id" class="col-md-6">
                <div class="concession-item d-flex p-3 gap-3 border rounded-4 transition-all" @click="handleAddCombo(c)">
                  <div class="concession-img-box bg-light rounded-3 d-flex align-items-center justify-content-center">
                    <i class="bi bi-cup-straw fs-3 text-warning"></i>
                  </div>
                  <div class="flex-grow-1">
                    <h6 class="fw-bold text-dark mb-0">{{ c.name }}</h6>
                    <p class="text-secondary extra-small mb-2">{{ c.desc }}</p>
                    <div class="d-flex justify-content-between align-items-center">
                      <span class="fw-bold text-danger">{{ c.price.toLocaleString() }}đ</span>
                      <div class="add-circle-btn shadow-sm"><i class="bi bi-plus"></i></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </main>

      <!-- Full-Height Receipt Sidebar -->
      <aside class="pos-sidebar-right d-flex flex-column border-start bg-white shadow-lg overflow-hidden">
        <div class="sidebar-header p-4 bg-light border-bottom text-center">
          <div class="receipt-icon-wrap bg-white text-danger shadow-sm rounded-circle d-inline-flex p-3 mb-2">
            <i class="bi bi-ticket-detailed fs-5"></i>
          </div>
          <h5 class="fw-bold text-dark mb-0 ls-tight">THANH TOÁN ĐƠN HÀNG</h5>
          <div class="extra-small fw-bold text-muted opacity-75">ID GIAO DỊCH: #POS-{{ Math.floor(Math.random()*90000+1000) }}</div>
        </div>

        <div class="sidebar-receipt-body flex-grow-1 p-4 overflow-y-auto no-scrollbar">
          <!-- Selection Live View -->
          <div v-if="selectedMovie" class="live-selection-card d-flex gap-3 mb-4 p-2 rounded-3 bg-light border">
            <img :src="selectedMovie.poster" class="rounded-2 shadow-sm" style="width: 44px; height: 60px; object-fit: cover;">
            <div class="overflow-hidden">
                <div class="fw-bold text-dark x-small text-truncate">{{ selectedMovie.title }}</div>
                <div class="extra-small text-secondary mt-1">
                  <span>{{ selectedShowtime?.room }}</span> • <span class="fw-bold text-dark">{{ selectedShowtime?.time }}</span>
                </div>
            </div>
          </div>

          <!-- Line Items -->
          <div class="line-items-stack d-flex flex-column gap-3">
            <div class="ticket-line">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="extra-small fw-bold text-muted tracking-wider">VÉ XEM PHIM ({{ selectedSeats.length }})</span>
                    <span class="x-small fw-bold text-dark">{{ (selectedSeats.length * 95000).toLocaleString() }}đ</span>
                </div>
                <div class="d-flex flex-wrap gap-1">
                    <span v-for="s in selectedSeats" :key="s" class="seat-badge-pos shadow-sm">{{ s }}</span>
                    <span v-if="selectedSeats.length === 0" class="text-muted italic extra-small">Cần chọn ghế...</span>
                </div>
            </div>

            <div v-if="selectedCombos.length > 0" class="divider-dashed my-1"></div>

            <div v-if="selectedCombos.length > 0" class="food-lines">
               <span class="extra-small fw-bold text-muted d-block mb-2">PHỤ KIỆN / BẮP NƯỚC</span>
               <div v-for="c in selectedCombos" :key="c.id" class="d-flex justify-content-between align-items-center mb-2 x-small">
                  <div class="text-dark">{{ c.name }} <span class="text-muted">x{{ c.qty }}</span></div>
                  <div class="fw-bold">{{ (c.price * c.qty).toLocaleString() }}đ</div>
               </div>
            </div>
          </div>
        </div>

        <!-- Sticky Bottom Payment -->
        <div class="sidebar-footer p-4 border-top bg-white shadow-sm mt-auto">
          <div class="d-flex justify-content-between align-items-end mb-4">
            <div class="total-summary">
                <span class="extra-small fw-bold text-muted d-block">TỔNG CẦN THU</span>
                <h3 class="fw-bold text-danger mb-0 ls-tight fs-2">{{ totalPrice.toLocaleString() }}đ</h3>
            </div>
            <div class="extra-small text-muted mb-1 text-end">Thuế & Phí<br>Đã bao gồm</div>
          </div>

          <div class="pay-methods-tabs d-flex gap-2 mb-3">
            <button class="m-pay-btn flex-grow-1" :class="{ 'active': paymentMethod === 'cash' }" @click="paymentMethod = 'cash'">
                <i class="bi bi-wallet me-2"></i>Tiền mặt
            </button>
            <button class="m-pay-btn flex-grow-1" :class="{ 'active': paymentMethod === 'card' }" @click="paymentMethod = 'card'">
                <i class="bi bi-credit-card me-2"></i>Thẻ/QR
            </button>
          </div>

          <el-button type="danger" class="pos-final-checkout-btn w-100 py-3 rounded-3 fw-bold shadow-lg" @click="handleCheckout">
             <i class="bi bi-printer-fill me-2"></i>XUẤT VÉ NGAY
          </el-button>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.pos-container-premium {
  background-color: #f8fafc;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  color: #1e293b;
}

/* Nav Bar Fixes */
.pos-navbar {
  height: 58px;
  z-index: 1000;
}

.bg-danger-gradient {
  background: linear-gradient(135deg, #e11d48, #be123c);
}

.logo-circle {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-pill {
  color: #94a3b8;
  border: 1px solid transparent;
  transition: 0.3s;
}

.step-pill.active {
  background: #fff1f2;
  color: #e11d48;
}

.step-pill.completed {
  color: #10b981;
}

.step-num {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: currentColor;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 900;
}

.step-path {
  width: 20px;
  height: 1px;
  background: #e2e8f0;
}

/* Sidebar & Receipt */
.pos-sidebar-right {
  width: 380px;
  z-index: 50;
}

.receipt-icon-wrap {
  width: 52px;
  height: 52px;
}

.seat-badge-pos {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 800;
  color: #1e293b;
}

.divider-dashed { border-top: 1px dashed #e2e8f0; }

/* Sections */
.flow-card { border: 1px solid #e2e8f0; }

.icon-blob {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.bg-danger-soft { background: #fff1f2; }
.bg-primary-soft { background: #eff6ff; }
.bg-warning-soft { background: #fffbeb; }

/* Pos Select Styles */
.pos-select :deep(.el-input__wrapper) {
  background-color: #f1f5f9 !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  border: 1px solid #e2e8f0 !important;
}

.pos-select :deep(.el-input__inner) {
  font-weight: 600 !important;
  color: #1e293b !important;
}

/* Seat Map */
.pos-seat-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 10px;
  max-width: 480px;
}

.pos-seat {
  width: 40px;
  height: 36px;
  background: #f1f5f9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 800;
  color: #94a3b8;
  cursor: pointer;
  transition: 0.2s;
  border: 1.5px solid #e2e8f0;
}

.pos-seat.available:hover {
  background: #e2e8f0;
  color: #64748b;
}

.pos-seat.selected {
  background: #e11d48;
  border-color: #e11d48;
  color: white;
  box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
}

.pos-seat.occupied {
  background: #cbd5e1;
  border-color: #cbd5e1;
  color: #f8fafc;
  opacity: 0.5;
  cursor: not-allowed;
}

.screen-surface {
  width: 80%;
  height: 5px;
  background: #cbd5e1;
  margin: 0 auto;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.pos-legend .legend-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
}

.pos-legend .legend-pill span {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.legend-pill.selected span { background: #e11d48; }
.legend-pill.available span { background: #f1f5f9; border: 1px solid #e2e8f0; }
.legend-pill.occupied span { background: #cbd5e1; }

/* Foods */
.concession-item {
  background: white;
  cursor: pointer;
}

.concession-item:hover {
  border-color: #e11d48;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04);
}

.concession-img-box {
  width: 60px;
  height: 60px;
}

.add-circle-btn {
  width: 26px;
  height: 26px;
  background: #e11d48;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Pay Buttons */
.m-pay-btn {
  border: 1.5px solid #e2e8f0;
  background: white;
  border-radius: 10px;
  padding: 10px;
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
  transition: 0.2s;
}

.m-pay-btn.active {
  border-color: #e11d48;
  color: #e11d48;
  background: #fff1f2;
}

.pos-final-checkout-btn {
  height: 56px !important;
  font-size: 15px !important;
  background: linear-gradient(135deg, #e11d48, #be123c) !important;
  border: none !important;
}

/* Helpers */
.ls-tight { letter-spacing: -0.02em; }
.no-scrollbar::-webkit-scrollbar { display: none; }
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
.thumb-mini-poster { width: 34px; height: 48px; object-fit: cover; }

.extra-small { font-size: 10px !important; }
.x-small { font-size: 12px !important; }

@media screen and (max-height: 850px) {
  .pos-container-premium { height: calc(100vh - 100px); }
  .pos-seat { width: 36px; height: 32px; }
  .pos-sidebar-right { width: 340px; }
}
</style>
