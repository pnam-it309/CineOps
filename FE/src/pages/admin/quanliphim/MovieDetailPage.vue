<template>
  <div class="form-page-container">
    <div v-loading="loading">
      <!-- Universal Form Header -->
      <div class="form-header-modern">
        <div class="d-flex flex-column">
          <el-button @click="goBack" class="form-header-back-btn">
            <el-icon><Back /></el-icon> Quay lại danh sách
          </el-button>
          <h3 class="form-header-title">Chi tiết phim</h3>
        </div>
      </div>

      <div v-if="movie" class="row">
        <!-- Left Column: Poster & Major Stats -->
        <div class="col-lg-3 mb-4">
          <div class="form-card-premium p-4 text-center border h-100">
            <div class="poster-container-simple mb-4 mx-auto overflow-hidden rounded-3 shadow-sm border" style="width: 100%; max-width: 240px; aspect-ratio: 2/3;">
              <img :src="movie.poster" class="w-100 h-100 object-fit-cover" alt="Poster" />
            </div>
            
            <div class="info-group-vertical text-start px-2">
              <div class="info-item mb-3">
                <label class="info-label-muted">MÃ PHIM</label>
                <div class="info-value-strong text-uppercase">#{{ movie.maPhim }}</div>
              </div>
              <div class="info-item mb-3">
                <label class="info-label-muted">TÊN PHIM</label>
                <div class="info-value-strong fs-5 lh-sm">{{ movie.tenPhim }}</div>
              </div>
              <div class="info-item mb-3">
                <label class="info-label-muted">GIÁ VÉ GỐC</label>
                <div class="info-value-strong text-danger fs-6">{{ movie.giaPhim?.toLocaleString('vi-VN') }} VNĐ</div>
              </div>
              <div class="info-item">
                <label class="info-label-muted">TRẠNG THÁI</label>
                <div class="mt-1">
                  <el-tag :type="getTrangThaiTag(movie.trangThai)" effect="light" class="fw-bold border-0">{{ getTrangThaiLabel(movie.trangThai) }}</el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Column: Details with Tabs -->
        <div class="col-lg-9">
          <el-tabs v-model="activeTab" class="premium-tabs-container">
            <!-- TAB 1: THÔNG TIN CHI TIẾT -->
            <el-tab-pane label="Thông tin phim" name="detail">
              <div class="form-card-premium p-4 border mb-0">
                <h5 class="section-title-clean mb-4 border-bottom pb-2">Hồ sơ nội dung</h5>
                <div class="row g-4 mb-4">
                  <div class="col-md-4 col-sm-6">
                    <label class="info-label-muted">THỜI LƯỢNG</label>
                    <div class="info-value">{{ movie.thoiLuong }} phút</div>
                  </div>
                  <div class="col-md-4 col-sm-6">
                    <label class="info-label-muted">ĐỊNH DẠNG</label>
                    <div class="info-value">{{ movie.loaiPhim || '2D' }}</div>
                  </div>
                  <div class="col-md-4 col-sm-6">
                    <label class="info-label-muted">NGÔN NGỮ</label>
                    <div class="info-value">{{ movie.ngonNgu || 'Đang cập nhật' }}</div>
                  </div>
                  <div class="col-md-4 col-sm-6">
                    <label class="info-label-muted">ĐỘ TUỔI</label>
                    <div class="info-value">
                      <el-tag :type="getDoTuoiTagType(movie.nhanDoTuoi)" round size="small" class="me-2">{{ movie.nhanDoTuoi }}</el-tag>
                      {{ movie.doTuoi }}+
                    </div>
                  </div>
                  <div class="col-md-4 col-sm-6">
                    <label class="info-label-muted">ĐÁNH GIÁ</label>
                    <div class="info-value fw-bold text-warning">⭐ {{ movie.danhGia || '0.0' }}</div>
                  </div>
                  <div class="col-md-4 col-sm-6">
                    <label class="info-label-muted">NGÀY CÔNG CHIẾU</label>
                    <div class="info-value">{{ movie.ngayKhoiChieu || '—' }}</div>
                  </div>
                </div>

                <div class="mb-4">
                  <label class="info-label-muted">THỂ LOẠI</label>
                  <div class="d-flex flex-wrap gap-2 mt-1">
                    <el-tag v-for="g in movie.theLoais" :key="g.id" class="border-red-light bg-red-white text-red-cine">{{ g.tenTheLoai }}</el-tag>
                    <span v-if="!movie.theLoais?.length" class="text-muted small italic">Chưa xác định</span>
                  </div>
                </div>

                <div>
                  <label class="info-label-muted">MÔ TẢ</label>
                  <div class="info-value-description lh-lg text-secondary">
                    {{ movie.moTa || 'Hiện chưa có mô tả chi tiết cho bộ phim này.' }}
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <!-- TAB 2: SUẤT CHIẾU -->
            <el-tab-pane name="showtimes">
              <template #label>
                <span>Suất chiếu <el-badge v-if="showtimes.length" :value="showtimes.length" class="ms-1" type="danger" is-dot /></span>
              </template>
              
              <div class="form-card-premium p-4 border h-100">
                <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                  <h5 class="section-title-clean m-0">Lịch chiếu của phim</h5>
                  <el-button type="primary" size="small" @click="goToCreateShowtime">
                    <el-icon class="me-1"><Plus /></el-icon> Lên lịch mới
                  </el-button>
                </div>

                <el-table :data="showtimes" v-loading="loadingShowtimes" border stripe style="width: 100%" max-height="400">
                  <el-table-column label="Ngày chiếu" prop="ngayChieu" width="120">
                    <template #default="{row}">
                      <div class="fw-bold">{{ row.ngayChieu }}</div>
                    </template>
                  </el-table-column>
                  <el-table-column label="Giờ" prop="gioBatDau" width="280">
                    <template #default="{row}">
                      <el-tag size="small" effect="plain" type="info">{{ row.gioBatDau }} - {{ row.gioKetThuc }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="Phòng" prop="tenPhongChieu" />
                  <el-table-column label="Ghế trống" prop="soGheTrong" width="120">
                    <template #default="{row}">
                      <span :class="row.soGheTrong < 10 ? 'text-danger fw-bold' : ''">{{ row.soGheTrong }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Thao tác" width="100" align="center">
                    <template #default="{row}">
                      <el-tooltip content="Xem sơ đồ ghế" placement="top">
                        <el-button type="info" size="small" circle @click="openSeatMap(row)">
                          <i class="bi bi-grid-3x3-gap"></i>
                        </el-button>
                      </el-tooltip>
                    </template>
                  </el-table-column>
                </el-table>
                <div v-if="!showtimes.length && !loadingShowtimes" class="text-center py-5">
                   <el-empty description="Phim này chưa có suất chiếu nào" :image-size="100" />
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <div v-else-if="!loading" class="text-center py-5">
         <el-empty description="Không tìm thấy thông tin phim" />
         <el-button @click="goBack">Trở về danh sách</el-button>
      </div>
    </div>

    <!-- ══════════════════════════════════════════════════
         MODAL: Sơ đồ ghế Real-time (Consistent with Admin)
         ══════════════════════════════════════════════════ -->
    <BaseModal v-model="seatMapVisible" title="Sơ đồ ghế Real-time" icon="bi bi-grid-3x3-gap-fill" width="950px" isDetail onlyCancel>
      <div v-if="selectedShowtime" class="p-4 bg-light-map">
        <!-- Header Map (Light Theme) -->
        <div class="d-flex justify-content-between align-items-center mb-5 px-3">
          <div>
            <h4 class="fw-bold mb-1 text-dark">{{ movie.tenPhim }}</h4>
            <div class="text-secondary fw-medium">
              <i class="bi bi-door-open me-1"></i> {{ selectedShowtime.tenPhongChieu }} 
              <span class="mx-2">|</span>
              <i class="bi bi-calendar3 me-1"></i> {{ selectedShowtime.ngayChieu }} 
              <span class="mx-2">|</span>
              <i class="bi bi-clock me-1"></i> {{ selectedShowtime.gioBatDau }}
            </div>
          </div>
          <div class="d-flex gap-4">
            <div class="text-center px-3 py-2 border rounded bg-white shadow-sm">
              <div class="h4 mb-0 fw-bold text-success">{{ selectedShowtime.soGheTrong }}</div>
              <div class="smaller text-secondary fw-bold">TRỐNG</div>
            </div>
            <div class="text-center px-3 py-2 border rounded bg-white shadow-sm">
              <div class="h4 mb-0 fw-bold text-danger">{{ (selectedShowtime.tongGhe || 0) - (selectedShowtime.soGheTrong || 0) }}</div>
              <div class="smaller text-secondary fw-bold">ĐÃ BÁN</div>
            </div>
          </div>
        </div>

        <!-- Screen -->
        <div class="screen-container mb-5">
          <div class="screen-line"></div>
          <div class="text-center smaller fw-bold text-secondary mt-2 tracking-widest">MÀN HÌNH</div>
        </div>

        <!-- Seat Grid -->
        <div class="seat-grid-container py-4" v-loading="loadingSeats">
          <div v-for="(rowSeats, rowName) in groupedSeats" :key="rowName" class="seat-row d-flex justify-content-center gap-2 mb-3">
            <div class="row-label text-secondary fw-bold">{{ rowName }}</div>
            <div v-for="seat in rowSeats" :key="seat.id" 
                 class="admin-seat" 
                 :class="[seat.loaiGhe?.toLowerCase(), seat.trangThai === 1 ? 'sold' : 'available']">
              <el-tooltip :content="`${seat.soGhe} - ${seat.loaiGhe} (${seat.trangThai === 1 ? 'Đã bán' : 'Trống'})`" placement="top">
                <span class="seat-num">{{ seat.cot }}</span>
              </el-tooltip>
            </div>
            <div class="row-label text-secondary fw-bold">{{ rowName }}</div>
          </div>

          <!-- Empty State -->
          <div v-if="!loadingSeats && Object.keys(groupedSeats).length === 0" class="text-center py-5">
            <el-empty description="Phòng chiếu này chưa được cấu hình sơ đồ ghế" />
          </div>
        </div>

        <!-- Legend (Clean) -->
        <div class="mt-4 d-flex justify-content-center gap-5 py-4 border-top bg-light-subtle rounded-3">
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat available sm shadow-none"></div> <span class="small fw-bold text-dark">Ghế trống</span>
          </div>
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat sold sm shadow-none"></div> <span class="small fw-bold text-dark">Đã bán</span>
          </div>
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat vip sm available shadow-none"></div> <span class="small fw-bold text-dark">Ghế VIP</span>
          </div>
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat couple sm available shadow-none" style="width: 30px;"></div> <span class="small fw-bold text-dark">Ghế Couple</span>
          </div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Back, Plus } from '@element-plus/icons-vue';
import { phimApi } from '@/services/api/admin/phimService';
import { suatChieuService } from '@/services/api/admin/suatChieuService';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import BaseModal from '@/components/common/BaseModal.vue';
import notification from '@/utils/notifications';

const route = useRoute();
const router = useRouter();
const movie = ref(null);
const loading = ref(false);

// Showtime states
const activeTab = ref('detail');
const showtimes = ref([]);
const loadingShowtimes = ref(false);

// Seat map states
const seatMapVisible = ref(false);
const loadingSeats = ref(false);
const currentSeats = ref([]);
const selectedShowtime = ref(null);

const openSeatMap = async (row) => {
  selectedShowtime.value = row;
  seatMapVisible.value = true;
  loadingSeats.value = true;
  try {
    const res = await suatChieuService.getGheStatus(row.id);
    currentSeats.value = res.data?.data || [];
  } catch (err) {
    notification.error('Không thể tải sơ đồ ghế');
  } finally {
    loadingSeats.value = false;
  }
};

const groupedSeats = computed(() => {
  const groups = {};
  currentSeats.value.forEach(s => {
    if (!groups[s.hang]) groups[s.hang] = [];
    groups[s.hang].push(s);
  });
  Object.keys(groups).forEach(key => {
    groups[key].sort((a, b) => a.cot - b.cot);
  });
  return groups;
});

const fetchMovieDetail = async () => {
    const id = route.params.id;
    if (!id) return;
    
    loading.value = true;
    try {
        const res = await phimApi.getById(id);
        movie.value = res.data.data;
        // After fetching movie, fetch its showtimes
        await fetchShowtimes(id);
    } catch (err) {
        notification.error('Lỗi khi tải thông tin phim');
        console.error(err);
    } finally {
        loading.value = false;
    }
};

const fetchShowtimes = async (movieObjectId) => {
    loadingShowtimes.value = true;
    try {
        // We filter by idPhim
        const res = await suatChieuService.getShowtimes({ idPhim: movieObjectId });
        if (res.data && res.data.data) {
            // If it's a page object (Spring Data)
            showtimes.value = res.data.data.content || res.data.data;
        }
    } catch (err) {
        console.error('Error fetching showtimes:', err);
    } finally {
        loadingShowtimes.value = false;
    }
};

const goToCreateShowtime = () => {
    router.push({ 
        name: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.name,
        query: { movieId: movie.value.id, movieName: movie.value.tenPhim }
    });
};

const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push({ name: ROUTES_CONSTANTS.ADMIN.children.MOVIES.name });
  }
};

