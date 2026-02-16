<template>
  <div class="homepage bg-dark-primary">
    <!-- Hero Bento Section -->
    <section class="hero-bento-section container-xl py-5" ref="bentoSectionRef">
      <!-- Animated Background Orbs -->
      <div class="hero-bg-orbs">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>

      <!-- Row 1: Hero (2x2) + Stats (1x1 each) -->
      <div class="row g-4">
        <!-- Hero Card — auto-rotating featured movie -->
        <div class="col-lg-8 hero-fade-in" :class="{ 'auto-glow': activeGlowIndex === 0 }">
          <div class="bento-card bento-card--hero p-5 h-100 d-flex flex-column justify-content-end position-relative overflow-hidden">
            <transition name="hero-img" mode="out-in">
              <img :src="currentHero.backdrop || currentHero.poster" :alt="currentHero.title" class="featured-bg" :key="'bg-' + currentHero.id">
            </transition>
            <div class="featured-overlay-gradient"></div>
            <div class="featured-overlay-vignette"></div>

            <!-- Slide indicator dots -->
            <div class="hero-dots position-absolute z-3">
              <span v-for="(m, i) in featuredMovies" :key="m.id"
                class="hero-dot" :class="{ active: i === heroIndex }"
                @click="heroIndex = i"></span>
            </div>

            <transition name="hero-text" mode="out-in">
              <div class="position-relative z-3" :key="'txt-' + currentHero.id">
                <span class="bento-badge bento-badge--featured mb-3 d-inline-block">⚡ Featured</span>
                <h1 class="display-3 fw-bold text-white mb-2 hero-title">{{ currentHero.title }}</h1>
                <p class="small text-white-50 mb-1">{{ currentHero.vietnameseTitle }}</p>
                <div class="d-flex gap-3 align-items-center mb-3 flex-wrap">
                  <span class="badge bg-warning text-dark" v-if="currentHero.imdbRating">★ {{ currentHero.imdbRating }}</span>
                  <span class="text-secondary small">{{ currentHero.genre }}</span>
                  <span class="text-secondary small">{{ currentHero.duration }} phút</span>
                  <span class="badge bg-secondary">{{ currentHero.rating }}</span>
                </div>
                <p class="fs-6 text-white-75 mb-4 hero-synopsis">{{ currentHero.synopsis }}</p>
                <button class="bento-btn-book" @click="goToMovie(currentHero)">
                  <span class="bento-btn-book__icon">▶</span>
                  Đặt Vé Ngay
                </button>
              </div>
            </transition>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="row g-4 h-100">
            <!-- Stat Card 1 -->
            <div class="col-12 hero-fade-in" style="animation-delay: 0.2s" :class="{ 'auto-glow': activeGlowIndex === 1 }">
              <div class="bento-card bento-card--stat h-100 d-flex flex-column align-items-center justify-content-center text-center p-4">
                <div class="accent-dot-pulse mb-3"></div>
                <h2 class="display-3 fw-bold text-gradient-animated mb-2">{{ displayTickets }}</h2>
                <p class="text-secondary mb-0 fw-medium">Phim Đang Chiếu</p>
              </div>
            </div>

            <!-- Stat Card 2 -->
            <div class="col-12 hero-fade-in" style="animation-delay: 0.3s" :class="{ 'auto-glow': activeGlowIndex === 2 }">
              <div class="bento-card bento-card--stat h-100 d-flex flex-column align-items-center justify-content-center text-center p-4">
                <div class="accent-dot-pulse mb-3"></div>
                <h2 class="display-3 fw-bold text-gradient-animated mb-2">{{ displayUsers }}</h2>
                <p class="text-secondary mb-0 fw-medium">Khách Hàng Hài Lòng</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 2: Trending (2x1) + Promo (1x1) -->
      <div class="row g-4 mt-0">
        <!-- Trending Card — auto-rotating -->
        <div class="col-lg-8 hero-fade-in" style="animation-delay: 0.4s" :class="{ 'auto-glow': activeGlowIndex === 3 }">
          <div class="bento-card bento-card--trending p-4 h-100 d-flex align-items-center gap-4">
            <transition name="trending-img" mode="out-in">
              <div class="trending-img-wrapper flex-shrink-0" :key="'timg-' + currentTrending.id">
                <img :src="currentTrending.poster" :alt="currentTrending.title" class="rounded-4 hover-scale-img" style="width: 120px; height: 180px; object-fit: cover;">
              </div>
            </transition>
            <transition name="hero-text" mode="out-in">
              <div class="flex-grow-1" :key="'ttxt-' + currentTrending.id">
                <span class="bento-badge bento-badge--trending mb-2 d-inline-block">🔥 Trending</span>
                <h3 class="h4 fw-bold text-white mb-1">{{ currentTrending.title }}</h3>
                <p class="small text-white-50 mb-2">{{ currentTrending.vietnameseTitle }}</p>
                <p class="text-secondary small mb-3 lh-base line-clamp-2">{{ currentTrending.synopsis }}</p>
                <div class="d-flex gap-3 align-items-center">
                  <div class="d-flex align-items-center gap-1" v-if="currentTrending.imdbRating">
                    <span style="color: #FFBA00;">★</span>
                    <span class="text-white fw-bold">{{ currentTrending.imdbRating }}</span>
                  </div>
                  <span class="text-secondary">|</span>
                  <span class="text-secondary small">{{ currentTrending.genre }}</span>
                </div>
              </div>
            </transition>
            <button class="bento-btn-outline d-none d-md-block" @click="goToMovie(currentTrending)">Chi tiết</button>
          </div>
        </div>

        <!-- Promo Square Card -->
        <div class="col-lg-4 hero-fade-in" style="animation-delay: 0.5s" :class="{ 'auto-glow': activeGlowIndex === 4 }">
          <div class="bento-card bento-card--promo h-100 d-flex flex-column align-items-center justify-content-center text-center p-4 position-relative overflow-hidden">
            <div class="promo-shimmer"></div>
            <div class="position-relative z-2">
              <span class="promo-emoji">🍿</span>
              <h4 class="fw-bold text-white mb-2">Combo Bắp Nước</h4>
              <p class="text-secondary small mb-3">Giảm 30% khi mua combo cùng vé xem phim</p>
              <button class="bento-btn-promo">Xem ngay</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Movies Grid Section -->
    <section class="movies-section py-5">
      <div class="container-xl d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="fs-1 fw-bold text-white mb-1">
            Phim <span class="text-gradient">Đang Chiếu</span>
          </h2>
          <div class="accent-line" style="width: 80px;"></div>
        </div>

        <el-radio-group v-model="activeTab" size="large" class="dark-toggle">
          <el-radio-button value="now-showing">Đang Chiếu</el-radio-button>
          <el-radio-button value="coming-soon">Sắp Chiếu</el-radio-button>
        </el-radio-group>
      </div>

      <swiper
        :key="activeTab"
        :modules="[Autoplay, Navigation]"
        :slides-per-view="1.8"
        :space-between="10"
        :autoplay="{ delay: 1200, disableOnInteraction: false, pauseOnMouseEnter: true }"
        :speed="600"
        :loop="true"
        :observer="true"
        :observe-parents="true"
        :breakpoints="{
          480: { slidesPerView: 2.5, spaceBetween: 10 },
          576: { slidesPerView: 3.3, spaceBetween: 12 },
          768: { slidesPerView: 4.3, spaceBetween: 12 },
          992: { slidesPerView: 5.3, spaceBetween: 14 },
          1200: { slidesPerView: 6.3, spaceBetween: 14 },
          1400: { slidesPerView: 7.3, spaceBetween: 14 }
        }"
        navigation
        class="movie-swiper pb-4"
        v-if="filteredMovies.length > 0"
      >
        <swiper-slide v-for="movie in filteredMovies" :key="movie.id" class="h-auto">
          <MovieCard :movie="movie" class="h-100" @click="goToMovie" />
        </swiper-slide>
      </swiper>

      <el-empty v-if="filteredMovies.length === 0" description="Không có phim nào khả dụng" class="text-white" />
    </section>

    <!-- Promotions Bento Section -->
    <section class="promotions-section container-xl py-5">
      <div class="mb-5">
        <h2 class="fs-1 fw-bold text-white mb-1">
          Ưu Đãi <span class="text-gradient">Đặc Biệt</span>
        </h2>
        <div class="accent-line" style="width: 100px;"></div>
      </div>

      <el-row :gutter="20">
        <el-col :xs="24" :md="8" v-for="promo in promotions" :key="promo.id" class="mb-4">
          <div class="promo-card dark-card h-100 hover-lift cursor-pointer">
            <img :src="promo.image" :alt="promo.title" class="w-100 rounded-3 mb-3" style="height: 200px; object-fit: cover;" />
            <div class="p-3">
              <h4 class="fw-bold text-white mb-2">{{ promo.title }}</h4>
              <p class="text-secondary small mb-3">{{ promo.description }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <el-tag class="btn-gradient">{{ promo.code }}</el-tag>
                <span class="text-tertiary small">Đến {{ formatDate(promo.validUntil) }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- News Section -->
    <section class="news-section container-xl py-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="fs-1 fw-bold text-white mb-1">
            Tin Tức & <span class="text-gradient">Sự Kiện</span>
          </h2>
          <div class="accent-line" style="width: 100px;"></div>
        </div>
        <el-button type="primary" class="btn-gradient-outline" @click="router.push('/news')">
          Xem tất cả
          <el-icon class="ms-1"><ArrowRight /></el-icon>
        </el-button>
      </div>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="8" v-for="news in latestNews" :key="news.id" class="mb-4">
          <div class="news-card glass p-4 h-100 hover-lift cursor-pointer" @click="router.push(`/news/${news.id}`)">
            <img :src="news.image" class="w-100 rounded-3 mb-3" style="height: 160px; object-fit: cover;">
            <div class="d-flex align-items-center gap-2 text-secondary small mb-2">
              <el-icon><Calendar /></el-icon>
              <span>{{ news.date }}</span>
            </div>
            <h5 class="fw-bold text-white mb-2 line-clamp-2">{{ news.title }}</h5>
            <p class="text-secondary small mb-0 line-clamp-3">{{ news.summary }}</p>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Calendar, ArrowRight } from '@element-plus/icons-vue';
import MovieCard from '@/components/common/MovieCard.vue';
import { mockMovies, mockPromotions } from '@/mock';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Autoplay, Navigation } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';

