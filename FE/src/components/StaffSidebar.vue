<template>
  <div class="d-flex flex-column h-100 bg-white border-end shadow-sm">
    <div class="logo-section" :style="{ height: isCollapse ? '60px' : '120px' }">
      <img src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg" alt="CineOps Logo" :class="['sidebar-logo', { 'logo-collapse': isCollapse }]" />
    </div>

    <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" background-color="#ffffff"
      text-color="#475569" active-text-color="#E31E24" class="flex-grow-1 border-0 overflow-auto py-2"
      style="--el-menu-bg-color: #ffffff; --el-menu-text-color: #475569; --el-menu-active-color: #E31E24;" router
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
    <div class="p-3 border-top" v-if="!isCollapse">
      <el-dropdown trigger="click" @command="handleCommand" class="w-100">
        <div class="d-flex align-items-center gap-2 cursor-pointer text-dark text-decoration-none">
          <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          <span class="text-truncate small fw-medium">{{ authStore.user?.username || 'Staff' }}</span>
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

const handleCommand = async (command) => {
  if (command === 'logout') {
    await authStore.logout();
    router.push(ROUTES_CONSTANTS.LOGIN.path);
  } else if (command === 'profile') {
    console.log('Navigate to profile');
  }
};
</script>

<style scoped>
/* CSS cho icon to hơn và cân đối */
:deep(.el-menu-item .el-icon) {
  font-size: 1.3rem !important; /* ~21px */
  margin-right: 12px !important;
  width: 24px !important;
  display: inline-flex !important;
  justify-content: center !important;
}

/* Hiệu ứng hover màu trắng cho menu item trong mode sáng */
:deep(.el-menu-item:hover) {
  background-color: #f8fafc !important;
  color: #E31E24 !important;
}

/* Biểu tượng cũng đổi màu khi hover */
:deep(.el-menu-item:hover .el-icon) {
  color: #E31E24 !important;
}

/* Highlight menu item đang active */
:deep(.el-menu-item.is-active) {
  background-color: #fef2f2 !important;
  color: #E31E24 !important;
  border-right: 3px solid #E31E24;
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

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #ffffff;
  transition: all 0.3s;
}

.sidebar-logo {
  width: 130%; /* Phóng to ra ngoài khung để loại bỏ khoảng trắng dư của ảnh */
  height: 130%;
  object-fit: contain;
  mix-blend-mode: multiply;
  filter: contrast(1.1) brightness(1.1);
  transition: all 0.3s;
  transform: scale(1.3); /* Phóng đại thêm để logo to nhất có thể */
}

.sidebar-logo.logo-collapse {
  width: 100%;
  height: 100%;
  transform: scale(1);
}
</style>

