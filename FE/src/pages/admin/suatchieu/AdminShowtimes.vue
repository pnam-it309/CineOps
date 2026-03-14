<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden">
    <BaseTable 
      title="Quản lí suất chiếu" 
      subtitle="Theo dõi tổng số suất chiếu "
      titleIcon="bi bi-display" 
      :data="paginatedRoomData"
      :columns="roomColumns" 
      :loading="loading" 
      :total="filteredRoomGroups.length" 
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize" 
      @reset-filter="handleReset" 
      :hidePagination="false"
      @fetch="fetchShowtimes"
    >
      <template #header-actions-left>
        <ExcelActions module="suat-chieu" @import-success="fetchShowtimes" />
      </template>

      <!-- Extra action button slot: Auto Generate -->
      <template #header-actions>
        <div class="filter-item border-start ps-3 ms-2">
          <el-tooltip content="Tạo suất chiếu tự động" placement="top">
            <el-button id="btn-auto-generate-showtime" type="warning" :icon="MagicStick" @click="openBatchDialog"
              class="square" plain style="height: 38px; width: 38px;">
            </el-button>
          </el-tooltip>
        </div>
      </template>

      <!-- Optimized Filters (Matching Image 1: Labels above inputs) -->
      <template #filters>
        <!-- Movie search removed as showtimes are no longer movie-dependent here -->

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Ngày chiếu</label>
          <el-date-picker v-model="filterDate" type="date" placeholder="Chọn ngày" format="DD/MM/YYYY"
            value-format="YYYY-MM-DD" size="default" class="w-100" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Phòng chiếu</label>
          <el-select v-model="filterRoom" placeholder="Tất cả" clearable class="w-100">
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="r in phongChieuList" :key="r.id" :label="r.tenPhong" :value="r.id" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Trạng thái</label>
          <el-select v-model="filterTrangThai" placeholder="Tất cả" clearable class="w-100">
            <el-option label="Tất cả" value="" />
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>
      </template>

      <!-- Content: BaseTable trực tiếp, không qua sub-component -->
      <template #cell-roomName="{ row }">
        <div class="d-flex flex-column align-items-center justify-content-center">
          <div class="room-badge shadow-sm mb-1">{{ row.tenPhong }}</div>
          <div class="smaller text-secondary fw-bold text-uppercase opacity-75" style="letter-spacing: 1px; font-size: 10px;">
            <i class="bi bi-award me-1"></i>Loại: {{ row.loaiPhong || 'Standard' }}
          </div>
        </div>
      </template>

      <template #cell-dateInfo="{ row }">
        <span class="fw-bold text-dark">{{ formatDate(row.ngayChieu) }}</span>
      </template>

      <template #cell-totalSlots="{ row }">
        <div class="d-flex flex-column align-items-center justify-content-center">
          <div class="display-6 fw-bold text-dark mb-0">{{ row.totalSlots }}</div>
          <div class="smaller text-secondary fw-bold text-uppercase" style="font-size: 10px; letter-spacing: 0.5px;">suất chiếu</div>
        </div>
      </template>

      <!-- Movie distribution removed -->

      <template #cell-avgOccupancy="{ row }">
        <div class="d-flex flex-column align-items-center" style="min-width: 120px;">
          <div class="fw-bold text-primary mb-1">{{ row.avgOccupancy }}%</div>
          <div class="bg-light w-100 rounded-pill overflow-hidden" style="height: 8px; border: 1px solid #e2e8f0;">
            <div class="bg-primary h-100 transition-all" :style="{ width: row.avgOccupancy + '%' }"></div>
          </div>
          <div class="smaller text-secondary mt-1 fw-bold" style="font-size: 11px;">
            Đã bán: {{ row.totalSold }}/{{ row.totalCapacity }} ghế
          </div>
        </div>
      </template>

      <template #cell-roomStatus="{ row }">
        <el-tag :type="row.totalSlots > 0 ? 'success' : 'info'" round size="small" effect="light">
          {{ row.totalSlots > 0 ? 'Đang vận hành' : 'Trống lịch' }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center align-items-center gap-2">
          <el-tooltip content="Xem chi tiết lịch chiếu tại phòng này" placement="top">
            <el-button type="danger" size="small" class="px-3" @click="viewRoomSchedules(row)">
              <i class="bi bi-calendar-event me-1"></i>Xem suất chiếu
            </el-button>
          </el-tooltip>
        </div>
      </template>
    </BaseTable>


    <!-- Add/Edit Modal -->
    <BaseModal v-model="dialogVisible" :title="editingId ? 'Cập nhật suất chiếu' : 'Thêm suất chiếu mới'"
      icon="bi bi-pencil-square" width="600px" :confirmText="editingId ? 'Cập nhật ngay' : 'Thêm suất chiếu'"
      :loading="saving" @confirm="handleSubmit">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-4">
          <!-- Movie selection removed -->
          <div class="col-12">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng" filterable>
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày chiếu" prop="ngayChieu">
              <el-date-picker v-model="form.ngayChieu" type="date" class="w-100" placeholder="Chọn ngày"
                value-format="YYYY-MM-DD" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ bắt đầu" prop="gioBatDau">
              <el-time-picker v-model="form.gioBatDau" class="w-100" format="HH:mm" value-format="HH:mm:00" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ kết thúc (dự kiến)">
              <el-time-picker v-model="form.gioKetThuc" class="w-100" format="HH:mm" value-format="HH:mm:00" readonly
                disabled />
              <div class="small text-secondary mt-1">Đã tính 15 phút dọn dẹp</div>
            </el-form-item>
          </div>

        </div>
      </el-form>
    </BaseModal>

    <!-- ══════════════════════════════════════════════════
         MODAL: Tạo hàng loạt suất chiếu tự động
         ══════════════════════════════════════════════════ -->
    <!-- Batch Generation Dialog -->
    <el-dialog v-model="batchDialogVisible" title="Tạo suất chiếu tự động" width="640px" append-to-body
      class="batch-gen-dialog rounded-modal">
      <div class="batch-dialog-header">
        <div class="batch-header-icon">
          <el-icon>
            <MagicStick />
          </el-icon>
        </div>
        <div>
          <div class="batch-header-title">Cấu hình suất chiếu hàng loạt</div>
          <p class="batch-header-sub d-none d-sm-block">Tự động tạo lịch chiếu cho nhiều ngày từ khung giờ mẫu</p>
        </div>
      </div>

      <el-form v-if="!batchResult" :model="batchForm" :rules="rules" ref="batchFormRef" label-position="top">
        <div class="row g-3">
          <!-- Movie selection removed -->

          <div class="col-md-6">
            <el-form-item label="Từ ngày" prop="tuNgay">
              <el-date-picker v-model="batchForm.tuNgay" type="date" value-format="YYYY-MM-DD" class="w-100"
                :disabled-date="disabledBeforeToday" />
            </el-form-item>
          </div>
          <div class="col-md-6">
            <el-form-item label="Đến ngày" prop="denNgay">
              <el-date-picker v-model="batchForm.denNgay" type="date" value-format="YYYY-MM-DD" class="w-100"
                :disabled-date="disabledBeforeStart" />
            </el-form-item>
          </div>

          <div class="col-md-6">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="batchForm.idPhongChieu" placeholder="Chọn phòng" class="w-100" filterable>
                <el-option v-for="r in phongChieuList" :key="r.id" :label="r.tenPhong" :value="r.id" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-md-6">
            <el-form-item label="Buffer giữa các suất (phút)">
              <el-input-number v-model="batchForm.bufferPhut" :min="0" :max="60" :step="5" class="w-100"
                controls-position="right" />
            </el-form-item>
          </div>

          <div class="col-md-12">
            <label class="el-form-item__label">Khung giờ trong ngày</label>
            <div class="batch-slot-container mt-1">
              <div class="batch-slot-scroll scrollbar-thin" style="max-height: 240px; overflow-y: auto; overflow-x: hidden; padding-right: 4px;">
                <div v-for="(slot, idx) in batchForm.danhSachGioBatDau" :key="idx" 
                     class="batch-slot-item d-flex align-items-center p-2 mb-2 border rounded bg-white">
                  <el-time-picker v-model="batchForm.danhSachGioBatDau[idx]" format="HH:mm" value-format="HH:mm:ss"
                    placeholder="Giờ" style="width: 110px" />

                  <div class="ms-3 flex-grow-1" v-if="slot">
                    <span class="text-secondary smaller me-1">Khung giờ:</span>
                    <span class="fw-bold text-primary smaller">{{ slot }}</span>
                  </div>

                  <div class="ms-auto ps-2">
                    <el-button type="danger" link @click="removeSlot(idx)" :disabled="batchForm.danhSachGioBatDau.length <= 1">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>

              <button type="button" class="btn btn-outline-secondary btn-sm border-dashed w-100 py-2 mt-2"
                @click="addSlot">
                <el-icon class="me-1">
                  <Plus />
                </el-icon> Thêm khung giờ
              </button>
            </div>
          </div>
        </div>

        <div v-if="batchPreviewCount > 0" class="batch-summary-box mt-3">
          <i class="bi bi-lightning-charge-fill me-1"></i> Dự kiến tạo tối đa <strong>{{ batchPreviewCount }}</strong>
          suất
          chiếu mới ({{ dayCount }} ngày × {{ batchForm.danhSachGioBatDau.filter(Boolean).length }} khung giờ).
        </div>
      </el-form>

      <!-- Result View -->
      <div v-if="batchResult" class="py-2">
        <div class="d-flex gap-2 mb-4">
          <div class="flex-grow-1 p-3 border rounded text-center bg-light">
            <div class="text-secondary small mb-1">Tổng slot</div>
            <div class="h4 mb-0 fw-bold">{{ batchResult.tongSlot }}</div>
          </div>
          <div class="flex-grow-1 p-3 border rounded text-center bg-success-light">
            <div class="text-success small mb-1">Đã tạo</div>
            <div class="h4 mb-0 fw-bold text-success">{{ batchResult.taoThanhCong }}</div>
          </div>
          <div class="flex-grow-1 p-3 border rounded text-center bg-warning-light">
            <div class="text-warning small mb-1">Bỏ qua (trùng)</div>
            <div class="h4 mb-0 fw-bold text-warning">{{ batchResult.boBoBiTrung }}</div>
          </div>
        </div>

        <el-collapse v-if="batchResult.danhSachBiTrung?.length > 0" class="border-0">
          <el-collapse-item :title="`Xem chi tiết ${batchResult.boBoBiTrung} slot bị bỏ qua`" name="skipped">
            <div class="overflow-auto px-1" style="max-height: 200px">
              <div v-for="(skip, i) in batchResult.danhSachBiTrung" :key="i"
                class="p-2 mb-1 border rounded bg-light small d-flex align-items-center">
                <i class="bi bi-x-circle text-danger me-2"></i>
                <span class="fw-bold me-2">{{ formatDate(skip.ngay) }}</span>
                <span class="text-secondary">{{ formatTime(skip.gioBatDau) }} → {{ formatTime(skip.gioKetThuc) }}</span>
                <span class="ms-auto text-secondary smaller italic">{{ skip.lyDo }}</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <template #footer>
        <div class="d-flex justify-content-end gap-2">
          <template v-if="!batchResult">
            <el-button @click="batchDialogVisible = false">Hủy</el-button>
            <el-button type="warning" :icon="MagicStick" :loading="batchSaving" @click="handleBatchSubmit">Tạo tự động
              ngay</el-button>
          </template>
          <template v-else>
            <el-button @click="closeBatchDialog">Đóng</el-button>
            <el-button type="primary" @click="resetBatchForm">Tạo đợt mới</el-button>
          </template>
        </div>
      </template>
    </el-dialog>

    <!-- ══════════════════════════════════════════════════
         MODAL: Sơ đồ ghế Real-time
         ══════════════════════════════════════════════════ -->
    <BaseModal v-model="seatMapVisible" title="Sơ đồ ghế Real-time" icon="bi bi-grid-3x3-gap-fill" width="950px" isDetail onlyCancel>
      <div v-if="selectedShowtime" class="p-4 bg-light-map">
        <!-- Header Map (Light Theme) -->
        <div class="d-flex justify-content-between align-items-center mb-5 px-3">
          <div>
            <h4 class="fw-bold mb-1 text-dark">{{ selectedShowtime.tenPhim }}</h4>
            <div class="text-secondary fw-medium">
              <i class="bi bi-door-open me-1"></i> {{ selectedShowtime.tenPhongChieu }} 
              <span class="mx-2">|</span>
              <i class="bi bi-calendar3 me-1"></i> {{ formatDate(selectedShowtime.ngayChieu) }} 
              <span class="mx-2">|</span>
              <i class="bi bi-clock me-1"></i> {{ selectedShowtime.gioBatDau }}
            </div>
          </div>
          <div class="d-flex gap-4">
            <div class="text-center px-3 py-2 border rounded bg-white shadow-sm">
              <div class="h4 mb-0 fw-bold text-success">{{ selectedShowtime.soGheTrong }}</div>
              <div class="smaller text-secondary fw-bold">TRỐNG</div>
            </div>
            <div class="text-center px-3 py-2 border rounded bg-white shadow-sm">
              <div class="h4 mb-0 fw-bold text-danger">{{ (selectedShowtime.tongGhe || 0) - (selectedShowtime.soGheTrong || 0) }}</div>
              <div class="smaller text-secondary fw-bold">ĐÃ BÁN</div>
            </div>
          </div>
        </div>

        <!-- Screen -->
        <div class="screen-container mb-5">
          <div class="screen-line"></div>
          <div class="text-center smaller fw-bold text-secondary mt-2 tracking-widest">MÀN HÌNH</div>
        </div>

        <!-- Seat Grid -->
        <div class="seat-grid-container py-4" v-loading="loadingSeats">
          <div v-for="(rowSeats, rowName) in groupedSeats" :key="rowName" class="seat-row d-flex justify-content-center gap-2 mb-3">
            <div class="row-label text-secondary fw-bold">{{ rowName }}</div>
            <div v-for="seat in rowSeats" :key="seat.id" 
                 class="admin-seat" 
                 :class="[seat.loaiGhe?.toLowerCase(), seat.trangThai === 1 ? 'sold' : 'available']">
              <el-tooltip :content="`${seat.soGhe} - ${seat.loaiGhe} (${seat.trangThai === 1 ? 'Đã bán' : 'Trống'})`" placement="top">
                <span class="seat-num">{{ seat.cot }}</span>
              </el-tooltip>
            </div>
            <div class="row-label text-secondary fw-bold">{{ rowName }}</div>
          </div>
          
          <!-- Empty State Fallback -->
          <div v-if="!loadingSeats && Object.keys(groupedSeats).length === 0" class="text-center py-5">
            <el-empty description="Phòng chiếu này chưa được cấu hình sơ đồ ghế" />
          </div>
        </div>

        <!-- Legend (Clean) -->
        <div class="mt-4 d-flex justify-content-center gap-5 py-4 border-top bg-light-subtle rounded-3">
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat available sm shadow-none"></div> <span class="small fw-bold text-dark">Ghế trống</span>
          </div>
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat sold sm shadow-none"></div> <span class="small fw-bold text-dark">Đã bán</span>
          </div>
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat vip sm available shadow-none"></div> <span class="small fw-bold text-dark">Ghế VIP</span>
          </div>
          <div class="d-flex align-items-center gap-2">
             <div class="admin-seat couple sm available shadow-none" style="width: 30px;"></div> <span class="small fw-bold text-dark">Ghế Couple</span>
          </div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Monitor, Filter, MagicStick, Plus, Delete } from '@element-plus/icons-vue';
