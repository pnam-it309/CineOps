<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable 
      title="Quản lý Hóa Đơn" 
      subtitle="Tra cứu và quản lý danh sách các giao dịch, hóa đơn thanh toán trong hệ thống"
      titleIcon="bi bi-receipt" 
      :data="invoices" 
      :columns="tableColumns"
      :loading="loading" 
      :total="total" 
      v-model:currentPage="listQuery.page" 
      v-model:pageSize="listQuery.size"
      @reset-filter="handleReset" 
      @edit="null" 
      @delete="null"
      @fetch="handleFilter"
    >
      <!-- Optimized Filters (Matching Image 1: Labels above inputs) -->
      <template #filters>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Tìm kiếm nhanh</label>
          <el-input v-model="listQuery.tuKhoa" placeholder="Mã HĐ, khách hàng..." :prefix-icon="Search" clearable
            @keyup.enter="handleFilter" @clear="handleFilter" style="width: 220px;" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Khoảng ngày</label>
          <el-date-picker v-model="customDateRange" type="daterange" range-separator="-" start-placeholder="Từ"
            end-placeholder="Đến" format="DD/MM/YYYY" value-format="YYYY-MM-DD" @change="handleDateChange"
            style="width: 240px" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Trạng thái</label>
          <el-select v-model="listQuery.trangThai" placeholder="Tất cả" clearable @change="handleFilter"
            style="width: 140px">
            <el-option label="Tất cả" value="" />
            <el-option label="Thành công" :value="1" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Kỳ thời gian</label>
          <el-select v-model="listQuery.kyThoiGian" placeholder="Tất cả" clearable @change="handlePeriodChange"
            style="width: 140px">
            <el-option label="Tất cả" value="" />
            <el-option label="Hôm nay" value="TODAY" />
            <el-option label="Tuần này" value="THIS_WEEK" />
            <el-option label="Tháng này" value="THIS_MONTH" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1 d-none d-xl-flex">
          <label class="smaller text-secondary fw-bold ms-1">Giá thanh toán</label>
          <div class="d-flex align-items-center gap-1">
            <el-input-number v-model="listQuery.minPrice" :controls="false" placeholder="Giá từ" @change="handleFilter"
              style="width: 90px" />
            <span class="text-secondary smaller">-</span>
            <el-input-number v-model="listQuery.maxPrice" :controls="false" placeholder="Đến" @change="handleFilter"
              style="width: 90px" />
          </div>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Sắp xếp</label>
          <el-select v-model="listQuery.sortDir" placeholder="Mới nhất" @change="handleFilter" style="width: 140px">
            <el-option label="Mới nhất" value="DESC" />
            <el-option label="Cũ nhất" value="ASC" />
          </el-select>
        </div>
      </template>

      <template #cell-stt="{ index }">
        <span class="text-secondary smaller">{{ (listQuery.page - 1) * listQuery.size + index + 1 }}</span>
      </template>

      <template #cell-maHoaDon="{ row }">
        <span class="fw-bold text-primary">#{{ row.maHoaDon }}</span>
      </template>

      <template #cell-maKhachHang="{ row }">
        <div class="d-flex justify-content-center">
          <div class="text-center">
            <div class="text-dark fw-bold">{{ row.tenKhachHang || 'Khách vãng lai' }}</div>
            <div class="smaller text-secondary">{{ row.maKhachHang || '' }}</div>
          </div>
        </div>
      </template>

      <template #cell-maNhanVien="{ row }">
        <span class="smaller text-secondary">{{ row.maNhanVien || '—' }}</span>
      </template>

      <template #cell-ngayTao="{ row }">
        <span class="smaller text-dark fw-bold">{{ formatDate(row.ngayTao) }}</span>
      </template>

      <template #cell-kenhBan="{ row }">
        <el-tag size="small" :type="row.loaiHoaDon === 1 ? 'success' : 'info'" effect="plain" class="fw-bold">
          {{ row.loaiHoaDon === 1 ? 'Online' : 'Quầy' }}
        </el-tag>
      </template>

      <template #cell-tongTienGiamGia="{ row }">
        <span class="text-danger smaller fw-bold">-{{ formatCurrency(row.tongTienGiamGia) }}</span>
      </template>

      <template #cell-tongTienThanhToan="{ row }">
        <span class="text-dark fw-bold">{{ formatCurrency(row.tongTienThanhToan) }}</span>
      </template>

      <template #cell-phuongThucThanhToan="{ row }">
        <span class="smaller">
          <i :class="row.phuongThucThanhToan === 0 ? 'bi bi-cash' : 'bi bi-credit-card'" class="me-1"></i>
          {{ row.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}
        </span>
      </template>

      <template #cell-trangThai="{ row }">
        <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" effect="dark" class="fw-bold">
          {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center align-items-center">
          <el-tooltip content="Chi tiết hóa đơn" placement="top">
            <button class="btn-action-icon action-view" @click="viewDetails(row)">
              <i class="bi bi-file-earmark-text"></i>
            </button>
          </el-tooltip>
        </div>
      </template>
    </BaseTable>

    <!-- Modal Chi Tiết Hóa Đơn -->
    <BaseModal v-model="modalVisible" :title="'Chi tiết hóa đơn: #' + (detailInvoice?.maHoaDon || '')" width="1100px"
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
                              <i class="bi bi-geo-alt-fill text-danger me-1"></i> Phòng: <span class="fw-bold">{{
                                item.tenPhongChieu || '—' }}</span>
                              <span class="mx-1">|</span>
                              <i class="bi bi-usb-drive-fill text-primary me-1"></i> Ghế: <span
                                class="fw-bold text-danger">{{ item.viTriGhe || '—' }}</span>
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
                      <span class="fw-bold">{{ formatCurrency(detailInvoice.trangThai === 1 ?
                        detailInvoice.tongTienThanhToan : 0) }}</span>
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
  { label: 'STT', key: 'stt', width: '60px', align: 'center' },
  { label: 'Mã hóa đơn', key: 'maHoaDon', width: '150px', align: 'center' },
  { label: 'Ngày tạo', key: 'ngayTao', width: '180px', align: 'center' },
  { label: 'Kênh bán', key: 'kenhBan', width: '110px', align: 'center' },
  { label: 'Hình thức', key: 'phuongThucThanhToan', width: '140px', align: 'center' },
  { label: 'Giảm giá', key: 'tongTienGiamGia', width: '110px', align: 'center' },
  { label: 'Thanh toán', key: 'tongTienThanhToan', width: '140px', align: 'center' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px', align: 'center' }
];

const listQuery = reactive({
  tuKhoa: '',
  trangThai: '',
  phuongThucThanhToan: '',
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
    tuKhoa: '', trangThai: '', phuongThucThanhToan: '',
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
/* Zen scoped CSS: purely functional functional functional */
:deep(.el-range-editor.el-input__wrapper) {
  padding: 0 10px;
}
</style>