const router = useRouter();
const activeTab = ref('now-showing');

const movies = ref(mockMovies);
const promotions = ref(mockPromotions);
const latestNews = ref([
  { id: 1, title: "Ưu đãi cuối tuần: Giảm giá 20%", date: "14 Th2, 2026", summary: "Nhận ngay ưu đãi giảm giá 20% cho mỗi vé đặt qua website hoặc ứng dụng di động. Áp dụng cho tất cả các suất chiếu từ thứ 6 đến chủ nhật.", image: "https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=600" },
  { id: 2, title: "Sự kiện Fan: Fantastic Four Premiere", date: "12 Th2, 2026", summary: "Tham gia sự kiện ra mắt phim Fantastic Four với các diễn viên và đạo diễn. Nhận quà tặng độc quyền và cơ hội chụp ảnh cùng dàn cast.", image: "https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=600" },
  { id: 3, title: "Thực đơn mới: Combo Sweet Valentine", date: "10 Th2, 2026", summary: "Khám phá combo bắp nước mới đặc biệt dành cho mùa Valentine với nhiều hương vị độc đáo và giá ưu đãi.", image: "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=600" }
]);

// ── Data-driven hero & trending from mockMovies ──
const nowShowingMovies = computed(() => movies.value.filter(m => m.status === 'now-showing'));

