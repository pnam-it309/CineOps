<script setup>
import { ref, onMounted, computed, reactive, watch } from 'vue';
import { Search, Plus, Edit, Delete, View, Filter, Message, User, Phone } from '@element-plus/icons-vue';

import BaseTable from '@/components/common/BaseTable.vue';
import CCCDScanner from '@/components/common/CCCDScanner.vue'; // <-- Integrate Scanner
import { khachHangService } from '@/services/api/admin/khachHangService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import BaseModal from '@/components/common/BaseModal.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';
import { addressService } from '@/services/api/common/addressService';
import { hoaDonService } from '@/services/api/admin/hoaDonService';

// --- State ---
const customers = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const filterTrangThai = ref('');
const filterGioiTinh = ref('');
const totalElements = ref(0);
const currentPage = ref(1);
const pageSize = ref(5);

// Invoice history states
const activeDetailTab = ref('info');
const customerInvoices = ref([]);
const loadingInvoices = ref(false);

const customerColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã KH', key: 'maKhachHang', width: '100px' },
  { label: 'Khách hàng', key: 'customer', width: '160px' },
  { label: 'Số điện thoại', key: 'sdt', width: '120px' },
  { label: 'Email', key: 'email', width: '180px' },
  { label: 'Địa chỉ', key: 'diaChi', width: '180px' },
  { label: 'Loại khách', key: 'loaiKhach', width: '120px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' },
];

// State điều khiển Modal
const dialogVisible = ref(false);
const selectedCustomer = ref(null);
const isEdit = ref(false);
const currentId = ref(null);
const formRef = ref(null);



const form = reactive({
  tenKhachHang: '',
  email: '',
  sdt: '',
  gioiTinh: 1,
  ngaySinh: '',
  thanhPhoTinh: '',
  quanHuyen: '',
  phuongXa: '',
  diaChiChiTiet: '',
  trangThai: 1
});

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);

const fetchProvinces = async () => {
  try {
    const res = await addressService.getProvinces();
    provinces.value = res.data;
  } catch (err) { console.error(err); }
};

const handleProvinceChange = async (val) => {
  form.quanHuyen = '';
  form.phuongXa = '';
  districts.value = [];
  wards.value = [];
  if (!val) return;

  const p = provinces.value.find(x => x.name === val);
  if (p) {
    // Lấy toàn bộ quận huyện và phường xã của tỉnh đó (depth=3)
    // Lưu ý: Người dùng yêu cầu không còn Huyện, nên ta gộp hết Xã vào 1 danh sách
    const res = await addressService.getDistricts(p.code); // districts.value will be populated
    const provinceDetail = res.data;
    if (provinceDetail && provinceDetail.districts) {
      let allWards = [];
      for (const d of provinceDetail.districts) {
        // Fetch wards for each district
        const wRes = await addressService.getWards(d.code);
        if (wRes.data && wRes.data.wards) {
          allWards = [...allWards, ...wRes.data.wards.map(w => ({ ...w, districtName: d.name }))];
        }
      }
      wards.value = allWards;
    }
  }
};

const handleDistrictChange = async (val) => {
  // Không dùng nữa vì bỏ Huyện
};

// Rules validation cơ bản cho form
const rules = {
  tenKhachHang: [{ required: true, message: 'Vui lòng nhập tên khách hàng', trigger: 'blur' }],
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không đúng định dạng', trigger: 'blur' }
  ],
  sdt: [{ required: true, message: 'Vui lòng nhập số điện thoại', trigger: 'blur' }]
};

import debounce from 'lodash/debounce';

