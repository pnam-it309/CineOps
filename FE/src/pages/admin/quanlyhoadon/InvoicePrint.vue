<template>
  <div class="vat-invoice-wrapper d-print-block">
    <div class="invoice-outer-border">
      <div class="invoice-container d-flex flex-column position-relative">
        
        <div class="watermark-bg"></div>

        <div class="header-section pb-1 mb-2">
          <div class="row align-items-center">
            
            <div class="col-3 text-center pe-2 border-end border-2 border-danger">
               <div class="logo-box mb-1">
                 <h2 class="fw-bold mb-0" style="letter-spacing: -1px; font-style: italic; font-size: 24px;">
                   <span style="color:#004080">Cine</span><span class="text-danger">Ops</span>
                 </h2>
                 <div class="text-dark fw-bold text-uppercase" style="font-size: 7px;">The Leading Cinema Group</div>
               </div>
               <div class="qr-code-box mx-auto mt-1" style="width: 45px; height: 45px; border: 1px dashed #000;"></div>
            </div>
            
            <div class="col-9 company-info ps-3">
              <h5 class="fw-bold text-danger mb-1 text-uppercase" style="font-size: 14px;">
                CÔNG TY TNHH RẠP CHIẾU PHIM CINEOPS VIỆT NAM
              </h5>
              <div class="info-line mb-1">
                <span class="label fw-bold">Mã số thuế:</span> 
                <span class="value fw-bold fs-6 tracking-wide ms-2">0 1 0 6 0 2 6 4 9 5</span>
              </div>
              <div class="info-line mb-1">
                <span class="label fw-bold">Địa chỉ:</span> 
                <span class="value ms-2">Số 1 Trịnh Văn Bô, Nam Từ Liêm, Thành phố Hà Nội</span>
              </div>
              <div class="row g-0 mb-1">
                <div class="col-6 info-line">
                  <span class="label fw-bold">Điện thoại:</span> <span class="value ms-2">0901 80 16 18</span>
                </div>
                <div class="col-6 info-line">
                  <span class="label fw-bold">Email:</span> <span class="value ms-2">contact@cineops.vn</span>
                </div>
              </div>
              <div class="row g-0">
                <div class="col-12 info-line d-flex">
                  <span class="label fw-bold">Số tài khoản:</span> 
                  <span class="value ms-2">21510001481852 tại BIDV - CN Hà Thành</span>
                </div>
              </div>
            </div>

          </div>
        </div>

        <div class="horizontal-dotted-line mb-2"></div>

        <div class="row mb-2 position-relative">
          <div class="col-8 text-center mt-1">
            <h3 class="fw-bold text-danger mb-0 text-uppercase" style="font-size: 18px;">HÓA ĐƠN GIÁ TRỊ GIA TĂNG</h3>
            <p class="fst-italic small mb-0 fw-bold" style="font-size: 11px;">(Bản thể hiện của hóa đơn điện tử)</p>
            <p class="mb-0 mt-1 fw-bold" style="font-size: 13px;">
              Ngày <span class="px-2">{{ currentDay }}</span> 
              tháng <span class="px-2">{{ currentMonth }}</span> 
              năm <span class="px-2">{{ currentYear }}</span>
            </p>
          </div>
          <div class="col-4 invoice-meta small fw-bold" style="font-size: 12px;">
            <div class="meta-item mb-1">
              <span class="label" style="width:65px; display:inline-block">Mẫu số:</span> 
              <span class="value">01GTKT0/001</span>
            </div>
            <div class="meta-item mb-1">
              <span class="label" style="width:65px; display:inline-block">Ký hiệu:</span> 
              <span class="value">CV/26E</span>
            </div>
            <div class="meta-item mt-1">
              <span class="label" style="width:65px; display:inline-block">Số:</span> 
              <span class="value text-danger fs-5">{{ invoice?.maHoaDon?.replace(/\D/g, '') || '10001' }}</span>
            </div>
          </div>
        </div>

        <div class="customer-info-section mb-2 small position-relative z-1 fw-bold" style="font-size: 12px;">
          <div class="dotted-line-row">
            <span class="label-box">Họ tên người mua hàng:</span>
            <span class="value-box" style="font-size: 14px;">{{ invoice?.tenKhachHang || 'Khách lẻ' }}</span>
          </div>
          <div class="dotted-line-row">
            <span class="label-box">Tên đơn vị:</span>
            <span class="value-box"></span>
          </div>
          <div class="dotted-line-row">
            <span class="label-box">Mã số thuế:</span>
            <span class="value-box"></span>
          </div>
          <div class="dotted-line-row">
            <span class="label-box">Địa chỉ:</span>
            <span class="value-box"></span>
          </div>
          <div class="dotted-line-row">
            <span class="label-box">Số tài khoản:</span>
            <span class="value-box"></span>
          </div>
          <div class="dotted-line-row">
            <span class="label-box">Hình thức thanh toán:</span>
            <span class="value-box">{{ invoice?.phuongThucThanhToan === 0 ? 'Tiền mặt' : 'Chuyển khoản' }}</span>
          </div>
        </div>

        <table class="table-custom w-100 position-relative z-1 mb-0">
          <thead>
            <tr class="text-center align-middle bg-white">
              <th width="6%" class="py-1">STT</th>
              <th width="38%" class="py-1">Tên hàng hóa, dịch vụ</th>
              <th width="10%" class="py-1">ĐVT</th>
              <th width="10%" class="py-1">Số lượng</th>
              <th width="16%" class="py-1">Đơn giá</th>
              <th width="20%" class="py-1">Thành tiền</th>
            </tr>
            <tr class="text-center bg-white text-muted align-middle" style="font-size: 10px;">
              <td class="py-0 border-top-0 border-bottom-0">(1)</td>
              <td class="py-0 border-top-0 border-bottom-0">(2)</td>
              <td class="py-0 border-top-0 border-bottom-0">(3)</td>
              <td class="py-0 border-top-0 border-bottom-0">(4)</td>
              <td class="py-0 border-top-0 border-bottom-0">(5)</td>
              <td class="py-0 border-top-0 border-bottom-0">(6=4x5)</td>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in details" :key="index" class="data-row">
              <td class="text-center align-middle print-compact-cell">{{ index + 1 }}</td>
              
              <td class="text-start align-middle print-compact-cell pb-2">
                <div class="mb-1">
                  <el-tag :type="item.tenPhim ? 'warning' : 'info'" effect="plain" size="small" class="custom-print-tag">
                    {{ item.tenPhim ? 'Vé xem phim' : 'Sản phẩm' }}
                  </el-tag>
                </div>

                <template v-if="item.tenPhim">
                  <div class="item-title fw-bold text-dark">{{ item.tenPhim }}</div>
                  <div class="item-sub text-secondary">
                    <i class="bi bi-geo-alt-fill me-1"></i>P: <span class="fw-bold">{{ item.tenPhongChieu || '—' }}</span>
                    <span class="mx-1">|</span>
                    <i class="bi bi-usb-drive-fill me-1"></i>Ghế: <span class="fw-bold text-danger">{{ item.viTriGhe || '—' }}</span>
                  </div>
                </template>

                <template v-else-if="item.tenSanPham">
                  <div class="item-title fw-bold text-dark">{{ item.tenSanPham }}</div>
                  <div class="item-sub text-secondary">
                    <i class="bi bi-upc-scan me-1"></i>Mã SP: {{ item.maMuc || '—' }}
                  </div>
                </template>

                <template v-else>
                  <span class="text-muted item-sub">— (Không xác định)</span>
                </template>
              </td>

              <td class="text-center align-middle print-compact-cell">{{ item.loai === 0 ? 'Vé' : 'Phần' }}</td>
              
              <td class="text-center align-middle print-compact-cell">{{ item.soLuong || 1 }}</td>
              
              <td class="text-end pe-2 align-middle print-compact-cell">{{ formatPrice(item.donGia) }}</td>
              
              <td class="text-end pe-2 align-middle print-compact-cell">{{ formatPrice(item.thanhTien) || formatPrice(item.donGia * (item.soLuong || 1)) }}</td>
            </tr>
            
            <tr v-for="i in Math.max(0, 4 - details.length)" :key="'empty-' + i" class="empty-row">
              <td></td><td></td><td></td><td></td><td></td><td></td>
            </tr>
          </tbody>
          
          <tfoot>
            <tr>
              <td colspan="4" class="border-end-0 border-bottom-0 bg-white"></td>
              <td class="fw-bold border-start-1" style="font-size: 11px;">Cộng tiền hàng:</td>
              <td class="text-end pe-2 fw-bold align-middle">{{ formatPrice(invoice?.tongTien) }}</td>
            </tr>
            <tr>
               <td colspan="2" class="fw-bold border-end-0 border-bottom-0 border-top-0 bg-white pt-1 ps-2" style="font-size: 11px;">
                 Chiết khấu / Giảm giá:
               </td>
               <td colspan="2" class="border-start-0 border-end-0 border-bottom-0 border-top-0 bg-white"></td>
               <td class="fw-bold border-start-1 text-danger align-middle" style="font-size: 11px;">Số tiền giảm:</td>
               <td class="text-end pe-2 fw-bold text-danger align-middle">{{ invoice?.tongTienGiamGia ? formatPrice(invoice?.tongTienGiamGia) : '0' }}</td>
            </tr>
            <tr>
              <td colspan="4" class="border-end-0 border-top-0 bg-white"></td>
              <td class="fw-bold border-start-1" style="font-size: 11px;">Tổng tiền thanh toán:</td>
              <td class="text-end pe-2 fw-bold text-danger fs-6 align-middle">{{ formatPrice(invoice?.tongTienThanhToan) }}</td>
            </tr>
          </tfoot>
        </table>

        <div class="mt-2 small mb-1 position-relative z-1">
          <div class="dotted-line-row">
            <span class="label-box fw-bold" style="font-size: 13px;">Số tiền bằng chữ:</span>
            <span class="value-box fw-bold fst-italic text-capitalize" style="font-size: 13px;">{{ amountInWordsText }} đồng chẵn.</span>
          </div>
        </div>

        <div class="row mt-2 text-center position-relative z-1">
          <div class="col-6">
            <p class="fw-bold mb-0" style="font-size: 13px;">Người mua hàng</p>
            <div class="mt-4 pt-3 fw-bold text-uppercase" style="font-size: 14px;">{{ invoice?.tenKhachHang || 'TRẦN ANH TUẤN' }}</div>
          </div>
          <div class="col-6">
            <p class="fw-bold mb-0" style="font-size: 13px;">Người bán hàng</p>
            <div class="mt-1 d-flex justify-content-center">
              <div class="digital-seal border border-2 border-success p-1 text-start rounded bg-white position-relative" style="width: 240px; color: #158548;">
                  <div class="fw-bold text-uppercase" style="font-size: 9px; line-height: 1.2;">ĐƯỢC KÝ BỞI CÔNG TY TNHH RẠP CHIẾU PHIM CINEOPS VIỆT NAM</div>
                  <div class="mt-1 fw-bold" style="font-size: 9px;">Ngày ký: {{ currentFullDate }}</div>
                  <div class="position-absolute" style="bottom: 2px; right: 5px;">
                    <i class="bi bi-check-lg" style="font-size: 20px; font-weight: 900;"></i>
                  </div>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-auto pt-2 w-100 position-relative z-1">
           <div class="small text-muted text-center" style="font-size: 10px;">
              <span style="font-size: 9px; color: #ccc;">(Đơn vị cung cấp giải pháp: Hệ thống CineOps - MST: 0106026495 - SĐT: 0901 80 16 18)</span>
           </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { numberToWordsVietnamese } from '@/utils/formatters';

