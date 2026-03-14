<template>
  <div class="visual-scheduler animation-fade-in">
    <!-- ══ TRÁI: Bảng chọn phim ══ -->
    <div class="sidebar-phim">
      <div class="sidebar-header">
        <span class="sidebar-title">Danh sách phim</span>
        <el-input v-model="movieSearch" placeholder="Tìm phim..." :prefix-icon="Search" clearable size="small"
          class="sidebar-search" />
      </div>

      <div class="phim-table-wrap custom-scrollbar">
        <table class="phim-table">
          <thead>
            <tr>
              <th class="th-stt">STT</th>
              <th class="th-poster">Poster</th>
              <th>Tên phim</th>
              <th class="th-date">Ngày bắt đầu</th>
              <th class="th-status">Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(phim, idx) in filteredPhimList" :key="phim.id" class="phim-row"
              :class="{ selected: selectedPhim?.id === phim.id }" @click="selectPhim(phim)">
              <td class="td-stt">{{ idx + 1 }}</td>
              <td class="td-poster">
                <img v-if="getPoster(phim)" :src="getPoster(phim)" class="phim-thumb"
                  @error="(e) => (e.target.style.display = 'none')" />
                <div v-else class="phim-thumb-ph">
                  <i class="bi bi-film"></i>
                </div>
              </td>
              <td class="td-name">
                <div class="phim-name-text">{{ getTenPhim(phim) }}</div>
                <div class="phim-sub-text">
                  {{ getThoiLuong(phim) }}p · {{ getLoaiPhim(phim) }}
                </div>
              </td>
              <td class="td-date">{{ formatDate(getNgayKhoiChieu(phim)) }}</td>
              <td class="td-status">
                <el-tag :type="getPhimBadgeType(phim)" size="small" round effect="light">
                  {{ getPhimStatusLabel(phim) }}
                </el-tag>
              </td>
            </tr>
            <tr v-if="filteredPhimList.length === 0">
              <td colspan="5" class="empty-row">
                <i class="bi bi-film opacity-25 me-1"></i>Không tìm thấy phim
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="sidebar-footer">
        <i class="bi bi-hand-index-thumb me-1"></i>Bấm vào phim để xem sơ đồ
        suất chiếu
      </div>
    </div>

    <!-- ══ PHẢI ══ -->
    <div class="main-content">
      <!-- Dòng 1: Thanh ngày + lọc phòng (Premium Toolbar) -->
      <div class="date-bar bg-white border-bottom p-3">
        <div class="d-flex align-items-center justify-content-between">
          <div class="d-flex align-items-center gap-2">
            <el-button-group class="premium-btn-group shadow-sm">
              <el-button @click="expandAll" size="small" :icon="Plus" plain>Mở hết</el-button>
              <el-button @click="collapseAll" size="small" :icon="Delete" plain>Đóng hết</el-button>
            </el-button-group>
            
            <div class="divider-v mx-3" style="width: 1px; height: 24px; background: #e2e8f0;"></div>

            <el-button @click="moveWeek(-1)" circle class="nav-btn" size="small">
              <i class="bi bi-chevron-left"></i>
            </el-button>

            <div class="date-chips">
              <button v-for="day in weekDays" :key="day.date" class="date-chip" :class="{
                active: selectedDate === day.date,
                today: isToday(day.date),
                'has-sc': selectedPhim && phimHasShowtimeOnDate(selectedPhim.id, day.date),
              }" @click="selectedDate = day.date">
                <span class="chip-wd">{{ day.dayName }}</span>
                <span class="chip-d">{{ day.label }}</span>
                <span v-if="selectedPhim && phimHasShowtimeOnDate(selectedPhim.id, day.date)" class="chip-dot"></span>
              </button>
            </div>

            <el-button @click="moveWeek(1)" circle class="nav-btn" size="small">
              <i class="bi bi-chevron-right"></i>
            </el-button>
          </div>

          <div class="d-flex align-items-center gap-2">
            <el-select v-model="filterRoomId" placeholder="Lọc phòng..." clearable style="width: 150px" class="premium-select">
              <el-option label="Tất cả phòng" value="" />
              <el-option v-for="r in phongChieuList" :key="r.id" :label="getTenPhong(r)" :value="r.id" />
            </el-select>
            <el-date-picker v-model="pickedDate" type="date" placeholder="Chọn ngày" format="DD/MM/YYYY"
              class="premium-date-picker" style="width: 140px" @change="onPickDate" />
          </div>
        </div>
      </div>

      <!-- Dòng 2: Stats — khung giờ hiện tại theo phòng -->
      <div class="stats-realtime-row custom-scrollbar-x">
        <div class="stats-realtime-label">
          <i class="bi bi-clock-fill me-1 text-danger"></i>
          <span>{{ nowTimeLabel }}</span>
        </div>
        <div class="stats-realtime-slots">
          <template v-if="currentSlots.length > 0">
            <div v-for="slot in currentSlots" :key="slot.key" class="realtime-slot-chip"
              :class="slot.trangThai === 2 ? 'chip-showing' : 'chip-upcoming'">
              <span class="chip-time">{{ slot.gioBatDau }}–{{ slot.gioKetThuc }}</span>
              <span class="chip-room"><i class="bi bi-door-open me-1"></i>{{ slot.tenPhong }}</span>
              <span class="chip-seats">{{ slot.soGheTrong }} ghế trống</span>
            </div>
          </template>
          <div v-else class="realtime-empty">
            <i class="bi bi-moon-stars me-1"></i>Không có suất chiếu đang diễn
            ra
          </div>
        </div>
      </div>


        <!-- Dòng 4: Sơ đồ -->
        <div class="showtime-area custom-scrollbar">
          <div v-if="!selectedPhim && !filterRoomId" class="empty-state">
            <i class="bi bi-hand-index-thumb fs-1 opacity-15"></i>
            <p class="text-secondary mt-3 small">
              Chọn một phim bên trái HOẶC lọc phòng phía trên để xem sơ đồ
            </p>
          </div>

          <template v-else>
            <!-- TIÊU ĐỀ: Khi lọc theo PHÒNG -->
            <div v-if="filterRoomId" class="room-context-bar mb-3 animate__animated animate__fadeIn">
              <div class="room-info-card">
                <i class="bi bi-door-open-fill room-icon"></i>
                <div class="room-details">
                  <div class="room-title">LỊCH CHIẾU: {{ getTenPhong(phongChieuList.find(r => r.id === filterRoomId)) }}</div>
                  <div class="room-movies-list">
                    <span class="label">Phim hôm nay:</span>
                    <span v-for="mId in Array.from(new Set(todayShowtimes.filter(s => String(scPhong(s)) === String(filterRoomId)).map(s => scPhim(s))))" :key="mId" class="movie-tag-small">
                      {{ getMovieTitleBySc({ idPhim: mId, id_phim: mId }) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- TIÊU ĐỀ: Khi chỉ chọn PHIM -->
            <div v-else-if="selectedPhim" class="selected-phim-bar mb-3 animate__animated animate__fadeIn">
              <img v-if="getPoster(selectedPhim)" :src="getPoster(selectedPhim)" class="sel-poster"
                @error="(e) => (e.target.style.display = 'none')" />
              <div v-else class="sel-poster-ph"><i class="bi bi-film"></i></div>
              <div class="sel-info">
                <div class="sel-name">{{ getTenPhim(selectedPhim) }}</div>
                <div class="sel-meta">
                  {{ getThoiLuong(selectedPhim) }} phút ·
                  {{ getLoaiPhim(selectedPhim) }}
                </div>
              </div>
              <el-tag class="ms-auto" :type="getPhimBadgeType(selectedPhim)" size="small" round effect="light">
                {{ getPhimStatusLabel(selectedPhim) }}
              </el-tag>
            </div>

          <!-- Cảnh báo không có khung giờ -->
          <div v-if="!allKhungGio || allKhungGio.length === 0" class="no-kg-hint">
            <i class="bi bi-exclamation-circle me-2 text-warning"></i>
            Chưa có dữ liệu khung giờ. Vui lòng kiểm tra API
            <code>/khung-gio-dropdown</code>
          </div>

          <!-- GROUP THEO PHÒNG -->
          <template v-else>
            <div v-for="room in visibleRooms" :key="room.id" class="room-group" :class="{ 'is-expanded': expandedRooms.includes(room.id) }">
              <!-- Header phòng (Clickable to toggle) -->
              <div class="room-header" @click="toggleRoom(room.id)">
                <div class="d-flex align-items-center flex-grow-1">
                  <i class="bi bi-door-open-fill me-2" style="color: #e31e24"></i>
                  <span class="room-name">{{ getTenPhong(room) }}</span>
                  <span v-if="getLoaiManHinh(room)" class="room-tag tag-screen ms-2">
                    {{ getLoaiManHinh(room) }}
                  </span>
                  <span class="room-tag ms-2">{{ getTongGhe(room) }} ghế</span>
                </div>

                <!-- Thông báo số suất -->
                <div class="room-meta-info d-flex align-items-center gap-3">
                  <span class="room-tag tag-count">
                    {{ (selectedPhim ? getShowtimesForRoom(selectedPhim.id, room.id) : todayShowtimes.filter(s => scPhong(s) === room.id)).length }} /
                    {{ allKhungGio.length }} suất
                  </span>
                  
                  <!-- Nút bấm hiệu ứng -->
                  <div class="btn-expand-effect">
                    <span class="expand-text">{{ expandedRooms.includes(room.id) ? 'Thu gọn' : 'Xem chi tiết' }}</span>
                    <div class="icon-circle shadow-sm">
                      <i class="bi" :class="expandedRooms.includes(room.id) ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Grid khung giờ (Animated) -->
              <transition name="room-expand">
                <div v-if="expandedRooms.includes(room.id)" class="slots-grid-wrap">
                  <div class="slots-grid custom-scrollbar-x">
                    <div v-for="kg in allKhungGio" :key="kg.id" class="slot-card"
                      :class="getSlotClass(selectedPhim.id, room.id, kg.id)"
                      @click="handleSlotClick(selectedPhim.id, room.id, kg.id)">
                      <div class="slot-stripe"></div>
                      <div class="slot-body">
                        <div class="slot-time-row">
                          <span class="slot-time-big">{{ fTime(kg, "gioBatDau", "gio_bat_dau") }}</span>
                        </div>
                        
                        <div v-if="getAnyShowtime(room.id, kg.id)" class="slot-movie-title">
                          {{ getMovieTitleBySc(getAnyShowtime(room.id, kg.id)) }}
                        </div>
                        <div v-else class="slot-kg-name">{{ getKgName(kg) }}</div>

                        <template v-if="getAnyShowtime(room.id, kg.id)">
                          <div class="slot-seats-box">
                            Còn <strong>{{ scGheTrong(getAnyShowtime(room.id, kg.id)) }}</strong>/{{ getTongGhe(room) }}
                          </div>
                          <div class="d-flex align-items-center justify-content-between mt-1">
                            <div class="slot-status-badge">
                              {{ getScStatusLabel(getAnyShowtime(room.id, kg.id)) }}
                            </div>
                            <el-tooltip content="Xem sơ đồ ghế" placement="top">
                              <button class="btn-mini-view shadow-sm" @click.stop="emit('view', getAnyShowtime(room.id, kg.id))">
                                <i class="bi bi-grid-3x3-gap-fill"></i>
                              </button>
                            </el-tooltip>
                          </div>
                        </template>
                        <template v-else>
                          <div class="slot-add-btn">
                            <i class="bi bi-plus-circle"></i>
                            <span>Thêm suất</span>
                          </div>
                        </template>
                      </div>
                    </div>
                  </div>
                </div>
              </transition>
            </div>

            <!-- Không có phòng nào sau khi lọc -->
            <div v-if="visibleRooms.length === 0" class="empty-state">
              <i class="bi bi-building fs-1 opacity-15"></i>
              <p class="text-secondary mt-3 small">
                Không có suất chiếu cho phim này hôm nay
              </p>
            </div>
          </template>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { Search, ArrowLeft, ArrowRight, Plus, Delete } from "@element-plus/icons-vue";
import dayjs from "dayjs";

const props = defineProps({
  showtimes: { type: Array, required: true },
  phongChieuList: { type: Array, required: true },
  phimList: { type: Array, required: true },
  allKhungGio: { type: Array, default: () => [] },
  initialDate: { type: String, default: "" },
  initialRoomId: { type: [String, Number], default: "" },
  initialMovieId: { type: [String, Number], default: "" },
});
const emit = defineEmits(["view", "addShowtime", "editShowtime", "weekChange"]);

// ── Field helpers — đọc cả camelCase lẫn snake_case ───────────────────────
const fv = (obj, ...keys) => {
  if (!obj) return null;
  for (const k of keys) {
    const v = obj[k];
    if (v !== undefined && v !== null && v !== "") return v;
  }
  return null;
};
const fTime = (obj, ...keys) => {
  const v = fv(obj, ...keys);
  return v ? String(v).substring(0, 5) : "—";
};

// Phim helpers
const getTenPhim = (p) => fv(p, "tenPhim", "ten_phim") ?? "";
const getThoiLuong = (p) => fv(p, "thoiLuong", "thoi_luong") ?? 0;
const getLoaiPhim = (p) => fv(p, "loaiPhim", "loai_phim") ?? "2D";
const getPoster = (p) => fv(p, "poster") ?? "";
const getNgayKhoiChieu = (p) =>
  fv(p, "ngayKhoiChieu", "ngay_khoi_chieu") ?? null;
const getTrangThaiPhim = (p) => fv(p, "trangThai", "trang_thai");

// Phong helpers
const getTenPhong = (r) => fv(r, "tenPhong", "ten_phong") ?? "";
const getLoaiManHinh = (r) => fv(r, "loaiManHinh", "loai_man_hinh") ?? "";
const getTongGhe = (r) => fv(r, "tongGhe", "tong_ghe") ?? 0;

// KhungGio helpers
const getKgName = (k) => fv(k, "tenKhungGio", "ten_khung_gio") ?? "";

// Suat chieu helpers
const scNgay = (sc) => fv(sc, "ngayChieu", "ngay_chieu");
const scPhong = (sc) => fv(sc, "idPhongChieu", "id_phong_chieu");
const scPhim = (sc) => fv(sc, "idPhim", "id_phim");
const scKhung = (sc) => fv(sc, "idKhungGio", "id_khung_gio");
const scTrangThai = (sc) => fv(sc, "trangThai", "trang_thai");
const scGheTrong = (sc) => fv(sc, "soGheTrong", "so_ghe_trong") ?? 0;

// ── State ─────────────────────────────────────────────────────────────────
const movieSearch = ref("");
const selectedPhim = ref(null);
const selectedDate = ref(dayjs().format("YYYY-MM-DD"));
const weekOffset = ref(0);
const pickedDate = ref(null);
const filterRoomId = ref("");
const nowTime = ref(dayjs().format("HH:mm"));
const expandedRooms = ref([]);

const toggleRoom = (id) => {
  const index = expandedRooms.value.indexOf(id);
  if (index > -1) expandedRooms.value.splice(index, 1);
  else expandedRooms.value.push(id);
};

// Hàm tự động chọn phim và mở phòng khi có tham số khởi tạo
const handleInitialAutoSelection = () => {
  if (!props.showtimes || props.showtimes.length === 0) return;
  if (selectedPhim.value) return; // Đã chọn rồi thì thôi

  const targetRoomId = props.initialRoomId;
  const targetDate = props.initialDate || selectedDate.value;

  if (targetRoomId) {
    // Tìm suất chiếu đầu tiên của phòng này trong ngày này
    const firstSc = props.showtimes.find(sc => 
      String(sc.idPhongChieu || sc.id_phong_chieu) === String(targetRoomId) &&
      scNgay(sc)?.substring(0, 10) === targetDate
    );

    if (firstSc) {
      const movieId = firstSc.idPhim || firstSc.id_phim;
      const movie = props.phimList.find(p => p.id === movieId);
      if (movie) {
        selectedPhim.value = movie;
        // Tự động mở rộng phòng này
        if (!expandedRooms.value.includes(targetRoomId)) {
          expandedRooms.value.push(targetRoomId);
        }
      }
    }
  } else if (props.initialMovieId) {
    const movie = props.phimList.find(p => p.id === props.initialMovieId);
    if (movie) selectedPhim.value = movie;
  }
};

const expandAll = () => {
  expandedRooms.value = visibleRooms.value.map(r => r.id);
};

const collapseAll = () => {
  expandedRooms.value = [];
};

// Tick đồng hồ realtime mỗi phút
let clockTimer = null;
onMounted(() => {
  if (props.initialDate) {
    selectedDate.value = props.initialDate;
    pickedDate.value = props.initialDate;
  }
  if (props.initialRoomId) {
    filterRoomId.value = props.initialRoomId;
  }

  handleInitialAutoSelection();

  clockTimer = setInterval(() => {
    nowTime.value = dayjs().format("HH:mm");
  }, 60000);
});
onUnmounted(() => clearInterval(clockTimer));

const nowTimeLabel = computed(() => dayjs().format("HH:mm - DD/MM/YYYY"));

// ── Week ──────────────────────────────────────────────────────────────────
const weekDays = computed(() => {
  const start = dayjs()
    .add(weekOffset.value, "week")
    .startOf("week")
    .add(1, "day");
  const names = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"];
  return Array.from({ length: 7 }, (_, i) => {
    const d = start.add(i, "day");
    return {
      date: d.format("YYYY-MM-DD"),
      label: d.format("DD/MM"),
      dayName: names[i],
    };
  });
});
const isToday = (d) => d === dayjs().format("YYYY-MM-DD");

// ── Phim ─────────────────────────────────────────────────────────────────
const filteredPhimList = computed(() => {
  if (!movieSearch.value) return props.phimList;
  const q = movieSearch.value.toLowerCase();
  return props.phimList.filter((p) => getTenPhim(p).toLowerCase().includes(q));
});

const getPhimBadgeType = (phim) => {
  const st = getTrangThaiPhim(phim);
  if (st === 1) return 'success';
  if (st === 2) return 'danger';
  return 'info';
};
const getPhimStatusLabel = (phim) => {
  return (
    { 1: "Đang chiếu", 2: "Sắp chiếu", 0: "Ngừng chiếu" }[
    getTrangThaiPhim(phim)
    ] ?? "—"
  );
};

// ── Showtimes ─────────────────────────────────────────────────────────────
const todayShowtimes = computed(() =>
  props.showtimes.filter(
    (sc) => scNgay(sc)?.substring(0, 10) === selectedDate.value,
  ),
);

const phimHasShowtimeOnDate = (phimId, date) =>
  props.showtimes.some(
    (sc) => scPhim(sc) === phimId && scNgay(sc)?.substring(0, 10) === date,
  );

// Lấy 1 suất: phim + phòng + khung giờ
const getShowtime = (phimId, roomId, khungId) =>
  todayShowtimes.value.find(
    (sc) =>
      scPhim(sc) === phimId &&
      scPhong(sc) === roomId &&
      scKhung(sc) === khungId,
  ) || null;

// Tất cả suất của phim trong 1 phòng hôm nay
const getShowtimesForRoom = (phimId, roomId) =>
  todayShowtimes.value.filter(
    (sc) => scPhim(sc) === phimId && scPhong(sc) === roomId,
  );

// ── Phòng hiển thị (theo filter) ─────────────────────────────────────────
const visibleRooms = computed(() => {
  let rooms = props.phongChieuList;
  if (filterRoomId.value)
    rooms = rooms.filter((r) => r.id === filterRoomId.value);
  return rooms;
});

// ── Slot ─────────────────────────────────────────────────────────────────
const getAnyShowtime = (roomId, khungId) =>
  todayShowtimes.value.find(
    (sc) => String(scPhong(sc)) === String(roomId) && String(scKhung(sc)) === String(khungId)
  ) || null;

const getMovieTitleBySc = (sc) => {
  const movieId = scPhim(sc);
  const movie = props.phimList.find(p => String(p.id) === String(movieId));
  return movie ? (movie.tenPhim || movie.ten_phim) : "—";
};

const getSlotClass = (phimId, roomId, khungId) => {
  const sc = getAnyShowtime(roomId, khungId);
  if (!sc) return "slot-empty";
  
  const tt = scTrangThai(sc);
  // Nếu đang lọc theo PHÒNG cụ thể, tất cả suất chiếu đều hiện ĐỎ (Active)
  const isSelectedPhim = selectedPhim.value && String(scPhim(sc)) === String(selectedPhim.value.id);
  const shouldBeActive = filterRoomId.value || isSelectedPhim;
  
  return {
    "slot-upcoming": tt === 1 && shouldBeActive,
    "slot-showing": tt === 2 && shouldBeActive,
    "slot-ended": tt === 3,
    "slot-other-movie": !shouldBeActive, 
    "slot-active": shouldBeActive,
  };
};

const getScStatusLabel = (sc) =>
  ({ 0: "Đã hủy", 1: "Sắp chiếu", 2: "Đang chiếu", 3: "Kết thúc" })[
  scTrangThai(sc)
  ] ?? "—";

// ── Watch ─────────────────────────────────────────────────────────────
import { watch } from 'vue';
watch(() => props.showtimes, () => {
  handleInitialAutoSelection();
}, { deep: true });

// ── Realtime current slots ────────────────────────────────────────────────
// Các suất đang chiếu hoặc sắp chiếu trong ~30 phút tới tại ngày hôm nay
const currentSlots = computed(() => {
  if (!isToday(selectedDate.value)) return [];
  const now = nowTime.value; // HH:mm
  const soon = dayjs().add(30, "minute").format("HH:mm");

  return todayShowtimes.value
    .filter((sc) => {
      const kg = props.allKhungGio.find((k) => k.id === scKhung(sc));
      if (!kg) return false;
      const start = fTime(kg, "gioBatDau", "gio_bat_dau");
      const end = fTime(kg, "gioKetThuc", "gio_ket_thuc");
      // Đang chiếu hoặc bắt đầu trong 30 phút tới
      return (start <= now && end >= now) || (start > now && start <= soon);
    })
    .map((sc) => {
      const kg = props.allKhungGio.find((k) => k.id === scKhung(sc));
      const room = props.phongChieuList.find((r) => r.id === scPhong(sc));
      return {
        key: sc.id,
        gioBatDau: fTime(kg, "gioBatDau", "gio_bat_dau"),
        gioKetThuc: fTime(kg, "gioKetThuc", "gio_ket_thuc"),
        tenPhong: getTenPhong(room),
        soGheTrong: scGheTrong(sc),
        trangThai: scTrangThai(sc),
      };
    });
});

// ── Actions ───────────────────────────────────────────────────────────────
const handleSlotClick = (phimId, roomId, khungId) => {
  const sc = getAnyShowtime(roomId, khungId);
  sc
    ? emit("editShowtime", sc)
    : emit("addShowtime", {
      phimId,
      roomId,
      khungId,
      date: selectedDate.value,
    });
};

const selectPhimById = (id) => {
  const movie = props.phimList.find(p => p.id === id);
  if (movie) selectedPhim.value = movie;
};

defineExpose({ selectPhimById });
const selectPhim = (phim) => {
  selectedPhim.value = selectedPhim.value?.id === phim.id ? null : phim;
};
const moveWeek = (dir) => {
  weekOffset.value += dir;
  selectedDate.value = weekDays.value[0].date;
  emit("weekChange", weekOffset.value);
};
const onPickDate = (val) => {
  if (!val) return;
  selectedDate.value = val;
  weekOffset.value = dayjs(val)
    .startOf("week")
    .diff(dayjs().startOf("week"), "week");
};
const formatDate = (d) => {
  if (!d) return "—";
  try {
    return dayjs(d).format("DD/MM/YYYY");
  } catch {
    return "—";
  }
};
</script>

<style scoped>
/* ══ Layout ══ */
.visual-scheduler {
  display: flex;
  height: 100%;
  min-height: 0;
  overflow: hidden;
  background: #f8fafc;
}

/* ══ SIDEBAR ══ */
.sidebar-phim {
  width: 460px;
  min-width: 460px;
  min-height: 0;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 12px 14px 10px;
  border-bottom: 1px solid #f1f5f9;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sidebar-title {
  font-size: 15px;
  font-weight: 850;
  color: #0f172a;
  letter-spacing: -0.3px;
}

.phim-table-wrap {
  flex: 1;
  overflow-y: auto;
}

.phim-table {
  width: 100%;
  border-collapse: collapse;
}

.phim-table thead tr {
  border-bottom: 2px solid #f1f5f9;
}

.phim-table th {
  padding: 10px 8px;
  font-size: 11px;
  font-weight: 750;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  text-align: left;
  background: #fafafa;
  position: sticky;
  top: 0;
  z-index: 1;
}

.th-stt {
  width: 36px;
  text-align: center;
}

.th-poster {
  width: 50px;
}

.th-date {
  width: 135px;
  white-space: nowrap;
}

.th-status {
  width: 90px;
  text-align: center;
}

.phim-row {
  cursor: pointer;
  border-bottom: 1px solid #f8fafc;
  transition: background 0.12s;
}

.phim-row:hover {
  background: #f8fafc;
}

.phim-row.selected {
  background: #fef2f2;
  border-left: 4px solid #e31e24;
  box-shadow: inset 4px 0 0 -1px #e31e24;
}

.phim-row.selected .phim-name-text {
  color: #e31e24;
}

.phim-row td {
  padding: 7px 8px;
  vertical-align: middle;
}

.td-stt {
  text-align: center;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
}

.phim-thumb {
  width: 36px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.4s ease;
}

.phim-thumb:hover, .sel-poster:hover {
  animation: poster-wobble 0.6s ease-in-out infinite;
  cursor: pointer;
}

@keyframes poster-wobble {
  0% { transform: rotate(0deg) scale(1.1); }
  25% { transform: rotate(-5deg) scale(1.1); }
  75% { transform: rotate(5deg) scale(1.1); }
  100% { transform: rotate(0deg) scale(1.1); }
}

.phim-thumb-ph {
  width: 36px;
  height: 50px;
  background: #f1f5f9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 14px;
}

.phim-name-text {
  font-size: 13px;
  font-weight: 800;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.phim-sub-text {
  font-size: 10px;
  color: #94a3b8;
}

.td-date {
  font-size: 11px;
  color: #64748b;
  white-space: nowrap;
}

.sidebar-footer {
  padding: 8px 14px;
  font-size: 11px;
  color: #94a3b8;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
  text-align: center;
}

/* ══ MAIN ══ */
.main-content {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.date-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
}

.date-chips {
  display: flex;
  gap: 4px;
  flex: 1;
}

.date-chip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 0;
  border-radius: 14px;
  border: 2px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  transition: all 0.18s;
  position: relative;
  flex: 1;
  min-width: 0;
}

.date-chip:hover {
  border-color: #e31e24;
  color: #e31e24;
  background: #fff5f5;
}

.date-chip.today {
  border-color: #e31e24;
  color: #e31e24;
}

.date-chip.active {
  background: #e31e24;
  border-color: #e31e24;
  color: #fff;
}

.date-chip.has-sc:not(.active) {
  background: #fff5f5;
  border-color: #fca5a5;
  color: #e31e24;
}

.chip-wd {
  font-size: 10px;
  font-weight: 700;
  opacity: 0.75;
}

.chip-d {
  font-size: 16px;
  font-weight: 900;
}

.chip-dot {
  position: absolute;
  bottom: 4px;
  left: 50%;
  transform: translateX(-50%);
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.8;
}

.date-chip.active .chip-dot {
  background: #fff;
}

.stats-realtime-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  background: #fff8f8;
  border-bottom: 1px solid #fee2e2;
  overflow-x: auto;
  min-height: 52px;
}

.stats-realtime-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-weight: 800;
  color: #e31e24;
  background: #fef2f2;
  padding: 4px 10px;
  border-radius: 20px;
  border: 1px solid #fca5a5;
}

