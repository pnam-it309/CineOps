<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Location, Calendar, Timer } from '@element-plus/icons-vue';
import { mockMovies, mockCinemas, mockShowtimes } from '@/mock/movies';

const route = useRoute();
const router = useRouter();

// State
const selectedMovieId = ref(null);
const selectedCinemaId = ref(null);
const selectedDate = ref(new Date());
const activeDateIndex = ref(0);

// Data
const movie = computed(() => mockMovies.find(m => m.id === selectedMovieId.value) || mockMovies[0]);
const filteredShowtimes = computed(() => {
  return mockShowtimes.filter(s => 
    s.movieId === selectedMovieId.value && 
    (selectedCinemaId.value ? s.cinemaId === selectedCinemaId.value : true)
  );
});

// Generate next 7 days
const dates = computed(() => {
  const d = [];
  for (let i = 0; i < 7; i++) {
    const date = new Date();
    date.setDate(date.getDate() + i);
    d.push({
      day: date.toLocaleDateString('en-US', { weekday: 'short' }),
      date: date.getDate(),
      month: date.toLocaleDateString('en-US', { month: 'short' }),
      full: date
    });
  }
  return d;
});

const selectShowtime = (showtime) => {
  router.push({ name: 'SelectSeats', params: { showtimeId: showtime.id } });
};

onMounted(() => {
  if (route.params.movieId) {
    selectedMovieId.value = parseInt(route.params.movieId);
  } else {
    selectedMovieId.value = mockMovies[0].id;
  }
  selectedCinemaId.value = mockCinemas[0].id;
});
</script>

<template>
  <div class="booking-step container py-5">
    <div class="row">
      <!-- Movie Summary Column -->
      <div class="col-lg-4 mb-4">
        <el-card class="border-0 shadow-sm rounded-4 overflow-hidden p-0">
          <img :src="movie?.poster" class="img-fluid w-100" style="aspect-ratio: 2/3; object-fit: cover;">
          <div class="p-4">
            <h3 class="fw-bold mb-2">{{ movie?.title }}</h3>
            <p class="text-secondary mb-3">{{ movie?.genre }}</p>
            <div class="d-flex gap-2">
              <span class="badge bg-light text-dark px-3">{{ movie?.rating }}</span>
              <span class="badge bg-light text-dark px-3">{{ movie?.duration }} min</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Showtime Selection Column -->
      <div class="col-lg-8">
        <h2 class="fw-bold mb-4">Select Showtime</h2>

        <!-- Step 1: Date Selection -->
        <div class="mb-5">
          <label class="form-label fw-semibold text-secondary mb-3">
            <el-icon class="me-1"><Calendar /></el-icon> Choose Date
          </label>
          <div class="d-flex gap-2 overflow-auto pb-2 scrollbar-hide">
            <div 
              v-for="(d, index) in dates" 
              :key="index"
              class="date-card text-center p-3 rounded-4 cursor-pointer flex-shrink-0"
              :class="activeDateIndex === index ? 'bg-primary text-white shadow-sm' : 'bg-white border border-light shadow-sm'"
              style="min-width: 80px;"
              @click="activeDateIndex = index"
            >
              <div class="small opacity-75">{{ d.day }}</div>
              <div class="fs-4 fw-bold">{{ d.date }}</div>
              <div class="small opacity-75">{{ d.month }}</div>
            </div>
          </div>
        </div>

        <!-- Step 2: Cinema Selection -->
        <div class="mb-5">
          <label class="form-label fw-semibold text-secondary mb-3">
            <el-icon class="me-1"><Location /></el-icon> Choose Cinema
          </label>
          <el-select v-model="selectedCinemaId" placeholder="Select Cinema" size="large" class="w-100">
            <el-option
              v-for="cinema in mockCinemas"
              :key="cinema.id"
              :label="cinema.name + ' - ' + cinema.location"
              :value="cinema.id"
            />
          </el-select>
        </div>

        <!-- Step 3: Available Showtimes -->
        <div class="mb-4">
          <label class="form-label fw-semibold text-secondary mb-3">
            <el-icon class="me-1"><Timer /></el-icon> Available Showtimes
          </label>
          
          <div v-if="filteredShowtimes.length > 0">
            <div class="card bg-light border-0 rounded-4 p-4">
              <div class="row g-3">
                <div 
                  v-for="st in filteredShowtimes" 
                  :key="st.id" 
                  class="col-6 col-sm-4 col-md-3"
                >
                  <button 
                    class="btn btn-white w-100 py-3 border-0 shadow-sm rounded-3 showtime-btn"
                    @click="selectShowtime(st)"
                  >
                    <div class="fw-bold fs-5">{{ st.time }}</div>
                    <div class="small text-secondary fw-semibold">{{ st.format }}</div>
                    <div class="small text-primary mt-1">{{ st.price.toLocaleString() }}đ</div>
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <el-empty v-else description="No showtimes available for the selected criteria." />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.date-card {
  transition: all 0.2s ease;
}

.date-card:hover:not(.bg-primary) {
  border-color: var(--el-color-primary) !important;
  color: var(--el-color-primary);
}

.showtime-btn {
  background: white;
  transition: all 0.2s ease;
}

.showtime-btn:hover {
  background: var(--el-color-primary);
  color: white !important;
  transform: translateY(-2px);
}

.showtime-btn:hover .text-secondary,
.showtime-btn:hover .text-primary {
  color: white !important;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
