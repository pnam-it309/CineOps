<template>
  <div class="vat-invoice-wrapper d-none d-print-block">
    <div class="invoice-container">
      <!-- Header Section -->
      <div class="header-section border-bottom border-2 border-dark pb-3 mb-4">
        <div class="row align-items-start">
          <div class="col-3 text-center">
            <div class="logo-box border border-dark p-3 mb-2">
               <h3 class="fw-bold mb-0 text-danger" style="letter-spacing: 2px;">CINEOPS</h3>
               <div class="tiny text-dark">CINEMA SYSTEM</div>
            </div>
          </div>
          <div class="col-6 company-info">
            <h5 class="fw-bold text-danger mb-1 text-uppercase">CÔNG TY TNHH RẠP CHIẾU PHIM CINEOPS VIỆT NAM</h5>
            <div class="info-line"><span class="label">Mã số thuế (Tax code):</span> <span class="value fw-bold">0106026495</span></div>
            <div class="info-line"><span class="label">Địa chỉ (Address):</span> <span class="value">Số 1 Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</span></div>
            <div class="info-line"><span class="label">Điện thoại (Tel):</span> <span class="value">0901 80 16 18</span> <span class="label ms-4">Email:</span> <span class="value">contact@cineops.vn</span></div>
            <div class="info-line"><span class="label">Số TK (Acct No.):</span> <span class="value">21510001481852</span> <span class="label ms-3">Tại NH (At Bank):</span> <span class="value">BIDV - CN Hà Thành</span></div>
          </div>
          <div class="col-3 invoice-meta text-end small">
            <div class="meta-item"><span class="label">Mẫu số (Template No.):</span> <span class="value fw-bold">01GTKT0/001</span></div>
            <div class="meta-item"><span class="label">Ký hiệu (Serial No.):</span> <span class="value fw-bold">CV/26E</span></div>
            <div class="meta-item"><span class="label">Số (No.):</span> <span class="value fw-bold text-danger fs-5">{{ invoice?.maHoaDon?.replace(/\D/g, '') || '0000000' }}</span></div>
          </div>
        </div>
      </div>

      <!-- Title Section -->
      <div class="text-center mb-4">
        <h2 class="fw-bold text-danger mb-1 text-uppercase">HÓA ĐƠN GIÁ TRỊ GIA TĂNG</h2>
        <h4 class="text-danger mb-1">(VAT INVOICE)</h4>
        <p class="fst-italic small mb-1">(Bản thể hiện của hóa đơn điện tử)</p>
        <p class="mb-0">Ngày (Date) <span class="dotted">{{ currentDay }}</span> tháng (Month) <span class="dotted">{{ currentMonth }}</span> năm (Year) <span class="dotted">{{ currentYear }}</span></p>
      </div>

      <!-- Customer Section -->
      <div class="customer-info-section mb-4">
        <div class="dotted-line-row">
          <span class="label-box">Họ tên người mua hàng (Buyer's name):</span>
          <span class="value-box fw-bold">{{ invoice?.tenKhachHang || 'Khách lẻ / vãng lai' }}</span>
        </div>
        <div class="dotted-line-row">
          <span class="label-box">Tên đơn vị (Enterprise):</span>
          <span class="value-box">.........................................................................................................................................</span>
        </div>
        <div class="dotted-line-row">
          <span class="label-box">Địa chỉ (Address):</span>
          <span class="value-box">................................................................................................................................................</span>
        </div>
        <div class="row g-0">
          <div class="col-7">
            <div class="dotted-line-row">
              <span class="label-box">Hình thức thanh toán (Payment method):</span>
              <span class="value-box">{{ invoice?.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</span>
            </div>
          </div>
          <div class="col-5">
            <div class="dotted-line-row">
              <span class="label-box ps-2">MST (Tax code):</span>
              <span class="value-box">.......................................................</span>
            </div>
          </div>
        </div>
        <div class="dotted-line-row">
          <span class="label-box">Nhân viên thực hiện (Staff):</span>
          <span class="value-box">{{ invoice?.tenNhanVien || 'Hệ thống Online' }}</span>
        </div>
      </div>

      <!-- Table Section -->
      <table class="table table-bordered border-dark invoice-main-table mb-0">
        <thead>
          <tr class="text-center align-middle bg-light">
            <th width="50">STT<br/><span class="tiny">(No)</span></th>
            <th width="">Tên hàng hóa, dịch vụ<br/><span class="tiny">(Description)</span></th>
            <th width="80">ĐVT<br/><span class="tiny">(Unit)</span></th>
            <th width="80">Số lượng<br/><span class="tiny">(Quantity)</span></th>
            <th width="120">Đơn giá<br/><span class="tiny">(Unit Price)</span></th>
            <th width="150">Thành tiền<br/><span class="tiny">(Amount)</span></th>
          </tr>
          <tr class="text-center tiny">
            <td>1</td>
            <td>2</td>
            <td>3</td>
            <td>4</td>
            <td>5</td>
            <td>6 = 4x5</td>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in details" :key="index" style="height: 40px;">
            <td class="text-center">{{ index + 1 }}</td>
            <td class="ps-2 align-middle">
              <div class="fw-bold">{{ item.loai === 0 ? 'Vé xem phim: ' + (item.tenPhim || 'Phim') : (item.tenSanPham || 'Sản phẩm') }}</div>
              <div v-if="item.loai === 0" class="tiny fst-italic ms-3">
                • {{ item.tenPhongChieu || 'Phòng' }} - Ghế: {{ item.viTriGhe || 'N/A' }}
              </div>
              <div v-else class="tiny fst-italic ms-3">
                • {{ item.maMuc || 'Mã SP' }}
              </div>
            </td>
            <td class="text-center align-middle">{{ item.loai === 0 ? 'Vé' : 'Phần' }}</td>
            <td class="text-center align-middle">{{ item.soLuong || 1 }}</td>
            <td class="text-end pe-2 align-middle">{{ formatPrice(item.donGia) }}</td>
            <td class="text-end pe-2 fw-bold align-middle">{{ formatPrice(item.thanhTien) || formatPrice(item.donGia * (item.soLuong || 1)) }}</td>
          </tr>
          <!-- Empty rows for aesthetics - Fill more space to standard -->
          <tr v-for="i in Math.max(0, 12 - details.length)" :key="'empty-' + i">
            <td style="height: 35px">&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
        </tbody>
        <tfoot class="border-top border-2 border-dark">
          <tr>
            <td colspan="4" class="p-2 border-end border-dark align-bottom" rowspan="3">
              <div class="amount-in-words pb-1">
                Số tiền bằng chữ (In words): <strong class="fst-italic text-capitalize">{{ amountInWordsText }} đồng.</strong>
              </div>
            </td>
            <td class="p-2 border-bottom border-dark text-start small">Cộng tiền hàng (Sub total):</td>
            <td class="p-2 border-bottom border-dark text-end fw-bold">{{ formatPrice(invoice?.tongTien) }}</td>
          </tr>
          <tr>
            <td class="p-2 border-bottom border-dark text-start small">Giảm giá (Discount):</td>
            <td class="p-2 border-bottom border-dark text-end text-danger">{{ formatPrice(invoice?.soTienGiam || 0) }}</td>
          </tr>
          <tr>
            <td class="p-2 text-start fw-bold fs-6">TỔNG CỘNG:</td>
            <td class="p-2 text-end fw-bold text-danger fs-5">{{ formatPrice(invoice?.tongTienThanhToan) }}</td>
          </tr>
        </tfoot>
      </table>

      <!-- Signature Section -->
      <div class="row mt-5 text-center">
        <div class="col-6">
          <p class="fw-bold mb-1 text-uppercase">Người mua hàng (Buyer)</p>
          <p class="tiny text-muted fst-italic">(Ký, ghi rõ họ tên / Signature & fullname)</p>
          <div class="mt-5 pt-4 fw-bold text-uppercase">{{ invoice?.tenKhachHang || '' }}</div>
        </div>
        <div class="col-6">
          <p class="fw-bold mb-1 text-uppercase">Người bán hàng (Seller)</p>
          <p class="tiny text-muted fst-italic">(Ký, ghi rõ họ tên / Signature & fullname)</p>
          <div class="mt-4">
            <div class="digital-seal border-3 border-success text-success p-3 d-inline-block rounded-2" style="background: rgba(40, 167, 69, 0.05); transform: rotate(-3deg);">
                <div class="fw-bold text-uppercase fs-6">Đã ký bởi (Digitally Signed)</div>
                <div class="fw-bold fs-5">CINEOPS VIETNAM</div>
                <div class="tiny">Ngày ký: {{ currentFullDate }}</div>
                <div class="tiny text-muted">Hóa đơn điện tử có giá trị như bản gốc</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Branding -->
      <div class="footer-branding text-center mt-5 pt-5 text-muted tiny border-top border-light">
        <p>Hệ thống quản lý rạp phim chuyên nghiệp CineOps - Phiên bản 2026</p>
        <p>website: cineops.vn • Hotline: 1900 1234 • Email: support@cineops.vn</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { numberToWordsVietnamese } from '@/utils/formatters';

const props = defineProps({
  invoice: Object,
  details: Array
});

const formatPrice = (v) => {
  if (v === null || v === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN').format(v) + ' ₫';
};

// Date Handling
const dateToUse = props.invoice?.ngayTao ? new Date(props.invoice.ngayTao) : new Date();
const currentDay = dateToUse.getDate().toString().padStart(2, '0');
const currentMonth = (dateToUse.getMonth() + 1).toString().padStart(2, '0');
const currentYear = dateToUse.getFullYear();
const currentFullDate = `${currentDay}/${currentMonth}/${currentYear}`;

// Convert amount to words
const amountInWordsText = computed(() => {
  const total = props.invoice?.tongTienThanhToan || 0;
  return numberToWordsVietnamese(total);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Times+New+Roman&display=swap');

.invoice-container {
  width: 210mm;
  min-height: 297mm;
  padding: 15mm 15mm 10mm 15mm;
  background: white;
  margin: 0 auto;
  color: #000;
  font-family: 'Times New Roman', serif;
  box-sizing: border-box;
}

.company-info .info-line {
  font-size: 14px;
  margin-bottom: 2px;
}

.company-info .label {
  color: #555;
  font-style: italic;
  font-size: 13px;
}

.invoice-meta .meta-item {
  font-size: 13px;
  margin-bottom: 2px;
}

.invoice-meta .label {
  font-style: italic;
}

.dotted-line-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  margin-bottom: 8px;
  width: 100%;
}

.dotted-line-row .label-box {
  flex-shrink: 0;
  font-style: italic;
  width: 250px; /* Fixed width for labels to align dotted lines */
}

/* Specific widths for smaller columns */
.col-7 .dotted-line-row .label-box {
  width: 250px;
}
.col-5 .dotted-line-row .label-box {
  width: 120px;
}

.dotted-line-row .value-box {
  flex-grow: 1;
  border-bottom: 1px dotted #888;
  padding-bottom: 2px;
  min-height: 20px;
}

.invoice-main-table th {
  text-transform: uppercase;
  font-size: 14px;
}

.invoice-main-table td {
  padding: 8px 4px;
  vertical-align: middle;
}

.tiny {
  font-size: 11px;
}

.amount-in-words {
  font-size: 15px;
  line-height: 1.5;
}

.digital-seal {
  border: 3px solid #28a745 !important;
}

@media print {
  @page {
    size: A4;
    margin: 0;
  }
  body {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
  .vat-invoice-wrapper {
    visibility: visible !important;
    display: block !important;
    position: absolute;
    left: 0;
    top: 0;
    width: 210mm;
    z-index: 9999;
  }
}
</style>