import { suatChieuService } from '@/services/api/admin/suatChieuService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import dayjs from 'dayjs';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';

const router = useRouter();

// ── State ─────────────────────────────────────────────────────────────────────
const loading = ref(false);
const saving = ref(false);
const batchSaving = ref(false);
const dialogVisible = ref(false);
const batchDialogVisible = ref(false);
const seatMapVisible = ref(false);
const loadingSeats = ref(false);
const currentSeats = ref([]);
const batchResult = ref(null);
const selectedShowtime = ref(null);
const editingId = ref(null);


const openSeatMap = async (row) => {
  selectedShowtime.value = row;
  seatMapVisible.value = true;
  loadingSeats.value = true;
  try {
    const res = await suatChieuService.getGheStatus(row.id);
    currentSeats.value = res.data?.data || [];
  } catch (err) {
    notification.error('Không thể tải sơ đồ ghế');
  } finally {
    loadingSeats.value = false;
  }
};

const groupedSeats = computed(() => {
  const groups = {};
  currentSeats.value.forEach(s => {
    if (!groups[s.hang]) groups[s.hang] = [];
    groups[s.hang].push(s);
  });
  // Sort each row by column
  Object.keys(groups).forEach(key => {
    groups[key].sort((a, b) => a.cot - b.cot);
  });
  return groups;
});
const formRef = ref(null);
const batchFormRef = ref(null);

