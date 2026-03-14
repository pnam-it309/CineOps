<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { phimApi } from '@/services/api/admin/phimService';
import BaseTable from '@/components/common/BaseTable.vue';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';

// ── State ─────────────────────────────────────────────────────────────────────
const router = useRouter();
const moviesList = ref([]);
const loading = ref(false);
const totalElements = ref(0);
const currentPage = ref(1);
const pageSize = ref(5);

const searchQuery = ref("");
const statusFilter = ref(null);
const genreFilter = ref(null);
const genreOptions = ref([]);

const selectedPhim = ref([]);

// Quick add genre
const showAddGenreDialog = ref(false);
const newGenreName = ref('');
const addingGenre = ref(false);

const handleView = (row) => {
  router.push({
    name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_DETAIL.name,
    params: { id: row.id }
  });
};

const handleQuickAddGenre = async () => {
  if (!newGenreName.value.trim()) {
    notification.warning('Vui lòng nhập tên thể loại!');
    return;
  }
  addingGenre.value = true;
  try {
    await phimApi.createTheLoai(newGenreName.value.trim());
    notification.success('Thêm thể loại thành công!');
    await fetchGenres();
    showAddGenreDialog.value = false;
    newGenreName.value = '';
  } catch (e) {
    notification.error('Lỗi khi thêm thể loại');
  } finally {
    addingGenre.value = false;
  }
};

const tableColumns = [
  { label: 'STT', key: 'stt', width: '60px', align: 'center' },
  { label: 'Mã phim', key: 'maPhim', width: '100px', align: 'center' },
  { label: 'Poster', key: 'poster', width: '80px', align: 'center' },
  { label: 'Tên phim', key: 'tenPhim', width: '180px', align: 'center' },
  { label: 'Độ tuổi', key: 'doTuoi', width: '90px', align: 'center' },
  { label: 'Thời lượng', key: 'thoiLuong', width: '110px', align: 'center' },
  { label: 'Thể loại', key: 'theLoais', width: '160px', align: 'center' },
  { label: 'Phát hành', key: 'ngayKhoiChieu', width: '130px', align: 'center' },
  { label: 'Giá gốc', key: 'giaPhim', width: '110px', align: 'center' },
  { label: 'Trạng thái', key: 'trangThai', width: '140px', align: 'center' },
];

const selectedIdsCount = computed(() => selectedPhim.value.length);

// ── Helpers ───────────────────────────────────────────────────────────────────
const getTrangThaiType = (tt) => ({ 1: 'success', 2: 'danger', 0: 'info' }[tt] ?? 'info');
const getTrangThaiLabel = (tt) => ({ 1: 'Đang chiếu', 2: 'Sắp chiếu', 0: 'Ngừng chiếu' }[tt] ?? '—');

const getDoTuoiTagType = (nhanDoTuoi) => {
  const types = { 'P': 'success', 'T13': 'warning', 'T16': 'danger', 'T18': 'info' };
  return types[nhanDoTuoi] || 'info';
};

const formatDate = (d) => {
  if (!d) return "—";
  try {
    const date = new Date(d);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  } catch {
    return "—";
  }
};

// ── API ───────────────────────────────────────────────────────────────────────
const fetchMovies = async () => {
  loading.value = true;
  try {
    const res = await phimApi.getPhim({
      tenPhim: searchQuery.value || null,
      trangThai: statusFilter.value === "" ? null : statusFilter.value,
      idTheLoai: genreFilter.value || null,
      page: currentPage.value - 1,
      size: pageSize.value,
    });
    moviesList.value = res.data.data.content;
    totalElements.value = res.data.data.totalElements;
  } catch {
    notification.error("Lỗi tải dữ liệu phim");
  } finally {
    loading.value = false;
  }
};

const fetchGenres = async () => {
  try {
    const res = await phimApi.getTheLoai();
    genreOptions.value = res.data.data;
  } catch {
    console.error("Lỗi tải thể loại");
  }
};

// ── CRUD ──────────────────────────────────────────────────────────────────────
const handleAdd = () => router.push({ name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_ADD.name });
const handleEdit = (row) => router.push({ name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_EDIT.name, params: { id: row.id } });

