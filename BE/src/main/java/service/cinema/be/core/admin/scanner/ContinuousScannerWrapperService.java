/**
 * Backend Wrapper Service cho Continuous Scanning System
 * Bọc bên ngoài API hiện tại, cung cấp thêm tính năng logging và monitoring
 */

package service.cinema.be.core.admin.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContinuousScannerWrapperService {
    
    private final Map<String, ScanSession> activeSessions = new ConcurrentHashMap<>();
    private final AtomicInteger totalScanAttempts = new AtomicInteger(0);
    private final AtomicInteger successfulScans = new AtomicInteger(0);
    private final AtomicInteger failedScans = new AtomicInteger(0);
    
    /**
     * Wrapper method cho API quét CCCD hiện tại
     * Thêm logging, monitoring và tracking
     */
    public ResponseEntity<?> scanWithTracking(String sessionId, MultipartFile file) {
        String scanId = generateScanId(sessionId);
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            // Log attempt
            totalScanAttempts.incrementAndGet();
            logScanAttempt(scanId, sessionId, file);
            
            // Track session
            ScanSession session = activeSessions.computeIfAbsent(sessionId, 
                k -> new ScanSession(sessionId, startTime));
            session.incrementAttempts();
            
            // Validate file trước khi gửi đến API hiện tại
            validateImageFile(file);
            
            // Gọi đến API hiện tại (đã có sẵn)
            ResponseEntity<?> response = callExistingScanAPI(file);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // Success case
                successfulScans.incrementAndGet();
                session.markSuccess();
                logScanSuccess(scanId, sessionId, response);
                
                // Enrich response với metadata
                return enrichResponse(response, scanId, session);
            } else {
                // Error case
                failedScans.incrementAndGet();
                session.markFailure();
                logScanFailure(scanId, sessionId, response);
                
                return response;
            }
            
        } catch (Exception e) {
            failedScans.incrementAndGet();
            logScanError(scanId, sessionId, e);
            
            ScanSession session = activeSessions.get(sessionId);
            if (session != null) {
                session.markFailure();
            }
            
            return ResponseEntity.badRequest().body(
                ApiResponse.error(400, "Lỗi xử lý quét: " + e.getMessage())
            );
        } finally {
            logScanCompletion(scanId, startTime);
        }
    }
    
    /**
     * Gọi đến API scan hiện tại (không sửa đổi)
     * INTEGRATION POINT - CHỈ SỬA ĐOẠN NÀY
     */
    private ResponseEntity<?> callExistingScanAPI(MultipartFile file) {
        try {
            // TODO: Inject và gọi service hiện tại của bạn
            // Ví dụ: return existingCccdScanService.scanCccd(file);
            
            // Placeholder implementation - cần tích hợp với API hiện tại
            throw new UnsupportedOperationException(
                "Cần tích hợp với API hiện tại tại đây. " +
                "Ví dụ: @Autowired private CccdScanService existingService;"
            );
            
        } catch (Exception e) {
            log.error("Error calling existing scan API: {}", e.getMessage(), e);
            throw e; // Re-throw để xử lý ở tầng trên
        }
    }
    
    /**
     * Validate file ảnh trước khi xử lý
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là định dạng ảnh");
        }
        
        // Kiểm tra kích thước file (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File quá lớn (tối đa 10MB)");
        }
        
        // Kiểm tra định dạng cho phép
        String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png"};
        boolean isAllowed = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new IllegalArgumentException("Chỉ hỗ trợ định dạng JPG, JPEG, PNG");
        }
    }
    
    /**
     * Enrich response với metadata về session
     */
    private ResponseEntity<?> enrichResponse(ResponseEntity<?> originalResponse, 
                                           String scanId, ScanSession session) {
        
        Map<String, Object> enrichedData = new HashMap<>();
        enrichedData.put("scanData", originalResponse.getBody());
        enrichedData.put("scanMetadata", Map.of(
            "scanId", scanId,
            "sessionId", session.getSessionId(),
            "attemptCount", session.getAttemptCount(),
            "sessionDuration", session.getSessionDuration(),
            "totalAttempts", totalScanAttempts.get(),
            "successRate", calculateSuccessRate()
        ));
        
        return ResponseEntity.ok()
            .headers(originalResponse.getHeaders())
            .body(ApiResponse.success(enrichedData, "Quét thành công"));
    }
    
    /**
     * Lấy thống kê quét
     */
    public Map<String, Object> getScanStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", totalScanAttempts.get());
        stats.put("successfulScans", successfulScans.get());
        stats.put("failedScans", failedScans.get());
        stats.put("successRate", calculateSuccessRate());
        stats.put("activeSessions", activeSessions.size());
        stats.put("timestamp", LocalDateTime.now());
        
        return stats;
    }
    
    /**
     * Kết thúc session
     */
    public void endSession(String sessionId) {
        ScanSession session = activeSessions.remove(sessionId);
        if (session != null) {
            log.info("Session {} kết thúc sau {} với {} lần thử", 
                sessionId, session.getSessionDuration(), session.getAttemptCount());
        }
    }
    
    /**
     * Dọn dẹp các session hết hạn
     */
    public void cleanupExpiredSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        
        activeSessions.entrySet().removeIf(entry -> {
            ScanSession session = entry.getValue();
            if (session.getStartTime().isBefore(cutoff)) {
                log.info("Session {} bị xóa do hết hạn", session.getSessionId());
                return true;
            }
            return false;
        });
    }
    
    // Helper methods
    private String generateScanId(String sessionId) {
        return sessionId + "-" + System.currentTimeMillis();
    }
    
    private double calculateSuccessRate() {
        int total = totalScanAttempts.get();
        if (total == 0) return 0.0;
        return (double) successfulScans.get() / total * 100;
    }
    
    private void logScanAttempt(String scanId, String sessionId, MultipartFile file) {
        log.info("Scan attempt - ID: {}, Session: {}, File: {} ({} bytes)", 
            scanId, sessionId, file.getOriginalFilename(), file.getSize());
    }
    
    private void logScanSuccess(String scanId, String sessionId, ResponseEntity<?> response) {
        log.info("Scan success - ID: {}, Session: {}, Status: {}", 
            scanId, sessionId, response.getStatusCode());
    }
    
    private void logScanFailure(String scanId, String sessionId, ResponseEntity<?> response) {
        log.warn("Scan failed - ID: {}, Session: {}, Status: {}", 
            scanId, sessionId, response.getStatusCode());
    }
    
    private void logScanError(String scanId, String sessionId, Exception e) {
        log.error("Scan error - ID: {}, Session: {}, Error: {}", 
            scanId, sessionId, e.getMessage(), e);
    }
    
    private void logScanCompletion(String scanId, LocalDateTime startTime) {
        long duration = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
        log.debug("Scan {} completed in {}ms", scanId, duration);
    }
    
    /**
     * Inner class để tracking session
     */
    public static class ScanSession {
        private final String sessionId;
        private final LocalDateTime startTime;
        private final AtomicInteger attempts = new AtomicInteger(0);
        private LocalDateTime lastSuccessTime;
        private LocalDateTime lastFailureTime;
        
        public ScanSession(String sessionId, LocalDateTime startTime) {
            this.sessionId = sessionId;
            this.startTime = startTime;
        }
        
        public void incrementAttempts() {
            attempts.incrementAndGet();
        }
        
        public void markSuccess() {
            this.lastSuccessTime = LocalDateTime.now();
        }
        
        public void markFailure() {
            this.lastFailureTime = LocalDateTime.now();
        }
        
        public String getSessionId() { return sessionId; }
        public LocalDateTime getStartTime() { return startTime; }
        public int getAttemptCount() { return attempts.get(); }
        public LocalDateTime getLastSuccessTime() { return lastSuccessTime; }
        public LocalDateTime getLastFailureTime() { return lastFailureTime; }
        
        public long getSessionDuration() {
            return java.time.Duration.between(startTime, LocalDateTime.now()).toSeconds();
        }
        
        public boolean isSuccess() {
            return lastSuccessTime != null && 
                (lastFailureTime == null || lastSuccessTime.isAfter(lastFailureTime));
        }
    }
}
