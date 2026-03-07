<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { voucherService } from '@/services/api/admin/voucherService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import {
  PriceTag, Document, Calendar, Check, ArrowLeft,
  Refresh, Opportunity, InfoFilled, Money,
  User, Message, Phone, Filter, Search
} from '@element-plus/icons-vue';
import { khachHangService } from '@/services/api/admin/khachHangService';
import BaseTable from '@/components/common/BaseTable.vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const saving = ref(false);

const isEdit = computed(() => !!route.params.id);

const form = reactive({
  maPhieuGiamGia: '',
  tenPhieu: '',
  loaiPhieu: 1,
  kieuPhatHanh: 0,
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
  idKhachHangs: [], // Mảng ID khách hàng nhận phiếu
});

import { watch } from 'vue'; // Ensure watch is imported if not already handled by vue-auto-imports (though it is here)

// Logic reset giá trị khi đổi loại phiếu
watch(() => form.loaiPhieu, (val) => {
  if (val === 1) form.soTienGiam = 0;
  else { form.phanTramGiamGia = 0; form.giamToiDa = 0; }
});

// State cho bảng khách hàng (khi là phiếu cá nhân)
const customers = ref([]);
const customerLoading = ref(false);
const customerSearch = ref('');
const totalCustomers = ref(0);
const selectedCustomers = ref([]); // Row data
const customerTableRef = ref(null);

const customerColumns = [
  { type: 'selection', width: '55px', key: 'selection' },
  { label: 'Khách hàng', key: 'customer', width: '180px' },
  { label: 'Email', key: 'email', width: '220px' },
  { label: 'Số điện thoại', key: 'sdt', width: '150px' },
];

const fetchCustomers = async () => {
    customerLoading.value = true;
    try {
        const res = await khachHangService.getAll(customerSearch.value, 1, 0, 1000);
        const apiRes = res.data?.data || res.data;
        if (apiRes && apiRes.content) {
            customers.value = apiRes.content;
            totalCustomers.value = apiRes.totalElements;
        } else {
            customers.value = Array.isArray(apiRes) ? apiRes : [];
            totalCustomers.value = customers.value.length;
        }
    } catch (error) {
        console.error('Lỗi khi tải danh sách khách hàng:', error);
        notification.error('Không thể tải danh sách khách hàng');
    } finally {
        customerLoading.value = false;
    }
};

const handleSelectionChange = (selection) => {
    selectedCustomers.value = selection;
    form.idKhachHangs = selection.map(c => c.id);
    
    // Nếu là kieuPhatHanh = 1 (Cá nhân), tự động cập nhật số lượng
    if (form.kieuPhatHanh === 1) {
        form.soLuong = selection.length;
    }
};

