<template>
  <div class="showtimes-page">
    <!-- Hero Header -->
    <section class="hero-header position-relative overflow-hidden">
      <div class="hero-bg-gradient"></div>
      <div class="container-xl position-relative z-2 py-3 text-center">
        <h1 class="hero-title fs-2 fw-bold text-white mb-1">
          Lịch Chiếu <span class="title-accent">Phim</span>
        </h1>
        <p class="text-white-50 small mb-0">Chọn ngày & suất chiếu phù hợp nhất với bạn</p>
      </div>
    </section>

    <!-- Interactive Date Timeline -->
    <section class="date-timeline-section container-xl">
      <div class="timeline-track" ref="timelineTrack">
        <div class="d-flex gap-2 px-2 py-2 timeline-inner">
          <div
            v-for="(date, index) in dates"
            :key="date.full"
            class="date-item flex-shrink-0"
            :class="{ 'date-active': selectedDateIndex === index }"
            @click="selectDate(index)"
          >
            <div class="date-weekday">{{ date.weekday }}</div>
            <div class="date-day">{{ date.day }}</div>
            <div class="date-month">{{ date.month }}</div>
            <div class="date-glow" v-if="selectedDateIndex === index"></div>
          </div>
        </div>
      </div>
      <!-- Timeline connector line -->
      <div class="timeline-line"></div>
    </section>

    <!-- Movie Schedules -->
    <section class="schedules-section container-xl d-flex flex-column" style="flex: 1; min-height: 0;">
      <div class="section-header mb-3">
        <h2 class="section-title text-white fw-bold mb-1">
          <span class="neon-text">Suất chiếu</span> hôm nay
        </h2>
        <p class="text-white-50 small mb-0">{{ movies.length }} phim đang có suất chiếu</p>
      </div>

      <!-- Scrollable movie list -->
      <div
        class="movies-scroll-container"
        ref="scrollContainer"
        @mouseenter="pauseAutoScroll"
        @mouseleave="resumeAutoScroll"
      >
        <div class="movies-grid pb-4">
          <div
            v-for="movie in movies"
            :key="movie.id"
            class="movie-schedule-card"
          >
            <div class="row g-0 align-items-stretch">
              <!-- Poster -->
              <div class="col-md-2 col-3">
                <div class="poster-wrapper h-100">
                  <img
                    :src="movie.poster"
                    :alt="movie.title"
                    class="movie-poster"
                    @error="(e) => e.target.src = 'https://placehold.co/300x450/1a1a1a/666?text=' + encodeURIComponent(movie.title)"
                  />
                  <div class="poster-overlay"></div>
                </div>
              </div>

              <!-- Info & Showtimes -->
              <div class="col-md-10 col-9 p-3 p-md-4 d-flex flex-column">
                <div class="d-flex flex-wrap justify-content-between align-items-start mb-3">
                  <div class="movie-info">
                    <h3 class="movie-title fw-bold text-white mb-1">{{ movie.title }}</h3>
                    <div class="d-flex flex-wrap align-items-center gap-2">
                      <span class="rating-badge">{{ movie.rating }}</span>
                      <span class="text-white-50 small">{{ movie.duration }} phút</span>
                      <span class="text-white-50 small">•</span>
                      <span class="text-white-50 small">{{ movie.genre }}</span>
                    </div>
                  </div>
                  <button
                    class="btn-detail d-none d-md-inline-flex"
                    @click="$router.push({ name: 'MovieDetails', params: { id: movie.id } })"
                  >
                    Chi tiết
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg>
                  </button>
                </div>

                <!-- Showtime Format Groups -->
                <div class="showtime-groups flex-grow-1">
                  <div v-for="format in movie.formats" :key="format.name" class="format-group mb-3">
                    <div class="format-label mb-2">{{ format.name }}</div>
                    <div class="d-flex flex-wrap gap-2">
                      <button
                        v-for="time in format.times"
                        :key="time"
                        class="showtime-chip"
                        @click="goToBooking(movie, time)"
                      >
                        {{ time }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="movies.length === 0" class="empty-state text-center py-5">
        <div class="empty-icon mb-3">🎬</div>
        <h4 class="text-white-50 fw-normal">Chưa có suất chiếu cho ngày này</h4>
        <p class="text-white-50 small">Vui lòng chọn ngày khác</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { mockMovies } from '@/mock';

const router = useRouter();
const scrollContainer = ref(null);
let autoScrollInterval = null;
let isScrollPaused = false;

// Generate next 14 days dynamically
const generateDates = () => {
  const result = [];
  const weekdays = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  const months = ['Th.01', 'Th.02', 'Th.03', 'Th.04', 'Th.05', 'Th.06', 'Th.07', 'Th.08', 'Th.09', 'Th.10', 'Th.11', 'Th.12'];
  
  for (let i = 0; i < 14; i++) {
    const d = new Date();
    d.setDate(d.getDate() + i);
    result.push({
      full: d.toISOString().split('T')[0],
      weekday: i === 0 ? 'Hôm nay' : (i === 1 ? 'Ngày mai' : weekdays[d.getDay()]),
      day: d.getDate().toString().padStart(2, '0'),
      month: months[d.getMonth()],
      isToday: i === 0,
      isWeekend: d.getDay() === 0 || d.getDay() === 6
    });
  }
  return result;
};

const dates = generateDates();
const selectedDateIndex = ref(0);
const timelineTrack = ref(null);

const selectDate = (index) => {
  selectedDateIndex.value = index;
  // Reset scroll to top when changing date
  if (scrollContainer.value) {
    scrollContainer.value.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

// Only show "now-showing" movies on showtimes page
const movies = computed(() => {
  return mockMovies
    .filter(m => m.status === 'now-showing')
    .map(m => ({
      ...m,
      formats: [
        { name: '2D Phụ Đề', times: generateRandomTimes(4) },
        { name: '2D Lồng Tiếng', times: generateRandomTimes(2) }
      ]
    }));
});

// Generate random showtime hours for demo
const generateRandomTimes = (count) => {
  const allTimes = ['09:30', '10:15', '11:00', '12:45', '13:30', '14:15', '15:00', '16:30', '17:45', '18:30', '19:00', '19:45', '20:30', '21:15', '22:00', '23:15'];
  const shuffled = [...allTimes].sort(() => 0.5 - Math.random());
  return shuffled.slice(0, count).sort();
};

const goToBooking = (movie, time) => {
  router.push({ name: 'SelectSeats', params: { showtimeId: '1' } });
};

// Auto-scroll logic
const startAutoScroll = () => {
  autoScrollInterval = setInterval(() => {
    if (isScrollPaused || !scrollContainer.value) return;
    const el = scrollContainer.value;
    // If reached bottom, scroll back to top
    if (el.scrollTop + el.clientHeight >= el.scrollHeight - 5) {
      el.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      el.scrollTop += 1;
    }
  }, 30);
};

const pauseAutoScroll = () => {
  isScrollPaused = true;
};

const resumeAutoScroll = () => {
  isScrollPaused = false;
};

onMounted(() => {
  startAutoScroll();
});

onUnmounted(() => {
  if (autoScrollInterval) clearInterval(autoScrollInterval);
});
</script>

<style scoped>
/* ===== PAGE BASE ===== */
.showtimes-page {
  background: #121212;
  height: calc(100vh - 64px); /* subtract navbar height */
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* ===== SCROLL CONTAINER ===== */
.movies-scroll-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
  /* Fade at bottom edge */
  mask-image: linear-gradient(to bottom, black 92%, transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, black 92%, transparent 100%);
  scroll-behavior: smooth;
}

/* 2-column movie grid */
.movies-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 992px) {
  .movies-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

/* Custom scrollbar for movies list */
.movies-scroll-container::-webkit-scrollbar {
  width: 5px;
}

.movies-scroll-container::-webkit-scrollbar-track {
  background: transparent;
}

.movies-scroll-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}

.movies-scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(227, 30, 36, 0.5);
}

/* ===== HERO HEADER ===== */
.hero-header {
  padding: 0.75rem 0 0.5rem;
  position: relative;
}

.hero-bg-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 50%, rgba(165, 28, 28, 0.15) 0%, transparent 60%),
    radial-gradient(ellipse at 80% 50%, rgba(255, 186, 0, 0.1) 0%, transparent 60%);
  z-index: 0;
}

