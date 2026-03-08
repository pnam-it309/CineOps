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

let stream = null;
let animationId = null;
let scanWorker = null;
let isBeProcessing = false;
let frameCount = 0;
let lastFpsTime = Date.now();

// Consensus Logic (3 identical results required)
let resultHistory = [];
const CONSENSUS_REQUIRED = 2; // Need 2 identical matches for 100% accuracy

// ── Image Processing Loop (30 FPS) ───────────────────────────────────────────
const startProcessLoop = () => {
    if (animationId) cancelAnimationFrame(animationId);
    
    // Initialize Worker
    if (!scanWorker) {
        scanWorker = new Worker(new URL('@/workers/scanWorker.js', import.meta.url), { type: 'module' });
        scanWorker.onmessage = handleWorkerMessage;
    }
    
    const loop = () => {
        if (!cameraActive.value || !videoElement.value) return;
        
        frameCount++;
        if (Date.now() - lastFpsTime >= 1000) {
            stats.value.fps = frameCount;
            frameCount = 0;
            lastFpsTime = Date.now();
        }

        // 1. Capture Frame to hidden Canvas
        captureFrameToDetectBlur();
        
        animationId = requestAnimationFrame(loop);
    };
    animationId = requestAnimationFrame(loop);
};

const captureFrameToDetectBlur = () => {
    const video = videoElement.value;
    if (video.readyState !== video.HAVE_ENOUGH_DATA) return;

    const canvas = canvasElement.value;
    const ctx = canvas.getContext('2d', { willReadFrequently: true });
    
    // Resize for efficient CV processing (Scan-ROI only)
    const ROI_SIZE = 400; 
    canvas.width = ROI_SIZE;
    canvas.height = ROI_SIZE;

    // Center Crop (Focus on the QR area in the frame)
    const sx = (video.videoWidth - ROI_SIZE) / 2;
    const sy = (video.videoHeight - ROI_SIZE) / 2;
    
    ctx.drawImage(video, sx, sy, ROI_SIZE, ROI_SIZE, 0, 0, ROI_SIZE, ROI_SIZE);
    
    // Send to Worker for LAPLACIAN Blur Detection
    const imageData = ctx.getImageData(0, 0, ROI_SIZE, ROI_SIZE);
    scanWorker.postMessage({ imageData, width: ROI_SIZE, height: ROI_SIZE, threshold: 30 });
};

// ── Worker Response (Producer/Consumer Result) ────────────────────────────────
const handleWorkerMessage = (e) => {
    const { isSharp, score } = e.data;
    stats.value.sharpScore = Math.floor(score);
    
    // 2. Only send to Backend if frame is sharp AND BE is not busy
    if (isSharp && !isBeProcessing) {
        sendFrameToBE();
    }
};

const sendFrameToBE = async () => {
    isBeProcessing = true;
    try {
        const video = videoElement.value;
        const canvas = document.createElement('canvas'); // Temp high-res canvas
        const ctx = canvas.getContext('2d');
        
        // Dynamic Resizing: Upscale small modules for better detection
        canvas.width = 1000;
        canvas.height = (video.videoHeight / video.videoWidth) * 1000;
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

        const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg', 0.9));
        const formData = new FormData();
        formData.append('file', blob, 'best_frame.jpg');

        const response = await axios.post('/api/v1/common/scan/cccd', formData);
        handleBeResult(response.data?.data);
    } catch (err) {
        if (err.response?.status === 400) {
            console.error('CCCD Scan Error:', err.response.data?.message || err.message);
        }
    } finally {
        // Throttle BE calls slightly even if fast
        setTimeout(() => { isBeProcessing = false; }, 200);
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
        
        if (videoElement.value) {
            videoElement.value.srcObject = stream;
            cameraActive.value = true;
            processing.value = false;
            statusMsg.value = 'Đang phát hiện mã QR...';
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