watch(() => form.kieuPhatHanh, (newVal) => {
    if (newVal === 1) {
        fetchCustomers();
    } else {
        form.idKhachHangs = [];
        selectedCustomers.value = [];
    }
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
        <el-card shadow="never" class="card-premium">
          <template #header>
            <div class="premium-title"><i class="bi bi-ticket-perforated me-2"></i>Thông tin chương trình</div>
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
                  <el-input v-model="form.tenPhieu" placeholder="VD: Khai trương giảm 20%" :prefix-icon="Document"
                    size="large" />
                </el-form-item>
              </div>
              <div class="col-12">
                <el-form-item label="Điều kiện áp dụng">
                  <el-input v-model="form.dieuKienApDung" type="textarea" :rows="3"
                    placeholder="Nhập các điều kiện áp dụng..." />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Ngày bắt đầu *" required>
                  <el-date-picker v-model="form.ngayBatDau" type="datetime" class="w-100"
                    value-format="YYYY-MM-DDTHH:mm:ss" size="large" />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Ngày kết thúc *" required>
                  <el-date-picker v-model="form.ngayKetThuc" type="datetime" class="w-100"
                    value-format="YYYY-MM-DDTHH:mm:ss" size="large" />
                </el-form-item>
              </div>
            </div>
          </el-form>
        </el-card>

        <!-- Customer Selection Table (Only for Private Vouchers) -->
        <el-card shadow="never" class="card-premium mt-4" v-if="form.kieuPhatHanh === 1">
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <div class="premium-title"><i class="bi bi-people me-2"></i>Chọn khách hàng nhận phiếu</div>
              <div class="d-flex align-items-center gap-2">
                <el-input 
                  v-model="customerSearch" 
                  placeholder="Tìm khách hàng..." 
                  size="small" 
                  style="width: 200px"
                  :prefix-icon="Search"
                  clearable
                  @input="fetchCustomers"
                />
                <el-tag type="primary" effect="dark" round>{{ selectedCustomers.length }} khách đã chọn</el-tag>
              </div>
            </div>
          </template>
          
          <div class="customer-selection-wrapper" style="height: 400px;">
            <BaseTable
              ref="customerTableRef"
              :data="customers"
              :columns="customerColumns"
              :loading="customerLoading"
              :total="totalCustomers"
              :hide-pagination="true"
              :show-actions="false"
              v-model:selection="selectedCustomers"
              @selection-change="handleSelectionChange"
            >
              <template #cell-customer="{ row }">
                <div class="fw-bold text-dark small">{{ row.tenKhachHang }}</div>
              </template>
              <template #cell-email="{ row }">
                <div class="text-dark small"><i class="bi bi-envelope me-2 text-primary"></i>{{ row.email || '—' }}</div>
              </template>
              <template #cell-sdt="{ row }">
                <div class="text-secondary small"><i class="bi bi-telephone me-2"></i>{{ row.sdt || '—' }}</div>
              </template>
            </BaseTable>
          </div>
        </el-card>

        <el-card shadow="never" class="card-premium mt-4">
          <template #header>
            <div class="premium-title"><i class="bi bi-cash-stack me-2"></i>Cấu hình giảm giá</div>
          </template>
          <div class="row g-3">
            <div class="col-12">
              <el-form-item label="Loại giảm giá">
                <el-select v-model="form.loaiPhieu" style="width: 200px" size="large">
                  <el-option label="Phần trăm (%)" :value="1" />
                  <el-option label="Tiền mặt (đ)" :value="2" />
                </el-select>
              </el-form-item>
            </div>
            
            <template v-if="form.loaiPhieu === 1">
              <div class="col-md-6">
                <el-form-item label="Phần trăm giảm (%)">
                  <el-input-number v-model="form.phanTramGiamGia" :min="1" :max="100" class="w-100" size="large"
                    controls-position="right" />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Giảm tối đa (đ)">
                  <el-input-number v-model="form.giamToiDa" :min="0" :step="1000" class="w-100" size="large"
                    controls-position="right" />
                </el-form-item>
              </div>
            </template>
            
            <div class="col-12" v-else>
              <el-form-item label="Số tiền giảm (đ)">
                <el-input-number v-model="form.soTienGiam" :min="1000" :step="1000" class="w-100" size="large"
                  controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-md-6">
              <el-form-item label="Giá trị đơn tối thiểu (đ)">
                <el-input-number v-model="form.giaTriHoaDonToiThieu" :min="0" :step="10000" class="w-100" size="large"
                  controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-md-6">
              <el-form-item label="Tổng số lượng phát hành">
                <el-input-number 
                  v-model="form.soLuong" 
                  :min="form.kieuPhatHanh === 1 ? form.idKhachHangs.length : 1" 
                  :disabled="form.kieuPhatHanh === 1"
                  class="w-100" 
                  size="large" 
                  controls-position="right" 
                />
                <div v-if="form.kieuPhatHanh === 1" class="small text-primary mt-1">
                  * Tự động cập nhật theo số lượng khách đã chọn
                </div>
              </el-form-item>
            </div>
          </div>
        </el-card>
      </div>

      <div class="col-lg-4">
        <div class="sidebar-info">
          <el-card shadow="never" class="card-premium">
            <template #header>
              <div class="premium-title"><i class="bi bi-gear-fill me-2"></i>Trạng thái & Ghi chú</div>
            </template>
            <el-form label-position="top">

              <el-form-item label="Đối tượng phát hành">
                <el-radio-group v-model="form.kieuPhatHanh" :disabled="isEdit"
                  class="vertical-radio-group w-100 d-flex flex-column align-items-start gap-3 bg-light p-3 rounded-3 mt-1">
                  <el-radio :value="0">Công khai (Tất cả KH)</el-radio>
                  <el-radio :value="1">Cá nhân (Tặng riêng)</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="Quy tắc cộng dồn">
                <el-radio-group v-model="form.coChoCongDon"
                  class="vertical-radio-group w-100 d-flex flex-column align-items-start gap-3 bg-light p-3 rounded-3 mt-1">
                  <el-radio :value="1">Có, được phép cộng dồn</el-radio>
                  <el-radio :value="0">Không, chỉ dùng đơn lẻ</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="Ghi chú nội bộ">
                <el-input v-model="form.ghiChu" type="textarea" :rows="4"
                  placeholder="Nhập ghi chú hoặc hướng dẫn cho nhân viên..." />
              </el-form-item>
            </el-form>
          </el-card>

          <div class="mt-4 p-4 rounded-4 bg-white border shadow-sm">
            <div class="d-flex align-items-center gap-3 mb-3 text-dark">
              <div class="p-2 bg-primary bg-opacity-10 text-primary rounded-3">
                <el-icon :size="24">
                  <Opportunity />
                </el-icon>
              </div>
              <h5 class="m-0 fw-bold">Xem trước hiển thị</h5>
            </div>
            <div class="preview-voucher p-4 rounded-4 border text-dark mb-3">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="small text-secondary fw-bold text-uppercase ls-1">Voucher</span>
                <div class="small fw-bold text-primary">{{ form.maPhieuGiamGia || 'VOU-XXXXXX' }}</div>
              </div>
              <h6 class="fw-bold mb-1 fs-5">{{ form.tenPhieu || 'Tên chương trình' }}</h6>
              <div class="fs-2 fw-bold text-danger mb-2">
                {{ form.loaiPhieu === 1 ? (form.phanTramGiamGia || 0) + '%' : (form.soTienGiam || 0).toLocaleString() +
                  '₫' }}
                <span class="fs-6 text-secondary fw-normal ms-1">Giảm giá</span>
              </div>
              <div class="small text-secondary d-flex align-items-center">
                <el-icon class="me-1">
                  <Calendar />
                </el-icon>
                HSD: {{ form.ngayKetThuc ? new Date(form.ngayKetThuc).toLocaleDateString('vi-VN') : '—' }}
              </div>
            </div>
            <p class="small m-0 text-secondary italic text-center text-muted">* Voucher sẽ hiển thị cho khách hàng nếu
              trạng
              thái là đang hoạt động.</p>
          </div>
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

.card-premium {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
  margin-bottom: 24px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  background-color: #ffffff;
  padding: 18px 24px;
}

.premium-title {
  color: #1e293b;
  font-weight: 700;
  display: flex;
  align-items: center;
}

.premium-form {
  padding: 8px;
}

.ls-1 {
  letter-spacing: 1px;
}

.preview-voucher {
  background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
  position: relative;
  overflow: hidden;
}

.preview-voucher::after {
  content: "";
  position: absolute;
  right: -20px;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  background-color: #f8fafc;
  border-radius: 50%;
  box-shadow: inset 2px 0 5px rgba(0, 0, 0, 0.05);
}



.sidebar-info {
  position: sticky;
  top: 24px;
}

.page-header {
  backdrop-filter: blur(8px);
  z-index: 10;
}

/* Ghi đè luật height: 32px !important từ admin-premium.css */
:deep(.vertical-radio-group) {
  height: auto !important;
  border: 1px solid #e2e8f0;
  /* Thêm viền nhẹ cho đẹp */
}

/* Đảm bảo chữ của thẻ radio không bị cắt và nút radio căn giữa chữ */
:deep(.vertical-radio-group .el-radio) {
  height: auto !important;
  margin-right: 0 !important;
  white-space: normal !important;
  line-height: 1.5 !important;
  display: flex !important;
  align-items: flex-start !important;
}

/* Chỉnh lại nút tròn của radio cho khỏi bị lệch khi chữ rớt dòng */
:deep(.vertical-radio-group .el-radio__input) {
  margin-top: 2px;
}
</style>
