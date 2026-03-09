/**
 * Hệ Thống Quét CCCD Liên Tục (Continuous Scanning System)
 * Bọc bên ngoài API hiện tại mà không sửa đổi Backend
 */

class ContinuousScanner {
    constructor() {
        this.video = document.getElementById('video');
        this.canvas = document.createElement('canvas');
        this.ctx = this.canvas.getContext('2d');
        
        this.isScanning = false;
        this.currentStream = null;
        this.currentCameraId = null;
        this.cameras = [];
        
        this.scanInterval = null;
        this.scanRate = 500; // 2 frame/giây (Tăng tốc để bắt mã QR 1cm)
        this.attemptCount = 0;
        this.maxRetries = 200; // Tăng số lần thử cho quét liên tục
        this.sessionId = null;
        
        this.backendUrl = '/api/v1/scanner/scan-continuous';
        this.scanTimeout = null;
        this.frameCounter = 0;
        
        // Audio elements
        this.successSound = document.getElementById('successSound');
        
        // UI elements
        this.startBtn = document.getElementById('startBtn');
        this.stopBtn = document.getElementById('stopBtn');
        this.switchCameraBtn = document.getElementById('switchCameraBtn');
        this.cameraStatus = document.getElementById('cameraStatus');
        this.imageQuality = document.getElementById('imageQuality');
        this.attemptCountEl = document.getElementById('attemptCount');
        this.scanRateEl = document.getElementById('scanRate');
        this.errorMessage = document.getElementById('errorMessage');
        this.loadingSpinner = document.getElementById('loadingSpinner');
        this.resultPanel = document.getElementById('resultPanel');
        this.resultContent = document.getElementById('resultContent');
        this.successIndicator = document.getElementById('successIndicator');
        
        this.init();
    }
    
    async init() {
        this.setupEventListeners();
        await this.getCameras();
        this.updateStatus('cameraStatus', 'Sẵn sàng', 'good');
        this.updateStatus('imageQuality', 'Chờ quét...', 'neutral');
    }
    
    setupEventListeners() {
        this.startBtn.addEventListener('click', () => this.startScanning());
        this.stopBtn.addEventListener('click', () => this.stopScanning());
        this.switchCameraBtn.addEventListener('click', () => this.switchCamera());
        
        // Vibrate on success (if supported)
        if ('vibrate' in navigator) {
            this.vibratePattern = [200, 100, 200];
        }
    }
    
    async getCameras() {
        try {
            const devices = await navigator.mediaDevices.enumerateDevices();
            this.cameras = devices.filter(device => device.kind === 'videoinput');
            
            if (this.cameras.length === 0) {
                throw new Error('Không tìm thấy camera nào trên thiết bị');
            }
            
            // Ưu tiên camera sau (selfie camera) cho việc quét CCCD
            this.currentCameraId = this.cameras.length > 1 ? this.cameras[1].deviceId : this.cameras[0].deviceId;
            
            if (this.cameras.length <= 1) {
                this.switchCameraBtn.disabled = true;
            }
        } catch (error) {
            this.showError('Không thể truy cập camera: ' + error.message);
        }
    }
    
    async startCamera() {
        try {
            const constraints = {
                video: {
                    deviceId: this.currentCameraId ? { exact: this.currentCameraId } : undefined,
                    facingMode: 'environment',
                    width: { ideal: 1280 },
                    height: { ideal: 720 },
                    focusMode: 'continuous' // Yêu cầu auto-focus nếu hỗ trợ
                },
                audio: false
            };
            
            this.currentStream = await navigator.mediaDevices.getUserMedia(constraints);
            this.video.srcObject = this.currentStream;
            
            // Chờ video load xong
            await new Promise(resolve => {
                this.video.onloadedmetadata = resolve;
            });
            
            this.updateStatus('cameraStatus', 'Đang hoạt động', 'good');
        } catch (error) {
            throw new Error('Không thể khởi động camera: ' + error.message);
        }
    }
    
    async startScanning() {
        try {
            await this.startCamera();
            
            this.isScanning = true;
            this.attemptCount = 0;
            
            // Cập nhật UI
            this.startBtn.disabled = true;
            this.stopBtn.disabled = false;
            this.switchCameraBtn.disabled = true;
            this.hideError();
            this.resultPanel.classList.remove('show');
            
            // Khởi động luồng quét đệ quy (Thay cho setInterval)
            this.scanLoop();
            
            this.updateStatus('imageQuality', 'Đang quét...', 'neutral');
            
        } catch (error) {
            this.showError(error.message);
        }
    }
    
