<template>
  <div class="cccd-scanner-wrapper">
    <!-- Nút mở Scanner -->
    <el-button 
      class="btn-cine-primary"
      @click="openScanner"
    >
      <i class="bi bi-qr-code-scan me-2"></i> Quét mã CCCD
    </el-button>

    <!-- Dialog chức năng Scan -->
    <el-dialog
      v-model="dialogVisible"
      title="Quét Mã QR Thẻ Căn Cước Công Dân"
      width="600px"
      class="premium-dialog scanner-dialog"
      :close-on-click-modal="false"
      destroy-on-close
      @close="stopCamera"
    >
      <!-- Vùng hiển thị Camera -->
      <div class="camera-container" v-loading="processing" element-loading-text="Tối ưu hóa Engine CV...">
        <div class="video-wrapper">
          <video ref="videoElement" autoplay playsinline muted></video>
          
          <!-- Lớp phủ hướng dẫn -->
          <div class="overlay-guide">
            <!-- Stats Overlay (Nerd Mode) -->
            <div class="cv-stats">
                <div class="stat-item">
                    <span class="label">FPS:</span>
                    <span class="value" :class="stats.fps < 20 ? 'text-warning' : 'text-success'">{{ stats.fps }}</span>
                </div>
                <div class="stat-item">
                    <span class="label">SHARP:</span>
                    <span class="value" :class="stats.sharpScore < 30 ? 'text-danger' : 'text-success'">{{ stats.sharpScore }}</span>
                </div>
            </div>

            <div class="guide-frame">
              <div class="corner top-left"></div>
              <div class="corner top-right"></div>
              <div class="corner bottom-left"></div>
              <div class="corner bottom-right"></div>
              <!-- Thanh quét hiệu ứng -->
              <div class="scan-line"></div>
            </div>
            
            <div class="feedback-container">
                <p class="guide-text" :class="{ 'pulse-alert': stats.sharpScore < 30 }">
                    <i class="bi" :class="stats.sharpScore < 30 ? 'bi-camera' : 'bi-check-circle'"></i>
                    {{ stats.sharpScore < 30 ? 'Đang lấy nét... Vui lòng giữ yên thẻ' : 'Đang giải mã QR thời gian thực...' }}
                </p>
            </div>
          </div>
        </div>

        <!-- Canvas ẩn dùng để xử lý CV -->
        <canvas ref="canvasElement" style="display: none;"></canvas>
      </div>

      <!-- Action Buttons -->
      <template #footer>
        <div class="d-flex justify-content-between align-items-center w-100">
          <div class="status-msg" :class="{ 'text-danger': !!errorMsg, 'text-success': isSuccess }">
            {{ errorMsg || statusMsg }}
          </div>
          <div class="d-flex gap-2">
            <el-button 
              v-if="hasTorch"
              :type="torchActive ? 'warning' : 'info'" 
              circle
              @click="toggleTorch"
            >
              <i class="bi" :class="torchActive ? 'bi-lightbulb-fill' : 'bi-lightbulb'"></i>
            </el-button>
            <el-button @click="closeScanner" class="btn-cine-secondary">Hủy</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount, onMounted } from 'vue';
import { Camera } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import axios from '@/services/axios';

const emit = defineEmits(['scanned']);

// ── State ─────────────────────────────────────────────────────────────────────
const dialogVisible = ref(false);
const videoElement = ref(null);
const canvasElement = ref(null);
const cameraActive = ref(false);
const processing = ref(false);
const statusMsg = ref('');
const errorMsg = ref('');
const isSuccess = ref(false);
const stats = ref({ fps: 0, sharpScore: 0 });
const hasTorch = ref(false);
const torchActive = ref(false);

let stream = null;
let animationId = null;
let scanWorker = null;
let isBeProcessing = false;
let frameCount = 0;
let lastFpsTime = Date.now();
let globalFrameCounter = 0;

// Persistent Canvases for re-use (Performance optimization)
let analysisCanvas = null;
let sendCanvas = null;

// ── Web Worker Initialization ───────────────────────────────────────────────
const initWorker = () => {
    try {
        // Sử dụng đường dẫn tương đối chuẩn cho Vite/Webpack
        scanWorker = new Worker(new URL('../../services/workers/scanWorker.js', import.meta.url));
        scanWorker.onmessage = (e) => {
            stats.value.sharpScore = Math.floor(e.data.score);
            if (e.data.isSharp && !isBeProcessing) {
                sendFrameToBE();
            }
        };
    } catch (err) {
        console.error("Worker Load Failed, falling back to Inline logic");
    }
};

// ── Torch Support ────────────────────────────────────────────────────────────
const toggleTorch = async () => {
    if (!stream) return;
    const track = stream.getVideoTracks()[0];
    const capabilities = track.getCapabilities();
    if (capabilities.torch) {
        torchActive.value = !torchActive.value;
        await track.applyConstraints({
            advanced: [{ torch: torchActive.value }]
        });
    }
};

