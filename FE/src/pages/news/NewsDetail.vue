<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Calendar, User, ArrowLeft } from '@element-plus/icons-vue';

const route = useRoute();
const id = ref(route.params.id);

// Mock detail data
const article = ref({
  id: 1,
  title: "Ưu đãi cuối tuần: Giảm giá 20%",
  category: "Khuyến mãi",
  date: "5 Th2, 2026",
  author: "Đội ngũ CineOps",
  image: "https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=1200",
  content: `
    <h3>Tận hưởng cuối tuần kỳ ảo tại CineOps</h3>
    <p>Chúng tôi rất vui mừng thông báo chương trình khuyến mãi đặc biệt dành cho tất cả những người yêu điện ảnh. Cuối tuần này, từ Thứ Sáu đến Chủ Nhật, nhận ngay ưu đãi giảm giá 20% cho mỗi vé đặt qua website hoặc ứng dụng di động của chúng tôi.</p>
    <p>Cho dù bạn đang mong đợi một bom tấn hành động hay một bộ phim tâm lý gia đình cảm động, đây là thời điểm hoàn hảo để ghé thăm rạp CineOps gần nhất.</p>
    
    <h4>Điều khoản & Điều kiện:</h4>
    <ul>
      <li>Ưu đãi chỉ áp dụng cho đặt vé trực tuyến.</li>
      <li>Sử dụng mã giảm giá <strong>CINE20</strong> khi thanh toán.</li>
      <li>Mỗi người dùng có thể áp dụng mã nhiều lần trong thời gian khuyến mãi.</li>
      <li>Không áp dụng đồng thời với các chương trình khuyến mãi hoặc voucher khác.</li>
    </ul>

    <p>Đừng bỏ lỡ cơ hội vàng này. Hãy đặt chỗ ngay hôm nay và tận hưởng trải nghiệm điện ảnh tốt nhất cùng CineOps!</p>
  `,
  relatedNews: [
    { id: 2, title: "Sự kiện Fan: Fantastic Four", date: "1 Th2, 2026" },
    { id: 3, title: "Khám phá Thực đơn Mới của chúng tôi", date: "28 Th1, 2026" }
  ]
});

onMounted(() => {
  // Normally fetch from API using id.value
});
</script>

<template>
  <div class="news-detail py-5 bg-white">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-lg-10">
          <router-link to="/news" class="btn btn-link text-secondary text-decoration-none p-0 mb-4 d-flex align-items-center gap-2">
            <el-icon><ArrowLeft /></el-icon> Quay lại danh sách tin
          </router-link>

          <header class="mb-5">
            <el-tag class="mb-3 px-3 py-1 fw-bold" size="large">{{ article.category }}</el-tag>
            <h1 class="display-5 fw-bold mb-4">{{ article.title }}</h1>
            
            <div class="d-flex align-items-center gap-4 text-secondary small">
              <div class="d-flex align-items-center gap-2">
                <el-icon><Calendar /></el-icon>
                <span>{{ article.date }}</span>
              </div>
              <div class="d-flex align-items-center gap-2">
                <el-icon><User /></el-icon>
                <span>{{ article.author }}</span>
              </div>
            </div>
          </header>

          <img :src="article.image" class="w-100 rounded-5 shadow-sm mb-5" style="aspect-ratio: 2/1; object-fit: cover;">

          <div class="article-body px-md-4 mb-5">
            <div class="content fs-5 lh-lg" v-html="article.content"></div>
          </div>

          <div class="related-section border-top pt-5">
            <h4 class="fw-bold mb-4">Có thể bạn quan tâm</h4>
            <div class="row g-4">
              <div v-for="related in article.relatedNews" :key="related.id" class="col-md-6">
                <el-card shadow="hover" class="rounded-4 border-light cursor-pointer" @click="$router.push(`/news/${related.id}`)">
                  <h6 class="fw-bold mb-2">{{ related.title }}</h6>
                  <p class="text-secondary small mb-0">{{ related.date }}</p>
                </el-card>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.article-body :deep(h3) {
  font-weight: 700;
  margin-top: 2.5rem;
  margin-bottom: 1.5rem;
}

.article-body :deep(h4) {
  font-weight: 600;
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.article-body :deep(p) {
  margin-bottom: 1.5rem;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
