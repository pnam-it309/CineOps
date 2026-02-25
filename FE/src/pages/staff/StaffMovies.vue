<template>
  <div class="container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
      <div>
        <h2 class="mb-2 fw-bold text-dark" style="font-size: 18px;">Quản lý Phim</h2>
        <p class="text-secondary mb-0 small">Quản lý danh mục và chi tiết các bộ phim</p>
      </div>
      <el-button type="success" :icon="Plus" @click="dialogVisible = true">
        Thêm phim mới
      </el-button>
    </div>

    <!-- View Toggle and Search -->
    <el-card shadow="never" class="mb-4">
      <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
        <el-input
          v-model="searchQuery"
          placeholder="Tìm phim theo tên..."
          :prefix-icon="Search"
          style="width: 300px"
          class="w-100 w-sm-auto"
          clearable
        />
        <el-radio-group v-model="viewMode">
          <el-radio-button value="grid">
            <el-icon><Grid /></el-icon>
            Lưới
          </el-radio-button>
          <el-radio-button value="list">
            <el-icon><List /></el-icon>
            Danh sách
          </el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <!-- Grid View -->
    <div class="row g-4" v-if="viewMode === 'grid'">
      <div class="col-12 col-sm-6 col-md-4 col-lg-3" v-for="movie in movies" :key="movie.id">
        <el-card shadow="hover" class="h-100 border-0 shadow-sm card-hover-effect" :body-style="{ padding: '1rem' }">
          <div class="position-relative mb-3 rounded overflow-hidden" style="padding-top: 150%;">
            <img :src="movie.poster" :alt="movie.title" class="position-absolute top-0 start-0 w-100 h-100 object-fit-cover" />
            <div class="position-absolute top-0 start-0 w-100 h-100 bg-dark bg-opacity-75 d-flex align-items-center justify-content-center gap-2 opacity-0 hover-opacity-100 transition-opacity">
              <el-button type="primary" circle :icon="Edit" @click="handleEdit(movie)" />
              <el-button type="danger" circle :icon="Delete" @click="handleDelete(movie)" />
            </div>
          </div>
          <div>
            <h3 class="fs-6 fw-bold mb-1 text-truncate" :title="movie.title">{{ movie.title }}</h3>
            <p class="text-muted small mb-2">{{ movie.genre }} • {{ movie.duration }}</p>
            <el-tag :type="movie.status === 'showing' ? 'success' : 'info'" size="small">
              {{ movie.status === 'showing' ? 'Đang chiếu' : movie.status === 'ended' ? 'Đã kết thúc' : 'Sắp chiếu' }}
            </el-tag>
          </div>
        </el-card>
      </div>
    </div>

    <!-- List View -->
    <el-card shadow="never" v-else class="border-0 shadow-sm">
      <el-table :data="movies" style="width: 100%">
        <el-table-column prop="title" label="Tên phim" min-width="200" />
        <el-table-column prop="genre" label="Thể loại" width="150" />
        <el-table-column prop="duration" label="Thời lượng" width="120" />
        <el-table-column prop="releaseDate" label="Ngày phát hành" width="150" />
        <el-table-column prop="status" label="Trạng thái" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'showing' ? 'success' : 'info'">
              {{ row.status === 'showing' ? 'Đang chiếu' : row.status === 'ended' ? 'Đã kết thúc' : 'Sắp chiếu' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Hành động" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="Edit" size="small" link @click="handleEdit(row)">
              Sửa
            </el-button>
            <el-button type="danger" :icon="Delete" size="small" link @click="handleDelete(row)">
              Xóa
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Movie Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="movieForm.id ? 'Chỉnh sửa phim' : 'Thêm phim mới'"
      width="600px"
    >
      <el-form :model="movieForm" label-width="120px">
        <el-form-item label="Tên phim">
          <el-input v-model="movieForm.title" placeholder="Nhập tên phim" />
        </el-form-item>
        
        <el-form-item label="Thể loại">
          <el-input v-model="movieForm.genre" placeholder="VD: Hành động, Tâm lý" />
        </el-form-item>
        
        <el-form-item label="Thời lượng">
          <el-input v-model="movieForm.duration" placeholder="VD: 2 giờ 15 phút" />
        </el-form-item>
        
        <el-form-item label="Ngày phát hành">
          <el-date-picker
            v-model="movieForm.releaseDate"
            type="date"
            placeholder="Chọn ngày"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="Trạng thái">
          <el-select v-model="movieForm.status" style="width: 100%">
            <el-option label="Đang chiếu" value="showing" />
            <el-option label="Sắp chiếu" value="coming-soon" />
            <el-option label="Đã kết thúc" value="ended" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">Hủy</el-button>
        <el-button type="primary" @click="handleSave">Lưu phim</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Plus, Search, Grid, List, Edit, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchQuery = ref('');
const viewMode = ref('grid');
const dialogVisible = ref(false);

const movieForm = ref({
  title: '',
  genre: '',
  duration: '',
  releaseDate: '',
  status: 'showing'
});

// Mock movie data
const movies = ref([
  {
    id: 1,
    title: 'Avatar: The Way of Water',
    genre: 'Action, Sci-Fi',
    duration: '3h 12m',
    releaseDate: '2024-01-15',
    status: 'showing',
    poster: 'https://dummyimage.com/300x450/667eea/ffffff?text=Avatar'
  },
  {
    id: 2,
    title: 'Top Gun: Maverick',
    genre: 'Action, Drama',
    duration: '2h 10m',
    releaseDate: '2024-01-20',
    status: 'showing',
    poster: 'https://dummyimage.com/300x450/764ba2/ffffff?text=TopGun'
  },
  {
    id: 3,
    title: 'The Batman',
    genre: 'Action, Crime',
    duration: '2h 56m',
    releaseDate: '2024-02-01',
    status: 'showing',
    poster: 'https://dummyimage.com/300x450/f093fb/ffffff?text=Batman'
  },
  {
    id: 4,
    title: 'Spider-Man: No Way Home',
    genre: 'Action, Adventure',
    duration: '2h 28m',
    releaseDate: '2023-12-15',
    status: 'ended',
    poster: 'https://dummyimage.com/300x450/f5576c/ffffff?text=SpiderMan'
  }
]);

const handleEdit = (movie) => {
  ElMessage.info(`Edit movie: ${movie.title}`);
};

const handleDelete = (movie) => {
  ElMessageBox.confirm(
    `Delete movie "${movie.title}"?`,
    'Confirm Delete',
    {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('Movie deleted');
  });
};

const handleSave = () => {
  ElMessage.success('Movie saved successfully');
  dialogVisible.value = false;
};
</script>

<style scoped>
.hover-opacity-100:hover {
  opacity: 1 !important;
}
.transition-opacity {
  transition: opacity 0.3s ease;
}
.object-fit-cover {
  object-fit: cover;
}
.w-sm-auto {
  @media (min-width: 576px) {
    width: auto !important;
  }
}
</style>
