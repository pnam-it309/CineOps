

<!--<script setup>-->
<!--import { ref, onMounted, computed, reactive } from 'vue';-->
<!--import { Search, Plus, Edit, Delete, View, Filter } from '@element-plus/icons-vue';-->
<!--import { ElMessage, ElMessageBox } from 'element-plus';-->
<!--import AdminTableLayout from '@/components/AdminTableLayout.vue';-->
<!--import StatCard from '@/components/common/StatCard.vue';-->
<!--import { khachHangService } from '@/services/api/admin/khachHangService';-->

<!--// -&#45;&#45; State -&#45;&#45;-->
<!--const customers = ref([]);-->
<!--const loading = ref(false);-->
<!--const searchQuery = ref('');-->
<!--const filterTrangThai = ref('');-->
<!--const currentPage = ref(1);-->
<!--const pageSize = ref(10);-->
<!--const selectedIds = ref([]);-->

<!--// State điều khiển Modal-->
<!--const dialogVisible = ref(false);-->
<!--const isEdit = ref(false); // Xác định đang thêm hay sửa-->
<!--const currentId = ref(null); // Lưu ID khi sửa-->
<!--const formRef = ref(null);-->

<!--const form = reactive({-->
<!--  tenKhachHang: '',-->
<!--  email: '',-->
<!--  sdt: '',-->
<!--  gioiTinh: 1,-->
<!--  ngaySinh: '',-->
<!--  trangThai: 1-->
<!--});-->

<!--// -&#45;&#45; Logic tải dữ liệu -&#45;&#45;-->
<!--const fetchCustomers = async () => {-->
<!--  loading.value = true;-->
<!--  try {-->
<!--    const res = await khachHangService.getAll(searchQuery.value);-->
<!--    if (res.data && res.data.success) {-->
<!--      customers.value = res.data.data;-->
<!--    } else {-->
<!--      customers.value = Array.isArray(res.data) ? res.data : (res.data.data || []);-->
<!--    }-->
<!--  } catch (error) {-->
<!--    ElMessage.error('Không thể tải danh sách khách hàng');-->
<!--  } finally {-->
<!--    loading.value = false;-->
<!--  }-->
<!--};-->

<!--onMounted(fetchCustomers);-->

<!--// -&#45;&#45; Logic xử lý Modal (Thêm & Sửa) -&#45;&#45;-->
<!--const handleAdd = () => {-->
<!--  isEdit.value = false;-->
<!--  currentId.value = null;-->
<!--  Object.assign(form, {-->
<!--    tenKhachHang: '',-->
<!--    email: '',-->
<!--    sdt: '',-->
<!--    gioiTinh: 1,-->
<!--    ngaySinh: '',-->
<!--    trangThai: 1-->
<!--  });-->
<!--  dialogVisible.value = true;-->
<!--};-->

<!--const handleEdit = (row) => {-->
<!--  isEdit.value = true;-->
<!--  currentId.value = row.id;-->
<!--  // Đổ dữ liệu từ hàng vào form-->
<!--  Object.assign(form, {-->
<!--    tenKhachHang: row.tenKhachHang,-->
<!--    email: row.email,-->
<!--    sdt: row.sdt,-->
<!--    gioiTinh: row.gioiTinh,-->
<!--    ngaySinh: row.ngaySinh,-->
<!--    trangThai: row.trangThai-->
<!--  });-->
<!--  dialogVisible.value = true;-->
<!--};-->

<!--// -&#45;&#45; Logic Lưu Dữ Liệu -&#45;&#45;-->
<!--const submitForm = async () => {-->
<!--  if (!formRef.value) return;-->
<!--  await formRef.value.validate(async (valid) => {-->
<!--    if (valid) {-->
<!--      try {-->
<!--        let res;-->
<!--        if (isEdit.value) {-->
<!--          // Gọi API Update (PUT)-->
<!--          res = await khachHangService.update(currentId.value, form);-->
<!--          ElMessage.success('Cập nhật khách hàng thành công');-->
<!--        } else {-->
<!--          // Gọi API Create (POST)-->
<!--          res = await khachHangService.create(form);-->
<!--          ElMessage.success('Thêm khách hàng thành công');-->
<!--        }-->

<!--        dialogVisible.value = false;-->
<!--        await fetchCustomers(); // Load lại bảng-->
<!--      } catch (error) {-->
<!--        const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra';-->
<!--        ElMessage.error(errorMsg);-->
<!--      }-->
<!--    }-->
<!--  });-->
<!--};-->

