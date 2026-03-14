<template>
  <div class="d-flex flex-column flex-grow-1 h-100 overflow-hidden"
    :class="{ 'visual-mode-active': activeTab === 'visual' }">
    <BaseTable :title="activeTab === 'list' ? 'Quản lý lịch chiếu' : ''"
      :subtitle="activeTab === 'list' ? 'Quản lý sơ đồ và danh sách suất chiếu phim tại các cụm rạp' : ''"
      titleIcon="bi bi-calendar-week-fill" :data="paginatedSchedules" :columns="listColumns" :loading="loading"
      :total="filteredSchedules.length" v-model:currentPage="currentPage" v-model:pageSize="pageSize"
      :show-filters="activeTab === 'list'" :hide-pagination="activeTab === 'visual'"
      addButtonLabel="Thêm lịch chiếu"
      :no-padding="activeTab === 'visual'" @reset-filter="resetFilter" @fetch="handleSearch" @add-click="openDialog()">
      <!-- Header Actions: visual/list toggle -->
      <template #header-actions>
        <div class="d-flex align-items-center gap-3">
          <el-radio-group v-model="activeTab" size="default" class="premium-tabs-toggle">
            <el-radio-button value="list">
              <i class="bi bi-list-ul me-1"></i> Danh sách
            </el-radio-button>
            <el-radio-button value="visual">
              <i class="bi bi-grid-3x3-gap me-1"></i> Sơ đồ
            </el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- Optimized Filters (Matching Image 1: Labels above inputs) -->
      <template #filters v-if="activeTab === 'list'">
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Tên phim</label>
          <el-input v-model="searchQuery" placeholder="Tìm tên phim..." :prefix-icon="Search" clearable
            @input="handleSearch" class="w-100" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Ngày chiếu</label>
          <el-date-picker v-model="filterDate" type="date" placeholder="Chọn ngày" format="DD/MM/YYYY"
            value-format="YYYY-MM-DD" class="w-100" @change="handleSearch" clearable />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Phòng chiếu</label>
          <el-select v-model="filterRoomId" placeholder="Tất cả" class="w-100" @change="handleSearch" clearable>
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="r in phongChieuList" :key="r.id" :label="r.tenPhong || r.ten_phong" :value="r.id" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Trạng thái</label>
          <el-select v-model="filterStatus" placeholder="Tất cả" class="w-100" @change="handleSearch" clearable>
            <el-option label="Tất cả" value="" />
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
            <el-option label="Kết thúc" :value="3" />
          </el-select>
        </div>
      </template>

      <!-- Cell Templates (list only) -->
      <template #cell-stt="{ index }">
        <div class="d-flex justify-content-center align-items-center">
          <span class="text-secondary smaller">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
        </div>
      </template>

      <template #cell-phim="{ row }">
        <div class="d-flex flex-column align-items-center justify-content-center">
          <div class="fw-bold text-dark">{{ row.tenPhim }}</div>
          <div class="smaller text-secondary">{{ row.loaiPhim || '2D' }} Digital</div>
        </div>
      </template>

      <template #cell-giaiDoan="{ row }">
        <div class="d-flex justify-content-center align-items-center smaller fw-bold text-primary">
          {{ row.giaiDoan }}
        </div>
      </template>

      <template #cell-tongSuat="{ row }">
        <div class="d-flex justify-content-center align-items-center">
          <el-tag type="info" effect="plain" class="fw-bold">{{ row.tongSuat }} suất</el-tag>
        </div>
      </template>

      <template #cell-phongs="{ row }">
        <div class="d-flex justify-content-center">
          <div class="smaller text-secondary text-truncate" style="max-width: 180px;" :title="row.phongs">
            {{ row.phongs }}
          </div>
        </div>
      </template>

      <template #cell-status="{ row }">
        <div class="d-flex justify-content-center align-items-center">
          <el-tag :type="row.statusType" effect="dark" size="small" class="fw-bold">
            {{ row.displayStatus }}
          </el-tag>
        </div>
      </template>

      <template #actions="{ row }">
        <div class="d-flex justify-content-center gap-1">
          <el-tooltip content="Xem chi tiết & Chỉnh sửa" placement="top">
            <button class="btn-action-icon action-view"
              @click="viewMovieSchedules(row)">
              <i class="bi bi-eye"></i>
            </button>
          </el-tooltip>
        </div>
      </template>
      <!-- Visual View overrides the default table when active -->
      <template v-if="activeTab === 'visual'">
        <div class="h-100 overflow-hidden">
          <ScheduleVisual ref="visualRef" :showtimes="showtimes" :phongChieuList="phongChieuList" :phimList="phimList"
            :allKhungGio="allKhungGio" :initialDate="filterDate" :initialRoomId="filterRoomId"
            @view="openSeatMap" @editShowtime="openDialog" @addShowtime="handleAddShowtime" @weekChange="onWeekChange" />
        </div>
      </template>
    </BaseTable>

    <!-- ══════════════════════════════════════════════════
         MODAL: Sơ đồ ghế Real-time (Light Clean & Clear)
         ══════════════════════════════════════════════════ -->
    <BaseModal v-model="seatMapVisible" title="Sơ đồ ghế Real-time" icon="bi bi-grid-3x3-gap-fill" width="950px"
      isDetail onlyCancel>
      <div v-if="selectedShowtime" class="p-4 bg-light-map">
        <!-- Header Map (Light Theme) -->
        <div class="d-flex justify-content-between align-items-center mb-5 px-3">
          <div>
            <h4 class="fw-bold mb-1 text-dark">{{ selectedShowtime.tenPhim || selectedShowtime.ten_phim }}</h4>
            <div class="text-secondary fw-medium">
              <i class="bi bi-door-open me-1"></i> {{ selectedShowtime.tenPhongChieu || selectedShowtime.ten_phong_chieu
              }}
              <span class="mx-2">|</span>
              <i class="bi bi-calendar3 me-1"></i> {{ formatDate(selectedShowtime.ngayChieu ||
                selectedShowtime.ngay_chieu)
              }}
              <span class="mx-2">|</span>
              <i class="bi bi-clock me-1"></i> {{ selectedShowtime.gioBatDau || selectedShowtime.gio_bat_dau }}
            </div>
          </div>
          <div class="d-flex gap-4">
            <div class="text-center px-3 py-2 border rounded bg-white shadow-sm">
              <div class="h4 mb-0 fw-bold text-success">{{ selectedShowtime.soGheTrong ?? selectedShowtime.so_ghe_trong
                }}
              </div>
              <div class="smaller text-secondary fw-bold">TRỐNG</div>
            </div>
            <div class="text-center px-3 py-2 border rounded bg-white shadow-sm">
              <div class="h4 mb-0 fw-bold text-danger">{{ (selectedShowtime.tongGhe || 0) - (selectedShowtime.soGheTrong
                ??
                selectedShowtime.so_ghe_trong ?? 0) }}</div>
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
          <div v-for="(rowSeats, rowName) in groupedSeats" :key="rowName"
            class="seat-row d-flex justify-content-center gap-2 mb-3">
            <div class="row-label text-secondary fw-bold">{{ rowName }}</div>
            <div v-for="seat in rowSeats" :key="seat.id" class="admin-seat"
              :class="[seat.loaiGhe?.toLowerCase(), seat.trangThai === 1 ? 'sold' : 'available']">
              <el-tooltip :content="`${seat.soGhe} - ${seat.loaiGhe} (${seat.trangThai === 1 ? 'Đã bán' : 'Trống'})`"
                placement="top">
                <span class="seat-num">{{ seat.cot }}</span>
              </el-tooltip>
            </div>
            <div class="row-label text-secondary fw-bold">{{ rowName }}</div>
          </div>

          <!-- Empty State -->
          <div v-if="!loadingSeats && Object.keys(groupedSeats).length === 0" class="text-center py-5">
            <el-empty description="Phòng chiếu này chưa được cấu hình sơ đồ ghế" />
          </div>
        </div>

        <!-- Legend -->
        <div class="mt-4 d-flex justify-content-center gap-5 py-4 border-top bg-light-subtle rounded-3">
          <div class="d-flex align-items-center gap-2">
            <div class="admin-seat available sm shadow-none"></div> <span class="small fw-bold text-dark">Ghế
              trống</span>
          </div>
          <div class="d-flex align-items-center gap-2">
            <div class="admin-seat sold sm shadow-none"></div> <span class="small fw-bold text-dark">Đã bán</span>
          </div>
          <div class="d-flex align-items-center gap-2">
            <div class="admin-seat vip sm available shadow-none"></div> <span class="small fw-bold text-dark">Ghế
              VIP</span>
          </div>
          <div class="d-flex align-items-center gap-2">
            <div class="admin-seat couple sm available shadow-none" style="width: 30px;"></div> <span
              class="small fw-bold text-dark">Ghế Couple</span>
          </div>
        </div>
      </div>
    </BaseModal>

    <!-- Modal: Thêm / Cập nhật -->
    <BaseModal v-model="dialogVisible" :title="editingId ? 'Cập nhật lịch chiếu' : 'Thêm lịch chiếu mới'"
      icon="bi bi-calendar-plus" width="820px" :confirmText="editingId ? 'Cập nhật' : 'Thêm lịch chiếu'"
      :loading="saving" @confirm="handleSubmit">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-4">
          <div class="col-12">
            <el-form-item label="Phim" prop="idPhim">
              <el-select v-model="form.idPhim" class="w-100" placeholder="Chọn phim" filterable>
                <el-option v-for="p in phimList" :key="p.id" :label="`${p.tenPhim} [${p.loaiPhim || '2D'}]`"
                  :value="p.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng" filterable>
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong || pc.ten_phong"
                  :value="pc.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Khung giờ" prop="idKhungGio">
              <el-select v-model="form.idKhungGio" class="w-100" placeholder="Chọn khung giờ" filterable>
                <el-option v-for="kg in allKhungGio" :key="kg.id"
                  :label="`${kg.tenKhungGio || kg.ten_khung_gio} (${formatTime(kg.gioBatDau || kg.gio_bat_dau)} → ${formatTime(kg.gioKetThuc || kg.gio_ket_thuc)})`"
                  :value="kg.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày chiếu" prop="ngayChieu">
              <el-date-picker v-model="form.ngayChieu" type="date" class="w-100" placeholder="Chọn ngày"
                value-format="YYYY-MM-DD" />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { Search, Refresh } from "@element-plus/icons-vue";
