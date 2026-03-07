<template>
  <div class="admin-seats-page">
    <!-- Hiển thị trực tiếp component theo activeTab, không dùng wrapper box -->
    <SeatListTab 
      v-if="activeTab === 'list'"
      v-model:selectedRoom="selectedRoom"
      :seats="seats"
      :loaiGheList="loaiGheList"
      :phongChieuList="phongChieuList"
      :loading="loading"
      @fetch-seats="fetchSeats"
      @open-dialog="openDialog"
      @update-status="handleUpdateStatus"
      @reset-filter="resetFilter"
    />

    <SeatLayoutTab 
      v-else-if="activeTab === 'layout'"
      v-model:selectedRoom="selectedRoom"
      :seats="seats"
      :phongChieuList="phongChieuList"
      @fetch-seats="fetchSeats"
      @open-dialog="openDialog"
      @bulk-edit="openBulkDialog"
    />

    <SeatConfigTab 
      v-else-if="activeTab === 'config'"
      :phongChieuList="phongChieuList"
      :loaiGheList="loaiGheList"
      :generating="generating"
      @handle-generate="handleGenerate"
    />

    <!-- Dialog thêm/sửa -->
    <SeatDialog 
      v-model:visible="dialogVisible"
      :editingId="editingId"
      :isBulk="isBulkMode"
      :bulkCount="selectedSeatIds.length"
      :initialData="form"
      :phongChieuList="phongChieuList"
      :loaiGheList="loaiGheList"
      :saving="saving"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup>
import { ROUTES_CONSTANTS } from '@/constants/routeConstants';
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import { gheService } from '@/services/api/admin/gheService';

// Child components
import SeatListTab from './SeatListTab.vue';
import SeatLayoutTab from './SeatLayoutTab.vue';
import SeatConfigTab from './SeatConfigTab.vue';
import SeatDialog from './SeatDialog.vue';

// =================== STATE ===================
const route = useRoute();
const activeTab = computed(() => {
  if (route.name === ROUTES_CONSTANTS.ADMIN.children.SEATS.children.LAYOUT.name) return 'layout';
  if (route.name === ROUTES_CONSTANTS.ADMIN.children.SEATS.children.CONFIG.name) return 'config';
  return 'list'; // default
});

const loading = ref(false);
const saving = ref(false);
const generating = ref(false);
const dialogVisible = ref(false);
const editingId = ref(null);
const isBulkMode = ref(false);
const selectedSeatIds = ref([]);

const seats = ref([]);
const loaiGheList = ref([]);
const phongChieuList = ref([]);
const selectedRoom = ref(null);
const form = ref(null);

// Xóa watcher query cũ

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

const handleGenerate = (configData) => {
  if (!configData.idPhongChieu) return notification.warning('Vui lòng chọn phòng');
  
  confirmDialog.custom(
    'Hệ thống sẽ XÓA HẾT ghế cũ của phòng này để tạo bộ mới. Tiếp tục?',
    'Cảnh báo xóa dữ liệu',
    'Đồng ý'
  ).then(async () => {
    generating.value = true;
    try {
      await gheService.generateSeats(configData);
      notification.success('Khởi tạo thành công!');
      selectedRoom.value = configData.idPhongChieu;
      // Chuyển sang tab sơ đồ sau khi tạo
      activeTab.value = 'layout';
      await fetchSeats();
    } catch (e) {
      notification.error('Lỗi khi khởi tạo!');
    } finally {
      generating.value = false;
    }
  }).catch(() => { });
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  isBulkMode.value = false;
  selectedSeatIds.value = [];
  
  if (row) {
    form.value = { ...row };
  } else {
    form.value = null; // trigger default in dialog
  }
  dialogVisible.value = true;
};

const openBulkDialog = (ids) => {
  editingId.value = null;
  isBulkMode.value = true;
  selectedSeatIds.value = ids;
  form.value = null; // trigger default in dialog
  dialogVisible.value = true;
};

const handleSubmit = async (formData) => {
  try {
    if (isBulkMode.value) {
      await confirmDialog.custom(
        `Bạn có chắc muốn cập nhật trạng thái/loại cho ${selectedSeatIds.value.length} ghế?`,
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
        ids: selectedSeatIds.value,
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
  height: calc(100vh - 60px);
  overflow: hidden;
  background-color: #ffffff;
}
</style>
