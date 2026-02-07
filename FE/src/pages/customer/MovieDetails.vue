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

            <p class="mb-3"><strong>Genre:</strong> {{ movie?.genre }}</p>
            <p class="mb-3"><strong>Director:</strong> {{ movie?.director }}</p>
            <p class="mb-3"><strong>Release Date:</strong> {{ formatDate(movie?.releaseDate) }}</p>
            
            <p class="lead mb-4">{{ movie?.synopsis }}</p>

            <div class="d-flex gap-3">
              <el-button type="primary" size="large" @click="bookNow">
                <el-icon><Tickets /></el-icon>
                <span class="ms-2">Book Tickets</span>
              </el-button>
              <el-button type="default" size="large" @click="playTrailer">
                <el-icon><VideoPlay /></el-icon>
                <span class="ms-2">Watch Trailer</span>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Cast Section -->
    <section class="cast-section bg-light py-5">
      <div class="container-xl">
        <h2 class="fs-3 fw-bold mb-4">Cast & Crew</h2>
        <el-row :gutter="20">
          <el-col 
            :xs="12" 
            :sm="6" 
            :md="4" 
            :lg="3" 
            v-for="(actor, index) in movie?.cast" 
            :key="index"
            class="mb-4"
          >
            <el-card shadow="hover" class="text-center h-100">
              <el-avatar :size="80" class="mb-3">
                <el-icon><User /></el-icon>
              </el-avatar>
              <h6 class="fw-bold mb-0">{{ actor }}</h6>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- Reviews Section -->
    <section class="reviews-section py-5">
      <div class="container-xl">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2 class="fs-3 fw-bold mb-0">Customer Reviews</h2>
          <el-button type="primary" @click="reviewDialogVisible = true">
            <el-icon><Edit /></el-icon>
            <span class="ms-2">Write Review</span>
          </el-button>
        </div>

        <!-- Reviews List -->
        <div class="row">
          <div class="col-md-6 mb-4" v-for="review in mockReviews" :key="review.id">
            <el-card shadow="hover">
              <div class="d-flex justify-content-between align-items-start mb-3">
                <div class="d-flex align-items-center gap-3">
                  <el-avatar :size="40">{{ review.userName.charAt(0) }}</el-avatar>
                  <div>
                    <h6 class="fw-bold mb-1">{{ review.userName }}</h6>
                    <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" />
                  </div>
                </div>
                <span class="text-secondary small">{{ formatDate(review.date) }}</span>
              </div>
              <p class="mb-0">{{ review.comment }}</p>
            </el-card>
          </div>
        </div>

        <!-- Empty State -->
        <el-empty v-if="mockReviews.length === 0" description="No reviews yet. Be the first to review!" />
      </div>
    </section>

    <!-- Review Dialog -->
    <el-dialog v-model="reviewDialogVisible" title="Write a Review" width="600px">
      <el-form :model="reviewForm" label-position="top">
        <el-form-item label="Rating">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
        <el-form-item label="Your Review">
          <el-input 
            v-model="reviewForm.comment" 
            type="textarea" 
            :rows="4" 
            placeholder="Share your thoughts about the movie..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitReview">Submit Review</el-button>
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
import { mockMovies } from '@/mock/movies';

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
  background: linear-gradient(to right, rgba(0,0,0,0.9), rgba(0,0,0,0.6), rgba(0,0,0,0.3));
}

.movie-details {
  background-color: #fff;
}
</style>
