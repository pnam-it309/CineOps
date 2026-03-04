<template>
  <div class="visual-scheduler-container animation-fade-in h-100 flex-grow-1 w-100">
    <!-- Left Sidebar: Movie Selection -->
    <div class="scheduler-sidebar">
      <div class="sidebar-header">
        <h4 class="sidebar-title">Chọn Phim</h4>
        <el-input 
          v-model="movieSearch" 
          placeholder="Tìm phim nhanh..." 
          :prefix-icon="Search" 
          clearable 
          class="modern-search"
        />
      </div>
      
      <div class="movie-list-scroll custom-scrollbar">
        <div 
          v-for="p in filteredPhimList" 
          :key="p.id" 
          class="movie-card" 
          :class="{ active: selectedPhimId === p.id }"
          @click="selectedPhimId = p.id"
        >
          <div class="card-content">
            <div class="poster-box">
              <img v-if="p.poster && p.poster.trim() !== ''" :src="p.poster" class="img-fluid rounded-2" />
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
        <p class="guide-text"><i class="bi bi-lightbulb me-2"></i>Chọn phim & nhấn vào lịch để xếp</p>
      </div>
    </div>

    <!-- Main Section: Calendar Grid -->
    <div class="scheduler-content shadow-sm">
      <!-- Week Navigation Toolbar -->
      <div class="grid-toolbar">
        <div class="week-nav">
          <el-button @click="moveWeek(-1)" :icon="ArrowLeft" circle class="nav-btn" />
          <h3 class="week-title">{{ currentWeekLabel }}</h3>
          <el-button @click="moveWeek(1)" :icon="ArrowRight" circle class="nav-btn" />
        </div>
      </div>

      <!-- Calendar Wrapper -->
      <div class="calendar-wrapper custom-scrollbar" ref="gridWrapper">
        <div class="calendar-fixed-header">
          <div class="time-header-cell">Giờ</div>
          <div v-for="day in currentWeekDays" :key="day.date" class="day-header-cell" :class="{ is_today: isToday(day.date) }">
            <span class="day-name">{{ day.name }}</span>
            <span class="day-label">{{ day.label }}</span>
          </div>
        </div>

        <div class="grid-layout">
          <!-- Hour Rows -->
          <div v-for="hour in hours" :key="hour" class="hour-row" :class="{ 'zebra-row': hour % 2 === 0 }">
            <div class="time-label-cell">{{ hour }}:00</div>
            <!-- Day Cells -->
            <div v-for="day in currentWeekDays" :key="day.date" 
                 class="grid-cell" @click="handleGridClick(day.date, hour)">
              <!-- Showtime Blocks -->
              <div v-for="sc in getShowtimesInCell(day.date, hour)" :key="sc.id" 
                   class="st-block" :style="getShowtimeStyle(sc)"
                   @click.stop="$emit('view', sc)">
                <div class="st-inner">
                  <span class="st-movie-title">{{ sc.tenPhim }}</span>
                  <span class="st-time-range">{{ sc.gioBatDau.substring(0,5) }} - {{ sc.gioKetThuc.substring(0,5) }}</span>
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
  showtimes: Array,
  phimList: Array,
  phongChieuList: Array,
  visualRoomId: String
});

const emit = defineEmits(['grid-click', 'view', 'week-change']);

const movieSearch = ref('');
const selectedPhimId = ref(null);
const currentWeekOffset = ref(0);

const hours = Array.from({ length: 17 }, (_, i) => i + 8);

const filteredPhimList = computed(() => {
  if (!movieSearch.value) return props.phimList;
  return props.phimList.filter(p => p.tenPhim.toLowerCase().includes(movieSearch.value.toLowerCase()));
});

const currentWeekDays = computed(() => {
  const startOfWeek = dayjs().add(currentWeekOffset.value, 'week').startOf('week').add(1, 'day');
  return Array.from({ length: 7 }, (_, i) => {
    const d = startOfWeek.add(i, 'day');
    return {
      date: d.format('YYYY-MM-DD'),
      label: d.format('DD/MM'),
      name: d.format('ddd').toUpperCase()
    };
  });
});

