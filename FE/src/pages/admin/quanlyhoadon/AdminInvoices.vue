<template>
  <div class="movie-management w-100 h-100 bg-transparent overflow-hidden">
    <AdminTableLayout
      title="Quản lý Hóa Đơn"
      titleIcon="bi bi-receipt"
      :data="invoices"
      :loading="loading"
      :total="total"
      v-model:currentPage="listQuery.page"
      v-model:pageSize="listQuery.size"
      @reset-filter="handleReset"
    >
      <template #filters>
        <div class="filter-item">
          <el-input 
            v-model="listQuery.tuKhoa" 
            placeholder="Mã đơn, Tên KH, SĐT..." 
            :prefix-icon="Search" 
            clearable
            @keyup.enter="handleFilter" 
            @clear="handleFilter" 
            style="width: 230px"
          />
        </div>

        <div class="filter-item">
          <el-select v-model="listQuery.trangThai" placeholder="Trạng thái" clearable @change="handleFilter" style="width: 130px">
              <el-option label="Thành công" :value="1"/>
              <el-option label="Đã hủy" :value="0"/>
          </el-select>
        </div>

        <div class="filter-item">
          <el-date-picker
            v-model="customDateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="Từ ngày"
            end-placeholder="Đến ngày"
            format="DD/MM/YYYY"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            style="width: 250px"
          />
        </div>

        <div class="filter-item">
            <el-select v-model="listQuery.kyThoiGian" placeholder="Kỳ thời gian" clearable @change="handlePeriodChange" style="width: 140px">
              <el-option label="Hôm nay" value="TODAY"/>
              <el-option label="Tuần này" value="THIS_WEEK"/>
              <el-option label="Tháng này" value="THIS_MONTH"/>
            </el-select>
        </div>

        <div class="filter-item d-flex align-items-center gap-1">
          <el-input-number 
            v-model="listQuery.minPrice" 
            :controls="false" 
            placeholder="Giá từ" 
            @change="handleFilter"
            style="width: 100px"
          />
          <span class="text-secondary">-</span>
          <el-input-number 
            v-model="listQuery.maxPrice" 
            :controls="false" 
            placeholder="Đến" 
            @change="handleFilter"
            style="width: 100px"
          />
        </div>

        <div class="filter-item">
          <el-select v-model="listQuery.phuongThucThanhToan" placeholder="Thanh toán" clearable @change="handleFilter" style="width: 130px">
              <el-option label="Tiền mặt" :value="0"/>
              <el-option label="Chuyển khoản" :value="1"/>
          </el-select>
        </div>
        <div class="filter-item">
  <el-select v-model="listQuery.sortDir" placeholder="Sắp xếp" @change="handleFilter" style="width: 160px">
    <el-option label="Mới nhất trước" value="DESC">
      <i class="bi bi-sort-numeric-down me-2"></i> Mới nhất trước
    </el-option>
    <el-option label="Cũ nhất trước" value="ASC">
      <i class="bi bi-sort-numeric-up me-2"></i> Cũ nhất trước
    </el-option>
  </el-select>
</div>
      </template>

      <template #content>
        <div class="h-100 overflow-hidden">
          <BaseTable 
            v-loading="loading" 
            :data="invoices" 
            :columns="tableColumns"
            :total="total" 
            v-model:current-page="listQuery.page"
            v-model:page-size="listQuery.size"
            :hide-pagination="true"
          >
            <template #cell-stt="{ index }">
              <span class="fw-bold text-secondary">{{ (listQuery.page - 1) * listQuery.size + index + 1 }}</span>
            </template>

            <template #cell-maHoaDon="{ row }">
              <span class="fw-bold text-primary">#{{ row.maHoaDon }}</span>
            </template>

            <template #cell-ngayTao="{ row }">
              <span style="white-space: nowrap;">{{ formatDate(row.ngayTao) }}</span>
            </template>

            <template #cell-tongTien="{ row }">
              <span class="text-secondary">{{ formatCurrency(row.tongTien) }}</span>
            </template>

            <template #cell-soTienGiam="{ row }">
              <span class="text-danger">{{ row.soTienGiam > 0 ? '-' + formatCurrency(row.soTienGiam) : '0 ₫' }}</span>
            </template>

            <template #cell-tongTienThanhToan="{ row }">
              <strong class="text-dark">{{ formatCurrency(row.tongTienThanhToan) }}</strong>
            </template>

            <template #cell-kemBanHang="{ row }">
              <el-tag :type="row.kemBanHang === 0 ? 'info' : 'success'" size="small">
                {{ row.kemBanHang === 0 ? 'Tại quầy' : 'Online' }}
              </el-tag>
            </template>

            <template #cell-phuongThucThanhToan="{ row }">
              <span class="small">{{ row.phuongThucThanhToan === 0 ? '💵 Tiền mặt' : '💳 Chuyển khoản' }}</span>
            </template>

            <template #cell-trangThai="{ row }">
              <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round>
                  {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
              </el-tag>
            </template>

            <template #actions="{ row }">
              <div class="d-flex gap-1 justify-content-center">
                <el-tooltip content="Chi tiết hóa đơn">
                  <button class="btn-action-icon action-view" @click="viewDetails(row)">
                    <i class="bi bi-eye"></i>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </BaseTable>
        </div>
      </template>
    </AdminTableLayout>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseTable from '@/components/common/BaseTable.vue';

// --- STATE QUẢN LÝ DỮ LIỆU ---
const router = useRouter();
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);
const customDateRange = ref([]); // Biến hứng dữ liệu từ DatePicker [cite: 2026-03-04]

const tableColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Mã hóa đơn', key: 'maHoaDon' , width: '180px'},
  { label: 'Ngày tạo', key: 'ngayTao', width: '170px' },
  { label: 'Tổng tiền', key: 'tongTien', width: '120px' },
  { label: 'Giảm giá', key: 'soTienGiam', width: '100px' },
  { label: 'Thanh toán', key: 'tongTienThanhToan', width: '130px' },
  { label: 'Kênh bán', key: 'kemBanHang', width: '110px' },
  { label: 'Hình thức', key: 'phuongThucThanhToan', width: '140px' },
  { label: 'Trạng thái', key: 'trangThai', width: '120px' }
];

// Query đầy đủ trường khớp với Backend
const listQuery = reactive({
  tuKhoa: '',
  trangThai: null,
  phuongThucThanhToan: null,
  kyThoiGian: '',
  tuNgay: null,
  denNgay: null,
  minPrice: null,
  maxPrice: null,
  sortDir: 'DESC',
  page: 1,
  size: 10
});

// --- METHODS ---

// Xử lý khi chọn khoảng ngày trên lịch [cite: 2026-03-04]
const handleDateChange = (val) => {
  if (val && val.length === 2) {
    listQuery.tuNgay = val[0];
    listQuery.denNgay = val[1];
    listQuery.kyThoiGian = ''; // Reset kỳ cố định để dùng ngày tự chọn
  } else {
    listQuery.tuNgay = null;
    listQuery.denNgay = null;
  }
  handleFilter();
};

// Xử lý khi chọn Kỳ cố định (Hôm nay/Tuần này)
const handlePeriodChange = () => {
  customDateRange.value = []; // Xóa trắng ô lịch
  listQuery.tuNgay = null;
  listQuery.denNgay = null;
  handleFilter();
};

const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    
    // Thuật toán Clean Params: Xóa trường rỗng tránh lỗi 400 Backend
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) delete params[key];
    });

    const res = await hoaDonService.getInvoices(params);
    invoices.value = res.data?.content || [];
    total.value = res.data?.totalElements || 0;
  } catch (error) {
    ElMessage.error('Không thể tải dữ liệu hóa đơn!');
  } finally {
    loading.value = false;
  }
};

const viewDetails = (row) => {
  sessionStorage.setItem('currentInvoice', JSON.stringify(row));
  router.push({ name: 'AdminInvoiceDetail', params: { id: row.id } });
};

const handleFilter = () => {
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const handleReset = () => {
  Object.assign(listQuery, {
    tuKhoa: '', trangThai: null, phuongThucThanhToan: null,
    kyThoiGian: '', tuNgay: null, denNgay: null,
    minPrice: null, maxPrice: null, page: 1,sortDir: 'DESC'
  });
  customDateRange.value = []; // Reset UI lịch [cite: 2026-03-04]
  fetchInvoices();
};

// --- FORMATTERS ---
const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  const day  = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  return `${time} ${day}`;
};

watch(() => listQuery.page, fetchInvoices);
watch(() => listQuery.size, () => { 
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
});

onMounted(fetchInvoices);
</script>

<style scoped>
.filter-item { margin-bottom: 10px; }
.btn-action-icon {
  width: 32px; height: 32px; border-radius: 8px; border: 1px solid #eee;
  background: white; cursor: pointer; transition: all 0.2s;
}
.btn-action-icon:hover { background-color: #f8fafc; color: #409eff; border-color: #409eff; }
</style>