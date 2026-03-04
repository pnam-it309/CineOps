
<script setup>
import { ref, onMounted, computed, reactive, watch } from 'vue';
import { Search, Plus, Edit, Delete, View, Filter, Message, User, Phone } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

import BaseTable from '@/components/common/BaseTable.vue';
import CCCDScanner from '@/components/common/CCCDScanner.vue'; // <-- Integrate Scanner
import { khachHangService } from '@/services/api/admin/khachHangService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import BaseModal from '@/components/common/BaseModal.vue';

// --- State ---
const customers = ref([]);
const selectedCustomers = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const filterTrangThai = ref(''); 
const currentPage = ref(1);
const pageSize = ref(10);

const customerColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'Mã KH', key: 'maKhachHang', width: '130px' },
  { label: 'Khách hàng', key: 'customer', minWidth: '350px' },
  { label: 'Email', key: 'email', minWidth: '350px' },
  { label: 'Số điện thoại', key: 'sdt', width: '220px' },
  { label: 'Giới tính', key: 'gender', width: '150px' },
  { label: 'Trạng thái', key: 'trangThai', width: '160px' },
];

// State điều khiển Modal
const dialogVisible = ref(false);
const selectedCustomer = ref(null);
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
    notification.error('Không thể tải danh sách khách hàng');
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
    hinhAnh: '',
    ghiChu: '',
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
    hinhAnh: row.hinhAnh || '',
    ghiChu: row.ghiChu || '',
    trangThai: row.trangThai
  });
  dialogVisible.value = true;
};

// Handle data returned from Scanner
const handleCCCDScanned = (data) => {
  if (data.name) form.tenKhachHang = data.name;
  if (data.dob) form.ngaySinh = data.dob;
};

// --- Logic Lưu Dữ Liệu ---
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await confirmDialog.update('khách hàng');
        } else {
          await confirmDialog.add('khách hàng');
        }
      } catch { return; }

      try {
        if (isEdit.value) {
          await khachHangService.update(currentId.value, form);
          notification.updateSuccess('khách hàng');
        } else {
          await khachHangService.create(form);
          notification.addSuccess('khách hàng');
        }
        dialogVisible.value = false;
        await fetchCustomers();
      } catch (error) {
        const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra';
        notification.error(errorMsg);
      }
    }
  });
};

// --- Logic Xóa ---
const handleDelete = (row) => {
  const isInactive = row.trangThai === 0;
  const newStatus = isInactive ? 1 : 0;
  const label = isInactive ? 'kích hoạt' : 'vô hiệu hóa';
  
  confirmDialog.custom(
    `Thay đổi trạng thái khách hàng <b>${row.tenKhachHang}</b> thành <b>${label}</b>?`,
    'Cập nhật trạng thái',
    'Xác nhận'
  ).then(async () => {
    try {
      await khachHangService.update(row.id, { ...row, trangThai: newStatus });
      notification.success(`Đã ${label} khách hàng thành công`);
      fetchCustomers();
    } catch (error) {
      notification.error('Cập nhật thất bại');
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
  confirmDialog.custom(
    `Thay đổi trạng thái cho <b>${selectedIds.value.length}</b> khách hàng đã chọn?`,
    'Cập nhật hàng loạt',
    'Đồng ý'
  ).then(async () => {
    try {
      await Promise.all(selectedCustomers.value.map(item => {
        const newStatus = item.trangThai === 1 ? 0 : 1;
        return khachHangService.update(item.id, { ...item, trangThai: newStatus });
      }));
      notification.success(`Đã cập nhật trạng thái cho ${selectedIds.value.length} khách hàng`);
      selectedCustomers.value = [];
      fetchCustomers();
    } catch (error) {
      notification.error('Có lỗi khi cập nhật hàng loạt');
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
        title="Quản lý khách hàng"
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
        <el-button v-if="selectedIds.length" type="warning" plain round :icon="Refresh" @click="handleBulkDelete">
          Đổi trạng thái {{ selectedIds.length }} khách hàng
        </el-button>
      </template>


  
      <template #filters>
        <div class="filter-item search-input-wrapper">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-input v-model="searchQuery" placeholder="Tên, email, SĐT..." :prefix-icon="Search" clearable @input="fetchCustomers" />
        </div>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="filterTrangThai" placeholder="Trạng thái" clearable @change="fetchCustomers">
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
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-maKhachHang="{ row }">
            <span class="text-secondary small">{{ row.maKhachHang }}</span>
          </template>

          <template #cell-customer="{ row }">
            <div class="fw-bold text-dark small">{{ row.tenKhachHang }}</div>
          </template>

          <template #cell-email="{ row }">
            <div class="text-dark small"><i class="bi bi-envelope me-2 text-primary"></i>{{ row.email || '—' }}</div>
          </template>

          <template #cell-sdt="{ row }">
            <div class="text-secondary small"><i class="bi bi-telephone me-2"></i>{{ row.sdt || '—' }}</div>
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

              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)">
                  <i class="bi bi-pencil"></i>
                </button>
              </el-tooltip>
              <el-tooltip :content="row.trangThai === 1 ? 'Vô hiệu hóa' : 'Kích hoạt'" placement="top">
                <button class="btn-action-icon btn-action-refresh" @click="handleDelete(row)">
                  <i class="bi bi-arrow-repeat"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>
  


    <BaseModal
      v-model="dialogVisible"
      :title="isEdit ? 'Cập nhật khách hàng' : 'Thêm khách hàng mới'"
      :icon="isEdit ? 'bi bi-person-gear' : 'bi bi-person-plus'"
      width="650px"
      confirmText="XÁC NHẬN"
      @confirm="submitForm"
    >
      <template #header-extra v-if="!isEdit">
        <CCCDScanner @scanned="handleCCCDScanned" />
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form">
        <el-form-item label="Tên khách hàng" prop="tenKhachHang">
          <el-input v-model="form.tenKhachHang" placeholder="Nguyễn Văn A" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="example@gmail.com" :prefix-icon="Message" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Số điện thoại" prop="sdt">
              <el-input v-model="form.sdt" placeholder="09xxx" :prefix-icon="Phone" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày sinh">
              <el-date-picker v-model="form.ngaySinh" type="date" value-format="YYYY-MM-DD" placeholder="Chọn ngày sinh" style="width: 100%" />
            </el-form-item>
          </div>
        </div>
        
        <el-form-item label="Hình ảnh URL">
          <el-input v-model="form.hinhAnh" placeholder="https://..." :prefix-icon="View" />
        </el-form-item>
        
        <el-form-item label="Ghi chú">
          <el-input v-model="form.ghiChu" type="textarea" :rows="2" placeholder="Nhập ghi chú khách hàng..." />
        </el-form-item>

        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Giới tính">
              <el-radio-group v-model="form.gioiTinh">
                <el-radio :value="1">Nam</el-radio>
                <el-radio :value="0">Nữ</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="col-6" v-if="isEdit">
            <el-form-item label="Trạng thái">
              <el-radio-group v-model="form.trangThai">
                <el-radio :value="1">Hoạt động</el-radio>
                <el-radio :value="0">Ngừng</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>