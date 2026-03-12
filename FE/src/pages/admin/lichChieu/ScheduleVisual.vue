<template>
  <div class="visual-scheduler animation-fade-in">
    <!-- ══ TRÁI: Bảng chọn phim ══ -->
    <div class="sidebar-phim">
      <div class="sidebar-header">
        <span class="sidebar-title">Danh sách phim</span>
        <el-input
          v-model="movieSearch"
          placeholder="Tìm phim..."
          :prefix-icon="Search"
          clearable
          size="small"
          class="sidebar-search"
        />
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
            <tr
              v-for="(phim, idx) in filteredPhimList"
              :key="phim.id"
              class="phim-row"
              :class="{ selected: selectedPhim?.id === phim.id }"
              @click="selectPhim(phim)"
            >
              <td class="td-stt">{{ idx + 1 }}</td>
              <td class="td-poster">
                <img
                  v-if="getPoster(phim)"
                  :src="getPoster(phim)"
                  class="phim-thumb"
                  @error="(e) => (e.target.style.display = 'none')"
                />
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
                <span class="status-badge" :class="getPhimBadgeClass(phim)">
                  {{ getPhimStatusLabel(phim) }}
                </span>
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
      <!-- Dòng 1: Thanh ngày + lọc phòng -->
      <div class="date-bar">
        <el-button
          circle
          size="small"
          class="nav-btn"
          @click="moveWeek(-1)"
          :icon="ArrowLeft"
        />
        <div class="date-chips">
          <button
            v-for="day in weekDays"
            :key="day.date"
            class="date-chip"
            :class="{
              active: selectedDate === day.date,
              today: isToday(day.date),
              'has-sc':
                selectedPhim &&
                phimHasShowtimeOnDate(selectedPhim.id, day.date),
            }"
            @click="selectedDate = day.date"
          >
            <span class="chip-wd">{{ day.dayName }}</span>
            <span class="chip-d">{{ day.label }}</span>
            <span
              v-if="
                selectedPhim && phimHasShowtimeOnDate(selectedPhim.id, day.date)
              "
              class="chip-dot"
            ></span>
          </button>
        </div>
        <el-button
          circle
          size="small"
          class="nav-btn"
          @click="moveWeek(1)"
          :icon="ArrowRight"
        />
        <el-date-picker
          v-model="pickedDate"
          type="date"
          format="DD/MM/YYYY"
          value-format="YYYY-MM-DD"
          class="date-picker-icon"
          @change="onPickDate"
        />

        <!-- Lọc phòng chiếu -->
        <el-select
          v-model="filterRoomId"
          placeholder="Tất cả phòng"
          clearable
          size="default"
          style="width: 180px; flex-shrink: 0"
        >
          <template #prefix
            ><i class="bi bi-door-open" style="font-size: 13px"></i
          ></template>
          <el-option label="Tất cả phòng" value="" />
          <el-option
            v-for="r in phongChieuList"
            :key="r.id"
            :label="getTenPhong(r)"
            :value="r.id"
          />
        </el-select>
      </div>

      <!-- Dòng 2: Stats — khung giờ hiện tại theo phòng -->
      <div class="stats-realtime-row custom-scrollbar-x">
        <div class="stats-realtime-label">
          <i class="bi bi-clock-fill me-1 text-danger"></i>
          <span>{{ nowTimeLabel }}</span>
        </div>
        <div class="stats-realtime-slots">
          <template v-if="currentSlots.length > 0">
            <div
              v-for="slot in currentSlots"
              :key="slot.key"
              class="realtime-slot-chip"
              :class="slot.trangThai === 2 ? 'chip-showing' : 'chip-upcoming'"
            >
              <span class="chip-time"
                >{{ slot.gioBatDau }}–{{ slot.gioKetThuc }}</span
              >
              <span class="chip-room"
                ><i class="bi bi-door-open me-1"></i>{{ slot.tenPhong }}</span
              >
              <span class="chip-seats">{{ slot.soGheTrong }} ghế trống</span>
            </div>
          </template>
          <div v-else class="realtime-empty">
            <i class="bi bi-moon-stars me-1"></i>Không có suất chiếu đang diễn
            ra
          </div>
        </div>
      </div>

      <!-- Dòng 3: Legend -->
      <div class="legend-bar">
        <span class="lg-item"
          ><span class="lg-dot dot-upcoming"></span>Sắp chiếu</span
        >
        <span class="lg-item"
          ><span class="lg-dot dot-showing"></span>Đang chiếu</span
        >
        <span class="lg-item"
          ><span class="lg-dot dot-ended"></span>Kết thúc</span
        >
        <span class="lg-item"
          ><span class="lg-dot dot-cancelled"></span>Đã hủy</span
        >
        <span class="lg-item" style="margin-left: 8px">
          <span class="lg-dot dot-empty"></span>Chưa có suất
        </span>
      </div>

      <!-- Dòng 4: Sơ đồ -->
      <div class="showtime-area custom-scrollbar">
        <div v-if="!selectedPhim" class="empty-state">
          <i class="bi bi-hand-index-thumb fs-1 opacity-15"></i>
          <p class="text-secondary mt-3 small">
            Chọn một phim bên trái để xem sơ đồ suất chiếu
          </p>
        </div>

        <template v-else>
          <!-- Info bar phim -->
          <div class="selected-phim-bar">
            <img
              v-if="getPoster(selectedPhim)"
              :src="getPoster(selectedPhim)"
              class="sel-poster"
              @error="(e) => (e.target.style.display = 'none')"
            />
            <div v-else class="sel-poster-ph"><i class="bi bi-film"></i></div>
            <div class="sel-info">
              <div class="sel-name">{{ getTenPhim(selectedPhim) }}</div>
              <div class="sel-meta">
                {{ getThoiLuong(selectedPhim) }} phút ·
                {{ getLoaiPhim(selectedPhim) }}
              </div>
            </div>
            <span
              class="status-badge ms-auto"
              :class="getPhimBadgeClass(selectedPhim)"
            >
              {{ getPhimStatusLabel(selectedPhim) }}
            </span>
          </div>

          <!-- Cảnh báo không có khung giờ -->
          <div
            v-if="!allKhungGio || allKhungGio.length === 0"
            class="no-kg-hint"
          >
            <i class="bi bi-exclamation-circle me-2 text-warning"></i>
            Chưa có dữ liệu khung giờ. Vui lòng kiểm tra API
            <code>/khung-gio-dropdown</code>
          </div>

          <!-- GROUP THEO PHÒNG -->
          <template v-else>
            <div v-for="room in visibleRooms" :key="room.id" class="room-group">
              <!-- Header phòng -->
              <div class="room-header">
                <i class="bi bi-door-open-fill me-2" style="color: #e31e24"></i>
                <span class="room-name">{{ getTenPhong(room) }}</span>
                <span v-if="getLoaiManHinh(room)" class="room-tag tag-screen">
                  {{ getLoaiManHinh(room) }}
                </span>
                <span class="room-tag">{{ getTongGhe(room) }} ghế</span>
                <!-- Tổng suất trong phòng này hôm nay -->
                <span class="room-tag tag-count ms-auto">
                  {{ getShowtimesForRoom(selectedPhim.id, room.id).length }} /
                  {{ allKhungGio.length }} suất
                </span>
              </div>

              <!-- Grid khung giờ của phòng này -->
              <div class="slots-grid">
                <div
                  v-for="kg in allKhungGio"
                  :key="kg.id"
                  class="slot-card"
                  :class="getSlotClass(selectedPhim.id, room.id, kg.id)"
                  @click="handleSlotClick(selectedPhim.id, room.id, kg.id)"
                >
                  <div class="slot-stripe"></div>
                  <div class="slot-body">
                    <!-- Giờ -->
                    <div class="slot-time-row">
                      <span class="slot-time-big">{{
                        fTime(kg, "gioBatDau", "gio_bat_dau")
                      }}</span>
                      <span class="slot-time-small"
                        >~{{ fTime(kg, "gioKetThuc", "gio_ket_thuc") }}</span
                      >
                    </div>
                    <!-- Tên khung giờ -->
                    <div class="slot-kg-name">{{ getKgName(kg) }}</div>

                    <!-- Có suất -->
                    <template
                      v-if="getShowtime(selectedPhim.id, room.id, kg.id)"
                    >
                      <div class="slot-seats-box">
                        Còn
                        <strong>{{
                          getSeats(selectedPhim.id, room.id, kg.id)
                        }}</strong
                        >/{{ getTongGhe(room) }}
                      </div>
                      <div class="slot-status-badge">
                        {{
                          getScStatusLabel(
                            getShowtime(selectedPhim.id, room.id, kg.id),
                          )
                        }}
                      </div>
                    </template>

                    <!-- Chưa có suất -->
                    <template v-else>
                      <div class="slot-no-sc">Chưa có suất</div>
                    </template>
                  </div>
                </div>
              </div>
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
import { Search, ArrowLeft, ArrowRight } from "@element-plus/icons-vue";
import dayjs from "dayjs";

