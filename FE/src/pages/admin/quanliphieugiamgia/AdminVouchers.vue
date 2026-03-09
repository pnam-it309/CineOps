<template>
  <div class="admin-vouchers-page">
    <AdminTableLayout title="Quản lý phiếu giảm giá" titleIcon="bi bi-ticket-perforated-fill"
      addButtonLabel="Thêm phiếu giảm giá" :data="vouchers" :loading="loading" :total="total"
      v-model:currentPage="currentPage" v-model:pageSize="pageSize" @add-click="openDialog()"
      @reset-filter="resetFilter">


      <template #header-actions-left>
        <ExcelActions module="phieu-giam-gia" @import-success="fetchVouchers" />
      </template>

      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input v-model="searchQuery" placeholder="Nhập mã hoặc tên phiếu..." :prefix-icon="Search" clearable @input="handleSearch" style="width: 250px;" />
        </div>

        <div class="filter-item">
          <el-select v-model="filterStatus" placeholder="Chọn trạng thái" style="width: 180px;" @change="fetchVouchers" clearable>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đang hoạt động" :value="1" />
            <el-option label="Tạm ngưng" :value="0" />
            <el-option label="Đã kết thúc" :value="2" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="filterLoaiPhieu" placeholder="Chọn loại phiếu" style="width: 180px;" @change="fetchVouchers" clearable>
            <el-option label="Tất cả loại phiếu" value="" />
            <el-option label="Giảm phần trăm" :value="1" />
            <el-option label="Giảm số tiền" :value="2" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="filterDoiTuong" placeholder="Chọn đối tượng" style="width: 180px;" @change="fetchVouchers" clearable>
            <el-option label="Tất cả đối tượng" value="" />
            <el-option label="Công khai" :value="0" />
            <el-option label="Cá nhân" :value="1" />
          </el-select>
        </div>
      </template>

      <template #content>
        <BaseTable :data="vouchers" :columns="voucherColumns" :loading="loading" :total="total"
          v-model:currentPage="currentPage" v-model:pageSize="pageSize" :hide-pagination="true" @edit="openDialog"
          @delete="handleUpdateStatus">
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-maPhieuGiamGia="{ row }">
            <el-tag type="primary" effect="light" round size="small" style="font-weight: 600; letter-spacing: 0.5px;">
              {{ row.maPhieuGiamGia }}
            </el-tag>
          </template>

          <template #cell-soLuong="{ row }">
            <span class="fw-bold text-dark">{{ row.soLuong }}</span>
          </template>

          <template #cell-tenPhieu="{ row }">
            <span class="fw-semibold text-dark" style="white-space: nowrap;">{{ row.tenPhieu }}</span>
          </template>

          <template #cell-loaiPhieu="{ row }">
            <div v-if="row.loaiPhieu === 1">
              <el-tag type="warning" effect="dark" round size="small">{{ row.phanTramGiamGia }}%</el-tag>
            </div>
            <div v-else>
              <el-tag type="danger" effect="dark" round size="small">-{{ formatCurrency(row.soTienGiam) }}</el-tag>
            </div>
          </template>

          <template #cell-giamToiDa="{ row }">
            <span class="small text-secondary">{{ row.loaiPhieu === 1 ? formatCurrency(row.giamToiDa) : '—' }}</span>
          </template>

          <template #cell-doiTuong="{ row }">
            <el-tag size="small" :type="row.doiTuong === 1 ? 'warning' : 'info'" effect="plain" round>
              {{ row.doiTuong === 1 ? 'Cá nhân' : 'Công khai' }}
            </el-tag>
          </template>


          <template #cell-trangThai="{ row }">
            <el-dropdown trigger="click" @command="status => handleUpdateStatus(row, status)"
              :disabled="row.trangThai === 0 || row.trangThai === 2">
              <el-tag :type="getStatusTag(row.trangThai)" round size="small"
                :class="{ 'cursor-pointer': row.trangThai !== 0 && row.trangThai !== 2 }">
                {{ getStatusLabel(row.trangThai) }}
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="1" :disabled="row.trangThai === 1">Hoạt động</el-dropdown-item>
                  <el-dropdown-item :command="0" :disabled="row.trangThai === 0">Tạm ngưng</el-dropdown-item>
                  <el-dropdown-item :command="2" :disabled="row.trangThai === 2">Kết thúc</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center align-items-center gap-1">
              <el-tooltip content="Xem chi tiết" placement="top">
                <button class="btn-action-icon action-view" @click="handleView(row)">
                  <i class="bi bi-eye fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0 || row.trangThai === 2"
                  @click="openDialog(row)">
                  <i class="bi bi-pencil fs-6"></i>
                </button>
              </el-tooltip>
              <el-switch
                :model-value="row.trangThai === 1"
                :disabled="row.trangThai === 2"
                @change="(val) => handleUpdateStatus(row, val ? 1 : 0)"
                class="status-switch mx-1"
                active-color="#ff4949"
                inactive-color="#ff4949"
              />
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <!-- Detail Modal -->
    <BaseModal v-model="detailVisible" title="Thông tin phiếu giảm giá" icon="bi bi-ticket-perforated-fill" width="650px" isDetail onlyCancel>
      <div v-if="selectedItem" class="voucher-premium-container">
        <!-- Premium Hero Section -->
        <div class="profile-hero-banner p-4 text-white position-relative overflow-hidden" 
             style="background: linear-gradient(135deg, #1e293b 0%, #334155 100%);">
          <div class="row align-items-center position-relative" style="z-index: 2;">
            <div class="col-md-5 text-center">
              <div class="voucher-hero-card bg-white p-3 shadow-lg rounded-0 d-inline-block text-dark border-start border-4 border-warning">
                <div class="small opacity-75 uppercase-label text-secondary">GIÁ TRỊ GIẢM</div>
                <div v-if="selectedItem.loaiPhieu === 1" class="display-5 fw-bold">{{ selectedItem.phanTramGiamGia }}%</div>
                <div v-else class="display-5 fw-bold">{{ formatCurrency(selectedItem.soTienGiam).replace('₫', '') }}<small class="fs-4">₫</small></div>
              </div>
            </div>
            <div class="col-md-7 border-start border-white border-opacity-10 ps-4">
              <div class="d-flex align-items-center gap-2 mb-2">
                <el-tag :type="getStatusTag(selectedItem.trangThai)" effect="dark" round size="small" class="fw-bold">
                  {{ getStatusLabel(selectedItem.trangThai).toUpperCase() }}
                </el-tag>
                <span class="badge bg-white bg-opacity-20 font-monospace">#{{ selectedItem.maPhieuGiamGia }}</span>
              </div>
              <h1 class="fw-bold m-0 text-truncate" :title="selectedItem.tenPhieu">{{ selectedItem.tenPhieu }}</h1>
              <p class="opacity-75 mb-0 mt-2"><i class="bi bi-clock-history me-2"></i>Hết hạn: {{ formatDate(selectedItem.ngayKetThuc) }}</p>
            </div>
          </div>
          <!-- Decoration -->
          <div class="decoration-circle position-absolute" style="width: 300px; height: 300px; background: rgba(255,193,7,0.1); border-radius: 50%; top: -150px; right: -50px;"></div>
        </div>

        <!-- Detail Body -->
        <div class="profile-details-body p-4 bg-white">
          <div class="row g-4">
            <!-- Usage Stats -->
            <div class="col-12">
               <h6 class="text-secondary fw-bold small text-uppercase mb-3 letter-spacing-1">Hiệu suất sử dụng</h6>
               <div class="p-3 border bg-light d-flex justify-content-between align-items-center mb-2">
                 <div>
                   <label class="text-secondary tiny-text d-block">TỔNG LƯỢT PHÁT HÀNH</label>
                   <div class="fw-bold fs-5">{{ selectedItem.soLuongBanDau || selectedItem.soLuong }} <span class="small fw-normal">phiếu</span></div>
                 </div>
                 <div class="text-center px-4 border-start border-end">
                    <label class="text-secondary tiny-text d-block">ĐÃ SỬ DỤNG</label>
                    <div class="fw-bold fs-5 text-primary">{{ (selectedItem.soLuongBanDau || selectedItem.soLuong) - selectedItem.soLuong }}</div>
                 </div>
                 <div class="text-end">
                    <label class="text-secondary tiny-text d-block">CÒN LẠI</label>
                    <div class="fw-bold fs-5 text-success">{{ selectedItem.soLuong }}</div>
                 </div>
               </div>
               <el-progress 
                :percentage="Math.round(((selectedItem.soLuongBanDau || selectedItem.soLuong) - selectedItem.soLuong) / (selectedItem.soLuongBanDau || selectedItem.soLuong) * 100)" 
                :stroke-width="12"
                :show-text="true"
                class="square-progress"
              />
            </div>

            <!-- Rules Grid -->
            <div class="col-md-6">
              <h6 class="text-secondary fw-bold small text-uppercase mb-3 letter-spacing-1">Điều kiện áp dụng</h6>
              <div class="info-card p-3 border h-100">
                <div class="info-row mb-3 pb-3 border-bottom">
                  <label class="text-secondary tiny-text d-block">ĐƠN TỐI THIỂU</label>
                  <div class="fw-bold fs-5">{{ formatCurrency(selectedItem.donToiThieu) }}</div>
                </div>
                <div class="info-row">
                  <label class="text-secondary tiny-text d-block">GIẢM TỐI ĐA</label>
                  <div class="fw-bold fs-5 text-danger">
                    {{ selectedItem.loaiPhieu === 1 ? formatCurrency(selectedItem.giamToiDa) : 'Không giới hạn' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Validity & Target -->
            <div class="col-md-6">
              <h6 class="text-secondary fw-bold small text-uppercase mb-3 letter-spacing-1">Thời hạn & Đối tượng</h6>
              <div class="info-card p-3 border h-100">
                <div class="info-row mb-3 pb-3 border-bottom">
                  <label class="text-secondary tiny-text d-block">NGÀY BẮT ĐẦU</label>
                  <div class="fw-bold">{{ formatDate(selectedItem.ngayBatDau) }}</div>
                </div>
                <div class="info-row">
                  <label class="text-secondary tiny-text d-block">ĐỐI TƯỢNG ÁP DỤNG</label>
                  <div class="fw-bold">
                    <i :class="selectedItem.doiTuong === 1 ? 'bi bi-person-heart text-warning' : 'bi bi-globe text-primary'" class="me-2"></i>
                    {{ selectedItem.doiTuong === 1 ? 'Khách hàng cá nhân' : 'Toàn bộ khách hàng' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Campaign Stats -->
            <div class="col-12" v-if="selectedItem.tongTienGiam">
              <div class="p-3 bg-warning bg-opacity-10 border border-warning border-opacity-20 d-flex justify-content-between align-items-center">
                 <div class="d-flex align-items-center gap-3">
                   <div class="icon-box bg-warning text-white p-3"><i class="bi bi-graph-up"></i></div>
                   <div>
                     <div class="small fw-bold text-dark">TỔNG NGÂN SÁCH ĐÃ TIẾT KIỆM</div>
                     <div class="text-secondary small">Dựa trên các giao dịch đã áp dụng mã này</div>
                   </div>
                 </div>
                 <div class="text-end">
                   <div class="display-6 fw-bold text-dark">{{ formatCurrency(selectedItem.tongTienGiam) }}</div>
                 </div>
              </div>
            </div>
          </div>
        </div>


      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Plus, Edit, Delete, Refresh, Ticket, PriceTag, Calendar } from '@element-plus/icons-vue';
import { voucherService } from '@/services/api/admin/voucherService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import debounce from 'lodash/debounce';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';

const router = useRouter();

const voucherColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã giảm giá', key: 'maPhieuGiamGia', width: '130px' },
  { label: 'Tên phiếu giảm giá', key: 'tenPhieu', width: '180px' },
  { label: 'Số lượng', key: 'soLuong', width: '90px' },
  { label: 'Giảm giá', key: 'loaiPhieu', width: '120px' },
  { label: 'Giảm tối đa', key: 'giamToiDa', width: '120px' },
  { label: 'Đối tượng', key: 'doiTuong', width: '110px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' },
];


const loading = ref(false);
const vouchers = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(5);
const filterStatus = ref('');
const filterLoaiPhieu = ref('');
const filterDoiTuong = ref('');
const searchQuery = ref('');
const detailVisible = ref(false);
const selectedItem = ref(null);

const handleView = (row) => {
  selectedItem.value = row;
  detailVisible.value = true;
};

const fetchVouchers = async () => {
  loading.value = true;
  try {
    const params = {
      keyword: searchQuery.value,
      trangThai: filterStatus.value === '' ? null : filterStatus.value,
      loaiPhieu: filterLoaiPhieu.value === '' ? null : filterLoaiPhieu.value,
      doiTuong: filterDoiTuong.value === '' ? null : filterDoiTuong.value,
      page: currentPage.value - 1,
      size: pageSize.value
    };
    const res = await voucherService.getAll(params);
    vouchers.value = res.data?.data?.content || [];
    total.value = res.data?.data?.totalElements || 0;
  } catch (e) {
    notification.error('Không thể tải danh sách voucher');
  } finally {
    loading.value = false;
  }
};

const handleSearch = debounce(() => {
  currentPage.value = 1;
  fetchVouchers();
}, 300);

const resetFilter = () => {
  searchQuery.value = '';
  filterStatus.value = '';
  filterLoaiPhieu.value = '';
  filterDoiTuong.value = '';
  currentPage.value = 1;
  fetchVouchers();
};

const openDialog = (row = null) => {
  if (row && row.id) {
    router.push(`/admin/vouchers/edit/${row.id}`);
  } else {
    router.push('/admin/vouchers/add');
  }
};

const handleUpdateStatus = (row, status = null) => {
  const isInactive = row.trangThai === 0;
  const newStatus = status !== null ? status : (isInactive ? 1 : 0);
  const label = getStatusLabel(newStatus).toLowerCase();

  if (newStatus === row.trangThai) return;

  confirmDialog.custom(
    `Thay đổi trạng thái voucher <b>${row.maPhieuGiamGia}</b> thành <b>${label}</b>?`,
    'Cập nhật trạng thái',
    'Xác nhận'
  ).then(async () => {
    try {
      await voucherService.update(row.id, { ...row, trangThai: newStatus });
      notification.success(`Đã ${label} voucher thành công`);
      fetchVouchers();
    } catch (e) {
      notification.error('Cập nhật thất bại');
    }
  }).catch(() => { });
};


const getStatusTag = (status) => {
  const map = { 1: 'success', 0: 'warning', 2: 'info' };
  return map[status] || 'info';
};

const getStatusLabel = (status) => {
  const map = { 1: 'Hoạt động', 0: 'Tạm ngưng', 2: 'Kết thúc' };
  return map[status] || 'Không xác định';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);
const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
};

onMounted(fetchVouchers);
watch([currentPage, pageSize], fetchVouchers);
</script>

<style scoped>

.voucher-premium-container { margin: -20px; background: #f8fafc; }
.voucher-ticket { display: flex; overflow: hidden; border-radius: 0 0 20px 20px; }
.ticket-left { width: 140px; display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative; border-right: 2px dashed #e2e8f0; }
.ticket-left::before, .ticket-left::after { content: ''; position: absolute; right: -10px; width: 20px; height: 20px; background: #f8fafc; border-radius: 50%; z-index: 2; }
.ticket-left::before { top: -10px; }
.ticket-left::after { bottom: -10px; }
.ticket-right { flex: 1; }
.discount-value { text-align: center; }
.ticket-sideways { position: absolute; left: -40px; top: 50%; transform: rotate(-90deg) translateY(-50%); letter-spacing: 2px; }
.section-title-sm { font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; color: #94a3b8; margin-bottom: 12px; }
.tiny-text { font-size: 9px; font-weight: 700; text-transform: uppercase; color: #94a3b8; display: block; margin-bottom: 2px; }
.rule-card { transition: all 0.2s; }
.rule-card:hover { border-color: var(--el-color-primary-light-5); box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
.voucher-code-box { border-bottom: 1px solid #f1f5f9; padding-bottom: 10px; }

.admin-vouchers-page {
  padding: 0;
}
</style>
