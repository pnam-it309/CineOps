<script setup>
import { computed, ref, watch } from "vue";
import { Plus, Refresh } from '@element-plus/icons-vue';

const props = defineProps({
  data: {
    type: Array,
    required: true,
  },
  columns: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  showActions: {
    type: Boolean,
    default: true,
  },
  // Selection state
  selection: {
    type: Array,
    default: () => [],
  },
  // Pagination props
  total: {
    type: Number,
    default: 0,
  },
  currentPage: {
    type: Number,
    default: 1,
  },
  pageSize: {
    type: Number,
    default: 10,
  },
  hidePagination: {
    type: Boolean,
    default: false,
  },
  // New props for consolidated layout
  title: {
    type: String,
    default: "",
  },
  titleIcon: {
    type: String,
    default: "",
  },
  addButtonLabel: {
    type: String,
    default: "",
  },
  showFilters: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits([
  "update:currentPage",
  "update:pageSize",
  "update:selection",
  "edit",
  "delete",
  "selection-change",
  "view",
  "add-click",
  "reset-filter",
  "update-status",
]);

// Selection logic
const isAllSelected = computed(() => {
  if (!props.data || props.data.length === 0) return false;
  return props.data.every((item) =>
    props.selection.some((i) => (i.id || i) === (item.id || item))
  );
});

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    // Clear selections of current page data
    const newSelection = props.selection.filter(
      (i) => !props.data.some((d) => (d.id || d) === (i.id || i))
    );
    emit("update:selection", newSelection);
    emit("selection-change", newSelection);
  } else {
    // Add all current page data to selection
    const newItems = props.data.filter(
      (d) => !props.selection.some((i) => (i.id || i) === (d.id || d))
    );
    const newSelection = [...props.selection, ...newItems];
    emit("update:selection", newSelection);
    emit("selection-change", newSelection);
  }
};

const toggleSelection = (item) => {
  const isSelected = props.selection.some(
    (i) => (i.id || i) === (item.id || item)
  );
  let newSelection;
  if (isSelected) {
    newSelection = props.selection.filter(
      (i) => (i.id || i) !== (item.id || item)
    );
  } else {
    newSelection = [...props.selection, item];
  }
  emit("update:selection", newSelection);
  emit("selection-change", newSelection);
};

const isItemSelected = (item) => {
  return props.selection.some((i) => (i.id || i) === (item.id || item));
};

const totalPages = computed(() => Math.ceil(props.total / props.pageSize));

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    emit("update:currentPage", page);
  }
};

const handlePageSizeChange = (size) => {
  emit("update:pageSize", size);
  emit("update:currentPage", 1);
};

