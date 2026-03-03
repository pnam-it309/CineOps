<template>
  <div class="pos-container p-4">
    <el-card class="steps-card mb-4" shadow="never">
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="Chọn Phim" />
        <el-step title="Suất Chiếu" />
        <el-step title="Chọn Ghế" />
        <el-step title="Đồ Ăn/Nước Uống" />
        <el-step title="Thanh Toán" />
      </el-steps>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="17">
        <el-card shadow="never" class="main-op-card">
          <div v-if="activeStep === 0" class="movie-selection">
            <h5 class="section-title">PHIM ĐANG CHIẾU</h5>
            <div class="movie-grid">
              <div v-for="movie in movies" :key="movie.id" 
                   class="movie-card" :class="{ 'selected': bookingData.idPhim === movie.id }"
                   @click="selectMovie(movie)">
                <div class="poster-wrapper">
                  <img :src="movie.poster" alt="poster">
                  <div class="hover-overlay"><el-icon><Check /></el-icon></div>
                </div>
                <p class="movie-name mt-2">{{ movie.tenPhim }}</p>
              </div>
            </div>
          </div>

          <div v-if="activeStep === 1" class="showtime-selection">
            <h5 class="section-title">CHỌN SUẤT CHIẾU: {{ selectedMovieName }}</h5>
            <div class="showtime-list d-flex flex-wrap gap-3">
              <el-button v-for="st in showtimes" :key="st.id" 
                         :type="bookingData.idSuatChieu === st.id ? 'danger' : 'info'" 
                         class="st-btn" @click="selectShowtime(st)">
                <div class="time">{{ st.gioBatDau }}</div>
                <div class="room">{{ st.tenPhong }}</div>
              </el-button>
            </div>
          </div>

          <div v-if="activeStep === 2" class="seat-selection">
            <div class="screen-bar mx-auto mb-5">MÀN HÌNH</div>
            <div class="seat-grid">
              <div v-for="n in 60" :key="n" 
                   class="seat" :class="getSeatClass(n)"
                   @click="onSelectSeat(n)">
                {{ n }}
              </div>
            </div>
          </div>

          <div v-if="activeStep === 3" class="food-selection">
            <h5 class="section-title">COMBO ƯU ĐÃI</h5>
            <el-row :gutter="20">
              <el-col :span="8" v-for="food in foods" :key="food.id" class="mb-3">
                <div class="food-item-card">
                  <img :src="food.image" class="food-img">
                  <div class="food-info p-2">
                    <p class="m-0 fw-bold">{{ food.ten }}</p>
                    <p class="text-danger small">{{ formatPrice(food.gia) }} đ</p>
                    <el-input-number v-model="food.quantity" :min="0" size="small" class="mt-2" />
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

      <el-col :span="7">
        <el-card shadow="never" class="summary-card">
          <h5 class="text-white border-bottom pb-3 mb-3">CHI TIẾT VÉ</h5>
          
          <div class="ticket-info">
            <div class="info-row"><span>Phim:</span> <strong>{{ selectedMovieName || 'Chưa chọn' }}</strong></div>
            <div class="info-row"><span>Suất:</span> <strong>{{ selectedTime || '--:--' }}</strong></div>
            <div class="info-row"><span>Ghế:</span> <strong class="text-danger">{{ selectedSeats.map(s => s.tenGhe).join(', ') || '...' }}</strong></div>
            
            <el-divider />
            
            <div class="food-summary" v-if="selectedFoods.length > 0">
              <p class="small text-muted">SẢN PHẨM ĐI KÈM</p>
              <div v-for="f in selectedFoods" :key="f.id" class="d-flex justify-content-between small mb-1">
                <span>{{ f.ten }} x{{ f.quantity }}</span>
                <span>{{ formatPrice(f.gia * f.quantity) }}đ</span>
              </div>
            </div>
          </div>

          <div class="total-price-section mt-4 p-3 bg-black rounded">
            <p class="m-0 small text-muted">TỔNG TIỀN THANH TOÁN</p>
            <h3 class="m-0 text-warning">{{ formatPrice(totalBill) }} đ</h3>
          </div>

          <div class="d-flex gap-2 mt-4">
            <el-button v-if="activeStep > 0" @click="activeStep--" class="w-100" plain>QUAY LẠI</el-button>
            <el-button v-if="activeStep < 4" type="danger" @click="nextStep" class="w-100" :disabled="!canNext">TIẾP TỤC</el-button>
            <el-button v-else type="success" @click="submitBooking" class="w-100" :loading="isSubmitting">TẠO VÉ</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { adVeService } from '@/services/api/admin/quanlyve/ad-ve-service.js';
import { Check, Plus, ShoppingCart } from '@element-plus/icons-vue';
import Swal from 'sweetalert2';

const activeStep = ref(0);
const isSubmitting = ref(false);

// Dữ liệu mô phỏng (Đức hãy gọi API thật từ Backend cho các mảng này)
const movies = ref([
  { id: 1, tenPhim: 'Dune: Hành Tinh Cát 2', poster: 'https://image.tmdb.org/t/p/w500/8b8R8l3899v9D0Xn9QDs799v9D0.jpg' },
  { id: 2, tenPhim: 'Kung Fu Panda 4', poster: 'https://image.tmdb.org/t/p/w500/kDp1vUBiRSToYvRSZ70rNvT3I0L.jpg' }
]);
const showtimes = ref([]);
const foods = ref([
  { id: 101, ten: 'Combo Bắp Nước', gia: 85000, quantity: 0, image: 'https://api.chieu.online/storage/images/combo-1.png' }
]);

