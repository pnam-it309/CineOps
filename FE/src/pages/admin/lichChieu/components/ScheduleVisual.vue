<template>
  <div class="visual-scheduler-container animation-fade-in h-100 flex-grow-1 w-100">
    <!-- Sidebar: chọn phim để gán -->
    <div class="scheduler-sidebar">
      <div class="sidebar-header">
        <h4 class="sidebar-title">Chọn Phim</h4>
        <el-input v-model="movieSearch" placeholder="Tìm phim nhanh..." :prefix-icon="Search"
          clearable class="modern-search" />
      </div>

      <div class="movie-list-scroll custom-scrollbar">
        <div v-for="p in filteredPhimList" :key="p.id" class="movie-card"
          :class="{ active: selectedPhimId === p.id }" @click="selectedPhimId = p.id">
          <div class="card-content">
            <div class="poster-box">
              <img v-if="p.poster && p.poster.trim()" :src="p.poster" class="img-fluid rounded-2" />
              <div v-else class="poster-placeholder"><i class="bi bi-film"></i></div>
            </div>
            <div class="movie-info">
              <h5 class="movie-name text-truncate">{{ p.tenPhim }}</h5>
              <div class="movie-meta">
                <span class="duration"><i class="bi bi-clock-history me-1"></i>{{ p.thoiLuong }}p</span>
                <span class="type-tag">{{ p.loaiPhim || '2D' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="sidebar-footer">
        <p class="guide-text"><i class="bi bi-lightbulb me-2"></i>Chọn phim &amp; nhấn vào lịch để xếp</p>
      </div>
    </div>

    <!-- Calendar Grid -->
    <div class="scheduler-content shadow-sm">
      <div class="grid-toolbar">
        <div class="week-nav">
          <el-button @click="moveWeek(-1)" :icon="ArrowLeft" circle class="nav-btn" />
          <h3 class="week-title">{{ currentWeekLabel }}</h3>
          <el-button @click="moveWeek(1)" :icon="ArrowRight" circle class="nav-btn" />
        </div>
      </div>

      <div class="calendar-wrapper custom-scrollbar">
        <div class="calendar-fixed-header">
          <div class="time-header-cell">Giờ</div>
          <div v-for="day in currentWeekDays" :key="day.date" class="day-header-cell"
            :class="{ is_today: isToday(day.date) }">
            <span class="day-name">{{ day.name }}</span>
            <span class="day-label">{{ day.label }}</span>
          </div>
        </div>

        <div class="grid-layout">
          <div v-for="hour in hours" :key="hour" class="hour-row"
            :class="{ 'zebra-row': hour % 2 === 0 }">
            <div class="time-label-cell"><span>{{ hour }}:00</span></div>
            <div v-for="day in currentWeekDays" :key="day.date" class="grid-cell"
              @click="handleGridClick(day.date, hour)">
              <div v-for="sc in getShowtimesInCell(day.date, hour)" :key="sc.id"
                class="st-block" :style="getShowtimeStyle(sc)"
                @click.stop="$emit('view', sc)">
                <div class="st-inner">
                  <span class="st-movie-title">{{ sc.tenPhim }}</span>
                  <span class="st-time-range">{{ sc.gioBatDau?.substring(0,5) }} - {{ sc.gioKetThuc?.substring(0,5) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Search, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import dayjs from 'dayjs';

const props = defineProps({
  showtimes: { type: Array, required: true },
  phongChieuList: { type: Array, required: true },
  phimList: { type: Array, required: true },
  visualRoomId: { type: [String, Number], default: '' }
});

const emit = defineEmits(['view', 'gridClick', 'weekChange']);

// State
const movieSearch = ref('');
const selectedPhimId = ref(null);
const currentWeekOffset = ref(0);
const hours = Array.from({ length: 17 }, (_, i) => i + 8); // 08:00 → 24:00

// Computed
const filteredPhimList = computed(() => {
  // 1. Tìm thông tin phòng đang chọn để lấy loaiManHinh
  const currentRoom = props.phongChieuList.find(r => r.id === props.visualRoomId);
  const roomType = currentRoom?.loaiManHinh || '';

  // 2. Lọc danh sách phim theo loại phòng (nếu có)
  let list = props.phimList;
  if (roomType) {
    list = list.filter(p => p.loaiPhim === roomType);
  }

  // 3. Lọc theo text search
  if (!movieSearch.value) return list;
  return list.filter(p =>
    p.tenPhim.toLowerCase().includes(movieSearch.value.toLowerCase())
  );
});

const currentWeekDays = computed(() => {
  const startOfWeek = dayjs().add(currentWeekOffset.value, 'week').startOf('week').add(1, 'day');
  const dayNamesVi = ['THỨ 2', 'THỨ 3', 'THỨ 4', 'THỨ 5', 'THỨ 6', 'THỨ 7', 'CHỦ NHẬT'];
  return Array.from({ length: 7 }, (_, i) => {
    const d = startOfWeek.add(i, 'day');
    return { 
      date: d.format('YYYY-MM-DD'), 
      label: d.format('DD/MM'), 
      name: dayNamesVi[i]
    };
  });
});

const currentWeekLabel = computed(() => {
  const start = currentWeekDays.value[0];
  const end   = currentWeekDays.value[6];
  return `${start.label} - ${end.label}, ${dayjs().add(currentWeekOffset.value, 'week').year()}`;
});

// Methods
const moveWeek = (offset) => { 
  currentWeekOffset.value += offset; 
  emit('weekChange', currentWeekOffset.value);
};

const isToday = (date) => date === dayjs().format('YYYY-MM-DD');

const getShowtimesInCell = (date, hour) =>
  props.showtimes.filter(sc =>
    sc.ngayChieu === date && parseInt(sc.gioBatDau.split(':')[0]) === hour
  );

const getShowtimeStyle = (sc) => {
  const [, m] = sc.gioBatDau.split(':').map(Number);
  const duration = sc.thoiLuong || 120;
  const rowHeight = 100;

  // Overlap logic
  const daySchedules = props.showtimes.filter(s => 
    dayjs(s.ngayChieu).format('YYYY-MM-DD') === dayjs(sc.ngayChieu).format('YYYY-MM-DD')
  ).sort((a,b) => a.gioBatDau.localeCompare(b.gioBatDau));

  const scStart = sc.gioBatDau;
  const scEndTime = dayjs(`2000-01-01 ${sc.gioBatDau}`).add(duration, 'minute').format('HH:mm');

  const overlaps = daySchedules.filter(s => {
    const sEnd = dayjs(`2000-01-01 ${s.gioBatDau}`).add(s.thoiLuong || 120, 'minute').format('HH:mm');
    return (s.gioBatDau < scEndTime && sEnd > scStart);
  });

  const overlapIndex = overlaps.findIndex(s => s.id === sc.id);
  const totalOverlaps = overlaps.length;

  const width = `calc((100% - 12px) / ${totalOverlaps})`;
  const left = `calc(6px + (${overlapIndex} * (100% - 12px) / ${totalOverlaps}))`;

  const isCancelled = sc.trangThai === 0;

  return {
    top: `${(m / 60) * rowHeight}px`,
    height: `${(duration / 60) * rowHeight - 2}px`,
    width: width,
    left: left,
    background: isCancelled ? '#fee2e2' : 'linear-gradient(135deg, #E31E24 0%, #A51C1C 100%)',
    color: isCancelled ? '#ef4444' : '#ffffff',
    zIndex: 10 + overlapIndex,
    borderLeft: isCancelled ? '4px solid #ef4444' : '4px solid #FFBA00',
    borderRight: totalOverlaps > 1 ? '1px solid rgba(255,255,255,0.2)' : 'none'
  };
};

const handleGridClick = (date, hour) => {
  emit('gridClick', { date, hour, selectedPhimId: selectedPhimId.value });
};
</script>

<style scoped>
/* Copying relevant styles from AdminSchedule.vue */
.visual-scheduler-container {
  display: flex; height: 100%; gap: 24px;
  background: transparent; overflow: hidden; padding: 0 5px;
}
.scheduler-sidebar {
  width: 380px; min-width: 380px; flex-shrink: 0;
  background: #fff; border-radius: 20px; display: flex;
  flex-direction: column; border: 1px solid #edf2f7;
  box-shadow: 0 4px 20px rgba(0,0,0,.03); overflow: hidden;
}
.sidebar-header { padding: 24px; border-bottom: 1px solid #f1f5f9; }
.sidebar-title  { font-size: 24px; font-weight: 800; color: #1a202c; margin-bottom: 16px; }
.modern-search :deep(.el-input__wrapper) {
  padding: 10px 16px; border-radius: 12px; background: #f8fafc;
  box-shadow: none !important; border: 1px solid #e2e8f0; font-size: 18px;
}
.movie-list-scroll { flex: 1; overflow-y: auto; display: flex; flex-direction: column; }
.movie-card {
  display: flex; align-items: center; padding: 10px 15px;
  cursor: pointer; transition: background-color .2s;
  border-bottom: 1px solid #f1f5f9; min-height: 90px;
}
.movie-card:hover { background-color: #f8fafc; }
.movie-card.active { background-color: #fef2f2; border-left: 4px solid #ef4444; }
.card-content { display: flex; gap: 12px; align-items: center; width: 100%; overflow: hidden; }
.poster-box {
  width: 50px; height: 70px; flex-shrink: 0;
  background: #f1f5f9; border-radius: 4px; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}
.poster-box img { width: 100%; height: 100%; object-fit: cover; }
.poster-placeholder {
  width: 100%; height: 100%; background: #f1f5f9;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; color: #94a3b8;
}
.movie-info { flex: 1; min-width: 0; overflow: hidden; }
.movie-name { font-size: 16px; font-weight: 700; color: #1e293b; margin-bottom: 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.movie-meta { display: flex; gap: 10px; align-items: center; }
.duration   { font-size: 16px; color: #64748b; font-weight: 600; }
.type-tag   { background: #f1f5f9; padding: 2px 8px; border-radius: 6px; font-size: 14px; font-weight: 800; color: #475569; }
.sidebar-footer { padding: 20px; background: #f8fafc; border-top: 1px solid #f1f5f9; }
.guide-text { font-size: 15px; color: #94a3b8; margin: 0; text-align: center; font-weight: 600; }

.scheduler-content {
  flex: 1; background: #fff; border-radius: 20px;
  display: flex; flex-direction: column;
  overflow: hidden; border: 1px solid #edf2f7;
}
.grid-toolbar { padding: 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: center; }
.week-nav    { display: flex; align-items: center; gap: 25px; }
.nav-btn     { width: 48px; height: 48px; font-size: 20px; border: 1px solid #e2e8f0 !important; }
.week-title  { font-size: 24px; font-weight: 800; color: #1e293b; min-width: 300px; text-align: center; }
.calendar-wrapper { flex: 1; display: flex; flex-direction: column; overflow-y: auto; overflow-x: auto; }
.calendar-fixed-header {
  display: grid; grid-template-columns: 80px repeat(7, 1fr);
  background: #fff; border-bottom: 1px solid #edf2f7;
  z-index: 200; position: sticky; top: 0; min-width: 750px;
}
.time-header-cell { 
  padding: 20px 0; 
  text-align: center; 
  font-size: 18px; 
  font-weight: 800; 
  color: #94a3b8;
  background: #fff;
  position: relative;
  z-index: 201;
}
.day-header-cell  { 
    display: flex; 
    flex-direction: column; 
    align-items: center; 
    padding: 16px 0; 
    gap: 4px; 
    position: relative;
    border-right: 1px solid #edf2f7;
}
.day-header-cell.is_today::after {
  content: ''; position: absolute; bottom: 0; left: 0; right: 0;
  height: 4px; background: #ef4444; border-radius: 4px 4px 0 0;
}
.day-name  { font-size: 13px; font-weight: 600; color: #64748b; letter-spacing: 0.5px; text-transform: uppercase; }
.day-label { font-size: 13px; font-weight: 500; color: #1e293b; }
.day-header-cell.is_today .day-label { color: #ef4444; }
.grid-layout { 
    display: flex; 
    flex-direction: column; 
    min-width: 750px;
    border-top: 2px solid #e2e8f0;
    border-left: 1px solid #edf2f7;
    position: relative;
    margin-top: 20px;
    background-image: 
        linear-gradient(#f1f5f9 1px, transparent 1px),
        linear-gradient(90deg, #f1f5f9 1px, transparent 1px);
    background-size: 100% 50px, calc((100% - 80px) / 7) 100%;
}
.hour-row {
  display: grid; grid-template-columns: 80px repeat(7, 1fr);
  height: 100px; 
  border-bottom: 1px solid #cbd5e1;
  position: relative; 
  box-sizing: border-box;
}
.hour-row::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 80px;
    right: 0;
    height: 1px;
    border-top: 1px dashed #e2e8f0;
    pointer-events: none;
}
.hour-row.zebra-row { background: rgba(248,250,252,.6); }
.time-label-cell { 
    position: relative;
    height: 100%;
    background: #fff;
    z-index: 100;
    border-right: 2px solid #e2e8f0;
    box-sizing: border-box;
}
.time-label-cell span {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    text-align: center;
    transform: translateY(-50%);
    font-size: 13px; 
    font-weight: 700; 
    color: #475569; 
    background: #fff;
    padding: 2px 0;
}
.time-label-cell::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 10px;
    height: 1px;
    background: #cbd5e1;
}
.grid-cell { 
    position: relative; 
    transition: background .2s; 
    border-right: 1px solid #cbd5e1;
    box-sizing: border-box;
}
.grid-cell:last-child { border-right: none; }
.grid-cell:hover { background: rgba(227, 30, 36, 0.03); }
.st-block {
  position: absolute; 
  border-radius: 8px; 
  padding: 8px 10px;
  overflow: hidden; 
  display: flex; 
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-sizing: border-box;
  box-shadow: 0 4px 12px rgba(0,0,0,.18);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
}
.st-block:hover { 
    transform: translateY(-2px) scale(1.03); 
    z-index: 50 !important; 
    box-shadow: 0 12px 24px rgba(165, 28, 28, 0.3) !important;
}
.st-inner { 
    display: flex; 
    flex-direction: column; 
    height: 100%; 
    justify-content: center; 
    pointer-events: none;
}
.st-movie-title {
  font-size: 14px; 
  font-weight: 800; 
  color: #fff; 
  line-height: 1.2; 
  margin-bottom: 3px;
  display: -webkit-box; 
  -webkit-line-clamp: 2; 
  -webkit-box-orient: vertical; 
  line-clamp: 2;
  overflow: hidden;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}
.st-time-range { 
    font-size: 11px; 
    font-weight: 700; 
    color: rgba(255,255,255,.9); 
    letter-spacing: 0.5px;
}

.animation-fade-in { animation: slideUp .6s cubic-bezier(.16,1,.3,1); }
@keyframes slideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform:translateY(0); } }
.custom-scrollbar::-webkit-scrollbar { width: 8px; height: 8px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #cbd5e1; }
</style>
