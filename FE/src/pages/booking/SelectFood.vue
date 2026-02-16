<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { Minus, Plus, ShoppingCart, Search } from '@element-plus/icons-vue';
import { useBookingStore } from '@/stores/booking';
import { mockFood } from '@/mock';
import { formatCurrency } from '@/utils/formatters';

const router = useRouter();
const bookingStore = useBookingStore();

// ── State ──
const activeCategory = ref('All');
const searchQuery = ref('');
const items = ref(mockFood);

// ── Filter logic ──
const filteredItems = computed(() => {
  let result = items.value;
  if (activeCategory.value !== 'All') {
    result = result.filter(i => i.category === activeCategory.value);
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(i => i.name.toLowerCase().includes(q) || i.description.toLowerCase().includes(q));
  }
  return result;
});

// Featured combo
const featuredItem = computed(() =>
  items.value.find(i => i.featured) || items.value.find(i => i.category === 'Combo') || items.value[0]
);

// Regular items
const gridItems = computed(() =>
  filteredItems.value.filter(i => i.id !== featuredItem.value?.id)
);

// ── Stats ──
const totalProducts = computed(() => items.value.length);
const totalCombos = computed(() => items.value.filter(i => i.category === 'Combo').length);

// ── Cart helpers ──
const getQuantity = (id) => {
  const item = bookingStore.selectedFood.find(f => f.id === id);
  return item ? item.quantity : 0;
};

const cartItemCount = computed(() =>
  bookingStore.selectedFood.reduce((sum, f) => sum + f.quantity, 0)
);

// ── Navigation ──
const goToNextStep = () => router.push({ name: 'Payment' });
const goToPrevStep = () => router.push({ name: 'SelectSeats', params: { showtimeId: bookingStore.showtimeId } });

// ── Category helpers ──
const getCategoryLabel = (cat) => {
  const map = { 'Popcorn': '🍿 Bắp', 'Drink': '🥤 Nước', 'Combo': '🎁 Combo' };
  return map[cat] || cat;
};

const getStockBadge = (stock) => {
  if (stock === 0) return { text: 'Hết hàng', class: 'stock-badge--out' };
  if (stock < 30) return { text: `Còn ${stock}`, class: 'stock-badge--low' };
  return { text: 'Còn hàng', class: 'stock-badge--ok' };
};

// ── Auto-Pulse Logic ──
const activeGlowIndex = ref(-1);
let pulseInterval = null;

function startAutoPulse() {
  pulseInterval = setInterval(() => {
    const totalCards = gridItems.value.length + 1;
    const idx = Math.floor(Math.random() * Math.min(totalCards, 8));
    activeGlowIndex.value = idx;
    setTimeout(() => { activeGlowIndex.value = -1; }, 1500);
  }, 4000);
}

function stopAutoPulse() {
  if (pulseInterval) { clearInterval(pulseInterval); pulseInterval = null; }
}

onMounted(() => { startAutoPulse(); });
onUnmounted(() => { stopAutoPulse(); });
</script>

