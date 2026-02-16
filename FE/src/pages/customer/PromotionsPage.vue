<template>
  <div class="promo-page">
    <div class="container-xl py-4">
      <!-- ─── Header ─── -->
      <div class="text-center mb-4">
        <h1 class="display-5 fw-bold text-white mb-1">
          CineOps <span class="text-gradient-promo">Promotions</span>
        </h1>
        <p class="text-secondary mb-0">Săn Deal cực hời — Xem phim cực đã!</p>
      </div>

      <!-- ─── Bento Grid ─── -->
      <div class="promo-bento-grid">

        <!-- ① Hero Banner (3 cols x 2 rows) -->
        <div class="bento-hero" :class="{ 'auto-wiggle': wiggleIndex === -1 }">
          <div class="bento-card bento-card--hero h-100 position-relative overflow-hidden cursor-pointer"
               @click="copyCode(heroPromo.code)">
            <img :src="heroPromo.image" :alt="heroPromo.title" class="hero-bg-img">
            <div class="hero-overlay"></div>
            <div class="hero-overlay-vignette"></div>

            <div class="position-relative z-3 p-4 h-100 d-flex flex-column justify-content-end">
              <span class="promo-badge promo-badge--hot mb-2 d-inline-block align-self-start">🔥 HOT DEAL</span>
              <h2 class="display-6 fw-bold text-white mb-2 hero-title">{{ heroPromo.title }}</h2>
              <p class="text-white-50 mb-3 lh-base hero-desc">{{ heroPromo.description }}</p>
              <div class="d-flex align-items-center gap-3 flex-wrap">
                <div class="code-chip">
                  <span class="code-chip__icon">🎟️</span>
                  <span class="code-chip__text">{{ heroPromo.code }}</span>
                </div>
                <button class="btn-copy-hero" @click.stop="copyCode(heroPromo.code)">
                  📋 Sao chép
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- ② Date / Deals Count Anchor Card (1 col x 2 rows) -->
        <div class="bento-date">
          <div class="bento-card bento-card--date h-100 d-flex flex-column align-items-center justify-content-center text-center p-3">
            <div class="date-day">{{ currentDay }}</div>
            <div class="date-month">{{ currentMonth }}</div>
            <div class="date-divider"></div>
            <div class="date-label">Hôm nay có</div>
            <div class="date-count">{{ promotions.length }}</div>
            <div class="date-label">ưu đãi</div>
          </div>
        </div>

        <!-- ③ Stat Tile: New Members -->
        <div class="bento-stat">
          <div class="bento-card bento-card--stat h-100 d-flex flex-column align-items-center justify-content-center text-center p-3">
            <div class="stat-icon-wrap mb-2">
              <span class="stat-icon">👥</span>
            </div>
            <h3 class="stat-value fw-bold mb-0">+{{ animatedMembers }}</h3>
            <span class="text-secondary x-small">Thành viên mới</span>
          </div>
        </div>

        <!-- ④ Stat Tile: Tickets Sold -->
        <div class="bento-stat">
          <div class="bento-card bento-card--stat h-100 d-flex flex-column align-items-center justify-content-center text-center p-3">
            <div class="stat-icon-wrap mb-2">
              <span class="stat-icon">🎬</span>
            </div>
            <h3 class="stat-value fw-bold mb-0">{{ animatedTicketsDisplay }}</h3>
            <span class="text-secondary x-small">Vé đã bán</span>
          </div>
        </div>

        <!-- ⑤⑥ Coupon Cards (remaining promos) -->
        <div class="bento-coupon" v-for="(promo, i) in standardPromos" :key="promo.id"
             :class="{ 'auto-wiggle': wiggleIndex === i }">
          <div class="bento-card bento-card--coupon h-100 d-flex flex-column">
            <!-- Image -->
            <div class="coupon-img-wrap position-relative">
              <img :src="promo.image" :alt="promo.title" class="coupon-img">
              <span class="coupon-tag" :class="'coupon-tag--' + promo.tagType">{{ promo.tag }}</span>
            </div>

            <!-- Coupon cut-out edge -->
            <div class="coupon-edge">
              <div class="coupon-edge__notch coupon-edge__notch--left"></div>
              <div class="coupon-edge__dash"></div>
              <div class="coupon-edge__notch coupon-edge__notch--right"></div>
            </div>

            <!-- Content -->
            <div class="p-3 d-flex flex-column flex-grow-1">
              <h4 class="fw-bold text-white fs-6 mb-1 line-clamp-2">{{ promo.title }}</h4>
              <p class="text-secondary x-small mb-3 line-clamp-2 flex-grow-1">{{ promo.description }}</p>

              <div class="mt-auto">
                <div class="d-flex align-items-center gap-2 text-secondary x-small mb-2">
                  <span>📅</span>
                  <span>{{ promo.expiry }}</span>
                </div>
                <button class="btn-copy-code w-100" @click="copyCode(promo.code)">
                  Sao chép mã: <span class="fw-bold">{{ promo.code }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- ⑦ Engagement CTA card (wide 2 cols) -->
        <div class="bento-cta">
          <div class="bento-card bento-card--cta h-100 p-4 d-flex align-items-center gap-4 position-relative overflow-hidden">
            <div class="cta-shimmer"></div>
            <div class="cta-icon-wrap flex-shrink-0 position-relative z-2">
              <span class="cta-icon">🎉</span>
            </div>
            <div class="flex-grow-1 position-relative z-2">
              <h4 class="fw-bold text-white mb-1 fs-6">Đăng ký thành viên CineOps</h4>
              <p class="text-secondary x-small mb-0">Nhận ngay ưu đãi 50K cho lần đầu tiên đặt vé. Đăng ký miễn phí, tích điểm mỗi lần xem phim!</p>
            </div>
            <button class="btn-cta-join flex-shrink-0 position-relative z-2 d-none d-md-block">Đăng ký ngay</button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';

// ── Data ──
const promotions = ref([
  {
    id: 1,
    title: 'Thứ 3 Vui Vẻ - Happy Tuesday',
    description: 'Ưu đãi hot nhất tuần! Đồng giá vé 45K cho mọi suất chiếu 2D tiêu chuẩn. Áp dụng cho thành viên CineOps.',
    image: 'https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=800',
    tag: 'Weekly Hot',
    tagType: 'danger',
    code: 'HAPPYTUE45',
    expiry: 'Vô thời hạn',
    isHero: true
  },
  {
    id: 2,
    title: 'Giảm 50% Combo Bắp Nước',
    description: 'Giảm ngay 50% cho Combo 2 nước 1 bắp khi mua kèm 2 vé xem phim bất kỳ.',
    image: 'https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=600',
    tag: 'Concession',
    tagType: 'warning',
    code: 'COMBO50',
    expiry: '30/06/2026'
  },
  {
    id: 3,
    title: 'U22 - Giá Vé Học Sinh Sinh Viên',
    description: 'Đồng giá 50K cả tuần cho HSSV dưới 22 tuổi. Chỉ cần xuất trình thẻ HSSV/CCCD tại quầy.',
    image: 'https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=600',
    tag: 'Student',
    tagType: 'success',
    code: 'U22STUDENT',
    expiry: '31/12/2026'
  },
  {
    id: 4,
    title: 'Thành Viên Mới - Tặng 1 Bắp Ngọt',
    description: 'Đăng ký thành viên mới nhận ngay voucher 1 bắp ngọt miễn phí cho lần đầu đặt vé.',
    image: 'https://images.unsplash.com/photo-1585647347483-22b66260dfff?auto=format&fit=crop&w=600',
    tag: 'New Member',
    tagType: 'primary',
    code: 'WELCOMEPOP',
    expiry: 'Vô thời hạn'
  },
  {
    id: 5,
    title: 'Ưu Đãi Thẻ Tín Dụng VPBank',
    description: 'Giảm 20K/vé khi thanh toán bằng thẻ tín dụng VPBank vào cuối tuần (T6, T7, CN).',
    image: 'https://images.unsplash.com/photo-1556742049-0cfed4f7a07d?auto=format&fit=crop&w=600',
    tag: 'Partner',
    tagType: 'info',
    code: 'VPBANK20',
    expiry: '30/09/2026'
  }
]);

// ── Computed ──
const heroPromo = computed(() => promotions.value.find(p => p.isHero));
const standardPromos = computed(() => promotions.value.filter(p => !p.isHero));

// ── Date ──
const date = new Date();
const currentDay = date.getDate();
const currentMonth = date.toLocaleString('en', { month: 'short' }).toUpperCase();

// ── Animated counters ──
const animatedMembers = ref(0);
const animatedTickets = ref(0);
const MEMBER_TARGET = 150;
const TICKET_TARGET = 1200;

const animatedTicketsDisplay = computed(() => {
  if (animatedTickets.value >= 1000) {
    return (animatedTickets.value / 1000).toFixed(1) + 'k';
  }
  return animatedTickets.value.toString();
});

function animateCounter(target, refVal, duration = 1800) {
  const start = performance.now();
  const step = (now) => {
    const elapsed = now - start;
    const progress = Math.min(elapsed / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3);
    refVal.value = Math.floor(eased * target);
    if (progress < 1) requestAnimationFrame(step);
  };
  requestAnimationFrame(step);
}

// ── Auto-wiggle engagement ──
const wiggleIndex = ref(-2); // -2 = idle, -1 = hero, 0..n = coupons
let wiggleInterval = null;

function startWiggle() {
  wiggleInterval = setInterval(() => {
    // Pick a random coupon card index (or -1 for hero)
    const total = standardPromos.value.length;
    const idx = Math.floor(Math.random() * (total + 1)) - 1;
    wiggleIndex.value = idx;
    setTimeout(() => { wiggleIndex.value = -2; }, 1200);
  }, 5000);
}

function stopWiggle() {
  if (wiggleInterval) { clearInterval(wiggleInterval); wiggleInterval = null; }
}

// ── Copy code ──
const copyCode = (code) => {
  if (!code) return;
  navigator.clipboard.writeText(code);
  ElMessage({
    message: `✅ Đã sao chép mã: ${code}`,
    type: 'success',
    grouping: true,
  });
};

// ── Lifecycle ──
onMounted(() => {
  animateCounter(MEMBER_TARGET, animatedMembers);
  animateCounter(TICKET_TARGET, animatedTickets);
  startWiggle();
});

onUnmounted(() => { stopWiggle(); });
</script>

<style scoped>
/* ── Page ── */
.promo-page {
  min-height: 100vh;
  background: #121212;
}

/* ── Gradient text ── */
.text-gradient-promo {
  background: linear-gradient(90deg, #FFBA00 0%, #F37021 50%, #E31E24 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ══════════════════════════════
   BENTO GRID — 4 columns
   ══════════════════════════════ */
.promo-bento-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: minmax(140px, auto);
  gap: 20px;
}

.bento-hero   { grid-column: span 3; grid-row: span 2; }
.bento-date   { grid-column: span 1; grid-row: span 2; }
.bento-stat   { grid-column: span 1; grid-row: span 1; }
.bento-coupon { grid-column: span 1; grid-row: span 2; }
.bento-cta    { grid-column: span 2; grid-row: span 1; }

@media (max-width: 992px) {
  .promo-bento-grid { grid-template-columns: repeat(2, 1fr); }
  .bento-hero { grid-column: span 2; }
  .bento-cta  { grid-column: span 2; }
}

@media (max-width: 576px) {
  .promo-bento-grid { grid-template-columns: 1fr; }
  .bento-hero, .bento-date, .bento-stat, .bento-coupon, .bento-cta {
    grid-column: span 1;
  }
}

/* ══════════════════════════════
   BASE BENTO CARD
   ══════════════════════════════ */
.bento-card {
  background: #1E1E1E;
  border-radius: 24px;
  border: 2px solid transparent;
  position: relative;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bento-card::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 26px;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 40%, #F37021 70%, #FFBA00 100%);
  opacity: 0;
  z-index: -1;
  transition: opacity 0.4s ease;
}

.bento-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 0 35px rgba(243,112,33,0.4), 0 16px 50px rgba(0,0,0,0.4);
}

.bento-card:hover::before { opacity: 1; }

/* ══════════════════════════════
   AUTO-WIGGLE ANIMATION
   ══════════════════════════════ */
.auto-wiggle .bento-card {
  animation: cardWiggle 1.2s ease-in-out;
}

@keyframes cardWiggle {
  0%   { transform: rotate(0deg) scale(1); box-shadow: 0 0 0 rgba(243,112,33,0); }
  15%  { transform: rotate(-1deg) scale(1.01); }
  30%  { transform: rotate(1deg) scale(1.02); box-shadow: 0 0 25px rgba(243,112,33,0.4); }
  45%  { transform: rotate(-0.5deg) scale(1.01); }
  60%  { transform: rotate(0.5deg); }
  75%  { transform: rotate(0deg) scale(1.01); box-shadow: 0 0 15px rgba(243,112,33,0.2); }
  100% { transform: rotate(0deg) scale(1); box-shadow: 0 0 0 rgba(243,112,33,0); }
}

.auto-wiggle .bento-card::before {
  animation: borderFlash 1.2s ease-in-out;
}

@keyframes borderFlash {
  0%   { opacity: 0; }
  30%  { opacity: 1; }
  70%  { opacity: 1; }
  100% { opacity: 0; }
}

/* ══════════════════════════════
   ① HERO CARD
   ══════════════════════════════ */
.bento-card--hero { overflow: hidden; }

.hero-bg-img {
  position: absolute; top: 0; left: 0;
  width: 100%; height: 100%;
  object-fit: cover; z-index: 0;
  transition: transform 0.6s ease;
}

.bento-card--hero:hover .hero-bg-img { transform: scale(1.05); }

.hero-overlay {
  position: absolute; top: 0; left: 0;
  width: 100%; height: 100%;
  background: linear-gradient(to right, rgba(0,0,0,0.92) 0%, rgba(0,0,0,0.6) 50%, transparent 100%);
  z-index: 1;
}

.hero-overlay-vignette {
  position: absolute; top: 0; left: 0;
  width: 100%; height: 100%;
  background: linear-gradient(to top, rgba(18,18,18,0.8) 0%, transparent 50%);
  z-index: 2;
}

.hero-title {
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
  letter-spacing: -0.02em;
}

.hero-desc {
  max-width: 500px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.code-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.15);
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
  font-weight: 700;
  color: white;
}

