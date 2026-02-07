<script setup>
import { ref } from 'vue';
import { View, Check, Close, Search, FullScreen } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const ticketCode = ref('');
const scanResult = ref(null);

const handleCheckin = () => {
  if (ticketCode.value === 'T123') {
    scanResult.value = {
      status: 'Valid',
      movie: 'Avatar 2',
      time: '18:30',
      hall: 'Hall 1',
      seats: 'G8, G9',
      user: 'John Doe'
    };
    ElMessage.success('Ticket Validated Successfully');
  } else if (ticketCode.value === 'TUSED') {
    scanResult.value = { status: 'Used', message: 'This ticket was used today at 14:12.' };
    ElMessage.error('Ticket Already Used');
  } else {
    scanResult.value = { status: 'Invalid', message: 'No ticket found with this code.' };
    ElMessage.error('Invalid Ticket Code');
  }
};
</script>

<template>
  <div class="staff-checkin container-fluid p-4 d-flex flex-column align-items-center justify-content-center h-100">
    <div style="max-width: 500px; width: 100%;">
      <h2 class="fs-4 fw-bold text-center mb-4">Ticket Check-in</h2>
      
      <!-- Input Area -->
      <el-card shadow="never" class="border-0 shadow-sm rounded-4 mb-4">
        <div class="p-3">
          <label class="small fw-bold text-secondary text-uppercase mb-2 d-block">Scan QR or Enter Code</label>
          <div class="d-flex gap-2">
            <el-input 
              v-model="ticketCode" 
              placeholder="e.g. CIN-102938" 
              size="large" 
              class="fs-5"
              @keyup.enter="handleCheckin"
            />
            <el-button type="primary" size="large" :icon="Search" @click="handleCheckin" />
          </div>
          <div class="mt-3 text-center">
            <el-button link :icon="FullScreen" class="text-primary fw-bold">Open Camera Scanner</el-button>
          </div>
        </div>
      </el-card>

      <!-- Result Card -->
      <transition name="fade">
        <el-card v-if="scanResult" shadow="never" class="border-0 shadow-sm rounded-4 overflow-hidden p-0">
          <div :class="`border-top border-5 p-4` + (scanResult.status === 'Valid' ? ' border-success' : ' border-danger')">
            <div class="d-flex align-items-center gap-3 mb-4">
              <el-avatar 
                :size="64" 
                :class="scanResult.status === 'Valid' ? 'bg-success text-white' : 'bg-danger text-white'"
              >
                <el-icon :size="32">
                  <Check v-if="scanResult.status === 'Valid'" />
                  <Close v-else />
                </el-icon>
              </el-avatar>
              <div>
                <h4 class="fw-bold mb-1" :class="scanResult.status === 'Valid' ? 'text-success' : 'text-danger'">
                  Ticket {{ scanResult.status }}
                </h4>
                <div v-if="scanResult.status === 'Valid'" class="small text-secondary">Ready for entry</div>
                <div v-else class="small text-secondary">{{ scanResult.message }}</div>
              </div>
            </div>

            <div v-if="scanResult.status === 'Valid'" class="bg-light p-3 rounded-3 mb-4">
              <div class="row g-2">
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Movie</div>
                  <div class="fw-bold">{{ scanResult.movie }}</div>
                </div>
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Showtime</div>
                  <div class="fw-bold">{{ scanResult.time }} | {{ scanResult.hall }}</div>
                </div>
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Seats</div>
                  <div class="fw-bold">{{ scanResult.seats }}</div>
                </div>
                <div class="col-6">
                  <div class="small text-secondary text-uppercase fw-bold" style="font-size: 0.65rem;">Customer</div>
                  <div class="fw-bold">{{ scanResult.user }}</div>
                </div>
              </div>
            </div>

            <el-button 
              v-if="scanResult.status === 'Valid'" 
              type="success" 
              class="w-100 py-3 fw-bold"
              @click="scanResult = null; ticketCode = '';"
            >
              CONFIRM ENTRY
            </el-button>
            <el-button 
              v-else 
              class="w-100 py-3"
              @click="scanResult = null; ticketCode = '';"
            >
              TRY ANOTHER CODE
            </el-button>
          </div>
        </el-card>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.staff-checkin { min-height: calc(100vh - 80px); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(10px); }
</style>
