<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { Plus, User, Edit, Delete, Lock, Key, Setting, Search, Phone, Message, Clock, Check, Close } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import { nhanVienService } from '@/services/api/admin/nhanVienService';

const staff = ref([]);
const selectedStaff = ref([]);
const loading = ref(false);

const staffColumns = [
  { label: 'NHÂN VIÊN', key: 'staff', minWidth: '250px' },
  { label: 'TÊN ĐĂNG NHẬP', key: 'username', width: '180px' },
  { label: 'VAI TRÒ', key: 'role', width: '150px' },
  { label: 'SỐ ĐIỆN THOẠI', key: 'phone', width: '150px', align: 'center' },
  { label: 'NGÀY THAM GIA', key: 'joinDate', width: '150px', align: 'center' },
  { label: 'TRẠNG THÁI', key: 'status', width: '150px', align: 'center' },
];

const roles = ref([
  { name: 'Quản trị viên', permissions: ['Toàn quyền hệ thống', 'Quản lý người dùng', 'Cấu hình hệ thống', 'Báo cáo & thống kê'], color: 'danger', icon: '👑' },
  { name: 'Quản lý rạp', permissions: ['Quản lý lịch chiếu', 'Quản lý phim', 'Quản lý giá vé', 'Báo cáo doanh thu'], color: 'warning', icon: '🎬' },
  { name: 'Nhân viên', permissions: ['Bán vé tại quầy', 'Check-in khách hàng', 'Hỗ trợ khách hàng'], color: 'primary', icon: '🎫' },
]);

const dialogVisible = ref(false);
const roleDialogVisible = ref(false);
const searchQuery = ref('');
const filterRole = ref('');
const filterStatus = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

const staffForm = ref({
  tenNhanVien: '',
  email: '',
  soDienThoai: '',
  cccd: '',
  ngaySinh: '',
  queQuan: '',
  gioiTinh: 1,
  chucVu: '',
  anhNhanVien: '',
  idPhanQuyen: '',
  trangThai: 1,
  matKhau: '',
});

const resetForm = () => {
  staffForm.value = {
    id: null,
    tenNhanVien: '',
    email: '',
    soDienThoai: '',
    cccd: '',
    ngaySinh: '',
    queQuan: '',
    gioiTinh: 1,
    chucVu: '',
    anhNhanVien: '',
    idPhanQuyen: '',
    trangThai: 1,
    matKhau: '',
  };
};

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
    ElMessage.error('Không thể tải danh sách nhân viên');
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

const handleEdit = (row) => {
  staffForm.value = {
    id: row.id,
    tenNhanVien: row.tenNhanVien,
    email: row.email,
    soDienThoai: row.soDienThoai,
    cccd: row.cccd || '',
    ngaySinh: row.ngaySinh || '',
    queQuan: row.queQuan || '',
    gioiTinh: row.gioiTinh ?? 1,
    chucVu: row.chucVu || '',
    anhNhanVien: row.anhNhanVien || '',
    idPhanQuyen: row.idPhanQuyen || '',
    trangThai: row.trangThai,
    matKhau: '',
  };
  dialogVisible.value = true;
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`Bạn có chắc muốn xóa nhân viên "${row.tenNhanVien}"?`, 'Xác nhận xóa', {
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
    type: 'warning',
  }).then(async () => {
    try {
      await nhanVienService.delete(row.id);
      ElMessage.success('Đã xóa nhân viên thành công');
      fetchStaff();
    } catch {
      ElMessage.error('Xóa nhân viên thất bại');
    }
  }).catch(() => {});
};

const handleSave = async () => {
  if (!staffForm.value.tenNhanVien || !staffForm.value.email) {
    ElMessage.warning('Vui lòng điền đầy đủ thông tin bắt buộc');
    return;
  }
  try {
    const payload = { ...staffForm.value };
    delete payload.id;
    if (staffForm.value.id) {
      if (!payload.matKhau) delete payload.matKhau;
      await nhanVienService.update(staffForm.value.id, payload);
      ElMessage.success('Cập nhật nhân viên thành công');
    } else {
      await nhanVienService.create(payload);
      ElMessage.success('Thêm nhân viên thành công');
    }
    dialogVisible.value = false;
    resetForm();
    fetchStaff();
  } catch (error) {
    const msg = error.response?.data?.message || 'Lưu nhân viên thất bại';
    ElMessage.error(msg);
  }
};

const handleToggleStatus = async (row) => {
  const newStatus = row.trangThai === 1 ? 0 : 1;
  try {
    await nhanVienService.update(row.id, { ...row, trangThai: newStatus, matKhau: undefined });
    row.trangThai = newStatus;
    ElMessage.success(`Đã ${newStatus === 1 ? 'kích hoạt' : 'vô hiệu hóa'} tài khoản ${row.tenNhanVien}`);
  } catch {
    ElMessage.error('Thay đổi trạng thái thất bại');
  }
};

const openAddDialog = () => {
  resetForm();
  dialogVisible.value = true;
};

const selectedIds = computed(() => selectedStaff.value.map(item => item.id));

