<template>
  <div class="stat-card border shadow-sm rounded-4 p-3 bg-white h-100 d-flex align-items-center position-relative overflow-hidden">
    <!-- Left indicator bar -->
    <div class="stat-indicator" :class="indicatorBgClass"></div>

    <div v-if="icon" class="stat-icon-wrapper rounded-3 me-3 d-flex align-items-center justify-content-center" :class="iconBgClass">
      <i :class="[icon, iconColorClass]" style="font-size: 1.1rem;"></i>
    </div>
    <div class="stat-content flex-grow-1">
      <div class="stat-label text-secondary fw-semibold mb-1" style="font-size: 12px; letter-spacing: 0.3px; text-transform: uppercase;">
        {{ label }}
      </div>
      <div class="stat-value fw-bold text-dark d-flex align-items-baseline" style="font-size: 24px; line-height: 1;">
        {{ value }}
        <span v-if="suffix" class="ms-1 small text-secondary fw-normal" style="font-size: 14px;">{{ suffix }}</span>
      </div>
      <div v-if="subText" class="stat-subtext mt-1 text-truncate" :class="subTextColorClass" style="font-size: 11px;">
        {{ subText }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  suffix: { type: String, default: '' },
  icon: { type: String, default: '' },
  type: { type: String, default: 'primary' }, // primary, success, warning, danger, info
  subText: { type: String, default: '' },
  subTextType: { type: String, default: 'secondary' }
});

const iconBgClass = computed(() => {
  if (props.type === 'primary') return 'bg-primary bg-opacity-10';
  if (props.type === 'success') return 'bg-success bg-opacity-10';
  if (props.type === 'danger') return 'bg-danger bg-opacity-10';
  if (props.type === 'warning') return 'bg-warning bg-opacity-10';
  return 'bg-secondary bg-opacity-10';
});
const indicatorBgClass = computed(() => `bg-${props.type}`);
const iconColorClass = computed(() => `text-${props.type}`);
const subTextColorClass = computed(() => `text-${props.subTextType}`);
</script>

<style scoped>
.stat-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-color: #f1f5f9 !important;
}

.stat-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 5px;
  border-radius: 0 4px 4px 0;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08) !important;
  border-color: #e2e8f0 !important;
}

.stat-icon-wrapper {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
}

.stat-label {
  letter-spacing: 0.5px;
}
</style>
