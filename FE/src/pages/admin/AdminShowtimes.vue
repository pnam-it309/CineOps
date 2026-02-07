<script setup>
import { ref, computed } from 'vue';
import { Plus, Calendar, Clock, Filter, Location, ArrowLeft, ArrowRight, Delete } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { mockMovies } from '@/mock/movies';

const weekDays = ['Mon 01', 'Tue 02', 'Wed 03', 'Thu 04', 'Fri 05', 'Sat 06', 'Sun 07'];
const timeSlots = ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00', '00:00'];

const selectedVenue = ref('CineOps Central - Hall 1');
const selectedMovie = ref(mockMovies[0].id);

const schedule = ref([
  { id: 1, movieId: 1, day: 'Mon 01', time: '10:00', duration: 120 },
  { id: 2, movieId: 2, day: 'Mon 01', time: '14:00', duration: 150 },
  { id: 3, movieId: 3, day: 'Tue 02', time: '18:00', duration: 110 },
  { id: 4, movieId: 1, day: 'Wed 03', time: '08:00', duration: 120 },
]);

const getMovieTitle = (id) => mockMovies.find(m => m.id === id)?.title || 'Unknown';

const handleAddSlot = (day, time) => {
  const movie = mockMovies.find(m => m.id === selectedMovie.value);
  schedule.value.push({
    id: Date.now(),
    movieId: selectedMovie.value,
    day,
    time,
    duration: movie.duration
  });
  ElMessage.success(`Scheduled "${movie.title}" at ${time} on ${day}`);
};

const deleteSlot = (id) => {
  schedule.value = schedule.value.filter(s => s.id !== id);
};
</script>

<template>
  <div class="admin-scheduling container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Showtime Scheduling</h2>
        <p class="text-secondary small mb-0">Plan screening slots for movies across all theater halls.</p>
      </div>
      <div class="d-flex gap-3 align-items-center">
        <el-button-group>
          <el-button :icon="ArrowLeft" />
          <el-button class="fw-bold">Feb 01 - Feb 07, 2026</el-button>
          <el-button :icon="ArrowRight" />
        </el-button-group>
        <el-select v-model="selectedVenue" size="large" class="venue-select">
          <el-option label="CineOps Central - Hall 1" value="CineOps Central - Hall 1" />
          <el-option label="CineOps Central - Hall 2" value="CineOps Central - Hall 2" />
        </el-select>
      </div>
    </div>

    <div class="row g-4">
      <!-- Left sidebar: Movie selection -->
      <div class="col-lg-3">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 mb-4">
          <template #header>
            <span class="fw-bold"><el-icon class="me-2"><Filter /></el-icon>Movie Selection</span>
          </template>
          <div class="movie-selector-list overflow-auto" style="max-height: 400px;">
            <div 
              v-for="movie in mockMovies" 
              :key="movie.id"
              class="p-2 mb-2 rounded-3 border cursor-pointer transition-all d-flex align-items-center gap-2"
              :class="selectedMovie === movie.id ? 'border-primary bg-primary bg-opacity-10 shadow-sm' : 'border-light'"
              @click="selectedMovie = movie.id"
            >
              <img :src="movie.poster" class="rounded" style="width: 40px; height: 60px; object-fit: cover;">
              <div class="overflow-hidden">
                <div class="small fw-bold text-truncate">{{ movie.title }}</div>
                <div class="text-secondary" style="font-size: 10px;">{{ movie.duration }}m • {{ movie.genre }}</div>
              </div>
            </div>
          </div>
          <div class="mt-3 p-3 bg-light rounded-3 small text-secondary">
            Tip: Select a movie then click a time slot in the calendar to schedule it.
          </div>
        </el-card>
      </div>

      <!-- Main Calendar: Weekly Grid -->
      <div class="col-lg-9">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden p-0">
          <div class="table-responsive">
            <table class="table table-bordered mb-0 calendar-table">
              <thead class="bg-light align-middle">
                <tr>
                  <th class="time-cell ps-3">Time</th>
                  <th v-for="day in weekDays" :key="day" class="text-center py-3">
                    <div class="fw-bold">{{ day.split(' ')[0] }}</div>
                    <div class="small text-secondary fw-normal">{{ day.split(' ')[1] }} Feb</div>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="time in timeSlots" :key="time">
                  <td class="time-cell ps-3 align-middle text-secondary small fw-bold">{{ time }}</td>
                  <td v-for="day in weekDays" :key="day" class="calendar-slot position-relative" @click="handleAddSlot(day, time)">
                    <div v-for="slot in schedule.filter(s => s.day === day && s.time === time)" :key="slot.id" class="scheduled-item p-2 rounded-3 shadow-sm bg-primary text-white position-relative">
                      <div class="small fw-bold">{{ getMovieTitle(slot.movieId) }}</div>
                      <div style="font-size: 9px;" class="opacity-75">{{ slot.time }} - {{ slot.duration }}m</div>
                      <el-button 
                        link 
                        :icon="Delete" 
                        class="position-absolute top-0 end-0 p-2 text-white opacity-0 delete-btn" 
                        @click.stop="deleteSlot(slot.id)" 
                      />
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.venue-select {
  width: 250px;
}
.calendar-table th, .calendar-table td {
  border-color: #f0f0f0 !important;
}
.time-cell {
  width: 80px;
  background-color: #fbfbfb;
}
.calendar-slot {
  height: 80px;
  min-width: 120px;
  transition: background-color 0.2s;
  cursor: pointer;
}
.calendar-slot:hover {
  background-color: rgba(13, 110, 253, 0.05);
}
.scheduled-item {
  font-size: 11px;
  z-index: 2;
  transition: transform 0.2s;
}
.scheduled-item:hover {
  transform: scale(1.02);
}
.scheduled-item:hover .delete-btn {
  opacity: 1 !important;
}
.cursor-pointer {
  cursor: pointer;
}
.transition-all {
  transition: all 0.2s ease;
}
</style>
