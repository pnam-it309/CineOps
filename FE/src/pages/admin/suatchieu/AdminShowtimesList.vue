<template>
  <div class="list-view-container animation-fade-in h-100 d-flex flex-column flex-grow-1 w-100">
    <BaseTable :data="paginatedData" :columns="columns" :loading="loading"
      :total="total" v-model:currentPage="localCurrentPage" v-model:pageSize="localPageSize"
      v-model:selection="localSelection" :hide-pagination="true" @edit="$emit('edit', $event)" @delete="$emit('delete', $event)">
      
      <template #cell-index="{ index }">
        <span class="text-secondary small">{{ (localCurrentPage - 1) * localPageSize + index + 1 }}</span>
      </template>

      <template #cell-phim="{ row }">
        <div class="d-flex align-items-center justify-content-center gap-2">
          <img v-if="row.poster" :src="row.poster" class="rounded shadow-sm"
            style="width: 32px; height: 44px; object-fit: cover;" :alt="row.tenPhim" />
          <div v-else class="rounded d-flex align-items-center justify-content-center bg-light border shadow-sm"
            style="width: 32px; height: 44px;">
            <i class="bi bi-film text-secondary"></i>
          </div>
          <div class="fw-semibold small text-dark text-start" style="line-height: 1.2;">{{ row.tenPhim || '—' }}</div>
        </div>
      </template>

      <template #cell-phongChieu="{ row }">
        <div class="small fw-semibold text-dark"><i class="bi bi-door-open me-1 text-primary"></i>{{ row.tenPhongChieu }}</div>
      </template>

      <template #cell-ngayChieu="{ row }">
        <span class="small fw-semibold text-dark">{{ formatDate(row.ngayChieu) }}</span>
      </template>

      <template #cell-gioChieu="{ row }">
        <div class="small d-flex align-items-center justify-content-center gap-1">
          <el-tag type="info" effect="plain" size="small" class="fw-bold" style="font-size: 11px;">
            {{ row.gioBatDau }}
          </el-tag>
          <span class="text-secondary">→</span>
          <el-tag type="info" effect="plain" size="small" style="font-size: 11px;">{{ row.gioKetThuc }}</el-tag>
        </div>
      </template>

      <template #cell-soGheTrong="{ row }">
        <el-tag :type="row.soGheTrong > 0 ? 'success' : 'danger'" effect="light" size="small" class="fw-bold">
          {{ row.soGheTrong }}
        </el-tag>
      </template>

      <template #cell-trangThai="{ row }">
        <el-tag :type="getStatusTag(row.trangThai)" round size="small">
          {{ getStatusLabel(row.trangThai) }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center gap-1">
          <el-tooltip content="Chi tiết" placement="top">
            <button class="btn-action-icon btn-action-view" @click="$emit('view', row)">
              <i class="bi bi-eye"></i>
            </button>
          </el-tooltip>
          <el-tooltip content="Chỉnh sửa" placement="top">
            <button class="btn-action-icon btn-action-edit" @click="$emit('edit', row)">
              <i class="bi bi-pencil"></i>
            </button>
          </el-tooltip>
          <el-tooltip :content="row.trangThai === 0 ? 'Kích hoạt lại' : 'Hủy suất'" placement="top">
            <button class="btn-action-icon btn-action-refresh" @click="$emit('delete', row)">
              <i class="bi bi-arrow-repeat"></i>
            </button>
          </el-tooltip>
        </div>
      </template>
    </BaseTable>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import BaseTable from '@/components/common/BaseTable.vue';

const props = defineProps({
  data: Array,
  loading: Boolean,
  total: Number,
  currentPage: Number,
  pageSize: Number,
  selection: Array
});

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'update:selection', 'edit', 'delete', 'view']);

const localCurrentPage = computed({
  get: () => props.currentPage,
  set: (val) => emit('update:currentPage', val)
});

const localPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
});

const localSelection = computed({
  get: () => props.selection,
  set: (val) => emit('update:selection', val)
});

const columns = [
  { label: 'STT', key: 'index', width: '60px' },
  { label: 'PHIM', key: 'phim', minWidth: '250px' },
  { label: 'PHÒNG CHIẾU', key: 'phongChieu', width: '200px' },
  { label: 'NGÀY CHIẾU', key: 'ngayChieu', width: '150px' },
  { label: 'GIỜ CHIẾU', key: 'gioChieu', width: '200px' },
  { label: 'TRỐNG', key: 'soGheTrong', width: '100px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '150px' },
];

const paginatedData = computed(() => {
  const start = (localCurrentPage.value - 1) * localPageSize.value;
  const end = start + localPageSize.value;
  return props.data.slice(start, end);
});

const getStatusTag = (status) => {
  const map = { 0: 'danger', 1: 'primary', 2: 'success', 3: 'info' };
  return map[status] || 'info';
};

const getStatusLabel = (status) => {
  const map = { 0: 'Đã hủy', 1: 'Sắp chiếu', 2: 'Đang chiếu', 3: 'Kết thúc' };
  return map[status] || 'Không xác định';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};
</script>
