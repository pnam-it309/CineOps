<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fw-bold text-dark" style="font-size: 18px;">Quản lý Lịch chiếu</h2>
        <p class="text-secondary mb-0 small">Quản lý lịch chiếu phim và các suất chiếu</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="dialogVisible = true">
        Thêm suất chiếu
      </el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="mb-4">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8" class="mb-2 mb-sm-0">
          <el-select v-model="movieFilter" placeholder="Chọn phim" clearable style="width: 100%">
            <el-option label="Tất cả phim" value="" />
            <el-option label="Avatar: The Way of Water" value="avatar" />
            <el-option label="Top Gun: Maverick" value="topgun" />
            <el-option label="The Batman" value="batman" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="8" class="mb-2 mb-sm-0">
          <el-date-picker
            v-model="dateFilter"
            type="date"
            placeholder="Chọn ngày"
            style="width: 100%"
          />
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-select v-model="hallFilter" placeholder="Chọn phòng chiếu" clearable style="width: 100%">
            <el-option label="Tất cả phòng" value="" />
            <el-option label="Phòng 1" value="1" />
            <el-option label="Phòng 2" value="2" />
            <el-option label="Phòng 3" value="3" />
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- Showtimes Table -->
    <el-card shadow="never" class="border-0 shadow-sm" :body-style="{ padding: '0' }">
      <el-table :data="showtimes" style="width: 100%" stripe>
        <el-table-column prop="movieTitle" label="Tên phim" min-width="200" />
        
        <el-table-column prop="date" label="Ngày" width="120" />
        
        <el-table-column prop="time" label="Giờ" width="100" />
        
        <el-table-column prop="hall" label="Phòng chiếu" width="120" />
        
        <el-table-column prop="capacity" label="Sức chứa" width="100" />
        
        <el-table-column prop="booked" label="Đã đặt" width="120">
          <template #default="{ row }">
            <el-progress
              :percentage="(row.booked / row.capacity) * 100"
              :color="getProgressColor((row.booked / row.capacity) * 100)"
              :format="() => `${row.booked}/${row.capacity}`"
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="Giá vé" width="120">
          <template #default="{ row }">
            {{ (row.price * 25000).toLocaleString() }}đ
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="Trạng thái" width="150">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status === 'active' ? 'Đang hoạt động' : (row.status === 'cancelled' ? 'Đã hủy' : 'Đã kết thúc') }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="Hành động" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="Edit" size="small" link>
              Sửa
            </el-button>
            <el-button type="danger" :icon="Delete" size="small" link>
              Xóa
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
          :total="showtimes.length"
        />
      </div>
    </el-card>

    <!-- Add Showtime Dialog -->
    <el-dialog
      v-model="dialogVisible"
      title="Thêm suất chiếu mới"
      width="500px"
    >
      <el-form :model="showtimeForm" label-width="100px">
        <el-form-item label="Phim">
          <el-select v-model="showtimeForm.movie" placeholder="Chọn phim" style="width: 100%">
            <el-option label="Avatar: The Way of Water" value="avatar" />
            <el-option label="Top Gun: Maverick" value="topgun" />
            <el-option label="The Batman" value="batman" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Ngày chiếu">
          <el-date-picker
            v-model="showtimeForm.date"
            type="date"
            placeholder="Chọn ngày"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="Giờ chiếu">
          <el-time-picker
            v-model="showtimeForm.time"
            placeholder="Chọn giờ"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="Phòng chiếu">
          <el-select v-model="showtimeForm.hall" placeholder="Chọn phòng" style="width: 100%">
            <el-option label="Phòng 1" value="1" />
            <el-option label="Phòng 2" value="2" />
            <el-option label="Phòng 3" value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Giá vé">
          <el-input-number v-model="showtimeForm.price" :min="0" :step="1000" style="width: 100%" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">Hủy</el-button>
        <el-button type="primary" @click="handleSave">Lưu suất chiếu</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const movieFilter = ref('');
const dateFilter = ref('');
const hallFilter = ref('');
const currentPage = ref(1);
const dialogVisible = ref(false);

const showtimeForm = ref({
  movie: '',
  date: '',
  time: '',
  hall: '',
  price: 12.00
});

// Mock showtime data
const showtimes = ref([
  {
    id: 1,
    movieTitle: 'Avatar: The Way of Water',
    date: '2024-02-15',
    time: '19:30',
    hall: 'Phòng 1',
    capacity: 150,
    booked: 120,
    price: 12.00,
    status: 'active'
  },
  {
    id: 2,
    movieTitle: 'Top Gun: Maverick',
    date: '2024-02-15',
    time: '21:00',
    hall: 'Phòng 2',
    capacity: 100,
    booked: 45,
    price: 12.00,
    status: 'active'
  },
  {
    id: 3,
    movieTitle: 'The Batman',
    date: '2024-02-16',
    time: '18:00',
    hall: 'Phòng 1',
    capacity: 150,
    booked: 80,
    price: 12.00,
    status: 'active'
  },
  {
    id: 4,
    movieTitle: 'Avatar: The Way of Water',
    date: '2024-02-16',
    time: '22:00',
    hall: 'Phòng 3',
    capacity: 120,
    booked: 5,
    price: 10.00,
    status: 'active'
  }
]);

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#f56c6c';
  if (percentage >= 50) return '#e6a23c';
  return '#67c23a';
};

const getStatusType = (status) => {
  const types = {
    active: 'success',
    cancelled: 'danger',
    ended: 'info'
  };
  return types[status] || 'info';
};

const handleSave = () => {
  ElMessage.success('Đã lưu suất chiếu thành công');
  dialogVisible.value = false;
};
</script>