<!--// -&#45;&#45; Logic Xóa -&#45;&#45;-->
<!--const handleDelete = (row) => {-->
<!--  ElMessageBox.confirm(-->
<!--      `Bạn có chắc chắn muốn xóa khách hàng "${row.tenKhachHang}"?`,-->
<!--      'Cảnh báo',-->
<!--      {-->
<!--        confirmButtonText: 'Xóa',-->
<!--        cancelButtonText: 'Hủy',-->
<!--        type: 'warning',-->
<!--      }-->
<!--  ).then(async () => {-->
<!--    try {-->
<!--      await khachHangService.delete(row.id);-->
<!--      ElMessage.success('Xóa khách hàng thành công');-->
<!--      fetchCustomers();-->
<!--    } catch (error) {-->
<!--      ElMessage.error('Lỗi khi xóa khách hàng');-->
<!--    }-->
<!--  }).catch(() => {});-->
<!--};-->

<!--// -&#45;&#45; Các hàm hỗ trợ khác -&#45;&#45;-->
<!--const resetFilter = () => {-->
<!--  searchQuery.value = '';-->
<!--  filterTrangThai.value = '';-->
<!--  fetchCustomers();-->
<!--};-->

<!--const handleSelectionChange = (val) => {-->
<!--  selectedIds.value = val.map(item => item.id);-->
<!--};-->

<!--const paginatedCustomers = computed(() => {-->
<!--  const start = (currentPage.value - 1) * pageSize.value;-->
<!--  return customers.value.slice(start, start + pageSize.value);-->
<!--});-->

<!--const getGenderText = (gender) => gender === 1 ? 'Nam' : (gender === 0 ? 'Nữ' : 'Khác');-->
<!--const getStatusType = (status) => status === 1 ? 'success' : 'info';-->
<!--</script>-->

<!--<template>-->
<!--  <AdminTableLayout-->
<!--      title="Quản Lý Khách Hàng"-->
<!--      titleIcon="bi bi-people-fill"-->
<!--      addButtonLabel="Thêm khách hàng"-->
<!--      :data="paginatedCustomers"-->
<!--      :loading="loading"-->
<!--      :total="customers.length"-->
<!--      v-model:currentPage="currentPage"-->
<!--      v-model:pageSize="pageSize"-->
<!--      @add-click="handleAdd"-->
<!--      @reset-filter="resetFilter"-->
<!--      @selection-change="handleSelectionChange"-->
<!--  >-->
<!--    <template #stats>-->
<!--      <div class="col-md-3">-->
<!--        <StatCard label="Tổng Khách Hàng" :value="customers.length" icon="bi bi-people" type="primary" />-->
<!--      </div>-->
<!--      <div class="col-md-3">-->
<!--        <StatCard label="Đang Hoạt Động" :value="customers.filter(c => c.trangThai === 1).length" icon="bi bi-person-check" type="success" />-->
<!--      </div>-->
<!--      <div class="col-md-3">-->
<!--        <StatCard label="Thành Viên Mới" value="12" icon="bi bi-person-plus" type="warning" subText="Trong tháng này" />-->
<!--      </div>-->
<!--      <div class="col-md-3">-->
<!--        <StatCard label="Ngừng Hoạt Động" :value="customers.filter(c => c.trangThai === 0).length" icon="bi bi-person-x" type="danger" />-->
<!--      </div>-->
<!--    </template>-->

<!--    <template #filters>-->
<!--      <div class="filter-item search-input-wrapper">-->
<!--        <el-input v-model="searchQuery" placeholder="Tìm theo tên, email, SĐT..." :prefix-icon="Search" clearable @input="fetchCustomers" />-->
<!--      </div>-->
<!--      <div class="filter-item">-->
<!--        <el-select v-model="filterTrangThai" placeholder="Trạng thái" style="width: 170px;" clearable @change="fetchCustomers">-->
<!--          <template #prefix><el-icon><Filter /></el-icon></template>-->
<!--          <el-option label="Tất cả trạng thái" value="" />-->
<!--          <el-option label="Hoạt động" :value="1" />-->
<!--          <el-option label="Ngừng hoạt động" :value="0" />-->
<!--        </el-select>-->
<!--      </div>-->
<!--    </template>-->

<!--    <template #columns>-->
<!--      <el-table-column type="index" label="STT" width="60" align="center" fixed="left" />-->
<!--      <el-table-column label="Khách hàng" min-width="220">-->
<!--        <template #default="{ row }">-->
<!--          <div class="d-flex align-items-center gap-2">-->
<!--            <el-avatar :size="32" class="bg-primary shadow-sm">{{ row.tenKhachHang?.charAt(0) }}</el-avatar>-->
<!--            <div>-->
<!--              <div class="fw-bold text-dark small">{{ row.tenKhachHang }}</div>-->
<!--              <div class="text-secondary" style="font-size: 11px;">{{ row.maKhachHang }}</div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="Liên hệ" min-width="200">-->
<!--        <template #default="{ row }">-->
<!--          <div class="small">-->
<!--            <div class="text-dark"><i class="bi bi-envelope me-1 text-primary"></i>{{ row.email || '—' }}</div>-->
<!--            <div class="text-secondary"><i class="bi bi-telephone me-1"></i>{{ row.sdt || '—' }}</div>-->
<!--          </div>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="Trạng thái" width="140" align="center">-->
<!--        <template #default="{ row }">-->
<!--          <el-tag :type="getStatusType(row.trangThai)" effect="light">-->
<!--            {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}-->
<!--          </el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="Hành động" width="120" fixed="right">-->
<!--        <template #default="{ row }">-->
<!--          <div class="d-flex gap-2">-->
<!--            <el-tooltip content="Chỉnh sửa" placement="top">-->
<!--              <el-button @click="handleEdit(row)" :icon="Edit" circle size="small" type="primary" plain />-->
<!--            </el-tooltip>-->
<!--            <el-tooltip content="Xóa" placement="top">-->
<!--              <el-button @click="handleDelete(row)" :icon="Delete" circle size="small" type="danger" plain />-->
<!--            </el-tooltip>-->
<!--          </div>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--    </template>-->
<!--  </AdminTableLayout>-->