const props = defineProps({
  showtimes: { type: Array, required: true },
  phongChieuList: { type: Array, required: true },
  phimList: { type: Array, required: true },
  allKhungGio: { type: Array, default: () => [] },
});
const emit = defineEmits(["view", "addShowtime", "weekChange"]);

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

// Tick đồng hồ realtime mỗi phút
let clockTimer = null;
onMounted(() => {
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

const getPhimBadgeClass = (phim) => {
  const st = getTrangThaiPhim(phim);
  if (st === 1) return "badge-showing";
  if (st === 2) return "badge-upcoming";
  return "badge-stopped";
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
const getSeats = (phimId, roomId, khungId) => {
  const sc = getShowtime(phimId, roomId, khungId);
  return sc ? scGheTrong(sc) : 0;
};

const getSlotClass = (phimId, roomId, khungId) => {
  const sc = getShowtime(phimId, roomId, khungId);
  if (!sc) return "slot-empty";
  const tt = scTrangThai(sc);
  return {
    "slot-upcoming": tt === 1,
    "slot-showing": tt === 2,
    "slot-ended": tt === 3,
    "slot-cancelled": tt === 0,
  };
};

const getScStatusLabel = (sc) =>
  ({ 0: "Đã hủy", 1: "Sắp chiếu", 2: "Đang chiếu", 3: "Kết thúc" })[
    scTrangThai(sc)
  ] ?? "—";

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
  const sc = getShowtime(phimId, roomId, khungId);
  sc
    ? emit("view", sc)
    : emit("addShowtime", {
        phimId,
        roomId,
        khungId,
        date: selectedDate.value,
      });
};
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
  overflow: hidden;
  background: #f8fafc;
}

/* ══ SIDEBAR ══ */
.sidebar-phim {
  width: 460px;
  min-width: 460px;
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
  font-size: 13px;
  font-weight: 800;
  color: #1e293b;
}
.sidebar-search :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
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
  padding: 8px 8px;
  font-size: 10px;
  font-weight: 700;
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
  width: 90px;
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
  border-left: 3px solid #e31e24;
}
.phim-row td {
  padding: 7px 8px;
  vertical-align: middle;
}
.phim-row.selected td:first-child {
  padding-left: 5px;
}
.td-stt {
  text-align: center;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
}
.td-poster {
  width: 50px;
}
.phim-thumb {
  width: 36px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
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
.td-name {
  max-width: 0;
}
.phim-name-text {
  font-size: 12px;
  font-weight: 700;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px;
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
.td-status {
  text-align: center;
}
.status-badge {
  display: inline-block;
  font-size: 10px;
  font-weight: 700;
  padding: 3px 7px;
  border-radius: 20px;
  white-space: nowrap;
}
.badge-showing {
  background: #dcfce7;
  color: #16a34a;
}
.badge-upcoming {
  background: #fef2f2;
  color: #e31e24;
}
.badge-stopped {
  background: #f1f5f9;
  color: #64748b;
}
.empty-row {
  text-align: center;
  padding: 24px;
  font-size: 12px;
  color: #94a3b8;
}
.sidebar-footer {
  padding: 8px 14px;
  font-size: 11px;
  color: #94a3b8;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
  text-align: center;
  flex-shrink: 0;
}
.ms-auto {
  margin-left: auto;
}

/* ══ MAIN ══ */
.main-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Date bar */
.date-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
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
  padding: 7px 0;
  border-radius: 10px;
  border: 1.5px solid #e2e8f0;
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
  font-size: 15px;
  font-weight: 800;
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
.nav-btn {
  width: 34px !important;
  height: 34px !important;
  border: 1px solid #e2e8f0 !important;
  flex-shrink: 0;
}
.date-picker-icon {
  width: 34px !important;
  flex-shrink: 0;
}
.date-picker-icon :deep(.el-input__wrapper) {
  padding: 0 !important;
  width: 34px !important;
  height: 34px !important;
  border-radius: 50% !important;
  border: 1px solid #e2e8f0 !important;
  box-shadow: none !important;
  display: flex;
  align-items: center;
  justify-content: center;
}
.date-picker-icon :deep(.el-input__inner) {
  display: none !important;
}
.date-picker-icon :deep(.el-input__suffix) {
  display: none !important;
}

/* ══ Realtime stats row ══ */
.stats-realtime-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  background: #fff8f8;
  border-bottom: 1px solid #fee2e2;
  flex-shrink: 0;
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
  white-space: nowrap;
  flex-shrink: 0;
  background: #fef2f2;
  padding: 4px 10px;
  border-radius: 20px;
  border: 1px solid #fca5a5;
}
.stats-realtime-slots {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: nowrap;
}

.realtime-slot-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 10px;
  white-space: nowrap;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
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
.chip-time {
  font-size: 12px;
  font-weight: 800;
}
.chip-room {
  opacity: 0.8;
}
.chip-seats {
  background: rgba(0, 0, 0, 0.06);
  padding: 1px 6px;
  border-radius: 10px;
}
.realtime-empty {
  font-size: 11px;
  color: #94a3b8;
  font-style: italic;
}
.custom-scrollbar-x::-webkit-scrollbar {
  height: 4px;
}
.custom-scrollbar-x::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar-x::-webkit-scrollbar-thumb {
  background: #fca5a5;
  border-radius: 10px;
}

/* Legend */
.legend-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 7px 14px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}
.lg-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  color: #475569;
  font-weight: 500;
}
.lg-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
  flex-shrink: 0;
}
.dot-upcoming {
  background: #e31e24;
}
.dot-showing {
  background: #16a34a;
}
.dot-ended {
  background: #94a3b8;
}
.dot-cancelled {
  background: #fca5a5;
}
.dot-empty {
  background: #f1f5f9;
  border: 1px dashed #cbd5e1;
}

