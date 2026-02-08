<script setup>
import { ref } from 'vue';
import { Timer, Money, Connection, CaretRight, SwitchButton } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const sessionData = ref({
  startTime: '08:00 AM',
  operator: 'Staff_01',
  ticketsSold: 84,
  cashTotal: 4250000,
  cardTotal: 8120000,
  revenue: 12370000
});

const cashInHand = ref(0);

const handleHandover = () => {
  ElMessage.success('Dữ liệu ca đã được lưu. Đang tạo báo cáo bàn giao...');
};
</script>

<template>
  <div class="staff-shift w-100 h-100 d-flex flex-column align-items-center justify-content-center overflow-hidden no-scroll">
    <div style="max-width: 600px; width: 100%;" class="flex-shrink-0">
      <h2 class="fs-4 fw-bold text-center mb-4">Kết thúc ca làm việc (Giao ca)</h2>

      <el-card shadow="never" class="border-black shadow-sm rounded-4 overflow-hidden mb-4 p-0">
        <div class="bg-primary text-white p-4">
          <div class="row align-items-center">
            <div class="col">
              <div class="small opacity-75 text-uppercase fw-bold">Doanh thu ca hiện tại</div>
              <div class="fs-2 fw-bold">{{ sessionData.revenue.toLocaleString() }}đ</div>
            </div>
            <div class="col-auto text-end">
              <div class="small opacity-75">Bắt đầu từ: <strong>{{ sessionData.startTime }}</strong></div>
              <div class="small opacity-75">Nhân viên: <strong>{{ sessionData.operator }}</strong></div>
            </div>
          </div>
        </div>
        
        <div class="p-4">
          <div class="row g-4 text-center mb-4">
            <div class="col-4 border-end">
              <div class="small text-secondary fw-bold text-uppercase mb-1" style="font-size: 0.65rem;">Số vé bán</div>
              <div class="fw-bold fs-5">{{ sessionData.ticketsSold }}</div>
            </div>
            <div class="col-4 border-end">
              <div class="small text-secondary fw-bold text-uppercase mb-1" style="font-size: 0.65rem;">Tiền mặt</div>
              <div class="fw-bold fs-5 text-success">{{ sessionData.cashTotal.toLocaleString() }}đ</div>
            </div>
            <div class="col-4">
              <div class="small text-secondary fw-bold text-uppercase mb-1" style="font-size: 0.65rem;">Thẻ / Chuyển khoản</div>
              <div class="fw-bold fs-5 text-primary">{{ sessionData.cardTotal.toLocaleString() }}đ</div>
            </div>
          </div>

          <div class="bg-light p-4 rounded-4 mb-4">
            <label class="small fw-bold text-secondary text-uppercase mb-3 d-block">Tổng tiền mặt bàn giao</label>
            <el-input-number 
               v-model="cashInHand" 
               class="w-100 mb-2" 
               size="large" 
               :min="0"
               :placeholder="'Nhập tổng tiền mặt trong két'"
            />
            <p class="small text-secondary mb-0">Vui lòng đếm lại số tiền mặt thực tế hiện có trong ngăn kéo.</p>
          </div>

          <div class="d-flex gap-2">
            <el-button type="primary" class="flex-grow-1 py-3 fw-bold" @click="handleHandover">
              XÁC NHẬN BÀN GIAO CA
            </el-button>
          </div>
        </div>
      </el-card>

      <div class="text-center">
        <el-button type="danger" plain :icon="SwitchButton">Đăng xuất khỏi quầy</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.staff-shift {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
}

.no-scroll {
  overflow: hidden !important;
}
</style>
