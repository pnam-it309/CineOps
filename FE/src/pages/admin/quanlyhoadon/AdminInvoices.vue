<template>
  <div class="admin-invoices-main-wrapper">
    <div class="admin-invoices-container">
      <el-card class="filter-card" shadow="never">
        <div class="filter-header d-flex justify-content-between align-items-center mb-3">
          <h3><i class="bi bi-receipt-cutoff me-2"></i>Quản Lý Hóa Đơn</h3>
          <el-button type="success" :icon="Download" @click="handleExportExcel" :loading="exportLoading">
            Xuất Báo Cáo Excel
          </el-button>
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
        <el-table 
          v-loading="loading" 
          :data="invoices" 
          border 
          stripe 
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" fixed="left" />
          
          <el-table-column type="index" label="STT" width="60" align="center" />
          
          <el-table-column prop="maHoaDon" label="Mã HĐ" width="150">
            <template #default="{ row }">
              <span class="fw-bold text-primary">#{{ row.maHoaDon }}</span>
            </template>
          </el-table-column>

          <el-table-column label="Khách hàng" min-width="165">
            <template #default="{ row }">
              <div class="fw-bold">{{ row.tenKhachHang || 'Khách lẻ' }}</div>
              <div v-if="row.sdt" class="text-muted small">
                <i class="bi bi-telephone me-1"></i>{{ row.sdt }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="Nhân viên / Kênh" min-width="140">
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
          <el-table-column label="Số ghế" width="100" align="center">
             <template #default="{ row }">{{ row.soGhe || row.viTriGhe || '---' }}</template>
          </el-table-column>
          <el-table-column label="Đơn giá" align="right" width="130">
            <template #default="{ row }">{{ formatCurrency(row.donGia) }}</template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button @click="dialogVisible = false">Đóng cửa sổ</el-button>
        </template>
      </el-dialog>
    </div>

    <div id="invoice-print-section" v-if="invoiceToPrint" class="print-only">
      <div class="invoice-box">
        <h2 class="cinema-name">CINEOPS THEATER</h2>
        <p class="invoice-type">HÓA ĐƠN THANH TOÁN</p>
        <div class="divider">***************************</div>
        <div class="info-row"><span>Mã HĐ:</span> <span class="fw-bold">{{ invoiceToPrint.maHoaDon }}</span></div>
        <div class="info-row"><span>Khách:</span> <span>{{ invoiceToPrint.tenKhachHang || 'Khách lẻ' }}</span></div>
        <div class="info-row"><span>Ngày:</span> <span>{{ formatDate(invoiceToPrint.ngayTao) }}</span></div>
        <div class="divider">---------------------------</div>
        <table class="items-table">
          <tr v-for="item in invoiceToPrint.details" :key="item.id">
            <td>
              {{ item.loai === 0 ? '🎬' : '🍿' }} {{ item.tenPhim || 'Sản phẩm' }}
              <div v-if="item.loai === 0" class="item-sub">Số ghế: {{ item.soGhe || item.viTriGhe }}</div>
            </td>
            <td align="right">{{ formatCurrency(item.donGia) }}</td>
          </tr>
        </table>
        <div class="divider">---------------------------</div>
        <div class="total-row"><strong>TỔNG TIỀN:</strong> <strong>{{ formatCurrency(invoiceToPrint.tongTienThanhToan) }}</strong></div>
        <div class="divider">***************************</div>
        <p class="footer-note">Cảm ơn và hẹn gặp lại tại CineOps!</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { Search, Printer, Download } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';

// --- TRẠNG THÁI QUẢN LÝ ---
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);
const exportLoading = ref(false);
const dialogVisible = ref(false);
const invoiceDetails = ref([]);
const detailLoading = ref(false);
const invoiceToPrint = ref(null);
const multipleSelection = ref([]);

const listQuery = reactive({
  tuKhoa: '', trangThai: null, kenhBanHang: null, kyThoiGian: '',
  page: 1, size: 10
});

