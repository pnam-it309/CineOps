<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Plus, User, Edit, Delete, Lock, Key, Setting, Search, Phone, Message, Clock, Check, Close } from '@element-plus/icons-vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

import BaseTable from '@/components/common/BaseTable.vue';
import CCCDScanner from '@/components/common/CCCDScanner.vue'; // <-- Integrate Scanner
import { nhanVienService } from '@/services/api/admin/nhanVienService';
import { phanQuyenService } from '@/services/api/admin/phanQuyenService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import BaseModal from '@/components/common/BaseModal.vue';

const staff = ref([]);
const selectedStaff = ref([]);
const loading = ref(false);

const staffColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'Nhân viên', key: 'staff', minWidth: '250px' },
  { label: 'Email', key: 'email', minWidth: '400px' },
  { label: 'Tên đăng nhập', key: 'username', width: '250px' },
  { label: 'Vai trò', key: 'role', width: '250px' },
  { label: 'Số điện thoại', key: 'phone', width: '200px' },
  { label: 'Ngày tham gia', key: 'joinDate', width: '200px' },
  { label: 'Trạng thái', key: 'status', width: '200px' },
];

const roles = ref([]);
const chucVuOptions = ref([]);

const fetchRoles = async () => {
  try {
    const res = await phanQuyenService.getAllRoles();
    if (res.data && res.data.data) {
      roles.value = res.data.data;
    }
  } catch (error) {
    console.error('Không thể tải danh sách vai trò', error);
  }
};

const fetchChucVu = async () => {
  try {
    const res = await nhanVienService.getChucVu();
    if (res.data && res.data.data) {
      chucVuOptions.value = res.data.data;
    }
  } catch (error) {
    console.error('Không thể tải danh sách chức vụ', error);
  }
};

const dialogVisible = ref(false);

const filterRole = ref('');
const filterStatus = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const phanQuyenList = ref([]);
const router = useRouter();
const searchQuery = ref('');
const roleDialogVisible = ref(false);



const fetchStaff = async () => {
  loading.value = true;
  try {
    const res = await nhanVienService.getAll(
      searchQuery.value || null,
      filterRole.value || null,
      filterStatus.value === '' ? null : filterStatus.value
    );
    if (res.data && res.data.data) {
      staff.value = Array.isArray(res.data.data) ? res.data.data : [];
    } else {
      staff.value = [];
    }
  } catch (error) {
    notification.error('Không thể tải danh sách nhân viên');
  } finally {
    loading.value = false;
  }
};

const filteredStaff = computed(() => staff.value);

const getRoleType = (role) => {
  if (!role) return 'info';
  if (role.toLowerCase().includes('admin') || role.includes('Quản trị')) return 'danger';
  if (role.toLowerCase().includes('manager') || role.includes('Quản lý')) return 'warning';
  return 'primary';
};

const getAvatarColor = (role) => {
  const type = getRoleType(role);
  if (type === 'danger') return '#f56c6c';
  if (type === 'warning') return '#e6a23c';
  return '#409eff';
};

const handleAdd = () => {
  router.push('/admin/staff/add');
};

const handleEdit = (row) => {
  router.push(`/admin/staff/edit/${row.id}`);
};

const handleDelete = (row) => {
  handleToggleStatus(row);
};



const handleToggleStatus = async (row) => {
  const newStatus = row.trangThai === 1 ? 0 : 1;
  try {
    await nhanVienService.update(row.id, { ...row, trangThai: newStatus, matKhau: undefined });
    row.trangThai = newStatus;
    notification.success(`Đã ${newStatus === 1 ? 'kích hoạt' : 'vô hiệu hóa'} tài khoản ${row.tenNhanVien}`);
  } catch {
    notification.error('Thay đổi trạng thái thất bại');
  }
};



const selectedIds = computed(() => selectedStaff.value.map(item => item.id));

const handleBulkDelete = () => {
    confirmDialog.custom(
        `Thay đổi trạng thái cho <b>${selectedIds.value.length}</b> nhân viên đã chọn?`,
        'Cập nhật hàng loạt',
        'Đồng ý'
    ).then(async () => {
        try {
            await Promise.all(selectedStaff.value.map(item => {
                const newStatus = item.trangThai === 1 ? 0 : 1;
                return nhanVienService.update(item.id, { ...item, trangThai: newStatus, matKhau: undefined });
            }));
            notification.success(`Đã cập nhật trạng thái cho ${selectedIds.value.length} nhân viên`);
            selectedStaff.value = [];
            fetchStaff();
        } catch (error) {
            notification.error('Có lỗi khi cập nhật hàng loạt');
        }
    }).catch(() => {});
};

