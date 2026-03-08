<template>
  <div class="admin-schedule-page">
    <AdminTableLayout
      title="Sơ đồ lịch chiếu"
      titleIcon="bi bi-grid-3x3-gap-fill"
      :data="showtimes"
      :loading="loading"
      :total="showtimes.length"
      :hide-pagination="true"
      :hide-filter="true"
      :disable-padding="true"
      @reset-filter="resetFilter"
    >
      <!-- Header Actions: room selector -->
      <template #header-actions>
        <div class="d-flex align-items-center gap-2 border-start ps-3">
          <span class="text-secondary small fw-bold">PHÒNG:</span>
          <el-select v-model="visualRoomId" placeholder="Chọn phòng" style="width:160px;" size="default">
            <template #prefix><i class="bi bi-door-open"></i></template>
            <el-option v-for="r in phongChieuList" :key="r.id" :label="r.name || r.tenPhong" :value="r.id" />
          </el-select>
        </div>
      </template>

      <!-- Content -->
      <template #content>
        <ScheduleVisual
          :showtimes="showtimes"
          :phongChieuList="phongChieuList"
          :phimList="phimList"
          v-model:visualRoomId="visualRoomId"
          @view="handleView"
          @gridClick="handleGridClick"
          @weekChange="fetchShowtimes"
        />
      </template>
    </AdminTableLayout>

    <!-- Modals (Shared with List view if needed, but here simple duplication for separation) -->
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

// Import sub-components
import ScheduleVisual from './components/ScheduleVisual.vue';

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
const visualRoomId   = ref('');

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
watch(visualRoomId, () => fetchShowtimes());

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
async function fetchShowtimes(offset = 0) {
  loading.value = true;
  try {
    const params = {};
    if (visualRoomId.value) params.idPhongChieu = visualRoomId.value;
    const res = await suatChieuService.getShowtimes(params);
    showtimes.value = res.data?.data || [];
  } catch {
    notification.error('Không thể tải danh sách lịch chiếu');
  } finally {
    loading.value = false;
  }
}

const resetFilter = () => {
  fetchShowtimes();
};

const handleView = (row) => { selectedShowtime.value = row; detailVisible.value = true; };

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  form.value = row ? { ...row }
    : { idPhim: '', idPhongChieu: '', ngayChieu: null, gioBatDau: null, gioKetThuc: null, trangThai: 1 };
  dialogVisible.value = true;
};

const handleGridClick = ({ date, hour, selectedPhimId }) => {
  if (!selectedPhimId) { notification.warning('Vui lòng chọn phim trước'); return; }
  if (!visualRoomId.value)   { notification.warning('Vui lòng chọn phòng chiếu'); return; }
  openDialog(null);
  form.value.idPhim        = selectedPhimId;
  form.value.idPhongChieu  = visualRoomId.value;
  form.value.ngayChieu     = date;
  form.value.gioBatDau     = `${String(hour).padStart(2,'0')}:00:00`;
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
  if (phongChieuList.value.length) visualRoomId.value = phongChieuList.value[0].id;
  await fetchShowtimes();
});
</script>

<style scoped>
.admin-schedule-page { height: 100%; display: flex; flex-direction: column; }
.admin-schedule-page :deep(.admin-table-layout) { flex: 1; }
</style>