.code-chip__icon { font-size: 1.1rem; }

.btn-copy-hero {
  padding: 8px 20px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(90deg, #E31E24, #F37021, #FFBA00);
  color: white;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-copy-hero:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(227,30,36,0.5);
}

/* ══════════════════════════════
   ② DATE ANCHOR CARD
   ══════════════════════════════ */
.bento-card--date {
  background: linear-gradient(135deg, #C62828 0%, #E31E24 40%, #EF5350 100%);
  border-color: transparent;
}

.bento-card--date::before { display: none; }

.bento-card--date:hover {
  transform: translateY(-8px);
  box-shadow: 0 0 40px rgba(227,30,36,0.5), 0 16px 40px rgba(0,0,0,0.3);
}

.date-day {
  font-size: 3.5rem;
  font-weight: 900;
  color: white;
  line-height: 1;
}

.date-month {
  font-size: 1.2rem;
  font-weight: 600;
  color: rgba(255,255,255,0.7);
  text-transform: uppercase;
  letter-spacing: 0.15em;
}

.date-divider {
  width: 40px;
  height: 2px;
  background: rgba(255,255,255,0.3);
  margin: 12px 0;
}

.date-label {
  font-size: 0.8rem;
  color: rgba(255,255,255,0.8);
  font-weight: 500;
}

.date-count {
  font-size: 2.5rem;
  font-weight: 900;
  color: #FFBA00;
  line-height: 1.1;
  text-shadow: 0 2px 10px rgba(255,186,0,0.3);
}

/* ══════════════════════════════
   ③④ STAT TILES (glassmorphism)
   ══════════════════════════════ */
.bento-card--stat {
  background: rgba(30,30,30,0.8);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.06);
}

.stat-icon-wrap {
  width: 40px; height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(165,28,28,0.2), rgba(255,186,0,0.15));
  display: flex; align-items: center; justify-content: center;
}

