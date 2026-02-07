<script setup>
import { ref } from 'vue';
import { Download, Search, Filter, Document, Money, Ticket, Calendar } from '@element-plus/icons-vue';

const dateRange = ref([]);
const selectedType = ref('All');
const reports = ref([
  { id: 'TX1001', date: '2026-02-07 14:30', customer: 'John Doe', items: '2x Avatar 3, 1x Combo Solo', total: 325000, status: 'Completed', method: 'VNPay' },
  { id: 'TX1002', date: '2026-02-07 15:15', customer: 'Jane Smith', items: '1x Spider-Man', total: 95000, status: 'Completed', method: 'Momo' },
  { id: 'TX1003', date: '2026-02-07 16:00', customer: 'Mike Ross', items: '4x Batman, 2x Combo Couple', total: 780000, status: 'Refunded', method: 'Credit Card' }
]);

const stats = ref([
  { label: 'Total Revenue', value: '124.5M', icon: Money, color: 'text-success' },
  { label: 'Tickets Sold', value: '1,240', icon: Ticket, color: 'text-primary' },
  { label: 'Avg. Order', value: '185k', icon: Document, color: 'text-warning' }
]);
</script>

<template>
  <div class="admin-reports container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Transaction Reports</h2>
        <p class="text-secondary small mb-0">Monitor sales performance and export detailed transaction logs.</p>
      </div>
      <el-button type="success" size="large" :icon="Download" round>Export CSV</el-button>
    </div>

    <!-- Stats Summary -->
    <div class="row g-4 mb-4">
      <div v-for="s in stats" :key="s.label" class="col-md-4">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 text-center py-2 h-100">
          <div class="p-3">
            <el-icon :size="28" :class="s.color" class="mb-2"><component :is="s.icon" /></el-icon>
            <div class="text-secondary small fw-bold text-uppercase">{{ s.label }}</div>
            <div class="fs-2 fw-bold text-dark">{{ s.value }}</div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Filters -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4 mb-4">
      <div class="row g-3 align-items-center">
        <div class="col-md-5">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="To"
            start-placeholder="Start date"
            end-placeholder="End date"
            size="large"
            class="w-100"
          />
        </div>
        <div class="col-md-3">
          <el-select v-model="selectedType" placeholder="Payment Method" size="large" class="w-100">
            <el-option label="All Methods" value="All" />
            <el-option label="VNPay" value="VNPay" />
            <el-option label="Momo" value="Momo" />
            <el-option label="Credit Card" value="Card" />
          </el-select>
        </div>
        <div class="col-md-4">
          <el-input placeholder="Transaction ID or Customer..." :prefix-icon="Search" size="large" />
        </div>
      </div>
    </el-card>

    <!-- Results Table -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden p-0">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="ps-3 border-0">ID</th>
              <th class="border-0">Date & Time</th>
              <th class="border-0">Customer</th>
              <th class="border-0">Items</th>
              <th class="text-end border-0">Total</th>
              <th class="text-center border-0">Status</th>
              <th class="text-end pe-3 border-0">Method</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in reports" :key="r.id">
              <td class="ps-3 fw-bold">{{ r.id }}</td>
              <td class="small text-secondary">{{ r.date }}</td>
              <td>{{ r.customer }}</td>
              <td class="small text-truncate" style="max-width: 250px;">{{ r.items }}</td>
              <td class="text-end fw-bold">{{ r.total.toLocaleString() }}đ</td>
              <td class="text-center">
                <el-tag :type="r.status === 'Completed' ? 'success' : 'danger'" size="small" round>{{ r.status }}</el-tag>
              </td>
              <td class="text-end pe-3 small fw-bold">{{ r.method }}</td>
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
