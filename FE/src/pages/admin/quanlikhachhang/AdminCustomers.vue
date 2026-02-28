
<script setup>
import { ref, onMounted, computed, reactive, watch } from 'vue';
import { Search, Plus, Edit, Delete, View, Filter } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import { khachHangService } from '@/services/api/admin/khachHangService';

// --- State ---
const customers = ref([]);
const selectedCustomers = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const filterTrangThai = ref(''); 
const currentPage = ref(1);
const pageSize = ref(10);

const customerColumns = [
  { label: 'KHÁCH HÀNG', key: 'customer', minWidth: '220px' },
  { label: 'LIÊN HỆ', key: 'contact', minWidth: '200px' },
  { label: 'GIỚI TÍNH', key: 'gender', width: '120px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '130px' },
];

// State điều khiển Modal
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentId = ref(null);
const formRef = ref(null);

const form = reactive({
  tenKhachHang: '',
  email: '',
  sdt: '',
  gioiTinh: 1,
  ngaySinh: '',
  trangThai: 1
});

// Rules validation cơ bản cho form
const rules = {
  tenKhachHang: [{ required: true, message: 'Vui lòng nhập tên khách hàng', trigger: 'blur' }],
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không đúng định dạng', trigger: 'blur' }
  ],
  sdt: [{ required: true, message: 'Vui lòng nhập số điện thoại', trigger: 'blur' }]
};

// --- Logic tải dữ liệu ---
const fetchCustomers = async () => {
  loading.value = true;
  try {
    // QUAN TRỌNG: Truyền cả searchQuery và filterTrangThai để Backend lọc chính xác
    const res = await khachHangService.getAll(searchQuery.value, filterTrangThai.value);
    if (res.data && res.data.success) {
      customers.value = res.data.data;
    } else {
      customers.value = Array.isArray(res.data) ? res.data : (res.data.data || []);
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách khách hàng');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchCustomers);

// --- Logic xử lý Modal (Thêm & Sửa) ---
const handleAdd = () => {
  isEdit.value = false;
  currentId.value = null;
  Object.assign(form, {
    tenKhachHang: '',
    email: '',
    sdt: '',
    gioiTinh: 1,
    ngaySinh: '',
    trangThai: 1
  });
  dialogVisible.value = true;
  if (formRef.value) formRef.value.clearValidate();
};

const handleEdit = (row) => {
  isEdit.value = true;
  currentId.value = row.id;
  Object.assign(form, {
    tenKhachHang: row.tenKhachHang,
    email: row.email,
    sdt: row.sdt,
    gioiTinh: row.gioiTinh,
    ngaySinh: row.ngaySinh,
    trangThai: row.trangThai
  });
  dialogVisible.value = true;
};

// --- Logic Lưu Dữ Liệu ---
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await khachHangService.update(currentId.value, form);
          ElMessage.success('Cập nhật khách hàng thành công');
        } else {
          await khachHangService.create(form);
          ElMessage.success('Thêm khách hàng thành công');
        }
        dialogVisible.value = false;
        await fetchCustomers();
      } catch (error) {
        const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra';
        ElMessage.error(errorMsg);
      }
    }
  });
};

// --- Logic Xóa ---
const handleDelete = (row) => {
  ElMessageBox.confirm(
      `Bạn có chắc chắn muốn xóa khách hàng "${row.tenKhachHang}"?`,
      'Cảnh báo',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
  ).then(async () => {
    try {
      await khachHangService.delete(row.id);
      ElMessage.success('Xóa khách hàng thành công');
      fetchCustomers();
    } catch (error) {
      ElMessage.error('Lỗi khi xóa khách hàng');
    }
  }).catch(() => {});
};

// --- Các hàm hỗ trợ khác ---
const resetFilter = () => {
  searchQuery.value = '';
  filterTrangThai.value = '';
  fetchCustomers();
};

const selectedIds = computed(() => selectedCustomers.value.map(item => item.id));

const handleBulkDelete = () => {
  ElMessageBox.confirm(
    `Xác nhận xóa <b>${selectedIds.value.length}</b> khách hàng đã chọn?`,
    'Xóa hàng loạt',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: 'Đồng ý',
      cancelButtonText: 'Hủy',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await Promise.all(selectedIds.value.map(id => khachHangService.delete(id)));
      ElMessage.success(`Đã xóa ${selectedIds.value.length} khách hàng`);
      selectedCustomers.value = [];
      fetchCustomers();
    } catch (error) {
      ElMessage.error('Có lỗi khi xóa hàng loạt');
    }
  }).catch(() => { });
};

const paginatedCustomers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return customers.value.slice(start, start + pageSize.value);
});

const getGenderText = (gender) => gender === 1 ? 'Nam' : (gender === 0 ? 'Nữ' : 'Khác');
const getStatusLabel = (status) => status === 1 ? 'Hoạt động' : 'Ngừng hoạt động';

