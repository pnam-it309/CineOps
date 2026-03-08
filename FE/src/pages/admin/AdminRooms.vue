<script setup>
import { ref, reactive, computed } from 'vue';
import { Plus, Edit, Delete, Location, House, Setting, View, Search, Refresh, Monitor, PieChart } from '@element-plus/icons-vue';
import BaseTable from '@/components/common/BaseTable.vue';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import BaseModal from '@/components/common/BaseModal.vue';
import { ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

// Define columns
const roomColumns = [
  { label: 'STT', key: 'stt', width: '60px' },
  { label: 'Phòng chiếu', key: 'room', minWidth: '220px' },
  { label: 'Sức chứa', key: 'capacity', width: '150px' },
  { label: 'Trạng thái', key: 'status', width: '150px' },
];

const selectedRooms = ref([]);
const selectedIds = computed(() => selectedRooms.value.map(r => r.id));

const handleBulkDelete = () => {
    confirmDialog.custom(
        `Thay đổi trạng thái cho <b>${selectedIds.value.length}</b> phòng chiếu đã chọn?`,
        'Cập nhật hàng loạt',
        'Đồng ý'
    ).then(() => {
        rooms.value = rooms.value.map(r => {
            if (selectedIds.value.includes(r.id)) {
                return { ...r, status: r.status === 'Hoạt động' ? 'Ngừng hoạt động' : 'Hoạt động' };
            }
            return r;
        });
        notification.success(`Đã cập nhật trạng thái cho ${selectedIds.value.length} phòng chiếu`);
        selectedRooms.value = [];
    }).catch(() => {});
};

// Mock Cinemas
const cinemas = ref([
  { id: 1, name: 'CineOps Central', address: 'District 1, HCM', rooms: 4 }
]);

const selectedCinema = ref(cinemas.value[0]);
const rooms = ref([
  { id: 101, name: 'Hall 1 (IMAX)', rows: 10, cols: 15, type: 'IMAX', status: 'Hoạt động' },
  { id: 102, name: 'Hall 2 (Standard)', rows: 8, cols: 12, type: '2D/3D', status: 'Hoạt động' },
  { id: 103, name: 'Hall 3 (VIP)', rows: 5, cols: 8, type: 'Gold Class', status: 'Ngừng hoạt động' }
]);

const activeRoom = ref(null);
const designerVisible = ref(false);

const roomConfig = reactive({
  name: '',
  rows: 8,
  cols: 12,
  type: '2D/3D'
});

const openDesigner = (room = null) => {
  if (room) {
    activeRoom.value = room;
    Object.assign(roomConfig, room);
  } else {
    activeRoom.value = null;
    roomConfig.name = '';
    roomConfig.rows = 8;
    roomConfig.cols = 12;
    roomConfig.type = '2D/3D';
  }
  designerVisible.value = true;
};

const handleCreateRoom = () => {
  const newRoom = {
    ...roomConfig,
    id: Date.now()
  };
  rooms.value.push(newRoom);
  designerVisible.value = false;
  notification.success('Room created successfully');
};

const handleUpdateRoom = () => {
  const index = rooms.value.findIndex(r => r.id === activeRoom.value.id);
  rooms.value[index] = { ...activeRoom.value, ...roomConfig };
  designerVisible.value = false;
  notification.success('Room updated');
};

const deleteRoom = (id) => {
  ElMessageBox.confirm('Bạn có chắc chắn muốn xóa phòng chiếu này?', 'Cảnh báo', {
      type: 'warning'
  }).then(() => {
    rooms.value = rooms.value.filter(r => r.id !== id);
    notification.success('Đã xóa phòng chiếu');
  });
};

const getSeatLabel = (row, col) => {
  const rowLabel = String.fromCharCode(64 + row);
  return `${rowLabel}${col}`;
};

const currentPage = ref(1);
const pageSize = ref(5);

// New variables for the updated template
const searchQuery = ref('');
const filterStatus = ref('all');
const filterType = ref('all');
const dialogVisible = ref(false);
const detailVisible = ref(false);
const selectedItem = ref(null);
const editingId = ref(null);

const handleView = (room) => {
  selectedItem.value = room;
  detailVisible.value = true;
};

const roomForm = reactive({
  name: '',
  type: '2D/3D Standard',
  rows: 8,
  cols: 12,
  status: 'Hoạt động',
  note: ''
});

const filteredRooms = computed(() => {
  let filtered = rooms.value;

  if (searchQuery.value) {
    filtered = filtered.filter(room =>
      room.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      room.note.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  if (filterStatus.value !== 'all' && filterStatus.value !== '') {
    filtered = filtered.filter(room => room.status === filterStatus.value);
  }

  if (filterType.value !== 'all' && filterType.value !== '') {
    filtered = filtered.filter(room => room.type === filterType.value);
  }

  return filtered;
});



const openDialog = (room = null) => {
  if (room) {
    editingId.value = room.id;
    Object.assign(roomForm, room);
  } else {
    editingId.value = null;
    roomForm.name = '';
    roomForm.type = '2D/3D Standard';
    roomForm.rows = 8;
    roomForm.cols = 12;
    roomForm.status = 'Hoạt động';
    roomForm.note = '';
  }
  dialogVisible.value = true;
};

const handleSave = () => {
  if (editingId.value) {
    const index = rooms.value.findIndex(r => r.id === editingId.value);
    if (index !== -1) {
      rooms.value[index] = { ...roomForm, id: editingId.value, capacity: roomForm.rows * roomForm.cols };
      notification.updateSuccess('phòng');
    }
  } else {
    const newRoom = {
      id: Date.now(),
      ...roomForm,
      capacity: roomForm.rows * roomForm.cols
    };
    rooms.value.push(newRoom);
    notification.addSuccess('phòng');
  }
  dialogVisible.value = false;
};

const handleDelete = (room) => {
  const isActive = room.status === 'Hoạt động';
  const newStatus = isActive ? 'Ngừng hoạt động' : 'Hoạt động';
  const label = isActive ? 'ngừng hoạt động' : 'kích hoạt';
  
  confirmDialog.custom(
    `Bạn có chắc chắn muốn <b>${label}</b> phòng "${room.name}"?`,
    'Cập nhật trạng thái',
    'Xác nhận'
  ).then(() => {
    const index = rooms.value.findIndex(r => r.id === room.id);
    if (index !== -1) {
      rooms.value[index].status = newStatus;
      notification.success(`Đã cập nhật trạng thái phòng ${room.name}`);
    }
  }).catch(() => {});
};
</script>

<template>
  <div class="admin-rooms-page">
    <AdminTableLayout
      title="Quản lý Phòng chiếu"
      titleIcon="bi bi-door-open-fill"
      addButtonLabel="Thêm phòng mới"
      :data="filteredRooms"
      :loading="false"
      :total="filteredRooms.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog()"
      @reset-filter="() => { searchQuery = ''; filterStatus = 'all'; filterType = 'all'; }"
    >
      <!-- Header Actions Left Slot -->
      <template #header-actions-left>
        <el-button v-if="selectedIds.length" type="warning" plain round :icon="Refresh" @click="handleBulkDelete">
          Đổi trạng thái {{ selectedIds.length }} phòng chiếu
        </el-button>
      </template>

      <!-- Filters Slot -->
      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm tên phòng..."
            :prefix-icon="Search"
            size="default"
            clearable
            style="width: 250px;"
          />
        </div>

        <div class="filter-item">
          <el-select v-model="filterStatus" placeholder="Chọn trạng thái" style="width: 180px;" size="default" clearable>
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Hoạt động" value="Hoạt động" />
            <el-option label="Ngừng hoạt động" value="Ngừng hoạt động" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="filterType" placeholder="Chọn loại phòng" style="width: 180px;" size="default" clearable>
            <el-option label="Tất cả loại" value="all" />
            <el-option label="2D/3D Standard" value="2D/3D Standard" />
            <el-option label="IMAX" value="IMAX" />
            <el-option label="Gold Class" value="Gold Class" />
          </el-select>
        </div>
      </template>

      <template #content>
        <BaseTable
          :data="filteredRooms.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
          :columns="roomColumns"
          :total="filteredRooms.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          v-model:selection="selectedRooms"
          :hide-pagination="true"
          @edit="openDialog"
          @delete="handleDelete"
          @update-status="({ row, val }) => handleDelete(row)"
        >
          <template #cell-status="{ row }">
            <el-tag :type="row.status === 'Hoạt động' ? 'success' : 'info'" size="small" round>
              {{ row.status }}
            </el-tag>
          </template>

          <template #cell-stt="{ index }">
            <span class="small fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-room="{ row }">
            <div class="d-flex align-items-center gap-3">
              <div class="room-icon-box bg-light text-primary rounded-3 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                <i class="bi bi-display fs-5"></i>
              </div>
              <div>
                <div class="fw-bold text-dark small">{{ row.name }}</div>
                <div class="text-secondary" style="font-size: 11px;">{{ row.type }}</div>
              </div>
            </div>
          </template>

          <template #cell-capacity="{ row }">
             <div class="stat-badge bg-light text-dark border px-3 py-1 rounded-pill small d-inline-block">
              <i class="bi bi-people me-1"></i>{{ row.capacity || (row.rows * row.cols) }} ghế
            </div>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center align-items-center gap-1">
              <el-tooltip content="Xem chi tiết" placement="top">
                <button class="btn-action-icon action-view" @click="handleView(row)">
                  <i class="bi bi-eye fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa sơ đồ ghế" placement="top">
                <button class="btn-action-icon action-variant" :disabled="row.status === 'Ngừng hoạt động'">
                  <i class="bi bi-grid-3x3 fs-6"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa thông tin" placement="top">
                <button class="btn-action-icon action-edit" :disabled="row.status === 'Ngừng hoạt động'" @click="openDialog(row)">
                  <i class="bi bi-pencil fs-6"></i>
                </button>
              </el-tooltip>
              <el-switch
                :model-value="row.status === 'Hoạt động'"
                @change="() => handleDelete(row)"
                class="status-switch mx-1"
                inactive-color="#ff4949"
              />
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <!-- Detail Modal -->
    <BaseModal v-model="detailVisible" title="Chi tiết Phòng chiếu" icon="bi bi-door-open" width="500px">
      <div v-if="selectedItem" class="p-3">
        <div class="d-flex align-items-center gap-3 mb-4 p-3 bg-light rounded-4">
          <div class="icon-box bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
            <i class="bi bi-display fs-4"></i>
          </div>
          <div>
            <h5 class="m-0 fw-bold">{{ selectedItem.name }}</h5>
            <el-tag size="small" type="info" round>{{ selectedItem.type }}</el-tag>
          </div>
        </div>
        <div class="detail-grid row g-3">
          <div class="col-6">
            <div class="lbl text-secondary small">Sức chứa</div>
            <div class="val fw-bold fs-5">{{ selectedItem.capacity || (selectedItem.rows * selectedItem.cols) }} ghế</div>
          </div>
          <div class="col-6">
            <div class="lbl text-secondary small">Cấu trúc</div>
            <div class="val fw-semibold">{{ selectedItem.rows }} hàng x {{ selectedItem.cols }} cột</div>
          </div>
          <div class="col-12">
            <div class="lbl text-secondary small">Trạng thái</div>
            <el-tag :type="selectedItem.status === 'Hoạt động' ? 'success' : 'info'" round>
              {{ selectedItem.status }}
            </el-tag>
          </div>
          <div class="col-12" v-if="selectedItem.note">
            <div class="lbl text-secondary small">Ghi chú</div>
            <div class="val text-muted">{{ selectedItem.note }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">Đóng</el-button>
        <el-button type="primary" @click="openDialog(selectedItem); detailVisible = false">Chỉnh sửa</el-button>
      </template>
    </BaseModal>

    <!-- Room Dialog -->
    <BaseModal
      v-model="dialogVisible"
      :title="editingId ? 'Cập nhật Phòng' : 'Thêm Phòng mới'"
      subtitle="Cấu hình thông số kỹ thuật phòng chiếu"
      icon="bi bi-door-open"
      width="550px"
      confirmText="Lưu thông tin"
      @confirm="handleSave"
    >
      <el-form :model="roomForm" label-position="top" class="premium-form">
        <div class="row g-3">
          <div class="col-8">
            <el-form-item label="Tên phòng" required>
              <el-input v-model="roomForm.name" placeholder="VD: Phòng chiếu 01" :prefix-icon="House" />
            </el-form-item>
          </div>
          <div class="col-4">
            <el-form-item label="Loại phòng">
              <el-select v-model="roomForm.type" class="w-100">
                <template #prefix><el-icon><Monitor /></el-icon></template>
                <el-option label="2D/3D Standard" value="2D/3D Standard" />
                <el-option label="IMAX" value="IMAX" />
                <el-option label="Gold Class" value="Gold Class" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Số hàng ghế">
              <el-input-number v-model="roomForm.rows" :min="1" class="w-100" controls-position="right" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Số ghế mỗi hàng">
              <el-input-number v-model="roomForm.cols" :min="1" class="w-100" controls-position="right" />
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Trạng thái">
              <el-select v-model="roomForm.status" class="w-100">
                <template #prefix><el-icon><PieChart /></el-icon></template>
                <el-option label="Hoạt động" value="Hoạt động" />
                <el-option label="Ngừng hoạt động" value="Ngừng hoạt động" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Ghi chú">
              <el-input v-model="roomForm.note" type="textarea" :rows="2" placeholder="Thông tin bổ sung..." />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<style scoped>
.designer-preview {
  background: radial-gradient(circle at center, #1e293b 0%, #0f172a 100%);
  display: flex !important;
  flex-direction: column;
}

.seat-mock {
  width: 20px;
  height: 20px;
  background-color: #334155;
  border: 1px solid #475569;
}

.seat-mock:hover {
  background-color: #4f46e5;
  border-color: #6366f1;
  cursor: crosshair;
}

.shadow-inner {
  box-shadow: inset 0 0 40px rgba(0, 0, 0, 0.5);
}

.bg-indigo-50 {
  background-color: #eff6ff;
}

.text-indigo-500 {
  color: #4f46e5;
}

.alert-indigo-light {
    background-color: #f0f4ff;
    color: #4338ca;
}
</style>

