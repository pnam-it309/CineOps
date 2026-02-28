<script setup>
import { onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const statusMessage = ref('Đang xử lý đăng nhập...');
const isError = ref(false);

onMounted(async () => {
  try {
    const state = route.query.state;
    if (!state) {
      throw new Error('Không tìm thấy thông tin xác thực từ Google.');
    }

    // Decode base64 state -> JSON { accessToken, refreshToken }
    const decoded = atob(state);
    const tokenData = JSON.parse(decoded);

    if (!tokenData.accessToken) {
      throw new Error('Token không hợp lệ. Vui lòng thử lại.');
    }

    // Save token & fetch profile
    sessionStorage.setItem('token', tokenData.accessToken);
    if (tokenData.refreshToken) {
      sessionStorage.setItem('refreshToken', tokenData.refreshToken);
    }

    authStore.token = tokenData.accessToken;
    await authStore.fetchProfile();

    statusMessage.value = 'Đăng nhập thành công! Đang chuyển hướng...';

    // Redirect based on role
    setTimeout(() => {
      if (authStore.isAdmin) {
        router.push({ name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name });
      } else if (authStore.isStaff) {
        router.push({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name });
      } else {
        router.push({ name: ROUTES_CONSTANTS.HOME.name });
      }
    }, 800);

  } catch (err) {
    isError.value = true;
    statusMessage.value = err.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
    console.error('OAuth2 callback error:', err);
    setTimeout(() => {
      router.push({ name: ROUTES_CONSTANTS.LOGIN.name });
    }, 2500);
  }
});
</script>

<template>
  <div class="oauth-callback-container">
    <div class="callback-card">
      <div v-if="!isError" class="spinner-wrap">
        <div class="spinner"></div>
      </div>
      <div v-else class="error-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="56" height="56" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10" />
          <line x1="12" y1="8" x2="12" y2="12" />
          <line x1="12" y1="16" x2="12.01" y2="16" />
        </svg>
      </div>

      <div class="brand">
        <span class="brand-icon">🎬</span>
        <span class="brand-name">CineOps</span>
      </div>

      <p class="status-msg" :class="{ 'error-msg': isError }">{{ statusMessage }}</p>

      <div v-if="isError" class="back-link">
        <a href="/login">← Quay lại đăng nhập</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.oauth-callback-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1c2c 0%, #A51C1C 50%, #4a192c 100%);
}

.callback-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 24px;
  padding: 48px 40px;
  text-align: center;
  min-width: 320px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

.spinner-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.spinner {
  width: 56px;
  height: 56px;
  border: 4px solid rgba(255, 255, 255, 0.15);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.9s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-icon {
  color: #ff6b6b;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 16px;
}

.brand-icon {
  font-size: 28px;
}

.brand-name {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
}

.status-msg {
  color: rgba(255, 255, 255, 0.85);
  font-size: 15px;
  margin: 0;
}

.error-msg {
  color: #ff8a8a;
}

.back-link {
  margin-top: 20px;
}

.back-link a {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.back-link a:hover {
  color: #fff;
}
</style>