// Featured: top-rated now-showing movies (for hero slider)
const featuredMovies = computed(() =>
  [...nowShowingMovies.value]
    .sort((a, b) => (b.imdbRating || 0) - (a.imdbRating || 0))
    .slice(0, 6)
);

// Trending: remaining now-showing movies (for trending slider)
const trendingMovies = computed(() => {
  const featuredIds = new Set(featuredMovies.value.map(m => m.id));
  const remaining = nowShowingMovies.value.filter(m => !featuredIds.has(m.id));
  return remaining.length > 0 ? remaining : nowShowingMovies.value.slice(0, 5);
});

// ── Hero auto-rotate ──
const heroIndex = ref(0);
let heroInterval = null;
const currentHero = computed(() => featuredMovies.value[heroIndex.value] || featuredMovies.value[0]);

function startHeroRotation() {
  heroInterval = setInterval(() => {
    heroIndex.value = (heroIndex.value + 1) % featuredMovies.value.length;
  }, 5000);
}

function stopHeroRotation() {
  if (heroInterval) { clearInterval(heroInterval); heroInterval = null; }
}

// Reset timer when user clicks a dot
watch(heroIndex, () => {
  stopHeroRotation();
  startHeroRotation();
});

// ── Trending auto-rotate ──
const trendingIndex = ref(0);
let trendingInterval = null;
const currentTrending = computed(() => trendingMovies.value[trendingIndex.value] || trendingMovies.value[0]);

