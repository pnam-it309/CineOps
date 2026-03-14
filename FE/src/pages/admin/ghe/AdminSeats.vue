<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable 
      title="Quản lý ghế" 
      subtitle="Quản lý sơ đồ ghế, thiết lập loại ghế và giá phụ phí cho từng phòng chiếu"
      titleIcon="bi bi-grid-3x3-gap-fill" 
      addButtonLabel="Thêm ghế lẻ"
      :data="paginatedSeats" 
      :columns="seatColumns" 
      :loading="loading" 
      :total="filteredSeats.length"
      v-model:currentPage="currentPage" 
      v-model:pageSize="pageSize" 
      @add-click="openDialog" 
      @reset-filter="resetFilter"
      @selection-change="handleSelectionChange" 
      :hidePagination="false"
      @fetch="fetchSeats"
    >
      <template #header-actions-left>
        <el-tooltip v-if="selectedSeats.length" :content="`Đổi trạng thái cho ${selectedSeats.length} ghế`" placement="top">
          <el-button 
            type="warning" 
            class="fw-bold" 
            :icon="Refresh"
            @click="handleBulkToggleStatus"
            style="border-radius: 8px;"
          >
            Cập nhật ({{ selectedSeats.length }})
          </el-button>
        </el-tooltip>
      </template>

      <!-- Optimized Filters (Matching Image 1: Labels above inputs) -->
      <template #filters>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Số ghế</label>
          <el-input v-model="searchQuery" placeholder="Tìm số ghế..." :prefix-icon="Search" clearable class="w-100" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Phòng chiếu</label>
          <el-select v-model="selectedRoom" placeholder="Tất cả" class="w-100" @change="fetchSeats" clearable>
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Loại ghế</label>
          <el-select v-model="filterLoaiGhe" placeholder="Tất cả" class="w-100">
            <el-option value="" label="Tất cả loại ghế" />
            <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
          </el-select>
        </div>
      </template>

      <!-- Cell Templates -->
      <template #cell-soGhe="{ row }">
        <span class="seat-pill-modern fw-bold" :class="getSeatTypeTag(row.tenLoaiGhe)">{{ row.soGhe }}</span>
      </template>

      <template #cell-soHang="{ row }">
        <span class="text-dark fw-bold smaller">Hàng {{ row.soHang }}</span>
      </template>

      <template #cell-soCot="{ row }">
        <span class="text-secondary smaller">Cột {{ row.soCot }}</span>
      </template>

      <template #cell-phongChieu="{ row }">
        <div class="d-flex align-items-center justify-content-center gap-1">
          <i class="bi bi-door-open text-primary smaller"></i>
          <span class="smaller fw-bold text-dark">{{phongChieuList.find(pc => pc.id === row.idPhongChieu)?.tenPhong ||
            '—'}}</span>
        </div>
      </template>

      <template #cell-tenLoaiGhe="{ row }">
        <el-tag :type="getSeatTypeTag(row.tenLoaiGhe)" round size="small" effect="plain" class="fw-bold">
          {{ row.tenLoaiGhe }}
        </el-tag>
      </template>

      <template #cell-phuPhi="{ row }">
        <span v-if="row.phuPhi > 0" class="text-primary fw-bold smaller">{{ formatCurrency(row.phuPhi) }}</span>
        <span v-else class="text-secondary opacity-50 smaller">—</span>
      </template>

      <template #cell-trangThai="{ row }">
        <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" effect="dark" size="small" class="fw-bold">
          {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center align-items-center gap-1">
          <el-tooltip content="Chỉnh sửa ghế" placement="top">
            <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click.stop="openDialog(row)">
              <i class="bi bi-pencil"></i>
            </button>
          </el-tooltip>
          <el-switch :model-value="row.trangThai === 1" class="status-switch mx-1"
            @change="(val) => handleUpdateStatus({ row, status: val ? 1 : 0 })" @click.stop />
        </div>
      </template>

      <template #empty>
        <div class="py-5 text-secondary opacity-50 text-center">
          <i class="bi bi-inbox fs-1 d-block mb-2"></i>
          <div class="smaller fw-bold">Không tìm thấy ghế nào</div>
        </div>
      </template>
    </BaseTable>

    <!-- Unified Dialog -->
    <BaseModal v-model="dialogVisible"
      :title="isBulkMode ? `Cập nhật ${selectedSeats.length} ghế` : (editingId ? 'Chỉnh sửa Ghế' : 'Thêm Ghế mới')"
      :icon="isBulkMode ? 'bi bi-collection' : (editingId ? 'bi bi-pencil-square' : 'bi bi-plus-lg')" width="460px"
      :confirmText="isBulkMode ? 'Cập nhật hàng loạt' : (editingId ? 'Lưu thay đổi' : 'Thêm ghế')" :loading="saving"
      @confirm="handleSubmit">
      <el-form :model="form" :rules="dialogRules" ref="formRef" label-position="top" class="premium-form">
        <div class="row g-2">
          <div class="col-12" v-if="isBulkMode">
            <p class="text-secondary small mb-3">
              <i class="bi bi-info-circle me-1"></i>
              Các trường để trống sẽ không được cập nhật.
            </p>
          </div>
          <div class="col-6" v-if="!isBulkMode">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng" :disabled="!!editingId">
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
              </el-select>
            </el-form-item>
          </div>
          <div :class="isBulkMode ? 'col-12' : 'col-6'">
            <el-form-item label="Loại ghế" prop="idLoaiGhe">
              <el-select v-model="form.idLoaiGhe" class="w-100" placeholder="Chọn loại" :clearable="isBulkMode">
                <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
              </el-select>
            </el-form-item>
          </div>
          <template v-if="!isBulkMode">
            <div class="col-4">
              <el-form-item label="Số ghế" prop="soGhe">
                <el-input v-model="form.soGhe" placeholder="Tự động" disabled :prefix-icon="Monitor" />
              </el-form-item>
            </div>
            <div class="col-4">
              <el-form-item label="Hàng" prop="soHang">
                <el-input v-model="form.soHang" placeholder="A" />
              </el-form-item>
            </div>
            <div class="col-4">
              <el-form-item label="Cột" prop="soCot">
                <el-input-number v-model="form.soCot" :min="1" class="w-100" controls-position="right" />
              </el-form-item>
            </div>
          </template>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import BaseTable from '@/components/common/BaseTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import { gheService } from '@/services/api/admin/gheService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import { Search, Monitor, Refresh } from '@element-plus/icons-vue';

// =================== UTILS ===================
const getSeatTypeTag = (tenLoai) => {
  if (!tenLoai) return 'info';
  const l = tenLoai.toLowerCase();
  if (l.includes('vip')) return 'warning';
  if (l.includes('couple') || l.includes('sweetbox')) return 'danger';
  return 'info';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);

const seatColumns = [
  { type: 'selection', width: '50px' },
  { label: 'STT', key: 'stt', width: '70px', align: 'center' },
  { label: 'Số ghế', key: 'soGhe', width: '100px', align: 'center' },
  { label: 'Hàng', key: 'soHang', width: '100px', align: 'center' },
  { label: 'Cột', key: 'soCot', width: '100px', align: 'center' },
  { label: 'Phòng', key: 'phongChieu', width: '140px', align: 'center' },
  { label: 'Loại ghế', key: 'tenLoaiGhe', width: '130px', align: 'center' },
  { label: 'Phụ phí', key: 'phuPhi', width: '140px', align: 'center' },
  { label: 'Trạng thái', key: 'trangThai', width: '140px', align: 'center' },
];

// =================== STATE ===================
const searchQuery = ref('');
const filterLoaiGhe = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const selectedSeats = ref([]);

const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);
const editingId = ref(null);
const isBulkMode = ref(false);

