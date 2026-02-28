<script setup>
import { ref, onMounted, computed } from 'vue';
import { Search, Plus, Edit, Delete, View, Filter, User } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import { khachHangService } from '@/services/api/admin/khachHangService';

// State
const customers = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const filterTrangThai = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const selectedIds = ref([]);

// Fetch data
const fetchCustomers = async () => {
  loading.value = true;
  try {
    const res = await khachHangService.getAll(searchQuery.value, filterTrangThai.value);
    if (res.data.success) {
      customers.value = res.data.data;
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách khách hàng');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchCustomers);

// Reset filter
const resetFilter = () => {
  searchQuery.value = '';
  filterTrangThai.value = '';
  fetchCustomers();
};

// Selection
const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id);
};

// Detail/Edit/Delete Actions
const handleView = (row) => {
  ElMessage.info(`Xem chi tiết khách hàng: ${row.tenKhachHang}`);
};

const handleEdit = (row) => {
  ElMessage.info(`Chỉnh sửa khách hàng: ${row.tenKhachHang}`);
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`Bạn có chắc chắn muốn xóa khách hàng "${row.tenKhachHang}"?`, 'Cảnh báo', {
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await khachHangService.delete(row.id);
      ElMessage.success('Đã xóa khách hàng thành công');
      fetchCustomers();
    } catch (error) {
      ElMessage.error('Lỗi khi xóa khách hàng');
    }
  });
};

const handleBulkDelete = () => {
  ElMessageBox.confirm(`Bạn có chắc muốn xóa ${selectedIds.value.length} khách hàng đã chọn?`, 'Cảnh báo', {
    type: 'warning'
  }).then(() => {
    ElMessage.warning('Chức năng xóa nhiều đang phát triển');
  });
};

// Paginated data
const paginatedCustomers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return customers.value.slice(start, end);
});

// Helper for labels
const getGenderText = (gender) => {
  if (gender === 1) return 'Nam';
  if (gender === 0) return 'Nữ';
  return 'Khác';
};

const getStatusType = (status) => {
  return status === 1 ? 'success' : 'info';
};
</script>

<template>
  <AdminTableLayout
    title="Quản Lý Khách Hàng"
    titleIcon="bi bi-people-fill"
    addButtonLabel="Thêm khách hàng"
    :data="paginatedCustomers"
    :loading="loading"
    :total="customers.length"
    v-model:currentPage="currentPage"
    v-model:pageSize="pageSize"
    @add-click="ElMessage.info('Thêm khách hàng đang phát triển')"
    @reset-filter="resetFilter"
    @selection-change="handleSelectionChange"
  >
    <!-- Header Actions Left -->
    <template #header-actions-left>
      <el-button 
        v-if="selectedIds.length" 
        type="danger" 
        plain 
        size="default" 
        :icon="Delete" 
        @click="handleBulkDelete"
      >
        Xóa {{ selectedIds.length }} khách hàng
      </el-button>
    </template>

    <!-- Stats -->
    <template #stats>
      <div class="col-md-3">
        <StatCard 
          label="Tổng Khách Hàng" 
          :value="customers.length" 
          icon="bi bi-people"
          type="primary"
        />
      </div>
      <div class="col-md-3">
        <StatCard 
          label="Đang Hoạt Động" 
          :value="customers.filter(c => c.trangThai === 1).length" 
          icon="bi bi-person-check"
          type="success"
        />
      </div>
      <div class="col-md-3">
        <StatCard 
          label="Thành Viên Mới" 
          value="12" 
          icon="bi bi-person-plus"
          type="warning"
          subText="Trong tháng này"
        />
      </div>
      <div class="col-md-3">
        <StatCard 
          label="Ngừng Hoạt Động" 
          :value="customers.filter(c => c.trangThai === 0).length" 
          icon="bi bi-person-x"
          type="danger"
        />
      </div>
    </template>

    <!-- Filters -->
    <template #filters>
      <div class="filter-item search-input-wrapper">
        <el-input
          v-model="searchQuery"
          placeholder="Tìm theo tên, email, SĐT..."
          :prefix-icon="Search"
          size="default"
          clearable
          @input="fetchCustomers"
        />
      </div>
      <div class="filter-item">
        <el-select v-model="filterTrangThai" placeholder="Trạng thái" style="width: 170px;" clearable @change="fetchCustomers">
          <template #prefix><el-icon><Filter /></el-icon></template>
          <el-option label="Tất cả trạng thái" value="" />
          <el-option label="Hoạt động" :value="1" />
          <el-option label="Ngừng hoạt động" :value="0" />
        </el-select>
      </div>
    </template>

    <!-- Table Columns -->
    <template #columns>
      <el-table-column type="index" label="STT" width="60" align="center" fixed="left" />
      
      <el-table-column label="Khách hàng" min-width="220">
        <template #default="{ row }">
          <div class="d-flex align-items-center gap-2">
            <el-avatar :size="32" :src="row.hinhAnh" class="bg-primary shadow-sm">
              {{ row.tenKhachHang?.charAt(0) }}
            </el-avatar>
            <div>
              <div class="fw-bold text-dark small">{{ row.tenKhachHang }}</div>
              <div class="text-secondary" style="font-size: 11px;">{{ row.maKhachHang }}</div>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="Liên hệ" min-width="200">
        <template #default="{ row }">
          <div class="small">
            <div class="text-dark"><i class="bi bi-envelope me-1 text-primary"></i>{{ row.email || '—' }}</div>
            <div class="text-secondary"><i class="bi bi-telephone me-1"></i>{{ row.sdt || '—' }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="Giới tính" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.gioiTinh === 1 ? '' : 'danger'" effect="plain" size="small">
            {{ getGenderText(row.gioiTinh) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="Hạng" width="130" align="center">
        <template #default="{ row }">
          <el-tag type="warning" effect="dark" size="small">
            {{ row.tenLoaiKhachHang || 'Thành viên' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="Trạng thái" width="140" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.trangThai)" effect="light">
            {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="Hành động" width="120" fixed="right">
        <template #default="{ row }">
          <div class="d-flex gap-1 justify-content-center">
            <el-tooltip content="Xem chi tiết" placement="top">
              <button class="btn-action-icon btn-action-view" @click="handleView(row)">
                <i class="bi bi-eye"></i>
              </button>
            </el-tooltip>
            <el-tooltip content="Chỉnh sửa" placement="top">
              <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)">
                <i class="bi bi-pencil"></i>
              </button>
            </el-tooltip>
            <el-tooltip content="Xóa" placement="top">
              <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)">
                <i class="bi bi-trash"></i>
              </button>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </template>
  </AdminTableLayout>
</template>

<style scoped>
.admin-customers {
  height: calc(100vh - 84px);
}
</style>
