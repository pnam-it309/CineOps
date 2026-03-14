<template>
  <MainLayout>
    <el-container class="min-vh-100 d-flex flex-column bg-dark-primary" style="overflow-x: hidden;">
      <!-- Top Navigation Bar -->
      <el-header class="shadow-sm p-0 nav-sleek" style="height: 64px;">
        <div class="container-xl h-100 d-flex align-items-center justify-content-between px-3">
          <div class="d-flex align-items-center">
            <router-link :to="{ name: 'Home' }" class="d-flex align-items-center gap-2 text-white text-decoration-none fs-4 fw-bold">
              <img src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg" alt="CineOps Logo" class="logo-img" />
              <span class="d-none d-md-block">CineOps</span>
            </router-link>
          </div>

          <!-- Desktop Menu -->
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            class="d-none d-lg-flex flex-grow-1 justify-content-center bg-transparent border-0"
            :ellipsis="false"
            text-color="rgba(255, 255, 255, 0.85)"
            active-text-color="#FFBA00"
            style="--el-menu-bg-color: transparent; --el-menu-hover-bg-color: rgba(255, 255, 255, 0.05);"
            router
          >
            <el-menu-item index="/">
              <el-icon><Film /></el-icon>
              <span>Phim</span>
            </el-menu-item>
            <el-menu-item index="/showtimes">
              <el-icon><Calendar /></el-icon>
              <span>Lịch Chiếu</span>
            </el-menu-item>
            <el-menu-item index="/promotions">
              <el-icon><Present /></el-icon>
              <span>Khuyến Mãi</span>
            </el-menu-item>
            <el-menu-item index="/cinemas">
              <el-icon><MapLocation /></el-icon>
              <span>Giá Vé</span>
            </el-menu-item>
          </el-menu>

          <!-- User Actions -->
        <div class="d-flex align-items-center gap-3">
          <!-- Search UI -->
          <div class="d-none d-sm-flex align-items-center bg-white bg-opacity-10 rounded-pill px-3 py-1 me-2 border border-white border-opacity-25 transition-all search-wrapper">
            <el-icon class="text-white-50"><Search /></el-icon>
            <input type="text" placeholder="Tìm kiếm phim..." class="bg-transparent border-0 text-white small ms-2 footer-search-input" style="outline: none; width: 120px;">
          </div>

          <template v-if="authStore.isAuthenticated">
              <el-dropdown trigger="click" @command="handleCommand" class="d-none d-lg-block">
                <div class="d-flex align-items-center gap-2 cursor-pointer text-white py-1 px-3 rounded-pill user-dropdown-hover">
                  <el-avatar :size="32" :src="userAvatar" />
                  <span class="fw-medium small">{{ authStore.user?.username || 'User' }}</span>
                  <el-icon><ArrowDown /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="authStore.isAdmin" command="admin">
                      <el-icon><Setting /></el-icon>
                      <span class="text-primary fw-bold">Quản trị Hệ thống</span>
                    </el-dropdown-item>
                    <el-dropdown-item v-if="authStore.isAdmin || authStore.isStaff" command="staff">
                      <el-icon><Monitor /></el-icon>
                      <span class="text-success fw-bold">Giao diện Bán vé</span>
                    </el-dropdown-item>
                    <el-dropdown-item v-if="authStore.isAdmin || authStore.isStaff" divided />
                    
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>
                      <span>Hồ sơ cá nhân</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="tickets">
                      <el-icon><Ticket /></el-icon>
                      <span>Vé của tôi</span>
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon><SwitchButton /></el-icon>
                      <span>Đăng xuất</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <div class="d-none d-lg-flex gap-2">
                <el-button type="default" size="default" @click="$router.push({ name: 'Login' })">
                  Đăng Nhập
                </el-button>
                <el-button type="primary" size="default" @click="$router.push('/register')">
                  Đăng Ký
                </el-button>
              </div>
            </template>

            <!-- Mobile Menu Toggle -->
            <el-icon class="d-lg-none fs-4 text-white cursor-pointer" @click="drawerVisible = true">
              <Menu />
            </el-icon>
          </div>
        </div>
      </el-header>

      <!-- Main Content -->
      <el-main class="flex-fill bg-dark-primary p-0 w-100">
        <div class="container-xl py-4 min-vh-75">
          <router-view />
        </div>
      </el-main>

      <!-- Premium Footer -->
      <footer class="customer-footer bg-dark-secondary text-white pt-5 pb-3 mt-auto border-top" style="border-color: var(--border-color) !important; overflow-x: hidden;">
        <div class="container-xl">
          <div class="row g-4 mb-5">
            <!-- Brand Column -->
            <div class="col-lg-4 col-md-6">
              <div class="d-flex align-items-center gap-2 mb-3">
                <img src="@/assets/picture/z7530699725399_311ba639a6b3d2fba5fe416d4f69b3ec.jpg" alt="CineOps Logo" class="footer-logo" />
                <span class="fs-3 fw-bold tracking-tight">Cine<span class="text-brand-light">Ops</span></span>
              </div>
              <p class="text-secondary small lh-lg">
                Hệ thống rạp chiếu phim hiện đại hàng đầu Việt Nam, mang đến trải nghiệm điện ảnh chân thực với công nghệ âm thanh và hình ảnh tiên tiến nhất. Tận hưởng từng khoảnh khắc giải trí tuyệt vời cùng gia đình và bạn bè.
              </p>
              <div class="d-flex gap-3 mt-4">
                <el-button circle size="small" class="footer-social-btn"><i class="bi bi-facebook"></i></el-button>
                <el-button circle size="small" class="footer-social-btn"><i class="bi bi-youtube"></i></el-button>
                <el-button circle size="small" class="footer-social-btn"><i class="bi bi-tiktok"></i></el-button>
                <el-button circle size="small" class="footer-social-btn"><i class="bi bi-instagram"></i></el-button>
              </div>
            </div>

            <!-- Quick Links -->
            <div class="col-lg-2 col-md-3 col-6 ps-lg-5">
              <h6 class="fw-bold mb-4 text-uppercase small tracking-wider">Khám Phá</h6>
              <ul class="list-unstyled footer-links">
                <li><router-link to="/home">Phim Đang Chiếu</router-link></li>
                <li><router-link to="/home">Phim Sắp Chiếu</router-link></li>
                <li><router-link to="/showtimes">Lịch Chiếu</router-link></li>
                <li><router-link to="/promotions">Khuyến Mãi</router-link></li>
                <li><router-link to="/news">Tin tức & Sự kiện</router-link></li>
              </ul>
            </div>

            <!-- Support -->
            <div class="col-lg-2 col-md-3 col-6">
              <h6 class="fw-bold mb-4 text-uppercase small tracking-wider">Hỗ Trợ</h6>
              <ul class="list-unstyled footer-links">
                <li><a href="#">Điều khoản sử dụng</a></li>
                <li><a href="#">Chính sách bảo mật</a></li>
                <li><a href="#">Chính sách hoàn vé</a></li>
                <li><a href="#">Liên hệ quảng cáo</a></li>
              </ul>
            </div>

            <!-- Contact -->
            <div class="col-lg-4 col-md-12">
              <h6 class="fw-bold mb-4 text-uppercase small tracking-wider">Chăm Sóc Khách Hàng</h6>
              <div class="d-flex flex-column gap-3 text-secondary small">
                <div class="d-flex align-items-center gap-3">
                  <el-icon :size="20" class="text-primary-light"><Phone /></el-icon>
                  <div>
                    <div class="text-white fw-bold">1900 1234</div>
                    <div class="x-small">Mở cửa: 8:00 - 24:00 hàng ngày</div>
                  </div>
                </div>
                <div class="d-flex align-items-center gap-3">
                  <el-icon :size="20" class="text-primary-light"><Message /></el-icon>
                  <div>support@cineops.vn</div>
                </div>
                <div class="d-flex align-items-center gap-3">
                  <el-icon :size="20" class="text-primary-light"><Location /></el-icon>
                  <div>Tầng 5, TTTM Cine Plaza, Quận 1, TP. HCM</div>
                </div>
              </div>
            </div>
          </div>

          <el-divider class="border-secondary opacity-25" />
          
          <div class="d-flex flex-column flex-md-row justify-content-between align-items-center gap-3 mt-4">
            <p class="m-0 small text-secondary">
              &copy; 2026 <strong>CineOps</strong>. All rights reserved.
            </p>
            <div class="d-flex gap-4 small text-secondary">
              <a href="#" class="text-decoration-none text-secondary-hover">Sitemap</a>
              <a href="#" class="text-decoration-none text-secondary-hover">Cookies</a>
              <a href="#" class="text-decoration-none text-secondary-hover">Quản lý phản hồi</a>
            </div>
          </div>
        </div>
      </footer>

      <!-- Mobile Drawer -->
      <el-drawer
        v-model="drawerVisible"
        title="Menu"
        direction="ltr"
        size="280px"
      >
        <el-menu :default-active="activeMenu" router @select="drawerVisible = false" class="border-0">
          <el-menu-item index="/">
            <el-icon><Film /></el-icon>
            <span>Phim</span>
          </el-menu-item>
          <el-menu-item index="/showtimes">
            <el-icon><Calendar /></el-icon>
            <span>Lịch Chiếu</span>
          </el-menu-item>
          <el-menu-item index="/promotions">
            <el-icon><Present /></el-icon>
            <span>Khuyến Mãi</span>
          </el-menu-item>
          <el-menu-item index="/cinemas">
            <el-icon><MapLocation /></el-icon>
            <span>Giá Vé</span>
          </el-menu-item>
          
          <template v-if="authStore.isAuthenticated">
            <el-divider />
            <el-menu-item v-if="authStore.isAdmin" index="/admin/dashboard">
              <el-icon><Setting /></el-icon>
              <span class="text-primary fw-bold">Quản trị Hệ thống</span>
            </el-menu-item>
            <el-menu-item v-if="authStore.isAdmin || authStore.isStaff" index="/staff/dashboard">
              <el-icon><Monitor /></el-icon>
              <span class="text-success fw-bold">Giao diện Bán vé</span>
            </el-menu-item>
            <el-divider v-if="authStore.isAdmin || authStore.isStaff" />
            
            <el-menu-item index="/customer/profile">
              <el-icon><User /></el-icon>
              <span>Hồ sơ cá nhân</span>
            </el-menu-item>
            <el-menu-item index="/customer/tickets">
              <el-icon><Ticket /></el-icon>
              <span>Vé của tôi</span>
            </el-menu-item>
            <el-menu-item @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>Đăng xuất</span>
            </el-menu-item>
          </template>
          <template v-else>
            <el-divider />
            <el-menu-item index="/login">
              <el-icon><User /></el-icon>
              <span>Đăng Nhập</span>
            </el-menu-item>
            <el-menu-item index="/register">
              <el-icon><VideoPlay /></el-icon>
              <span>Đăng Ký</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-drawer>
    </el-container>
  </MainLayout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import MainLayout from './MainLayout.vue';
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import {
  VideoPlay,
  Film,
  Calendar,
  Present,
  User,
  Ticket,
  SwitchButton,
  ArrowDown,
  Menu,
  Phone,
  Message,
  Location,
  Search,
  Monitor,
  Setting,
  MapLocation
} from '@element-plus/icons-vue';

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const drawerVisible = ref(false);

