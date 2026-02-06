<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2>Movie Management</h2>
    <button class="btn btn-success"><i class="bi bi-plus-lg"></i> Add Movie</button>
  </div>
  
  <div class="row g-4">
    <div class="col-md-3" v-for="i in 4" :key="i">
      <div class="card h-100">
        <div class="card-img-top bg-secondary d-flex align-items-center justify-content-center text-white" style="height: 300px;">
          Poster
        </div>
        <div class="card-body">
          <h5 class="card-title">Movie Title {{ i }}</h5>
          <p class="card-text text-muted">Action, 2h 15m</p>
        </div>
        <div class="card-footer bg-white border-top-0">
          <button class="btn btn-sm btn-outline-primary w-100">Edit Details</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup></script>
