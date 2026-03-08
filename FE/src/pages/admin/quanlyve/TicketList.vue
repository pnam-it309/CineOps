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
import BaseTable from '@/components/common/BaseTable.vue';

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

const ticketColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã vé', key: 'maVe', width: '120px' },
  { label: 'Tên phim', key: 'tenPhim', width: '150px' },
  { label: 'Phòng', key: 'phong', width: '100px' },
  { label: 'Suất chiếu', key: 'suatChieu', width: '110px' },
  { label: 'Ngày chiếu', key: 'ngayChieu', width: '110px' },
  { label: 'Ghế', key: 'ghe', width: '100px' },
  { label: 'Thanh toán', key: 'giaThanhToan', width: '120px' },
  { label: 'Trạng thái', key: 'trangThai', width: '130px' },
];

// Filters
const params = reactive({
  tuKhoa: '',
  trangThai: null,
  loaiVe: null,
  idPhongChieu: null,
  page: 1, 
  size: 5
});

const phongChieuList = ref([]);

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
  params.loaiVe = null;
  params.idPhongChieu = null;
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


import { gheService } from '@/services/api/admin/gheService';

const fetchPhongChieu = async () => {
  try {
    const res = await gheService.getPhongChieuDropdown();
    phongChieuList.value = res.data?.data || [];
  } catch (e) {
    console.error('Không thể tải danh sách phòng chiếu');
  }
};

onMounted(() => { 
  loadData(); 
  fetchPhongChieu();
});
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
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
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

.cutout-left,
.cutout-right {
  position: absolute;
  top: 50%;
  width: 24px;
  height: 24px;
  background: #fff;
  /* Matches modal background or parent */
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
<!-- TicketList.vue — uses BaseTable in #content slot, consistent with other admin pages -->
  <div class="admin-ticket-page">
    <AdminTableLayout
      title="Quản Lý Giao Dịch Vé"
      titleIcon="bi bi-ticket-perforated-fill"
      :data="tickets"
      :loading="loading"
      :total="totalElements"
      v-model:currentPage="params.page"
      v-model:pageSize="params.size"
      @reset-filter="resetFilter"
    >
      <template #header-actions-left>
        <el-button type="primary" :icon="Plus" @click="$router.push('/admin/pos')">
          Xuất vé mới
        </el-button>
        <el-button class="btn-cine-secondary text-success border-success-subtle" :icon="Download"
          @click="exportExcel">
          Xuất Excel
        </el-button>
      </template>



      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input v-model="params.tuKhoa" placeholder="Tìm mã vé, tên phim, SĐT..." :prefix-icon="Search"
            clearable @input="onSearch" style="width: 250px;" />
        </div>

        <div class="filter-item">
          <el-select v-model="params.trangThai" @change="loadData" placeholder="Chọn trạng thái" style="width: 180px;" clearable>
            <el-option label="Tất cả trạng thái" value="" /> 
            <el-option label="Thành công" :value="1" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="params.loaiVe" @change="loadData" placeholder="Kênh bán" style="width: 180px;" clearable>
            <el-option label="Tất cả kênh" value="" />
            <el-option label="Tại quầy" :value="0" />
            <el-option label="Online" :value="1" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="params.idPhongChieu" @change="loadData" placeholder="Chọn phòng chiếu" style="width: 180px;" clearable>
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
          </el-select>
        </div>
      </template>

      <template #content>
        <BaseTable
          :data="tickets"
          :columns="ticketColumns"
          :loading="loading"
          :total="totalElements"
          v-model:currentPage="params.page"
          v-model:pageSize="params.size"
          :hide-pagination="true"
        >
          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (params.page - 1) * params.size + index + 1 }}</span>
          </template>

          <template #cell-maVe="{ row }">
            <span class="fw-bold text-indigo-500">#{{ row.maVe }}</span>
          </template>

          <template #cell-tenPhim="{ row }">
            <div class="fw-bold text-dark">{{ row.tenPhim || '—' }}</div>
          </template>

          <template #cell-phong="{ row }">
            <span class="text-secondary small fw-semibold">{{ row.tenPhongChieu || '—' }}</span>
          </template>

          <template #cell-suatChieu="{ row }">
             <el-tag size="small" type="info" effect="plain">{{ formatTime(row.ngayTao) }}</el-tag>
          </template>

          <template #cell-ngayChieu="{ row }">
             <span class="small">{{ formatDate(row.ngayTao) }}</span>
          </template>

          <template #cell-ghe="{ row }">
            <div class="fw-bold text-danger text-nowrap">{{ row.viTriGhe || '—' }}</div>
          </template>

          <template #cell-giaThanhToan="{ row }">
            <span class="fw-bold text-primary">{{ formatPrice(row.giaThanhToan) }}đ</span>
          </template>

          <template #cell-trangThai="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round effect="light">
              {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
            </el-tag>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Chi tiết vé" placement="top">
                <button class="btn-action-icon action-view" @click="handleViewDetail(row)">
                  <i class="bi bi-eye"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="In vé" placement="top">
                <button class="btn-action-icon btn-action-print" @click="printTicket(row)">
                  <i class="bi bi-printer"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Hủy vé" placement="top" v-if="row.trangThai === 1">
                <button class="btn-action-icon action-delete" @click="confirmCancel(row.id)">
                  <i class="bi bi-slash-circle"></i>
                </button>
              </el-tooltip>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <!-- ===== DIGITAL TICKET DETAIL ===== -->
    <BaseModal v-model="detailVisible" title="Chi tiết Vé Xem Phim" icon="bi bi-ticket-perforated" width="450px">
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
.text-indigo-500 {
  color: #4f46e5;
}

.admin-ticket-page {
  padding: 0;
}

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

.btn-action-icon:hover {
  background-color: #f1f5f9;
}

.btn-action-print {
  color: #3b82f6;
}

.btn-action-print:hover {
  color: #1d4ed8;
  background-color: #eff6ff !important;
}

.action-delete {
  color: #ef4444;
}

.action-delete:hover {
  color: #b91c1c;
  background-color: #fef2f2 !important;
}
</style>
