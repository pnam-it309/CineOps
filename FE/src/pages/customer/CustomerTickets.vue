<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fs-2 fw-bold text-dark tracking-tight">Lịch sử <span class="text-primary">Giao dịch</span></h2>
        <p class="text-secondary mb-0 small">Quản lý và xem lại tất cả vé xem phim của bạn.</p>
      </div>
      <el-button type="primary" size="large" round @click="$router.push('/')" class="shadow-sm">
        Đặt thêm vé mới
      </el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="mb-5 border-0 bg-transparent">
      <el-radio-group v-model="statusFilter" size="large" class="modern-toggle">
        <el-radio-button value="">Tất cả vé</el-radio-button>
        <el-radio-button value="upcoming">Sắp chiếu</el-radio-button>
        <el-radio-button value="past">Lịch sử xem</el-radio-button>
        <el-radio-button value="cancelled">Đã hủy</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- Tickets List -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="12" :lg="8" v-for="ticket in filteredTickets" :key="ticket.id" class="mb-4">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect" :class="{'opacity-75': ticket.status === 'cancelled'}">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <el-tag :type="getStatusType(ticket.status)" effect="dark" round class="px-3 border-0">
              {{ getStatusLabel(ticket.status) }}
            </el-tag>
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, ticket)">
              <el-icon class="cursor-pointer fs-5 text-secondary hover-primary"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="view">
                    <el-icon><View /></el-icon>
                    Xem chi tiết
                  </el-dropdown-item>
                  <el-dropdown-item command="download" v-if="ticket.status !== 'cancelled'">
                    <el-icon><Download /></el-icon>
                    Tải vé (PDF)
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel" divided v-if="ticket.status === 'upcoming'" class="text-danger">
                    <el-icon><Delete /></el-icon>
                    Hủy vé này
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="d-flex gap-3 mb-3">
            <div class="flex-shrink-0" style="width: 100px; height: 140px;">
              <img :src="ticket.poster" :alt="ticket.movieTitle" class="w-100 h-100 object-fit-cover rounded" />
            </div>

            <div class="flex-grow-1">
              <h3 class="fs-6 fw-bold text-dark mb-2">{{ ticket.movieTitle }}</h3>
              
              <div class="d-flex align-items-center gap-2 mb-2 small text-secondary">
                <el-icon><Calendar /></el-icon>
                <span>{{ ticket.showDate }}</span>
              </div>
              
              <div class="d-flex align-items-center gap-2 mb-2 small text-secondary">
                <el-icon><Clock /></el-icon>
                <span>{{ ticket.showTime }}</span>
              </div>
              
              <div class="d-flex align-items-center gap-2 mb-2 small text-secondary-dark">
                <el-icon><Location /></el-icon>
                <span>Rạp: {{ ticket.cinema }} - Phòng {{ ticket.hall }}</span>
              </div>
              
              <div class="d-flex align-items-center gap-2 small text-secondary-dark">
                <el-icon><Tickets /></el-icon>
                <span>Ghế: <strong class="text-dark">{{ ticket.seats.join(', ') }}</strong></span>
              </div>
            </div>
          </div>

          <el-divider class="my-4 border-light-subtle"/>

          <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex flex-column">
              <span class="text-secondary-light x-small text-uppercase fw-bold mb-1">Tổng tiền vé</span>
              <span class="fs-4 fw-bold text-primary">{{ ticket.totalPrice.toLocaleString('vi-VN') }}đ</span>
            </div>
            <el-button
              v-if="ticket.status === 'upcoming'"
              type="primary"
              size="default"
              @click="handleViewQR(ticket)"
              round
              class="px-3"
            >
              Hiện mã QR
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Empty State -->
    <el-empty v-if="filteredTickets.length === 0" description="Bạn chưa có vé nào trong mục này.">
      <el-button type="primary" @click="$router.push('/')" round class="px-4">
        Đặt vé ngay
      </el-button>
    </el-empty>

    <!-- Pagination -->
    <div class="p-4 d-flex justify-content-center" v-if="filteredTickets.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="filteredTickets.length"
      />
    </div>

    <!-- QR Code Dialog -->
    <el-dialog v-model="qrDialogVisible" title="Your Ticket QR Code" width="400px">
      <div>
        <div class="d-flex justify-content-center p-4 bg-light rounded mb-3">
          <el-icon :size="120" class="text-secondary"><Postcard /></el-icon>
        </div>
        <p class="text-center text-secondary mb-3 small">Show this QR code at the cinema entrance</p>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="Movie">{{ selectedTicket?.movieTitle }}</el-descriptions-item>
          <el-descriptions-item label="Date">{{ selectedTicket?.showDate }}</el-descriptions-item>
          <el-descriptions-item label="Time">{{ selectedTicket?.showTime }}</el-descriptions-item>
          <el-descriptions-item label="Seats">{{ selectedTicket?.seats.join(', ') }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import {
  Plus,
  Calendar,
  Clock,
  Location,
  Tickets,
  MoreFilled,
  View,
  Download,
  Delete,
  Postcard
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(6);
const qrDialogVisible = ref(false);
const selectedTicket = ref(null);

// Mock ticket data
const tickets = ref([
  {
    id: 1,
    movieTitle: 'Avatar: The Way of Water',
    poster: 'https://dummyimage.com/200x300/667eea/ffffff?text=Avatar',
    showDate: '2024-02-15',
    showTime: '19:30',
    cinema: 'CineOps Central',
    hall: '1',
    seats: ['A5', 'A6'],
    totalPrice: 24.00,
    status: 'upcoming',
    bookingDate: '2024-02-01'
  },
  {
    id: 2,
    movieTitle: 'Top Gun: Maverick',
    poster: 'https://dummyimage.com/200x300/764ba2/ffffff?text=TopGun',
    showDate: '2024-01-28',
    showTime: '21:00',
    cinema: 'CineOps Downtown',
    hall: '3',
    seats: ['C10', 'C11', 'C12'],
    totalPrice: 36.00,
    status: 'past',
    bookingDate: '2024-01-20'
  },
  {
    id: 3,
    movieTitle: 'The Batman',
    poster: 'https://dummyimage.com/200x300/f093fb/ffffff?text=Batman',
    showDate: '2024-02-20',
    showTime: '18:00',
    cinema: 'CineOps Mall',
    hall: '2',
    seats: ['B7', 'B8'],
    totalPrice: 24.00,
    status: 'upcoming',
    bookingDate: '2024-02-05'
  },
  {
    id: 4,
    movieTitle: 'Spider-Man: No Way Home',
    poster: 'https://dummyimage.com/200x300/f5576c/ffffff?text=SpiderMan',
    showDate: '2024-01-15',
    showTime: '20:30',
    cinema: 'CineOps Central',
    hall: '1',
    seats: ['D5'],
    totalPrice: 12.00,
    status: 'cancelled',
    bookingDate: '2024-01-10'
  }
]);

const filteredTickets = computed(() => {
  if (!statusFilter.value) return tickets.value;
  return tickets.value.filter(ticket => ticket.status === statusFilter.value);
});

const getStatusType = (status) => {
  const types = {
    upcoming: 'success',
    past: 'info',
    cancelled: 'danger'
  };
  return types[status] || 'info';
};

const getStatusLabel = (status) => {
  const labels = {
    upcoming: 'Sắp chiếu',
    past: 'Đã xem',
    cancelled: 'Đã hủy'
  };
  return labels[status] || status;
};

const handleCommand = (command, ticket) => {
  if (command === 'view') {
    handleViewQR(ticket);
  } else if (command === 'download') {
    ElMessage.success('Ticket downloaded');
  } else if (command === 'cancel') {
    handleCancelTicket(ticket);
  }
};

const handleViewQR = (ticket) => {
  selectedTicket.value = ticket;
  qrDialogVisible.value = true;
};

const handleCancelTicket = (ticket) => {
  ElMessageBox.confirm(
    'Are you sure you want to cancel this ticket? This action cannot be undone.',
    'Cancel Ticket',
    {
      confirmButtonText: 'Yes, Cancel',
      cancelButtonText: 'No, Keep It',
      type: 'warning'
    }
  ).then(() => {
    ticket.status = 'cancelled';
    ElMessage.success('Ticket cancelled successfully');
  });
};
</script>

<style scoped>
.card-hover-effect:hover {
  transform: translateY(-4px);
  transition: transform 0.3s ease;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}
.cursor-pointer {
  cursor: pointer;
}
.object-fit-cover {
  object-fit: cover;
}

.modern-toggle :deep(.el-radio-button__inner) {
  border-radius: 50px !important;
  margin: 0 5px;
  border: 1px solid #e2e8f0 !important;
  font-weight: 600;
  background-color: #fff;
  color: #64748b;
}

.modern-toggle :deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background-color: var(--el-color-primary);
  color: #fff;
  border-color: var(--el-color-primary) !important;
}

.tracking-tight { letter-spacing: -0.025em; }
.x-small { font-size: 0.7rem; }
.text-secondary-light { color: #94a3b8; }
.text-secondary-dark { color: #64748b; }
</style>
