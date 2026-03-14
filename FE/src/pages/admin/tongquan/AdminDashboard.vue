<template>
  <div class="admin-dashboard-wrapper container-fluid p-4">
    <!-- Page Header -->
    <!-- Page Header (Standardized Title Card) -->
    <div class="premium-card title-card mb-4 p-4">
      <div class="d-flex align-items-center justify-content-between">
        <div class="d-flex align-items-center gap-3">
          <div class="header-icon-box-modern">
            <i class="bi bi-speedometer2"></i>
          </div>
          <div>
            <h1 class="premium-page-title m-0">Tổng quan Hệ thống</h1>
            <p class="premium-page-subtitle m-0">Chào mừng bạn trở lại! Dưới đây là tóm tắt hoạt động của rạp phim hôm nay.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div class="stats-row mb-4">
      <!-- Total Users -->
      <div class="stat-col">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect stat-card-premium">
          <div class="d-flex align-items-start h-100">
            <div class="stat-icon-box bg-light text-dark me-4">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content flex-grow-1">
              <span class="stat-label">Tổng người dùng</span>
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-trend trend-up">
                <el-icon><CaretTop /></el-icon>
                <span>12% tháng trước</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Current Revenue -->
      <div class="stat-col">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect stat-card-premium">
          <div class="d-flex align-items-start h-100">
            <div class="stat-icon-box bg-light text-dark me-4">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content flex-grow-1">
              <span class="stat-label">Doanh thu hôm nay</span>
              <div class="stat-value">${{ stats.revenue }}</div>
              <div class="stat-trend trend-up">
                <el-icon><CaretTop /></el-icon>
                <span>8% so với hôm qua</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Expected Revenue -->
      <div class="stat-col">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect stat-card-premium">
          <div class="d-flex align-items-start h-100">
            <div class="stat-icon-box bg-light text-dark me-4">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content flex-grow-1">
              <span class="stat-label">Doanh thu dự kiến</span>
              <div class="stat-value">${{ stats.expectedRevenue }}</div>
              <div class="stat-trend trend-primary">
                <el-icon><Calendar /></el-icon>
                <span>Dự báo tháng này</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Pending Tickets -->
      <div class="stat-col">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect stat-card-premium">
          <div class="d-flex align-items-start h-100">
            <div class="stat-icon-box bg-light text-dark me-4">
              <el-icon><Ticket /></el-icon>
            </div>
            <div class="stat-content flex-grow-1">
              <span class="stat-label">Vé đang chờ</span>
              <div class="stat-value">{{ stats.pendingTickets }}</div>
              <div class="stat-trend trend-warning">
                <el-icon><Warning /></el-icon>
                <span>3 mục cần chú ý</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- System Issues -->
      <div class="stat-col">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect stat-card-premium">
          <div class="d-flex align-items-start h-100">
            <div class="stat-icon-box bg-light text-dark me-4">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-content flex-grow-1">
              <span class="stat-label">Lỗi hệ thống</span>
              <div class="stat-value text-danger">{{ stats.issues }}</div>
              <div class="stat-trend trend-danger">
                <span>Yêu cầu xử lý ngay</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Charts and Activity Section -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :lg="16" class="mb-4 mb-lg-0">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm">
          <template #header>
            <div class="d-flex justify-content-between align-items-center" style="min-height: 32px;">
              <span class="d-flex align-items-center gap-2 fw-bold fs-6">
                <el-icon><TrendCharts /></el-icon>
                Thống kê Doanh thu
              </span>
              <el-radio-group v-model="activeTab" size="small">
                <el-radio-button value="week">Tuần</el-radio-button>
                <el-radio-button value="month">Tháng</el-radio-button>
                <el-radio-button value="year">Năm</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="d-flex flex-column align-items-center justify-content-center rounded bg-light p-5 text-center" style="min-height: 300px;">
            <el-icon :size="64" class="text-secondary mb-3"><TrendCharts /></el-icon>
            <p class="fs-5 fw-medium text-dark mb-1">Biểu đồ thống kê sẽ được hiển thị tại đây</p>
            <p class="text-secondary small mb-0">Hệ thống hỗ trợ Chart.js, ECharts và nhiều thư viện khác</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm">
          <template #header>
            <div class="d-flex justify-content-between align-items-center" style="min-height: 32px;">
              <span class="d-flex align-items-center gap-2 fw-bold fs-6">
                <el-icon><Lightning /></el-icon>
                Thao tác nhanh
              </span>
            </div>
          </template>
          <div class="d-flex flex-column gap-3">
            <el-button type="primary" size="large" class="w-100 custom-btn-start" @click="handleAction('movie')">
              <span class="btn-icon-wrapper"><el-icon :size="18"><VideoPlay /></el-icon></span>
              <span class="btn-label">Thêm phim mới</span>
            </el-button>
            <el-button type="success" size="large" class="w-100 custom-btn-start" @click="handleAction('report')">
              <span class="btn-icon-wrapper"><el-icon :size="18"><Document /></el-icon></span>
              <span class="btn-label">Xuất báo cáo</span>
            </el-button>
            <el-button type="info" size="large" class="w-100 custom-btn-start" @click="handleAction('user')">
              <span class="btn-icon-wrapper"><el-icon :size="18"><User /></el-icon></span>
              <span class="btn-label">Quản lý tài khoản</span>
            </el-button>
            <el-button type="warning" size="large" class="w-100 custom-btn-start" @click="handleAction('settings')">
              <span class="btn-icon-wrapper"><el-icon :size="18"><Setting /></el-icon></span>
              <span class="btn-label">Cấu hình hệ thống</span>
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
                <el-radio-button value="week">Tuần</el-radio-button>
                <el-radio-button value="month">Tháng</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          
          <!-- Mock Chart Visualization -->
          <div class="py-4">
            <div class="d-flex align-items-end justify-content-between gap-2" style="height: 250px;">
              <div v-for="(val, i) in revenueData" :key="i" class="flex-grow-1 bg-secondary bg-opacity-25 rounded-top position-relative bar-hover" :style="{ height: val + '%' }">
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
                    <div class="small text-secondary">{{ movie.screenings }} suất chiếu</div>
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
                <div class="p-2 bg-light rounded-circle text-dark">
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
  Download,
  Document,
  Setting,
  Clock,
  Calendar,
  CaretTop,
  CaretBottom
} from '@element-plus/icons-vue';
import notification from '@/utils/notifications';

