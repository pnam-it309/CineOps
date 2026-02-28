<script setup>
import { ref, computed } from 'vue';
import { Plus, User, Edit, Delete, Lock, Key, Setting, Search, Phone, Message, Clock, Check, Close } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

const staff = ref([
  { id: 1, name: 'Nguyễn Văn An', username: 'admin', role: 'Quản trị viên', status: 'Đang hoạt động', email: 'admin@cineops.com', phone: '0901 234 567', joinDate: '2024-01-15' },
  { id: 2, name: 'Trần Thị Hồng', username: 'manager1', role: 'Quản lý rạp', status: 'Đang hoạt động', email: 'hong@cineops.com', phone: '0912 345 678', joinDate: '2024-03-20' },
  { id: 3, name: 'Lê Minh Tuấn', username: 'tuan_le', role: 'Nhân viên', status: 'Ngừng hoạt động', email: 'tuan@cineops.com', phone: '0923 456 789', joinDate: '2024-06-10' },
  { id: 4, name: 'Phạm Thị Mai', username: 'mai_pham', role: 'Nhân viên', status: 'Đang hoạt động', email: 'mai@cineops.com', phone: '0934 567 890', joinDate: '2025-01-05' },
  { id: 5, name: 'Hoàng Đức Anh', username: 'duc_anh', role: 'Quản lý rạp', status: 'Đang hoạt động', email: 'ducanh@cineops.com', phone: '0945 678 901', joinDate: '2024-09-12' },
  { id: 6, name: 'Võ Thanh Sơn', username: 'son_vo', role: 'Nhân viên', status: 'Đang hoạt động', email: 'son@cineops.com', phone: '0956 789 012', joinDate: '2025-02-01' },
]);

const roles = ref([
  { name: 'Quản trị viên', permissions: ['Toàn quyền hệ thống', 'Quản lý người dùng', 'Cấu hình hệ thống', 'Báo cáo & thống kê'], color: 'danger', icon: '👑' },
  { name: 'Quản lý rạp', permissions: ['Quản lý lịch chiếu', 'Quản lý phim', 'Quản lý giá vé', 'Báo cáo doanh thu'], color: 'warning', icon: '🎬' },
  { name: 'Nhân viên', permissions: ['Bán vé tại quầy', 'Check-in khách hàng', 'Hỗ trợ khách hàng'], color: 'primary', icon: '🎫' },
]);

const dialogVisible = ref(false);
const roleDialogVisible = ref(false);
const searchQuery = ref('');
const filterRole = ref('all');
const filterStatus = ref('all');
const currentPage = ref(1);
const pageSize = ref(10);

const staffForm = ref({
  name: '',
  username: '',
  email: '',
  phone: '',
  role: 'Nhân viên',
  status: 'Đang hoạt động',
});

const resetForm = () => {
  staffForm.value = {
    name: '',
    username: '',
    email: '',
    phone: '',
    role: 'Nhân viên',
    status: 'Đang hoạt động',
  };
};

const filteredStaff = computed(() => {
  return staff.value.filter((s) => {
    const matchSearch =
      !searchQuery.value ||
      s.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      s.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      s.email.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchRole = filterRole.value === 'all' || s.role === filterRole.value;
    const matchStatus = filterStatus.value === 'all' || s.status === filterStatus.value;
    return matchSearch && matchRole && matchStatus;
  });
});

const getRoleType = (role) => {
  const map = { 'Quản trị viên': 'danger', 'Quản lý rạp': 'warning', 'Nhân viên': 'primary' };
  return map[role] || 'info';
};

const handleEdit = (s) => {
  staffForm.value = { ...s };
  dialogVisible.value = true;
};

const handleDelete = (s) => {
  ElMessageBox.confirm(`Bạn có chắc muốn xóa nhân viên "${s.name}"?`, 'Xác nhận xóa', {
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
    type: 'warning',
  }).then(() => {
    staff.value = staff.value.filter((item) => item.id !== s.id);
    ElMessage.success('Đã xóa nhân viên thành công');
  }).catch(() => {});
};

const handleSave = () => {
  if (!staffForm.value.name || !staffForm.value.email) {
    ElMessage.warning('Vui lòng điền đầy đủ thông tin bắt buộc');
    return;
  }
  if (staffForm.value.id) {
    const index = staff.value.findIndex((s) => s.id === staffForm.value.id);
    if (index !== -1) staff.value[index] = { ...staffForm.value };
    ElMessage.success('Cập nhật nhân viên thành công');
  } else {
    const newId = Math.max(...staff.value.map((s) => s.id)) + 1;
    staff.value.push({ ...staffForm.value, id: newId, joinDate: new Date().toISOString().slice(0, 10) });
    ElMessage.success('Thêm nhân viên thành công');
  }
  dialogVisible.value = false;
  resetForm();
};

const handleToggleStatus = (s) => {
  s.status = s.status === 'Đang hoạt động' ? 'Ngừng hoạt động' : 'Đang hoạt động';
  ElMessage.success(`Đã ${s.status === 'Đang hoạt động' ? 'kích hoạt' : 'vô hiệu hóa'} tài khoản ${s.name}`);
};

const openAddDialog = () => {
  resetForm();
  dialogVisible.value = true;
};
</script>

