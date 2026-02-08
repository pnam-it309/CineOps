<script setup>
import { onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { CircleCheck, HomeFilled, Ticket } from '@element-plus/icons-vue';
import { useBookingStore } from '@/stores/booking';

const router = useRouter();
const bookingStore = useBookingStore();

const goToHome = () => {
  bookingStore.resetBooking();
  router.push('/');
};

const goToTickets = () => {
  bookingStore.resetBooking();
  router.push('/customer/tickets');
};

// Auto-reset store after some time? Let user see first
onUnmounted(() => {
  // bookingStore.resetBooking(); // Optional: depend on flow
});
</script>

<template>
  <div class="booking-result min-vh-100 d-flex align-items-center py-5 bg-light">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
          <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
            <!-- Header Status -->
            <div class="bg-success py-5 text-center text-white">
              <el-icon size="80" class="mb-3 animate-bounce"><CircleCheck /></el-icon>
              <h2 class="fw-bold mb-1">Đặt vé thành công!</h2>
              <p class="opacity-75 mb-0">Mã giao dịch #CO-{{ Math.floor(Math.random() * 900000) + 100000 }}</p>
            </div>

            <div class="p-4 p-md-5 bg-white">
              <!-- QR Section -->
              <div class="qr-container bg-light rounded-4 p-4 text-center mb-5 border-dashed">
                <p class="small text-secondary fw-bold text-uppercase mb-3">Mã QR Vé Của Bạn</p>
                <div class="qr-placeholder mx-auto p-3 bg-white rounded-3 shadow-sm" style="width: 180px; height: 180px;">
                  <img src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=CineOpsMockTicket-12345" class="img-fluid" alt="QR Code">
                </div>
                <p class="mt-3 small text-secondary mb-0">Vui lòng xuất trình mã QR này tại quầy rạp <br>để nhận vé.</p>
              </div>

              <!-- Booking Details -->
              <div class="row g-4 mb-5">
                <div class="col-6">
                  <span class="d-block small text-secondary fw-bold text-uppercase mb-1">Phim</span>
                  <span class="fw-bold fs-5">{{ bookingStore.movieTitle }}</span>
                </div>
                <div class="col-6 text-end">
                  <span class="d-block small text-secondary fw-bold text-uppercase mb-1">Rạp</span>
                  <span class="fw-bold">{{ bookingStore.cinemaName }}</span>
                </div>
                <div class="col-6">
                  <span class="d-block small text-secondary fw-bold text-uppercase mb-1">Ngày & Giờ</span>
                  <span class="fw-semibold">Chủ Nhật, 8 Th2, 2026 • {{ bookingStore.showtimeTime }}</span>
                </div>
                <div class="col-6 text-end">
                  <span class="d-block small text-secondary fw-bold text-uppercase mb-1">Ghế</span>
                  <span class="fw-bold text-primary">{{ bookingStore.selectedSeats.map(s => s.id).join(', ') }}</span>
                </div>
              </div>

              <!-- Actions -->
              <div class="d-grid gap-3">
                <button @click="goToTickets" class="btn btn-primary py-3 rounded-3 fw-bold shadow-sm d-flex align-items-center justify-content-center gap-2">
                  <el-icon><Ticket /></el-icon> Xem vé của tôi
                </button>
                <button @click="goToHome" class="btn btn-outline-secondary py-3 border-0 rounded-3 text-secondary d-flex align-items-center justify-content-center gap-2">
                  <el-icon><HomeFilled /></el-icon> Quay lại trang chủ
                </button>
              </div>
            </div>

            <!-- Footer Note -->
            <div class="bg-light p-3 text-center border-top">
              <p class="small text-secondary mb-0">Email xác nhận đã được gửi đến địa chỉ đăng ký của bạn.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.border-dashed {
  border: 2px dashed #dee2e6;
}

.animate-bounce {
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
  40% {transform: translateY(-20px);}
  60% {transform: translateY(-10px);}
}

.btn-outline-secondary:hover {
  background: #f8f9fa;
  color: var(--el-color-primary) !important;
}
</style>
