<script setup>
import { ref, onMounted, reactive, computed, watch } from 'vue';
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
import BaseModal from '@/components/common/BaseModal.vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const saving = ref(false);
const previewVisible = ref(false);

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
  { type: 'selection', width: '55px', fixed: 'left' },
  { label: 'Mã KH', key: 'maKhachHang', width: '100px' },
  { label: 'Tên KH', key: 'tenKhachHang', minWidth: '180px' },
  { label: 'SĐT', key: 'sdt', width: '130px' },
  { label: 'Email', key: 'email', width: '220px' },
  { label: 'Ngày sinh', key: 'ngaySinh', width: '120px' },
  { label: 'Ngày mua gần nhất', key: 'buyLast', width: '160px' },
  { label: 'Số đơn', key: 'orders', width: '100px', sortable: true },
  { label: 'Số tiền đã tiêu', key: 'spent', width: '150px', sortable: true },
];

const fetchCustomers = async () => {
    customerLoading.value = true;
    try {
        // Remove hardcoded trangThai to ensure we see available customers
        const res = await khachHangService.getAll(customerSearch.value, null, 0, 1000);
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
}, { immediate: true });

const handleSelectAllCustomers = () => {
    if (customerTableRef.value) {
        customerTableRef.value.toggleSelectAll();
    }
};

const handleOpenPreview = () => {
    previewVisible.value = true;
};

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
        <el-button :icon="Opportunity" @click="handleOpenPreview">Xem trước</el-button>
        <el-button @click="router.back()">Hủy bỏ</el-button>
        <el-button type="primary" :icon="Check" @click="handleSubmit" :loading="saving">
          {{ isEdit ? 'Lưu thay đổi' : 'Phát hành ngay' }}
        </el-button>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-lg-8">
        <!-- Card 1: Thông tin chương trình -->
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

        <!-- Card 2: Trạng thái & Ghi chú -->
        <el-card shadow="never" class="card-premium mt-4">
          <template #header>
            <div class="premium-title"><i class="bi bi-gear-fill me-2"></i>Trạng thái & Ghi chú</div>
          </template>
          <el-form label-position="top">
            <div class="row g-3">
              <div class="col-md-6">
                <el-form-item label="Đối tượng phát hành">
                  <el-radio-group v-model="form.kieuPhatHanh" :disabled="isEdit"
                    class="vertical-radio-group w-100 d-flex flex-column align-items-start gap-3 bg-light p-3 rounded-3 mt-1">
                    <el-radio :value="0">Công khai (Tất cả)</el-radio>
                    <el-radio :value="1">Cá nhân (Chọn khách hàng)</el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Quy tắc cộng dồn">
                  <el-radio-group v-model="form.coChoCongDon"
                    class="vertical-radio-group w-100 d-flex flex-column align-items-start gap-3 bg-light p-3 rounded-3 mt-1">
                    <el-radio :value="1">Có, được phép cộng dồn</el-radio>
                    <el-radio :value="0">Không, chỉ dùng đơn lẻ</el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>
              <div class="col-12">
                <el-form-item label="Ghi chú nội bộ">
                  <el-input v-model="form.ghiChu" type="textarea" :rows="3"
                    placeholder="Nhập ghi chú hoặc hướng dẫn cho nhân viên..." />
                </el-form-item>
              </div>
            </div>
          </el-form>
        </el-card>
      </div>

      <div class="col-lg-4">
        <!-- Card 3: Cấu hình giảm giá -->
        <el-card shadow="never" class="card-premium">
          <template #header>
            <div class="premium-title"><i class="bi bi-cash-stack me-2"></i>Cấu hình giảm giá</div>
          </template>
          <div class="row g-3">
            <div class="col-12">
              <el-form-item label="Loại giảm giá">
                <el-select v-model="form.loaiPhieu" style="width: 100%" size="large">
                  <el-option label="Phần trăm (%)" :value="1" />
                  <el-option label="Tiền mặt (đ)" :value="2" />
                </el-select>
              </el-form-item>
            </div>
            
            <template v-if="form.loaiPhieu === 1">
              <div class="col-12">
                <el-form-item label="Phần trăm giảm (%)">
                  <el-input-number v-model="form.phanTramGiamGia" :min="1" :max="100" class="w-100" size="large"
                    controls-position="right" />
                </el-form-item>
              </div>
              <div class="col-12">
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
            <div class="col-12">
              <el-form-item label="Giá trị đơn tối thiểu (đ)">
                <el-input-number v-model="form.giaTriHoaDonToiThieu" :min="0" :step="10000" class="w-100" size="large"
                  controls-position="right" />
              </el-form-item>
            </div>
            <div class="col-12">
              <el-form-item label="Tổng số lượng phát hành">
                <el-input-number 
                  v-model="form.soLuong" 
                  :min="form.kieuPhatHanh === 1 ? (form.idKhachHangs.length || 1) : 1" 
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
    </div>

    <!-- Bảng khách hàng - Chỉ hiện khi Phát hành Cá nhân -->
    <div class="row mt-4" v-if="form.kieuPhatHanh === 1">
      <div class="col-12">
        <el-card shadow="never" class="card-premium customer-table-card">
          <div class="d-flex justify-content-between align-items-start mb-4">
            <div>
              <h5 class="fw-bold mb-1">Danh sách khách hàng nhận phiếu</h5>
              <p class="text-secondary small mb-0">Đã chọn: <span class="text-primary fw-bold">{{ selectedCustomers.length }}</span> khách hàng</p>
            </div>
            
            <div class="d-flex align-items-center gap-2 flex-wrap">
              <el-input 
                v-model="customerSearch" 
                placeholder="Tìm theo mã / tên / SĐT / email..." 
                size="default" 
                style="width: 320px"
                :prefix-icon="Search"
                clearable
                @input="fetchCustomers"
              />
              <div class="d-flex align-items-center gap-2">
                <span class="text-secondary small">Bộ lọc:</span>
                <el-select placeholder="Theo tháng" style="width: 140px">
                  <el-option label="Theo tháng" value="month" />
                </el-select>
                <el-date-picker type="month" placeholder="Chọn tháng" style="width: 160px" />
                <el-button type="default" @click="handleSelectAllCustomers">Chọn tất cả</el-button>
              </div>
            </div>
          </div>

          <div class="customer-full-table">
            <BaseTable
              ref="customerTableRef"
              :data="customers"
              :columns="customerColumns"
              :loading="customerLoading"
              :total="totalCustomers"
              :show-actions="false"
              v-model:selection="selectedCustomers"
              @selection-change="handleSelectionChange"
            >
              <template #cell-maKhachHang="{ row }">
                <span class="small text-secondary fw-bold">{{ row.maKhachHang }}</span>
              </template>
              <template #cell-tenKhachHang="{ row }">
                <span class="fw-bold text-dark">{{ row.tenKhachHang }}</span>
              </template>
              <template #cell-ngaySinh="{ row }">
                <span class="small">{{ row.ngaySinh || '—' }}</span>
              </template>
              <template #cell-buyLast="{ row }">
                <span class="small">{{ row.buyLast || '—' }}</span>
              </template>
              <template #cell-orders="{ row }">
                <span class="small">{{ row.orders || 0 }}</span>
              </template>
              <template #cell-spent="{ row }">
                <span class="small fw-bold">{{ (row.spent || 0).toLocaleString() }}₫</span>
              </template>
            </BaseTable>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Modal Xem trước hiển thị -->
    <BaseModal
      v-model="previewVisible"
      title="Xem trước hiển thị Voucher"
      width="500px"
      icon="bi bi-lightbulb"
    >
      <template #footer>
        <el-button type="primary" @click="previewVisible = false" class="w-100" style="height: 45px;">Đóng</el-button>
      </template>

      <div class="modern-voucher-preview p-4 rounded-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <span class="voucher-label">VOUCHER</span>
          <div class="voucher-code-styled">{{ form.maPhieuGiamGia || 'VOU-XXXXXX' }}</div>
        </div>
        
        <h4 class="voucher-program-name mb-3">{{ form.tenPhieu || 'Tên chương trình' }}</h4>
        
        <div class="voucher-discount-section mb-4">
          <span class="discount-value">
            {{ form.loaiPhieu === 1 ? (form.phanTramGiamGia || 0) + '%' : (form.soTienGiam || 0).toLocaleString() + '₫' }}
          </span>
          <span class="discount-label">Giảm giá</span>
        </div>

        <div class="voucher-expiry-info">
           <i class="bi bi-calendar3 me-2 text-primary"></i>
           HSD: {{ form.ngayKetThuc ? new Date(form.ngayKetThuc).toLocaleDateString('vi-VN') : '—' }}
        </div>
      </div>
      <p class="preview-footer-note mt-4 italic text-center text-muted mb-0">
        * Voucher này sẽ hiển thị cho khách hàng nếu trạng thái được kích hoạt.
      </p>
    </BaseModal>
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

