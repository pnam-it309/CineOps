<template>
  <div class="homepage">
    <!-- Banner Slider -->
    <section class="banner-section">
      <el-carousel :interval="5000" arrow="always" height="500px" class="banner-carousel">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div 
            class="banner-slide d-flex align-items-center justify-content-center position-relative"
            :style="{ backgroundImage: `url(${banner.image})`, backgroundSize: 'cover', backgroundPosition: 'center' }"
          >
            <div class="banner-overlay position-absolute top-0 start-0 w-100 h-100"></div>
            <div class="banner-content position-relative text-center text-white px-4">
              <h1 class="display-3 fw-bold mb-3">{{ banner.title }}</h1>
              <p class="fs-4 mb-4">{{ banner.subtitle }}</p>
              <el-button 
                v-if="banner.movieId" 
                type="primary" 
                size="large" 
                @click="goToMovie(banner.movieId)"
              >
                Đặt Vé Ngay
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- Quick Booking Widget -->
    <section class="quick-booking-section container-xl position-relative">
      <div class="quick-booking-container bg-white shadow-lg rounded-4 p-4 p-md-3 d-flex flex-column flex-md-row gap-3 align-items-end justify-content-between">
        <div class="flex-fill d-flex flex-column gap-2 w-100">
          <label class="x-small fw-bold text-uppercase text-secondary tracking-wider ms-1">Chọn Phim</label>
          <el-select v-model="quickBooking.movie" placeholder="Chọn phim" size="large" class="w-100 custom-quick-select" filterable>
            <el-option v-for="movie in movies" :key="movie.id" :label="movie.title" :value="movie.id" />
          </el-select>
        </div>
        
        <div class="flex-fill d-flex flex-column gap-2 w-100">
          <label class="x-small fw-bold text-uppercase text-secondary tracking-wider ms-1">Chọn Rạp</label>
          <el-select v-model="quickBooking.cinema" placeholder="Chọn rạp" size="large" class="w-100 custom-quick-select" :disabled="!quickBooking.movie">
            <el-option label="CineOps Central" value="1" />
            <el-option label="CineOps Downtown" value="2" />
            <el-option label="CineOps Mall" value="3" />
          </el-select>
        </div>

        <div class="flex-fill d-flex flex-column gap-2 w-100">
          <label class="x-small fw-bold text-uppercase text-secondary tracking-wider ms-1">Chọn Ngày</label>
          <el-date-picker
            v-model="quickBooking.date"
            type="date"
            placeholder="Chọn ngày"
            size="large"
            class="w-100 custom-quick-select"
            :disabled="!quickBooking.cinema"
            format="DD/MM/YYYY"
          />
        </div>

        <div class="flex-fill d-flex flex-column gap-2 w-100">
          <label class="x-small fw-bold text-uppercase text-secondary tracking-wider ms-1">Suất Chiếu</label>
          <el-select v-model="quickBooking.showtime" placeholder="Suất chiếu" size="large" class="w-100 custom-quick-select" :disabled="!quickBooking.date">
            <el-option label="18:00 - 2D" value="1" />
            <el-option label="20:30 - 2D" value="2" />
            <el-option label="22:45 - 2D" value="3" />
          </el-select>
        </div>

        <el-button type="primary" size="large" class="px-5 fs-6 fw-bold shadow-sm buy-now-btn" :disabled="!quickBooking.showtime" @click="handleQuickBuy">
          MUA VÉ NGAY
        </el-button>
      </div>
    </section>

    <!-- Movies Section -->
    <section class="movies-section container-xl py-5">
      <!-- Section Header -->
      <div class="d-flex justify-content-between align-items-center mb-5 mt-4">
        <div>
          <h2 class="fs-1 fw-bold mb-1 tracking-tight">Phim <span class="text-primary">Đang Chiếu</span></h2>
          <p class="text-secondary small mb-0">Khám phá thế giới điện ảnh đầy màu sắc</p>
        </div>
        
        <!-- Tab Switcher -->
        <el-radio-group v-model="activeTab" size="large" class="modern-toggle">
          <el-radio-button value="now-showing">Đang Chiếu</el-radio-button>
          <el-radio-button value="coming-soon">Sắp Chiếu</el-radio-button>
        </el-radio-group>
      </div>

      <!-- Movies Grid -->
      <el-row :gutter="20">
        <el-col 
          :xs="12" 
          :sm="8" 
          :md="6" 
          :lg="4" 
          v-for="movie in filteredMovies" 
          :key="movie.id"
          class="mb-4"
        >
          <el-card 
            shadow="hover" 
            :body-style="{ padding: '0px' }"
            class="movie-card h-100 cursor-pointer"
            @click="goToMovie(movie.id)"
          >
            <div class="movie-poster position-relative">
              <img :src="movie.poster" :alt="movie.title" class="w-100" style="aspect-ratio: 2/3; object-fit: cover;" />
              <div class="movie-rating position-absolute top-0 end-0 m-2 bg-dark text-white px-2 py-1 rounded">
                <el-icon><Star /></el-icon>
                <span class="ms-1 small fw-bold">{{ movie.imdbRating }}</span>
              </div>
            </div>
            <div class="p-3">
              <h5 class="movie-title mb-2 fw-bold text-truncate">{{ movie.title }}</h5>
              <p class="text-secondary small mb-2">{{ movie.genre }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <span class="badge bg-light text-dark">{{ movie.rating }}</span>
                <span class="text-secondary small">{{ movie.duration }} phút</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Empty State -->
      <el-empty v-if="filteredMovies.length === 0" description="Không có phim nào khả dụng" />
    </section>

    <!-- Promotions Section -->
    <section class="promotions-section bg-light py-5">
      <div class="container-xl">
        <h2 class="fs-2 fw-bold mb-4">Ưu Đãi Đặc Biệt</h2>
        
        <el-row :gutter="20">
          <el-col :xs="24" :md="8" v-for="promo in promotions" :key="promo.id" class="mb-4">
            <el-card shadow="hover" class="h-100 promo-card">
              <img :src="promo.image" :alt="promo.title" class="w-100 rounded mb-3" style="height: 150px; object-fit: cover;" />
              <h4 class="fw-bold mb-2">{{ promo.title }}</h4>
              <p class="text-secondary mb-3">{{ promo.description }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <el-tag type="success">{{ promo.code }}</el-tag>
                <span class="text-secondary small">Có hiệu lực đến {{ formatDate(promo.validUntil) }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>
    
    <!-- News Section -->
    <section class="news-section container-xl py-5">
      <div class="d-flex justify-content-between align-items-end mb-4">
        <div>
          <h2 class="fs-2 fw-bold mb-1">Tin Tức <span class="text-primary">& Sự Kiện</span></h2>
          <p class="text-secondary small mb-0">Cập nhật những thông tin mới nhất từ CineOps</p>
        </div>
        <el-button type="primary" link @click="router.push('/news')" class="mb-1">
          Xem tất cả <el-icon class="ms-1"><ArrowRight /></el-icon>
        </el-button>
      </div>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="8" v-for="news in latestNews" :key="news.id" class="mb-4">
          <div class="news-horizontal-card d-flex gap-3 align-items-center cursor-pointer" @click="router.push(`/news/${news.id}`)">
            <img :src="news.image" class="rounded-3" style="width: 100px; height: 100px; object-fit: cover;">
            <div>
              <div class="d-flex align-items-center gap-1 text-secondary x-small mb-1">
                <el-icon><Calendar /></el-icon>
                <span>{{ news.date }}</span>
              </div>
              <h6 class="fw-bold mb-1 line-clamp-2">{{ news.title }}</h6>
              <p class="text-secondary x-small mb-0 line-clamp-2">{{ news.summary }}</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Star, Calendar, ArrowRight } from '@element-plus/icons-vue';
import { mockMovies, mockBanners, mockPromotions } from '@/mock/movies';

const router = useRouter();
const activeTab = ref('now-showing');

// Mock data
const movies = ref(mockMovies);
const banners = ref(mockBanners);
const promotions = ref(mockPromotions);
const latestNews = ref([
  {
    id: 1,
    title: "Ưu đãi cuối tuần: Giảm giá 20%",
    date: "5 Th2, 2026",
    summary: "Nhận ngay ưu đãi giảm giá 20% cho mỗi vé đặt qua website hoặc ứng dụng di động...",
    image: "https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=600"
  },
  {
    id: 2,
    title: "Sự kiện Fan: Fantastic Four",
    date: "1 Th2, 2026",
    summary: "Những siêu anh hùng tuổi thơ đã trở lại. Tham gia sự kiện fan ngày 15/3 để nhận quà độc quyền!",
    image: "https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=600"
  },
  {
    id: 3,
    title: "Khám phá Thực đơn Mới",
    date: "28 Th1, 2026",
    summary: "Từ Bắp Caramel đến Khoai tây chiên phô mai, hãy xem thực đơn bắp nước mới cập nhật của chúng tôi.",
    image: "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=600"
  }
]);

const quickBooking = ref({
  movie: '',
  cinema: '',
  date: '',
  showtime: ''
});

const handleQuickBuy = () => {
  router.push({ 
    name: 'SelectSeats', 
    params: { showtimeId: quickBooking.value.showtime } 
  });
};

// Filtered movies based on active tab
const filteredMovies = computed(() => {
  return movies.value.filter(movie => movie.status === activeTab.value);
});

// Navigate to movie details
const goToMovie = (movieId) => {
  router.push({ name: 'MovieDetails', params: { id: movieId } });
};

// Format date
const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN', { month: 'short', day: 'numeric', year: 'numeric' });
};
</script>

<style scoped>
.homepage {
  min-height: 100vh;
}

.banner-section {
  width: 100%;
}

.banner-slide {
  height: 500px;
}

.banner-overlay {
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.6));
}