const invoice = ref({});
const details = ref([]);

onMounted(async () => {
  const savedInvoice = localStorage.getItem('printInvoice');
  const savedDetails = localStorage.getItem('printInvoiceDetails');

  if (savedInvoice) invoice.value = JSON.parse(savedInvoice);
  if (savedDetails) details.value = JSON.parse(savedDetails);

  await nextTick();
  setTimeout(() => {
    window.print();
    window.close(); // Auto close print window after printing
  }, 500); 
});

const formatPrice = (v) => {
  if (v === null || v === undefined) return '0';
  return new Intl.NumberFormat('vi-VN').format(v);
};

const dateInfo = computed(() => {
  const dateToUse = invoice.value?.ngayTao ? new Date(invoice.value.ngayTao) : new Date();
  return {
    day: dateToUse.getDate().toString().padStart(2, '0'),
    month: (dateToUse.getMonth() + 1).toString().padStart(2, '0'),
    year: dateToUse.getFullYear(),
    fullDate: `${dateToUse.getDate().toString().padStart(2, '0')}/${(dateToUse.getMonth() + 1).toString().padStart(2, '0')}/${dateToUse.getFullYear()}`
  };
});

const currentDay = computed(() => dateInfo.value.day);
const currentMonth = computed(() => dateInfo.value.month);
const currentYear = computed(() => dateInfo.value.year);
const currentFullDate = computed(() => dateInfo.value.fullDate);

