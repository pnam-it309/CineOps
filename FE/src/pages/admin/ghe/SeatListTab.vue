<template>
  <div class="seat-table-wrapper h-100">
    <BaseTable
      :data="paginatedSeats"
      :columns="seatColumns"
      :loading="loading"
      :hide-pagination="true"
      :selection="selectedSeats"
      @selection-change="handleSelectionChange"
      @view="(row) => $emit('view', row)"
      @edit="(row) => $emit('open-dialog', row)"
    >
      <!-- Selection & Index handled by BaseTable if configured, 
           but we'll keep it explicit via columns if needed. 
           In our BaseTable, selection is a special type. -->

      <template #cell-soGhe="{ row }">
        <span class="seat-pill-modern" :class="getSeatTypeTag(row.tenLoaiGhe)">{{ row.soGhe }}</span>
      </template>

      <template #cell-soHang="{ row }">
        <span class="text-dark fw-medium">Hàng {{ row.soHang }}</span>
      </template>

      <template #cell-soCot="{ row }">
        <span class="text-dark">Cột {{ row.soCot }}</span>
      </template>

      <template #cell-phongChieu="{ row }">
        <div class="badge-room py-1 px-3 rounded-pill bg-light text-dark border small fw-bold">
           <i class="bi bi-door-open me-1 text-primary"></i>
           {{ phongChieuList.find(pc => pc.id === row.idPhongChieu)?.tenPhong || '—' }}
        </div>
      </template>

      <template #cell-tenLoaiGhe="{ row }">
        <el-tag :type="getSeatTypeTag(row.tenLoaiGhe)" round size="small" effect="plain">{{ row.tenLoaiGhe }}</el-tag>
      </template>

      <template #cell-phuPhi="{ row }">
        <span v-if="row.phuPhi > 0" class="text-primary fw-bold small text-nowrap">{{ formatCurrency(row.phuPhi) }}</span>
        <span v-else class="text-secondary opacity-50">—</span>
      </template>

      <template #cell-trangThai="{ row }">
        <el-tag :type="row.trangThai === 1 ? 'success' : 'info'" round size="small">
          {{ row.trangThai === 1 ? 'Hoạt động' : 'Bảo trì' }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center align-items-center gap-1">
          <el-tooltip content="Chỉnh sửa ghế" placement="top">
            <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click.stop="$emit('open-dialog', row)">
              <i class="bi bi-pencil"></i>
            </button>
          </el-tooltip>
          <el-switch 
            :model-value="row.trangThai === 1" 
            class="status-switch mx-1"
            inactive-color="#ff4949"
            @change="(val) => $emit('update-status', { row, status: val ? 1 : 0 })"
            @click.stop
          />
        </div>
      </template>

      <template #empty>
        <div class="py-5 text-secondary opacity-50">
          <i class="bi bi-inbox fs-1 d-block mb-3"></i>
          <div class="fw-medium">Không tìm thấy ghế nào</div>
        </div>
      </template>
    </BaseTable>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import BaseTable from '@/components/common/BaseTable.vue';

const props = defineProps({
  seats: Array,
  loaiGheList: Array,
  phongChieuList: Array,
  loading: Boolean,
  searchQuery: String,
  filterLoaiGhe: [String, Number],
  currentPage: Number,
  pageSize: Number,
  selectedSeats: Array
});

const seatColumns = [
  { type: 'selection', width: '50px' },
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'Số ghế', key: 'soGhe', width: '100px' },
  { label: 'Hàng', key: 'soHang', width: '100px' },
  { label: 'Cột', key: 'soCot', width: '100px' },
  { label: 'Phòng', key: 'phongChieu', width: '140px' },
  { label: 'Loại ghế', key: 'tenLoaiGhe', width: '130px' },
  { label: 'Phụ phí', key: 'phuPhi', width: '140px' },
  { label: 'Trạng thái', key: 'trangThai', width: '140px' },
];

const emit = defineEmits(['update:selectedSeats', 'open-dialog', 'update-status']);

const filteredSeats = computed(() => {
  return props.seats.filter(s => {
    const matchLoai = props.filterLoaiGhe === '' || s.idLoaiGhe === props.filterLoaiGhe;
    const matchSearch = !props.searchQuery || s.soGhe?.toLowerCase().includes(props.searchQuery.toLowerCase());
    return matchLoai && matchSearch;
  });
});

const paginatedSeats = computed(() => {
  const start = (props.currentPage - 1) * props.pageSize;
  const end = start + props.pageSize;
  return filteredSeats.value.slice(start, end).map((item, index) => ({
    ...item,
    stt: start + index + 1
  }));
});

const handleSelectionChange = (val) => {
  emit('update:selectedSeats', val);
};

const getSeatTypeTag = (tenLoai) => {
  if (!tenLoai) return 'info';
  const l = tenLoai.toLowerCase();
  if (l.includes('vip')) return 'warning';
  if (l.includes('couple') || l.includes('sweetbox')) return 'danger';
  return 'info';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);
</script>

<style scoped>
.seat-table-wrapper {
  height: 100%;
}

.seat-pill-modern {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 13px;
  background: #f8fafc;
  color: #1e293b;
  border: 1.5px solid #e2e8f0;
  min-width: 45px;
}

.seat-pill-modern.warning { background: #fffbeb; border-color: #f59e0b; color: #b45309; }
.seat-pill-modern.danger { background: #fef2f2; border-color: #ef4444; color: #b91c1c; }

.status-badge-hover {
  cursor: pointer;
  transition: all 0.2s;
}

.status-badge-hover:hover {
  transform: scale(1.05);
  filter: brightness(0.95);
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
  cursor: pointer;
  color: #64748b;
}

.btn-action-icon:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.action-edit:hover { color: #4f46e5; background: #eef2ff; }
.action-refresh:hover { color: #10b981; background: #ecfdf5; }
.action-view:hover { color: #3b82f6; background: #eff6ff; }

/* Custom styles for status switch */
.status-switch {
  --el-switch-on-color: #13ce66;
  --el-switch-off-color: #ff4949;
}
.status-switch :deep(.el-switch__label) {
  display: none !important;
}
</style>
