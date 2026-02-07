<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fs-2 fw-bold text-dark">Transaction History</h2>
        <p class="text-secondary mb-0">View all your movie ticket bookings and concessions.</p>
      </div>
      <el-button type="primary" size="large" round @click="$router.push('/')">
        Book New Movie
      </el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="mb-4">
      <el-radio-group v-model="statusFilter" size="default">
        <el-radio-button label="">All Tickets</el-radio-button>
        <el-radio-button label="upcoming">Upcoming</el-radio-button>
        <el-radio-button label="past">Past</el-radio-button>
        <el-radio-button label="cancelled">Cancelled</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- Tickets List -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="12" :lg="8" v-for="ticket in filteredTickets" :key="ticket.id" class="mb-4">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect" :class="{'opacity-75': ticket.status === 'cancelled'}">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <el-tag :type="getStatusType(ticket.status)" effect="dark">
              {{ ticket.status.toUpperCase() }}
            </el-tag>
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, ticket)">
              <el-icon class="cursor-pointer fs-5 text-secondary hover-primary"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="view">
                    <el-icon><View /></el-icon>
                    View Details
                  </el-dropdown-item>
                  <el-dropdown-item command="download" v-if="ticket.status !== 'cancelled'">
                    <el-icon><Download /></el-icon>
                    Download
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel" divided v-if="ticket.status === 'upcoming'">
                    <el-icon><Delete /></el-icon>
                    Cancel Ticket
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
              
              <div class="d-flex align-items-center gap-2 mb-2 small text-secondary">
                <el-icon><Location /></el-icon>
                <span>{{ ticket.cinema }} - Hall {{ ticket.hall }}</span>
              </div>
              
              <div class="d-flex align-items-center gap-2 small text-secondary">
                <el-icon><Tickets /></el-icon>
                <span>Seats: {{ ticket.seats.join(', ') }}</span>
              </div>
            </div>
          </div>

          <el-divider class="my-3"/>

          <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex flex-column">
              <span class="text-secondary small mb-1">Total Amount</span>
              <span class="fs-5 fw-bold text-primary">${{ ticket.totalPrice }}</span>
            </div>
            <el-button
              v-if="ticket.status === 'upcoming'"
              type="primary"
              size="small"
              @click="handleViewQR(ticket)"
            >
              Show QR Code
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Empty State -->
    <el-empty v-if="filteredTickets.length === 0" description="No tickets found">
      <el-button type="primary" @click="$router.push('/showtimes')">
        Book Your First Ticket
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
    poster: 'https://via.placeholder.com/200x300/667eea/ffffff?text=Avatar',
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
    poster: 'https://via.placeholder.com/200x300/764ba2/ffffff?text=TopGun',
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
    poster: 'https://via.placeholder.com/200x300/f093fb/ffffff?text=Batman',
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
    poster: 'https://via.placeholder.com/200x300/f5576c/ffffff?text=SpiderMan',
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
.hover-primary:hover {
  color: var(--el-color-primary) !important;
}
.cursor-pointer {
  cursor: pointer;
}
.object-fit-cover {
  object-fit: cover;
}
</style>
