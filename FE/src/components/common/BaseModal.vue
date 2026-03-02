<template>
  <el-dialog
    v-model="visible"
    :width="width"
    class="premium-dialog"
    :show-close="false"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    @close="$emit('close')"
  >
    <template #header>
      <div class="premium-header-modern">
        <div class="header-left">
          <div v-if="icon" class="header-icon-box-modern">
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

    <div class="modal-body-content">
      <slot></slot>
    </div>

    <template #footer v-if="!$slots.footer">
      <div class="premium-footer-modern">
        <el-button @click="visible = false" class="btn-modern-secondary">
          {{ cancelText }}
        </el-button>
        <el-button 
          type="primary" 
          @click="$emit('confirm')" 
          :loading="loading"
          class="btn-modern-primary"
        >
          {{ confirmText }}
        </el-button>
      </div>
    </template>
    <template #footer v-else>
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
    default: 'Hủy bỏ'
  },
  icon: {
    type: String,
    default: ''
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
  padding: 10px 5px;
}
</style>
