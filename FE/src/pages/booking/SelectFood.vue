<script setup>
import { useRouter } from 'vue-router';
import { Minus, Plus, ShoppingCart } from '@element-plus/icons-vue';
import { useBookingStore } from '@/stores/booking';
import { mockFood } from '@/mock/movies';
import { formatCurrency } from '@/utils/formatters';
import { calculateTotal } from '@/utils/helpers';

const router = useRouter();
const bookingStore = useBookingStore();

const getQuantity = (id) => {
  const item = bookingStore.selectedFood.find(f => f.id === id);
  return item ? item.quantity : 0;
};

const goToNextStep = () => {
  router.push({ name: 'Payment' });
};

const goToPrevStep = () => {
  router.push({ name: 'SelectSeats', params: { showtimeId: bookingStore.showtimeId } });
};
</script>

<template>
  <div class="food-selection container py-5">
    <div class="row">
      <!-- Food List -->
      <div class="col-lg-8">
        <h2 class="fw-bold mb-4">Bắp & Nước</h2>
        
        <div class="row g-4">
          <div v-for="item in mockFood" :key="item.id" class="col-md-6">
            <el-card class="border-0 shadow-sm rounded-4 h-100 overflow-hidden" :body-style="{ padding: '0px' }">
              <div class="d-flex h-100">
                <img :src="item.image" class="food-img" style="width: 150px; object-fit: cover;">
                <div class="p-3 flex-grow-1 d-flex flex-column">
                  <h5 class="fw-bold mb-1">{{ item.name }}</h5>
                  <p class="text-secondary small mb-3">{{ item.description }}</p>
                  
                  <div class="mt-auto d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-primary">{{ formatCurrency(item.price) }}</span>
                    
                    <div class="d-flex align-items-center gap-2">
                      <el-button 
                        circle 
                        size="small" 
                        :icon="Minus" 
                        :disabled="getQuantity(item.id) === 0"
                        @click="bookingStore.removeFood(item.id)"
                      />
                      <span class="fw-bold px-2">{{ getQuantity(item.id) }}</span>
                      <el-button 
                        circle 
                        size="small" 
                        type="primary" 
                        :icon="Plus" 
                        @click="bookingStore.addFood(item)"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- Note about policy -->
        <div class="alert alert-info border-0 rounded-4 mt-5 d-flex align-items-center gap-3">
          <el-icon size="24"><InfoFilled /></el-icon>
          <span class="small">Bắp và nước chỉ áp dụng cho rạp và suất chiếu đã chọn. Vui lòng nhận combo tại quầy sau khi thanh toán thành công.</span>
        </div>
      </div>

      <!-- Order Summary -->
      <div class="col-lg-4 mt-4 mt-lg-0">
        <div class="card border-0 shadow-lg rounded-4 sticky-top" style="top: 100px;">
          <div class="p-4 border-bottom bg-light rounded-top-4">
            <h5 class="fw-bold mb-0">Tóm tắt đơn hàng</h5>
          </div>
          
          <div class="p-4 bg-white">
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-secondary">Ghế ({{ bookingStore.seatsCount }})</span>
                <span class="fw-semibold">{{ formatCurrency(bookingStore.seatsTotal) }}</span>
              </div>
              <div v-for="seat in bookingStore.selectedSeats" :key="seat.id" class="small text-secondary ps-2 mb-1">
                Ghế {{ seat.id }} ({{ seat.type.toUpperCase() }})
              </div>
            </div>

            <div v-if="bookingStore.selectedFood.length > 0" class="mb-4 border-top pt-3">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-secondary">Bắp nước</span>
                <span class="fw-semibold">{{ formatCurrency(bookingStore.foodTotal) }}</span>
              </div>
              <div v-for="food in bookingStore.selectedFood" :key="food.id" class="small text-secondary ps-2 mb-1 d-flex justify-content-between">
                <span>{{ food.name }} x{{ food.quantity }}</span>
                <span>{{ formatCurrency(food.price * food.quantity) }}</span>
              </div>
            </div>

            <div class="border-top mb-4 pt-4">
              <div class="d-flex justify-content-between align-items-center">
                <span class="fs-5 fw-bold">Tổng cộng</span>
                <span class="fs-4 fw-bold text-primary">{{ formatCurrency(bookingStore.finalTotal) }}</span>
              </div>
            </div>

            <div class="d-grid gap-2">
              <button 
                class="btn btn-primary py-3 rounded-3 fw-bold shadow-sm"
                @click="goToNextStep"
              >
                Tiến hành thanh toán
              </button>
              <button class="btn btn-outline-secondary py-2 border-0" @click="goToPrevStep">
                Quay lại chọn ghế
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// For InfoFilled icon
import { InfoFilled } from '@element-plus/icons-vue'
export default {
  components: { InfoFilled }
}
</script>

<style scoped>
.food-img {
  transition: transform 0.3s ease;
}

.el-card:hover .food-img {
  transform: scale(1.05);
}

.btn-outline-secondary:hover {
  background: transparent;
  color: var(--el-color-primary) !important;
  text-decoration: underline;
}
</style>
