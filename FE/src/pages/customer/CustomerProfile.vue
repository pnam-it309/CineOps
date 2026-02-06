<template>
  <div class="row justify-content-center">
    <div class="col-md-8">
      <div class="card shadow-sm">
        <div class="card-body text-center p-5">
          <div class="mb-4">
             <div class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 100px; height: 100px; font-size: 2rem;">
               {{ user?.username?.charAt(0).toUpperCase() || 'U' }}
             </div>
          </div>
          <h3>{{ user?.username }}</h3>
          <p class="text-muted">{{ user?.email || 'email@example.com' }}</p>
          <hr>
          <div class="text-start">
             <h5>Account Settings</h5>
             <form class="mt-3">
               <div class="mb-3">
                 <label class="form-label">Full Name</label>
                 <input type="text" class="form-control" value="Nguyen Van A">
               </div>
               <div class="mb-3">
                 <label class="form-label">Phone</label>
                 <input type="text" class="form-control" value="0912345678">
               </div>
               <button class="btn btn-primary">Save Changes</button>
             </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { computed } from 'vue';

const authStore = useAuthStore();
const user = computed(() => authStore.currentUser);
</script>
