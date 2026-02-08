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
        <thead class="bg-primary-subtle text-secondary small text-uppercase sticky-top shadow-sm" style="z-index: 10;">
          <tr class="border-bottom border-light">
            <th v-if="showCheckbox" class="py-3 px-2 bg-primary-subtle" style="width: 50px;">
              <div class="form-check d-flex justify-content-center">
                <input class="form-check-input" type="checkbox" @change="$emit('selection-change', $event)">
              </div>
            </th>
            <th v-for="col in columns" :key="col.key" class="py-3 px-2 fw-semibold text-nowrap text-center bg-primary-subtle" :style="{ width: col.width || 'auto', color: '#334155' }">
              {{ col.label }}
            </th>
            <th v-if="showActions" class="py-3 px-2 fw-semibold text-center bg-primary-subtle" style="width: 120px; color: #334155">Thao tác</th>
          </tr>
        </thead>
        <tbody v-if="!loading && data.length > 0">
          <tr v-for="(item, index) in data" :key="item.id || index" class="border-bottom border-light-subtle">
            <td v-if="showCheckbox" class="py-3 px-2">
              <div class="form-check d-flex justify-content-center">
                <input class="form-check-input" type="checkbox">
              </div>
            </td>
            <td v-for="col in columns" :key="col.key" class="py-3 px-2 text-secondary small">
              <slot :name="'cell-' + col.key" :row="item">
                {{ item[col.key] }}
              </slot>
            </td>
            <td v-if="showActions" class="py-3 px-2">
              <div class="d-flex justify-content-center gap-2">
                <slot name="actions" :row="item">
                  <button class="btn btn-sm btn-link p-1 border-0 text-secondary opacity-75" @click="$emit('edit', item)">
                    <i class="bi bi-pencil fs-6"></i>
                  </button>
                  <button class="btn btn-sm btn-link p-1 border-0 text-danger opacity-75" @click="$emit('delete', item)">
                    <i class="bi bi-trash fs-6"></i>
                  </button>
                </slot>
              </div>
            </td>
          </tr>
        </tbody>
        <tbody v-else-if="loading">
          <tr>
            <td :colspan="columns.length + (showCheckbox ? 1 : 0) + (showActions ? 1 : 0)" class="py-5 border-0">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr>
            <td :colspan="columns.length + (showCheckbox ? 1 : 0) + (showActions ? 1 : 0)" class="py-5 text-secondary opacity-50 border-0">
              <i class="bi bi-inbox fs-1 d-block mb-2"></i>
              Không có dữ liệu
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang cố định ở dưới -->
    <div class="table-footer-pagination d-flex justify-content-between align-items-center pt-3 px-2 w-100 flex-shrink-0 flex-wrap gap-2">
      <div class="d-flex align-items-center gap-3">
        <nav v-if="total > pageSize" aria-label="Table navigation">
          <ul class="pagination pagination-sm mb-0 list-unstyled d-flex">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button class="page-link rounded-3 border-light-subtle mx-1 px-3 shadow-none" @click="handlePageChange(currentPage - 1)">
                <i class="bi bi-chevron-left"></i>
              </button>
            </li>
            
            <li v-if="pagesToShow[0] > 1" class="page-item disabled">
              <span class="page-link border-0">...</span>
            </li>

            <li v-for="page in pagesToShow" :key="page" class="page-item" :class="{ active: currentPage === page }">
              <button 
                class="page-link rounded-3 mx-1 px-3 shadow-none fw-bold" 
                :class="currentPage === page ? 'bg-primary text-white border-primary' : 'bg-white text-secondary border-light-subtle'"
                @click="handlePageChange(page)"
              >
                {{ page }}
              </button>
            </li>

            <li v-if="pagesToShow[pagesToShow.length-1] < totalPages" class="page-item disabled">
              <span class="page-link border-0">...</span>
            </li>

            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <button class="page-link rounded-3 border-light-subtle mx-1 px-3 shadow-none" @click="handlePageChange(currentPage + 1)">
                <i class="bi bi-chevron-right"></i>
              </button>
            </li>
          </ul>
        </nav>

        <div class="text-secondary small fw-medium opacity-75">
          <span class="d-none d-sm-inline">Hiển thị</span> 
          <span class="text-dark fw-bold mx-1">{{ total === 0 ? 0 : (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, total) }}</span> 
          trên <span class="text-dark fw-bold mx-1">{{ total }}</span> kết quả
        </div>
      </div>

      <div class="d-flex align-items-center gap-2">
        <span class="text-secondary small">Kích thước:</span>
        <el-select 
          :model-value="pageSize" 
          size="small" 
          style="width: 100px"
          @update:model-value="handlePageSizeChange"
        >
          <el-option label="5 dòng" :value="5" />
          <el-option label="10 dòng" :value="10" />
          <el-option label="15 dòng" :value="15" />
          <el-option label="20 dòng" :value="20" />
        </el-select>
      </div>
    </div>
  </div>
</template>

<style scoped>
.base-table-container {
  min-height: 0; /* Quan trọng để flex-grow hoạt động đúng */
}

.table-content-wrapper {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.table-content-wrapper::-webkit-scrollbar {
  display: none;
}

.sticky-top {
  top: 0;
  background-color: #f8fafc; /* bg-primary-subtle tương ứng */
}

.form-check-input {
  cursor: pointer;
}

.page-link {
  transition: all 0.2s;
  background-color: white;
}

.page-link:hover:not(.active) {
  background-color: var(--bs-gray-100);
}

/* Đảm bảo border radius của container không bị nội dung đè lên khi scroll */
.table-content-wrapper {
  position: relative;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px !important;
}
</style>
