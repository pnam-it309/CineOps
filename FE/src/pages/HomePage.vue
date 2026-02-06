<template>
  <div class="container-responsive py-4">
    <!-- Hero Section / Banner -->
    <div class="p-5 mb-4 bg-dark text-white rounded-3 shadow" style="background-image: linear-gradient(rgba(0,0,0,0.6), rgba(0,0,0,0.6)), url('https://placehold.co/1200x400?text=Cinema+Banner'); background-size: cover; background-position: center;">
      <div class="container-fluid py-5">
        <h1 class="display-5 fw-bold">Blockbuster Season is Here!</h1>
        <p class="col-md-8 fs-4">Experience the magic of cinema. Book your tickets now for the latest releases.</p>
        <button class="btn btn-primary btn-lg" type="button">Book Now</button>
      </div>
    </div>

    <!-- Now Showing -->
    <div class="row align-items-center mb-4">
      <div class="col">
        <h2 class="fw-bold border-start border-4 border-primary ps-3">Now Showing</h2>
      </div>
      <div class="col-auto">
        <a href="#" class="text-decoration-none">View All <i class="bi bi-arrow-right"></i></a>
      </div>
    </div>

    <div class="row row-cols-1 row-cols-md-3 row-cols-lg-4 g-4">
      <!-- Mock Movie Card 1 -->
      <div class="col" v-for="n in 4" :key="n">
        <div class="card h-100 shadow-sm border-0">
          <div class="card-img-top bg-secondary text-white d-flex align-items-center justify-content-center" style="height: 350px;">
             <span>Movie Poster {{ n }}</span>
          </div>
          <div class="card-body">
            <h5 class="card-title fw-bold text-truncate">Movie Title {{ n }}</h5>
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="badge bg-warning text-dark">PG-13</span>
                <small class="text-muted"><i class="bi bi-clock"></i> 2h 15m</small>
            </div>
            <p class="card-text text-muted small text-truncate">Action, Adventure, Sci-Fi</p>
          </div>
          <div class="card-footer bg-white border-top-0 pb-3">
             <button class="btn btn-outline-primary w-100">Book Ticket</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Promotions Section -->
     <div class="row align-items-center my-5">
      <div class="col">
        <h2 class="fw-bold border-start border-4 border-danger ps-3">Promotions</h2>
      </div>
    </div>
    <div class="row g-4">
        <div class="col-md-6">
            <div class="p-4 bg-danger text-white rounded-3">
                <h4>Student Discount</h4>
                <p>Get 20% off on weekdays with valid student ID.</p>
            </div>
        </div>
        <div class="col-md-6">
            <div class="p-4 bg-primary text-white rounded-3">
                <h4>Member Exclusive</h4>
                <p>Free popcorn upgrade for CineOps Gold members.</p>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
// No script needed for placeholder
</script>
