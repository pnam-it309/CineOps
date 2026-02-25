<template>
  <div class="d-flex flex-column h-100 bg-white border-end shadow-sm">
    <div
      class="d-flex align-items-center justify-content-center gap-2 p-3 text-white fw-bold fs-4 admin-header"
      style="height: 60px;">
      <img v-if="!isCollapse" src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg" alt="CineOps Logo" class="sidebar-logo" />
      <!-- <transition name="fade">
      </transition> -->
    </div>

    <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" background-color="#ffffff"
      text-color="#475569" active-text-color="#E31E24" class="flex-grow-1 border-0 overflow-auto py-2"
      style="--el-menu-bg-color: #ffffff; --el-menu-text-color: #475569; --el-menu-active-color: #E31E24;" router
      menu-trigger="hover">
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.DASHBOARD)">
        <el-icon><DataAnalysis /></el-icon>
        <template #title>Tổng quan</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.STATISTICS)">
        <el-icon><Histogram /></el-icon>
        <template #title>Thống kê</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.POS)">
        <el-icon><Sell /></el-icon>
        <template #title>Đặt vé tại quầy</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.CHECKIN)">
        <el-icon><CircleCheck /></el-icon>
        <template #title>Check-in vé</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.INVOICES)">
        <el-icon><Document /></el-icon>
        <template #title>Quản lý hoá đơn</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.TICKETS)">
        <el-icon><Ticket /></el-icon>
        <template #title>Quản lý vé</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SEATS)">
        <el-icon><Place /></el-icon>
        <template #title>Quản lý ghế</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE)">
        <el-icon><Film /></el-icon>
        <template #title>Quản lý Phim & Lịch chiếu</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES)">
        <el-icon><Calendar /></el-icon>
        <template #title>Quản lý suất chiếu</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.FOOD)">
        <el-icon><Coffee /></el-icon>
        <template #title>Quản lý sản phẩm đi kèm</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.VOUCHERS)">
        <el-icon><PriceTag /></el-icon>
        <template #title>Quản lý phiếu giảm giá</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS)">
        <el-icon><Avatar /></el-icon>
        <template #title>Quản lý khách hàng</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.STAFF)">
        <el-icon><User /></el-icon>
        <template #title>Quản lý nhân viên</template>
      </el-menu-item>
    </el-menu>

    <!-- User Profile at Bottom -->
    <div class="p-3 border-top" v-if="!isCollapse">
      <el-dropdown trigger="click" @command="handleCommand" class="w-100">
        <div class="d-flex align-items-center gap-2 cursor-pointer text-dark text-decoration-none">
          <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          <span class="text-truncate small fw-medium">{{ authStore.user?.username || 'Admin' }}</span>
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
  Histogram,
  Sell,
  CircleCheck,
  Document,
  Ticket,
  Place,
  Film,
  Calendar,
  Coffee,
  PriceTag,
  Avatar,
  User
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
  return `${ROUTES_CONSTANTS.ADMIN.path}/${childRoute.path}`;
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
/* CSS cho icon to hơn và cân đối */
:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  font-size: 22px !important; /* Phóng to icon lên 22px */
  margin-right: 12px !important;
  width: 24px !important;
  display: inline-flex !important;
  justify-content: center !important;
}

/* Hiệu ứng hover cho menu item và submenu title trong mode sáng */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #f8fafc !important;
  color: #E31E24 !important;
}

/* Biểu tượng cũng đổi màu khi hover */
:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
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

.sidebar-logo {
  height: 36px;
  width: auto;
  object-fit: contain;
  border-radius: 4px;
}

.admin-header {
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
}
</style>

