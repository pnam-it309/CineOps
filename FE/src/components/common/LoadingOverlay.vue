<template>
  <Transition name="fade">
    <div v-if="isLoading" class="loading-overlay">
      
      <!-- Progress Bar Animation (Option 1) -->
      <div v-if="animationType === 'progressBar'" class="loading-progress-container">
        <div class="loading-progress-bar"></div>
      </div>
      
      <!-- Film Reel Spinner (Option 2) -->
      <div v-else class="loading-spinner-container">
        <div class="film-reel-wrapper">
          <svg class="film-reel" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
            <!-- Outer Ring -->
            <circle class="reel-outer" cx="50" cy="50" r="45" />
            
            <!-- Inner Circles (Film Holes) -->
            <circle class="reel-hole" cx="50" cy="20" r="4" />
            <circle class="reel-hole" cx="70" cy="30" r="4" />
            <circle class="reel-hole" cx="80" cy="50" r="4" />
            <circle class="reel-hole" cx="70" cy="70" r="4" />
            <circle class="reel-hole" cx="50" cy="80" r="4" />
            <circle class="reel-hole" cx="30" cy="70" r="4" />
            <circle class="reel-hole" cx="20" cy="50" r="4" />
            <circle class="reel-hole" cx="30" cy="30" r="4" />
            
            <!-- Center Core -->
            <circle class="reel-center" cx="50" cy="50" r="15" />
            
            <!-- Film Strips -->
            <rect class="film-strip" x="35" y="48" width="30" height="4" rx="1" />
            <rect class="film-strip" x="48" y="35" width="4" height="30" rx="1" />
          </svg>
          
          <!-- Glowing Border -->
          <div class="glow-ring"></div>
        </div>
        
        <!-- Loading Text -->
        <p class="loading-text">Loading<span class="dots"></span></p>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref } from 'vue';

// Props
const props = defineProps({
  isLoading: {
    type: Boolean,
    default: false
  },
  animationType: {
    type: String,
    default: 'filmReel', // 'progressBar' or 'filmReel'
    validator: (value) => ['progressBar', 'filmReel'].includes(value)
  }
});
</script>

<style scoped>
/* Brand Gradient */
:root {
  --brand-gradient: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
}

/* Overlay Container */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 99999;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #121212 !important;
}

/* Fade Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ==================== PROGRESS BAR ANIMATION ==================== */
.loading-progress-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  overflow: hidden;
  z-index: 10000;
}

.loading-progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  background-size: 200% 100%;
  animation: progressSlide 1.5s ease-in-out infinite, shimmer 2s linear infinite;
  box-shadow: 0 0 20px rgba(227, 30, 36, 0.8), 0 0 40px rgba(255, 186, 0, 0.5);
}

@keyframes progressSlide {
  0% {
    width: 0%;
    margin-left: 0%;
  }
  50% {
    width: 70%;
  }
  100% {
    width: 0%;
    margin-left: 100%;
  }
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* ==================== FILM REEL SPINNER ==================== */
.loading-spinner-container {
  position: relative;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

.film-reel-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.film-reel {
  width: 100px;
  height: 100px;
  filter: drop-shadow(0 0 20px rgba(227, 30, 36, 0.6));
  animation: spin 2s linear infinite;
  position: relative;
  z-index: 2;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Reel SVG Elements */
.reel-outer {
  fill: none;
  stroke: url(#gradientStroke);
  stroke-width: 3;
}

.reel-hole {
  fill: #FFBA00;
  opacity: 0.8;
}

.reel-center {
  fill: #1a1a1a;
  stroke: url(#gradientStroke);
  stroke-width: 2;
}

.film-strip {
  fill: rgba(255, 186, 0, 0.6);
}

/* Glowing Ring */
.glow-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 130px;
  height: 130px;
  border-radius: 50%;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  opacity: 0.4;
  filter: blur(15px);
  animation: pulse 2s ease-in-out infinite;
  z-index: 1;
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.4;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.7;
  }
}

/* Loading Text */
.loading-text {
  color: white;
  font-size: 1.25rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: shimmerText 3s linear infinite;
  margin: 0;
}

@keyframes shimmerText {
  0% {
    background-position: 200% center;
  }
  100% {
    background-position: -200% center;
  }
}

.dots::after {
  content: '';
  animation: dots 1.5s steps(4, end) infinite;
}

@keyframes dots {
  0%, 20% {
    content: '';
  }
  40% {
    content: '.';
  }
  60% {
    content: '..';
  }
  80%, 100% {
    content: '...';
  }
}

/* Gradient Definition for SVG */
.film-reel::before {
  content: '';
  position: absolute;
}

/* Add gradient to SVG via inline style if needed, or use CSS variables */
.reel-outer,
.reel-center {
  stroke: #E31E24;
}

/* Responsive Design */
@media (max-width: 768px) {
  .film-reel-wrapper {
    width: 100px;
    height: 100px;
  }
  
  .film-reel {
    width: 80px;
    height: 80px;
  }
  
  .glow-ring {
    width: 110px;
    height: 110px;
  }
  
  .loading-text {
    font-size: 1rem;
  }
}
</style>