    async scanLoop() {
        if (!this.isScanning) return;
        
        await this.captureAndScan();
        
        // Đợi xong frame này mới lập lịch quét frame tiếp theo
        if (this.isScanning) {
            this.scanTimeout = setTimeout(() => this.scanLoop(), this.scanRate);
        }
    }
    
    stopScanning() {
        this.isScanning = false;
        
        // Dừng timeout
        if (this.scanTimeout) {
            clearTimeout(this.scanTimeout);
            this.scanTimeout = null;
        }
        
        // Dừng camera
        if (this.currentStream) {
            this.currentStream.getTracks().forEach(track => track.stop());
            this.currentStream = null;
            this.video.srcObject = null;
        }
        
        // Kết thúc session trên server
        if (this.sessionId) {
            this.endSession(this.sessionId);
            this.sessionId = null;
        }
        
        // Cập nhật UI
        this.startBtn.disabled = false;
        this.stopBtn.disabled = true;
        this.switchCameraBtn.disabled = this.cameras.length <= 1;
        
        this.updateStatus('cameraStatus', 'Đã dừng', 'neutral');
        this.updateStatus('imageQuality', 'Đã dừng', 'neutral');
    }
    
    async switchCamera() {
        if (this.cameras.length <= 1) return;
        
        const currentIndex = this.cameras.findIndex(cam => cam.deviceId === this.currentCameraId);
        const nextIndex = (currentIndex + 1) % this.cameras.length;
        this.currentCameraId = this.cameras[nextIndex].deviceId;
        
        if (this.isScanning) {
            this.stopScanning();
            await new Promise(resolve => setTimeout(resolve, 500));
            await this.startScanning();
        }
    }
    
    async captureAndScan() {
        if (!this.isScanning) return;
        
        try {
            // 1. Chụp ảnh xen kẽ (Toàn cảnh / Soi cận)
            const blob = await this.captureFrameBlob();
            
            // 2. Phân tích Sharpness (Inline để đảm bảo ổn định 100%)
            const qualityAnalysis = await this.analyzeImageQuality(blob);
            
            // Cập nhật điểm lên giao diện
            this.updateStatus('imageQuality', qualityAnalysis.reason, qualityAnalysis.isGood ? 'good' : 'bad');
            
            if (!qualityAnalysis.isGood && this.attemptCount < 50) { 
                // Nếu quá mờ thì bỏ qua frame này (trừ khi đã thử quá nhiều lần)
                return; 
            }
            
            this.attemptCount++;
            this.updateStatus('attemptCount', this.attemptCount.toString());
            
            // 3. Gửi lên Backend
            const result = await this.sendToBackend(blob);
            
            if (result.success) {
                this.handleSuccess(result.data);
            }
            
        } catch (error) {
            console.error('Scan attempt failed:', error);
        }
    }
    
    /**
     * Chụp và Crop vùng ảnh chứa mã QR dựa trên khung Guide Box 
     */
    captureFrameBlob() {
        return new Promise(resolve => {
            const videoWidth = this.video.videoWidth;
            const videoHeight = this.video.videoHeight;
            const minDim = Math.min(videoWidth, videoHeight);
            
            let cropSize, sourceX, sourceY;
            let currentMode = "";

            // CHIẾN LƯỢC XEN KẼ: Cứ 1 frame soi gần (Cắt nhỏ) thì 1 frame quét xa (Để nguyên)
            if (this.frameCounter % 2 === 0) {
                // CHẾ ĐỘ 1: QUÉT TOÀN CẢNH (Dành cho mã QR to ở gần)
                currentMode = "Toàn cảnh";
                cropSize = minDim * 0.95;
                sourceX = (videoWidth - cropSize) / 2;
                sourceY = (videoHeight - cropSize) / 2;
            } else {
                // CHẾ ĐỘ 2: SOI CẬN CẢNH (Dành cho mã QR siêu nhỏ ở xa)
                // Tập trung vào vùng trung tâm để "phóng đại" chi tiết mã QR xa
                currentMode = "Soi cận";
                cropSize = minDim * 0.4; // Chỉ lấy 40% diện tích trung tâm
                sourceX = (videoWidth - cropSize) / 2;
                sourceY = (videoHeight - cropSize) / 2;
            }

            this.canvas.width = cropSize;
            this.canvas.height = cropSize;
            
            this.ctx.drawImage(
                this.video, 
                sourceX, sourceY, cropSize, cropSize,
                0, 0, cropSize, cropSize
            );
            
            this.frameCounter++;
            this.canvas.toBlob(blob => {
                blob.mode = currentMode; // Đính kèm mode để debug nếu cần
                resolve(blob);
            }, 'image/jpeg', 0.85);
        });
    }

