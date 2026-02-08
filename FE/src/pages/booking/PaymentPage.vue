<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Tickets, Wallet, CreditCard, Ticket } from '@element-plus/icons-vue';
import { useBookingStore } from '@/stores/booking';

const router = useRouter();
const bookingStore = useBookingStore();

// State
const discountCode = ref('');
const selectedPayment = ref('vnpay');
const isProcessing = ref(false);

const applyVoucher = () => {
  if (discountCode.value === 'CINE20') {
    ElMessage.success('Đã áp dụng mã giảm giá! Giảm 20%.');
  } else {
    ElMessage.error('Mã giảm giá không hợp lệ');
  }
};

const handlePayment = async () => {
  isProcessing.value = true;
  try {
    // Mock payment delay
    await new Promise(resolve => setTimeout(resolve, 2000));
    router.push({ name: 'BookingResult' });
  } finally {
    isProcessing.value = false;
  }
};

const backToFood = () => {
  router.push({ name: 'SelectFood' });
};
</script>

<template>
  <div class="payment-page container py-5">
    <h2 class="fw-bold mb-4">Kiểm tra & Thanh toán</h2>
    
    <div class="row">
      <!-- Checkout Info Column -->
      <div class="col-lg-8">
        <!-- Order Summary Detail -->
        <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
          <h5 class="fw-bold mb-4 d-flex align-items-center">
            <el-icon class="me-2 text-primary"><Tickets /></el-icon> Chi tiết đơn hàng
          </h5>
          
          <div class="row align-items-center mb-4">
            <div class="col-md-2 col-4">
              <img 
                src="https://dummyimage.com/150x225/111827/ffffff?text=Poster" 
                class="img-fluid rounded-3 shadow-sm"
              >
            </div>
            <div class="col-md-10 col-8">
              <h4 class="fw-bold mb-1">{{ bookingStore.movieTitle }}</h4>
              <p class="text-secondary mb-0">{{ bookingStore.cinemaName }}</p>
              <p class="text-secondary small">Chủ Nhật, 8 Th2, 2026 • {{ bookingStore.showtimeTime }}</p>
              <div class="d-flex gap-2 mt-2">
                <span class="badge bg-light text-dark">{{ bookingStore.seatsCount }} Ghế</span>
                <span class="badge bg-light text-dark">{{ bookingStore.selectedSeats.map(s => s.id).join(', ') }}</span>
              </div>
            </div>
          </div>

          <!-- Price Breakdown -->
          <div class="bg-light rounded-4 p-4">
            <div class="d-flex justify-content-between mb-2">
              <span class="text-secondary">Vé xem phim</span>
              <span class="fw-semibold">{{ bookingStore.seatsTotal.toLocaleString() }}đ</span>
            </div>
            <div v-if="bookingStore.selectedFood.length > 0">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-secondary">Bắp & Nước</span>
                <span class="fw-semibold">{{ bookingStore.foodTotal.toLocaleString() }}đ</span>
              </div>
              <div v-for="food in bookingStore.selectedFood" :key="food.id" class="small text-secondary px-3 mb-1">
                - {{ food.name }} x{{ food.quantity }}
              </div>
            </div>
            <div class="d-flex justify-content-between mb-2 text-success">
              <span class="">Phí dịch vụ</span>
              <span class="fw-semibold">0đ</span>
            </div>
          </div>
        </div>

        <!-- Payment Methods -->
        <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
          <h5 class="fw-bold mb-4 d-flex align-items-center">
            <el-icon class="me-2 text-primary"><Wallet /></el-icon> Phương thức thanh toán
          </h5>
          
          <div class="d-grid gap-3">
            <div 
              class="payment-option p-3 rounded-4 border-2 cursor-pointer d-flex align-items-center justify-content-between"
              :class="selectedPayment === 'vnpay' ? 'border-primary bg-primary bg-opacity-10' : 'border-light'"
              @click="selectedPayment = 'vnpay'"
            >
              <div class="d-flex align-items-center gap-3">
                <img src="https://sandbox.vnpayment.vn/paymentv2/Images/brands/logo-vnpay.svg" height="24">
                <span class="fw-bold">Ví VNPay / Ngân hàng</span>
              </div>
              <el-radio v-model="selectedPayment" label="vnpay">{{''}}</el-radio>
            </div>

            <div 
              class="payment-option p-3 rounded-4 border-2 cursor-pointer d-flex align-items-center justify-content-between"
              :class="selectedPayment === 'card' ? 'border-primary bg-primary bg-opacity-10' : 'border-light'"
              @click="selectedPayment = 'card'"
            >
              <div class="d-flex align-items-center gap-3">
                <el-icon size="24" class="text-secondary"><CreditCard /></el-icon>
                <span class="fw-bold">Thẻ tín dụng/ghi nợ quốc tế</span>
              </div>
              <el-radio v-model="selectedPayment" label="card">{{''}}</el-radio>
            </div>
          </div>
        </div>
      </div>

      <!-- Final Sidebar -->
      <div class="col-lg-4">
        <div class="card border-0 shadow-lg rounded-4 sticky-top" style="top: 100px;">
          <div class="p-4 bg-white">
            <h5 class="fw-bold mb-4">Tổng kết thanh toán</h5>
            
            <!-- Voucher -->
            <div class="mb-4">
              <label class="small text-secondary fw-bold text-uppercase mb-2 d-block">Mã giảm giá</label>
              <div class="d-flex gap-2">
                <el-input v-model="discountCode" placeholder="Nhập mã" />
                <el-button type="primary" @click="applyVoucher">Áp dụng</el-button>
              </div>
              <p class="small text-secondary mt-2 mb-0">Thử mã <strong>CINE20</strong> để được giảm 20%</p>
            </div>

            <div class="border-top mb-4 pt-4">
              <div class="d-flex justify-content-between align-items-center mb-2 text-secondary">
                <span>Tạm tính</span>
                <span>{{ bookingStore.finalTotal.toLocaleString() }}đ</span>
              </div>
              <div class="d-flex justify-content-between align-items-center mb-3 text-success">
                <span>Giảm giá</span>
                <span>-0đ</span>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span class="fs-5 fw-bold">Tổng tiền thanh toán</span>
                <span class="fs-3 fw-bold text-primary">{{ bookingStore.finalTotal.toLocaleString() }}đ</span>
              </div>
            </div>

            <button 
              class="btn btn-primary w-100 py-3 rounded-3 fw-bold shadow-sm mb-3 d-flex align-items-center justify-content-center gap-2"
              @click="handlePayment"
              :disabled="isProcessing"
            >
              <span v-if="isProcessing" class="spinner-border spinner-border-sm"></span>
              {{ isProcessing ? 'Đang xử lý...' : 'Xác nhận & Thanh toán' }}
            </button>
            <button class="btn btn-link link-secondary w-100 text-decoration-none small" @click="backToFood">
              Tôi muốn thay đổi bắp nước
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.payment-option {
  border-style: solid;
  transition: all 0.2s ease;
}
.payment-option:hover {
  background: rgba(13, 110, 253, 0.05);
}
.cursor-pointer {
  cursor: pointer;
}
</style>
