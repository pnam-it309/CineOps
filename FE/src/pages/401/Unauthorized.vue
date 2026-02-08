<template>
  <div class="d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="text-center">
      <h1 class="display-1 fw-bold text-warning">401</h1>
      <h2 class="h4 mb-4">Unauthorized Access</h2>
      <p class="lead text-muted mb-4">
        Oops! It seems you are not authorized to access this page.<br>
        Please log in to continue.
      </p>
      <router-link to="/login" class="btn btn-primary btn-lg px-4">
        Go to Login
      </router-link>
      <div class="mt-3">
        <router-link to="/" class="text-decoration-none">Back to Home</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>
