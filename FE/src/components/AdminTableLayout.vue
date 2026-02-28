<template>
  <div class="admin-table-layout d-flex flex-column h-100 p-4">
    <!-- Header Section -->
    <div class="page-header d-flex justify-content-between align-items-end mb-4 flex-shrink-0">
      <div>
        <h3 class="fw-bold text-dark mb-1" style="font-size: 22px;">
          <i :class="[titleIcon, 'me-2 text-primary']" v-if="titleIcon"></i>{{ title }}
        </h3>
        <p v-if="subtitle" class="text-secondary small mb-0">{{ subtitle }}</p>
      </div>
      <div class="d-flex gap-2 align-items-center">
        <slot name="header-actions-left"></slot>
        <div class="filter-item border-start ps-3 ms-2" v-if="addButtonLabel">
          <el-button @click="$emit('add-click')" class="btn-add-premium">
            <template #icon>
              <el-icon><Plus /></el-icon>
            </template>
            {{ addButtonLabel }}
          </el-button>
        </div>
        <slot name="header-actions"></slot>
      </div>
    </div>

    <!-- Stats Section -->
    <div v-if="$slots.stats" class="row g-3 mb-4 flex-shrink-0">
      <slot name="stats"></slot>
    </div>

    <!-- Filter Bar Section -->
    <div class="filter-card p-3 mb-4 bg-white flex-shrink-0 shadow-sm border">
      <div class="d-flex align-items-center gap-3 w-100 px-1">
          <slot name="filters"></slot>
          
          <div class="filter-item border-start ps-3 ms-1 d-flex align-items-center">
              <button @click="$emit('reset-filter')" class="btn-reset-square" title="Làm mới bộ lọc">
                <el-icon><Refresh /></el-icon>
              </button>
          </div>

          <slot name="filter-right"></slot>
      </div>
    </div>

    <!-- Content or Table Section -->
    <div class="table-container flex-grow-1 overflow-hidden d-flex flex-column">
      <div v-if="$slots.content" class="content-wrapper flex-grow-1 overflow-auto no-scroll">
          <slot name="content"></slot>
      </div>
      <div v-else class="table-wrapper flex-grow-1 bg-white rounded-4 border shadow-sm overflow-hidden">
        <el-table 
          :data="data" 
          v-loading="loading" 
          stripe 
          height="100%" 
          class="admin-table"
          style="width: 100%;"
          @selection-change="val => $emit('selection-change', val)"
        >
          <el-table-column v-if="selectable" type="selection" width="55" align="center" fixed="left" />
          <slot name="columns"></slot>
          
          <!-- Empty State -->
          <template #empty>
            <div class="py-5 text-secondary opacity-50">
              <i class="bi bi-inbox fs-1 d-block mb-3"></i>
              <div class="fw-medium">Không có dữ liệu hiển thị</div>
            </div>
          </template>
        </el-table>
      </div>
    </div>

    <!-- Pagination Section (Refined to match image 1) -->
    <div class="pagination-footer d-flex align-items-center pt-4 px-1 flex-shrink-0 flex-wrap gap-3 mt-auto">
      <!-- Left: Total and Page Size -->
      <div class="pagination-left d-flex align-items-center gap-3" style="flex: 1; min-width: 200px;">
        <div class="text-dark fw-bold small">
          Tổng cộng: {{ total || 0 }}
        </div>
        
        <el-select 
          :model-value="pageSize || 10" 
          size="small" 
          style="width: 110px"
          class="compact-select no-border-select"
          @update:model-value="handlePageSizeChange"
        >
          <el-option label="5/trang" :value="5" />
          <el-option label="10/trang" :value="10" />
          <el-option label="20/trang" :value="20" />
          <el-option label="50/trang" :value="50" />
        </el-select>
      </div>

      <!-- Center: Navigation -->
      <div class="pagination-nav-wrapper d-flex justify-content-center" style="flex: 1; min-width: 200px;">
        <nav v-if="totalPages > 0" aria-label="Table navigation">
          <ul class="pagination-premium-list mb-0 list-unstyled d-flex align-items-center gap-1">
            <li class="page-item" :class="{ disabled: currentPage <= 1 }">
              <el-button class="page-link-btn" @click="handlePageChange(currentPage - 1)" :disabled="currentPage <= 1" link>
                <i class="bi bi-chevron-left small"></i>
              </el-button>
            </li>
            
            <li v-for="page in pagesToShow" :key="page" class="page-item">
              <el-button 
                class="page-link-btn" 
                :class="{ active: currentPage === page }"
                @click="handlePageChange(page)"
                link
              >
                {{ page }}
              </el-button>
            </li>

            <li class="page-item" :class="{ disabled: currentPage >= totalPages || total === 0 }">
              <el-button class="page-link-btn" @click="handlePageChange(currentPage + 1)" :disabled="currentPage >= totalPages || total === 0" link>
                <i class="bi bi-chevron-right small"></i>
              </el-button>
            </li>
          </ul>
        </nav>
      </div>

      <!-- Right: Empty spacer for centering balance -->
      <div class="pagination-right d-none d-md-block" style="flex: 1;"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { Plus, Refresh } from '@element-plus/icons-vue';

