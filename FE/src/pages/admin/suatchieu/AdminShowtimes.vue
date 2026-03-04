<template>
  <div class="admin-showtimes-page">
    <AdminTableLayout 
      title="Quản lí suất chiếu" 
      titleIcon="bi bi-calendar2-week-fill" 
      :addButtonLabel="viewMode === 'list' ? 'Thêm suất chiếu' : null"
      :data="filteredShowtimes" 
      :loading="loading" 
      :total="filteredShowtimes.length" 
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" 
      v-model:selection="selectedShowtimes" 
      @add-click="openDialog()"
      @reset-filter="resetFilter" 
      :hide-pagination="viewMode === 'visual'"
      :hide-filter="viewMode === 'visual'"
      :disable-padding="viewMode === 'visual'"
    >
      <!-- Header Actions Slot: Gom nhóm các nút về bên phải -->
      <template #header-actions v-if="viewMode === 'visual'">
        <div class="d-flex align-items-center gap-3">
          <div class="d-flex align-items-center gap-2 border-start ps-3">
            <span class="text-secondary small fw-bold">PHÒNG:</span>
            <el-select v-model="visualRoomId" placeholder="Chọn phòng" style="width: 160px;" size="default" class="premium-select">
              <template #prefix><i class="bi bi-door-open"></i></template>
              <el-option v-for="room in phongChieuList" :key="room.id" :label="room.name || room.tenPhong" :value="room.id" />
            </el-select>
          </div>
        </div>
      </template>
      <!-- Filter Slot (Only in List Mode) -->
      <template #filters v-if="viewMode === 'list'">
        <div class="filter-item search-input-wrapper">
          <el-input v-model="searchQuery" placeholder="Tìm tên phim..." :prefix-icon="Search" size="default" clearable />
        </div>
        <div class="filter-item">
          <el-date-picker v-model="filterDate" type="date" placeholder="Ngày chiếu" format="DD/MM/YYYY" value-format="YYYY-MM-DD" size="default" style="width: 140px;" />
        </div>
        <div class="filter-item">
          <el-select v-model="filterRoom" placeholder="Phòng chiếu" clearable style="width: 150px;">
            <template #prefix><el-icon><Monitor /></el-icon></template>
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="room in phongChieuList" :key="room.id" :label="room.tenPhong" :value="room.id" />
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="filterTrangThai" placeholder="Trạng thái" clearable style="width: 140px;">
            <template #prefix><el-icon><Filter /></el-icon></template>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>
      </template>

      <!-- Content Slot -->
      <template #content>
        <AdminShowtimesList 
          v-if="viewMode === 'list'"
          :data="filteredShowtimes"
          :loading="loading"
          :total="filteredShowtimes.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          v-model:selection="selectedShowtimes"
          @edit="openDialog"
          @delete="handleDelete"
          @view="handleView"
        />
        
        <AdminShowtimesVisual 
          v-if="viewMode === 'visual'"
          :showtimes="showtimes"
          :phimList="phimList"
          :phongChieuList="phongChieuList"
          v-model:visualRoomId="visualRoomId"
          @grid-click="handleGridClick"
          @view="handleView"
          @week-change="fetchShowtimes"
        />
      </template>
    </AdminTableLayout>

    <!-- Modals (Shared) -->
    <BaseModal v-model="detailVisible" title="Chi tiết suất chiếu" icon="bi bi-calendar-event" width="550px">
      <div v-if="selectedShowtime" class="p-2">
        <div class="d-flex gap-4 mb-4 pb-4 border-bottom">
          <img v-if="selectedShowtime.poster" :src="selectedShowtime.poster" class="rounded-3 shadow-sm" style="width: 100px; height: 140px; object-fit: cover;" />
          <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center border" style="width: 100px; height: 140px;">
            <i class="bi bi-film text-secondary fs-1"></i>
          </div>
          <div class="flex-grow-1">
            <h4 class="fw-bold text-dark mb-1">{{ selectedShowtime.tenPhim }}</h4>
            <div class="d-flex align-items-center gap-2 mb-3">
              <el-tag :type="getStatusTag(selectedShowtime.trangThai)" round size="small">{{ getStatusLabel(selectedShowtime.trangThai) }}</el-tag>
              <el-tag type="danger" effect="dark" round size="small" v-if="selectedShowtime.loaiPhim">{{ selectedShowtime.loaiPhim }}</el-tag>
              <span class="text-secondary small"><i class="bi bi-clock me-1"></i>{{ selectedShowtime.thoiLuong }} phút</span>
            </div>
          </div>
        </div>
        <div class="bg-light p-3 rounded-4 mb-4">
          <div class="small text-secondary mb-1">Phòng chiếu</div>
          <div class="fw-bold text-dark"><i class="bi bi-door-open me-2 text-primary"></i>{{ selectedShowtime.tenPhongChieu }} ({{ selectedShowtime.loaiManHinh }})</div>
        </div>
        <div class="row g-4">
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-calendar3 me-2 text-primary"></i>Ngày chiếu</div>
              <div class="val fw-bold">{{ formatDate(selectedShowtime.ngayChieu) }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-alarm me-2 text-primary"></i>Thời gian</div>
              <div class="val fw-bold text-primary">{{ selectedShowtime.gioBatDau }} → {{ selectedShowtime.gioKetThuc }}</div>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>

    <BaseModal v-model="dialogVisible" :title="editingId ? 'Cập nhật Suất chiếu' : 'Thêm Suất chiếu mới'"
      icon="bi bi-pencil-square" width="820px" :confirmText="editingId ? 'Cập nhật ngay' : 'Thêm suất chiếu'" :loading="saving" @confirm="handleSubmit">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-4">
          <div class="col-12">
            <el-form-item label="Phim" prop="idPhim">
              <el-select v-model="form.idPhim" class="w-100" placeholder="Chọn phim" filterable>
                <el-option v-for="p in phimList" :key="p.id" :label="`${p.tenPhim} [${p.loaiPhim || '2D'}]`" :value="p.id" />
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
              <el-date-picker v-model="form.ngayChieu" type="date" class="w-100" placeholder="Chọn ngày" value-format="YYYY-MM-DD" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ bắt đầu" prop="gioBatDau">
              <el-time-picker v-model="form.gioBatDau" class="w-100" format="HH:mm" value-format="HH:mm:00" />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { Search, Monitor, Filter, Refresh } from '@element-plus/icons-vue';
import { suatChieuService } from '@/services/api/admin/suatChieuService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import dayjs from 'dayjs';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseModal from '@/components/common/BaseModal.vue';

// Import Components
import AdminShowtimesList from './AdminShowtimesList.vue';
import AdminShowtimesVisual from './AdminShowtimesVisual.vue';

// =================== STATE ===================
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const selectedShowtime = ref(null);
const editingId = ref(null);
const formRef = ref(null);
const route = useRoute();

const viewMode = computed(() => route.query.tab || 'list');
const visualRoomId = ref('');
const showtimes = ref([]);
const phongChieuList = ref([]);
const phimList = ref([]);

const filterDate = ref(null);
const filterRoom = ref(null);
const filterTrangThai = ref('');
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(5);
const selectedShowtimes = ref([]);

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
};

