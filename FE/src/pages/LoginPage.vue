<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const router = useRouter();
const authStore = useAuthStore();
const username = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

const handleLogin = async () => {
  loading.value = true;
  error.value = '';
  try {
    // authStore.login action handles calling service and fetching profile
    await authStore.login({ username: username.value, password: password.value });
    
    // Redirect based on role
    if (authStore.isAdmin) {
      router.push({ name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name });
    } else if (authStore.isStaff) {
      router.push({ name: ROUTES_CONSTANTS.STAFF.children.BOOKINGS.name });
    } else {
      router.push({ name: ROUTES_CONSTANTS.HOME.name });
    }
  } catch (err) {
    error.value = 'Login failed. Please check your credentials.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="p-5 bg-white shadow rounded-3" style="width: 400px;">
      <h2 class="fw-bold mb-4 text-center">Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="form-label">Username</label>
          <input type="text" class="form-control" v-model="username" required>
        </div>
        <div class="mb-4">
          <label class="form-label">Password</label>
          <input type="password" class="form-control" v-model="password" required>
        </div>
        <div v-if="error" class="alert alert-danger mb-3">{{ error }}</div>
        <button type="submit" class="btn btn-primary w-100" :disabled="loading">
          {{ loading ? 'Signing In...' : 'Sign In' }}
        </button>
      </form>
    </div>
  </div>
</template>
