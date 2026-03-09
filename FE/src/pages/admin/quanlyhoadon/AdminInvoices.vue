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
      @reset-filter="handleReset"
    >
      <template #filters>
        <div class="filter-item">
          <el-input 
            v-model="listQuery.tuKhoa" 
            placeholder="Mã đơn, Tên KH, SĐT..." 
            :prefix-icon="Search" 
            clearable
            @keyup.enter="handleFilter" 
            @clear="handleFilter" 
            style="width: 230px"
          />
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
            style="width: 250px"
          />
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
            style="width: 100px"
          />
          <span class="text-secondary">-</span>
          <el-input-number 
            v-model="listQuery.maxPrice" 
            :controls="false" 
            placeholder="Đến" 
            @change="handleFilter"
            style="width: 100px"
          />
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
            :hide-pagination="true"
          >
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

            <template #cell-soTienGiam="{ row }">
              <span class="text-danger small fw-bold">{{ formatCurrency(row.soTienGiam) }}</span>
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

    <!-- Modal Chi Tiết Hóa Đơn (Premium Dashboard) -->
    <BaseModal v-model="modalVisible" :title="'Hồ sơ hóa đơn'" width="1100px" icon="bi bi-receipt-cutoff" isDetail onlyCancel>
      <div v-if="detailInvoice" class="p-0">
        <!-- Profile Header (Colorless) -->
        <div class="p-4 border-bottom bg-white d-flex align-items-center gap-4">
          <div class="shadow-sm border d-flex align-items-center justify-content-center bg-light text-secondary" 
               style="width: 100px; height: 100px;">
            <i class="bi bi-receipt fs-1"></i>
          </div>
          <div class="flex-grow-1">
            <div class="d-flex align-items-center gap-2 mb-1">
              <h3 class="fw-bold m-0 text-dark">Hóa đơn #{{ detailInvoice.maHoaDon }}</h3>
              <el-tag :type="detailInvoice.trangThai === 1 ? 'info' : 'plain'" effect="plain" round size="small">
                {{ detailInvoice.trangThai === 1 ? 'ĐẠ THANH TOÁN' : 'ĐÃ HỦY' }}
              </el-tag>
            </div>
            <div class="d-flex align-items-center gap-3">
              <div class="small text-secondary"><i class="bi bi-calendar3 me-1"></i>Ngày lập: {{ formatDate(detailInvoice.ngayTao) }}</div>
              <div class="small text-secondary"><i class="bi bi-wallet2 me-1"></i>{{ detailInvoice.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</div>
            </div>
          </div>
          <div class="text-end">
            <div class="text-secondary small mb-1">TỔNG THANH TOÁN</div>
            <div class="fw-bold fs-2 text-dark">{{ formatCurrency(detailInvoice.tongTienThanhToan) }}</div>
          </div>
        </div>

        <!-- Detail Layout -->
        <div class="profile-details-body p-4 bg-white">
          <div class="row g-4">
            <div class="col-lg-8">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Chi tiết giao dịch</h6>
              <div class="border overflow-hidden">
                <table class="table table-hover m-0 w-100">
                  <thead class="bg-light">
                    <tr>
                      <th class="small py-3 ps-4 border-0 text-secondary">Mô tả mặt hàng</th>
                      <th class="small py-3 text-center border-0 text-secondary">SL</th>
                      <th class="small py-3 text-end pe-4 border-0 text-secondary">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, idx) in invoiceDetails" :key="idx">
                      <td class="ps-4 py-3 border-bottom">
                         <div class="fw-bold text-dark">{{ item.loai === 0 ? item.tenPhim : item.tenSanPham }}</div>
                         <div class="text-secondary small">{{ item.loai === 0 ? 'Vé · Ghế ' + item.viTriGhe : 'Snack & Drink' }}</div>
                      </td>
                      <td class="text-center py-3 border-bottom text-dark fw-bold">{{ item.soLuong }}</td>
                      <td class="text-end pe-4 py-3 border-bottom text-dark fw-bold">{{ formatCurrency(item.thanhTien) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <!-- Billing Summary (Colorless) -->
              <div class="mt-4 p-4 border bg-light d-flex justify-content-end gap-5">
                 <div class="text-end">
                    <div class="text-secondary small mb-1">TỔNG TRƯỚC GIẢM</div>
                    <div class="fw-bold text-dark fs-5">{{ formatCurrency(invoiceDetails.reduce((acc, i) => acc + i.thanhTien, 0)) }}</div>
                 </div>
                 <div class="text-end border-start ps-5">
                    <div class="text-secondary small mb-1">KHUYẾN MÃI</div>
                    <div class="fw-bold text-dark fs-5">-{{ formatCurrency(detailInvoice.soTienGiam || 0) }}</div>
                 </div>
              </div>
            </div>

            <!-- Right Column: Customer & Staff Info (Colorless) -->
            <div class="col-lg-4">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Thông tin liên quan</h6>
              <div class="p-3 border mb-3 bg-white">
                 <div class="d-flex align-items-center gap-3 mb-3 pb-3 border-bottom">
                   <div class="p-2 border bg-light text-secondary"><i class="bi bi-person fs-4"></i></div>
                   <div>
                     <label class="text-secondary tiny-text">KHÁCH HÀNG</label>
                     <div class="fw-bold text-dark">{{ detailInvoice.tenKhachHang || 'Khách vãng lai' }}</div>
                     <div class="small text-secondary">{{ detailInvoice.sdtKhachHang || '—' }}</div>
                   </div>
                 </div>
                 <div class="d-flex align-items-center gap-3">
                   <div class="p-2 border bg-light text-secondary"><i class="bi bi-person-workspace fs-4"></i></div>
                   <div>
                     <label class="text-secondary tiny-text">NHÂN VIÊN LẬP</label>
                     <div class="fw-bold text-dark">{{ detailInvoice.nguoiTao || 'Admin System' }}</div>
                     <div class="small text-secondary">{{ detailInvoice.maNhanVien || 'SYSTEM' }}</div>
                   </div>
                 </div>
              </div>

              <!-- VAT Info (Colorless) -->
              <div class="p-3 border bg-light mt-3">
                 <h6 class="fw-bold small text-secondary mb-3 text-uppercase">Thông tin VAT</h6>
                 <div class="d-flex justify-content-between mb-2">
                   <span class="text-secondary small">Thuế suất (VAT)</span>
                   <span class="text-dark fw-bold">8.0%</span>
                 </div>
                 <div class="d-flex justify-content-between">
                   <span class="text-secondary small">Tiền thuế</span>
                   <span class="text-dark fw-bold">{{ formatCurrency((detailInvoice.tongTienThanhToan || 0) * 0.08) }}</span>
                 </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>

    <!-- Professional Print Template -->
    <VatInvoiceTemplate :invoice="detailInvoice" :details="invoiceDetails" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';
import BaseTable from '@/components/common/BaseTable.vue';
import VatInvoiceTemplate from './VatInvoiceTemplate.vue';
import BaseModal from '@/components/common/BaseModal.vue';

// --- STATE QUẢN LÝ DỮ LIỆU ---
const router = useRouter();
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);
const customDateRange = ref([]); // Biến hứng dữ liệu từ DatePicker [cite: 2026-03-04]

// Modal state
const modalVisible = ref(false);
const activeTab = ref('info');
const detailInvoice = ref(null);
const invoiceDetails = ref([]);
const paymentHistory = ref([]);

const tableColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã hóa đơn', key: 'maHoaDon' , minWidth: '150px'},
  { label: 'Ngày tạo', key: 'ngayTao', width: '180px' },
  { label: 'Kênh bán', key: 'kenhBan', width: '110px' },
  { label: 'Hình thức', key: 'phuongThucThanhToan', width: '140px' },
  { label: 'Giảm giá', key: 'soTienGiam', width: '110px' },
  { label: 'Thanh toán', key: 'tongTienThanhToan', width: '140px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' }
];

// Query đầy đủ trường khớp với Backend
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

// Xử lý khi chọn khoảng ngày trên lịch [cite: 2026-03-04]
const handleDateChange = (val) => {
  if (val && val.length === 2) {
    listQuery.tuNgay = val[0];
    listQuery.denNgay = val[1];
    listQuery.kyThoiGian = ''; // Reset kỳ cố định để dùng ngày tự chọn
  } else {
    listQuery.tuNgay = null;
    listQuery.denNgay = null;
  }
  handleFilter();
};

// Xử lý khi chọn Kỳ cố định (Hôm nay/Tuần này)
const handlePeriodChange = () => {
  customDateRange.value = []; // Xóa trắng ô lịch
  listQuery.tuNgay = null;
  listQuery.denNgay = null;
  handleFilter();
};

const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    
    // Thuật toán Clean Params: Xóa trường rỗng tránh lỗi 400 Backend
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
  activeTab.value = 'info';
  modalVisible.value = true;
  
  try {
    const [resDetails, resHistory] = await Promise.all([
      hoaDonService.getInvoiceDetails(row.id),
      hoaDonService.getPaymentHistory(row.id)
    ]);
    invoiceDetails.value = resDetails.data || [];
    paymentHistory.value = resHistory.data || [];
  } catch (error) {
    ElMessage.error('Không thể tải chi tiết hóa đơn!');
  }
};

