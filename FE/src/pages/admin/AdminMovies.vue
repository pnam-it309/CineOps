<script setup>
import { ref, computed } from 'vue';
import { Search, Plus, Edit, Delete, Filter, Picture, VideoPlay } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { mockMovies } from '@/assets/mock';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

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

// Pagination State
const currentPage = ref(1);
const pageSize = ref(10);
</script>

<template>
  <div class="admin-movies-page">
    <AdminTableLayout
      title="Quản lý Phim"
      titleIcon="bi bi-film"
      addButtonLabel="Thêm phim mới"
      :data="filteredMovies.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      :total="filteredMovies.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="handleAdd"
      @reset-filter="() => { searchQuery = ''; statusFilter = 'all'; }"
    >
      <template #filters>
        <div class="filter-item" style="width: 300px;">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo tên phim..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="statusFilter" placeholder="Trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Đang chiếu" value="NOW_SHOWING" />
            <el-option label="Sắp chiếu" value="COMING_SOON" />
          </el-select>
        </div>
      </template>

      <template #columns>
        <el-table-column label="Thông tin phim" min-width="300">
          <template #default="{ row }">
            <div class="d-flex align-items-center gap-3 w-100">
              <img :src="row.poster" class="rounded shadow-sm" style="width: 45px; height: 65px; object-fit: cover;">
              <div class="text-start">
                <div class="fw-bold text-dark">{{ row.title }}</div>
                <div class="small text-secondary">{{ row.rating }} / 10</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="Thể loại" prop="genre" width="180" align="center" />
        <el-table-column label="Thời lượng" width="120" align="center">
          <template #default="{ row }">
            <span class="text-secondary small">{{ row.duration }}m</span>
          </template>
        </el-table-column>
        
        <el-table-column label="Trạng thái" width="150" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light" size="small" round>
              {{ row.status.replace('_', ' ') }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Thao tác" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <button class="btn-action-icon btn-action-edit" @click="handleEdit(row)">
                <i class="bi bi-pencil fs-6"></i>
              </button>
              <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)">
                <i class="bi bi-trash fs-6"></i>
              </button>
            </div>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      width="680px"
      destroy-on-close
      class="premium-dialog"
    >
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i :class="editingMovie ? 'bi bi-pencil-square' : 'bi bi-plus-lg'"></i>
            </div>
            <div class="header-text">
              <h5 class="title">{{ editingMovie ? 'Chỉnh sửa Phim' : 'Thêm Phim mới' }}</h5>
              <p class="subtitle opacity-75">Quản lý kho nội dung điện ảnh</p>
            </div>
          </div>
        </div>
      </template>

      <el-form :model="movieForm" label-position="top" class="premium-form">
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
          <el-button @click="dialogVisible = false" class="btn-premium-secondary">Hủy</el-button>
          <el-button @click="saveMovie" class="btn-premium-primary">Lưu lại</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* Redundant scoped styles removed as they are handled by admin-premium.css and AdminTableLayout.vue */
</style>
