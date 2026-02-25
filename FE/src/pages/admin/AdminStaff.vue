<script setup>
import { ref, computed } from 'vue';
import { Plus, User, Edit, Delete, Lock, Key, Setting, Search, Phone, Message, Clock, Check, Close } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import BaseTable from '@/components/common/BaseTable.vue';

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

const tableColumns = [
  { label: 'Nhân viên', key: 'name' },
  { label: 'Tên đăng nhập', key: 'username' },
  { label: 'Vai trò', key: 'role' },
  { label: 'SĐT', key: 'phone' },
  { label: 'Ngày tham gia', key: 'joinDate' },
  { label: 'Trạng thái', key: 'status' },
];

const dialogVisible = ref(false);
const roleDialogVisible = ref(false);
const searchQuery = ref('');
const filterRole = ref('all');
const filterStatus = ref('all');
const currentPage = ref(1);
const pageSize = 10;

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
  <div class="admin-staff w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fw-bold text-dark mb-1" style="font-size: 18px;">Quản lý Nhân viên</h2>
      </div>
      <div class="d-flex gap-2">
        <el-button type="info" :icon="Setting" round @click="roleDialogVisible = true">Vai trò & Quyền</el-button>
        <el-button type="primary" :icon="Plus" round @click="openAddDialog">Thêm nhân viên</el-button>
      </div>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 w-100 flex-shrink-0">
      <div class="row g-2 align-items-center">
        <div class="col-md-5">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo tên, username, email..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="col-md-3">
          <el-select v-model="filterRole" placeholder="Lọc theo vai trò" size="default" class="w-100">
            <el-option label="Tất cả vai trò" value="all" />
            <el-option v-for="r in roles" :key="r.name" :label="r.name" :value="r.name" />
          </el-select>
        </div>
        <div class="col-md-3">
          <el-select v-model="filterStatus" placeholder="Lọc theo trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Đang hoạt động" value="Đang hoạt động" />
            <el-option label="Ngừng hoạt động" value="Ngừng hoạt động" />
          </el-select>
        </div>
        <div class="col-md-1 text-end">
          <el-tag type="info" effect="plain" round class="px-3">{{ filteredStaff.length }}</el-tag>
        </div>
      </div>
    </el-card>

    <!-- Staff Table -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable
        :data="filteredStaff"
        :columns="tableColumns"
        :total="filteredStaff.length"
        v-model:currentPage="currentPage"
        :page-size="pageSize"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <template #cell-name="{ row }">
          <div class="d-flex align-items-center gap-3">
            <el-avatar :size="36" class="flex-shrink-0" :style="{ background: getRoleType(row.role) === 'danger' ? '#f56c6c' : getRoleType(row.role) === 'warning' ? '#e6a23c' : '#409eff' }">
              {{ row.name.charAt(0) }}
            </el-avatar>
            <div>
              <div class="fw-bold">{{ row.name }}</div>
              <div class="small text-secondary">{{ row.email }}</div>
            </div>
          </div>
        </template>

        <template #cell-username="{ row }">
          <code class="small fw-bold text-dark">@{{ row.username }}</code>
        </template>

        <template #cell-role="{ row }">
          <el-tag :type="getRoleType(row.role)" size="small" effect="light" round>{{ row.role }}</el-tag>
        </template>

        <template #cell-status="{ row }">
          <div class="d-flex align-items-center gap-2 cursor-pointer" @click="handleToggleStatus(row)">
            <span class="status-dot" :class="row.status === 'Đang hoạt động' ? 'bg-success' : 'bg-secondary'"></span>
            <span class="small" :class="row.status === 'Đang hoạt động' ? 'text-success' : 'text-secondary'">{{ row.status }}</span>
          </div>
        </template>
      </BaseTable>
    </div>

    <!-- Add/Edit Staff Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="staffForm.id ? 'Chỉnh sửa nhân viên' : 'Thêm nhân viên mới'"
      width="500px"
      class="rounded-4"
    >
      <el-form :model="staffForm" label-position="top">
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
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" class="px-4" @click="handleSave">
            {{ staffForm.id ? 'Cập nhật' : 'Thêm nhân viên' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Roles Dialog -->
    <el-dialog v-model="roleDialogVisible" title="Quản lý Vai trò & Quyền hạn" width="600px" class="rounded-4">
      <div class="d-flex flex-column gap-4">
        <div v-for="role in roles" :key="role.name" class="p-3 border rounded-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="d-flex align-items-center gap-2">
              <span class="fs-5">{{ role.icon }}</span>
              <el-tag :type="role.color" effect="dark" round>{{ role.name }}</el-tag>
            </div>
            <el-button size="small" :icon="Edit" text>Sửa</el-button>
          </div>
          <div class="d-flex flex-wrap gap-2">
            <el-tag v-for="perm in role.permissions" :key="perm" type="info" effect="plain" size="small" round>
              {{ perm }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">Đóng</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-staff {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}

.cursor-pointer {
  cursor: pointer;
}

code {
  letter-spacing: 0.5px;
}

.no-scroll {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
  overflow: hidden !important;
}

.no-scroll::-webkit-scrollbar {
  display: none !important;
}

.overflow-auto.no-scroll {
  overflow-y: auto !important;
}
</style>