.modern-voucher-preview {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  position: relative;
}

.voucher-label {
  font-size: 14px;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.5px;
}

.voucher-code-styled {
  font-weight: 700;
  color: #d63031;
}

.voucher-program-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 1.25rem;
}

.discount-value {
  font-size: 2.5rem;
  font-weight: 800;
  color: #d63031;
}

.discount-label {
  font-size: 1.1rem;
  color: #64748b;
  margin-left: 10px;
}

.voucher-expiry-info {
  font-size: 14px;
  color: #64748b;
  display: flex;
  align-items: center;
}

.preview-footer-note {
  font-size: 13px;
}

.customer-table-card {
  padding: 24px;
}

.customer-full-table :deep(.table-header-bg) {
  background-color: #1e293b !important;
  color: #ffffff !important;
  font-weight: 600;
  padding: 12px 0;
}

.page-header {
  position: sticky;
  top: 0;
  background: rgba(248, 250, 252, 0.9);
  backdrop-filter: blur(8px);
  z-index: 100;
}

:deep(.vertical-radio-group) {
  height: auto !important;
  border: 1px solid #e2e8f0;
}

:deep(.vertical-radio-group .el-radio) {
  height: auto !important;
  margin-right: 0 !important;
  white-space: normal !important;
  line-height: 1.5 !important;
  display: flex !important;
  align-items: flex-start !important;
}

:deep(.vertical-radio-group .el-radio__input) {
  margin-top: 2px;
}
</style>
