<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { Plus, User, Edit, Delete, Lock, Key, Setting, Search, Phone, Message, Clock, Check, Close } from '@element-plus/icons-vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
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
  { label: 'NHÂN VIÊN', key: 'staff', minWidth: '250px' },
  { label: 'EMAIL', key: 'email', minWidth: '400px' },
  { label: 'TÊN ĐĂNG NHẬP', key: 'username', width: '250px' },
  { label: 'VAI TRÒ', key: 'role', width: '250px' },
  { label: 'SỐ ĐIỆN THOẠI', key: 'phone', width: '200px' },
  { label: 'NGÀY THAM GIA', key: 'joinDate', width: '200px' },
  { label: 'TRẠNG THÁI', key: 'status', width: '200px' },
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
const detailStaffVisible = ref(false);
const selectedStaffDetail = ref(null);
const roleDialogVisible = ref(false);
const searchQuery = ref('');

const handleView = (row) => {
  selectedStaffDetail.value = row;
  detailStaffVisible.value = true;
};
const filterRole = ref('');
const filterStatus = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const phanQuyenList = ref([]);

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
  confirmDialog.delete('nhân viên', row.tenNhanVien).then(async () => {
    try {
      await nhanVienService.delete(row.id);
      notification.deleteSuccess('nhân viên');
      fetchStaff();
    } catch {
      notification.error('Xóa nhân viên thất bại');
    }
  }).catch(() => {});
};

