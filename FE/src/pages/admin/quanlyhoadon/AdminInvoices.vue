<template>
  <div class="movie-management w-100 h-100 bg-transparent overflow-hidden">
    <AdminTableLayout
      title="Quản lý Hóa Đơn"
      titleIcon="bi bi-receipt"
      :data="invoices"
      :loading="loading"
      :total="total"
      v-model:currentPage="listQuery.page"
      v-model:pageSize="listQuery.size"
      @reset-filter="handleReset">

      <template #filters>
        <div class="filter-item">
          <el-input
            v-model="listQuery.tuKhoa"
            placeholder="Mã đơn, Tên KH, SĐT..."
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleFilter"
            @clear="handleFilter"
            style="width: 230px" />
        </div>

        <div class="filter-item">
          <el-select v-model="listQuery.trangThai" placeholder="Trạng thái" clearable @change="handleFilter" style="width: 140px">
            <el-option label="Tất cả trạng thái" value=""/>
            <el-option label="Thành công" :value="1"/>
            <el-option label="Đã hủy" :value="0"/>
          </el-select>
        </div>

        <div class="filter-item">
          <el-date-picker
            v-model="customDateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="Từ ngày"
            end-placeholder="Đến ngày"
            format="DD/MM/YYYY"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            style="width: 250px" />
        </div>

        <div class="filter-item">
          <el-select v-model="listQuery.kyThoiGian" placeholder="Kỳ thời gian" clearable @change="handlePeriodChange" style="width: 150px">
            <el-option label="Tất cả thời gian" value=""/>
            <el-option label="Hôm nay" value="TODAY"/>
            <el-option label="Tuần này" value="THIS_WEEK"/>
            <el-option label="Tháng này" value="THIS_MONTH"/>
          </el-select>
        </div>

        <div class="filter-item d-flex align-items-center gap-1">
          <el-input-number
            v-model="listQuery.minPrice"
            :controls="false"
            placeholder="Giá từ"
            @change="handleFilter"
            style="width: 100px" />
          <span class="text-secondary">-</span>
          <el-input-number
            v-model="listQuery.maxPrice"
            :controls="false"
            placeholder="Đến"
            @change="handleFilter"
            style="width: 100px" />
        </div>

        <div class="filter-item">
          <el-select v-model="listQuery.phuongThucThanhToan" placeholder="Thanh toán" clearable @change="handleFilter" style="width: 130px">
            <el-option label="Tiền mặt" :value="0"/>
            <el-option label="Chuyển khoản" :value="1"/>
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="listQuery.sortDir" placeholder="Sắp xếp" @change="handleFilter" style="width: 160px">
            <el-option label="Mới nhất trước" value="DESC">
              <i class="bi bi-sort-numeric-down me-2"></i> Mới nhất trước
            </el-option>
            <el-option label="Cũ nhất trước" value="ASC">
              <i class="bi bi-sort-numeric-up me-2"></i> Cũ nhất trước
            </el-option>
          </el-select>
        </div>
      </template>

      <template #content>
        <div class="h-100 overflow-hidden">
          <BaseTable
            v-loading="loading"
            :data="invoices"
            :columns="tableColumns"
            :total="total"
            v-model:current-page="listQuery.page"
            v-model:page-size="listQuery.size"
            :hide-pagination="true">

            <template #cell-stt="{ index }">
              <span class="fw-bold text-secondary">{{ (listQuery.page - 1) * listQuery.size + index + 1 }}</span>
            </template>

            <template #cell-maHoaDon="{ row }">
              <span class="fw-bold text-primary">#{{ row.maHoaDon }}</span>
            </template>

            <template #cell-maKhachHang="{ row }">
              <span class="small text-secondary">{{ row.maKhachHang || 'Vãng lai' }}</span>
            </template>

            <template #cell-maNhanVien="{ row }">
              <span class="small text-secondary">{{ row.maNhanVien || '—' }}</span>
            </template>

            <template #cell-ngayTao="{ row }">
              <span style="white-space: nowrap;" class="small">{{ formatDate(row.ngayTao) }}</span>
            </template>

            <template #cell-kenhBan="{ row }">
              <el-tag size="small" :type="row.loaiHoaDon === 1 ? 'success' : 'info'" effect="plain">
                {{ row.loaiHoaDon === 1 ? 'Online' : 'Tại quầy' }}
              </el-tag>
            </template>

            <template #cell-tongTienGiamGia="{ row }">
              <span class="text-danger small fw-bold">{{ formatCurrency(row.tongTienGiamGia) }}</span>
            </template>

            <template #cell-tongTienThanhToan="{ row }">
              <strong class="text-dark">{{ formatCurrency(row.tongTienThanhToan) }}</strong>
            </template>

            <template #cell-phuongThucThanhToan="{ row }">
              <span class="small">{{ row.phuongThucThanhToan === 0 ? '💵 Tiền mặt' : '💳 Chuyển khoản' }}</span>
            </template>

            <template #cell-trangThai="{ row }">
              <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round>
                {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
              </el-tag>
            </template>

            <template #actions="{ row }">
              <div class="d-flex gap-1 justify-content-center">
                <el-tooltip content="Chi tiết hóa đơn">
                  <button class="btn-action-icon action-view" @click="viewDetails(row)">
                    <i class="bi bi-eye"></i>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </BaseTable>
        </div>
      </template>
    </AdminTableLayout>

    <!-- Modal Chi Tiết Hóa Đơn -->
    <BaseModal
      v-model="modalVisible"
      :title="'Chi tiết hóa đơn: #' + (detailInvoice?.maHoaDon || '')"
      width="1100px"
      icon="bi bi-receipt">

      <template #footer><span></span></template>

      <div class="invoice-detail-container" v-if="detailInvoice">
        <!-- Header Info Card -->
        <div class="invoice-header-card mb-4">
          <div class="row g-0">
            <div class="col-md-4 border-end">
              <div class="info-item">
                <div class="info-label">Mã hóa đơn</div>
                <div class="info-value fw-bold">#{{ detailInvoice.maHoaDon }}</div>
              </div>
            </div>
            <div class="col-md-4 border-end">
              <div class="info-item">
                <div class="info-label">Ngày tạo</div>
                <div class="info-value">{{ formatDate(detailInvoice.ngayTao) }}</div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="info-item">
                <div class="info-label">Trạng thái</div>
                <div class="info-value">
                  <el-tag :type="detailInvoice.trangThai === 1 ? 'success' : 'danger'" size="small" round>
                    {{ detailInvoice.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <!-- Left Column: Customer & Payment Info -->
          <div class="col-md-4">
            <!-- Thông tin khách hàng -->
            <div class="detail-card mb-4">
              <div class="detail-card-header">
                <i class="bi bi-person-fill me-2"></i>Thông tin khách hàng
              </div>
              <div class="detail-card-body">
                <div class="mb-3">
                  <div class="text-secondary small">Tên khách hàng</div>
                  <div class="fw-bold">{{ detailInvoice.tenKhachHang || 'Khách lẻ' }}</div>
                </div>
                <div class="mb-3">
                  <div class="text-secondary small">Mã khách hàng</div>
                  <div>{{ detailInvoice.maKhachHang || '—' }}</div>
                </div>
                <div class="mb-0">
                  <div class="text-secondary small">Số điện thoại</div>
                  <div>{{ detailInvoice.sdt || '—' }}</div>
                </div>
              </div>
            </div>

            <!-- Thông tin nhân viên -->
            <div class="detail-card mb-4">
              <div class="detail-card-header">
                <i class="bi bi-person-badge-fill me-2"></i>Thông tin nhân viên
              </div>
              <div class="detail-card-body">
                <div class="mb-3">
                  <div class="text-secondary small">Nhân viên tạo</div>
                  <div class="fw-bold">{{ detailInvoice.tenNhanVien || 'Hệ thống' }}</div>
                </div>
                <div class="mb-0">
                  <div class="text-secondary small">Mã nhân viên</div>
                  <div>{{ detailInvoice.maNhanVien || '—' }}</div>
                </div>
              </div>
            </div>

            <!-- Thông tin thanh toán -->
            <div class="detail-card">
              <div class="detail-card-header">
                <i class="bi bi-credit-card-fill me-2"></i>Thông tin thanh toán
              </div>
              <div class="detail-card-body">
                <div class="mb-3">
                  <div class="text-secondary small">Kênh bán</div>
                  <div class="fw-bold">
                    <el-tag size="small" :type="detailInvoice.loaiHoaDon === 1 ? 'success' : 'info'" effect="plain">
                      {{ detailInvoice.loaiHoaDon === 1 ? 'Online' : 'Tại quầy' }}
                    </el-tag>
                  </div>
                </div>
                <div class="mb-0">
                  <div class="text-secondary small">Phương thức thanh toán</div>
                  <div class="fw-bold">
                    {{ detailInvoice.phuongThucThanhToan === 0 ? '💵 Tiền mặt' : '💳 Chuyển khoản' }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column: Items & Summary -->
          <div class="col-md-8">
            <!-- Chi tiết sản phẩm/vé -->
            <div class="detail-card mb-4">
              <div class="detail-card-header">
                <i class="bi bi-cart-fill me-2"></i>Chi tiết hóa đơn
              </div>
              <div class="detail-card-body p-0">
                <div class="table-responsive">
                  <table class="table table-hover mb-0">
                    <thead class="bg-light">
                      <tr>
                        <th class="ps-4 py-3 small fw-bold text-secondary">STT</th>
                        <th class="py-3 small fw-bold text-secondary">Loại</th>
                        <th class="py-3 small fw-bold text-secondary">Tên mục</th>
                        <th class="py-3 small fw-bold text-secondary text-center">Số lượng</th>
                        <th class="py-3 small fw-bold text-secondary text-end">Đơn giá</th>
                        <th class="py-3 small fw-bold text-secondary text-end pe-4">Thành tiền</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(item, idx) in invoiceDetails" :key="idx">
                        <td class="ps-4">{{ idx + 1 }}</td>
                        <td>
          <el-tag :type="item.tenPhim ? 'warning' : 'info'" effect="plain">
            {{ item.tenPhim ? 'Vé xem phim' : 'Sản phẩm' }}
          </el-tag>
        </td>

        <td class="text-start">
          
          <template v-if="item.tenPhim">
            <div class="fw-bold text-dark mb-1">{{ item.tenPhim }}</div>
            <div class="text-secondary small">
              <i class="bi bi-geo-alt-fill text-danger me-1"></i> Phòng: <span class="fw-bold">{{ item.tenPhongChieu || '—' }}</span>
              <span class="mx-1">|</span>
              <i class="bi bi-usb-drive-fill text-primary me-1"></i> Ghế: <span class="fw-bold text-danger">{{ item.viTriGhe || '—' }}</span>
            </div>
          </template>

          <template v-else-if="item.tenSanPham">
            <div class="fw-bold text-dark mb-1">{{ item.tenSanPham }}</div>
            <div class="text-secondary small">
              <i class="bi bi-upc-scan me-1"></i> Mã SP: {{ item.maMuc || '—' }}
            </div>
          </template>

          <template v-else>
            <span class="text-muted">— (Không xác định)</span>
          </template>
        </td>
                        <td class="text-center">{{ item.soLuong }}</td>
                        <td class="text-end">{{ formatCurrency(item.donGia) }}</td>
                        <td class="text-end fw-bold pe-4">{{ formatCurrency(item.thanhTien) }}</td>
                      </tr>
                      <tr v-if="invoiceDetails.length === 0">
                        <td colspan="6" class="text-center py-4 text-muted">Không có chi tiết sản phẩm</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <!-- Tổng tiền -->
            <div class="summary-card">
              <div class="row g-0 align-items-center">
                <div class="col-md-7">
                  <div class="summary-items">
                    <div class="summary-item">
                      <span class="text-secondary">Tổng tiền:</span>
                      <span class="fw-bold">{{ formatCurrency(detailInvoice.tongTien) }}</span>
                    </div>
                    <div class="summary-item">
                      <span class="text-secondary">Giảm giá:</span>
                      <span class="fw-bold text-danger">-{{ formatCurrency(detailInvoice.tongTienGiamGia) }}</span>
                    </div>
                    <div class="summary-item border-0">
                      <span class="text-secondary">Khách đã trả:</span>
                      <span class="fw-bold">{{ formatCurrency(detailInvoice.trangThai === 1 ? detailInvoice.tongTienThanhToan : 0) }}</span>
                    </div>
                  </div>
                </div>
                <div class="col-md-5 text-md-end">
                  <div>
                    <div class="total-label">TỔNG CỘNG</div>
                    <div class="total-amount">{{ formatCurrency(detailInvoice.tongTienThanhToan) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="action-buttons mt-4 d-flex gap-3 justify-content-end">
              <el-button class="print-btn" @click="handlePrint">
                <i class="bi bi-printer me-2"></i>In hóa đơn
              </el-button>
              <el-button type="danger" plain @click="modalVisible = false">
                <i class="bi bi-x-lg me-2"></i>Đóng
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import { useRouter } from 'vue-router';
const router = useRouter();
// --- STATE ---
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);
const customDateRange = ref([]);

// Modal state
const modalVisible = ref(false);
const detailInvoice = ref(null);
const invoiceDetails = ref([]);

const tableColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã hóa đơn', key: 'maHoaDon', minWidth: '150px' },
  { label: 'Ngày tạo', key: 'ngayTao', width: '180px' },
  { label: 'Kênh bán', key: 'kenhBan', width: '110px' },
  { label: 'Hình thức', key: 'phuongThucThanhToan', width: '140px' },
  { label: 'Giảm giá', key: 'tongTienGiamGia', width: '110px' },
  { label: 'Thanh toán', key: 'tongTienThanhToan', width: '140px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' }
];

const listQuery = reactive({
  tuKhoa: '',
  trangThai: null,
  phuongThucThanhToan: null,
  kyThoiGian: '',
  tuNgay: null,
  denNgay: null,
  minPrice: null,
  maxPrice: null,
  sortDir: 'DESC',
  page: 1,
  size: 5
});

// --- METHODS ---
const handleDateChange = (val) => {
  if (val && val.length === 2) {
    listQuery.tuNgay = val[0];
    listQuery.denNgay = val[1];
    listQuery.kyThoiGian = '';
  } else {
    listQuery.tuNgay = null;
    listQuery.denNgay = null;
  }
  handleFilter();
};

const handlePeriodChange = () => {
  customDateRange.value = [];
  listQuery.tuNgay = null;
  listQuery.denNgay = null;
  handleFilter();
};

const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) delete params[key];
    });

    const res = await hoaDonService.getInvoices(params);
    invoices.value = res.data?.content || [];
    total.value = res.data?.totalElements || 0;
  } catch (error) {
    ElMessage.error('Không thể tải dữ liệu hóa đơn!');
  } finally {
    loading.value = false;
  }
};

