<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { voucherService } from '@/services/api/admin/voucherService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import { 
  PriceTag, Document, Calendar, Check, ArrowLeft, 
  Refresh, Opportunity, InfoFilled, Money
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const saving = ref(false);

const isEdit = computed(() => !!route.params.id);

const form = reactive({
  maPhieuGiamGia: '',
  tenPhieu: '',
  loaiPhieu: 1,
  phanTramGiamGia: 0,
  soTienGiam: 0,
  giamToiDa: 0,
  giaTriHoaDonToiThieu: 0,
  soLuong: 0,
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: 1,
  coChoCongDon: 0,
  dieuKienApDung: '',
  ghiChu: '',
});

const rules = {
  tenPhieu: [{ required: true, message: 'Vui lòng nhập tên chương trình', trigger: 'blur' }],
  loaiPhieu: [{ required: true, message: 'Vui lòng chọn loại giảm giá', trigger: 'change' }],
  soLuong: [{ required: true, message: 'Vui lòng nhập số lượng', trigger: 'blur' }],
  ngayBatDau: [{ required: true, message: 'Vui lòng chọn ngày bắt đầu', trigger: 'change' }],
  ngayKetThuc: [{ required: true, message: 'Vui lòng chọn ngày kết thúc', trigger: 'change' }],
};

const generateVoucherCode = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let code = 'VOU-';
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return code;
};

const fetchVoucherDetail = async () => {
  if (!isEdit.value) {
    form.maPhieuGiamGia = generateVoucherCode();
    return;
  }
  loading.value = true;
  try {
    const res = await voucherService.getById(route.params.id);
    const row = res.data.data || res.data;
    if (row) {
      Object.assign(form, row);
    }
  } catch {
    notification.error('Không thể tải thông tin phiếu giảm giá');
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!form.tenPhieu || !form.ngayBatDau || !form.ngayKetThuc) {
    notification.validationError('Vui lòng điền đầy đủ các thông tin bắt buộc');
    return;
  }

  try {
    if (isEdit.value) await confirmDialog.update('phiếu giảm giá');
    else await confirmDialog.add('phiếu giảm giá');
  } catch { return; }

  saving.value = true;
  try {
    if (isEdit.value) {
      await voucherService.update(route.params.id, form);
      notification.updateSuccess('phiếu giảm giá');
    } else {
      await voucherService.create(form);
      notification.addSuccess('phiếu giảm giá');
    }
    router.push('/admin/vouchers');
  } catch (error) {
    notification.error(error.response?.data?.message || 'Lưu phiếu giảm giá thất bại');
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  fetchVoucherDetail();
});
</script>

