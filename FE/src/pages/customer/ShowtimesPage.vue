<template>
  <div class="showtimes-page pb-5">
    <div class="header-section text-center py-5 bg-dark text-white mb-5 rounded-4 shadow-sm" style="background: linear-gradient(rgba(0,0,0,0.6), rgba(0,0,0,0.6)), url('https://images.unsplash.com/photo-1485846234645-a62644ffb4d7?auto=format&fit=crop&w=1200') center/cover;">
      <h1 class="display-4 fw-bold mb-3 tracking-tight">Lịch Chiếu <span class="text-primary-light">Toàn Hệ Thống</span></h1>
      <p class="lead opacity-75">Chọn thời gian phù hợp nhất với bạn</p>
    </div>

    <div class="container overflow-hidden">
      <!-- Date Picker -->
      <div class="date-selector-wrapper mb-5 overflow-auto pb-3">
        <div class="d-flex gap-3 justify-content-center" style="min-width: 800px;">
          <div 
            v-for="date in dates" 
            :key="date.full"
            class="date-card p-3 rounded-4 shadow-sm border cursor-pointer text-center transition-all bg-white"
            :class="{'active-date bg-primary text-white border-primary shadow-lg': selectedDate === date.full}"
            @click="selectedDate = date.full"
            style="width: 100px;"
          >
            <div class="x-small text-uppercase fw-bold tracking-wider mb-1 opacity-75">{{ date.weekday }}</div>
            <div class="fs-4 fw-bold">{{ date.day }}</div>
            <div class="x-small opacity-75">{{ date.month }}</div>
          </div>
        </div>
      </div>

      <!-- Cinema Selection -->
      <div class="row mb-5">
        <div class="col-md-4 mx-auto">
          <label class="x-small fw-bold text-uppercase text-secondary tracking-wider ms-1 mb-2">Chọn Rạp Chiếu</label>
          <el-select v-model="selectedCinema" placeholder="Tất cả các rạp" size="large" class="w-100 custom-select" clearable>
            <el-option label="CineOps Central" value="1" />
            <el-option label="CineOps Downtown" value="2" />
            <el-option label="CineOps Mall" value="3" />
          </el-select>
        </div>
      </div>

      <!-- Movie Schedule List -->
      <div class="movie-schedules">
        <el-card v-for="movie in movies" :key="movie.id" shadow="hover" class="mb-5 border-0 rounded-4 overflow-hidden shadow-sm schedule-movie-card">
          <div class="row g-0">
            <div class="col-md-3">
              <img :src="movie.poster" class="w-100 h-100 object-fit-cover" style="min-height: 250px;">
            </div>
            <div class="col-md-9 p-4">
              <div class="d-flex justify-content-between align-items-start mb-3">
                <div>
                  <h3 class="fs-4 fw-bold text-dark mb-2">{{ movie.title }}</h3>
                  <div class="d-flex gap-2">
                    <span class="badge bg-light text-dark">{{ movie.rating }}</span>
                    <span class="text-secondary small">{{ movie.duration }} phút | {{ movie.genre }}</span>
                  </div>
                </div>
                <el-button type="primary" plain round size="default" @click="$router.push({ name: 'MovieDetails', params: { id: movie.id } })">Chi tiết phim</el-button>
              </div>

              <div class="showtime-groups">
                <div v-for="format in movie.formats" :key="format.name" class="mb-4">
                  <h6 class="fw-bold text-secondary x-small text-uppercase tracking-wider mb-3">{{ format.name }}</h6>
                  <div class="d-flex flex-wrap gap-2">
                    <div 
                      v-for="time in format.times" 
                      :key="time"
                      class="time-slot px-3 py-2 rounded-3 border text-center cursor-pointer transition-all bg-light-subtle"
                      @click="goToBooking(time)"
                    >
                      <span class="fw-bold text-dark">{{ time }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { mockMovies } from '@/mock/movies';

const router = useRouter();
const selectedDate = ref('2024-02-15');
const selectedCinema = ref('');

// Mock dates for slider
const dates = [
  { full: '2024-02-15', weekday: 'Thứ 5', day: '15', month: 'Th. 02' },
  { full: '2024-02-16', weekday: 'Thứ 6', day: '16', month: 'Th. 02' },
  { full: '2024-02-17', weekday: 'Thứ 7', day: '17', month: 'Th. 02' },
  { full: '2024-02-18', weekday: 'Chủ Nhật', day: '18', month: 'Th. 02' },
  { full: '2024-02-19', weekday: 'Thứ 2', day: '19', month: 'Th. 02' },
  { full: '2024-02-20', weekday: 'Thứ 3', day: '20', month: 'Th. 02' },
  { full: '2024-02-21', weekday: 'Thứ 4', day: '21', month: 'Th. 02' }
];

const movies = ref(mockMovies.map(m => ({
  ...m,
  formats: [
    { name: '2D Phụ Đề', times: ['14:30', '16:00', '19:15', '21:45'] },
    { name: '2D Lồng Tiếng', times: ['13:00', '15:15'] }
  ]
})));

const goToBooking = (time) => {
  router.push({ name: 'SelectSeats', params: { showtimeId: '1' } });
};
</script>

<style scoped>
.text-primary-light { color: #a5b4fc; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.x-small { font-size: 0.7rem; }
.cursor-pointer { cursor: pointer; }
.transition-all { transition: all 0.3s ease; }

.date-card:hover { transform: translateY(-3px); }
.time-slot:hover {
  background-color: var(--el-color-primary) !important;
  color: #fff !important;
  border-color: var(--el-color-primary) !important;
}
.time-slot:hover span { color: #fff !important; }

.schedule-movie-card:hover {
  transform: scale(1.01);
  box-shadow: 0 1rem 3rem rgba(0,0,0,0.1) !important;
}

.custom-select :deep(.el-input__wrapper) {
  border-radius: 12px;
  background-color: #f8fafc;
}
</style>
