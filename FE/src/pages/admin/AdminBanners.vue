<script setup>
import { ref } from 'vue';
import { Plus, Picture, Delete, Edit, Rank, Check, View } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const banners = ref([
  { id: 1, title: 'Avatar: The Way of Water', image: 'https://dummyimage.com/1200x400?text=Avatar+Banner', link: '/movie/1', order: 1, status: true },
  { id: 2, title: 'Popcorn Combo Special', image: 'https://dummyimage.com/1200x400?text=Food+Banner', link: '/news/1', order: 2, status: true },
  { id: 3, title: 'New Member Offer', image: 'https://dummyimage.com/1200x400?text=Membership+Banner', link: '/register', order: 3, status: false }
]);

const handleStatusChange = (banner) => {
  ElMessage.success(`Banner "${banner.title}" status updated.`);
};
</script>

<template>
  <div class="admin-banners container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Quản lý Banners</h2>
      </div>
      <el-button type="primary" size="large" :icon="Plus" round>Thêm Banner</el-button>
    </div>

    <div class="row g-4">
      <div v-for="banner in banners" :key="banner.id" class="col-12">
        <el-card shadow="hover" class="border-0 shadow-sm rounded-4 overflow-hidden p-0">
          <div class="row g-0 align-items-center">
            <div class="col-md-4">
              <img :src="banner.image" class="w-100 object-fit-cover" style="height: 180px;">
            </div>
            <div class="col-md-5 p-4">
              <div class="d-flex align-items-center gap-2 mb-2">
                <el-tag size="small" effect="dark" type="info">Thứ tự #{{ banner.order }}</el-tag>
                <el-tag v-if="banner.status" size="small" type="success">Đang hiển thị</el-tag>
                <el-tag v-else size="small" type="danger">Đang ẩn</el-tag>
              </div>
              <h5 class="fw-bold mb-1">{{ banner.title }}</h5>
              <p class="text-secondary small mb-0">{{ banner.link }}</p>
            </div>
            <div class="col-md-3 p-4 border-start text-center">
              <div class="d-flex flex-column gap-2">
                <div class="d-flex align-items-center justify-content-center gap-2 mb-2">
                  <span class="small text-secondary fw-bold">Trạng thái:</span>
                  <el-switch v-model="banner.status" @change="handleStatusChange(banner)" />
                </div>
                <div class="d-flex gap-2 justify-content-center">
                  <el-button :icon="Edit" size="small" circle />
                  <el-button :icon="Rank" size="small" circle />
                  <el-button :icon="Delete" size="small" type="danger" plain circle />
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="mt-4 p-4 border border-2 border-dashed rounded-4 text-center bg-light cursor-pointer">
      <el-icon :size="48" class="text-secondary mb-2">
        <Plus />
      </el-icon>
      <div class="fw-bold">Tải lên ảnh Banner mới</div>
      <div class="text-secondary small">Kích thước khuyến nghị: 1920x600px</div>
    </div>
  </div>
</template>

<style scoped>
.object-fit-cover {
  object-fit: cover;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
