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
    @add-click="$emit('open-dialog')"
    @reset-filter="resetFilter"
    @selection-change="handleSelectionChange"
  >
    <!-- Header Actions Left Slot -->
    <template #header-actions-left>
      <el-button 
        v-if="selectedIds.length" 
        type="danger" 
        plain 
        size="default" 
        :icon="Delete" 
        @click="handleBulkDelete"
      >
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

    <!-- Table Columns Slot -->
    <template #columns>
      <el-table-column type="index" label="STT" width="60" align="center" fixed="left" />
      <el-table-column label="Số ghế" width="100" align="center">
        <template #default="{ row }">
          <span class="badge bg-dark text-white fw-bold px-2 py-1 rounded-2">{{ row.soGhe }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Vị trí" width="150" align="center">
        <template #default="{ row }">
          <span class="text-dark small">Hàng <b>{{ row.soHang }}</b> - Cột <b>{{ row.soCot }}</b></span>
        </template>
      </el-table-column>
      <el-table-column label="Phòng" prop="tenPhongChieu" min-width="150" />
      <el-table-column label="Loại ghế" width="140">
        <template #default="{ row }">
          <el-tag :type="getSeatTypeTag(row.tenLoaiGhe)" round size="small" effect="plain">{{ row.tenLoaiGhe }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Phụ phí" width="120" align="right">
        <template #default="{ row }">
          <span class="text-primary fw-bold small">{{ row.phuPhi > 0 ? formatCurrency(row.phuPhi) : '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Trạng thái" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="row.trangThai === 1 ? 'success' : 'warning'" round size="small">
            {{ row.trangThai === 1 ? 'Hoạt động' : 'Bảo trì' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Thao tác" width="110" align="center" fixed="right">
        <template #default="{ row }">
          <div class="d-flex gap-1 justify-content-center">
            <el-tooltip content="Chỉnh sửa" placement="top">
              <button class="btn-action-icon btn-action-edit" @click="$emit('open-dialog', row)">
                <i class="bi bi-pencil"></i>
              </button>
            </el-tooltip>
            <el-tooltip content="Xóa" placement="top">
              <button class="btn-action-icon btn-action-delete" @click="$emit('delete-seat', row)">
                <i class="bi bi-trash"></i>
              </button>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </template>
  </AdminTableLayout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Search, Monitor, PriceTag, Delete } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import { gheService } from '@/services/api/admin/gheService';

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
const selectedIds = ref([]);

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id);
};

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
            selectedIds.value = [];
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