function startTrendingRotation() {
  trendingInterval = setInterval(() => {
    trendingIndex.value = (trendingIndex.value + 1) % trendingMovies.value.length;
  }, 4000);
}

function stopTrendingRotation() {
  if (trendingInterval) { clearInterval(trendingInterval); trendingInterval = null; }
}

// ── Filtered movies for swiper ──
const filteredMovies = computed(() => movies.value.filter(m => m.status === activeTab.value));

const goToMovie = (movie) => {
  router.push({ name: 'MovieDetails', params: { id: movie.id } });
};

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN', { day: 'numeric', month: 'short', year: 'numeric' });
};

// ── Animated Counters ──
const ticketsSold = ref(0);
const activeUsers = ref(0);
const TICKET_TARGET = computed(() => nowShowingMovies.value.length);
const USER_TARGET = 1000;

const displayTickets = computed(() => ticketsSold.value + '+');
const displayUsers = computed(() => activeUsers.value + '+');

function animateCounter(targetVal, refVal, duration = 2000) {
  const startTime = performance.now();
  const step = (now) => {
    const elapsed = now - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3);
    refVal.value = Math.floor(eased * targetVal);
    if (progress < 1) requestAnimationFrame(step);
  };
  requestAnimationFrame(step);
}

// ── Auto-Pulse Logic ──
const activeGlowIndex = ref(-1);
let pulseInterval = null;
const CARD_COUNT = 5;

function startAutoPulse() {
  pulseInterval = setInterval(() => {
    const idx = Math.floor(Math.random() * CARD_COUNT);
    activeGlowIndex.value = idx;
    setTimeout(() => { activeGlowIndex.value = -1; }, 1500);
  }, 3000);
}

function stopAutoPulse() {
  if (pulseInterval) { clearInterval(pulseInterval); pulseInterval = null; }
}

// ── Intersection Observer ──
const bentoSectionRef = ref(null);
let observer = null;
let hasAnimated = false;

onMounted(() => {
  observer = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting && !hasAnimated) {
        hasAnimated = true;
        animateCounter(TICKET_TARGET.value, ticketsSold);
        animateCounter(USER_TARGET, activeUsers);
        startAutoPulse();
        startHeroRotation();
        startTrendingRotation();
      }
    },
    { threshold: 0.2 }
  );
  if (bentoSectionRef.value) observer.observe(bentoSectionRef.value);
});

