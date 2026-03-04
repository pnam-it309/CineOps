<template>
  <div class="admin-vouchers-page">
    <AdminTableLayout title="Quản lý phiếu giảm giá" titleIcon="bi bi-ticket-perforated-fill"
      addButtonLabel="Thêm phiếu giảm giá" :data="vouchers" :loading="loading" :total="total" v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" @add-click="openDialog()" @reset-filter="resetFilter">
      <!-- Header Actions Left Slot -->
      <template #header-actions-left>
        <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
          Xóa {{ selectedIds.length }} phiếu giảm giá
        </el-button>
      </template>

      <!-- Stats Slot -->
      <template #stats>
        <div class="col-md-3">
          <StatCard label="Tổng số phiếu" :value="total" icon="bi bi-ticket-perforated-fill" type="primary" />
        </div>
        <div class="col-md-3">
          <StatCard label="Đang hoạt động" :value="stats.active || 0" icon="bi bi-check-circle-fill" type="success" />
        </div>
        <div class="col-md-3">
          <StatCard label="Hết hạn/Hết lượt" :value="stats.expired || 0" icon="bi bi-exclamation-triangle-fill"
            type="danger" />
        </div>
        <div class="col-md-3">
          <StatCard label="Dừng hoạt động" :value="stats.inactive || 0" icon="bi bi-pause-circle-fill" type="warning" />
        </div>
      </template>

      <!-- Filters Slot -->
      <template #filters>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="filterStatus" placeholder="Tất cả" @change="fetchVouchers">
            <el-option label="Tất cả" value="" />
            <el-option label="Đang hoạt động" :value="1" />
            <el-option label="Tạm ngưng" :value="0" />
            <el-option label="Đã kết thúc" :value="2" />
          </el-select>
        </div>

        <div class="filter-item search-input-wrapper">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-input v-model="searchQuery" placeholder="Nhập mã hoặc tên phiếu..." clearable
            @input="handleSearch" />
        </div>
      </template>

      <!-- Content Slot with BaseTable -->
      <template #content>
        <BaseTable
          :data="vouchers"
          :columns="voucherColumns"
          :loading="loading"
          :total="total"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          v-model:selection="selectedObjects"
          :hide-pagination="true"
          @edit="openDialog"
          @delete="handleDelete"
          @view="handleView"
        >
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-maPhieuGiamGia="{ row }">
            <el-tag type="primary" effect="light" round size="small" style="font-weight: 600; letter-spacing: 0.5px;">
              {{ row.maPhieuGiamGia }}
            </el-tag>
          </template>

          <template #cell-tenPhieu="{ row }">
            <span class="fw-semibold text-dark small" style="white-space: nowrap;">{{ row.tenPhieu }}</span>
          </template>

          <template #cell-giaTriHoaDonToiThieu="{ row }">
            <span class="fw-bold text-dark small text-nowrap">{{ formatCurrency(row.giaTriHoaDonToiThieu) }}</span>
          </template>

          <template #cell-loaiPhieu="{ row }">
            <div v-if="row.loaiPhieu === 1">
              <el-tag type="warning" effect="dark" round size="small">{{ row.phanTramGiamGia }}%</el-tag>
            </div>
            <div v-else>
              <el-tag type="danger" effect="dark" round size="small">-{{ formatCurrency(row.soTienGiam) }}</el-tag>
            </div>
          </template>

          <template #cell-giamToiDa="{ row }">
            <span v-if="row.loaiPhieu === 1" class="text-dark fw-bold small">{{ formatCurrency(row.giamToiDa) }}</span>
            <span v-else class="text-secondary small">—</span>
          </template>

          <template #cell-soLuong="{ row }">
            <span class="text-dark fw-bold small">{{ row.soLuong }}</span>
          </template>

          <template #cell-ngayBatDau="{ row }">
            <div class="small text-secondary" style="font-size: 11px;">
              <div class="text-nowrap"><i class="bi bi-calendar-check me-1"></i>{{ formatDate(row.ngayBatDau) }}</div>
            </div>
          </template>

          <template #cell-ngayKetThuc="{ row }">
            <div class="small text-secondary" style="font-size: 11px;">
              <div class="text-nowrap"><i class="bi bi-calendar-x me-1"></i>{{ formatDate(row.ngayKetThuc) }}</div>
            </div>
          </template>

          <template #cell-trangThai="{ row }">
            <el-tag :type="getStatusTag(row.trangThai)" round size="small">
              {{ getStatusLabel(row.trangThai) }}
            </el-tag>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <BaseModal
      v-model="dialogVisible"
      :title="isReadonly ? 'Chi tiết phiếu giảm giá' : (editingId ? 'Cập nhật phiếu giảm giá' : 'Phát hành phiếu giảm giá mới')"
      :icon="editingId ? 'bi bi-pencil-square' : 'bi bi-ticket-perforated'"
      width="640px"
      :confirmText="editingId ? 'CẬP NHẬT' : 'PHÁT HÀNH'"
      :loading="saving"
      @confirm="handleSubmit"
    >
      <template #footer v-if="isReadonly"></template>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form"
        :disabled="isReadonly">
        <div class="row g-3">
          <div class="col-md-6">
            <el-form-item label="Mã Voucher" prop="maPhieuGiamGia">
              <el-input v-model="form.maPhieuGiamGia" placeholder="Hệ thống tự tạo..." readonly :prefix-icon="PriceTag">
                <template #append>
                  <el-button :icon="Refresh" @click="form.maPhieuGiamGia = generateVoucherCode()" v-if="!editingId && !isReadonly" />
                </template>
              </el-input>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Tên chương trình" prop="tenPhieu">
              <el-input v-model="form.tenPhieu" placeholder="Nhập tên chương trình..." :prefix-icon="Document" />
            </el-form-item>
          </div>

          <div class="col-md-4" v-if="!isReadonly">
            <el-form-item label="Loại giảm giá" prop="loaiPhieu">
              <el-select v-model="form.loaiPhieu" class="w-100">
                <template #prefix><el-icon><Opportunity /></el-icon></template>
                <el-option label="Phần trăm (%)" :value="1" />
                <el-option label="Tiền mặt (đ)" :value="2" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-4" v-if="form.loaiPhieu === 1">
            <el-form-item label="Phần trăm giảm" prop="phanTramGiamGia">
              <el-input v-model.number="form.phanTramGiamGia" type="number" placeholder="0" class="w-100">
                <template #suffix><span class="text-secondary fw-bold pe-2">%</span></template>
              </el-input>
            </el-form-item>
          </div>
          <div class="col-md-4" v-if="form.loaiPhieu === 1">
            <el-form-item label="Giảm tối đa" prop="giamToiDa">
              <el-input v-model.number="form.giamToiDa" type="number" placeholder="0" class="w-100">
                <template #suffix><span class="text-secondary fw-bold pe-2">₫</span></template>
              </el-input>
            </el-form-item>
          </div>
          <div class="col-md-8" v-if="form.loaiPhieu === 2">
            <el-form-item label="Số tiền giảm" prop="soTienGiam">
              <el-input v-model.number="form.soTienGiam" type="number" placeholder="0" class="w-100">
                <template #suffix><span class="text-secondary fw-bold pe-2">₫</span></template>
              </el-input>
            </el-form-item>
          </div>

          <div class="col-md-4">
            <el-form-item label="Đơn tối thiểu" prop="giaTriHoaDonToiThieu">
              <el-input v-model.number="form.giaTriHoaDonToiThieu" type="number" placeholder="0" class="w-100">
                <template #suffix><span class="text-secondary fw-bold pe-2">₫</span></template>
              </el-input>
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="Số lượng" prop="soLuong">
              <el-input v-model.number="form.soLuong" type="number" placeholder="0" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="Trạng thái" prop="trangThai">
              <el-select v-model="form.trangThai" class="w-100">
                <el-option label="Hoạt động" :value="1" />
                <el-option label="Dừng" :value="0" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-md-6">
            <el-form-item label="Ngày bắt đầu" prop="ngayBatDau">
              <el-date-picker v-model="form.ngayBatDau" type="datetime" class="w-100" placeholder="Chọn thời gian"
                value-format="YYYY-MM-DDTHH:mm:ss" :prefix-icon="Calendar" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Ngày kết thúc" prop="ngayKetThuc">
              <el-date-picker v-model="form.ngayKetThuc" type="datetime" class="w-100" placeholder="Chọn thời gian"
                value-format="YYYY-MM-DDTHH:mm:ss" :prefix-icon="Calendar" />
            </el-form-item>
          </div>

          <div class="col-md-6">
            <el-form-item label="Cộng dồn?">
              <el-radio-group v-model="form.coChoCongDon">
                <el-radio :value="1">Có</el-radio>
                <el-radio :value="0">Không</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>

          <div class="col-md-12">
            <el-form-item label="Điều kiện áp dụng">
              <el-input v-model="form.dieuKienApDung" type="textarea" :rows="2" placeholder="VD: Chỉ áp dụng cho phim 2D..." />
            </el-form-item>
          </div>

          <div class="col-md-12">
            <el-form-item label="Ghi chú">
              <el-input v-model="form.ghiChu" type="textarea" :rows="2" placeholder="Thông tin nội bộ..." />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { Search, Plus, Edit, Delete, View, Refresh, Ticket, PriceTag, Calendar, Opportunity, Document } from '@element-plus/icons-vue';
