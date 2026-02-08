<script setup>
import { ref } from 'vue';
import { Plus, User, Edit, Delete, Lock, Key, ShieldCheck, Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const staff = ref([
  { id: 1, name: 'Admin One', username: 'admin', role: 'System Admin', status: 'Active', email: 'admin@cineops.com' },
  { id: 2, name: 'Manager Rose', username: 'manager1', role: 'Cinema Manager', status: 'Active', email: 'rose@cineops.com' },
  { id: 3, name: 'Staff Mike', username: 'mike99', role: 'Staff Member', status: 'Inactive', email: 'mike@cineops.com' }
]);

const roles = ref([
  { name: 'System Admin', permissions: 'All Access', color: 'danger' },
  { name: 'Cinema Manager', permissions: 'Management, Scheduling, Pricing', color: 'warning' },
  { name: 'Staff Member', permissions: 'Check-in, Ticket Sales', color: 'primary' }
]);

const dialogVisible = ref(false);
const roleDialogVisible = ref(false);

const staffForm = ref({
  name: '',
  username: '',
  email: '',
  role: 'Staff Member',
  status: 'Active'
});
</script>

<template>
  <div class="admin-staff container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Nhân viên & Quyền hạn</h2>
        <p class="text-secondary small mb-0">Quản lý tài khoản quản trị và định nghĩa các vai trò truy cập.</p>
      </div>
      <div class="d-flex gap-2">
        <el-button type="info" :icon="ShieldCheck" round @click="roleDialogVisible = true">Vai trò & Quyền</el-button>
        <el-button type="primary" :icon="Plus" round @click="dialogVisible = true">Mời nhân viên</el-button>
      </div>
    </div>

    <!-- Staff Table -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden mb-5">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="ps-3">Nhân viên</th>
              <th>Tên đăng nhập</th>
              <th>Vai trò</th>
              <th>Trạng thái</th>
              <th class="text-end pe-3">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in staff" :key="s.id">
              <td class="ps-3">
                <div class="d-flex align-items-center gap-3">
                  <el-avatar :size="32" class="bg-primary">{{ s.name.charAt(0) }}</el-avatar>
                  <div>
                    <div class="fw-bold">{{ s.name }}</div>
                    <div class="small text-secondary">{{ s.email }}</div>
                  </div>
                </div>
              </td>
              <td class="small fw-bold">@{{ s.username }}</td>
              <td>
                <el-tag size="small" effect="plain">{{ s.role }}</el-tag>
              </td>
              <td>
                <el-badge :is-dot="true" :type="s.status === 'Active' ? 'success' : 'info'" class="me-2" />
                <span class="small">{{ s.status }}</span>
              </td>
              <td class="text-end pe-3">
                <el-button-group>
                  <el-button :icon="Edit" size="small" />
                  <el-button :icon="Key" size="small" />
                  <el-button :icon="Delete" size="small" type="danger" plain />
                </el-button-group>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <!-- Roles Matrix Section -->
    <h4 class="fw-bold mb-4">Roles Discovery</h4>
    <div class="row g-4">
      <div v-for="role in roles" :key="role.name" class="col-md-4">
        <el-card shadow="hover" class="border-0 shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-2 mb-3">
            <el-tag :type="role.color" effect="dark" round>{{ role.name }}</el-tag>
          </div>
          <div class="small fw-bold text-secondary mb-2">PERMISSIONS:</div>
          <p class="small mb-0">{{ role.permissions }}</p>
        </el-card>
      </div>
    </div>

    <!-- Invite Staff Dialog -->
    <el-dialog v-model="dialogVisible" title="Mời nhân viên mới" width="450px" class="rounded-4">
      <el-form :model="staffForm" label-position="top">
        <el-form-item label="Họ và tên" required>
          <el-input v-model="staffForm.name" placeholder="VD: Nguyễn Văn A" />
        </el-form-item>
        <el-form-item label="Địa chỉ Email" required>
          <el-input v-model="staffForm.email" placeholder="vanna@cineops.com" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Tên đăng nhập">
              <el-input v-model="staffForm.username" placeholder="vanna123" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Vai trò">
              <el-select v-model="staffForm.role" class="w-100">
                <el-option v-for="r in roles" :key="r.name" :label="r.name" :value="r.name" />
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Gửi lời mời</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.table thead th {
  border-bottom: none;
}
</style>
