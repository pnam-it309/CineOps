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
    <div class="filter-card bg-white border rounded-0 shadow-sm mb-3">
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
            <el-tooltip :content="addButtonLabel" placement="top" v-if="addButtonLabel">
              <el-button @click="$emit('add-click')" type="primary" class="square-btn">
                <template #icon>
                  <el-icon><Plus /></el-icon>
                </template>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </div>

      <!-- Filters Area (Top) -->
      <div class="p-3 border-top" v-if="showFilters && $slots.filters">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
          <!-- Left side: Filters and Reset -->
          <div class="d-flex align-items-center gap-3 flex-wrap">
            <slot name="filters"></slot>
            <el-tooltip content="Làm mới bộ lọc" placement="top">
              <el-button v-if="showFilters && $slots.filters" @click="$emit('reset-filter')" size="default" type="info" plain class="square-btn">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <!-- Card 2: Table Content and Pagination -->
    <div class="table-card bg-white border rounded-0 d-flex flex-column h-100 overflow-hidden shadow-sm" style="min-height: 0;">
      <!-- Table content wrapper -->
      <div
        class="table-content-wrapper"
        style="flex: 1 1 0; min-height: 0; overflow: auto"
      >
        <table
          class="table table-bordered align-middle mb-0 text-center"
          style="
            border-collapse: separate;
            border-spacing: 0;
            table-layout: auto;
            min-width: 100%;
          "
        >
          <!-- Sticky Header -->
          <thead class="sticky-top shadow-sm" style="z-index: 20">
            <tr>
              <th
                v-for="(col, colIdx) in columns"
                :key="col.key || colIdx"
                class="py-3 px-3 fw-bold text-nowrap text-center table-header-bg border-start border-end"
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
                class="py-3 px-3 fw-bold text-center table-header-bg sticky-col-actions border-start"
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
              class="border-bottom"
            >
              <td
                v-for="(col, colIdx) in columns"
                :key="col.key || colIdx"
                class="py-3 px-3 text-dark border-start border-end"
                :class="{
                  'table-cell-sticky-stt': colIdx === 0,
                }"
                :style="{
                  width: col.width || 'auto',
                  minWidth: col.minWidth || col.width || 'auto',
                  whiteSpace: 'normal',
                  overflow: 'hidden',
                  maxWidth: col.maxWidth || 'none',
                  textAlign: 'center'
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
                class="py-3 px-3 td-sticky-actions border-start"
                style="width: 160px; min-width: 160px"
              >
                <div class="d-flex justify-content-center align-items-center gap-2">
                  <slot name="actions" :row="item">
                    <el-tooltip content="Chi tiết" placement="top">
                      <button
                        class="btn-action-icon action-view square-action"
                        @click="$emit('view', item)"
                      >
                        <i class="bi bi-eye fs-6"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip content="Chỉnh sửa" placement="top">
                      <button
                        class="btn-action-icon action-edit square-action"
                        :disabled="item.trangThai === 0 || item.status === 'Ngừng hoạt động' || item.status === 'Đang bảo trì'"
                        @click="$emit('edit', item)"
                      >
                        <i class="bi bi-pencil fs-6"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip :content="item.trangThai === 1 || item.status === 'Đang hoạt động' ? 'Ngừng hoạt động' : 'Kích hoạt'" placement="top">
                      <el-switch
                        :model-value="item.trangThai === 1 || item.status === 'Đang hoạt động' || item.status === 'Hoạt động' || item.status === 'Sẵn sàng'"
                        @change="(val) => $emit('update-status', { row: item, val })"
                        class="status-switch mx-1"
                        inactive-color="#ff4949"
                      />
                    </el-tooltip>
                    <el-tooltip content="Xóa" placement="top">
                      <button
                        class="btn-action-icon action-delete square-action"
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
      <div v-if="!hidePagination && total > 0" class="pagination-section border-top px-3 py-3 bg-white">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
          <!-- Left side: Total results and Page Size Selector -->
          <div class="d-flex align-items-center gap-3">
            <span class="text-secondary small">Tổng cộng: <strong>{{ total }}</strong></span>
            <div class="d-flex align-items-center gap-2">
              <el-select
                v-model="pageSizeLocal"
                size="small"
                class="square-select"
                style="width: 120px"
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
              class="square-page-btn"
              :class="{ disabled: currentPage <= 1 }"
              @click="handlePageChange(currentPage - 1)"
            >
              <i class="bi bi-chevron-left"></i>
            </button>

            <button
              v-for="page in pagesToShow"
              :key="page"
              class="square-page-btn"
              :class="{ active: page === currentPage }"
              @click="handlePageChange(page)"
            >
              {{ page }}
            </button>

            <button
              class="square-page-btn"
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
  background-color: #f1f5f9;
}

/* Square Corners for Cards */
.filter-card, .table-card {
  border-radius: 0 !important;
  border: 1px solid #cbd5e1 !important;
}

.table-header-bg {
  background-color: #f8fafc !important;
  color: #334155 !important;
  font-weight: 700 !important;
  font-size: 14px !important;
  border-bottom: 2px solid #cbd5e1 !important;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Grid Lines */
.table-bordered td, .table-bordered th {
  border: 1px solid #e2e8f0 !important;
}

/* Center Content */
th, td {
  text-align: center !important;
  vertical-align: middle !important;
}

/* Square Buttons & Actions */
.square-btn {
  border-radius: 0 !important;
  height: 40px;
  width: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.square-action {
  border-radius: 0 !important;
  border: 1px solid #e2e8f0 !important;
  background: #fff;
  width: 34px;
  height: 34px;
}

.square-action:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.action-view { color: #3b82f6; }
.action-edit { color: #10b981; }
.action-delete { color: #ef4444; }

/* Square Page Buttons */
.square-page-btn {
  min-width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #475569;
  font-size: 14px;
  font-weight: 600;
  margin: 0 -1px; /* Overlap borders */
  cursor: pointer;
  transition: all 0.2s;
}

.square-page-btn:hover:not(.disabled):not(.active) {
  background: #f1f5f9;
  z-index: 1;
}

.square-page-btn.active {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
  z-index: 2;
}

.square-page-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
}

/* Select Styling */
:deep(.square-select .el-input__wrapper) {
  border-radius: 0 !important;
  box-shadow: none !important;
  border: 1px solid #e2e8f0 !important;
}

:deep(.square-select .el-input__wrapper:hover) {
  border-color: #cbd5e1 !important;
}

/* Scrollbar */
.table-content-wrapper {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

/* Sticky styles with grid consistency */
.sticky-col-stt, .table-cell-sticky-stt {
  position: sticky !important;
  left: 0;
  z-index: 10;
  background-color: #fff !important;
  border-right: 2px solid #cbd5e1 !important;
}

.sticky-col-actions, .td-sticky-actions {
  position: sticky !important;
  right: 0;
  z-index: 10;
  background-color: #fff !important;
  border-left: 2px solid #cbd5e1 !important;
}

.spinner-premium {
  width: 32px;
  height: 32px;
  border: 3px solid #f1f5f9;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 1s linear infinite;
}

@keyframes spin { 100% { transform: rotate(360deg); } }
</style>