    /**
     * Phân tích chất lượng ảnh trực tiếp trên Main Thread một cách nhẹ nhàng
     */
    analyzeImageQuality(blob) {
        return new Promise((resolve) => {
            const url = URL.createObjectURL(blob);
            const img = new Image();
            img.onload = () => {
                URL.revokeObjectURL(url);
                const tempCanvas = document.createElement('canvas');
                // Phóng nhỏ ảnh xuống để tính toán cực nhanh (~1ms)
                const analysisSize = 100; 
                tempCanvas.width = analysisSize;
                tempCanvas.height = analysisSize;
                const ctx = tempCanvas.getContext('2d');
                ctx.drawImage(img, 0, 0, analysisSize, analysisSize);
                
                const imageData = ctx.getImageData(0, 0, analysisSize, analysisSize);
                const score = this.calculateBlurScore(imageData.data, analysisSize, analysisSize);
                
                const threshold = (blob.mode === "Soi cận") ? 15 : 10;
                
                if (score >= threshold) {
                    resolve({ isGood: true, reason: blob.mode + ' (' + Math.round(score) + ')' });
                } else {
                    resolve({ isGood: false, reason: 'Mờ (' + Math.round(score) + ')' });
                }
            };
            img.onerror = () => resolve({ isGood: true, reason: 'Check...' });
            img.src = url;
        });
    }

    
    calculateBrightness(data) {
        let totalBrightness = 0;
        for (let i = 0; i < data.length; i += 4) {
            const r = data[i];
            const g = data[i + 1];
            const b = data[i + 2];
            totalBrightness += (r + g + b) / 3;
        }
        return totalBrightness / (data.length / 4);
    }
    
    calculateBlurScore(data, width, height) {
        // Simplified Laplacian variance for blur detection
        let laplacianSum = 0;
        let pixelCount = 0;
        
        for (let y = 1; y < height - 1; y++) {
            for (let x = 1; x < width - 1; x++) {
                const idx = (y * width + x) * 4;
                
                // Get current pixel and neighbors
                const center = (data[idx] + data[idx + 1] + data[idx + 2]) / 3;
                const top = (data[((y - 1) * width + x) * 4] + data[((y - 1) * width + x) * 4 + 1] + data[((y - 1) * width + x) * 4 + 2]) / 3;
                const bottom = (data[((y + 1) * width + x) * 4] + data[((y + 1) * width + x) * 4 + 1] + data[((y + 1) * width + x) * 4 + 2]) / 3;
                const left = (data[(y * width + (x - 1)) * 4] + data[(y * width + (x - 1)) * 4 + 1] + data[(y * width + (x - 1)) * 4 + 2]) / 3;
                const right = (data[(y * width + (x + 1)) * 4] + data[(y * width + (x + 1)) * 4 + 1] + data[(y * width + (x + 1)) * 4 + 2]) / 3;
                
                // Laplacian operator
                const laplacian = -4 * center + top + bottom + left + right;
                laplacianSum += laplacian * laplacian;
                pixelCount++;
            }
        }
        
        return pixelCount > 0 ? laplacianSum / pixelCount : 0;
    }
    
    calculateContrast(data) {
        let min = 255, max = 0;
        
        for (let i = 0; i < data.length; i += 4) {
            const gray = (data[i] + data[i + 1] + data[i + 2]) / 3;
            min = Math.min(min, gray);
            max = Math.max(max, gray);
        }
        
        return max - min;
    }
    
    async sendToBackend(blob) {
        // Generate or get session ID
        if (!this.sessionId) {
            this.sessionId = 'scan-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);
        }
        
        // Create FormData
        const formData = new FormData();
        formData.append('file', blob, 'cccd-scan.jpg');
        formData.append('sessionId', this.sessionId);
        
        try {
            const response = await fetch(this.backendUrl, {
                method: 'POST',
                body: formData
            });
            
            if (response.ok) {
                const payload = await response.json();
                // KIỂM TRA LÕI APIRESPONSE (Tránh bẫy HTTP 200)
                if (payload.status === 200 || payload.code === 200) {
                    return { success: true, data: payload.data };
                } else {
                    return { success: false, message: payload.message };
                }
            } else {
                return { success: false, message: 'Lỗi server' };
            }
        } catch (error) {
            return { success: false, message: 'Lỗi kết nối' };
        }
    }
    
