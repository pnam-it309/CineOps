<template>
  <el-container class="min-vh-100 d-flex flex-column">
    <!-- Top Navigation Bar -->
    <el-header class="shadow-sm p-0" style="height: 64px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
      <div class="container-xl h-100 d-flex align-items-center justify-content-between px-3">
        <div class="d-flex align-items-center">
          <router-link :to="{ name: 'Home' }" class="d-flex align-items-center gap-2 text-white text-decoration-none fs-4 fw-bold">
            <el-icon :size="24"><VideoPlay /></el-icon>
            <span class="d-none d-md-block">CineOps</span>
          </router-link>
        </div>

        <!-- Desktop Menu -->
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="d-none d-lg-flex flex-grow-1 justify-content-center bg-transparent border-0"
          :ellipsis="false"
          text-color="rgba(255, 255, 255, 0.9)"
          active-text-color="#ffffff"
          style="--el-menu-bg-color: transparent; --el-menu-hover-bg-color: rgba(255, 255, 255, 0.1);"
          router
        >
          <el-menu-item index="/home">
            <el-icon><Film /></el-icon>
            <span>Phim</span>
          </el-menu-item>
          <el-menu-item index="/showtimes">
            <el-icon><Calendar /></el-icon>
            <span>Lịch Chiếu</span>
          </el-menu-item>
          <el-menu-item index="/promotions">
            <el-icon><Present /></el-icon>
            <span>Khuyến Mãi</span>
          </el-menu-item>
        </el-menu>

        <!-- User Actions -->
        <div class="d-flex align-items-center gap-3">
          <template v-if="authStore.isAuthenticated">
            <el-dropdown trigger="click" @command="handleCommand" class="d-none d-lg-block">
              <div class="d-flex align-items-center gap-2 cursor-pointer text-white py-1 px-3 rounded-pill user-dropdown-hover">
                <el-avatar :size="32" :src="userAvatar" />
                <span class="fw-medium small">{{ authStore.user?.username || 'User' }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    <span>Hồ sơ cá nhân</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="tickets">
                    <el-icon><Ticket /></el-icon>
                    <span>Vé của tôi</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    <span>Đăng xuất</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="d-none d-lg-flex gap-2">
              <el-button type="default" size="default" @click="$router.push({ name: 'Login' })">
                Đăng Nhập
              </el-button>
              <el-button type="primary" size="default" @click="$router.push('/register')">
                Đăng Ký
              </el-button>
            </div>
          </template>

          <!-- Mobile Menu Toggle -->
          <el-icon class="d-lg-none fs-4 text-white cursor-pointer" @click="drawerVisible = true">
            <Menu />
          </el-icon>
        </div>
      </div>
    </el-header>

    <!-- Main Content -->
    <el-main class="flex-fill bg-light p-0 w-100">
      <div class="container-xl py-0">
        <router-view />
      </div>
    </el-main>

    <!-- Footer -->
    <el-footer class="bg-dark text-white text-center py-4 h-auto">
      <div class="container-xl">
        <p class="m-0 small">&copy; 2026 CineOps. All rights reserved.</p>
      </div>
    </el-footer>

    <!-- Mobile Drawer -->
    <el-drawer
      v-model="drawerVisible"
      title="Menu"
      direction="ltr"
      size="280px"
    >
      <el-menu :default-active="activeMenu" router @select="drawerVisible = false" class="border-0">
        <el-menu-item index="/home">
          <el-icon><Film /></el-icon>
          <span>Phim</span>
        </el-menu-item>
        <el-menu-item index="/showtimes">
          <el-icon><Calendar /></el-icon>
          <span>Lịch Chiếu</span>
        </el-menu-item>
        <el-menu-item index="/promotions">
          <el-icon><Present /></el-icon>
          <span>Khuyến Mãi</span>
        </el-menu-item>
        
        <template v-if="authStore.isAuthenticated">
          <el-divider />
          <el-menu-item index="/customer/profile">
            <el-icon><User /></el-icon>
            <span>Hồ sơ cá nhân</span>
          </el-menu-item>
          <el-menu-item index="/customer/tickets">
            <el-icon><Ticket /></el-icon>
            <span>Vé của tôi</span>
          </el-menu-item>
          <el-menu-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>Đăng xuất</span>
          </el-menu-item>
        </template>
        <template v-else>
          <el-divider />
          <el-menu-item index="/login">
            <el-icon><User /></el-icon>
            <span>Đăng Nhập</span>
          </el-menu-item>
          <el-menu-item index="/register">
            <el-icon><VideoPlay /></el-icon>
            <span>Đăng Ký</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
  VideoPlay,
  Film,
  Calendar,
  Present,
  User,
  Ticket,
  SwitchButton,
  ArrowDown,
  Menu
} from '@element-plus/icons-vue';

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const drawerVisible = ref(false);

const activeMenu = computed(() => route.path);

const userAvatar = computed(() => 
  'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
);

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push({ name: 'CustomerProfile' });
  } else if (command === 'tickets') {
    router.push({ name: 'CustomerTickets' });
  } else if (command === 'logout') {
    handleLogout();
  }
};

const handleLogout = async () => {
  await authStore.logout();
  router.push({ name: 'Login' });
  drawerVisible.value = false;
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.user-dropdown-hover:hover {
  background: rgba(255, 255, 255, 0.1);
}
</style>
