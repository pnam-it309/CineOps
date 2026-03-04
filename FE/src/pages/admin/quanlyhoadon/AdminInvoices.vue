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
          <el-input v-model="listQuery.tuKhoa" placeholder="Nhập mã hóa đơn..." :prefix-icon="Search" clearable
                      @keyup.enter="handleFilter" @clear="handleFilter" />
        </div>
        <div class="filter-item">
          <el-select v-model="listQuery.trangThai" placeholder="Trạng thái" clearable
                      @change="handleFilter">
              <el-option label="Thành công" :value="1"/>
              <el-option label="Đã hủy" :value="0"/>
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="listQuery.phuongThucThanhToan" placeholder="Thanh toán" clearable
                      @change="handleFilter">
              <el-option label="Tiền mặt" :value="0"/>
              <el-option label="Chuyển khoản" :value="1"/>
          </el-select>
        </div>
        <div class="filter-item">
            <el-select v-model="listQuery.kyThoiGian" placeholder="Kỳ thời gian" clearable @change="handleFilter">
              <el-option label="Tất cả thời gian" value=""/>
              <el-option label="Hôm nay" value="TODAY"/>
              <el-option label="Tuần này" value="THIS_WEEK"/>
              <el-option label="Tháng này" value="THIS_MONTH"/>
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
            :showCheckbox="false"
            v-model:current-page="listQuery.page"
            :page-size="listQuery.size" 
            v-model:page-size="listQuery.size"
            :hide-pagination="true"
          >
            <template #cell-stt="{ index }">
              <span class="fw-bold text-secondary">{{ (listQuery.page - 1) * listQuery.size + index + 1 }}</span>
            </template>

            <template #cell-maHoaDon="{ row }">
              <span class="fw-bold">{{ row.maHoaDon }}</span>
            </template>

            <template #cell-ngayTao="{ row }">
              <span>{{ formatDate(row.ngayTao) }}</span>
            </template>

            <template #cell-tongTien="{ row }">
              <span class="text-secondary">{{ formatCurrency(row.tongTien) }}</span>
            </template>

            <template #cell-soTienGiam="{ row }">
              <span class="text-danger">-{{ formatCurrency(row.soTienGiam) }}</span>
            </template>

            <template #cell-tongTienThanhToan="{ row }">
              <strong class="">{{ formatCurrency(row.tongTienThanhToan) }}</strong>
            </template>

            <template #cell-kemBanHang="{ row }">
              <el-tag :type="row.kemBanHang === 0 ? 'info' : 'success'" size="large">
                {{ row.kemBanHang === 0 ? 'Tại quầy' : 'Online' }}
              </el-tag>
            </template>

            <template #cell-phuongThucThanhToan="{ row }">
              <el-tag :type="row.phuongThucThanhToan === 0 ? 'success' : 'warning'" size="large">
                {{ row.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}
              </el-tag>
            </template>

            <template #cell-trangThai="{ row }">
              <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="large" round style="white-space: nowrap;">
                  {{ row.trangThai === 1 ? 'Thành công' : 'Đã hủy' }}
              </el-tag>
            </template>

            <template #actions="{ row }">
              <div class="d-flex gap-1 justify-content-center align-items-center">
                <el-tooltip content="Chi tiết hóa đơn" placement="top">
                  <button class="btn-action-icon btn-action-view" @click="viewDetails(row)">
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
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import BaseModal from '@/components/common/BaseModal.vue';
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

const tableColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'MÃ HÓA ĐƠN', key: 'maHoaDon' , width: '230px'},
  { label: 'NGÀY TẠO', key: 'ngayTao', width: '230px' },
  { label: 'TỔNG TIỀN', key: 'tongTien', width: '130px' },
  { label: 'GIẢM GIÁ', key: 'soTienGiam', width: '110px' },
  { label: 'THANH TOÁN', key: 'tongTienThanhToan', width: '140px' },
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

const viewDetails = (row) => {
  sessionStorage.setItem('currentInvoice', JSON.stringify(row));
  router.push({ name: 'AdminInvoiceDetail', params: { id: row.id } });
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

const formatDateShort = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) + ' ' + 
         date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
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

<style scoped>
.invoice-meta-header {
  border-left: 5px solid var(--el-color-primary);
}
.ticket-detail-item {
  transition: all 0.3s ease;
  background: #fff;
}
.ticket-detail-item:hover {
  border-color: var(--el-color-primary) !important;
  background: #f0f7ff;
}
.poster-mini {
  border: 1px solid #eee;
}
.snack-table-wrapper {
  background: #fff;
}
.invoice-footer-billing {
  border-top: 1px dashed #dcdfe6;
}
.section-title i {
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}
</style>