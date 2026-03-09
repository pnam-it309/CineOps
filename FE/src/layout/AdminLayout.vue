<template>
  <MainLayout>
    <el-container class="admin-staff-layout" style="height: 100vh; overflow: hidden;">
      <!-- Sidebar -->
      <el-aside :width="uiStore.sidebarCollapsed ? '80px' : '250px'" class="bg-white transition-width"
        style="border-right: 1px solid #cbd5e1 !important; overflow: visible;">
        <AdminSidebar :is-collapse="uiStore.sidebarCollapsed" />
      </el-aside>

      <!-- Main Container -->
      <el-container class="d-flex flex-column bg-white" style="min-height: 0; flex: 1 1 0; overflow-x: hidden;">
        <!-- Top Navigation Bar -->
        <el-header class="d-flex align-items-center justify-content-between bg-white px-3 border-bottom border-light"
          style="height: 60px;">
          <div class="d-flex align-items-center gap-3">
            <el-icon class="fs-4 cursor-pointer text-dark" @click="uiStore.toggleSidebar()">
              <Fold v-if="!uiStore.sidebarCollapsed" />
              <Expand v-else />
            </el-icon>

            <el-breadcrumb separator="/">
              <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="{ path: item.path }">
                {{ item.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="d-flex align-items-center gap-2">


            <!-- User Dropdown (visible on small screens) -->
            <el-dropdown trigger="click" @command="handleCommand" class="d-block d-md-none">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <template #dropdown>
                <ul class="dropdown-menu-list p-0 m-0 list-unstyled">
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon>
                        <User />
                      </el-icon>
                      <span>Hồ Sơ</span>
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon>
                        <SwitchButton />
                      </el-icon>
                      <span>Đăng Xuất</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </ul>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- Main Content Area -->
        <el-main class="p-0 text-dark"
          style="flex: 1 1 0; min-height: 0; overflow-y: auto; display: flex; flex-direction: column;">
          <div class="admin-content-container">
            <router-view v-slot="{ Component, route }">
              <transition name="page-enter" appear>
                <component :is="Component" :key="route.fullPath" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </MainLayout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useUIStore } from '@/stores/ui';
import AdminSidebar from '@/components/AdminSidebar.vue';
import MainLayout from './MainLayout.vue';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import {
  Fold,
  Expand,
  SwitchButton,
  User,
  Monitor,
  House
} from '@element-plus/icons-vue';

const authStore = useAuthStore();
const uiStore = useUIStore();
const route = useRoute();
const router = useRouter();

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title);
  const breadcrumbList = matched.map(item => ({
    path: item.path,
    title: item.meta.title
  }));

  if (breadcrumbList.length === 0) {
    const pathSegments = route.path.split('/').filter(Boolean);
    return pathSegments.map((segment, index) => ({
      path: '/' + pathSegments.slice(0, index + 1).join('/'),
      title: segment.charAt(0).toUpperCase() + segment.slice(1)
    }));
  }

  return breadcrumbList;
});

const handleCommand = async (command) => {
  if (command === 'logout') {
    await authStore.logout();
    router.push(ROUTES_CONSTANTS.LOGIN.path);
  } else if (command === 'profile') {
    console.log('Navigate to profile');
  }
};

// Auto-collapse sidebar on mobile (uiStore handles localStorage)
if (window.innerWidth < 768) {
  uiStore.setSidebarCollapsed(true);
}
</script>

<style scoped>
.admin-content-container {
  padding: 25px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background-color: transparent;
}

.admin-content-container>* {
  width: 100%;
  flex: 1;
}

.admin-staff-layout {
  --text-primary: #1e293b !important;
  --text-secondary: #64748b !important;
  --text-tertiary: #94a3b8 !important;

  /* Element Plus Variable Overrides */
  --el-text-color-primary: #1e293b !important;
  --el-text-color-regular: #475569 !important;
  --el-text-color-secondary: #64748b !important;
  --el-border-color-light: #e2e8f0 !important;
  --el-border-color: #e2e8f0 !important;
  --el-border-color-lighter: #f1f5f9 !important;
  --el-bg-color: #ffffff !important;
  --el-bg-color-overlay: #ffffff !important;
  --el-fill-color-blank: #ffffff !important;
  --el-fill-color-light: #ffffff !important;

  color: #1e293b;
}