const pagesToShow = computed(() => {
  const current = props.currentPage;
  const total = totalPages.value;
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

const pageSizeLocal = computed({
  get: () => props.pageSize,
  set: (value) => handlePageSizeChange(value),
});
</script>

<template>
  <div class="base-table-container h-100 d-flex flex-column overflow-hidden p-3 bg-light">
    <!-- Card 1: Header, Filters, and Pagination Consolidated -->
    <div class="filter-card bg-white border rounded-4 shadow-sm mb-3">
      <!-- Card Header with Title and Main Actions -->
      <div class="card-header-section p-3 border-bottom" v-if="title || addButtonLabel">
        <div class="d-flex align-items-center justify-content-between">
          <div class="d-flex align-items-center gap-3">
            <h4 class="fw-bold text-dark mb-0 d-flex align-items-center" style="font-size: 18px;" v-if="title">
              <i :class="[titleIcon, 'me-2 text-primary']" v-if="titleIcon"></i>
              {{ title }}
            </h4>
          </div>
          <div class="d-flex align-items-center gap-2">
            <slot name="header-actions-left"></slot>
            <el-button v-if="addButtonLabel" @click="$emit('add-click')" type="primary">
              <template #icon>
                <el-icon><Plus /></el-icon>
              </template>
              {{ addButtonLabel }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- Filters Area (Top) -->
      <div class="p-3" v-if="showFilters && $slots.filters">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
          <!-- Left side: Filters and Reset -->
          <div class="d-flex align-items-center gap-3 flex-wrap">
            <slot name="filters"></slot>
            <el-button v-if="showFilters && $slots.filters" @click="$emit('reset-filter')" size="small" type="info" plain>
              <el-icon><Refresh /></el-icon>
              Làm mới
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Card 2: Table Content and Pagination -->
    <div class="table-card bg-white border rounded-4 d-flex flex-column h-100 overflow-hidden shadow-sm" style="min-height: 0;">
      <!-- Table content wrapper -->
      <div
        class="table-content-wrapper"
        style="flex: 1 1 0; min-height: 0; overflow: auto"
      >
        <table
          class="table table-hover align-middle mb-0 text-center"
          style="
            border-collapse: separate;
            border-spacing: 0;
            table-layout: auto;
            min-width: 100%;
          "
        >
          <!-- Sticky Header -->
          <thead class="sticky-top shadow-sm" style="z-index: 20">
            <tr class="border-bottom">
              <th
                v-for="(col, colIdx) in columns"
                :key="col.key || colIdx"
                class="py-2 px-3 fw-bold text-nowrap text-center table-header-bg"
                :class="{
                  'sticky-col-stt': colIdx === 0,
                }"
                :style="{
                  width: col.width || 'auto',
                  minWidth: col.minWidth || col.width || 'auto',
                }"
              >
                <template v-if="col.type === 'selection'">
                  <div class="form-check d-flex justify-content-center">
                    <input
                      class="form-check-input"
                      type="checkbox"
                      :checked="isAllSelected"
                      @change="toggleSelectAll"
                    />
                  </div>
                </template>
                <template v-else>
                  {{ col.label }}
                </template>
              </th>
              <th
                v-if="showActions"
                class="py-2 px-3 fw-bold text-center table-header-bg sticky-col-actions"
                style="width: 160px; min-width: 160px"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody v-if="!loading && (data || []).length > 0">
            <tr
              v-for="(item, index) in data"
              :key="item.id || index"
              class="border-bottom border-light-subtle"
            >
              <td
                v-for="(col, colIdx) in columns"
                :key="col.key || colIdx"
                class="py-2 px-3 text-dark"
                :class="{
                  'table-cell-sticky-stt': colIdx === 0,
                }"
                :style="{
                  width: col.width || 'auto',
                  minWidth: col.minWidth || col.width || 'auto',
                  whiteSpace: 'normal',
                  overflow: 'hidden',
                  maxWidth: col.maxWidth || 'none',
                }"
              >
                <template v-if="col.type === 'selection'">
                  <div class="form-check d-flex justify-content-center">
                    <input
                      class="form-check-input"
                      type="checkbox"
                      :checked="isItemSelected(item)"
                      @change="toggleSelection(item)"
                    />
                  </div>
                </template>
                <template v-else>
                  <slot :name="'cell-' + col.key" :row="item" :index="index">
                    {{ item[col.key] }}
                  </slot>
                </template>
              </td>
              <td
                v-if="showActions"
                class="py-2 px-3 td-sticky-actions"
                style="width: 140px; min-width: 140px"
              >
                <div class="d-flex justify-content-center align-items-center gap-2">
                  <slot name="actions" :row="item">
                    <el-tooltip content="Chi tiết" placement="top">
                      <button
                        class="btn-action-icon action-view"
                        @click="$emit('view', item)"
                      >
                        <i class="bi bi-eye fs-6"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip content="Chỉnh sửa" placement="top">
                      <button
                        class="btn-action-icon action-edit"
                        :disabled="item.trangThai === 0 || item.status === 'Ngừng hoạt động' || item.status === 'Đang bảo trì'"
                        @click="$emit('edit', item)"
                      >
                        <i class="bi bi-pencil fs-6"></i>
                      </button>
                    </el-tooltip>
                    <el-switch
                      :model-value="item.trangThai === 1 || item.status === 'Đang hoạt động' || item.status === 'Hoạt động' || item.status === 'Sẵn sàng'"
                      @change="(val) => $emit('update-status', { row: item, val })"
                      class="status-switch mx-1"
                      inactive-color="#ff4949"
                    />
                    <el-tooltip content="Xóa" placement="top">
                      <button
                        class="btn-action-icon action-delete"
                        @click="$emit('delete', item)"
                      >
                        <i class="bi bi-trash fs-6"></i>
                      </button>
                    </el-tooltip>
                  </slot>
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="loading">
            <tr>
              <td
                :colspan="(columns || []).length + (showActions ? 1 : 0)"
                class="py-5 border-0"
              >
                <div class="spinner-premium"></div>
                <div class="mt-3 text-secondary small">
                  Đang tải dữ liệu...
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <td
                :colspan="(columns || []).length + (showActions ? 1 : 0)"
                class="py-5 text-secondary opacity-50 border-0"
              >
                <div class="mb-3">
                  <i class="bi bi-inbox fs-1"></i>
                </div>
                <div class="fw-medium">Không có dữ liệu</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Section (At the bottom of Table Card) -->
      <div v-if="!hidePagination && total > 0" class="pagination-section border-top px-3 py-3">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
          <!-- Left side: Total results and Page Size Selector -->
          <div class="d-flex align-items-center gap-3">
            <span class="text-secondary small">Tổng cộng: <strong>{{ total }}</strong></span>
            <div class="d-flex align-items-center gap-2">
              <el-select
                v-model="pageSizeLocal"
                size="small"
                class="compact-select-simple"
                style="width: 100px"
              >
                <el-option :value="5" label="5 / trang" />
                <el-option :value="10" label="10 / trang" />
                <el-option :value="20" label="20 / trang" />
                <el-option :value="50" label="50 / trang" />
              </el-select>
            </div>
          </div>

          <!-- Right side: Page Navigation -->
          <div class="d-flex align-items-center gap-1">
            <button
              class="simple-page-btn arrow"
              :class="{ disabled: currentPage <= 1 }"
              @click="handlePageChange(currentPage - 1)"
            >
              <i class="bi bi-chevron-left"></i>
            </button>

            <button
              v-for="page in pagesToShow"
              :key="page"
              class="simple-page-btn"
              :class="{ active: page === currentPage }"
              @click="handlePageChange(page)"
            >
              {{ page }}
            </button>

            <button
              class="simple-page-btn arrow"
              :class="{ disabled: currentPage >= totalPages }"
              @click="handlePageChange(currentPage + 1)"
            >
              <i class="bi bi-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Main wrapper styles */
