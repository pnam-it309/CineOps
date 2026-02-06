<template>
  <div class="d-flex min-vh-100 bg-light">
    <!-- Sidebar -->
    <aside class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark" style="width: 280px; height: 100vh; position: sticky; top: 0;">
      <a href="/" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none">
        <span class="fs-4 fw-bold">CineOps Admin</span>
      </a>
      <hr>
      <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
          <router-link :to="{ name: 'AdminDashboard' }" class="nav-link text-white" exact-active-class="active">
            Dashboard
          </router-link>
        </li>
        <li>
          <router-link :to="{ name: 'AdminUsers' }" class="nav-link text-white" exact-active-class="active">
            Users
          </router-link>
        </li>
        <li>
          <router-link :to="{ name: 'AdminRoles' }" class="nav-link text-white" exact-active-class="active">
            Roles
          </router-link>
        </li>
        <li>
          <router-link :to="{ name: 'AdminReports' }" class="nav-link text-white" exact-active-class="active">
            Reports
          </router-link>
        </li>
      </ul>
      <hr>
      <!-- Admin Switcher -->
      <div v-if="authStore.isAdmin" class="mb-3">
         <router-link :to="{ name: 'StaffBookings' }" class="btn btn-outline-light w-100 btn-sm">
           <i class="bi bi-arrow-right-circle me-2"></i>Switch to Staff View
         </router-link>
      </div>

      <div class="dropdown">
        <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" id="dropdownUser1" data-bs-toggle="dropdown" aria-expanded="false">
          <img src="https://github.com/mdo.png" alt="" width="32" height="32" class="rounded-circle me-2">
          <strong>{{ authStore.user?.username || 'Admin' }}</strong>
        </a>
        <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser1">
          <li><a class="dropdown-item" href="#">Profile</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item" href="#" @click.prevent="authStore.logout(); $router.push('/login')">Sign out</a></li>
        </ul>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-grow-1 p-4 overflow-auto">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
</script>
