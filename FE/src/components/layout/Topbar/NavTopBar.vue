<template>
  <el-header class="nav-top-bar d-flex align-items-center justify-content-between bg-white px-3 border-bottom border-light"
    style="height: 60px;">
    <div class="d-flex align-items-center gap-3">
      <div class="sidebar-toggle-wrapper" :class="{ 'is-active': isCollapse }" @click="$emit('toggle-sidebar')">
        <el-icon class="toggle-icon">
          <Fold />
        </el-icon>
      </div>

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

.sidebar-toggle-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: transparent;
  color: #e31e24; /* Giữ màu đỏ như ảnh bạn gửi */
}

.sidebar-toggle-wrapper:hover {
  transform: scale(1.1);
}

.toggle-icon {
  font-size: 26px;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Khi đang MỞ (!isCollapse / .is-active là false) */
.sidebar-toggle-wrapper:not(.is-active) .toggle-icon {
  transform: rotate(180deg); /* Mặc định quay ra ngoài (phải) */
}
.sidebar-toggle-wrapper:not(.is-active):hover .toggle-icon {
  transform: rotate(0deg); /* Di chuột vào quay vào sidebar (trái) */
}

/* Khi đang ĐÓNG (isCollapse / .is-active là true) */
.sidebar-toggle-wrapper.is-active .toggle-icon {
  transform: rotate(0deg); /* Mặc định quay vào sidebar (trái) */
}
.sidebar-toggle-wrapper.is-active:hover .toggle-icon {
  transform: rotate(180deg); /* Di chuột vào quay ra ngoài (phải) */
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
