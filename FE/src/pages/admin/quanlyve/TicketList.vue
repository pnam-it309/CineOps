<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { adVeService } from '@/services/api/admin/ad-ve-service.js';
import {
  Search, Plus, Monitor, Place, Refresh, Printer, Delete,
  Calendar, Timer, Download, Ticket, CircleCheck, CircleClose, Money
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import MovieTicketTemplate from './MovieTicketTemplate.vue';

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
const customDateRange = ref([]);

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
  startDate: null,
  endDate: null,
  kyThoiGian: '',
  minPrice: null,
  maxPrice: null,
  sortDir: 'DESC',
  page: 1,
  size: 5
});

const phongChieuList = ref([]);

// Load Data
const loadData = async () => {
  loading.value = true;
  try {
    // Chuẩn bị params gửi lên API
    const apiParams = {
      tuKhoa: params.tuKhoa,
      trangThai: params.trangThai,
      minPrice: params.minPrice,
      maxPrice: params.maxPrice,
      tuNgay: params.startDate,
      denNgay: params.endDate,
      kyThoiGian: params.kyThoiGian,
      sortDir: params.sortDir,
      page: params.page - 1, // Backend dùng index 0
      size: params.size
    };

    // Loại bỏ các param null hoặc undefined hoặc chuỗi rỗng (trừ tuKhoa)
    Object.keys(apiParams).forEach(key => {
      if (apiParams[key] === null || apiParams[key] === undefined || (apiParams[key] === '' && key !== 'tuKhoa')) {
        delete apiParams[key];
      }
    });

    const res = await adVeService.timKiemVe(apiParams);
    tickets.value = res.data?.content || [];
    totalElements.value = res.data?.totalElements || 0;
  } catch (e) {
    console.error('API Error:', e);
    ElMessage.error('Không thể tải danh sách vé.');
  } finally {
    loading.value = false;
  }
};



// Handlers
const onSearch = debounce(() => { params.page = 1; loadData(); }, 500);
const resetFilter = () => {
  params.tuKhoa = '';
  params.trangThai = null;
  params.loaiVe = null;
  params.idPhongChieu = null;
  params.startDate = null;
  params.endDate = null;
  params.kyThoiGian = '';
  params.minPrice = null;
  params.maxPrice = null;
  params.sortDir = 'DESC';
  params.page = 1;
  customDateRange.value = [];
  loadData();
};

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    params.startDate = val[0];
    params.endDate = val[1];
    params.kyThoiGian = ''; // Xóa kỳ thời gian nếu chọn ngày cụ thể
  } else {
    params.startDate = null;
    params.endDate = null;
  }
  params.page = 1;
  loadData();
};

