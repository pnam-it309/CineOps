<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { isValidEmail } from '@/utils/validators';

const router = useRouter();
const email = ref('');
const loading = ref(false);
const submitted = ref(false);

const handleReset = async () => {
  // Validate email first
  if (!isValidEmail(email.value)) {
    ElMessage.error('Vui lòng nhập địa chỉ email hợp lệ');
    return;
  }
  
  loading.value = true;
  try {
    // Mock reset request
    await new Promise(resolve => setTimeout(resolve, 1500));
    submitted.value = true;
    ElMessage.success('Liên kết đặt lại đã được gửi đến email của bạn');
  } catch (err) {
    ElMessage.error('Có lỗi xảy ra. Vui lòng thử lại.');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container d-flex align-items-center justify-content-center min-vh-100">
    <div class="glass-card p-4 p-md-5 bg-white bg-opacity-10 border border-white border-opacity-25 shadow-lg rounded-4 m-3 w-100" style="max-width: 450px;">
      <h2 class="display-6 fw-bold mb-3 text-center text-white">Đặt lại mật khẩu</h2>
      
      <div v-if="!submitted">
        <p class="text-white text-center opacity-75 mb-4 px-2">
          Nhập địa chỉ email của bạn và chúng tôi sẽ gửi liên kết để đặt lại mật khẩu.
        </p>
        
        <form @submit.prevent="handleReset">
          <div class="mb-4">
            <input 
              type="email" 
              class="glass-input form-control bg-white bg-opacity-10 border-white border-opacity-25 rounded-pill text-white py-2 px-4 shadow-none" 
              placeholder="Địa chỉ Email" 
              v-model="email" 
              required
            >
          </div>
          
          <button 
            type="submit" 
            class="btn btn-light w-100 rounded-pill py-2 fw-semibold mb-3 border-0 shadow-sm hover-lift" 
            :disabled="loading"
          >
            {{ loading ? '...' : 'Gửi liên kết đặt lại' }}
          </button>
        </form>
      </div>

      <div v-else class="text-center text-white py-4">
        <div class="mb-4">
          <el-icon size="60" class="text-success"><CircleCheck /></el-icon>
        </div>
        <h4 class="fw-bold mb-3">Kiểm tra Email của bạn</h4>
        <p class="opacity-75 mb-4">
          Chúng tôi đã gửi liên kết đặt lại mật khẩu đến <br><strong>{{ email }}</strong>
        </p>
        <button @click="submitted = false" class="btn btn-outline-light rounded-pill px-4">
          Chờ đã, tôi dùng sai email
        </button>
      </div>

      <div class="text-center text-white mt-4 pt-3 border-top border-white border-opacity-10">
        <router-link to="/login" class="text-white fw-bold text-decoration-none hover-underline">
          <i class="bi bi-arrow-left me-1"></i> Quay lại Đăng nhập
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
// For CircleCheck icon
import { CircleCheck } from '@element-plus/icons-vue'
export default {
  components: { CircleCheck }
}
</script>

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
</style>
