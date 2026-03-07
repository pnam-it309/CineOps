<template>
  <AdminTableLayout
    title="Danh sách ghế"
    titleIcon="bi bi-list-ul"
    addButtonLabel="Thêm ghế lẻ"
    :data="paginatedSeats"
    :loading="loading"
    :total="filteredSeats.length"
    v-model:currentPage="currentPage"
    v-model:pageSize="pageSize"
    v-model:selection="selectedSeats"
    @add-click="$emit('open-dialog')"
    @reset-filter="resetFilter"
  >
    <!-- Header Actions Left Slot -->
    <template #header-actions-left>
      <ExcelActions module="ghe" @import-success="$emit('fetch-seats')" />
      <el-button v-if="selectedIds.length" type="warning" plain round :icon="Refresh" @click="handleBulkToggleStatus">
        Đổi trạng thái {{ selectedIds.length }} ghế
      </el-button>
    </template>


    <!-- Filters Slot -->
    <template #filters>
      <div class="filter-item search-input-wrapper">
        <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
        <el-input 
          v-model="searchQuery" 
          placeholder="Tìm kiếm số ghế..." 
          :prefix-icon="Search"
          size="default" 
          clearable 
        />
      </div>

      <div class="filter-item">
        <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
        <el-select 
          :model-value="selectedRoom" 
          @update:model-value="val => $emit('update:selectedRoom', val)" 
          placeholder="Phòng chiếu" 
          style="width: 180px;" 
          @change="$emit('fetch-seats')"
        >
          <template #prefix><el-icon><Monitor /></el-icon></template>
          <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
        </el-select>
      </div>

      <div class="filter-item">
        <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
        <el-select v-model="filterLoaiGhe" placeholder="Loại ghế" style="width: 150px;">
          <template #prefix><el-icon><PriceTag /></el-icon></template>
          <el-option value="" label="Tất cả loại" />
          <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
        </el-select>
      </div>
    </template>

    <!-- Content Slot with BaseTable -->
    <template #content>
      <BaseTable
        :data="paginatedSeats"
        :columns="seatColumns"
        :loading="loading"
        :total="filteredSeats.length"
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        v-model:selection="selectedSeats"
        :hide-pagination="true"
        @edit="row => $emit('open-dialog', row)"
        @toggle-status="row => $emit('toggle-status', row)"
      >
        <template #cell-index="{ index }">
          <span class="text-secondary small">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
        </template>

        <template #cell-soGhe="{ row }">
          <span class="badge fw-bold px-2 py-1 rounded-2"
            style="font-size: 11px; background: #fff; color: #000; border: 1.5px solid #000;">{{ row.soGhe }}</span>
        </template>

        <template #cell-soHang="{ row }">
          <span class="text-dark small">Hàng <b>{{ row.soHang }}</b></span>
        </template>

        <template #cell-soCot="{ row }">
          <span class="text-dark small">Cột <b>{{ row.soCot }}</b></span>
        </template>

        <template #cell-tenPhongChieu="{ row }">
          <span class="text-secondary small">{{ row.tenPhongChieu }}</span>
        </template>

        <template #cell-loaiGhe="{ row }">
          <el-tag :type="getSeatTypeTag(row.tenLoaiGhe)" round size="small" effect="plain">{{ row.tenLoaiGhe }}</el-tag>
        </template>

        <template #cell-phuPhi="{ row }">
          <span class="text-primary fw-bold small">{{ row.phuPhi > 0 ? formatCurrency(row.phuPhi) : '—' }}</span>
        </template>

        <template #cell-trangThai="{ row }">
          <el-tag :type="row.trangThai === 1 ? 'success' : 'warning'" round size="small" :class="{ 'cursor-pointer': row.trangThai === 1 }" @click="row.trangThai === 1 ? $emit('update-status', { row, status: 0 }) : null">
            {{ row.trangThai === 1 ? 'Hoạt động' : 'Bảo trì' }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <div class="d-flex justify-content-center gap-1">
            <el-tooltip content="Chỉnh sửa ghế" placement="top">
              <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="$emit('open-dialog', row)">
                <i class="bi bi-pencil"></i>
              </button>
            </el-tooltip>
            <el-tooltip content="Thay đổi trạng thái" placement="top">
              <button class="btn-action-icon action-refresh" :disabled="row.trangThai === 0" @click="$emit('update-status', { row, status: row.trangThai === 1 ? 0 : 1 })">
                <i class="bi bi-arrow-repeat"></i>
              </button>
            </el-tooltip>
          </div>
        </template>
      </BaseTable>
    </template>
  </AdminTableLayout>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Search, Monitor, PriceTag, Refresh } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