const seats = ref([]);
const loaiGheList = ref([]);
const phongChieuList = ref([]);
const selectedRoom = ref(null);

const form = ref({
  idPhongChieu: '',
  idLoaiGhe: '',
  soGhe: '',
  soHang: '',
  soCot: 1,
  trangThai: 1
});

// =================== COMPUTED ===================
const filteredSeats = computed(() => {
  return seats.value.filter(s => {
    const matchLoai = filterLoaiGhe.value === '' || s.idLoaiGhe === filterLoaiGhe.value;
    const matchSearch = !searchQuery.value || s.soGhe?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchLoai && matchSearch;
  });
});

const paginatedSeats = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredSeats.value.slice(start, end).map((item, index) => ({
    ...item,
    stt: start + index + 1
  }));
});

const dialogRules = computed(() => {
  if (isBulkMode.value) return {};
  return {
    idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
    idLoaiGhe: [{ required: true, message: 'Vui lòng chọn loại ghế', trigger: 'change' }],
    soGhe: [{ required: true, message: 'Nhập số ghế', trigger: 'blur' }],
    soHang: [{ required: true, message: 'Nhập hàng', trigger: 'blur' }],
    soCot: [{ required: true, message: 'Nhập cột', trigger: 'blur' }],
  };
});

// =================== WATCHERS ===================
watch([() => form.value.soHang, () => form.value.soCot], ([newHang, newCot]) => {
  if (!isBulkMode.value && newHang && newCot) {
    form.value.soGhe = `${newHang}${newCot}`;
  }
});

