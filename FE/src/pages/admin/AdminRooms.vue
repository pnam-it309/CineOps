<script setup>
import { ref, reactive, computed } from 'vue';
import { Plus, Edit, Delete, Location, House, Setting, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import StatCard from '@/components/common/StatCard.vue';
import BaseTable from '@/components/common/BaseTable.vue';

// Define columns
const roomColumns = [
  { label: 'PHÒNG CHIẾU', key: 'room', minWidth: '250px' },
  { label: 'SỨC CHỨA', key: 'capacity', width: '150px' },
  { label: 'TRẠNG THÁI', key: 'status', width: '150px' },
  { label: 'GHI CHÚ', key: 'note', minWidth: '200px' },
];

const selectedRooms = ref([]);
const selectedIds = computed(() => selectedRooms.value.map(r => r.id));

const handleBulkDelete = () => {
    ElMessageBox.confirm(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> phòng chiếu đã chọn?`,
        'Xóa hàng loạt',
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy',
            type: 'warning'
        }
    ).then(() => {
        rooms.value = rooms.value.filter(r => !selectedIds.value.includes(r.id));
        ElMessage.success(`Đã xóa ${selectedIds.value.length} phòng chiếu`);
        selectedRooms.value = [];
    }).catch(() => {});
};

// Mock Cinemas
const cinemas = ref([
  { id: 1, name: 'CineOps Central', address: 'District 1, HCM', rooms: 4 }
]);

const selectedCinema = ref(cinemas.value[0]);
const rooms = ref([
  { id: 101, name: 'Hall 1 (IMAX)', rows: 10, cols: 15, type: 'IMAX' },
  { id: 102, name: 'Hall 2 (Standard)', rows: 8, cols: 12, type: '2D/3D' },
  { id: 103, name: 'Hall 3 (VIP)', rows: 5, cols: 8, type: 'Gold Class' }
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
  ElMessage.success('Room created successfully');
};

const handleUpdateRoom = () => {
  const index = rooms.value.findIndex(r => r.id === activeRoom.value.id);
  rooms.value[index] = { ...activeRoom.value, ...roomConfig };
  designerVisible.value = false;
  ElMessage.success('Room updated');
};

const deleteRoom = (id) => {
  ElMessageBox.confirm('Bạn có chắc chắn muốn xóa phòng chiếu này?', 'Cảnh báo', {
      type: 'warning'
  }).then(() => {
    rooms.value = rooms.value.filter(r => r.id !== id);
    ElMessage.success('Đã xóa phòng chiếu');
  });
};

const getSeatLabel = (row, col) => {
  const rowLabel = String.fromCharCode(64 + row);
  return `${rowLabel}${col}`;
};

const currentPage = ref(1);
const pageSize = ref(10);

// New variables for the updated template
const searchQuery = ref('');
const filterStatus = ref('all');
const dialogVisible = ref(false);
const editingId = ref(null);

const roomForm = reactive({
  name: '',
  type: '2D/3D Standard',
  rows: 8,
  cols: 12,
  status: 'Sẵn sàng',
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

  if (filterStatus.value !== 'all') {
    filtered = filtered.filter(room => room.status === filterStatus.value);
  }

  return filtered;
});

const stats = computed(() => [
  { label: 'Tổng số phòng', value: rooms.value.length, icon: 'bi bi-door-open-fill', type: 'primary' },
  { label: 'Phòng sẵn sàng', value: rooms.value.filter(r => r.status === 'Sẵn sàng').length, icon: 'bi bi-check-circle-fill', type: 'success' },
  { label: 'Phòng bảo trì', value: rooms.value.filter(r => r.status === 'Đang bảo trì').length, icon: 'bi bi-tools', type: 'warning' },
  { label: 'Tổng số ghế', value: rooms.value.reduce((acc, r) => acc + r.capacity, 0), icon: 'bi bi-grid-3x3-gap-fill', type: 'info' }
]);

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
    roomForm.status = 'Sẵn sàng';
    roomForm.note = '';
  }
  dialogVisible.value = true;
};

const handleSave = () => {
  if (editingId.value) {
    const index = rooms.value.findIndex(r => r.id === editingId.value);
    if (index !== -1) {
      rooms.value[index] = { ...roomForm, id: editingId.value, capacity: roomForm.rows * roomForm.cols };
      ElMessage.success('Cập nhật phòng thành công!');
    }
  } else {
    const newRoom = {
      id: Date.now(),
      ...roomForm,
      capacity: roomForm.rows * roomForm.cols
    };
    rooms.value.push(newRoom);
    ElMessage.success('Thêm phòng mới thành công!');
  }
  dialogVisible.value = false;
};

const handleDelete = (room) => {
  ElMessageBox.confirm(`Bạn có chắc chắn muốn xóa phòng "${room.name}"?`, 'Cảnh báo', {
    type: 'warning'
  }).then(() => {
    rooms.value = rooms.value.filter(r => r.id !== room.id);
    ElMessage.success('Đã xóa phòng chiếu!');
  }).catch(() => {
    // User cancelled
  });
};
</script>

<template>
  <div class="admin-rooms-page">
    <AdminTableLayout
      title="Quản lý Phòng chiếu"
      subtitle="Quản lý cấu hình phòng và sơ đồ ghế ngồi"
      titleIcon="bi bi-door-open-fill"
      addButtonLabel="Thêm phòng mới"
      :data="filteredRooms.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      :total="filteredRooms.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog()"
      @reset-filter="() => { searchQuery = ''; filterStatus = 'all'; }"
    >
      <template #header-actions-left>
        <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
          Xóa {{ selectedIds.length }} phòng chiếu
        </el-button>
      </template>
      <template #stats>
        <div v-for="s in stats" :key="s.label" class="col-md-3">
          <StatCard 
              :label="s.label" 
              :value="s.value" 
              :icon="s.icon"
              :type="s.type"
            />
        </div>
      </template>

      <template #filters>
        <div class="filter-item" style="width: 350px;">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm theo tên phòng hoặc mô tả..."
            :prefix-icon="Search"
            size="default"
            clearable
          />
        </div>
        <div class="filter-item" style="width: 200px;">
          <el-select v-model="filterStatus" placeholder="Trạng thái" size="default" class="w-100">
            <el-option label="Tất cả trạng thái" value="all" />
            <el-option label="Sẵn sàng" value="Sẵn sàng" />
            <el-option label="Đang bảo trì" value="Đang bảo trì" />
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
        >
          <template #cell-room="{ row }">
            <div class="d-flex align-items-center gap-3 text-start">
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

          <template #cell-status="{ row }">
            <el-tag :type="row.status === 'Sẵn sàng' ? 'success' : 'warning'" size="small" effect="light" round>
              {{ row.status || 'Sẵn sàng' }}
            </el-tag>
          </template>

          <template #cell-note="{ row }">
            <span class="text-secondary small">{{ row.note || '—' }}</span>
          </template>

          <template #cell-actions="{ row }">
            <div class="d-flex justify-content-center gap-1">
              <el-tooltip content="Chỉnh sửa sơ đồ ghế" placement="top">
                <button class="btn-action-icon btn-action-view">
                  <i class="bi bi-grid-3x3 fs-6"></i>
                </button>
              </el-tooltip>
              <button class="btn-action-icon btn-action-edit" @click="openDialog(row)">
                <i class="bi bi-pencil fs-6"></i>
              </button>
              <button class="btn-action-icon btn-action-delete" @click="handleDelete(row)">
                <i class="bi bi-trash fs-6"></i>
              </button>
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <!-- Room Dialog -->
    <el-dialog
      v-model="dialogVisible"
      width="550px"
      class="premium-dialog"
      destroy-on-close
    >
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i class="bi bi-door-open"></i>
            </div>
            <div class="header-text">
              <h5 class="title">{{ editingId ? 'Cập nhật Phòng' : 'Thêm Phòng mới' }}</h5>
              <p class="subtitle opacity-75">Cấu hình thông số kỹ thuật phòng chiếu</p>
            </div>
          </div>
        </div>
      </template>

      <el-form :model="roomForm" label-position="top" class="premium-form">
        <div class="row g-3">
          <div class="col-8">
            <el-form-item label="Tên phòng" required>
              <el-input v-model="roomForm.name" placeholder="VD: Phòng chiếu 01" />
            </el-form-item>
          </div>
          <div class="col-4">
            <el-form-item label="Loại phòng">
              <el-select v-model="roomForm.type" class="w-100">
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
                <el-option label="Sẵn sàng" value="Sẵn sàng" />
                <el-option label="Đang bảo trì" value="Đang bảo trì" />
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
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false" class="btn-premium-secondary">Hủy</el-button>
          <el-button type="primary" @click="handleSave" class="btn-premium-primary">Lưu thông tin</el-button>
        </div>
      </template>
    </el-dialog>
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