const props = defineProps({
  title: String,
  subtitle: String,
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

const emit = defineEmits(['add-click', 'reset-filter', 'update:currentPage', 'update:pageSize', 'selection-change']);

// Pagination Logic (Old Style)
const totalPages = computed(() => {
  if (!props.pageSize || props.pageSize <= 0) return 0;
  return Math.ceil((props.total || 0) / props.pageSize);
});

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    emit('update:currentPage', page);
  }
};

const handlePageSizeChange = (size) => {
  emit('update:pageSize', size);
  emit('update:currentPage', 1);
};

const pagesToShow = computed(() => {
  const current = props.currentPage || 1;
  const total = totalPages.value;
  if (total <= 0) return [];
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);

  let start = Math.max(1, current - 2);
  let end = Math.min(total, start + 4);
  
  if (end === total) {
    start = Math.max(1, end - 4);
  }
  
  const pages = [];
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});
</script>

<style scoped>
.admin-table-layout {
  overflow: hidden;
  background-color: #ffffff;
}

.table-container {
  min-height: 0;
}

.table-wrapper {
    position: relative;
}

/* Table styling fixes */
:deep(.el-table) {
    --el-table-border-color: #f1f5f9;
    --el-table-header-bg-color: #ffffff;
    border-radius: 16px;
}

:deep(.el-table th) {
  background-color: #ffffff !important;
  font-weight: 700 !important;
  color: #475569 !important;
  height: 54px !important;
  font-size: 12px !important;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #f1f5f9 !important;
}

:deep(.el-table__row) {
  height: 64px !important;
}

:deep(.el-table td) {
    color: #1e293b;
    font-size: 14px;
    border-bottom: 1px solid #f8fafc !important;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
    width: 6px;
    height: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
    background-color: #e2e8f0;
    border-radius: 10px;
}

.btn-reset-square {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    border: 1px solid #e2e8f0;
    background: #fff;
    color: #64748b;
    transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    padding: 0;
}

.btn-reset-square:hover {
    transform: rotate(360deg);
    color: #4f46e5;
    border-color: #4f46e5;
    background-color: #f0efff;
}

/* Custom Pagination Style (Matched with Image 1) */
.page-link-btn {
    width: 32px !important;
    height: 32px !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    color: #475569 !important;
    font-size: 14px !important;
    font-weight: 500 !important;
    transition: all 0.2s !important;
    padding: 0 !important;
}

.page-link-btn:hover:not(.active) {
    color: #1e293b !important;
    background: #f1f5f9 !important;
    border-radius: 6px !important;
}

.page-link-btn.active {
    color: #4f46e5 !important;
    font-weight: 700 !important;
}

:deep(.page-link-btn.is-disabled) {
    opacity: 0.3 !important;
    background: transparent !important;
}

:deep(.page-item.disabled .page-link-btn) {
    opacity: 0.3;
    cursor: not-allowed;
}

:deep(.compact-select .el-input__wrapper) {
    border-radius: 8px !important;
    background-color: #ffffff !important;
    box-shadow: none !important;
    border: 1px solid #e2e8f0 !important;
    height: 32px !important;
}

:deep(.no-border-select .el-input__wrapper) {
    border: none !important;
    background-color: #ffffff !important;
}

/* Global button border removal for action icons */
:deep(.btn-action-icon) {
    border: none !important;
    outline: none !important;
    box-shadow: none !important;
}
</style>