// --- Logic tải dữ liệu ---
const fetchCustomers = async () => {
  loading.value = true;
  try {
    const res = await khachHangService.getAll(
      searchQuery.value || null,
      filterTrangThai.value === '' ? null : filterTrangThai.value,
      filterGioiTinh.value === '' ? null : filterGioiTinh.value,
      currentPage.value - 1,
      pageSize.value,
      'id,desc'
    );

    const apiRes = res.data;
    if (apiRes && apiRes.data) {
      // Backend trả về Page object
      if (apiRes.data.content) {
        customers.value = apiRes.data.content;
        totalElements.value = apiRes.data.totalElements;
      } else {
        customers.value = Array.isArray(apiRes.data) ? apiRes.data : [];
        totalElements.value = customers.value.length;
      }
    } else {
      customers.value = [];
      totalElements.value = 0;
    }
  } catch (error) {
    notification.error('Không thể tải danh sách khách hàng');
  } finally {
    loading.value = false;
  }
};

const debouncedFetch = debounce(() => {
  currentPage.value = 1;
  fetchCustomers();
}, 300);

onMounted(() => {
  fetchCustomers();
  fetchProvinces();
});

// --- Logic xử lý Modal (Thêm & Sửa) ---
const handleAdd = () => {
  isEdit.value = false;
  currentId.value = null;
  Object.assign(form, {
    tenKhachHang: '',
    email: '',
    sdt: '',
    gioiTinh: 1,
    ngaySinh: '',
    hinhAnh: '',
    ghiChu: '',
    thanhPhoTinh: '',
    quanHuyen: '',
    phuongXa: '',
    diaChiChiTiet: '',
    trangThai: 1
  });
  dialogVisible.value = true;
  if (formRef.value) formRef.value.clearValidate();
};

const handleEdit = (row) => {
  isEdit.value = true;
  currentId.value = row.id;
  Object.assign(form, {
    tenKhachHang: row.tenKhachHang,
    email: row.email,
    sdt: row.sdt,
    gioiTinh: row.gioiTinh,
    ngaySinh: row.ngaySinh,
    hinhAnh: row.hinhAnh || '',
    ghiChu: row.ghiChu || '',
    thanhPhoTinh: row.thanhPhoTinh || '',
    quanHuyen: row.quanHuyen || '',
    phuongXa: row.phuongXa || '',
    diaChiChiTiet: row.diaChiChiTiet || '',
    trangThai: row.trangThai
  });

  if (form.thanhPhoTinh) {
    handleProvinceChange(form.thanhPhoTinh).then(() => {
      if (form.quanHuyen) {
        handleDistrictChange(form.quanHuyen);
      }
    });
  }

  dialogVisible.value = true;
};

const detailVisible = ref(false);
const selectedItem = ref(null);
const handleView = (row) => {
  selectedItem.value = row;
  activeDetailTab.value = 'info';
  detailVisible.value = true;
  fetchCustomerInvoices(row.id);
};

const fetchCustomerInvoices = async (customerId) => {
  loadingInvoices.value = true;
  customerInvoices.value = [];
  try {
    const res = await hoaDonService.getInvoices({ idKhachHang: customerId, size: 50 });
    if (res.data && res.data.data) {
      customerInvoices.value = res.data.data.content || res.data.data;
    }
  } catch (err) {
    console.error('Error fetching invoices:', err);
  } finally {
    loadingInvoices.value = false;
  }
};

const getInvoiceStatusLabel = (s) => ({ 1: 'Hoàn thành', 0: 'Hủy', 2: 'Chờ' }[s] || 'Khác');
const getInvoiceStatusTag = (s) => ({ 1: 'success', 0: 'danger', 2: 'warning' }[s] || 'info');

// Handle data returned from Scanner
const handleCCCDScanned = (data) => {
  if (data.name) form.tenKhachHang = data.name;
  if (data.dob) form.ngaySinh = data.dob;

  // Mapping địa chỉ Tỉnh / Xã (BỎ QUA HUYỆN)
  if (data.province) {
    const provinceMatch = provinces.value.find(p =>
      p.name.toLowerCase().includes(data.province.toLowerCase()) ||
      data.province.toLowerCase().includes(p.name.toLowerCase())
    );

    if (provinceMatch) {
      form.thanhPhoTinh = provinceMatch.name;
      handleProvinceChange(provinceMatch.name).then(() => {
        if (data.ward) {
          const wardMatch = wards.value.find(w =>
            w.name.toLowerCase().includes(data.ward.toLowerCase()) ||
            data.ward.toLowerCase().includes(w.name.toLowerCase())
          );
          if (wardMatch) form.phuongXa = wardMatch.name;
        }
      });
    }
  }

  // Theo yêu cầu khách hàng: Không cần địa chỉ chi tiết và Huyện
  form.quanHuyen = '';
  form.diaChiChiTiet = '';
};