const disabledDate = (time) => {
  return time.getTime() > Date.now();
};

onMounted(() => {
    fetchStaff();
    fetchRoles();
    fetchChucVu();
});
watch([searchQuery, filterRole, filterStatus], fetchStaff);
</script>

<template>
  <div class="admin-staff-page">
    <AdminTableLayout
      title="Quản lý nhân viên"
      titleIcon="bi bi-people-fill"
      addButtonLabel="Thêm nhân viên"
      :data="filteredStaff"
      :loading="loading"
      :total="filteredStaff.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="handleAdd"
      @reset-filter="() => { searchQuery = ''; filterRole = ''; filterStatus = ''; }"
    >
      <template #header-actions-left>
        <div class="d-flex align-items-center gap-2">
          <el-button v-if="selectedIds.length" type="warning" plain round :icon="Refresh" @click="handleBulkDelete">
            Đổi trạng thái {{ selectedIds.length }} nhân viên
          </el-button>
          <el-button class="btn-premium-secondary" :icon="Setting" @click="roleDialogVisible = true" round>Vai trò & Quyền</el-button>
        </div>
      </template>

      <template #filters>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-input
            v-model="searchQuery"
            placeholder="Tên, tên đăng nhập, email..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="filterRole" placeholder="Tất cả" size="default" class="w-100">
            <el-option label="Tất cả vai trò" value="all" />
            <el-option v-for="r in roles" :key="r.id" :label="r.tenVaiTro" :value="r.id" />
          </el-select>
        </div>
        <div class="filter-item">
           <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="filterStatus" placeholder="Tất cả" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đang hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>
      </template>

      <template #content>
        <BaseTable
          :data="filteredStaff.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
          :columns="staffColumns"
          :loading="loading"
          :total="filteredStaff.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          v-model:selection="selectedStaff"
          :hide-pagination="true"
          @edit="handleEdit"
          @delete="handleDelete"
        >
          <template #actions="{ row }">
            <div class="d-flex justify-content-center gap-2">
                <el-tooltip content="Chỉnh sửa" placement="top">
                  <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)">
                    <i class="bi bi-pencil fs-6"></i>
                  </button>
                </el-tooltip>
                <el-tooltip :content="row.trangThai === 1 ? 'Vô hiệu hóa' : 'Kích hoạt'" placement="top">
                  <button class="btn-action-icon btn-action-refresh" @click="handleDelete(row)">
                    <i class="bi bi-arrow-repeat fs-6"></i>
                  </button>
                </el-tooltip>
            </div>
          </template>
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-staff="{ row }">
            <div class="fw-bold text-dark small">{{ row.tenNhanVien }}</div>
          </template>

          <template #cell-email="{ row }">
            <div class="text-secondary small">{{ row.email }}</div>
          </template>

          <template #cell-username="{ row }">
            <code class="small fw-bold text-indigo-500">{{ row.tenDangNhap || row.maNhanVien }}</code>
          </template>

          <template #cell-role="{ row }">
            <el-tag :type="getRoleType(row.tenPhanQuyen)" size="small" effect="light" round>{{ row.tenPhanQuyen || 'Chưa gán' }}</el-tag>
          </template>

          <template #cell-phone="{ row }">
            <span class="small">{{ row.soDienThoai }}</span>
          </template>
          
          <template #cell-joinDate="{ row }">
            <span class="small text-secondary">
              {{ row.ngayTao ? new Date(row.ngayTao).toLocaleDateString('vi-VN') : '—' }}
            </span>
          </template>

          <template #cell-status="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" size="small" round class="cursor-pointer" @click="handleToggleStatus(row)" title="Bấm để đổi trạng thái">
              {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
            </el-tag>
          </template>

        </BaseTable>
      </template>
    </AdminTableLayout>





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
    </el-dialog>
  </div>
</template>
<style scoped>
.bg-indigo-500 {
  background-color: #6366f1;
}

.shadow-success-lite {
  box-shadow: 0 0 8px rgba(103, 194, 58, 0.4);
}

.extra-small {
  font-size: 11px;
}

.cursor-pointer {
  cursor: pointer;
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
