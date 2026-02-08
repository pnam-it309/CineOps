<template>
  <div class="d-flex flex-column h-100 bg-dark text-white">
    <div
      class="d-flex align-items-center justify-content-center p-3 text-white fw-bold fs-4 border-bottom border-light border-opacity-10"
      style="height: 60px;">
      <transition name="fade">
        <span v-if="!isCollapse" class="text-nowrap">Quản Trị CineOps</span>
        <span v-else class="fs-4">QC</span>
      </transition>
    </div>

    <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" background-color="#212529"
      text-color="#adb5bd" active-text-color="#0d6efd" class="flex-grow-1 border-0 overflow-auto"
      style="--el-menu-bg-color: #212529; --el-menu-text-color: #adb5bd; --el-menu-active-color: #0d6efd;" router
      menu-trigger="hover">
      <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.DASHBOARD)">
        <el-icon>
          <DataAnalysis />
        </el-icon>
        <template #title>Bảng Điều Khiển</template>
      </el-menu-item>

      <el-sub-menu index="core">
        <template #title>
          <el-icon>
            <VideoPlay />
          </el-icon>
          <span>Quản Lý Hệ Thống</span>
        </template>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.MOVIES)">
          <el-icon>
            <Film />
          </el-icon>
          <template #title>Quản Lý Phim</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.ROOMS)">
          <el-icon>
            <House />
          </el-icon>
          <template #title>Cụm Rạp & Phòng</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES)">
          <el-icon>
            <Calendar />
          </el-icon>
          <template #title>Lịch Chiếu</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.PRICING)">
          <el-icon>
            <PriceTag />
          </el-icon>
          <template #title>Cấu Hình Giá</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.FOOD)">
          <el-icon>
            <Coffee />
          </el-icon>
          <template #title>Đồ Ăn & Combo</template>
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="marketing">
        <template #title>
          <el-icon>
            <Sell />
          </el-icon>
          <span>Tiếp Thị</span>
        </template>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.VOUCHERS)">
          <el-icon>
            <Ticket />
          </el-icon>
          <template #title>Mã Giảm Giá</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.BANNERS)">
          <el-icon>
            <Picture />
          </el-icon>
          <template #title>Banner Quảng Cáo</template>
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="admin">
        <template #title>
          <el-icon>
            <Setting />
          </el-icon>
          <span>Quản Trị</span>
        </template>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.STAFF)">
          <el-icon>
            <User />
          </el-icon>
          <template #title>Nhân Viên & Quyền</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS)">
          <el-icon>
            <Avatar />
          </el-icon>
          <template #title>Khách Hàng</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.REPORTS)">
          <el-icon>
            <Document />
          </el-icon>
          <template #title>Giao Dịch</template>
        </el-menu-item>
        <el-menu-item :index="getRoutePath(ROUTES_CONSTANTS.ADMIN.children.REVIEWS)">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <template #title>Đánh Giá</template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

    <!-- User Profile at Bottom -->
    <div class="p-3 border-top border-light border-opacity-10" v-if="!isCollapse">
      <el-dropdown trigger="click" @command="handleCommand" class="w-100">
        <div class="d-flex align-items-center gap-2 cursor-pointer text-white text-decoration-none">
          <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          <span class="text-truncate small">{{ authStore.user?.username || 'Admin' }}</span>
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
  VideoPlay,
  Film,
  House,
  Calendar,
  PriceTag,
  Coffee,
  Sell,
  Ticket,
  Picture,
  Setting,
  User,
  Avatar,
  Document,
  ChatDotRound
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
/* Hiệu ứng hover màu trắng cho menu item và submenu title */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

/* Biểu tượng cũng chuyển trắng khi hover */
:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
  color: #ffffff !important;
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
</style>