watch([currentPage, pageSize], fetchCustomers);
</script>

<template>
  <div class="admin-customers-page">
    <AdminTableLayout
        title="Quản Lý Khách Hàng"
        titleIcon="bi bi-people-fill"
        addButtonLabel="Thêm khách hàng"
        :data="customers"
        :loading="loading"
        :total="customers.length"
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        @add-click="handleAdd"
        @reset-filter="resetFilter"
    >
      <template #header-actions-left>
        <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
          Xóa {{ selectedIds.length }} khách hàng
        </el-button>
      </template>

      <template #stats>
        <div class="col-md-3">
          <StatCard label="Tổng Khách Hàng" :value="customers.length" icon="bi bi-people" type="primary" />
        </div>
        <div class="col-md-3">
          <StatCard label="Đang Hoạt Động" :value="customers.filter(c => c.trangThai === 1).length" icon="bi bi-person-check" type="success" />
        </div>
        <div class="col-md-3">
          <StatCard label="Thành Viên Mới" value="12" icon="bi bi-person-plus" type="warning" subText="Trong tháng này" />
        </div>
        <div class="col-md-3">
          <StatCard label="Ngừng Hoạt Động" :value="customers.filter(c => c.trangThai === 0).length" icon="bi bi-person-x" type="danger" />
        </div>
      </template>
  
      <template #filters>
        <div class="filter-item flex-grow-1 search-input-wrapper" style="max-width: 400px;">
          <span class="filter-label text-dark small fw-bold mb-1 d-block">Tìm kiếm</span>
          <el-input v-model="searchQuery" placeholder="Tên, email, SĐT..." :prefix-icon="Search" clearable @input="fetchCustomers" />
        </div>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block">Trạng thái</span>
          <el-select v-model="filterTrangThai" placeholder="Trạng thái" style="width: 170px;" clearable @change="fetchCustomers">
            <template #prefix><el-icon><Filter /></el-icon></template>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>
      </template>
  
      <template #content>
        <BaseTable
          :data="paginatedCustomers"
          :columns="customerColumns"
          :loading="loading"
          :total="customers.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          v-model:selection="selectedCustomers"
          :hide-pagination="true"
          @edit="handleEdit"
          @delete="handleDelete"
        >
          <template #cell-customer="{ row }">
            <div class="d-flex align-items-center gap-2 text-start">
              <el-avatar :size="32" class="bg-primary shadow-sm border border-white">{{ row.tenKhachHang?.charAt(0) }}</el-avatar>
              <div>
                <div class="fw-bold text-dark small">{{ row.tenKhachHang }}</div>
                <div class="text-secondary" style="font-size: 11px;">{{ row.maKhachHang }}</div>
              </div>
            </div>
          </template>

          <template #cell-contact="{ row }">
            <div class="small text-start">
              <div class="text-dark"><i class="bi bi-envelope me-2 text-primary"></i>{{ row.email || '—' }}</div>
              <div class="text-secondary"><i class="bi bi-telephone me-2"></i>{{ row.sdt || '—' }}</div>
            </div>
          </template>

          <template #cell-gender="{ row }">
            <el-tag :type="row.gioiTinh === 1 ? 'primary' : 'danger'" size="small" effect="plain" class="rounded-pill">
              {{ getGenderText(row.gioiTinh) }}
            </el-tag>
          </template>

          <template #cell-trangThai="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" size="small" round>
              {{ getStatusLabel(row.trangThai) }}
            </el-tag>
          </template>

          <template #actions="{ row }">
            <div class="d-flex gap-1 justify-content-center">
              <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)" title="Chỉnh sửa">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)" title="Xóa">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>
  
    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Cập nhật khách hàng' : 'Thêm khách hàng mới'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="Tên khách hàng" prop="tenKhachHang">
          <el-input v-model="form.tenKhachHang" placeholder="Nguyễn Văn A" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="example@gmail.com" />
        </el-form-item>
        <el-form-item label="Số điện thoại" prop="sdt">
          <el-input v-model="form.sdt" placeholder="09xxx" />
        </el-form-item>
        <el-form-item label="Ngày sinh">
          <el-date-picker v-model="form.ngaySinh" type="date" value-format="YYYY-MM-DD" placeholder="Chọn ngày sinh" style="width: 100%" />
        </el-form-item>
        <div class="row">
          <div class="col-6">
            <el-form-item label="Giới tính">
              <el-radio-group v-model="form.gioiTinh">
                <el-radio :label="1">Nam</el-radio>
                <el-radio :label="0">Nữ</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="col-6" v-if="isEdit">
            <el-form-item label="Trạng thái">
              <el-radio-group v-model="form.trangThai">
                <el-radio :label="1">Hoạt động</el-radio>
                <el-radio :label="0">Ngừng</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false" class="btn-premium-secondary">HỦY BỎ</el-button>
          <el-button @click="submitForm" class="btn-premium-primary">XÁC NHẬN</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>