/* Showtime area */
.showtime-area {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

/* Selected phim bar */
.selected-phim-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 10px 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
}
.sel-poster {
  width: 32px;
  height: 44px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}
.sel-poster-ph {
  width: 32px;
  height: 44px;
  background: #f1f5f9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  flex-shrink: 0;
}
.sel-name {
  font-size: 14px;
  font-weight: 800;
  color: #1e293b;
}
.sel-meta {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 2px;
}

.no-kg-hint {
  padding: 14px;
  background: #fffbeb;
  border: 1px dashed #fbbf24;
  border-radius: 8px;
  font-size: 12px;
  color: #92400e;
}
.no-kg-hint code {
  background: #fef3c7;
  padding: 1px 5px;
  border-radius: 4px;
}

/* ══ ROOM GROUP ══ */
.room-group {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.room-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #f1f5f9;
  font-size: 13px;
  font-weight: 800;
  color: #334155;
}
.room-name {
  font-size: 13px;
  font-weight: 800;
  color: #1e293b;
}
.room-tag {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 5px;
  background: #f1f5f9;
  color: #64748b;
}
.tag-screen {
  background: #ede9fe;
  color: #7c3aed;
}
.tag-count {
  background: #fef2f2;
  color: #e31e24;
}

/* ══ SLOTS GRID — Momo style ══ */
.slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 10px;
  padding: 14px 16px;
}

