<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const router = useRouter();
const authStore = useAuthStore();
const email = ref('');
const password = ref('');
const loading = ref(false);
const googleLoading = ref(false);
const error = ref('');

const BACKEND_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8888';
const FRONTEND_CALLBACK_URL = `${window.location.origin}/oauth2/redirect`;

const handleLogin = async () => {
  loading.value = true;
  error.value = '';
  try {
    await authStore.login({ email: email.value, password: password.value });

    // Redirect based on role
    if (authStore.isAdmin) {
      router.push({ name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name });
    } else if (authStore.isStaff) {
      router.push({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name });
    } else {
      router.push({ name: ROUTES_CONSTANTS.HOME.name });
    }
  } catch (err) {
    error.value = 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const handleGoogleLogin = () => {
  googleLoading.value = true;
  // Redirect to backend OAuth2 authorize endpoint
  // Backend will handle Google OAuth2 flow and redirect back to FRONTEND_CALLBACK_URL with ?state=<base64token>
  const params = new URLSearchParams({
    redirect_uri: FRONTEND_CALLBACK_URL,
    register: 'true',      // auto-register if not exist
    screen: 'ROLE_USER',   // default customer role for Google login
  });
  window.location.href = `${BACKEND_URL}/oauth2/authorize/google?${params.toString()}`;
};
</script>

<template>
  <div class="login-container d-flex align-items-center justify-content-center min-vh-100">
    <!-- Glass Card with Bootstrap utilities -->
    <div
      class="glass-card p-4 p-md-5 bg-white bg-opacity-10 border border-white border-opacity-25 shadow-lg rounded-4 m-3 w-100"
      style="max-width: 420px;">
      <h2 class="display-5 fw-bold mb-4 text-center text-white">Đăng nhập</h2>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <input type="email"
            class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
            placeholder="Email" v-model="email" required>
        </div>

        <div class="mb-3">
          <input type="password"
            class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
            placeholder="Mật khẩu" v-model="password" required>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-4 text-white small px-1">
          <div class="form-check m-0">
            <input class="form-check-input bg-transparent border-white" type="checkbox" id="rememberMe">
            <label class="form-check-label" for="rememberMe">Ghi nhớ tôi</label>
          </div>
          <a href="#" class="text-white text-decoration-none hover-underline">Quên mật khẩu?</a>
        </div>

        <div v-if="error" class="alert alert-danger py-2 mb-3 small opacity-90">{{ error }}</div>

        <button type="submit"
          class="btn btn-light w-100 rounded-pill py-2 fw-semibold mb-3 border-0 shadow-sm hover-lift"
          :disabled="loading || googleLoading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
        </button>

        <!-- Divider -->
        <div class="divider d-flex align-items-center gap-2 mb-3">
          <hr class="flex-grow-1 border-white opacity-25">
          <span class="text-white opacity-50 small">hoặc</span>
          <hr class="flex-grow-1 border-white opacity-25">
        </div>

        <!-- Google Login Button -->
        <button
          type="button"
          class="btn-google w-100 rounded-pill py-2 fw-semibold mb-4"
          :disabled="loading || googleLoading"
          @click="handleGoogleLogin"
        >
          <span v-if="googleLoading" class="spinner-border spinner-border-sm me-2" style="color:#4285f4"></span>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 48 48" class="me-2">
            <path fill="#FFC107" d="M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8-6.627 0-12-5.373-12-12s5.373-12 12-12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 12.955 4 4 12.955 4 24s8.955 20 20 20 20-8.955 20-20c0-1.341-.138-2.65-.389-3.917z"/>
            <path fill="#FF3D00" d="m6.306 14.691 6.571 4.819C14.655 15.108 18.961 12 24 12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 16.318 4 9.656 8.337 6.306 14.691z"/>
            <path fill="#4CAF50" d="M24 44c5.166 0 9.86-1.977 13.409-5.192l-6.19-5.238A11.91 11.91 0 0 1 24 36c-5.202 0-9.619-3.317-11.283-7.946l-6.522 5.025C9.505 39.556 16.227 44 24 44z"/>
            <path fill="#1976D2" d="M43.611 20.083H42V20H24v8h11.303a12.04 12.04 0 0 1-4.087 5.571l.003-.002 6.19 5.238C36.971 39.205 44 34 44 24c0-1.341-.138-2.65-.389-3.917z"/>
          </svg>
          {{ googleLoading ? 'Đang kết nối...' : 'Đăng nhập với Google' }}
        </button>

        <div class="text-center text-white">
          <span class="opacity-75">Chưa có tài khoản? </span>
          <router-link to="/register" class="text-white fw-bold text-decoration-none hover-underline">Đăng
            ký</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  background: linear-gradient(135deg, #1a1c2c 0%, #A51C1C 50%, #4a192c 100%);
  background-attachment: fixed;
}

.glass-card {
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
}

.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.glass-input:focus {
  background: rgba(255, 255, 255, 0.15) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
}

.hover-lift {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.hover-lift:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3) !important;
}

.hover-underline:hover {
  text-decoration: underline !important;
}

/* Fix for checkbox focus vs background */
.form-check-input:checked {
  background-color: white !important;
  border-color: white !important;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3e%3cpath fill='none' stroke='%234a192c' stroke-linecap='round' stroke-linejoin='round' stroke-width='3' d='m6 10 3 3 6-6'/%3e%3c/svg%3e") !important;
}

/* Google Login Button */
.btn-google {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: #3c4043;
  border: 1.5px solid rgba(255,255,255,0.35);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
  letter-spacing: 0.2px;
  padding: 10px 0;
}

.btn-google:hover:not(:disabled) {
  background: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.28);
}

.btn-google:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.divider hr {
  border-color: rgba(255, 255, 255, 0.3);
  margin: 0;
}
</style>
