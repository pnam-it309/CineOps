<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="500px"
    :before-close="handleClose"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="false"
    class="age-warning-dialog"
  >
    <div class="text-center py-4">
      <div class="warning-icon mb-4">
        <i class="bi bi-exclamation-triangle-fill text-warning" style="font-size: 4rem;"></i>
      </div>
      
      <h4 class="mb-3">{{ title }}</h4>
      
      <div class="age-tag mb-3">
        <el-tag :type="getAgeTagType(movieInfo.nhanDoTuoi)" size="large" effect="dark">
          {{ movieInfo.nhanDoTuoi || movieInfo.doTuoi + '+' }}
        </el-tag>
      </div>
      
      <p class="text-muted mb-4">
        {{ getAgeWarningMessage(movieInfo.nhanDoTuoi) }}
      </p>
      
      <div class="actions">
        <el-button @click="handleCancel" size="large">
          <i class="bi bi-arrow-left me-2"></i>Quay lại
        </el-button>
        <el-button type="primary" @click="handleConfirm" size="large">
          <i class="bi bi-check-circle me-2"></i>Tôi đủ tuổi
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  movieInfo: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update:visible', 'confirm', 'cancel']);

const title = computed(() => `Cảnh báo độ tuổi - ${props.movieInfo.tenPhim}`);

const getAgeTagType = (nhanDoTuoi) => {
  const types = { 'P': 'success', 'T13': 'warning', 'T16': 'danger', 'T18': 'info' };
  return types[nhanDoTuoi] || '';
};

const getAgeWarningMessage = (nhanDoTuoi) => {
  const messages = {
    'P': 'Phim này phù hợp cho mọi lứa tuổi.',
    'T13': 'Phim này chỉ phù hợp cho khán giả từ 13 tuổi trở lên.',
    'T16': 'Phim này chỉ phù hợp cho khán giả từ 16 tuổi trở lên.',
    'T18': 'Phim này chỉ phù hợp cho khán giả từ 18 tuổi trở lên.'
  };
  return messages[nhanDoTuoi] || 'Vui lòng xác nhận bạn đủ độ tuổi để xem phim này.';
};

const handleClose = () => {
  emit('update:visible', false);
};

const handleConfirm = () => {
  emit('confirm');
  handleClose();
};

const handleCancel = () => {
  emit('cancel');
  handleClose();
};
</script>

<style scoped>
.age-warning-dialog {
  --el-dialog-padding-primary: 30px;
}

.warning-icon {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.age-tag {
  display: inline-block;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 20px;
}

.actions .el-button {
  min-width: 140px;
}
</style>