const showtimes = ref([]);
const phongChieuList = ref([]);
const phimList = ref([]);

const filterDate = ref(null);
const filterRoom = ref(null);
const filterTrangThai = ref('');
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(5);
const selectedShowtimes = ref([]);

const form = ref({
  idPhongChieu: '',
  ngayChieu: null,
  gioBatDau: null,
  gioKetThuc: null,
  trangThai: 1
});

const batchForm = ref({
  idPhongChieu: '',
  tuNgay: null,
  denNgay: null,
  danhSachGioBatDau: ['08:00:00', '11:00:00', '14:00:00', '18:00:00'],
  bufferPhut: 20,
  trangThai: 1
});

const rules = {
  idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
  ngayChieu: [{ required: true, message: 'Vui lòng chọn ngày chiếu', trigger: 'change' }],
  gioBatDau: [{ required: true, message: 'Vui lòng chọn giờ bắt đầu', trigger: 'change' }],
};

const batchRules = {
  idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
  tuNgay: [{ required: true, message: 'Vui lòng chọn ngày bắt đầu', trigger: 'change' }],
  denNgay: [{ required: true, message: 'Vui lòng chọn ngày kết thúc', trigger: 'change' }],
  danhSachGioBatDau: [
    { required: true, message: 'Cần ít nhất 1 khung giờ', trigger: 'change' },
    {
      validator: (rule, value, cb) => {
        const valid = value && value.filter(Boolean).length > 0;
        valid ? cb() : cb(new Error('Vui lòng nhập ít nhất 1 khung giờ hợp lệ'));
      },
      trigger: 'change'
    }
  ]
};

