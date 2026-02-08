<template>
  <div class="container-fluid p-4">
    <!-- Page Header -->
    <div class="mb-4">
      <h2 class="mb-2 fs-3 fw-bold text-dark">Tổng quan Bảng điều khiển</h2>
    </div>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :lg="6" class="mb-4 mb-lg-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect">
          <div class="d-flex align-items-center gap-3">
            <div class="rounded-3 d-flex align-items-center justify-content-center text-white flex-shrink-0" style="width: 70px; height: 70px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="flex-grow-1">
              <p class="text-secondary small mb-1">Tổng người dùng</p>
              <h3 class="fs-4 fw-bold text-dark mb-1">{{ stats.totalUsers }}</h3>
              <p class="small mb-0 text-success d-flex align-items-center gap-1">
                <el-icon><CaretTop /></el-icon>
                12% so với tháng trước
              </p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6" class="mb-4 mb-lg-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect">
          <div class="d-flex align-items-center gap-3">
            <div class="rounded-3 d-flex align-items-center justify-content-center text-white flex-shrink-0" style="width: 70px; height: 70px; background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);">
              <el-icon :size="40"><Money /></el-icon>
            </div>
            <div class="flex-grow-1">
              <p class="text-secondary small mb-1">Doanh thu (Hôm nay)</p>
              <h3 class="fs-4 fw-bold text-dark mb-1">${{ stats.revenue }}</h3>
              <p class="small mb-0 text-success d-flex align-items-center gap-1">
                <el-icon><CaretTop /></el-icon>
                8% so với hôm qua
              </p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6" class="mb-4 mb-sm-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect">
          <div class="d-flex align-items-center gap-3">
            <div class="rounded-3 d-flex align-items-center justify-content-center text-white flex-shrink-0" style="width: 70px; height: 70px; background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);">
              <el-icon :size="40"><Ticket /></el-icon>
            </div>
            <div class="flex-grow-1">
              <p class="text-secondary small mb-1">Vé đang chờ</p>
              <h3 class="fs-4 fw-bold text-dark mb-1">{{ stats.pendingTickets }}</h3>
              <p class="small mb-0 text-warning d-flex align-items-center gap-1">
                <el-icon><CaretBottom /></el-icon>
                3 mục cần chú ý
              </p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect">
          <div class="d-flex align-items-center gap-3">
            <div class="rounded-3 d-flex align-items-center justify-content-center text-white flex-shrink-0" style="width: 70px; height: 70px; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);">
              <el-icon :size="40"><Warning /></el-icon>
            </div>
            <div class="flex-grow-1">
              <p class="text-secondary small mb-1">Vấn đề hệ thống</p>
              <h3 class="fs-4 fw-bold text-dark mb-1">{{ stats.issues }}</h3>
              <p class="small mb-0 text-danger">
                Yêu cầu xử lý ngay
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts and Activity Section -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :lg="16" class="mb-4 mb-lg-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm">
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <span class="d-flex align-items-center gap-2 fw-bold fs-6">
                <el-icon><TrendCharts /></el-icon>
                Revenue Analytics
              </span>
              <el-button-group size="small">
                <el-button :type="activeTab === 'week' ? 'primary' : ''" @click="activeTab = 'week'">Week</el-button>
                <el-button :type="activeTab === 'month' ? 'primary' : ''" @click="activeTab = 'month'">Month</el-button>
                <el-button :type="activeTab === 'year' ? 'primary' : ''" @click="activeTab = 'year'">Year</el-button>
              </el-button-group>
            </div>
          </template>
          <div class="d-flex flex-column align-items-center justify-content-center rounded bg-light p-5 text-center" style="min-height: 300px;">
            <el-icon :size="64" class="text-secondary mb-3"><TrendCharts /></el-icon>
            <p class="fs-5 fw-medium text-dark mb-1">Chart visualization will be integrated here</p>
            <p class="text-secondary small mb-0">You can use Chart.js, ECharts, or any other charting library</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm">
          <template #header>
            <span class="d-flex align-items-center gap-2 fw-bold fs-6">
              <el-icon><Lightning /></el-icon>
              Thao tác nhanh
            </span>
          </template>
          <div class="d-flex flex-column gap-3">
            <el-button type="primary" :icon="VideoPlay" size="large" class="w-100 justify-content-start" @click="handleAction('movie')">
              Thêm phim mới
            </el-button>
            <el-button type="success" :icon="Document" size="large" class="w-100 justify-content-start" @click="handleAction('report')">
              Xuất báo cáo
            </el-button>
            <el-button type="info" :icon="User" size="large" class="w-100 justify-content-start" @click="handleAction('user')">
              Quản lý tài khoản
            </el-button>
            <el-button type="warning" :icon="Setting" size="large" class="w-100 justify-content-start" @click="handleAction('settings')">
              Cấu hình hệ thống
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts and Activity Section -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :lg="16" class="mb-4 mb-lg-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm rounded-4">
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-bold fs-5">Phân tích Doanh thu</span>
              <el-radio-group v-model="activeTab" size="small">
                <el-radio-button label="Tuần" value="week" />
                <el-radio-button label="Tháng" value="month" />
              </el-radio-group>
            </div>
          </template>
          
          <!-- Mock Chart Visualization -->
          <div class="py-4">
            <div class="d-flex align-items-end justify-content-between gap-2" style="height: 250px;">
              <div v-for="(val, i) in revenueData" :key="i" class="flex-grow-1 bg-primary bg-opacity-75 rounded-top position-relative bar-hover" :style="{ height: val + '%' }">
                <div class="position-absolute bottom-100 start-50 translate-middle-x mb-2 small fw-bold opacity-0 bar-tooltip">{{ val }}k</div>
              </div>
            </div>
            <div class="d-flex justify-content-between mt-3 text-secondary small fw-bold">
              <span>Mon</span><span>Tue</span><span>Wed</span><span>Thu</span><span>Fri</span><span>Sat</span><span>Sun</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm rounded-4">
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-bold fs-5">Tỉ lệ lấp đầy phòng</span>
              <div class="p-2 bg-light rounded-2 small text-primary fw-bold">Live</div>
            </div>
          </template>
          <div class="d-flex flex-column gap-4">
            <div v-for="room in occupancyStats" :key="room.name">
              <div class="d-flex justify-content-between mb-2 small">
                <span class="fw-bold">{{ room.name }}</span>
                <span class="text-secondary">{{ room.percentage }}%</span>
              </div>
              <el-progress :percentage="room.percentage" :color="room.color" :show-text="false" />
            </div>
          </div>
          <div class="mt-4 pt-4 border-top">
            <p class="small text-secondary mb-0">Tỉ lệ lấp đầy trung bình tăng <strong>12%</strong> so với tuần trước.</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Bottom Lists -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="12" class="mb-4 mb-lg-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm rounded-4 overflow-hidden">
          <template #header>
            <span class="fw-bold fs-5">Phim Hot tháng này</span>
          </template>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="bg-light text-secondary small text-uppercase">
                <tr>
                  <th class="ps-3 border-0">Phim</th>
                  <th class="text-center border-0">Số vé</th>
                  <th class="text-end pe-3 border-0">Doanh thu</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="movie in topMovies" :key="movie.id">
                  <td class="ps-3">
                    <div class="fw-bold">{{ movie.title }}</div>
                    <div class="small text-secondary">{{ movie.screenings }} screenings</div>
                  </td>
                  <td class="text-center">
                    <span class="badge bg-light text-dark px-3 rounded-pill">{{ movie.tickets }}</span>
                  </td>
                  <td class="text-end pe-3 fw-bold text-primary">${{ (movie.tickets * 12).toLocaleString() }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm rounded-4">
          <template #header>
            <span class="fw-bold fs-5">Giao dịch gần đây</span>
          </template>
          <div class="list-group list-group-flush">
            <div v-for="tx in recentTransactions" :key="tx.id" class="list-group-item d-flex justify-content-between align-items-center bg-transparent px-0 py-3 border-bottom border-light">
              <div class="d-flex align-items-center gap-3">
                <div class="p-2 bg-primary bg-opacity-10 rounded-circle text-primary">
                  <el-icon><Ticket /></el-icon>
                </div>
                <div>
                  <h6 class="mb-0 fw-bold">{{ tx.customer }}</h6>
                  <p class="small text-secondary mb-0">{{ tx.movie }} • {{ tx.time }}</p>
                </div>
              </div>
              <div class="text-end pe-2">
                <div class="fw-bold text-success">+${{ tx.amount }}</div>
                <div class="small text-secondary fw-medium">{{ tx.method }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import {
  User,
  Money,
  Ticket,
  Warning,
  TrendCharts,
  Lightning,
  VideoPlay,
  Document,
  Setting,
  Clock,
  Calendar,
  CaretTop,
  CaretBottom
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const activeTab = ref('week');
const revenueData = ref([45, 62, 55, 80, 75, 95, 88]);

const stats = ref({
  totalUsers: 1420,
  revenue: '12,450',
  pendingTickets: 23,
  issues: 0
});

const occupancyStats = ref([
  { name: 'Hall 01 (IMAX)', percentage: 85, color: '#667eea' },
  { name: 'Hall 02 (2D/3D)', percentage: 62, color: '#f6ad55' },
  { name: 'Hall 03 (Gold Class)', percentage: 45, color: '#10b981' },
  { name: 'Hall 04 (Kids)', percentage: 92, color: '#ed64a6' }
]);

const recentTransactions = ref([
  { id: 1, customer: 'John Wick', movie: 'Avatar 3', amount: 45.00, method: 'VNPay', time: '10 mins ago' },
  { id: 2, customer: 'Sarah Connor', movie: 'Terminator', amount: 32.00, method: 'Credit Card', time: '25 mins ago' },
  { id: 3, customer: 'Peter Parker', movie: 'Spider-Man', amount: 15.00, method: 'Momo', time: '1 hr ago' },
  { id: 4, customer: 'Bruce Wayne', movie: 'Batman', amount: 120.00, method: 'VNPay', time: '2 hrs ago' }
]);

const topMovies = ref([
  { id: 1, title: 'Avatar: The Way of Water', screenings: 24, tickets: 1240, trend: 'up' },
  { id: 2, title: 'Top Gun: Maverick', screenings: 18, tickets: 980, trend: 'up' },
  { id: 3, title: 'The Batman', screenings: 15, tickets: 756, trend: 'down' },
  { id: 4, title: 'Everything Everywhere', screenings: 12, tickets: 654, trend: 'up' }
]);

const handleAction = (action) => {
  ElMessage({
    message: `Action "${action}" will be implemented soon!`,
    type: 'info'
  });
};
</script>

<style scoped>
.card-hover-effect:hover {
  transform: translateY(-4px);
  transition: transform 0.3s ease;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.bar-hover:hover {
  background-color: var(--el-color-primary) !important;
}
.bar-hover:hover .bar-tooltip {
  opacity: 1 !important;
}
.bar-tooltip {
  transition: opacity 0.2s ease;
}

.table th {
  border-top: none;
}
.backdrop-blur {
  backdrop-filter: blur(10px);
}
</style>
