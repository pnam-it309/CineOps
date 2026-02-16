<template>
  <div class="movie-details">
    <!-- Hero Section with Backdrop -->
    <section 
      class="movie-hero position-relative"
      :style="{ backgroundImage: `url(${movie?.backdrop})`, backgroundSize: 'cover', backgroundPosition: 'center' }"
    >
      <div class="hero-overlay position-absolute top-0 start-0 w-100 h-100"></div>
      <div class="container-xl position-relative py-5">
        <div class="row align-items-center">
          <!-- Movie Poster -->
          <div class="col-md-4 mb-4 mb-md-0">
            <img 
              :src="movie?.poster" 
              :alt="movie?.title" 
              class="w-100 rounded shadow-lg"
              style="max-width: 400px;"
            />
          </div>

          <!-- Movie Info -->
          <div class="col-md-8 text-white">
            <h1 class="display-4 fw-bold mb-3">{{ movie?.title }}</h1>
            
            <div class="d-flex flex-wrap gap-3 mb-3">
              <span class="badge bg-primary fs-6">{{ movie?.rating }}</span>
              <span class="badge bg-warning text-dark fs-6">
                <el-icon><Star /></el-icon> {{ movie?.imdbRating }}
              </span>
              <span class="badge bg-secondary fs-6">{{ movie?.duration }} min</span>
            </div>

            <p class="mb-3"><strong>Thể loại:</strong> {{ movie?.genre }}</p>
            <p class="mb-3"><strong>Đạo diễn:</strong> {{ movie?.director }}</p>
            <p class="mb-3"><strong>Ngày khởi chiếu:</strong> {{ formatDate(movie?.releaseDate) }}</p>
            
            <p class="lead mb-4 lh-base opacity-90">{{ movie?.synopsis }}</p>

            <div class="d-flex gap-3">
              <el-button type="primary" size="large" @click="bookNow" class="px-4 py-3 fs-6 shadow-sm" round>
                <el-icon><Tickets /></el-icon>
                <span class="ms-2">Đặt vé ngay</span>
              </el-button>
              <el-button type="default" size="large" @click="playTrailer" class="px-4 py-3 fs-6 shadow-sm" round>
                <el-icon><VideoPlay /></el-icon>
                <span class="ms-2">Xem Trailer</span>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Cast Section -->
    <section class="cast-section bg-light py-5">
      <div class="container-xl">
        <h2 class="fs-3 fw-bold mb-5 tracking-tight">Diễn viên <span class="text-primary">& Đoàn làm phim</span></h2>
        <el-row :gutter="25">
          <el-col 
            :xs="12" 
            :sm="6" 
            :md="4" 
            :lg="3" 
            v-for="(actor, index) in movie?.cast" 
            :key="index"
            class="mb-4"
          >
            <div class="cast-card text-center p-4 bg-white rounded-4 shadow-sm border border-light h-100 transition-all">
              <el-avatar :size="90" class="mb-3 cast-avatar shadow-sm">
                <el-icon :size="40"><User /></el-icon>
              </el-avatar>
              <h6 class="fw-bold text-dark mb-1">{{ actor }}</h6>
              <span class="text-secondary x-small text-uppercase tracking-wider">Diễn viên</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- Reviews Section -->
    <section class="reviews-section py-5">
      <div class="container-xl">
        <div class="row mb-5">
          <div class="col-md-4">
            <h2 class="fs-2 fw-bold mb-3 tracking-tight">Đánh giá <span class="text-primary">từ khán giả</span></h2>
            <div class="rating-summary-card p-4 bg-light rounded-4 border text-center mb-4">
              <div class="display-3 fw-bold text-dark mb-1">4.8</div>
              <el-rate :model-value="4.8" disabled show-score text-color="#ff9900" class="mb-2" />
              <div class="text-secondary small">(1,248 nhận xét)</div>
            </div>
          </div>
          <div class="col-md-8 d-flex align-items-end justify-content-md-end">
            <el-button type="primary" size="large" @click="reviewDialogVisible = true" round class="px-4 shadow-sm">
              <el-icon class="me-2"><Edit /></el-icon>
              Viết đánh giá của bạn
            </el-button>
          </div>
        </div>

        <!-- Reviews List -->
        <div class="row g-4">
          <div class="col-md-6" v-for="review in mockReviews" :key="review.id">
            <el-card shadow="never" class="h-100 border-0 bg-light rounded-4 review-item-card">
              <div class="d-flex justify-content-between align-items-start mb-3">
                <div class="d-flex align-items-center gap-3">
                  <el-avatar :size="48" class="bg-primary text-white fs-5 fw-bold shadow-sm">
                    {{ review.userName.charAt(0) }}
                  </el-avatar>
                  <div>
                    <h6 class="fw-bold text-dark mb-1 fs-6">{{ review.userName }}</h6>
                    <el-rate v-model="review.rating" disabled text-color="#ff9900" />
                  </div>
                </div>
                <span class="text-secondary-light small mt-1">{{ formatDate(review.date) }}</span>
              </div>
              <p class="text-dark-subtle lh-lg mb-0 font-italic">"{{ review.comment }}"</p>
            </el-card>
          </div>
        </div>

        <!-- Empty State -->
        <el-empty v-if="mockReviews.length === 0" description="Chưa có đánh giá nào. Hãy là người đầu tiên!" />
      </div>
    </section>

    <!-- Review Dialog -->
    <el-dialog v-model="reviewDialogVisible" title="Cảm nhận của bạn về bộ phim" width="600px" custom-class="custom-review-dialog" round>
      <el-form :model="reviewForm" label-position="top">
        <el-form-item label="Điểm số của bạn">
          <el-rate v-model="reviewForm.rating" show-text :texts="['Tệ', 'Trung bình', 'Khá', 'Tốt', 'Tuyệt vời']" />
        </el-form-item>
        <el-form-item label="Bình luận">
          <el-input 
            v-model="reviewForm.comment" 
            type="textarea" 
            :rows="5" 
            placeholder="Bạn thấy bộ phim này thế nào? Chia sẻ cùng mọi người nhé..."
            class="custom-textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="reviewDialogVisible = false" round>Hủy bỏ</el-button>
          <el-button type="primary" @click="submitReview" round class="px-4">Gửi đánh giá</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Trailer Dialog -->
    <el-dialog v-model="trailerDialogVisible" title="Trailer" width="800px" @close="stopTrailer">
      <div class="ratio ratio-16x9">
        <iframe 
          :src="trailerEmbedUrl" 
          frameborder="0" 
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
          allowfullscreen
        ></iframe>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Star, Tickets, VideoPlay, User, Edit } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { mockMovies } from '@/mock';

