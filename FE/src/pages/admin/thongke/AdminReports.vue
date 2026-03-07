<script setup>
import { ref, markRaw, computed } from 'vue';
import { Download, Search, Filter, Document, Money, Ticket, Calendar, Refresh } from '@element-plus/icons-vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseTable from '@/components/common/BaseTable.vue';

const reportColumns = [
  { label: 'Mã GD', key: 'id', width: '120px' },
  { label: 'Thời gian', key: 'date', width: '180px' },
  { label: 'Khách hàng', key: 'customer', minWidth: '150px' },
  { label: 'Sản phẩm', key: 'items', minWidth: '200px' },
  { label: 'Tổng tiền', key: 'total', width: '150px' },
  { label: 'Trạng thái', key: 'status', width: '130px' },
  { label: 'Hình thức', key: 'method', width: '130px' },
];


const dateRange = ref([]);
const selectedType = ref('All');
const reports = ref([
  { id: 'TX1001', date: '2026-02-07 14:30', customer: 'John Doe', items: '2x Avatar 3, 1x Combo Solo', total: 325000, status: 'Completed', method: 'VNPay' },
  { id: 'TX1002', date: '2026-02-07 15:15', customer: 'Jane Smith', items: '1x Spider-Man', total: 95000, status: 'Completed', method: 'Momo' },
  { id: 'TX1003', date: '2026-02-07 16:00', customer: 'Mike Ross', items: '4x Batman, 2x Combo Couple', total: 780000, status: 'Refunded', method: 'Credit Card' }
]);



const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');

const filteredReports = computed(() => {
    return reports.value.filter(r => {
        const matchSearch = !searchQuery.value || r.id.toLowerCase().includes(searchQuery.value.toLowerCase()) || r.customer.toLowerCase().includes(searchQuery.value.toLowerCase());
        const matchType = selectedType.value === 'All' || r.method === selectedType.value;
        return matchSearch && matchType;
    });
});
</script>

<template>
  <div class="admin-reports-page">
    <AdminTableLayout
      title="Báo cáo Giao dịch"
      titleIcon="bi bi-bar-chart-fill"
      :data="filteredReports"
      :total="filteredReports.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @reset-filter="() => { dateRange = []; selectedType = 'All'; searchQuery = ''; }"
    >
      <template #header-actions-left>
        <el-button class="btn-cine-secondary text-success border-success-subtle" :icon="Download">Xuất CSV</el-button>
      </template>



      <template #filters>
        <div class="filter-item">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="Đến"
            start-placeholder="Bắt đầu"
            end-placeholder="Kết thúc"
            size="default"
            class="w-100"
          />
        </div>
        <div class="filter-item">
          <el-select v-model="selectedType" placeholder="Phương thức" size="default" class="w-100">
            <el-option label="Tất cả phương thức" value="All" />
            <el-option label="VNPay" value="VNPay" />
            <el-option label="Momo" value="Momo" />
            <el-option label="Credit Card" value="Credit Card" />
          </el-select>
        </div>
        <div class="filter-item">
          <el-input v-model="searchQuery" placeholder="Mã GD hoặc khách hàng..." :prefix-icon="Search" size="default" clearable />
        </div>
      </template>

      <template #content>
        <BaseTable
          :data="filteredReports.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
          :columns="reportColumns"
          :total="filteredReports.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          :show-actions="false"
          :hide-pagination="true"
        >
          <template #cell-id="{ row }">
            <span class="fw-bold text-indigo-500">#{{ row.id }}</span>
          </template>

          <template #cell-date="{ row }">
            <div class="small text-secondary"><i class="bi bi-clock me-1"></i>{{ row.date }}</div>
          </template>

          <template #cell-customer="{ row }">
            <span class="fw-semibold text-dark">{{ row.customer }}</span>
          </template>

          <template #cell-items="{ row }">
            <div class="small text-truncate" style="max-width: 250px;" :title="row.items">{{ row.items }}</div>
          </template>

          <template #cell-total="{ row }">
            <span class="fw-bold text-dark">{{ row.total.toLocaleString() }}đ</span>
          </template>

          <template #cell-status="{ row }">
            <el-tag :type="row.status === 'Completed' ? 'success' : 'danger'" size="small" round effect="light">
              {{ row.status === 'Completed' ? 'Thành công' : 'Đã hoàn tiền' }}
            </el-tag>
          </template>

          <template #cell-method="{ row }">
            <span class="badge bg-light-subtle text-dark border px-2 py-1">{{ row.method }}</span>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>
  </div>
</template>

<style scoped>
.text-indigo-500 {
  color: #4f46e5;
}
</style>

