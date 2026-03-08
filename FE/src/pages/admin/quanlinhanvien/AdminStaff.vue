<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { User, Edit, Lock, Key, Setting, Search, Refresh } from '@element-plus/icons-vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

import BaseTable from '@/components/common/BaseTable.vue';
import CCCDScanner from '@/components/common/CCCDScanner.vue'; // <-- Integrate Scanner
import { nhanVienService } from '@/services/api/admin/nhanVienService';
import { phanQuyenService } from '@/services/api/admin/phanQuyenService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import BaseModal from '@/components/common/BaseModal.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';

const staff = ref([]);
const loading = ref(false);
const searchQuery = ref('');

const staffColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã NV', key: 'maNhanVien', width: '100px' },
  { label: 'Nhân viên', key: 'staff', minWidth: '180px' },
  { label: 'Số điện thoại', key: 'soDienThoai', width: '120px' },
  { label: 'Email', key: 'email', width: '180px' },
  { label: 'Vai trò', key: 'role', width: '130px' },
  { label: 'Địa chỉ', key: 'diaChi', minWidth: '200px' },
  { label: 'Trạng thái', key: 'trangThai', width: '140px' },
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
watch([searchQuery, filterRole, filterStatus, currentPage, pageSize], fetchStaff);
</script>

<template>
  <div class="admin-staff-page">
    <AdminTableLayout
      title="Quản lý nhân viên"
      titleIcon="bi bi-people-fill"
      addButtonLabel="Thêm nhân viên"
      :data="staff"
      :loading="loading"
      :total="totalElements"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="handleAdd"
      @reset-filter="() => { searchQuery = ''; filterRole = ''; filterStatus = ''; }"
    >
      <template #header-actions-left>
        <div class="d-flex align-items-center gap-2">
          <ExcelActions module="nhan-vien" @import-success="fetchStaff" />
          <el-button class="btn-cine-secondary" :icon="Setting" @click="roleDialogVisible = true" round>Vai trò & Quyền</el-button>
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
          <el-select v-model="filterRole" placeholder="Chọn vai trò" style="width: 200px;" size="default">
            <el-option label="Tất cả vai trò" value="all" />
            <el-option v-for="r in roles" :key="r.id" :label="r.tenVaiTro" :value="r.id" />
          </el-select>
        </div>
        <div class="filter-item">
           <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="filterStatus" placeholder="Chọn trạng thái" style="width: 200px;" size="default">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đang hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>
      </template>

      <template #content>
        <BaseTable
          :data="staff"
          :columns="staffColumns"
          :loading="loading"
          :total="totalElements"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          :hide-pagination="true"
          @edit="handleEdit"
          @delete="handleDelete"
          @update-status="({ row, val }) => handleUpdateStatus(row, val ? 1 : 0)"
        >
          <template #actions="{ row }">
            <div class="d-flex justify-content-center align-items-center gap-2">
                <el-tooltip content="Xem chi tiết" placement="top">
                  <button class="btn-action-icon action-view" @click="handleView(row)">
                    <i class="bi bi-eye fs-6"></i>
                  </button>
                </el-tooltip>
                <el-tooltip content="Chỉnh sửa" placement="top">
                  <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="handleEdit(row)">
                    <i class="bi bi-pencil fs-6"></i>
                  </button>
                </el-tooltip>
                <el-tooltip content="Gửi email đặt lại mật khẩu" placement="top">
                  <button class="btn-action-icon action-email" :disabled="row.trangThai === 0" @click="handleSendPasswordReset(row)">
                    <i class="bi bi-envelope fs-6"></i>
                  </button>
                </el-tooltip>
                <el-switch
                  :model-value="row.trangThai === 1"
                  @change="(val) => handleUpdateStatus(row, val ? 1 : 0)"
                  class="status-switch mx-1"
                  inactive-color="#ff4949"
                />
            </div>
          </template>

          <template #cell-trangThai="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" size="small" round>
              {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
            </el-tag>
          </template>
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-maNhanVien="{ row }">
            <span class="text-secondary small fw-bold">#{{ row.maNhanVien }}</span>
          </template>

          <template #cell-staff="{ row }">
            <div class="fw-bold text-dark">{{ row.tenNhanVien }}</div>
          </template>

          <template #cell-soDienThoai="{ row }">
            <div class="text-secondary small">{{ row.soDienThoai || '—' }}</div>
          </template>

          <template #cell-email="{ row }">
            <div class="text-secondary small">{{ row.email || '—' }}</div>
          </template>

          <template #cell-role="{ row }">
            <el-tag :type="getRoleType(row.tenPhanQuyen)" size="small" effect="light" round>{{ row.tenPhanQuyen || 'Chưa gán' }}</el-tag>
          </template>

          <template #cell-diaChi="{ row }">
            <div class="text-secondary small text-truncate" style="max-width: 250px;" :title="row.diaChi">{{ row.diaChi || '—' }}</div>
          </template>

        </BaseTable>
      </template>
    </AdminTableLayout>





    <!-- Staff Detail Modal -->
    <BaseModal v-model="detailVisible" title="Chi tiết nhân viên" icon="bi bi-person-badge" width="500px">
      <div v-if="selectedItem" class="p-3">
        <div class="d-flex align-items-center gap-3 mb-4 p-3 bg-light rounded-4">
          <div class="avatar-circle" :style="{ backgroundColor: getAvatarColor(selectedItem.tenPhanQuyen) }">
            {{ selectedItem.tenNhanVien?.charAt(0).toUpperCase() }}
          </div>
          <div>
            <h5 class="m-0 fw-bold">{{ selectedItem.tenNhanVien }}</h5>
            <el-tag :type="getRoleType(selectedItem.tenPhanQuyen)" size="small" round>{{ selectedItem.tenPhanQuyen }}</el-tag>
          </div>
        </div>
        <div class="detail-list">
          <div class="detail-item mb-3">
            <div class="lbl text-secondary small">Email</div>
            <div class="val fw-semibold">{{ selectedItem.email }}</div>
          </div>
          <div class="detail-item mb-3">
            <div class="lbl text-secondary small">Tên đăng nhập</div>
            <div class="val fw-semibold"><code>{{ selectedItem.tenDangNhap || selectedItem.maNhanVien }}</code></div>
          </div>
          <div class="detail-item mb-3">
            <div class="lbl text-secondary small">Số điện thoại</div>
            <div class="val fw-semibold">{{ selectedItem.soDienThoai || '—' }}</div>
          </div>
          <div class="detail-item mb-3">
            <div class="lbl text-secondary small">Ngày tham gia</div>
            <div class="val fw-semibold">{{ selectedItem.ngayTao ? new Date(selectedItem.ngayTao).toLocaleDateString('vi-VN') : '—' }}</div>
          </div>
          <div class="detail-item mb-3">
            <div class="lbl text-secondary small">Địa chỉ</div>
            <div class="val fw-semibold">{{ selectedItem.diaChi || '—' }}</div>
          </div>
          <div class="detail-item">
            <div class="lbl text-secondary small">Trạng thái</div>
            <el-tag :type="selectedItem.trangThai === 1 ? 'success' : 'info'" size="small" round>
              {{ selectedItem.trangThai === 1 ? 'Đang hoạt động' : 'Ngừng hoạt động' }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">Đóng</el-button>
        <el-button type="primary" @click="handleEdit(selectedItem)">Chỉnh sửa</el-button>
      </template>
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
