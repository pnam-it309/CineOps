<template>
  <div class="d-flex flex-column h-100 bg-white sidebar-inner-wrapper">
    <div
      class="logo-container"
      :style="{ height: isCollapse ? '60px' : '120px' }"
    >
      <img
        src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg"
        alt="CineOps Logo"
        :class="['brand-logo', { 'is-collapsed': isCollapse }]"
      />
    </div>

    <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" background-color="#ffffff"
      text-color="#475569" active-text-color="#E31E24" class="border-0 overflow-auto py-2 h-100"
      style="--el-menu-bg-color: #ffffff; --el-menu-text-color: #475569; --el-menu-active-color: #E31E24;" router
      menu-trigger="hover">
      
      <!-- ═══════════════════════════════════════════════════════ -->
      <!-- KHỐI 1: ROOT MENU (1-Click - High Frequency Operations) -->
      <!-- ═══════════════════════════════════════════════════════ -->
      
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.DASHBOARD)">
        <el-icon><DataAnalysis /></el-icon>
        <template #title>Tổng quan</template>
      </el-menu-item>

      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.POS)">
        <el-icon><Sell /></el-icon>
        <template #title>Đặt vé tại quầy</template>
      </el-menu-item>

      <el-menu-item
        :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.CHECKIN)"
      >
        <el-icon><CircleCheck /></el-icon>
        <template #title>Check-in vé</template>
      </el-menu-item>

      <!-- ═══════════════════════════════════════════════════════ -->
      <!-- KHỐI 2: SUB-MENUS (2-Click - CRUD/Management)          -->
      <!-- ═══════════════════════════════════════════════════════ -->
      
      <!-- 📁 Quản lý Giao dịch -->
      <el-sub-menu index="admin-transaction">
        <template #title>
          <el-icon><Wallet /></el-icon>
          <span>Quản lý Giao dịch</span>
        </template>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.INVOICES)">
          <el-icon><Document /></el-icon>
          <template #title>Quản lý hóa đơn</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.TICKETS)">
          <el-icon><Ticket /></el-icon>
          <template #title>Quản lý vé</template>
        </el-menu-item>
      </el-sub-menu>

      <!-- 📁 Quản lý Phim & Rạp -->
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE)">
        <el-icon><VideoCamera /></el-icon>
        <template #title>Quản lý phim</template>
      </el-menu-item>
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.ROOMS)">
        <el-icon><OfficeBuilding /></el-icon>
        <template #title>Phòng chiếu</template>
      </el-menu-item>
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SEATS)">
        <el-icon><Monitor /></el-icon>
        <template #title>Ghế</template>
      </el-menu-item>
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES)">
        <el-icon><AlarmClock /></el-icon>
        <template #title>Suất chiếu</template>
      </el-menu-item>
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SCHEDULE)">
        <el-icon><Calendar /></el-icon>
        <template #title>Lịch chiếu</template>
      </el-menu-item>

      <!-- 📁 Dịch vụ & Marketing -->
      <el-sub-menu index="admin-service-marketing">
        <template #title>
          <el-icon><ShoppingBag /></el-icon>
          <span>Dịch vụ & Marketing</span>
        </template>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.FOOD)">
          <el-icon><Coffee /></el-icon>
          <template #title>Sản phẩm đi kèm</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.VOUCHERS)">
          <el-icon><PriceTag /></el-icon>
          <template #title>Phiếu giảm giá</template>
        </el-menu-item>
      </el-sub-menu>

      <!-- 📁 Hệ thống & Thống kê -->
      <el-sub-menu index="admin-system-stats">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>Hệ thống & Thống kê</span>
        </template>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.STATISTICS)">
          <el-icon><Histogram /></el-icon>
          <template #title>Báo cáo thống kê</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS)">
          <el-icon><User /></el-icon>
          <template #title>Quản lý khách hàng</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.STAFF)">
          <el-icon><Avatar /></el-icon>
          <template #title>Quản lý nhân viên</template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

    <!-- User Profile at Bottom -->
    <div class="p-3 border-top" v-if="!isCollapse">
      <el-dropdown trigger="click" @command="handleCommand" class="w-100">
        <div
          class="d-flex align-items-center gap-2 cursor-pointer text-dark text-decoration-none"
        >
          <el-avatar
            :size="32"
            src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
          />
          <span class="text-truncate small fw-medium">{{
            authStore.user?.username || "Admin"
          }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">Hồ Sơ</el-dropdown-item>
            <el-dropdown-item divided command="logout"
              >Đăng Xuất</el-dropdown-item
            >
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { ROUTES_CONSTANTS } from "@/constants/routeConstants";
import {
  DataAnalysis,
  Histogram,
  Sell,
  CircleCheck,
  Document,
  Ticket,
  Calendar,
  AlarmClock,
  Coffee,
  PriceTag,
  Avatar,
  User,
  Monitor,
  Setting,
  Wallet,
  VideoCamera,
  OfficeBuilding,
  ShoppingBag
} from '@element-plus/icons-vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    required: true,
  },
});

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const activeMenu = computed(() => {
  return route.fullPath;
});

const getRoutePath = (childRoute) => {
  return `${ROUTES_CONSTANTS.ADMIN.path}/${childRoute.path}`;
};

const handleCommand = async (command) => {
  if (command === "logout") {
    await authStore.logout();
    router.push(ROUTES_CONSTANTS.LOGIN.path);
  } else if (command === "profile") {
    console.log("Navigate to profile");
  }
};
</script>

<style scoped>
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  margin: 0 !important;
  border-radius: 0 !important;
  height: 54px !important;
  line-height: 54px !important;
  transition: background-color 0.3s !important;
  display: flex !important;
  align-items: center !important;
  font-size: 15px !important;
  font-weight: 500 !important;
}

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

:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  font-size: 24px !important;
  margin-right: 14px !important;
  min-width: 36px;
  flex-shrink: 0;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
}

:deep(.el-menu--collapse) {
  width: 80px !important;
}

:deep(.el-menu--collapse .el-menu-item .el-icon),
:deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin: 0 !important;
  font-size: 26px !important;
}

:deep(.el-menu--collapse .el-menu-item),
:deep(.el-sub-menu__title) {
  justify-content: center !important;
  padding: 0 15px !important;
}

:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
  color: #e31e24 !important;
  transform: scale(1.2) rotate(5deg);
}

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
  transform: rotateZ(-90deg) !important;
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transform: rotateZ(0deg) !important;
}

:deep(.el-menu-item.is-active) {
  color: #e31e24 !important;
  background-color: #fff1f1 !important;
  border-right: 3px solid #e31e24;
  font-weight: 600 !important;
}

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

.el-menu::-webkit-scrollbar {
  display: none;
}

:deep(.el-menu) {
  border-right: none !important;
  border-inline-end: none !important;
}

.sidebar-inner-wrapper {
  position: relative;
  z-index: 11;
  border-right: none !important;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.brand-logo {
  width: auto;
  height: auto;
  max-width: 90%;
  max-height: 140px;
  object-fit: contain;
  mix-blend-mode: multiply;
  filter: brightness(1.08) contrast(1.1);
  transition: all 0.3s;
  transform: scale(1.8);
}

.brand-logo.is-collapsed {
  max-height: 60px;
  width: auto;
  transform: scale(2.0);
}

:deep(.el-menu-item span),
:deep(.el-sub-menu span) {
  white-space: normal;
  line-height: 1.2;
}
</style>
