<template>
  <div class="admin-vouchers-page">
    <AdminTableLayout title="Quản lý phiếu giảm giá" titleIcon="bi bi-ticket-perforated-fill"
      addButtonLabel="Thêm phiếu giảm giá" :data="vouchers" :loading="loading" :total="total" v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" @add-click="openDialog()" @reset-filter="resetFilter">
      
      <template #header-actions-left>
        <el-button v-if="selectedIds.length" type="warning" plain round :icon="Refresh" @click="handleBulkDelete">
          Đổi trạng thái {{ selectedIds.length }} voucher
        </el-button>
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
          v-model:selection="selectedObjects"
          :hide-pagination="true"
          @edit="openDialog"
          @delete="handleDelete"
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
            <span class="fw-semibold text-dark small" style="white-space: nowrap;">{{ row.tenPhieu }}</span>
          </template>

          <template #cell-giaTriHoaDonToiThieu="{ row }">
            <span class="fw-bold text-dark small text-nowrap">{{ formatCurrency(row.giaTriHoaDonToiThieu) }}</span>
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
            <span v-if="row.loaiPhieu === 1" class="text-dark fw-bold small">{{ formatCurrency(row.giamToiDa) }}</span>
            <span v-else class="text-secondary small">—</span>
          </template>

          <template #cell-soLuong="{ row }">
            <span class="text-dark fw-bold small">{{ row.soLuong }}</span>
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
            <el-tag :type="getStatusTag(row.trangThai)" round size="small">
              {{ getStatusLabel(row.trangThai) }}
            </el-tag>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon btn-action-edit" @click="openDialog(row)">
                  <i class="bi bi-pencil fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip :content="row.trangThai === 0 ? 'Kích hoạt' : 'Tạm ngưng'" placement="top">
                <button class="btn-action-icon btn-action-refresh" @click="handleDelete(row)">
                  <i class="bi bi-arrow-repeat fs-6"></i>
                </button>
              </el-tooltip>
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

const router = useRouter();

const voucherColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'MÃ GIẢM GIÁ', key: 'maPhieuGiamGia', width: '160px' },
  { label: 'TÊN PHIẾU GIẢM GIÁ', key: 'tenPhieu', minWidth: '400px' },
  { label: 'ĐƠN TỐI THIỂU', key: 'giaTriHoaDonToiThieu', width: '180px' },
  { label: 'GIẢM GIÁ', key: 'loaiPhieu', width: '150px' },
  { label: 'GIẢM TỐI ĐA', key: 'giamToiDa', width: '180px' },
  { label: 'SỐ LƯỢNG', key: 'soLuong', width: '120px' },
  { label: 'TỪ NGÀY', key: 'ngayBatDau', width: '180px' },
  { label: 'ĐẾN NGÀY', key: 'ngayKetThuc', width: '180px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '160px' },
];

const selectedObjects = ref([]);
const selectedIds = computed(() => selectedObjects.value.map(item => item.id));

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

const handleDelete = (row) => {
  const isInactive = row.trangThai === 0;
  const newStatus = isInactive ? 1 : 0;
  const label = isInactive ? 'kích hoạt' : 'tạm ngưng';
  
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

const handleBulkDelete = () => {
    confirmDialog.custom(
        `Thay đổi trạng thái cho <b>${selectedIds.value.length}</b> voucher đã chọn?`,
        'Cập nhật hàng loạt',
        'Đồng ý'
    ).then(async () => {
        try {
            await Promise.all(selectedObjects.value.map(item => {
                const newStatus = item.trangThai === 0 ? 1 : 0;
                return voucherService.update(item.id, { ...item, trangThai: newStatus });
            }));
            notification.success(`Đã cập nhật ${selectedIds.value.length} voucher thành công`);
            selectedObjects.value = [];
            fetchVouchers();
        } catch (error) {
            notification.error('Có lỗi khi cập nhật trạng thái');
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
