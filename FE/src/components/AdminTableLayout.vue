<template>
  <div
    class="admin-table-layout d-flex flex-column h-100"
    :class="{ 'pb-3': !disablePadding }"
  >
    <!-- 1. Page Header (Totally Outside) -->
    <div
      class="page-header-outside mb-3 flex-shrink-0 d-flex justify-content-between align-items-center"
    >
      <div class="d-flex align-items-center gap-2">
        <i :class="[titleIcon, 'fs-4 text-primary mt-1']" v-if="titleIcon"></i>
        <h2 class="fw-bold text-dark mb-0" style="font-size: 20px">
          {{ title }}
        </h2>
        <span
          v-if="subtitle"
          class="text-secondary small ms-2 opacity-75 border-start ps-2"
          >{{ subtitle }}</span
        >
      </div>
      <div class="d-flex align-items-center gap-2">
        <slot name="page-header-right"></slot>
      </div>
    </div>

    <!-- 2. Refined Filter Card (Collapsible content only) -->
    <div
      v-if="!hideFilter"
      class="filter-wrapper-card mb-3 flex-shrink-0 border rounded-4 bg-white shadow-sm overflow-hidden"
    >
      <!-- Header always visible -->
      <div
        class="filter-card-header d-flex justify-content-between align-items-center px-4 py-2 bg-light-subtle border-bottom"
        @click="showFilter = !showFilter"
        style="cursor: pointer"
      >
        <div class="d-flex align-items-center gap-2">
          <el-icon
            class="text-primary fw-bold filter-icon-animate"
            :class="{ 'rotate-180': !showFilter }"
            ><Filter
          /></el-icon>
          <span class="fw-bold filter-title">BỘ LỌC TÌM KIẾM</span>
        </div>
        <div class="d-flex align-items-center gap-2 filter-hint">
          <span>{{ showFilter ? "Nhấn để thu gọn" : "Nhấn để mở rộng" }}</span>
        </div>
      </div>

      <!-- Body collapses -->
      <el-collapse-transition>
        <div v-show="showFilter" class="filter-card-body-wrapper border-top">
          <div class="filter-card-body p-4">
            <div class="d-flex align-items-center gap-3 flex-wrap">
              <slot name="filters"></slot>
              <div class="ms-auto d-flex gap-2">
                <el-button
                  @click="$emit('reset-filter')"
                  :icon="Refresh"
                  plain
                  class="rounded-3"
                  >Đặt lại</el-button
                >
                <slot name="filter-right"></slot>
              </div>
            </div>
          </div>
        </div>
      </el-collapse-transition>
    </div>

    <!-- 3. Main Data Card (Actions + Table + Pagination) -->
    <div
      class="main-data-card bg-white border rounded-4 shadow-sm d-flex flex-column h-100 overflow-hidden"
      style="flex: 1 1 0; min-height: 0"
    >
      <!-- Action Buttons Bar -->
      <div
        class="actions-header-bar px-3 py-2 d-flex justify-content-between align-items-center flex-shrink-0 border-bottom"
      >
        <div class="d-flex align-items-center gap-2">
          <slot name="header-actions-left"></slot>
        </div>
        <div class="d-flex gap-2 align-items-center">
          <slot name="header-actions"></slot>
          <div
            class="v-separator mx-2"
            v-if="
              addButtonLabel &&
              ($slots.header_actions || $slots.header_actions_left)
            "
          ></div>
          <el-button
            v-if="addButtonLabel"
            @click="$emit('add-click')"
            type="primary"
            class="add-main-btn px-4 shadow-sm"
            rounded
          >
            <template #icon
              ><el-icon><Plus /></el-icon
            ></template>
            {{ addButtonLabel }}
          </el-button>
        </div>
      </div>

      <!-- Main Table Content -->
      <div class="table-viewport flex-grow-1" style="min-height: 0">
        <div v-if="$slots.content" class="h-100">
          <slot name="content"></slot>
        </div>
        <div v-else class="h-100">
          <el-table
            :data="data"
            v-loading="loading"
            stripe
            height="100%"
            class="modern-grid-table"
            style="width: 100%"
          >
            <slot name="columns"></slot>
            <template #empty>
              <div class="py-5 text-secondary opacity-50 text-center">
                <i class="bi bi-inbox fs-1 d-block mb-3"></i>
                <div class="fw-medium table-empty-text">
                  Không có dữ liệu hiển thị
                </div>
              </div>
            </template>
          </el-table>
        </div>
      </div>

      <!-- Integrated Pagination Footer -->
      <div
        v-if="!hidePagination && (total > 0 || totalPages > 0)"
        class="table-footer-pagination border-top px-4 py-2 bg-white flex-shrink-0"
      >
        <div class="pagination-footer-row">
          <div class="footer-left">
            <div class="d-flex align-items-center gap-3">
              <span class="pagination-info-text">
                Hiển thị {{ startRecord }}–{{ endRecord }} / {{ total }} bản ghi
              </span>
              <el-select
                :model-value="pageSize || 5"
                size="default"
                style="width: 160px"
                class="pagination-select"
                @update:model-value="handlePageSizeChange"
              >
                <el-option label="5 dòng / trang" :value="5" />
                <el-option label="10 dòng / trang" :value="10" />
                <el-option label="20 dòng / trang" :value="20" />
                <el-option label="50 dòng / trang" :value="50" />
              </el-select>
            </div>
          </div>

          <div class="footer-center">
            <nav v-if="totalPages > 0">
              <ul
                class="pagination-list-clean mb-0 list-unstyled d-flex align-items-center gap-1 justify-content-center"
              >
                <li>
                  <el-button
                    class="nav-btn-circle"
                    @click="handlePageChange(currentPage - 1)"
                    :disabled="currentPage <= 1"
                    circle
                  >
                    <el-icon><ArrowLeft /></el-icon>
                  </el-button>
                </li>
                <li v-for="page in pagesToShow" :key="page">
                  <el-button
                    class="nav-btn-page"
                    :class="{ 'is-active': currentPage === page }"
                    @click="handlePageChange(page)"
                  >
                    {{ page }}
                  </el-button>
                </li>
                <li>
                  <el-button
                    class="nav-btn-circle"
                    @click="handlePageChange(currentPage + 1)"
                    :disabled="currentPage >= totalPages"
                    circle
                  >
                    <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </li>
              </ul>
            </nav>
          </div>

          <div class="footer-right">
            <span class="pagination-page-text">
              Trang <strong class="text-dark">{{ currentPage || 1 }}</strong> /
              <strong class="text-dark">{{ totalPages || 1 }}</strong>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import {
  Plus,
  Refresh,
  Filter,
  Fold,
  CaretBottom,
  ArrowLeft,
  ArrowRight,
} from "@element-plus/icons-vue";

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
    default: false,
  },
  hideFilter: {
    type: Boolean,
    default: false,
  },
  disablePadding: {
    type: Boolean,
    default: false,
  },
});