// --- Logic Lưu Dữ Liệu ---
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await confirmDialog.update('khách hàng');
        } else {
          await confirmDialog.add('khách hàng');
        }
      } catch { return; }

      try {
        if (isEdit.value) {
          await khachHangService.update(currentId.value, form);
          notification.updateSuccess('khách hàng');
        } else {
          await khachHangService.create(form);
          notification.addSuccess('khách hàng');
        }
        dialogVisible.value = false;
        await fetchCustomers();
      } catch (error) {
        const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra';
        notification.error(errorMsg);
      }
    }
  });
};

// --- Logic Xóa ---
const handleUpdateStatus = (row, status = null) => {
  const isInactive = row.trangThai === 0;
  const newStatus = status !== null ? status : (isInactive ? 1 : 0);
  const label = newStatus === 1 ? 'kích hoạt' : 'vô hiệu hóa';

  if (newStatus === row.trangThai) return;

  confirmDialog.custom(
    `Thay đổi trạng thái khách hàng <b>${row.tenKhachHang}</b> thành <b>${label}</b>?`,
    'Cập nhật trạng thái',
    'Xác nhận'
  ).then(async () => {
    try {
      await khachHangService.update(row.id, { ...row, trangThai: newStatus });
      notification.success(`Đã ${label} khách hàng thành công`);
      fetchCustomers();
    } catch (error) {
      notification.error('Cập nhật thất bại');
    }
  }).catch(() => { });
};

// --- Các hàm hỗ trợ khác ---
const resetFilter = () => {
  searchQuery.value = '';
  filterTrangThai.value = '';
  filterGioiTinh.value = '';
  currentPage.value = 1;
  fetchCustomers();
};



const paginatedCustomers = computed(() => customers.value);

const formatCurrency = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const calculateAge = (dob) => {
  if (!dob) return '—';
  const birthDate = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const m = today.getMonth() - birthDate.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) age--;
  return age;
};

const getGenderText = (g) => g === 1 ? 'Nam' : 'Nữ';
const getStatusLabel = (status) => status === 1 ? 'Hoạt động' : 'Ngừng hoạt động';

watch(searchQuery, debouncedFetch);
watch([filterTrangThai, filterGioiTinh, currentPage, pageSize], () => {
  if (loading.value) return;
  fetchCustomers();
});
</script>