const route = useRoute();
const router = useRouter();

// State
const movie = ref(null);
const reviewDialogVisible = ref(false);
const trailerDialogVisible = ref(false);
const reviewForm = ref({
  rating: 5,
  comment: ''
});

// Mock reviews
const mockReviews = ref([
  {
    id: 1,
    userName: 'John Doe',
    rating: 5,
    comment: 'Amazing movie! The visuals were stunning and the story was captivating.',
    date: '2024-02-01'
  },
  {
    id: 2,
    userName: 'Jane Smith',
    rating: 4,
    comment: 'Great movie overall. Some parts were a bit slow but definitely worth watching.',
    date: '2024-02-03'
  },
  {
    id: 3,
    userName: 'Mike Johnson',
    rating: 5,
    comment: 'Absolutely loved it! Best movie I\'ve seen this year.',
    date: '2024-02-05'
  },
  {
    id: 4,
    userName: 'Sarah Williams',
    rating: 4,
    comment: 'Excellent cinematography and performances. Highly recommended!',
    date: '2024-02-06'
  }
]);

// Computed
const trailerEmbedUrl = computed(() => {
  if (!movie.value?.trailer) return '';
  // Convert YouTube watch URL to embed URL
  const videoId = movie.value.trailer.split('v=')[1];
  return `https://www.youtube.com/embed/${videoId}`;
});

// Methods
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
};

const playTrailer = () => {
  trailerDialogVisible.value = true;
};

const stopTrailer = () => {
  // Stop video when dialog closes
  trailerDialogVisible.value = false;
};

const bookNow = () => {
  // Navigate to booking flow (Step 1: Showtime selection)
  router.push({ name: 'SelectShowtime', params: { movieId: movie.value.id } });
};

const submitReview = () => {
  if (!reviewForm.value.comment.trim()) {
    ElMessage.warning('Please write a review comment');
    return;
  }
  
  // Add review to list (mock)
  mockReviews.value.unshift({
    id: mockReviews.value.length + 1,
    userName: 'You',
    rating: reviewForm.value.rating,
    comment: reviewForm.value.comment,
    date: new Date().toISOString().split('T')[0]
  });
  
  ElMessage.success('Review submitted successfully!');
  reviewDialogVisible.value = false;
  reviewForm.value = { rating: 5, comment: '' };
};

// Load movie data
onMounted(() => {
  const movieId = parseInt(route.params.id);
  movie.value = mockMovies.find(m => m.id === movieId);
  
  if (!movie.value) {
    ElMessage.error('Movie not found');
    router.push({ name: 'Home' });
  }
});
</script>

<style scoped>
.movie-hero {
  min-height: 500px;
}

.hero-overlay {
  background: linear-gradient(to right, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.7) 50%, rgba(0,0,0,0.4) 100%);
}

.movie-details {
  background-color: #fff;
}

.cast-card:hover {
  transform: translateY(-5px);
  border-color: var(--el-color-primary-light-7) !important;
}

.review-item-card {
  transition: all 0.3s ease;
}

.review-item-card:hover {
  background-color: #f1f5f9 !important;
  transform: scale(1.02);
}

.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.x-small { font-size: 0.7rem; }
.opacity-90 { opacity: 0.9; }
.transition-all { transition: all 0.3s ease; }

:deep(.custom-textarea .el-textarea__inner) {
  border-radius: 12px;
  padding: 15px;
  background-color: #f8fafc;
}
</style>
