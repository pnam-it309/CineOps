<script setup>
import { ref } from 'vue';
import { Plus, PriceTag, Setting, Edit, Delete, Calendar, Clock, User } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const priceRules = ref([
  { id: 1, name: 'Normal Weekday', type: 'Weekday', timeFrom: '08:00', timeTo: '17:00', regular: 85000, vip: 110000, couple: 180000 },
  { id: 2, name: 'Weekday Peak', type: 'Weekday', timeFrom: '17:00', timeTo: '23:59', regular: 95000, vip: 125000, couple: 210000 },
  { id: 3, name: 'Weekend Standard', type: 'Weekend/Holiday', timeFrom: '08:00', timeTo: '23:59', regular: 110000, vip: 145000, couple: 240000 },
  { id: 4, name: 'Late Night', type: 'All Days', timeFrom: '00:00', timeTo: '03:00', regular: 75000, vip: 100000, couple: 150000 }
]);

const editingRule = ref(null);
const dialogVisible = ref(false);

const ruleForm = ref({
  name: '',
  type: 'Weekday',
  timeFrom: '08:00',
  timeTo: '17:00',
  regular: 85000,
  vip: 110000,
  couple: 180000
});

const handleEdit = (rule) => {
  editingRule.value = rule;
  Object.assign(ruleForm.value, rule);
  dialogVisible.value = true;
};

const formatPrice = (p) => p.toLocaleString() + 'đ';
import BaseTable from '@/components/common/BaseTable.vue';

const tableColumns = [
  { label: 'Tên cấu hình', key: 'name' },
  { label: 'Ngày áp dụng', key: 'type' },
  { label: 'Khung giờ', key: 'timeRange' },
  { label: 'Ghế Thường', key: 'regular' },
  { label: 'Ghế VIP', key: 'vip' },
  { label: 'Ghế Đôi', key: 'couple' }
];

const currentPage = ref(1);
const pageSize = 10;
</script>

<template>
  <div class="admin-pricing w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Ma trận Cấu hình Giá</h2>
      </div>
      <el-button type="primary" size="default" :icon="Plus" round @click="dialogVisible = true">Tạo cấu hình mới</el-button>
    </div>

    <div class="row g-3 mb-3 flex-shrink-0">
      <!-- Quick Reference Summary -->
      <div class="col-md-3">
        <el-card shadow="never" class="border-black shadow-sm rounded-4 h-100 bg-primary bg-opacity-10 text-primary">
          <div class="d-flex align-items-center gap-2">
            <div class="p-2 bg-white rounded-circle shadow-sm">
              <el-icon :size="20"><PriceTag /></el-icon>
            </div>
            <div>
              <div class="small fw-bold opacity-75 x-small">Giá cơ bản</div>
              <div class="fw-bold fs-6">85,000đ</div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="col-md-3">
        <el-card shadow="never" class="border-black shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-2">
            <div class="p-2 bg-light rounded-circle">
              <el-icon :size="20" class="text-warning"><Clock /></el-icon>
            </div>
            <div>
              <div class="small text-secondary fw-bold x-small">Quy tắc đang áp dụng</div>
              <div class="fw-bold fs-6">{{ priceRules.length }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Pricing Matrix Table Container -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable
        :data="priceRules"
        :columns="tableColumns"
        :total="priceRules.length"
        v-model:currentPage="currentPage"
        :page-size="pageSize"
        @edit="handleEdit"
        @delete="(r) => ElMessage.warning('Delete not implemented for proto')"
      >
        <template #cell-type="{ row }">
          <el-tag round :type="row.type === 'Weekday' ? 'info' : 'warning'" effect="plain" size="small">
            {{ row.type }}
          </el-tag>
        </template>

        <template #cell-timeRange="{ row }">
          <div class="d-flex align-items-center justify-content-center gap-2 small">
            <el-icon><Clock /></el-icon>
            {{ row.timeFrom }} - {{ row.timeTo }}
          </div>
        </template>

        <template #cell-regular="{ row }">
          <span class="fw-bold">{{ formatPrice(row.regular) }}</span>
        </template>

        <template #cell-vip="{ row }">
          <span class="fw-bold text-warning">{{ formatPrice(row.vip) }}</span>
        </template>

        <template #cell-couple="{ row }">
          <span class="fw-bold text-danger">{{ formatPrice(row.couple) }}</span>
        </template>
      </BaseTable>
    </div>


    <!-- Rule Config Dialog -->
    <el-dialog v-model="dialogVisible" :title="editingRule ? 'Chỉnh sửa quy tắc giá' : 'Tạo quy tắc giá mới'" width="500px" class="rounded-4">
      <el-form :model="ruleForm" label-position="top">
        <el-form-item label="Tên quy tắc" required>
          <el-input v-model="ruleForm.name" placeholder="VD: Cuối tuần giờ cao điểm" />
        </el-form-item>
        
        <div class="row g-2">
          <div class="col-md-12">
            <el-form-item label="Ngày áp dụng">
              <el-radio-group v-model="ruleForm.type" class="w-100">
                <el-radio-button label="Ngày thường" value="Weekday" />
                <el-radio-button label="Cuối tuần/Lễ" value="Weekend/Holiday" />
                <el-radio-button label="Tất cả các ngày" value="All Days" />
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Từ giờ">
              <el-input v-model="ruleForm.timeFrom" placeholder="00:00" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Đến giờ">
              <el-input v-model="ruleForm.timeTo" placeholder="23:59" />
            </el-form-item>
          </div>
          
          <div class="col-12 mt-2"><hr class="my-2 opacity-10"></div>
          
          <div class="col-md-4">
            <el-form-item label="Ghế Thường">
              <el-input-number v-model="ruleForm.regular" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="Ghế VIP">
              <el-input-number v-model="ruleForm.vip" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="Ghế Đôi">
              <el-input-number v-model="ruleForm.couple" class="w-100" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Lưu cấu hình</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-pricing {
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
.badge {
  font-weight: 500;
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
