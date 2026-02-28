<template>
  <div id="app">
    <!-- Loading Overlay with Film Reel Animation -->
    <LoadingOverlay 
      :is-loading="isLoading" 
      :animation-type="loadingType" 
      :show-background="loadingBg"
      :use-blur="true"
    />
    
    <!-- Main Content -->
    <router-view></router-view>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import LoadingOverlay from '@/components/common/LoadingOverlay.vue';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();
const isLoading = ref(false);
const loadingType = ref('filmReel'); // 'filmReel' or 'progressBar'
const loadingBg = ref(true);
const isInitialLoad = ref(true);

// Watch for store loading (Login/Register/Logout actions)
watch(() => authStore.loading, (newVal) => {
  if (newVal) {
    loadingType.value = 'filmReel';
    loadingBg.value = true;
    isLoading.value = true;
  } else {
    // Only hide if we're not currently navigating (which has its own logic)
    // Actually, it's safer to just set isLoading to false after a delay
    setTimeout(() => {
      // If we are still navigating, the router guard will keep isLoading true
      // but if the action finished, we should probably hide it
      isLoading.value = false;
    }, 400);
  }
});

// Show loading on route navigation
router.beforeEach((to, from, next) => {
  // Check if target is login or register
  const isAuthRoute = to.name === ROUTES_CONSTANTS.LOGIN.name || to.name === ROUTES_CONSTANTS.REGISTER.name;

  // If it's the first load, OR navigating to login/register -> Full Screen Film Reel
  if (isInitialLoad.value || isAuthRoute) {
    loadingType.value = 'filmReel';
    loadingBg.value = true;
  } else {
    // Other route changes -> Sleek Top Progress Bar
    loadingType.value = 'progressBar';
    loadingBg.value = false;
  }
  
  isLoading.value = true;
  next();
});

router.afterEach(() => {
  // Mark initial load as complete
  isInitialLoad.value = false;
  
  // Quick fade out for top bar, regular for splash
  const delay = loadingType.value === 'progressBar' ? 200 : 400;
  setTimeout(() => {
    isLoading.value = false;
  }, delay);
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
