<template>
  <div class="admin-table-layout d-flex flex-column h-100" :class="{ 'p-3': !disablePadding }">
    <!-- 1. Page Header (Totally Outside) -->
    <div class="page-header-outside mb-3 flex-shrink-0 d-flex justify-content-between align-items-center">
      <div class="d-flex align-items-center gap-2">
        <i :class="[titleIcon, 'fs-4 text-primary mt-1']" v-if="titleIcon"></i>
        <h2 class="fw-bold text-dark mb-0" style="font-size: 20px;">{{ title }}</h2>
        <span v-if="subtitle" class="text-secondary small ms-2 opacity-75 border-start ps-2">{{ subtitle }}</span>
      </div>
      <div class="d-flex align-items-center gap-2">
        <slot name="page-header-right"></slot>
      </div>
    </div>

    <!-- 2. Refined Filter Card (Collapsible content only) -->
    <div v-if="!hideFilter" class="filter-wrapper-card mb-3 flex-shrink-0 border rounded-0 bg-white shadow-sm overflow-hidden">
      <!-- Header always visible -->
      <div class="filter-card-header d-flex justify-content-between align-items-center px-4 py-2 bg-light-subtle border-bottom" 
           @click="showFilter = !showFilter" style="cursor: pointer;">
        <div class="d-flex align-items-center gap-2">
          <el-icon class="text-primary fw-bold filter-icon-animate" :class="{ 'rotate-180': !showFilter }"><Filter /></el-icon>
          <span class="fw-bold text-dark" style="font-size: 15px; letter-spacing: 0.5px;">BỘ LỌC TÌM KIẾM</span>
        </div>
        <div class="d-flex align-items-center gap-2 text-secondary" style="font-size: 13px;">
          <span>{{ showFilter ? 'Nhấn để thu gọn' : 'Nhấn để mở rộng' }}</span>
        </div>
      </div>
      
      <!-- Body collapses -->
      <el-collapse-transition>
        <div v-show="showFilter" class="filter-card-body-wrapper border-top">
          <div class="filter-card-body p-4">
            <div class="d-flex align-items-center gap-3 flex-wrap justify-content-center">
              <slot name="filters"></slot>
              <div class="ms-auto d-flex gap-2">
                <el-tooltip content="Đặt lại bộ lọc" placement="top">
                  <el-button @click="$emit('reset-filter')" :icon="Refresh" plain class="square-btn">
                  </el-button>
                </el-tooltip>
                <slot name="filter-right"></slot>
              </div>
            </div>
          </div>
        </div>
      </el-collapse-transition>
    </div>

    <!-- 3. Main Data Card (Actions + Table + Pagination) -->
    <div class="main-data-card bg-white border rounded-0 shadow-sm d-flex flex-column h-100 overflow-hidden" 
         style="flex: 1 1 0; min-height: 0;">
      
      <!-- Action Buttons Bar -->
      <div class="actions-header-bar p-3 d-flex justify-content-between align-items-center flex-shrink-0 border-bottom">
        <div class="d-flex align-items-center gap-2">
          <slot name="header-actions-left"></slot>
        </div>
        <div class="d-flex gap-2 align-items-center">
          <slot name="header-actions"></slot>
          <div class="v-separator mx-2" v-if="addButtonLabel && ($slots.header_actions || $slots.header_actions_left)"></div>
          <el-tooltip :content="addButtonLabel" placement="top" v-if="addButtonLabel">
            <el-button @click="$emit('add-click')" type="primary" class="square-btn main-add-btn">
              <template #icon><el-icon><Plus /></el-icon></template>
            </el-button>
          </el-tooltip>
        </div>
      </div>

      <!-- Main Table Content -->
      <div class="table-viewport flex-grow-1" style="min-height: 0;">
        <div v-if="$slots.content" class="h-100">
          <slot name="content"></slot>
        </div>
        <div v-else class="h-100">
          <el-table 
            :data="data" 
            v-loading="loading" 
            stripe 
            height="100%" 
            border
            class="square-grid-table" 
            style="width: 100%;"
          >
            <slot name="columns"></slot>
            <template #empty>
              <div class="py-5 text-secondary opacity-50 text-center">
                <i class="bi bi-inbox fs-1 d-block mb-3"></i>
                <div class="fw-medium">Không có dữ liệu hiển thị</div>
              </div>
            </template>
          </el-table>
        </div>
      </div>

      <!-- Integrated Pagination Footer -->
      <div v-if="!hidePagination && (total > 0 || totalPages > 0)" 
           class="table-footer-pagination border-top p-3 bg-white flex-shrink-0">
        <div class="pagination-footer-row">
          <div class="footer-left">
            <div class="d-flex align-items-center gap-3">
              <el-select 
                :model-value="pageSize || 5" 
                size="default" 
                style="width: 150px"
                class="square-select" 
                @update:model-value="handlePageSizeChange"
              >
                <el-option label="5 / trang" :value="5" />
                <el-option label="10 / trang" :value="10" />
                <el-option label="20 / trang" :value="20" />
                <el-option label="50 / trang" :value="50" />
              </el-select>
              <span class="text-secondary small">Tổng: <strong>{{ total }}</strong></span>
            </div>
          </div>

          <div class="footer-center">
            <nav v-if="totalPages > 0">
              <ul class="pagination-list-square mb-0 list-unstyled d-flex align-items-center gap-0">
                <li>
                  <button class="page-nav-btn first" @click="handlePageChange(currentPage - 1)" :disabled="currentPage <= 1">
                    <el-icon><ArrowLeft /></el-icon>
                  </button>
                </li>
                <li v-for="page in pagesToShow" :key="page">
                  <button 
                    class="page-nav-btn" 
                    :class="{ active: currentPage === page }" 
                    @click="handlePageChange(page)"
                  >
                    {{ page }}
                  </button>
                </li>
                <li>
                  <button class="page-nav-btn last" @click="handlePageChange(currentPage + 1)" :disabled="currentPage >= totalPages">
                    <el-icon><ArrowRight /></el-icon>
                  </button>
                </li>
              </ul>
            </nav>
          </div>

          <div class="footer-right">
            <span class="text-secondary">
              Trang <strong>{{ currentPage || 1 }}</strong> / {{ totalPages || 1 }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Plus, Refresh, Filter, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';

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
  hidePagination: {
    type: Boolean,
    default: false
  },
  hideFilter: {
    type: Boolean,
    default: false
  },
  disablePadding: {
    type: Boolean,
    default: false
  }
});

