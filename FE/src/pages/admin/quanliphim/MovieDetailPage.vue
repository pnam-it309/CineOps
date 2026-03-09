<template>
  <div class="movie-detail-page bg-light min-vh-100 p-4">
    <div class="container-fluid max-width-1200 mx-auto">
      <!-- Header with Back Button -->
      <div class="d-flex align-items-center justify-content-between mb-4">
        <div class="d-flex align-items-center gap-3">
          <el-button @click="router.back()" :icon="Back" circle />
          <div>
            <h3 class="m-0 fw-bold text-dark">Chi tiết phim</h3>
            <p class="text-secondary m-0">Xem thông tin đầy đủ về bộ phim</p>
          </div>
        </div>

      </div>

      <div v-loading="loading" class="movie-content-card shadow-lg border-0 rounded-0 bg-white overflow-hidden">
        <div v-if="movie" class="row g-0">
          <!-- Premium Hero Section -->
          <div class="col-12 profile-hero-banner p-0 text-white position-relative overflow-hidden" 
               style="height: 400px; background: #1e293b;">
            <div class="poster-blur-bg position-absolute inset-0" 
                 :style="{ backgroundImage: `url(${movie.poster})`, backgroundSize: 'cover', backgroundPosition: 'center', filter: 'blur(20px) brightness(0.4)', scale: '1.1' }"></div>
            
            <div class="container-fluid h-100 position-relative" style="z-index: 2;">
              <div class="row h-100 align-items-center px-4">
                <div class="col-md-3 text-center">
                  <div class="poster-hero-card bg-white p-2 shadow-2xl rounded-0 d-inline-block overflow-hidden" style="width: 220px; height: 320px;">
                    <img :src="movie.poster" class="w-100 h-100 object-fit-cover" />
                  </div>
                </div>
                <div class="col-md-7 border-start border-white border-opacity-10 ps-5">
                  <div class="d-flex align-items-center gap-3 mb-3">
                    <el-tag :type="getDoTuoiTagType(movie.nhanDoTuoi)" effect="dark" round size="large" class="fw-bold px-3">
                      {{ movie.nhanDoTuoi }}
                    </el-tag>
                    <span class="badge bg-white bg-opacity-20 font-monospace py-2 px-3">#{{ movie.maPhim }}</span>
                  </div>
                  <h1 class="display-3 fw-bold m-0 lh-1 mb-3">{{ movie.tenPhim }}</h1>
                  
                  <div class="d-flex gap-4 mt-4">
                    <div class="stat-item">
                      <div class="tiny-text opacity-75 mb-1">THỜI LƯỢNG</div>
                      <div class="fw-bold fs-4"><i class="bi bi-clock me-2"></i>{{ movie.thoiLuong }} phút</div>
                    </div>
                    <div class="stat-item border-start border-white border-opacity-20 ps-4">
                      <div class="tiny-text opacity-75 mb-1">ĐÁNH GIÁ</div>
                      <div class="fw-bold fs-4 text-warning"><i class="bi bi-star-fill me-2"></i>{{ movie.danhGia || '0.0' }} / 10</div>
                    </div>
                    <div class="stat-item border-start border-white border-opacity-20 ps-4">
                      <div class="tiny-text opacity-75 mb-1">GIÁ VÉ GỐC</div>
                      <div class="fw-bold fs-4 text-success">{{ movie.giaPhim?.toLocaleString('vi-VN') }}đ</div>
                    </div>
                  </div>
                </div>
                <div class="col-md-2 text-end">
                  <div :class="`status-hero-pill px-4 py-3 border border-width-2 d-inline-block border-white border-opacity-25 bg-white bg-opacity-10` + getTrangThaiClass(movie.trangThai)">
                    <div class="tiny-text opacity-75 mb-1 uppercase-label">TRẠNG THÁI</div>
                    <div class="fw-bold fs-5">{{ getTrangThaiLabel(movie.trangThai).toUpperCase() }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Info Body Section -->
          <div class="col-12 p-5 bg-white">
            <div class="row g-5">
              <!-- Left: Description -->
              <div class="col-lg-8">
                <h5 class="section-title-premium mb-4"><span>Mô tả cốt truyện</span></h5>
                <div class="movie-description-box p-4 bg-light border-start border-4 border-primary text-secondary lh-lg fs-5" style="white-space: pre-wrap;">
                  {{ movie.moTa || 'Hiện chưa có mô tả chi tiết cho bộ phim này.' }}
                </div>

                <div class="mt-5">
                   <h5 class="section-title-premium mb-4"><span>Thông tin sản xuất</span></h5>
                   <div class="production-grid row g-4">
                      <div class="col-sm-6">
                         <div class="p-3 bg-light border">
                            <label class="tiny-text text-secondary mb-1">ĐẠO DIỄN</label>
                            <div class="fw-bold text-dark fs-5">{{ movie.daoDien || 'Đang cập nhật' }}</div>
                         </div>
                      </div>
                      <div class="col-sm-6">
                         <div class="p-3 bg-light border">
                            <label class="tiny-text text-secondary mb-1">DIỄN VIÊN</label>
                            <div class="fw-bold text-dark fs-5">{{ movie.dienVien || 'Đang cập nhật' }}</div>
                         </div>
                      </div>
                      <div class="col-sm-6">
                         <div class="p-3 bg-light border">
                            <label class="tiny-text text-secondary mb-1">QUỐC GIA</label>
                            <div class="fw-bold text-dark fs-5">Việt Nam</div>
                         </div>
                      </div>
                      <div class="col-sm-6">
                         <div class="p-3 bg-light border">
                            <label class="tiny-text text-secondary mb-1">NHÀ SẢN XUẤT</label>
                            <div class="fw-bold text-dark fs-5">CineOps Studios</div>
                         </div>
                      </div>
                   </div>
                </div>
              </div>

              <!-- Right: Metadata Sidebar -->
              <div class="col-lg-4">
                 <div class="sidebar-card p-4 border bg-light sticky-top" style="top: 2rem; z-index: 1;">
                   <h6 class="text-secondary fw-bold small text-uppercase mb-4 letter-spacing-1">Phân loại & Lịch trình</h6>
                   
                   <div class="meta-row mb-4 pb-4 border-bottom">
                     <label class="tiny-text text-secondary d-block mb-2">THỂ LOẠI PHIM</label>
                     <div class="d-flex flex-wrap gap-2">
                        <el-tag v-for="g in movie.theLoais" :key="g.id" effect="dark" square class="rounded-0">{{ g.tenTheLoai }}</el-tag>
                     </div>
                   </div>

                   <div class="meta-row mb-4">
                     <label class="tiny-text text-secondary d-block mb-1">NGÀY CÔNG CHIẾU</label>
                     <div class="fw-bold fs-5 text-dark"><i class="bi bi-calendar-check me-2 text-primary"></i>{{ movie.ngayKhoiChieu || '—' }}</div>
                   </div>

                   <div class="meta-row mb-4">
                     <label class="tiny-text text-secondary d-block mb-1">NGÀY KẾT THÚC</label>
                     <div class="fw-bold fs-5 text-dark"><i class="bi bi-calendar-x me-2 text-danger"></i>{{ movie.ngayKetThuc || '—' }}</div>
                   </div>

                   <div class="meta-row mb-4">
                     <label class="tiny-text text-secondary d-block mb-1">GIỚI HẠN ĐỘ TUỔI</label>
                     <div class="fw-bold fs-5 text-dark"><i class="bi bi-shield-exclamation me-2 text-warning"></i>{{ movie.doTuoi }}+ Tuổi</div>
                   </div>

                   <el-button type="primary" class="w-100 py-3 square" size="large" @click="router.back()">
                     <i class="bi bi-arrow-left me-2"></i> QUAY LẠI DANH SÁCH
                   </el-button>
                 </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else-if="!loading" class="py-5 text-center text-secondary">
           <i class="bi bi-exclamation-circle fs-1"></i>
           <p class="mt-3">Không tìm thấy thông tin phim.</p>
           <el-button type="primary" plain @click="router.back()">Quay lại danh sách</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Back } from '@element-plus/icons-vue';
