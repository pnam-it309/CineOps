<template>
  <div class="admin-invoices-container">
    <el-card class="filter-card" shadow="never">
      <div class="filter-header">
        <h3>Quản lý Hóa Đơn</h3>
      </div>
      
      <el-row :gutter="15" class="filter-row">
        <el-col :span="6">
          <el-input v-model="listQuery.tuKhoa" placeholder="Nhập mã hóa đơn..." clearable @clear="handleFilter">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        
        <el-col :span="4">
          <el-select v-model="listQuery.trangThai" placeholder="Trạng thái" clearable @change="handleFilter" style="width: 100%">
            <el-option label="Đã thanh toán" :value="1" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </el-col>

        <el-col :span="4">
          <el-select v-model="listQuery.phuongThucThanhToan" placeholder="Thanh toán" clearable @change="handleFilter" style="width: 100%">
            <el-option label="Tiền mặt" :value="0" />
            <el-option label="Chuyển khoản" :value="1" />
          </el-select>
        </el-col>

        <el-col :span="10">
          <el-radio-group v-model="listQuery.kyThoiGian" @change="handleFilter">
            <el-radio-button label="">Tất cả</el-radio-button>
            <el-radio-button label="TODAY">Hôm nay</el-radio-button>
            <el-radio-button label="THIS_WEEK">Tuần này</el-radio-button>
            <el-radio-button label="THIS_MONTH">Tháng này</el-radio-button>
          </el-radio-group>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="invoices" border stripe style="width: 100%">
        <el-table-column type="index" label="STT" width="60" align="center" />
        <el-table-column prop="maHoaDon" label="Mã HĐ" width="150" />
        
        <el-table-column prop="ngayTao" label="Ngày tạo" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.ngayTao) }}
          </template>
        </el-table-column>

        <el-table-column prop="tongTienThanhToan" label="Tổng tiền" align="right">
          <template #default="scope">
            <strong class="text-danger">{{ formatCurrency(scope.row.tongTienThanhToan) }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="kemBanHang" label="Kênh bán" align="center" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.kemBanHang === 0 ? 'info' : 'success'" effect="dark">
              {{ scope.row.kemBanHang === 0 ? '🏠 Tại quầy' : '🌐 Online' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="phuongThucThanhToan" label="Hình thức" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.phuongThucThanhToan === 0 ? 'success' : 'warning'">
              {{ scope.row.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="trangThai" label="Trạng thái" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.trangThai === 1 ? 'success' : 'danger'">
              {{ scope.row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Thao tác" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetails(scope.row.id)">
              Chi tiết
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="listQuery.page"
          v-model:page-size="listQuery.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="Chi tiết Hóa Đơn" width="60%">
      <el-table v-loading="detailLoading" :data="invoiceDetails" border>
        <el-table-column type="index" label="STT" width="50" align="center" />
        <el-table-column prop="tenPhim" label="Tên phim / Sản phẩm">
          <template #default="scope">
            <span v-if="scope.row.loai === 0">🎬 {{ scope.row.tenPhim }}</span>
            <span v-else>🍿 Đồ ăn</span>
          </template>
        </el-table-column>
        <el-table-column prop="tenPhongChieu" label="Phòng chiếu" width="120" align="center" />
        <el-table-column prop="viTriGhe" label="Ghế" width="80" align="center" />
        <el-table-column prop="donGia" label="Đơn giá" align="right" width="120">
          <template #default="scope">{{ formatCurrency(scope.row.donGia) }}</template>
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
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import { ElMessage } from 'element-plus';

// --- STATE QUẢN LÝ DỮ LIỆU ---
const invoices = ref([]);
const total = ref(0);
const loading = ref(false);

const dialogVisible = ref(false);
const invoiceDetails = ref([]);
const detailLoading = ref(false);

// Query gửi xuống Backend (trừ page đi 1 vì Spring Boot đếm từ 0)
const listQuery = reactive({
  tuKhoa: '',
  trangThai: null,
  phuongThucThanhToan: null,
  kyThoiGian: '',
  page: 1, // Trên UI hiện từ 1
  size: 10
});

// --- METHODS ---

// 1. Lấy danh sách hóa đơn
const fetchInvoices = async () => {
  loading.value = true;
  try {
    // Clone query ra để format lại page cho Spring Boot
    const params = { ...listQuery, page: listQuery.page - 1 };
    
    // Xóa các key rỗng để URL sạch sẽ
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key];
      }
    });

    const res = await hoaDonService.getInvoices(params);
    // Tùy theo cấu trúc trả về của Axios (thường data nằm trong res.data)
    invoices.value = res.data.content || [];
    total.value = res.data.totalElements || 0;
  } catch (error) {
    ElMessage.error('Không thể tải dữ liệu hóa đơn!');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 2. Lấy chi tiết hóa đơn
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

// 3. Các sự kiện xử lý bộ lọc & phân trang
const handleFilter = () => {
  listQuery.page = 1; // Reset về trang 1 khi lọc
  fetchInvoices();
};

const handleSizeChange = (val) => {
  listQuery.size = val;
  fetchInvoices();
};

const handleCurrentChange = (val) => {
  listQuery.page = val;
  fetchInvoices();
};

// 4. Tiện ích Format
const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('vi-VN'); // Hiển thị chuẩn VN: DD/MM/YYYY, HH:mm:ss
};

// --- LIFECYCLE ---
onMounted(() => {
  fetchInvoices();
});
</script>

<style scoped>
.admin-invoices-container {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.filter-header {
  margin-bottom: 15px;
}
.filter-row {
  display: flex;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.text-danger {
  color: #f56c6c;
}
</style>