<template>
  <div class="d-flex flex-column h-100 bg-white sidebar-inner-wrapper">
    <div class="logo-container" :style="{ height: isCollapse ? '50px' : '80px' }">
      <img src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg" alt="CineOps Logo" :class="['brand-logo', { 'is-collapsed': isCollapse }]" />
    </div>

    <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" background-color="#ffffff"
      text-color="#475569" active-text-color="#E31E24" class="border-0 overflow-auto py-2 h-100"
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

      <el-sub-menu index="admin-seats-submenu">
        <template #title>
          <el-icon><Place /></el-icon>
          <span>Quản lý ghế</span>
        </template>
        <el-menu-item :index="getNamedRoutePath(ROUTES_CONSTANTS.ADMIN.children.SEATS.children.LIST.name)">
          <el-icon><Tickets /></el-icon>
          <template #title>Danh sách ghế</template>
        </el-menu-item>
        <el-menu-item :index="getNamedRoutePath(ROUTES_CONSTANTS.ADMIN.children.SEATS.children.LAYOUT.name)">
          <el-icon><Monitor /></el-icon>
          <template #title>Sơ đồ ghế</template>
        </el-menu-item>
        <el-menu-item :index="getNamedRoutePath(ROUTES_CONSTANTS.ADMIN.children.SEATS.children.CONFIG.name)">
          <el-icon><Setting /></el-icon>
          <template #title>Cấu hình phòng</template>
        </el-menu-item>
      </el-sub-menu>

      <!-- Quản lý phim — item trực tiếp -->
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE)">
        <el-icon><Film /></el-icon>
        <template #title>Quản lý phim</template>
      </el-menu-item>

      <!-- Quản lý lịch chiếu — item trực tiếp, chỉ có sơ đồ -->
      <el-menu-item :index="getNamedRoutePath(ROUTES_CONSTANTS.ADMIN.children.SCHEDULE.children.VISUAL.name)">
        <el-icon><Grid /></el-icon>
        <template #title>Quản lý lịch chiếu</template>
      </el-menu-item>

      <!-- Quản lý suất chiếu — item trực tiếp, không dropdown -->
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES)">
        <el-icon><AlarmClock /></el-icon>
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
  AlarmClock,
  Coffee,
  PriceTag,
  Avatar,
  User,
  Tickets,
  Monitor,
  Setting,
  VideoPlay,
  Grid
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
  // Dùng fullPath (path + query) để match đúng với menu items có ?tab=xxx
  return route.fullPath;
});

// Helper to construct path from named route
const getNamedRoutePath = (routeName) => {
  const r = router.resolve({ name: routeName });
  return r ? r.path : '';
};

// Helper to construct full path for navigation
const getRoutePath = (childRoute) => {
  return `${ROUTES_CONSTANTS.ADMIN.path}/${childRoute.path}`;
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
/* Simplified menu item styles to match Sample 2 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  margin: 0 !important;
  border-radius: 0 !important;
  height: 45px !important;
  line-height: 45px !important;
  transition: background-color 0.3s !important;
  display: flex !important;
  align-items: center !important;
}

/* Text animation */
:deep(.el-menu-item span),
:deep(.el-sub-menu__title span) {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-block;
}

:deep(.el-menu-item:hover span),
:deep(.el-sub-menu__title:hover span) {
  transform: translateX(5px);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #f8fafc !important;
}

/* Icon enhancement */
:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  font-size: 20px !important; /* Thu nhỏ icon */
  margin-right: 12px !important;
  min-width: 32px;
  flex-shrink: 0;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
}

/* Căn giữa icon khi thu gọn */
:deep(.el-menu--collapse) {
  width: 80px !important;
}

:deep(.el-menu--collapse .el-menu-item .el-icon),
:deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin: 0 !important;
  font-size: 24px !important; /* Thu nhỏ icon khi thu gọn */
}

:deep(.el-menu--collapse .el-menu-item),
:deep(.el-menu--collapse .el-sub-menu__title) {
  justify-content: center !important;
  padding: 0 !important;
}

:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
  color: #E31E24 !important;
  transform: scale(1.2) rotate(5deg);
}

/* Fix misalignment and size for the arrow icon */
/* Triệt tiêu mũi tên khi thu gọn - Đây là nguyên nhân gây lệch icon */
:deep(.el-menu--collapse .el-sub-menu__icon-arrow) {
  display: none !important;
}

:deep(.el-sub-menu__icon-arrow) {
  position: static !important;
  margin: 0 !important;
  margin-left: auto !important;
  font-size: 18px !important;
  color: inherit !important;
  transform: none !important;
}

:deep(.el-sub-menu__title:hover .el-sub-menu__icon-arrow) {
  transform: rotateZ(-90deg) !important; /* Standard EL arrow rotation on hover/open */
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transform: rotateZ(0deg) !important; 
}
/* Note: Element Plus handles rotation internally, 
   but since we forced position static and font-size, 
   we might need to ensure it still looks good. 
   Actually, usually just removing the 28px inheritance fixes it. */

/* Active State indicator like Sample 2 (usually side border or just color) */
:deep(.el-menu-item.is-active) {
  color: #E31E24 !important;
  background-color: #fff1f1 !important;
  border-right: 3px solid #E31E24;
  font-weight: 600 !important;
}

/* Red indicator on the left for specific active item */
:deep(.el-menu-item.is-active::before) {
  display: none; /* Removed the previous floating indicator */
}

/* Transition optimizations */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.cursor-pointer {
  cursor: pointer;
}

/* Hide scrollbar and borders */
.el-menu::-webkit-scrollbar {
  display: none;
}

:deep(.el-menu) {
  border-right: none !important;
  border-inline-end: none !important;
}

:deep(.el-aside) {
  min-height: 100vh;
  background: #ffffff !important;
  border-right: 1px solid #cbd5e1 !important;
  z-index: 10;
  box-shadow: none !important;
}

.sidebar-inner-wrapper {
  position: relative;
  z-index: 11;
  border-right: none !important; /* Clean up any internal border */
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.brand-logo {
  width: 100%;
  height: auto;
  max-width: 100%;
  max-height: 120px;
  object-fit: contain;
  mix-blend-mode: multiply;
  filter: brightness(1.08) contrast(1.1);
  transition: all 0.3s;
  transform: scale(0.9);
}

.brand-logo.is-collapsed {
  max-height: 80px;
  width: 100%;
  transform: scale(1.1);
}
</style>
