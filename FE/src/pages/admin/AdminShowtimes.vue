<script setup>
import { ref, computed } from 'vue';
import { Plus, Calendar, Clock, Filter, Location, ArrowLeft, ArrowRight, Delete } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { mockMovies } from '@/mock';

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
  <div class="admin-scheduling-wrapper h-100 overflow-hidden">
    <div class="admin-scheduling h-100 d-grid overflow-hidden">
      <!-- Header Area -->
      <header class="p-3 pb-0 d-flex justify-content-between align-items-center flex-shrink-0">
        <h2 class="fs-2 fw-bold text-dark mb-0">Lập lịch Chiếu phim</h2>
        <div class="d-flex gap-2 align-items-center">
          <el-button-group>
            <el-button :icon="ArrowLeft" />
            <el-button class="fw-bold px-4">01/02 - 07/02, 2026</el-button>
            <el-button :icon="ArrowRight" />
          </el-button-group>
          <el-select v-model="selectedVenue" size="large" style="width: 180px;">
            <el-option label="Phòng 1" value="CineOps Central - Hall 1" />
            <el-option label="Phòng 2" value="CineOps Central - Hall 2" />
          </el-select>
        </div>
      </header>

      <!-- Content Area -->
      <main class="p-3 row m-0 g-3 overflow-hidden">
        <!-- Left Column -->
        <section class="col-lg-3 d-flex flex-column h-100 overflow-hidden">
          <el-card shadow="never" class="border border-secondary-subtle shadow-sm rounded-4 d-flex flex-column overflow-hidden h-100 custom-card">
            <template #header>
              <div class="d-flex align-items-center">
                <el-icon class="me-2 text-primary font-bold"><Filter /></el-icon>
                <span class="fw-bold">Chọn Phim</span>
              </div>
            </template>
            
            <div class="movie-selector-list overflow-auto flex-grow-1 pe-2">
              <div v-for="movie in mockMovies" :key="movie.id"
                class="p-2 mb-2 rounded-3 border cursor-pointer transition-all d-flex align-items-center gap-2"
                :class="selectedMovie === movie.id ? 'border-primary bg-primary bg-opacity-10 shadow-sm' : 'border-light-subtle'"
                @click="selectedMovie = movie.id">
                <img :src="movie.poster" class="rounded shadow-sm" style="width: 32px; height: 44px; object-fit: cover;">
                <div class="overflow-hidden">
                  <div class="small fw-bold text-truncate lh-1 mb-1">{{ movie.title }}</div>
                  <div class="text-secondary" style="font-size: 9px;">{{ movie.duration }}m • {{ movie.genre }}</div>
                </div>
              </div>
            </div>
            
            <div class="mt-2 p-2 bg-light rounded-3 small text-secondary flex-shrink-0" style="font-size: 0.7rem;">
              <el-icon class="me-1"><Clock /></el-icon>Mẹo: Chọn phim rồi nhấn vào lịch.
            </div>
          </el-card>
        </section>

        <!-- Right Column -->
        <section class="col-lg-9 d-flex flex-column h-100 overflow-hidden">
          <el-card shadow="never" class="border border-secondary-subtle shadow-sm rounded-4 overflow-hidden p-0 d-flex flex-column h-100 custom-card">
            <div class="table-container flex-grow-1 overflow-auto h-100">
              <table class="table table-bordered mb-0 calendar-table">
                <thead class="bg-light align-middle sticky-top" style="z-index: 10;">
                  <tr>
                    <th class="time-cell ps-3">Giờ</th>
                    <th v-for="day in weekDays" :key="day" class="text-center py-2 bg-light border-bottom">
                      <div class="fw-bold small">{{ day.split(' ')[0] }}</div>
                      <div class="small text-secondary fw-normal" style="font-size: 0.65rem;">{{ day.split(' ')[1] }} Th2</div>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="time in timeSlots" :key="time">
                    <td class="time-cell ps-3 align-middle text-secondary small fw-bold bg-light border-end">{{ time }}</td>
                    <td v-for="day in weekDays" :key="day" class="calendar-slot position-relative p-0"
                      @click="handleAddSlot(day, time)">
                      <div class="slot-inner w-100 h-100 p-1">
                        <div v-for="slot in schedule.filter(s => s.day === day && s.time === time)" :key="slot.id"
                          class="scheduled-item p-1 rounded-2 shadow-sm bg-primary text-white position-relative">
                          <div class="fw-bold truncate-2" style="font-size: 9px; line-height: 1.1;">{{ getMovieTitle(slot.movieId) }}</div>
                          <div style="font-size: 8px;" class="opacity-75">{{ slot.duration }}m</div>
                          <el-button link :icon="Delete"
                            class="position-absolute top-0 end-0 p-1 text-white opacity-0 delete-btn"
                            @click.stop="deleteSlot(slot.id)" />
                        </div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </section>
      </main>
    </div>
  </div>
</template>

<style>
/* Reset parent container strictly */
.el-main:has(.admin-scheduling-wrapper) {
  padding: 0 !important;
  overflow: hidden !important;
  height: 100% !important;
  display: flex !important;
  flex-direction: column !important;
}
</style>

<style scoped>
.admin-scheduling-wrapper {
  box-sizing: border-box;
}

.admin-scheduling {
  grid-template-rows: auto 1fr;
  height: 100%;
}

.truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

:deep(.custom-card) {
  height: 100% !important;
  display: flex !important;
  flex-direction: column !important;
  min-height: 0;
}

:deep(.custom-card .el-card__header) {
  padding: 0.75rem 1rem !important;
  flex-shrink: 0;
}

:deep(.custom-card .el-card__body) {
  flex: 1 !important;
  min-height: 0;
  overflow: hidden !important;
  display: flex !important;
  flex-direction: column !important;
  padding: 1rem !important;
}

:deep(.p-0 .el-card__body) {
  padding: 0 !important;
}

.table-container {
  scrollbar-width: thin;
}

.calendar-table {
  table-layout: fixed;
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.calendar-table th,
.calendar-table td {
  border-color: #f0f0f0 !important;
}

.time-cell {
  width: 55px;
  background-color: #fbfbfb;
  position: sticky;
  left: 0;
  z-index: 5;
  font-size: 0.7rem;
}

.calendar-slot {
  height: 75px;
  transition: background-color 0.2s;
  cursor: pointer;
}

.slot-inner {
  overflow-y: auto;
}

.calendar-slot:hover {
  background-color: rgba(13, 110, 253, 0.04);
}

.scheduled-item {
  z-index: 2;
  transition: all 0.2s;
}

.scheduled-item:hover {
  transform: translateY(-1px);
  filter: brightness(1.1);
}

.scheduled-item:hover .delete-btn {
  opacity: 1 !important;
}

.movie-selector-list::-webkit-scrollbar,
.table-container::-webkit-scrollbar {
  width: 4px;
}

.movie-selector-list::-webkit-scrollbar-thumb,
.table-container::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 4px;
}
</style>