import { suatChieuService } from "@/services/api/admin/suatChieuService";
import notification from "@/utils/notifications";
import confirmDialog from "@/utils/confirm";
import dayjs from "dayjs";
import debounce from "lodash/debounce";

import BaseModal from "@/components/common/BaseModal.vue";
import BaseTable from "@/components/common/BaseTable.vue";
import ScheduleVisual from "./ScheduleVisual.vue";

const route = useRoute();
const activeTab = ref("list");
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const seatMapVisible = ref(false);
const loadingSeats = ref(false);
const currentSeats = ref([]);
const selectedShowtime = ref(null);
const visualRef = ref(null);
const formRef = ref(null);

const showtimes = ref([]);
const phongChieuList = ref([]);
const phimList = ref([]);
const allKhungGio = ref([]);

const searchQuery = ref("");
const filterDate = ref(null);
const filterRoomId = ref("");
const filterStatus = ref("");
const currentPage = ref(1);
const pageSize = ref(10);

const listColumns = [
  { label: "STT", key: "stt", width: "60px", align: "center" },
  { label: "Giai đoạn chiếu", key: "giaiDoan", width: "200px", align: "center" },
  { label: "Tổng suất chiếu", key: "tongSuat", width: "120px", align: "center" },
  { label: "Phòng áp dụng", key: "phongs", width: "180px" ,align: "center"},
  { label: "Trạng thái", key: "status", width: "130px", align: "center" },
];