.hero-particles {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

.particle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
  animation: particleFloat 15s ease-in-out infinite;
}

.p1 {
  width: 200px; height: 200px;
  background: radial-gradient(circle, rgba(227, 30, 36, 0.3), transparent 70%);
  top: -50px; left: 10%;
  animation-duration: 20s;
}

.p2 {
  width: 150px; height: 150px;
  background: radial-gradient(circle, rgba(243, 112, 33, 0.3), transparent 70%);
  bottom: -30px; right: 20%;
  animation-duration: 25s;
  animation-delay: 3s;
}

.p3 {
  width: 100px; height: 100px;
  background: radial-gradient(circle, rgba(255, 186, 0, 0.3), transparent 70%);
  top: 30%; right: 10%;
  animation-duration: 18s;
  animation-delay: 7s;
}

@keyframes particleFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.1); }
  66% { transform: translate(-20px, 15px) scale(0.95); }
}

.hero-title {
  font-size: 2.5rem;
  letter-spacing: -0.03em;
}

.title-accent {
  background: linear-gradient(90deg, #E31E24 0%, #F37021 50%, #FFBA00 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ===== DATE TIMELINE ===== */
.date-timeline-section {
  position: relative;
  padding: 0 0 0.75rem;
}

.timeline-track {
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  mask-image: linear-gradient(to right, transparent 0%, black 3%, black 97%, transparent 100%);
  -webkit-mask-image: linear-gradient(to right, transparent 0%, black 3%, black 97%, transparent 100%);
}

.timeline-track::-webkit-scrollbar {
  display: none;
}

.timeline-inner {
  min-width: max-content;
  justify-content: center;
}

.timeline-line {
  position: absolute;
  bottom: 0;
  left: 5%;
  right: 5%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.08), transparent);
}