.base-table-container {
  min-height: 0;
  background-color: #f8fafc;
}

/* Consolidated Card at Top */
.filter-card {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* Table Card at Bottom */
.table-card {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  flex: 1 1 0;
  min-height: 0;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-content-wrapper {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
  flex: 1 1 0;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.table-header-bg {
  background-color: #ffffff !important;
  color: #1e293b !important;
  font-weight: 700 !important;
  font-size: 16px !important;
  letter-spacing: normal;
  text-transform: none;
  border-bottom: 2px solid #e2e8f0 !important;
}

.cursor-pointer {
  cursor: pointer;
}

/* Pagination buttons */

.simple-page-btn {
  min-width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
  border: none;
  background: transparent;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
}

.simple-page-btn:hover:not(.active):not(.arrow) {
  color: #4f46e5;
  background: #f8fafc;
  border-radius: 6px;
}

.simple-page-btn.active {
  color: #4f46e5;
  font-weight: 700;
  position: relative;
}

.simple-page-btn.arrow {
  color: #94a3b8;
}

.simple-page-btn.arrow:hover {
  color: #475569;
}

.page-item.disabled .simple-page-btn {
  opacity: 0.3;
  cursor: not-allowed;
}

.highlight-total {
  background-color: #ffffff !important;
  border-color: #e2e8f0 !important;
}

.btn-action-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: none;
  background: transparent;
  transition: all 0.2s;
  padding: 0;
  cursor: pointer;
}

.btn-action-icon:hover {
  background-color: #f1f5f9;
}

.spinner-premium {
  width: 32px;
  height: 32px;
  border: 3px solid #f1f5f9;
  border-top: 3px solid #4f46e5;
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

:deep(.compact-select-simple .el-input__wrapper) {
  border-radius: 6px !important;
  background-color: #ffffff !important;
  box-shadow: none !important;
  border: 1px solid #e2e8f0 !important;
  height: 30px !important;
}

:deep(.compact-select-simple .el-input__wrapper.is-focus) {
  border-color: #4f46e5 !important;
}

/* Sticky Column Styles */
.sticky-col-check,
.sticky-col-stt,
.sticky-col-stt-with-check,
.sticky-col-actions,
.table-cell-sticky-stt,
.table-cell-sticky-stt-with-check {
  position: sticky !important;
  background-color: #fff !important;
}

/* Individual Positions */
.sticky-col-check {
  left: 0;
  z-index: 12;
}
.td-sticky-check {
  position: sticky;
  left: 0;
  z-index: 10;
  background-color: #fff !important;
}

.sticky-col-stt {
  left: 0;
  z-index: 12;
  border-right: 1px solid #f1f5f9 !important;
}
.table-cell-sticky-stt {
  left: 0;
  z-index: 10;
  border-right: 1px solid #f1f5f9 !important;
}

.sticky-col-stt-with-check {
  left: 50px;
  z-index: 12;
  border-right: 1px solid #f1f5f9 !important;
}
.table-cell-sticky-stt-with-check {
  left: 50px;
  z-index: 10;
  border-right: 1px solid #f1f5f9 !important;
}

.sticky-col-actions {
  right: 0;
  z-index: 12;
  border-left: 1px solid #f1f5f9 !important;
}
.td-sticky-actions {
  position: sticky;
  right: 0;
  z-index: 10;
  background-color: #fff !important;
  border-left: 1px solid #f1f5f9 !important;
}

/* Group Boundary Indicators (Shadows) */
.sticky-col-stt::after,
.sticky-col-stt-with-check::after,
.table-cell-sticky-stt::after,
.table-cell-sticky-stt-with-check::after {
  content: "";
  position: absolute;
  top: 0;
  right: -10px;
  bottom: 0;
  width: 10px;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.05), transparent);
  pointer-events: none;
}

.sticky-col-actions::before,
.td-sticky-actions::before {
  content: "";
  position: absolute;
  top: 0;
  left: -10px;
  bottom: 0;
  width: 10px;
  background: linear-gradient(to left, rgba(0, 0, 0, 0.05), transparent);
  pointer-events: none;
}

:deep(.form-check-input) {
  width: 18px;
  height: 18px;
  border: 1.5px solid #dcdfe6;
  border-radius: 5px;
  background-color: #ffffff;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: none !important;
}

:deep(.form-check-input:checked) {
  background-color: #4f46e5;
  border-color: #4f46e5;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3e%3cpath fill='none' stroke='%23fff' stroke-linecap='round' stroke-linejoin='round' stroke-width='3' d='m6 10 3 3 6-6'/%3e%3c/svg%3e");
}

/* Custom styles for status switch */
.status-switch {
  --el-switch-on-color: #13ce66;
  --el-switch-off-color: #ff4949;
}
.status-switch :deep(.el-switch__label) {
  display: none !important;
}
</style>
