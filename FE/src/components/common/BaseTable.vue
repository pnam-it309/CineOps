<script setup>
import { computed, ref } from "vue";
import { Plus, Refresh } from '@element-plus/icons-vue';

const props = defineProps({
  data: {
    type: Array,
    required: true,
  },
  columns: {
    type: Array,
    default: () => [],
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
  subtitle: {
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
  hideTable: {
    type: Boolean,
    default: false,
  },
  hideHeader: {
    type: Boolean,
    default: false,
  },
  noPadding: {
    type: Boolean,
    default: false,
  }
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
    const newSelection = props.selection.filter(
      (i) => !props.data.some((d) => (d.id || d) === (i.id || i))
    );
    emit("update:selection", newSelection);
    emit("selection-change", newSelection);
  } else {
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
  <div class="base-table-container d-flex flex-column h-100" :class="{ 'p-4': !noPadding, 'p-0': noPadding }">
    <!-- Card 1: Title Card -->
    <div class="premium-card mb-3 p-4 px-5" v-if="title && !noPadding">
      <div class="d-flex align-items-center gap-3">
        <div class="header-icon-box-modern shadow-sm" style="width: 44px; height: 44px; font-size: 22px;">
          <i :class="titleIcon || 'bi bi-grid-fill'"></i>
        </div>
        <div>
          <h1 class="premium-page-title m-0" style="font-size: 22px; color: #1e293b; text-transform: none; letter-spacing: -0.5px;">{{ title }}</h1>
          <p v-if="subtitle" class="m-0 text-secondary mt-1 smaller" style="font-size: 13px; opacity: 0.8;">{{ subtitle }}</p>
          <p v-else class="m-0 text-secondary mt-1 smaller" style="font-size: 13px; opacity: 0.8;">Quản lý và theo dõi danh sách {{ title.toLowerCase() }}</p>
        </div>
      </div>
    </div>

    <!-- Card 2: Filter Card (Keep stretched content) -->
    <div class="premium-card mb-3 p-4 px-5" v-if="showFilters && $slots.filters && !noPadding">
      <div class="filter-stretch-wrapper">
        <div class="d-flex align-items-end justify-content-between flex-wrap gap-3">
          <div class="filters-main-area d-flex align-items-end gap-3 flex-wrap flex-grow-1">
            <slot name="filters"></slot>
          </div>
          
          <div class="filter-actions-area d-flex align-items-end gap-2 ms-auto">
            <el-tooltip content="Làm mới bộ lọc" placement="top">
              <button @click="$emit('reset-filter')" class="btn-cine-refresh-icon">
                <el-icon><Refresh /></el-icon>
              </button>
            </el-tooltip>
            <slot name="filter-extra-actions"></slot>
          </div>
        </div>
      </div>
    </div>

    <!-- Card 3: Table Card -->
    <div class="premium-card flex-grow-1 d-flex flex-column overflow-hidden" :class="{ 'mb-3': !noPadding }">
      <!-- Moved Actions to Table Header -->
      <div v-if="!hideHeader" class="p-3 px-5 border-bottom d-flex align-items-center justify-content-between bg-white sticky-top z-3">
        <h2 class="m-0 text-dark fw-bold text-uppercase smaller letter-spacing-1" style="font-size: 11px; opacity: 0.6;">Dữ liệu bảng</h2>
        <div class="d-flex align-items-center gap-2">
          <slot name="header-actions-left"></slot>
          <slot name="header-actions"></slot>
          <slot name="table-header-actions"></slot>
          <el-button v-if="addButtonLabel" @click="$emit('add-click')" type="primary" class="btn-premium-action-main shadow-sm px-4 ms-2">
            <el-icon class="me-2"><Plus /></el-icon>
            {{ addButtonLabel }}
          </el-button>
        </div>
      </div>

      <div class="table-scroll-area flex-grow-1 overflow-auto">
        <slot>
          <table class="table cine-premium-table align-middle m-0">
            <thead class="sticky-top">
              <tr>
                <th v-for="(col, colIdx) in columns" :key="col.key || colIdx" :style="{
                  width: col.width || 'auto',
                  minWidth: col.minWidth || col.width || 'auto',
                  textAlign: col.align || 'center'
                }" :class="{ 'text-center': true, 'sticky-col': colIdx === 0 }">
                  <template v-if="col.type === 'selection'">
                    <div class="form-check d-flex justify-content-center">
                      <input class="form-check-input" type="checkbox" :checked="isAllSelected"
                        @change="toggleSelectAll" />
                    </div>
                  </template>
                  <template v-else>
                    {{ col.label }}
                  </template>
                </th>
                <th v-if="showActions" class="text-center sticky-col-actions" style="width: 150px;">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody v-if="!loading && (data || []).length > 0">
              <tr v-for="(item, index) in data" :key="item.id || index">
                <td v-for="(col, colIdx) in columns" :key="col.key || colIdx" :style="{
                  width: col.width || 'auto',
                  minWidth: col.minWidth || col.width || 'auto',
                  textAlign: col.align || 'center'
                }" :class="{ 'text-center': true, 'sticky-col': colIdx === 0 }">
                  <template v-if="col.type === 'selection'">
                    <div class="form-check d-flex justify-content-center">
                      <input class="form-check-input" type="checkbox" :checked="isItemSelected(item)"
                        @change="toggleSelection(item)" />
                    </div>
                  </template>
                  <template v-else>
                    <slot :name="'cell-' + col.key" :row="item" :index="index">
                      {{ item[col.key] }}
                    </slot>
                  </template>
                </td>
                <td v-if="showActions" class="text-center sticky-col-actions">
                  <div class="d-flex justify-content-center gap-1">
                    <slot name="actions" :row="item">
                      <el-tooltip content="Xem chi tiết" placement="top">
                        <button class="btn-cine-action action-view" @click="$emit('view', item)">
                          <i class="bi bi-eye"></i>
                        </button>
                      </el-tooltip>
                      <el-tooltip content="Cập nhật" placement="top">
                        <button class="btn-cine-action action-edit" :disabled="item.trangThai === 0"
                          @click="$emit('edit', item)">
                          <i class="bi bi-pencil"></i>
                        </button>
                      </el-tooltip>
                      <el-tooltip content="Xóa" placement="top">
                        <button class="btn-cine-action action-delete" @click="$emit('delete', item)">
                          <i class="bi bi-trash"></i>
                        </button>
                      </el-tooltip>
                    </slot>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="loading">
              <tr>
                <td :colspan="(columns || []).length + (showActions ? 1 : 0)" class="py-5 text-center">
                  <div class="spinner-premium"></div>
                  <div class="mt-3 text-secondary small">Đang kết nối dữ liệu...</div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <td :colspan="(columns || []).length + (showActions ? 1 : 0)" class="py-5 text-center text-secondary">
                  <i class="bi bi-inbox fs-1 opacity-25"></i>
                  <div class="mt-2 fw-medium">Danh sách trống</div>
                </td>
              </tr>
            </tbody>
          </table>
        </slot>
      </div>
    </div>

    <!-- Card 4: Pagination Card (Separate) -->
    <div class="premium-card p-3" v-if="!hidePagination && total > 0">
      <div class="d-flex align-items-center justify-content-between flex-wrap w-100">
        <!-- Left Part: Result count & Page size -->
        <div class="pagination-left-info d-flex align-items-center gap-3" style="min-width: 220px;">
          <span class="text-secondary small">Hiện <strong>{{ total }}</strong> kết quả</span>
          <el-select v-model="pageSizeLocal" size="small" style="width: 110px;" class="premium-page-size-select">
            <el-option :value="5" label="5 / trang" />
            <el-option :value="10" label="10 / trang" />
            <el-option :value="20" label="20 / trang" />
            <el-option :value="50" label="50 / trang" />
          </el-select>
        </div>

        <!-- Center Part: Page numbers -->
        <div class="pagination-center-numbers flex-grow-1 d-flex justify-content-center my-2 my-md-0">
          <div class="pagination-premium d-flex align-items-center gap-1">
            <button class="btn-cine-page" :disabled="currentPage <= 1" @click="handlePageChange(currentPage - 1)">
              <i class="bi bi-chevron-left"></i>
            </button>
            <button v-for="page in pagesToShow" :key="page" class="btn-cine-page"
              :class="{ active: page === currentPage }" @click="handlePageChange(page)">
              {{ page }}
            </button>
            <button class="btn-cine-page" :disabled="currentPage >= totalPages"
              @click="handlePageChange(currentPage + 1)">
              <i class="bi bi-chevron-right"></i>
            </button>
          </div>
        </div>

        <!-- Right Part: Spacer for centering -->
        <div class="pagination-right-spacer d-none d-lg-block" style="min-width: 220px;"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Scoped styles are now minimal as they are moved to admin-tables.css */
.base-table-container {
  min-height: 100%;
}

/* Target specific layout needs not covered by global CSS */
.filter-box-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.sticky-top {
  border-bottom: 1px solid var(--border-color-light) !important;
}

/* Custom UI spacers */
.filter-stretch-wrapper {
  margin-top: 5px;
}

.filters-main-area :deep(.flex-grow-1) {
  flex-grow: 1 !important;
}

.filters-main-area :deep(.el-input),
.filters-main-area :deep(.el-select),
.filters-main-area :deep(.el-date-editor) {
  width: 100% !important;
  min-width: 160px;
}

/* Ensure filter items with labels stretch correctly */
.filters-main-area :deep(.d-flex.flex-column) {
  flex: 1 1 200px;
  max-width: 400px;
}

.letter-spacing-1 {
  letter-spacing: 0.05em;
}
</style>
