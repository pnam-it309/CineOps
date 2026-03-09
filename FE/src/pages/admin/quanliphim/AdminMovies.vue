<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { Search, Refresh } from '@element-plus/icons-vue';
import { phimApi } from '@/services/api/admin/phimService';
import BaseTable from '@/components/common/BaseTable.vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';


// ── State ─────────────────────────────────────────────────────────────────────
const router       = useRouter();
const moviesList   = ref([]);
const loading      = ref(false);
const totalElements = ref(0);
const currentPage  = ref(1);
const pageSize     = ref(5);

const searchQuery  = ref('');
const statusFilter = ref(null);
const genreFilter  = ref(null);
const genreOptions = ref([]);

const selectedPhim = ref([]);

const handleView = (row) => {
  router.push({ 
    name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_DETAIL.name, 
    params: { id: row.id } 
  });
};

// ── Columns ───────────────────────────────────────────────────────────────────
const tableColumns = [
  { label: 'STT',           key: 'stt',          width: '60px'   },
  { label: 'Mã phim',       key: 'maPhim',       width: '100px'  },
  { label: 'Poster',        key: 'poster',       width: '80px'   },
  { label: 'Tên phim',      key: 'tenPhim',      width: '180px' },
  { label: 'Độ tuổi',       key: 'nhanDoTuoi',   width: '90px'   },
  { label: 'Thời lượng',    key: 'thoiLuong',    width: '100px'  },
  { label: 'Thể loại',      key: 'theLoais',     width: '160px'  },
  { label: 'Giá gốc',       key: 'giaPhim',      width: '110px'  },
  { label: 'Trạng thái',    key: 'trangThai',    width: '140px'  },
];

// ── Computed ──────────────────────────────────────────────────────────────────
const selectedIdsCount = computed(() => selectedPhim.value.length);

// ── Helpers ───────────────────────────────────────────────────────────────────
const getTrangThaiLabel = (tt) => ({ 1: 'Đang chiếu', 2: 'Sắp chiếu', 0: 'Ngừng chiếu' }[tt] ?? '—');
const getTrangThaiClass = (tt) => ({ 1: 'status-now', 2: 'status-soon', 0: 'status-stop' }[tt] ?? '');

const getLoaiPhimType = (loaiPhim) => {
  const types = { '2D': 'info', '3D': 'primary', 'IMAX': 'danger', '4DX': 'warning' };
  return types[loaiPhim] || 'info';
};

const getDoTuoiTagType = (nhanDoTuoi) => {
  const types = { 'P': 'success', 'T13': 'warning', 'T16': 'danger', 'T18': 'info' };
  return types[nhanDoTuoi] || 'info';
};