// ── Watch auto end time calculation ──
import { watch } from 'vue';
watch([() => form.value.idPhim, () => form.value.gioBatDau], ([newPhimId, newStart]) => {
  if (newPhimId && newStart) {
    const phim = phimList.value.find(p => p.id === newPhimId);
    if (phim?.thoiLuong) {
      const [h, m] = newStart.split(':').map(Number);
      const total = h * 60 + m + phim.thoiLuong + 15;
      form.value.gioKetThuc =
        `${String(Math.floor(total / 60) % 24).padStart(2, '0')}:${String(total % 60).padStart(2, '0')}:00`;
    }
  }
});

// ── Columns ───────────────────────────────────────────────────────────────────
const roomColumns = [
  { label: 'Phòng chiếu', key: 'roomName', width: '220px', align: 'center' },
  { label: 'Ngày xem', key: 'dateInfo', width: '150px', align: 'center' },
  { label: 'Tổng suất chiếu', key: 'totalSlots', width: '170px', align: 'center' },
  { label: 'Hiệu suất lấp đầy', key: 'avgOccupancy', width: '180px', align: 'center' },
  { label: 'Trạng thái', key: 'roomStatus', width: '150px', align: 'center' },
];

// ── Computed ──────────────────────────────────────────────────────────────────
const filteredRoomGroups = computed(() => {
  const groups = [];
  const targetDate = filterDate.value || dayjs().format('YYYY-MM-DD');

  phongChieuList.value.forEach(room => {
    const roomShowtimes = showtimes.value.filter(s => 
      s.idPhongChieu === room.id && 
      dayjs(s.ngayChieu).format('YYYY-MM-DD') === targetDate
    );

    const movies = [...new Set(roomShowtimes.map(s => s.tenPhim))].filter(Boolean);
    
    let totalOccupancy = 0;
    let totalSold = 0;
    let totalCapacity = 0;

    roomShowtimes.forEach(s => {
      const capacity = s.tongGhe || room.tongGhe || 0;
      const sold = capacity - (s.soGheTrong || 0);
      totalOccupancy += capacity > 0 ? (sold / capacity) * 100 : 0;
      totalSold += sold;
      totalCapacity += capacity;
    });

    groups.push({
      id: room.id,
      tenPhong: room.tenPhong,
      loaiPhong: room.loaiPhong || 'Premium',
      totalSlots: roomShowtimes.length,
      ngayChieu: targetDate,
      movies: movies,
      totalSold,
      totalCapacity,
      avgOccupancy: roomShowtimes.length > 0 ? Math.round(totalOccupancy / roomShowtimes.length) : 0
    });
  });

  return groups.filter(g => {
    if (!searchQuery.value) return true;
    return g.tenPhong.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
           g.movies.some(m => m.toLowerCase().includes(searchQuery.value.toLowerCase()));
  });
});

