import { ref, onMounted, onUnmounted, computed } from 'vue';

/**
 * Responsive breakpoint detection composable
 * Based on Bootstrap breakpoints
 */
export function useBreakpoints() {
  const windowWidth = ref(window.innerWidth);

  const updateWidth = () => {
    windowWidth.value = window.innerWidth;
  };

  onMounted(() => {
    window.addEventListener('resize', updateWidth);
  });

  onUnmounted(() => {
    window.removeEventListener('resize', updateWidth);
  });

  // Bootstrap breakpoints
  const isMobile = computed(() => windowWidth.value < 768);
  const isTablet = computed(() => windowWidth.value >= 768 && windowWidth.value < 992);
  const isDesktop = computed(() => windowWidth.value >= 992);
  const isLargeDesktop = computed(() => windowWidth.value >= 1200);

  return {
    windowWidth,
    isMobile,
    isTablet,
    isDesktop,
    isLargeDesktop
  };
}
