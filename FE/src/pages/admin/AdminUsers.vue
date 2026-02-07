<template>
  <div class="container-fluid p-4">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fs-3 fw-bold text-dark">User Management</h2>
        <p class="text-secondary mb-0 small">Manage system users and their permissions</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="dialogVisible = true">
        Add New User
      </el-button>
    </div>

    <!-- Filter and Search Bar -->
    <el-card shadow="never" class="mb-4">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" class="mb-2 mb-md-0">
          <el-input
            v-model="searchQuery"
            placeholder="Search users..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="mb-2 mb-md-0">
          <el-select v-model="roleFilter" placeholder="Filter by role" clearable style="width: 100%">
            <el-option label="All Roles" value="" />
            <el-option label="Admin" value="admin" />
            <el-option label="Staff" value="staff" />
            <el-option label="Customer" value="customer" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="mb-2 mb-sm-0">
          <el-select v-model="statusFilter" placeholder="Filter by status" clearable style="width: 100%">
            <el-option label="All Status" value="" />
            <el-option label="Active" value="active" />
            <el-option label="Inactive" value="inactive" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="default" :icon="Refresh" @click="handleRefresh" class="w-100">Refresh</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- Users Table -->
    <el-card shadow="never" class="border-0 shadow-sm" :body-style="{ padding: '0' }">
      <el-table
        :data="displayedUsers"
        style="width: 100%"
        stripe
        v-loading="loading"
      >
        <el-table-column type="index" width="60" label="#" />
        
        <el-table-column prop="username" label="Username" min-width="150">
          <template #default="{ row }">
            <div class="d-flex align-items-center gap-2">
              <el-avatar :size="32" :src="row.avatar" />
              <span class="fw-medium">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="Email" min-width="200" />
        
        <el-table-column prop="role" label="Role" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" effect="light">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" effect="plain">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="created" label="Joined Date" width="150" />
        
        <el-table-column label="Actions" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="Edit" size="small" link @click="handleEdit(row)">
              Edit
            </el-button>
            <el-button type="danger" :icon="Delete" size="small" link @click="handleDelete(row)">
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="p-4 d-flex justify-content-end border-top">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredUsers.length"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- Add/Edit User Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingUser ? 'Edit User' : 'Add New User'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="Username">
          <el-input v-model="userForm.username" placeholder="Enter username" />
        </el-form-item>
        
        <el-form-item label="Email">
          <el-input v-model="userForm.email" type="email" placeholder="Enter email" />
        </el-form-item>
        
        <el-form-item label="Role">
          <el-select v-model="userForm.role" placeholder="Select role" style="width: 100%">
            <el-option label="Admin" value="admin" />
            <el-option label="Staff" value="staff" />
            <el-option label="Customer" value="customer" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Status">
          <el-switch
            v-model="userForm.status"
            active-text="Active"
            inactive-text="Inactive"
            active-value="active"
            inactive-value="inactive"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave">
          {{ editingUser ? 'Update' : 'Create' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

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

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handlePageChange = (val) => {
  currentPage.value = val;
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