import { voucherService } from '@/services/api/admin/voucherService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import debounce from 'lodash/debounce';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';

const voucherColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'MÃ GIẢM GIÁ', key: 'maPhieuGiamGia', width: '160px' },
  { label: 'TÊN PHIẾU GIẢM GIÁ', key: 'tenPhieu', minWidth: '500px' },
  { label: 'ĐƠN TỐI THIỂU', key: 'giaTriHoaDonToiThieu', width: '180px' },
  { label: 'GIẢM GIÁ', key: 'loaiPhieu', width: '150px' },
  { label: 'GIẢM TỐI ĐA', key: 'giamToiDa', width: '180px' },
  { label: 'SỐ LƯỢNG', key: 'soLuong', width: '120px' },
  { label: 'TỪ NGÀY', key: 'ngayBatDau', width: '180px' },
  { label: 'ĐẾN NGÀY', key: 'ngayKetThuc', width: '180px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '160px' },
];

const selectedObjects = ref([]);
const selectedIds = computed(() => selectedObjects.value.map(item => item.id));

const handleBulkDelete = () => {
    confirmDialog.custom(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> voucher đã chọn?`,
        'Xóa hàng loạt',
        'Đồng ý'
    ).then(async () => {
        try {
            await Promise.all(selectedIds.value.map(id => voucherService.delete(id)));
            notification.success(`Đã xóa ${selectedIds.value.length} voucher thành công`);
            selectedObjects.value = [];
            fetchVouchers();
        } catch (error) {
            notification.error('Có lỗi khi xóa hàng loạt');
        }
    }).catch(() => {});
};

const loading = ref(false);
const vouchers = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const filterStatus = ref('');
const searchQuery = ref('');

const stats = ref({ active: 0, expired: 0, inactive: 0 });

const dialogVisible = ref(false);
const saving = ref(false);
const editingId = ref(null);
const isReadonly = ref(false);
const formRef = ref(null);

const generateVoucherCode = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let code = 'VC';
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return code;
};

const handleView = (row) => {
  isReadonly.value = true;
  editingId.value = row.id;
  form.value = { ...row };
  dialogVisible.value = true;
};

const form = ref({
  maPhieuGiamGia: '',
  tenPhieu: '',
  loaiPhieu: 1,
  phanTramGiamGia: 0,
  soTienGiam: 0,
  giaTriHoaDonToiThieu: 0,
  giamToiDa: 0,
  ngayBatDau: '',
  ngayKetThuc: '',
  soLuong: 1,
  coChoCongDon: 0,
  dieuKienApDung: '',
  ghiChu: '',
  trangThai: 1
});


// ===== RULES (CHỈ SỬA trigger thành blur + change) =====
const rules = {
  maPhieuGiamGia: [
    { required: true, message: 'Mã phiếu không được để trống', trigger: ['blur', 'change'] },
    { min: 3, max: 30, message: 'Mã phiếu phải từ 3 - 30 ký tự', trigger: ['blur', 'change'] },
    { pattern: /^[A-Z0-9-]+$/, message: 'Chỉ cho phép chữ in hoa, số và dấu gạch ngang', trigger: ['blur', 'change'] }
  ],

  tenPhieu: [
    { required: true, message: 'Vui lòng nhập tên phiếu', trigger: ['blur', 'change'] },
    { min: 3, max: 50, message: 'Tên phải từ 3 đến 50 ký tự', trigger: ['blur', 'change'] }
  ],

  loaiPhieu: [
    { required: true, message: 'Loại phiếu không được để trống', trigger: ['change'] }
  ],

  phanTramGiamGia: [
    {
      validator: (rule, value, callback) => {
        if (form.value.loaiPhieu === 1) {
          if (!value) return callback(new Error('Phần trăm giảm không được để trống'));
          if (value < 1) return callback(new Error('Phần trăm phải >= 1'));
        }
        callback();
      },
      trigger: ['blur', 'change']
    }
  ],

  soTienGiam: [
    {
      validator: (rule, value, callback) => {
        if (form.value.loaiPhieu === 2) {
          if (!value) return callback(new Error('Số tiền giảm không được để trống'));
          if (value < 0) return callback(new Error('Số tiền giảm phải >= 0'));
        }
        callback();
      },
      trigger: ['blur', 'change']
    }
  ],

  giamToiDa: [
    {
      validator: (rule, value, callback) => {
        if (value != null && value < 0) {
          return callback(new Error('Giảm tối đa không được âm'));
        }
        callback();
      },
      trigger: ['blur', 'change']
    }
  ],

  giaTriHoaDonToiThieu: [
    { required: true, message: 'Đơn tối thiểu không được để trống', trigger: ['blur', 'change'] },
    { type: 'number', min: 0, message: 'Đơn tối thiểu phải >= 0', trigger: ['blur', 'change'] }
  ],

  soLuong: [
    { required: true, message: 'Số lượng không được để trống', trigger: ['blur', 'change'] },
    { type: 'number', min: 1, message: 'Số lượng phải >= 1', trigger: ['blur', 'change'] }
  ],

  ngayBatDau: [
    { required: true, message: 'Ngày bắt đầu không được để trống', trigger: ['change'] }
  ],

  ngayKetThuc: [
    { required: true, message: 'Ngày kết thúc không được để trống', trigger: ['change'] },
    {
      validator: (rule, value, callback) => {
        if (!value) return callback();
        if (new Date(value) <= new Date(form.value.ngayBatDau)) {
          callback(new Error('Ngày kết thúc phải sau ngày bắt đầu'));
        } else {
          callback();
        }
      },
      trigger: ['change']
    }
  ]
};


const fetchVouchers = async () => {
  loading.value = true;
  try {
    const params = {
      keyword: searchQuery.value,
      trangThai: filterStatus.value === '' ? null : filterStatus.value,
      page: currentPage.value - 1,
      size: pageSize.value
    };
    const res = await voucherService.getAll(params);
    vouchers.value = res.data?.data?.content || [];
    total.value = res.data?.data?.totalElements || 0;

    stats.value = {
      active: res.data?.data?.activeCount || 0,
      inactive: res.data?.data?.inactiveCount || 0,
      expired: res.data?.data?.expiredCount || 0
    };
  } catch (e) {
    notification.error('Không thể tải danh sách voucher');
  } finally {
    loading.value = false;
  }
};

const handleSearch = debounce(() => {
  currentPage.value = 1;
  fetchVouchers();
}, 300);

const resetFilter = () => {
  searchQuery.value = '';
  filterStatus.value = '';
  currentPage.value = 1;
  fetchVouchers();
};

const handleSelectionChange = (val) => {
  selectedObjects.value = val;
};

const openDialog = (row = null) => {
  isReadonly.value = false;
  editingId.value = row?.id || null;
  if (row) {
    form.value = { ...row };
  } else {
    form.value = {
      maPhieuGiamGia: generateVoucherCode(),
      tenPhieu: '',
      loaiPhieu: 1,
      phanTramGiamGia: 0,
      soTienGiam: 0,
      giaTriHoaDonToiThieu: 0,
      giamToiDa: 0,
      ngayBatDau: '',
      ngayKetThuc: '',
      soLuong: 1,
      coChoCongDon: 0,
      dieuKienApDung: '',
      ghiChu: '',
      trangThai: 1
    };
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    try {
      if (editingId.value) {
        await confirmDialog.update('voucher');
      } else {
        await confirmDialog.add('voucher');
      }
    } catch {
      return; // bấm hủy thì dừng
    }

    saving.value = true;
    try {
      if (editingId.value) {
        await voucherService.update(editingId.value, form.value);
        notification.updateSuccess('voucher');
      } else {
        await voucherService.create(form.value);
        notification.addSuccess('voucher');
      }

      dialogVisible.value = false;
      fetchVouchers();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};
const handleDelete = (row) => {
  confirmDialog.delete('voucher', row.maPhieuGiamGia).then(async () => {
    try {
      await voucherService.delete(row.id);
      notification.deleteSuccess('voucher');
      fetchVouchers();
    } catch (e) {
      notification.error('Xóa thất bại');
    }
  });
};

const getStatusTag = (status) => {
  const map = { 1: 'success', 0: 'warning', 2: 'info' };
  return map[status] || 'info';
};

const getStatusLabel = (status) => {
  const map = { 1: 'Hoạt động', 0: 'Tạm ngưng', 2: 'Kết thúc' };
  return map[status] || 'Không xác định';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);
const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
};

onMounted(() => {
  fetchVouchers();
});

watch([currentPage, pageSize], () => {
  fetchVouchers();
});
</script>

<style scoped>
.admin-voucher-container {
  height: calc(100vh - 84px);
}

.extra-small {
  font-size: 10px;
}
</style>
