<script setup>
import { ref, markRaw } from 'vue';
import { Download, Search, Filter, Document, Money, Ticket, Calendar } from '@element-plus/icons-vue';

const dateRange = ref([]);
const selectedType = ref('All');
const reports = ref([
  { id: 'TX1001', date: '2026-02-07 14:30', customer: 'John Doe', items: '2x Avatar 3, 1x Combo Solo', total: 325000, status: 'Completed', method: 'VNPay' },
  { id: 'TX1002', date: '2026-02-07 15:15', customer: 'Jane Smith', items: '1x Spider-Man', total: 95000, status: 'Completed', method: 'Momo' },
  { id: 'TX1003', date: '2026-02-07 16:00', customer: 'Mike Ross', items: '4x Batman, 2x Combo Couple', total: 780000, status: 'Refunded', method: 'Credit Card' }
]);

const stats = ref([
  { label: 'Total Revenue', value: '124.5M', icon: markRaw(Money), color: 'text-success' },
  { label: 'Tickets Sold', value: '1,240', icon: markRaw(Ticket), color: 'text-primary' },
  { label: 'Avg. Order', value: '185k', icon: markRaw(Document), color: 'text-warning' }
]);
import BaseTable from '@/components/common/BaseTable.vue';

const tableColumns = [
  { label: 'Mã GD', key: 'id' },
  { label: 'Thời gian', key: 'date' },
  { label: 'Khách hàng', key: 'customer' },
  { label: 'Sản phẩm', key: 'items' },
  { label: 'Tổng tiền', key: 'total' },
  { label: 'Trạng thái', key: 'status' },
  { label: 'Hình thức', key: 'method' }
];

const currentPage = ref(1);
const pageSize = 10;
</script>

<template>
  <div class="admin-reports w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fw-bold text-dark mb-1" style="font-size: 18px;">Báo cáo Giao dịch</h2>
      </div>
      <el-button type="success" size="default" :icon="Download" round>Xuất CSV</el-button>
    </div>

    <!-- Stats Summary -->
    <div class="row g-3 mb-3 flex-shrink-0">
      <div v-for="s in stats" :key="s.label" class="col-md-4">
        <el-card shadow="never" class="border-black shadow-sm rounded-4 h-100">
          <div class="p-2 d-flex align-items-center justify-content-center gap-3">
            <div class="p-2 bg-light rounded-circle shadow-sm d-flex align-items-center justify-content-center">
              <el-icon :size="20" :class="s.color"><component :is="s.icon" /></el-icon>
            </div>
            <div class="text-start">
              <div class="text-secondary x-small fw-bold text-uppercase" style="font-size: 0.6rem; letter-spacing: 0.5px;">{{ s.label }}</div>
              <div class="fs-5 fw-bold text-dark lh-1">{{ s.value }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Filters -->
    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 flex-shrink-0">
      <div class="row g-2 align-items-center">
        <div class="col-md-5">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="Đến"
            start-placeholder="Ngày bắt đầu"
            end-placeholder="Ngày kết thúc"
            size="default"
            class="w-100 compact-input"
            popper-class="compact-date-picker"
          />
        </div>
        <div class="col-md-3">
          <el-select v-model="selectedType" placeholder="Phương thức" size="default" class="w-100 compact-input">
            <el-option label="Tất cả phương thức" value="All" />
            <el-option label="VNPay" value="VNPay" />
            <el-option label="Momo" value="Momo" />
            <el-option label="Thẻ tín dụng" value="Card" />
          </el-select>
        </div>
        <div class="col-md-4">
          <el-input placeholder="Mã giao dịch hoặc khách hàng..." :prefix-icon="Search" size="default" class="compact-input" />
        </div>
      </div>
    </el-card>

    <!-- Results Table Container -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable
        :data="reports"
        :columns="tableColumns"
        :total="reports.length"
        v-model:currentPage="currentPage"
        :page-size="pageSize"
        :show-actions="false"
      >
        <template #cell-id="{ row }">
          <span class="fw-bold small">{{ row.id }}</span>
        </template>

        <template #cell-date="{ row }">
          <span class="x-small text-secondary">{{ row.date }}</span>
        </template>

        <template #cell-items="{ row }">
          <div class="x-small text-truncate mx-auto" style="max-width: 200px;">{{ row.items }}</div>
        </template>

        <template #cell-total="{ row }">
          <span class="fw-bold small">{{ row.total.toLocaleString() }}đ</span>
        </template>

        <template #cell-status="{ row }">
          <el-tag :type="row.status === 'Completed' ? 'success' : 'danger'" size="small" round>{{ row.status }}</el-tag>
        </template>

        <template #cell-method="{ row }">
          <span class="small fw-bold">{{ row.method }}</span>
        </template>
      </BaseTable>
    </div>
  </div>
</template>

<style scoped>
.admin-reports {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.compact-input :deep(.el-input__wrapper) {
  padding: 0 8px !important;
  font-size: 0.8rem !important;
}

/* Shrink the range picker input height slightly */
:deep(.el-range-editor.el-input__wrapper) {
  padding: 0 10px !important;
}

.table thead th {
  border-bottom: none;
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

.x-small {
  font-size: 0.65rem;
}
</style>

<!-- Global style for the date picker popper as it is rendered outside the app root -->
<style>
.compact-date-picker {
  transform: scale(0.85);
  transform-origin: top left;
  margin-top: -10px !important;
}

.compact-date-picker .el-picker-panel__body {
  min-width: unset !important;
}

.compact-date-picker .el-date-range-picker {
  width: auto !important;
}
</style>
