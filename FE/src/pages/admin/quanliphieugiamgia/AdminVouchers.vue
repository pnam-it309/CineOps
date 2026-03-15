<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable 
      title="Quản lý phiếu giảm giá" 
      subtitle="Quản lý danh sách Phiếu Giảm Giá có mặt tại cửa hàng"
      titleIcon="bi bi-ticket-perforated-fill" 
      addButtonLabel="Thêm phiếu giảm giá"
      :data="vouchers" 
      :columns="voucherColumns" 
      :loading="loading" 
      :total="total"
      v-model:currentPage="currentPage" 
      v-model:pageSize="pageSize" 
      @add-click="openDialog()" 
      @reset-filter="resetFilter"
      @edit="openDialog" 
      @delete="handleUpdateStatus"
      @fetch="fetchVouchers"
    >
      <template #header-actions-left>
        <ExcelActions module="phieu-giam-gia" @import-success="fetchVouchers" />
      </template>

      <!-- Optimized Filters (Matching Image 1: Labels above inputs) -->
      <template #filters>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Tìm kiếm chung</label>
          <el-input v-model="searchQuery" placeholder="Tìm theo mã hoặc tên..." :prefix-icon="Search" clearable
            class="w-100" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Kiểu voucher</label>
          <el-select v-model="filterLoaiPhieu" placeholder="Tất cả" class="w-100" clearable>
            <el-option label="Tất cả" value="" />
            <el-option label="Giảm phần trăm" :value="0" />
            <el-option label="Giảm số tiền" :value="1" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Đối tượng</label>
          <el-select v-model="filterKieuPhatHanh" placeholder="Tất cả" class="w-100" clearable content="filterKieuPhatHanh">
            <el-option label="Tất cả" value="" />
            <el-option label="Công khai" :value="0" />
            <el-option label="Cá nhân" :value="1" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Thời gian hiệu lực</label>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="Từ"
            end-placeholder="Đến"
            class="w-100"
          />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Trạng thái hoạt động</label>
          <el-select v-model="filterStatus" placeholder="Tất cả" class="w-100" clearable>
            <el-option label="Tất cả" value="" />
            <el-option label="Hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>
      </template>

      <!-- Cell Templates -->
      <template #cell-stt="{ index }">
        <span class="text-secondary smaller">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
      </template>

      <template #cell-maPhieuGiamGia="{ row }">
        <div class="text-center py-1">
          <span class="fw-bold text-dark font-monospace">{{ row.maPhieuGiamGia }}</span>
        </div>
      </template>

      <template #cell-tenPhieu="{ row }">
        <div class="fw-bold text-dark">{{ row.tenPhieu }}</div>
      </template>

      <template #cell-kieuPhatHanh="{ row }">
        <div class="text-center">
          <el-tag size="small" :type="row.kieuPhatHanh === 1 ? 'primary' : 'info'" effect="light" round class="fw-bold" style="opacity: 0.8">
            {{ row.kieuPhatHanh === 1 ? 'Cá nhân' : 'Công khai' }}
          </el-tag>
        </div>
      </template>

      <template #cell-soLuong="{ row }">
        <div class="text-center fw-bold text-dark">{{ row.soLuong }}</div>
      </template>

      <template #cell-detail="{ row }">
        <div class="d-flex justify-content-center py-1">
          <div class="text-start" style="min-width: 140px;">
            <el-tag :type="row.loaiPhieu === 0 ? 'danger' : 'success'" effect="light" size="small" class="fw-bold mb-1">
              {{ row.loaiPhieu === 0 ? `Giảm ${row.phanTramGiamGia}%` : `Giảm -${formatCurrency(row.soTienGiam)}` }}
            </el-tag>
            <div class="smaller text-secondary">Tối đa: {{ row.loaiPhieu === 0 ? formatCurrency(row.giamToiDa) : '—' }}</div>
            <div class="smaller text-secondary">Đơn tối thiểu: {{ formatCurrency(row.giaTriHoaDonToiThieu) }}</div>
          </div>
        </div>
      </template>

      <template #cell-time="{ row }">
        <div class="d-flex justify-content-center">
          <div class="text-start smaller text-secondary lh-sm" style="white-space: nowrap;">
            <div><span class="opacity-50 d-inline-block" style="min-width: 32px;">Từ:</span> {{ formatFullDate(row.ngayBatDau) }}</div>
            <div><span class="opacity-50 d-inline-block" style="min-width: 32px;">Đến:</span> {{ formatFullDate(row.ngayKetThuc) }}</div>
          </div>
        </div>
      </template>

      <template #cell-trangThai="{ row }">
        <el-dropdown trigger="click" @command="status => handleUpdateStatus(row, status)"
          :disabled="row.trangThai === 0 || row.trangThai === 2">
          <el-tag :type="getStatusTag(row.trangThai)" round size="small" class="fw-bold"
            :class="{ 'cursor-pointer': row.trangThai !== 0 && row.trangThai !== 2 }">
            {{ getStatusLabel(row.trangThai) }}
          </el-tag>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :command="1">Đang diễn ra</el-dropdown-item>
              <el-dropdown-item :command="0">Chưa bắt đầu</el-dropdown-item>
              <el-dropdown-item :command="2">Đã kết thúc</el-dropdown-item>
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
          <el-switch :model-value="row.trangThai === 1" :disabled="row.trangThai === 2"
            @change="(val) => handleUpdateStatus(row, val ? 1 : 0)" class="status-switch mx-1" active-color="#ff4949"
            inactive-color="#ff4949" />
        </div>
      </template>
    </BaseTable>

    <!-- Detail Modal -->
    <BaseModal v-model="detailVisible" title="Thông tin phiếu giảm giá" icon="bi bi-ticket-perforated-fill"
      width="650px" isDetail onlyCancel>
      <div v-if="selectedItem" class="voucher-premium-container">
        <!-- Premium Hero Section -->
        <div class="profile-hero-banner p-4 text-white position-relative overflow-hidden"
          style="background: linear-gradient(135deg, #1e293b 0%, #334155 100%);">
          <div class="row align-items-center position-relative" style="z-index: 2;">
            <div class="col-md-5 text-center">
              <div
                class="voucher-hero-card bg-white p-3 shadow-lg rounded-0 d-inline-block text-dark border-start border-4 border-warning">
                <div class="small opacity-75 uppercase-label text-secondary">GIÁ TRỊ GIẢM</div>
                <div v-if="selectedItem.loaiPhieu === 1" class="display-5 fw-bold">{{ selectedItem.phanTramGiamGia }}%
                </div>
                <div v-else class="display-5 fw-bold">{{ formatCurrency(selectedItem.soTienGiam).replace('₫', '')
                  }}<small class="fs-4">₫</small></div>
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
              <p class="opacity-75 mb-0 mt-2"><i class="bi bi-clock-history me-2"></i>Hết hạn: {{
                formatDate(selectedItem.ngayKetThuc) }}</p>
            </div>
          </div>
          <!-- Decoration -->
          <div class="decoration-circle position-absolute"
            style="width: 300px; height: 300px; background: rgba(255,193,7,0.1); border-radius: 50%; top: -150px; right: -50px;">
          </div>
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
                  <div class="fw-bold fs-5">{{ selectedItem.soLuongBanDau || selectedItem.soLuong }} <span
                      class="small fw-normal">phiếu</span></div>
                </div>
                <div class="text-center px-4 border-start border-end">
                  <label class="text-secondary tiny-text d-block">ĐÃ SỬ DỤNG</label>
                  <div class="fw-bold fs-5 text-primary">{{ (selectedItem.soLuongBanDau || selectedItem.soLuong) -
                    selectedItem.soLuong }}</div>
                </div>
                <div class="text-end">
                  <label class="text-secondary tiny-text d-block">CÒN LẠI</label>
                  <div class="fw-bold fs-5 text-success">{{ selectedItem.soLuong }}</div>
                </div>
              </div>
              <el-progress
                :percentage="Math.round(((selectedItem.soLuongBanDau || selectedItem.soLuong) - selectedItem.soLuong) / (selectedItem.soLuongBanDau || selectedItem.soLuong) * 100)"
                :stroke-width="12" :show-text="true" class="square-progress" />
            </div>

            <!-- Rules Grid -->
            <div class="col-md-6">
              <h6 class="text-secondary fw-bold small text-uppercase mb-3 letter-spacing-1">Điều kiện áp dụng</h6>
              <div class="info-card p-3 border h-100">
                <div class="info-row mb-3 pb-3 border-bottom">
                  <label class="text-secondary tiny-text d-block">ĐƠN TỐI THIỂU</label>
                  <div class="fw-bold fs-5">{{ formatCurrency(selectedItem.giaTriHoaDonToiThieu) }}</div>
                </div>
                <div class="info-row">
                  <label class="text-secondary tiny-text d-block">GIẢM TỐI ĐA</label>
                  <div class="fw-bold fs-5 text-danger">
                    {{ selectedItem.loaiPhieu === 0 ? formatCurrency(selectedItem.giamToiDa) : 'Không giới hạn' }}
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
                    <i :class="selectedItem.kieuPhatHanh === 1 ? 'bi bi-person-heart text-warning' : 'bi bi-globe text-primary'"
                      class="me-2"></i>
                    {{ selectedItem.kieuPhatHanh === 1 ? 'Khách hàng cá nhân' : 'Toàn bộ khách hàng' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Campaign Stats -->
            <div class="col-12" v-if="selectedItem.tongTienGiam">
              <div
                class="p-3 bg-warning bg-opacity-10 border border-warning border-opacity-20 d-flex justify-content-between align-items-center">
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
import BaseTable from '@/components/common/BaseTable.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';

const router = useRouter();

const voucherColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã', key: 'maPhieuGiamGia', width: '130px', align: 'center' },
  { label: 'Tên', key: 'tenPhieu', width: '200px' },
  { label: 'Đối tượng', key: 'kieuPhatHanh', width: '110px', align: 'center' },
  { label: 'Số lượng', key: 'soLuong', width: '120px', align: 'center' },
  { label: 'Chi tiết ưu đãi', key: 'detail', width: '220px', align: 'center' },
  { label: 'Thời gian áp dụng', key: 'time', width: '200px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px', align: 'center' },
];


const loading = ref(false);
const vouchers = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(5);
const filterStatus = ref('');
const filterLoaiPhieu = ref('');
const filterKieuPhatHanh = ref('');
const searchQuery = ref('');
const dateRange = ref([]);
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
      kieuPhatHanh: filterKieuPhatHanh.value === '' ? null : filterKieuPhatHanh.value,
      startDate: dateRange.value?.[0] ? dateRange.value[0].toISOString() : null,
      endDate: dateRange.value?.[1] ? dateRange.value[1].toISOString() : null,
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: 'id,desc'
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
  filterKieuPhatHanh.value = '';
  dateRange.value = [];
  currentPage.value = 1;
};

