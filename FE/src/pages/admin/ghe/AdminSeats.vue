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
      @delete-seat="handleDelete"
      @reset-filter="resetFilter"
    />

    <SeatLayoutTab 
      v-else-if="activeTab === 'layout'"
      v-model:selectedRoom="selectedRoom"
      :seats="seats"
      :phongChieuList="phongChieuList"
      @fetch-seats="fetchSeats"
      @open-dialog="openDialog"
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
      :initialData="form"
      :phongChieuList="phongChieuList"
      :loaiGheList="loaiGheList"
      :saving="saving"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { gheService } from '@/services/api/admin/gheService';

// Child components
import SeatListTab from './SeatListTab.vue';
import SeatLayoutTab from './SeatLayoutTab.vue';
import SeatConfigTab from './SeatConfigTab.vue';
import SeatDialog from './SeatDialog.vue';

// =================== STATE ===================
const route = useRoute();
const activeTab = ref(route.query.tab || 'list');

const loading = ref(false);
const saving = ref(false);
const generating = ref(false);
const dialogVisible = ref(false);
const editingId = ref(null);

const seats = ref([]);
const loaiGheList = ref([]);
const phongChieuList = ref([]);
const selectedRoom = ref(null);
const form = ref(null);

// Theo dõi thay đổi query tab để chuyển đổi component
watch(() => route.query.tab, (newTab) => {
  activeTab.value = newTab || 'list';
});

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
    ElMessage.error('Không thể tải dữ liệu dropdown');
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
    ElMessage.error('Không thể tải danh sách ghế');
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
  if (!configData.idPhongChieu) return ElMessage.warning('Vui lòng chọn phòng');
  
  ElMessageBox.confirm(
    'Hệ thống sẽ XÓA HẾT ghế cũ của phòng này để tạo bộ mới. Tiếp tục?',
    'Cảnh báo xóa dữ liệu',
    { confirmButtonText: 'Đồng ý', cancelButtonText: 'Hủy', type: 'warning' }
  ).then(async () => {
    generating.value = true;
    try {
      await gheService.generateSeats(configData);
      ElMessage.success('Khởi tạo thành công!');
      selectedRoom.value = configData.idPhongChieu;
      // Chuyển sang tab sơ đồ sau khi tạo
      activeTab.value = 'layout';
      await fetchSeats();
    } catch (e) {
      ElMessage.error('Lỗi khi khởi tạo!');
    } finally {
      generating.value = false;
    }
  }).catch(() => { });
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  if (row) {
    form.value = { ...row };
  } else {
    form.value = { 
      idPhongChieu: selectedRoom.value || '', 
      idLoaiGhe: loaiGheList.value[0]?.id || '', 
      soGhe: '', 
      soHang: '', 
      soCot: 1, 
      trangThai: 1 
    };
  }
  dialogVisible.value = true;
};

const handleSubmit = async (formData) => {
  saving.value = true;
  try {
    if (editingId.value) {
      await gheService.updateSeat(editingId.value, formData);
      ElMessage.success('Cập nhật thành công');
    } else {
      await gheService.createSeat(formData);
      ElMessage.success('Thêm thành công');
    }
    dialogVisible.value = false;
    await fetchSeats();
  } catch (e) {
    ElMessage.error(e.response?.data?.message || 'Có lỗi xảy ra');
  } finally {
    saving.value = false;
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`Xóa ghế "<b>${row.soGhe}</b>"?`, 'Xác nhận', { dangerouslyUseHTMLString: true, type: 'warning' })
    .then(async () => {
      try {
        await gheService.deleteSeat(row.id);
        ElMessage.success('Đã xóa');
        await fetchSeats();
      } catch (e) { ElMessage.error('Xóa thất bại'); }
    }).catch(() => { });
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
