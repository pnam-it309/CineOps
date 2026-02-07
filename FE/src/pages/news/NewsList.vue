<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Collection, Gift, Calendar } from '@element-plus/icons-vue';

const router = useRouter();

const mockNews = ref([
  {
    id: 1,
    title: "Weekend Special: 20% Discount",
    category: "Promotion",
    date: "Feb 5, 2026",
    summary: "Enjoy 20% off on all movie tickets this weekend at any CineOps location. Book now and use code CINE20!",
    image: "https://via.placeholder.com/600x400/10b981/ffffff?text=Promo+Weekend",
    content: "Full content about weekend special discount..."
  },
  {
    id: 2,
    title: "Coming Soon: Marvel's Fantastic Four",
    category: "Event",
    date: "Feb 1, 2026",
    summary: "The heroes of our childhood are back. Join our fan event on March 15 to get exclusive merchandise and see it first!",
    image: "https://via.placeholder.com/600x400/3b82f6/ffffff?text=F4+Event",
    content: "Full content about Marvel event..."
  },
  {
    id: 3,
    title: "New Snack Menu Available",
    category: "News",
    date: "Jan 28, 2026",
    summary: "From Caramel Popcorn to Cheese fries, check out our updated concessions menu. More flavors, more fun!",
    image: "https://via.placeholder.com/600x400/f59e0b/ffffff?text=New+Menu",
    content: "Full content about new menu..."
  }
]);

const goToDetail = (id) => {
  router.push({ name: 'NewsDetail', params: { id } });
};
</script>

<template>
  <div class="news-list container py-5">
    <div class="d-flex justify-content-between align-items-center mb-5">
      <div>
        <h1 class="fw-bold mb-1">News & Events</h1>
        <p class="text-secondary mb-0">Stay updated with the latest promotions and cinema news.</p>
      </div>
      <div class="d-none d-md-flex gap-2">
        <el-tag size="large" type="primary" class="cursor-pointer">All</el-tag>
        <el-tag size="large" type="info" class="cursor-pointer">Promotions</el-tag>
        <el-tag size="large" type="info" class="cursor-pointer">Events</el-tag>
      </div>
    </div>

    <div class="row g-4">
      <div v-for="item in mockNews" :key="item.id" class="col-lg-4 col-md-6">
        <el-card 
          shadow="hover" 
          class="border-0 shadow-sm rounded-4 h-100 cursor-pointer overflow-hidden"
          :body-style="{ padding: '0px' }"
          @click="goToDetail(item.id)"
        >
          <div class="news-img-wrapper overflow-hidden">
            <img :src="item.image" class="w-100 news-img" style="aspect-ratio: 16/10; object-fit: cover;">
            <div class="item-category position-absolute top-0 start-0 m-3 px-3 py-1 bg-primary text-white rounded-pill small fw-bold">
              {{ item.category }}
            </div>
          </div>
          
          <div class="p-4">
            <div class="d-flex align-items-center gap-2 mb-2 text-secondary small">
              <el-icon><Calendar /></el-icon>
              <span>{{ item.date }}</span>
            </div>
            <h4 class="fw-bold mb-3 line-clamp-2 h-news-title">{{ item.title }}</h4>
            <p class="text-secondary small line-clamp-3 mb-0">{{ item.summary }}</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.news-img {
  transition: transform 0.5s ease;
}

.el-card:hover .news-img {
  transform: scale(1.1);
}

.news-img-wrapper {
  position: relative;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.h-news-title {
  min-height: 3rem;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
