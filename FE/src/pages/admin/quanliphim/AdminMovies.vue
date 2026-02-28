<script setup>
import {ref, onMounted, watch, computed} from 'vue';
import {Search, Plus, Refresh, Delete} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {phimApi} from '@/services/api/admin/phimService';
import BaseTable from '@/components/common/BaseTable.vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

const currentData = computed(() => activeTab.value === 'phim' ? moviesList.value : lcMoviesList.value);
const currentTotal = computed(() => activeTab.value === 'phim' ? totalElements.value : lcTotalElements.value);
const currentLoading = computed(() => activeTab.value === 'phim' ? loading.value : lcLoading.value);
const currentSearch = computed({
  get: () => activeTab.value === 'phim' ? searchQuery.value : lcSearchQuery.value,
  set: (val) => {
    if (activeTab.value === 'phim') searchQuery.value = val;
    else lcSearchQuery.value = val;
  }
});
const layoutCurrentPage = computed({
  get: () => activeTab.value === 'phim' ? currentPage.value : lcCurrentPage.value,
  set: (val) => {
    if (activeTab.value === 'phim') currentPage.value = val;
    else lcCurrentPage.value = val;
  }
});


// ==================== STATE ====================
const activeTab = ref('phim');
const moviesList = ref([]);
const loading = ref(false);
const totalElements = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const searchQuery = ref('');
const statusFilter = ref(null);
const genreFilter = ref(null);
const genreOptions = ref([]);

const lcSearchQuery = ref('');
const lcThuFilter = ref(null);
const lcCurrentPage = ref(1);
const lcTotalElements = ref(0);
const lcMoviesList = ref([]);
const lcLoading = ref(false);

// ==================== DIALOG STATE ====================
const dialogVisible = ref(false);
const detailVisible = ref(false);
const scheduleVisible = ref(false);
const editLCVisible = ref(false);
const editingMovie = ref(null);
const selectedMovie = ref(null);

const defaultForm = () => ({
  tenPhim: '', thoiLuong: 120,
  ngayKhoiChieu: '', ngayKetThuc: '',
  lichChieu: [],
  idTheLoais: [],
  giaVeGoc: null, trangThai: 1,
  poster: '', trailer: '', moTa: '',
  daoDien: '', dienVien: '', ngonNgu: '', doTuoi: 0, danhGia: 0
});
const movieForm = ref(defaultForm());
const selectedPhim = ref([]);
const selectedLC = ref([]);

const handleSelectionChangePhim = (val) => {
  selectedPhim.value = val;
};

const handleSelectionChangeLC = (val) => {
  selectedLC.value = val;
};

const selectedIdsCount = computed(() => {
    return activeTab.value === 'phim' ? selectedPhim.value.length : selectedLC.value.length;
});

const handleBulkDelete = () => {
    const isPhim = activeTab.value === 'phim';
    const selection = isPhim ? selectedPhim.value : selectedLC.value;
    const count = selection.length;
    const label = isPhim ? 'phim' : 'lịch chiếu';
    
    ElMessageBox.confirm(
        `Xác nhận xóa <b>${count}</b> ${label} đã chọn?`,
        'Xóa hàng loạt',
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await Promise.all(selection.map(item => phimApi.delete(item.id)));
            ElMessage.success(`Đã xóa ${count} ${label} thành công`);
            if (isPhim) selectedPhim.value = [];
            else selectedLC.value = [];
            fetchMovies();
            if (activeTab.value === 'lichChieu') fetchLichChieu();
        } catch (error) {
            ElMessage.error('Có lỗi khi xóa hàng loạt');
        }
    }).catch(() => {});
};

const editLCForm = ref({lichChieu: [], ngayKetThuc: ''});

// ==================== HELPER ====================
const thuLabels = {'0': 'CN', '1': 'T2', '2': 'T3', '3': 'T4', '4': 'T5', '5': 'T6', '6': 'T7'};
const thuOptions = [
  {label: 'Thứ 2', value: '1'}, {label: 'Thứ 3', value: '2'},
  {label: 'Thứ 4', value: '3'}, {label: 'Thứ 5', value: '4'},
  {label: 'Thứ 6', value: '5'}, {label: 'Thứ 7', value: '6'},
  {label: 'Chủ nhật', value: '0'},
];

const getLichChieuTags = (lichChieu) => {
  if (!lichChieu) return [];
  return lichChieu.split(',').map(t => thuLabels[t.trim()]).filter(Boolean);
};

const getTrangThaiLabel = (tt) => ({1: 'Đang chiếu', 2: 'Sắp chiếu', 0: 'Ngừng chiếu'}[tt] ?? '—');
const getTrangThaiClass = (tt) => ({1: 'status-now', 2: 'status-soon', 0: 'status-stop'}[tt] ?? '');

// Tính 5 ngày gần nhất theo lịch chiếu
const getDatesFromSchedule = (lichChieuStr) => {
  if (!lichChieuStr) return [];
  const allowedDays = lichChieuStr.split(',').map(Number);
  const dates = [];
  const today = new Date();
  for (let i = 0; i < 14; i++) {
    const d = new Date();
    d.setDate(today.getDate() + i);
    if (allowedDays.includes(d.getDay())) {
      dates.push({
        isToday: i === 0,
        thu: i === 0 ? 'H.nay' : thuLabels[d.getDay()],
        ngay: `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}`,
      });
    }
    if (dates.length >= 5) break;
  }
  return dates;
};

