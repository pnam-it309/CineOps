<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { User, Edit, Lock, Key, Setting, Search, Refresh } from '@element-plus/icons-vue';

import BaseTable from '@/components/common/BaseTable.vue';
import CCCDScanner from '@/components/common/CCCDScanner.vue'; // <-- Integrate Scanner
import { nhanVienService } from '@/services/api/admin/nhanVienService';
import { phanQuyenService } from '@/services/api/admin/phanQuyenService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import BaseModal from '@/components/common/BaseModal.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';

import debounce from 'lodash/debounce';

const staff = ref([]);
const loading = ref(false);
const searchQuery = ref('');

const staffColumns = [
  { label: 'STT', key: 'stt', width: '60px', align: 'center' },
  { label: 'Mã NV', key: 'maNhanVien', width: '100px', align: 'center' },
  { label: 'Nhân viên', key: 'staff', minWidth: '180px', align: 'center' },
  { label: 'Số điện thoại', key: 'soDienThoai', width: '140px', align: 'center' },
  { label: 'Email', key: 'email', width: '180px', align: 'center' },
  { label: 'Vai trò', key: 'role', width: '130px', align: 'center' },
  { label: 'Địa chỉ', key: 'diaChi', minWidth: '200px', align: 'center' },
  { label: 'Trạng thái', key: 'trangThai', width: '140px', align: 'center' },
];

const roles = ref([]);
const chucVuOptions = ref([]);

const getRoleIcon = (code) => {
  const icons = {
    'ROLE_ADMIN': 'bi-shield-lock-fill',
    'ROLE_STAFF': 'bi-person-badge-fill',
    'ROLE_CUSTOMER': 'bi-person-fill'
  };
  return icons[code] || 'bi-gear-fill';
};

const getRoleColor = (code) => {
  const colors = {
    'ROLE_ADMIN': 'danger',
    'ROLE_STAFF': 'warning',
    'ROLE_CUSTOMER': 'primary'
  };
  return colors[code] || 'info';
};

const getRoleType = (roleName) => {
  if (!roleName) return 'info';
  const name = roleName.toLowerCase();
  if (name.includes('admin') || name.includes('quản trị')) return 'danger';
  if (name.includes('nhân viên') || name.includes('staff')) return 'warning';
  return 'primary';
};