.banner-content {
  z-index: 2;
  max-width: 800px;
}

.movie-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.movie-poster {
  overflow: hidden;
}

.movie-rating {
  backdrop-filter: blur(10px);
  background-color: rgba(0, 0, 0, 0.7) !important;
}

.movie-title {
  font-size: 1rem;
  line-height: 1.3;
}

.cursor-pointer {
  cursor: pointer;
}

.promo-card {
  transition: transform 0.3s ease;
}

.promo-card:hover {
  transform: translateY(-4px);
}

/* Quick Booking Widget */
.quick-booking-section {
  margin-top: -60px;
  z-index: 10;
}

.quick-booking-container {
  border: 1px solid rgba(0,0,0,0.05);
  backdrop-filter: blur(20px);
}

.custom-quick-select :deep(.el-input__wrapper) {
  border-radius: 12px;
  background-color: #f8fafc;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
}

.buy-now-btn {
  height: 54px;
  border-radius: 12px;
  letter-spacing: 0.5px;
}

.modern-toggle :deep(.el-radio-button__inner) {
  border-radius: 50px !important;
  margin: 0 5px;
  border: 1px solid #e2e8f0 !important;
  font-weight: 600;
}

.modern-toggle :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: 1px solid #e2e8f0 !important;
}

/* Utilities */
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.x-small { font-size: 0.7rem; }

.news-horizontal-card {
  transition: all 0.2s ease;
}
.news-horizontal-card:hover {
  transform: translateX(5px);
}
.news-horizontal-card:hover h6 {
  color: var(--el-color-primary);
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

