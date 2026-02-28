<script setup>
import { ref } from 'vue';
import { Check, Close, Search, FullScreen } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const ticketCode = ref('');
const scanResult = ref(null);

const handleCheckin = () => {
  if (ticketCode.value === 'T123') {
    scanResult.value = {
      status: 'Hợp lệ',
      movie: 'Avatar 2',
      time: '18:30',
      hall: 'Phòng 1',
      seats: 'G8, G9',
      user: 'Nguyễn Văn A'
    };
    ElMessage.success('Xác thực vé thành công');
  } else if (ticketCode.value === 'TUSED') {
    scanResult.value = { status: 'Đã dùng', message: 'Vé này đã được sử dụng lúc 14:12 hôm nay.' };
    ElMessage.error('Vé đã được sử dụng');
  } else {
    scanResult.value = { status: 'Không hợp lệ', message: 'Không tìm thấy vé với mã này.' };
    ElMessage.error('Mã vé không hợp lệ');
  }
};
</script>

<template>
  <div class="shared-checkin w-100 h-100 d-flex flex-column align-items-center justify-content-center overflow-hidden no-scroll">
    <div style="max-width: 500px; width: 100%;" class="flex-shrink-0">
      <h2 class="fw-bold text-center mb-4" style="font-size: 18px;">Soát vé vào cổng</h2>
      
      <!-- Input Area -->
      <div class="filter-card p-4 mb-4 shadow-sm">
        <div class="p-1">
          <label class="small fw-bold text-secondary text-uppercase mb-2 d-block">Quét QR hoặc Nhập mã vé</label>
          <div class="d-flex gap-2">
            <el-input 
              v-model="ticketCode" 
              placeholder="VD: CIN-102938" 
              size="large" 
              class="fs-5"
              @keyup.enter="handleCheckin"
            />
            <el-button type="primary" size="large" :icon="Search" @click="handleCheckin" />
          </div>
          <div class="mt-3 text-center">
            <el-button link :icon="FullScreen" class="text-primary fw-bold">Mở máy quét Camera</el-button>
          </div>
        </div>
      </div>

      <!-- Result Card -->
      <transition name="fade">
        <el-card v-if="scanResult" shadow="never" class="shadow-sm rounded-4 overflow-hidden p-0">
          <div :class="`border-top border-5 p-4` + (scanResult.status === 'Hợp lệ' ? ' border-success' : ' border-danger')">
            <div class="d-flex align-items-center gap-3 mb-4">
              <el-avatar 
                :size="56" 
                :class="scanResult.status === 'Hợp lệ' ? 'bg-success text-white' : 'bg-danger text-white'"
              >
                <el-icon :size="28">
                  <Check v-if="scanResult.status === 'Hợp lệ'" />
                  <Close v-else />
                </el-icon>
              </el-avatar>
              <div>
                <h4 class="fw-bold mb-1 fs-5" :class="scanResult.status === 'Hợp lệ' ? 'text-success' : 'text-danger'">
                  Vé {{ scanResult.status }}
                </h4>
                <div v-if="scanResult.status === 'Hợp lệ'" class="small text-secondary">Có thể vào phòng chiếu</div>
                <div v-else class="small text-secondary">{{ scanResult.message }}</div>
              </div>
            </div>

            <div v-if="scanResult.status === 'Hợp lệ'" class="bg-light p-3 rounded-3 mb-4">
              <div class="row g-2">
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Phim</div>
                  <div class="fw-bold small">{{ scanResult.movie }}</div>
                </div>
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Suất chiếu</div>
                  <div class="fw-bold small">{{ scanResult.time }} | {{ scanResult.hall }}</div>
                </div>
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Chỗ ngồi</div>
                  <div class="fw-bold small">{{ scanResult.seats }}</div>
                </div>
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Khách hàng</div>
                  <div class="fw-bold small">{{ scanResult.user }}</div>
                </div>
              </div>
            </div>

            <el-button 
              v-if="scanResult.status === 'Hợp lệ'" 
              type="success" 
              class="btn-premium-primary w-100 py-3 fw-bold"
              @click="scanResult = null; ticketCode = '';"
            >
              XÁC NHẬN VÀO CỬA
            </el-button>
            <el-button 
              v-else 
              class="btn-premium-secondary w-100 py-3"
              @click="scanResult = null; ticketCode = '';"
            >
              THỬ MÃ KHÁC
            </el-button>
          </div>
        </el-card>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.shared-checkin {
  height: calc(100vh - 84px);
  background-color: #f8fafc;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(10px); }

.no-scroll {
  overflow: hidden !important;
}
</style>