<template>
  <div class="admin-staff-page">
    <AdminTableLayout
      title="Quản lý Nhân viên"
      titleIcon="bi bi-people-fill"
      addButtonLabel="Thêm nhân viên"
      :data="filteredStaff.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      :total="filteredStaff.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openAddDialog"
      @reset-filter="() => { searchQuery = ''; filterRole = 'all'; filterStatus = 'all'; }"
    >
      <template #header-actions-left>
        <el-button class="btn-premium-secondary" :icon="Setting" @click="roleDialogVisible = true">Vai trò & Quyền</el-button>
      </template>

      <template #filters>
        <div class="filter-item" style="width: 350px;">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo tên, username, email..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="filterRole" placeholder="Vai trò" size="default" class="w-100">
            <el-option label="Tất cả vai trò" value="all" />
            <el-option v-for="r in roles" :key="r.name" :label="r.name" :value="r.name" />
          </el-select>
        </div>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="filterStatus" placeholder="Trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Đang hoạt động" value="Đang hoạt động" />
            <el-option label="Ngừng hoạt động" value="Ngừng hoạt động" />
          </el-select>
        </div>
      </template>

      <template #columns>
        <el-table-column label="Nhân viên" min-width="250">
          <template #default="{ row }">
            <div class="d-flex align-items-center gap-3">
              <el-avatar :size="36" class="flex-shrink-0" :style="{ background: getRoleType(row.role) === 'danger' ? '#f56c6c' : getRoleType(row.role) === 'warning' ? '#e6a23c' : '#409eff' }">
                {{ row.name.charAt(0) }}
              </el-avatar>
              <div class="text-start">
                <div class="fw-bold text-dark">{{ row.name }}</div>
                <div class="small text-secondary">{{ row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Tên đăng nhập" width="180">
          <template #default="{ row }">
            <code class="small fw-bold text-indigo-500">@{{ row.username }}</code>
          </template>
        </el-table-column>

        <el-table-column label="Vai trò" width="150" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" size="small" effect="light" round>{{ row.role }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Số điện thoại" prop="phone" width="150" align="center" />
        
        <el-table-column label="Ngày tham gia" prop="joinDate" width="150" align="center" />

        <el-table-column label="Trạng thái" width="150" align="center">
          <template #default="{ row }">
            <div class="d-flex align-items-center justify-content-center gap-2 cursor-pointer" @click="handleToggleStatus(row)">
              <span class="status-dot" :class="row.status === 'Đang hoạt động' ? 'bg-success' : 'bg-secondary'"></span>
              <span class="small" :class="row.status === 'Đang hoạt động' ? 'text-success' : 'text-secondary'">{{ row.status }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Thao tác" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)">
                <i class="bi bi-pencil fs-6"></i>
              </button>
              <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)">
                <i class="bi bi-trash fs-6"></i>
              </button>
            </div>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>

    <!-- Add/Edit Staff Dialog -->
    <el-dialog
      v-model="dialogVisible"
      width="600px"
      class="premium-dialog"
      destroy-on-close
    >
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i :class="staffForm.id ? 'bi bi-person-gear' : 'bi bi-person-plus'"></i>
            </div>
            <div class="header-text">
              <h5 class="title">{{ staffForm.id ? 'Chỉnh sửa Nhân viên' : 'Thêm Nhân viên mới' }}</h5>
              <p class="subtitle opacity-75">Quản trị nhân sự và quyền truy cập</p>
            </div>
          </div>
        </div>
      </template>

      <el-form :model="staffForm" label-position="top" class="premium-form">
        <el-form-item label="Họ và tên" required>
          <el-input v-model="staffForm.name" placeholder="VD: Nguyễn Văn A" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="Địa chỉ Email" required>
          <el-input v-model="staffForm.email" placeholder="vanna@cineops.com" :prefix-icon="Message" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Tên đăng nhập">
              <el-input v-model="staffForm.username" placeholder="vanna123" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Số điện thoại">
              <el-input v-model="staffForm.phone" placeholder="0901 234 567" :prefix-icon="Phone" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Vai trò">
              <el-select v-model="staffForm.role" class="w-100">
                <el-option v-for="r in roles" :key="r.name" :label="r.name" :value="r.name" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Trạng thái">
              <el-select v-model="staffForm.status" class="w-100">
                <el-option label="Đang hoạt động" value="Đang hoạt động" />
                <el-option label="Ngừng hoạt động" value="Ngừng hoạt động" />
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false" class="btn-premium-secondary">Hủy</el-button>
          <el-button type="primary" @click="handleSave" class="btn-premium-primary">Lưu lại</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Roles Dialog -->
    <el-dialog v-model="roleDialogVisible" width="600px" class="premium-dialog">
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i class="bi bi-shield-check"></i>
            </div>
            <div class="header-text">
              <h5 class="title">Vai trò & Quyền hạn</h5>
              <p class="subtitle opacity-75">Cấu hình phân quyền hệ thống</p>
            </div>
          </div>
        </div>
      </template>

      <div class="d-flex flex-column gap-3">
        <div v-for="role in roles" :key="role.name" class="p-3 border rounded-3 bg-light-subtle">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="d-flex align-items-center gap-2">
              <span class="fs-5">{{ role.icon }}</span>
              <el-tag :type="role.color" effect="dark" round>{{ role.name }}</el-tag>
            </div>
            <el-button size="small" :icon="Edit" text class="text-indigo-500">Sửa</el-button>
          </div>
          <div class="d-flex flex-wrap gap-2">
            <el-tag v-for="perm in role.permissions" :key="perm" type="info" effect="plain" size="small" round>
              {{ perm }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false" class="btn-premium-secondary">Đóng</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

code {
  letter-spacing: 0.5px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.text-indigo-500 {
  color: #4f46e5;
}
</style>
