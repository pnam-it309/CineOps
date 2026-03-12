<template>
  <div class="standalone-invoice-detail h-100 overflow-hidden bg-white">
    <!-- Loading Overlay -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-secondary">Đang tải chi tiết hóa đơn...</p>
    </div>

    <!-- Main Content -->
    <div v-else-if="detailInvoice" class="invoice-detail-container h-100 d-flex flex-column">
      <!-- Header Bar (Minimal, fixed) -->
      <div class="invoice-header bg-primary text-white p-3 d-flex align-items-center justify-content-between">
        <div>
          <h4 class="mb-0 fw-bold">
            <i class="bi bi-receipt-cutoff me-2"></i>
            Hóa đơn #{{ detailInvoice.maHoaDon }}
          </h4>
          <small>{{ formatDate(detailInvoice.ngayTao) }}</small>
        </div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-light btn-sm" @click="printInvoice">
            <i class="bi bi-printer me-1"></i> In hóa đơn
          </button>
          <button class="btn btn-outline-light btn-sm" @click="printVatInvoice">
            <i class="bi bi-file-earmark-text me-1"></i> In Hóa đơn VAT
          </button>
          <router-link to="/admin/quanlyhoadon" class="btn btn-light btn-sm">
            <i class="bi bi-list-ul me-1"></i> Danh sách
          </router-link>
        </div>
      </div>

      <!-- Detail Body -->
      <div class="flex-grow-1 overflow-auto p-4">
        <!-- Profile Header -->
        <div class="profile-header p-4 border-bottom bg-light d-flex align-items-center gap-4 mb-4">
          <div class="shadow-sm border d-flex align-items-center justify-content-center bg-white text-secondary" style="width: 100px; height: 100px;">
            <i class="bi bi-receipt fs-1"></i>
          </div>
          <div class="flex-grow-1">
            <div class="d-flex align-items-center gap-2 mb-1">
              <h3 class="fw-bold m-0 text-dark">Hóa đơn {{ detailInvoice.maHoaDon }}</h3>
              <el-tag :type="detailInvoice.trangThai === 1 ? 'success' : 'danger'" effect="dark" size="large">
                {{ detailInvoice.trangThai === 1 ? 'ĐÃ THANH TOÁN' : 'ĐÃ HỦY' }}
              </el-tag>
            </div>
            <div class="d-flex align-items-center gap-3 small text-secondary">
              <div><i class="bi bi-calendar3 me-1"></i>{{ formatDate(detailInvoice.ngayTao) }}</div>
              <div><i class="bi bi-wallet2 me-1"></i>{{ detailInvoice.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</div>
            </div>
          </div>
          <div class="text-end">
            <div class="text-secondary small mb-1">TỔNG THANH TOÁN</div>
            <div class="fw-bold fs-2 text-primary">{{ formatCurrency(detailInvoice.tongTienThanhToan) }}</div>
          </div>
        </div>

        <!-- Detail Layout -->
        <div class="profile-details-body">
          <div class="row g-4">
            <div class="col-lg-8">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Chi tiết giao dịch</h6>
              <div class="border overflow-hidden rounded">
                <table class="table table-hover m-0 w-100">
                  <thead class="bg-light">
                    <tr>
                      <th class="small py-3 ps-4 border-0 text-secondary">Mô tả mặt hàng</th>
                      <th class="small py-3 text-center border-0 text-secondary">SL</th>
                      <th class="small py-3 text-end pe-4 border-0 text-secondary">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, idx) in invoiceDetails" :key="idx">
                      <td class="ps-4 py-3 border-bottom">
                        <div class="fw-bold text-dark">
                          {{ item.loai === 1 ? item.tenPhim : item.tenSanPham }}
                        </div>
                        <div class="text-secondary small">
                          <span v-if="item.loai === 1">
                            🪑 Ghế: {{ item.viTriGhe }} | 📍 {{ item.tenPhongChieu }}
                          </span>
                          <span v-else>
                            📦 Mã: {{ item.maMuc }}
                          </span>
                        </div>
                      </td>
                      <td class="text-center py-3 border-bottom text-dark fw-bold">
                        {{ item.soLuong }}
                      </td>
                      <td class="text-end pe-4 py-3 border-bottom text-dark fw-bold">
                        {{ formatCurrency(item.thanhTien) }}
                      </td>
                    </tr>
                    <tr v-if="!invoiceDetails.length">
                      <td colspan="3" class="text-center text-secondary py-4">Không có sản phẩm</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <!-- Billing Summary -->
              <div class="mt-4 p-4 border bg-light d-flex justify-content-end gap-5">
                <div class="text-end">
                  <div class="text-secondary small mb-1">TỔNG TRƯỚC GIẢM</div>
                  <div class="fw-bold text-dark fs-5">{{ formatCurrency(invoiceDetails.reduce((acc, i) => acc + i.thanhTien, 0)) }}</div>
                </div>
                <div class="text-end border-start ps-5">
                  <div class="text-secondary small mb-1">KHUYẾN MÃI</div>
                  <div class="fw-bold text-danger fs-5">-{{ formatCurrency(detailInvoice.soTienGiam || 0) }}</div>
                </div>
              </div>
            </div>

            <!-- Right Column: Info -->
            <div class="col-lg-4">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Thông tin liên quan</h6>
              <div class="p-3 border mb-3 bg-white">
                <div class="d-flex align-items-center gap-3 mb-3 pb-3 border-bottom">
                  <div class="p-2 border bg-light text-secondary"><i class="bi bi-person fs-4"></i></div>
                  <div>
                    <label class="text-secondary tiny-text">KHÁCH HÀNG</label>
                    <div class="fw-bold text-dark">{{ detailInvoice.tenKhachHang || 'Khách vãng lai' }}</div>
                    <div class="small text-secondary">{{ detailInvoice.sdtKhachHang || '—' }}</div>
                  </div>
                </div>
                <div class="d-flex align-items-center gap-3">
                  <div class="p-2 border bg-light text-secondary"><i class="bi bi-person-workspace fs-4"></i></div>
                  <div>
                    <label class="text-secondary tiny-text">NHÂN VIÊN LẬP</label>
                    <div class="fw-bold text-dark">{{ detailInvoice.nguoiTao || 'Admin System' }}</div>
                    <div class="small text-secondary">{{ detailInvoice.maNhanVien || 'SYSTEM' }}</div>
                  </div>
                </div>
              </div>

              <!-- VAT Info -->
              <div class="p-3 border bg-light mt-3">
                <h6 class="fw-bold small text-secondary mb-3 text-uppercase">Thông tin VAT</h6>
                <div class="d-flex justify-content-between mb-2">
                  <span class="text-secondary small">Thuế suất (VAT)</span>
                  <span class="text-dark fw-bold">8.0%</span>
                </div>
                <div class="d-flex justify-content-between">
                  <span class="text-secondary small">Tiền thuế</span>
                  <span class="text-dark fw-bold">{{ formatCurrency((detailInvoice.tongTienThanhToan || 0) * 0.08) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Print Trigger -->
      <VatInvoiceTemplate :invoice="detailInvoice" :details="invoiceDetails" />
    </div>

    <!-- No Data -->
    <div v-else class="empty-state d-flex flex-column align-items-center justify-content-center h-100 text-center p-5">
      <i class="bi bi-receipt-x fs-1 text-muted mb-3"></i>
      <h5 class="text-muted mb-1">Không tìm thấy hóa đơn</h5>
      <p class="text-secondary mb-4">Hóa đơn không tồn tại hoặc đã bị xóa</p>
      <router-link to="/admin/quanlyhoadon" class="btn btn-outline-primary">
        <i class="bi bi-arrow-left me-2"></i>Quay về danh sách
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { hoaDonService } from '@/services/api/admin/hoaDonService';
import VatInvoiceTemplate from '@/pages/admin/quanlyhoadon/VatInvoiceTemplate.vue';

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const detailInvoice = ref(null);
const invoiceDetails = ref([]);
const paymentHistory = ref([]);

onMounted(async () => {
  const invoiceId = route.params.id;
  if (!invoiceId) {
    ElMessage.error('Thiếu ID hóa đơn');
    router.push('/admin/quanlyhoadon');
    return;
  }

  try {
    // Load invoice details (from service, matches AdminInvoices)
    const [resDetails, resInvoice] = await Promise.all([
      hoaDonService.getInvoiceDetails(invoiceId),
      hoaDonService.getInvoiceById(invoiceId)
    ]);
    invoiceDetails.value = resDetails.data || [];
    detailInvoice.value = resInvoice.data || null;
  } catch (error) {
    ElMessage.error('Không thể tải chi tiết hóa đơn!');
    router.push('/admin/quanlyhoadon');
  } finally {
    loading.value = false;
  }
});

const printInvoice = () => {
  // Store for print template
  sessionStorage.setItem('printInvoice', JSON.stringify(detailInvoice.value));
  sessionStorage.setItem('printInvoiceDetails', JSON.stringify(invoiceDetails.value));
  
  // Navigate to print page (reuse existing)
  window.open('/admin/quanlyhoadon/InvoicePrint', '_blank');
};

const printVatInvoice = () => {
  // Same data, but for VAT template (reuses same sessionStorage keys)
  sessionStorage.setItem('printInvoice', JSON.stringify(detailInvoice.value));
  sessionStorage.setItem('printInvoiceDetails', JSON.stringify(invoiceDetails.value));
  
  // Temporary VAT print - open VatInvoiceTemplate (modal hidden on print)
  const vatPrintWindow = window.open('', '_blank');
  vatPrintWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>In Hóa đơn VAT</title>
      <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
      <div id="vat-print-root"></div>
      <script type="module">
        import { createApp } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
        import VatInvoiceTemplate from 'http://localhost:3456/src/pages/admin/quanlyhoadon/VatInvoiceTemplate.vue';
        const app = createApp(VatInvoiceTemplate, {
          invoice: ${JSON.stringify(detailInvoice.value)},
          details: ${JSON.stringify(invoiceDetails.value)}
        });
        app.mount('#vat-print-root');
        setTimeout(() => window.print(), 500);
      </\script>
    </body>
    </html>
  `);
  vatPrintWindow.document.close();
};

const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  const day = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  return `${time} ${day}`;
};
</script>

<style scoped>
.standalone-invoice-detail {
  font-family: system-ui, -apple-system, sans-serif;
}

.loading-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.invoice-detail-container {
  height: 100vh;
}

.invoice-header {
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.profile-details-body {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.tiny-text {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #64748b;
}

@media print {
  .invoice-header,
  .btn {
    display: none !important;
  }
  
  .profile-details-body {
    max-height: none !important;
  }
  
  .vat-invoice-wrapper {
    display: block !important;
  }
}
</style>

