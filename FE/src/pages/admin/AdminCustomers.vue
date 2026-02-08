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
  switch (level) {
    case 'Platinum': return 'danger';
    case 'Gold': return 'warning';
    default: return 'info';
  }
};
import BaseTable from '@/components/common/BaseTable.vue';

const tableColumns = [
  { label: 'Thông tin khách hàng', key: 'name' },
  { label: 'Liên hệ', key: 'contact' },
  { label: 'Hạng thành viên', key: 'level' },
  { label: 'Điểm tích lũy', key: 'points' },
  { label: 'Tổng số vé', key: 'tickets' },
  { label: 'Ngày đăng ký', key: 'joinDate' }
];

const currentPage = ref(1);
const pageSize = 10;
</script>

<template>
  <div class="admin-customers w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Cơ sở dữ liệu Khách hàng</h2>
      </div>
    </div>

    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 flex-shrink-0">
      <div class="row g-2 align-items-center">
        <div class="col-md-6 col-lg-8">
          <el-input placeholder="Tìm kiếm khách hàng..." :prefix-icon="Search" size="default" clearable />
        </div>
        <div class="col-md-6 col-lg-4">
          <el-select placeholder="Lọc theo hạng" size="default" class="w-100">
            <el-option label="Tất cả các hạng" value="all" />
            <el-option label="Bạch kim (Platinum)" value="platinum" />
            <el-option label="Vàng (Gold)" value="gold" />
            <el-option label="Bạc (Silver)" value="silver" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- Customers Table Container -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable :data="customers" :columns="tableColumns" :total="customers.length" v-model:currentPage="currentPage"
        :page-size="pageSize" @view="(c) => ElMessage.info('Detail view not implemented')">
        <template #cell-name="{ row }">
          <div class="d-flex align-items-center justify-content-center gap-2">
            <el-avatar :size="28" class="bg-primary small">{{ row.name.charAt(0) }}</el-avatar>
            <div class="fw-bold small">{{ row.name }}</div>
          </div>
        </template>

        <template #cell-contact="{ row }">
          <div class="x-small">{{ row.email }}</div>
          <div class="text-secondary x-small">{{ row.phone }}</div>
        </template>

        <template #cell-level="{ row }">
          <el-tag :type="getLevelColor(row.level)" effect="dark" size="small">{{ row.level }}</el-tag>
        </template>

        <template #cell-points="{ row }">
          <span class="fw-bold text-primary">{{ row.points.toLocaleString() }}</span>
        </template>

        <template #cell-tickets="{ row }">
          <span class="badge bg-light text-dark px-2 rounded-pill">{{ row.tickets }}</span>
        </template>

        <template #actions="{ row }">
          <button class="btn btn-action-icon" title="View Details" @click="ElMessage.info('Details for ' + row.name)">
            <i class="bi bi-eye"></i>
          </button>
        </template>
      </BaseTable>
    </div>
  </div>
</template>

<style scoped>
.admin-customers {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
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