:deep(.el-aside) {
  min-height: 100vh;
  background: #ffffff !important;
  border-right: 1px solid #cbd5e1 !important;
  z-index: 10;
  overflow: visible !important;
}

.transition-width {
  transition: width 0.28s;
}

.cursor-pointer {
  cursor: pointer;
}

/* Breadcrumb overrides for light mode */
:deep(.el-header) {
  font-size: 14px;
}

:deep(.el-menu--collapse .el-menu-item .el-icon),
:deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin: 0 !important;
  font-size: 36px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

:deep(.el-menu--collapse .el-menu-item),
:deep(.el-menu--collapse .el-sub-menu__title) {
  justify-content: center !important;
  padding: 0 !important;
  width: 80px !important;
  /* Force width */
}

/* Table and Card overrides to ensure readability */
:deep(.el-card) {
  --el-card-bg-color: #ffffff;
  --el-card-border-color: #e2e8f0;
}

:deep(.el-table) {
  --el-table-border-color: #e2e8f0;
  --el-table-header-bg-color: #ffffff;
  --el-table-text-color: #1e293b;
  --el-table-header-text-color: #475569;
}

/* Font chữ table được quản lý tập trung trong /assets/css/admin-tables.css */

:global(.el-tag--large) {
  height: auto !important;
  padding: 8px 15px !important;
}

/* ✅ Fix: Dùng flex grow thay vì height: 100% để tương thích mọi mức zoom */
:deep(.el-main)>* {
  flex: 1 1 0;
  min-height: 0;
}

/* Global Pagination Overrides */
:deep(.el-pagination) {
  font-weight: 600;
  padding: 0 !important;
}

:deep(.el-pagination__sizes) {
  margin: 0 !important;
}

:deep(.el-pagination__jump) {
  margin: 0 !important;
  color: #1e293b !important;
}

/* Standardize inputs to prevent jiggling */
:deep(.el-pagination .el-input__wrapper) {
  border: 1px solid #e2e8f0 !important;
  box-shadow: none !important;
  border-radius: 6px !important;
}

/* Standard Scrollbar for Admin Light Theme */
:deep(.el-main)::-webkit-scrollbar,
:deep(.el-main *)::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

:deep(.el-main)::-webkit-scrollbar-track,
:deep(.el-main *)::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

:deep(.el-main)::-webkit-scrollbar-thumb,
:deep(.el-main *)::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
  border: 2px solid #f1f5f9;
}

:deep(.el-main)::-webkit-scrollbar-thumb:hover,
:deep(.el-main *)::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Page Transition - chỉ animate enter để tránh blank khi bấm nhanh */
.page-enter-enter-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.page-enter-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-enter-enter-to {
  opacity: 1;
  transform: translateY(0);
}

/* Leave instant - không đợi animation, không bao giờ bị stuck */
.page-enter-leave-active {
  transition: none;
}

.page-enter-leave-to {
  opacity: 0;
}

/* Global Modal & Popup Overrides */
:global(.el-dialog),
:global(.el-message-box),
:global(.el-notification) {
  --el-dialog-font-size: 14px !important;
  --el-dialog-title-font-size: 18px !important;
  --el-message-box-font-size: 14px !important;
  --el-notification-title-font-size: 14px !important;
  --el-notification-content-font-size: 13px !important;
}

:global(.el-dialog__title),
:global(.el-message-box__title) {
  font-size: 18px !important;
  font-weight: 700 !important;
}

:global(.el-dialog__body),
:global(.el-message-box__content),
:global(.el-message-box__message) {
  font-size: 14px !important;
  color: #1e293b !important;
}

:global(.el-form-item__label) {
  font-size: 14px !important;
  font-weight: 600 !important;
  color: #475569 !important;
}

:global(.el-input__inner),
:global(.el-select__placeholder) {
  font-size: 14px !important;
}

:global(.el-dialog .el-input__wrapper),
:global(.el-dialog .el-select__wrapper) {
  height: 40px !important;
  border-radius: 8px !important;
}
</style>