// Consensus Logic (3 identical results required)
let resultHistory = [];
const CONSENSUS_REQUIRED = 2; // Need 2 identical matches for 100% accuracy

// ── Image Processing Loop (30 FPS) ───────────────────────────────────────────
const startProcessLoop = () => {
    if (animationId) cancelAnimationFrame(animationId);
    
    const loop = () => {
        if (!cameraActive.value || !videoElement.value) return;
        
        frameCount++;
        if (Date.now() - lastFpsTime >= 1000) {
            stats.value.fps = frameCount;
            frameCount = 0;
            lastFpsTime = Date.now();
        }

        // Thực hiện phân tích chất lượng ảnh và quét
        captureAndAnalyzeFrame();
        
        animationId = requestAnimationFrame(loop);
    };
    animationId = requestAnimationFrame(loop);
};

const captureAndAnalyzeFrame = () => {
    const video = videoElement.value;
    if (video.readyState !== video.HAVE_ENOUGH_DATA) return;

    if (!analysisCanvas) analysisCanvas = document.createElement('canvas');
    const ctx = analysisCanvas.getContext('2d', { alpha: false });
    
    const analysisSize = 120; // Tăng độ phân giải phân tích một chút
    analysisCanvas.width = analysisSize;
    analysisCanvas.height = analysisSize;

    const minDim = Math.min(video.videoWidth, video.videoHeight);
    const ROI_SIZE = minDim * 0.5;
    const sx = (video.videoWidth - ROI_SIZE) / 2;
    const sy = (video.videoHeight - ROI_SIZE) / 2;
    
    ctx.drawImage(video, sx, sy, ROI_SIZE, ROI_SIZE, 0, 0, analysisSize, analysisSize);
    const imageData = ctx.getImageData(0, 0, analysisSize, analysisSize);

    if (scanWorker) {
        // Gửi sang Worker để xử lý song song (0% Lag Main Thread)
        scanWorker.postMessage({ 
            imageData, 
            width: analysisSize, 
            height: analysisSize,
            threshold: 25 
        });
    } else {
        // Fallback sang Inline nếu Worker lỗi
        const score = calculateInlineBlurScore(imageData.data, analysisSize, analysisSize);
        stats.value.sharpScore = Math.floor(score);
        if (score > 25 && !isBeProcessing) sendFrameToBE();
    }
};

const calculateInlineBlurScore = (data, width, height) => {
    let laplacianSum = 0;
    let pixelCount = 0;
    for (let y = 1; y < height - 1; y++) {
        for (let x = 1; x < width - 1; x++) {
            const idx = (y * width + x) * 4;
            const center = (data[idx] + data[idx+1] + data[idx+2]) / 3;
            const top = (data[((y-1)*width+x)*4] + data[((y-1)*width+x)*4+1] + data[((y-1)*width+x)*4+2]) / 3;
            const bottom = (data[((y+1)*width+x)*4] + data[((y+1)*width+x)*4+1] + data[((y+1)*width+x)*4+2]) / 3;
            const left = (data[(y*width+(x-1))*4] + data[(y*width+(x-1))*4+1] + data[(y*width+(x-1))*4+2]) / 3;
            const right = (data[(y*width+(x+1))*4] + data[(y*width+(x+1))*4+1] + data[(y*width+(x+1))*4+2]) / 3;
            const laplacian = -4 * center + top + bottom + left + right;
            laplacianSum += laplacian * laplacian;
            pixelCount++;
        }
    }
    return pixelCount > 0 ? (laplacianSum / pixelCount) : 0;
};

const sendFrameToBE = async () => {
    isBeProcessing = true;
    try {
        const video = videoElement.value;
        if (!video) return;

        // Quản lý Session cho bộ quét liên tục
        if (!window._scanSessionId) {
            window._scanSessionId = 'vue-scan-' + Date.now();
        }

        if (!sendCanvas) sendCanvas = document.createElement('canvas');
        const ctx = sendCanvas.getContext('2d');
        
        const videoWidth = video.videoWidth;
        const videoHeight = video.videoHeight;
        const minDim = Math.min(videoWidth, videoHeight);
        
        globalFrameCounter++;
        let scanMode = "";

        if (globalFrameCounter % 2 === 0) {
            scanMode = "Toàn cảnh";
            sendCanvas.width = 800; // Giảm từ 1000 xuống 800 (Siêu nhẹ)
            sendCanvas.height = (videoHeight / videoWidth) * 800;
            ctx.drawImage(video, 0, 0, sendCanvas.width, sendCanvas.height);
        } else {
            scanMode = "Soi cận";
            const cropSize = minDim * 0.60; 
            const sx = (videoWidth - cropSize) / 2;
            const sy = (videoHeight - cropSize) / 2;
            sendCanvas.width = 640; // Giảm từ 800 xuống 640
            sendCanvas.height = 640;
            ctx.drawImage(video, sx, sy, cropSize, cropSize, 0, 0, 640, 640);
        }

        // CHIẾN THUẬT NÉN: Giảm chất lượng xuống 0.6 để tốc độ gửi đạt mức Real-time
        let mimeType = 'image/jpeg';
        let quality = 0.6; 
        
        if (globalFrameCounter % 5 === 0) {
            mimeType = 'image/png';
            scanMode += " [PNG]";
        }

        statusMsg.value = `Máy quét: [${scanMode}] ...`;

        const blob = await new Promise(resolve => sendCanvas.toBlob(resolve, mimeType, quality));
        const formData = new FormData();
        formData.append('file', blob, 'best_frame.jpg');
        formData.append('sessionId', window._scanSessionId);

        // Đổi sang endpoint quản lý liên tục
        const response = await axios.post('/api/v1/scanner/scan-continuous', formData);
        
        // Cấu trúc response của scan-continuous bọc trong data.scanData
        const resultData = response.data?.data?.scanData || response.data?.data;
        handleBeResult(resultData);
    } catch (err) {
        // console.error('Scan Error:', err);
    } finally {
        // Giảm thời gian nghỉ xuống mức tối đa để máy quét liên tục
        setTimeout(() => { isBeProcessing = false; }, 30);
    }
};