const showFilter = ref(!props.hideFilter);

const emit = defineEmits(['add-click', 'reset-filter', 'update:currentPage', 'update:pageSize', 'selection-change']);

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
  if (total <= 5) return Array.from({ length: total }, (_, i) => i + 1);

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
  background-color: #f1f5f9;
}

/* Page Header */
.page-header-outside h2 {
  color: #1e293b;
  letter-spacing: -0.01em;
}

/* Grid Design */
.filter-wrapper-card, .main-data-card {
  border-radius: 0 !important;
  border: 1px solid #cbd5e1 !important;
}

.filter-card-header {
  background-color: #f8fafc !important;
  transition: background-color 0.2s;
}

.filter-icon-animate {
  transition: transform 0.3s ease;
}

.rotate-180 { transform: rotate(180deg); }

/* Square Grid Table */
:deep(.square-grid-table) {
  --el-table-header-bg-color: #f8fafc;
  border-radius: 0 !important;
}

:deep(.square-grid-table th) {
  background-color: #f8fafc !important;
  color: #334155 !important;
  font-weight: 700 !important;
  height: 50px !important;
  font-size: 13px !important;
  text-transform: uppercase;
  border-bottom: 2px solid #cbd5e1 !important;
  text-align: center !important;
}

:deep(.square-grid-table td) {
  text-align: center !important;
  padding: 12px 0 !important;
  color: #1e293b;
}

/* Square Buttons */
.square-btn {
  border-radius: 0 !important;
  height: 40px;
  width: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-add-btn {
  box-shadow: none !important;
}

/* Square Pagination */
.pagination-footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-nav-btn {
  min-width: 38px;
  height: 38px;
  background: white;
  border: 1px solid #cbd5e1;
  color: #475569;
  font-weight: 600;
  margin-left: -1px;
  transition: all 0.2s;
  cursor: pointer;
}

.page-nav-btn:hover:not(:disabled):not(.active) {
  background-color: #f1f5f9;
}

.page-nav-btn.active {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
  z-index: 1;
}

.page-nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f8fafc;
}

/* Square Select */
:deep(.square-select .el-input__wrapper) {
  border-radius: 0 !important;
  box-shadow: none !important;
  border: 1px solid #cbd5e1 !important;
}

.v-separator {
  width: 1px;
  height: 24px;
  background-color: #cbd5e1;
}
</style>
