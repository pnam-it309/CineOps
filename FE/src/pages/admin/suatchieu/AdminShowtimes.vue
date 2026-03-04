<template>
  <div class="admin-showtimes-page">
    <AdminTableLayout title="Quản lí suất chiếu " titleIcon="bi bi-calendar2-week-fill" addButtonLabel="Thêm suất chiếu"
      :data="paginatedShowtimes" :loading="loading" :total="filteredShowtimes.length" v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" v-model:selection="selectedShowtimes" @add-click="openDialog()"
      @reset-filter="resetFilter">
      <!-- Header Actions Left Slot -->
      <template #header-actions-left>
        <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
          Xóa {{ selectedIds.length }} suất chiếu
        </el-button>
      </template>
      <!-- Stats Slot -->
      <template #stats>
        <div class="col-md-3">
          <StatCard label="Tổng suất chiếu" :value="filteredShowtimes.length" icon="bi bi-calendar-event-fill"
            type="dark" />
        </div>
        <div class="col-md-3">
          <StatCard label="Sắp chiếu" :value="filteredShowtimes.filter(s => s.trangThai === 1).length"
            icon="bi bi-clock-history" type="primary" />
        </div>
        <div class="col-md-3">
          <StatCard label="Đang chiếu" :value="filteredShowtimes.filter(s => s.trangThai === 2).length"
            icon="bi bi-play-circle-fill" type="success" />
        </div>
        <div class="col-md-3">
          <StatCard label="Đã hủy" :value="filteredShowtimes.filter(s => s.trangThai === 0).length"
            icon="bi bi-x-circle-fill" type="danger" />
        </div>
      </template>

      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input v-model="searchQuery" placeholder="Tìm tên phim..." :prefix-icon="Search" size="default"
            clearable />
        </div>

        <div class="filter-item">
          <el-date-picker v-model="filterDate" type="date" placeholder="Ngày chiếu" format="DD/MM/YYYY"
            value-format="YYYY-MM-DD" size="default" style="width: 140px;" />
        </div>

        <div class="filter-item">
          <el-select v-model="filterRoom" placeholder="Phòng chiếu" clearable style="width: 150px;">
            <template #prefix><el-icon>
                <Monitor />
              </el-icon></template>
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="room in phongChieuList" :key="room.id" :label="room.tenPhong" :value="room.id" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="filterTrangThai" placeholder="Trạng thái" clearable style="width: 140px;">
            <template #prefix><el-icon>
                <Filter />
              </el-icon></template>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>
      </template>

      <!-- Content Slot with BaseTable -->
      <template #content>
        <BaseTable :data="paginatedShowtimes" :columns="showtimeColumns" :loading="loading"
          :total="filteredShowtimes.length" v-model:currentPage="currentPage" v-model:pageSize="pageSize"
          v-model:selection="selectedShowtimes" :hide-pagination="true" @edit="openDialog" @delete="handleDelete">
          <template #cell-index="{ index }">
            <span class="text-secondary small">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-phim="{ row }">
            <div class="d-flex align-items-center justify-content-center gap-2">
              <img v-if="row.poster" :src="row.poster" class="rounded shadow-sm"
                style="width: 32px; height: 44px; object-fit: cover;" :alt="row.tenPhim" />
              <div v-else class="rounded d-flex align-items-center justify-content-center bg-light border shadow-sm"
                style="width: 32px; height: 44px;">
                <i class="bi bi-film text-secondary"></i>
              </div>
              <div class="fw-semibold small text-dark text-start" style="line-height: 1.2;">{{ row.tenPhim || '—' }}</div>
            </div>
          </template>

          <template #cell-loaiPhim="{ row }">
            <el-tag :type="row.loaiPhim === 'IMAX' ? 'warning' : (row.loaiPhim === '3D' ? 'danger' : 'primary')" 
              effect="dark" size="small" class="fw-bold px-2" round>
              {{ row.loaiPhim || '2D' }}
            </el-tag>
          </template>

          <template #cell-thoiLuong="{ row }">
            <span class="text-dark small fw-semibold">{{ row.thoiLuong ? row.thoiLuong + ' phút' : '—' }}</span>
          </template>

          <template #cell-phongChieu="{ row }">
            <div class="small fw-semibold text-dark"><i class="bi bi-door-open me-1 text-primary"></i>{{ row.tenPhongChieu }}</div>
          </template>

          <template #cell-loaiManHinh="{ row }">
            <span class="text-secondary small fw-semibold">{{ row.loaiManHinh || '—' }}</span>
          </template>

          <template #cell-ngayChieu="{ row }">
            <span class="small fw-semibold text-dark">{{ formatDate(row.ngayChieu) }}</span>
          </template>

          <template #cell-gioChieu="{ row }">
            <div class="small d-flex align-items-center justify-content-center gap-1">
              <el-tag type="info" effect="plain" size="small" class="fw-bold" style="font-size: 11px;">
                {{ row.gioBatDau }}
              </el-tag>
              <span class="text-secondary">→</span>
              <el-tag type="info" effect="plain" size="small" style="font-size: 11px;">{{ row.gioKetThuc }}</el-tag>
            </div>
          </template>

          <template #cell-soGheTrong="{ row }">
            <el-tag :type="row.soGheTrong > 0 ? 'success' : 'danger'" effect="light" size="small" class="fw-bold">
              {{ row.soGheTrong }}
            </el-tag>
          </template>

          <template #cell-trangThai="{ row }">
            <el-tag :type="getStatusTag(row.trangThai)" round size="small">
              {{ getStatusLabel(row.trangThai) }}
            </el-tag>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Chi tiết" placement="top">
                <button class="btn-action-icon btn-action-view" @click="handleView(row)">
                  <i class="bi bi-eye"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon btn-action-edit" @click="openDialog(row)">
                  <i class="bi bi-pencil"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Xóa" placement="top">
                <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)">
                  <i class="bi bi-trash"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <BaseModal v-model="detailVisible" title="Chi tiết suất chiếu" icon="bi bi-calendar-event" width="550px">
      <div v-if="selectedShowtime" class="p-2">
        <div class="d-flex gap-4 mb-4 pb-4 border-bottom">
          <img v-if="selectedShowtime.poster" :src="selectedShowtime.poster" class="rounded-3 shadow-sm"
            style="width: 100px; height: 140px; object-fit: cover;" />
          <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center border"
            style="width: 100px; height: 140px;">
            <i class="bi bi-film text-secondary fs-1"></i>
          </div>
          <div class="flex-grow-1">
            <h4 class="fw-bold text-dark mb-1">{{ selectedShowtime.tenPhim }}</h4>
            <div class="d-flex align-items-center gap-2 mb-3">
              <el-tag :type="getStatusTag(selectedShowtime.trangThai)" round size="small">{{
                getStatusLabel(selectedShowtime.trangThai) }}</el-tag>
              <el-tag type="danger" effect="dark" round size="small" v-if="selectedShowtime.loaiPhim">{{ selectedShowtime.loaiPhim }}</el-tag>
              <span class="text-secondary small"><i class="bi bi-clock me-1"></i>{{ selectedShowtime.thoiLuong }}
                phút</span>
            </div>

            <div class="bg-light p-3 rounded-4 border-0 shadow-none">
              <div class="row g-2">
                <div class="col-12">
                  <div class="small text-secondary mb-1">Phòng chiếu</div>
                  <div class="fw-bold text-dark"><i class="bi bi-door-open me-2 text-primary"></i>{{
                    selectedShowtime.tenPhongChieu }} ({{ selectedShowtime.loaiManHinh }})</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-calendar3 me-2 text-primary"></i>Ngày chiếu
              </div>
              <div class="val fw-bold" style="font-size: 15px;">{{ formatDate(selectedShowtime.ngayChieu) }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-alarm me-2 text-primary"></i>Thời gian</div>
              <div class="val fw-bold text-primary" style="font-size: 15px;">{{ selectedShowtime.gioBatDau }} <span
                  class="text-secondary fw-normal">→</span> {{ selectedShowtime.gioKetThuc }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-person-check me-2 text-primary"></i>Ghế trống</div>
              <div class="val fw-bold" :class="selectedShowtime.soGheTrong > 0 ? 'text-success' : 'text-danger'">
                {{ selectedShowtime.soGheTrong }} ghế
              </div>
            </div>
          </div>
          <div class="col-12">
            <div class="p-3 bg-yellow-50 rounded-4 border border-yellow-100 mb-0">
              <div class="small text-dark d-flex align-items-center gap-2">
                <i class="bi bi-info-circle-fill text-warning"></i>
                <span>Thời gian kết thúc đã bao gồm 15 phút dọn dẹp phòng máy.</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer></template>
    </BaseModal>

    <BaseModal v-model="dialogVisible" :title="editingId ? 'Cập nhật Suất chiếu' : 'Thêm Suất chiếu mới'"
      :icon="editingId ? 'bi bi-pencil-square' : 'bi bi-plus-lg'" width="820px"
      :confirmText="editingId ? 'Cập nhật ngay' : 'Thêm suất chiếu'" :loading="saving" @confirm="handleSubmit">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form">
        <div class="row g-4">
          <div class="col-12">
            <el-form-item label="Phim" prop="idPhim">
              <el-select v-model="form.idPhim" class="w-100" placeholder="Chọn phim" filterable :fit-input-width="true" :teleported="false">
                <template #prefix><i class="bi bi-film me-2"></i></template>
                <el-option v-for="p in phimList" :key="p.id" :label="`${p.tenPhim} [${p.loaiPhim || '2D'}] (${p.thoiLuong || '?'} phút)`"
                  :value="p.id">
                  <div class="d-flex justify-content-between align-items-center w-100">
                    <span>{{ p.tenPhim }}</span>
                    <el-tag size="small" :type="p.loaiPhim === 'IMAX' ? 'warning' : (p.loaiPhim === '3D' ? 'danger' : 'info')" effect="dark" round>
                      {{ p.loaiPhim || '2D' }}
                    </el-tag>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng" filterable :fit-input-width="true" :teleported="false">
                <template #prefix><i class="bi bi-door-open me-2"></i></template>
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-6">
            <el-form-item label="Ngày chiếu" prop="ngayChieu">
              <el-date-picker v-model="form.ngayChieu" type="date" class="w-100" placeholder="Chọn ngày"
                value-format="YYYY-MM-DD" :disabled-date="(d) => d < new Date(new Date().setHours(0, 0, 0, 0))"
                :prefix-icon="Calendar" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Trạng thái" prop="trangThai">
              <el-select v-model="form.trangThai" class="w-100" :fit-input-width="true" :teleported="false">
                <template #prefix><i class="bi bi-info-circle me-2"></i></template>
                <el-option label="Sắp chiếu" :value="1" />
                <el-option label="Đang chiếu" :value="2" />
                <el-option label="Đã hủy" :value="0" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-6">
            <el-form-item label="Giờ bắt đầu" prop="gioBatDau">
              <el-time-picker v-model="form.gioBatDau" class="w-100" placeholder="00:00" format="HH:mm"
                value-format="HH:mm:ss" :prefix-icon="Clock" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ kết thúc (+15p dọn)" prop="gioKetThuc">
              <el-time-picker v-model="form.gioKetThuc" class="w-100" placeholder="Tự động tính..." format="HH:mm"
                value-format="HH:mm:ss" readonly :prefix-icon="Clock" />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { Search, Calendar, Monitor, Operation, Delete, Filter } from '@element-plus/icons-vue';
import { suatChieuService } from '@/services/api/admin/suatChieuService';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';

const showtimeColumns = [
  { label: 'STT', key: 'index', width: '60px' },
  { label: 'PHIM', key: 'phim', minWidth: '220px' },
  { label: 'ĐỊNH DẠNG', key: 'loaiPhim', width: '130px' },
  { label: 'THỜI LƯỢNG', key: 'thoiLuong', width: '170px' },
  { label: 'PHÒNG CHIẾU', key: 'phongChieu', width: '190px' },
  { label: 'LOẠI MÀN HÌNH', key: 'loaiManHinh', width: '160px' },
  { label: 'NGAY CHIẾU', key: 'ngayChieu', width: '120px' },
  { label: 'GIỜ CHIẾU', key: 'gioChieu', width: '170px' },
  { label: 'TRỐNG', key: 'soGheTrong', width: '100px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '110px' },
];

// =================== STATE ===================
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const selectedShowtime = ref(null);
const editingId = ref(null);
const formRef = ref(null);

const handleView = (row) => {
  selectedShowtime.value = row;
  detailVisible.value = true;
};

const showtimes = ref([]);
const phongChieuList = ref([]);
const phimList = ref([]);

const filterDate = ref(null);
const filterRoom = ref(null);
const filterTrangThai = ref('');
const searchQuery = ref('');

// Pagination state
const currentPage = ref(1);
const pageSize = ref(5);
const selectedShowtimes = ref([]);
const selectedIds = computed(() => selectedShowtimes.value.map(item => item.id));

const handleBulkDelete = () => {
  confirmDialog.custom(
    `Xác nhận xóa <b>${selectedIds.value.length}</b> suất chiếu đã chọn?`,
    'Xóa hàng loạt',
    'Đồng ý'
  ).then(async () => {
    try {
      await Promise.all(selectedIds.value.map(id => suatChieuService.deleteShowtime(id)));
      notification.success(`Đã xóa ${selectedIds.value.length} suất chiếu`);
      selectedShowtimes.value = [];
      fetchShowtimes();
    } catch (error) {
      notification.error('Có lỗi khi xóa hàng loạt');
    }
  }).catch(() => { });
};

const form = ref({
  idPhim: '',
  idPhongChieu: '',
  ngayChieu: null,
  gioBatDau: null,
  gioKetThuc: null,
  trangThai: 1
});

const rules = {
  idPhim: [{ required: true, message: 'Chọn phim', trigger: 'change' }],
  idPhongChieu: [{ required: true, message: 'Chọn phòng chiếu', trigger: 'change' }],
  ngayChieu: [{ required: true, message: 'Chọn ngày chiếu', trigger: 'change' }],
  gioBatDau: [{ required: true, message: 'Chọn giờ bắt đầu', trigger: 'change' }],
  gioKetThuc: [{ required: true, message: 'Chọn giờ kết thúc', trigger: 'change' }],
};

// =================== WATCHERS ===================
watch([() => form.value.idPhim, () => form.value.gioBatDau], ([newPhimId, newStart]) => {
  if (newPhimId && newStart) {
    const phim = phimList.value.find(p => p.id === newPhimId);
    if (phim && phim.thoiLuong) {
      // Logic: Giờ kết thúc = Giờ bắt đầu + thoiLuong (phút) + 15p dọn phòng
      const [hours, minutes] = newStart.split(':').map(Number);
      const totalMinutes = hours * 60 + minutes + phim.thoiLuong + 15;

      const endHours = Math.floor(totalMinutes / 60) % 24;
      const endMinutes = totalMinutes % 60;

      form.value.gioKetThuc = `${String(endHours).padStart(2, '0')}:${String(endMinutes).padStart(2, '0')}:00`;
    }
  }
});

// =================== COMPUTED ===================
const filteredShowtimes = computed(() => {
  return showtimes.value.filter(s => {
    const matchRoom = !filterRoom.value || s.idPhongChieu === filterRoom.value;
    const matchStatus = filterTrangThai.value === ''
      ? true
      : s.trangThai === filterTrangThai.value;
    const matchSearch = !searchQuery.value
      || s.tenPhim?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchRoom && matchStatus && matchSearch;
  });
});

const paginatedShowtimes = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredShowtimes.value.slice(start, end);
});

// =================== METHODS ===================
const fetchDropdowns = async () => {
  try {
    const [phongRes, phimRes] = await Promise.all([
      suatChieuService.getPhongChieuDropdown(),
      suatChieuService.getPhimDropdown()
    ]);
    phongChieuList.value = phongRes.data?.data || [];
    phimList.value = phimRes.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải dữ liệu dropdown');
  }
};

const fetchShowtimes = async () => {
  loading.value = true;
  try {
    const params = {};
    if (filterDate.value) params.ngayChieu = filterDate.value;
    if (filterRoom.value) params.idPhongChieu = filterRoom.value;
    const res = await suatChieuService.getShowtimes(params);
    showtimes.value = res.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải danh sách suất chiếu');
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  filterDate.value = null;
  filterRoom.value = null;
  filterTrangThai.value = '';
  searchQuery.value = '';
  currentPage.value = 1;
  fetchShowtimes();
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  if (row) {
    form.value = {
      idPhim: row.idPhim,
      idPhongChieu: row.idPhongChieu,
      ngayChieu: row.ngayChieu,
      gioBatDau: row.gioBatDau,
      gioKetThuc: row.gioKetThuc,
      trangThai: row.trangThai
    };
  } else {
    form.value = {
      idPhim: '',
      idPhongChieu: '',
      ngayChieu: null,
      gioBatDau: null,
      gioKetThuc: null,
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
        await confirmDialog.update('suất chiếu');
      } else {
        await confirmDialog.add('suất chiếu');
      }
    } catch { return; }

    saving.value = true;
    try {
      if (editingId.value) {
        await suatChieuService.updateShowtime(editingId.value, form.value);
        notification.updateSuccess('suất chiếu');
      } else {
        await suatChieuService.createShowtime(form.value);
        notification.addSuccess('suất chiếu');
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleDelete = (row) => {
  confirmDialog.delete('suất chiếu', row.tenPhim)
  .then(async () => {
    try {
      await suatChieuService.deleteShowtime(row.id);
      notification.deleteSuccess('suất chiếu');
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Xóa thất bại');
    }
  }).catch(() => { });
};

// =================== HELPERS ===================
const getStatusTag = (status) => {
  const map = { 0: 'danger', 1: 'primary', 2: 'success', 3: 'info' };
  return map[status] || 'info';
};

const getStatusLabel = (status) => {
  const map = { 0: 'Đã hủy', 1: 'Sắp chiếu', 2: 'Đang chiếu', 3: 'Kết thúc' };
  return map[status] || 'Không xác định';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

// =================== LIFECYCLE ===================
onMounted(async () => {
  await fetchDropdowns();
  await fetchShowtimes();
});
</script>

<style scoped>
.admin-showtimes {
  height: calc(100vh - 84px);
}
</style>