// ── Consensus Logic (Filter Noise) ───────────────────────────────────────────
const handleBeResult = (data) => {
    if (!data || !data.cccd) return;

    // Compare with history to ensure 100% accuracy
    const lastResult = resultHistory[resultHistory.length - 1];
    
    if (lastResult && lastResult.cccd === data.cccd) {
        resultHistory.push(data);
    } else {
        resultHistory = [data]; // Reset if mismatch (noisy frame)
    }

    if (resultHistory.length >= CONSENSUS_REQUIRED) {
        finalizeScan(data);
    } else {
        statusMsg.value = `Đang xác thực bảo mật... (${resultHistory.length}/${CONSENSUS_REQUIRED})`;
    }
};

const finalizeScan = (data) => {
    stopCamera();
    isSuccess.value = true;
    statusMsg.value = 'Xác thực thành công!';
    ElMessage.success('Đã quét thành công thẻ CCCD');
    emit('scanned', data);
    closeScanner();
};

// ── Lifecycle & Camera ────────────────────────────────────────────────────────
const initCamera = async () => {
    isSuccess.value = false;
    errorMsg.value = '';
    processing.value = true;
    resultHistory = [];

    try {
        stream = await navigator.mediaDevices.getUserMedia({
            video: { 
                facingMode: 'environment',
                width: { ideal: 1920 }, // High-res for QR
                height: { ideal: 1080 },
                frameRate: { ideal: 30 }
            }
        });
        
        const track = stream.getVideoTracks()[0];
        const capabilities = track.getCapabilities();
        hasTorch.value = !!capabilities.torch;
        
        if (videoElement.value) {
            videoElement.value.srcObject = stream;
            cameraActive.value = true;
            processing.value = false;
            statusMsg.value = 'Đang phát hiện mã QR...';
            initWorker();
            startProcessLoop();
        }
    } catch (err) {
        errorMsg.value = 'Không thể bật camera.';
        processing.value = false;
    }
};

const stopCamera = () => {
    if (animationId) cancelAnimationFrame(animationId);
    if (stream) stream.getTracks().forEach(t => t.stop());
    stream = null;
    cameraActive.value = false;
    isBeProcessing = false;
};

const openScanner = () => { dialogVisible.value = true; initCamera(); };
const closeScanner = () => { dialogVisible.value = false; stopCamera(); };

onBeforeUnmount(stopCamera);
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
  background: rgba(0, 0, 0, 0.4); 
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

/* Khung vuông để quét QR */
.guide-frame {
  width: 250px;
  height: 250px;
  border: 1px dashed rgba(255, 255, 255, 0.4);
  position: relative;
  background: transparent;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.6); 
  border-radius: 12px;
  margin-bottom: 20px;
  overflow: hidden;
}

/* Các góc màn hình quét (Corner brackets) */
.corner {
  position: absolute;
  width: 40px;
  height: 40px;
  border: 4px solid #00d2d3;
}

.top-left { top: -2px; left: -2px; border-right: none; border-bottom: none; border-top-left-radius: 12px; }
.top-right { top: -2px; right: -2px; border-left: none; border-bottom: none; border-top-right-radius: 12px; }
.bottom-left { bottom: -2px; left: -2px; border-right: none; border-top: none; border-bottom-left-radius: 12px;}
.bottom-right { bottom: -2px; right: -2px; border-left: none; border-top: none; border-bottom-right-radius: 12px; }

/* Hiệu ứng quét vạch laser (Scan line) */
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: #00d2d3;
  box-shadow: 0 0 10px #00d2d3, 0 0 20px #00d2d3;
  animation: scan 2s infinite linear;
}

@keyframes scan {
  0% { top: 0; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

.guide-text {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  text-shadow: 0 1px 3px rgba(0,0,0,0.8);
  background: rgba(0,0,0,0.6);
  padding: 8px 16px;
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
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.text-success {
  color: #28a745 !important;
}
</style>
