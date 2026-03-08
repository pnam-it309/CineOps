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
          <el-input v-model="listQuery.tuKhoa" placeholder="Nhập mã hóa đơn..." :prefix-icon="Search" clearable
                      @keyup.enter="handleFilter" @clear="handleFilter" />
        </div>
        <div class="filter-item">
          <el-select v-model="listQuery.trangThai" placeholder="Chọn trạng thái" style="width: 200px;" clearable
                      @change="handleFilter">
              <el-option label="Tất cả trạng thái" value=""/>
              <el-option label="Thành công" :value="1"/>
              <el-option label="Đã hủy" :value="0"/>
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="listQuery.phuongThucThanhToan" placeholder="Chọn thanh toán" style="width: 200px;" clearable
                      @change="handleFilter">
              <el-option label="Tất cả hình thức" value=""/>
              <el-option label="Tiền mặt" :value="0"/>
              <el-option label="Chuyển khoản" :value="1"/>
          </el-select>
        </div>
        <div class="filter-item">
            <el-select v-model="listQuery.kyThoiGian" placeholder="Chọn kỳ thời gian" style="width: 200px;" clearable @change="handleFilter">
              <el-option label="Tất cả thời gian" value=""/>
              <el-option label="Hôm nay" value="TODAY"/>
              <el-option label="Tuần này" value="THIS_WEEK"/>
              <el-option label="Tháng này" value="THIS_MONTH"/>
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
            :page-size="listQuery.size" 
            v-model:page-size="listQuery.size"
            :hide-pagination="true"
          >
            <template #cell-stt="{ index }">
              <span class="fw-bold text-secondary">{{ (listQuery.page - 1) * listQuery.size + index + 1 }}</span>
            </template>

            <template #cell-maHoaDon="{ row }">
              <span class="fw-bold">{{ row.maHoaDon }}</span>
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

            <template #cell-giamGia="{ row }">
              <span class="text-danger small fw-bold">{{ formatCurrency(row.tongTienGiamGia) }}</span>
            </template>


            <template #cell-tongTienThanhToan="{ row }">
              <strong class="">{{ formatCurrency(row.tongTienThanhToan) }}</strong>
            </template>


            <template #cell-trangThai="{ row }">
              <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="large" round style="white-space: nowrap;">
                  {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
              </el-tag>
            </template>

            <template #actions="{ row }">
              <div class="d-flex gap-1 justify-content-center align-items-center">
                <el-tooltip content="Chi tiết hóa đơn" placement="top">
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

    <!-- Modal Chi Tiết Hóa Đơn (Theo khuôn mẫu) -->
    <BaseModal
      v-model="modalVisible"
      :title="'Chi tiết hóa đơn: #' + (detailInvoice?.maHoaDon || '')"
      width="1350px"
      icon="bi bi-receipt"
    >
      <template #footer><span></span></template>
      
      <div class="custom-invoice-modal">
        <div class="tabs-container mb-4">
          <div class="d-flex gap-4 border-bottom">
            <div 
              class="tab-item py-2 fw-bold cursor-pointer" 
              :class="{ active: activeTab === 'info' }"
              @click="activeTab = 'info'"
            >Thông tin</div>
            <div 
              class="tab-item py-2 fw-bold cursor-pointer" 
              :class="{ active: activeTab === 'history' }"
              @click="activeTab = 'history'"
            >Lịch sử thanh toán</div>
          </div>
        </div>

        <div v-if="activeTab === 'info'" class="tab-content animate__animated animate__fadeIn">
          <div class="row g-0 align-items-start">
            <!-- Cột trái: Thông tin -->
            <div class="col-md-2 border-end pe-3 pt-1">
              <div class="info-group mb-3">
                <div class="label text-secondary small mb-1">Mã hóa đơn:</div>
                <div class="value fw-bold">{{ detailInvoice?.maHoaDon }}</div>
              </div>
              <div class="info-group mb-4">
                <div class="label text-secondary small mb-1">Thời gian:</div>
                <div class="value fw-bold">{{ formatDate(detailInvoice?.ngayTao) }}</div>
              </div>
              <div class="info-group mb-4">
                <div class="label text-secondary small mb-1">Khách hàng:</div>
                <div class="value fw-bold">{{ detailInvoice?.tenKhachHang || 'Khách lẻ' }}</div>
              </div>
              <div class="info-group mb-4">
                <div class="label text-secondary small mb-1">Nhân viên:</div>
                <div class="value fw-bold">{{ detailInvoice?.tenNhanVien || 'Hệ thống' }}</div>
              </div>
              <div class="info-group mb-4">
                <div class="label text-secondary small mb-1">Kênh bán:</div>
                <div class="value fw-bold">{{ detailInvoice?.loaiHoaDon === 1 ? 'Online' : 'Tại quầy' }}</div>
              </div>
            </div>

            <!-- Cột giữa: Bảng chi tiết -->
            <div class="col-md-7 px-3 pt-1">
              <div class="detail-table-wrapper border rounded-2">
                <table class="table table-hover mb-0" style="table-layout: fixed; width: 100%;">
                  <thead class="bg-light">
                    <tr>
                      <th style="width: 50px;" class="small fw-bold py-3 text-secondary border-0 ps-3">STT</th>
                      <th style="width: 100px;" class="small fw-bold py-3 text-secondary border-0">Mã mục</th>
                      <th style="min-width: 180px;" class="small fw-bold py-3 text-secondary border-0">Tên mục / SP</th>
                      <th style="width: 60px;" class="small fw-bold py-3 text-secondary border-0 text-center">SL</th>
                      <th style="width: 110px;" class="small fw-bold py-3 text-secondary border-0 text-end">Đơn giá</th>
                      <th style="width: 100px;" class="small fw-bold py-3 text-secondary border-0 text-end">Giảm giá</th>
                      <th style="width: 110px;" class="small fw-bold py-3 text-secondary border-0 text-end">Giá bán</th>
                      <th style="width: 120px;" class="small fw-bold py-3 text-secondary border-0 text-end">Thành tiền</th>
                      <th style="width: 80px;" class="small fw-bold py-3 text-secondary border-0 text-center">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, idx) in invoiceDetails" :key="idx">
                      <td class="small ps-3">{{ idx + 1 }}</td>
                      <td class="small text-secondary">{{ item.maMuc || '—' }}</td>
                      <td class="small fw-bold">
                        {{ item.loai === 0 ? item.tenPhim : item.tenSanPham }}
                        <div v-if="item.loai === 0" class="tiny text-muted fw-normal">Ghế: {{ item.viTriGhe }}</div>
                      </td>
                      <td class="small text-center">{{ item.soLuong }}</td>
                      <td class="small text-end">{{ formatCurrency(item.donGia) }}</td>
                      <td class="small text-end text-danger">{{ formatCurrency(item.giamGia || 0) }}</td>
                      <td class="small text-end">{{ formatCurrency(item.donGia - (item.giamGia || 0)) }}</td>
                      <td class="small text-end fw-bold">{{ formatCurrency(item.thanhTien) }}</td>
                      <td class="small text-center">
                        <button class="btn btn-sm btn-light border p-1 rounded-2"><i class="bi bi-eye"></i></button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Cột phải: Tổng kết & Nút bấm -->
            <div class="col-md-3 ps-3 pt-1">
              <div class="summary-box mb-4">
                <div class="d-flex justify-content-between mb-2">
                  <span class="text-secondary small">Tổng tiền:</span>
                  <span class="fw-bold">{{ formatCurrency(detailInvoice?.tongTien) }}</span>
                </div>
                <div class="d-flex justify-content-between mb-2 pb-2 border-bottom">
                  <span class="text-secondary small">Khách đã trả:</span>
                  <span class="fw-bold">{{ formatCurrency(detailInvoice?.trangThai === 1 ? detailInvoice?.tongTienThanhToan : 0) }}</span>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-3">
                  <span class="small fw-bold">Trạng thái:</span>
                  <span class="fw-bold" :class="detailInvoice?.trangThai === 1 ? 'text-success' : 'text-danger'">
                    {{ detailInvoice?.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
                  </span>
                </div>
                <div class="total-big text-end mt-4">
                   <div class="text-secondary tiny">TỔNG CỘNG</div>
                   <h4 class="fw-bold text-danger mb-0">{{ formatCurrency(detailInvoice?.tongTienThanhToan) }}</h4>
                </div>
              </div>

              <div class="actions-stack d-flex flex-column gap-2 mt-4">
                <el-button type="danger" class="premium-btn w-100" @click="handleUpdateInvoice">Cập nhật</el-button>
                <el-button class="print-btn w-100" @click="handlePrint">In hóa đơn</el-button>
                <el-button type="danger" plain class="w-100 close-btn" @click="modalVisible = false">Đóng</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- Tab Lịch sử thanh toán -->
        <div v-if="activeTab === 'history'" class="tab-content animate__animated animate__fadeIn">
          <div class="payment-history-list">
             <div v-for="h in paymentHistory" :key="h.id" class="history-item d-flex gap-3 p-3 mb-2 rounded bg-light border">
                <div class="icon-box text-success">
                   <i class="bi bi-check-circle-fill fs-4"></i>
                </div>
                <div class="flex-grow-1">
                   <div class="d-flex justify-content-between">
                      <span class="fw-bold">Thanh toán hoàn tất</span>
                      <span class="fw-bold text-primary">{{ formatCurrency(h.soTien) }}</span>
                   </div>
                   <div class="text-secondary small">Mã GD: #{{ h.maGiaoDich || h.id.substring(0,8).toUpperCase() }} • Phương thức: {{ h.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</div>
                   <div class="text-muted tiny mt-1">{{ formatDate(h.ngayThanhToan) }}</div>
                </div>
             </div>
             <div v-if="paymentHistory.length === 0" class="text-center py-5 text-muted">Không có lịch sử thanh toán nào.</div>
          </div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>



<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import BaseModal from '@/components/common/BaseModal.vue';
import { Search } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseTable from '@/components/common/BaseTable.vue';

// --- STATE QUẢN LÝ DỮ LIỆU ---
const router = useRouter();
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);

// Modal state
const modalVisible = ref(false);
const activeTab = ref('info');
const detailInvoice = ref(null);
const invoiceDetails = ref([]);
const paymentHistory = ref([]);

const tableColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã hóa đơn', key: 'maHoaDon' , minWidth: '150px'},
  { label: 'Mã KH', key: 'maKhachHang', width: '100px' },
  { label: 'Mã NV', key: 'maNhanVien', width: '100px' },
  { label: 'Ngày tạo', key: 'ngayTao', width: '180px' },
  { label: 'Kênh bán', key: 'kenhBan', width: '110px' },
  { label: 'Giảm giá', key: 'giamGia', width: '110px' },
  { label: 'Thanh toán', key: 'tongTienThanhToan', width: '140px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' }
];