const activeTab = ref('week');
const revenueData = ref([45, 62, 55, 80, 75, 95, 88]);

const stats = ref({
  totalUsers: 1420,
  revenue: '12,450',
  expectedRevenue: '45,800',
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
  { id: 1, customer: 'John Wick', movie: 'Avatar 3', amount: 45.00, method: 'VNPay', time: '10 phút trước' },
  { id: 2, customer: 'Sarah Connor', movie: 'Terminator', amount: 32.00, method: 'Thẻ tín dụng', time: '25 phút trước' },
  { id: 3, customer: 'Peter Parker', movie: 'Spider-Man', amount: 15.00, method: 'Momo', time: '1 giờ trước' },
  { id: 4, customer: 'Bruce Wayne', movie: 'Batman', amount: 120.00, method: 'VNPay', time: '2 giờ trước' }
]);

const topMovies = ref([
  { id: 1, title: 'Avatar: The Way of Water', screenings: 24, tickets: 1240, trend: 'up' },
  { id: 2, title: 'Top Gun: Maverick', screenings: 18, tickets: 980, trend: 'up' },
  { id: 3, title: 'The Batman', screenings: 15, tickets: 756, trend: 'down' },
  { id: 4, title: 'Everything Everywhere', screenings: 12, tickets: 654, trend: 'up' }
]);

const handleAction = (action) => {
  notification.info(`Chức năng "${action}" đang được phát triển!`);
};
</script>

<style scoped>
.stat-card-premium {
  border-radius: 16px !important;
  background: #ffffff;
}

.stat-icon-box {
  width: 56px;
  height: 56px;
  min-width: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}

/* Neutral Styles */
.bg-light { background-color: #f8fafc !important; }

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  text-align: left;
  overflow: hidden;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
  white-space: nowrap;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 4px;
  line-height: 1.2;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
}

.trend-up { color: #10b981; }
.trend-down { color: #ef4444; }
.trend-primary { color: #6366f1; }
.trend-warning { color: #f59e0b; }
.trend-danger { color: #ef4444; }

.card-hover-effect {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.card-hover-effect:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px -10px rgba(0, 0, 0, 0.15) !important;
}

.stats-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.stat-col {
  flex: 1 1 0;
  min-width: 200px;
}

.custom-btn-start {
  justify-content: flex-start !important;
  padding-left: 20px !important;
  height: 52px !important;
}
</style>

