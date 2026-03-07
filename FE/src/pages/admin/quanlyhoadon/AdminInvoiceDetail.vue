<template>
  <div class="h-100 overflow-y-auto w-100 px-4 py-4" style="background-color: #f8fafc;">
    <!-- Header Page -->
    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
      <div>
        <h3 class="fw-bold mb-1">Chi tiết đơn hàng</h3>
        <div class="d-flex gap-2 text-secondary small align-items-center">
          <span>Mã đơn hàng: <span class="fw-bold text-primary">#{{ invoice?.maHoaDon }}</span></span>
          <span v-if="invoice?.ngayTao">|</span>
          <span v-if="invoice?.ngayTao">Ngày tạo: {{ formatDate(invoice?.ngayTao) }}</span>
        </div>
        <div class="d-flex gap-2 text-secondary small align-items-center mt-1">
          <span>Tạo bởi: <span class="fw-semibold">{{ invoice?.tenNhanVien || 'Hệ thống' }}</span></span>
        </div>
      </div>
      <div>
        <el-button @click="goBack" class="rounded-pill px-4 text-secondary shadow-sm" style="border: none; background: #fff;">
          <i class="bi bi-arrow-left me-2"></i> Quay lại danh sách
        </el-button>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="row g-4 pb-5" v-loading="loading">
      
      <!-- Left Column (Status, Customer, Shipping/Transaction, Products) -->
      <div class="col-lg-8">

        <!-- 1. Status Section -->
        <div class="card border-0 shadow-sm rounded-4 mb-4">
          <div class="card-header border-0 py-3 pb-0">
            <h6 class="mb-0 fw-bold d-flex align-items-center text-primary">
              <i class="bi bi-flag me-2"></i> Trạng thái đơn hàng
            </h6>
          </div>
          <div class="card-body py-5 text-center px-4">
            <div 
              class="status-ring mx-auto mb-3 d-flex align-items-center justify-content-center"
              :class="invoice?.trangThai === 1 ? 'border-success text-success bg-success-subtle' : 'border-danger text-danger bg-danger-subtle'"
            >
              <i class="bi fs-1" :class="invoice?.trangThai === 1 ? 'bi-check2' : 'bi-hourglass-split'"></i>
            </div>
            <h5 class="fw-bold fs-5 mb-1" :class="invoice?.trangThai === 1 ? 'text-success' : 'text-danger'">
              {{ invoice?.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
            </h5>
            <div class="text-secondary small">{{ formatDate(invoice?.ngayTao) }}</div>
          </div>
        </div>

        <!-- 2. Info Cards Row -->
        <div class="row g-4 mb-4">
          <!-- Customer Info -->
          <div class="col-md-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-header border-0 py-3 pb-0">
                <h6 class="mb-0 fw-bold d-flex align-items-center text-primary">
                  <i class="bi bi-person me-2"></i> Thông tin khách hàng
                </h6>
              </div>
              <div class="card-body">
                <div class="d-flex justify-content-between border-bottom pb-2 mb-2">
                  <span class="text-secondary small">Tên khách hàng</span>
                  <span class="fw-bold text-black small text-end">{{ invoice?.tenKhachHang || 'Khách vãng lai' }}</span>
                </div>
                <div class="d-flex justify-content-between border-bottom pb-2 mb-2">
                  <span class="text-secondary small">Số điện thoại</span>
                  <span class="fw-bold text-black small text-end">{{ invoice?.sdt || 'Chưa có thông tin' }}</span>
                </div>
                <div class="d-flex justify-content-between">
                  <span class="text-secondary small">Email</span>
                  <span class="fw-bold text-black small text-end">{{ invoice?.email || 'Chưa có thông tin' }}</span>
                </div>
              </div>
            </div>
          </div>
          <!-- Transaction Info (Replaces Delivery since these are tickets) -->
          <div class="col-md-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-header border-0 py-3 pb-0">
                <h6 class="mb-0 fw-bold d-flex align-items-center text-primary">
                  <i class="bi bi-geo-alt me-2"></i> Thông tin giao dịch
                </h6>
              </div>
              <div class="card-body">
                <div class="d-flex justify-content-between border-bottom pb-2 mb-2">
                  <span class="text-secondary small">Kênh bán</span>
                  <span class="fw-bold text-black small text-end">{{ invoice?.kemBanHang === 0 ? 'Bán tại quầy' : 'Bán Online' }}</span>
                </div>
                <div class="d-flex justify-content-between border-bottom pb-2 mb-2">
                  <span class="text-secondary small">Ghi chú</span>
                  <span class="fw-bold text-black small text-end">{{ invoice?.ghiChu || 'Không' }}</span>
                </div>
                <div class="d-flex justify-content-between">
                  <span class="text-secondary small">Nhân viên chốt</span>
                  <span class="fw-bold text-black small text-end">{{ invoice?.tenNhanVien || 'Hệ thống' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 3. Products List -->
        <div class="card border-0 shadow-sm rounded-4 mb-4">
          <div class="card-header border-0 py-3 pb-2">
            <h6 class="mb-0 fw-bold d-flex align-items-center text-primary">
              <i class="bi bi-box-seam me-2"></i> Danh sách sản phẩm ({{ invoiceDetails.length }})
            </h6>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead class="table-light">
                  <tr>
                    <th class="ps-4 fw-semibold text-secondary small py-3 border-0">STT</th>
                    <th class="fw-semibold text-secondary small py-3 border-0">Loại mục / Tên SP</th>
                    <th class="text-center fw-semibold text-secondary small py-3 border-0">SL</th>
                    <th class="text-end fw-semibold text-secondary small py-3 border-0">Đơn giá</th>
                    <th class="text-end pe-4 fw-semibold text-secondary small py-3 border-0">Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in invoiceDetails" :key="index" class="border-light">
                    <td class="ps-4 text-secondary small">{{ index + 1 }}</td>
                    <td class="py-3">
                      <div class="d-flex flex-column">
                        <span class="fw-bold mb-1 d-flex align-items-center text-black">
                          <i v-if="item.loai === 0" class="bi bi-ticket-perforated-fill text-primary me-2"></i>
                          <i v-else class="bi bi-cup-straw text-warning me-2"></i>
                          {{ item.loai === 0 ? item.tenPhim : (item.tenSanPham || 'Sản phẩm đi kèm') }}
                        </span>
                        <div v-if="item.loai === 0" class="small text-secondary fw-medium mt-1 text-black">
                           {{ item.tenPhongChieu }} • {{ item.thoiGianBatDau ? formatDateShort(item.thoiGianBatDau) : '' }} 
                           <span class="ms-1 px-2 py-0 rounded text-danger bg-danger-subtle fw-bold">Ghế: {{ item.viTriGhe }}</span>
                        </div>
                      </div>
                    </td>
                    <td class="text-center fw-semibold text-black">{{ item.soLuong || 1 }}</td>
                    <td class="text-end text-black">{{ formatCurrency(item.donGia) }}</td>
                    <td class="text-end pe-4 fw-bold text-black">{{ formatCurrency(item.donGia * (item.soLuong || 1)) }}</td>
                  </tr>
                  <tr v-if="invoiceDetails.length === 0 && !loading">
                    <td colspan="5" class="text-center py-5 text-secondary">Chưa có chi tiết nào được ghi nhận.</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

      </div>

      <!-- Right Column (Billing, Payment History) -->
      <div class="col-lg-4">

        <!-- 1. Billing Info -->
        <div class="card border-0 shadow-sm rounded-4 mb-4">
          <div class="card-header border-0 py-3 pb-0">
            <h6 class="mb-0 fw-bold d-flex align-items-center text-primary">
              <i class="bi bi-receipt me-2"></i> Tổng kết thanh toán
            </h6>
          </div>
          <div class="card-body">
            <div class="d-flex justify-content-between mb-3">
              <span class="text-secondary small">Tổng tiền hàng</span>
              <span class="fw-semibold text-dark">{{ formatCurrency(invoice?.tongTien) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span class="text-secondary small">Giảm giá (Voucher)</span>
              <span class="fw-semibold text-danger">{{ invoice?.soTienGiam > 0 ? '-' + formatCurrency(invoice?.soTienGiam) : '- 0 ₫' }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3 pb-3 border-bottom">
              <span class="text-secondary small">Phí dịch vụ ngoài</span>
              <span class="fw-semibold text-dark">+ 0 ₫</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2">
              <span class="text-danger fw-bold fs-6">TỔNG TIỀN</span>
              <span class="text-danger fw-bold fs-4">{{ formatCurrency(invoice?.tongTienThanhToan) }}</span>
            </div>
          </div>
        </div>

        <!-- 2. Payment History / Actions -->
        <div class="card border-0 shadow-sm rounded-4">
          <div class="card-header border-0 py-3 pb-0">
            <h6 class="mb-0 fw-bold d-flex align-items-center text-primary">
              <i class="bi bi-credit-card me-2"></i> Lịch sử thanh toán
            </h6>
          </div>
          <div class="card-body text-center pt-4">
            <div v-if="invoice?.trangThai === 1" class="text-secondary small mb-4 pb-3 border-bottom text-start">
              <div class="d-flex gap-3 mb-2">
                <i class="bi bi-check-circle-fill text-success fs-5"></i>
                <div>
                  <div class="text-dark fw-bold mb-1">Thanh toán hoàn tất</div>
                  <div>Qua {{ invoice?.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</div>
                  <div class="text-muted mt-1" style="font-size: 11px;">{{ formatDate(invoice?.ngayTao) }}</div>
                </div>
              </div>
            </div>
            <div v-else class="text-secondary small mb-4 pb-3 border-bottom text-start">
              <div class="d-flex gap-3 mb-2">
                 <i class="bi bi-x-circle-fill text-danger fs-5"></i>
                 <div>
                    <div class="text-dark fw-bold mb-1">Đã hủy / Thất bại</div>
                    <div class="text-muted mt-1" style="font-size: 11px;">Chưa lưu thanh toán thành công</div>
                 </div>
              </div>
            </div>

            <div class="d-flex flex-column gap-2">
              <el-button type="primary" class="rounded-3 w-100 fw-bold py-3" size="large">
                <i class="bi bi-printer me-2 fs-5"></i> In hóa đơn
              </el-button>
            </div>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const invoiceId = route.params.id;

const loading = ref(true);
const invoice = ref(null);
const invoiceDetails = ref([]);

const fetchInvoiceDetail = async () => {
  loading.value = true;
  try {
    // 1. Fetch from getInvoiceById using BE
    const resInfo = await hoaDonService.getInvoiceById(invoiceId);
    if(resInfo && resInfo.data) invoice.value = resInfo.data;

    // 2. Fetch invoice details
    const resDetails = await hoaDonService.getInvoiceDetails(invoiceId);
    invoiceDetails.value = resDetails.data || [];
  } catch (error) {
    ElMessage.error('Lỗi tải dữ liệu chi tiết đơn hàng!');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push({ name: 'AdminInvoices' });
};

const formatCurrency = (value) => {
  if (value === undefined || value === null) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '—';
  const date = new Date(dateString);
  return date.toLocaleString('vi-VN', {
    hour: '2-digit', minute: '2-digit', second: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
  });
};

const formatDateShort = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) + ' ' + 
         date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
};

onMounted(() => {
  const cached = sessionStorage.getItem('currentInvoice');
  if (cached) {
    try {
      const parsed = JSON.parse(cached);
      if (parsed.id === invoiceId) {
        invoice.value = parsed;
      }
    } catch(e) {}
  }
  fetchInvoiceDetail();
});
</script>

<style scoped>
.card {
  background-color: #ffffff !important;
  border: 1px solid #e2e8f0 !important;
}

.table-light {
  background-color: #f8fafc !important;
  color: #64748b !important;
}

.status-ring {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  border-width: 2px !important;
  border-style: solid;
}
</style>