const paginatedRoomData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredRoomGroups.value.slice(start, start + pageSize.value);
});

/** Phim đang chọn trong batch form */
const selectedBatchPhim = computed(() =>
  phimList.value.find(p => p.id === batchForm.value.idPhim) || null
);

/** Số ngày trong khoảng */
const dayCount = computed(() => {
  if (!batchForm.value.tuNgay || !batchForm.value.denNgay) return 0;
  const diff = dayjs(batchForm.value.denNgay).diff(dayjs(batchForm.value.tuNgay), 'day') + 1;
  return diff > 0 ? diff : 0;
});

/** Số suất dự kiến tạo tối đa */
const batchPreviewCount = computed(() => {
  const validSlots = batchForm.value.danhSachGioBatDau.filter(Boolean).length;
  return dayCount.value * validSlots;
});

// ── Methods ───────────────────────────────────────────────────────────────────
async function fetchShowtimes() {
  loading.value = true;
  try {
    const params = {};
    if (filterDate.value) params.ngayChieu = filterDate.value;
    if (filterRoom.value) params.idPhongChieu = filterRoom.value;
    const res = await suatChieuService.getShowtimes(params);
    const data = res.data?.data || [];
    
    // Enrich with tongGhe from phongChieuList
    showtimes.value = data.map(s => {
      const room = phongChieuList.value.find(r => r.id === s.idPhongChieu);
      return {
        ...s,
        tongGhe: s.tongGhe || room?.tongGhe || 0
      }
    });
  } catch {
    notification.error('Không thể tải danh sách suất chiếu');
  } finally {
    loading.value = false;
  }
}

