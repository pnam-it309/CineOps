<template>
  <div class="admin-schedule-page">
    <AdminTableLayout
      title="Danh sách lịch chiếu"
      titleIcon="bi bi-list-ul"
      :data="filteredShowtimes"
      :loading="loading"
      :total="filteredShowtimes.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @reset-filter="resetFilter"
    >
      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input v-model="searchQuery" placeholder="Tìm tên phim (ẩn trong bảng)..." :prefix-icon="Search" clearable @input="handleSearch" style="width: 250px;" />
        </div>
        <div class="filter-item">
          <el-date-picker v-model="filterDate" type="date" placeholder="Chọn ngày chiếu" format="DD/MM/YYYY" value-format="YYYY-MM-DD" style="width: 180px;" @change="handleSearch" clearable />
        </div>
        <div class="filter-item">
          <el-select v-model="filterRoomId" placeholder="Chọn phòng" style="width: 180px;" @change="handleSearch" clearable>
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="r in phongChieuList" :key="r.id" :label="r.tenPhong || r.name" :value="r.id" />
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="filterStatus" placeholder="Chọn trạng thái" style="width: 180px;" @change="handleSearch" clearable>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
            <el-option label="Kết thúc" :value="3" />
          </el-select>
        </div>
      </template>

      <!-- Content -->
      <template #content>
        <div class="h-100">
          <BaseTable
            :data="paginatedShowtimes"
            :columns="listColumns"
            :loading="loading"
            :total="filteredShowtimes.length"
            v-model:currentPage="currentPage"
            v-model:pageSize="pageSize"
            :hide-pagination="true"
            @edit="openDialog"
            @delete="handleUpdateStatus"
          >
            <template #cell-stt="{ index }">
              <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
            </template>

            <template #cell-phong="{ row }">
              <div class="fw-bold text-dark small">
                <i class="bi bi-door-open me-1 text-primary"></i>{{ row.tenPhongChieu }}
              </div>
            </template>

            <template #cell-ngay="{ row }">
              <span class="small fw-semibold">{{ formatDate(row.ngayChieu) }}</span>
            </template>

            <template #cell-gio="{ row }">
              <div class="small d-flex align-items-center gap-1">
                <el-tag type="info" effect="plain" size="small" class="fw-bold">{{ row.gioBatDau }}</el-tag>
                <span class="text-secondary">→</span>
                <el-tag type="info" effect="plain" size="small">{{ row.gioKetThuc }}</el-tag>
              </div>
            </template>

            <template #cell-ghe="{ row }">
              <el-tag :type="row.soGheTrong > 10 ? 'success' : 'warning'" size="small" round>
                {{ row.soGheTrong }} ghế
              </el-tag>
            </template>

            <template #cell-status="{ row }">
              <el-tag :type="getStatusTag(row.trangThai)" size="small" round>
                {{ getStatusLabel(row.trangThai) }}
              </el-tag>
            </template>

            <template #actions="{ row }">
              <div class="d-flex justify-content-center gap-1">
                <el-tooltip content="Xem chi tiết (Bao gồm Phim)" placement="top">
                  <button class="btn-action-icon action-view" @click="handleView(row)">
                    <i class="bi bi-eye"></i>
                  </button>
                </el-tooltip>
                <el-tooltip content="Chỉnh sửa" placement="top">
                  <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0 || row.trangThai === 3" @click="openDialog(row)">
                    <i class="bi bi-pencil"></i>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </BaseTable>
        </div>
      </template>
    </AdminTableLayout>

    <!-- Modals -->
    <BaseModal v-model="detailVisible" title="Chi tiết lịch chiếu" icon="bi bi-calendar-event" width="550px">
      <div v-if="selectedShowtime" class="p-2">
        <div class="d-flex gap-4 mb-4 pb-4 border-bottom">
          <img v-if="selectedShowtime.poster" :src="selectedShowtime.poster" class="rounded-3 shadow-sm"
            style="width:100px;height:140px;object-fit:cover;" />
          <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center border"
            style="width:100px;height:140px;">
            <i class="bi bi-film text-secondary fs-1"></i>
          </div>
          <div class="flex-grow-1">
            <h4 class="fw-bold text-dark mb-1">{{ selectedShowtime.tenPhim }}</h4>
            <div class="d-flex align-items-center gap-2">
              <el-tag :type="getStatusTag(selectedShowtime.trangThai)" round size="small">
                {{ getStatusLabel(selectedShowtime.trangThai) }}
              </el-tag>
              <el-tag type="danger" effect="dark" round size="small" v-if="selectedShowtime.loaiPhim">
                {{ selectedShowtime.loaiPhim }}
              </el-tag>
              <span class="text-secondary small"><i class="bi bi-clock me-1"></i>{{ selectedShowtime.thoiLuong }} phút</span>
            </div>
          </div>
        </div>
        <div class="bg-light p-3 rounded-4 mb-4">
          <div class="small text-secondary mb-1">Phòng chiếu</div>
          <div class="fw-bold text-dark">
            <i class="bi bi-door-open me-2 text-primary"></i>
            {{ selectedShowtime.tenPhongChieu }} ({{ selectedShowtime.loaiManHinh }})
          </div>
        </div>
        <div class="row g-4">
          <div class="col-6">
            <div class="p-3 bg-light rounded-3">
              <div class="small text-secondary mb-1"><i class="bi bi-calendar3 me-2 text-primary"></i>Ngày chiếu</div>
              <div class="fw-bold">{{ formatDate(selectedShowtime.ngayChieu) }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="p-3 bg-light rounded-3">
              <div class="small text-secondary mb-1"><i class="bi bi-alarm me-2 text-primary"></i>Thời gian</div>
              <div class="fw-bold text-primary">{{ selectedShowtime.gioBatDau }} → {{ selectedShowtime.gioKetThuc }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer></template>
    </BaseModal>

    <BaseModal
      v-model="dialogVisible"
      :title="editingId ? 'Cập nhật lịch chiếu' : 'Thêm lịch chiếu mới'"
      icon="bi bi-calendar-plus"
      width="820px"
      :confirmText="editingId ? 'Cập nhật' : 'Thêm lịch chiếu'"
      :loading="saving"
      @confirm="handleSubmit"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-4">
          <div class="col-12">
            <el-form-item label="Phim" prop="idPhim">
              <el-select v-model="form.idPhim" class="w-100" placeholder="Chọn phim" filterable>
                <el-option v-for="p in phimList" :key="p.id"
                  :label="`${p.tenPhim} [${p.loaiPhim || '2D'}]`" :value="p.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng" filterable>
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày chiếu" prop="ngayChieu">
              <el-date-picker v-model="form.ngayChieu" type="date" class="w-100"
                placeholder="Chọn ngày" value-format="YYYY-MM-DD" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ bắt đầu" prop="gioBatDau">
              <el-time-picker v-model="form.gioBatDau" class="w-100" format="HH:mm" value-format="HH:mm:00" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ kết thúc (dự kiến)">
              <el-time-picker v-model="form.gioKetThuc" class="w-100" format="HH:mm" value-format="HH:mm:00" readonly disabled />
              <div class="small text-secondary mt-1">Đã tính 15 phút dọn dẹp</div>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { Search, Plus, Calendar } from '@element-plus/icons-vue';
import { suatChieuService } from '@/services/api/admin/suatChieuService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import dayjs from 'dayjs';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import debounce from 'lodash/debounce';

// ── State ───────────────────────────────────────────────────────────────
const loading    = ref(false);
const saving     = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const selectedShowtime = ref(null);
const editingId  = ref(null);
const formRef    = ref(null);

const showtimes      = ref([]);
const phongChieuList = ref([]);
const phimList       = ref([]);

// List view filters & pagination
const searchQuery = ref('');
const filterDate = ref(null);
const filterRoomId = ref('');
const filterStatus = ref('');
const currentPage = ref(1);
const pageSize = ref(5);

const listColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Phòng chiếu', key: 'phong', minWidth: '150px' },
  { label: 'Ngày chiếu', key: 'ngay', width: '130px' },
  { label: 'Giờ chiếu', key: 'gio', width: '200px' },
  { label: 'Ghế trống', key: 'ghe', width: '120px' },
  { label: 'Trạng thái', key: 'status', width: '130px' },
];

const form = ref({
  idPhim: '', idPhongChieu: '',
  ngayChieu: null, gioBatDau: null, gioKetThuc: null, trangThai: 1
});

const rules = {
  idPhim:       [{ required: true, message: 'Vui lòng chọn phim',        trigger: 'change' }],
  idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
  ngayChieu:    [{ required: true, message: 'Vui lòng chọn ngày chiếu',  trigger: 'change' }],
  gioBatDau:    [{ required: true, message: 'Vui lòng chọn giờ bắt đầu', trigger: 'change' }],
};

// ── Watchers ──────────────────────────────────────────────────────────────────
// Auto-tính giờ kết thúc
watch([() => form.value.idPhim, () => form.value.gioBatDau], ([newPhimId, newStart]) => {
  if (newPhimId && newStart) {
    const phim = phimList.value.find(p => p.id === newPhimId);
    if (phim?.thoiLuong) {
      const [h, m] = newStart.split(':').map(Number);
      const total = h * 60 + m + phim.thoiLuong + 15;
      form.value.gioKetThuc =
        `${String(Math.floor(total / 60) % 24).padStart(2,'0')}:${String(total % 60).padStart(2,'0')}:00`;
    }
  }
});

// ── Methods ───────────────────────────────────────────────────────────────────
async function fetchShowtimes() {
  loading.value = true;
  try {
    const res = await suatChieuService.getShowtimes({});
    showtimes.value = res.data?.data || [];
  } catch {
    notification.error('Không thể tải danh sách lịch chiếu');
  } finally {
    loading.value = false;
  }
}

const filteredShowtimes = computed(() => {
  return showtimes.value.filter(s => {
    const matchRoomRes = !filterRoomId.value || s.idPhongChieu === filterRoomId.value;
    const matchStatusRes = filterStatus.value === '' || s.trangThai === filterStatus.value;
    const matchDateRes = !filterDate.value || s.ngayChieu === filterDate.value;
    const matchSearchRes = !searchQuery.value || s.tenPhim?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchRoomRes && matchStatusRes && matchDateRes && matchSearchRes;
  });
});

const paginatedShowtimes = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredShowtimes.value.slice(start, start + pageSize.value);
});

