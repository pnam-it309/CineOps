<script setup>
import { ref, computed } from 'vue';
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import BaseTable from '@/components/common/BaseTable.vue';

const tableColumns = [
  { label: 'Tên đăng nhập', key: 'username' },
  { label: 'Email', key: 'email' },
  { label: 'Vai trò', key: 'role' },
  { label: 'Trạng thái', key: 'status' },
  { label: 'Ngày đăng ký', key: 'created' }
];

const loading = ref(false);
const searchQuery = ref('');
const roleFilter = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const dialogVisible = ref(false);
const editingUser = ref(null);

const userForm = ref({
  username: '',
  email: '',
  role: '',
  status: 'active'
});

// Mock user data
const users = ref([
  {
    id: 1,
    username: 'john_doe',
    email: 'john@example.com',
    role: 'admin',
    status: 'active',
    created: '2024-01-15',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  },
  {
    id: 2,
    username: 'jane_smith',
    email: 'jane@example.com',
    role: 'staff',
    status: 'active',
    created: '2024-01-20',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  },
  {
    id: 3,
    username: 'bob_wilson',
    email: 'bob@example.com',
    role: 'customer',
    status: 'active',
    created: '2024-02-01',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  },
  {
    id: 4,
    username: 'alice_johnson',
    email: 'alice@example.com',
    role: 'customer',
    status: 'inactive',
    created: '2024-02-05',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  },
  {
    id: 5,
    username: 'charlie_brown',
    email: 'charlie@example.com',
    role: 'staff',
    status: 'active',
    created: '2024-02-10',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  }
]);

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch = user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                         user.email.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesRole = !roleFilter.value || user.role === roleFilter.value;
    const matchesStatus = !statusFilter.value || user.status === statusFilter.value;
    
    return matchesSearch && matchesRole && matchesStatus;
  });
});

const displayedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredUsers.value.slice(start, end);
});

const getRoleType = (role) => {
  const types = {
    admin: 'danger',
    staff: 'warning',
    customer: 'success'
  };
  return types[role] || 'info';
};

const handleSearch = () => {
  currentPage.value = 1;
};

const handleRefresh = () => {
  loading.value = true;
  searchQuery.value = '';
  roleFilter.value = '';
  statusFilter.value = '';
  currentPage.value = 1;
  
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('Data refreshed successfully');
  }, 500);
};

const handleEdit = (row) => {
  editingUser.value = row;
  userForm.value = { ...row };
  dialogVisible.value = true;
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete user "${row.username}"?`,
    'Confirm Delete',
    {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(() => {
    const index = users.value.findIndex(u => u.id === row.id);
    if (index > -1) {
      users.value.splice(index, 1);
      ElMessage.success('User deleted successfully');
    }
  }).catch(() => {
    ElMessage.info('Delete cancelled');
  });
};

const handleSave = () => {
  if (editingUser.value) {
    // Update existing user
    const index = users.value.findIndex(u => u.id === editingUser.value.id);
    if (index > -1) {
      users.value[index] = { ...users.value[index], ...userForm.value };
      ElMessage.success('User updated successfully');
    }
  } else {
    // Add new user
    const newUser = {
      id: users.value.length + 1,
      ...userForm.value,
      created: new Date().toISOString().split('T')[0],
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    };
    users.value.push(newUser);
    ElMessage.success('User created successfully');
  }
  
  dialogVisible.value = false;
  resetForm();
};

const resetForm = () => {
  editingUser.value = null;
  userForm.value = {
    username: '',
    email: '',
    role: '',
    status: 'active'
  };
};
</script>

<template>
  <div class="user-management w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-3 w-100 flex-shrink-0 pt-2">
      <div>
        <h2 class="mb-2 fs-2 fw-bold text-dark">Quản lý Người dùng</h2>
      </div>
      <el-button type="primary" size="default" :icon="Plus" round @click="dialogVisible = true">
        Thêm người dùng mới
      </el-button>
    </div>

    <!-- Filter and Search Bar -->
    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 flex-shrink-0">
      <el-row :gutter="10">
        <el-col :xs="24" :sm="12" :md="8" class="mb-1 mb-md-0">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo tên hoặc email..."
            :prefix-icon="Search"
            size="default"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="mb-1 mb-md-0">
          <el-select v-model="roleFilter" placeholder="Lọc theo vai trò" size="default" clearable style="width: 100%">
            <el-option label="Tất cả vai trò" value="" />
            <el-option label="Quản trị viên" value="admin" />
            <el-option label="Nhân viên" value="staff" />
            <el-option label="Khách hàng" value="customer" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="mb-1 mb-sm-0">
          <el-select v-model="statusFilter" placeholder="Lọc trạng thái" size="default" clearable style="width: 100%">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đang hoạt động" value="active" />
            <el-option label="Bị khóa" value="inactive" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="default" size="default" :icon="Refresh" @click="handleRefresh" class="w-100">Làm mới</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- Users Table Container -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable
        :data="displayedUsers"
        :columns="tableColumns"
        :total="filteredUsers.length"
        v-model:currentPage="currentPage"
        :page-size="pageSize"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <template #cell-username="{ row }">
          <div class="d-flex align-items-center justify-content-center gap-2">
            <el-avatar :size="28" :src="row.avatar" />
            <span class="fw-medium small">{{ row.username }}</span>
          </div>
        </template>

        <template #cell-role="{ row }">
          <el-tag :type="getRoleType(row.role)" effect="light" size="small">
            {{ row.role }}
          </el-tag>
        </template>

        <template #cell-status="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'info'" effect="plain" size="small">
            {{ row.status }}
          </el-tag>
        </template>
      </BaseTable>
    </div>

    <!-- Add/Edit User Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingUser ? 'Chỉnh sửa người dùng' : 'Thêm người dùng mới'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="userForm" label-width="120px">
        <el-form-item label="Tên đăng nhập">
          <el-input v-model="userForm.username" placeholder="Nhập tên đăng nhập" />
        </el-form-item>
        
        <el-form-item label="Địa chỉ Email">
          <el-input v-model="userForm.email" type="email" placeholder="email@example.com" />
        </el-form-item>
        
        <el-form-item label="Vai trò">
          <el-select v-model="userForm.role" placeholder="Chọn vai trò" style="width: 100%">
            <el-option label="Quản trị viên" value="admin" />
            <el-option label="Nhân viên" value="staff" />
            <el-option label="Khách hàng" value="customer" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Trạng thái">
          <el-switch
            v-model="userForm.status"
            active-text="Hoạt động"
            inactive-text="Bị khóa"
            active-value="active"
            inactive-value="inactive"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">Hủy</el-button>
        <el-button type="primary" @click="handleSave" class="px-4">
          {{ editingUser ? 'Cập nhật' : 'Tạo mới' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-management {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
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