const currentWeekLabel = computed(() => {
  const start = currentWeekDays.value[0];
  const end = currentWeekDays.value[6];
  return `${start.label} - ${end.label}, ${dayjs().add(currentWeekOffset.value, 'week').year()}`;
});

const isToday = (date) => date === dayjs().format('YYYY-MM-DD');

const moveWeek = (offset) => {
  currentWeekOffset.value += offset;
  emit('week-change');
};

const getShowtimesInCell = (date, hour) => {
  return props.showtimes.filter(sc => {
    return sc.ngayChieu === date && parseInt(sc.gioBatDau.split(':')[0]) === hour;
  });
};

const getShowtimeStyle = (sc) => {
  const [h, m] = sc.gioBatDau.split(':').map(Number);
  const duration = sc.thoiLuong || 120; // phút
  const rowHeight = 100; // PHẢI khớp với height của .hour-row trong CSS

  // Tính toán vị trí top dựa trên số phút lẻ (m)
  // (m / 60) * rowHeight sẽ cho ra vị trí tương đối trong ô đó
  return {
    top: `${(m / 60) * rowHeight + 2}px`, 
    height: `${(duration / 60) * rowHeight - 4}px`, // Trừ đi 4px để tạo khoảng cách nhỏ giữa các block
    width: 'calc(100% - 10px)',
    left: '5px',
    backgroundColor: sc.trangThai === 0 ? 'rgba(254, 226, 226, 0.95)' : 'rgba(239, 68, 68, 0.9)',
    color: sc.trangThai === 0 ? '#b91c1c' : '#ffffff',
    zIndex: 10
  };
};

const handleGridClick = (date, hour) => {
  emit('grid-click', { date, hour, selectedPhimId: selectedPhimId.value });
};
</script>

<style scoped>
/* Main Container */
.visual-scheduler-container {
  display: flex;
  height: 100%;
  min-height: calc(100vh - 180px); /* Fill remaining space safely */
  background: transparent;
  gap: 24px;
  overflow: hidden;
  padding: 0 5px 0 5px; /* Removed bottom padding */
}

/* Sidebar Styling */
.scheduler-sidebar {
  width: 380px;
  min-width: 380px;
  flex-shrink: 0;
  background: #ffffff;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  border: 1px solid #edf2f7;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
  overflow: hidden;
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
}

.sidebar-title {
  font-size: 24px;
  font-weight: 800;
  color: #1a202c;
  margin-bottom: 16px;
}

.modern-search :deep(.el-input__wrapper) {
  padding: 10px 16px;
  border-radius: 12px;
  background: #f8fafc;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
  font-size: 18px;
}

.movie-list-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.movie-card {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background: #ffffff;
  border-radius: 0;
  cursor: pointer;
  transition: background-color 0.2s;
  border: none;
  border-bottom: 1px solid #f1f5f9;
  margin: 0;
  box-shadow: none;
  overflow: hidden;
  min-height: 90px;
}

.movie-card:hover {
  background-color: #f8fafc;
  transform: none;
  box-shadow: none;
}

.movie-card.active {
  background-color: #fef2f2;
  border-left: 4px solid #ef4444;
  box-shadow: none;
}

.card-content {
  display: flex;
  gap: 12px;
  align-items: center;
  width: 100%;
  overflow: hidden;
}

.poster-box {
  width: 50px; 
  height: 70px;
  flex-shrink: 0; /* Giữ ảnh không bị co lại khi chữ quá dài */
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.poster-box img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* Đảm bảo ảnh lấp đầy khung */
}

.poster-placeholder {
  width: 100%;
  height: 100%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #94a3b8;
}

