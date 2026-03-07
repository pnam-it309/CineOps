<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { phimApi } from '@/services/api/admin/phimService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import { 
  Tickets, Files, Calendar, Opportunity, Monitor, 
  Link, VideoPlay, ArrowLeft, Check
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const genreOptions = ref([]);

const generateMovieCode = () => {
  const chars = '0123456789';
  let code = 'MP-';
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return code;
};

const movieForm = ref({
  tenPhim: '', thoiLuong: 120,
  ngayKhoiChieu: '', ngayKetThuc: '',
  lichChieu: [],
  idTheLoais: [],
  giaPhim: null, trangThai: 1,
  poster: '', trailer: '', moTa: '',
  ngonNgu: '', doTuoi: 0, danhGia: 0,
  maPhim: generateMovieCode(), loaiPhim: '2D', phuPhiLoaiPhim: 0,
  nhanDoTuoi: '', hienThiCanhBaoDoTuoi: true
});

// Logic chặn chọn ngày quá khứ cho Ngày kết thúc
const disabledDateEnd = (time) => {
  return time.getTime() < Date.now() - 8.64e7; // Chặn trước ngày hôm nay
};

// Tự động tính trạng thái dựa trên ngày
const predictedStatus = computed(() => {
  if (!movieForm.value.ngayKhoiChieu || !movieForm.value.ngayKetThuc) return movieForm.value.trangThai;
  
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const start = new Date(movieForm.value.ngayKhoiChieu);
  const end = new Date(movieForm.value.ngayKetThuc);

  if (today < start) return 2; // Sắp chiếu
  if (today <= end) return 1; // Đang chiếu
  return 0; // Ngừng chiếu
});

watch(predictedStatus, (newVal) => {
  movieForm.value.trangThai = newVal;
});

const isEdit = computed(() => !!route.params.id);

const thuLabels = { '0': 'CN', '1': 'T2', '2': 'T3', '3': 'T4', '4': 'T5', '5': 'T6', '6': 'T7' };
const thuOptions = [
  { label: 'Thứ 2', value: '1' }, { label: 'Thứ 3', value: '2' },
  { label: 'Thứ 4', value: '3' }, { label: 'Thứ 5', value: '4' },
  { label: 'Thứ 6', value: '5' }, { label: 'Thứ 7', value: '6' },
  { label: 'Chủ nhật', value: '0' },
];

const fetchGenres = async () => {
  try {
    const res = await phimApi.getTheLoai();
    genreOptions.value = res.data.data;
  } catch {
    notification.error('Không thể tải danh sách thể loại');
  }
};

const fetchMovieDetail = async () => {
  if (!isEdit.value) return;
  loading.value = true;
  try {
    const res = await phimApi.getById(route.params.id);
    const row = res.data.data || res.data;
    if (row) {
      movieForm.value = {
        ...row,
        idTheLoais: (row.theLoais || []).map(t => t.id),
        lichChieu: row.lichChieu ? row.lichChieu.split(',').map(t => t.trim()) : [],
      };
    }
  } catch {
    notification.error('Không thể tải thông tin phim');
  } finally {
    loading.value = false;
  }
};

const onLichChieuChange = (val) => {
  if (val.length > 5) {
    movieForm.value.lichChieu = val.slice(0, 5);
    notification.warning('Lịch chiếu tối đa 5 thứ trong tuần!');
  }
};

const handleSave = async () => {
  if (!movieForm.value.tenPhim) {
    notification.error('Tên phim không được để trống!');
    return;
  }
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (movieForm.value.ngayKetThuc && new Date(movieForm.value.ngayKetThuc) < today) {
    notification.error('Ngày kết thúc không được nhỏ hơn ngày hôm nay!');
    return;
  }

  if (movieForm.value.ngayKhoiChieu && movieForm.value.ngayKetThuc
      && movieForm.value.ngayKetThuc < movieForm.value.ngayKhoiChieu) {
    notification.error('Ngày kết thúc phải sau ngày khởi chiếu!');
    return;
  }
  
  try {
    if (isEdit.value) await confirmDialog.update('phim');
    else await confirmDialog.add('phim');
  } catch { return; }

  loading.value = true;
  try {
    const payload = {
      ...movieForm.value,
      lichChieu: Array.isArray(movieForm.value.lichChieu)
          ? (movieForm.value.lichChieu.length > 0 ? movieForm.value.lichChieu.sort().join(',') : null)
          : movieForm.value.lichChieu || null,
      ngayKetThuc: movieForm.value.ngayKetThuc || null,
      ngayKhoiChieu: movieForm.value.ngayKhoiChieu || null,
    };
    if (isEdit.value) {
      await phimApi.update(route.params.id, payload);
      notification.updateSuccess('phim');
    } else {
      await phimApi.create(payload);
      notification.addSuccess('phim');
    }
    router.push('/admin/movies-schedule');
  } catch (e) {
    const msg = e?.response?.data?.message || 'Lỗi lưu phim, vui lòng thử lại!';
    notification.error(msg);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchGenres();
  if (isEdit.value) fetchMovieDetail();
});
</script>