const viewRoomSchedules = (row) => {
  // Chuyển sang tab Lịch chiếu và áp dụng filter
  router.push({
    path: '/admin/schedule',
    query: {
      roomId: row.id,
      date: row.ngayChieu,
      tab: 'visual'
    }
  });
  notification.success(`Đang chuyển tới sơ đồ suất chiếu ${row.tenPhong}`);
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  form.value = row
    ? {
      idPhim: row.idPhim, idPhongChieu: row.idPhongChieu, ngayChieu: row.ngayChieu,
      gioBatDau: row.gioBatDau, gioKetThuc: row.gioKetThuc, trangThai: row.trangThai
    }
    : { idPhim: '', idPhongChieu: '', ngayChieu: null, gioBatDau: null, gioKetThuc: null, trangThai: 1 };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      if (editingId.value) {
        await confirmDialog.update('suất chiếu');
      } else {
        await confirmDialog.add('suất chiếu');
      }
    } catch { return; }

    saving.value = true;
    try {
      if (editingId.value) {
        await suatChieuService.updateShowtime(editingId.value, form.value);
        notification.updateSuccess('suất chiếu');
      } else {
        await suatChieuService.createShowtime(form.value);
        notification.addSuccess('suất chiếu');
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleUpdateStatus = (row, status = null) => {
  const newStatus = status !== null ? status : (row.trangThai === 0 ? 1 : 0);
  const label = getStatusLabel(newStatus).toLowerCase();

  if (newStatus === row.trangThai) return;

  confirmDialog.status('suất chiếu', label, newStatus !== 0).then(async () => {
    try {
      await suatChieuService.updateShowtime(row.id, { ...row, trangThai: newStatus });
      notification.success('Cập nhật trạng thái thành công');
      fetchShowtimes();
    } catch { notification.error('Cập nhật thất bại'); }
  }).catch(() => { });
};

// ── Batch Generate ────────────────────────────────────────────────────────────
const openBatchDialog = () => {
  batchResult.value = null;
  batchForm.value = {
    idPhim: '',
    idPhongChieu: '',
    tuNgay: null,
    denNgay: null,
    danhSachGioBatDau: ['08:00:00', '11:00:00', '14:00:00', '18:00:00'],
    bufferPhut: 20,
    trangThai: 1
  };
  batchDialogVisible.value = true;
};

const closeBatchDialog = () => {
  batchDialogVisible.value = false;
  batchResult.value = null;
  fetchShowtimes();
};

const resetBatchForm = () => {
  batchResult.value = null;
};

const onBatchPhimChange = () => { /* reactive — selectedBatchPhim computed handles it */ };

const addSlot = () => {
  batchForm.value.danhSachGioBatDau.push('');
};

const removeSlot = (idx) => {
  if (batchForm.value.danhSachGioBatDau.length > 1) {
    batchForm.value.danhSachGioBatDau.splice(idx, 1);
  }
};

/** Tính giờ kết thúc dự kiến = gioBatDau + thoiLuong + buffer */
const calcEndTime = (timeStr) => {
  if (!timeStr || !selectedBatchPhim.value) return '';
  const [h, m] = timeStr.split(':').map(Number);
  const totalMinutes = h * 60 + m + (selectedBatchPhim.value.thoiLuong || 120) + (batchForm.value.bufferPhut || 20);
  const endH = Math.floor(totalMinutes / 60) % 24;
  const endM = totalMinutes % 60;
  return `${String(endH).padStart(2, '0')}:${String(endM).padStart(2, '0')}`;
};

const disabledBeforeToday = (time) => time < new Date(new Date().setHours(0, 0, 0, 0));
const disabledBeforeStart = (time) => {
  if (!batchForm.value.tuNgay) return false;
  return time < new Date(batchForm.value.tuNgay);
};

const handleBatchSubmit = async () => {
  if (!batchFormRef.value) return;
  await batchFormRef.value.validate(async (valid) => {
    if (!valid) return;

    const validSlots = batchForm.value.danhSachGioBatDau.filter(Boolean);
    if (validSlots.length === 0) {
      notification.error('Vui lòng thêm ít nhất 1 khung giờ hợp lệ');
      return;
    }

    batchSaving.value = true;
    try {
      const payload = {
        ...batchForm.value,
        danhSachGioBatDau: validSlots.map(t => t.length === 5 ? t + ':00' : t)
      };
      const res = await suatChieuService.generateBatch(payload);
      batchResult.value = res.data?.data || res.data;
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi khi tạo suất chiếu tự động');
    } finally {
      batchSaving.value = false;
    }
  });
};

// ── Helpers ───────────────────────────────────────────────────────────────────
const getStatusTag = (s) => ({ 0: 'danger', 1: 'primary', 2: 'success', 3: 'info' }[s] || 'info');
const getStatusLabel = (s) => ({ 0: 'Đã hủy', 1: 'Sắp chiếu', 2: 'Đang chiếu', 3: 'Kết thúc' }[s] || '—');
const formatCurrency = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const formatDate = (d) => d ? dayjs(d).format('DD/MM/YYYY') : '—';
const formatTime = (t) => t ? String(t).substring(0, 5) : '—';

const getDoTuoiTagType = (nhanDoTuoi) => {
  const types = { 'P': 'success', 'T13': 'warning', 'T16': 'danger', 'T18': 'info' };
  return types[nhanDoTuoi] || 'info';
};

const handleReset = () => {
  filterDate.value = dayjs().format('YYYY-MM-DD');
  filterRoom.value = null;
  filterTrangThai.value = '';
  searchQuery.value = '';
  currentPage.value = 1;
};

watch([filterDate, filterRoom, searchQuery], () => {
  currentPage.value = 1;
  fetchShowtimes();
});

onMounted(async () => {
  const [pcRes, phimRes] = await Promise.all([
    suatChieuService.getPhongChieuDropdown(),
    suatChieuService.getPhimDropdown()
  ]);
  phongChieuList.value = pcRes.data?.data || [];
  phimList.value = phimRes.data?.data || [];
  await fetchShowtimes();
});
</script>

<style scoped>
/* Simplified to zero local styles beyond functional overrides */
.border-dashed {
  border: 1px dashed var(--border-color);
  background: var(--color-gray-100);
  border-radius: 8px;
  transition: all 0.2s;
}

.border-dashed:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-white);
}