const form = ref({
  idPhim: "",
  idPhongChieu: "",
  idKhungGio: "",
  ngayChieu: null,
  trangThai: 1,
});

const rules = {
  idPhim: [
    { required: true, message: "Vui lòng chọn phim", trigger: "change" },
  ],
  idPhongChieu: [
    { required: true, message: "Vui lòng chọn phòng chiếu", trigger: "change" },
  ],
  idKhungGio: [
    { required: true, message: "Vui lòng chọn khung giờ", trigger: "change" },
  ],
  ngayChieu: [
    { required: true, message: "Vui lòng chọn ngày chiếu", trigger: "change" },
  ],
};

watch(activeTab, () => {
  currentPage.value = 1;
});

// ── Fetch ──────────────────────────────────────────────────────────────────
async function fetchShowtimes() {
  loading.value = true;
  try {
    const res = await suatChieuService.getShowtimes();
    const data = res.data?.data || [];

    showtimes.value = data.map(s => {
      const room = phongChieuList.value.find(r => r.id === (s.idPhongChieu ?? s.id_phong_chieu));
      return {
        ...s,
        tongGhe: s.tongGhe ?? s.tong_ghe ?? room?.tongGhe ?? 0
      }
    });
  } catch {
    notification.error("Không thể tải danh sách lịch chiếu");
  } finally {
    loading.value = false;
  }
}

