<template>
  <div class="admin-vouchers-page">
    <AdminTableLayout title="Quản lý phiếu giảm giá" titleIcon="bi bi-ticket-perforated-fill"
      addButtonLabel="Thêm phiếu giảm giá" :data="vouchers" :loading="loading" :total="total" v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" @add-click="openDialog()" @reset-filter="resetFilter">
      

      <template #header-actions-left>
        <ExcelActions module="phieu-giam-gia" @import-success="fetchVouchers" />
      </template>

      <template #filters>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="filterStatus" placeholder="Tất cả" @change="fetchVouchers">
            <el-option label="Tất cả" value="" />
            <el-option label="Đang hoạt động" :value="1" />
            <el-option label="Tạm ngưng" :value="0" />
            <el-option label="Đã kết thúc" :value="2" />
          </el-select>
        </div>

        <div class="filter-item search-input-wrapper">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-input v-model="searchQuery" placeholder="Nhập mã hoặc tên phiếu..." clearable
            @input="handleSearch" />
        </div>
      </template>

      <template #content>
        <BaseTable
          :data="vouchers"
          :columns="voucherColumns"
          :loading="loading"
          :total="total"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          :hide-pagination="true"
          @edit="openDialog"
          @delete="handleUpdateStatus"
        >
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-maPhieuGiamGia="{ row }">
            <el-tag type="primary" effect="light" round size="small" style="font-weight: 600; letter-spacing: 0.5px;">
              {{ row.maPhieuGiamGia }}
            </el-tag>
          </template>

          <template #cell-tenPhieu="{ row }">
            <span class="fw-semibold text-dark" style="white-space: nowrap;">{{ row.tenPhieu }}</span>
          </template>

          <template #cell-giaTriHoaDonToiThieu="{ row }">
            <span class="fw-bold text-dark text-nowrap">{{ formatCurrency(row.giaTriHoaDonToiThieu) }}</span>
          </template>

          <template #cell-loaiPhieu="{ row }">
            <div v-if="row.loaiPhieu === 1">
              <el-tag type="warning" effect="dark" round size="small">{{ row.phanTramGiamGia }}%</el-tag>
            </div>
            <div v-else>
              <el-tag type="danger" effect="dark" round size="small">-{{ formatCurrency(row.soTienGiam) }}</el-tag>
            </div>
          </template>

          <template #cell-kieuPhatHanh="{ row }">
            <el-tag :type="row.kieuPhatHanh === 0 ? 'success' : 'info'" effect="light" round size="small">
              <i class="bi" :class="row.kieuPhatHanh === 0 ? 'bi-globe' : 'bi-person-lock'"></i>
              {{ row.kieuPhatHanh === 0 ? ' Công khai' : ' Cá nhân' }}
            </el-tag>
          </template>

          <template #cell-giamToiDa="{ row }">
            <span v-if="row.loaiPhieu === 1" class="text-dark fw-bold">{{ formatCurrency(row.giamToiDa) }}</span>
            <span v-else class="text-secondary small">—</span>
          </template>

          <template #cell-soLuong="{ row }">
            <span class="text-dark fw-bold">{{ row.soLuong }}</span>
          </template>

          <template #cell-ngayBatDau="{ row }">
            <div class="small text-secondary" style="font-size: 11px;">
              <div class="text-nowrap"><i class="bi bi-calendar-check me-1"></i>{{ formatDate(row.ngayBatDau) }}</div>
            </div>
          </template>

          <template #cell-ngayKetThuc="{ row }">
            <div class="small text-secondary" style="font-size: 11px;">
              <div class="text-nowrap"><i class="bi bi-calendar-x me-1"></i>{{ formatDate(row.ngayKetThuc) }}</div>
            </div>
          </template>

          <template #cell-trangThai="{ row }">
            <el-dropdown trigger="click" @command="status => handleUpdateStatus(row, status)" :disabled="row.trangThai === 0 || row.trangThai === 2">
              <el-tag :type="getStatusTag(row.trangThai)" round size="small" :class="{ 'cursor-pointer': row.trangThai !== 0 && row.trangThai !== 2 }">
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
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0 || row.trangThai === 2" @click="openDialog(row)">
                  <i class="bi bi-pencil fs-6"></i>
                </button>
              </el-tooltip>
              <el-dropdown trigger="click" @command="status => handleUpdateStatus(row, status)" :disabled="row.trangThai === 0 || row.trangThai === 2">
                <span class="el-dropdown-link d-inline-block">
                  <el-tooltip content="Thay đổi trạng thái" placement="top">
                    <button class="btn-action-icon action-refresh" :disabled="row.trangThai === 0 || row.trangThai === 2">
                      <i class="bi bi-list-check fs-6"></i>
                    </button>
                  </el-tooltip>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="1" :disabled="row.trangThai === 1">Hoạt động</el-dropdown-item>
                    <el-dropdown-item :command="0" :disabled="row.trangThai === 0">Tạm ngưng</el-dropdown-item>
                    <el-dropdown-item :command="2" :disabled="row.trangThai === 2">Kết thúc</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>
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
  { label: 'Mã giảm giá', key: 'maPhieuGiamGia', width: '140px' },
  { label: 'Tên phiếu giảm giá', key: 'tenPhieu', minWidth: '180px' },
  { label: 'Đơn tối thiểu', key: 'giaTriHoaDonToiThieu', width: '150px' },
  { label: 'Giảm giá', key: 'loaiPhieu', width: '120px' },
  { label: 'Đối tượng', key: 'kieuPhatHanh', width: '130px' },
  { label: 'Giảm tối đa', key: 'giamToiDa', width: '140px' },
  { label: 'Số lượng', key: 'soLuong', width: '100px' },
  { label: 'Từ ngày', key: 'ngayBatDau', width: '160px' },
  { label: 'Đến ngày', key: 'ngayKetThuc', width: '160px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' },
];


const loading = ref(false);
const vouchers = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const filterStatus = ref('');
const searchQuery = ref('');

const fetchVouchers = async () => {
  loading.value = true;
  try {
    const params = {
      keyword: searchQuery.value,
      trangThai: filterStatus.value === '' ? null : filterStatus.value,
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
  }).catch(() => {});
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
.admin-vouchers-page {
  padding: 0;
}
</style>

