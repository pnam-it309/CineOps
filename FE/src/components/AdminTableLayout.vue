<template>
  <div class="admin-table-layout d-flex flex-column h-100" :class="{ 'p-3': !disablePadding }">
    <!-- Header Section -->
    <div class="page-header d-flex justify-content-between align-items-end mb-4 flex-shrink-0">
      <div>
        <h3 class="fw-bold text-dark mb-1 d-flex align-items-center" style="font-size: 20px;">
          <i :class="[titleIcon, 'me-2 text-primary']" v-if="titleIcon"></i>
          {{ title }}
          <el-tooltip :content="showFilter ? 'Ẩn bộ lọc' : 'Hiện bộ lọc'" placement="top" v-if="!hideFilter">
            <button @click="showFilter = !showFilter" class="btn-filter-toggle ms-2" :class="{ active: showFilter }">
              <el-icon><Filter /></el-icon>
            </button>
          </el-tooltip>
        </h3>
        <p v-if="subtitle" class="text-secondary small mb-0">{{ subtitle }}</p>
      </div>
      <div class="d-flex gap-2 align-items-center">
        <slot name="header-actions-left"></slot>
        <!-- (Removed filter button from here) -->
        <div class="filter-item border-start ps-3 ms-2" v-if="addButtonLabel">
          <el-button @click="$emit('add-click')" type="primary">
            <template #icon>
              <el-icon>
                <Plus />
              </el-icon>
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
    <el-collapse-transition v-if="!hideFilter">
      <div v-show="showFilter" class="filter-card p-3 mb-4 bg-white flex-shrink-0 shadow-sm border">
        <div class="d-flex align-items-end gap-3 w-100 px-1 flex-wrap">
          <slot name="filters"></slot>

          <div class="filter-item border-start ps-3 ms-1 d-flex align-items-center gap-2">
            <span class="filter-label mb-1 d-block"></span>
            <button @click="$emit('reset-filter')" class="btn-reset-square" title="Làm mới bộ lọc">
              <el-icon><Refresh /></el-icon>
            </button>
            <button @click="showFilter = false" class="btn-reset-square text-danger" title="Thu gọn bộ lọc">
              <el-icon><Fold /></el-icon>
            </button>
          </div>

          <slot name="filter-right"></slot>
        </div>
      </div>
    </el-collapse-transition>

    <!-- Content or Table Section — flex-grow fills remaining space, min-height:0 allows shrink -->
    <div class="table-container d-flex flex-column" style="flex: 1 1 0; min-height: 0; overflow: hidden;">
      <!-- #content slot (used by pages with BaseTable) -->
      <div v-if="$slots.content" class="content-wrapper d-flex flex-column"
        style="flex: 1 1 0; min-height: 0; overflow: hidden;">
        <slot name="content"></slot>
      </div>
      <!-- #columns slot (used by pages with el-table directly) -->
      <div v-else class="table-wrapper flex-grow-1 bg-white rounded-4 border shadow-sm overflow-hidden text-center">
        <el-table :data="data" v-loading="loading" stripe height="100%" class="admin-table" style="width: 100%;">
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

    <!-- Pagination Section — luôn bám đáy, không cuộn theo bảng -->
    <div v-if="!hidePagination"
      class="pagination-footer d-flex align-items-center pt-4 px-1 flex-shrink-0 flex-wrap gap-3">
      <!-- Left: Total and Page Size -->
      <div class="pagination-left d-flex align-items-center gap-3" style="flex: 1; min-width: 200px;">
        <div class="text-dark fw-bold" style="font-size: 14px;">
          Tổng cộng: {{ total || 0 }}
        </div>

        <el-select :model-value="pageSize || 10" size="default" style="width: 120px; font-size: 14px;"
          class="compact-select no-border-select" @update:model-value="handlePageSizeChange">
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
              <el-button class="page-link-btn" @click="handlePageChange(currentPage - 1)" :disabled="currentPage <= 1"
                link>
                <i class="bi bi-chevron-left"></i>
              </el-button>
            </li>

            <li v-for="page in pagesToShow" :key="page" class="page-item">
              <el-button class="page-link-btn" :class="{ active: currentPage === page }" @click="handlePageChange(page)"
                link>
                {{ page }}
              </el-button>
            </li>

            <li class="page-item" :class="{ disabled: currentPage >= totalPages || total === 0 }">
              <el-button class="page-link-btn" @click="handlePageChange(currentPage + 1)"
                :disabled="currentPage >= totalPages || total === 0" link>
                <i class="bi bi-chevron-right"></i>
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
import { ref, computed } from 'vue';
import { Plus, Refresh, Filter, Fold } from '@element-plus/icons-vue';

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
  color: #1e293b !important;
  height: 46px !important;
  font-size: 16px !important;
  text-transform: none;
  letter-spacing: normal;
  border-bottom: 2px solid #e2e8f0 !important;
}