// --- XỬ LÝ DỮ LIỆU ---
const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    // Làm sạch tham số (Lọc bỏ các trường rỗng)
    Object.keys(params).forEach(k => { if (params[k] === '' || params[k] === null) delete params[k] });
    const res = await hoaDonService.getInvoices(params);
    invoices.value = res.data.content || [];
    total.value = res.data.totalElements || 0;
  } catch (e) { ElMessage.error('Không thể tải danh sách hóa đơn.'); }
  finally { loading.value = false; }
};

const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

/**
 * FIX LỖI 400: Loại bỏ tham số phân trang và chuỗi rỗng
 */
const handleExportExcel = async () => {
  exportLoading.value = true;
  try {
    const params = {
      tuKhoa: listQuery.tuKhoa,
      trangThai: listQuery.trangThai,
      kenhBanHang: listQuery.kenhBanHang,
      kyThoiGian: listQuery.kyThoiGian
    };

    Object.keys(params).forEach(key => {
      if (params[key] === "" || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });

    const res = await hoaDonService.exportExcel(params); 
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `CineOps_HoaDon_${new Date().getTime()}.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    ElMessage.success("Xuất báo cáo Excel thành công!");
  } catch (error) { ElMessage.error("Lỗi: Kiểm tra lại tham số lọc tại Backend!"); }
  finally { exportLoading.value = false; }
};

const viewDetails = async (id) => {
  dialogVisible.value = true;
  detailLoading.value = true;
  try {
    const res = await hoaDonService.getInvoiceDetails(id);
    invoiceDetails.value = res.data || [];
  } catch (e) { ElMessage.error('Lỗi khi lấy chi tiết!'); }
  finally { detailLoading.value = false; }
};

const handlePrintInvoice = async (id) => {
  loading.value = true;
  try {
    const res = await hoaDonService.getInvoiceDetails(id);
    const info = invoices.value.find(i => i.id === id);
    if (info && res.data) {
      invoiceToPrint.value = { ...info, details: res.data };
      await nextTick();
      setTimeout(() => { window.print(); invoiceToPrint.value = null; }, 500);
    }
  } catch (e) { ElMessage.error('Lỗi chuẩn bị dữ liệu in!'); }
  finally { loading.value = false; }
};

const handleFilter = () => { listQuery.page = 1; fetchInvoices(); };
const formatCurrency = (v) => v ? new Intl.NumberFormat('vi-VN').format(v) + ' ₫' : '0 ₫';
const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN') : '---';

onMounted(fetchInvoices);
</script>

<style scoped>
.admin-invoices-container { padding: 24px; background: #f8fafc; min-height: 100vh; }
.filter-card, .table-card { border-radius: 12px; }
.text-danger { color: #ef4444; font-weight: bold; }
.fw-bold { font-weight: bold; }

/* CSS ĐẶC TRỊ IN K80 */
@media print {
  body > *:not(.admin-invoices-main-wrapper),
  .admin-invoices-main-wrapper > *:not(#invoice-print-section) { display: none !important; }
  #invoice-print-section { display: block !important; position: absolute; top: 0; left: 0; width: 100%; background: #fff; }
  .invoice-box { width: 80mm; margin: 0 auto; padding: 10mm 2mm; font-family: 'Courier New', monospace; text-align: center; color: #000; }
  .cinema-name { font-size: 16pt; font-weight: bold; }
  .info-row { display: flex; justify-content: space-between; font-size: 9pt; text-align: left; margin-bottom: 2pt; }
  .items-table { width: 100%; font-size: 9pt; text-align: left; margin: 5pt 0; border-collapse: collapse; }
  .divider { border-top: 1pt dashed #000; margin: 5pt 0; }
  .total-row { display: flex; justify-content: space-between; font-size: 11pt; font-weight: bold; }
  .item-sub { font-size: 8pt; color: #333; padding-left: 5pt; }
}
.print-only { display: none; }
</style>