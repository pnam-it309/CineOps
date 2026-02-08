<template>
  <div class="promotions-page pb-5">
    <div class="header-section text-center py-5 bg-dark text-white mb-5 rounded-4 shadow-sm" style="background: linear-gradient(rgba(0,0,0,0.6), rgba(0,0,0,0.6)), url('https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200') center/cover;">
      <h1 class="display-4 fw-bold mb-3 tracking-tight">CineOps <span class="text-primary-light">Promotions</span></h1>
      <p class="lead opacity-75">Săn Deal cực hời - Xem phim cực đã!</p>
    </div>

    <div class="container">
      <el-row :gutter="30">
        <el-col v-for="promo in promotions" :key="promo.id" :xs="24" :md="12" :lg="8" class="mb-4">
          <el-card shadow="hover" class="promo-card h-100 border-0 shadow-sm overflow-hidden" :body-style="{ padding: '0' }">
            <div class="promo-image-wrapper position-relative">
              <img :src="promo.image" class="w-100 object-fit-cover" style="height: 200px;">
              <div class="promo-badge position-absolute top-0 start-0 m-3 px-3 py-1 rounded-pill bg-primary text-white small fw-bold shadow-sm">
                {{ promo.tag }}
              </div>
            </div>
            <div class="p-4">
              <h3 class="fs-5 fw-bold text-dark mb-2">{{ promo.title }}</h3>
              <p class="text-secondary small mb-4 lh-base">{{ promo.description }}</p>
              
              <div class="coupon-section bg-light rounded-3 p-3 border border-dashed border-secondary border-opacity-25 d-flex justify-content-between align-items-center mb-4">
                <div class="d-flex flex-column">
                  <span class="x-small text-uppercase text-secondary fw-bold tracking-wider">Mã Giảm Giá</span>
                  <span class="fw-bold text-dark fs-5 tracking-tight font-monospace">{{ promo.code }}</span>
                </div>
                <el-button type="primary" size="small" :icon="CopyDocument" @click="copyCode(promo.code)" round>Sao chép</el-button>
              </div>

              <div class="d-flex justify-content-between align-items-center">
                <span class="text-secondary x-small"><el-icon class="me-1"><Calendar /></el-icon> Hết hạn: {{ promo.expiry }}</span>
                <el-button type="primary" plain round size="default" @click="$router.push('/')">Đặt vé ngay</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { CopyDocument, Calendar } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const promotions = ref([
  {
    id: 1,
    title: 'Thứ 3 Vui Vẻ - Vé Đồng Giá 45K',
    description: 'Ưu đãi dành riêng cho ngày thứ 3 hàng tuần. Cơ hội thưởng thức các bom tấn Hollywood với giá cực hời.',
    image: 'https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=600',
    tag: 'Hot Deal',
    code: 'HAPPYTUE45',
    expiry: '31/12/2026'
  },
  {
    id: 2,
    title: 'Combo Couple Siêu Tiết Kiệm',
    description: 'Giảm ngay 20% khi mua Combo 2 nước 1 bắp lớn. Tận hưởng trọn vẹn từng khoảnh khắc điện ảnh cùng người ấy.',
    image: 'https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=600',
    tag: 'Concession',
    code: 'COUPLELOVE20',
    expiry: '30/06/2026'
  },
  {
    id: 3,
    title: 'U22 Cinema - Đồng giá cho Sinh Viên',
    description: 'Chỉ 45k cho vé 2D tất cả các ngày trong tuần (trừ Lễ/Tết) cho thành viên dưới 22 tuổi.',
    image: 'https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=600',
    tag: 'Student',
    code: 'U22STUDENT',
    expiry: 'Vô thời hạn'
  }
]);

const copyCode = (code) => {
  navigator.clipboard.writeText(code);
  ElMessage({
    message: 'Đã sao chép mã giảm giá: ' + code,
    type: 'success',
    duration: 2000
  });
};
</script>

<style scoped>
.promo-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.promo-card:hover {
  transform: translateY(-8px);
}
.text-primary-light { color: #a5b4fc; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.x-small { font-size: 0.65rem; }
.font-monospace { font-family: 'JetBrains Mono', monospace !important; }
</style>
