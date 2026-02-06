<template>
  <div class="d-flex min-vh-100 flex-column">
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
      <div class="container-responsive">
        <router-link :to="{ name: 'Home' }" class="navbar-brand fw-bold">
          <i class="bi bi-film me-2"></i>CineOps
        </router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
          <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link :to="{ name: 'Home' }" class="nav-link">Phim</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/showtimes" class="nav-link">Lịch Chiếu</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/promotions" class="nav-link">Khuyến Mãi</router-link>
            </li>
          </ul>
          
          <ul class="navbar-nav ms-auto">
            <template v-if="authStore.isAuthenticated">
               <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                  Xin chào, {{ authStore.user?.username || 'User' }}
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                   <li><router-link :to="{ name: 'CustomerProfile' }" class="dropdown-item">Hồ sơ cá nhân</router-link></li>
                   <li><router-link :to="{ name: 'CustomerTickets' }" class="dropdown-item">Vé của tôi</router-link></li>
                   <li><hr class="dropdown-divider"></li>
                   <li><a class="dropdown-item" href="#" @click.prevent="handleLogout">Đăng xuất</a></li>
                </ul>
              </li>
            </template>
            <template v-else>
              <li class="nav-item">
                <router-link :to="{ name: 'Login' }" class="btn btn-outline-light btn-sm me-2">Đăng Nhập</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/register" class="btn btn-primary btn-sm">Đăng Ký</router-link>
              </li>
            </template>
          </ul>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-grow-1">
      <router-view></router-view>
    </main>

    <!-- Footer -->
    <footer class="bg-dark text-white py-4 mt-auto">
      <div class="container-responsive text-center">
        <p class="mb-0">&copy; 2026 CineOps. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = async () => {
  await authStore.logout();
  router.push({ name: 'Login' });
};
</script>
