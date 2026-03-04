<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { adVeService } from '@/services/api/admin/ad-ve-service.js';
import { 
  Search, Plus, Monitor, Place, Refresh, Printer, Delete,
  Calendar, Timer, Download, Ticket, CircleCheck, CircleClose, Money
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseModal from '@/components/common/BaseModal.vue';

import confirmDialog from '@/utils/confirm';
import debounce from 'lodash/debounce';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import * as XLSX from 'xlsx';

// Data & State
const tickets = ref([]);
const totalElements = ref(0);
const loading = ref(false);
const detailVisible = ref(false);
const selectedTicket = ref(null);

const handleViewDetail = (ticket) => {
  selectedTicket.value = ticket;
  detailVisible.value = true;
};


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



// Handlers
const onSearch = debounce(() => { params.page = 1; loadData(); }, 500);
const resetFilter = () => { 
  params.tuKhoa = ''; 
  params.trangThai = null; 
  params.page = 1;
  loadData(); 
};

const confirmCancel = (id) => {
  confirmDialog.custom(
    'Hủy vé sẽ hoàn lại ghế trống vào phòng. Bạn chắc chắn chứ?',
    'Xác nhận hủy vé',
    'Đồng ý hủy'
  ).then(async () => {
    try {
      await adVeService.huyVe(id);
      ElMessage.success('Đã hủy vé thành công');
      loadData();
    } catch (e) { ElMessage.error('Lỗi khi hủy vé'); }
  });
};

// Formatters
const formatPrice = (v) => new Intl.NumberFormat('vi-VN').format(v || 0);
const formatDate = (d) => d ? format(new Date(d), 'dd/MM/yyyy', { locale: vi }) : '---';
const formatTime = (d) => d ? format(new Date(d), 'HH:mm', { locale: vi }) : '---';

// Features (Print & Excel)
const printTicket = (ticket) => {
  const printContent = `
    <div style="font-family: Arial, sans-serif; width: 300px; padding: 20px; border: 1px solid #ccc; background: white; margin: 0 auto;">
      <h2 style="text-align: center; margin-bottom: 5px;">CINEOPS</h2>
      <p style="text-align: center; margin: 0; font-size: 13px; color: #666;">Hóa đơn bán vé</p>
      <hr style="border-top: 1px dashed #ccc; margin: 15px 0;">
      <p style="margin: 8px 0;"><strong>Mã vé:</strong> #${ticket.maVe}</p>
      <p style="margin: 8px 0;"><strong>Phim:</strong> ${ticket.tenPhim || '---'}</p>
      <p style="margin: 8px 0;"><strong>Phòng chiếu:</strong> ${ticket.tenPhongChieu || '---'}</p>
      <p style="margin: 8px 0;"><strong>Ghế:</strong> ${ticket.viTriGhe || '---'}</p>
      <p style="margin: 8px 0;"><strong>Khách hàng:</strong> ${ticket.tenLoaiKhachHang || 'Vãng lai'}</p>
      <p style="margin: 8px 0;"><strong>Thời gian:</strong> ${formatDate(ticket.ngayTao)} ${formatTime(ticket.ngayTao)}</p>
      <hr style="border-top: 1px dashed #ccc; margin: 15px 0;">
      <h3 style="text-align: left; display: flex; justify-content: space-between; margin: 10px 0;">
        <span>Tổng tiền:</span> 
        <span>${formatPrice(ticket.giaThanhToan)} đ</span>
      </h3>
      <p style="text-align: center; margin-top: 20px; font-size: 12px; color: #666;">Cảm ơn quý khách đã sử dụng dịch vụ!</p>
    </div>
  `;

  const printWindow = window.open('', '_blank', 'width=450,height=650');
  if (printWindow) {
    printWindow.document.write(`
      <html>
        <head>
          <title>In vé - ${ticket.maVe}</title>
          <style>@page { size: auto; margin: 0mm; } body { margin: 20px; }</style>
        </head>
        <body>
          ${printContent}
        </body>
      </html>
    `);
    printWindow.document.close();
    printWindow.focus();
    setTimeout(() => {
      printWindow.print();
      printWindow.close();
    }, 500);
  }
};

const exportExcel = async () => {
  try {
    loading.value = true;
    const exportParams = { ...params, page: 0, size: 10000 };
    const res = await adVeService.timKiemVe(exportParams);
    const dataToExport = res.data?.content || [];
    
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất');
      return;
    }

    const excelData = dataToExport.map((row, index) => ({
      'STT': index + 1,
      'Mã vé': row.maVe,
      'Loại khách': row.tenLoaiKhachHang || 'Khách vãng lai',
      'Tên phim': row.tenPhim || '---',
      'Phòng chiếu': row.tenPhongChieu || '---',
      'Ghế': row.viTriGhe || '---',
      'Kênh bán': row.loaiVe === 0 ? 'Tại quầy' : 'Online',
      'Thanh toán (đ)': row.giaThanhToan,
      'Trạng thái': row.trangThai === 1 ? 'Thành công' : 'Đã hủy',
      'Thời gian': `${formatDate(row.ngayTao)} ${formatTime(row.ngayTao)}`,
      'Người tạo': row.nguoiTao || '---'
    }));

    const worksheet = XLSX.utils.json_to_sheet(excelData);
    
    const wscols = [
      { wch: 5 }, { wch: 15 }, { wch: 20 }, { wch: 30 }, 
      { wch: 15 }, { wch: 15 }, { wch: 15 }, { wch: 15 }, 
      { wch: 15 }, { wch: 20 }, { wch: 25 }
    ];
    worksheet['!cols'] = wscols;

    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Danh sách vé');
    
    XLSX.writeFile(workbook, `DanhSachVe_${format(new Date(), 'dd_MM_yyyy')}.xlsx`);
    ElMessage.success('Xuất file Excel thành công');
  } catch (error) {
    ElMessage.error('Có lỗi xảy ra khi xuất Excel');
  } finally {
    loading.value = false;
  }
};