import { phimApi } from '@/services/api/admin/phimService';
import notification from '@/utils/notifications';

const route = useRoute();
const router = useRouter();
const movie = ref(null);
const loading = ref(false);

const fetchMovieDetail = async () => {
    const id = route.params.id;
    if (!id) return;
    
    loading.value = true;
    try {
        const res = await phimApi.getById(id);
        movie.value = res.data.data;
    } catch (err) {
        notification.error('Lỗi khi tải thông tin phim');
        console.error(err);
    } finally {
        loading.value = false;
    }
};

const getTrangThaiLabel = (tt) => ({ 1: 'Đang chiếu', 2: 'Sắp chiếu', 0: 'Ngừng chiếu' }[tt] ?? '—');
const getTrangThaiClass = (tt) => ({ 1: 'bg-success text-white', 2: 'bg-warning text-dark', 0: 'bg-danger text-white' }[tt] ?? 'bg-secondary text-white');
const getDoTuoiTagType = (nhanDoTuoi) => {
  const types = { 'P': 'success', 'T13': 'warning', 'T16': 'danger', 'T18': 'info' };
  return types[nhanDoTuoi] || 'info';
};



onMounted(fetchMovieDetail);
</script>

<style scoped>
.max-width-1200 { max-width: 1200px; }
.poster-container {
  aspect-ratio: 2/3;
  box-shadow: 0 1rem 3rem rgba(0,0,0,.175)!important;
}
.status-box {
  text-align: center;
  box-shadow: inset 0 0 10px rgba(0,0,0,0.1);
}
.info-card {
  transition: all 0.3s ease;
}
.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.1);
  border-color: var(--el-color-primary) !important;
}
.section-title {
  color: #344767;
}
.movie-description {
  font-size: 1.1rem;
}
</style>