.movie-info {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.movie-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-meta {
  display: flex;
  gap: 10px;
  align-items: center;
}

.duration {
  font-size: 16px;
  color: #64748b;
  font-weight: 600;
}

.type-tag {
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 800;
  color: #475569;
}

.sidebar-footer {
  padding: 20px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  border-bottom-left-radius: 20px;
  border-bottom-right-radius: 20px;
}

.guide-text {
  font-size: 15px;
  color: #94a3b8;
  margin: 0;
  text-align: center;
  font-weight: 600;
}

/* Scheduler Grid Section */
.scheduler-content {
  flex: 1;
  background: #ffffff;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #edf2f7;
}

.grid-toolbar {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: center;
}

.week-nav {
  display: flex;
  align-items: center;
  gap: 25px;
}

.nav-btn {
  width: 48px;
  height: 48px;
  font-size: 20px;
  border: 1px solid #e2e8f0 !important;
}

.week-title {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  min-width: 320px;
  text-align: center;
}

.calendar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: auto;
  position: relative;
}

/* Dùng CSS Grid cho header và nội dung chính */
.calendar-fixed-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr); /* Thu hẹp cột Giờ */
  background: #ffffff;
  border-bottom: 1px solid #edf2f7;
  z-index: 100;
  position: sticky;
  top: 0;
  min-width: 900px; /* Force minimum width to avoid severe squishing */
}

.time-header-cell {
  padding: 20px 0;
  text-align: center;
  font-size: 18px;
  font-weight: 800;
  color: #94a3b8;
  border-right: none;
}

.day-header-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
  gap: 4px;
}

.day-header-cell.is_today {
  position: relative;
}

.day-header-cell.is_today::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  right: 20%;
  height: 4px;
  background: #ef4444;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

.day-name {
  font-size: 15px;
  font-weight: 800;
  color: #64748b; /* Đậm hơn một chút */
  letter-spacing: 1px;
}

.day-label {
  font-size: 24px; /* To hẳn lên */
  font-weight: 900;
  color: #1e293b;
}

.day-header-cell.is_today .day-label {
  color: #ef4444;
}

.grid-layout {
  display: flex;
  flex-direction: column;
  min-width: 900px; /* Tương ứng header */
}

/* Áp dụng tiếp CSS Grid cho mỗi dòng chứa giờ (tương tự header) */
.hour-row {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  height: 100px; /* Chọn chiều cao cố định mỗi hàng giờ */
  border-bottom: 1px solid #f1f5f9;
  position: relative;
  box-sizing: border-box;
}

.hour-row.zebra-row {
  background: rgba(248, 250, 252, 0.5); /* Zebra coloring */
}

.time-label-cell {
  font-size: 14px;
  font-weight: 700;
  color: #94a3b8;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 10px;
  border-right: none; /* No vertical line */
  background: transparent;
}

.grid-cell {
  position: relative;
  transition: background 0.2s;
  /* Bỏ viền dọc tạo không gian mở */
  border-left: none;
}

.grid-cell:hover {
  background: rgba(239, 68, 68, 0.02);
}

/* --- Tinh chỉnh Showtime Blocks --- */
.st-block {
  position: absolute;
  border-radius: 4px; /* Bo góc nhẹ để chuyên nghiệp hơn */
  padding: 6px 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.2s ease-in-out;
  border: none;
  /* Dùng dải màu mờ để thấy được lưới bên dưới */
  background-color: rgba(239, 68, 68, 0.85) !important; 
  border-left: 3px solid #b91c1c !important;
  box-sizing: border-box; /* BẮT BUỘC: Giữ block không bị tràn layout */
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.st-block:hover {
  transform: scale(1.02);
  z-index: 20 !important;
  box-shadow: 0 10px 30px rgba(0,0,0,0.18);
}

.st-inner {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: flex-start;
}

.st-movie-title {
  font-size: 13px; /* Thu nhỏ để tránh bị cắt chữ quá nhiều */
  font-weight: 800;
  color: #fff;
  line-height: 1.2;
  margin-bottom: 2px;
  text-transform: none;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.st-time-range {
  font-size: 11px;
  font-weight: 600;
  color: rgba(255,255,255,0.9);
}

/* Animations & Scrollbars */
.animation-fade-in {
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>