const showFilter = ref(!props.hideFilter);

const emit = defineEmits([
  "add-click",
  "reset-filter",
  "update:currentPage",
  "update:pageSize",
  "selection-change",
]);

const totalPages = computed(() => {
  if (!props.pageSize || props.pageSize <= 0) return 0;
  return Math.ceil((props.total || 0) / props.pageSize);
});

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

const startRecord = computed(() => {
  if (!props.total) return 0;
  return (props.currentPage - 1) * props.pageSize + 1;
});

const endRecord = computed(() => {
  return Math.min(props.currentPage * props.pageSize, props.total);
});
</script>

<style scoped>
.admin-table-layout {
  background-color: transparent;
  overflow: hidden;
  padding-top: 8px !important;
}

/* Page Header */
.page-header-outside h2 {
  color: #1e293b;
  letter-spacing: -0.02em;
}

/* Filter Card */
.filter-wrapper-card {
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
  border-color: #e2e8f0;
}

.filter-card-header {
  background-color: #f8fafc !important;
  user-select: none;
  transition: background-color 0.2s;
}

.filter-card-header:hover {
  background-color: #f1f5f9 !important;
}

.filter-icon-animate {
  transition: transform 0.3s ease !important;
}

.rotate-180 {
  transform: rotate(180deg);
}

/* Table Card styling */
.main-data-card {
  border-color: #e2e8f0;
}

.actions-header-bar {
  background-color: #ffffff;
}

.add-main-btn {
  font-weight: 500;
  letter-spacing: 0.3px;
}

/* Professional Table Look - Clean & White */
:deep(.modern-grid-table) {
  --el-table-header-bg-color: #f8fafc;
  font-size: 13px;
}

/* Header */
:deep(.modern-grid-table th) {
  background-color: #f8fafc !important;
  color: #64748b !important;
  font-weight: 600 !important;
  height: 44px !important;
  font-size: 12px !important;
  letter-spacing: 0.3px;
  border-bottom: 1px solid #e2e8f0 !important;
}

:deep(.modern-grid-table td) {
  padding: 8px 6px !important;
  font-size: 13px;
  color: #334155;
}

:deep(.modern-grid-table tbody tr:hover td) {
  background-color: #f9fafb !important;
}

:deep(.modern-grid-table .el-table__row--striped td) {
  background-color: #fcfcfd;
}

/* Pagination Clean */
.table-footer-pagination {
  background-color: #ffffff;
}

.pagination-footer-row {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  width: 100%;
}

.footer-left {
  display: flex;
  justify-content: flex-start;
  color: #72777e;
}

.footer-center {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.footer-right {
  display: flex;
  justify-content: flex-end;
  font-size: 14px;
  font-weight: 600;
  color: #72777e;
}

.footer-right * {
  color: #72777e !important;
}

.nav-btn-circle {
  border: 1px solid #e2e8f0 !important;
  background: white !important;
  color: #64748b !important;
  width: 30px !important;
  height: 30px !important;
  display: flex !important;
  align-items: center;
  justify-content: center;
}

.nav-btn-circle:hover:not(:disabled) {
  color: #ff0000 !important;
  border-color: #ff0000 !important;
}

.nav-btn-page {
  font-weight: 600 !important;
  color: #64748b !important;
  min-width: 30px !important;
  height: 30px !important;
  font-size: 14px !important;
  border: 1px solid #e2e8f0 !important;
  background-color: white !important;
  border-radius: 8px !important;
  display: flex !important;
  align-items: center;
  justify-content: center;
  padding: 0 !important;
  transition: all 0.2s;
}

.nav-btn-page:hover {
  border-color: #ff0000 !important;
  color: #ff0000 !important;
}

.nav-btn-page.is-active {
  background-color: white !important;
  color: #ff0000 !important;
  border: 1.5px solid #ff0000 !important;
  box-shadow: 0 0 15px rgba(255, 0, 0, 0.2) !important;
  border-radius: 10px !important;
  font-weight: 700 !important;
}

:deep(.pagination-select .el-input__inner) {
  font-size: 14px !important;
  font-weight: 500 !important;
}

:deep(.pagination-select .el-select__wrapper) {
  font-size: 14px !important;
  font-weight: 500 !important;
  min-height: 30px !important;
}

.v-separator {
  width: 1px;
  height: 20px;
  background-color: #e2e8f0;
}

.rounded-3 {
  border-radius: 8px !important;
}</style>