.bg-success-light {
  background: #f0fdf4;
  border-color: #bcf0da !important;
}

.bg-warning-light {
  background: #fffbeb;
  border-color: #fde68a !important;
}

:deep(.el-dialog__header) {
  padding-bottom: 0;
}

.info-label-muted {
  display: block;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.section-title-clean {
  font-size: 15px;
  font-weight: 800;
  color: #1e293b;
  position: relative;
}

.uppercase-label {
  text-transform: uppercase;
  letter-spacing: 1px;
}

.bg-light-subtle {
  background-color: #f8fafc !important;
}

.info-value {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

/* ──────────────────────────────────────────────────
   STYLE: Seat Map Real-time (Light Clean & Clear)
   ────────────────────────────────────────────────── */
.bg-light-map {
  background: #ffffff;
  border-radius: 0 0 16px 16px;
  min-height: 480px;
  border: 1px solid #f1f5f9;
}

.screen-container { width: 100%; padding: 0 15%; }
.screen-line {
  height: 8px;
  background: #e2e8f0;
  border-radius: 20px;
}

.row-label {
  width: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 14px;
}

.admin-seat {
  width: 42px;
  height: 42px;
  background: #ffffff;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #475569;
  cursor: default;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.admin-seat.sm { width: 22px; height: 22px; font-size: 0; pointer-events: none; border-width: 1px; }

.admin-seat.available { border-color: #cbd5e1; }
.admin-seat.available:hover {
  transform: translateY(-3px);
  border-color: #3b82f6;
  color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.admin-seat.sold {
  background: #f1f5f9 !important;
  border-color: #e2e8f0 !important;
  color: #cbd5e1 !important;
  position: relative;
}
.admin-seat.sold::after {
  content: '✕';
  position: absolute;
  font-size: 18px;
  opacity: 0.4;
}

.admin-seat.vip.available { border-color: #f59e0b; color: #b45309; }
.admin-seat.vip.available:hover { border-color: #f59e0b; box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15); }

.admin-seat.couple.available { border-color: #ec4899; color: #be185d; width: 92px; }
.admin-seat.couple.available:hover { border-color: #ec4899; box-shadow: 0 4px 12px rgba(236, 72, 153, 0.15); }

/* ──────────────────────────────────────────────────
   STYLE: Batch Generation Modal
   ────────────────────────────────────────────────── */
.batch-dialog-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #fffbeb;
  border: 1px solid #fef3c7;
  border-radius: 12px;
  margin-bottom: 24px;
}

.batch-header-icon {
  width: 48px;
  height: 48px;
  background: #f59e0b;
  color: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.batch-header-title {
  font-weight: 800;
  font-size: 16px;
  color: #92400e;
}

.batch-header-sub {
  font-size: 12px;
  color: #b45309;
  margin: 0;
}

.batch-info-box {
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  font-size: 12px;
}

.batch-summary-box {
  background: #eff6ff;
  border: 1px solid #dbeafe;
  padding: 12px;
  border-radius: 8px;
  color: #1e40af;
  font-size: 13px;
  text-align: center;
}

.scrollbar-thin::-webkit-scrollbar {
  width: 6px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: #f1f5f9;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
.scrollbar-thin::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.batch-slot-item {
  transition: all 0.2s ease;
}

.batch-slot-item:hover {
  border-color: #3b82f6 !important;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.05);
}

.rounded-modal :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.btn-action-icon.action-seat {
  color: #6366f1;
}

.btn-action-icon.action-seat:hover {
  background: #e0e7ff;
}
.room-badge {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: 800;
  font-size: 13px;
}

.movie-tag-item {
  border-radius: 4px;
  font-weight: 600;
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #475569;
}

.bg-primary-light {
  background-color: #eff6ff;
}

.italic {
  font-style: italic;
}
</style>
