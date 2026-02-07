<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fs-3 fw-bold text-dark">Role Management</h2>
        <p class="text-secondary mb-0 small">Manage user roles and permissions</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAddRole">
        Add New Role
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :lg="8" v-for="role in roles" :key="role.id" class="mb-4">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm transition-transform card-hover-effect">
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <div class="d-flex align-items-center gap-2 fw-bold fs-6">
                <el-icon :size="20" :color="role.color">
                  <component :is="role.icon" />
                </el-icon>
                <span>{{ role.name }}</span>
              </div>
              <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, role)">
                <el-icon class="cursor-pointer fs-5 text-secondary hover-primary"><MoreFilled /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">
                      <el-icon><Edit /></el-icon>
                      Edit
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      Delete
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
          
          <div>
            <p class="text-secondary small mb-3">{{ role.description }}</p>
            
            <div class="mb-3">
              <div class="d-flex align-items-center gap-2 text-secondary small">
                <el-icon><User /></el-icon>
                <span>{{ role.userCount }} users</span>
              </div>
            </div>
            
            <el-divider class="my-3"/>
            
            <div>
              <h4 class="mb-2 fs-6 fw-bold text-dark">Permissions</h4>
              <div class="d-flex flex-wrap gap-2">
                <el-tag
                  v-for="permission in role.permissions"
                  :key="permission"
                  size="small"
                  effect="plain"
                  class="cursor-default"
                >
                  {{ permission }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Plus, MoreFilled, Edit, Delete, User, Lock, Setting } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const roles = ref([
  {
    id: 1,
    name: 'Administrator',
    description: 'Full system access with all permissions',
    icon: Lock,
    color: '#F56C6C',
    userCount: 5,
    permissions: ['All Access', 'User Management', 'System Config', 'Reports']
  },
  {
    id: 2,
    name: 'Staff',
    description: 'Limited access for staff members',
    icon: User,
    color: '#E6A23C',
    userCount: 24,
    permissions: ['View Users', 'Manage Bookings', 'View Reports']
  },
  {
    id: 3,
    name: 'Customer',
    description: 'Basic access for customers',
    icon: User,
    color: '#67C23A',
    userCount: 1240,
    permissions: ['View Profile', 'Book Tickets', 'View History']
  }
]);

const handleAddRole = () => {
  ElMessage.info('Add role dialog will be implemented');
};

const handleCommand = (command, role) => {
  if (command === 'edit') {
    ElMessage.info(`Edit role: ${role.name}`);
  } else if (command === 'delete') {
    ElMessageBox.confirm(
      `Delete role "${role.name}"? This action cannot be undone.`,
      'Confirm Delete',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    ).then(() => {
      ElMessage.success('Role deleted');
    });
  }
};
</script>

<style scoped>
.transition-transform {
  transition: transform 0.3s ease;
}
.card-hover-effect:hover {
  transform: translateY(-4px);
}
.hover-primary:hover {
  color: var(--el-color-primary) !important;
}
.cursor-pointer {
  cursor: pointer;
}
.cursor-default {
  cursor: default;
}
</style>