watch(searchQuery, handleSearch);
watch([filterStatus, filterLoaiPhieu, filterKieuPhatHanh, dateRange, currentPage, pageSize], () => {
  if (loading.value) return;
  fetchVouchers();
});

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
  const map = { 1: 'Đang diễn ra', 0: 'Chưa bắt đầu', 2: 'Đã kết thúc' };
  return map[status] || 'Không xác định';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);
const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric'
  });
};

const formatFullDate = (dateStr) => {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  const time = d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const date = d.toLocaleDateString('vi-VN', { day: 'numeric', month: 'numeric', year: 'numeric' });
  return `${time} ${date}`;
};

onMounted(fetchVouchers);
watch([currentPage, pageSize], fetchVouchers);
</script>

<style scoped>
/* Scoped styles are minimal, prioritizing global CSS and Bootstrap utilities */
.status-switch :deep(.el-switch__label) {
  display: none !important;
}

.w-fit {
  width: fit-content;
}

.font-monospace {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

:deep(.el-tag--danger.el-tag--light) {
  background-color: #fee2e2;
  border-color: #fca5a5;
  color: #ef4444;
}

:deep(.el-tag--success.el-tag--light) {
  background-color: #f0fdf4;
  border-color: #86efac;
  color: #16a34a;
}

:deep(.el-tag--primary.el-tag--light) {
  background-color: #eff6ff;
  border-color: #bfdbfe;
  color: #3b82f6;
}

.letter-spacing-1 {
  letter-spacing: 1px;
}
</style>
