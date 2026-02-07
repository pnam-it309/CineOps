<script setup>
import { ref } from 'vue';
import { Search, Printer, Close, View, RefreshRight } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const invoices = ref([
  { id: 'INV-1023', time: '17:45', customer: 'Cash (Guest)', amount: 195000, items: '2x Spider-Man', status: 'Paid' },
  { id: 'INV-1022', time: '17:30', customer: 'Alice Johnson', amount: 325000, items: '2x Avatar, 1x Combo S', status: 'Paid' },
  { id: 'INV-1021', time: '17:15', customer: 'Cash (Guest)', amount: 95000, items: '1x Spider-Man', status: 'Cancelled' },
  { id: 'INV-1020', time: '16:50', customer: 'Bob Smith', amount: 450000, items: '4x Batman', status: 'Paid' }
]);

const handleVoid = (id) => {
  ElMessageBox.confirm('Are you sure you want to void this invoice? This action is logged.', 'Warning', {
    confirmButtonText: 'Void Invoice',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => {
    ElMessage.error(`Invoice ${id} has been voided.`);
  });
};

const handleReprint = (id) => {
  ElMessage.success(`Reprinting ticket for ${id}...`);
};
</script>

<template>
  <div class="staff-invoices container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="fs-4 fw-bold mb-1">Daily Sales Log</h2>
        <p class="text-secondary small mb-0">Today's transactions for terminal: <strong>POS-01</strong></p>
      </div>
      <el-button :icon="RefreshRight" circle />
    </div>

    <!-- Filters -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-3 mb-4">
      <div class="row g-2 align-items-center">
        <div class="col-md-9">
          <el-input placeholder="Search by Invoice ID or Customer..." :prefix-icon="Search" size="large" />
        </div>
        <div class="col-md-3">
          <el-button type="primary" class="w-100" size="large">SEARCH</el-button>
        </div>
      </div>
    </el-card>

    <!-- Table -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-3 overflow-hidden p-0">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase" style="font-size: 0.7rem;">
            <tr>
              <th class="ps-3 border-0">Invoice ID</th>
              <th class="border-0">Time</th>
              <th class="border-0">Customer</th>
              <th class="border-0">Amount</th>
              <th class="border-0">Status</th>
              <th class="text-end pe-3 border-0">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="inv in invoices" :key="inv.id">
              <td class="ps-3 fw-bold">{{ inv.id }}</td>
              <td class="small">{{ inv.time }}</td>
              <td>{{ inv.customer }}</td>
              <td class="fw-bold text-primary">{{ inv.amount.toLocaleString() }}đ</td>
              <td>
                <span 
                  :class="inv.status === 'Paid' ? 'text-success' : 'text-danger'"
                  class="small fw-bold border p-1 px-2 rounded-pill"
                  style="font-size: 0.7rem;"
                >
                  {{ inv.status }}
                </span>
              </td>
              <td class="text-end pe-3">
                <el-button-group>
                  <el-button :icon="Printer" size="small" @click="handleReprint(inv.id)" />
                  <el-button :icon="Close" type="danger" size="small" plain @click="handleVoid(inv.id)" />
                </el-button-group>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.table thead th { letter-spacing: 0.5px; }
</style>