const formatDate = (d) => {
  if (!d) return '—';
  try {
    return new Date(d).toLocaleString('vi-VN', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  } catch { return '—'; }
};

// ── API ───────────────────────────────────────────────────────────────────────
const fetchMovies = async () => {
  loading.value = true;
  try {
    const res = await phimApi.getPhim({
      tenPhim:   searchQuery.value || null,
      trangThai: statusFilter.value === '' ? null : statusFilter.value,
      idTheLoai: genreFilter.value || null,
      page:      currentPage.value - 1,
      size:      pageSize.value,
    });
    moviesList.value    = res.data.data.content;
    totalElements.value = res.data.data.totalElements;
  } catch {
    notification.error('Lỗi tải dữ liệu phim');
  } finally {
    loading.value = false;
  }
};

const fetchGenres = async () => {
  try {
    const res = await phimApi.getTheLoai();
    genreOptions.value = res.data.data;
  } catch { console.error('Lỗi tải thể loại'); }
};

// ── CRUD ──────────────────────────────────────────────────────────────────────
const handleAdd  = () => router.push({ name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_ADD.name });
const handleEdit = (row) => router.push({ name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_EDIT.name, params: { id: row.id } });

const handleUpdateStatus = (row, status = null) => {
  const isInactive = row.trangThai === 0;
  const newStatus = status !== null ? status : (isInactive ? 1 : 0);
  const label = getTrangThaiLabel(newStatus).toLowerCase();
  
  if (newStatus === row.trangThai) return;

  confirmDialog.custom(
    `Thay đổi trạng thái phim "${row.tenPhim}" thành <b>${label}</b>?`,
    'Cập nhật trạng thái', 'Xác nhận'
  ).then(async () => {
    try {
      const now = new Date();
      const todayStr = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');
      
      const tomorrow = new Date(now);
      tomorrow.setDate(now.getDate() + 1);
      const tomorrowStr = tomorrow.getFullYear() + '-' + String(tomorrow.getMonth() + 1).padStart(2, '0') + '-' + String(tomorrow.getDate()).padStart(2, '0');

      const payload = { ...row };
      payload.trangThai = newStatus;

      if (newStatus === 1) { // Đang chiếu
        payload.ngayKhoiChieu = todayStr;
      } else if (newStatus === 2) { // Sắp chiếu
        if (!payload.ngayKhoiChieu || payload.ngayKhoiChieu <= todayStr) {
          payload.ngayKhoiChieu = tomorrowStr;
        }
      } else if (newStatus === 0) { // Ngừng chiếu
        payload.ngayKetThuc = todayStr;
      }

      await phimApi.update(row.id, payload);
      notification.success(`Đã chuyển trạng thái phim thành công`);
      fetchMovies();
    } catch { notification.error('Thay đổi trạng thái thất bại'); }
  }).catch(() => {});
};

const handleBulkStatusChange = () => {
  const count = selectedPhim.value.length;
  confirmDialog.custom(
    `Xác nhận <b>đổi trạng thái</b> cho <b>${count}</b> phim đã chọn?`,
    'Cập nhật hàng loạt', 'Đồng ý'
  ).then(async () => {
    try {
      const now = new Date();
      const todayStr = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');
      
      await Promise.all(selectedPhim.value.map(item => {
        const newStatus = item.trangThai === 0 ? 1 : 0;
        const payload = { ...item };
        payload.trangThai = newStatus;
        
        if (newStatus === 1) {
          payload.ngayKhoiChieu = todayStr;
        } else if (newStatus === 0) {
          payload.ngayKetThuc = todayStr;
        }
        
        return phimApi.update(item.id, payload);
      }));
      notification.success(`Đã cập nhật ${count} phim thành công`);
      selectedPhim.value = [];
      fetchMovies();
    } catch { notification.error('Có lỗi khi cập nhật hàng loạt'); }
  }).catch(() => {});
};

const handleReset = () => {
  searchQuery.value = ''; statusFilter.value = null;
  genreFilter.value = null; currentPage.value = 1;
  fetchMovies();
};

// ── Lifecycle ─────────────────────────────────────────────────────────────────
onMounted(() => { fetchGenres(); fetchMovies(); });
watch([currentPage, pageSize], fetchMovies);
</script>

<template>
  <div class="movie-management w-100 h-100 bg-transparent overflow-hidden">
    <AdminTableLayout
      title="Quản lý phim"
      titleIcon="bi bi-film"
      addButtonLabel="Thêm phim mới"
      :data="moviesList"
      :loading="loading"
      :total="totalElements"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="handleAdd"
      @reset-filter="handleReset"
    >
      <!-- Header actions -->
      <template #header-actions-left>

        <el-button v-if="selectedIdsCount" type="warning" :icon="Refresh"
          @click="handleBulkStatusChange">
          Đổi trạng thái {{ selectedIdsCount }} phim
        </el-button>
      </template>

      <!-- Filters -->
      <template #filters>
        <div class="filter-item">
          <el-input v-model="searchQuery" placeholder="Nhập tên phim..." :prefix-icon="Search"
            clearable @keyup.enter="fetchMovies" />
        </div>
        <div class="filter-item">
          <el-select v-model="statusFilter" placeholder="Chọn trạng thái" style="width: 200px;" clearable @change="fetchMovies">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đang chiếu" :value="1" />
            <el-option label="Sắp chiếu"  :value="2" />
            <el-option label="Ngừng chiếu" :value="0" />
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="genreFilter" placeholder="Chọn thể loại" style="width: 200px;" clearable @change="fetchMovies">
            <el-option label="Tất cả thể loại" value="" />
            <el-option v-for="g in genreOptions" :key="g.id" :label="g.tenTheLoai" :value="g.id" />
          </el-select>
        </div>
      </template>

      <!-- Content -->
      <template #content>
        <BaseTable
          v-loading="loading"
          :data="moviesList"
          :columns="tableColumns"
          :total="totalElements"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          v-model:selection="selectedPhim"
          :hide-pagination="true"
          @edit="handleEdit"
          @delete="handleUpdateStatus"
          @update-status="({ row, val }) => {
            if (row.trangThai === 2 && val) {
              handleUpdateStatus(row, 1);
            } else if (row.trangThai === 1 && !val) {
              handleUpdateStatus(row, 0);
            } else if (row.trangThai === 0) {
              notification.warning('Phim đã ngừng chiếu không thể thay đổi trạng thái');
            }
          }"
        >
          <template #cell-trangThai="{ row }">
            <el-dropdown trigger="click" @command="status => handleUpdateStatus(row, status)" :disabled="row.trangThai === 0">
              <span class="badge rounded-pill px-2 py-1" :class="[getTrangThaiClass(row.trangThai), row.trangThai === 1 ? 'badge-pulse' : '', row.trangThai !== 0 ? 'cursor-pointer' : '']">
                {{ getTrangThaiLabel(row.trangThai) }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="1" :disabled="row.trangThai === 1">Đang chiếu</el-dropdown-item>
                  <el-dropdown-item :command="2" :disabled="row.trangThai === 2">Sắp chiếu</el-dropdown-item>
                  <el-dropdown-item :command="0" :disabled="row.trangThai === 0">Ngừng chiếu</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template #actions="{ row }">
            <div class="d-flex gap-1 justify-content-center align-items-center">
              <el-tooltip content="Xem chi tiết" placement="top">
                <button class="btn-action-icon action-view" @click="handleView(row)">
                  <i class="bi bi-eye fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="handleEdit(row)">
                  <i class="bi bi-pencil fs-6"></i>
                </button>
              </el-tooltip>
              <el-switch
                :model-value="row.trangThai === 1"
                :disabled="row.trangThai === 0"
                @change="(val) => {
                  if (row.trangThai === 2 && val) {
                    handleUpdateStatus(row, 1);
                  } else if (row.trangThai === 1 && !val) {
                    handleUpdateStatus(row, 0);
                  }
                }"
                class="status-switch mx-1"
                active-color="#ff4949"
                inactive-color="#ff4949"
              />
            </div>
          </template>
          <template #cell-stt="{ index }">
            <span class="fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>



          <template #cell-poster="{ row }">
            <div class="py-1">
              <img v-if="row.poster" :src="row.poster" class="rounded-3 shadow-sm"
                style="width:42px;height:62px;object-fit:cover;"
                @error="e => e.target.style.display='none'" />
              <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center"
                style="width:42px;height:62px;">
                <i class="bi bi-film text-secondary" style="font-size:18px;"></i>
              </div>
            </div>
          </template>

          <template #cell-maPhim="{ row }">
            <span class="text-secondary small fw-bold">#{{ row.maPhim }}</span>
          </template>

          <template #cell-tenPhim="{ row }">
            <div class="fw-bold" style="color:#344767; white-space: nowrap;">{{ row.tenPhim }}</div>
          </template>

          <template #cell-nhanDoTuoi="{ row }">
            <el-tag :type="getDoTuoiTagType(row.nhanDoTuoi)" size="small" effect="plain" round>{{ row.nhanDoTuoi }}</el-tag>
          </template>

          <template #cell-giaPhim="{ row }">
            <span class="fw-bold text-primary small">{{ row.giaPhim?.toLocaleString('vi-VN') }}đ</span>
          </template>

          <template #cell-theLoais="{ row }">
            <div class="d-flex flex-nowrap gap-1 justify-content-center" style="overflow: hidden;">
              <el-tag v-for="g in (row.theLoais || [])" :key="g.id"
                size="small" effect="plain" class="genre-tag flex-shrink-0">{{ g.tenTheLoai }}</el-tag>
              <span v-if="!row.theLoais?.length" class="text-secondary">—</span>
            </div>
          </template>

          <template #cell-thoiLuong="{ row }">
            <div class="small text-secondary fw-semibold">{{ row.thoiLuong }} phút</div>
          </template>

          <template #cell-danhGia="{ row }">
            <div class="d-flex align-items-center justify-content-center gap-1">
              <i class="bi bi-star-fill text-warning small"></i>
              <span class="small fw-bold">{{ row.danhGia || '0.0' }}</span>
            </div>
          </template>


        </BaseTable>
      </template>
    </AdminTableLayout>
  </div>
</template>

<style scoped>
.genre-tag   { border: 1px solid #dcdfe6 !important; color: #909399 !important; background: #fff; border-radius: 6px; }
</style>
