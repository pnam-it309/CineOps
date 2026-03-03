<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { adVeService } from '@/services/api/admin/ad-ve-service.js';
import { 
  Search, Plus, Monitor, Place, Refresh, Printer, Delete,
  Calendar, Timer, Download, Ticket, CircleCheck, CircleClose, Money
} from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import debounce from 'lodash/debounce';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';

// Data & State
const tickets = ref([]);
const totalElements = ref(0);
const loading = ref(false);
const statsData = reactive({ tongVe: 0, veHopLe: 0, veDaHuy: 0, doanhThu: 0 });

// Filters
const params = reactive({ 
  tuKhoa: '', 
  trangThai: null, 
  page: 1, // Đổi về 1 để khớp với AdminTableLayout
  size: 10 
});

// Load Data
const loadData = async () => {
  loading.value = true;
  try {
    // Chuyển đổi page về index 0 cho Backend
    const apiParams = { ...params, page: params.page - 1 };
    const res = await adVeService.timKiemVe(apiParams);
    tickets.value = res.data?.content || [];
    totalElements.value = res.data?.totalElements || 0;
  } catch (e) {
    ElMessage.error('Không thể kết nối Backend.');
  } finally { loading.value = false; }
};

const loadStats = async () => {
  try {
    const res = await adVeService.getThongKe();
    if (res.data) Object.assign(statsData, res.data);
  } catch (e) { console.error(e); }
};

// Handlers
const onSearch = debounce(() => { params.page = 1; loadData(); }, 500);
const resetFilter = () => { 
  params.tuKhoa = ''; 
  params.trangThai = null; 
  params.page = 1;
  loadData(); 
};

const confirmCancel = (id) => {
  ElMessageBox.confirm('Hủy vé sẽ hoàn lại ghế trống vào phòng. Bạn chắc chắn chứ?', 'Xác nhận hủy vé', {
    confirmButtonText: 'Đồng ý hủy',
    cancelButtonText: 'Quay lại',
    type: 'warning'
  }).then(async () => {
    try {
      await adVeService.huyVe(id);
      ElMessage.success('Đã hủy vé thành công');
      loadData(); loadStats();
    } catch (e) { ElMessage.error('Lỗi khi hủy vé'); }
  });
};

// Formatters
const formatPrice = (v) => new Intl.NumberFormat('vi-VN').format(v || 0);
const formatDate = (d) => d ? format(new Date(d), 'dd/MM/yyyy', { locale: vi }) : '---';
const formatTime = (d) => d ? format(new Date(d), 'HH:mm', { locale: vi }) : '---';

// Stats Configuration
const summaryStats = computed(() => [
  { label: 'TỔNG VÉ XUẤT', value: statsData.tongVe.toString(), icon: 'bi bi-ticket-detailed-fill', type: 'primary' },
  { label: 'VÉ HỢP LỆ', value: statsData.veHopLe.toString(), icon: 'bi bi-check-circle-fill', type: 'success' },
  { label: 'VÉ ĐÃ HỦY', value: statsData.veDaHuy.toString(), icon: 'bi bi-x-circle-fill', type: 'danger' },
  { label: 'DOANH THU', value: formatPrice(statsData.doanhThu) + 'đ', icon: 'bi bi-cash-stack', type: 'warning' },
]);

onMounted(() => { loadData(); loadStats(); });
</script>

<template>
  <div class="admin-ticket-page">
    <AdminTableLayout
      title="Quản Lý Giao Dịch Vé"
      titleIcon="bi bi-ticket-perforated-fill"
      :data="tickets"
      :total="totalElements"
      v-model:currentPage="params.page"
      v-model:pageSize="params.size"
      v-loading="loading"
      @change-page="loadData"
      @change-size="loadData"
      @reset-filter="resetFilter"
    >
      <template #header-actions-left>
        <el-button type="primary" :icon="Plus" @click="$router.push('/admin/pos')">
          Xuất vé mới
        </el-button>
        <el-button class="btn-premium-secondary text-success border-success-subtle" :icon="Download">
          Xuất Excel
        </el-button>
      </template>

      <template #stats>
        <div v-for="s in summaryStats" :key="s.label" class="col-md-3">
          <StatCard 
            :label="s.label" 
            :value="s.value" 
            :icon="s.icon"
            :type="s.type"
          />
        </div>
      </template>

      <template #filters>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="params.trangThai" @change="loadData" placeholder="Trạng thái" clearable>
  <el-option label="Tất cả trạng thái" value="" /> <el-option label="Thành công" :value="1" />
  <el-option label="Đã hủy" :value="0" />
