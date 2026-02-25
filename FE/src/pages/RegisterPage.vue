<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { isValidEmail, isValidPhone, validatePassword } from '@/utils/validators';

const router = useRouter();
const name = ref('');
const email = ref('');
const phone = ref('');
const password = ref('');
const confirmPassword = ref('');
const loading = ref(false);

const handleRegister = async () => {
  // Validate email
  if (!isValidEmail(email.value)) {
    ElMessage.error('Email không hợp lệ');
    return;
  }

  // Validate phone
  if (!isValidPhone(phone.value)) {
    ElMessage.error('Số điện thoại không hợp lệ');
    return;
  }

  // Validate password
  const passwordValidation = validatePassword(password.value);
  if (!passwordValidation.isValid) {
    ElMessage.error(passwordValidation.errors[0]);
    return;
  }

  // Check password match
  if (password.value !== confirmPassword.value) {
    ElMessage.error('Mật khẩu không khớp');
    return;
  }

  loading.value = true;
  try {
    // Mock registration
    await new Promise(resolve => setTimeout(resolve, 1500));
    ElMessage.success('Đăng ký thành công! Vui lòng đăng nhập.');
    router.push('/login');
  } catch (err) {
    ElMessage.error('Đăng ký thất bại. Vui lòng thử lại.');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container d-flex align-items-center justify-content-center min-vh-100">
    <div
      class="glass-card p-4 p-md-5 bg-white bg-opacity-10 border border-white border-opacity-25 shadow-lg rounded-4 m-3 w-100"
      style="max-width: 500px;">
      <h2 class="display-5 fw-bold mb-4 text-center text-white">Đăng ký</h2>

      <form @submit.prevent="handleRegister">
        <div class="row g-3 mb-3">
          <div class="col-md-12">
            <input type="text"
              class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
              placeholder="Họ và tên" v-model="name" required>
          </div>
          <div class="col-md-12">
            <input type="email"
              class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
              placeholder="Địa chỉ Email" v-model="email" required>
          </div>
          <div class="col-md-12">
            <input type="tel"
              class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
              placeholder="Số điện thoại" v-model="phone" required>
          </div>
          <div class="col-md-6">
            <input type="password"
              class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
              placeholder="Mật khẩu" v-model="password" required>
          </div>
          <div class="col-md-6">
            <input type="password"
              class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none"
              placeholder="Xác nhận mật khẩu" v-model="confirmPassword" required>
          </div>
        </div>

        <div class="form-check mb-4 text-white small ms-2">
          <input class="form-check-input bg-transparent border-white" type="checkbox" id="terms" required>
          <label class="form-check-label" for="terms">
            Tôi đồng ý với các <a href="#" class="text-white fw-bold text-decoration-none">Điều khoản & Điều kiện</a>
          </label>
        </div>

        <button type="submit"
          class="btn btn-light w-100 rounded-pill py-2 fw-semibold mb-4 border-0 shadow-sm hover-lift"
          :disabled="loading">
          {{ loading ? '...' : 'Tạo tài khoản' }}
        </button>

        <div class="text-center text-white">
          <span class="opacity-75">Đã có tài khoản? </span>
          <router-link to="/login" class="text-white fw-bold text-decoration-none hover-underline">Đăng
            nhập</router-link>
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

.form-check-input:checked {
  background-color: white !important;
  border-color: white !important;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3e%3cpath fill='none' stroke='%234a192c' stroke-linecap='round' stroke-linejoin='round' stroke-width='3' d='m6 10 3 3 6-6'/%3e%3c/svg%3e") !important;
}
</style>
