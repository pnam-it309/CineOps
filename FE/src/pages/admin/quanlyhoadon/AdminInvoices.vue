<template>
  <div class="admin-invoices-main-wrapper">
    <div class="admin-invoices-container">
      <el-card class="filter-card" shadow="never">
        <div class="filter-header">
          <h3><i class="bi bi-receipt-cutoff me-2"></i>Quản Lý Hóa Đơn</h3>
        </div>
        
        <el-row :gutter="15" class="filter-row">
          <el-col :span="6">
            <el-input 
              v-model="listQuery.tuKhoa" 
              placeholder="Mã HD, Khách hàng, Nhân viên..." 
              clearable 
              @clear="handleFilter"
              @keyup.enter="handleFilter"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-col>
          
          <el-col :span="4">
            <el-select v-model="listQuery.trangThai" placeholder="Trạng thái" clearable @change="handleFilter" style="width: 100%">
              <el-option label="Thành công" :value="1" />
              <el-option label="Đã hủy" :value="0" />
            </el-select>
          </el-col>

          <el-col :span="4">
            <el-select v-model="listQuery.kenhBanHang" placeholder="Kênh bán" clearable @change="handleFilter" style="width: 100%">
              <el-option label="🏠 Tại quầy (POS)" :value="0" />
              <el-option label="🌐 Trực tuyến (Web)" :value="1" />
            </el-select>
          </el-col>

          <el-col :span="10">
            <el-radio-group v-model="listQuery.kyThoiGian" @change="handleFilter">
              <el-radio-button value="">Tất cả</el-radio-button>
              <el-radio-button value="TODAY">Hôm nay</el-radio-button>
              <el-radio-button value="THIS_WEEK">Tuần này</el-radio-button>
              <el-radio-button value="THIS_MONTH">Tháng này</el-radio-button>
            </el-radio-group>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="table-card mt-3">
        <el-table v-loading="loading" :data="invoices" border stripe style="width: 100%">
          <el-table-column type="index" label="STT" width="60" align="center" />
          
          <el-table-column prop="maHoaDon" label="Mã HĐ" width="130">
            <template #default="{ row }">
              <span class="fw-bold text-primary">#{{ row.maHoaDon }}</span>
            </template>
          </el-table-column>

          <el-table-column label="Khách hàng" min-width="180">
            <template #default="{ row }">
              <div class="fw-bold">{{ row.tenKhachHang || 'Khách lẻ' }}</div>
              <div v-if="row.soDienThoai" class="text-muted small">
                <i class="bi bi-telephone me-1"></i>{{ row.soDienThoai }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="Nhân viên / Kênh" min-width="150">
            <template #default="{ row }">
              <div class="small">{{ row.tenNhanVien || 'Hệ thống' }}</div>
              <el-tag :type="row.kenhBanHang === 0 ? 'info' : 'success'" size="small" class="mt-1">
                {{ row.kenhBanHang === 0 ? '🏠 POS' : '🌐 Web' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="Tổng tiền" align="right" width="140">
            <template #default="{ row }">
              <strong class="text-danger">{{ formatCurrency(row.tongTienThanhToan) }}</strong>
            </template>
          </el-table-column>

          <el-table-column label="Trạng thái" align="center" width="120">
            <template #default="{ row }">
              <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" round>
                {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="Thời gian" width="160" align="center">
            <template #default="{ row }">
              <div class="small">{{ formatDate(row.ngayTao) }}</div>
            </template>
          </el-table-column>

          <el-table-column label="Thao tác" width="150" align="center" fixed="right">
            <template #default="{ row }">
              <div class="d-flex justify-content-center gap-2">
                <el-button type="primary" size="small" plain @click="viewDetails(row.id)">Chi tiết</el-button>
                <el-tooltip content="In hóa đơn" placement="top">
                  <el-button type="warning" size="small" :icon="Printer" circle @click="handlePrintInvoice(row.id)" />
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container mt-3 d-flex justify-content-end">
          <el-pagination
            v-model:current-page="listQuery.page"
            v-model:page-size="listQuery.size"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="total"
            @size-change="handleFilter"
            @current-change="fetchInvoices"
          />
        </div>
      </el-card>

      <el-dialog v-model="dialogVisible" title="CHI TIẾT GIAO DỊCH" width="750px">
        <el-table v-loading="detailLoading" :data="invoiceDetails" border stripe>
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column label="Phim / Sản phẩm">
            <template #default="{ row }">
              <span v-if="row.tenPhim">🎬 <strong>{{ row.tenPhim }}</strong> (Vé xem phim)</span>
              <span v-else>🍿 Sản phẩm đi kèm (Combo/Bắp nước)</span>
            </template>
          </el-table-column>
          <el-table-column prop="viTriGhe" label="Ghế" width="80" align="center" />
          <el-table-column label="Đơn giá" align="right" width="130">
            <template #default="{ row }">{{ formatCurrency(row.donGia) }}</template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button @click="dialogVisible = false">Đóng cửa sổ</el-button>
        </template>
      </el-dialog>
    </div>

    <div id="invoice-print-section" v-if="invoiceToPrint">
      <div class="invoice-box">
        <h2 class="cinema-name">CINEOPS THEATER</h2>
        <p class="invoice-type">HÓA ĐƠN THANH TOÁN</p>
        <div class="divider">***************************</div>
        <div class="info">Mã HĐ: {{ invoiceToPrint.maHoaDon }}</div>
        <div class="info">Khách: {{ invoiceToPrint.tenKhachHang || 'Khách lẻ' }}</div>
        <div class="info">Ngày: {{ formatDate(invoiceToPrint.ngayTao) }}</div>
        <div class="divider">---------------------------</div>
        <table class="items-table">
          <tr v-for="item in invoiceToPrint.details" :key="item.id">
            <td>{{ item.tenPhim || 'Sản phẩm' }} {{ item.viTriGhe ? '('+item.viTriGhe+')' : '' }}</td>
            <td align="right">{{ formatCurrency(item.donGia) }}</td>
          </tr>
        </table>
        <div class="divider">---------------------------</div>
        <div class="total-section">
          <strong>TỔNG TIỀN:</strong>
          <strong>{{ formatCurrency(invoiceToPrint.tongTienThanhToan) }}</strong>
        </div>
        <div class="divider">***************************</div>
        <p class="footer-note">Cảm ơn và hẹn gặp lại!</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Search, Printer } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';

// --- STATE QUẢN LÝ ---
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);
const dialogVisible = ref(false);
const invoiceDetails = ref([]);
const detailLoading = ref(false);
const invoiceToPrint = ref(null);

const listQuery = reactive({
  tuKhoa: '',
  trangThai: null,
  kenhBanHang: null, // Đồng bộ với Entity
  kyThoiGian: '',
  page: 1,
  size: 10
});

// --- HÀM XỬ LÝ DỮ LIỆU ---
const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    // Làm sạch tham số rỗng
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) delete params[key];
    });

    const res = await hoaDonService.getInvoices(params);
    invoices.value = res.data.content || [];
    total.value = res.data.totalElements || 0;
  } catch (error) {
    ElMessage.error('Lỗi hệ thống: Không thể tải danh sách hóa đơn.');
  } finally {
    loading.value = false;
  }
};