const handlePeriodChange = (val) => {
  if (val) {
    customDateRange.value = [];
    params.startDate = null;
    params.endDate = null;
  }
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
  selectedTicket.value = ticket;
  // Đợi một chút để template cập nhật data của selectedTicket
  setTimeout(() => {
    window.print();
  }, 150);
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
    <BaseTable
      title="Quản Lý Vé"
      titleIcon="bi bi-ticket-perforated-fill"
      :data="tickets"
      :columns="ticketColumns"
      :loading="loading"
      :total="totalElements"
      v-model:currentPage="params.page"
      v-model:pageSize="params.size"
      @reset-filter="resetFilter"
      :hidePagination="false"
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
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Tìm kiếm</label>
          <el-input 
            v-model="params.tuKhoa" 
            placeholder="Mã vé, phim, SĐT..."
            :prefix-icon="Search" 
            clearable 
            class="w-100"
            @input="onSearch" 
          />
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Khoảng ngày</label>
          <el-date-picker
            v-model="customDateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="Từ"
            end-placeholder="Đến"
            format="DD/MM/YYYY"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            class="w-100"
          />
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Kỳ thời gian</label>
          <el-select v-model="params.kyThoiGian" placeholder="Tất cả" clearable @change="handlePeriodChange" class="w-100">
            <el-option label="Tất cả thời gian" value=""/>
            <el-option label="Hôm nay" value="TODAY"/>
            <el-option label="Tuần này" value="THIS_WEEK"/>
            <el-option label="Tháng này" value="THIS_MONTH"/>
          </el-select>
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Trạng thái</label>
          <el-select v-model="params.trangThai" placeholder="Tất cả" clearable @change="loadData" class="w-100">
            <el-option label="Thành công" :value="1" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Kênh bán</label>
          <el-select v-model="params.loaiVe" @change="loadData" placeholder="Tất cả" class="w-100" clearable>
            <el-option label="Tại quầy" :value="0" />
            <el-option label="Online" :value="1" />
          </el-select>
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Phòng chiếu</label>
          <el-select v-model="params.idPhongChieu" @change="loadData" placeholder="Tất cả" class="w-100" clearable>
            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
          </el-select>
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Giá thanh toán</label>
          <div class="d-flex align-items-center gap-1">
            <el-input-number v-model="params.minPrice" :controls="false" placeholder="Giá từ" class="w-100" @change="loadData" />
            <span class="text-secondary smaller">-</span>
            <el-input-number v-model="params.maxPrice" :controls="false" placeholder="Đến" class="w-100" @change="loadData" />
          </div>
        </div>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Sắp xếp</label>
          <el-select v-model="params.sortDir" placeholder="Mới nhất" @change="loadData" class="w-100">
            <el-option label="Mới nhất" value="DESC"/>
            <el-option label="Cũ nhất" value="ASC"/>
          </el-select>
        </div>
      </template>

      <template #cell-stt="{ index }">
        <span class="small fw-bold text-secondary">{{ (params.page - 1) * params.size + index + 1 }}</span>
      </template>

      <template #cell-maVe="{ row }">
        <span class="fw-bold text-indigo-500">#{{ row.maVe }}</span>
      </template>
      <template #cell-maHoaDon="{ row }">
        <span class="fw-bold text-indigo-500">#{{ row.maHoaDon }}</span>
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

    <!-- ===== DIGITAL TICKET DETAIL (PREMIUM) ===== -->
    <BaseModal v-model="detailVisible" title="Thông tin vé điện tử" icon="bi bi-ticket-perforated-fill" width="600px" isDetail onlyCancel>
      <div v-if="selectedTicket" class="premium-ticket-detail">
        <!-- Premium Hero Section -->
        <div class="profile-hero-banner p-4 text-white position-relative overflow-hidden" 
             style="background: linear-gradient(135deg, #1e293b 0%, #334155 100%);">
          <div class="row align-items-center position-relative" style="z-index: 2;">
            <div class="col-md-3 text-center">
              <div class="mini-poster shadow-lg border-4 border-white border rounded-0 d-inline-block overflow-hidden" style="width: 120px; height: 180px;">
                <img :src="selectedTicket.poster || 'https://placehold.co/150x220?text=Movie'" class="w-100 h-100 object-fit-cover" />
              </div>
            </div>
            <div class="col-md-6 border-start border-white border-opacity-10 ps-4">
              <div class="d-flex align-items-center gap-2 mb-2">
                <el-tag :type="selectedTicket.trangThai === 1 ? 'success' : 'danger'" effect="dark" round size="small" class="fw-bold">
                  {{ selectedTicket.trangThai === 1 ? 'HỢP LỆ' : 'ĐÃ HỦY' }}
                </el-tag>
                <span class="badge bg-white bg-opacity-20 font-monospace">#{{ selectedTicket.maVe }}</span>
              </div>
              <h1 class="fw-bold m-0 text-truncate display-6" :title="selectedTicket.tenPhim">{{ selectedTicket.tenPhim }}</h1>
              <div class="mt-3 d-flex gap-3">
                 <div class="text-center">
                    <div class="small opacity-75 uppercase-label tiny-text">PHÒNG</div>
                    <div class="fw-bold fs-4">{{ selectedTicket.tenPhongChieu }}</div>
                 </div>
                 <div class="text-center border-start border-white border-opacity-20 ps-3">
                    <div class="small opacity-75 uppercase-label tiny-text">GHẾ</div>
                    <div class="fw-bold fs-4 text-warning">{{ selectedTicket.viTriGhe }}</div>
                 </div>
              </div>
            </div>
            <div class="col-md-3 text-end">
               <div class="qr-code-box bg-white p-2 d-inline-block shadow-lg">
                 <i class="bi bi-qr-code fs-1 text-dark" style="font-size: 4rem !important;"></i>
               </div>
            </div>
          </div>
          <!-- Decoration -->
          <div class="decoration-circle position-absolute" style="width: 300px; height: 300px; background: rgba(239, 68, 68, 0.1); border-radius: 50%; top: -100px; right: -50px;"></div>
        </div>

        <!-- Detail Body -->
        <div class="profile-details-body p-4 bg-white">
          <div class="row g-4">
            <!-- Screening Info -->
            <div class="col-md-6">
              <h6 class="text-secondary fw-bold small text-uppercase mb-3 letter-spacing-1">Suất chiếu & Lịch trình</h6>
              <div class="info-card p-3 border bg-light h-100">
                <div class="info-row mb-3 pb-3 border-bottom">
                  <label class="text-secondary tiny-text d-block">NGÀY CHIẾU</label>
                  <div class="fw-bold fs-5">{{ formatDate(selectedTicket.ngayTao) }}</div>
                </div>
                <div class="info-row">
                  <label class="text-secondary tiny-text d-block">THỜI GIAN</label>
                  <div class="fw-bold fs-5 text-primary">{{ formatTime(selectedTicket.ngayTao) }}</div>
                </div>
              </div>
            </div>

            <!-- Transaction Info -->
            <div class="col-md-6">
              <h6 class="text-secondary fw-bold small text-uppercase mb-3 letter-spacing-1">Chi tiết thanh toán</h6>
              <div class="info-card p-3 border bg-light h-100">
                <div class="info-row mb-3 pb-3 border-bottom">
                  <label class="text-secondary tiny-text d-block">GIÁ THANH TOÁN</label>
                  <div class="fw-bold fs-5 text-danger">{{ formatPrice(selectedTicket.giaThanhToan) }}đ</div>
                </div>
                <div class="info-row">
                  <label class="text-secondary tiny-text d-block">KÊNH BÁN</label>
                  <div class="fw-bold">{{ selectedTicket.loaiVe === 0 ? 'TẠI QUẦY' : 'TRỰC TUYẾN' }}</div>
                </div>
              </div>
            </div>

            <!-- Audit Trail -->
            <div class="col-12">
               <div class="p-3 border rounded-0 bg-white d-flex justify-content-between align-items-center">
                 <div class="d-flex align-items-center gap-3">
                   <div class="icon-box bg-secondary bg-opacity-10 text-secondary p-3"><i class="bi bi-shield-lock fs-4"></i></div>
                   <div>
                     <div class="small fw-bold text-dark">THÔNG TIN ĐỐI SOÁT</div>
                     <div class="text-secondary small">ID Giao dịch: INV-{{ selectedTicket.idHoaDon || 'N/A' }}</div>
                   </div>
                 </div>
                 <div class="text-end">
                    <div class="tiny-text text-secondary">NHÂN VIÊN LẬP</div>
                    <div class="fw-bold text-dark">{{ selectedTicket.nguoiTao || 'System' }}</div>
                 </div>
               </div>
            </div>
          </div>
        </div>


      </div>
    </BaseModal>

    <!-- Professional Print Ticket Template -->
    <MovieTicketTemplate :ticket="selectedTicket" />
  </div>
</template>

<style scoped>
.admin-ticket-page {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.filter-item {
  margin-bottom: 10px;
}

@media print {
  /* Ẩn các thành phần giao diện chính và overlay */
  .admin-staff-layout,
  .el-overlay,
  .el-overlay-dialog,
  .base-modal-overlay,
  .admin-ticket-page > *:not(.movie-ticket-wrapper) {
    display: none !important;
  }

  /* Đưa template in vé lên đầu trang */
  .movie-ticket-wrapper {
    display: block !important;
    position: absolute !important;
    top: 0 !important;
    left: 0 !important;
    width: 80mm !important;
    margin: 0 !important;
    padding: 0 !important;
    z-index: 99999 !important;
    background: white !important;
  }

  body, html {
    margin: 0 !important;
    padding: 0 !important;
    height: auto !important;
    overflow: visible !important;
  }
}
</style>