// ==================== API ====================
const fetchMovies = async () => {
  loading.value = true;
  try {
    const res = await phimApi.getPhim({
      tenPhim: searchQuery.value || null,
      trangThai: statusFilter.value,
      idTheLoai: genreFilter.value || null,
      page: currentPage.value - 1,
      size: pageSize.value,
    });
    moviesList.value = res.data.data.content;
    totalElements.value = res.data.data.totalElements;
  } catch {
    ElMessage.error('Lỗi tải dữ liệu phim');
  } finally {
    loading.value = false;
  }
};

const fetchGenres = async () => {
  try {
    const res = await phimApi.getTheLoai();
    genreOptions.value = res.data.data;
  } catch {
    console.error('Lỗi tải thể loại');
  }
};

const fetchLichChieu = async () => {
  lcLoading.value = true;
  try {
    const res = await phimApi.getPhim({
      tenPhim: lcSearchQuery.value || null,
      trangThai: null, idTheLoai: null,
      page: lcCurrentPage.value - 1,
      size: pageSize.value,
    });
    let data = res.data.data.content;
    if (lcThuFilter.value) {
      data = data.filter(m =>
          (m.lichChieu || '').split(',').map(t => t.trim()).includes(String(lcThuFilter.value))
      );
    }
    lcMoviesList.value = data;
    lcTotalElements.value = res.data.data.totalElements;
  } catch {
    ElMessage.error('Lỗi tải dữ liệu lịch chiếu');
  } finally {
    lcLoading.value = false;
  }
};

// ==================== CRUD ====================
const handleAdd = () => {
  editingMovie.value = null;
  movieForm.value = defaultForm();
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  editingMovie.value = row;
  movieForm.value = {
    ...row,
    idTheLoais: (row.theLoais || []).map(t => t.id),
    lichChieu: row.lichChieu ? row.lichChieu.split(',').map(t => t.trim()) : [],
  };
  dialogVisible.value = true;
};

const onLichChieuChange = (val) => {
  if (val.length > 5) {
    movieForm.value.lichChieu = val.slice(0, 5);
    ElMessage.warning('Lịch chiếu tối đa 5 thứ trong tuần!');
  }
};

const handleSave = async () => {
  if (!movieForm.value.tenPhim) {
    ElMessage.error('Tên phim không được để trống!');
    return;
  }
  if (movieForm.value.ngayKhoiChieu && movieForm.value.ngayKetThuc
      && movieForm.value.ngayKetThuc < movieForm.value.ngayKhoiChieu) {
    ElMessage.error('Ngày kết thúc phải sau ngày khởi chiếu!');
    return;
  }
  try {
    // ✅ FIX: convert array lichChieu → string trước khi gửi BE
    const payload = {
      ...movieForm.value,
      lichChieu: Array.isArray(movieForm.value.lichChieu)
          ? (movieForm.value.lichChieu.length > 0 ? movieForm.value.lichChieu.sort().join(',') : null)
          : movieForm.value.lichChieu || null,
      ngayKetThuc: movieForm.value.ngayKetThuc || null,
      ngayKhoiChieu: movieForm.value.ngayKhoiChieu || null,
      giaVeGoc: movieForm.value.giaVeGoc,
      danhGia: movieForm.value.danhGia || null,
      doTuoi: movieForm.value.doTuoi || null,
    };
    if (editingMovie.value) await phimApi.update(editingMovie.value.id, payload);
    else await phimApi.create(payload);
    dialogVisible.value = false;
    ElMessage.success(editingMovie.value ? 'Cập nhật phim thành công!' : 'Thêm phim thành công!');
    fetchMovies();
    if (activeTab.value === 'lichChieu') fetchLichChieu();
  } catch (e) {
    const msg = e?.response?.data?.message || 'Lỗi lưu phim, vui lòng thử lại!';
    ElMessage.error(msg);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`Xác nhận xóa phim "${row.tenPhim}"?`, 'Cảnh báo', {
    type: 'warning', confirmButtonText: 'Xóa', confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    await phimApi.delete(row.id);
    ElMessage.success('Đã xóa phim');
    fetchMovies();
    if (detailVisible.value) detailVisible.value = false;
  }).catch(() => {
  });
};

// ==================== SỬA LỊCH CHIẾU ====================
const handleEditLichChieu = (row) => {
  selectedMovie.value = row;
  editLCForm.value = {
    lichChieu: row.lichChieu ? row.lichChieu.split(',').map(t => t.trim()) : [],
    ngayKetThuc: row.ngayKetThuc || '',
  };
  editLCVisible.value = true;
};

const onEditLCChange = (val) => {
  if (val.length > 5) {
    editLCForm.value.lichChieu = val.slice(0, 5);
    ElMessage.warning('Lịch chiếu tối đa 5 thứ trong tuần!');
  }
};

const handleSaveLC = async () => {
  if (editLCForm.value.ngayKetThuc && selectedMovie.value.ngayKhoiChieu
      && editLCForm.value.ngayKetThuc < selectedMovie.value.ngayKhoiChieu) {
    ElMessage.error('Ngày kết thúc phải sau ngày khởi chiếu!');
    return;
  }
  try {
    const payload = {
      ...selectedMovie.value,
      idTheLoais: (selectedMovie.value.theLoais || []).map(t => t.id),
      lichChieu: editLCForm.value.lichChieu.length > 0
          ? editLCForm.value.lichChieu.sort().join(',')
          : null,
      ngayKetThuc: editLCForm.value.ngayKetThuc || null,
    };
    await phimApi.update(selectedMovie.value.id, payload);
    ElMessage.success('Cập nhật lịch chiếu thành công!');
    editLCVisible.value = false;
    fetchMovies();
    fetchLichChieu();
  } catch (e) {
    const msg = e?.response?.data?.message || 'Lỗi cập nhật lịch chiếu!';
    ElMessage.error(msg);
  }
};