</el-select>
        </div>
        <div class="filter-item flex-grow-1">
          <el-input 
            v-model="params.tuKhoa" 
            placeholder="Tìm mã vé, tên phim, SĐT khách hàng..." 
            :prefix-icon="Search" 
            clearable 
            @input="onSearch"
          />
        </div>
      </template>

      <template #columns>
        <el-table-column label="MÃ VÉ" width="150" fixed="left" align="center">
          <template #default="{ row }">
            <span class="fw-bold text-indigo-500">#{{ row.maVe }}</span>
          </template>
        </el-table-column>

        <el-table-column label="LOẠI KHÁCH" width="200" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.tenLoaiKhachHang" type="warning" size="small" effect="plain" round>
              {{ row.tenLoaiKhachHang }}
            </el-tag>
            <span v-else class="text-muted small">—</span>
          </template>
        </el-table-column>

        <el-table-column label="TÊN PHIM" min-width="220" align="center">
          <template #default="{ row }">
            <div class="fw-bold text-dark">{{ row.tenPhim || '—' }}</div>
          </template>
        </el-table-column>

        <el-table-column label="PHÒNG" width="120" align="center">
          <template #default="{ row }">
            <div class="text-muted small" style="white-space: nowrap;"><i class="bi bi-display me-1"></i>{{ row.tenPhongChieu || '---' }}</div>
          </template>
        </el-table-column>

        <el-table-column label="GHẾ" width="100" align="center">
          <template #default="{ row }">
            <div class="fw-bold text-danger" style="white-space: nowrap;">{{ row.viTriGhe || '—' }}</div>
          </template>
        </el-table-column>

        <el-table-column label="KÊNH BÁN" width="180" align="center">
          <template #default="{ row }">
            <el-tag :type="row.loaiVe === 0 ? 'info' : 'success'" size="small" effect="dark">
              {{ row.loaiVe === 0 ? '🏠 Tại quầy' : '🌐 Online' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="THANH TOÁN" width="220" align="center">
          <template #default="{ row }">
            <span class="fw-bold text-primary">{{ formatPrice(row.giaThanhToan) }}đ</span>
          </template>
        </el-table-column>

        <el-table-column label="TRẠNG THÁI" width="190" align="center">
          <template #default="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round effect="light">
              {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="THỜI GIAN" width="320" align="center">
          <template #default="{ row }">
            <div class="small text-secondary" style="white-space: nowrap;"><i class="bi bi-calendar3 me-1"></i>{{ formatDate(row.ngayTao) }} {{ formatTime(row.ngayTao) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="NGƯỜI TẠO" width="300" align="center">
          <template #default="{ row }">
            <div class="small text-muted" style="white-space: nowrap;"><i class="bi bi-person me-1"></i>{{ row.nguoiTao || '—' }}</div>
          </template>
        </el-table-column>

        <el-table-column label="THAO TÁC" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="In vé" placement="top">
                <button class="btn-action-icon btn-action-print" @click="">
                  <i class="bi bi-printer"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Hủy vé" placement="top" v-if="row.trangThai === 1">
                <button class="btn-action-icon btn-action-delete" @click="confirmCancel(row.id)">
                  <i class="bi bi-trash"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>
  </div>
</template>

<style scoped>
.text-indigo-500 { color: #4f46e5; }
.admin-ticket-page { padding: 0; }

/* Action buttons — đồng bộ với BaseTable.vue */
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
  font-size: 16px;
}
.btn-action-icon:hover { background-color: #f1f5f9; }
.btn-action-print { color: #3b82f6; }
.btn-action-print:hover { color: #1d4ed8; background-color: #eff6ff !important; }
.btn-action-delete { color: #ef4444; }
.btn-action-delete:hover { color: #b91c1c; background-color: #fef2f2 !important; }
</style>