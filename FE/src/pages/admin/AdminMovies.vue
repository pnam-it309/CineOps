<script setup>
import { ref, computed } from 'vue';
import { Search, Plus, Edit, Delete, Filter, Picture } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { mockMovies } from '@/mock';

// State
const searchQuery = ref('');
const statusFilter = ref('all');
const dialogVisible = ref(false);
const editingMovie = ref(null);

const movieForm = ref({
  title: '',
  genre: '',
  duration: 120,
  rating: '8.5',
  status: 'NOW_SHOWING',
  poster: '',
  backdrop: '',
  description: ''
});

// Mock Movies List (Expanded from mockMovies)
const moviesList = ref([...mockMovies]);

const filteredMovies = computed(() => {
  return moviesList.value.filter(m => {
    const matchesSearch = m.title.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesStatus = statusFilter.value === 'all' || m.status === statusFilter.value;
    return matchesSearch && matchesStatus;
  });
});

const handleAdd = () => {
  editingMovie.value = null;
  movieForm.value = {
    title: '',
    genre: '',
    duration: 120,
    rating: '0.0',
    status: 'NOW_SHOWING',
    poster: '',
    backdrop: '',
    description: ''
  };
  dialogVisible.value = true;
};

const handleEdit = (movie) => {
  editingMovie.value = movie;
  movieForm.value = { ...movie };
  dialogVisible.value = true;
};

const handleDelete = (movie) => {
  ElMessageBox.confirm(`Are you sure you want to delete "${movie.title}"?`, 'Warning', {
    type: 'warning',
    confirmButtonClass: 'btn-danger'
  }).then(() => {
    moviesList.value = moviesList.value.filter(m => m.id !== movie.id);
    ElMessage.success('Movie deleted successfully');
  });
};

const saveMovie = () => {
  if (editingMovie.value) {
    const idx = moviesList.value.findIndex(m => m.id === editingMovie.value.id);
    moviesList.value[idx] = { ...movieForm.value };
    ElMessage.success('Movie updated');
  } else {
    moviesList.value.push({
      ...movieForm.value,
      id: Date.now()
    });
    ElMessage.success('Movie added');
  }
  dialogVisible.value = false;
};

const getStatusType = (status) => {
  return status === 'NOW_SHOWING' ? 'success' : 'info';
};

import BaseTable from '@/components/common/BaseTable.vue';

// Columns for BaseTable
const tableColumns = [
  { label: 'Ảnh poster', key: 'poster', width: '120px' },
  { label: 'Thông tin phim', key: 'title' },
  { label: 'Thể loại', key: 'genre' },
  { label: 'Thời lượng', key: 'duration' },
  { label: 'Trạng thái', key: 'status' }
];

// Pagination State
const currentPage = ref(1);
const pageSize = ref(10);
</script>

<template>
  <div class="movie-management w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fw-bold text-dark mb-1" style="font-size: 18px;">Quản lý Phim</h2>
      </div>
      <el-button type="primary" size="large" :icon="Plus" round @click="handleAdd">
        Thêm phim mới
      </el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 flex-shrink-0">
      <div class="row g-2 align-items-center">
        <div class="col-md-6 col-lg-4">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo tên phim..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="col-md-4 col-lg-3">
          <el-select v-model="statusFilter" placeholder="Lọc theo trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Đang chiếu" value="NOW_SHOWING" />
            <el-option label="Sắp chiếu" value="COMING_SOON" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- Table Container -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable 
        :data="filteredMovies"
        :columns="tableColumns"
        :total="filteredMovies.length"
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <template #cell-poster="{ row }">
          <img :src="row.poster" class="rounded object-fit-cover shadow-sm" style="width: 45px; height: 65px;">
        </template>

        <template #cell-title="{ row }">
          <div class="fw-bold fs-6 mb-0 text-start ps-3">{{ row.title }}</div>
          <div class="small text-secondary text-start ps-3">{{ row.rating }} / 10</div>
        </template>

        <template #cell-duration="{ row }">
          <span class="text-secondary small">{{ row.duration }}m</span>
        </template>

        <template #cell-status="{ row }">
          <el-tag :type="getStatusType(row.status)" effect="light" size="small">
            {{ row.status.replace('_', ' ') }}
          </el-tag>
        </template>
      </BaseTable>
    </div>

    <!-- ... rest of the file ... -->


    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingMovie ? 'Chỉnh sửa phim' : 'Thêm phim mới'"
      width="600px"
      class="rounded-4"
    >
      <el-form :model="movieForm" label-position="top">
        <div class="row g-3">
          <div class="col-md-12">
            <el-form-item label="Tên phim" required>
              <el-input v-model="movieForm.title" placeholder="VD: Avatar: Dòng chảy của nước" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Thể loại">
              <el-input v-model="movieForm.genre" placeholder="Hành động, Viễn tưởng" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Trạng thái">
              <el-select v-model="movieForm.status" class="w-100">
                <el-option label="Đang chiếu" value="NOW_SHOWING" />
                <el-option label="Sắp chiếu" value="COMING_SOON" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Thời lượng (phút)">
              <el-input-number v-model="movieForm.duration" :min="1" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Điểm IMDb">
              <el-input v-model="movieForm.rating" />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Link ảnh Poster">
              <el-input v-model="movieForm.poster" placeholder="https://..." />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Link ảnh Backdrop">
              <el-input v-model="movieForm.backdrop" placeholder="https://..." />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Mô tả nội dung">
              <el-input v-model="movieForm.description" type="textarea" :rows="3" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" @click="saveMovie" class="px-4">Lưu lại</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.object-fit-cover {
  object-fit: cover;
}
.table thead th {
  border-bottom: none;
}

.movie-management {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.no-scroll {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
  overflow: hidden !important;
}

.no-scroll::-webkit-scrollbar {
  display: none !important;
}

.overflow-auto.no-scroll {
  overflow-y: auto !important;
}
</style>
