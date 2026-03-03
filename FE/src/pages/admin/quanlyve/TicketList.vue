<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { adVeService } from '@/services/api/admin/ad-ve-service.js';
import { Search, Plus, Printer, Delete, Download, Phone } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import debounce from 'lodash/debounce';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';

// --- TRẠNG THÁI DỮ LIỆU ---
const tickets = ref([]);
const totalElements = ref(0);
const loading = ref(false);
const statsData = reactive({ tongVe: 0, veHopLe: 0, veDaHuy: 0, doanhThu: 0 });
const ticketToPrint = ref(null); // Thông tin vé đơn lẻ để in

const params = reactive({ 
  tuKhoa: '', 
  trangThai: null, 
  page: 1, 
  size: 10 
});

// --- HÀM TẢI DỮ LIỆU ---
const loadData = async () => {
  loading.value = true;
  try {
    // 1. Loại bỏ các tham số null/rỗng để tránh lỗi 400
    const cleanParams = Object.fromEntries(
      Object.entries({ ...params, page: params.page - 1 })
        .filter(([_, v]) => v !== null && v !== '')
    );
    
    const res = await adVeService.timKiemVe(cleanParams);
    tickets.value = res.data?.content || [];
    totalElements.value = res.data?.totalElements || 0;
  } catch (e) {
    ElMessage.error('Lỗi kết nối máy chủ hoặc tham số không hợp lệ.');
  } finally { loading.value = false; }
};

const loadStats = async () => {
  try {
    const res = await adVeService.getThongKe();
    if (res.data) Object.assign(statsData, res.data);
  } catch (e) { console.error('Không thể tải thống kê:', e); }
};

// --- XỬ LÝ SỰ KIỆN ---
const onSearch = debounce(() => { params.page = 1; loadData(); }, 500);

const resetFilter = () => { 
  params.tuKhoa = ''; 
  params.trangThai = null; 
  params.page = 1;
  loadData(); 
};

// Logic in vé cô lập hoàn toàn
const handlePrint = async (id) => {
  loading.value = true;
  try {
    const res = await adVeService.getChiTietVe(id);
    if (res.data) {
      ticketToPrint.value = res.data;
      // Đợi Vue render xong dữ liệu mới gọi lệnh in
      setTimeout(() => {
        window.print();
        ticketToPrint.value = null; // Xóa dữ liệu in sau khi đóng hộp thoại
      }, 500); 
    }
  } catch (e) {
    ElMessage.error('Không thể lấy dữ liệu vé để in');
  } finally { loading.value = false; }
};

const confirmCancel = (id) => {
  ElMessageBox.confirm('Hủy vé sẽ hoàn lại ghế vào phòng chiếu. Bạn chắc chắn chứ?', 'Xác nhận hủy', {
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Quay lại',
    type: 'warning'
  }).then(async () => {
    try {
      await adVeService.huyVe(id);
      ElMessage.success('Đã hủy vé thành công');
      loadData(); loadStats();
    } catch (e) { ElMessage.error('Lỗi khi thực hiện hủy vé'); }
  });
};

