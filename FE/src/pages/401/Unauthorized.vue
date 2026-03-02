<template>
  <div class="film-background theme-401">
    <div class="projector-light"></div>
    <div class="cinema-card">
      <div class="clapperboard-icon mb-3"><i class="bi bi-ticket-perforated"></i></div>
      <h1 class="error-code flicker-text">401</h1>
      <h2 class="error-title">Khán Giả Chưa Mua Vé</h2>
      <p class="error-desc">
        Oops! Có vẻ như bạn chưa xuất trình vé (đăng nhập) để vào phòng chiếu này.<br>
        Vui lòng đăng nhập để tiếp tục thưởng thức phim.
      </p>
      <div class="action-buttons d-flex justify-content-center gap-3 mt-4">
        <router-link to="/login" class="btn-cinema">
          Đăng Nhập Ngay
        </router-link>
        <router-link to="/" class="btn-cinema btn-cinema-secondary">
          Về Sảnh Chính
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;700;900&display=swap');

.film-background {
  min-height: 100vh;
  background: #111;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  color: #fff;
  font-family: 'Outfit', sans-serif;
}

.film-background::before,
.film-background::after {
  content: "";
  position: absolute;
  top: 0;
  bottom: 0;
  width: 50px;
  background-color: #050505;
  background-image: repeating-linear-gradient(to bottom, transparent 0, transparent 40px, #e0e0e0 40px, #e0e0e0 60px);
  background-size: 100% 100px;
  z-index: 10;
  border-left: 4px solid #1a1a1a;
  border-right: 4px solid #1a1a1a;
  box-shadow: 0 0 15px rgba(0,0,0,0.8);
}

.film-background::before { left: 5%; }
.film-background::after { right: 5%; }

.projector-light {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120vw;
  height: 120vh;
  background: radial-gradient(circle, rgba(255,255,255,0.05) 0%, rgba(0,0,0,0.8) 60%, rgba(0,0,0,1) 100%);
  pointer-events: none;
  z-index: 1;
}

.cinema-card {
  position: relative;
  z-index: 2;
  background: rgba(20, 20, 20, 0.85);
  padding: 3.5rem 4rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 25px 50px rgba(0,0,0,0.9), 0 0 0 1px rgba(255, 255, 255, 0.03) inset;
  text-align: center;
  backdrop-filter: blur(12px);
  max-width: 650px;
  width: 90%;
}

.clapperboard-icon {
  font-size: 4.5rem;
  line-height: 1;
  display: inline-block;
  animation: float 4s ease-in-out infinite;
}

.error-code {
  font-size: 8rem;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 0.5rem;
  font-family: 'Courier New', Courier, monospace;
  letter-spacing: -5px;
}

.error-title {
  font-size: 2.2rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 3px;
  margin-bottom: 1.5rem;
  color: #fff;
}

.error-desc {
  font-size: 1.15rem;
  color: #a0a0a0;
  margin-bottom: 2.5rem;
  line-height: 1.6;
}

.btn-cinema {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 14px 32px;
  font-size: 1.1rem;
  font-weight: 600;
  text-decoration: none;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  border-radius: 50px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.btn-cinema-secondary {
  background: transparent !important;
  border: 2px solid #555 !important;
  color: #ccc !important;
  box-shadow: none !important;
}

.btn-cinema-secondary:hover {
  border-color: #fff !important;
  color: #fff !important;
  background: rgba(255,255,255,0.05) !important;
}

@keyframes float {
  0% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(8deg); }
  100% { transform: translateY(0px) rotate(0deg); }
}

@keyframes flicker {
  0%, 19%, 21%, 23%, 25%, 54%, 56%, 100% { 
    opacity: 1; 
    text-shadow: 0 0 10px var(--neon-color), 0 0 20px var(--neon-color), 0 0 30px var(--neon-color); 
  }
  20%, 24%, 55% { 
    opacity: 0.7; 
    text-shadow: 0 0 5px var(--neon-color); 
  }
}

.flicker-text { animation: flicker 4s infinite alternate; }

.theme-401 { --neon-color: #ff9f43; }
.theme-401 .error-code { color: #ff9f43; }
.theme-401 .btn-cinema { background: transparent; border: 2px solid #ff9f43; color: #ff9f43; }
.theme-401 .btn-cinema:hover { background: #ff9f43; color: #111; box-shadow: 0 0 20px rgba(255, 159, 67, 0.6); transform: translateY(-3px); }

@media (max-width: 768px) {
  .film-background::before, .film-background::after {
    width: 30px;
    background-size: 100% 60px;
    background-image: repeating-linear-gradient(to bottom, transparent 0, transparent 20px, #e0e0e0 20px, #e0e0e0 30px);
  }
  .film-background::before { left: 2%; }
  .film-background::after { right: 2%; }
  .cinema-card { padding: 2.5rem 2rem; }
  .error-code { font-size: 6rem; }
  .error-title { font-size: 1.5rem; letter-spacing: 2px; }
  .btn-cinema { padding: 12px 24px; font-size: 1rem; width: 100%; margin-bottom: 10px; }
  .action-buttons { flex-direction: column; }
}
</style>
