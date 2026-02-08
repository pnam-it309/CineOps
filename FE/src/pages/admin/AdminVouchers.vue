<script setup>
import { ref } from 'vue';
import { Plus, Ticket, Delete, Edit, Search, Check, Timer } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import BaseTable from '@/components/common/BaseTable.vue';

const vouchers = ref([
  { id: 1, code: 'NEWYEAR2026', type: 'Percentage', value: 20, minSpend: 200000, expiry: '2026-02-28', usage: '124 / 500', status: 'Active' },
  { id: 2, code: 'STUDENT50', type: 'Fixed Amount', value: 50000, minSpend: 0, expiry: '2026-12-31', usage: '842 / 1000', status: 'Active' },
  { id: 3, code: 'CINEMALOVER', type: 'Percentage', value: 15, minSpend: 150000, expiry: '2026-01-31', usage: '200 / 200', status: 'Expired' }
]);

const tableColumns = [
  { label: 'Mã giảm giá', key: 'code' },
  { label: 'Loại', key: 'type' },
  { label: 'Giá trị giảm', key: 'value' },
  { label: 'Lượt dùng (Sử dụng/Tổng)', key: 'usage' },
  { label: 'Ngày hết hạn', key: 'expiry' },
  { label: 'Trạng thái', key: 'status' }
];

const currentPage = ref(1);
const pageSize = 10;
const dialogVisible = ref(false);
const searchQuery = ref('');

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

const handleEdit = (v) => {
  ElMessage.info('Editing voucher: ' + v.code);
};

const handleDelete = (v) => {
  ElMessage.error('Delete functionality restricted in demo');
};
</script>

<template>
  <div class="admin-vouchers w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Quản lý Voucher</h2>
      </div>
      <el-button type="primary" size="default" :icon="Plus" round @click="dialogVisible = true">Tạo Voucher</el-button>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" class="border-black shadow-sm rounded-4 mb-3 w-100 flex-shrink-0">
      <div class="row g-2 align-items-center">
        <div class="col-md-6 col-lg-8">
          <el-input 
            v-model="searchQuery" 
            placeholder="Tìm voucher theo mã..." 
            :prefix-icon="Search" 
            size="default" 
            clearable 
          />
        </div>
        <div class="col-md-6 col-lg-4">
          <el-select placeholder="Lọc theo trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Đang hoạt động" value="Active" />
            <el-option label="Đã hết hạn" value="Expired" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- Vouchers Table Container -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable
        :data="vouchers"
        :columns="tableColumns"
        :total="vouchers.length"
        v-model:currentPage="currentPage"
        :page-size="pageSize"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <template #cell-code="{ row }">
          <code class="fw-bold fs-6 text-primary">{{ row.code }}</code>
        </template>

        <template #cell-value="{ row }">
          <span class="fw-bold small">
            {{ row.type === 'Percentage' ? row.value + '%' : row.value.toLocaleString() + 'đ' }}
          </span>
        </template>

        <template #cell-status="{ row }">
          <el-tag :type="getStatusType(row.status)" round effect="light" size="small">{{ row.status }}</el-tag>
        </template>
      </BaseTable>
    </div>

    <!-- Create Voucher Dialog -->
    <el-dialog v-model="dialogVisible" title="Tạo Voucher mới" width="500px" class="rounded-4">
      <el-form :model="voucherForm" label-position="top">
        <el-form-item label="Mã Voucher" required>
          <el-input v-model="voucherForm.code" placeholder="VD: MOVIE2026" />
        </el-form-item>
        <div class="row g-2">
          <div class="col-md-6">
            <el-form-item label="Loại giảm giá">
              <el-select v-model="voucherForm.type" class="w-100">
                <el-option label="Theo phần trăm (%)" value="Percentage" />
                <el-option label="Số tiền cố định (đ)" value="Fixed Amount" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Giá trị">
              <el-input-number v-model="voucherForm.value" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Đơn tối thiểu">
              <el-input-number v-model="voucherForm.minSpend" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Giới hạn lượt dùng">
              <el-input-number v-model="voucherForm.limit" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-12">
            <el-form-item label="Ngày hết hạn">
              <el-date-picker v-model="voucherForm.expiry" type="date" class="w-100" placeholder="Chọn ngày" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Phát hành Voucher</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-vouchers {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

code {
  letter-spacing: 1px;
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
</style>