// --- ĐỊNH DẠNG HIỂN THỊ ---
const formatPrice = (v) => new Intl.NumberFormat('vi-VN').format(v || 0);
const formatDate = (d) => d ? format(new Date(d), 'dd/MM/yyyy', { locale: vi }) : '---';
const formatTime = (d) => d ? format(new Date(d), 'HH:mm', { locale: vi }) : '---';

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
      subtitle="Theo dõi toàn bộ hóa đơn từ hệ thống CineOps"
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
        <el-button type="primary" :icon="Plus" @click="$router.push('/admin/pos')">Xuất vé mới</el-button>
        <el-button class="btn-premium-secondary" :icon="Download">Xuất Excel</el-button>
      </template>

      <template #stats>
        <div v-for="s in summaryStats" :key="s.label" class="col-md-3">
          <StatCard :label="s.label" :value="s.value" :icon="s.icon" :type="s.type" />
        </div>
      </template>

      <template #filters>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="params.trangThai" @change="loadData" placeholder="Trạng thái" clearable>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Thành công" :value="1" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>
        <div class="filter-item flex-grow-1">
          <el-input v-model="params.tuKhoa" placeholder="Tìm mã vé, tên phim, SĐT..." :prefix-icon="Search" clearable @input="onSearch" />
        </div>
      </template>

      <template #columns>
        <el-table-column label="MÃ VÉ" width="130">
          <template #default="{ row }">
            <span class="ticket-code">#{{ row.maVe }}</span>
          </template>
        </el-table-column>

        <el-table-column label="KHÁCH HÀNG" min-width="180">
          <template #default="{ row }">
            <div class="customer-name">{{ row.tenKhachHang || 'Khách lẻ' }}</div>
            <div class="customer-phone-info" v-if="row.soDienThoai">
              <el-icon><Phone /></el-icon> <span>{{ row.soDienThoai }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="PHIM & PHÒNG" min-width="220">
          <template #default="{ row }">
            <div class="movie-title-row">{{ row.tenPhim }}</div>
            <div class="room-details">
              <i class="bi bi-display me-1"></i>{{ row.tenPhongChieu }}
              <span class="seat-badge">Ghế: {{ row.viTriGhe }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="GIÁ VÉ" width="130" align="center">
          <template #default="{ row }">
            <span class="price-text">{{ formatPrice(row.giaThanhToan) }}đ</span>
          </template>
        </el-table-column>

        <el-table-column label="TRẠNG THÁI" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round effect="light">
              {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="THỜI GIAN" width="160" align="center">
          <template #default="{ row }">
            <div class="time-stamp">{{ formatDate(row.ngayTao) }}</div>
            <div class="time-stamp-sub">{{ formatTime(row.ngayTao) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="THAO TÁC" width="110" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" :icon="Printer" plain circle @click="handlePrint(row.id)" />
              <el-button v-if="row.trangThai === 1" type="danger" size="small" :icon="Delete" circle @click="confirmCancel(row.id)" />
            </div>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>

    <div id="print-section" v-if="ticketToPrint">
      <div class="ticket-box">
        <h2 class="cinema-title">CINEOPS THEATER</h2>
        <div class="print-divider">---------------------------</div>
        <div class="print-row"><strong>MÃ VÉ:</strong> {{ ticketToPrint.maVe }}</div>
        <div class="print-row"><strong>PHIM:</strong> {{ ticketToPrint.tenPhim }}</div>
        <div class="print-row"><strong>PHÒNG:</strong> {{ ticketToPrint.tenPhongChieu }}</div>
        <div class="print-row"><strong>GHẾ:</strong> {{ ticketToPrint.viTriGhe }}</div>
        <div class="print-row"><strong>GIÁ:</strong> {{ formatPrice(ticketToPrint.giaThanhToan) }}đ</div>
        <div class="print-divider">---------------------------</div>
        <p class="print-footer">Chúc bạn xem phim vui vẻ!</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* --- 1. CSS GIAO DIỆN WEB --- */
.admin-ticket-page { padding: 24px; background-color: #f8fafc; min-height: 100vh; }
.ticket-code { color: #6366f1; font-family: 'JetBrains Mono', monospace; font-weight: 700; background: #eff6ff; padding: 4px 10px; border-radius: 6px; border: 1px solid #dbeafe; }
.customer-name { color: #0f172a !important; font-weight: 700; }
.customer-phone-info { display: flex; align-items: center; color: #64748b; font-size: 0.85rem; margin-top: 2px; }
.movie-title-row { color: #1e293b !important; font-weight: 700; }
.room-details { color: #64748b; font-size: 0.85rem; display: flex; align-items: center; margin-top: 4px; }
.seat-badge { margin-left: 8px; color: #ef4444 !important; background: #fef2f2; padding: 1px 6px; border-radius: 4px; border: 1px solid #fee2e2; }
.price-text { color: #4f46e5; font-weight: 700; }
.time-stamp { font-size: 0.85rem; color: #475569; }
.time-stamp-sub { font-size: 0.8rem; color: #94a3b8; }

/* --- 2. CSS ĐẶC TRỊ LỖI IN (CHUẨN CGV) --- */
@media screen {
  #print-section { display: none; }
}

@media print {
  /* ẨN TUYỆT ĐỐI Dashboard và các phần tử web */
  body > *:not(#print-section),
  #app > *:not(#print-section),
  .admin-ticket-page > *:not(#print-section),
  .sidebar-container, .el-aside, .el-header, .navbar {
    display: none !important;
  }

  html, body { background: #fff !important; margin: 0 !important; padding: 0 !important; }

  /* ĐƯA VÙNG IN LÊN VỊ TRÍ ĐẦU TRANG */
  #print-section {
    display: block !important;
    position: absolute;
    top: 0; left: 0; width: 100%;
    background: #fff;
  }

  .ticket-box {
    width: 80mm; margin: 0 auto; padding: 10mm 2mm;
    text-align: center; color: #000;
    font-family: 'Courier New', Courier, monospace;
  }

  .cinema-title { font-size: 20pt; font-weight: bold; margin-bottom: 5pt; }
  .print-divider { border-top: 1pt dashed #000; margin: 8pt 0; width: 100%; }
  .print-row { text-align: left; font-size: 11pt; margin: 5pt 0; line-height: 1.4; }
  .print-footer { margin-top: 15pt; font-style: italic; font-size: 10pt; }
}
</style>