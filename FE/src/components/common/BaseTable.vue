<script setup>
import { computed } from 'vue';

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  columns: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  showCheckbox: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: true
  },
  // Pagination props
  total: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  }
});

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'edit', 'delete', 'selection-change', 'view']);

const totalPages = computed(() => Math.ceil(props.total / props.pageSize));

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    emit('update:currentPage', page);
  }
};

const handlePageSizeChange = (size) => {
  emit('update:pageSize', size);
  emit('update:currentPage', 1); // Reset to first page when changing size
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
</script>

<template>
  <div class="base-table-container h-100 d-flex flex-column overflow-hidden">
    <div class="table-content-wrapper flex-grow-1 overflow-auto no-scroll rounded-4 border bg-white shadow-sm">
      <table class="table table-hover align-middle mb-0 text-center w-100">
        <!-- Sticky Header -->
        <thead class="sticky-top shadow-sm" style="z-index: 10;">
          <tr class="border-bottom">
            <th v-if="showCheckbox" class="py-3 px-3 table-header-bg" style="width: 50px;">
              <div class="form-check d-flex justify-content-center">
                <input class="form-check-input" type="checkbox" @change="$emit('selection-change', $event)">
              </div>
            </th>
            <th v-for="col in columns" :key="col.key" class="py-3 px-3 fw-bold text-nowrap text-center table-header-bg" :style="{ width: col.width || 'auto' }">
              {{ col.label }}
            </th>
            <th v-if="showActions" class="py-3 px-3 fw-bold text-center table-header-bg" style="width: 140px;">Thao tác</th>
          </tr>
        </thead>
        <tbody v-if="!loading && data.length > 0">
          <tr v-for="(item, index) in data" :key="item.id || index" class="border-bottom border-light-subtle">
            <td v-if="showCheckbox" class="py-3 px-3">
              <div class="form-check d-flex justify-content-center">
                <input class="form-check-input" type="checkbox">
              </div>
            </td>
            <td v-for="col in columns" :key="col.key" class="py-3 px-3 text-secondary" style="font-size: 13px;">
              <slot :name="'cell-' + col.key" :row="item" :index="index">
                {{ item[col.key] }}
              </slot>
            </td>
            <td v-if="showActions" class="py-3 px-3">
              <div class="d-flex justify-content-center gap-2">
                <slot name="actions" :row="item">
                  <el-tooltip content="Chi tiết" placement="top">
                    <button class="btn-action-icon btn-action-view" @click="$emit('view', item)">
                      <i class="bi bi-eye fs-6"></i>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="Chỉnh sửa" placement="top">
                    <button class="btn-action-icon btn-action-edit" @click="$emit('edit', item)">
                      <i class="bi bi-pencil fs-6"></i>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="Xóa" placement="top">
                    <button class="btn-action-icon btn-action-delete" @click="$emit('delete', item)">
                      <i class="bi bi-trash fs-6"></i>
                    </button>
                  </el-tooltip>
                </slot>
              </div>
            </td>
          </tr>
        </tbody>
        <!-- Loading & Empty states remain same but with better padding/styling if needed -->
        <tbody v-else-if="loading">
          <tr>
            <td :colspan="columns.length + (showCheckbox ? 1 : 0) + (showActions ? 1 : 0)" class="py-5 border-0">
              <div class="spinner-premium"></div>
              <div class="mt-3 text-secondary small">Đang tải dữ liệu...</div>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr>
            <td :colspan="columns.length + (showCheckbox ? 1 : 0) + (showActions ? 1 : 0)" class="py-5 text-secondary opacity-50 border-0">
              <div class="mb-3">
                <i class="bi bi-inbox fs-1"></i>
              </div>
              <div class="fw-medium">Không có dữ liệu</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang cố định ở dưới -->
    <div class="table-footer-pagination d-flex justify-content-between align-items-center pt-3 px-2 w-100 flex-shrink-0">
      <div class="pagination-left d-flex align-items-center gap-3">
        <div class="total-badge highlight-total py-1 px-3 rounded-pill bg-light border">
          <span class="text-secondary small fw-bold">Tổng cộng:</span> 
          <span class="text-dark fw-bold ms-1" style="font-size: 14px;">{{ total }}</span>
        </div>

        <div class="d-flex align-items-center gap-2">
          <el-select 
            :model-value="pageSize" 
            size="small" 
            style="width: 100px"
            class="compact-select-simple"
            @update:model-value="handlePageSizeChange"
          >
            <el-option label="10 / trang" :value="10" />
            <el-option label="20 / trang" :value="20" />
            <el-option label="50 / trang" :value="50" />
          </el-select>
        </div>
      </div>

      <div class="pagination-right">
        <nav v-if="total > 0" aria-label="Table navigation">
          <ul class="pagination-simple mb-0 list-unstyled d-flex align-items-center gap-1">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button class="simple-page-btn arrow" @click="handlePageChange(currentPage - 1)">
                <i class="bi bi-chevron-left"></i>
              </button>
            </li>
            
            <li v-if="pagesToShow[0] > 1" class="page-item">
              <span class="simple-page-dots">...</span>
            </li>

            <li v-for="page in pagesToShow" :key="page" class="page-item">
              <button 
                class="simple-page-btn" 
                :class="{ active: currentPage === page }"
                @click="handlePageChange(page)"
              >
                {{ page }}
              </button>
            </li>

            <li v-if="pagesToShow[pagesToShow.length-1] < totalPages" class="page-item">
              <span class="simple-page-dots">...</span>
            </li>

            <li class="page-item" :class="{ disabled: currentPage === totalPages || total === 0 }">
              <button class="simple-page-btn arrow" @click="handlePageChange(currentPage + 1)">
                <i class="bi bi-chevron-right"></i>
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </div>
</template>

<style scoped>
.base-table-container {
  min-height: 0;
}

.table-content-wrapper {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

.table-header-bg {
    background-color: #f8fafc !important;
    color: #475569 !important;
    font-size: 12px;
    letter-spacing: 0.025em;
    text-transform: uppercase;
}

/* Simple Pagination Style like Image 4 */
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

.simple-page-dots {
    padding: 0 4px;
    color: #94a3b8;
}

.highlight-total {
    background-color: #f8fafc !important;
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

.text-indigo-500 { color: #4f46e5; }
.text-danger { color: #ef4444; }

/* Loading Spinner */
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
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

:deep(.compact-select-simple .el-input__wrapper) {
    border-radius: 6px !important;
    background-color: #f8fafc !important;
    box-shadow: none !important;
    border: 1px solid #e2e8f0 !important;
    height: 30px !important;
}

:deep(.compact-select-simple .el-input__wrapper.is-focus) {
    border-color: #4f46e5 !important;
}
</style>
