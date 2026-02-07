<script setup>
import { ref } from 'vue';
import { ShoppingCart, VideoPlay, Timer, Connection } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const router = useRouter();

const upcomingShowtimes = ref([
  { id: 1, movie: 'Avatar: The Way of Water', time: '18:30', room: 'Hall 1', format: '3D', seats: '12/120', status: 'Selling' },
  { id: 2, movie: 'Spider-Man: No Way Home', time: '19:00', room: 'Hall 3', format: '2D', seats: '85/150', status: 'Selling' },
  { id: 3, movie: 'The Batman', time: '19:15', room: 'Hall 2', format: 'IMAX', seats: '140/140', status: 'Full' },
  { id: 4, movie: 'Doctor Strange', time: '20:00', room: 'Hall 1', format: '2D', seats: '0/120', status: 'Pending' }
]);

const quickStats = ref([
  { label: 'Tickets Today', value: '142', icon: Connection, color: 'text-primary' },
  { label: 'Session Revenue', value: '12.4M', icon: Timer, color: 'text-success' }
]);

const goToSales = (showtime) => {
  router.push({ name: ROUTES_CONSTANTS.STAFF.children.SALES.name, query: { showtimeId: showtime.id } });
};
</script>

<template>
  <div class="staff-dashboard container-fluid p-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="fs-4 fw-bold mb-1">POS Dashboard</h2>
        <p class="text-secondary small mb-0">Welcome, Staff Operator. Select a showtime to start selling.</p>
      </div>
      <div class="d-flex gap-4">
        <div v-for="stat in quickStats" :key="stat.label" class="text-end">
          <div class="small text-secondary fw-bold text-uppercase" style="font-size: 0.7rem;">{{ stat.label }}</div>
          <div class="fs-4 fw-bold" :class="stat.color">{{ stat.value }}</div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="row g-3 mb-4">
      <div class="col-md-6">
        <el-card shadow="never" class="border-0 shadow-sm rounded-3 bg-primary text-white h-100">
          <div class="p-3 d-flex align-items-center justify-content-between">
            <div>
              <h5 class="fw-bold mb-1">New Counter Sale</h5>
              <p class="small mb-0 opacity-75">Start a fresh booking flow.</p>
            </div>
            <el-button color="#ffffff" :icon="ShoppingCart" circle @click="router.push({ name: ROUTES_CONSTANTS.STAFF.children.SALES.name })" />
          </div>
        </el-card>
      </div>
      <div class="col-md-6">
        <el-card shadow="never" class="border-0 shadow-sm rounded-3 bg-dark text-white h-100">
          <div class="p-3 d-flex align-items-center justify-content-between">
            <div>
              <h5 class="fw-bold mb-1">Shift Handover</h5>
              <p class="small mb-0 opacity-75">Current session end summary.</p>
            </div>
            <el-button color="#ffffff" plain :icon="Timer" circle @click="router.push({ name: ROUTES_CONSTANTS.STAFF.children.SHIFT.name })" />
          </div>
        </el-card>
      </div>
    </div>

    <!-- Upcoming Showtimes -->
    <h6 class="fw-bold text-uppercase text-secondary mb-3" style="font-size: 0.8rem; letter-spacing: 1px;">
      Upcoming Showtimes
    </h6>
    
    <div class="row g-3">
      <div v-for="st in upcomingShowtimes" :key="st.id" class="col-12">
        <el-card 
          shadow="hover" 
          class="border-0 shadow-sm rounded-3 cursor-pointer"
          :body-style="{ padding: '1rem' }"
          @click="goToSales(st)"
        >
          <div class="row align-items-center">
            <div class="col-auto">
              <div class="bg-light p-3 rounded-3 text-center" style="min-width: 80px;">
                <div class="fs-4 fw-bold text-primary">{{ st.time }}</div>
                <div class="small text-secondary">{{ st.room }}</div>
              </div>
            </div>
            <div class="col px-3">
              <h5 class="fw-bold mb-1">{{ st.movie }}</h5>
              <div class="d-flex align-items-center gap-2">
                <el-tag size="small" effect="dark" type="info">{{ st.format }}</el-tag>
                <span class="small text-secondary">Seats: <strong>{{ st.seats }}</strong></span>
              </div>
            </div>
            <div class="col-auto">
              <div class="d-flex align-items-center gap-3">
                <el-tag :type="st.status === 'Full' ? 'danger' : (st.status === 'Selling' ? 'success' : 'info')" round>
                  {{ st.status }}
                </el-tag>
                <el-button type="primary" plain circle :icon="ShoppingCart" />
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cursor-pointer { cursor: pointer; }
</style>