const bookingData = reactive({ idPhim: null, idSuatChieu: null });
const selectedSeats = ref([]);
const selectedMovieName = ref('');
const selectedTime = ref('');

// Điều kiện để được nhấn nút "Tiếp tục"
const canNext = computed(() => {
  if (activeStep.value === 0) return bookingData.idPhim !== null;
  if (activeStep.value === 1) return bookingData.idSuatChieu !== null;
  if (activeStep.value === 2) return selectedSeats.value.length > 0;
  return true;
});

const selectedFoods = computed(() => foods.value.filter(f => f.quantity > 0));
const totalBill = computed(() => {
  const seatPrice = selectedSeats.value.length * 75000; // Giá vé giả định
  const foodPrice = selectedFoods.value.reduce((s, f) => s + (f.gia * f.quantity), 0);
  return seatPrice + foodPrice;
});

// LOGIC XỬ LÝ
const selectMovie = (movie) => {
  bookingData.idPhim = movie.id;
  selectedMovieName.value = movie.tenPhim;
  // Giả lập load showtime từ phim
  showtimes.value = [{ id: 501, gioBatDau: '14:30', tenPhong: 'Phòng 01' }, { id: 502, gioBatDau: '19:00', tenPhong: 'Phòng 03' }];
};

const selectShowtime = (st) => {
  bookingData.idSuatChieu = st.id;
  selectedTime.value = st.gioBatDau;
};

const onSelectSeat = (n) => {
  const seatId = 'GHE_ID_' + n;
  const idx = selectedSeats.value.findIndex(s => s.idGhe === seatId);
  if (idx === -1) selectedSeats.value.push({ idGhe: seatId, tenGhe: n });
  else selectedSeats.value.splice(idx, 1);
};

const getSeatClass = (n) => ({
  'active': selectedSeats.value.some(s => s.tenGhe === n),
  'occupied': n % 9 === 0
});

const nextStep = () => { if (canNext.value) activeStep.value++; };

const submitBooking = async () => {
  isSubmitting.value = true;
  try {
    const payload = {
      idSuatChieu: bookingData.idSuatChieu,
      danhSachGhe: selectedSeats.value.map(s => ({ idGhe: s.idGhe, idLoaiKhachHang: 'ID_KHACH_LON' })),
      sanPhamKem: selectedFoods.value.map(f => ({ idSanPham: f.id, soLuong: f.quantity }))
    };
    await adVeService.datVeMoi(payload);
    Swal.fire('Thành công!', 'Vé và sản phẩm đã được tạo.', 'success');
    // Reset form...
  } catch (e) {
    Swal.fire('Lỗi', 'Không thể tạo vé', 'error');
  } finally { isSubmitting.value = false; }
};

const formatPrice = (v) => new Intl.NumberFormat('vi-VN').format(v);
</script>

<style scoped>
.pos-container { background-color: #020617; min-height: 100vh; }
.steps-card { background: #0f172a; border: none; }
:deep(.el-step__title) { color: #64748b !important; font-size: 12px; }
:deep(.el-step__title.is-process) { color: #fff !important; font-weight: bold; }

.main-op-card { background: #0f172a; border: none; min-height: 65vh; border-radius: 16px; }
.section-title { color: #94a3b8; font-size: 14px; letter-spacing: 1px; margin-bottom: 25px; border-left: 4px solid #E31E24; padding-left: 10px; }

/* Movie Grid */
.movie-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 20px; }
.movie-card { cursor: pointer; transition: 0.3s; }
.poster-wrapper { position: relative; border-radius: 12px; overflow: hidden; border: 2px solid transparent; }
.poster-wrapper img { width: 100%; height: 210px; object-fit: cover; }
.movie-card.selected .poster-wrapper { border-color: #E31E24; }
.hover-overlay { position: absolute; inset: 0; background: rgba(227, 30, 36, 0.6); display: flex; align-items: center; justify-content: center; opacity: 0; font-size: 30px; color: white; }
.movie-card.selected .hover-overlay { opacity: 1; }
.movie-name { color: #cbd5e1; font-size: 13px; font-weight: bold; text-align: center; }

/* Seat selection */
.screen-bar { width: 70%; height: 5px; background: #334155; box-shadow: 0 0 15px rgba(255,255,255,0.2); border-radius: 5px; text-align: center; color: #475569; font-size: 10px; }
.seat-grid { display: grid; grid-template-columns: repeat(10, 1fr); gap: 10px; max-width: 500px; margin: 0 auto; }
.seat { width: 35px; height: 35px; background: #1e293b; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; color: #64748b; font-size: 11px; }
.seat.active { background: #E31E24; color: white; box-shadow: 0 0 10px rgba(227, 30, 36, 0.5); }
.seat.occupied { background: #020617; color: #334155; cursor: not-allowed; }

/* Summary */
.summary-card { background: #1e293b; border: none; border-radius: 16px; position: sticky; top: 20px; }
.info-row { display: flex; justify-content: space-between; margin-bottom: 12px; color: #94a3b8; font-size: 13px; }
.info-row strong { color: #f1f5f9; }
</style>