import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * UI Store - Quản lý trạng thái giao diện toàn cục
 * Mục đích: Cache trạng thái UI để tránh render lại không cần thiết
 * và đồng bộ trạng thái giữa các trang mà không cần load lại.
 */
export const useUIStore = defineStore('ui', () => {
  // ==========================================
  // SIDEBAR STATE
  // ==========================================
  const sidebarCollapsed = ref(
    localStorage.getItem('sidebar-collapsed') === 'true' || window.innerWidth < 768
  );

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value;
    localStorage.setItem('sidebar-collapsed', sidebarCollapsed.value);
  };

  const setSidebarCollapsed = (value) => {
    sidebarCollapsed.value = value;
    localStorage.setItem('sidebar-collapsed', value);
  };

  // ==========================================
  // GLOBAL LOADING STATE
  // ==========================================
  const globalLoading = ref(false);
  const loadingMessage = ref('Đang tải...');

  const showLoading = (message = 'Đang tải...') => {
    loadingMessage.value = message;
    globalLoading.value = true;
  };

  const hideLoading = () => {
    globalLoading.value = false;
    loadingMessage.value = 'Đang tải...';
  };

  // ==========================================
  // PAGE DATA CACHE (tránh gọi API lại khi quay về trang)
  // ==========================================
  const pageCache = ref(new Map());

  const getCachedData = (key) => {
    const cached = pageCache.value.get(key);
    if (!cached) return null;

    const now = Date.now();
    const ttl = cached.ttl || 60_000; // Default: 1 phút
    if (now - cached.timestamp > ttl) {
      pageCache.value.delete(key);
      return null; // Cache đã hết hạn
    }
    return cached.data;
  };

  const setCachedData = (key, data, ttlMs = 60_000) => {
    pageCache.value.set(key, {
      data,
      timestamp: Date.now(),
      ttl: ttlMs,
    });
  };

  const clearCache = (key) => {
    if (key) {
      pageCache.value.delete(key);
    } else {
      pageCache.value.clear();
    }
  };

  // ==========================================
  // PAGE PREFERENCES (filter/sort/pagination)
  // ==========================================
  const pagePreferences = ref(
    JSON.parse(sessionStorage.getItem('page-preferences') || '{}')
  );

  const getPagePreference = (pageName, key, defaultValue = null) => {
    return pagePreferences.value[pageName]?.[key] ?? defaultValue;
  };

  const setPagePreference = (pageName, key, value) => {
    if (!pagePreferences.value[pageName]) {
      pagePreferences.value[pageName] = {};
    }
    pagePreferences.value[pageName][key] = value;
    // Lưu vào sessionStorage để không mất khi F5
    sessionStorage.setItem('page-preferences', JSON.stringify(pagePreferences.value));
  };

  // ==========================================
  // NOTIFICATION BADGE
  // ==========================================
  const notificationCount = ref(0);
  const hasNotifications = computed(() => notificationCount.value > 0);

  return {
    // Sidebar
    sidebarCollapsed,
    toggleSidebar,
    setSidebarCollapsed,

    // Loading
    globalLoading,
    loadingMessage,
    showLoading,
    hideLoading,

    // Cache
    getCachedData,
    setCachedData,
    clearCache,

    // Preferences
    getPagePreference,
    setPagePreference,

    // Notifications
    notificationCount,
    hasNotifications,
  };
});