const handleSave = async () => {
  // 1. Kiểm tra các trường bắt buộc trên FE
  const requiredFields = [
    { key: 'tenNhanVien', label: 'Họ và tên' },
    { key: 'email', label: 'Email' },
    { key: 'soDienThoai', label: 'Số điện thoại' },
    { key: 'cccd', label: 'CCCD' },
    { key: 'ngaySinh', label: 'Ngày sinh' },
    { key: 'chucVu', label: 'Chức vụ' },
    { key: 'idPhanQuyen', label: 'Vai trò hệ thống' }
  ];

  const missingFields = requiredFields
    .filter(f => !staffForm.value[f.key])
    .map(f => f.label);

  if (missingFields.length > 0) {
    notification.validationError(`Vui lòng điền: ${missingFields.join(', ')}`);
    return;
  }

  // Nếu là thêm mới, bắt buộc phải có mật khẩu
  if (!staffForm.value.id && !staffForm.value.matKhau) {
    notification.validationError('Vui lòng nhập mật khẩu cho nhân viên mới');
    return;
  }

  try {
    if (staffForm.value.id) {
      await confirmDialog.update('nhân viên');
    } else {
      await confirmDialog.add('nhân viên');
    }
  } catch { return; }

  try {
    const payload = { ...staffForm.value };
    delete payload.id;
    
    if (staffForm.value.id) {
      if (!payload.matKhau) delete payload.matKhau;
      await nhanVienService.update(staffForm.value.id, payload);
      notification.updateSuccess('nhân viên');
    } else {
      await nhanVienService.create(payload);
      notification.addSuccess('nhân viên');
    }
    dialogVisible.value = false;
    resetForm();
    fetchStaff();
  } catch (error) {
    // Xử lý lỗi từ Server (Lỗi validate của Spring Boot)
    const errorData = error.response?.data;
    if (errorData?.message && Array.isArray(errorData.message)) {
      notification.badRequest(errorData.message);
    } else if (errorData?.errors) {
      notification.badRequest(errorData.errors);
    } else {
      notification.error(errorData?.message || 'Lưu nhân viên thất bại');
    }
  }
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

const openAddDialog = () => {
  resetForm();
  dialogVisible.value = true;
};

// Handle data returned from Scanner
const handleCCCDScanned = (data) => {
  if (data.cccd) staffForm.value.cccd = data.cccd;
  if (data.name) staffForm.value.tenNhanVien = data.name;
  if (data.dob) staffForm.value.ngaySinh = data.dob;
};

const selectedIds = computed(() => selectedStaff.value.map(item => item.id));

const handleBulkDelete = () => {
    confirmDialog.custom(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> nhân viên đã chọn?`,
        'Xóa hàng loạt',
        'Đồng ý'
    ).then(async () => {
        try {
            await Promise.all(selectedIds.value.map(id => nhanVienService.delete(id)));
            notification.success(`Đã xóa ${selectedIds.value.length} nhân viên`);
            selectedStaff.value = [];
            fetchStaff();
        } catch (error) {
            notification.error('Có lỗi khi xóa hàng loạt');
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

          <template #actions="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Xem chi tiết" placement="top">
                <button class="btn-action-icon btn-action-view" @click="handleView(row)">
                  <i class="bi bi-eye fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Kiểm tra/Sửa" placement="top">
                <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)">
                  <i class="bi bi-pencil fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Xóa nhân viên" placement="top">
                <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)">
                  <i class="bi bi-trash fs-6"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <BaseModal
      v-model="detailStaffVisible"
      title="Chi tiết nhân viên"
      icon="bi bi-person-badge-fill"
      width="600px"
    >
      <div v-if="selectedStaffDetail" class="p-2">
        <div class="d-flex align-items-center gap-4 mb-4 pb-4 border-bottom">
          <el-avatar :size="80" class="border border-white shadow-sm" :style="{ background: getAvatarColor(selectedStaffDetail.tenPhanQuyen) }">
            {{ (selectedStaffDetail.tenNhanVien || '').charAt(0) }}
          </el-avatar>
          <div class="flex-grow-1">
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <h4 class="fw-bold text-dark mb-1">{{ selectedStaffDetail.tenNhanVien }}</h4>
                <div class="text-secondary small mb-2">Mã NV: <span class="fw-bold">{{ selectedStaffDetail.maNhanVien }}</span></div>
              </div>
              <el-tag :type="getRoleType(selectedStaffDetail.tenPhanQuyen)" effect="dark" round>{{ selectedStaffDetail.tenPhanQuyen }}</el-tag>
            </div>
            <div class="d-flex align-items-center gap-3 mt-1">
              <el-tag :type="selectedStaffDetail.trangThai === 1 ? 'success' : 'info'" size="small" round>
                {{ selectedStaffDetail.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-envelope me-2 text-primary"></i>Email cá nhân</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.email || '—' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-telephone me-2 text-primary"></i>Số điện thoại</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.soDienThoai || '—' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-briefcase me-2 text-primary"></i>Chức vụ</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.chucVu || '—' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-card-text me-2 text-primary"></i>Số CCCD</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.cccd || '—' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-calendar-event me-2 text-primary"></i>Ngày sinh</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.ngaySinh || '—' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-gender-ambiguous me-2 text-primary"></i>Giới tính</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.gioiTinh === 1 ? 'Nam' : 'Nữ' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-geo-alt me-2 text-primary"></i>Quê quán</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.queQuan || '—' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-clock me-2 text-primary"></i>Ngày tham gia</div>
              <div class="val fw-semibold">{{ selectedStaffDetail.ngayTao ? new Date(selectedStaffDetail.ngayTao).toLocaleDateString('vi-VN') : '—' }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
      </template>
    </BaseModal>

    <!-- Add/Edit Staff Dialog -->
    <BaseModal
      v-model="dialogVisible"
      :title="staffForm.id ? 'Chỉnh sửa Nhân viên' : 'Thêm Nhân viên mới'"
      :icon="staffForm.id ? 'bi bi-person-gear' : 'bi bi-person-plus'"
      width="660px"
      confirmText="Lưu lại"
      @confirm="handleSave"
    >
      <template #header-extra v-if="!staffForm.id">
        <CCCDScanner @scanned="handleCCCDScanned" />
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
              <el-date-picker 
                v-model="staffForm.ngaySinh" 
                type="date" 
                class="w-100" 
                value-format="YYYY-MM-DD" 
                placeholder="Chọn ngày" 
                :disabled-date="disabledDate"
              />
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
              <el-select 
                v-model="staffForm.chucVu" 
                placeholder="Chọn hoặc nhập chức vụ" 
                filterable 
                allow-create
                default-first-option
                class="w-100"
              >
                <el-option v-for="item in chucVuOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Quê quán">
              <el-input v-model="staffForm.queQuan" placeholder="Hà Nội" />
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Ảnh nhân viên (URL)">
              <el-input v-model="staffForm.anhNhanVien" placeholder="https://example.com/anh.jpg" clearable>
                <template #prefix><i class="bi bi-image"></i></template>
              </el-input>
            </el-form-item>
          </div>
          <div class="col-6" v-if="staffForm.id">
            <el-form-item label="Trạng thái">
              <el-select v-model="staffForm.trangThai" class="w-100">
                <el-option label="Đang hoạt động" :value="1" />
                <el-option label="Ngừng hoạt động" :value="0" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Vai trò hệ thống" required>
              <el-select v-model="staffForm.idPhanQuyen" class="w-100" placeholder="Chọn vai trò">
                <el-option v-for="r in roles" :key="r.id" :label="r.tenVaiTro" :value="r.id" />
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