const viewDetails = async (id) => {
  dialogVisible.value = true;
  detailLoading.value = true;
  try {
    const res = await hoaDonService.getInvoiceDetails(id);
    invoiceDetails.value = res.data || [];
  } catch (error) {
    ElMessage.error('Lỗi khi lấy chi tiết hóa đơn!');
  } finally {
    detailLoading.value = false;
  }
};

const handlePrintInvoice = async (id) => {
  loading.value = true;
  try {
    const res = await hoaDonService.getInvoiceDetails(id);
    const invoiceInfo = invoices.value.find(item => item.id === id);

    if (invoiceInfo && res.data) {
      invoiceToPrint.value = { ...invoiceInfo, details: res.data };
      setTimeout(() => {
        window.print();
        invoiceToPrint.value = null;
      }, 500);
    }
  } catch (error) {
    ElMessage.error('Lỗi chuẩn bị dữ liệu in!');
  } finally {
    loading.value = false;
  }
};

const handleFilter = () => {
  listQuery.page = 1;
  fetchInvoices();
};

const formatCurrency = (v) => v ? new Intl.NumberFormat('vi-VN').format(v) + ' ₫' : '0 ₫';
const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN') : '---';

onMounted(fetchInvoices);
</script>

<style scoped>
.admin-invoices-container { padding: 24px; background: #f8fafc; min-height: 100vh; }
.filter-card, .table-card { border-radius: 12px; }
.filter-header h3 { color: #1e293b; font-size: 1.2rem; font-weight: bold; }
.text-danger { color: #ef4444; }

/* CSS DÀNH RIÊNG CHO IN ẤN (K80) */
@media print {
  /* Ẩn mọi thứ trừ vùng in */
  body > *:not(.admin-invoices-page-wrapper),
  .admin-invoices-page-wrapper > *:not(#invoice-print-section) {
    display: none !important;
  }
  #invoice-print-section {
    display: block !important;
    position: absolute;
    top: 0; left: 0; width: 100%;
    background: #fff;
  }
  .invoice-box {
    width: 80mm;
    margin: 0 auto;
    padding: 10mm 2mm;
    font-family: 'Courier New', Courier, monospace;
    text-align: center;
    color: #000;
  }
  .cinema-name { font-size: 18pt; font-weight: bold; }
  .items-table { width: 100%; text-align: left; font-size: 10pt; margin: 10pt 0; }
  .total-section { display: flex; justify-content: space-between; font-weight: bold; font-size: 11pt; }
}
</style>