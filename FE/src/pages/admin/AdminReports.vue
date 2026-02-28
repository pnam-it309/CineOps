<script setup>
import { ref, markRaw, computed } from 'vue';
import { Download, Search, Filter, Document, Money, Ticket, Calendar, Refresh } from '@element-plus/icons-vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';

const dateRange = ref([]);
const selectedType = ref('All');
const reports = ref([
  { id: 'TX1001', date: '2026-02-07 14:30', customer: 'John Doe', items: '2x Avatar 3, 1x Combo Solo', total: 325000, status: 'Completed', method: 'VNPay' },
  { id: 'TX1002', date: '2026-02-07 15:15', customer: 'Jane Smith', items: '1x Spider-Man', total: 95000, status: 'Completed', method: 'Momo' },
  { id: 'TX1003', date: '2026-02-07 16:00', customer: 'Mike Ross', items: '4x Batman, 2x Combo Couple', total: 780000, status: 'Refunded', method: 'Credit Card' }
]);

const stats = ref([
  { label: 'Doanh thu tổng', value: '124.5M', icon: 'bi bi-cash-stack', type: 'success' },
  { label: 'Vé đã bán', value: '1,240', icon: 'bi bi-ticket-perforated-fill', type: 'primary' },
  { label: 'TB Đơn hàng', value: '185k', icon: 'bi bi-file-earmark-text-fill', type: 'warning' }
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
      subtitle="Thống kê và lịch sử giao dịch bán vé & đồ ăn"
      titleIcon="bi bi-bar-chart-fill"
      :data="filteredReports.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      :total="filteredReports.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      :selectable="false"
      @reset-filter="() => { dateRange = []; selectedType = 'All'; searchQuery = ''; }"
    >
      <template #header-actions-left>
        <el-button class="btn-premium-secondary text-success border-success-subtle" :icon="Download">Xuất CSV</el-button>
      </template>

      <template #stats>
        <div v-for="s in stats" :key="s.label" class="col-md-4">
          <StatCard 
              :label="s.label" 
              :value="s.value" 
              :icon="s.icon"
              :type="s.type"
            />
        </div>
      </template>

      <template #filters>
        <div class="filter-item" style="width: 350px;">
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
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="selectedType" placeholder="Phương thức" size="default" class="w-100">
            <el-option label="Tất cả phương thức" value="All" />
            <el-option label="VNPay" value="VNPay" />
            <el-option label="Momo" value="Momo" />
            <el-option label="Credit Card" value="Credit Card" />
          </el-select>
        </div>
        <div class="filter-item flex-grow-1">
          <el-input v-model="searchQuery" placeholder="Mã GD hoặc khách hàng..." :prefix-icon="Search" size="default" clearable />
        </div>
      </template>

      <template #columns>
        <el-table-column label="Mã GD" width="120">
            <template #default="{ row }">
                <span class="fw-bold text-indigo-500">#{{ row.id }}</span>
            </template>
        </el-table-column>
        
        <el-table-column label="Thời gian" width="180" align="center">
          <template #default="{ row }">
            <div class="small text-secondary"><i class="bi bi-clock me-1"></i>{{ row.date }}</div>
          </template>
        </el-table-column>

        <el-table-column label="Khách hàng" prop="customer" min-width="150" />

        <el-table-column label="Sản phẩm" min-width="200">
          <template #default="{ row }">
            <div class="small text-truncate" style="max-width: 250px;" :title="row.items">{{ row.items }}</div>
          </template>
        </el-table-column>

        <el-table-column label="Tổng tiền" width="150" align="center">
          <template #default="{ row }">
            <span class="fw-bold text-dark">{{ row.total.toLocaleString() }}đ</span>
          </template>
        </el-table-column>

        <el-table-column label="Trạng thái" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'Completed' ? 'success' : 'danger'" size="small" round effect="light">
              {{ row.status === 'Completed' ? 'Thành công' : 'Đã hoàn tiền' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Hình thức" width="130" align="center">
          <template #default="{ row }">
            <span class="badge bg-light-subtle text-dark border px-2 py-1">{{ row.method }}</span>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>
  </div>
</template>

<style scoped>
.text-indigo-500 {
  color: #4f46e5;
}
</style>