onUnmounted(() => {
  stopAutoPulse();
  stopHeroRotation();
  stopTrendingRotation();
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.homepage { min-height: 100vh; }

/* ── Base Bento Card ── */
.bento-card {
  background: #1E1E1E;
  border-radius: 24px;
  border: 2px solid transparent;
  position: relative;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bento-card::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 26px;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  opacity: 0;
  z-index: -1;
  transition: opacity 0.4s ease;
}

.col-lg-8:hover .bento-card,
.col-lg-4:hover .bento-card,
.col-12:hover .bento-card {
  transform: translateY(-10px);
  box-shadow: 0 0 40px rgba(243, 112, 33, 0.45), 0 20px 60px rgba(0, 0, 0, 0.4);
}

.col-lg-8:hover .bento-card::before,
.col-lg-4:hover .bento-card::before,
.col-12:hover .bento-card::before {
  opacity: 1;
}

/* ── Auto-Pulse ── */
.auto-glow .bento-card { animation: autoGlow 1.5s ease-in-out; }

@keyframes autoGlow {
  0%   { transform: scale(1); box-shadow: 0 0 0 rgba(243, 112, 33, 0); }
  50%  { transform: scale(1.02); box-shadow: 0 0 30px rgba(243, 112, 33, 0.4); }
  100% { transform: scale(1); box-shadow: 0 0 0 rgba(243, 112, 33, 0); }
}

/* ── Hero Card ── */
.bento-card--hero { min-height: 480px; }

.featured-bg {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  object-fit: cover;
  z-index: 0;
}

.featured-overlay-gradient {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(to top, rgba(18,18,18,0.98) 0%, rgba(18,18,18,0.7) 40%, rgba(165,28,28,0.1) 70%, transparent 100%);
  z-index: 1;
}

.featured-overlay-vignette {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(ellipse at center, transparent 40%, rgba(0,0,0,0.7) 100%);
  z-index: 2;
}

.hero-title {
  text-shadow: 0 2px 10px rgba(0,0,0,0.5), 0 0 30px rgba(255,186,0,0.2);
  letter-spacing: -0.02em;
}

.hero-synopsis {
  max-width: 500px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Hero slide indicators */
.hero-dots {
  top: 20px;
  right: 24px;
  display: flex;
  gap: 8px;
}

.hero-dot {
  width: 10px; height: 10px;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.hero-dot.active {
  background: linear-gradient(90deg, #F37021, #FFBA00);
  transform: scale(1.3);
  box-shadow: 0 0 8px rgba(243,112,33,0.6);
}

/* Hero transition animations */
.hero-img-enter-active, .hero-img-leave-active {
  transition: opacity 0.8s ease;
}
.hero-img-enter-from, .hero-img-leave-to { opacity: 0; }

.hero-text-enter-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1) 0.15s;
}
.hero-text-leave-active {
  transition: all 0.3s ease;
}
.hero-text-enter-from { opacity: 0; transform: translateY(20px); }
.hero-text-leave-to { opacity: 0; transform: translateY(-10px); }

.trending-img-enter-active, .trending-img-leave-active {
  transition: all 0.5s ease;
}
.trending-img-enter-from { opacity: 0; transform: scale(0.9); }
.trending-img-leave-to { opacity: 0; transform: scale(1.1); }

/* ── Stat Cards ── */
.bento-card--stat { overflow: hidden; }

.bento-card--stat::after {
  content: '';
  position: absolute; top: 0; left: -100%;
  width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,186,0,0.08), transparent);
  transition: left 0.6s ease;
}

.col-12:hover .bento-card--stat::after { left: 100%; }

/* ── Trending Card ── */
.bento-card--trending { overflow: hidden; }

.hover-scale-img { transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1); }
.trending-img-wrapper:hover .hover-scale-img { transform: scale(1.05); }

