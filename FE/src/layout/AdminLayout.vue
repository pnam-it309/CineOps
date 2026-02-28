<template>
  <MainLayout>
    <el-container class="vh-100 overflow-hidden admin-staff-layout">
      <!-- Sidebar -->
      <el-aside :width="isCollapse ? '64px' : '240px'" class="bg-white shadow transition-width overflow-hidden">
        <AdminSidebar :is-collapse="isCollapse" />
      </el-aside>

      <!-- Main Container -->
      <el-container class="d-flex flex-column vh-100 overflow-hidden bg-white">
        <!-- Top Navigation Bar -->
        <el-header class="d-flex align-items-center justify-content-between bg-white shadow-sm px-3 border-bottom border-light" style="height: 60px;">
          <div class="d-flex align-items-center gap-3">
            <el-icon class="fs-4 cursor-pointer text-dark" @click="toggleSidebar">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="{ path: item.path }">
                {{ item.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="d-flex align-items-center gap-2">
            <!-- View Switchers -->
            <el-button v-if="authStore.isAdmin" type="primary" size="small" @click="$router.push({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name })">
              <el-icon><Monitor /></el-icon>
              <span class="ms-1 d-none d-md-inline">Giao diện Bán vé</span>
            </el-button>

            <el-button type="info" size="small" plain @click="$router.push({ name: ROUTES_CONSTANTS.HOME.name })">
              <el-icon><House /></el-icon>
              <span class="ms-1 d-none d-md-inline">Trang Chủ Khách</span>
            </el-button>
            
            <!-- User Dropdown (visible on small screens) -->
            <el-dropdown trigger="click" @command="handleCommand" class="d-block d-md-none">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <template #dropdown>
                <ul class="dropdown-menu-list p-0 m-0 list-unstyled">
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>
                      <span>Hồ Sơ</span>
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon><SwitchButton /></el-icon>
                      <span>Đăng Xuất</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </ul>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- Main Content Area -->
        <el-main class="p-0 flex-fill overflow-y-auto text-dark">
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </MainLayout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
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
const route = useRoute();
const router = useRouter();

const isCollapse = ref(false);

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

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

// Auto-collapse sidebar on mobile
if (window.innerWidth < 768) {
  isCollapse.value = true;
}
</script>

<style scoped>
.admin-staff-layout {
  /* Force light theme variables within admin/staff scope */
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

.transition-width {
  transition: width 0.28s;
}
.cursor-pointer {
  cursor: pointer;
}

/* Breadcrumb overrides for light mode */
:deep(.el-breadcrumb__inner) {
  color: #64748b !important;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #1e293b !important;
  font-weight: 600;
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

/* Global Pagination Overrides */
:deep(.el-pagination) {
  --el-pagination-font-size: 15px !important;
  --el-pagination-button-width: 36px !important;
  --el-pagination-button-height: 36px !important;
  font-weight: 600;
  padding: 0 !important; /* Clean padding for nested parts */
}

:deep(.el-pager li) {
  font-size: 15px !important;
  min-width: 36px !important;
  height: 36px !important;
  line-height: 36px !important;
  margin: 0 4px !important;
}

:deep(.el-pagination button) {
  width: 36px !important;
  height: 36px !important;
}

/* Remove large margins as they are now in separate containers */
:deep(.el-pagination__sizes) {
  margin: 0 !important;
}

:deep(.el-pagination__jump) {
  margin: 0 !important;
  font-size: 14px !important;
  color: #000 !important;
}

:deep(.el-pagination .el-select .el-input) {
  width: 110px !important;
}

:deep(.el-pagination__jump .el-input) {
  width: 50px !important;
  margin: 0 8px !important;
}

/* Standardize inputs to prevent jiggling */
:deep(.el-pagination .el-input__wrapper) {
  border: 1px solid #000 !important;
  box-shadow: none !important;
  border-radius: 6px !important;
}

/* Standard Scrollbar for Admin Light Theme */
:deep(.el-main)::-webkit-scrollbar {
  width: 8px;
}
:deep(.el-main)::-webkit-scrollbar-track {
  background: #ffffff;
}
:deep(.el-main)::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
:deep(.el-main)::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Page Transition */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateX(20px) scale(0.98);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px) scale(1.02);
}
</style>