<template>
  <div class="food-page d-flex flex-column">
    <!-- ─── Top Bar: Header + Filter (fixed height) ─── -->
    <div class="food-topbar flex-shrink-0">
      <div class="container-xl">
        <!-- Compact header -->
        <div class="d-flex align-items-center justify-content-between py-3">
          <div>
            <h1 class="fs-4 fw-bold text-white mb-0">
              Bắp & Nước <span class="text-gradient-food">Giải Khát</span>
            </h1>
            <p class="text-secondary x-small mb-0">Chọn combo yêu thích để trải nghiệm xem phim trọn vẹn</p>
          </div>
          <!-- Cart badge (mobile) -->
          <div class="d-lg-none bento-cart-badge" v-if="cartItemCount > 0">
            <el-icon size="20" color="white">
              <ShoppingCart />
            </el-icon>
            <span class="badge-count">{{ cartItemCount }}</span>
          </div>
        </div>

        <!-- Filter bar -->
        <div class="bento-filter d-flex flex-wrap justify-content-between align-items-center gap-2 pb-3">
          <div class="d-flex gap-2 flex-wrap">
            <button class="bento-filter-btn" :class="{ active: activeCategory === 'All' }"
              @click="activeCategory = 'All'">Tất cả</button>
            <button class="bento-filter-btn" :class="{ active: activeCategory === 'Combo' }"
              @click="activeCategory = 'Combo'">🎁 Combo</button>
            <button class="bento-filter-btn" :class="{ active: activeCategory === 'Popcorn' }"
              @click="activeCategory = 'Popcorn'">🍿 Bắp</button>
            <button class="bento-filter-btn" :class="{ active: activeCategory === 'Drink' }"
              @click="activeCategory = 'Drink'">🥤 Nước</button>
          </div>
          <div class="bento-search">
            <el-icon class="search-icon">
              <Search />
            </el-icon>
            <input v-model="searchQuery" type="text" placeholder="Tìm kiếm..." class="bento-search-input">
          </div>
        </div>
      </div>
    </div>

    <!-- ─── Main Content: Scrollable Grid + Fixed Summary ─── -->
    <div class="food-body flex-grow-1 overflow-hidden">
      <div class="container-xl h-100">
        <div class="row g-3 h-100">
          <!-- Left: Scrollable Food Grid -->
          <div class="col-lg-8 h-100">
            <div class="food-scroll-area">
              <!-- Featured + Stats row -->
              <div class="row g-3 mb-3">
                <div class="col-md-8" :class="{ 'auto-glow': activeGlowIndex === 0 }">
                  <div
                    class="bento-card bento-card--featured h-100 position-relative overflow-hidden d-flex flex-column justify-content-end p-4">
                    <img :src="featuredItem.image" :alt="featuredItem.name" class="bento-featured-bg">
                    <div class="bento-featured-overlay"></div>
                    <div class="position-relative z-2">
                      <span class="bento-badge bento-badge--hot mb-2 d-inline-block">⭐ Best Seller</span>
                      <h2 class="fw-bold text-white mb-1 fs-5">{{ featuredItem.name }}</h2>
                      <p class="text-white-50 x-small mb-2 lh-base" style="max-width: 360px;">{{
                        featuredItem.description }}</p>
                      <div class="d-flex align-items-center gap-3 flex-wrap">
                        <span class="bento-price-tag fs-5">{{ formatCurrency(featuredItem.price) }}</span>
                        <div class="d-flex align-items-center gap-2">
                          <button class="bento-qty-btn bento-qty-btn--sm" :disabled="getQuantity(featuredItem.id) === 0"
                            @click="bookingStore.removeFood(featuredItem.id)">−</button>
                          <span class="fw-bold text-white px-1">{{ getQuantity(featuredItem.id) }}</span>
                          <button class="bento-qty-btn bento-qty-btn--add bento-qty-btn--sm"
                            @click="bookingStore.addFood(featuredItem)">+</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="col-md-4">
                  <div class="row g-3 h-100">
                    <div class="col-6 col-md-12">
                      <div
                        class="bento-card bento-card--stat h-100 d-flex flex-column align-items-center justify-content-center text-center p-3">
                        <span class="stat-emoji">🎬</span>
                        <h3 class="fw-bold text-gradient-food fs-2 mb-0">{{ totalProducts }}</h3>
                        <p class="text-secondary x-small mb-0">Sản phẩm</p>
                      </div>
                    </div>
                    <div class="col-6 col-md-12">
                      <div
                        class="bento-card bento-card--stat h-100 d-flex flex-column align-items-center justify-content-center text-center p-3">
                        <span class="stat-emoji">🎁</span>
                        <h3 class="fw-bold text-gradient-food fs-2 mb-0">{{ totalCombos }}</h3>
                        <p class="text-secondary x-small mb-0">Combo</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Daily Special wide card -->
              <div class="row g-3 mb-3">
                <div class="col-12" :class="{ 'auto-glow': activeGlowIndex === 1 }">
                  <div class="bento-card bento-card--daily p-3 d-flex align-items-center gap-3">
                    <div class="daily-icon-wrap flex-shrink-0">
                      <span class="daily-icon">🔥</span>
                    </div>
                    <div class="flex-grow-1">
                      <span class="bento-badge bento-badge--trending d-inline-block mb-1">Ưu đãi hôm nay</span>
                      <h5 class="fw-bold text-white mb-0 fs-6">Mua 2 Combo bất kỳ — Giảm thêm 15%</h5>
                      <p class="text-secondary x-small mb-0">Ưu đãi có giới hạn, nhanh tay đặt ngay!</p>
                    </div>
                    <div class="d-none d-md-block">
                      <div class="daily-countdown text-center">
                        <div class="daily-countdown__time text-white fw-bold fs-5">12:00</div>
                        <div class="text-secondary x-small">Còn lại</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Product cards grid -->
              <div class="row g-3">
                <div class="col-lg-4 col-md-6 col-6" v-for="(item, i) in gridItems" :key="item.id"
                  :class="{ 'auto-glow': activeGlowIndex === i + 2 }">
                  <div class="bento-card bento-card--product h-100 d-flex flex-column overflow-hidden">
                    <div class="bento-product-img-wrap position-relative">
                      <img :src="item.image" :alt="item.name" class="bento-product-img">
                      <span class="bento-category-badge">{{ getCategoryLabel(item.category) }}</span>
                      <span class="bento-stock-badge" :class="getStockBadge(item.stock).class">{{
                        getStockBadge(item.stock).text }}</span>
                    </div>
                    <div class="p-2 p-md-3 d-flex flex-column flex-grow-1">
                      <h6 class="fw-bold text-white mb-1 product-title">{{ item.name }}</h6>
                      <p class="text-secondary x-small mb-2 line-clamp-2 flex-grow-1 d-none d-md-block">{{
                        item.description }}</p>
                      <div class="d-flex justify-content-between align-items-center mt-auto">
                        <span class="bento-price-tag small">{{ formatCurrency(item.price) }}</span>
                        <div class="d-flex align-items-center gap-1">
                          <button class="bento-qty-btn bento-qty-btn--sm" :disabled="getQuantity(item.id) === 0"
                            @click="bookingStore.removeFood(item.id)">−</button>
                          <span class="fw-bold text-white px-1 small">{{ getQuantity(item.id) }}</span>
                          <button class="bento-qty-btn bento-qty-btn--add bento-qty-btn--sm"
                            @click="bookingStore.addFood(item)">+</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <el-empty v-if="gridItems.length === 0 && !featuredItem" description="Không tìm thấy sản phẩm nào"
                class="text-white mt-4" />

              <!-- Bottom spacer for scroll -->
              <div style="height: 24px;"></div>
            </div>
          </div>

          <!-- Right: Order Summary (fills height, no scroll on page) -->
          <div class="col-lg-4 h-100 d-none d-lg-block">
            <div class="bento-card bento-card--summary p-0 h-100 d-flex flex-column">
              <!-- Summary header -->
              <div class="summary-header p-3 d-flex align-items-center gap-3 flex-shrink-0">
                <div class="summary-icon">
                  <el-icon size="20" color="white">
                    <ShoppingCart />
                  </el-icon>
                </div>
                <div>
                  <h5 class="fw-bold text-white mb-0 fs-6">Đơn hàng của bạn</h5>
                  <span class="text-secondary x-small">{{ cartItemCount }} sản phẩm đã chọn</span>
                </div>
              </div>

              <!-- Scrollable order items -->
              <div class="summary-body flex-grow-1 overflow-auto p-3">
                <!-- Seats -->
                <div class="mb-3">
                  <div class="d-flex justify-content-between mb-1">
                    <span class="text-secondary x-small">Ghế ({{ bookingStore.seatsCount }})</span>
                    <span class="fw-semibold text-white x-small">{{ formatCurrency(bookingStore.seatsTotal) }}</span>
                  </div>
                  <div v-for="seat in bookingStore.selectedSeats" :key="seat.id"
                    class="x-small text-secondary ps-2 mb-1">
                    Ghế {{ seat.id }} ({{ seat.type?.toUpperCase() }})
                  </div>
                </div>

                <!-- Food items -->
                <div v-if="bookingStore.selectedFood.length > 0" class="mb-3 border-top pt-3"
                  style="border-color: rgba(255,255,255,0.08) !important;">
                  <div class="d-flex justify-content-between mb-1">
                    <span class="text-secondary x-small">Bắp nước</span>
                    <span class="fw-semibold text-white x-small">{{ formatCurrency(bookingStore.foodTotal) }}</span>
                  </div>
                  <div v-for="food in bookingStore.selectedFood" :key="food.id"
                    class="x-small text-secondary ps-2 mb-1 d-flex justify-content-between">
                    <span>{{ food.name }} x{{ food.quantity }}</span>
                    <span>{{ formatCurrency(food.price * food.quantity) }}</span>
                  </div>
                </div>

                <div v-else class="text-center py-3">
                  <span class="text-secondary x-small">Chưa chọn bắp nước</span>
                </div>
              </div>

              <!-- Fixed bottom: Total + buttons -->
              <div class="summary-footer flex-shrink-0 p-3">
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <span class="fw-bold text-white">Tổng cộng</span>
                  <span class="fs-5 fw-bold text-gradient-food">{{ formatCurrency(bookingStore.finalTotal) }}</span>
                </div>
                <div class="d-grid gap-2">
                  <button class="bento-btn-checkout" @click="goToNextStep">Tiến hành thanh toán</button>
                  <button class="bento-btn-back" @click="goToPrevStep">← Quay lại chọn ghế</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ─── Mobile bottom bar ─── -->
    <div class="food-mobile-bar d-lg-none flex-shrink-0">
      <div class="container-xl d-flex align-items-center justify-content-between gap-3 py-2">
        <div>
          <div class="x-small text-secondary">Tổng cộng</div>
          <div class="fw-bold text-gradient-food fs-5">{{ formatCurrency(bookingStore.finalTotal) }}</div>
        </div>
        <div class="d-flex gap-2">
          <button class="bento-btn-back px-3 py-2" @click="goToPrevStep">←</button>
          <button class="bento-btn-checkout px-4 py-2" @click="goToNextStep">Thanh toán ({{ cartItemCount }})</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ── Page fills viewport exactly ── */