// =================== COMPUTED ===================
const filteredShowtimes = computed(() => {
  return showtimes.value.filter(s => {
    const matchRoom = !filterRoom.value || s.idPhongChieu === filterRoom.value;
    const matchStatus = filterTrangThai.value === '' ? true : s.trangThai === filterTrangThai.value;
    const matchSearch = !searchQuery.value || s.tenPhim?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchRoom && matchStatus && matchSearch;
  });
});

const selectedPhimInfo = computed(() => {
  return phimList.value.find(p => p.id === form.value.idPhim);
});

// =================== WATCHERS ===================
watch(viewMode, (newMode) => {
  fetchShowtimes();
}, { immediate: true });

watch(visualRoomId, () => {
  if (viewMode.value === 'visual') fetchShowtimes();
});

watch([() => form.value.idPhim, () => form.value.gioBatDau], ([newPhimId, newStart]) => {
  if (newPhimId && newStart) {
    const phim = phimList.value.find(p => p.id === newPhimId);
    if (phim && phim.thoiLuong) {
      const [hours, minutes] = newStart.split(':').map(Number);
      const totalMinutes = hours * 60 + minutes + phim.thoiLuong + 15;
      const endHours = Math.floor(totalMinutes / 60) % 24;
      const endMinutes = totalMinutes % 60;
      form.value.gioKetThuc = `${String(endHours).padStart(2, '0')}:${String(endMinutes).padStart(2, '0')}:00`;
    }
  }
});