const amountInWordsText = computed(() => {
  const total = invoice.value?.tongTienThanhToan || 0;
  return typeof numberToWordsVietnamese === 'function' ? numberToWordsVietnamese(total) : '';
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Arial:wght@400;700&display=swap');

.vat-invoice-wrapper {
  background-color: #e2e8f0;
  min-height: 100vh;
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

.invoice-outer-border {
  width: 210mm;
  height: 297mm; /* Kích thước chuẩn A4 */
  background: white;
  padding: 6px; 
  border: 4px solid #5bc0de;
  outline: 1px solid #5bc0de;
  outline-offset: -6px; 
  box-shadow: 0 5px 15px rgba(0,0,0,0.15);
  box-sizing: border-box;
  overflow: hidden;
}

/* Đổi thành flex-column để căn footer tự nhiên xuống đáy */
.invoice-container {
  width: 100%;
  height: 100%;
  padding: 6mm 10mm 6mm 10mm; /* Bóp padding trên dưới lại */
  color: #000;
  font-family: 'Arial', sans-serif; 
  box-sizing: border-box;
}

/* Watermark */
.watermark-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 350px;
  height: 350px;
  border-radius: 50%;
  border: 30px dashed rgba(230, 235, 240, 0.6);
  z-index: 0;
  pointer-events: none;
}

/* Typography & Spacing */
.tracking-wide { letter-spacing: 2px; }
.horizontal-dotted-line { border-bottom: 2px dotted #aaa; width: 100%; }
.tiny { font-size: 11px; }

.info-line { display: flex; align-items: baseline; font-size: 12px; }
.info-line .label { min-width: 75px; }

.dotted-line-row {
  display: flex;
  align-items: flex-end;
  margin-bottom: 6px;
  width: 100%;
}
.dotted-line-row .label-box {
  flex-shrink: 0;
  width: auto;
  white-space: nowrap;
  padding-right: 5px;
}
.dotted-line-row .value-box {
  flex-grow: 1;
  border-bottom: 1px dotted #ccc; 
  padding-bottom: 1px;
  min-height: 16px;
}

/* Table */
.table-custom {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #000;
  background-color: transparent;
}
.table-custom th, .table-custom td {
  border: 1px solid #000;
  padding: 4px 6px; /* Giảm padding ô */
  font-size: 12px;
}
.table-custom thead th {
  font-weight: bold;
}
.table-custom tbody tr td {
  border-top: none !important;
  border-bottom: none !important;
}
.table-custom tbody {
  border-bottom: 1px solid #000;
}
.empty-row td {
  height: 20px; /* Bóp chiều cao dòng trống */
}

/* Footer Table Border */
.border-end-0 { border-right: none !important; }
.border-start-0 { border-left: none !important; }
.border-start-1 { border-left: 1px solid #000 !important; }
.border-top-0 { border-top: none !important; }
.border-bottom-0 { border-bottom: none !important; }

/* In ấn */
@media print {
  @page { size: A4 portrait; margin: 0; }
  body { 
    -webkit-print-color-adjust: exact; 
    print-color-adjust: exact; 
    background-color: white; 
    margin: 0; padding: 0; 
  }
  .vat-invoice-wrapper { background-color: transparent; padding: 0; display: block; }
  .invoice-outer-border { 
      border: 4px solid #5bc0de; 
      outline: 1px solid #5bc0de; 
      box-shadow: none; margin: 0; page-break-after: avoid; 
  }
}
</style>