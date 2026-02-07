<script setup>
import { ref } from 'vue';
import { Search, User, View, Ticket, Star, Filter } from '@element-plus/icons-vue';

const customers = ref([
  { id: 1, name: 'Alice Johnson', email: 'alice@example.com', phone: '0901234567', points: 1250, level: 'Gold', joinDate: '2025-10-15', tickets: 24 },
  { id: 2, name: 'Bob Smith', email: 'bob@example.com', phone: '0912345678', points: 450, level: 'Silver', joinDate: '2025-11-20', tickets: 8 },
  { id: 3, name: 'Charlie Davis', email: 'charlie@example.com', phone: '0923456789', points: 2800, level: 'Platinum', joinDate: '2025-08-01', tickets: 45 },
  { id: 4, name: 'Diana Prince', email: 'diana@example.com', phone: '0934567890', points: 150, level: 'Silver', joinDate: '2026-01-10', tickets: 2 }
]);

const getLevelColor = (level) => {
  switch(level) {
    case 'Platinum': return 'danger';
    case 'Gold': return 'warning';
    default: return 'info';
  }
};
</script>

<template>
  <div class="admin-customers container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Customer Database</h2>
        <p class="text-secondary small mb-0">View customer loyalty points, spending habits, and member tiers.</p>
      </div>
    </div>

    <el-card shadow="never" class="border-0 shadow-sm rounded-4 mb-4">
      <div class="row g-3 align-items-center">
        <div class="col-md-6 col-lg-8">
          <el-input placeholder="Search customers by name, email or phone..." :prefix-icon="Search" size="large" clearable />
        </div>
        <div class="col-md-6 col-lg-4">
          <el-select placeholder="Filter by Member Level" size="large" class="w-100">
            <el-option label="All Levels" value="all" />
            <el-option label="Platinum" value="platinum" />
            <el-option label="Gold" value="gold" />
            <el-option label="Silver" value="silver" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="ps-3">Customer Info</th>
              <th>Contact</th>
              <th>Member Tier</th>
              <th class="text-center">Loyalty Points</th>
              <th class="text-center">Total Tickets</th>
              <th>Join Date</th>
              <th class="text-end pe-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in customers" :key="c.id">
              <td class="ps-3">
                <div class="d-flex align-items-center gap-3">
                  <el-avatar :size="32" class="bg-primary">{{ c.name.charAt(0) }}</el-avatar>
                  <div class="fw-bold fs-6">{{ c.name }}</div>
                </div>
              </td>
              <td>
                <div class="small">{{ c.email }}</div>
                <div class="text-secondary small">{{ c.phone }}</div>
              </td>
              <td>
                <el-tag :type="getLevelColor(c.level)" effect="dark" size="small">{{ c.level }}</el-tag>
              </td>
              <td class="text-center fw-bold text-primary">
                {{ c.points.toLocaleString() }}
              </td>
              <td class="text-center">
                <span class="badge bg-light text-dark px-2 rounded-pill">{{ c.tickets }}</span>
              </td>
              <td class="text-secondary small">{{ c.joinDate }}</td>
              <td class="text-end pe-3">
                <el-button :icon="View" size="small" round>Details</el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.table thead th {
  border-bottom: none;
}
</style>
