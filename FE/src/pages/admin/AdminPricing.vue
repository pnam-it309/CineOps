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
</script>

<template>
  <div class="admin-pricing container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Pricing Rules Matrix</h2>
        <p class="text-secondary small mb-0">Configure ticket prices based on day of week, time of day, and seat type.</p>
      </div>
      <el-button type="primary" size="large" :icon="Plus" round @click="dialogVisible = true">Create Rule</el-button>
    </div>

    <div class="row g-4 mb-4">
      <!-- Quick Reference Summary -->
      <div class="col-md-3">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 h-100 bg-primary bg-opacity-10 text-primary">
          <div class="d-flex align-items-center gap-3">
            <div class="p-3 bg-white rounded-circle shadow-sm">
              <el-icon :size="24"><PriceTag /></el-icon>
            </div>
            <div>
              <div class="small fw-bold opacity-75">Base Rate</div>
              <div class="fw-bold fs-4">85,000đ</div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="col-md-3">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-3">
            <div class="p-3 bg-light rounded-circle">
              <el-icon :size="24" class="text-warning"><Clock /></el-icon>
            </div>
            <div>
              <div class="small text-secondary fw-bold">Active Rules</div>
              <div class="fw-bold fs-4">{{ priceRules.length }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Pricing Matrix Table -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden mb-4">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="ps-3">Rule Name</th>
              <th>Applicable Days</th>
              <th>Time Range</th>
              <th class="text-center">Regular Seat</th>
              <th class="text-center">VIP Seat</th>
              <th class="text-center">Couple Seat</th>
              <th class="text-end pe-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="rule in priceRules" :key="rule.id">
              <td class="ps-3 fw-bold">{{ rule.name }}</td>
              <td>
                <el-tag round :type="rule.type === 'Weekday' ? 'info' : 'warning'" effect="plain">
                  {{ rule.type }}
                </el-tag>
              </td>
              <td>
                <div class="d-flex align-items-center gap-2 small">
                  <el-icon><Clock /></el-icon>
                  {{ rule.timeFrom }} - {{ rule.timeTo }}
                </div>
              </td>
              <td class="text-center fw-bold">{{ formatPrice(rule.regular) }}</td>
              <td class="text-center fw-bold text-warning">{{ formatPrice(rule.vip) }}</td>
              <td class="text-center fw-bold text-danger">{{ formatPrice(rule.couple) }}</td>
              <td class="text-end pe-3">
                <el-button-group>
                  <el-button :icon="Edit" size="small" @click="handleEdit(rule)" />
                  <el-button :icon="Delete" size="small" type="danger" plain />
                </el-button-group>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <!-- Rule Config Dialog -->
    <el-dialog v-model="dialogVisible" :title="editingRule ? 'Edit Pricing Rule' : 'New Pricing Rule'" width="500px" class="rounded-4">
      <el-form :model="ruleForm" label-position="top">
        <el-form-item label="Rule Name" required>
          <el-input v-model="ruleForm.name" placeholder="e.g. Weekend Afternoon Peak" />
        </el-form-item>
        
        <div class="row g-2">
          <div class="col-md-12">
            <el-form-item label="Applied Days">
              <el-radio-group v-model="ruleForm.type" class="w-100">
                <el-radio-button label="Weekday" />
                <el-radio-button label="Weekend/Holiday" />
                <el-radio-button label="All Days" />
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Time From">
              <el-input v-model="ruleForm.timeFrom" placeholder="00:00" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Time To">
              <el-input v-model="ruleForm.timeTo" placeholder="23:59" />
            </el-form-item>
          </div>
          
          <div class="col-12 mt-2"><hr class="my-2 opacity-10"></div>
          
          <div class="col-md-4">
            <el-form-item label="Regular">
              <el-input-number v-model="ruleForm.regular" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="VIP">
              <el-input-number v-model="ruleForm.vip" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-md-4">
            <el-form-item label="Couple">
              <el-input-number v-model="ruleForm.couple" class="w-100" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Save Configuration</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.table thead th {
  border-bottom: none;
}
.badge {
  font-weight: 500;
}
</style>