.food-page {
  height: 100vh;
  background: #121212;
  overflow: hidden;
}

/* ── Top bar (fixed) ── */
.food-topbar {
  background: #121212;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  z-index: 10;
}

/* ── Main body fills remaining space ── */
.food-body {
  min-height: 0;
  /* crucial for flex child overflow */
}

/* ── Scrollable food grid ── */
.food-scroll-area {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 4px 12px 0;
  scrollbar-width: thin;
  scrollbar-color: rgba(243, 112, 33, 0.3) transparent;
}

.food-scroll-area::-webkit-scrollbar {
  width: 6px;
}

.food-scroll-area::-webkit-scrollbar-track {
  background: transparent;
}

.food-scroll-area::-webkit-scrollbar-thumb {
  background: rgba(243, 112, 33, 0.3);
  border-radius: 3px;
}

.food-scroll-area::-webkit-scrollbar-thumb:hover {
  background: rgba(243, 112, 33, 0.5);
}

/* ── Mobile bottom bar ── */
.food-mobile-bar {
  background: #1a1a1a;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  z-index: 10;
}

.bento-cart-badge {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #1E1E1E;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-count {
  position: absolute;
  top: -4px;
  right: -4px;
  background: linear-gradient(90deg, #E31E24, #F37021);
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ── Gradient text ── */
.text-gradient-food {
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ── Filter bar ── */
.bento-filter-btn {
  padding: 6px 16px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: #1E1E1E;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 600;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.bento-filter-btn:hover {
  border-color: rgba(243, 112, 33, 0.4);
  color: white;
}

.bento-filter-btn.active {
  background: linear-gradient(90deg, #A51C1C, #E31E24, #F37021, #FFBA00);
  border-color: transparent;
  color: white;
}

.bento-search {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 10px;
  color: rgba(255, 255, 255, 0.4);
  z-index: 1;
}

.bento-search-input {
  padding: 6px 12px 6px 32px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: #1E1E1E;
  color: white;
  font-size: 0.8rem;
  width: 170px;
  outline: none;
  transition: all 0.3s ease;
}

.bento-search-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.bento-search-input:focus {
  border-color: rgba(243, 112, 33, 0.5);
  width: 200px;
}

/* ── Base Bento Card ── */
.bento-card {
  background: #1E1E1E;
  border-radius: 16px;
  border: 2px solid transparent;
  position: relative;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bento-card::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 18px;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  opacity: 0;
  z-index: -1;
  transition: opacity 0.4s ease;
}

.bento-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 0 30px rgba(243, 112, 33, 0.35), 0 16px 40px rgba(0, 0, 0, 0.4);
}

.bento-card:hover::before {
  opacity: 1;
}

/* ── Auto-pulse ── */
.auto-glow .bento-card {
  animation: foodGlow 1.5s ease-in-out;
}

@keyframes foodGlow {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 rgba(243, 112, 33, 0);
  }

  50% {
    transform: scale(1.02);
    box-shadow: 0 0 25px rgba(243, 112, 33, 0.4);
  }

  100% {
    transform: scale(1);
    box-shadow: 0 0 0 rgba(243, 112, 33, 0);
  }
}

/* ── Featured Card ── */
.bento-card--featured {
  min-height: 220px;
}

.bento-featured-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
  transition: transform 0.6s ease;
}

.bento-card--featured:hover .bento-featured-bg {
  transform: scale(1.05);
}

.bento-featured-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(18, 18, 18, 0.98) 0%, rgba(18, 18, 18, 0.5) 50%, rgba(165, 28, 28, 0.15) 100%);
  z-index: 1;
}