const handleSearch = debounce(() => {
  currentPage.value = 1;
}, 300);

const resetFilter = () => {
  searchQuery.value = '';
  filterDate.value = null;
  filterRoomId.value = '';
  filterStatus.value = '';
  currentPage.value = 1;
};

const handleView = (row) => { selectedShowtime.value = row; detailVisible.value = true; };

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  form.value = row ? { ...row }
    : { idPhim: '', idPhongChieu: '', ngayChieu: null, gioBatDau: null, gioKetThuc: null, trangThai: 1 };
  dialogVisible.value = true;
};

const handleUpdateStatus = (row, status = null) => {
  const newStatus = status !== null ? status : (row.trangThai === 0 ? 1 : 0);
  const label = getStatusLabel(newStatus).toLowerCase();
  
  if (newStatus === row.trangThai) return;

  confirmDialog.custom(`Đổi trạng thái lịch chiếu sang <b>${label}</b>?`, 'Xác nhận').then(async () => {
    try {
      await suatChieuService.updateShowtime(row.id, { ...row, trangThai: newStatus });
      notification.success('Cập nhật trạng thái thành công');
      fetchShowtimes();
    } catch { notification.error('Cập nhật thất bại'); }
  }).catch(() => {});
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (editingId.value) {
        await suatChieuService.updateShowtime(editingId.value, form.value);
        notification.updateSuccess('lịch chiếu');
      } else {
        await suatChieuService.createShowtime(form.value);
        notification.addSuccess('lịch chiếu');
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
}

// Common helpers
const getStatusTag   = (s) => ({ 0: 'danger', 1: 'primary', 2: 'success', 3: 'info' }[s] || 'info');
const getStatusLabel = (s) => ({ 0: 'Đã hủy', 1: 'Sắp chiếu', 2: 'Đang chiếu', 3: 'Kết thúc' }[s] || '—');
const formatDate     = (d) => d ? dayjs(d).format('DD/MM/YYYY') : '—';

// ── Lifecycle ─────────────────────────────────────────────────────────────────
onMounted(async () => {
  const [phongRes, phimRes] = await Promise.all([
    suatChieuService.getPhongChieuDropdown(),
    suatChieuService.getPhimDropdown()
  ]);
  phongChieuList.value = phongRes.data?.data || [];
  phimList.value       = phimRes.data?.data  || [];
  await fetchShowtimes();
});
</script>

<style scoped>
.admin-schedule-page { height: 100%; display: flex; flex-direction: column; }
.admin-schedule-page :deep(.admin-table-layout) { flex: 1; }
</style>