:deep(.el-table__row) {
  height: 48px !important;
}

:deep(.el-table td) {
  color: #475569 !important;
  font-weight: 500 !important;
  font-size: 16px !important;
  border-bottom: 1px solid #f1f5f9 !important;
  vertical-align: middle;
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
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #64748b;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  padding: 0;
}

.btn-reset-square:hover {
  transform: rotate(360deg);
  color: #E31E24;
  border-color: #E31E24;
  background-color: #fff1f1;
}

.btn-filter-toggle {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-filter-toggle:hover {
  background-color: #f1f5f9;
  color: #E31E24;
}

.btn-filter-toggle.active {
  color: #E31E24;
}

/* Tăng font-size cho thanh bộ lọc */
:deep(.filter-card .el-input__inner),
:deep(.filter-card .el-select__wrapper),
:deep(.filter-card .el-select__placeholder),
:deep(.filter-card .el-select__selected-item) {
  font-size: 14px !important;
  color: #1e293b !important;
}

:deep(.filter-card .el-select),
:deep(.filter-card .el-date-editor),
:deep(.filter-card .el-range-editor) {
  width: 180px !important;
}

:deep(.filter-card .el-date-editor.el-input) {
  height: 34px !important;
}

:deep(.filter-card .el-range-editor) {
  width: 380px !important;
}

:deep(.filter-card .el-input) {
  width: 280px !important;
}

:deep(.filter-card .el-input__wrapper),
:deep(.filter-card .el-select__wrapper),
:deep(.filter-card .el-radio-button__inner) {
  height: 34px !important;
  box-shadow: 0 0 0 1px #e2e8f0 inset !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  font-size: 14px !important;
}

:deep(.filter-card .el-input__wrapper),
:deep(.filter-card .el-select__wrapper) {
  border-radius: 8px !important;
}

/* Fix Grouping for Radio Buttons */
:deep(.filter-card .el-radio-group) {
  display: flex !important;
  flex-wrap: nowrap !important;
  gap: 0 !important;
}

:deep(.filter-card .el-radio-button__inner) {
  border: none !important;
  border-radius: 0 !important;
}

:deep(.filter-card .el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 10px 0 0 10px !important;
}

:deep(.filter-card .el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0 10px 10px 0 !important;
}

:deep(.filter-card .el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #E31E24 inset !important;
}

/* Custom Pagination Style */
.page-link-btn {
  width: 34px !important;
  height: 34px !important;
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
  color: #E31E24 !important;
  font-weight: 700 !important;
}

:deep(.page-link-btn.is-disabled) {
  opacity: 0.3 !important;
  background: transparent !important;
}

:deep(.compact-select .el-input__inner),
:deep(.compact-select .el-select__wrapper),
:deep(.compact-select .el-select__placeholder),
:deep(.compact-select .el-select__selected-item) {
  font-size: 14px !important;
  line-height: normal !important;
  color: #1e293b !important;
}

:deep(.compact-select .el-input__wrapper) {
  border-radius: 8px !important;
  background-color: #ffffff !important;
  box-shadow: none !important;
  border: 1px solid #e2e8f0 !important;
  height: 34px !important;
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
