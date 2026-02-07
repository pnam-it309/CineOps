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
      router.push({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name });
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
  <div class="login-container d-flex align-items-center justify-content-center min-vh-100">
    <!-- Glass Card with Bootstrap utilities -->
    <div class="glass-card p-4 p-md-5 bg-white bg-opacity-10 border border-white border-opacity-25 shadow-lg rounded-4 m-3 w-100" style="max-width: 420px;">
      <h2 class="display-5 fw-bold mb-4 text-center text-white">Login</h2>
      
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <input 
            type="text" 
            class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none" 
            placeholder="Username" 
            v-model="username" 
            required
          >
        </div>
        
        <div class="mb-3">
          <input 
            type="password" 
            class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none" 
            placeholder="Password" 
            v-model="password" 
            required
          >
        </div>
        
        <div class="d-flex justify-content-between align-items-center mb-4 text-white small px-1">
          <div class="form-check m-0">
            <input class="form-check-input bg-transparent border-white" type="checkbox" id="rememberMe">
            <label class="form-check-label" for="rememberMe">Remember me</label>
          </div>
          <a href="#" class="text-white text-decoration-none hover-underline">Forgot password?</a>
        </div>

        <div v-if="error" class="alert alert-danger py-2 mb-3 small opacity-90">{{ error }}</div>
        
        <button 
          type="submit" 
          class="btn btn-light w-100 rounded-pill py-2 fw-semibold mb-4 border-0 shadow-sm hover-lift" 
          :disabled="loading"
        >
          {{ loading ? '...' : 'Login' }}
        </button>

        <div class="text-center text-white">
          <span class="opacity-75">Don't have an account? </span>
          <router-link to="/register" class="text-white fw-bold text-decoration-none hover-underline">Register</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  background: linear-gradient(135deg, #1a1c2c 0%, #4a192c 100%);
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
</style>
