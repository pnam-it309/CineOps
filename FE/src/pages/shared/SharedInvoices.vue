<script setup>
import { ref, computed } from 'vue';
import { Search, Printer } from '@element-plus/icons-vue';
import notification from '@/utils/notifications';
import BaseTable from '@/components/common/BaseTable.vue';

const invoiceColumns = [
  { label: 'Mã hóa đơn', key: 'id', width: '150px' },
  { label: 'Khách hàng', key: 'customer', minWidth: '200px' },
  { label: 'Ngày lập', key: 'createdAt', width: '180px' },
  { label: 'Tổng tiền', key: 'totalAmount', width: '150px' },
  { label: 'Trạng thái', key: 'status', width: '150px' },
];

const invoices = ref([
  { id: 'INV-1023', createdAt: '2026-02-28 17:45', customerName: 'Tiền mặt (Khách vãng lai)', customerPhone: 'N/A', totalAmount: 195000, items: '2x Spider-Man', status: 'PAID' },
  { id: 'INV-1022', createdAt: '2026-02-28 17:30', customerName: 'Alice Johnson', customerPhone: '0901 222 333', totalAmount: 325000, items: '2x Avatar, 1x Combo S', status: 'PAID' },
  { id: 'INV-1021', createdAt: '2026-02-28 17:15', customerName: 'Tiền mặt (Khách vãng lai)', customerPhone: 'N/A', totalAmount: 95000, items: '1x Spider-Man', status: 'CANCELLED' },
  { id: 'INV-1020', createdAt: '2026-02-28 16:50', customerName: 'Bob Smith', customerPhone: '0988 777 666', totalAmount: 450000, items: '4x Batman', status: 'PAID' }
]);

const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');
const statusFilter = ref('all');
const detailVisible = ref(false);
const selectedInvoice = ref(null);

const filteredInvoices = computed(() => {
    return invoices.value.filter(inv => {
        const matchSearch = !searchQuery.value || 
                          inv.id.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                          inv.customerName.toLowerCase().includes(searchQuery.value.toLowerCase());
        const matchStatus = statusFilter.value === 'all' || inv.status === statusFilter.value;
        return matchSearch && matchStatus;
    });
});

const handleView = (row) => {
  selectedInvoice.value = row;
  detailVisible.value = true;
};

const handlePrint = (row) => {
  notification.success(`Đang xử lý in hóa đơn ${row.id}...`);
};

const getStatusType = (status) => {
  const map = { PAID: 'success', CANCELLED: 'danger', PENDING: 'warning' };
  return map[status] || 'info';
};

const getStatusLabel = (status) => {
  const map = { PAID: 'Đã thanh toán', CANCELLED: 'Đã hủy', PENDING: 'Chờ thanh toán' };
  return map[status] || 'Không xác định';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);
</script>

<template>
  <div class="shared-invoices-page">
    <BaseTable
      title="Hóa đơn"
      subtitle="Quản lý và tra cứu lịch sử hóa đơn khách hàng"
      titleIcon="bi bi-receipt"
      :data="filteredInvoices.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      :columns="invoiceColumns"
      :total="filteredInvoices.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @reset-filter="() => { searchQuery = ''; statusFilter = 'all'; }"
      :hide-pagination="false"
    >
      <template #filters>
        <div class="filter-item" style="width: 350px;">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo mã HD, khách hàng hoặc SĐT..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="statusFilter" placeholder="Trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Đã thanh toán" value="PAID" />
            <el-option label="Đã hủy" value="CANCELLED" />
            <el-option label="Chờ thanh toán" value="PENDING" />
          </el-select>
        </div>
      </template>

      <template #cell-id="{ row }">
        <span class="fw-bold text-indigo-500">{{ row.id }}</span>
      </template>

      <template #cell-customer="{ row }">
        <div class="text-center">
          <div class="fw-bold text-dark">{{ row.customerName }}</div>
          <div class="small text-secondary">{{ row.customerPhone }}</div>
        </div>
      </template>

      <template #cell-createdAt="{ row }">
        <div class="small">{{ row.createdAt }}</div>
      </template>

      <template #cell-totalAmount="{ row }">
        <span class="fw-bold">{{ formatCurrency(row.totalAmount) }}</span>
      </template>

      <template #cell-status="{ row }">
        <el-tag :type="getStatusType(row.status)" effect="light" size="small" round>
          {{ getStatusLabel(row.status) }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center gap-1">
          <button class="btn-action-icon action-view" @click="handleView(row)">
            <i class="bi bi-eye fs-6"></i>
          </button>
          <button class="btn-action-icon btn-action-print" @click="handlePrint(row)">
            <i class="bi bi-printer fs-6"></i>
          </button>
        </div>
      </template>
    </BaseTable>

    <!-- Invoice Detail Dialog -->
    <el-dialog
      v-model="detailVisible"
      width="700px"
      class="premium-dialog invoice-detail-dialog"
      destroy-on-close
    >
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i class="bi bi-receipt-cutoff"></i>
            </div>
            <div class="header-text">
              <h5 class="title">Chi tiết hóa đơn</h5>
              <p class="subtitle opacity-75">Mã HD: {{ selectedInvoice?.id }}</p>
            </div>
          </div>
        </div>
      </template>
      
      <div v-if="selectedInvoice" class="invoice-container p-2">
        <!-- Invoice content here -->
        <p class="text-center text-secondary py-5">Nội dung chi tiết hóa đơn đang được hiển thị...</p>
      </div>

      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="detailVisible = false" class="btn-cine-secondary">Hủy</el-button>
          <el-button type="primary" :icon="Printer" class="btn-cine-primary">In hóa đơn</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
code {
  letter-spacing: 0.5px;
}
.text-indigo-500 {
  color: #4f46e5;
}
.btn-action-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0;
  border: 1px solid #e2e8f0;
  background: white;
  transition: all 0.2s;
}

.btn-action-icon:hover {
  background: #f1f5f9;
}

.action-view:hover { color: #3b82f6; border-color: #3b82f6; }
.btn-action-print:hover { color: #10b981; border-color: #10b981; }
</style>
