<template>
  <div class="movie-ticket-wrapper d-none d-print-block">
    <div class="ticket-container">
      <!-- Cinema Branding -->
      <div class="ticket-header text-center border-bottom border-dark border-1 pb-2 mb-3">
        <h3 class="fw-bold mb-0">VÉ XEM PHIM</h3>
        <div class="small fw-bold text-uppercase">CINEOPS PREMIUM CINEMA</div>
        <div class="tiny">Số 1 Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</div>
      </div>

      <!-- Ticket Info Section -->
      <div class="ticket-info mb-3">
        <div class="d-flex justify-content-between">
          <span class="label">Mẫu số: 01/VE2/003</span>
          <span class="label">Ký hiệu: {{ ticket?.loaiVe === 0 ? 'POS' : 'WEB' }}/26E</span>
        </div>
        <div class="d-flex justify-content-between">
          <span class="label">Số vé (No): <strong class="text-danger">{{ ticket?.maVe?.replace(/\D/g, '') || '0000000' }}</strong></span>
          <span class="label">MST: 0106026495</span>
        </div>
      </div>

      <div class="divider-dashed my-2"></div>

      <!-- Movie Details Section -->
      <div class="movie-details mb-3">
        <h4 class="fw-bold mb-1 text-uppercase movie-title">{{ ticket?.tenPhim }}</h4>
        <div class="d-flex align-items-center gap-2 mb-1">
          <span class="badge border border-dark text-dark px-2 py-1">{{ ticket?.nhanDoTuoi || 'P' }}</span>
          <span class="small fw-bold">{{ ticket?.thoiLuong || '120' }} phút | 2D Phụ đề</span>
        </div>
        
        <div class="showtime-info mt-2">
          <div class="row g-0">
            <div class="col-6">
              <div class="label">Ngày (Date)</div>
              <div class="value fw-bold fs-5">{{ formatDate(ticket?.ngayChieu) }}</div>
            </div>
            <div class="col-6 text-end">
              <div class="label">Giờ (Time)</div>
              <div class="value fw-bold fs-5">{{ formatTime(ticket?.gioBatDau) }}</div>
            </div>
          </div>
        </div>

        <div class="seat-info mt-2 p-2 border border-dark rounded-2 text-center bg-light">
          <div class="row align-items-center">
            <div class="col-6 border-end border-dark">
              <div class="label">Rạp (Cinema)</div>
              <div class="value fw-bold fs-5">{{ ticket?.tenPhongChieu || 'Rạp 01' }}</div>
            </div>
            <div class="col-6">
              <div class="label">Ghế (Seat)</div>
              <div class="value fw-bold fs-4 text-danger">{{ ticket?.viTriGhe }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="divider-dashed my-2"></div>

      <!-- Pricing Section -->
      <div class="pricing-section mb-3">
        <div class="d-flex justify-content-between align-items-center">
          <span class="label">{{ ticket?.tenLoaiVe || 'Vé Thường' }} ({{ ticket?.tenLoaiKhachHang || 'Adult' }})</span>
          <span class="value fw-bold">{{ formatPrice(ticket?.giaThanhToan) }}</span>
        </div>
        <div class="tiny text-muted text-end">(Đã bao gồm 5% Thuế GTGT)</div>
        
        <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top border-dark">
          <span class="fw-bold">TỔNG CỘNG (Total)</span>
          <span class="fw-bold fs-5">{{ formatPrice(ticket?.giaThanhToan) }}</span>
        </div>
        <div class="small mt-1">Hình thức (Pay): {{ ticket?.loaiVe === 0 ? 'Tiền mặt' : 'Thanh toán Online' }}</div>
      </div>

      <!-- Barcode & Seller Section -->
      <div class="footer-section text-center mt-4">
        <div class="barcode-area mb-2 border border-dark p-2" style="background: repeating-linear-gradient(90deg, #000, #000 2px, #fff 2px, #fff 4px); height: 50px; width: 100%;"></div>
        <div class="fw-bold small">Sales No. {{ ticket?.id?.substring(0,20).toUpperCase() }}</div>
        
        <div class="mt-3 pt-3 border-top border-light">
          <div class="small">Bán bởi: <strong>{{ ticket?.nguoiTao || 'Hệ thống' }}</strong></div>
          <div class="tiny text-muted mt-1">Vui lòng kiểm tra kỹ thông tin vé. Vé không đổi trả.</div>
          <div class="tiny text-muted">Customer Service: 1900 1234</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  ticket: Object
});

const formatPrice = (v) => {
  if (v === null || v === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN').format(v) + ' ₫';
};

const formatDate = (dateString) => {
  if (!dateString) return '--/--/----';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

const formatTime = (timeString) => {
  if (!timeString) return '--:--';
  // If it's a LocalTime string like "14:30:00"
  if (typeof timeString === 'string') return timeString.substring(0, 5);
  return timeString;
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Courier+Prime:wght@400;700&display=swap');

.ticket-container {
  width: 80mm;
  padding: 10mm 5mm;
  background: white;
  margin: 0 auto;
  color: #000;
  font-family: 'Courier Prime', 'Courier', monospace;
  box-sizing: border-box;
}

.ticket-header h3 {
  font-size: 20px;
  letter-spacing: 1px;
}

.label {
  font-size: 11px;
  color: #333;
}

.value {
  font-size: 14px;
}

.movie-title {
  font-size: 18px;
  line-height: 1.2;
}

.tiny {
  font-size: 10px;
}

.divider-dashed {
  border-top: 1px dashed #000;
  width: 100%;
}

.badge {
  font-size: 10px;
  border-radius: 4px;
}

@media print {
  @page {
    size: auto;
    margin: 0;
  }
  body {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
  .movie-ticket-wrapper {
    visibility: visible !important;
    display: block !important;
    position: absolute;
    left: 0;
    top: 0;
    width: 80mm;
    z-index: 99999;
  }
}
</style>
