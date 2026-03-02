<template>
  <div class="cccd-scanner-wrapper">
    <!-- Nút mở Scanner -->
    <el-button 
      type="primary" 
      plain 
      :icon="Camera" 
      @click="openScanner"
      class="btn-scanner"
    >
      <i class="bi bi-person-video me-2"></i> Quét thẻ CCCD
    </el-button>

    <!-- Dialog chức năng Scan -->
    <el-dialog
      v-model="dialogVisible"
      title="Quét Thẻ Căn Cước Công Dân"
      width="600px"
      class="premium-dialog scanner-dialog"
      :close-on-click-modal="false"
      destroy-on-close
      @close="stopCamera"
    >
      <!-- Vùng hiển thị Camera -->
      <div class="camera-container" v-loading="processing" element-loading-text="Đang xử lý hình ảnh và trích xuất dữ liệu, vui lòng đợi...">
        <div class="video-wrapper">
          <video ref="videoElement" autoplay playsinline></video>
          
          <!-- Lớp phủ hướng dẫn -->
          <div class="overlay-guide">
            <div class="guide-frame">
              <div class="corner top-left"></div>
              <div class="corner top-right"></div>
              <div class="corner bottom-left"></div>
              <div class="corner bottom-right"></div>
            </div>
            <p class="guide-text bounce-anim">
              <i class="bi bi-info-circle me-1"></i>
              Vui lòng đặt CCCD vào đúng khung hình
            </p>
          </div>
        </div>

        <!-- Canvas ẩn dùng để xử lý ảnh -->
        <canvas ref="canvasElement" style="display: none;"></canvas>
      </div>

      <!-- Action Buttons -->
      <template #footer>
        <div class="d-flex justify-content-between align-items-center w-100">
          <div class="status-msg" :class="{ 'text-danger': !!errorMsg }">
            {{ errorMsg || statusMsg }}
          </div>
          <div class="d-flex gap-2">
            <el-button @click="closeScanner" class="btn-premium-secondary" :disabled="processing"></el-button>
            <el-button 
              v-if="cameraActive" 
              type="primary" 
              @click="captureAndProcess" 
              class="btn-premium-primary"
              :loading="processing"
            >
              Chụp & Xử lý
            </el-button>
            <el-button 
              v-else 
              type="warning" 
              @click="initCamera" 
              class="btn-premium-primary"
              :disabled="processing"
            >
              Thử Lại (Reset)
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue';
import { Camera } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import Tesseract from 'tesseract.js';

const emit = defineEmits(['scanned']);

// State
const dialogVisible = ref(false);
const videoElement = ref(null);
const canvasElement = ref(null);
const cameraActive = ref(false);
const processing = ref(false);
const statusMsg = ref('');
const errorMsg = ref('');
let stream = null;

// Khởi chạy camera
const initCamera = async () => {
  errorMsg.value = '';
  statusMsg.value = 'Đang kết nối camera...';
  processing.value = false;
  
  try {
    if (stream) stopCamera();

    stream = await navigator.mediaDevices.getUserMedia({ 
      video: { 
        facingMode: 'environment', // Ưu tiên camera sau nếu có
        width: { ideal: 1920 },
        height: { ideal: 1080 }
      } 
    });
    
    if (videoElement.value) {
      videoElement.value.srcObject = stream;
      videoElement.value.play();
      cameraActive.value = true;
      statusMsg.value = 'Camera đã sẵn sàng. Vui lòng căn chỉnh thẻ.';
    }
  } catch (err) {
    console.error('Lỗi truy cập camera:', err);
    errorMsg.value = 'Không thể truy cập camera. Vui lòng kiểm tra quyền!';
    ElMessage.error('Không thể truy cập camera');
  }
};

// Dừng camera
const stopCamera = () => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop());
    stream = null;
  }
  cameraActive.value = false;
  if (videoElement.value) {
    videoElement.value.srcObject = null;
  }
};

