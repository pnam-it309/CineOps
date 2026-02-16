<template>
  <MainLayout>
    <el-container class="vh-100 overflow-hidden">
      <!-- Sidebar -->
      <el-aside :width="isCollapse ? '64px' : '240px'" class="bg-dark shadow transition-width overflow-hidden">
        <AdminSidebar :is-collapse="isCollapse" />
      </el-aside>

      <!-- Main Container -->
      <el-container class="d-flex flex-column vh-100 overflow-hidden bg-dark-primary">
        <!-- Top Navigation Bar -->
        <el-header class="d-flex align-items-center justify-content-between bg-dark-secondary shadow-sm px-3 border-bottom border-dark" style="height: 60px;">
          <div class="d-flex align-items-center gap-3">
            <el-icon class="fs-4 cursor-pointer" @click="toggleSidebar">
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
        <el-main class="p-4 flex-fill overflow-auto">
          <router-view />
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

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout();
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
.transition-width {
  transition: width 0.28s;
}
.cursor-pointer {
  cursor: pointer;
}

/* Breadcrumb overrides for dark mode */
:deep(.el-breadcrumb__inner) {
  color: #adb5bd !important;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #fff !important;
  font-weight: 600;
}
</style>