const handleUpdateStatus = (row, status = null) => {
  const isInactive = row.trangThai === 0;
  const newStatus = status !== null ? status : isInactive ? 1 : 0;
  const label = getTrangThaiLabel(newStatus).toLowerCase();

  if (newStatus === row.trangThai) return;

  confirmDialog
    .custom(
      `Thay đổi trạng thái phim "${row.tenPhim}" thành <b>${label}</b>?`,
      "Cập nhật trạng thái",
      "Xác nhận",
    )
    .then(async () => {
      try {
        const now = new Date();
        const todayStr =
          now.getFullYear() +
          "-" +
          String(now.getMonth() + 1).padStart(2, "0") +
          "-" +
          String(now.getDate()).padStart(2, "0");

        const tomorrow = new Date(now);
        tomorrow.setDate(now.getDate() + 1);
        const tomorrowStr =
          tomorrow.getFullYear() +
          "-" +
          String(tomorrow.getMonth() + 1).padStart(2, "0") +
          "-" +
          String(tomorrow.getDate()).padStart(2, "0");

        const payload = { ...row };
        payload.trangThai = newStatus;

        if (newStatus === 1) {
          payload.ngayKhoiChieu = todayStr;
        } else if (newStatus === 2) {
          if (!payload.ngayKhoiChieu || payload.ngayKhoiChieu <= todayStr) {
            payload.ngayKhoiChieu = tomorrowStr;
          }
        } else if (newStatus === 0) {
          payload.ngayKetThuc = todayStr;
        }

        await phimApi.update(row.id, payload);
        notification.success(`Đã chuyển trạng thái phim thành công`);
        fetchMovies();
      } catch {
        notification.error("Thay đổi trạng thái thất bại");
      }
    })
    .catch(() => { });
};

const handleBulkStatusChange = () => {
  const count = selectedPhim.value.length;
  confirmDialog
    .custom(
      `Xác nhận <b>đổi trạng thái</b> cho <b>${count}</b> phim đã chọn?`,
      "Cập nhật hàng loạt",
      "Đồng ý",
    )
    .then(async () => {
      try {
        const now = new Date();
        const todayStr =
          now.getFullYear() +
          "-" +
          String(now.getMonth() + 1).padStart(2, "0") +
          "-" +
          String(now.getDate()).padStart(2, "0");

        await Promise.all(
          selectedPhim.value.map((item) => {
            const newStatus = item.trangThai === 0 ? 1 : 0;
            const payload = { ...item };
            payload.trangThai = newStatus;

            if (newStatus === 1) {
              payload.ngayKhoiChieu = todayStr;
            } else if (newStatus === 0) {
              payload.ngayKetThuc = todayStr;
            }

            return phimApi.update(item.id, payload);
          }),
        );
        notification.success(`Đã cập nhật ${count} phim thành công`);
        selectedPhim.value = [];
        fetchMovies();
      } catch {
        notification.error("Có lỗi khi cập nhật hàng loạt");
      }
    })
    .catch(() => { });
};

const handleReset = () => {
  searchQuery.value = "";
  statusFilter.value = null;
  genreFilter.value = null;
  currentPage.value = 1;
  fetchMovies();
};

// ── Lifecycle ─────────────────────────────────────────────────────────────────
onMounted(() => {
  fetchGenres();
  fetchMovies();
});
watch([currentPage, pageSize], fetchMovies);
</script>