const openScanner = () => {
  dialogVisible.value = true;
  initCamera();
};

const closeScanner = () => {
  dialogVisible.value = false;
  stopCamera();
};

// Xử lý ảnh: Chuyển sang trắng đen và tăng độ tương phản
const applyImageProcessing = (canvas, ctx, width, height) => {
  const imageData = ctx.getImageData(0, 0, width, height);
  const data = imageData.data;
  
  // Áp dụng Grayscale và Contrast (Công thức cơ bản)
  const contrast = 1.5; // Tăng phản quang 50%
  const intercept = 128 * (1 - contrast);

  for (let i = 0; i < data.length; i += 4) {
    const r = data[i];
    const g = data[i + 1];
    const b = data[i + 2];
    
    // Grayscale (Tỷ lệ nhạy cảm ánh sáng của mắt)
    let gray = 0.299 * r + 0.587 * g + 0.114 * b;
    
    // Contrast
    gray = gray * contrast + intercept;
    
    // Cắt gọt về khoảng [0, 255]
    if (gray > 255) gray = 255;
    if (gray < 0) gray = 0;

    data[i] = gray;
    data[i + 1] = gray;
    data[i + 2] = gray;
    // data[i+3] là Alpha, không đổi
  }
  
  ctx.putImageData(imageData, 0, 0);
};

// Chụp và OCR
const captureAndProcess = async () => {
  if (!videoElement.value || !canvasElement.value) return;

  processing.value = true;
  statusMsg.value = 'Đang xử lý hình ảnh...';
  errorMsg.value = '';

  try {
    const video = videoElement.value;
    const canvas = canvasElement.value;
    const ctx = canvas.getContext('2d');

    // Thiết lập kích thước canvas bằng kích thước video thực
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    // Vẽ toàn bộ frame hiện tại lên Canvas
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    // Xử lý tiền kỳ ảnh (Trắng đen, Tăng sáng) để OCR đọc tốt hơn trên camera laptop
    applyImageProcessing(canvas, ctx, canvas.width, canvas.height);

    // Convert sang Base64 Image
    const imageDataUrl = canvas.toDataURL('image/jpeg');

    // Dừng camera (Snapshot mode)
    stopCamera();
    
    statusMsg.value = 'Đang nhận dạng chữ (OCR)...';

    // Bắt đầu Tesseract OCR (ngôn ngữ Tiếng Việt + Tiếng Anh)
    const result = await Tesseract.recognize(
      imageDataUrl,
      'vie+eng',
      { 
        logger: m => {
          if (m.status === 'recognizing text') {
            statusMsg.value = `Đang phân tích: ${Math.round(m.progress * 100)}%`;
          }
        }
      }
    );

    const text = result.data.text;
    console.log("OCR Result Text:\n", text);

    // Rút trích dữ liệu bằng Regex
    const extractedData = extractCCCDData(text);

    if (extractedData.cccd || extractedData.name || extractedData.dob) {
      ElMessage.success('Quét dữ liệu thành công!');
      emit('scanned', extractedData);
      closeScanner();
    } else {
      errorMsg.value = 'Không tìm thấy thông tin hợp lệ. Vui lòng thử lại!';
    }

  } catch (error) {
    console.error('OCR Error:', error);
    errorMsg.value = 'Có lỗi trong quá trình quét. Vui lòng thử lại!';
    ElMessage.error(errorMsg.value);
  } finally {
    processing.value = false;
  }
};

