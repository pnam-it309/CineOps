<template>
  <div class="admin-showtimes-page">
    <!-- ===== HEADER ===== -->
    <div class="page-header d-flex justify-content-between align-items-center mb-3 flex-shrink-0">
      <div>
        <h2 class="fw-bold text-dark mb-1" style="font-size: 18px;">
          <i class="bi bi-calendar2-week-fill me-2 text-primary"></i>Quản Lý Suất Chiếu
        </h2>
      </div>
      <div class="d-flex gap-2">
        <el-button type="primary" :icon="Plus" @click="openDialog()">Thêm suất chiếu</el-button>
      </div>
    </div>

    <!-- ===== NEW PREMIUM FILTER BAR ===== -->
    <div class="filter-container p-3 mb-3 bg-white rounded-3 shadow-sm border border-black flex-shrink-0">
      <div class="d-flex align-items-center gap-4 flex-wrap w-100">
        <div class="filter-group d-flex align-items-center gap-4 flex-wrap w-100">
          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Calendar /></el-icon>Ngày chiếu
            </span>
            <el-date-picker
              v-model="filterDate"
              type="date"
              placeholder="Chọn ngày"
              value-format="YYYY-MM-DD"
              @change="fetchShowtimes"
              style="width: 160px;"
              class="custom-input"
            />
          </div>



          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Monitor /></el-icon>Phòng chiếu
            </span>
            <el-select v-model="filterRoom" placeholder="Tất cả" style="width: 130px;" @change="fetchShowtimes" class="custom-input">
              <el-option
                v-for="pc in phongChieuList"
                :key="pc.id"
                :label="`${pc.tenRap} - ${pc.tenPhong}`"
                :value="pc.id"
              />
            </el-select>
          </div>

          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Operation /></el-icon>Trạng thái
            </span>
            <el-select v-model="filterTrangThai" placeholder="Tất cả" style="width: 140px;" class="custom-input">
              <el-option label="Sắp chiếu" :value="1" />
              <el-option label="Đang chiếu" :value="2" />
              <el-option label="Đã kết thúc" :value="3" />
              <el-option label="Đã hủy" :value="0" />
            </el-select>
          </div>


          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Search /></el-icon>Tìm kiếm
            </span>
            <el-input
              v-model="searchQuery"
              placeholder="Nhập tên phim..."
              style="width: 250px;"
              class="custom-input"
            />
          </div>

          <div class="filter-item">
            <el-tooltip content="Xóa bộ lọc" placement="top">
              <el-button @click="resetFilter" :icon="Close" circle class="btn-reset" />
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-3 flex-shrink-0">
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Tổng suất chiếu</div>
          <div class="fw-bold fs-5 text-dark">{{ filteredShowtimes.length }}</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Sắp chiếu</div>
          <div class="fw-bold fs-5 text-primary">{{ filteredShowtimes.filter(s => s.trangThai === 1).length }}</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Đang chiếu</div>
          <div class="fw-bold fs-5 text-success">{{ filteredShowtimes.filter(s => s.trangThai === 2).length }}</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Đã hủy</div>
          <div class="fw-bold fs-5 text-danger">{{ filteredShowtimes.filter(s => s.trangThai === 0).length }}</div>
        </div>
      </div>
    </div>

    <!-- ===== TABLE ===== -->
    <div class="table-wrapper bg-white rounded-3 border border-black shadow-sm flex-grow-1 overflow-hidden">
      <el-table
        :data="paginatedShowtimes"
        v-loading="loading"
        stripe
        style="width: 100%;"
        height="100%"
        class="admin-table"
        empty-text="Không có suất chiếu nào"
      >
        <el-table-column type="index" label="STT" width="60" align="center" />

        <el-table-column label="Phim" min-width="200">
          <template #default="{ row }">
            <div class="d-flex align-items-center gap-2">
              <img
                v-if="row.poster"
                :src="row.poster"
                class="rounded"
                style="width: 32px; height: 44px; object-fit: cover;"
                :alt="row.tenPhim"
              />
              <div v-else class="rounded d-flex align-items-center justify-content-center bg-light border"
                   style="width: 32px; height: 44px;">
                <i class="bi bi-film text-secondary"></i>
              </div>
              <div>
                <div class="fw-semibold small text-dark" style="line-height: 1.2;">{{ row.tenPhim || '—' }}</div>
                <div class="text-secondary" style="font-size: 11px;">{{ row.thoiLuong ? row.thoiLuong + ' phút' : '' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Phòng chiếu" min-width="160">
          <template #default="{ row }">
            <div class="small">
              <div class="fw-semibold"><i class="bi bi-door-open me-1 text-primary"></i>{{ row.tenPhongChieu }}</div>
              <div class="text-secondary">{{ row.loaiManHinh }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Ngày chiếu" width="120" align="center">
          <template #default="{ row }">
            <span class="small fw-semibold">{{ formatDate(row.ngayChieu) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="Giờ chiếu" width="190" align="center">
          <template #default="{ row }">
            <div class="small">
              <el-tag type="info" effect="plain" size="small" class="fw-bold me-1">
                {{ row.gioBatDau }}
              </el-tag>
              <span class="text-secondary mx-1">→</span>
              <el-tag type="info" effect="plain" size="small">{{ row.gioKetThuc }}</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Trạng thái" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.trangThai)" round size="small">
              {{ getStatusLabel(row.trangThai) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Thao tác" width="110" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex gap-1 justify-content-center">
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn btn-action-icon text-primary" @click="openDialog(row)">
                  <i class="bi bi-pencil"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Xóa" placement="top">
                <button class="btn btn-action-icon text-danger" @click="handleDelete(row)">
                  <i class="bi bi-trash"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ===== PAGINATION ===== -->
    <div class="pagination-container d-flex justify-content-end p-2 bg-white border border-black border-top-0 rounded-bottom-3 shadow-sm">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredShowtimes.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        small
      />
    </div>

    <!-- ===== DIALOG THÊM/SỬA SUẤT CHIẾU ===== -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '✏️ Chỉnh sửa suất chiếu' : '🎬 Thêm suất chiếu mới'"
      width="520px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="Phim" prop="idPhim">
          <el-select v-model="form.idPhim" class="w-100" placeholder="Chọn phim" filterable>
            <el-option
              v-for="p in phimList"
              :key="p.id"
              :label="`${p.tenPhim} (${p.thoiLuong || '?'} phút)`"
              :value="p.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Phòng chiếu" prop="idPhongChieu">
          <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng chiếu" filterable>
            <el-option
              v-for="pc in phongChieuList"
              :key="pc.id"
              :label="`${pc.tenRap} — ${pc.tenPhong} (${pc.loaiManHinh || 'Standard'})`"
              :value="pc.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Ngày chiếu" prop="ngayChieu">
          <el-date-picker
            v-model="form.ngayChieu"
            type="date"
            class="w-100"
            placeholder="Chọn ngày chiếu"
            value-format="YYYY-MM-DD"
            :disabled-date="(d) => d < new Date(new Date().setHours(0,0,0,0))"
          />
        </el-form-item>

        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Giờ bắt đầu" prop="gioBatDau">
              <el-time-picker
                v-model="form.gioBatDau"
                class="w-100"
                placeholder="Chọn giờ bắt đầu"
                format="HH:mm"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ kết thúc" prop="gioKetThuc">
              <el-time-picker
                v-model="form.gioKetThuc"
                class="w-100"
                placeholder="Chọn giờ kết thúc"
                format="HH:mm"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </div>
        </div>

        <el-form-item label="Trạng thái" prop="trangThai">
          <el-select v-model="form.trangThai" class="w-100">
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">Hủy</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="saving">
          {{ editingId ? 'Cập nhật' : 'Thêm suất chiếu' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Plus, Search, Calendar, Monitor, Operation, RefreshLeft, Delete, Close } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from '@/services/axios';
import {
  API_ADMIN_SUAT_CHIEU,
  API_ADMIN_PHONG_CHIEU_DROP,
  API_PHIM_DROP
} from '@/constants/apiEndpoints';

// =================== STATE ===================
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const editingId = ref(null);
const formRef = ref(null);

const showtimes = ref([]);
const phongChieuList = ref([]);
const phimList = ref([]);

const filterDate = ref(null);
const filterRoom = ref(null);
const filterTrangThai = ref(null);
const searchQuery = ref('');

// Pagination state
const currentPage = ref(1);
const pageSize = ref(5);

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

// =================== COMPUTED ===================
const filteredShowtimes = computed(() => {
  return showtimes.value.filter(s => {
    const matchRoom = !filterRoom.value || s.idPhongChieu === filterRoom.value;
    const matchStatus = filterTrangThai.value === null || filterTrangThai.value === undefined
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
      axios.get(API_ADMIN_PHONG_CHIEU_DROP),
      axios.get(API_PHIM_DROP)
    ]);
    phongChieuList.value = phongRes.data?.data || [];
    phimList.value = phimRes.data?.data || [];
  } catch (e) {
    ElMessage.error('Không thể tải dữ liệu dropdown');
  }
};

const fetchShowtimes = async () => {
  loading.value = true;
  try {
    const params = {};
    if (filterDate.value) params.ngayChieu = filterDate.value;
    if (filterRoom.value) params.idPhongChieu = filterRoom.value;
    const res = await axios.get(API_ADMIN_SUAT_CHIEU, { params });
    showtimes.value = res.data?.data || [];
  } catch (e) {
    ElMessage.error('Không thể tải danh sách suất chiếu');
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  filterDate.value = null;
  filterRoom.value = null;
  filterTrangThai.value = null;
  searchQuery.value = '';
  currentPage.value = 1; // Reset to page 1
  fetchShowtimes();
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
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
    saving.value = true;
    try {
      if (editingId.value) {
        await axios.put(`${API_ADMIN_SUAT_CHIEU}/${editingId.value}`, form.value);
        ElMessage.success('Cập nhật suất chiếu thành công');
      } else {
        await axios.post(API_ADMIN_SUAT_CHIEU, form.value);
        ElMessage.success('Thêm suất chiếu thành công');
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      ElMessage.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Bạn có chắc muốn xóa suất chiếu phim "<b>${row.tenPhim}</b>" ngày ${formatDate(row.ngayChieu)}?`,
    'Xác nhận xóa',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await axios.delete(`${API_ADMIN_SUAT_CHIEU}/${row.id}`);
      ElMessage.success('Đã xóa suất chiếu');
      await fetchShowtimes();
    } catch (e) {
      ElMessage.error(e.response?.data?.message || 'Xóa thất bại');
    }
  }).catch(() => {});
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
.admin-showtimes-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.table-wrapper {
  flex: 1;
  min-height: 0;
  border-bottom-left-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}

/* Custom Scrollbar */
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
}
:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background-color: #d1d5db;
  border-radius: 10px;
}
:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background-color: #f3f4f6;
}

.pagination-container {
  margin-top: -1px; /* Align with table border */
}

:deep(.el-table th) {
  background-color: #f8f9fa !important;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
}

/* Premium Filter Styles */
.filter-container {
  transition: all 0.3s ease;
}

.filter-separator {
  width: 1px;
  height: 32px;
  background-color: #e5e7eb;
  margin: 0 4px;
  align-self: flex-end;
}

.filter-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
}

:deep(.custom-input .el-input__wrapper) {
  box-shadow: none !important;
  border: 1px solid #e5e7eb !important;
  border-radius: 8px !important;
  transition: all 0.2s;
  background-color: #f9fafb;
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: var(--el-color-primary) !important;
  background-color: #fff;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1) !important;
  background-color: #fff;
}

:deep(.el-table td) {
  font-size: 13px;
}

.btn-action-icon {
  width: 30px;
  height: 30px;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 13px;
  color: #6b7280;
}

.btn-action-icon:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.btn-action-icon.text-primary:hover {
  background-color: #eff6ff !important;
  border-color: #bfdbfe !important;
}

.btn-action-icon.text-danger:hover {
  background-color: #fef2f2 !important;
  border-color: #fecaca !important;
}

.btn-action-icon i {
  transition: transform 0.2s;
}

.btn-action-icon:active {
  transform: translateY(0) scale(0.95);
}

.btn-reset {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-color: #e5e7eb !important;
  color: #6b7280 !important;
}

.btn-reset:hover {
  background-color: #fef2f2 !important;
  border-color: #fecaca !important;
  color: #ef4444 !important;
  transform: rotate(90deg);
}

.btn-reset:active {
  transform: rotate(90deg) scale(0.9);
}

.stat-card {
  transition: box-shadow 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
}
</style>