/* ── Stat cards ── */
.bento-card--stat {
  overflow: hidden;
}

.stat-emoji {
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
  display: block;
}

/* ── Daily Special ── */
.bento-card--daily {
  background: linear-gradient(135deg, #1E1E1E 0%, #2a1a1a 100%);
}

.daily-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #F37021, #FFBA00);
  display: flex;
  align-items: center;
  justify-content: center;
}

.daily-icon {
  font-size: 1.2rem;
}

.daily-countdown {
  background: rgba(243, 112, 33, 0.15);
  border: 2px solid rgba(243, 112, 33, 0.3);
  border-radius: 12px;
  padding: 8px 16px;
}

/* ── Product cards ── */
.bento-product-img-wrap {
  height: 120px;
  overflow: hidden;
  border-radius: 14px 14px 0 0;
}

.bento-product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.bento-card--product:hover .bento-product-img {
  transform: scale(1.08);
}

.product-title {
  font-size: 0.85rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bento-category-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 2px 8px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  color: white;
  font-size: 0.65rem;
  font-weight: 600;
}

.bento-stock-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 0.6rem;
  font-weight: 700;
}

.stock-badge--ok {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.stock-badge--low {
  background: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

.stock-badge--out {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

/* ── Badges ── */
.bento-badge {
  padding: 3px 10px;
  border-radius: 16px;
  font-size: 0.7rem;
  font-weight: 600;
}

.bento-badge--hot {
  background: linear-gradient(90deg, #A51C1C, #E31E24);
  color: white;
}

.bento-badge--trending {
  background: linear-gradient(90deg, #F37021, #FFBA00);
  color: #121212;
}

/* ── Price tag ── */
.bento-price-tag {
  font-weight: 800;
  background: linear-gradient(90deg, #F37021, #FFBA00);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ── Quantity buttons ── */
.bento-qty-btn {
  width: 30px;
  height: 30px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: transparent;
  color: white;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bento-qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.bento-qty-btn--add {
  background: linear-gradient(90deg, #F37021, #FFBA00);
  border-color: transparent;
  color: #121212;
}

.bento-qty-btn--add:hover {
  box-shadow: 0 0 12px rgba(243, 112, 33, 0.5);
  transform: scale(1.08);
}

.bento-qty-btn--sm {
  width: 26px;
  height: 26px;
  font-size: 0.85rem;
  border-radius: 7px;
}

/* ── Summary card ── */
.bento-card--summary {
  background: #1a1a1a;
}

.bento-card--summary:hover {
  transform: none;
  box-shadow: none;
}

.bento-card--summary:hover::before {
  opacity: 0;
}

.summary-header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.summary-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #A51C1C, #F37021);
  display: flex;
  align-items: center;
  justify-content: center;
}

.summary-body {
  scrollbar-width: thin;
  scrollbar-color: rgba(243, 112, 33, 0.2) transparent;
}

.summary-body::-webkit-scrollbar {
  width: 4px;
}

.summary-body::-webkit-scrollbar-track {
  background: transparent;
}

.summary-body::-webkit-scrollbar-thumb {
  background: rgba(243, 112, 33, 0.2);
  border-radius: 2px;
}

.summary-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

/* ── Action buttons ── */
.bento-btn-checkout {
  padding: 12px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 50%, #F37021 75%, #FFBA00 100%);
  color: white;
  font-size: 0.9rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(227, 30, 36, 0.3);
}

.bento-btn-checkout:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(227, 30, 36, 0.5);
}

.bento-btn-back {
  padding: 8px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: transparent;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.bento-btn-back:hover {
  border-color: rgba(243, 112, 33, 0.3);
  color: white;
}

/* ── Utilities ── */
.x-small {
  font-size: 0.78rem;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ── Responsive ── */
@media (max-width: 992px) {
  .bento-card--featured {
    min-height: 200px;
  }

  .bento-product-img-wrap {
    height: 100px;
  }
}

@media (max-width: 768px) {
  .bento-card--featured {
    min-height: 180px;
  }

  .bento-card--daily {
    flex-direction: column;
    text-align: center;
  }

  .daily-icon-wrap {
    margin: 0 auto;
  }

  .bento-product-img-wrap {
    height: 90px;
  }

  .product-title {
    font-size: 0.75rem;
  }
}

/* ── Reduced Motion ── */
@media (prefers-reduced-motion: reduce) {

  .bento-card,
  .bento-product-img,
  .bento-featured-bg {
    transition: none !important;
  }

  .auto-glow .bento-card {
    animation: none !important;
  }
}
</style>