// Query
const listQuery = reactive({
  tuKhoa: '',
  trangThai: null,
  phuongThucThanhToan: null,
  kyThoiGian: '',
  page: 1,
  size: 5
});

// --- METHODS ---
const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key];
      }
    });

    const res = await hoaDonService.getInvoices(params);
    invoices.value = res.data.content || [];
    total.value = res.data.totalElements || 0;
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
  window.print();
};

const handleUpdateInvoice = () => {
  ElMessage.info('Tính năng cập nhật đang được phát triển');
};

const handleFilter = () => {
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const handleReset = () => {
  listQuery.tuKhoa = '';
  listQuery.trangThai = null;
  listQuery.phuongThucThanhToan = null;
  listQuery.kyThoiGian = '';
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const formatDateShort = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) + ' ' + 
         date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
};

const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const day  = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  return `${time} ${day}`;
};

watch(() => listQuery.page, fetchInvoices);
watch(() => listQuery.size, () => { 
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
});

onMounted(() => {
  fetchInvoices();
});
</script>

<style scoped>
.invoice-meta-header {
  border-left: 5px solid var(--el-color-primary);
}
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

.modal-custom-header {
  margin-bottom: 20px;
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

/* Print Styles */
@media print {
  body * {
    visibility: hidden;
  }
  .custom-invoice-modal, .custom-invoice-modal * {
    visibility: visible;
  }
  .custom-invoice-modal {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    padding: 0;
    margin: 0;
  }
  .actions-stack, .btn-close, .tabs-container {
    display: none !important;
  }
  .detail-table-wrapper {
    max-height: none !important;
    overflow: visible !important;
  }
  .col-md-2, .col-md-7, .col-md-3 {
    width: auto !important;
    flex: none !important;
    float: none !important;
    padding: 10px 0 !important;
    border: none !important;
  }
  .row {
    display: block !important;
  }
  .modal-custom-header h4 {
    margin-bottom: 20px;
  }
}
</style>
