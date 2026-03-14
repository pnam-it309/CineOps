<template>
  <el-header class="nav-top-bar d-flex align-items-center justify-content-between bg-white px-3 border-bottom border-light"
    style="height: 60px;">
    <div class="d-flex align-items-center gap-3">
      <el-icon class="fs-4 cursor-pointer text-dark" @click="$emit('toggle-sidebar')">
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
      <slot name="extra-actions"></slot>
      
      <!-- User Dropdown (visible on small screens) -->
      <el-dropdown trigger="click" @command="handleCommand" class="d-block d-md-none">
        <el-avatar :size="32" :src="userAvatar" />
        <template #dropdown>
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
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import {
  Fold,
  Expand,
  SwitchButton,
  User
} from '@element-plus/icons-vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  },
  userAvatar: {
    type: String,
    default: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  }
});

const emit = defineEmits(['toggle-sidebar']);

const authStore = useAuthStore();
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
    // router.push({ name: 'profile' }); // Example
  }
};
</script>

<style scoped>
.nav-top-bar {
  z-index: 5;
}
.cursor-pointer {
  cursor: pointer;
}
:deep(.el-breadcrumb__inner) {
  color: #64748b !important;
}
:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #1e293b !important;
  font-weight: 600;
}
</style>
