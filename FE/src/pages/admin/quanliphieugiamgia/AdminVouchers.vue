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
                inactive-color="#ff4949"
              />
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <!-- Detail Modal -->
    <BaseModal v-model="detailVisible" title="Chi tiết phiếu giảm giá" icon="bi bi-ticket-perforated" width="500px">
      <div v-if="selectedItem" class="p-3">
        <div class="d-flex align-items-center gap-3 mb-4 p-3 bg-light rounded-4">
          <div class="icon-box bg-primary text-white rounded-circle d-flex align-items-center justify-content-center"
            style="width: 50px; height: 50px;">
            <i class="bi bi-ticket-perforated fs-4"></i>
          </div>
          <div>
            <h5 class="m-0 fw-bold">{{ selectedItem.tenPhieu }}</h5>
            <div class="text-secondary small">Mã: {{ selectedItem.maPhieuGiamGia }}</div>
          </div>
        </div>
        <div class="detail-grid">
          <div class="row g-3">
            <div class="col-6">
              <div class="lbl text-secondary small">Loại phiếu</div>
              <div class="val fw-semibold">{{ selectedItem.loaiPhieu === 1 ? 'Giảm phần trăm' : 'Giảm số tiền' }}</div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Giá trị giảm</div>
              <div class="val fw-bold text-danger">
                {{ selectedItem.loaiPhieu === 1 ? selectedItem.phanTramGiamGia + '%' :
                  formatCurrency(selectedItem.soTienGiam) }}
              </div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Đơn tối thiểu</div>
              <div class="val fw-semibold">{{ formatCurrency(selectedItem.donToiThieu) }}</div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Giảm tối đa</div>
              <div class="val fw-semibold">{{ selectedItem.loaiPhieu === 1 ? formatCurrency(selectedItem.giamToiDa) :
                '—' }}
              </div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Số lượng</div>
              <div class="val fw-semibold">{{ selectedItem.soLuong }} / {{ selectedItem.soLuongBanDau ||
                selectedItem.soLuong }}</div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Đối tượng</div>
              <div class="val fw-semibold">{{ selectedItem.doiTuong === 1 ? 'Cá nhân' : 'Công khai' }}</div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Từ ngày</div>
              <div class="val fw-semibold small">{{ formatDate(selectedItem.ngayBatDau) }}</div>
            </div>
            <div class="col-6">
              <div class="lbl text-secondary small">Đến ngày</div>
              <div class="val fw-semibold small">{{ formatDate(selectedItem.ngayKetThuc) }}</div>
            </div>
            <div class="col-12">
              <div class="lbl text-secondary small">Trạng thái</div>
              <el-tag :type="getStatusTag(selectedItem.trangThai)" round size="small">
                {{ getStatusLabel(selectedItem.trangThai) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">Đóng</el-button>
        <el-button type="primary" @click="openDialog(selectedItem); detailVisible = false">Chỉnh sửa</el-button>
      </template>
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
.admin-vouchers-page {
  padding: 0;
}
</style>