const handleReset = () => {
  searchQuery.value = '';
  statusFilter.value = null;
  genreFilter.value = null;
  currentPage.value = 1;
  fetchMovies();
};
const handleResetLC = () => {
  lcSearchQuery.value = '';
  lcThuFilter.value = null;
  lcCurrentPage.value = 1;
  fetchLichChieu();
};

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchMovies();
  fetchGenres();
});
watch([currentPage, pageSize], fetchMovies);
watch([lcCurrentPage, pageSize], fetchLichChieu);
watch(activeTab, (tab) => {
  if (tab === 'lichChieu') fetchLichChieu();
});

const selectedDateIdx = ref(0);

// ==================== COLUMNS ====================
const tableColumnsPhim = [
  {label: 'POSTER', key: 'poster', width: '80px'},
  {label: 'THÔNG TIN', key: 'tenPhim'},
  {label: 'THỂ LOẠI', key: 'theLoais', width: '160px'},
  {label: 'THỜI LƯỢNG', key: 'thoiLuong', width: '100px'},
  {label: 'NGÀY BẮT ĐẦU', key: 'ngayKhoiChieu', width: '115px'},
  {label: 'NGÀY KẾT THÚC', key: 'ngayKetThuc', width: '115px'},
  {label: 'LỊCH CHIẾU', key: 'lichChieu', width: '160px'},
  {label: 'GIÁ VÉ GỐC', key: 'giaVeGoc', width: '115px'},
  {label: 'TRẠNG THÁI', key: 'trangThai', width: '115px'},
];

const tableColumnsLC = [
  {label: 'POSTER', key: 'poster', width: '80px'},
  {label: 'TÊN PHIM', key: 'tenPhim'},
  {label: 'THỂ LOẠI', key: 'theLoais', width: '160px'},
  {label: 'NGÀY BẮT ĐẦU', key: 'ngayKhoiChieu', width: '115px'},
  {label: 'NGÀY KẾT THÚC', key: 'ngayKetThuc', width: '115px'},
  {label: 'LỊCH CHIẾU', key: 'lichChieu', width: '180px'},
  {label: 'TRẠNG THÁI', key: 'trangThai', width: '115px'},
];
</script>

