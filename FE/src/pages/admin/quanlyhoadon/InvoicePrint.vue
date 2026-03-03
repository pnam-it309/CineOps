<template>
  <div id="invoice-print-frame" class="print-only">
    <div class="invoice-box">
      <h2 class="cinema-name">CINEOPS THEATER</h2>
      <p class="invoice-type">HÓA ĐƠN THANH TOÁN</p>
      <div class="divider">***************************</div>
      
      <div class="info-row">
        <span>Mã HD:</span> 
        <span class="fw-bold">{{ invoiceData.maHoaDon }}</span>
      </div>
      <div class="info-row">
        <span>Khách hàng:</span> 
        <span>{{ invoiceData.tenKhachHang || 'Khách lẻ' }}</span>
      </div>
      <div class="info-row" v-if="invoiceData.sdt">
        <span>SĐT:</span> 
        <span>{{ invoiceData.sdt }}</span>
      </div>
      <div class="info-row">
        <span>Ngày:</span> 
        <span>{{ formatDate(invoiceData.ngayTao) }}</span>
      </div>
      <div class="info-row">
        <span>Nhân viên:</span> 
        <span>{{ invoiceData.tenNhanVien || 'Hệ thống' }}</span>
      </div>
      
      <div class="divider">---------------------------</div>
      
      <table class="items-table">
        <thead>
          <tr>
            <th align="left">Sản phẩm</th>
            <th align="right">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in invoiceData.details" :key="item.id">
            <td>
              <div class="item-name">
                {{ item.loai === 0 ? '🎬' : '🍿' }} {{ item.tenPhim || 'Sản phẩm' }}
              </div>
              <div v-if="item.loai === 0" class="item-sub">
                Phòng: {{ item.tenPhongChieu }} | Ghế: {{ item.soGhe || item.viTriGhe }}
              </div>
            </td>
            <td align="right">{{ formatCurrency(item.donGia) }}</td>
          </tr>
        </tbody>
      </table>
      
      <div class="divider">---------------------------</div>
      
      <div class="total-row">
        <span>TỔNG TIỀN:</span>
        <span class="total-price">{{ formatCurrency(invoiceData.tongTienThanhToan) }}</span>
      </div>
      
      <div class="divider">***************************</div>
      <p class="footer-note">Cảm ơn và hẹn gặp lại tại CineOps!</p>
      
      </div>
  </div>
</template>

<script setup>
/**
 * Nhận dữ liệu từ AdminInvoices.vue
 */
defineProps({
  invoiceData: { 
    type: Object, 
    required: true, 
    default: () => ({ details: [] }) 
  }
});

/**
 * Các hàm định dạng (Utility functions)
 */
const formatCurrency = (v) => v ? new Intl.NumberFormat('vi-VN').format(v) + ' ₫' : '0 ₫';
const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN') : '---';
</script>

<style scoped>
/* CSS ĐẶC TRỊ CHO VIỆC IN (Print Styling) */
@media print {
  /* Cô lập vùng in tuyệt đối để không bị dính Sidebar */
  body > *:not(#invoice-print-frame) {
    display: none !important;
  }

  #invoice-print-frame {
    display: block !important;
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    background: #fff;
    margin: 0;
    padding: 0;
  }

  .invoice-box {
    width: 80mm; /* Khổ K80 chuẩn [cite: 2026-03-03] */
    margin: 0 auto;
    padding: 5mm 2mm;
    font-family: 'Courier New', Courier, monospace;
    text-align: center;
    color: #000;
  }

  .cinema-name { font-size: 16pt; font-weight: bold; margin-bottom: 2pt; }
  .invoice-type { font-size: 10pt; margin-bottom: 5pt; text-decoration: underline; }
  .info-row { display: flex; justify-content: space-between; font-size: 9pt; margin-bottom: 2pt; text-align: left; }
  .divider { border-top: 1pt dashed #000; margin: 5pt 0; }
  
  .items-table { width: 100%; font-size: 9pt; border-collapse: collapse; margin: 8pt 0; }
  .items-table th { border-bottom: 0.5pt solid #000; padding-bottom: 3pt; }
  .item-name { font-weight: bold; text-align: left; }
  .item-sub { font-size: 8pt; color: #444; text-align: left; padding-left: 10pt; }
  
  .total-row { display: flex; justify-content: space-between; font-size: 11pt; font-weight: bold; margin-top: 5pt; }
  .total-price { font-size: 13pt; border-bottom: 1pt double #000; }
  .footer-note { font-size: 8pt; margin-top: 5pt; font-style: italic; }
}

/* Ẩn vùng in trên trình duyệt bình thường */
.print-only {
  display: none;
}
</style>