// Regex trích xuất thông tin
const extractCCCDData = (text) => {
  const lines = text.split('\n').map(l => l.trim()).filter(l => l.length > 0);
  const data = {
    cccd: '',
    name: '',
    dob: ''
  };

  // Nối chuỗi để regex hoạt động tốt hơn với form có ký tự xuống dòng
  const fullText = lines.join(' ');

  // 1. Tìm CCCD (Chuỗi 12 chữ số)
  const cccdMatch = fullText.match(/\b\d{12}\b/);
  if (cccdMatch) {
    data.cccd = cccdMatch[0];
  }

  // 2. Tìm Ngày sinh (Định dạng DD/MM/YYYY hoặc tương tự)
  const dobMatch = fullText.match(/\b(\d{2})[\/\-\.](\d{2})[\/\-\.](\d{4})\b/);
  if (dobMatch) {
    data.dob = `${dobMatch[3]}-${dobMatch[2]}-${dobMatch[1]}`; // Convert to YYYY-MM-DD
  }

  // 3. Tìm Tên (Dựa theo dòng chữ IN HOA sau cụm Họ và tên/Họ tên)
  // Tool trên laptop thư viện JS OCR thường sai sót, nên bắt tạm logic: 
  // Dòng nào có toàn chữ in hoa (không dấu gạch dưới, không có số) khả năng cao là tên
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    // Tìm cụm báo hiệu tên
    if (line.toLowerCase().includes('họ và tên') || line.toLowerCase().includes('ho và ten') || line.toLowerCase().includes('full name')) {
        // Tên thường nằm ngay cạnh hoặc ở dòng tiếp theo
        let nameCandidate = line.replace(/.*họ và tên[\s:]*/i, '').trim();
        if(nameCandidate.length < 3 && i + 1 < lines.length) {
            nameCandidate = lines[i+1].trim();
        }
        // Chuẩn hóa: lọc bỏ các ký tự đặc biệt lọt vào
        data.name = nameCandidate.replace(/[^a-zA-ZÀ-Ỹà-ỹ\s]/g, '').trim().toUpperCase();
        break;
    }
  }

  // Fallback tìm tên thủ công: Tìm dòng chỉ toàn chữ viết hoa (kể cả tiếng Việt)
  if (!data.name) {
    const nameRegex = /^[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪỬỮỰỲỴÝỶỸ\s]{5,}$/;
     for (let line of lines) {
        // Trừ đi một số dòng in hoa mặc định của form
        if (line.includes('CỘNG HÒA') || line.includes('ĐỘC LẬP') || line.includes('CĂN CƯỚC')) continue;
        if (nameRegex.test(line)) {
            data.name = line;
            break;
        }
     }
  }

  return data;
};

// Clean up
onBeforeUnmount(() => {
  stopCamera();
});
</script>

<style scoped>
.btn-scanner {
  font-weight: 500;
  border-radius: 8px;
}

.camera-container {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 350px;
}

.video-wrapper {
  position: relative;
  width: 100%;
  height: auto;
  aspect-ratio: 4/3;
}

video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Lớp phủ Khung CCCD */
.overlay-guide {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4); /* Mask mờ xung quanh */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

/* Khoảng trống trong suốt có khung giới hạn */
.guide-frame {
  width: 80%;
  height: 55%;
  border: 1px dashed rgba(255, 255, 255, 0.5);
  position: relative;
  background: transparent;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5); /* Tạo hiệu ứng tối xung quanh */
  border-radius: 8px;
  margin-bottom: 20px;
}

/* Các góc màn hình quét (Corner brackets) */
.corner {
  position: absolute;
  width: 30px;
  height: 30px;
  border: 3px solid #00d2d3; /* Đổi màu xanh premium */
}

.top-left { top: -2px; left: -2px; border-right: none; border-bottom: none; border-top-left-radius: 8px; }
.top-right { top: -2px; right: -2px; border-left: none; border-bottom: none; border-top-right-radius: 8px; }
.bottom-left { bottom: -2px; left: -2px; border-right: none; border-top: none; border-bottom-left-radius: 8px;}
.bottom-right { bottom: -2px; right: -2px; border-left: none; border-top: none; border-bottom-right-radius: 8px; }

.guide-text {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  text-shadow: 0 1px 3px rgba(0,0,0,0.8);
  background: rgba(0,0,0,0.6);
  padding: 6px 16px;
  border-radius: 20px;
  z-index: 10;
}

.bounce-anim {
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.status-msg {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}
</style>
