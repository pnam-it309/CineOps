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
        <div class="filter-item flex-grow-1" style="max-width:300px;">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-input v-model="listQuery.tuKhoa" placeholder="Nhập mã hóa đơn..." :prefix-icon="Search" clearable
                      @keyup.enter="handleFilter" @clear="handleFilter" />
        </div>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="listQuery.trangThai" placeholder="Trạng thái" clearable style="width:160px;"
                      @change="handleFilter">
              <el-option label="Thành công" :value="1"/>
              <el-option label="Đã hủy" :value="0"/>
          </el-select>
        </div>
        <div class="filter-item">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
          <el-select v-model="listQuery.phuongThucThanhToan" placeholder="Thanh toán" clearable style="width:160px;"
                      @change="handleFilter">
              <el-option label="Tiền mặt" :value="0"/>
              <el-option label="Chuyển khoản" :value="1"/>
          </el-select>
        </div>
        <div class="filter-item ms-3">
            <el-radio-group v-model="listQuery.kyThoiGian" @change="handleFilter" class="mt-1">
              <el-radio-button value="">Tất cả</el-radio-button>
              <el-radio-button value="TODAY">Hôm nay</el-radio-button>
              <el-radio-button value="THIS_WEEK">Tuần này</el-radio-button>
              <el-radio-button value="THIS_MONTH">Tháng này</el-radio-button>
            </el-radio-group>
        </div>
      </template>

      <template #content>
        <div class="h-100 overflow-hidden">
          <BaseTable 
            v-loading="loading" 
            :data="invoices" 
            :columns="tableColumns"
            :total="total" 
            :showCheckbox="false"
            v-model:current-page="listQuery.page"
            :page-size="listQuery.size" 
            v-model:page-size="listQuery.size"
            :hide-pagination="true"
          >
            <template #cell-stt="{ index }">
              <span class="small fw-bold text-secondary">{{ (listQuery.page - 1) * listQuery.size + index + 1 }}</span>
            </template>

            <template #cell-maHoaDon="{ row }">
              <span class="fw-bold text-dark">{{ row.maHoaDon }}</span>
            </template>

            <template #cell-ngayTao="{ row }">
              <span>{{ formatDate(row.ngayTao) }}</span>
            </template>

            <template #cell-tongTienThanhToan="{ row }">
              <strong class="text-danger">{{ formatCurrency(row.tongTienThanhToan) }}</strong>
            </template>

            <template #cell-kemBanHang="{ row }">
              <el-tag :type="row.kemBanHang === 0 ? 'info' : 'success'" size="large" effect="dark">
                {{ row.kemBanHang === 0 ? '🏠 Tại quầy' : '🌐 Online' }}
              </el-tag>
            </template>

            <template #cell-phuongThucThanhToan="{ row }">
              <el-tag :type="row.phuongThucThanhToan === 0 ? 'success' : 'warning'" size="large">
                {{ row.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}
              </el-tag>
            </template>

            <template #cell-trangThai="{ row }">
              <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="large" effect="dark" round style="white-space: nowrap;">
                  {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
              </el-tag>
            </template>

            <template #actions="{ row }">
              <div class="d-flex gap-1 justify-content-center align-items-center">
                <el-tooltip content="Chi tiết hóa đơn" placement="top">
                  <button class="btn-action-icon btn-action-view" @click="viewDetails(row.id)">
                    <i class="bi bi-eye"></i>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </BaseTable>
        </div>
      </template>
    </AdminTableLayout>

    <el-dialog v-model="dialogVisible" title="Chi tiết Hóa Đơn" width="60%">
      <el-table v-loading="detailLoading" :data="invoiceDetails" border class="admin-table">
        <el-table-column type="index" label="STT" width="70" align="center" />
        <el-table-column prop="tenPhim" label="Tên phim / Sản phẩm">
          <template #default="scope">
            <span v-if="scope.row.loai === 0">🎬 {{ scope.row.tenPhim }}</span>
            <span v-else>🍿 Đồ ăn</span>
          </template>
        </el-table-column>
        <el-table-column prop="tenPhongChieu" label="Phòng chiếu" width="150" align="center" />
        <el-table-column prop="viTriGhe" label="Ghế" width="100" align="center" />
        <el-table-column prop="donGia" label="Đơn giá" align="right" width="160">
          <template #default="scope">
            <strong class="text-danger">{{ formatCurrency(scope.row.donGia) }}</strong>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Đóng</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>



<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseTable from '@/components/common/BaseTable.vue';

// --- STATE QUẢN LÝ DỮ LIỆU ---
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);

const dialogVisible = ref(false);
const invoiceDetails = ref([]);
const detailLoading = ref(false);

const tableColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'MÃ HĐ', key: 'maHoaDon' },
  { label: 'NGÀY TẠO', key: 'ngayTao', width: '180px' },
  { label: 'TỔNG TIỀN', key: 'tongTienThanhToan' },
  { label: 'KÊNH BÁN', key: 'kemBanHang', width: '140px' },
  { label: 'HÌNH THỨC', key: 'phuongThucThanhToan', width: '160px' },
  { label: 'TRẠNG THÁI', key: 'trangThai', width: '140px' }
];

// Query
const listQuery = reactive({
  tuKhoa: '',
  trangThai: null,
  phuongThucThanhToan: null,
  kyThoiGian: '',
  page: 1,
  size: 10
});

// --- METHODS ---
const fetchInvoices = async () => {
  loading.value = true;
  try {
    const params = { ...listQuery, page: listQuery.page - 1 };
    
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key];
      }
    });

    const res = await hoaDonService.getInvoices(params);
    invoices.value = res.data.content || [];
    total.value = res.data.totalElements || 0;
  } catch (error) {
    ElMessage.error('Không thể tải dữ liệu hóa đơn!');
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

const handleFilter = () => {
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const handleReset = () => {
  listQuery.tuKhoa = '';
  listQuery.trangThai = null;
  listQuery.phuongThucThanhToan = null;
  listQuery.kyThoiGian = '';
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
};

const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('vi-VN');
};

watch(() => listQuery.page, fetchInvoices);
watch(() => listQuery.size, () => { 
  if (listQuery.page !== 1) listQuery.page = 1;
  else fetchInvoices();
});

onMounted(() => {
  fetchInvoices();
});
</script>