<!--  <el-dialog v-model="dialogVisible" :title="isEdit ? 'Cập nhật khách hàng' : 'Thêm khách hàng mới'" width="500px" destroy-on-close>-->
<!--    <el-form :model="form" ref="formRef" label-position="top">-->
<!--      <el-form-item label="Tên khách hàng" required prop="tenKhachHang">-->
<!--        <el-input v-model="form.tenKhachHang" placeholder="Nguyễn Văn A" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="Email" required prop="email">-->
<!--        <el-input v-model="form.email" placeholder="example@gmail.com" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="Số điện thoại" required prop="sdt">-->
<!--        <el-input v-model="form.sdt" placeholder="09xxx" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="Ngày sinh">-->
<!--        <el-date-picker v-model="form.ngaySinh" type="date" value-format="YYYY-MM-DD" style="width: 100%" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="Giới tính">-->
<!--        <el-radio-group v-model="form.gioiTinh">-->
<!--          <el-radio :label="1">Nam</el-radio>-->
<!--          <el-radio :label="0">Nữ</el-radio>-->
<!--        </el-radio-group>-->
<!--      </el-form-item>-->
<!--      <el-form-item v-if="isEdit" label="Trạng thái">-->
<!--        <el-radio-group v-model="form.trangThai">-->
<!--          <el-radio :label="1">Hoạt động</el-radio>-->
<!--          <el-radio :label="0">Ngừng hoạt động</el-radio>-->
<!--        </el-radio-group>-->
<!--      </el-form-item>-->
<!--    </el-form>-->
<!--    <template #footer>-->
<!--      <el-button @click="dialogVisible = false">Hủy</el-button>-->
<!--      <el-button type="primary" @click="submitForm">Xác nhận</el-button>-->
<!--    </template>-->
<!--  </el-dialog>-->
<!--</template>-->
<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { Search, Plus, Edit, Delete, View, Filter } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import { khachHangService } from '@/services/api/admin/khachHangService';

// --- State ---
const customers = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const filterTrangThai = ref(''); // Lưu giá trị: '', 1, hoặc 0
const currentPage = ref(1);
const pageSize = ref(10);
const selectedIds = ref([]);

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

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id);
};

const paginatedCustomers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return customers.value.slice(start, start + pageSize.value);
});

const getGenderText = (gender) => gender === 1 ? 'Nam' : (gender === 0 ? 'Nữ' : 'Khác');
const getStatusType = (status) => status === 1 ? 'success' : 'info';
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
      @add-click="handleAdd"
      @reset-filter="resetFilter"
      @selection-change="handleSelectionChange"
  >
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
      <div class="filter-item search-input-wrapper">
        <el-input v-model="searchQuery" placeholder="Tìm theo tên, email, SĐT..." :prefix-icon="Search" clearable @input="fetchCustomers" />
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

    <template #columns>
      <el-table-column type="index" label="STT" width="60" align="center" fixed="left" />
      <el-table-column label="Khách hàng" min-width="220">
        <template #default="{ row }">
          <div class="d-flex align-items-center gap-2">
            <el-avatar :size="32" class="bg-primary shadow-sm">{{ row.tenKhachHang?.charAt(0) }}</el-avatar>
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
          <el-tag :type="row.gioiTinh === 1 ? '' : 'danger'" size="small" effect="plain">
            {{ getGenderText(row.gioiTinh) }}
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
      <el-table-column label="Hành động" width="120" fixed="right" align="center">
        <template #default="{ row }">
          <div class="d-flex gap-2 justify-content-center">
            <el-tooltip content="Chỉnh sửa" placement="top">
              <el-button @click="handleEdit(row)" :icon="Edit" circle size="small" type="primary" plain />
            </el-tooltip>
            <el-tooltip content="Xóa" placement="top">
              <el-button @click="handleDelete(row)" :icon="Delete" circle size="small" type="danger" plain />
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
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
      <el-button @click="dialogVisible = false">Hủy</el-button>
      <el-button type="primary" @click="submitForm">Xác nhận</el-button>
    </template>
  </el-dialog>
</template>