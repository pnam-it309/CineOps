<template>
  <div class="stat-card border shadow-sm rounded-4 px-3 py-2 bg-white d-flex align-items-center" :class="'stat-style-' + type">
    <div v-if="icon" class="stat-icon-wrapper rounded-3 me-2 d-flex align-items-center justify-content-center" :class="iconBgClass">
      <i :class="[icon, iconColorClass]" style="font-size: 1.5rem;"></i>
    </div>
    <div class="stat-content flex-grow-1">
      <div class="stat-label text-secondary fw-semibold mb-0" style="font-size: 22px; line-height: 1.2; letter-spacing: 0.3px;">
        {{ label }}
      </div>
      <div class="stat-value fw-bold text-dark d-flex align-items-baseline" style="font-size: 28px; line-height: 1.2;">
        {{ value }}
        <span v-if="suffix" class="ms-1 small text-secondary fw-normal" style="font-size: 20px;">{{ suffix }}</span>
      </div>
      <div v-if="subText" class="stat-subtext mt-0 text-truncate" :class="subTextColorClass" style="font-size: 18px; line-height: 1.2;">
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

const iconBgClass = computed(() => `bg-${props.type} bg-opacity-10`);
const iconColorClass = computed(() => `text-${props.type}`);
const subTextColorClass = computed(() => `text-${props.subTextType}`);
</script>

<style scoped>
.stat-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-color: #f1f5f9 !important;
  border-left-width: 5px !important;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.05) !important;
  border-color: var(--el-color-primary-light-8) !important;
}

/* Dynamic Left Border Colors */
.stat-style-primary { border-left-color: #E31E24 !important; }
.stat-style-success { border-left-color: #10b981 !important; }
.stat-style-warning { border-left-color: #f59e0b !important; }
.stat-style-danger { border-left-color: #ef4444 !important; }
.stat-style-info { border-left-color: #3b82f6 !important; }

.stat-icon-wrapper {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
}

.stat-label {
  text-transform: capitalize;
}
</style>