const viewDetails = async (row) => {
  detailInvoice.value = row;
  modalVisible.value = true;

  try {
    const resDetails = await hoaDonService.getInvoiceDetails(row.id);
    invoiceDetails.value = resDetails.data || [];
    console.log('🔍 DEBUG invoiceDetails:', JSON.stringify(invoiceDetails.value, null, 2));
  } catch (error) {
    ElMessage.error('Không thể tải chi tiết hóa đơn!');
  }
};

const handlePrint = () => {
  // Lưu data vào localStorage
  localStorage.setItem('printInvoice', JSON.stringify(detailInvoice.value));
  localStorage.setItem('printInvoiceDetails', JSON.stringify(invoiceDetails.value));

  // 3. Lúc này biến 'router' đã tồn tại và sẵn sàng hoạt động
  const routeData = router.resolve({ name: 'InvoicePrintStandalone' });

  window.open(routeData.href, '_blank');
};

const handleFilter = () => {
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const handleReset = () => {
  Object.assign(listQuery, {
    tuKhoa: '', trangThai: null, phuongThucThanhToan: null,
    kyThoiGian: '', tuNgay: null, denNgay: null,
    minPrice: null, maxPrice: null, page: 1, sortDir: 'DESC'
  });
  customDateRange.value = [];
  fetchInvoices();
};

// --- FORMATTERS ---
const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  const day = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  return `${time} ${day}`;
};

