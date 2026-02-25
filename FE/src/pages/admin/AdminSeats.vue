<template>
  <div class="admin-seats-page">
    <!-- ===== HEADER ===== -->
    <div class="page-header d-flex justify-content-between align-items-center mb-3 flex-shrink-0">
      <div>
        <h2 class="fw-bold text-dark mb-1" style="font-size: 18px;">
          <i class="bi bi-grid-3x3-gap-fill me-2 text-primary"></i>Quản Lý Ghế
        </h2>
      </div>
      <div class="d-flex gap-2">
        <el-button type="primary" :icon="Plus" @click="openDialog()">Thêm ghế</el-button>
      </div>
    </div>

    <!-- ===== NEW PREMIUM FILTER BAR ===== -->
    <div class="filter-container p-3 mb-3 bg-white rounded-3 shadow-sm border border-black flex-shrink-0">
      <div class="d-flex align-items-center gap-4 flex-wrap">
        <div class="filter-group d-flex align-items-center gap-4 flex-wrap w-100">
          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Monitor /></el-icon>Phòng chiếu
            </span>
            <el-select
              v-model="selectedRoom"
              placeholder="Tất cả phòng"
              style="width: 150px;"
              @change="fetchSeats"
              class="custom-select"
            >
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
              <el-icon class="me-1"><PriceTag /></el-icon>Loại ghế
            </span>
            <el-select 
              v-model="filterLoaiGhe" 
              placeholder="Tất cả" 
              style="width: 150px;" 
              @change="fetchSeats"
              class="custom-select"
            >
              <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
            </el-select>
          </div>

          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Operation /></el-icon>Trạng thái
            </span>
            <el-select 
              v-model="filterTrangThai" 
              placeholder="Tất cả" 
              style="width: 140px;" 
              class="custom-select"
            >
              <el-option label="Hoạt động" :value="1" />
              <el-option label="Bảo trì" :value="0" />
            </el-select>
          </div>


          <div class="filter-item">
            <span class="filter-label text-secondary small fw-bold mb-1 d-block">
              <el-icon class="me-1"><Search /></el-icon>Tìm kiếm
            </span>
            <el-input
              v-model="searchQuery"
              placeholder="Nhập số ghế..."
              style="width: 250px;"
              class="custom-search"
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

    <!-- ===== SEAT MAP (nếu đã chọn phòng) ===== -->
    <div v-if="selectedRoom && seats.length > 0" class="seat-map-wrapper mb-3 bg-white rounded-3 border border-black shadow-sm p-3 flex-shrink-0">
      <div class="text-center mb-2">
        <div class="screen-bar mx-auto rounded-pill"></div>
        <span class="small text-secondary fst-italic">Màn hình</span>
      </div>
      <div class="seat-grid-preview d-flex flex-wrap justify-content-center gap-1">
        <div
          v-for="seat in seats"
          :key="seat.id"
          class="seat-chip"
          :class="{
            'vip': seat.tenLoaiGhe?.toLowerCase().includes('vip') || seat.tenLoaiGhe?.toLowerCase().includes('couple'),
            'maintenance': seat.trangThai === 0
          }"
          :title="`${seat.soGhe} - ${seat.tenLoaiGhe}`"
        >
          {{ seat.soGhe }}
        </div>
      </div>
      <div class="d-flex justify-content-end gap-3 mt-2">
        <span class="seat-legend normal">Thường</span>
        <span class="seat-legend vip">VIP/Couple</span>
        <span class="seat-legend maintenance">Bảo trì</span>
      </div>
    </div>

    <!-- ===== STATS ===== -->
    <div class="row g-3 mb-3 flex-shrink-0">
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Tổng số ghế</div>
          <div class="fw-bold fs-5 text-dark">{{ filteredSeats.length }}</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Đang hoạt động</div>
          <div class="fw-bold fs-5 text-success">{{ filteredSeats.filter(s => s.trangThai === 1).length }}</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Đang bảo trì</div>
          <div class="fw-bold fs-5 text-warning">{{ filteredSeats.filter(s => s.trangThai === 0).length }}</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card border border-black shadow-sm rounded-3 p-2 bg-white">
          <div class="small text-secondary fw-semibold" style="font-size: 11px;">Loại ghế</div>
          <div class="fw-bold fs-5 text-primary">{{ loaiGheList.length }}</div>
        </div>
      </div>
    </div>

    <!-- ===== TABLE ===== -->
    <div class="table-wrapper bg-white rounded-3 border border-black shadow-sm flex-grow-1 overflow-hidden">
      <el-table
        :data="paginatedSeats"
        v-loading="loading"
        stripe
        style="width: 100%"
        height="100%"
        class="admin-table"
        :empty-text="selectedRoom ? 'Phòng chưa có ghế nào' : 'Hãy chọn phòng chiếu để xem ghế'"
      >
        <el-table-column type="index" label="STT" width="60" align="center" />
        <el-table-column label="Số ghế" prop="soGhe" width="100" align="center">
          <template #default="{ row }">
            <span class="badge bg-dark text-white fw-bold px-2 py-1 rounded-2">{{ row.soGhe }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Vị trí (Hàng - Cột)" min-width="140" align="center">
          <template #default="{ row }">
            <span class="text-secondary small">Hàng <b>{{ row.hang }}</b> – Cột <b>{{ row.cot }}</b></span>
          </template>
        </el-table-column>
        <el-table-column label="Phòng chiếu" prop="tenPhongChieu" min-width="160">
          <template #default="{ row }">
            <i class="bi bi-door-open me-1 text-primary"></i>{{ row.tenPhongChieu }}
          </template>
        </el-table-column>
        <el-table-column label="Loại ghế" min-width="140">
          <template #default="{ row }">
            <el-tag :type="getSeatTypeTag(row.tenLoaiGhe)" round size="small" effect="plain">
              {{ row.tenLoaiGhe }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Phụ phí" width="130" align="right">
          <template #default="{ row }">
            <span class="text-primary fw-bold small">
              {{ row.phuPhi > 0 ? formatCurrency(row.phuPhi) : '—' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="Trạng thái" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'warning'" round size="small">
              {{ row.trangThai === 1 ? 'Hoạt động' : 'Bảo trì' }}
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
        :total="filteredSeats.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        small
      />
    </div>

    <!-- ===== DIALOG THÊM/SỬA GHẾ ===== -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '✏️ Chỉnh sửa ghế' : '➕ Thêm ghế mới'"
      width="480px"
      destroy-on-close
      class="seat-dialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng">
                <el-option
                  v-for="pc in phongChieuList"
                  :key="pc.id"
                  :label="`${pc.tenRap} - ${pc.tenPhong}`"
                  :value="pc.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Loại ghế" prop="idLoaiGhe">
              <el-select v-model="form.idLoaiGhe" class="w-100" placeholder="Chọn loại">
                <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-4">
            <el-form-item label="Số ghế" prop="soGhe">
              <el-input v-model="form.soGhe" placeholder="VD: A1" />
            </el-form-item>
          </div>
          <div class="col-4">
            <el-form-item label="Hàng" prop="hang">
              <el-input v-model="form.hang" placeholder="VD: A" maxlength="5" />
            </el-form-item>
          </div>
          <div class="col-4">
            <el-form-item label="Cột" prop="cot">
              <el-input-number v-model="form.cot" :min="1" :max="50" class="w-100" controls-position="right" />
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Trạng thái" prop="trangThai">
              <el-radio-group v-model="form.trangThai">
                <el-radio-button :value="1">Hoạt động</el-radio-button>
                <el-radio-button :value="0">Bảo trì</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Hủy</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="saving">
          {{ editingId ? 'Cập nhật' : 'Thêm ghế' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Plus, Search, Monitor, PriceTag, Operation, RefreshLeft, Delete, Close } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from '@/services/axios';
import {
  API_ADMIN_GHE,
  API_ADMIN_LOAI_GHE,
  API_ADMIN_PHONG_CHIEU_DROP
} from '@/constants/apiEndpoints';

// =================== STATE ===================
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const editingId = ref(null);
const formRef = ref(null);

const seats = ref([]);
const loaiGheList = ref([]);
const phongChieuList = ref([]);

const selectedRoom = ref(null);
const filterLoaiGhe = ref(null);
const filterTrangThai = ref(null);
const searchQuery = ref('');

// Pagination state
const currentPage = ref(1);
const pageSize = ref(5);

const form = ref({
  idPhongChieu: '',
  idLoaiGhe: '',
  soGhe: '',
  hang: '',
  cot: 1,
  trangThai: 1
});

const rules = {
  idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
  idLoaiGhe: [{ required: true, message: 'Vui lòng chọn loại ghế', trigger: 'change' }],
  soGhe: [{ required: true, message: 'Nhập số ghế', trigger: 'blur' }],
  hang: [{ required: true, message: 'Nhập hàng', trigger: 'blur' }],
  cot: [{ required: true, message: 'Nhập cột', trigger: 'blur' }],
};

// =================== COMPUTED ===================
const filteredSeats = computed(() => {
  return seats.value.filter(s => {
    const matchLoai = !filterLoaiGhe.value || s.idLoaiGhe === filterLoaiGhe.value;
    const matchTrang = filterTrangThai.value === null || filterTrangThai.value === undefined
      ? true
      : s.trangThai === filterTrangThai.value;
    const matchSearch = !searchQuery.value
      || s.soGhe?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchLoai && matchTrang && matchSearch;
  });
});

const paginatedSeats = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredSeats.value.slice(start, end);
});

// =================== METHODS ===================
const fetchDropdowns = async () => {
  try {
    const [loaiRes, phongRes] = await Promise.all([
      axios.get(API_ADMIN_LOAI_GHE),
      axios.get(API_ADMIN_PHONG_CHIEU_DROP)
    ]);
    loaiGheList.value = loaiRes.data?.data || [];
    phongChieuList.value = phongRes.data?.data || [];
  } catch (e) {
    ElMessage.error('Không thể tải dữ liệu dropdown');
  }
};

const fetchSeats = async () => {
  loading.value = true;
  try {
    const params = {};
    if (selectedRoom.value) params.idPhongChieu = selectedRoom.value;
    const res = await axios.get(API_ADMIN_GHE, { params });
    seats.value = res.data?.data || [];
  } catch (e) {
    ElMessage.error('Không thể tải danh sách ghế');
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  selectedRoom.value = null;
  filterLoaiGhe.value = null;
  filterTrangThai.value = null;
  searchQuery.value = '';
  currentPage.value = 1; // Reset to page 1
  fetchSeats();
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
      idPhongChieu: row.idPhongChieu,
      idLoaiGhe: row.idLoaiGhe,
      soGhe: row.soGhe,
      hang: row.hang,
      cot: row.cot,
      trangThai: row.trangThai
    };
  } else {
    form.value = {
      idPhongChieu: selectedRoom.value || '',
      idLoaiGhe: '',
      soGhe: '',
      hang: '',
      cot: 1,
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
        await axios.put(`${API_ADMIN_GHE}/${editingId.value}`, form.value);
        ElMessage.success('Cập nhật ghế thành công');
      } else {
        await axios.post(API_ADMIN_GHE, form.value);
        ElMessage.success('Thêm ghế thành công');
      }
      dialogVisible.value = false;
      await fetchSeats();
    } catch (e) {
      ElMessage.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Bạn có chắc muốn xóa ghế "<b>${row.soGhe}</b>"?`,
    'Xác nhận xóa',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await axios.delete(`${API_ADMIN_GHE}/${row.id}`);
      ElMessage.success('Đã xóa ghế');
      await fetchSeats();
    } catch (e) {
      ElMessage.error(e.response?.data?.message || 'Xóa ghế thất bại');
    }
  }).catch(() => {});
};

// =================== HELPERS ===================
const getSeatTypeTag = (tenLoai) => {
  if (!tenLoai) return 'info';
  const l = tenLoai.toLowerCase();
  if (l.includes('vip')) return 'warning';
  if (l.includes('couple') || l.includes('sweetbox')) return 'danger';
  return 'info';
};

const formatCurrency = (val) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

// =================== LIFECYCLE ===================
onMounted(async () => {
  await fetchDropdowns();
  await fetchSeats();
});
</script>

<style scoped>
.admin-seats-page {
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

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background-color: #f8f9fa !important;
  font-weight: 600;
  color: #374151;
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

:deep(.custom-select .el-input__wrapper),
:deep(.custom-search .el-input__wrapper) {
  box-shadow: none !important;
  border: 1px solid #e5e7eb !important;
  border-radius: 8px !important;
  transition: all 0.2s;
  background-color: #f9fafb;
}

:deep(.custom-select .el-input__wrapper:hover),
:deep(.custom-search .el-input__wrapper:hover) {
  border-color: var(--el-color-primary) !important;
  background-color: #fff;
}

:deep(.custom-select .el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1) !important;
  background-color: #fff;
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

/* Seat Map */
.screen-bar {
  height: 6px;
  width: 60%;
  background: linear-gradient(90deg, transparent, #6c757d, transparent);
  margin-bottom: 4px;
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

.seat-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 28px;
  background: #e8f4fd;
  border: 1.5px solid #93c5fd;
  border-radius: 6px;
  font-size: 9px;
  font-weight: 700;
  color: #1d4ed8;
  cursor: default;
  transition: transform 0.15s;
}

.seat-chip:hover {
  transform: scale(1.15);
}

.seat-chip.vip {
  background: #fef3c7;
  border-color: #f59e0b;
  color: #92400e;
}

.seat-chip.maintenance {
  background: #f3f4f6;
  border-color: #9ca3af;
  color: #6b7280;
  opacity: 0.6;
}

.seat-legend {
  font-size: 11px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.seat-legend::before {
  content: '';
  width: 14px;
  height: 14px;
  border-radius: 4px;
  display: inline-block;
}

.seat-legend.normal::before { background: #e8f4fd; border: 1.5px solid #93c5fd; }
.seat-legend.vip::before { background: #fef3c7; border: 1.5px solid #f59e0b; }
.seat-legend.maintenance::before { background: #f3f4f6; border: 1.5px solid #9ca3af; }

.stat-card {
  transition: box-shadow 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
}
</style>
