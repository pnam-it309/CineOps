<script setup>
import { ref } from 'vue';
import { Plus, Ticket, Delete, Edit, Search, Check, Timer } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const vouchers = ref([
  { id: 1, code: 'NEWYEAR2026', type: 'Percentage', value: 20, minSpend: 200000, expiry: '2026-02-28', usage: '124 / 500', status: 'Active' },
  { id: 2, code: 'STUDENT50', type: 'Fixed Amount', value: 50000, minSpend: 0, expiry: '2026-12-31', usage: '842 / 1000', status: 'Active' },
  { id: 3, code: 'CINEMALOVER', type: 'Percentage', value: 15, minSpend: 150000, expiry: '2026-01-31', usage: '200 / 200', status: 'Expired' }
]);

const dialogVisible = ref(false);
const voucherForm = ref({
  code: '',
  type: 'Percentage',
  value: 10,
  minSpend: 0,
  expiry: '',
  limit: 100
});

const getStatusType = (status) => {
  return status === 'Active' ? 'success' : 'info';
};
</script>

<template>
  <div class="admin-vouchers container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Voucher Management</h2>
        <p class="text-secondary small mb-0">Create and manage digital discount codes for marketing campaigns.</p>
      </div>
      <el-button type="primary" size="large" :icon="Plus" round @click="dialogVisible = true">Create Voucher</el-button>
    </div>

    <el-card shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="ps-3">Voucher Code</th>
              <th>Type</th>
              <th>Discount Value</th>
              <th>Usage (Used/Total)</th>
              <th>Expiry Date</th>
              <th>Status</th>
              <th class="text-end pe-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in vouchers" :key="v.id">
              <td class="ps-3"><code class="fw-bold fs-6 text-primary">{{ v.code }}</code></td>
              <td>{{ v.type }}</td>
              <td class="fw-bold">{{ v.type === 'Percentage' ? v.value + '%' : v.value.toLocaleString() + 'đ' }}</td>
              <td>{{ v.usage }}</td>
              <td class="text-secondary small">{{ v.expiry }}</td>
              <td>
                <el-tag :type="getStatusType(v.status)" round effect="light">{{ v.status }}</el-tag>
              </td>
              <td class="text-end pe-3">
                <el-button-group>
                  <el-button :icon="Edit" size="small" />
                  <el-button :icon="Delete" size="small" type="danger" plain />
                </el-button-group>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <!-- Create Voucher Dialog -->
    <el-dialog v-model="dialogVisible" title="Generate New Voucher" width="500px" class="rounded-4">
      <el-form :model="voucherForm" label-position="top">
        <el-form-item label="Voucher Code" required>
          <el-input v-model="voucherForm.code" placeholder="e.g. MOVIE2026" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-md-6">
            <el-form-item label="Discount Type">
              <el-select v-model="voucherForm.type" class="w-100">
                <el-option label="Percentage (%)" value="Percentage" />
                <el-option label="Fixed Amount (đ)" value="Fixed Amount" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Value">
              <el-input-number v-model="voucherForm.value" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Min Spend">
              <el-input-number v-model="voucherForm.minSpend" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Total Limit">
              <el-input-number v-model="voucherForm.limit" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Expiry Date">
              <el-date-picker v-model="voucherForm.expiry" type="date" class="w-100" placeholder="Pick a date" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Generate Code</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.table thead th {
  border-bottom: none;
}
code {
  letter-spacing: 1px;
}
</style>