/* ── Promo Card ── */
.bento-card--promo { overflow: hidden; }

.promo-shimmer {
  position: absolute; top: 0; left: -100%;
  width: 200%; height: 100%;
  background: linear-gradient(110deg, transparent 25%, rgba(243,112,33,0.06) 35%, rgba(255,186,0,0.1) 50%, rgba(243,112,33,0.06) 65%, transparent 75%);
  animation: shimmerSlide 4s ease-in-out infinite;
  z-index: 1;
}

@keyframes shimmerSlide {
  0%   { transform: translateX(-30%); }
  100% { transform: translateX(30%); }
}

.promo-emoji {
  font-size: 3rem; display: block; margin-bottom: 0.75rem;
  animation: promoBounce 2s ease-in-out infinite;
}

@keyframes promoBounce {
  0%, 100% { transform: translateY(0); }
  50%      { transform: translateY(-6px); }
}

/* ── Badges ── */
.bento-badge { padding: 4px 14px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }
.bento-badge--featured { background: linear-gradient(90deg, #A51C1C, #E31E24); color: white; }
.bento-badge--trending { background: linear-gradient(90deg, #F37021, #FFBA00); color: #121212; }

/* ── Buttons ── */
.bento-btn-book {
  display: inline-flex; align-items: center; gap: 10px;
  padding: 14px 32px; border: none; border-radius: 14px;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 50%, #F37021 75%, #FFBA00 100%);
  color: white; font-size: 1rem; font-weight: 700; cursor: pointer;
  box-shadow: 0 4px 15px rgba(227,30,36,0.4);
  animation: btnPulse 2s ease-in-out infinite;
  transition: all 0.3s ease;
}

.bento-btn-book:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(227,30,36,0.6);
}

.bento-btn-book__icon { font-size: 0.85rem; }

@keyframes btnPulse {
  0%, 100% { box-shadow: 0 4px 15px rgba(227,30,36,0.4); }
  50%      { box-shadow: 0 4px 25px rgba(255,186,0,0.6); }
}

.bento-btn-outline {
  padding: 10px 24px; border: 2px solid rgba(255,255,255,0.2); border-radius: 12px;
  background: transparent; color: white; font-weight: 600; cursor: pointer;
  transition: all 0.3s ease; white-space: nowrap;
}

.bento-btn-outline:hover {
  border-color: #F37021; background: rgba(243,112,33,0.1); color: #FFBA00;
}

.bento-btn-promo {
  padding: 10px 28px; border: none; border-radius: 12px;
  background: linear-gradient(90deg, #F37021, #FFBA00);
  color: #121212; font-weight: 700; font-size: 0.9rem; cursor: pointer;
  transition: all 0.3s ease;
}

.bento-btn-promo:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(243,112,33,0.5);
}

/* ── Accent Dot ── */
.accent-dot-pulse {
  width: 12px; height: 12px; border-radius: 50%;
  background: linear-gradient(135deg, #E31E24, #FFBA00);
  animation: dotPulse 2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50%      { transform: scale(1.2); opacity: 0.7; }
}

/* ── Gradient Animated Text ── */
.text-gradient-animated {
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 25%, #F37021 50%, #FFBA00 75%, #E31E24 100%);
  background-size: 200% auto;
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 3s linear infinite;
}

@keyframes gradientShift {
  0%   { background-position: 0% center; }
  100% { background-position: 200% center; }
}

/* ── Entrance Animations ── */
.hero-fade-in { animation: heroFadeIn 0.8s ease-out forwards; }

@keyframes heroFadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.hero-slide-up { animation: heroSlideUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards; }

@keyframes heroSlideUp {
  from { opacity: 0; transform: translateY(30px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ── Background Orbs ── */
.hero-bento-section { position: relative; overflow: clip; }

.hero-bg-orbs {
  position: absolute; top: 0; left: 0;
  width: 100%; height: 100%; z-index: 0;
  overflow: hidden; pointer-events: none;
}

.orb {
  position: absolute; border-radius: 50%;
  filter: blur(80px); opacity: 0.15;
  animation: orbFloat 20s ease-in-out infinite;
}

.orb-1 { width: 400px; height: 400px; background: linear-gradient(135deg, #A51C1C, #E31E24); top: -100px; left: -100px; animation-duration: 25s; }
.orb-2 { width: 500px; height: 500px; background: linear-gradient(225deg, #F37021, #FFBA00); bottom: -150px; right: -150px; animation-duration: 30s; animation-delay: 5s; }
.orb-3 { width: 300px; height: 300px; background: linear-gradient(180deg, #FFBA00, #A51C1C); top: 50%; left: 50%; transform: translate(-50%, -50%); animation-duration: 35s; animation-delay: 10s; opacity: 0.08; }

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25%      { transform: translate(50px, -50px) scale(1.1); }
  50%      { transform: translate(-30px, 30px) scale(0.9); }
  75%      { transform: translate(20px, -20px) scale(1.05); }
}

/* ── Responsive ── */
@media (max-width: 992px) {
  .bento-card--hero { min-height: 380px; }
}

@media (max-width: 768px) {
  .bento-card--hero { min-height: 320px; }
  .bento-card--trending .d-flex { flex-direction: column; text-align: center; }
  .hero-title { font-size: 2.5rem !important; }
  .orb { filter: blur(60px); opacity: 0.1; }
  .bento-btn-book { animation: none; }
}

/* ── Dark Toggle ── */
.dark-toggle :deep(.el-radio-button__inner) {
  background: var(--dark-bg-elevated); border: 1px solid var(--border-color);
  color: var(--text-secondary); border-radius: 8px; margin: 0 4px;
  transition: all var(--transition-fast);
}

.dark-toggle :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--brand-gradient); border-color: transparent;
  color: white; box-shadow: var(--shadow-gradient);
}

.dark-toggle :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: 1px solid var(--border-color);
}

/* ── Section Cards ── */
.promo-card { border-radius: 16px; overflow: hidden; transition: all var(--transition-normal); }
.promo-card:hover { border-color: var(--border-color-strong); }

.news-card { border-radius: 16px; transition: all var(--transition-normal); }
.news-card:hover { border-color: var(--glass-border); }

/* ── Utilities ── */
.cursor-pointer { cursor: pointer; }

.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.line-clamp-3 { display: -webkit-box; -webkit-line-clamp: 3; line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }

/* ── Swiper Controls ── */
:deep(.movie-swiper) { overflow: hidden; position: relative; max-width: 100%; }

:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
  color: white; background: rgba(255,255,255,0.1);
  width: 50px; height: 50px; border-radius: 50%;
  backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.1);
  transition: all 0.3s ease; top: 50%; margin-top: 0;
  transform: translateY(-50%); z-index: 10;
}

:deep(.swiper-button-prev) { left: 10px; }
:deep(.swiper-button-next) { right: 10px; }

:deep(.swiper-button-next:hover),
:deep(.swiper-button-prev:hover) {
  background: var(--brand-gradient); border-color: transparent;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 0 15px rgba(227,30,36,0.5);
}

:deep(.swiper-button-next::after),
:deep(.swiper-button-prev::after) { font-size: 22px; font-weight: bold; }

:deep(.swiper-slide) { height: auto; padding-bottom: 20px; }

/* ── Reduced Motion ── */
@media (prefers-reduced-motion: reduce) {
  .hero-fade-in, .hero-slide-up, .bento-btn-book,
  .accent-dot-pulse, .text-gradient-animated,
  .orb, .promo-shimmer, .promo-emoji,
  .auto-glow .bento-card {
    animation: none !important; opacity: 1 !important; transform: none !important;
  }
  .hover-scale-img, .bento-card { transition: none !important; }
}
</style>