watch(() => listQuery.page, fetchInvoices);
watch(() => listQuery.size, () => {
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
});

onMounted(fetchInvoices);
</script>

<style scoped>
/* ====== NEW PROFESSIONAL INVOICE STYLES ====== */
.invoice-detail-container {
  padding: 5px;
  color: #333;
}

/* Header Card - Góc cạnh chuyên nghiệp */
.invoice-header-card {
  background: linear-gradient(90deg, #7A1114, #D92128, #F26622, #FBB040);
  border-radius: 0;
  padding: 20px;
  color: white;
  position: relative;
  clip-path: polygon(0 0, 100% 0, 100% calc(100% - 15px), calc(100% - 15px) 100%, 0 100%);
}

.invoice-header-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, transparent 50%, rgba(255,255,255,0.05) 50%);
}

.invoice-header-card .row > div {
  padding: 10px 15px;
}

.invoice-header-card .border-end {
  border-color: rgba(255,255,255,0.15) !important;
}

.invoice-header-card .info-item {
  text-align: left;
}

.invoice-header-card .info-label {
  font-size: 11px;
  opacity: 0.7;
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 600;
}

.invoice-header-card .info-value {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

/* Detail Cards - Góc cạnh */
.detail-card {
  background: white;
  border: 2px solid #1a1a2e;
  border-radius: 0;
  overflow: hidden;
  box-shadow: 4px 4px 0 rgba(26, 26, 46, 0.1);
}

.detail-card-header {
  background: linear-gradient(90deg, #7A1114, #D92128);
  padding: 12px 16px;
  border-bottom: none;
  font-weight: 700;
  color: #fff;
  display: flex;
  align-items: center;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 1px;
}

.detail-card-header i {
  color: #e94560;
  margin-right: 8px;
}

.detail-card-body {
  padding: 16px;
}

/* Table Styles - Góc cạnh chuyên nghiệp */
.detail-card .table {
  margin-bottom: 0;
  border-collapse: separate;
  border-spacing: 0;
}

.detail-card .table thead th {
  background: #f1f3f5 !important;
  border-bottom: 2px solid #1a1a2e !important;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 14px 12px !important;
  font-weight: 700;
  color: #1a1a2e;
  white-space: nowrap;
}

.detail-card .table thead th:first-child {
  border-left: none;
}

.detail-card .table thead th:last-child {
  border-right: none;
}

.detail-card .table tbody td {
  padding: 12px !important;
  vertical-align: middle;
  border-bottom: 1px solid #dee2e6;
  font-size: 13px;
}

.detail-card .table tbody td:first-child {
  border-left: none;
}

.detail-card .table tbody td:last-child {
  border-right: none;
}

.detail-card .table tbody tr:last-child td {
  border-bottom: none;
}

.detail-card .table tbody tr:hover {
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
}

/* Table góc cạnh - Viền ngoài */
.detail-card .table {
  border: 2px solid #1a1a2e;
  border-radius: 0;
}

/* Summary Card - Góc cạnh */
.summary-card {
  background: linear-gradient(90deg, #F26622, #FBB040);
  border: 2px solid #D92128;
  border-radius: 0;
  padding: 20px;
  color: white;
  box-shadow: 4px 4px 0 rgba(217, 33, 40, 0.2);
}

.summary-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px dashed rgba(255,255,255,0.2);
}

.summary-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.summary-item .text-secondary {
  color: rgba(255,255,255,0.7) !important;
}

.total-section {
  padding: 15px;
  background: rgba(255,255,255,0.1);
  border-radius: 0;
  border: 1px solid rgba(255,255,255,0.2);
}

.total-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: rgba(255,255,255,0.7);
  margin-bottom: 8px;
  font-weight: 600;
}

.total-amount {
  font-size: 24px;
  font-weight: 800;
  color: #ffd700;
  letter-spacing: 1px;
}

/* Action Buttons */
.action-buttons .print-btn {
  background: #6366f1 !important;
  border-color: #6366f1 !important;
  color: white !important;
  font-weight: 600;
  padding: 10px 24px;
  border-radius: 8px;
  transition: all 0.2s;
}

.action-buttons .print-btn:hover {
  background: #4f46e5 !important;
  border-color: #4f46e5 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.action-buttons .el-button--danger.is-plain {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 600;
}

/* Responsive */
@media (max-width: 768px) {
  .invoice-header-card .row > div {
    border-bottom: 1px solid rgba(255,255,255,0.2);
  }

  .invoice-header-card .row > div:last-child {
    border-bottom: none;
  }

  .summary-card .row > div {
    margin-bottom: 15px;
  }

  .total-section {
    text-align: center;
  }
}

.filter-item { margin-bottom: 10px; }
.btn-action-icon {
  width: 32px; height: 32px; border-radius: 8px; border: 1px solid #eee;
  background: white; cursor: pointer; transition: all 0.2s;
}
.btn-action-icon:hover { background-color: #f8fafc; color: #409eff; border-color: #409eff; }
</style>
