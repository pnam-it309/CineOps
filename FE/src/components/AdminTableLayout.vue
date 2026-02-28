<template>
  <div class="admin-table-layout d-flex flex-column h-100 p-3 bg-light">
    <!-- Header Section -->
    <div class="page-header d-flex justify-content-between align-items-center mb-4 flex-shrink-0">
      <h3 class="fw-bold text-dark mb-0" style="font-size: 20px;">
        <i :class="[titleIcon, 'me-2 text-primary']" v-if="titleIcon"></i>{{ title }}
      </h3>
      <div class="d-flex gap-2 align-items-center">
        <slot name="header-actions-left"></slot>
        <el-button v-if="addButtonLabel" type="primary" :icon="Plus" @click="$emit('add-click')">
          {{ addButtonLabel }}
        </el-button>
        <slot name="header-actions"></slot>
      </div>
    </div>

    <!-- Stats Section -->
    <div v-if="$slots.stats" class="row g-3 mb-4 flex-shrink-0">
      <slot name="stats"></slot>
    </div>

    <!-- Filter Bar Section -->
    <div class="filter-container p-2 mb-3 bg-white rounded-3 shadow-sm border border-black flex-shrink-0">
      <div class="d-flex align-items-center gap-3 w-100 px-1">
          <slot name="filters"></slot>
          
          <div class="filter-item ms-auto">
            <el-tooltip content="Làm mới bộ lọc" placement="top">
              <el-button @click="$emit('reset-filter')" :icon="Refresh" circle class="btn-reset" size="default" />
            </el-tooltip>
          </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-wrapper bg-white rounded-3 border border-black shadow-sm flex-grow-1 overflow-hidden">
      <el-table 
        :data="data" 
        v-loading="loading" 
        stripe 
        height="100%" 
        class="admin-table"
        style="width: 100%;"
        @selection-change="val => $emit('selection-change', val)"
      >
        <el-table-column v-if="selectable" type="selection" width="50" align="center" />
        <slot name="columns"></slot>
      </el-table>
    </div>

    <!-- Pagination Section -->
    <div class="pagination-container d-flex align-items-center p-2 bg-white border border-black border-top-0 rounded-bottom-3 shadow-sm flex-shrink-0">
      <!-- 1. Total (Fixed Width) -->
      <div class="pagination-left px-3" style="width: 180px;">
        <span class="small fw-bold text-dark">Tổng cộng: {{ total }}</span>
      </div>
      
      <!-- 2. Sizes Area (Fixed Position) -->
      <div class="pagination-sizes-box" style="width: 130px;">
        <el-pagination
          :page-size="pageSize"
          @update:page-size="val => $emit('update:pageSize', val)"
          :page-sizes="[5, 10, 20, 50, 100]"
          layout="sizes"
          :total="total"
        />
      </div>

      <!-- 3. Pager Area (Flexible & Centered) -->
      <div class="pagination-pager-box flex-grow-1 d-flex justify-content-center">
        <el-pagination
          :current-page="currentPage"
          @update:current-page="val => $emit('update:currentPage', val)"
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
        />
      </div>

      <!-- 4. Jumper Area (Fixed Position) -->
      <div class="pagination-jumper-box d-flex justify-content-end px-3" style="width: 200px;">
        <el-pagination
          :current-page="currentPage"
          @update:current-page="val => $emit('update:currentPage', val)"
          layout="jumper"
          :total="total"
          :page-size="pageSize"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { Plus, Refresh } from '@element-plus/icons-vue';

defineProps({
  title: String,
  titleIcon: String,
  addButtonLabel: String,
  data: Array,
  loading: Boolean,
  total: Number,
  currentPage: Number,
  pageSize: Number,
  selectable: {
    type: Boolean,
    default: true
  }
});

defineEmits(['add-click', 'reset-filter', 'update:currentPage', 'update:pageSize', 'selection-change']);
</script>

<style scoped>
.admin-table-layout {
  overflow-y: hidden;
}

.table-wrapper {
  min-height: 0;
}

/* Common Styles Extracted */
:deep(.stat-card) {
  transition: transform 0.2s, box-shadow 0.2s;
  border-width: 1px !important;
}

:deep(.stat-card:hover) {
  transform: translateY(-3px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1) !important;
}

:deep(.filter-label) {
  font-size: 11px;
  letter-spacing: 0.5px;
}

:deep(.btn-action-icon) {
  width: 32px;
  height: 32px;
  display: flex !important;
  align-items: center;
  justify-content: center;
  border: 1px solid #000;
  border-radius: 8px;
  background: white;
  transition: all 0.2s;
  padding: 0;
}

/* Combined definition below */

.btn-reset {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #000;
  color: #000;
}

.btn-reset:hover {
  transform: rotate(360deg);
  color: var(--el-color-info);
  border-color: var(--el-color-info);
  background-color: #f0f9ff;
}

/* Table styling fixes */
:deep(.el-table th) {
  background-color: #f8f9fa !important;
  font-weight: 600;
  color: #374151;
  text-align: center !important;
}

:deep(.el-table .cell) {
  text-align: center !important;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  overflow: visible !important; /* Allow scale transform to show */
}

/* Give rows more height to accommodate hover effects */
:deep(.el-table__row) {
  height: 54px !important;
}

:deep(.el-table .d-flex) {
    justify-content: center;
    overflow: visible !important;
}

/* Action Button Hover Refinement */
:deep(.btn-action-icon:hover) {
  background: #f1f5f9;
  transform: scale(1.08); /* Slightly reduced from 1.1 to be safe */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

/* Limit search box width */
.admin-table-layout :deep(.search-input-wrapper) {
  max-width: 450px !important;
  flex-grow: 1;
}

/* Ensure filter items don't shrink too much */
.admin-table-layout :deep(.filter-item) {
  flex-shrink: 0;
}

.admin-table-layout :deep(.filter-item.flex-grow-1) {
  flex-shrink: 1;
}

/* Hide Scrollbars & One Point View Fixes */
.admin-table-layout :deep(*)::-webkit-scrollbar {
  display: none !important;
}

.admin-table-layout :deep(*) {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
}

:deep(.el-table__body-wrapper) {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
}

:deep(.el-table) {
  height: 100% !important;
}
</style>
