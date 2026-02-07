<script setup>
import { ref, computed } from 'vue';
import { ShoppingCart, VideoPlay, Menu, User, Money, Check, ArrowRight, Delete } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { mockMovies } from '@/mock/movies';

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

const handleCheckout = () => {
  ElMessage.success('Order completed successfully! Printing tickets...');
  // Reset
  selectedSeats.value = [];
  selectedCombos.value = [];
};
</script>

<template>
  <div class="staff-sales container-fluid p-4 h-100 overflow-hidden d-flex flex-column">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="fs-4 fw-bold mb-0">Sales Counter</h2>
      <div class="text-secondary small">Operator: <strong>Staff_01</strong></div>
    </div>

    <div class="row g-3 flex-grow-1 overflow-hidden">
      <!-- Left: Selection Panel -->
      <div class="col-lg-8 overflow-auto h-100 d-flex flex-column gap-3">
        
        <!-- Section 1: Movie & Showtime -->
        <el-card shadow="never" class="border-0 shadow-sm rounded-3">
          <div class="row g-3">
            <div class="col-md-7">
              <label class="small fw-bold text-secondary text-uppercase mb-2 d-block">1. Select Movie</label>
              <el-select v-model="selectedMovie" value-key="id" class="w-100" size="large">
                <el-option v-for="m in mockMovies" :key="m.id" :label="m.title" :value="m" />
              </el-select>
            </div>
            <div class="col-md-5">
              <label class="small fw-bold text-secondary text-uppercase mb-2 d-block">2. Showtime</label>
              <el-select v-model="selectedShowtime" value-key="id" class="w-100" size="large">
                <el-option v-for="s in showtimes" :key="s.id" :label="`${s.time} - ${s.room}`" :value="s" />
              </el-select>
            </div>
          </div>
        </el-card>

        <!-- Section 2: Seats -->
        <el-card shadow="never" class="border-0 shadow-sm rounded-3 flex-grow-1">
          <label class="small fw-bold text-secondary text-uppercase mb-3 d-block">3. Select Seats</label>
          <div class="text-center mb-4">
            <div class="bg-secondary bg-opacity-10 py-1 rounded-pill small w-50 mx-auto text-secondary fw-bold">SCREEN</div>
          </div>
          <div class="d-flex flex-wrap justify-content-center gap-2 mb-4">
            <div 
              v-for="i in 40" 
              :key="i" 
              class="rounded-1 d-flex align-items-center justify-content-center cursor-pointer border shadow-none"
              style="width: 32px; height: 32px; font-size: 0.7rem;"
              :class="i % 7 === 0 ? 'bg-secondary bg-opacity-25 border-0' : (i < 5 ? 'bg-primary text-white border-primary' : 'bg-light')"
            >
              {{ i }}
            </div>
          </div>
          <div class="d-flex justify-content-center gap-4 small text-secondary">
            <div class="d-flex align-items-center gap-1"><span class="d-inline-block bg-primary rounded-1" style="width: 12px; height: 12px;"></span> Selected</div>
            <div class="d-flex align-items-center gap-1"><span class="d-inline-block bg-light border rounded-1" style="width: 12px; height: 12px;"></span> Available</div>
            <div class="d-flex align-items-center gap-1"><span class="d-inline-block bg-secondary bg-opacity-25 rounded-1" style="width: 12px; height: 12px;"></span> Sold</div>
          </div>
        </el-card>

        <!-- Section 3: Concessions -->
        <el-card shadow="never" class="border-0 shadow-sm rounded-3">
          <label class="small fw-bold text-secondary text-uppercase mb-3 d-block">4. Concessions</label>
          <div class="row g-2">
            <div v-for="c in combos" :key="c.id" class="col-md-6">
              <div class="p-2 border rounded-3 d-flex justify-content-between align-items-center hover-bg" @click="handleAddCombo(c)">
                <div>
                  <div class="fw-bold small">{{ c.name }}</div>
                  <div class="text-primary fw-bold">{{ c.price.toLocaleString() }}đ</div>
                </div>
                <el-button :icon="ShoppingCart" size="small" circle />
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Right: Order Summary & Payment -->
      <div class="col-lg-4 h-100">
        <el-card shadow="never" class="border-0 shadow-sm rounded-3 h-100 d-flex flex-column p-0">
          <div class="bg-light p-3 border-bottom">
            <h6 class="fw-bold mb-0">Order Summary</h6>
          </div>
          <div class="p-3 flex-grow-1 overflow-auto">
            <div class="mb-3">
              <div class="fw-bold">{{ selectedMovie.title }}</div>
              <div class="small text-secondary">{{ selectedShowtime.time }} | {{ selectedShowtime.room }}</div>
            </div>
            
            <div class="border-top pt-3 mb-3">
              <div class="d-flex justify-content-between small mb-2">
                <span>Tickets ({{ selectedSeats.length }})</span>
                <span class="fw-bold">{{ (selectedSeats.length * 95000).toLocaleString() }}đ</span>
              </div>
              <div v-for="c in selectedCombos" :key="c.id" class="d-flex justify-content-between small mb-2">
                <span>{{ c.name }} x{{ c.qty }}</span>
                <span class="fw-bold">{{ (c.price * c.qty).toLocaleString() }}đ</span>
              </div>
            </div>

            <div class="border-top border-dark border-2 pt-3">
              <div class="d-flex justify-content-between align-items-center">
                <span class="fw-bold">TOTAL</span>
                <span class="fs-4 fw-bold text-primary">{{ totalPrice.toLocaleString() }}đ</span>
              </div>
            </div>
          </div>

          <div class="p-3 bg-light border-top mt-auto">
            <label class="small fw-bold text-secondary text-uppercase mb-3 d-block">Payment Method</label>
            <div class="d-flex gap-2 mb-3">
              <el-button type="primary" class="flex-grow-1" size="large">CASH</el-button>
              <el-button class="flex-grow-1" size="large">CARD</el-button>
            </div>
            <el-button type="success" class="w-100 py-3 fw-bold" size="large" @click="handleCheckout">
              CONFIRM ORDER
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.hover-bg { cursor: pointer; transition: background 0.2s; }
.hover-bg:hover { background-color: #f8f9fa; }
.staff-sales { max-height: calc(100vh - 80px); }
</style>
