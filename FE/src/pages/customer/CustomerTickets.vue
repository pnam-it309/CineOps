<template>
  <h2 class="mb-4">My Tickets</h2>
  <div class="card mb-3 shadow-sm border-0 bg-white">
    <div class="row g-0">
      <div class="col-md-2 bg-dark text-white d-flex align-items-center justify-content-center flex-column p-3">
         <span class="display-6 fw-bold">12</span>
         <span>OCT</span>
      </div>
      <div class="col-md-10">
        <div class="card-body">
          <h5 class="card-title">Avengers: Endgame</h5>
          <p class="card-text mb-1"><i class="bi bi-geo-alt"></i> Theater 1, Seat F12, F13</p>
          <p class="card-text mb-0"><i class="bi bi-clock"></i> 19:30 - 22:30</p>
        </div>
      </div>
    </div>
  </div>
   <div class="alert alert-info" v-if="false">
    You haven't booked any tickets yet.
  </div>
</template>
<script setup></script>