<template>
  <div class="voucher-form-page p-4">
    <div class="page-header d-flex justify-content-between align-items-center mb-4">
      <div>
        <el-button :icon="ArrowLeft" link @click="router.back()" class="mb-2">Quay lại</el-button>
        <h3 class="fw-bold text-dark mb-0">
          {{ isEdit ? 'Chỉnh sửa voucher: ' + form.tenPhieu : 'Phát hành voucher mới' }}
        </h3>
      </div>
      <div class="d-flex gap-2">
        <el-button @click="router.back()">Hủy bỏ</el-button>
        <el-button type="primary" :icon="Check" @click="handleSubmit" :loading="saving">
          {{ isEdit ? 'Lưu thay đổi' : 'Phát hành ngay' }}
        </el-button>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-lg-8">
        <el-card shadow="never" class="rounded-4 border-0 shadow-sm">
          <template #header>
            <div class="fw-bold"><i class="bi bi-ticket-perforated me-2"></i>Thông tin chương trình</div>
          </template>
          <el-form :model="form" label-position="top" class="premium-form">
            <div class="row g-3">
              <div class="col-md-6">
                <el-form-item label="Mã Voucher" required>
                  <el-input v-model="form.maPhieuGiamGia" readonly :prefix-icon="PriceTag" size="large">
                    <template #append v-if="!isEdit">
                      <el-button @click="form.maPhieuGiamGia = generateVoucherCode()">Tạo mới</el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Tên chương trình *" required>
                  <el-input v-model="form.tenPhieu" placeholder="VD: Khai trương giảm 20%" :prefix-icon="Document" size="large" />
                </el-form-item>
              </div>
              <div class="col-12">
                <el-form-item label="Điều kiện áp dụng">
                  <el-input v-model="form.dieuKienApDung" type="textarea" :rows="3" placeholder="Nhập các điều kiện áp dụng..." />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Ngày bắt đầu *" required>
                  <el-date-picker v-model="form.ngayBatDau" type="datetime" class="w-100" value-format="YYYY-MM-DDTHH:mm:ss" size="large" />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Ngày kết thúc *" required>
                  <el-date-picker v-model="form.ngayKetThuc" type="datetime" class="w-100" value-format="YYYY-MM-DDTHH:mm:ss" size="large" />
                </el-form-item>
              </div>
            </div>
          </el-form>
        </el-card>

        <el-card shadow="never" class="rounded-4 border-0 shadow-sm mt-4">
          <template #header>
            <div class="fw-bold"><i class="bi bi-cash-stack me-2"></i>Cấu hình giảm giá</div>
          </template>
          <div class="row g-3">
            <div class="col-md-4">
              <el-form-item label="Loại giảm giá">
                <el-select v-model="form.loaiPhieu" class="w-100" size="large">
                  <el-option label="Phần trăm (%)" :value="1" />
                  <el-option label="Tiền mặt (đ)" :value="2" />
                </el-select>
              </el-form-item>
            </div>
            <div class="col-md-4" v-if="form.loaiPhieu === 1">
              <el-form-item label="Phần trăm giảm (%)">
                <el-input-number v-model="form.phanTramGiamGia" :min="1" :max="100" class="w-100" size="large" controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-md-4" v-if="form.loaiPhieu === 1">
              <el-form-item label="Giảm tối đa (đ)">
                <el-input-number v-model="form.giamToiDa" :min="0" :step="1000" class="w-100" size="large" controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-md-8" v-else>
              <el-form-item label="Số tiền giảm (đ)">
                <el-input-number v-model="form.soTienGiam" :min="1000" :step="1000" class="w-100" size="large" controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-md-6">
              <el-form-item label="Giá trị đơn tối thiểu (đ)">
                <el-input-number v-model="form.giaTriHoaDonToiThieu" :min="0" :step="10000" class="w-100" size="large" controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-md-6">
              <el-form-item label="Tổng số lượng phát hành">
                <el-input-number v-model="form.soLuong" :min="1" class="w-100" size="large" controls-position="right" />
              </el-form-item>
            </div>
          </div>
        </el-card>
      </div>

      <div class="col-lg-4">
        <el-card shadow="never" class="rounded-4 border-0 shadow-sm">
          <template #header>
            <div class="fw-bold"><i class="bi bi-gear-fill me-2"></i>Trạng thái & Ghi chú</div>
          </template>
          <el-form label-position="top">
            <el-form-item label="Trạng thái voucher">
              <el-select v-model="form.trangThai" class="w-100" size="large">
                <el-option label="Đang hoạt động" :value="1" />
                <el-option label="Tạm ngưng" :value="0" />
                <el-option label="Đã kết thúc" :value="2" disabled />
              </el-select>
            </el-form-item>
            <el-form-item label="Cho phép cộng dồn với voucher khác?">
              <el-radio-group v-model="form.coChoCongDon" class="mt-2">
                <el-radio :value="1">Có, được cộng dồn</el-radio>
                <el-radio :value="0">Không, dùng đơn lẻ</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="Ghi chú nội bộ">
              <el-input v-model="form.ghiChu" type="textarea" :rows="4" placeholder="Nhập ghi chú hoặc hướng dẫn cho nhân viên..." />
            </el-form-item>
          </el-form>
        </el-card>

        <div class="mt-4 p-4 rounded-4 bg-white border shadow-sm">
          <div class="d-flex align-items-center gap-3 mb-3 text-dark">
            <div class="p-2 bg-primary bg-opacity-10 text-primary rounded-3">
              <el-icon :size="24"><Opportunity /></el-icon>
            </div>
            <h5 class="m-0 fw-bold">Xem trước hiển thị</h5>
          </div>
          <div class="preview-voucher p-3 rounded-3 border bg-light text-dark mb-2">
            <div class="d-flex justify-content-between align-items-center mb-2">
               <span class="small text-secondary fw-bold text-uppercase ls-1">Voucher</span>
               <div class="small fw-bold text-primary">{{ form.maPhieuGiamGia || 'VOU-XXXXXX' }}</div>
            </div>
            <h6 class="fw-bold mb-1">{{ form.tenPhieu || 'Tên chương trình' }}</h6>
            <div class="fs-4 fw-bold text-danger mb-2">
               {{ form.loaiPhieu === 1 ? (form.phanTramGiamGia || 0) + '%' : (form.soTienGiam || 0).toLocaleString() + '₫' }}
               <span class="fs-6 text-secondary fw-normal">Giảm giá</span>
            </div>
            <div class="small text-secondary">HSD: {{ form.ngayKetThuc ? new Date(form.ngayKetThuc).toLocaleDateString('vi-VN') : '—' }}</div>
          </div>
          <p class="small m-0 text-secondary">* Voucher sẽ hiển thị cho khách hàng nếu trạng thái là đang hoạt động.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.voucher-form-page {
  background-color: #f8fafc;
  min-height: 100vh;
}
:deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  background-color: #ffffff;
}
.ls-1 { letter-spacing: 1px; }
</style>