<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable
      title="Quản lý phim"
      titleIcon="bi bi-film"
      addButtonLabel="Thêm phim mới"
      :data="moviesList"
      :columns="tableColumns"
      :loading="loading"
      :total="totalElements"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      v-model:selection="selectedPhim"
      @add-click="handleAdd"
      @reset-filter="handleReset"
      @edit="handleEdit"
      @delete="handleUpdateStatus"
    >
      <template #header-actions-left>
        <el-button v-if="selectedIdsCount" type="warning" :icon="Refresh" @click="handleBulkStatusChange" plain>
          Đổi trạng thái {{ selectedIdsCount }} phim
        </el-button>
      </template>

      <!-- Filters using Bootstrap spacing -->
      <template #filters>
        <div class="me-2 mb-2 mb-md-0" style="min-width: 220px;">
          <el-input v-model="searchQuery" placeholder="Nhập tên phim..." :prefix-icon="Search" clearable @keyup.enter="fetchMovies" />
        </div>
        <div class="me-2 mb-2 mb-md-0">
          <el-select v-model="statusFilter" placeholder="Trạng thái" style="width: 170px" clearable @change="fetchMovies">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đang chiếu" :value="1" />
            <el-option label="Sắp chiếu" :value="2" />
            <el-option label="Ngừng chiếu" :value="0" />
          </el-select>
        </div>
        <div class="mb-2 mb-md-0">
          <el-select v-model="genreFilter" placeholder="Thể loại" style="width: 170px;" clearable @change="fetchMovies" filterable>
            <template #suffix>
              <el-icon @click.stop="showAddGenreDialog = true" class="cursor-pointer">
                <Plus />
              </el-icon>
            </template>
            <el-option label="Tất cả thể loại" value="" />
            <el-option v-for="g in genreOptions" :key="g.id" :label="g.tenTheLoai" :value="g.id" />
          </el-select>
        </div>
      </template>

      <!-- Status Cell -->
      <template #cell-trangThai="{ row }">
        <el-dropdown trigger="click" @command="(status) => handleUpdateStatus(row, status)" :disabled="row.trangThai === 0">
          <el-tag :type="getTrangThaiType(row.trangThai)" round effect="light" class="fw-bold" 
            :class="{'badge-pulse': row.trangThai === 1, 'cursor-pointer': row.trangThai !== 0}">
            {{ getTrangThaiLabel(row.trangThai) }}
          </el-tag>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :command="1" :disabled="row.trangThai === 1">Đang chiếu</el-dropdown-item>
              <el-dropdown-item :command="2" :disabled="row.trangThai === 2">Sắp chiếu</el-dropdown-item>
              <el-dropdown-item :command="0" :disabled="row.trangThai === 0">Ngừng chiếu</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>

      <!-- Actions -->
      <template #actions="{ row }">
        <div class="d-flex gap-1 justify-content-center align-items-center">
          <el-tooltip content="Xem chi tiết" placement="top">
            <button class="btn-action-icon action-view" @click="handleView(row)">
              <i class="bi bi-eye"></i>
            </button>
          </el-tooltip>
          <el-tooltip content="Chỉnh sửa" placement="top">
            <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="handleEdit(row)">
              <i class="bi bi-pencil"></i>
            </button>
          </el-tooltip>
          <el-switch :model-value="row.trangThai === 1 || row.trangThai === 2" :disabled="row.trangThai === 0" 
            @change="(val) => handleUpdateStatus(row, val ? 1 : 0)" class="status-switch mx-1" />
        </div>
      </template>

      <!-- STT -->
      <template #cell-stt="{ index }">
        <span class="text-secondary smaller">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
      </template>

      <!-- Poster -->
      <template #cell-poster="{ row }">
        <div class="p-1 border bg-white rounded shadow-sm d-inline-block">
          <img v-if="row.poster" :src="row.poster" class="rounded flex-shrink-0" style="width:36px;height:50px;object-fit:cover;" />
          <div v-else class="rounded d-flex align-items-center justify-content-center bg-light" style="width:36px;height:50px;">
            <i class="bi bi-image text-secondary smaller"></i>
          </div>
        </div>
      </template>

      <template #cell-maPhim="{ row }">
        <span class="text-secondary smaller fw-bold">#{{ row.maPhim }}</span>
      </template>

      <template #cell-tenPhim="{ row }">
        <div class="d-flex justify-content-center">
          <div class="fw-bold text-dark text-center">{{ row.tenPhim }}</div>
        </div>
      </template>

      <template #cell-doTuoi="{ row }">
        <el-tag :type="getDoTuoiTagType(row.nhanDoTuoi)" size="small" effect="plain" class="fw-bold">{{ row.nhanDoTuoi }} ({{ row.doTuoi }}+)</el-tag>
      </template>

      <template #cell-giaPhim="{ row }">
        <span class="fw-bold text-primary smaller">{{ row.giaPhim?.toLocaleString("vi-VN") }}đ</span>
      </template>

      <template #cell-theLoais="{ row }">
        <span class="text-secondary smaller">
          {{row.theLoais?.map((g) => g.tenTheLoai).join(", ") || "—"}}
        </span>
      </template>

      <template #cell-thoiLuong="{ row }">
        <span class="fw-bold text-dark">{{ row.thoiLuong }} phút</span>
      </template>

      <template #cell-ngayKhoiChieu="{ row }">
        <span class="text-secondary smaller fw-bold">{{ formatDate(row.ngayKhoiChieu) }}</span>
      </template>
    </BaseTable>

    <!-- Genre Add Dialog -->
    <el-dialog v-model="showAddGenreDialog" title="Thêm thể loại mới" width="400px" append-to-body class="rounded-modal">
      <div class="px-2 pb-2">
        <el-input v-model="newGenreName" placeholder="Ví dụ: Hành động, Kinh dị..." @keyup.enter="handleQuickAddGenre" />
      </div>
      <template #footer>
        <div class="px-2 pb-2">
          <el-button @click="showAddGenreDialog = false">Hủy</el-button>
          <el-button type="primary" @click="handleQuickAddGenre" :loading="addingGenre">Thêm ngay</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.status-switch :deep(.el-switch__label) {
  display: none !important;
}
</style>