.date-item {
  width: 72px;
  padding: 10px 6px;
  border-radius: 14px;
  text-align: center;
  cursor: pointer;
  position: relative;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}

.date-item:hover:not(.date-active) {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.12);
  transform: translateY(-2px);
}

.date-active {
  background: rgba(227, 30, 36, 0.1);
  border-color: transparent;
  transform: scale(1.1);
  box-shadow:
    0 0 20px rgba(227, 30, 36, 0.2),
    0 0 40px rgba(243, 112, 33, 0.1),
    inset 0 0 0 1.5px transparent;
  background-image: linear-gradient(rgba(227, 30, 36, 0.1), rgba(255, 186, 0, 0.05)),
                     linear-gradient(90deg, #A51C1C, #E31E24, #F37021, #FFBA00);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  border: 1.5px solid transparent;
}

.date-glow {
  position: absolute;
  inset: -8px;
  border-radius: 24px;
  background: linear-gradient(90deg, rgba(165, 28, 28, 0.15), rgba(243, 112, 33, 0.1), rgba(255, 186, 0, 0.15));
  filter: blur(12px);
  z-index: -1;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.date-weekday {
  font-size: 0.65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: rgba(255, 255, 255, 0.4);
  margin-bottom: 4px;
  transition: color 0.3s;
}

.date-active .date-weekday {
  color: #FFBA00;
}

.date-day {
  font-size: 1.5rem;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.1;
  transition: color 0.3s;
}

.date-active .date-day {
  color: #fff;
  text-shadow: 0 0 15px rgba(255, 186, 0, 0.4);
}

.date-month {
  font-size: 0.6rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.3);
  margin-top: 2px;
  transition: color 0.3s;
}

.date-active .date-month {
  color: rgba(255, 186, 0, 0.8);
}

/* ===== SECTION HEADER ===== */
.section-title {
  font-size: 1.5rem;
  letter-spacing: -0.02em;
}

.neon-text {
  color: #fff;
  text-shadow:
    0 0 10px rgba(227, 30, 36, 0.5),
    0 0 30px rgba(227, 30, 36, 0.2);
}

/* ===== MOVIE SCHEDULE CARD ===== */
.movie-schedule-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.movie-schedule-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* Poster */
.poster-wrapper {
  position: relative;
  overflow: hidden;
  min-height: 200px;
}

.movie-poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.movie-schedule-card:hover .movie-poster {
  transform: scale(1.05);
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, transparent 60%, #121212 100%);
  pointer-events: none;
}

/* Movie Info */
.movie-title {
  font-size: 1.15rem;
  letter-spacing: -0.01em;
}

.rating-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.65rem;
  font-weight: 800;
  letter-spacing: 0.05em;
  background: linear-gradient(135deg, #E31E24, #A51C1C);
  color: white;
}

.btn-detail {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: transparent;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-detail:hover {
  border-color: #FFBA00;
  color: #FFBA00;
  background: rgba(255, 186, 0, 0.08);
}

/* Format Groups */
.format-label {
  font-size: 0.68rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: rgba(255, 255, 255, 0.35);
}

/* Showtime Chips */
.showtime-chip {
  padding: 8px 18px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.75);
  font-size: 0.88rem;
  font-weight: 700;
  font-family: 'Inter', monospace;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.showtime-chip::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: inherit;
  z-index: -1;
}

.showtime-chip:hover {
  color: #fff;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow:
    0 4px 15px rgba(227, 30, 36, 0.3),
    0 0 30px rgba(243, 112, 33, 0.15);
}

.showtime-chip:hover::before {
  opacity: 1;
}

.showtime-chip:active {
  transform: translateY(0) scale(0.97);
}

/* ===== EMPTY STATE ===== */
.empty-icon {
  font-size: 3rem;
  opacity: 0.3;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .hero-title {
    font-size: 1.8rem;
  }

  .date-item {
    width: 72px;
    padding: 10px 6px;
  }

  .date-day {
    font-size: 1.25rem;
  }

  .date-active {
    transform: scale(1.08);
  }

  .movie-title {
    font-size: 1rem;
  }

  .movie-schedule-card .p-md-4 {
    padding: 0.75rem !important;
  }

  .showtime-chip {
    padding: 6px 12px;
    font-size: 0.8rem;
  }

  .timeline-inner {
    justify-content: flex-start;
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

@media (max-width: 576px) {
  .poster-wrapper {
    min-height: 150px;
  }

  .btn-detail {
    display: none;
  }
}

/* ===== REDUCED MOTION ===== */
@media (prefers-reduced-motion: reduce) {
  .particle,
  .date-glow {
    animation: none !important;
  }
  
  .date-item,
  .movie-schedule-card,
  .showtime-chip {
    transition: none !important;
  }
}
</style>
