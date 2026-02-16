<template>
  <div id="app">
    <!-- Loading Overlay with Film Reel Animation -->
    <LoadingOverlay :is-loading="isLoading" animation-type="filmReel" />
    
    <!-- Main Content -->
    <router-view></router-view>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import LoadingOverlay from '@/components/common/LoadingOverlay.vue';

const router = useRouter();
const isLoading = ref(false);

// Show loading on route navigation
router.beforeEach((to, from, next) => {
  isLoading.value = true;
  next();
});

router.afterEach(() => {
  // Add slight delay for smooth transition
  setTimeout(() => {
    isLoading.value = false;
  }, 400);
});

onMounted(() => {
  // Force scroll to top on load/refresh
  window.scrollTo(0, 0);
  if ('scrollRestoration' in history) {
    history.scrollRestoration = 'manual';
  }
});
</script>

<style>
/* Global strict overflow control */
html, body, #app {
  overflow-x: clip !important;
  width: 100%;
  max-width: 100vw;
  overscroll-behavior-x: none;
}

/* Custom Sleek Scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 0px; /* Hide horizontal scrollbar */
}

::-webkit-scrollbar-track {
  background: #121212; 
}

::-webkit-scrollbar-thumb {
  background: #333;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #E31E24; /* Brand color on hover */
}
</style>

<style scoped>
</style>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