<template>
  <div class="movie-management w-100 h-100 bg-transparent overflow-hidden">
    <AdminTableLayout
      title="Phim & Lịch chiếu"
      titleIcon="bi bi-film"
      addButtonLabel="Thêm phim mới"
      :data="currentData"
      :loading="currentLoading"
      :total="currentTotal"
      v-model:currentPage="layoutCurrentPage"
      v-model:pageSize="pageSize"
      @add-click="handleAdd"
      @reset-filter="activeTab === 'phim' ? handleReset : handleResetLC"
    >
      <!-- Tabs in header left -->
      <template #header-actions-left>
        <el-button v-if="selectedIdsCount" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
          Xóa {{ selectedIdsCount }} {{ activeTab === 'phim' ? 'phim' : 'lịch chiếu' }}
        </el-button>
        <div class="ms-3 segmented-control shadow-sm">
          <button 
            @click="activeTab = 'phim'" 
            class="segmented-tab" 
            :class="{ active: activeTab === 'phim' }"
          >
            <i class="bi bi-film me-2"></i>Phim
          </button>
          <button 
            @click="activeTab = 'lichChieu'" 
            class="segmented-tab" 
            :class="{ active: activeTab === 'lichChieu' }"
          >
            <i class="bi bi-calendar-event me-2"></i>Lịch chiếu
          </button>
        </div>
      </template>

      <!-- Filters Slot -->
      <template #filters>
        <!-- Phim Filters -->
        <template v-if="activeTab === 'phim'">
          <div class="filter-item flex-grow-1" style="max-width:350px;">
            <span class="filter-label text-dark small fw-bold mb-1 d-block">Tìm theo tên phim</span>
            <el-input v-model="searchQuery" placeholder="Nhập tên phim..." :prefix-icon="Search" clearable
                        @keyup.enter="fetchMovies"/>
          </div>
          <div class="filter-item">
            <span class="filter-label text-dark small fw-bold mb-1 d-block">Trạng thái</span>
            <el-select v-model="statusFilter" placeholder="Tất cả trạng thái" clearable style="width:160px;"
                         @change="fetchMovies">
                <el-option label="Đang chiếu" :value="1"/>
                <el-option label="Sắp chiếu" :value="2"/>
                <el-option label="Ngừng chiếu" :value="0"/>
            </el-select>
          </div>
          <div class="filter-item">
            <span class="filter-label text-dark small fw-bold mb-1 d-block">Thể loại</span>
            <el-select v-model="genreFilter" placeholder="Chọn thể loại" clearable style="width:180px;"
                         @change="fetchMovies">
                <el-option v-for="g in genreOptions" :key="g.id" :label="g.tenTheLoai" :value="g.id"/>
            </el-select>
          </div>
        </template>

        <!-- Lịch chiếu Filters -->
        <template v-else>
           <div class="filter-item flex-grow-1" style="max-width:350px;">
            <span class="filter-label text-dark small fw-bold mb-1 d-block">Tìm theo tên phim</span>
            <el-input v-model="lcSearchQuery" placeholder="Tìm theo tên phim..." :prefix-icon="Search" clearable
                        @keyup.enter="fetchLichChieu"/>
          </div>
          <div class="filter-item">
            <span class="filter-label text-dark small fw-bold mb-1 d-block">Thứ trong tuần</span>
            <el-select v-model="lcThuFilter" placeholder="Lọc theo thứ" clearable style="width:160px;"
                         @change="fetchLichChieu">
                <el-option v-for="t in thuOptions" :key="t.value" :label="t.label" :value="t.value"/>
            </el-select>
          </div>
        </template>
      </template>

      <!-- Content Slot with BaseTable -->
      <template #content>
        <div class="h-100 overflow-hidden">
             <!-- ==================== TAB PHIM ==================== -->
            <template v-if="activeTab==='phim'">
                <BaseTable v-loading="loading" :data="moviesList" :columns="tableColumnsPhim"
                           :total="totalElements" v-model:current-page="currentPage"
                           :page-size="pageSize" v-model:page-size="pageSize"
                           v-model:selection="selectedPhim"
                           :hide-pagination="true"
                           @edit="handleEdit" @delete="handleDelete">
        
                  <template #actions="{ row }">
                    <div class="d-flex gap-1 justify-content-center align-items-center">
                      <button class="btn-action text-detail" title="Chi tiết"
                              @click="selectedMovie=row; detailVisible=true">
                        <i class="bi bi-eye"></i>
                      </button>
                      <button class="btn-action text-edit" title="Chỉnh sửa" @click="handleEdit(row)">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn-action text-delete" title="Xóa" @click="handleDelete(row)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </template>
        
                  <template #cell-poster="{ row }">
                    <div class="py-1">
                      <img v-if="row.poster" :src="row.poster" class="rounded-3 shadow-sm"
                           style="width:42px;height:62px;object-fit:cover;"
                           @error="e => e.target.style.display='none'"/>
                      <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center"
                           style="width:42px;height:62px;">
                        <i class="bi bi-film text-secondary" style="font-size:18px;"></i>
                      </div>
                    </div>
                  </template>
        
                  <template #cell-tenPhim="{ row }">
                    <div class="fw-bold text-start ps-2" style="font-size:13px;color:#344767;">{{ row.tenPhim }}</div>
                    <div class="text-secondary text-start ps-2" style="font-size:11px;">
                      <span v-if="row.danhGia > 0">⭐ {{ row.danhGia }}/10</span>
                      <span v-else>—</span>
                    </div>
                  </template>
        
                  <template #cell-theLoais="{ row }">
                    <div class="d-flex flex-wrap gap-1 justify-content-center">
                      <el-tag v-for="g in (row.theLoais || [])" :key="g.id"
                              size="small" effect="plain" class="genre-tag">{{ g.tenTheLoai }}
                      </el-tag>
                      <span v-if="!row.theLoais?.length" class="text-secondary" style="font-size:12px;">—</span>
                    </div>
                  </template>
        
                  <template #cell-thoiLuong="{ row }">
                    <span style="font-size:12px;color:#606266;">{{ row.thoiLuong }} phút</span>
                  </template>
        
                  <template #cell-ngayKhoiChieu="{ row }">
                    <span style="font-size:12px;color:#00b341;font-weight:600;">{{ row.ngayKhoiChieu || '—' }}</span>
                  </template>
        
                  <template #cell-ngayKetThuc="{ row }">
                    <span style="font-size:12px;color:#ff4d4f;font-weight:600;">{{ row.ngayKetThuc || '—' }}</span>
                  </template>
        
                  <template #cell-lichChieu="{ row }">
                    <div v-if="row.lichChieu" class="d-flex justify-content-center">
                      <button class="btn-xem-lich" @click="selectedMovie=row; selectedDateIdx=0; scheduleVisible=true">
                        <i class="bi bi-calendar3 me-1"></i>Xem lịch chiếu
                      </button>
                    </div>
                    <span v-else class="text-secondary" style="font-size:12px;">Chưa có</span>
                  </template>
        
                  <template #cell-giaVeGoc="{ row }">
                    <span class="fw-bold text-price">{{ row.giaVeGoc?.toLocaleString('vi-VN') }} đ</span>
                  </template>
        
                  <template #cell-trangThai="{ row }">
                    <span class="badge rounded-pill px-2 py-1" :class="getTrangThaiClass(row.trangThai)"
                          style="font-size:11px;">
                      {{ getTrangThaiLabel(row.trangThai) }}
                    </span>
                  </template>
                </BaseTable>
            </template>
        
            <!-- ==================== TAB LỊCH CHIẾU ==================== -->
            <template v-if="activeTab==='lichChieu'">
                <BaseTable v-loading="lcLoading" :data="lcMoviesList" :columns="tableColumnsLC"
                           :total="lcTotalElements" v-model:current-page="lcCurrentPage"
                           :page-size="pageSize" v-model:page-size="pageSize"
                           v-model:selection="selectedLC"
                           :hide-pagination="true">
        
                  <template #actions="{ row }">
                    <div class="d-flex gap-1 justify-content-center align-items-center">
                      <button class="btn-action text-edit" title="Sửa lịch chiếu"
                              @click="handleEditLichChieu(row)">
                        <i class="bi bi-pencil-square"></i>
                      </button>
                      <button class="btn-action text-delete" title="Xóa phim"
                              @click="handleDelete(row)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </template>
        
                  <template #cell-poster="{ row }">
                    <div class="py-1">
                      <img v-if="row.poster" :src="row.poster" class="rounded-3 shadow-sm"
                           style="width:42px;height:62px;object-fit:cover;"
                           @error="e => e.target.style.display='none'"/>
                      <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center"
                           style="width:42px;height:62px;">
                        <i class="bi bi-film text-secondary" style="font-size:18px;"></i>
                      </div>
                    </div>
                  </template>
        
                  <template #cell-tenPhim="{ row }">
                    <div class="fw-bold text-start ps-2" style="font-size:13px;color:#344767;">{{ row.tenPhim }}</div>
                  </template>
        
                  <template #cell-theLoais="{ row }">
                    <div class="d-flex flex-wrap gap-1 justify-content-center">
                      <el-tag v-for="g in (row.theLoais || [])" :key="g.id"
                              size="small" effect="plain" class="genre-tag">{{ g.tenTheLoai }}
                      </el-tag>
                      <span v-if="!row.theLoais?.length" class="text-secondary" style="font-size:12px;">—</span>
                    </div>
                  </template>
        
                  <template #cell-ngayKhoiChieu="{ row }">
                    <span style="font-size:12px;color:#00b341;font-weight:600;">{{ row.ngayKhoiChieu || '—' }}</span>
                  </template>
        
                  <template #cell-ngayKetThuc="{ row }">
                    <span style="font-size:12px;color:#ff4d4f;font-weight:600;">{{ row.ngayKetThuc || '—' }}</span>
                  </template>
        
                  <template #cell-lichChieu="{ row }">
                    <div v-if="row.lichChieu" class="d-flex flex-wrap gap-1 justify-content-center">
                      <el-tag v-for="t in getLichChieuTags(row.lichChieu)" :key="t"
                              size="small" effect="light" type="primary" class="tag-thu">{{ t }}
                      </el-tag>
                    </div>
                    <span v-else class="text-secondary" style="font-size:12px;">Chưa có</span>
                  </template>
        
                  <template #cell-trangThai="{ row }">
                    <span class="badge rounded-pill px-2 py-1" :class="getTrangThaiClass(row.trangThai)"
                          style="font-size:11px;">
                      {{ getTrangThaiLabel(row.trangThai) }}
                    </span>
                  </template>
                </BaseTable>
            </template>
        </div>
      </template>
    </AdminTableLayout>


    <!-- ===== DIALOG THÊM / SỬA PHIM ===== -->
    <el-dialog v-model="dialogVisible"
               :title="editingMovie ? 'Chỉnh sửa phim' : 'Thêm phim mới'"
               width="640px" class="rounded-4" :close-on-click-modal="false">
      <el-form :model="movieForm" label-position="top" class="px-1">
        <div class="row g-3">
          <div class="col-12">
            <el-form-item label="Tên phim *">
              <el-input v-model="movieForm.tenPhim" placeholder="VD: Dune: Part Two"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Thể loại">
              <el-select v-model="movieForm.idTheLoais" multiple placeholder="Chọn thể loại" class="w-100">
                <el-option v-for="g in genreOptions" :key="g.id" :label="g.tenTheLoai" :value="g.id"/>
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Trạng thái">
              <el-select v-model="movieForm.trangThai" class="w-100">
                <el-option label="Đang chiếu" :value="1"/>
                <el-option label="Sắp chiếu" :value="2"/>
                <el-option label="Ngừng chiếu" :value="0"/>
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Thời lượng (phút) *">
              <el-input-number v-model="movieForm.thoiLuong" :min="1" class="w-100"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Giá vé gốc (VNĐ) *">
              <el-input-number v-model="movieForm.giaVeGoc" :min="1000" :step="5000" :precision="0" class="w-100"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Ngày khởi chiếu">
              <el-input v-model="movieForm.ngayKhoiChieu" type="date" class="w-100"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Ngày kết thúc">
              <el-input v-model="movieForm.ngayKetThuc" type="date" class="w-100"/>
            </el-form-item>
          </div>
          <!-- ✅ Checkbox lịch chiếu -->
          <div class="col-12">
            <el-form-item>
              <template #label>
                <span style="font-size:13px;">Lịch chiếu trong tuần</span>
                <span class="text-secondary ms-2" style="font-size:11px;">(tối đa 5 thứ)</span>
              </template>
              <el-checkbox-group v-model="movieForm.lichChieu" @change="onLichChieuChange"
                                 class="d-flex flex-wrap gap-1">
                <el-checkbox-button v-for="t in thuOptions" :key="t.value" :value="t.value" size="small">
                  {{ t.label }}
                </el-checkbox-button>
              </el-checkbox-group>
              <div class="text-secondary mt-1" style="font-size:11px;">
                Đã chọn: <strong>{{ movieForm.lichChieu.length }}/5</strong>
                <span v-if="movieForm.lichChieu.length > 0" class="ms-2 text-primary">
                  ({{ movieForm.lichChieu.map(t => thuLabels[t]).join(', ') }})
                </span>
              </div>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Đạo diễn">
              <el-input v-model="movieForm.daoDien" placeholder="Tên đạo diễn"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Ngôn ngữ">
              <el-input v-model="movieForm.ngonNgu" placeholder="VD: Tiếng Anh"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Giới hạn độ tuổi">
              <el-input-number v-model="movieForm.doTuoi" :min="0" :max="18" class="w-100"/>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Điểm đánh giá (0–10)">
              <el-input-number v-model="movieForm.danhGia" :min="0" :max="10" :precision="1" :step="0.1" class="w-100"/>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Diễn viên">
              <el-input v-model="movieForm.dienVien" placeholder="Phân cách bằng dấu phẩy"/>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Link ảnh Poster">
              <el-input v-model="movieForm.poster" placeholder="https://..."/>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Link Trailer">
              <el-input v-model="movieForm.trailer" placeholder="https://youtube.com/..."/>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Mô tả nội dung">
              <el-input v-model="movieForm.moTa" type="textarea" :rows="3"/>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible=false">Hủy</el-button>
          <el-button type="primary" @click="handleSave">
            {{ editingMovie ? 'Cập nhật' : 'Thêm phim' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ===== DIALOG CHI TIẾT PHIM ===== -->
    <el-dialog v-model="detailVisible" title="Chi tiết phim" width="700px"
               class="rounded-4 dialog-detail" :close-on-click-modal="true">
      <div v-if="selectedMovie" class="p-2">

        <!-- Header: poster + info chính (giống ảnh mẫu) -->
        <div class="d-flex gap-4 mb-4 pb-4 border-bottom">
          <!-- Poster với fallback đẹp -->
          <div class="poster-wrap flex-shrink-0">
            <img :src="selectedMovie.poster || ''"
                 class="detail-poster"
                 @load="e => { e.target.style.display='block'; e.target.nextElementSibling.style.display='none'; }"
                 @error="e => { e.target.style.display='none'; e.target.nextElementSibling.style.display='flex'; }"
                 style="display:block;"/>
            <div class="detail-poster-fallback">
              <i class="bi bi-film"></i>
            </div>
          </div>
          <!-- Info bên phải -->
          <div class="flex-grow-1 d-flex flex-column justify-content-center">
            <h4 class="fw-bold text-dark mb-1" style="font-size:17px;">{{ selectedMovie.tenPhim }}</h4>
            <div class="text-secondary fst-italic mb-3" style="font-size:12px;">
              {{ selectedMovie.daoDien || 'Đạo diễn chưa cập nhật' }}
            </div>

            <!-- Badge trạng thái + thể loại -->
            <div class="d-flex gap-1 flex-wrap mb-3">
              <span class="badge rounded-pill px-3 py-1" :class="getTrangThaiClass(selectedMovie.trangThai)"
                    style="font-size:11px;">
                {{ getTrangThaiLabel(selectedMovie.trangThai) }}
              </span>
              <el-tag v-for="g in (selectedMovie.theLoais||[])" :key="g.id"
                      size="small" effect="plain" class="genre-tag rounded-pill">{{ g.tenTheLoai }}
              </el-tag>
            </div>

            <!-- Thông tin inline kiểu icon (giống ảnh mẫu) -->
            <div class="d-flex flex-wrap gap-3 mb-3" style="font-size:13px;color:#606266;">
              <span><i class="bi bi-clock me-1"></i>{{ selectedMovie.thoiLuong }} phút</span>
              <span><i class="bi bi-calendar me-1"></i>{{ selectedMovie.ngayKhoiChieu || '—' }}</span>
              <span v-if="selectedMovie.danhGia > 0" style="color:#f59e0b;"><i
                  class="bi bi-star-fill me-1"></i>{{ selectedMovie.danhGia }}/10</span>
            </div>

            <!-- Grid 2 cột -->
            <div class="row g-2">
              <div class="col-6">
                <div class="lbl">Giá vé gốc</div>
                <div class="val text-price">{{ selectedMovie.giaVeGoc?.toLocaleString('vi-VN') }} đ</div>
              </div>
              <div class="col-6">
                <div class="lbl">Ngôn ngữ</div>
                <div class="val">{{ selectedMovie.ngonNgu || '—' }}</div>
              </div>
              <div class="col-6">
                <div class="lbl">Ngày kết thúc</div>
                <div class="val" style="color:#ff4d4f;">{{ selectedMovie.ngayKetThuc || '—' }}</div>
              </div>
              <div class="col-6">
                <div class="lbl">Giới hạn tuổi</div>
                <div class="val">{{ selectedMovie.doTuoi ? 'T' + selectedMovie.doTuoi + '+' : '—' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Lịch chiếu -->
        <div class="mb-3">
          <div class="lbl mb-2">Lịch chiếu trong tuần</div>
          <div v-if="selectedMovie.lichChieu" class="d-flex flex-wrap gap-2">
            <div v-for="item in getDatesFromSchedule(selectedMovie.lichChieu)" :key="item.ngay"
                 class="date-card-mini">
              <div class="card-thu">{{ item.thu }}</div>
              <div class="card-ngay">{{ item.ngay }}</div>
            </div>
          </div>
          <div v-else class="text-secondary" style="font-size:12px;">Chưa có lịch chiếu</div>
        </div>

        <!-- Trailer + Diễn viên + Nội dung -->
        <div class="mb-3">
          <div class="lbl mb-1">Trailer</div>
          <a v-if="selectedMovie.trailer" :href="selectedMovie.trailer" target="_blank"
             class="text-danger fw-bold text-decoration-none" style="font-size:13px;">
            <i class="bi bi-youtube me-1"></i>Xem trailer
          </a>
          <span v-else class="text-secondary" style="font-size:12px;">—</span>
        </div>

        <div class="mb-3">
          <div class="lbl mb-2">Diễn viên</div>
          <div class="d-flex flex-wrap gap-1">
            <el-tag v-for="a in (selectedMovie.dienVien?.split(',') || [])" :key="a"
                    effect="plain" class="rounded-3" size="small">{{ a.trim() }}
            </el-tag>
            <span v-if="!selectedMovie.dienVien" class="text-secondary" style="font-size:12px;">—</span>
          </div>
        </div>

        <div class="lbl mb-2">Nội dung</div>
        <div class="p-3 bg-light rounded-4 lh-base text-secondary" style="font-size:12px;">
          {{ selectedMovie.moTa || '—' }}
        </div>
      </div>
      <template #footer>
        <div class="d-flex justify-content-between px-2 pb-1">
          <el-button type="danger" plain class="rounded-3" @click="handleDelete(selectedMovie)">
            <i class="bi bi-trash me-1"></i>Xóa phim
          </el-button>
          <div class="d-flex gap-2">
            <el-button @click="detailVisible=false" class="rounded-3">Đóng</el-button>
            <el-button type="primary" class="rounded-3"
                       @click="detailVisible=false; handleEdit(selectedMovie)">
              <i class="bi bi-pencil me-1"></i>Chỉnh sửa
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- ===== DIALOG XEM LỊCH CHIẾU ===== -->
    <el-dialog v-model="scheduleVisible" title="Lịch chiếu phim" width="600px" class="rounded-4">
      <div v-if="selectedMovie" class="p-2">
        <div class="d-flex gap-3 mb-4 pb-3 border-bottom align-items-center">
          <!-- Poster với fallback -->
          <div class="flex-shrink-0" style="width:70px;height:100px;position:relative;">
            <img :src="selectedMovie.poster || ''"
                 style="width:70px;height:100px;object-fit:cover;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,.15);display:block;"
                 @error="e => { e.target.style.display='none'; e.target.nextElementSibling.style.display='flex'; }"/>
            <div
                style="display:none;width:70px;height:100px;background:#f0f2f5;border-radius:8px;align-items:center;justify-content:center;position:absolute;top:0;left:0;">
              <i class="bi bi-film text-secondary fs-3"></i>
            </div>
          </div>
          <div>
            <h5 class="fw-bold mb-1 text-dark" style="font-size:15px;">{{ selectedMovie.tenPhim }}</h5>
            <div class="d-flex gap-1 flex-wrap mb-1">
              <span class="badge rounded-pill px-2 py-1" :class="getTrangThaiClass(selectedMovie.trangThai)"
                    style="font-size:10px;">
                {{ getTrangThaiLabel(selectedMovie.trangThai) }}
              </span>
              <el-tag v-for="g in (selectedMovie.theLoais||[])" :key="g.id"
                      size="small" effect="plain" class="genre-tag rounded-pill">{{ g.tenTheLoai }}
              </el-tag>
            </div>
            <div style="font-size:11px;color:#909399;" class="mt-1">
              <i class="bi bi-clock me-1"></i>{{ selectedMovie.thoiLuong }} phút
              <span v-if="selectedMovie.ngayKhoiChieu" class="ms-2">
                <i class="bi bi-calendar me-1"></i>{{ selectedMovie.ngayKhoiChieu }}
                <span v-if="selectedMovie.ngayKetThuc"> → <span style="color:#ff4d4f;">{{
                    selectedMovie.ngayKetThuc
                  }}</span></span>
              </span>
            </div>
          </div>
        </div>

        <!-- ✅ Chỉ hiển thị lịch chiếu (các thứ + ngày), không có khung giờ -->
        <div class="mb-3">
          <div class="small fw-bold text-secondary text-uppercase mb-3" style="font-size:11px;">Lịch chiếu</div>
          <div v-if="getDatesFromSchedule(selectedMovie.lichChieu).length > 0" class="d-flex gap-2 flex-wrap">
            <div v-for="(item, idx) in getDatesFromSchedule(selectedMovie.lichChieu)" :key="idx"
                 class="date-card" :class="idx === selectedDateIdx ? 'active' : ''"
                 @click="selectedDateIdx=idx">
              <div class="card-ngay">{{ item.ngay }}</div>
              <div class="card-thu">{{ item.thu }}</div>
            </div>
          </div>
          <div v-else class="p-3 bg-light rounded-3 text-center text-secondary" style="font-size:12px;">
            <i class="bi bi-calendar-x me-2"></i>Chưa có lịch chiếu. Vui lòng cập nhật ở tab Lịch chiếu.
          </div>
        </div>

        <div v-if="getDatesFromSchedule(selectedMovie.lichChieu).length > 0"
             class="p-3 bg-light rounded-3 text-center text-secondary" style="font-size:12px;">
          <i class="bi bi-info-circle me-2"></i>
          Suất chiếu sẽ được xếp bởi quản lý sau khi lịch chiếu được duyệt.
        </div>
      </div>
      <template #footer>
        <el-button @click="scheduleVisible=false" class="rounded-3">Đóng</el-button>
      </template>
    </el-dialog>

    <!-- ===== DIALOG SỬA LỊCH CHIẾU ===== -->
    <el-dialog v-model="editLCVisible" title="Cập nhật lịch chiếu" width="460px" class="rounded-4">
      <div v-if="selectedMovie" class="px-1">
        <div class="d-flex gap-3 mb-4 pb-3 border-bottom align-items-center">
          <img v-if="selectedMovie.poster" :src="selectedMovie.poster"
               class="rounded-3 shadow-sm" style="width:50px;height:72px;object-fit:cover;"
               @error="e => e.target.style.display='none'"/>
          <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center"
               style="width:50px;height:72px;">
            <i class="bi bi-film text-secondary fs-4"></i>
          </div>
          <div>
            <div class="fw-bold" style="font-size:14px;color:#344767;">{{ selectedMovie.tenPhim }}</div>
            <div style="font-size:11px;color:#909399;" class="mt-1">
              <i class="bi bi-calendar me-1"></i>Khởi chiếu:
              <span style="color:#00b341;font-weight:600;">{{ selectedMovie.ngayKhoiChieu || '—' }}</span>
            </div>
          </div>
        </div>
        <el-form :model="editLCForm" label-position="top">
          <el-form-item label="Ngày kết thúc chiếu">
            <el-input v-model="editLCForm.ngayKetThuc" type="date" class="w-100"/>
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="font-size:13px;">Lịch chiếu trong tuần</span>
              <span class="text-secondary ms-2" style="font-size:11px;">(tối đa 5 thứ)</span>
            </template>
            <el-checkbox-group v-model="editLCForm.lichChieu" @change="onEditLCChange" class="d-flex flex-wrap gap-1">
              <el-checkbox-button v-for="t in thuOptions" :key="t.value" :value="t.value" size="default">
                {{ t.label }}
              </el-checkbox-button>
            </el-checkbox-group>
            <div class="text-secondary mt-2" style="font-size:11px;">
              Đã chọn: <strong class="text-primary">{{ editLCForm.lichChieu.length }}/5</strong>
              <span v-if="editLCForm.lichChieu.length > 0" class="ms-2">
                ({{ editLCForm.lichChieu.map(t => thuLabels[t]).join(' · ') }})
              </span>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="editLCVisible=false">Hủy</el-button>
          <el-button type="primary" @click="handleSaveLC">Lưu lịch chiếu</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.btn-red {
  background-color: #d32f2f !important;
  color: #fff !important;
  border-color: #d32f2f !important;
}

.btn-add-movie {
  background-color: #409eff !important;
  font-weight: 600;
  padding: 9px 22px;
}

.filter-wrapper {
  border: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, .04);
}

.text-price {
  color: #00b341;
  font-size: 13px;
}

.status-now {
  background: #e6f9f0;
  color: #00b341;
}

.status-soon {
  background: #fff9e6;
  color: #ff9900;
}

.status-stop {
  background: #ffeef0;
  color: #ff4d4f;
}

.genre-tag {
  border: 1px solid #dcdfe6 !important;
  color: #909399 !important;
  background: #fff;
  border-radius: 6px;
  font-size: 11px !important;
}

/* Action buttons */
.btn-action {
  border: none;
  background: transparent;
  padding: 3px 7px;
  font-size: 16px;
  transition: .2s;
  cursor: pointer;
  border-radius: 6px;
}

.text-schedule {
  color: #00b341;
}

.text-detail {
  color: #409eff;
}

.text-edit {
  color: #909399;
}

.text-delete {
  color: #ff4d4f;
}

.btn-action:hover {
  opacity: .75;
  transform: translateY(-1px);
}

/* Nút xem lịch chiếu trong cột */
.btn-xem-lich {
  background: none;
  border: 1px solid #409eff;
  color: #409eff;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 11px;
  cursor: pointer;
  transition: .15s;
}

.btn-xem-lich:hover {
  background: #409eff;
  color: #fff;
}

/* Tag thứ nhỏ */
.tag-thu {
  font-size: 11px !important;
  padding: 0 6px !important;
}

/* Dialog detail */
.dialog-detail .lbl {
  color: #909399;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  margin-bottom: 2px;
}

.dialog-detail .val {
  font-weight: 700;
  font-size: 13px;
  color: #303133;
}

/* Date cards in dialog */
.date-card {
  border: 1px solid #dcdfe6;
  border-radius: 10px;
  padding: 7px 11px;
  min-width: 64px;
  text-align: center;
  cursor: pointer;
  transition: .2s;
}

.date-card:hover {
  border-color: #d32f2f;
}

.date-card.active {
  background: #d32f2f;
  color: #fff;
  border-color: #d32f2f;
}

.card-ngay {
  font-weight: 800;
  font-size: 14px;
}

.card-thu {
  font-size: 10px;
  opacity: .85;
}

/* Date cards mini in detail dialog */
.date-card-mini {
  background: #f0f7ff;
  border: 1px solid #b3d4ff;
  border-radius: 8px;
  padding: 5px 10px;
  text-align: center;
  min-width: 56px;
}

.date-card-mini .card-thu {
  font-size: 11px;
  font-weight: 700;
  color: #409eff;
}

.date-card-mini .card-ngay {
  font-size: 10px;
  color: #606266;
}

/* Info grid */
.info-grid .lbl {
  color: #909399;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  margin-bottom: 1px;
}

.info-grid .val {
  font-weight: 700;
  font-size: 13px;
  color: #303133;
}

:deep(.el-checkbox-button__inner) {
  border-radius: 6px !important;
  margin: 2px;
  font-size: 12px !important;
}

/* Poster trong dialog chi tiết */
.poster-wrap {
  width: 140px;
  height: 200px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, .18);
  background: #f0f2f5;
  flex-shrink: 0;
}

.detail-poster {
  width: 140px;
  height: 200px;
  object-fit: cover;
  display: block;
  border-radius: 12px;
}

.detail-poster-fallback {
  display: flex;
  width: 140px;
  height: 200px;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #c0c4cc;
  position: absolute;
  top: 0;
  left: 0;
}

/* Segmented Control */
.segmented-control {
  background: #f1f3f4;
  padding: 4px;
  border-radius: 12px;
  display: flex;
  gap: 2px;
  border: 1px solid rgba(0,0,0,0.05);
}

.segmented-tab {
  padding: 8px 24px;
  border: none;
  background: transparent;
  border-radius: 9px;
  font-size: 13px;
  font-weight: 600;
  color: #5f6368;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 120px;
}

.segmented-tab.active {
  background: #ffffff;
  color: #409eff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
}

.segmented-tab i {
  font-size: 14px;
  transition: transform 0.2s;
}

.segmented-tab.active i {
  transform: scale(1.1);
}

.segmented-tab:not(.active):hover {
  background: rgba(0,0,0,0.03);
  color: #202124;
}
</style>