<template>
  <div class="vat-invoice-wrapper d-none d-print-block">
    <div class="invoice-container">
      <div class="row align-items-start border-bottom border-2 border-dark pb-2">
        <div class="col-3 text-center">
          <div class="logo-placeholder fw-bold text-danger border p-2">CINEOPS</div>
        </div>
        <div class="col-6 text-start company-info">
          <h5 class="fw-bold text-danger mb-1 text-uppercase">CÔNG TY TNHH RẠP CHIẾU PHIM CINEOPS</h5>
          <p>Mã số thuế: <strong>0106026495</strong></p>
          <p>Địa chỉ: Số 1 Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</p>
          <p>Điện thoại: 0901 80 16 18 - Email: contact@cineops.vn</p>
          <p>Số tài khoản: 21510001481852 tại Ngân hàng BIDV - CN Hà Thành</p>
        </div>
        <div class="col-3 text-end invoice-series small">
          <p class="mb-0">Mẫu số: <strong>01GTKT0/001</strong></p>
          <p class="mb-0">Ký hiệu: <strong>CV/26E</strong></p>
          <p class="mb-0">Số: <span class="text-danger fw-bold fs-5">{{ invoice?.maHoaDon?.replace(/\D/g, '') || '0000000' }}</span></p>
        </div>
      </div>

      <div class="text-center my-4">
        <h2 class="fw-bold text-danger mb-0">HÓA ĐƠN GIÁ TRỊ GIA TĂNG</h2>
        <p class="fst-italic small">(Bản thể hiện của hóa đơn điện tử)</p>
        <p>Ngày {{ currentDay }} tháng {{ currentMonth }} năm {{ currentYear }}</p>
      </div>

      <div class="customer-section mb-3 small">
        <p class="mb-1">Họ tên người mua hàng: <strong>{{ invoice?.tenKhachHang || 'Khách lẻ' }}</strong></p>
        <p class="mb-1">Tên đơn vị: ..................................................................................................................................................</p>
        <p class="mb-1">Địa chỉ: ................................................................................................................................................................</p>
        <div class="row">
          <div class="col-7">Hình thức thanh toán: {{ invoice?.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</div>
          <div class="col-5">Mã số thuế: .......................................................</div>
        </div>
      </div>

      <table class="table table-bordered border-dark invoice-table small">
        <thead>
          <tr class="text-center align-middle bg-light">
            <th width="5%">STT</th>
            <th width="45%">Tên hàng hóa, dịch vụ</th>
            <th width="10%">ĐVT</th>
            <th width="10%">Số lượng</th>
            <th width="15%">Đơn giá</th>
            <th width="15%">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in details" :key="index">
            <td class="text-center">{{ index + 1 }}</td>
            <td>
              {{ item.loai === 0 ? 'Vé xem phim: ' + item.tenPhim : item.tenSanPham }}
              <div v-if="item.viTriGhe" class="small fst-italic">Ghế: {{ item.viTriGhe }} - {{ item.tenPhongChieu }}</div>
            </td>
            <td class="text-center">{{ item.loai === 0 ? 'Vé' : 'Bộ' }}</td>
            <td class="text-center">{{ item.soLuong || 1 }}</td>
            <td class="text-end">{{ formatPrice(item.donGia) }}</td>
            <td class="text-end fw-bold">{{ formatPrice(item.donGia * (item.soLuong || 1)) }}</td>
          </tr>
          <tr v-for="i in Math.max(0, 5 - details.length)" :key="'empty-' + i">
            <td v-for="j in 6" :key="j" style="height: 32px">&nbsp;</td>
          </tr>
        </tbody>
        <tfoot class="fw-bold">
          <tr>
            <td colspan="5" class="text-end">Cộng tiền hàng hóa, dịch vụ:</td>
            <td class="text-end">{{ formatPrice(invoice?.tongTien) }}</td>
          </tr>
          <tr>
            <td colspan="5" class="text-end">Tiền thuế GTGT (0%):</td>
            <td class="text-end">0 ₫</td>
          </tr>
          <tr>
            <td colspan="5" class="text-end">Tổng cộng tiền thanh toán:</td>
            <td class="text-end text-danger fs-6">{{ formatPrice(invoice?.tongTienThanhToan) }}</td>
          </tr>
        </tfoot>
      </table>

      <div class="mt-2 mb-4">
        <p>Số tiền viết bằng chữ: <strong class="text-capitalize fst-italic">{{ amountInWords }} đồng.</strong></p>
      </div>

      <div class="row text-center mt-5">
        <div class="col-6">
          <p class="fw-bold mb-5 text-uppercase">Người mua hàng</p>
          <p class="small text-muted">(Ký, ghi rõ họ tên)</p>
        </div>
        <div class="col-6 position-relative">
          <p class="fw-bold mb-4 text-uppercase">Người bán hàng</p>
          <div class="digital-seal border border-success text-success p-2 d-inline-block rounded small">
            <strong>Đã ký bởi:</strong><br/>
            CINEOPS SYSTEM<br/>
            Ngày: {{ currentFullDate }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  invoice: Object,
  details: Array
});

const formatPrice = (v) => new Intl.NumberFormat('vi-VN').format(v || 0) + ' ₫';

// Xử lý ngày tháng
const now = new Date();
const currentDay = now.getDate();
const currentMonth = now.getMonth() + 1;
const currentYear = now.getFullYear();
const currentFullDate = `${currentDay}/${currentMonth}/${currentYear}`;

// Mock đọc số (Đức có thể tích hợp thư viện đọc số sau)
const amountInWords = computed(() => "Hai trăm sáu mươi lăm nghìn");
</script>

<style scoped>
.invoice-container {
  width: 210mm;
  padding: 15mm;
  background: white;
  margin: 0 auto;
  color: #000;
  font-family: "Times New Roman", Times, serif;
}
.company-info p { margin: 0; font-size: 13px; line-height: 1.4; }
.invoice-table th, .invoice-table td { border: 1px solid #000 !important; padding: 6px; }
.digital-seal { transform: rotate(-2deg); border-width: 2px !important; }

@media print {
  @page { size: A4; margin: 0; }
  .vat-invoice-wrapper { visibility: visible !important; display: block !important; position: absolute; left: 0; top: 0; width: 100%; }
}
</style>