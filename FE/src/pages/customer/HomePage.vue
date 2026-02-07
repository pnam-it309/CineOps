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
                Book Now
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- Movies Section -->
    <section class="movies-section container-xl py-5">
      <!-- Section Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fs-2 fw-bold mb-0">Movies</h2>
        
        <!-- Tab Switcher -->
        <el-radio-group v-model="activeTab" size="default">
          <el-radio-button label="now-showing">Now Showing</el-radio-button>
          <el-radio-button label="coming-soon">Coming Soon</el-radio-button>
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
                <span class="text-secondary small">{{ movie.duration }} min</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Empty State -->
      <el-empty v-if="filteredMovies.length === 0" description="No movies available" />
    </section>

    <!-- Promotions Section -->
    <section class="promotions-section bg-light py-5">
      <div class="container-xl">
        <h2 class="fs-2 fw-bold mb-4">Special Offers</h2>
        
        <el-row :gutter="20">
          <el-col :xs="24" :md="8" v-for="promo in promotions" :key="promo.id" class="mb-4">
            <el-card shadow="hover" class="h-100 promo-card">
              <img :src="promo.image" :alt="promo.title" class="w-100 rounded mb-3" style="height: 150px; object-fit: cover;" />
              <h4 class="fw-bold mb-2">{{ promo.title }}</h4>
              <p class="text-secondary mb-3">{{ promo.description }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <el-tag type="success">{{ promo.code }}</el-tag>
                <span class="text-secondary small">Valid until {{ formatDate(promo.validUntil) }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Star } from '@element-plus/icons-vue';
import { mockMovies, mockBanners, mockPromotions } from '@/mock/movies';

const router = useRouter();
const activeTab = ref('now-showing');

// Mock data
const movies = ref(mockMovies);
const banners = ref(mockBanners);
const promotions = ref(mockPromotions);

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
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
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
</style>
