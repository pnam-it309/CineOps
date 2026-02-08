<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fs-3 fw-bold text-dark">Đặt vé & Vé đã bán</h2>
        <p class="text-secondary mb-0 small">Quản lý các lượt đặt vé và bán vé của khách hàng</p>
      </div>
      <el-button type="primary" :icon="Plus">
        Đặt vé mới
      </el-button>
    </div>

    <!-- Search and Filter -->
    <el-card shadow="never" class="mb-4">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="10" class="mb-2 mb-md-0">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo mã đặt vé, số điện thoại hoặc email..."
            :prefix-icon="Search"
            clearable
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="mb-2 mb-md-0">
          <el-select v-model="statusFilter" placeholder="Lọc theo trạng thái" clearable style="width: 100%">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Đã xác nhận" value="confirmed" />
            <el-option label="Đang chờ" value="pending" />
            <el-option label="Đã hủy" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4" class="mb-2 mb-sm-0">
          <el-date-picker
            v-model="dateFilter"
            type="date"
            placeholder="Lọc theo ngày"
            style="width: 100%"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="primary" :icon="Search" style="width: 100%">Tìm kiếm</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- Bookings Table -->
    <el-card shadow="never" class="border-0 shadow-sm" :body-style="{ padding: '0' }">
      <el-table :data="bookings" style="width: 100%" stripe>
        <el-table-column prop="bookingCode" label="Mã Đặt vé" width="150" />
        
        <el-table-column prop="customerName" label="Khách hàng" min-width="150" />
        
        <el-table-column prop="movieTitle" label="Tên phim" min-width="200" />
        
        <el-table-column prop="showDate" label="Ngày chiếu" width="120" />
        
        <el-table-column prop="showTime" label="Suất chiếu" width="100" />
        
        <el-table-column prop="seats" label="Chỗ ngồi" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.seats.length }} ghế</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="totalPrice" label="Tổng tiền" width="120">
          <template #default="{ row }">
            {{ (row.totalPrice * 25000).toLocaleString() }}đ
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="Trạng thái" width="150">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status === 'confirmed' ? 'Đã xác nhận' : row.status === 'pending' ? 'Đang chờ' : 'Đã hủy' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="Hành động" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="View" size="small" link>
              Xem
            </el-button>
            <el-button type="success" :icon="Printer" size="small" link>
              In vé
            </el-button>
            <el-button type="danger" :icon="Delete" size="small" link v-if="row.status !== 'cancelled'">
              Hủy vé
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="p-4 d-flex justify-content-end border-top">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="10"
          layout="total, prev, pager, next"
          :total="bookings.length"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Plus, Search, View, Printer, Delete } from '@element-plus/icons-vue';

const searchQuery = ref('');
const statusFilter = ref('');
const dateFilter = ref('');
const currentPage = ref(1);

// Mock booking data
const bookings = ref([
  {
    id: 1,
    bookingCode: 'BK001234',
    customerName: 'John Doe',
    movieTitle: 'Avatar: The Way of Water',
    showDate: '2024-02-15',
    showTime: '19:30',
    seats: ['A5', 'A6'],
    totalPrice: 24.00,
    status: 'confirmed'
  },
  {
    id: 2,
    bookingCode: 'BK001235',
    customerName: 'Jane Smith',
    movieTitle: 'Top Gun: Maverick',
    showDate: '2024-02-15',
    showTime: '21:00',
    seats: ['C10', 'C11', 'C12'],
    totalPrice: 36.00,
    status: 'confirmed'
  },
  {
    id: 3,
    bookingCode: 'BK001236',
    customerName: 'Bob Wilson',
    movieTitle: 'The Batman',
    showDate: '2024-02-16',
    showTime: '18:00',
    seats: ['B7', 'B8'],
    totalPrice: 24.00,
    status: 'pending'
  },
  {
    id: 4,
    bookingCode: 'BK001237',
    customerName: 'Alice Johnson',
    movieTitle: 'Spider-Man',
    showDate: '2024-02-14',
    showTime: '20:30',
    seats: ['D5'],
    totalPrice: 12.00,
    status: 'cancelled'
  }
]);

const getStatusType = (status) => {
  const types = {
    confirmed: 'success',
    pending: 'warning',
    cancelled: 'info'
  };
  return types[status] || 'info';
};
</script>