.slot-card {
  position: relative;
  overflow: hidden;
  background: #fff;
  border: 1.5px solid #e8ecf0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.slot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
}

/* Stripe trái */
.slot-stripe {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}
.slot-upcoming .slot-stripe {
  background: #e31e24;
}
.slot-showing .slot-stripe {
  background: #16a34a;
}
.slot-ended .slot-stripe {
  background: #cbd5e1;
}
.slot-cancelled .slot-stripe {
  background: #fca5a5;
}
.slot-empty .slot-stripe {
  background: #e2e8f0;
}

/* Màu nền + border */
.slot-upcoming {
  border-color: #fca5a5;
  background: #fff8f8;
}
.slot-showing {
  border-color: #86efac;
  background: #f0fdf4;
}
.slot-ended {
  border-color: #e2e8f0;
  opacity: 0.65;
}
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
.slot-empty:hover {
  transform: none;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* Nội dung */
.slot-body {
  padding: 12px 12px 12px 16px;
}
.slot-time-row {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 2px;
}
.slot-time-big {
  font-size: 21px;
  font-weight: 900;
  color: #1e293b;
  line-height: 1;
}
.slot-time-small {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 600;
}
.slot-kg-name {
  font-size: 10px;
  font-weight: 700;
  color: #94a3b8;
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

/* Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}

.animation-fade-in {
  animation: fadeUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
