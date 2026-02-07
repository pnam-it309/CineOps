<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fs-3 fw-bold text-dark">Showtime Management</h2>
        <p class="text-secondary mb-0 small">Manage movie screening schedules</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="dialogVisible = true">
        Add Showtime
      </el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="mb-4">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8" class="mb-2 mb-sm-0">
          <el-select v-model="movieFilter" placeholder="Select movie" clearable style="width: 100%">
            <el-option label="All Movies" value="" />
            <el-option label="Avatar: The Way of Water" value="avatar" />
            <el-option label="Top Gun: Maverick" value="topgun" />
            <el-option label="The Batman" value="batman" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="8" class="mb-2 mb-sm-0">
          <el-date-picker
            v-model="dateFilter"
            type="date"
            placeholder="Select date"
            style="width: 100%"
          />
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-select v-model="hallFilter" placeholder="Select hall" clearable style="width: 100%">
            <el-option label="All Halls" value="" />
            <el-option label="Hall 1" value="1" />
            <el-option label="Hall 2" value="2" />
            <el-option label="Hall 3" value="3" />
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- Showtimes Table -->
    <el-card shadow="never" class="border-0 shadow-sm" :body-style="{ padding: '0' }">
      <el-table :data="showtimes" style="width: 100%" stripe>
        <el-table-column prop="movieTitle" label="Movie" min-width="200" />
        
        <el-table-column prop="date" label="Date" width="120" />
        
        <el-table-column prop="time" label="Time" width="100" />
        
        <el-table-column prop="hall" label="Hall" width="100" />
        
        <el-table-column prop="capacity" label="Capacity" width="100" />
        
        <el-table-column prop="booked" label="Booked" width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="(row.booked / row.capacity) * 100"
              :color="getProgressColor((row.booked / row.capacity) * 100)"
              :format="() => `${row.booked}/${row.capacity}`"
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="Price" width="100">
          <template #default="{ row }">
            ${{ row.price }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="Actions" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="Edit" size="small" link>
              Edit
            </el-button>
            <el-button type="danger" :icon="Delete" size="small" link>
              Delete
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
      title="Add New Showtime"
      width="500px"
    >
      <el-form :model="showtimeForm" label-width="100px">
        <el-form-item label="Movie">
          <el-select v-model="showtimeForm.movie" placeholder="Select movie" style="width: 100%">
            <el-option label="Avatar: The Way of Water" value="avatar" />
            <el-option label="Top Gun: Maverick" value="topgun" />
            <el-option label="The Batman" value="batman" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Date">
          <el-date-picker
            v-model="showtimeForm.date"
            type="date"
            placeholder="Select date"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="Time">
          <el-time-picker
            v-model="showtimeForm.time"
            placeholder="Select time"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="Hall">
          <el-select v-model="showtimeForm.hall" placeholder="Select hall" style="width: 100%">
            <el-option label="Hall 1" value="1" />
            <el-option label="Hall 2" value="2" />
            <el-option label="Hall 3" value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Price">
          <el-input-number v-model="showtimeForm.price" :min="0" :step="0.5" style="width: 100%" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave">Save</el-button>
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
    hall: 'Hall 1',
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
    hall: 'Hall 2',
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
    hall: 'Hall 1',
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
    hall: 'Hall 3',
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
  ElMessage.success('Showtime saved successfully');
  dialogVisible.value = false;
};
</script>