// ── Computed: list tab ─────────────────────────────────────────────────────
const filteredSchedules = computed(() => {
  return phimList.value.map(phim => {
    // Lọc danh sách suất chiếu của phim này theo các tiêu chí đang chọn
    const movieShowtimes = showtimes.value.filter(s => {
      const matchPhim = (s.idPhim ?? s.id_phim) === phim.id;
      const matchRoom = !filterRoomId.value || (s.idPhongChieu ?? s.id_phong_chieu) === filterRoomId.value;
      const matchDate = !filterDate.value || dayjs(s.ngayChieu || s.ngay_chiêu).isSame(dayjs(filterDate.value), 'day');
      const matchStatus = filterStatus.value === '' || (s.trangThai ?? s.trang_thai) === filterStatus.value;
      return matchPhim && matchRoom && matchDate && matchStatus;
    });
    
    const rooms = [...new Set(movieShowtimes.map(s => s.tenPhongChieu || s.ten_phong_chieu))].filter(Boolean);
    
    // Ưu tiên lấy giai đoạn từ phim, nếu không có thì lấy từ các suất chiếu tìm thấy
    let start = phim.ngayKhoiChieu || phim.ngay_khoi_chieu || phim.ngay_phat_hanh;
    let end = phim.ngayKetThuc || phim.ngay_ket_thuc;

    if (!start && movieShowtimes.length > 0) {
      const dates = movieShowtimes.map(s => s.ngayChieu || s.ngay_chieu).filter(Boolean).sort();
      start = dates[0];
      end = dates[dates.length - 1];
    }

    const giaiDoan = start && end
      ? `${dayjs(start).format('DD/MM/YYYY')} - ${dayjs(end).format('DD/MM/YYYY')}`
      : '—';

    // Tính toán trạng thái hiển thị thông minh
    const today = dayjs().startOf('day');
    const startDate = start ? dayjs(start).startOf('day') : null;
    const endDate = end ? dayjs(end).startOf('day') : null;
    const pStatus = phim.trangThai ?? phim.trang_thai;

    let displayStatus = 'Ngừng chiếu';
    let statusType = 'info';

    if (pStatus === 0) {
      displayStatus = 'Ngừng chiếu';
      statusType = 'danger';
    } else if (startDate && today.isBefore(startDate)) {
      displayStatus = 'Sắp chiếu';
      statusType = 'primary';
    } else if (endDate && today.isAfter(endDate)) {
      displayStatus = 'Kết thúc';
      statusType = 'info';
    } else if (startDate) {
      displayStatus = 'Đang chiếu';
      statusType = 'success';
    } else {
      displayStatus = 'Chưa rõ';
      statusType = 'info';
    }

    return {
      ...phim,
      giaiDoan,
      displayStatus,
      statusType,
      tongSuat: movieShowtimes.length,
      phongs: rooms.join(', ') || '—'
    };
  }).filter(m => {
    // Lọc theo từ khóa tìm kiếm
    const matchSearch = !searchQuery.value || m.tenPhim?.toLowerCase().includes(searchQuery.value.toLowerCase());
    
    // Nếu đang có bộ lọc (Phòng/Ngày/Trạng thái), chỉ hiện các phim có suất chiếu thỏa mãn
    const hasFilter = filterRoomId.value || filterDate.value || filterStatus.value !== '';
    if (hasFilter) {
      return matchSearch && m.tongSuat > 0;
    }
    
    return matchSearch;
  });
});