const getTrangThaiLabel = (tt) => ({ 1: 'Đang chiếu', 2: 'Sắp chiếu', 0: 'Ngừng chiếu' }[tt] ?? '—');
const getTrangThaiTag = (tt) => ({ 1: 'success', 2: 'warning', 0: 'danger' }[tt] ?? 'info');
const getDoTuoiTagType = (nhanDoTuoi) => {
  const types = { 'P': 'success', 'T13': 'warning', 'T16': 'danger', 'T18': 'info' };
  return types[nhanDoTuoi] || 'info';
};

onMounted(fetchMovieDetail);
</script>

<style scoped>
.info-label-muted {
  display: block;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.info-value {
  font-size: 15px;
  color: #334155;
  font-weight: 500;
}

.info-value-strong {
  font-weight: 800;
  color: #1e293b;
}

.info-value-description {
  font-size: 14px;
  line-height: 1.6;
  color: #475569;
  background: #f8fafc;
  padding: 15px;
  border-radius: 8px;
  border: 1px dashed #e2e8f0;
}

.section-title-clean {
  font-size: 16px;
  font-weight: 800;
  color: #1e293b;
  position: relative;
}

/* Tabs Styling */
.premium-tabs-container :deep(.el-tabs__header) {
  margin-bottom: 20px;
  background: #fff;
  padding: 0 10px;
  border-radius: 8px 8px 0 0;
}

.premium-tabs-container :deep(.el-tabs__item) {
  font-weight: 700;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.5px;
  height: 50px;
}

.premium-tabs-container :deep(.el-tabs__active-bar) {
  background-color: #E31E24;
  height: 3px;
}

.premium-tabs-container :deep(.el-tabs__item.is-active) {
  color: #E31E24;
}

.border-red-light {
  border-color: #fee2e2 !important;
}

.bg-red-white {
  background-color: #fef2f2 !important;
}

.text-red-cine {
  color: #E31E24 !important;
  font-weight: 700;
}

:deep(.el-table) {
  --el-table-header-bg-color: #f8fafc;
  --el-table-header-text-color: #475569;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 800;
  text-transform: uppercase;
  font-size: 11px;
}

:deep(.el-tag--light) {
  border: none;
}

/* ──────────────────────────────────────────────────
   STYLE: Seat Map Real-time (Light Clean & Clear)
   ────────────────────────────────────────────────── */
.bg-light-map {
  background: #ffffff;
  border-radius: 0 0 16px 16px;
  min-height: 480px;
  border: 1px solid #f1f5f9;
}

.screen-container { width: 100%; padding: 0 15%; }
.screen-line {
  height: 8px;
  background: #e2e8f0;
  border-radius: 20px;
}

.row-label {
  width: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 14px;
}

.admin-seat {
  width: 42px;
  height: 42px;
  background: #ffffff;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #475569;
  cursor: default;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.admin-seat.sm { width: 22px; height: 22px; font-size: 0; pointer-events: none; border-width: 1px; }

.admin-seat.available { border-color: #cbd5e1; }
.admin-seat.available:hover {
  transform: translateY(-3px);
  border-color: #3b82f6;
  color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.admin-seat.sold {
  background: #f1f5f9 !important;
  border-color: #e2e8f0 !important;
  color: #cbd5e1 !important;
  position: relative;
}
.admin-seat.sold::after {
  content: '✕';
  position: absolute;
  font-size: 18px;
  opacity: 0.4;
}

.admin-seat.vip.available { border-color: #f59e0b; color: #b45309; }
.admin-seat.vip.available:hover { border-color: #f59e0b; box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15); }

.admin-seat.couple.available { border-color: #ec4899; color: #be185d; width: 92px; }
.admin-seat.couple.available:hover { border-color: #ec4899; box-shadow: 0 4px 12px rgba(236, 72, 153, 0.15); }

.bg-light-subtle {
  background-color: #f8fafc !important;
}
</style>
