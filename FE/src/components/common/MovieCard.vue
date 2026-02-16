<template>
  <div class="movie-card hover-lift" @click="handleClick">
    <div class="movie-poster-wrapper">
      <img :src="movie.poster || '/placeholder-movie.jpg'" :alt="movie.title" class="movie-poster" />
      <div class="movie-overlay">
        <div class="play-button">
          <el-icon :size="48"><VideoPlay /></el-icon>
        </div>
      </div>
    </div>
    <div class="movie-info p-3">
      <h3 class="movie-title mb-2">{{ movie.title }}</h3>
      <div class="d-flex align-items-center gap-2 mb-2">
        <el-tag v-if="movie.rating" type="warning" size="small">{{ movie.rating }}</el-tag>
        <span class="text-secondary small">{{ movie.duration }} phút</span>
      </div>
      <div class="d-flex align-items-center justify-content-between">
        <span class="text-tertiary small">{{ movie.genre }}</span>
        <el-button type="primary" size="small" class="btn-gradient">Đặt vé</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { VideoPlay } from '@element-plus/icons-vue';

const props = defineProps({
  movie: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['click']);

const handleClick = () => {
  emit('click', props.movie);
};
</script>

<style scoped>
.movie-card {
  position: relative;
  background: var(--dark-bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.movie-card::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: var(--brand-gradient);
  border-radius: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
  filter: blur(10px);
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6), 0 0 40px rgba(227, 30, 36, 0.4);
}

.movie-card:hover::before {
  opacity: 0.6;
  animation: pulse-glow 2s ease-in-out infinite;
}

@keyframes pulse-glow {
  0%, 100% {
    filter: blur(10px);
    opacity: 0.6;
  }
  50% {
    filter: blur(15px);
    opacity: 0.8;
  }
}

.movie-poster-wrapper {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
  background: var(--dark-bg-secondary);
}

.movie-poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.movie-card:hover .movie-poster {
  transform: scale(1.05);
}

.movie-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(18, 18, 18, 0.9) 0%, transparent 60%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.movie-card:hover .movie-overlay {
  opacity: 1;
}

.play-button {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--brand-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 24px rgba(227, 30, 36, 0.5);
  transform: scale(0.8);
  transition: transform 0.3s ease;
}

.movie-card:hover .play-button {
  transform: scale(1);
}

.movie-info {
  background: var(--dark-bg-card);
}

.movie-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-secondary {
  color: var(--text-secondary);
}

.text-tertiary {
  color: var(--text-tertiary);
}

.btn-gradient {
  padding: 4px 12px;
  font-size: 0.8rem;
}
</style>
