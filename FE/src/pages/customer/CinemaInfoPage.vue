<template>
  <div class="cinema-info-page pb-5">
    <div class="header-section text-center py-5 bg-dark text-white mb-5 rounded-4 shadow-sm" style="background: linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.7)), url('https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=1200') center/cover;">
      <h1 class="display-4 fw-bold mb-3 tracking-tight">Rạp <span class="text-primary-light">CineOps</span></h1>
      <p class="lead opacity-75">Không gian điện ảnh đẳng cấp - Trải nghiệm không giới hạn</p>
    </div>

    <div class="container">
      <div class="row g-5">
        <!-- Left: Map & Info -->
        <div class="col-lg-7">
          <section class="mb-5">
            <h2 class="fs-3 fw-bold mb-4 d-flex align-items-center gap-2">
              <el-icon class="text-primary"><Location /></el-icon>
              Vị trí rạp
            </h2>
            <div class="map-container rounded-4 overflow-hidden shadow-sm border mb-4" style="height: 400px;">
              <iframe 
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m12!1m3!1d3919.460232422744!2d106.665303511116!3d10.77601938932697!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f1e6378e9f9%3A0xe549cd97b0a7b45!2zSOG7hyB0aOG7kW5nIHLhuqFwIENpbmVPcHM!5e0!3m2!1svi!2svn!4v1700000000000!5m2!1svi!2svn" 
                width="100%" 
                height="100%" 
                style="border:0;" 
                allowfullscreen="" 
                loading="lazy"
              ></iframe>
            </div>
            <div class="info-card p-4 bg-white rounded-4 shadow-sm border border-light">
              <div class="row g-4">
                <div class="col-md-6">
                  <h6 class="fw-bold text-dark x-small text-uppercase tracking-wider mb-2">Địa Chỉ</h6>
                  <p class="text-secondary small mb-0">Tầng 5, TTTM Cine Plaza, 123 Lê Lợi, Quận 1, TP. Hồ Chí Minh</p>
                </div>
                <div class="col-md-6">
                  <h6 class="fw-bold text-dark x-small text-uppercase tracking-wider mb-2">Hotline Quầy Vé</h6>
                  <p class="text-primary fw-bold mb-0">1900 1234 (Phím 1)</p>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- Right: Pricing Table -->
        <div class="col-lg-5">
          <section>
            <h2 class="fs-3 fw-bold mb-4 d-flex align-items-center gap-2">
              <el-icon class="text-primary"><Tickets /></el-icon>
              Bảng Giá Vé
            </h2>
            <div class="pricing-card bg-white rounded-4 shadow-sm border border-light overflow-hidden">
              <el-tabs v-model="activeTab" class="custom-pricing-tabs" stretch>
                <el-tab-pane label="Vé 2D" name="2d">
                  <div class="p-3">
                    <table class="table table-hover align-middle mb-0">
                      <thead class="bg-light">
                        <tr class="x-small text-uppercase text-secondary tracking-wider">
                          <th class="py-3 ps-3">Thời gian</th>
                          <th class="py-3">Thành viên</th>
                          <th class="py-3 text-end pe-3">Người lớn</th>
                        </tr>
                      </thead>
                      <tbody class="small">
                        <tr>
                          <td class="py-3 ps-3 fw-medium">Thứ 2 - Thứ 5</td>
                          <td class="py-3"><span class="badge bg-success-light text-success">45.000đ</span></td>
                          <td class="py-3 text-end pe-3 fw-bold">75.000đ</td>
                        </tr>
                        <tr>
                          <td class="py-3 ps-3 fw-medium">Thứ 3 Vui Vẻ</td>
                          <td class="py-3 fw-bold text-primary">45.000đ</td>
                          <td class="py-3 text-end pe-3 fw-bold text-primary">45.000đ</td>
                        </tr>
                        <tr>
                          <td class="py-3 ps-3 fw-medium">Thứ 6 - CN</td>
                          <td class="py-3"><span class="badge bg-success-light text-success">55.000đ</span></td>
                          <td class="py-3 text-end pe-3 fw-bold">95.000đ</td>
                        </tr>
                        <tr>
                          <td class="py-3 ps-3 fw-medium">Ngày Lễ</td>
                          <td class="py-3"><span class="badge bg-success-light text-success">65.000đ</span></td>
                          <td class="py-3 text-end pe-3 fw-bold">115.000đ</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="Vé 3D / IMAX" name="3d">
                  <div class="p-4 text-center text-secondary py-5">
                    <el-icon :size="48" class="opacity-25 mb-3"><Film /></el-icon>
                    <p>Đang cập nhật bảng giá IMAX mới nhất...</p>
                  </div>
                </el-tab-pane>
              </el-tabs>
              <div class="p-3 bg-light-subtle border-top">
                <p class="x-small text-secondary mb-0">* Giá vé đã bao gồm thuế GTGT. Trẻ em dưới 0.9m được miễn phí vé (ngồi cùng người lớn).</p>
              </div>
            </div>
            
            <el-button type="primary" class="w-100 mt-4 py-3 rounded-3 shadow-sm fs-6 fw-bold" @click="$router.push('/')">
              Đặt vé ngay để nhận ưu đãi
            </el-button>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Location, Tickets, Film } from '@element-plus/icons-vue';

const activeTab = ref('2d');
</script>

<style scoped>
.text-primary-light { color: #a5b4fc; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.x-small { font-size: 0.7rem; }
.bg-success-light { background-color: #dcfce7 !important; }

.custom-pricing-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}
.custom-pricing-tabs :deep(.el-tabs__item) {
  height: 54px;
  font-weight: 600;
}

.pricing-card {
  transition: box-shadow 0.3s ease;
}
.pricing-card:hover {
  box-shadow: 0 1rem 2rem rgba(0,0,0,0.1) !important;
}

table tr:last-child td {
  border-bottom: none;
}
</style>
