<template>
  <el-dialog
    v-model="visible"
    :width="width"
    top="2vh"
    class="premium-dialog square-modal"
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
        <el-button @click="visible = false" class="square-btn">
          {{ cancelText }}
        </el-button>
        <el-button 
          v-if="!onlyCancel"
          type="primary" 
          @click="$emit('confirm')" 
          :loading="loading"
          class="square-btn"
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
    default: 'Đóng'
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
  padding: 1.5rem;
  max-height: 85vh;
  overflow-y: auto;
}

.modal-body-content.no-padding {
  padding: 0;
}

/* Square Modal */
:deep(.square-modal) {
  border-radius: 0 !important;
  overflow: hidden;
}

:deep(.square-modal .el-dialog__header) {
  padding: 0;
  margin: 0;
}

:deep(.square-modal .el-dialog__body) {
  padding: 0;
}

.premium-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
}

.detail-header {
  background: #f8fafc;
  padding: 1.5rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-icon-box-modern {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eff6ff;
  color: #3b82f6;
  border-radius: 0;
  font-size: 1.25rem;
  border: 1px solid #dbeafe;
}

.detail-icon {
  background: #1e293b;
  color: #fff;
  border: none;
}

.header-text-modern .title {
  margin: 0;
  font-weight: 700;
  font-size: 1.15rem;
  color: #1e293b;
}

.header-text-modern .subtitle {
  margin: 0;
  font-size: 0.85rem;
  color: #64748b;
}

.header-close-btn {
  background: transparent;
  border: none;
  font-size: 1.25rem;
  color: #94a3b8;
  cursor: pointer;
  padding: 0.5rem;
  display: flex;
  transition: all 0.2s;
}

.header-close-btn:hover {
  color: #ef4444;
  background: #fef2f2;
}

.premium-footer-modern {
  padding: 1.25rem 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.square-btn {
  border-radius: 0 !important;
}

/* Custom Scrollbar */
.modal-body-content::-webkit-scrollbar {
  width: 6px;
}
.modal-body-content::-webkit-scrollbar-track {
  background: transparent;
}
.modal-body-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 0;
}
.modal-body-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
