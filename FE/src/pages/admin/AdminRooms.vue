<script setup>
import { ref, reactive } from 'vue';
import { Plus, Edit, Delete, Location, House, Setting, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// Mock Cinemas
const cinemas = ref([
  { id: 1, name: 'CineOps Central', address: 'District 1, HCM', rooms: 4 },
  { id: 2, name: 'CineOps East Side', address: 'District 2, HCM', rooms: 6 },
  { id: 3, name: 'CineOps West Park', address: 'Tan Binh, HCM', rooms: 8 }
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
</script>

<template>
  <div class="admin-rooms container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Cinemas & Rooms</h2>
        <p class="text-secondary small mb-0">Manage theater locations and configure hall seat layouts.</p>
      </div>
      <div class="d-flex gap-2">
        <el-select v-model="selectedCinema" value-key="id" size="large" class="cinema-select">
          <el-option v-for="c in cinemas" :key="c.id" :label="c.name" :value="c" />
        </el-select>
        <el-button type="primary" size="large" :icon="Plus" round @click="openDesigner()">Add Room</el-button>
      </div>
    </div>

    <!-- Stats Summary -->
    <div class="row g-4 mb-4">
      <div class="col-md-4">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 bg-primary bg-opacity-10 text-primary h-100">
          <div class="d-flex align-items-center gap-3">
            <div class="p-3 bg-white rounded-circle shadow-sm">
              <el-icon :size="24"><Location /></el-icon>
            </div>
            <div>
              <div class="small fw-bold opacity-75">Address</div>
              <div class="fw-bold">{{ selectedCinema.address }}</div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="col-md-4">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-3">
            <div class="p-3 bg-light rounded-circle">
              <el-icon :size="24" class="text-success"><House /></el-icon>
            </div>
            <div>
              <div class="small text-secondary fw-bold">Active Halls</div>
              <div class="fw-bold fs-4">{{ rooms.length }}</div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="col-md-4">
        <el-card shadow="never" class="border-0 shadow-sm rounded-4 h-100">
          <div class="d-flex align-items-center gap-3">
            <div class="p-3 bg-light rounded-circle">
              <el-icon :size="24" class="text-warning"><View /></el-icon>
            </div>
            <div>
              <div class="small text-secondary fw-bold">Total Seats</div>
              <div class="fw-bold fs-4">{{ rooms.reduce((acc, r) => acc + (r.rows * r.cols), 0) }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Rooms Table -->
    <el-card shadow="never" class="border-0 shadow-sm rounded-4">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light text-secondary small text-uppercase">
            <tr>
              <th class="ps-3">Room Name</th>
              <th>Format</th>
              <th>Dimensions</th>
              <th>Total Seats</th>
              <th class="text-end pe-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="room in rooms" :key="room.id">
              <td class="ps-3 fw-bold">{{ room.name }}</td>
              <td>
                <el-tag round effect="plain">{{ room.type }}</el-tag>
              </td>
              <td class="text-secondary small">{{ room.rows }} Rows × {{ room.cols }} Cols</td>
              <td>
                <span class="badge bg-light text-dark px-2">{{ room.rows * room.cols }} Seats</span>
              </td>
              <td class="text-end pe-3">
                <el-button-group>
                  <el-button :icon="Edit" size="small" @click="openDesigner(room)">Edit Layout</el-button>
                  <el-button :icon="Setting" size="small" />
                  <el-button :icon="Delete" size="small" type="danger" plain @click="deleteRoom(room.id)" />
                </el-button-group>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <!-- Seat Layout Designer Modal -->
    <el-dialog
      v-model="designerVisible"
      :title="activeRoom ? 'Room Layout Designer: ' + activeRoom.name : 'Create New Room'"
      width="90%"
      style="max-width: 1200px;"
      class="rounded-4 overflow-hidden"
    >
      <div class="row g-4">
        <!-- Configuration Column -->
        <div class="col-lg-3">
          <div class="card border-0 bg-light p-3 rounded-4 mb-4">
            <h6 class="fw-bold mb-3">Configuration</h6>
            <el-form :model="roomConfig" label-position="top">
              <el-form-item label="Hall Name">
                <el-input v-model="roomConfig.name" placeholder="e.g. Hall 5" />
              </el-form-item>
              <el-form-item label="Hall Type">
                <el-select v-model="roomConfig.type" class="w-100">
                  <el-option label="Standard (2D/3D)" value="2D/3D" />
                  <el-option label="IMAX Experience" value="IMAX" />
                  <el-option label="Gold Class (VIP)" value="Gold Class" />
                  <el-option label="Kids Playground" value="Kids" />
                </el-select>
              </el-form-item>
              <div class="row g-2">
                <div class="col-6">
                  <el-form-item label="Rows">
                    <el-input-number v-model="roomConfig.rows" :min="1" :max="20" class="w-100" />
                  </el-form-item>
                </div>
                <div class="col-6">
                  <el-form-item label="Columns">
                    <el-input-number v-model="roomConfig.cols" :min="1" :max="30" class="w-100" />
                  </el-form-item>
                </div>
              </div>
            </el-form>
          </div>
          <div class="alert alert-info border-0 rounded-4 small mb-0">
            <el-icon class="me-1"><Setting /></el-icon>
            Adjust the rows and columns to automatically generate the digital seat map on the right.
          </div>
        </div>

        <!-- Designer Column -->
        <div class="col-lg-9">
          <div class="designer-preview p-4 bg-dark rounded-4 shadow-inner text-center overflow-auto" style="min-height: 500px;">
            <div class="screen-indicator mx-auto mb-5 text-uppercase small text-white opacity-50 border-top border-2 pt-2" style="width: 60%; letter-spacing: 5px;">
              Cinema Screen
            </div>
            
            <div class="seat-grid d-inline-block">
              <div v-for="r in roomConfig.rows" :key="r" class="d-flex gap-1 mb-1 justify-content-center">
                <div v-for="c in roomConfig.cols" :key="c" class="seat-mock rounded-1" :title="getSeatLabel(r, c)"></div>
              </div>
            </div>
          </div>
          
          <div class="d-flex justify-content-between align-items-center mt-3">
            <div class="text-secondary small">
              <strong>Interactive Preview:</strong> {{ roomConfig.rows * roomConfig.cols }} seats generated.
            </div>
            <div class="d-flex gap-2">
              <el-button @click="designerVisible = false">Cancel</el-button>
              <el-button type="primary" @click="activeRoom ? handleUpdateRoom() : handleCreateRoom()" class="px-4">
                {{ activeRoom ? 'Update Layout' : 'Save Room' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
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
  box-shadow: inset 0 0 20px rgba(0,0,0,0.5);
}
.table thead th {
  border-bottom: none;
}
</style>