const paginatedSchedules = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredSchedules.value.slice(start, start + pageSize.value);
});

// ── Handlers ───────────────────────────────────────────────────────────────
const handleSearch = debounce(() => {
  currentPage.value = 1;
}, 300);

const resetFilter = () => {
  searchQuery.value = "";
  filterDate.value = null;
  filterRoomId.value = "";
  filterStatus.value = "";
  currentPage.value = 1;
  fetchShowtimes();
};

const viewMovieSchedules = (row) => {
  activeTab.value = "visual";
  // Đợi component Sơ đồ render xong mới set movie
  setTimeout(() => {
    if (visualRef.value?.selectPhimById) {
      visualRef.value.selectPhimById(row.id);
    }
  }, 100);
};

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
  Object.keys(groups).forEach(key => {
    groups[key].sort((a, b) => a.cot - b.cot);
  });
  return groups;
});

const openDialog = (row = null) => {
  // logic linh hoạt: nếu truyền vào suat_chieu (từ tab Sơ đồ) hoặc phim (từ tab Danh sách)
  const isShowtime = row && (row.idKhungGio || row.id_khung_gio);
  
  editingId.value = isShowtime ? row.id : null;
  
  form.value = row
    ? {
      idPhim: row.idPhim || row.id_phim || (isShowtime ? '' : row.id), // row.id của phim nếu không phải suat_chieu
      idPhongChieu: row.idPhongChieu || row.id_phong_chieu || "",
      idKhungGio: row.idKhungGio || row.id_khung_gio || "",
      ngayChieu: row.ngayChieu || row.ngay_chieu || null,
      trangThai: row.trangThai ?? row.trang_thai ?? 1,
    }
    : {
      idPhim: "",
      idPhongChieu: "",
      idKhungGio: "",
      ngayChieu: null,
      trangThai: 1,
    };
  dialogVisible.value = true;
};

const handleAddShowtime = async ({ phimId, roomId, khungId, date }) => {
  const phim = phimList.value.find((p) => p.id === phimId);
  const phong = phongChieuList.value.find((r) => r.id === roomId);
  const khung = allKhungGio.value.find((k) => k.id === khungId);

  const timeStr = khung
    ? `${khung.gioBatDau || khung.gio_bat_dau}`.substring(0, 5)
    : "";
  const dateStr = dayjs(date).format("DD/MM/YYYY");

  const confirmed = await confirmDialog.confirm(
    "Xác nhận thêm lịch chiếu?",
    `Bạn có muốn thêm lịch chiếu phim "<strong>${phim?.tenPhim}</strong>" tại <strong>${phong?.tenPhong}</strong> lúc <strong>${timeStr}</strong> ngày <strong>${dateStr}</strong> không?`,
  );

  if (confirmed) {
    saving.value = true;
    try {
      await suatChieuService.createShowtime({
        idPhim: phimId,
        idPhongChieu: roomId,
        idKhungGio: khungId,
        ngayChieu: date,
        trangThai: 1,
      });
      notification.addSuccess("lịch chiếu");
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      saving.value = false;
    }
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    try {
      if (editingId.value) {
        await confirmDialog.update('lịch chiếu');
      } else {
        await confirmDialog.add('lịch chiếu');
      }
    } catch { return; }

    saving.value = true;
    try {
      if (editingId.value) {
        await suatChieuService.updateShowtime(editingId.value, form.value);
        notification.updateSuccess("lịch chiếu");
      } else {
        await suatChieuService.createShowtime(form.value);
        notification.addSuccess("lịch chiếu");
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      saving.value = false;
    }
  });
};

