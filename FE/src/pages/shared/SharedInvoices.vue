<script setup>
import { ref, computed } from 'vue';
import { Search, Printer, Close, RefreshRight } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

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
  ElMessage.success(`Đang xử lý in hóa đơn ${row.id}...`);
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
    <AdminTableLayout
      title="Hóa đơn"
      subtitle="Quản lý và tra cứu lịch sử hóa đơn khách hàng"
      titleIcon="bi bi-receipt"
      :data="filteredInvoices.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      :total="filteredInvoices.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      :selectable="false"
      @reset-filter="() => { searchQuery = ''; statusFilter = 'all'; }"
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

      <template #columns>
        <el-table-column label="Mã hóa đơn" width="150">
          <template #default="{ row }">
            <span class="fw-bold text-indigo-500">{{ row.id }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="Khách hàng" min-width="200">
          <template #default="{ row }">
            <div class="text-start">
              <div class="fw-bold text-dark">{{ row.customerName }}</div>
              <div class="small text-secondary">{{ row.customerPhone }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Ngày lập" width="180" align="center">
          <template #default="{ row }">
            <div class="small">{{ row.createdAt }}</div>
          </template>
        </el-table-column>

        <el-table-column label="Tổng tiền" width="150" align="center">
          <template #default="{ row }">
            <span class="fw-bold">{{ formatCurrency(row.totalAmount) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="Trạng thái" width="150" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light" size="small" round>
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Thao tác" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <button class="btn-action-icon btn-action-view" @click="handleView(row)">
                <i class="bi bi-eye fs-6"></i>
              </button>
              <button class="btn-action-icon btn-action-print" @click="handlePrint(row)">
                <i class="bi bi-printer fs-6"></i>
              </button>
            </div>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>

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
          <el-button @click="detailVisible = false" class="btn-premium-secondary">Hủy</el-button>
          <el-button type="primary" :icon="Printer" class="btn-premium-primary">In hóa đơn</el-button>
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
</style>
