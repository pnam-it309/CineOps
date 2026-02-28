<template>
  <div class="admin-vouchers-page">
    <AdminTableLayout
      title="Quản Lý Phiếu Giảm Giá"
      titleIcon="bi bi-ticket-perforated-fill"
      addButtonLabel="Thêm Voucher"
      :data="vouchers"
      :loading="loading"
      :total="total"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog()"
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
          Xóa {{ selectedIds.length }} voucher
        </el-button>
      </template>

      <!-- Stats Slot -->
      <template #stats>
        <div class="col-md-3">
          <StatCard 
            label="Tổng số phiếu" 
            :value="total" 
            icon="bi bi-ticket-perforated-fill"
            type="primary"
          />
        </div>
        <div class="col-md-3">
          <StatCard 
            label="Đang hoạt động" 
            :value="stats.active || 0" 
            icon="bi bi-check-circle-fill"
            type="success"
          />
        </div>
        <div class="col-md-3">
          <StatCard 
            label="Hết hạn/Hết lượt" 
            :value="stats.expired || 0" 
            icon="bi bi-exclamation-triangle-fill"
            type="danger"
          />
        </div>
        <div class="col-md-3">
          <StatCard 
            label="Dừng hoạt động" 
            :value="stats.inactive || 0" 
            icon="bi bi-pause-circle-fill"
            type="warning"
          />
        </div>
      </template>

      <!-- Filters Slot -->
      <template #filters>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block">Trạng thái</span>
          <el-select v-model="filterStatus" placeholder="Tất cả" style="width: 150px;" @change="fetchVouchers">
            <el-option label="Tất cả" value="" />
            <el-option label="Đang hoạt động" :value="1" />
            <el-option label="Tạm ngưng" :value="0" />
            <el-option label="Đã kết thúc" :value="2" />
          </el-select>
        </div>

        <div class="filter-item flex-grow-1 search-input-wrapper">
          <span class="filter-label text-dark small fw-bold mb-1 d-block">Tìm kiếm</span>
          <el-input 
            v-model="searchQuery" 
            placeholder="Nhập mã hoặc tên phiếu..." 
            style="width: 100%;" 
            clearable 
            @input="handleSearch"
          />
        </div>
      </template>

      <!-- Table Columns Slot -->
      <template #columns>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="Thông tin phiếu" min-width="250">
          <template #default="{ row }">
            <div>
              <div class="text-dark fw-bold mb-0">
                <span class="badge bg-light text-primary border border-primary-subtle me-2">{{ row.maPhieuGiamGia }}</span>
                {{ row.tenPhieu }}
              </div>
              <div class="text-dark extra-small mt-1" style="font-size: 11px;">
                Đơn tối thiểu: <span class="fw-bold">{{ formatCurrency(row.giaTriHoaDonToiThieu) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Giảm giá" width="160" align="center">
          <template #default="{ row }">
            <div v-if="row.loaiPhieu === 1">
              <el-tag type="warning" effect="dark" round size="small">{{ row.phanTramGiamGia }}%</el-tag>
              <div class="text-dark extra-small mt-1" style="font-size: 10px;">Tối đa: {{ formatCurrency(row.giamToiDa) }}</div>
            </div>
            <div v-else>
              <el-tag type="danger" effect="dark" round size="small">-{{ formatCurrency(row.soTienGiam) }}</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Số lượng" width="120" align="center">
          <template #default="{ row }">
            <span class="text-dark fw-bold">{{ row.soLuong }}</span>
          </template>
        </el-table-column>

        <el-table-column label="Thời gian" width="180">
          <template #default="{ row }">
            <div class="small text-dark">
              <div><i class="bi bi-calendar-check me-1"></i>{{ formatDate(row.ngayBatDau) }}</div>
              <div><i class="bi bi-calendar-x me-1"></i>{{ formatDate(row.ngayKetThuc) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Trạng thái" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.trangThai)" round size="small">
              {{ getStatusLabel(row.trangThai) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Thao tác" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex gap-1 justify-content-center">
              <el-tooltip content="Chi tiết" placement="top">
                <button class="btn-action-icon btn-action-view" @click="handleView(row)">
                  <i class="bi bi-eye"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon btn-action-edit" @click="openDialog(row)">
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

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      width="680px"
      destroy-on-close
      class="premium-dialog"
    >
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i :class="editingId ? 'bi bi-pencil-square' : 'bi bi-ticket-perforated'"></i>
            </div>
            <div class="header-text">
              <h5 class="title">{{ editingId ? 'Cập nhật Voucher' : 'Phát hành Voucher mới' }}</h5>
              <p class="subtitle opacity-75">Tạo chương trình ưu đãi và mã giảm giá</p>
            </div>
          </div>
          <div class="premium-header-bg"></div>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form">
        <div class="row g-3">
          <div class="col-md-6">
            <el-form-item label="Mã Voucher" prop="maPhieuGiamGia">
              <el-input v-model="form.maPhieuGiamGia" placeholder="VD: TET2026" :disabled="!!editingId">
                 <template #prefix><i class="bi bi-tag me-1"></i></template>
              </el-input>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Tên chương trình" prop="tenPhieu">
              <el-input v-model="form.tenPhieu" placeholder="Nhập tên chương trình..." />
            </el-form-item>
          </div>

          <div class="col-md-4">
            <el-form-item label="Loại giảm giá" prop="loaiPhieu">
              <el-select v-model="form.loaiPhieu" class="w-100">
                <el-option label="Phần trăm (%)" :value="1" />
                <el-option label="Tiền mặt (đ)" :value="2" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-4" v-if="form.loaiPhieu === 1">
            <el-form-item label="Phần trăm giảm" prop="phanTramGiamGia">
              <el-input-number v-model="form.phanTramGiamGia" :min="1" :max="100" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-4" v-if="form.loaiPhieu === 1">
            <el-form-item label="Giảm tối đa" prop="giamToiDa">
              <el-input-number v-model="form.giamToiDa" :min="0" class="w-100" :step="1000" />
            </el-form-item>
          </div>
          <div class="col-md-8" v-if="form.loaiPhieu === 2">
            <el-form-item label="Số tiền giảm" prop="soTienGiam">
              <el-input-number v-model="form.soTienGiam" :min="0" class="w-100" :step="1000" />
            </el-form-item>
          </div>

          <div class="col-md-4">
            <el-form-item label="Đơn tối thiểu" prop="giaTriHoaDonToiThieu">
              <el-input-number v-model="form.giaTriHoaDonToiThieu" :min="0" class="w-100" :step="1000" />
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="Số lượng" prop="soLuong">
              <el-input-number v-model="form.soLuong" :min="1" class="w-100" />
            </el-form-item>
          </div>
           <div class="col-md-4">
            <el-form-item label="Trạng thái" prop="trangThai">
              <el-select v-model="form.trangThai" class="w-100">
                <el-option label="Hoạt động" :value="1" />
                <el-option label="Dừng" :value="0" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-md-6">
            <el-form-item label="Ngày bắt đầu" prop="ngayBatDau">
              <el-date-picker v-model="form.ngayBatDau" type="datetime" class="w-100" placeholder="Chọn thời gian" value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Ngày kết thúc" prop="ngayKetThuc">
              <el-date-picker v-model="form.ngayKetThuc" type="datetime" class="w-100" placeholder="Chọn thời gian" value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false" class="btn-premium-secondary">HỦY BỎ</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="saving" class="btn-premium-primary">
            {{ editingId ? 'CẬP NHẬT' : 'PHÁT HÀNH NGAY' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import { Search, Plus, Edit, Delete, View, Refresh } from '@element-plus/icons-vue';
import { voucherService } from '@/services/api/admin/voucherService';
import debounce from 'lodash/debounce';

const loading = ref(false);
const vouchers = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const filterStatus = ref('');
const searchQuery = ref('');
const selectedIds = ref([]);
const stats = ref({ active: 0, expired: 0, inactive: 0 });

const dialogVisible = ref(false);
const saving = ref(false);
const editingId = ref(null);
const formRef = ref(null);

const form = ref({
  maPhieuGiamGia: '',
  tenPhieu: '',
  loaiPhieu: 1,
  phanTramGiamGia: 0,
  soTienGiam: 0,
  giaTriHoaDonToiThieu: 0,
  giamToiDa: 0,
  ngayBatDau: '',
  ngayKetThuc: '',
  soLuong: 1,
  trangThai: 1
});

const rules = {
  maPhieuGiamGia: [{ required: true, message: 'Vui lòng nhập mã', trigger: 'blur' }],
  tenPhieu: [{ required: true, message: 'Vui lòng nhập tên', trigger: 'blur' }],
  ngayBatDau: [{ required: true, message: 'Vui lòng chọn ngày bắt đầu', trigger: 'change' }],
  ngayKetThuc: [{ required: true, message: 'Vui lòng chọn ngày kết thúc', trigger: 'change' }],
  soLuong: [{ required: true, message: 'Vui lòng nhập số lượng', trigger: 'blur' }]
};

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
    
    // Tạm thời tính stats từ dữ liệu trả về (nếu backend chưa trả về riêng)
    // Thực tế nên có endpoint stats riêng
    stats.value = {
        active: vouchers.value.filter(v => v.trangThai === 1).length,
        inactive: vouchers.value.filter(v => v.trangThai === 0).length,
        expired: vouchers.value.filter(v => v.trangThai === 2).length
    };
  } catch (e) {
    ElMessage.error('Không thể tải danh sách voucher');
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

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id);
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  if (row) {
    form.value = { ...row };
  } else {
    form.value = {
      maPhieuGiamGia: '',
      tenPhieu: '',
      loaiPhieu: 1,
      phanTramGiamGia: 0,
      soTienGiam: 0,
      giaTriHoaDonToiThieu: 0,
      giamToiDa: 0,
      ngayBatDau: '',
      ngayKetThuc: '',
      soLuong: 1,
      trangThai: 1
    };
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (editingId.value) {
        await voucherService.update(editingId.value, form.value);
        ElMessage.success('Cập nhật thành công');
      } else {
        await voucherService.create(form.value);
        ElMessage.success('Phát hành voucher thành công');
      }
      dialogVisible.value = false;
      fetchVouchers();
    } catch (e) {
      ElMessage.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`Xác nhận xóa voucher "${row.maPhieuGiamGia}"?`, 'Cảnh báo', {
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
    type: 'warning'
  }).then(async () => {
    try {
      await voucherService.delete(row.id);
      ElMessage.success('Đã xóa thành công');
      fetchVouchers();
    } catch (e) {
      ElMessage.error('Xóa thất bại');
    }
  });
};

const handleBulkDelete = () => {
    ElMessageBox.confirm(`Xóa ${selectedIds.value.length} voucher đã chọn?`, 'Cảnh báo', {
        type: 'warning'
    }).then(async () => {
        try {
            await Promise.all(selectedIds.value.map(id => voucherService.delete(id)));
            ElMessage.success('Xóa hàng loạt thành công');
            selectedIds.value = [];
            fetchVouchers();
        } catch (e) {
            ElMessage.error('Có lỗi khi xóa hàng loạt');
        }
    });
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

onMounted(() => {
  fetchVouchers();
});

watch([currentPage, pageSize], () => {
  fetchVouchers();
});
</script>

<style scoped>
.admin-voucher-container {
    height: calc(100vh - 84px);
}

.extra-small {
  font-size: 10px;
}
</style>