const handleBulkDelete = () => {
    ElMessageBox.confirm(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> nhân viên đã chọn?`,
        'Xóa hàng loạt',
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await Promise.all(selectedIds.value.map(id => nhanVienService.delete(id)));
            ElMessage.success(`Đã xóa ${selectedIds.value.length} nhân viên`);
            selectedStaff.value = [];
            fetchStaff();
        } catch (error) {
            ElMessage.error('Có lỗi khi xóa hàng loạt');
        }
    }).catch(() => {});
};

onMounted(fetchStaff);
watch([searchQuery, filterRole, filterStatus], fetchStaff);
</script>

<template>
  <div class="admin-staff-page">
    <AdminTableLayout
      title="Quản lý Nhân viên"
      titleIcon="bi bi-people-fill"
      addButtonLabel="Thêm nhân viên"
      :data="filteredStaff"
      :loading="loading"
      :total="filteredStaff.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openAddDialog"
      @reset-filter="() => { searchQuery = ''; filterRole = ''; filterStatus = ''; }"
    >
      <template #header-actions-left>
        <div class="d-flex align-items-center gap-2">
          <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
            Xóa {{ selectedIds.length }} nhân viên
          </el-button>
          <el-button class="btn-premium-secondary" :icon="Setting" @click="roleDialogVisible = true" round>Vai trò & Quyền</el-button>
        </div>
      </template>

      <template #filters>
        <div class="filter-item flex-grow-1" style="max-width: 350px;">
          <span class="filter-label text-dark small fw-bold mb-1 d-block">Tìm kiếm</span>
          <el-input
            v-model="searchQuery"
            placeholder="Tên, username, email..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="filter-item" style="width: 200px;">
          <span class="filter-label text-dark small fw-bold mb-1 d-block">Vai trò</span>
          <el-select v-model="filterRole" placeholder="Tất cả" size="default" class="w-100">
            <el-option label="Tất cả vai trò" value="all" />
            <el-option v-for="r in roles" :key="r.name" :label="r.name" :value="r.name" />
          </el-select>
        </div>
        <div class="filter-item" style="width: 200px;">
           <span class="filter-label text-dark small fw-bold mb-1 d-block">Trạng thái</span>
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
          <template #cell-staff="{ row }">
            <div class="d-flex align-items-center gap-3 text-start">
              <el-avatar :size="36" class="flex-shrink-0 border border-white shadow-sm" :style="{ background: getAvatarColor(row.tenPhanQuyen) }">
                {{ (row.tenNhanVien || '').charAt(0) }}
              </el-avatar>
              <div>
                <div class="fw-bold text-dark small">{{ row.tenNhanVien }}</div>
                <div class="text-secondary extra-small" style="font-size: 11px;">{{ row.email }}</div>
              </div>
            </div>
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
            <div class="d-flex align-items-center justify-content-center gap-2 cursor-pointer" @click="handleToggleStatus(row)" title="Bấm để đổi trạng thái">
              <span class="status-dot" :class="row.trangThai === 1 ? 'bg-success shadow-success-lite' : 'bg-secondary'"></span>
              <span class="extra-small" :class="row.trangThai === 1 ? 'text-success' : 'text-secondary'">{{ row.trangThai === 1 ? 'Đang hoạt động' : 'Ngừng hoạt động' }}</span>
            </div>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)" title="Kiểm tra/Sửa">
                <i class="bi bi-pencil fs-6"></i>
              </button>
              <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)" title="Xóa nhân viên">
                <i class="bi bi-trash fs-6"></i>
              </button>
            </div>
          </template>
        </BaseTable>
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
          <el-input v-model="staffForm.tenNhanVien" placeholder="VD: Nguyễn Văn A" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="Địa chỉ Email" required>
          <el-input v-model="staffForm.email" placeholder="vanna@cineops.com" :prefix-icon="Message" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Số điện thoại" required>
              <el-input v-model="staffForm.soDienThoai" placeholder="0901234567" :prefix-icon="Phone" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="CCCD" required>
              <el-input v-model="staffForm.cccd" placeholder="012345678901" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày sinh" required>
              <el-date-picker v-model="staffForm.ngaySinh" type="date" class="w-100" value-format="YYYY-MM-DD" placeholder="Chọn ngày" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giới tính">
              <el-select v-model="staffForm.gioiTinh" class="w-100">
                <el-option label="Nam" :value="1" />
                <el-option label="Nữ" :value="0" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Chức vụ" required>
              <el-input v-model="staffForm.chucVu" placeholder="Nhân viên bán vé" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Quê quán">
              <el-input v-model="staffForm.queQuan" placeholder="Hà Nội" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Trạng thái">
              <el-select v-model="staffForm.trangThai" class="w-100">
                <el-option label="Đang hoạt động" :value="1" />
                <el-option label="Ngừng hoạt động" :value="0" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item :label="staffForm.id ? 'Mật khẩu mới (để trống nếu không đổi)' : 'Mật khẩu'" :required="!staffForm.id">
              <el-input v-model="staffForm.matKhau" type="password" placeholder="Tối thiểu 6 ký tự" show-password />
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