.stats-realtime-slots {
  display: flex;
  gap: 8px;
  align-items: center;
}

.realtime-slot-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  border: 1.5px solid transparent;
}

.chip-showing {
  background: #f0fdf4;
  border-color: #86efac;
  color: #16a34a;
}

.chip-upcoming {
  background: #fff8f8;
  border-color: #fca5a5;
  color: #e31e24;
}

.showtime-area {
  flex: 1;
  overflow-y: auto;
  padding: 0;
  background: #f8fafc;
}

/* Info bar phim */
.selected-phim-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 20px;
  background: #fff;
  border-bottom: 3px solid #f1f5f9;
  position: sticky;
  top: 0;
  z-index: 10;
}

.sel-poster {
  width: 45px;
  height: 65px;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.sel-poster-ph {
  width: 45px;
  height: 65px;
  background: #f1f5f9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}

.sel-name {
  font-size: 18px;
  font-weight: 900;
  color: #0f172a;
  line-height: 1.2;
}

.sel-meta {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-top: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  text-align: center;
}

.room-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.room-group:not(.is-expanded) .room-header:hover {
  background: #f8fafc;
}

.is-expanded .room-header {
  background: #fff;
  border-bottom-color: #f1f5f9;
}

/* Nút bấm hiệu ứng */
.btn-expand-effect {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 4px 4px 15px;
  background: #f1f5f9;
  border-radius: 30px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.room-header:hover .btn-expand-effect {
  background: #e31e24;
  color: #fff;
  transform: translateX(-5px);
  box-shadow: 0 4px 12px rgba(227, 30, 36, 0.2);
}

.expand-text {
  font-size: 11px;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.icon-circle {
  width: 26px;
  height: 26px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e31e24;
  transition: all 0.3s ease;
}

.is-expanded .btn-expand-effect {
  background: #1e293b;
  color: #fff;
}

.is-expanded .icon-circle {
  color: #1e293b;
}

.room-tag {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 6px;
  background: #f1f5f9;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.tag-screen {
  background: #ede9fe;
  color: #7c3aed;
  border-color: #ddd6fe;
}

/* ── Room Context Bar ── */
.room-context-bar {
  background: #fff;
  border: 1px solid #e31e24;
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 4px 12px rgba(227, 30, 36, 0.08);
}

.room-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.room-icon {
  font-size: 28px;
  color: #e31e24;
  background: #fef2f2;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.room-title {
  font-size: 16px;
  font-weight: 850;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.room-movies-list {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.room-movies-list .label {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
}

.movie-tag-small {
  background: #f1f5f9;
  color: #0f172a;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
}

.sel-poster {
  width: 40px;
  height: 56px;
  border-radius: 6px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.4s ease;
}

.slots-grid-wrap {
  background: #fafafa;
  overflow: hidden;
}

.slots-grid {
  display: flex;
  overflow-x: auto;
  gap: 15px;
  padding: 20px;
  scroll-behavior: smooth;
}

/* Animation cho expand */
.room-expand-enter-active,
.room-expand-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  max-height: 500px;
}

.room-expand-enter-from,
.room-expand-leave-to {
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px);
}

.slot-card {
  flex: 0 0 180px;
  position: relative;
  overflow: hidden;
  background: #fff;
  border: 1.5px solid #e2e8f0;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
}

.slot-card:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.08);
  border-color: #e31e24;
}

.slot-stripe {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 5px;
}

/* Các màu sắc trạng thái */
.slot-upcoming .slot-stripe { background: #e31e24; }
.slot-showing .slot-stripe { background: #16a34a; }
.slot-ended .slot-stripe { background: #94a3b8; }
.slot-cancelled .slot-stripe { background: #ef4444; }
.slot-empty .slot-stripe { background: #cbd5e1; }

.slot-upcoming { border-color: #fecdd3; background: #fff; }
.slot-showing { border-color: #bbf7d0; background: #f0fdf4; }
.slot-ended { opacity: 0.7; background: #f8fafc; border-style: dashed; }

.slot-cancelled {
  border-color: #fca5a5;
  background: #fff5f5;
  opacity: 0.8;
}

.slot-empty {
  border-color: #f1f5f9;
  background: #fafafa;
  cursor: default;
}

.slot-body {
  padding: 16px;
}

.slot-time-row {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 2px;
}

.slot-time-big {
  font-size: 24px;
  font-weight: 900;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.slot-time-small {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

.slot-kg-name {
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 10px;
}

.slot-seats-box {
  display: inline-block;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 9px;
  border-radius: 6px;
  margin-bottom: 6px;
  background: rgba(227, 30, 36, 0.08);
  color: #e31e24;
}

.slot-showing .slot-seats-box {
  background: rgba(22, 163, 74, 0.08);
  color: #16a34a;
}

.slot-ended .slot-seats-box {
  background: #f1f5f9;
  color: #94a3b8;
}

.slot-cancelled .slot-seats-box {
  background: #fee2e2;
  color: #dc2626;
}

.slot-status-badge {
  display: inline-block;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 20px;
  background: rgba(227, 30, 36, 0.08);
  color: #e31e24;
}

.slot-showing .slot-status-badge {
  background: #dcfce7;
  color: #16a34a;
}

.slot-ended .slot-status-badge {
  background: #f1f5f9;
  color: #94a3b8;
}

.slot-cancelled .slot-status-badge {
  background: #fee2e2;
  color: #dc2626;
}

.slot-no-sc {
  font-size: 11px;
  color: #cbd5e1;
  font-style: italic;
}

.custom-scrollbar-x::-webkit-scrollbar {
  height: 4px;
}

.custom-scrollbar-x::-webkit-scrollbar-thumb {
  background: #fca5a5;
  border-radius: 10px;
}

.btn-mini-view {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  transition: all 0.2s;
  padding: 0;
}

.btn-mini-view:hover {
  background: #f8fafc;
  color: #e31e24;
  border-color: #fca5a5;
  transform: scale(1.1);
}

.btn-mini-view i {
  font-size: 11px;
}
</style>
