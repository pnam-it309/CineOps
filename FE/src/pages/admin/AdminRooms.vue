<script setup>
import { ref, reactive } from 'vue';
import { Plus, Edit, Delete, Location, House, Setting, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// Mock Cinemas
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
  rooms.value = rooms.value.filter(r => r.id !== id);
};

// Seat Grid Logic
const getSeatLabel = (row, col) => {
  const rowLabel = String.fromCharCode(64 + row);
  return `${rowLabel}${col}`;
};
import BaseTable from '@/components/common/BaseTable.vue';

const tableColumns = [
  { label: 'Tên phòng chiếu', key: 'name' },
  { label: 'Định dạng', key: 'type' },
  { label: 'Kích thước', key: 'dimensions' },
  { label: 'Tổng số ghế', key: 'seats' }
];

const currentPage = ref(1);
const pageSize = 10;
</script>

<template>
  <div class="admin-rooms w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 w-100 flex-shrink-0">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Cụm Rạp & Phòng Chiếu</h2>
      </div>
      <div class="d-flex gap-2">
        <el-button type="primary" size="default" :icon="Plus" round @click="openDesigner()">Thêm phòng</el-button>
      </div>
    </div>

    <!-- Stats Summary -->
    <div class="row g-3 mb-3 flex-shrink-0">
      <div class="col-md-4">
        <el-card shadow="never" class="border-black shadow-sm rounded-4 bg-primary bg-opacity-10 text-primary h-100">
          <div class="d-flex align-items-center gap-2">
            <div class="p-2 bg-white rounded-circle shadow-sm">
              <el-icon :size="20">
                <Location />
              </el-icon>
            </div>
            <div class="overflow-hidden">
              <div class="small fw-bold opacity-75 x-small">Địa chỉ</div>
              <div class="fw-bold small text-truncate">{{ selectedCinema.address }}</div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="col-md-4">
        <el-card shadow="never" class="border-black shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-2">
            <div class="p-2 bg-light rounded-circle">
              <el-icon :size="20" class="text-success">
                <House />
              </el-icon>
            </div>
            <div>
              <div class="small text-secondary fw-bold x-small">Phòng đang hoạt động</div>
              <div class="fw-bold fs-6">{{ rooms.length }}</div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="col-md-4">
        <el-card shadow="never" class="border-black shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-2">
            <div class="p-2 bg-light rounded-circle">
              <el-icon :size="20" class="text-warning">
                <View />
              </el-icon>
            </div>
            <div>
              <div class="small text-secondary fw-bold x-small">Tổng số ghế</div>
              <div class="fw-bold fs-6">{{rooms.reduce((acc, r) => acc + (r.rows * r.cols), 0)}}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Rooms Table -->
    <div class="flex-grow-1 overflow-auto no-scroll">
      <BaseTable :data="rooms" :columns="tableColumns" :total="rooms.length" v-model:currentPage="currentPage"
        :page-size="pageSize" @edit="openDesigner" @delete="(r) => deleteRoom(r.id)">
        <template #cell-type="{ row }">
          <el-tag round effect="plain" size="small">{{ row.type }}</el-tag>
        </template>

        <template #cell-dimensions="{ row }">
          <span class="text-secondary small">{{ row.rows }} Hàng × {{ row.cols }} Cột</span>
        </template>

        <template #cell-seats="{ row }">
          <span class="badge bg-light text-dark px-2">{{ row.rows * row.cols }} Ghế</span>
        </template>

        <template #actions="{ row }">
          <div class="d-flex justify-content-center gap-2">
            <button class="btn btn-action-icon" title="Edit Layout" @click="openDesigner(row)">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-action-icon text-danger" title="Delete" @click="deleteRoom(row.id)">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </template>
      </BaseTable>
    </div>


    <!-- Seat Layout Designer Modal -->
    <el-dialog v-model="designerVisible"
      :title="activeRoom ? 'Thiết kế sơ đồ phòng: ' + activeRoom.name : 'Tạo phòng chiếu mới'" width="90%"
      style="max-width: 1200px;" class="rounded-4 overflow-hidden">
      <div class="row g-4">
        <!-- Configuration Column -->
        <div class="col-lg-3">
          <div class="card border-0 bg-light p-3 rounded-4 mb-4">
            <h6 class="fw-bold mb-3">Cấu hình phòng</h6>
            <el-form :model="roomConfig" label-position="top">
              <el-form-item label="Tên phòng (Hall)">
                <el-input v-model="roomConfig.name" placeholder="VD: Phòng 5" />
              </el-form-item>
              <el-form-item label="Định dạng phòng">
                <el-select v-model="roomConfig.type" class="w-100">
                  <el-option label="Tiêu chuẩn (2D/3D)" value="2D/3D" />
                  <el-option label="Trải nghiệm IMAX" value="IMAX" />
                  <el-option label="Hạng Thương gia (VIP)" value="Gold Class" />
                  <el-option label="Khu vui chơi trẻ em" value="Kids" />
                </el-select>
              </el-form-item>
              <div class="row g-2">
                <div class="col-6">
                  <el-form-item label="Số hàng ghế">
                    <el-input-number v-model="roomConfig.rows" :min="1" :max="20" class="w-100" />
                  </el-form-item>
                </div>
                <div class="col-6">
                  <el-form-item label="Số cột (Ghế/Hàng)">
                    <el-input-number v-model="roomConfig.cols" :min="1" :max="30" class="w-100" />
                  </el-form-item>
                </div>
              </div>
            </el-form>
          </div>
          <div class="alert alert-info border-0 rounded-4 small mb-0">
            <el-icon class="me-1">
              <Setting />
            </el-icon>
            Điều chỉnh số hàng và cột để tự động tạo sơ đồ ghế kỹ thuật số ở bên phải.
          </div>
        </div>

        <!-- Designer Column -->
        <div class="col-lg-9">
          <div class="designer-preview p-4 bg-dark rounded-4 shadow-inner text-center overflow-auto"
            style="min-height: 500px;">
            <div
              class="screen-indicator mx-auto mb-5 text-uppercase small text-white opacity-50 border-top border-2 pt-2"
              style="width: 60%; letter-spacing: 5px;">
              Màn hình chính
            </div>

            <div class="seat-grid d-inline-block">
              <div v-for="r in roomConfig.rows" :key="r" class="d-flex gap-1 mb-1 justify-content-center">
                <div v-for="c in roomConfig.cols" :key="c" class="seat-mock rounded-1" :title="getSeatLabel(r, c)">
                </div>
              </div>
            </div>
          </div>

          <div class="d-flex justify-content-between align-items-center mt-3">
            <div class="text-secondary small">
              <strong>Xem trước:</strong> đã tạo {{ roomConfig.rows * roomConfig.cols }} ghế.
            </div>
            <div class="d-flex gap-2">
              <el-button @click="designerVisible = false">Hủy</el-button>
              <el-button type="primary" @click="activeRoom ? handleUpdateRoom() : handleCreateRoom()" class="px-4">
                {{ activeRoom ? 'Cập nhật sơ đồ' : 'Lưu phòng' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-rooms {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.cinema-select {
  width: 250px;
}

.designer-preview {
  background: radial-gradient(circle at center, #1a1a1a 0%, #000 100%);
  display: flex !important;
  flex-direction: column;
}

.seat-mock {
  width: 20px;
  height: 20px;
  background-color: #333;
  border: 1px solid #444;
}

.seat-mock:hover {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  cursor: crosshair;
}

.shadow-inner {
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.5);
}

.table thead th {
  border-bottom: none;
}

.no-scroll {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
  overflow: hidden !important;
}

.no-scroll::-webkit-scrollbar {
  display: none !important;
}

.overflow-auto.no-scroll {
  overflow-y: auto !important;
}

.x-small {
  font-size: 0.65rem;
}
</style>
