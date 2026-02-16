<template>
  <el-config-provider :locale="vi">
    <div class="cineops-app-shell">
      <!-- Đây là nơi các Layout con (Admin, Customer, Staff) sẽ được render vào -->
      <slot>
        <router-view v-if="!$slots.default" />
      </slot>
      
      <!-- Các thành phần toàn cục luôn hiện diện -->
      <el-backtop :right="20" :bottom="20" />
    </div>
  </el-config-provider>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted } from 'vue';
import vi from 'element-plus/dist/locale/vi.mjs';

const authStore = useAuthStore();

onMounted(() => {
  // Logic khởi tạo ứng dụng toàn cục (ví dụ: check token, load config từ server)
  if (authStore.isAuthenticated && !authStore.user) {
    authStore.fetchUserProfile();
  }
});
</script>

<style>
/* CSS Dùng chung cho toàn bộ hệ thống */
:root {
  --cineops-primary: #667eea;
  --cineops-secondary: #764ba2;
  --cineops-dark: #1a1a1a;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  background-color: #f8fafc;
  -webkit-font-smoothing: antialiased;
  overflow-x: clip;
  width: 100%;
  overscroll-behavior-x: none;
}

html {
  overflow-x: clip;
  overscroll-behavior-x: none;
}

.cineops-app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-x: clip;
  max-width: 100vw;
}

/* Các hiệu ứng chuyển cảnh trang toàn cục */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Tùy chỉnh thanh cuộn đẹp hơn cho toàn hệ thống */
::-webkit-scrollbar {
  width: 6px;
  height: 0;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>

