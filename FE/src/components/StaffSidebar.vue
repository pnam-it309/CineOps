<template>
  <div class="d-flex flex-column h-100 bg-dark text-white">
    <div
      class="d-flex align-items-center justify-content-center gap-2 p-3 text-white fw-bold fs-4 border-bottom border-light border-opacity-10 staff-header"
      style="height: 60px;">
      <img v-if="!isCollapse" src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg" alt="CineOps Logo" class="sidebar-logo" />
      <transition name="fade">
        <span v-if="!isCollapse" class="text-nowrap">Nhân Viên CineOps</span>
        <span v-else class="fs-5">NV</span>
      </transition>
    </div>

    <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" background-color="#212529"
      text-color="#adb5bd" active-text-color="#E31E24" class="flex-grow-1 border-0 overflow-auto"
      style="--el-menu-bg-color: #212529; --el-menu-text-color: #adb5bd; --el-menu-active-color: #E31E24;" router
      menu-trigger="hover">
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.STAFF.children.DASHBOARD)">
        <el-icon>
          <DataAnalysis />
        </el-icon>
        <template #title>Điều Khiển POS</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.STAFF.children.SALES)">
        <el-icon>
          <ShoppingCart />
        </el-icon>
        <template #title>Quầy Bán Vé</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.STAFF.children.CHECKIN)">
        <el-icon>
          <CircleCheck />
        </el-icon>
        <template #title>Soát Vé (Check-in)</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.STAFF.children.INVOICES)">
        <el-icon>
          <Files />
        </el-icon>
        <template #title>Hóa Đơn Ngày</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.STAFF.children.SHIFT)">
        <el-icon>
          <Timer />
        </el-icon>
        <template #title>Bàn Giao Ca</template>
      </el-menu-item>
    </el-menu>

    <!-- User Profile at Bottom -->
    <div class="p-3 border-top border-light border-opacity-10" v-if="!isCollapse">
      <el-dropdown trigger="click" @command="handleCommand" class="w-100">
        <div class="d-flex align-items-center gap-2 cursor-pointer text-white text-decoration-none">
          <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          <span class="text-truncate small">{{ authStore.user?.username || 'Staff' }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">Hồ Sơ</el-dropdown-item>
            <el-dropdown-item divided command="logout">Đăng Xuất</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import {
  DataAnalysis,
  ShoppingCart,
  CircleCheck,
  Files,
  Timer
} from '@element-plus/icons-vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    required: true
  }
});

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const activeMenu = computed(() => {
  return route.path;
});

// Helper to construct full path for navigation
const getRoutePath = (childRoute) => {
  return `${ROUTES_CONSTANTS.STAFF.path}/${childRoute.path}`;
};

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout();
    router.push(ROUTES_CONSTANTS.LOGIN.path);
  } else if (command === 'profile') {
    console.log('Navigate to profile');
  }
};
</script>

<style scoped>
/* Hiệu ứng hover màu trắng cho menu item */
:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

/* Biểu tượng cũng chuyển trắng khi hover */
:deep(.el-menu-item:hover .el-icon) {
  color: #ffffff !important;
}

/* Transition optimizations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.cursor-pointer {
  cursor: pointer;
}

/* Hide scrollbar but maintain functionality */
.el-menu {
  scrollbar-width: none;
  /* Firefox */
  -ms-overflow-style: none;
  /* IE/Edge */
}

.el-menu::-webkit-scrollbar {
  display: none;
  /* Chrome, Safari, Edge */
}

.sidebar-logo {
  height: 36px;
  width: auto;
  object-fit: contain;
  border-radius: 4px;
}

.staff-header {
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
}
</style>