const fetchRoles = async () => {
  try {
    const res = await phanQuyenService.getAllRoles();
    if (res.data && res.data.data) {
      roles.value = res.data.data.map(r => ({
        ...r,
        name: r.tenVaiTro,
        icon: getRoleIcon(r.maPhanQuyen),
        color: getRoleColor(r.maPhanQuyen),
        permissions: r.quyenHan === 'ALL' ? ['Toàn quyền hệ thống'] : (r.quyenHan ? r.quyenHan.split(',').map(p => p.trim()) : [])
      }));
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
const totalElements = ref(0);
const currentPage = ref(1);
const pageSize = ref(5);
const phanQuyenList = ref([]);
const router = useRouter();
const roleDialogVisible = ref(false);
const detailVisible = ref(false);
const selectedItem = ref(null);

const handleView = (row) => {
  selectedItem.value = row;
  detailVisible.value = true;
};



const fetchStaff = async () => {
  loading.value = true;
  try {
    const res = await nhanVienService.getAll(
      searchQuery.value || null,
      filterRole.value || null,
      filterStatus.value === '' ? null : filterStatus.value,
      currentPage.value - 1,
      pageSize.value
    );

    const apiRes = res.data;
    if (apiRes && apiRes.data) {
      // Backend returns Page object
      if (apiRes.data.content) {
        staff.value = apiRes.data.content;
        totalElements.value = apiRes.data.totalElements;
      } else {
        staff.value = Array.isArray(apiRes.data) ? apiRes.data : [];
        totalElements.value = staff.value.length;
      }
    } else {
      staff.value = [];
      totalElements.value = 0;
    }
  } catch (error) {
    notification.error('Không thể tải danh sách nhân viên');
  } finally {
    loading.value = false;
  }
};

const debouncedFetch = debounce(() => {
  currentPage.value = 1;
  fetchStaff();
}, 300);

const handleReset = () => {
  searchQuery.value = '';
  filterRole.value = '';
  filterStatus.value = '';
  currentPage.value = 1;
  fetchStaff();
};

const filteredStaff = computed(() => staff.value);

const formatCurrency = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const getChucVuTagType = (ten) => {
  if (!ten) return 'info';
  if (ten.toLowerCase().includes('admin') || ten.includes('Quản trị')) return 'danger';
  if (ten.toLowerCase().includes('manager') || ten.includes('Quản lý')) return 'warning';
  return 'primary';
};

const getAvatarColor = (role) => {
  const type = getRoleType(role);
  if (type === 'danger') return '#f56c6c';
  if (type === 'warning') return '#e6a23c';
  return '#409eff';
};

const handleAdd = () => {
  router.push({ name: ROUTES_CONSTANTS.ADMIN.children.STAFF_ADD.name });
};

const handleEdit = (row) => {
  router.push({ name: ROUTES_CONSTANTS.ADMIN.children.STAFF_EDIT.name, params: { id: row.id } });
};

const handleDelete = (row) => {
  handleUpdateStatus(row);
};



const handleUpdateStatus = async (row, status = null) => {
  const newStatus = status !== null ? status : (row.trangThai === 1 ? 0 : 1);
  const label = newStatus === 1 ? 'kích hoạt' : 'vô hiệu hóa';
  try {
    await confirmDialog.custom(`Bạn có chắc muốn ${label} tài khoản nhân viên ${row.tenNhanVien}?`, 'Xác nhận');
    await nhanVienService.update(row.id, { ...row, trangThai: newStatus, matKhau: undefined });
    row.trangThai = newStatus;
    notification.success(`Đã ${label} tài khoản ${row.tenNhanVien}`);
  } catch (e) {
    if (e !== 'cancel') notification.error('Thay đổi trạng thái thất bại');
  }
};



const handleSendPasswordReset = async (row) => {
  try {
    await confirmDialog.custom(`Bạn có chắc muốn gửi email đặt lại mật khẩu đến ${row.email}?`, 'Xác nhận gửi email');
    await nhanVienService.resetPassword(row.id);
    notification.success('Email đặt lại mật khẩu đã được gửi đến ' + row.email);
  } catch (e) {
    if (e !== 'cancel') {
      notification.error('Gửi email đặt lại mật khẩu thất bại');
    }
  }
};

const disabledDate = (time) => {
  return time.getTime() > Date.now();
};

onMounted(() => {
  fetchStaff();
  fetchRoles();
  fetchChucVu();
});

watch(searchQuery, debouncedFetch);
watch([filterRole, filterStatus, currentPage, pageSize], fetchStaff);
</script>

<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable title="Quản lý nhân viên" titleIcon="bi bi-people-fill" addButtonLabel="Thêm nhân viên" :data="staff"
      :columns="staffColumns" :loading="loading" :total="totalElements" v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" @add-click="handleAdd"
      @reset-filter="handleReset" @edit="handleEdit"
      @delete="handleDelete" @update-status="({ row, val }) => handleUpdateStatus(row, val ? 1 : 0)">
      <template #header-actions-left>
        <div class="d-flex align-items-center gap-2">
          <ExcelActions module="nhan-vien" @import-success="fetchStaff" />
          <el-tooltip content="Cấu hình Vai trò & Quyền hạn" placement="top">
            <el-button class="btn-cine-secondary square" :icon="Setting" @click="roleDialogVisible = true"
              plain></el-button>
          </el-tooltip>
        </div>
      </template>

      <!-- Optimized Filters -->
      <template #filters>
        <div class="me-2 mb-2 mb-md-0" style="min-width: 240px;">
          <el-input v-model="searchQuery" placeholder="Tên, email, SĐT..." :prefix-icon="Search" clearable />
        </div>
        <div class="me-2 mb-2 mb-md-0">
          <el-select v-model="filterRole" placeholder="Vai trò" style="width: 170px;">
            <el-option label="Tất cả vai trò" value="" />

            <el-option v-for="r in roles" :key="r.id" :label="r.tenVaiTro" :value="r.id" />
          </el-select>
        </div>
        <div class="mb-2 mb-md-0">
          <el-select v-model="filterStatus" placeholder="Trạng thái" style="width: 170px;">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>
      </template>

      <!-- Action Templates -->
      <template #actions="{ row }">
        <div class="d-flex justify-content-center align-items-center gap-1">
          <el-tooltip content="Xem chi tiết" placement="top">
            <button class="btn-action-icon action-view" @click="handleView(row)">
              <i class="bi bi-eye"></i>
            </button>
          </el-tooltip>
          <el-tooltip content="Chỉnh sửa" placement="top">
            <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="handleEdit(row)">
              <i class="bi bi-pencil"></i>
            </button>
          </el-tooltip>
          <el-tooltip content="Gửi mã reset" placement="top">
            <button class="btn-action-icon action-email" :disabled="row.trangThai === 0"
              @click="handleSendPasswordReset(row)">
              <i class="bi bi-envelope"></i>
            </button>
          </el-tooltip>
          <el-switch :model-value="row.trangThai === 1" @change="(val) => handleUpdateStatus(row, val ? 1 : 0)"
            class="status-switch mx-1" />
        </div>
      </template>

      <!-- Cell Templates -->
      <template #cell-stt="{ index }">
        <span class="text-secondary smaller">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
      </template>

      <template #cell-maNhanVien="{ row }">
        <span class="text-primary smaller fw-bold">#{{ row.maNhanVien }}</span>
      </template>

      <template #cell-staff="{ row }">
        <div class="d-flex flex-column align-items-center">
          <div class="fw-bold text-dark">{{ row.tenNhanVien }}</div>
          <div class="smaller text-secondary">{{ row.email || '—' }}</div>
        </div>
      </template>

      <template #cell-role="{ row }">
        <el-tag :type="getRoleType(row.tenPhanQuyen)" size="small" effect="plain" class="fw-bold border-0">{{
          row.tenPhanQuyen || 'Chưa gán' }}</el-tag>
      </template>

      <template #cell-trangThai="{ row }">
        <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" size="small" effect="dark" class="fw-bold">
          {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
        </el-tag>
      </template>

      <template #cell-soDienThoai="{ row }">
        <span class="fw-bold text-dark" style="white-space: nowrap;">{{ row.soDienThoai || '—' }}</span>
      </template>
      <template #cell-diaChi="{ row }">
        <div class="text-secondary smaller text-truncate text-center mx-auto" style="max-width: 250px;" :title="row.diaChi">{{ row.diaChi ||
          '—' }}</div>
      </template>
    </BaseTable>





    <BaseModal v-model="detailVisible" title="Hồ sơ nhân viên" icon="bi bi-person-workspace" width="600px" isDetail
      onlyCancel>
      <div v-if="selectedItem" class="p-0">
        <!-- Profile Header (Colorless) -->
        <div class="p-4 border-bottom bg-white d-flex align-items-center gap-4">
          <div class="shadow-sm border d-flex align-items-center justify-content-center bg-light text-secondary"
            style="width: 100px; height: 100px; font-size: 2.5rem; font-weight: bold;">
            {{ selectedItem.tenNhanVien?.charAt(0).toUpperCase() }}
          </div>
          <div class="flex-grow-1">
            <div class="d-flex align-items-center gap-2 mb-1">
              <h3 class="fw-bold m-0 text-dark">{{ selectedItem.tenNhanVien }}</h3>
              <span class="text-secondary small fw-bold">#{{ selectedItem.maNhanVien }}</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <el-tag type="info" effect="plain" round size="small">{{ selectedItem.tenPhanQuyen }}</el-tag>
              <el-tag :type="selectedItem.trangThai === 1 ? 'info' : 'plain'" effect="plain" round size="small">
                {{ selectedItem.trangThai === 1 ? 'HOẠT ĐỘNG' : 'NGỪNG HOẠT ĐỘNG' }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- Details Body (Colorless) -->
        <div class="p-4 bg-white">
          <div class="row g-4">
            <div class="col-12">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Thông tin tài khoản</h6>
              <div class="row g-3">
                <div class="col-6">
                  <div class="p-3 border bg-white">
                    <div class="text-secondary small mb-1">TÊN ĐĂNG NHẬP</div>
                    <div class="fw-bold text-dark">{{ selectedItem.tenDangNhap || '—' }}</div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="p-3 border bg-white">
                    <div class="text-secondary small mb-1">EMAIL CÔNG VIỆC</div>
                    <div class="fw-bold text-dark text-truncate">{{ selectedItem.email || '—' }}</div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="p-3 border bg-white">
                    <div class="text-secondary small mb-1">SỐ ĐIỆN THOẠI</div>
                    <div class="fw-bold text-dark">{{ selectedItem.soDienThoai || '—' }}</div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="p-3 border bg-white">
                    <div class="text-secondary small mb-1">CHỨC VỤ</div>
                    <div class="fw-bold text-dark">{{ selectedItem.tenChucVu || 'Nhân viên' }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-12">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Hiệu suất làm việc</h6>
              <div class="row g-3">
                <div class="col-6">
                  <div class="p-3 border bg-white text-center">
                    <div class="text-secondary small mb-1">VÉ ĐÃ BÁN</div>
                    <div class="fw-bold fs-4 text-dark">{{ selectedItem.tongVeBan || 0 }}</div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="p-3 border bg-white text-center">
                    <div class="text-secondary small mb-1">TỔNG DOANH THU</div>
                    <div class="fw-bold fs-4 text-dark">{{ formatCurrency(selectedItem.doanhSoCuaHang || 0) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-12">
              <div class="p-3 border bg-light">
                <h6 class="text-uppercase small fw-bold text-secondary mb-1">ĐỊA CHỈ</h6>
                <p class="text-dark mb-0 small">{{ selectedItem.diaChi || 'Chưa cập nhật địa chỉ' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>

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
        <div v-for="role in roles" :key="role.id" class="p-3 border rounded-3 bg-light-subtle">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="d-flex align-items-center gap-2">
              <i :class="[role.icon, 'fs-5']" :style="{ color: `var(--el-color-${role.color})` }"></i>
              <el-tag :type="role.color" effect="dark" round>{{ role.name }}</el-tag>
            </div>
            <el-button size="small" :icon="Edit" text class="text-indigo-500">Sửa</el-button>
          </div>
          <div class="d-flex flex-wrap gap-2">
            <el-tag v-for="(perm, idx) in role.permissions" :key="idx" type="info" effect="plain" size="small" round>
              {{ perm }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<style scoped>
.staff-profile-container {
  margin: -20px;
}

.profile-header {
  border-radius: 0 0 30px 30px;
}

.header-decoration {
  position: absolute;
  top: -50px;
  right: -50px;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 800;
}

.tiny-id {
  font-size: 11px;
  letter-spacing: 0.5px;
}

.icon-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-square {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.bg-success-light {
  background: #f0fdf4;
}

.bg-danger-light {
  background: #fef2f2;
}

.letter-spacing-tight {
  letter-spacing: -1px;
}

.avatar-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px;
}

.detail-item {
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.detail-item:last-child {
  border-bottom: none;
}
</style>
