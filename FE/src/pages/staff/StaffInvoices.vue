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
  <div class="staff-invoices w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fw-bold mb-1" style="font-size: 18px;">Nhật ký Bán hàng</h2>
      </div>
      <el-button :icon="RefreshRight" circle />
    </div>

    <!-- Filters -->
    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 w-100 flex-shrink-0">
      <div class="row g-2 align-items-center">
        <div class="col-md-9">
          <el-input placeholder="Tìm theo Mã Hóa đơn hoặc Khách hàng..." :prefix-icon="Search" size="default" clearable />
        </div>
        <div class="col-md-3">
          <el-button type="primary" class="w-100" size="default">TÌM KIẾM</el-button>
        </div>
      </div>
    </el-card>

    <!-- Table using BaseTable -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable
        :data="invoices"
        :columns="tableColumns"
        :total="invoices.length"
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        :show-checkbox="false"
      >
        <template #cell-id="{ row }">
          <code class="fw-bold text-primary">{{ row.id }}</code>
        </template>

        <template #cell-time="{ row }">
          <span class="small text-secondary">{{ row.time }}</span>
        </template>

        <template #cell-amount="{ row }">
          <span class="fw-bold text-dark">{{ row.amount.toLocaleString() }}đ</span>
        </template>

        <template #cell-status="{ row }">
          <el-tag 
            :type="row.status === 'Đã thanh toán' ? 'success' : 'danger'"
            effect="light"
            round
            size="small"
          >
            {{ row.status }}
          </el-tag>
        </template>

        <template #actions="{ row }">
          <div class="d-flex justify-content-center gap-1">
            <el-button :icon="Printer" size="small" type="primary" plain @click="handleReprint(row)" />
            <el-button :icon="Close" size="small" type="danger" plain @click="handleVoid(row)" />
          </div>
        </template>
      </BaseTable>
    </div>
  </div>
</template>

<style scoped>
.staff-invoices {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.no-scroll {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
  overflow: hidden !important;
}

.no-scroll::-webkit-scrollbar {
  display: none !important;
}

.overflow-auto.no-scroll {
  overflow-y: auto !important;
}

code {
  letter-spacing: 0.5px;
}
</style>
