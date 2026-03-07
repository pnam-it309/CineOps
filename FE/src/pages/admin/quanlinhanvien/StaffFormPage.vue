<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { nhanVienService } from '@/services/api/admin/nhanVienService';
import { phanQuyenService } from '@/services/api/admin/phanQuyenService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import CCCDScanner from '@/components/common/CCCDScanner.vue';
import { 
  User, Message, Phone, ArrowLeft, Check, 
  Picture
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const resetLoading = ref(false);
const roles = ref([]);
const chucVuOptions = ref([]);

const isEdit = computed(() => !!route.params.id);

const staffForm = reactive({
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

const fetchRoles = async () => {
  try {
    const res = await phanQuyenService.getAllRoles();
    if (res.data && res.data.data) roles.value = res.data.data;
  } catch (error) {
    console.error('Không thể tải danh sách vai trò', error);
  }
};

const fetchChucVu = async () => {
  try {
    const res = await nhanVienService.getChucVu();
    if (res.data && res.data.data) chucVuOptions.value = res.data.data;
  } catch (error) {
    console.error('Không thể tải danh sách chức vụ', error);
  }
};

const fetchStaffDetail = async () => {
  if (!isEdit.value) return;
  loading.value = true;
  try {
    const res = await nhanVienService.getById(route.params.id);
    const row = res.data.data || res.data;
    if (row) {
      Object.assign(staffForm, {
        ...row,
        matKhau: '', // Don't show password
      });
    }
  } catch {
    notification.error('Không thể tải thông tin nhân viên');
  } finally {
    loading.value = false;
  }
};

const handleCCCDScanned = (data) => {
  if (data.cccd) staffForm.cccd = data.cccd;
  if (data.name) staffForm.tenNhanVien = data.name;
  if (data.dob) staffForm.ngaySinh = data.dob;
};

const handleSave = async () => {
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
    .filter(f => !staffForm[f.key])
    .map(f => f.label);

  if (missingFields.length > 0) {
    notification.validationError(`Vui lòng điền: ${missingFields.join(', ')}`);
    return;
  }

  try {
    if (isEdit.value) await confirmDialog.update('nhân viên');
    else await confirmDialog.add('nhân viên');
  } catch { return; }

  loading.value = true;
  try {
    const payload = { ...staffForm };
    if (isEdit.value) {
      if (!payload.matKhau) delete payload.matKhau;
      await nhanVienService.update(route.params.id, payload);
      notification.updateSuccess('nhân viên');
    } else {
      await nhanVienService.create(payload);
      notification.addSuccess('nhân viên');
    }
    router.push('/admin/staff');
  } catch (error) {
    const errorData = error.response?.data;
    notification.error(errorData?.message || 'Lưu nhân viên thất bại');
  } finally {
    loading.value = false;
  }
};

const handleSendPasswordReset = async () => {
  try {
    await confirmDialog.custom(`Bạn có chắc muốn gửi email đặt lại mật khẩu đến ${staffForm.email}?`, 'Xác nhận gửi email');
    resetLoading.value = true;
    await nhanVienService.resetPassword(route.params.id);
    notification.success('Email đặt lại mật khẩu đã được gửi đến ' + staffForm.email);
  } catch (e) {
    if (e !== 'cancel') {
      notification.error('Gửi email đặt lại mật khẩu thất bại');
    }
  } finally {
    resetLoading.value = false;
  }
};

const disabledDate = (time) => time.getTime() > Date.now();

onMounted(() => {
  fetchRoles();
  fetchChucVu();
  if (isEdit.value) fetchStaffDetail();
});
</script>

<template>
  <div class="staff-form-page p-4">
    <div class="page-header d-flex justify-content-between align-items-center mb-4">
      <div>
        <el-button :icon="ArrowLeft" link @click="router.back()" class="mb-2">Quay lại</el-button>
        <h3 class="fw-bold text-dark mb-0">
          {{ isEdit ? 'Chỉnh sửa nhân viên: ' + staffForm.tenNhanVien : 'Thêm nhân viên mới' }}
        </h3>
      </div>
      <div class="d-flex gap-2">
        <el-button @click="router.back()">Hủy bỏ</el-button>
        <el-button type="primary" :icon="Check" @click="handleSave" :loading="loading">
          {{ isEdit ? 'Lưu thay đổi' : 'Tạo nhân viên' }}
        </el-button>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-lg-8">
        <el-card shadow="never" class="rounded-4 border-0 shadow-sm">
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <div class="fw-bold"><i class="bi bi-person-lines-fill me-2"></i>Thông tin cá nhân</div>
              <CCCDScanner v-if="!isEdit" @scanned="handleCCCDScanned" />
            </div>
          </template>
          <el-form :model="staffForm" label-position="top" class="premium-form">
            <div class="row g-3">
              <div class="col-md-6">
                <el-form-item label="Họ và tên *" required>
                  <el-input v-model="staffForm.tenNhanVien" placeholder="VD: Nguyễn Văn A" :prefix-icon="User" size="large" />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Số điện thoại *" required>
                  <el-input v-model="staffForm.soDienThoai" placeholder="0901234567" :prefix-icon="Phone" size="large" />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Số CCCD *" required>
                  <el-input v-model="staffForm.cccd" placeholder="012345678901" size="large" />
                </el-form-item>
              </div>
              <div class="col-md-4">
                <el-form-item label="Ngày sinh *" required>
                  <el-date-picker v-model="staffForm.ngaySinh" type="date" class="w-100" value-format="YYYY-MM-DD" size="large" :disabled-date="disabledDate" />
                </el-form-item>
              </div>
              <div class="col-md-4">
                <el-form-item label="Giới tính">
                  <el-select v-model="staffForm.gioiTinh" class="w-100" size="large">
                    <el-option label="Nam" :value="1" />
                    <el-option label="Nữ" :value="0" />
                  </el-select>
                </el-form-item>
              </div>
              <div class="col-md-4">
                <el-form-item label="Quê quán">
                  <el-input v-model="staffForm.queQuan" placeholder="VD: Hà Nội" size="large" />
                </el-form-item>
              </div>
              <div class="col-12">
                <el-form-item label="Ảnh chân dung (URL)">
                  <el-input v-model="staffForm.anhNhanVien" placeholder="Link ảnh nhân viên..." :prefix-icon="Picture" size="large" />
                </el-form-item>
              </div>
            </div>
          </el-form>
        </el-card>
      </div>

      <div class="col-lg-4">
        <el-card shadow="never" class="rounded-4 border-0 shadow-sm">
          <template #header>
            <div class="fw-bold"><i class="bi bi-shield-lock me-2"></i>Tài khoản & Vai trò</div>
          </template>
          <el-form label-position="top">
            <el-form-item label="Mã nhân viên" v-if="isEdit">
              <el-input v-model="staffForm.maNhanVien" disabled size="large" />
            </el-form-item>
            <el-form-item label="Email đăng nhập *" required>
              <el-input v-model="staffForm.email" placeholder="vanna@cineops.com" :prefix-icon="Message" size="large" />
            </el-form-item>
            <el-form-item label="Vai trò hệ thống *" required>
              <el-select v-model="staffForm.idPhanQuyen" class="w-100" size="large">
                <el-option v-for="r in roles" :key="r.id" :label="r.tenVaiTro" :value="r.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="Chức vụ *" required>
              <el-select v-model="staffForm.chucVu" filterable allow-create class="w-100" size="large">
                <el-option v-for="item in chucVuOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>

            <div v-if="!isEdit" class="account-info-box mb-3 p-3 rounded">
               <p class="mb-0 text-secondary"><i class="bi bi-info-circle me-1"></i> Mật khẩu sẽ được hệ thống <strong>tạo ngẫu nhiên</strong> và gửi tới email trên sau khi tạo thành công.</p>
            </div>

            <div v-if="isEdit" class="password-reset-box mb-3 p-3 rounded">
               <p class="mb-2 text-secondary"><i class="bi bi-shield-lock me-1"></i> <strong>Quản lý mật khẩu:</strong></p>
               <p class="mb-2 text-secondary small">Admin không thể trực tiếp sửa mật khẩu. Vui lòng sử dụng chức năng gửi email để nhân viên tự đặt lại mật khẩu.</p>
               <el-button type="warning" size="small" @click="handleSendPasswordReset" :loading="resetLoading">
                 <i class="bi bi-envelope me-1"></i> Gửi email đặt lại mật khẩu
               </el-button>
            </div>

                      </el-form>
        </el-card>

        <div v-if="staffForm.anhNhanVien" class="mt-4 text-center">
           <el-avatar :size="150" :src="staffForm.anhNhanVien" class="shadow-sm border border-4 border-white" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.staff-form-page {
  background-color: #f8fafc;
  min-height: 100vh;
}
:deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  background-color: #ffffff;
}
.account-info-box {
  background-color: #f0f9ff;
  border: 1px solid #e0f2fe;
  font-size: 14px;
}

.password-reset-box {
  background-color: #fef3c7;
  border: 1px solid #fbbf24;
  font-size: 14px;
}
</style>