.stat-icon { font-size: 1.2rem; }

.stat-value {
  font-size: 1.8rem;
  background: linear-gradient(90deg, #A51C1C 0%, #E31E24 30%, #F37021 60%, #FFBA00 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% auto;
  animation: statGradientShift 3s linear infinite;
}

@keyframes statGradientShift {
  0%   { background-position: 0% center; }
  100% { background-position: 200% center; }
}

/* ══════════════════════════════
   ⑤⑥ COUPON CARDS
   ══════════════════════════════ */
.bento-card--coupon {
  border: 2px dashed rgba(255,255,255,0.12);
  background: #1E1E1E;
  overflow: hidden;
}

/* Marching ants on hover */
.bento-card--coupon:hover {
  border-color: transparent;
  animation: marchingAnts 0.5s linear infinite;
  background-image: none;
}

@keyframes marchingAnts {
  0%   { border-color: #E31E24; }
  25%  { border-color: #F37021; }
  50%  { border-color: #FFBA00; }
  75%  { border-color: #F37021; }
  100% { border-color: #E31E24; }
}

/* Override the ::before for coupon to be a dashed gradient */
.bento-card--coupon::before {
  border-radius: 26px;
  background: linear-gradient(90deg, #A51C1C, #E31E24, #F37021, #FFBA00);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: exclude;
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  padding: 2px;
}

.coupon-img-wrap {
  height: 150px;
  overflow: hidden;
}

.coupon-img {
  width: 100%; height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.bento-card--coupon:hover .coupon-img { transform: scale(1.08); }

.coupon-tag {
  position: absolute;
  top: 10px; right: 10px;
  padding: 3px 10px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 700;
  backdrop-filter: blur(8px);
}

.coupon-tag--danger  { background: rgba(227,30,36,0.85); color: white; }
.coupon-tag--warning { background: rgba(243,112,33,0.85); color: white; }
.coupon-tag--success { background: rgba(16,185,129,0.85); color: white; }
.coupon-tag--primary { background: rgba(59,130,246,0.85); color: white; }
.coupon-tag--info    { background: rgba(139,92,246,0.85); color: white; }

/* Coupon cut-out edge */
.coupon-edge {
  display: flex;
  align-items: center;
  position: relative;
  height: 20px;
  margin: 0 -2px;
}

.coupon-edge__notch {
  width: 20px; height: 20px;
  border-radius: 50%;
  background: #121212;
  flex-shrink: 0;
}

.coupon-edge__notch--left  { margin-left: -10px; }
.coupon-edge__notch--right { margin-right: -10px; }

.coupon-edge__dash {
  flex-grow: 1;
  border-top: 2px dashed rgba(255,255,255,0.1);
}

/* Copy code button */
.btn-copy-code {
  padding: 8px 16px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(90deg, #A51C1C, #E31E24, #F37021, #FFBA00);
  background-size: 200% auto;
  color: white;
  font-size: 0.78rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: btnGradientMove 3s linear infinite;
}

@keyframes btnGradientMove {
  0%   { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.btn-copy-code:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(243,112,33,0.5);
}

/* ══════════════════════════════
   ⑦ CTA CARD
   ══════════════════════════════ */
.bento-card--cta {
  background: linear-gradient(135deg, #1E1E1E 0%, #2a1a1a 100%);
}

.cta-shimmer {
  position: absolute; top: 0; left: -100%;
  width: 200%; height: 100%;
  background: linear-gradient(110deg, transparent 25%, rgba(243,112,33,0.06) 35%, rgba(255,186,0,0.1) 50%, rgba(243,112,33,0.06) 65%, transparent 75%);
  animation: shimmerSlide 4s ease-in-out infinite;
  z-index: 1;
}

@keyframes shimmerSlide {
  0%   { transform: translateX(-30%); }
  100% { transform: translateX(30%); }
}

.cta-icon-wrap {
  width: 50px; height: 50px;
  border-radius: 14px;
  background: linear-gradient(135deg, #F37021, #FFBA00);
  display: flex; align-items: center; justify-content: center;
}

.cta-icon { font-size: 1.4rem; }

.btn-cta-join {
  padding: 10px 24px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(90deg, #E31E24, #FFBA00);
  color: white;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-cta-join:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(227,30,36,0.5);
}

/* ══════════════════════════════
   BADGES
   ══════════════════════════════ */
.promo-badge {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 0.78rem;
  font-weight: 700;
}

.promo-badge--hot {
  background: linear-gradient(90deg, #A51C1C, #E31E24);
  color: white;
  animation: badgePulse 2s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% { box-shadow: 0 0 0 rgba(227,30,36,0); }
  50%      { box-shadow: 0 0 15px rgba(227,30,36,0.5); }
}

/* ══════════════════════════════
   UTILITIES
   ══════════════════════════════ */
.cursor-pointer { cursor: pointer; }
.x-small { font-size: 0.8rem; }

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ── Reduced Motion ── */
@media (prefers-reduced-motion: reduce) {
  .bento-card, .hero-bg-img, .coupon-img { transition: none !important; }
  .auto-wiggle .bento-card,
  .auto-wiggle .bento-card::before,
  .stat-value, .btn-copy-code,
  .cta-shimmer, .promo-badge--hot {
    animation: none !important;
  }
}
</style>
