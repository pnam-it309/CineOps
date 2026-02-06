<template>
  <div class="d-flex min-vh-100 bg-light">
    <!-- Sidebar -->
    <aside class="d-flex flex-column flex-shrink-0 p-3 bg-white shadow-sm" style="width: 250px; height: 100vh; position: sticky; top: 0;">
      <div class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none">
        <span class="fs-4 fw-bold text-primary">CineOps Staff</span>
      </div>
      <hr>
      <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
          <router-link :to="{ name: 'StaffBookings' }" class="nav-link link-dark" exact-active-class="active">
            Booking & Tickets
          </router-link>
        </li>
        <li>
          <router-link :to="{ name: 'StaffMovies' }" class="nav-link link-dark" exact-active-class="active">
            Movies
          </router-link>
        </li>
        <li>
          <router-link :to="{ name: 'StaffShowtimes' }" class="nav-link link-dark" exact-active-class="active">
            Showtimes
          </router-link>
        </li>
      </ul>
      <hr>
      <div v-if="authStore.isAdmin" class="mb-3">
         <router-link :to="{ name: 'AdminDashboard' }" class="btn btn-outline-dark w-100 btn-sm">
           <i class="bi bi-arrow-left-circle me-2"></i>Back to Admin
         </router-link>
      </div>
      <div class="dropdown">
        <a href="#" class="d-flex align-items-center link-dark text-decoration-none dropdown-toggle" id="dropdownUser2" data-bs-toggle="dropdown" aria-expanded="false">
          <strong>{{ authStore.user?.username || 'Staff' }}</strong>
        </a>
        <ul class="dropdown-menu text-small shadow" aria-labelledby="dropdownUser2">
          <li><a class="dropdown-item" href="#">Profile</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item" href="#" @click.prevent="authStore.logout(); $router.push('/login')">Sign out</a></li>
        </ul>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-grow-1 p-4">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
</script>