    handleSuccess(data) {
        // Dừng quét
        this.stopScanning();
        
        // Hiển thị kết quả
        this.displayResult(data);
        
        // Phản hồi thành công
        this.showSuccess();
        
        // Rung và âm thanh
        this.playSuccessFeedback();
    }
    
    displayResult(data) {
        this.resultPanel.classList.add('show');
        
        let html = '';
        
        // Handle new response format with scanData and scanMetadata
        if (data.scanData && data.scanMetadata) {
            // Display scan metadata
            html += `
                <div class="result-item">
                    <strong>Session ID:</strong> ${data.scanMetadata.sessionId}
                </div>
                <div class="result-item">
                    <strong>Số lần thử:</strong> ${data.scanMetadata.attemptCount}
                </div>
                <div class="result-item">
                    <strong>Thời gian session:</strong> ${data.scanMetadata.sessionDuration}s
                </div>
                <div class="result-item">
                    <strong>Tỷ lệ thành công:</strong> ${data.scanMetadata.successRate.toFixed(1)}%
                </div>
            `;
            
            // Display actual scan data
            const scanData = data.scanData;
            if (typeof scanData === 'object') {
                for (const [key, value] of Object.entries(scanData)) {
                    html += `
                        <div class="result-item">
                            <strong>${this.formatFieldName(key)}:</strong> ${value}
                        </div>
                    `;
                }
            } else {
                html += `<div class="result-item">${JSON.stringify(scanData, null, 2)}</div>`;
            }
        } else if (typeof data === 'object') {
            // Handle old format or direct object
            for (const [key, value] of Object.entries(data)) {
                html += `
                    <div class="result-item">
                        <strong>${this.formatFieldName(key)}:</strong> ${value}
                    </div>
                `;
            }
        } else {
            html = `<div class="result-item">${JSON.stringify(data, null, 2)}</div>`;
        }
        
        this.resultContent.innerHTML = html;
    }
    
    formatFieldName(fieldName) {
        const fieldNames = {
            'hoTen': 'Họ tên',
            'soCCCD': 'Số CCCD',
            'ngaySinh': 'Ngày sinh',
            'gioiTinh': 'Giới tính',
            'queQuan': 'Quê quán',
            'ngayCap': 'Ngày cấp',
            'noiCap': 'Nơi cấp'
        };
        
        return fieldNames[fieldName] || fieldName;
    }
    
    showSuccess() {
        // Hiển thị indicator
        this.successIndicator.classList.add('show');
        setTimeout(() => {
            this.successIndicator.classList.remove('show');
        }, 3000);
        
        this.updateStatus('imageQuality', 'Quét thành công!', 'good');
    }
    
    playSuccessFeedback() {
        // Âm thanh
        if (this.successSound) {
            this.successSound.play().catch(() => {
                // Ignore audio play errors
            });
        }
        
        // Rung (vibration)
        if (this.vibratePattern && navigator.vibrate) {
            navigator.vibrate(this.vibratePattern);
        }
    }
    
    updateStatus(elementId, text, type = 'neutral') {
        const element = document.getElementById(elementId);
        element.textContent = text;
        element.className = 'status-value status-' + type;
    }
    
    showError(message) {
        this.errorMessage.textContent = message;
        this.errorMessage.classList.add('show');
        
        setTimeout(() => {
            this.hideError();
        }, 5000);
    }
    
    hideError() {
        this.errorMessage.classList.remove('show');
    }
    
    async endSession(sessionId) {
        try {
            await fetch(`/api/v1/scanner/end-session/${sessionId}`, {
                method: 'POST'
            });
            console.log('Session ended:', sessionId);
        } catch (error) {
            console.warn('Failed to end session:', error);
        }
    }
}

// Khởi động hệ thống khi DOM sẵn sàng
document.addEventListener('DOMContentLoaded', () => {
    new ContinuousScanner();
});

// Xử lý lỗi toàn cục
window.addEventListener('error', (event) => {
    console.error('Global error:', event.error);
});

window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
});