<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable title="Quản lý khách hàng" titleIcon="bi bi-people-fill" addButtonLabel="Thêm khách hàng"
      :data="customers" :columns="customerColumns" :loading="loading" :total="totalElements"
      v-model:currentPage="currentPage" v-model:pageSize="pageSize" @add-click="handleAdd" @reset-filter="resetFilter"
      @edit="handleEdit" @delete="handleUpdateStatus">
      <template #header-actions-left>
        <ExcelActions module="khach-hang" @import-success="fetchCustomers" />
      </template>

      <!-- Optimized Filters -->
      <template #filters>
        <div class="me-2 mb-2 mb-md-0" style="min-width: 240px;">
          <el-input v-model="searchQuery" placeholder="Tên, email, SĐT..." :prefix-icon="Search" clearable />
        </div>

        <div class="me-2 mb-2 mb-md-0">
          <el-select v-model="filterTrangThai" placeholder="Trạng thái" style="width: 170px;" clearable>
            <el-option label="Hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>

        <div class="mb-2 mb-md-0">
          <el-select v-model="filterGioiTinh" placeholder="Giới tính" style="width: 170px;" clearable>
            <el-option label="Nam" :value="1" />
            <el-option label="Nữ" :value="0" />
          </el-select>
        </div>
      </template>


      <!-- Cell Templates -->
      <template #cell-stt="{ index }">
        <span class="text-secondary smaller">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
      </template>

      <template #cell-maKhachHang="{ row }">
        <span class="text-primary smaller fw-bold">#{{ row.maKhachHang }}</span>
      </template>

      <template #cell-customer="{ row }">
        <div class="fw-bold text-dark">{{ row.tenKhachHang }}</div>
        <div class="smaller text-secondary">{{ getGenderText(row.gioiTinh) }}, {{ calculateAge(row.ngaySinh) }} tuổi
        </div>
      </template>

      <template #cell-sdt="{ row }">
        <span class="smaller text-dark fw-semibold">{{ row.sdt || '—' }}</span>
      </template>

      <template #cell-email="{ row }">
        <span class="smaller text-secondary">{{ row.email || '—' }}</span>
      </template>

      <template #cell-diaChi="{ row }">
        <div class="text-secondary smaller text-truncate" style="max-width: 250px;" :title="row.diaChi">{{ row.diaChi ||
          '—' }}</div>
      </template>

      <template #cell-loaiKhach="{ row }">
        <el-tag size="small" :type="row.tenLoaiKhachHang?.toLowerCase().includes('vip') ? 'warning' : 'info'"
          effect="plain" class="fw-bold">
          {{ row.tenLoaiKhachHang || 'Thường' }}
        </el-tag>
      </template>

      <template #cell-trangThai="{ row }">
        <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" size="small" effect="dark" class="fw-bold">
          {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center align-items-center gap-1">
          <el-tooltip content="Xem chi tiết" placement="top">
            <button class="btn-action-icon action-view" @click="handleView(row)">
              <i class="bi bi-eye"></i>
            </button>
          </el-tooltip>
          <el-tooltip content="Chỉnh sửa" placement="top">
            <button class="btn-action-icon action-edit" @click="handleEdit(row)">
              <i class="bi bi-pencil"></i>
            </button>
          </el-tooltip>
          <el-switch :model-value="row.trangThai === 1" @change="(val) => handleUpdateStatus(row, val ? 1 : 0)"
            class="status-switch mx-1" />
        </div>
      </template>
    </BaseTable>



    <BaseModal v-model="dialogVisible" :title="isEdit ? 'Cập nhật khách hàng' : 'Thêm khách hàng mới'"
      :icon="isEdit ? 'bi bi-person-gear' : 'bi bi-person-plus'" width="650px" confirmText="XÁC NHẬN"
      @confirm="submitForm">
      <template #header-extra v-if="!isEdit">
        <CCCDScanner @scanned="handleCCCDScanned" />
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form">
        <el-form-item label="Tên khách hàng" prop="tenKhachHang">
          <el-input v-model="form.tenKhachHang" placeholder="Nguyễn Văn A" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="example@gmail.com" :prefix-icon="Message" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Số điện thoại" prop="sdt">
              <el-input v-model="form.sdt" placeholder="09xxx" :prefix-icon="Phone" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày sinh">
              <el-date-picker v-model="form.ngaySinh" type="date" value-format="YYYY-MM-DD" placeholder="Chọn ngày sinh"
                style="width: 100%" />
            </el-form-item>
          </div>
        </div>

        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Tỉnh/Thành phố">
              <el-select v-model="form.thanhPhoTinh" placeholder="Chọn tỉnh" class="w-100" filterable
                @change="handleProvinceChange">
                <el-option v-for="p in provinces" :key="p.code" :label="p.name" :value="p.name" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Phường/Xã">
              <el-select v-model="form.phuongXa" placeholder="Chọn xã" class="w-100" filterable
                :disabled="!form.thanhPhoTinh">
                <el-option v-for="w in wards" :key="w.code" :label="w.name" :value="w.name">
                  <span>{{ w.name }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </div>
          <!-- Ẩn Quận/Huyện theo yêu cầu "không còn huyện" -->
          <!-- <div class="col-6">
            <el-form-item label="Quận/Huyện">
              <el-select v-model="form.quanHuyen" placeholder="Chọn huyện" class="w-100" filterable @change="handleDistrictChange" :disabled="!form.thanhPhoTinh">
                <el-option v-for="d in districts" :key="d.code" :label="d.name" :value="d.name" />
              </el-select>
            </el-form-item>
          </div> -->
          <!-- Ẩn địa chỉ chi tiết theo yêu cầu (Chỉ cần Tỉnh / Huyện / Xã) -->
          <!-- <div class="col-6">
             <el-form-item label="Chi tiết (Đường/Số nhà)">
               <el-input v-model="form.diaChiChiTiet" placeholder="Nhập địa chỉ chi tiết" />
             </el-form-item>
          </div> -->
        </div>

        <el-form-item label="Hình ảnh URL">
          <el-input v-model="form.hinhAnh" placeholder="https://..." :prefix-icon="View" />
        </el-form-item>

        <el-form-item label="Ghi chú">
          <el-input v-model="form.ghiChu" type="textarea" :rows="2" placeholder="Nhập ghi chú khách hàng..." />
        </el-form-item>

        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Giới tính">
              <el-radio-group v-model="form.gioiTinh">
                <el-radio :value="1">Nam</el-radio>
                <el-radio :value="0">Nữ</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>

        </div>
      </el-form>
    </BaseModal>

    <BaseModal v-model="detailVisible" title="Hồ sơ khách hàng" icon="bi bi-person-badge-fill" width="850px" isDetail
      onlyCancel>
      <div v-if="selectedItem" class="customer-profile-container">
        <!-- Profile Header -->
        <div class="p-4 border-bottom bg-white d-flex align-items-center gap-4">
          <div class="profile-avatar shadow-sm border d-flex align-items-center justify-content-center bg-light text-secondary">
            {{ selectedItem.tenKhachHang?.charAt(0).toUpperCase() }}
          </div>
          <div class="flex-grow-1">
            <div class="d-flex align-items-center gap-2 mb-1">
              <h3 class="fw-bold m-0 text-dark">{{ selectedItem.tenKhachHang }}</h3>
              <span class="text-secondary small fw-bold">#{{ selectedItem.maKhachHang }}</span>
            </div>
            <div class="d-flex align-items-center gap-2">
              <el-tag :type="selectedItem.tenLoaiKhachHang?.toLowerCase().includes('vip') ? 'warning' : 'info'"
                effect="plain" round size="small" class="fw-bold">
                {{ selectedItem.tenLoaiKhachHang?.toUpperCase() || 'THÀNH VIÊN' }}
              </el-tag>
              <div class="small text-secondary"><i class="bi bi-envelope me-1"></i>{{ selectedItem.email || '—' }}</div>
            </div>
          </div>
        </div>

        <!-- Detail Tabs -->
        <el-tabs v-model="activeDetailTab" class="customer-tabs">
          <el-tab-pane label="THÔNG TIN CÁ NHÂN" name="info">
            <div class="p-4 bg-white">
              <div class="row g-4">
                <div class="col-12">
                  <h6 class="text-uppercase small fw-bold text-secondary mb-3 border-start border-3 border-primary ps-2">Thông số tài chính</h6>
                  <div class="row g-3">
                    <div class="col-6">
                      <div class="stat-card p-3 border bg-light-subtle rounded">
                        <div class="text-secondary small mb-1">TỔNG CHI TIÊU</div>
                        <div class="fw-bold fs-4 text-primary">{{ formatCurrency(selectedItem.tongChiTieu || 0) }}</div>
                      </div>
                    </div>
                    <div class="col-6">
                      <div class="stat-card p-3 border bg-light-subtle rounded">
                        <div class="text-secondary small mb-1">ĐIỂM TÍCH LŨY</div>
                        <div class="fw-bold fs-4 text-warning">{{ selectedItem.diemTichLuy || 0 }} PTS</div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="col-md-6">
                  <h6 class="text-uppercase small fw-bold text-secondary mb-3 border-start border-3 border-success ps-2">Liên hệ</h6>
                  <div class="p-3 border bg-white rounded h-100">
                    <div class="mb-3">
                      <label class="text-secondary small d-block mb-1">SỐ ĐIỆN THOẠI</label>
                      <div class="fw-bold text-dark">{{ selectedItem.sdt || '—' }}</div>
                    </div>
                    <div>
                      <label class="text-secondary small d-block mb-1">ĐỊA CHỈ</label>
                      <div class="text-dark small lh-sm">{{ selectedItem.diaChi || 'Chưa cập nhật' }}</div>
                    </div>
                  </div>
                </div>

                <div class="col-md-6">
                  <h6 class="text-uppercase small fw-bold text-secondary mb-3 border-start border-3 border-info ps-2">Cá nhân</h6>
                  <div class="p-3 border bg-white rounded h-100">
                    <div class="row g-3">
                      <div class="col-6">
                        <label class="text-secondary small d-block mb-1">GIỚI TÍNH</label>
                        <div class="fw-bold text-dark">{{ getGenderText(selectedItem.gioiTinh) }}</div>
                      </div>
                      <div class="col-6">
                        <label class="text-secondary small d-block mb-1">ĐỘ TUỔI</label>
                        <div class="fw-bold text-dark">{{ calculateAge(selectedItem.ngaySinh) }} tuổi</div>
                      </div>
                      <div class="col-12 border-top pt-2">
                        <label class="text-secondary small d-block mb-1">TỔNG VÉ ĐÃ MUA</label>
                        <div class="fw-bold text-dark fs-5">{{ selectedItem.tongVe || 0 }} vé</div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="selectedItem.ghiChu" class="col-12">
                  <div class="p-3 border bg-light rounded italic">
                    <h6 class="text-uppercase small fw-bold text-secondary mb-1">GHI CHÚ QUẢN TRỊ</h6>
                    <p class="text-muted mb-0 small">"{{ selectedItem.ghiChu }}"</p>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="LỊCH SỬ GIAO DỊCH" name="history">
            <div class="p-4 bg-white min-h-300">
               <el-table :data="customerInvoices" v-loading="loadingInvoices" border stripe style="width: 100%" size="small" max-height="350">
                  <el-table-column label="Mã HĐ" prop="maHoaDon" width="110">
                    <template #default="{row}">
                      <span class="fw-bold text-primary">#{{ row.maHoaDon }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Ngày mua" width="160">
                    <template #default="{row}">
                      {{ row.ngayTao }}
                    </template>
                  </el-table-column>
                  <el-table-column label="Tổng tiền">
                    <template #default="{row}">
                      <span class="fw-bold text-danger">{{ formatCurrency(row.tongTienThanhToan) }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Kênh" width="100">
                    <template #default="{row}">
                      <el-tag size="small" :type="row.kenhBanHang === 0 ? 'success' : 'info'">
                        {{ row.kenhBanHang === 0 ? 'Tại quầy' : 'Online' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="Trạng thái" width="120">
                    <template #default="{row}">
                      <el-tag size="small" :type="getInvoiceStatusTag(row.trangThai)">
                        {{ getInvoiceStatusLabel(row.trangThai) }}
                      </el-tag>
                    </template>
                  </el-table-column>
               </el-table>
               <div v-if="!customerInvoices.length && !loadingInvoices" class="text-center py-5">
                 <el-empty description="Khách hàng chưa có giao dịch nào" :image-size="80" />
               </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </BaseModal>
  </div>
</template>

<style scoped>
/* Zen scoped CSS: purely functional */
.status-switch :deep(.el-switch__label) {
  display: none !important;
}

.customer-profile-container {
  overflow: hidden;
}

.profile-avatar {
  width: 100px; 
  height: 100px; 
  font-size: 2.5rem; 
  font-weight: bold;
  border-radius: 12px;
}

.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.customer-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.customer-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.customer-tabs :deep(.el-tabs__item) {
  font-weight: 700;
  font-size: 12px;
  height: 50px;
}

.min-h-300 {
  min-height: 300px;
}
</style>