// =================== METHODS ===================
const fetchDropdowns = async () => {
  try {
    const [loaiRes, phongRes] = await Promise.all([
      gheService.getLoaiGheList(),
      gheService.getPhongChieuDropdown()
    ]);
    loaiGheList.value = loaiRes.data?.data || [];
    phongChieuList.value = phongRes.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải dữ liệu dropdown');
  }
};

const fetchSeats = async () => {
  loading.value = true;
  try {
    const res = await gheService.getSeatsByRoom(selectedRoom.value || null);
    seats.value = res.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải danh sách ghế');
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  searchQuery.value = '';
  filterLoaiGhe.value = '';
  if (phongChieuList.value.length > 0) {
    selectedRoom.value = phongChieuList.value[0].id;
  }
  fetchSeats();
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  isBulkMode.value = false;

  if (row) {
    form.value = { ...row };
  } else {
    form.value = {
      idPhongChieu: selectedRoom.value || phongChieuList.value[0]?.id || '',
      idLoaiGhe: loaiGheList.value[0]?.id || '',
      soGhe: '',
      soHang: '',
      soCot: 1,
      trangThai: 1
    };
  }
  dialogVisible.value = true;
};

const handleSelectionChange = (val) => {
  selectedSeats.value = val;
};

const handleBulkToggleStatus = () => {
  handleUpdateStatus(selectedSeats.value);
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    try {
      if (isBulkMode.value) {
        await confirmDialog.custom(
          `Bạn có chắc muốn cập nhật cho ${selectedSeats.value.length} ghế?`,
          'Xác nhận cập nhật hàng loạt',
          'Cập nhật'
        );
      } else if (editingId.value) {
        await confirmDialog.update('ghế');
      } else {
        await confirmDialog.add('ghế');
      }
    } catch { return; }

    saving.value = true;
    try {
      if (isBulkMode.value) {
        await gheService.updateBulkSeats({
          ids: selectedSeats.value.map(s => s.id),
          idLoaiGhe: form.value.idLoaiGhe || null,
          trangThai: form.value.trangThai
        });
        notification.success('Cập nhật hàng loạt thành công!');
      } else if (editingId.value) {
        await gheService.updateSeat(editingId.value, form.value);
        notification.updateSuccess('ghế');
      } else {
        await gheService.createSeat(form.value);
        notification.addSuccess('ghế');
      }
      dialogVisible.value = false;
      await fetchSeats();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleUpdateStatus = async (target) => {
  const isBulk = Array.isArray(target);
  let items = isBulk ? target : [target.row];
  let newStatus = isBulk ? (items[0].trangThai === 1 ? 0 : 1) : target.status;

  if (items.length === 0) return;

  try {
    const label = newStatus === 1 ? 'Hoạt động' : 'Ngừng hoạt động';
    if (!isBulk) {
      const row = items[0];
      await confirmDialog.custom(`Đổi trạng thái ghế ${row.soGhe} sang ${label}?`, 'Xác nhận');
      saving.value = true;
      await gheService.updateSeat(row.id, { ...row, trangThai: newStatus });
      notification.success('Cập nhật trạng thái thành công');
    } else {
      await confirmDialog.custom(`Đổi trạng thái ${items.length} ghế sang ${label}?`, 'Xác nhận hàng loạt');
      saving.value = true;
      await gheService.updateBulkSeats({
        ids: items.map(i => i.id),
        trangThai: newStatus
      });
      notification.success(`Đã chuyển ${items.length} ghế sang ${label}`);
    }
    await fetchSeats();
  } catch (e) {
    if (e !== 'cancel') {
      notification.error('Thao tác thất bại');
    }
  } finally {
    saving.value = false;
  }
};

onMounted(async () => {
  await fetchDropdowns();
  if (phongChieuList.value.length) {
    selectedRoom.value = phongChieuList.value[0].id;
    await fetchSeats();
  }
});
</script>

<style scoped>
/* Zen scoped CSS: purely functional */
.status-switch :deep(.el-switch__label) {
  display: none !important;
}
</style>
