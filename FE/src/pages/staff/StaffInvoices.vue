<script setup>
import { ref } from 'vue';
import { Search, Printer, Close, RefreshRight } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import BaseTable from '@/components/common/BaseTable.vue';

const invoices = ref([
  { id: 'INV-1023', time: '17:45', customer: 'Tiền mặt (Khách vãng lai)', amount: 195000, items: '2x Spider-Man', status: 'Đã thanh toán' },
  { id: 'INV-1022', time: '17:30', customer: 'Alice Johnson', amount: 325000, items: '2x Avatar, 1x Combo S', status: 'Đã thanh toán' },
  { id: 'INV-1021', time: '17:15', customer: 'Tiền mặt (Khách vãng lai)', amount: 95000, items: '1x Spider-Man', status: 'Đã hủy' },
  { id: 'INV-1020', time: '16:50', customer: 'Bob Smith', amount: 450000, items: '4x Batman', status: 'Đã thanh toán' }
]);

const tableColumns = [
  { label: 'Mã Hóa đơn', key: 'id' },
  { label: 'Thời gian', key: 'time' },
  { label: 'Khách hàng', key: 'customer' },
  { label: 'Tổng tiền', key: 'amount' },
  { label: 'Trạng thái', key: 'status' }
];

const currentPage = ref(1);
const pageSize = ref(10);

const handleVoid = (row) => {
  ElMessageBox.confirm('Bạn có chắc chắn muốn hủy hóa đơn này không? Hành động này sẽ được ghi lại.', 'Cảnh báo', {
    confirmButtonText: 'Hủy Hóa đơn',
    cancelButtonText: 'Bỏ qua',
    type: 'warning'
  }).then(() => {
    ElMessage.error(`Hóa đơn ${row.id} đã được hủy.`);
  });
};

const handleReprint = (row) => {
  ElMessage.success(`Đang in lại vé cho ${row.id}...`);
};
</script>

<template>
  <div class="staff-invoices w-100">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-4 fw-bold mb-1">Nhật ký Bán hàng</h2>
      </div>
      <el-button :icon="RefreshRight" circle />
    </div>

    <!-- Filters -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4 mb-4">
      <div class="row g-2 align-items-center">
        <div class="col-md-9">
          <el-input placeholder="Tìm theo Mã Hóa đơn hoặc Khách hàng..." :prefix-icon="Search" size="large" />
        </div>
        <div class="col-md-3">
          <el-button type="primary" class="w-100" size="large">TÌM KIẾM</el-button>
        </div>
      </div>
    </el-card>

    <!-- Table using BaseTable -->
    <BaseTable
      :data="invoices"
      :columns="tableColumns"
      :total="invoices.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      :show-checkbox="false"
    >
      <template #cell-id="{ row }">
        <span class="fw-bold text-dark">{{ row.id }}</span>
      </template>

      <template #cell-time="{ row }">
        <span class="small text-secondary">{{ row.time }}</span>
      </template>

      <template #cell-amount="{ row }">
        <span class="fw-bold text-primary">{{ row.amount.toLocaleString() }}đ</span>
      </template>

      <template #cell-status="{ row }">
        <span 
          :class="row.status === 'Đã thanh toán' ? 'badge bg-success-subtle text-success' : 'badge bg-danger-subtle text-danger'"
          class="rounded-pill px-3 py-2 border-0"
          style="font-size: 0.75rem;"
        >
          {{ row.status }}
        </span>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center gap-1">
          <el-button :icon="Printer" size="small" type="primary" plain @click="handleReprint(row)" />
          <el-button :icon="Close" size="small" type="danger" plain @click="handleVoid(row)" />
        </div>
      </template>
    </BaseTable>
  </div>
</template>

<style scoped>
.badge {
  font-weight: 600;
}
</style>