const activeMenu = computed(() => route.path);

const userAvatar = computed(() => 
  'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
);

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push({ name: 'CustomerProfile' });
  } else if (command === 'tickets') {
    router.push({ name: 'CustomerTickets' });
  } else if (command === 'admin') {
    router.push({ name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name });
  } else if (command === 'staff') {
    router.push({ name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name });
  } else if (command === 'logout') {
    handleLogout();
  }
};

const handleLogout = async () => {
  await authStore.logout();
  router.push({ name: 'Login' });
  drawerVisible.value = false;
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.user-dropdown-hover:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* Footer Styles */
.customer-footer {
  font-family: 'Inter', sans-serif;
  background-color: var(--dark-bg-secondary) !important;
}

.logo-img {
  height: 40px;
  width: auto;
  object-fit: contain;
  border-radius: 4px;
}

.footer-logo {
  height: 42px;
  width: auto;
  object-fit: contain;
  border-radius: 4px;
}

.text-primary-light {
  color: #F37021; /* Brand Orange */
}

.text-brand-light {
  color: #FFBA00; /* Brand Yellow */
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-links a {
  color: #94a3b8;
  text-decoration: none;
  font-size: 0.85rem;
  transition: all 0.2s ease;
}

.footer-links a:hover {
  color: #fff;
  padding-left: 5px;
}

.footer-social-btn {
  background: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #94a3b8 !important;
  transition: all 0.3s ease !important;
}

.footer-social-btn:hover {
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%) !important;
  border-color: #E31E24 !important;
  color: #fff !important;
  transform: translateY(-3px);
}

.text-secondary-hover {
  transition: color 0.2s ease;
}

.text-secondary-hover:hover {
  color: #fff !important;
}

/* Utilities */
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.min-vh-75 { min-height: 75vh; }
.x-small { font-size: 0.75rem; }

:deep(.el-divider--horizontal) {
  margin: 24px 0;
}
.search-wrapper {
  transition: all 0.3s ease;
}
.search-wrapper:focus-within {
  background: rgba(255, 255, 255, 0.2) !important;
  width: 200px !important;
}
.footer-search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}
.footer-search-input {
  transition: width 0.3s ease;
}
.search-wrapper:focus-within .footer-search-input {
  width: 160px !important;
}
</style>
