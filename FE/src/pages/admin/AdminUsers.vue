<template>
  <h2 class="mb-4">User Management</h2>
  <div class="card shadow-sm">
    <div class="card-body">
      <table class="table table-hover">
        <thead class="table-light">
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="i in 5" :key="i">
            <td>#{{ i }}</td>
            <td>User {{ i }}</td>
            <td>user{{ i }}@example.com</td>
            <td><span class="badge bg-info">Customer</span></td>
            <td><span class="badge bg-success">Active</span></td>
            <td>
              <button class="btn btn-sm btn-outline-primary me-2">Edit</button>
              <button class="btn btn-sm btn-outline-danger">Ban</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
</script>