const onWeekChange = async () => {
  await fetchShowtimes();
};

// ── Helpers ────────────────────────────────────────────────────────────────
const getStatusTag = (s) =>
  ({ 0: "danger", 1: "primary", 2: "success", 3: "info" })[s] || "info";

const getStatusLabel = (s) =>
  ({ 0: "Đã hủy", 1: "Sắp chiếu", 2: "Đang chiếu", 3: "Kết thúc" })[s] || "—";

const formatDate = (d) => (d ? dayjs(d).format("DD/MM/YYYY") : "—");

const formatTime = (t) => (t ? String(t).substring(0, 5) : "—");

// ── Lifecycle ──────────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    const [phongRes, phimRes, khungRes] = await Promise.all([
      suatChieuService.getPhongChieuDropdown(),
      suatChieuService.getPhimDropdown(),
      suatChieuService.getKhungGioDropdown(),
    ]);

    phongChieuList.value = (phongRes.data?.data || []).map((r) => ({
      ...r,
      id: r.id,
      tenPhong: r.tenPhong ?? r.ten_phong,
      loaiManHinh: r.loaiManHinh ?? r.loai_man_hinh,
      tongGhe: r.tongGhe ?? r.tong_ghe,
    }));

    phimList.value = (phimRes.data?.data || []).map((p) => ({
      ...p,
      id: p.id,
      tenPhim: p.tenPhim ?? p.ten_phim,
      thoiLuong: p.thoiLuong ?? p.thoi_luong ?? 0,
      ngayKhoiChieu: p.ngayKhoiChieu ?? p.ngay_khoi_chieu ?? p.ngay_phat_hanh ?? null,
      ngayKetThuc: p.ngayKetThuc ?? p.ngay_ket_thuc ?? null,
      loaiPhim: p.loaiPhim ?? p.loai_phim ?? "2D",
      poster: p.poster ?? "",
      trangThai: p.trangThai ?? p.trang_thai ?? null,
    }));

    allKhungGio.value = (khungRes.data?.data || []).map((k) => ({
      ...k,
      id: k.id,
      tenKhungGio: k.tenKhungGio ?? k.ten_khung_gio,
      gioBatDau: k.gioBatDau ?? k.gio_bat_dau,
      gioKetThuc: k.gioKetThuc ?? k.gio_ket_thuc,
    }));

    await fetchShowtimes();

    // Handle deep linking from Showtimes page
    if (route.query.tab) activeTab.value = route.query.tab;
    if (route.query.roomId) filterRoomId.value = route.query.roomId;
    if (route.query.date) filterDate.value = route.query.date;
  } catch (e) {
    notification.error("Không thể tải dữ liệu ban đầu");
    console.error(e);
  }
});
</script>

<style scoped>
/* ──────────────────────────────────────────────────
   STYLE: Seat Map (Consistent Light Theme)
   ────────────────────────────────────────────────── */
.bg-light-map {
  background: #ffffff;
  border-radius: 0 0 16px 16px;
  min-height: 480px;
  border: 1px solid #f1f5f9;
}

.screen-container {
  width: 100%;
  padding: 0 15%;
}

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

.admin-seat.sm {
  width: 22px;
  height: 22px;
  font-size: 0;
  pointer-events: none;
  border-width: 1px;
}

.admin-seat.available {
  border-color: #cbd5e1;
}

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

.admin-seat.vip.available {
  border-color: #f59e0b;
  color: #b45309;
}

.admin-seat.vip.available:hover {
  border-color: #f59e0b;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.admin-seat.couple.available {
  border-color: #ec4899;
  color: #be185d;
  width: 92px;
}

.admin-seat.couple.available:hover {
  border-color: #ec4899;
  box-shadow: 0 4px 12px rgba(236, 72, 153, 0.15);
}

.bg-light-subtle {
  background-color: #f8fafc !important;
}

.btn-action-icon.action-seat {
  color: #6366f1;
}

.btn-action-icon.action-seat:hover {
  background: #e0e7ff;
}
</style>
