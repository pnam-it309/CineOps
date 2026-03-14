<template>
  <el-dialog
    v-model="visible"
    :width="width"
    top="5vh"
    class="premium-dialog modern-modal"
    :show-close="false"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    @close="$emit('close')"
  >
    <template #header>
      <div class="premium-header-modern" :class="{ 'detail-header': isDetail }">
        <div class="header-left">
          <div v-if="icon" class="header-icon-box-modern" :class="{ 'detail-icon': isDetail }">
            <i :class="icon"></i>
          </div>
          <div class="header-text-modern">
            <h5 class="title">{{ title }}</h5>
            <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>
          </div>
        </div>
        <div class="header-right-group">
          <slot name="header-extra"></slot>
          <button class="header-close-btn" @click="visible = false">
            <el-icon><Close /></el-icon>
          </button>
        </div>
      </div>
    </template>

    <div class="modal-body-content" :class="{ 'no-padding': isDetail }">
      <slot></slot>
    </div>

    <template #footer v-if="!$slots.footer && !hideFooter && !isDetail">
      <div class="premium-footer-modern">
        <el-button @click="visible = false" class="modern-btn">
          {{ cancelText }}
        </el-button>
        <el-button 
          v-if="!onlyCancel"
          type="primary" 
          @click="$emit('confirm')" 
          :loading="loading"
          class="modern-btn confirm-btn"
        >
          {{ confirmText }}
        </el-button>
      </div>
    </template>
    <template #footer v-else-if="$slots.footer && !hideFooter && !isDetail">
      <slot name="footer"></slot>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue';
import { Close } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '700px'
  },
  loading: {
    type: Boolean,
    default: false
  },
  confirmText: {
    type: String,
    default: 'Xác nhận'
  },
  cancelText: {
    type: String,
    default: 'Bỏ qua'
  },
  icon: {
    type: String,
    default: ''
  },
  hideFooter: {
    type: Boolean,
    default: false
  },
  isDetail: {
    type: Boolean,
    default: false
  },
  onlyCancel: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'confirm', 'close']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});
</script>

<style scoped>
.modal-body-content {
  padding: 1.75rem;
  max-height: 80vh;
  overflow-y: auto;
  background-color: #fff;
}

.modal-body-content.no-padding {
  padding: 0;
}

/* Modern Rounded Modal */
:deep(.el-overlay) {
  backdrop-filter: blur(4px);
  background-color: rgba(15, 23, 42, 0.4);
}

:deep(.modern-modal) {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.modern-modal .el-dialog__header) {
  padding: 0;
  margin: 0;
}

:deep(.modern-modal .el-dialog__body) {
  padding: 0;
}

.premium-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.75rem;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}

.detail-header {
  background: #f8fafc;
  border-bottom-color: #e2e8f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.header-icon-box-modern {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  color: #475569;
  border-radius: 12px;
  font-size: 1.25rem;
  transition: all 0.3s ease;
}

.detail-icon {
  background: #1e293b;
  color: #fff;
}

.header-text-modern .title {
  margin: 0;
  font-weight: 700;
  font-size: 1.25rem;
  color: #0f172a;
  letter-spacing: -0.025em;
}

.header-text-modern .subtitle {
  margin: 0.15rem 0 0 0;
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 400;
}

.header-close-btn {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  font-size: 1rem;
  color: #64748b;
  cursor: pointer;
  padding: 0.5rem;
  display: flex;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.header-close-btn:hover {
  color: #0f172a;
  background: #f1f5f9;
  transform: scale(1.05);
}

.premium-footer-modern {
  padding: 1.25rem 1.75rem;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.modern-btn {
  border-radius: 10px !important;
  font-weight: 600;
  padding: 12px 24px;
  height: auto;
  transition: all 0.2s;
  border: 1px solid #e2e8f0;
  color: #475569;
}

.modern-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.confirm-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #ea580c 100%) !important;
  border-color: transparent !important;
  color: #fff !important;
  box-shadow: 0 4px 15px -3px rgba(234, 88, 12, 0.4) !important;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #fbbf24 0%, #f97316 100%) !important;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.5) !important;
}

/* Custom Scrollbar */
.modal-body-content::-webkit-scrollbar {
  width: 8px;
}
.modal-body-content::-webkit-scrollbar-track {
  background: #f8fafc;
}
.modal-body-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
  border: 2px solid #f8fafc;
}
.modal-body-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
