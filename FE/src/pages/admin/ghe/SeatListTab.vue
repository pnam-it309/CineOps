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
      <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
        Xóa {{ selectedIds.length }} ghế
      </el-button>
    </template>
    <!-- Stats Slot -->
    <template #stats>
      <div class="col-md-3">
        <StatCard 
          label="Tổng số ghế" 
          :value="filteredSeats.length" 
          icon="bi bi-grid-3x3-gap-fill"
          type="dark"
        />
      </div>
      <div class="col-md-3">
        <StatCard 
          label="Đang hoạt động" 
          :value="filteredSeats.filter(s => s.trangThai === 1).length" 
          icon="bi bi-check-circle-fill"
          type="success"
        />
      </div>
      <div class="col-md-3">
        <StatCard 
          label="Đang bảo trì" 
          :value="filteredSeats.filter(s => s.trangThai === 0).length" 
          icon="bi bi-exclamation-triangle-fill"
          type="warning"
        />
      </div>
      <div class="col-md-3">
        <StatCard 
          label="Loại ghế" 
          :value="loaiGheList.length" 
          icon="bi bi-tags-fill"
          type="primary"
        />
      </div>
    </template>

    <!-- Filters Slot -->
    <template #filters>
      <div class="filter-item search-input-wrapper">
        <el-input 
          v-model="searchQuery" 
          placeholder="Tìm kiếm số ghế..." 
          :prefix-icon="Search"
          size="default" 
          clearable 
        />
      </div>

      <div class="filter-item">
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
        @delete="row => $emit('delete-seat', row)"
      >
        <template #cell-index="{ index }">
          <span class="text-secondary small">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
        </template>

        <template #cell-soGhe="{ row }">
          <span class="badge bg-dark text-white fw-bold px-2 py-1 rounded-2" style="font-size: 11px;">{{ row.soGhe }}</span>
        </template>

        <template #cell-viTri="{ row }">
          <span class="text-dark small">Hàng <b>{{ row.soHang }}</b> - Cột <b>{{ row.soCot }}</b></span>
        </template>

        <template #cell-loaiGhe="{ row }">
          <el-tag :type="getSeatTypeTag(row.tenLoaiGhe)" round size="small" effect="plain">{{ row.tenLoaiGhe }}</el-tag>
        </template>

        <template #cell-phuPhi="{ row }">
          <span class="text-primary fw-bold small">{{ row.phuPhi > 0 ? formatCurrency(row.phuPhi) : '—' }}</span>
        </template>

        <template #cell-trangThai="{ row }">
          <el-tag :type="row.trangThai === 1 ? 'success' : 'warning'" round size="small">
            {{ row.trangThai === 1 ? 'Hoạt động' : 'Bảo trì' }}
          </el-tag>
        </template>
      </BaseTable>
    </template>
  </AdminTableLayout>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Search, Monitor, PriceTag, Delete } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import { gheService } from '@/services/api/admin/gheService';

const seatColumns = [
  { label: 'STT', key: 'index', width: '60px' },
  { label: 'SỐ GHẾ', key: 'soGhe', width: '100px' },
  { label: 'VỊ TRÍ', key: 'viTri', width: '150px' },
  { label: 'PHÒNG', key: 'tenPhongChieu', minWidth: '150px' },
  { label: 'LOẠI GHẾ', key: 'loaiGhe', width: '140px' },
  { label: 'PHỤ PHÍ', key: 'phuPhi', width: '120px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '120px' },
];

const props = defineProps({
  seats: Array,
  loaiGheList: Array,
  phongChieuList: Array,
  selectedRoom: [Number, String],
  loading: Boolean
});

const emit = defineEmits(['update:selectedRoom', 'fetch-seats', 'open-dialog', 'delete-seat', 'reset-filter']);

const filterLoaiGhe = ref('');
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(5);
const selectedSeats = ref([]);
const selectedIds = computed(() => selectedSeats.value.map(item => item.id));

const handleBulkDelete = () => {
    ElMessageBox.confirm(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> ghế đã chọn?`,
        'Xóa hàng loạt',
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy',
            type: 'warning'
        }
    ).then(async () => {
        try {
            // Ở đây Backend chưa có endpoint xóa mảng, ta tạm thời xóa từng cái
            // Hoặc nếu backend đã hỗ trợ, hãy thay thế bằng endpoint đó
            await Promise.all(selectedIds.value.map(id => gheService.deleteSeat(id)));
            ElMessage.success(`Đã xóa ${selectedIds.value.length} ghế`);
            selectedSeats.value = [];
            emit('fetch-seats');
        } catch (error) {
            ElMessage.error('Có lỗi khi xóa hàng loạt');
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
