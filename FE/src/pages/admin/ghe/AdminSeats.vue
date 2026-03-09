<template>
  <div class="admin-seats-page d-flex flex-column h-100">
    <AdminTableLayout 
      title="Quản lý ghế" 
      titleIcon="bi bi-grid-3x3-gap-fill" 
      addButtonLabel="Thêm ghế lẻ"
      :total="totalSeats"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog"
      @reset-filter="resetFilter"
    >
    <template #filters>
      <div class="filter-item">
        <el-input 
          v-model="searchQuery" 
          placeholder="Tìm kiếm theo số ghế..." 
          :prefix-icon="Search"
          style="width: 250px;"
          clearable 
        />
      </div>
      <div class="filter-item">
        <el-select 
          v-model="selectedRoom" 
          placeholder="Chọn phòng chiếu" 
          style="width: 200px;" 
          @change="fetchSeats"
        >
          <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
        </el-select>
      </div>
      <div class="filter-item">
        <el-select v-model="filterLoaiGhe" placeholder="Chọn loại ghế" style="width: 200px;">
          <el-option value="" label="Tất cả loại ghế" />
          <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
        </el-select>
      </div>
    </template>

    <!-- Header Actions Left Slot -->
    <template #header-actions-left>
      <el-button v-if="selectedSeats.length" type="warning" plain round :icon="Refresh" @click="handleBulkToggleStatus">
        Đổi trạng thái {{ selectedSeats.length }} ghế
      </el-button>
    </template>

    <!-- Main Content Table Slot -->
    <template #content>
      <SeatListTab 
        :seats="seats"
        :loaiGheList="loaiGheList"
        :phongChieuList="phongChieuList"
        :loading="loading"
        :searchQuery="searchQuery"
        :filterLoaiGhe="filterLoaiGhe"
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        v-model:selectedSeats="selectedSeats"
        @open-dialog="openDialog"
        @update-status="handleUpdateStatus"
      />
    </template>

    </AdminTableLayout>

    <!-- Dialogs -->
    <SeatDialog 
      v-model:visible="dialogVisible"
      :editingId="editingId"
      :isBulk="isBulkMode"
      :bulkCount="selectedSeats.length"
      :initialData="form"
      :phongChieuList="phongChieuList"
      :loaiGheList="loaiGheList"
      :saving="saving"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import SeatListTab from './SeatListTab.vue';
import SeatDialog from './SeatDialog.vue';
import { gheService } from '@/services/api/admin/gheService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import { Search, Monitor, PriceTag, Refresh } from '@element-plus/icons-vue';

// =================== STATE ===================
const getSeatTypeTag = (tenLoai) => {
  if (!tenLoai) return 'info';
  const l = tenLoai.toLowerCase();
  if (l.includes('vip')) return 'warning';
  if (l.includes('couple') || l.includes('sweetbox')) return 'danger';
  return 'info';
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0);
const searchQuery = ref('');
const filterLoaiGhe = ref('');
const currentPage = ref(1);
const pageSize = ref(5);
const selectedSeats = ref([]);

const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const batchDialogVisible = ref(false);
const batchResult = ref(null);
const selectedShowtime = ref(null);
const selectedItem = ref(null);
const editingId = ref(null);
const isBulkMode = ref(false);

const seats = ref([]);
const loaiGheList = ref([]);
const phongChieuList = ref([]);
const selectedRoom = ref(null);
const form = ref({});

const filteredSeatsCount = computed(() => {
  return seats.value.filter(s => {
    const matchLoai = filterLoaiGhe.value === '' || s.idLoaiGhe === filterLoaiGhe.value;
    const matchSearch = !searchQuery.value || s.soGhe?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchLoai && matchSearch;
  }).length;
});

const totalSeats = computed(() => filteredSeatsCount.value);

// =================== METHODS ===================
const fetchDropdowns = async () => {
  try {
    const [loaiRes, phongRes] = await Promise.all([
      gheService.getLoaiGheList(),
      gheService.getPhongChieuDropdown()
    ]);
    loaiGheList.value = loaiRes.data?.data || [];
    phongChieuList.value = phongRes.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải dữ liệu dropdown');
  }
};

const fetchSeats = async () => {
  if (!selectedRoom.value) {
    seats.value = [];
    return;
  }
  loading.value = true;
  try {
    const res = await gheService.getSeatsByRoom(selectedRoom.value);
    seats.value = res.data?.data || [];
  } catch (e) {
    notification.error('Không thể tải danh sách ghế');
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  if (phongChieuList.value.length > 0) {
    selectedRoom.value = phongChieuList.value[0].id;
  } else {
    selectedRoom.value = null;
  }
  fetchSeats();
};



const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  isBulkMode.value = false;
  
  if (row) {
    form.value = { ...row };
  } else {
    form.value = null; // trigger default in dialog
  }
  dialogVisible.value = true;
};

const handleBulkToggleStatus = () => {
  handleUpdateStatus(selectedSeats.value);
};

const handleSubmit = async (formData) => {
  try {
    if (isBulkMode.value) {
      await confirmDialog.custom(
        `Bạn có chắc muốn cập nhật trạng thái/loại cho ${selectedSeats.value.length} ghế?`,
        'Xác nhận cập nhật hàng loạt',
        'Cập nhật'
      );
    } else if (editingId.value) {
      await confirmDialog.update('ghế');
    } else {
      await confirmDialog.add('ghế');
    }
  } catch { return; }

  saving.value = true;
  try {
    if (isBulkMode.value) {
      await gheService.updateBulkSeats({
        ids: selectedSeats.value.map(s => s.id),
        idLoaiGhe: formData.idLoaiGhe || null,
        trangThai: formData.trangThai
      });
      notification.success('Cập nhật hàng loạt thành công!');
    } else if (editingId.value) {
      await gheService.updateSeat(editingId.value, formData);
      notification.updateSuccess('ghế');
    } else {
      await gheService.createSeat(formData);
      notification.addSuccess('ghế');
    }
    dialogVisible.value = false;
    await fetchSeats();
  } catch (e) {
    notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
  } finally {
    saving.value = false;
  }
};

const handleUpdateStatus = async (target) => {
  const isBulk = Array.isArray(target);
  let items = isBulk ? target : [target.row];
  let newStatus = isBulk ? (items[0].trangThai === 1 ? 0 : 1) : target.status;
  
  if (items.length === 0) return;

  try {
    const label = newStatus === 1 ? 'Hoạt động' : 'Bảo trì';
    if (!isBulk) {
      const row = items[0];
      await confirmDialog.custom(`Đổi trạng thái ghế ${row.soGhe} sang ${label}?`, 'Xác nhận');
      saving.value = true;
      await gheService.updateSeat(row.id, { ...row, trangThai: newStatus });
      notification.success('Cập nhật trạng thái thành công');
    } else {
      await confirmDialog.custom(`Đổi trạng thái ${items.length} ghế sang ${label}?`, 'Xác nhận hàng loạt');
      saving.value = true;
      await gheService.updateBulkSeats({
        ids: items.map(i => i.id),
        trangThai: newStatus
      });
      notification.success(`Đã chuyển ${items.length} ghế sang ${label}`);
    }
    await fetchSeats();
  } catch (e) {
    if (e !== 'cancel') {
      notification.error('Thao tác thất bại');
    }
  } finally {
    saving.value = false;
  }
};

onMounted(async () => {
  await fetchDropdowns();
  if (phongChieuList.value.length) {
    selectedRoom.value = phongChieuList.value[0].id;
    await fetchSeats();
  }
});
</script>

<style scoped>
.admin-seats-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