import BaseTable from '@/components/common/BaseTable.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';
import { gheService } from '@/services/api/admin/gheService';

const seatColumns = [
  { label: 'STT', key: 'index', width: '60px' },
  { label: 'Số ghế', key: 'soGhe', width: '100px' },
  { label: 'Hàng', key: 'soHang', width: '100px' },
  { label: 'Cột', key: 'soCot', width: '100px' },
  { label: 'Phòng', key: 'tenPhongChieu', width: '150px' },
  { label: 'Loại ghế', key: 'loaiGhe', width: '100px' },
  { label: 'Phụ phí', key: 'phuPhi', width: '100px' },
  { label: 'Trạng thái', key: 'trangThai', width: '100px' },
];

const props = defineProps({
  seats: Array,
  loaiGheList: Array,
  phongChieuList: Array,
  selectedRoom: [Number, String],
  loading: Boolean
});

const emit = defineEmits(['update:selectedRoom', 'fetch-seats', 'open-dialog', 'update-status', 'reset-filter']);

const filterLoaiGhe = ref('');
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(5);
const selectedSeats = ref([]);
const selectedIds = computed(() => selectedSeats.value.map(item => item.id));

const handleBulkToggleStatus = () => {
    ElMessageBox.confirm(
        `Xác nhận thay đổi trạng thái cho <b>${selectedIds.value.length}</b> ghế đã chọn?`,
        'Thay đổi trạng thái hàng loạt',
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy',
            type: 'warning'
        }
    ).then(async () => {
        try {
            // Toggle status cho từng ghế dựa trên trạng thái hiện tại (nếu đa số đang 1 thì set về 0, hoặc ngược lại)
            // Đơn giản nhất là toggle status của từng cái theo cách thủ công hoặc gọi API bulk nếu backend hỗ trợ toggle
            // Ở đây ta gọi @toggle-status cho AdminSeats xử lý logic toggle
            // Ở đây ta gọi @update-status cho AdminSeats xử lý logic
            // Cho bulk thì có thể mặc định đổi hết sang trạng thái ngược lại của cái đầu tiên 
            // Hoặc đơn giản là truyền mảng và để cha xử lý.
            emit('update-status', selectedSeats.value);
            selectedSeats.value = [];
        } catch (error) {
            ElMessage.error('Có lỗi khi thay đổi trạng thái');
        }
    }).catch(() => {});
};

const filteredSeats = computed(() => {
  return props.seats.filter(s => {
    const matchLoai = filterLoaiGhe.value === '' || s.idLoaiGhe === filterLoaiGhe.value;
    const matchSearch = !searchQuery.value || s.soGhe?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchLoai && matchSearch;
  });
});

const paginatedSeats = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredSeats.value.slice(start, end);
});

const resetFilter = () => {
  filterLoaiGhe.value = '';
  searchQuery.value = '';
  currentPage.value = 1;
  emit('reset-filter');
};

const getSeatTypeTag = (tenLoai) => {
  if (!tenLoai) return 'info';
  const l = tenLoai.toLowerCase();
  if (l.includes('vip')) return 'warning';
  if (l.includes('couple') || l.includes('sweetbox')) return 'danger';
  return 'info';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
</script>

<style scoped>
/* Scoped styles are mostly handled by the common layout component */
.text-orange-custom {
  color: #ff914d !important;
}
</style>