const handlePrint = () => {
  // Đợi một chút để template render xong (nếu cần) và gọi in
  setTimeout(() => {
    window.print();
  }, 100);
};

const handleUpdateInvoice = () => {
  ElMessage.info('Tính năng cập nhật đang được phát triển');
};

const handleFilter = () => {
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const handleReset = () => {
  Object.assign(listQuery, {
    tuKhoa: '', trangThai: null, phuongThucThanhToan: null,
    kyThoiGian: '', tuNgay: null, denNgay: null,
    minPrice: null, maxPrice: null, page: 1,sortDir: 'DESC'
  });
  customDateRange.value = []; // Reset UI lịch [cite: 2026-03-04]
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
  const day  = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
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

.custom-invoice-modal-v2 { background: #fdfdfd; margin: -20px; color: #1e293b; }
.border-bottom-light { border-bottom: 1px solid #f1f5f9; }
.section-divider { display: flex; align-items: center; margin: 20px 0; color: #94a3b8; font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; }
.section-divider::after { content: ''; flex-grow: 1; height: 1px; background: #e2e8f0; margin-left: 15px; }
.tiny-text { font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
:deep(.premium-tabs .el-tabs__item) { font-weight: 700; font-size: 15px; padding: 10px 20px !important; }
:deep(.premium-tabs .el-tabs__active-bar) { height: 3px; border-radius: 3px; }
.payment-timeline { max-height: 500px; overflow-y: auto; }

.filter-item { margin-bottom: 10px; }
.btn-action-icon {
  width: 32px; height: 32px; border-radius: 8px; border: 1px solid #eee;
  background: white; cursor: pointer; transition: all 0.2s;
}
.btn-action-icon:hover { background-color: #f8fafc; color: #409eff; border-color: #409eff; }

.ticket-detail-item {
  transition: all 0.3s ease;
  background: #fff;
}
.ticket-detail-item:hover {
  border-color: var(--el-color-primary) !important;
  background: #f0f7ff;
}
.poster-mini {
  border: 1px solid #eee;
}
.snack-table-wrapper {
  background: #fff;
}
.invoice-footer-billing {
  border-top: 1px dashed #dcdfe6;
}
.section-title i {
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

/* Modal Custom Styles */
.custom-invoice-modal {
  padding: 10px;
  color: #333;
}

.tabs-container .tab-item {
  color: #666;
  position: relative;
  transition: all 0.2s;
}

.tabs-container .tab-item.active {
  color: #d63031;
}

.tabs-container .tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #d63031;
}

.info-group .label {
  font-weight: 500;
}

.detail-table-wrapper {
  max-height: 480px;
  overflow-y: auto;
  overflow-x: hidden;
}

.detail-table-wrapper table td {
  vertical-align: middle;
  word-break: break-word;
}

.detail-table-wrapper table thead th {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #f8f9fa;
}

.premium-btn {
  background-color: #d63031 !important;
  border-color: #d63031 !important;
  color: white !important;
  font-weight: bold;
  border-radius: 8px !important;
}

.print-btn {
  background-color: #ffffff !important;
  border: 1px solid #dcdfe6 !important;
  color: #374151 !important;
  font-weight: bold;
  border-radius: 8px !important;
}

.close-btn {
  font-weight: bold;
  border-radius: 8px !important;
  border-width: 2px !important;
}

.tiny {
  font-size: 0.75rem;
}

.history-item {
  transition: all 0.2s;
}

.history-item:hover {
  background-color: #fff !important;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.actions-stack .el-button {
  height: 50px !important;
  margin-left: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}

/* Print Styles: Xóa bỏ hoàn toàn khoảng trắng và các thành phần thừa */
@media print {
  /* Ẩn toàn bộ khung ứng dụng, modal và các lớp phủ overlay */
  #app > *:not(.vat-invoice-wrapper),
  .el-overlay,
  .el-overlay-dialog,
  .admin-staff-layout,
  .base-modal-overlay {
    display: none !important;
  }

  /* Đưa hóa đơn lên vị trí đầu trang tuyệt đối */
  .vat-invoice-wrapper {
    display: block !important;
    position: absolute !important;
    top: 0 !important;
    left: 0 !important;
    width: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
    background: white !important;
    z-index: 99999 !important;
  }

  /* Reset body để không bị cuộn hay cách lề */
  body, html {
    margin: 0 !important;
    padding: 0 !important;
    height: auto !important;
    overflow: visible !important;
  }
}
</style>
