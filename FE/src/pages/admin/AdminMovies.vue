<script setup>
import { ref, computed } from 'vue';
import { Search, Plus, Edit, Delete, Filter, Picture } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { mockMovies } from '@/mock/movies';

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
</script>

<template>
  <div class="movie-management container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Movie Management</h2>
        <p class="text-secondary small mb-0">Browse and manage your digital movie catalog.</p>
      </div>
      <el-button type="primary" size="large" :icon="Plus" round @click="handleAdd">
        Add New Movie
      </el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4 mb-4">
      <div class="row g-3 align-items-center">
        <div class="col-md-6 col-lg-4">
          <el-input
            v-model="searchQuery"
            placeholder="Search by title..."
            :prefix-icon="Search"
            size="large"
            clearable
          />
        </div>
        <div class="col-md-4 col-lg-3">
          <el-select v-model="statusFilter" placeholder="Filter by Status" size="large" class="w-100">
            <el-option label="All Statuses" value="all" />
            <el-option label="Now Showing" value="NOW_SHOWING" />
            <el-option label="Coming Soon" value="COMING_SOON" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- Movies Table -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th style="width: 80px;">Poster</th>
              <th>Movie Details</th>
              <th>Genre</th>
              <th>Duration</th>
              <th>Status</th>
              <th class="text-end" style="width: 150px;">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="movie in filteredMovies" :key="movie.id">
              <td>
                <img :src="movie.poster" class="rounded object-fit-cover shadow-sm" style="width: 60px; height: 85px;">
              </td>
              <td>
                <div class="fw-bold fs-6 mb-1">{{ movie.title }}</div>
                <div class="small text-secondary">{{ movie.rating }} / 10</div>
              </td>
              <td>{{ movie.genre }}</td>
              <td class="text-secondary small">{{ movie.duration }}m</td>
              <td>
                <el-tag :type="getStatusType(movie.status)" effect="light">
                  {{ movie.status.replace('_', ' ') }}
                </el-tag>
              </td>
              <td class="text-end">
                <el-button-group>
                  <el-button :icon="Edit" size="small" @click="handleEdit(movie)" />
                  <el-button :icon="Delete" size="small" type="danger" plain @click="handleDelete(movie)" />
                </el-button-group>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="filteredMovies.length === 0" class="py-5 text-center">
        <el-empty description="No movies match your search." />
      </div>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingMovie ? 'Edit Movie' : 'Add New Movie'"
      width="600px"
      class="rounded-4"
    >
      <el-form :model="movieForm" label-position="top">
        <div class="row g-3">
          <div class="col-md-12">
            <el-form-item label="Movie Title" required>
              <el-input v-model="movieForm.title" placeholder="e.g. Avatar: The Way of Water" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Genre">
              <el-input v-model="movieForm.genre" placeholder="Action, Sci-Fi" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Status">
              <el-select v-model="movieForm.status" class="w-100">
                <el-option label="Now Showing" value="NOW_SHOWING" />
                <el-option label="Coming Soon" value="COMING_SOON" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Duration (minutes)">
              <el-input-number v-model="movieForm.duration" :min="1" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Rating">
              <el-input v-model="movieForm.rating" />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Poster URL">
              <el-input v-model="movieForm.poster" placeholder="https://..." />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Backdrop URL">
              <el-input v-model="movieForm.backdrop" placeholder="https://..." />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Description">
              <el-input v-model="movieForm.description" type="textarea" :rows="3" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="saveMovie" class="px-4">Save Movie</el-button>
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
</style>