// =================== METHODS ===================
async function fetchShowtimes() {
  loading.value = true;
  try {
    const params = {};
    if (viewMode.value === 'visual') {
      if (visualRoomId.value) params.idPhongChieu = visualRoomId.value;
    } else {
      if (filterDate.value) params.ngayChieu = filterDate.value;
      if (filterRoom.value) params.idPhongChieu = filterRoom.value;
    }
    const res = await suatChieuService.getShowtimes(params);
    showtimes.value = res.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải danh sách suất chiếu');
  } finally {
    loading.value = false;
  }
}

const resetFilter = () => {
  filterDate.value = null;
  filterRoom.value = null;
  filterTrangThai.value = '';
  searchQuery.value = '';
  currentPage.value = 1;
  fetchShowtimes();
};

const handleView = (row) => {
  selectedShowtime.value = row;
  detailVisible.value = true;
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  if (row) {
    form.value = { ...row };
  } else {
    form.value = { idPhim: '', idPhongChieu: '', ngayChieu: null, gioBatDau: null, gioKetThuc: null, trangThai: 1 };
  }
  dialogVisible.value = true;
};

const handleGridClick = ({ date, hour, selectedPhimId }) => {
  if (!selectedPhimId) {
    notification.warning('Vui lòng chọn phim trước');
    return;
  }
  if (!visualRoomId.value) {
    notification.warning('Vui lòng chọn phòng chiếu');
    return;
  }
  openDialog(null);
  form.value.idPhim = selectedPhimId;
  form.value.idPhongChieu = visualRoomId.value;
  form.value.ngayChieu = date;
  form.value.gioBatDau = `${hour.toString().padStart(2, '0')}:00:00`;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    try {
      saving.value = true;
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
  const isInactive = row.trangThai === 0;
  const newStatus = isInactive ? 1 : 0;
  confirmDialog.custom(`Đổi trạng thái suất chiếu?`, 'Xác nhận').then(async () => {
    try {
      await suatChieuService.updateShowtime(row.id, { ...row, trangThai: newStatus });
      notification.success('Thành công');
      fetchShowtimes();
    } catch { notification.error('Lỗi'); }
  });
};

const getStatusTag = (s) => ({ 0: 'danger', 1: 'primary', 2: 'success', 3: 'info' }[s] || 'info');
const getStatusLabel = (s) => ({ 0: 'Đã hủy', 1: 'Sắp chiếu', 2: 'Đang chiếu', 3: 'Kết thúc' }[s] || '—');
const formatDate = (d) => d ? dayjs(d).format('DD/MM/YYYY') : '—';

onMounted(async () => {
  const [phongRes, phimRes] = await Promise.all([
    suatChieuService.getPhongChieuDropdown(),
    suatChieuService.getPhimDropdown()
  ]);
  phongChieuList.value = phongRes.data?.data || [];
  phimList.value = phimRes.data?.data || [];
  if (phongChieuList.value.length) visualRoomId.value = phongChieuList.value[0].id;
  await fetchShowtimes();
});
</script>

<style scoped>
.admin-showtimes-page {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Đảm bảo flex grow cho toàn bộ wrapper của bảng khi dùng height 100% */
.admin-showtimes-page :deep(.admin-table-layout) {
  flex: 1;
}

/* Ghi đè padding nội bộ bảng AdminTableLayout khi có mode visual */
.admin-showtimes-page :deep(.admin-table-layout.p-4) {
  padding: 24px;
}
.admin-showtimes-page :deep(.admin-table-layout:not(.p-4)) {
  padding: 24px 24px 0 24px;
}
</style>
