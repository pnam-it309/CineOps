<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Calendar, User, ArrowLeft } from '@element-plus/icons-vue';

const route = useRoute();
const id = ref(route.params.id);

// Mock detail data
const article = ref({
  id: 1,
  title: "Weekend Special: 20% Discount",
  category: "Promotion",
  date: "Feb 5, 2026",
  author: "CineOps Team",
  image: "https://via.placeholder.com/1200x600/10b981/ffffff?text=Promo+Weekend",
  content: `
    <h3>Enjoy a magical weekend at CineOps</h3>
    <p>We are excited to announce a special promotion for all our movie lovers. This weekend, from Friday to Sunday, get 20% off on every ticket booked through our website or mobile app.</p>
    <p>Whether you're excited about the latest blockbuster or a touching family drama, now is the perfect time to visit your nearest CineOps location.</p>
    
    <h4>Terms & Conditions:</h4>
    <ul>
      <li>Offer is valid for online bookings only.</li>
      <li>Use discount code <strong>CINE20</strong> at checkout.</li>
      <li>Each user can apply the code multiple times during the promotion period.</li>
      <li>Not valid in conjunction with other promotions or vouchers.</li>
    </ul>

    <p>Don't miss out on this golden opportunity. Grab your seats today and enjoy the best cinema experience with CineOps!</p>
  `,
  relatedNews: [
    { id: 2, title: "Fantastic Four Fan Event", date: "Feb 1, 2026" },
    { id: 3, title: "Check Out Our New Menu", date: "Jan 28, 2026" }
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
            <el-icon><ArrowLeft /></el-icon> Back to All news
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
            <h4 class="fw-bold mb-4">You might also be interested in</h4>
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
