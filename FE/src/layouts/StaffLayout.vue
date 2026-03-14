<template>
  <MainLayout>
    <el-container class="vh-100 overflow-hidden admin-staff-layout">
      <!-- Sidebar -->
      <el-aside :width="isCollapse ? '64px' : '240px'" class="bg-white shadow transition-width overflow-hidden">
        <StaffSidebar :is-collapse="isCollapse" />
      </el-aside>

      <!-- Main Container -->
      <el-container class="d-flex flex-column vh-100 overflow-hidden bg-light">
        <!-- Top Navigation Bar -->
        <NavTopBar 
          :is-collapse="isCollapse" 
          @toggle-sidebar="toggleSidebar"
        />

        <!-- Main Content Area -->
        <el-main class="p-4 flex-fill overflow-auto text-dark">
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
import StaffSidebar from '@/components/layout/Sidebar/StaffSidebar.vue';
import NavTopBar from '@/components/layout/Topbar/NavTopBar.vue';
import MainLayout from './MainLayout.vue';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import {
  Fold,
  Expand,
  SwitchButton,
  User,
  Setting,
  House
} from '@element-plus/icons-vue';


const isCollapse = ref(false);

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
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
  --el-fill-color-light: #f8fafc !important;
  
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
  --el-table-header-bg-color: #f8fafc;
  --el-table-text-color: #1e293b;
  --el-table-header-text-color: #475569;
}
</style>