// Stats Configuration


onMounted(() => { loadData(); });
</script>

<style scoped>
.ticket-stub-container {
  display: flex;
  justify-content: center;
  padding: 10px;
}
.ticket-stub {
  width: 100%;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  border: 1px solid #eee;
}
.border-dashed {
  border-bottom-style: dashed !important;
  border-bottom-width: 2px !important;
}
.stub-cinema-brand {
  font-family: 'Outfit', sans-serif;
  letter-spacing: 3px;
  color: var(--el-color-primary);
  font-weight: 700;
  font-size: 12px;
}
.lbl {
  font-size: 11px;
  text-transform: none;
  color: #94a3b8;
  font-weight: 700;
  margin-bottom: 2px;
}
.val {
  color: #1e293b;
  font-weight: 600;
}
.cutout-left, .cutout-right {
  position: absolute;
  top: 50%;
  width: 24px;
  height: 24px;
  background: #fff; /* Matches modal background or parent */
  border-radius: 50%;
  z-index: 5;
  border: 1px solid #eee;
}
.cutout-left {
  left: -12px;
  transform: translateY(-50%);
}
.cutout-right {
  right: -12px;
  transform: translateY(-50%);
}
.bg-light {
  background-color: #f8fafc !important;
}
</style>

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
        <el-button class="btn-premium-secondary text-success border-success-subtle" :icon="Download" @click="exportExcel">
          Xuất Excel
        </el-button>
      </template>



      <template #filters>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="params.trangThai" @change="loadData" placeholder="Trạng thái" clearable>
  <el-option label="Tất cả trạng thái" value="" /> <el-option label="Thành công" :value="1" />
  <el-option label="Đã hủy" :value="0" />
</el-select>
        </div>
        <div class="filter-item search-input-wrapper">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
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
              {{ row.loaiVe === 0 ? 'Tại quầy' : 'Online' }}
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

        <el-table-column label="THAO TÁC" width="167" align="center" fixed="right">
          <template #default="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Chi tiết vé" placement="top">
                <button class="btn-action-icon btn-action-view" @click="handleViewDetail(row)">
                  <i class="bi bi-eye"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="In vé" placement="top">
                <button class="btn-action-icon btn-action-print" @click="printTicket(row)">
                  <i class="bi bi-printer"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Hủy vé" placement="top" v-if="row.trangThai === 1">
                <button class="btn-action-icon btn-action-delete" @click="confirmCancel(row.id)">
                  <i class="bi bi-slash-circle"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </template>
    </AdminTableLayout>

    <!-- ===== DIGITAL TICKET DETAIL ===== -->
    <BaseModal
      v-model="detailVisible"
      title="Chi tiết Vé Xem Phim"
      icon="bi bi-ticket-perforated"
      width="450px"
    >
      <div v-if="selectedTicket" class="ticket-stub-container">
        <div class="ticket-stub">
          <!-- Phim Section -->
          <div class="stub-header p-4 text-center border-bottom border-dashed">
            <div class="stub-cinema-brand mb-2">CINEOPS PREMIUM</div>
            <h4 class="fw-bold text-dark m-0">{{ selectedTicket.tenPhim }}</h4>
            <div class="text-secondary small mt-1">Định dạng: 2D Normal</div>
          </div>
          
          <!-- Details Section -->
          <div class="stub-body p-4 border-bottom border-dashed position-relative">
            <!-- Cutout circles side -->
            <div class="cutout-left"></div>
            <div class="cutout-right"></div>
            
            <div class="row g-3">
              <div class="col-6">
                <div class="lbl">Ngày chiếu</div>
                <div class="val">{{ formatDate(selectedTicket.ngayTao) }}</div>
              </div>
              <div class="col-6">
                <div class="lbl">Suất chiếu</div>
                <div class="val text-primary fw-bold">{{ formatTime(selectedTicket.ngayTao) }}</div>
              </div>
              <div class="col-6">
                <div class="lbl">Phòng</div>
                <div class="val">{{ selectedTicket.tenPhongChieu }}</div>
              </div>
              <div class="col-6">
                <div class="lbl">Ghế</div>
                <div class="val text-danger fw-bold fs-5">{{ selectedTicket.viTriGhe }}</div>
              </div>
            </div>
          </div>
          
          <!-- Pricing & Code Section -->
          <div class="stub-footer p-4 bg-light bg-opacity-50 text-center">
            <div class="barcode-wrapper mb-3 p-3 bg-white rounded-3 shadow-sm border">
               <div class="qr-placeholder d-flex flex-column align-items-center">
                 <i class="bi bi-qr-code fs-1 text-dark"></i>
                 <div class="fw-bold mt-2">#{{ selectedTicket.maVe }}</div>
               </div>
            </div>
            
            <div class="d-flex justify-content-between align-items-center px-2">
              <span class="text-secondary small">Thanh toán</span>
              <span class="fw-bold text-dark fs-5">{{ formatPrice(selectedTicket.giaThanhToan) }} đ</span>
            </div>
            
            <div class="mt-3 pt-3 border-top small text-secondary">
              <div>Người tạo: <b>{{ selectedTicket.nguoiTao || '—' }}</b></div>
              <div class="mt-1">Kênh: {{ selectedTicket.loaiVe === 0 ? 'Tại quầy' : 'Online' }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer></template>
    </BaseModal>
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