<template>
  <div class="movie-form-page p-4">
    <div class="page-header d-flex justify-content-between align-items-center mb-4">
      <div>
        <el-button :icon="ArrowLeft" link @click="router.back()" class="mb-2">Quay lại</el-button>
        <h3 class="fw-bold text-dark mb-0">
          {{ isEdit ? 'Chỉnh sửa phim: ' + movieForm.tenPhim : 'Thêm phim mới' }}
        </h3>
      </div>
      <div class="d-flex gap-2">
        <el-button @click="router.back()">Hủy bỏ</el-button>
        <el-button type="primary" :icon="Check" @click="handleSave" :loading="loading">
          {{ isEdit ? 'Lưu thay đổi' : 'Tạo phim mới' }}
        </el-button>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-lg-8">
        <el-card shadow="never" class="rounded-4 border-0 shadow-sm">
          <template #header>
            <div class="fw-bold"><i class="bi bi-info-circle me-2"></i>Thông tin cơ bản</div>
          </template>
          <el-form :model="movieForm" label-position="top" class="premium-form">
            <div class="row g-3">
              <div class="col-md-8">
                <el-form-item label="Tên phim *" required>
                  <el-input v-model="movieForm.tenPhim" placeholder="VD: Dune: Part Two" :prefix-icon="Files" size="large" />
                </el-form-item>
              </div>
              <div class="col-md-4">
                <el-form-item label="Mã phim">
                  <el-input v-model="movieForm.maPhim" readonly :prefix-icon="Tickets" size="large">
                    <template #append v-if="!isEdit">
                      <el-button @click="movieForm.maPhim = generateMovieCode()">Tạo mới</el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </div>
              <div class="col-12">
                <el-form-item label="Mô tả nội dung">
                  <el-input v-model="movieForm.moTa" type="textarea" :rows="5" placeholder="Nhập tóm tắt nội dung phim..." />
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Thể loại">
                  <el-select v-model="movieForm.idTheLoais" multiple placeholder="Chọn thể loại" class="w-100" size="large">
                    <el-option v-for="g in genreOptions" :key="g.id" :label="g.tenTheLoai" :value="g.id"/>
                  </el-select>
                </el-form-item>
              </div>
              <div class="col-md-6">
                <el-form-item label="Ngôn ngữ">
                  <el-input v-model="movieForm.ngonNgu" placeholder="VD: Tiếng Anh - Phụ đề Tiếng Việt" size="large" />
                </el-form-item>
              </div>
            </div>
          </el-form>
        </el-card>

        <el-card shadow="never" class="rounded-4 border-0 shadow-sm mt-4">
          <template #header>
            <div class="fw-bold"><i class="bi bi-calendar-event me-2"></i>Lịch chiếu & Thời gian</div>
          </template>
          <div class="row g-3">
            <div class="col-md-6">
              <el-form-item label="Ngày khởi chiếu">
                <el-date-picker v-model="movieForm.ngayKhoiChieu" type="date" class="w-100" value-format="YYYY-MM-DD" size="large" />
              </el-form-item>
            </div>
            <div class="col-md-6">
              <el-form-item label="Ngày kết thúc">
                <el-date-picker v-model="movieForm.ngayKetThuc" type="date" class="w-100" value-format="YYYY-MM-DD" size="large" :disabled-date="disabledDateEnd" />
              </el-form-item>
            </div>
            <div class="col-12">
              <label class="form-label small fw-bold mb-2">Lịch chiếu trong tuần (tối đa 5 thứ)</label>
              <div class="p-3 bg-light rounded-3">
                <el-checkbox-group v-model="movieForm.lichChieu" @change="onLichChieuChange" class="d-flex flex-wrap gap-2">
                  <el-checkbox-button v-for="t in thuOptions" :key="t.value" :value="t.value">{{ t.label }}</el-checkbox-button>
                </el-checkbox-group>
                <div class="text-secondary mt-2 small" v-if="movieForm.lichChieu.length > 0">
                  Đã chọn: <span class="text-primary fw-bold">{{ movieForm.lichChieu.map(t => thuLabels[t]).join(', ') }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="col-lg-4">
        <el-card shadow="never" class="rounded-4 border-0 shadow-sm">
          <template #header>
            <div class="fw-bold"><i class="bi bi-gear-fill me-2"></i>Cấu hình & Phân loại</div>
          </template>
          <el-form :model="movieForm" label-position="top">

            <el-form-item label="Giá phim (VNĐ)">
              <el-input-number v-model="movieForm.giaPhim" :min="0" :step="5000" class="w-100" size="large" controls-position="right" />
            </el-form-item>
            <div class="row g-2">
              <div class="col-6">
                <el-form-item label="Thời lượng (p)">
                  <el-input-number v-model="movieForm.thoiLuong" :min="1" class="w-100" size="large" controls-position="right" />
                </el-form-item>
              </div>
              <div class="col-6">
                <el-form-item label="Độ tuổi">
                  <el-input-number v-model="movieForm.doTuoi" :min="0" :max="18" class="w-100" size="large" controls-position="right" />
                </el-form-item>
              </div>
            </div>
            <el-form-item label="Đánh giá (0-10)">
              <el-input-number v-model="movieForm.danhGia" :min="0" :max="10" :precision="1" :step="0.1" class="w-100" size="large" controls-position="right" />
            </el-form-item>
            
            <el-form-item label="Định dạng phim">
              <el-select v-model="movieForm.loaiPhim" placeholder="Chọn định dạng" class="w-100" size="large">
                <el-option label="2D" value="2D" />
                <el-option label="3D" value="3D" />
                <el-option label="IMAX" value="IMAX" />
                <el-option label="4DX" value="4DX" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="Phụ phí định dạng (VNĐ)">
              <el-input-number v-model="movieForm.phuPhiLoaiPhim" :min="0" :step="5000" class="w-100" size="large" controls-position="right" />
            </el-form-item>
            
            <el-divider content-position="left">Kiểm duyệt độ tuổi</el-divider>
            
            <el-form-item label="Nhãn độ tuổi">
              <el-select v-model="movieForm.nhanDoTuoi" placeholder="Chọn nhãn độ tuổi" class="w-100" size="large">
                <el-option label="P - Tất cả" value="P" />
                <el-option label="T13" value="T13" />
                <el-option label="T16" value="T16" />
                <el-option label="T18" value="T18" />
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-checkbox v-model="movieForm.hienThiCanhBaoDoTuoi" label="Hiển thị cảnh báo độ tuổi trên giao diện khách hàng" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="rounded-4 border-0 shadow-sm mt-4">
          <template #header>
            <div class="fw-bold"><i class="bi bi-image me-2"></i>Hình ảnh & Media</div>
          </template>
          <el-form-item label="Link Poster">
            <el-input v-model="movieForm.poster" placeholder="https://..." size="large" />
            <div v-if="movieForm.poster" class="mt-3 text-center">
              <img :src="movieForm.poster" class="rounded-3 shadow-sm border" style="width: 100%; max-height: 250px; object-fit: cover;" @error="e => e.target.style.display='none'" />
            </div>
          </el-form-item>
          <el-form-item label="Link Trailer (YouTube)">
            <el-input v-model="movieForm.trailer" placeholder="https://..." size="large" />
          </el-form-item>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.movie-form-page {
  background-color: #f8fafc;
  min-height: 100vh;
}
:deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  background-color: #ffffff;
}
.movie-form-page :deep(.el-form-item) {
  margin-bottom: 24px;
}
.movie-form-page :deep(.el-select .el-select__tags) {
  padding: 6px 0;
  display: flex !important;
  flex-wrap: wrap !important;
  gap: 4px;
}
.movie-form-page :deep(.el-select .el-tag) {
  margin: 0 !important;
  height: 28px;
  line-height: 26px;
  font-weight: 600;
}
.movie-form-page :deep(.el-form-item__label) {
  font-weight: 700 !important;
  color: #1e293b !important;
  margin-bottom: 8px !important;
  line-height: 1.2 !important;
}
.movie-form-page :deep(.el-select__wrapper) {
  min-height: 45px;
  height: auto !important;
  padding: 4px 12px;
}
</style>
