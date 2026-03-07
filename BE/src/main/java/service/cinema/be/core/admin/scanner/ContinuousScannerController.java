package service.cinema.be.core.admin.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import service.cinema.be.core.common.response.ApiResponse;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/scanner")
@RequiredArgsConstructor
public class ContinuousScannerController {
    
    private final ContinuousScannerWrapperService scannerService;
    
    /**
     * Endpoint cho frontend gửi ảnh quét liên tục
     * Bọc bên ngoài API hiện tại
     */
    @PostMapping("/scan-continuous")
    public ResponseEntity<?> scanContinuous(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            HttpServletRequest request) {
        
        try {
            // Generate session ID nếu không có
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = generateSessionId();
            }
            
            log.info("Continuous scan request - Session: {}, IP: {}, File: {}", 
                sessionId, getClientIP(request), file.getOriginalFilename());
            
            return scannerService.scanWithTracking(sessionId, file);
            
        } catch (Exception e) {
            log.error("Error in continuous scan: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error(400, "Lỗi quét: " + e.getMessage())
            );
        }
    }
    
    /**
     * Endpoint để kết thúc session
     */
    @PostMapping("/end-session/{sessionId}")
    public ResponseEntity<?> endSession(@PathVariable String sessionId) {
        try {
            scannerService.endSession(sessionId);
            log.info("Session {} ended", sessionId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Session đã kết thúc"));
        } catch (Exception e) {
            log.error("Error ending session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponse.error(400, "Lỗi kết thúc session: " + e.getMessage())
            );
        }
    }
    
    /**
     * Endpoint lấy thống kê quét
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = scannerService.getScanStatistics();
            return ResponseEntity.ok(ApiResponse.success(stats, "Thống kê quét"));
        } catch (Exception e) {
            log.error("Error getting statistics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponse.error(400, "Lỗi lấy thống kê: " + e.getMessage())
            );
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "healthy",
            "timestamp", java.time.LocalDateTime.now(),
            "service", "Continuous Scanner"
        );
        
        return ResponseEntity.ok(ApiResponse.success(health, "Service đang hoạt động"));
    }
    
    /**
     * Cleanup expired sessions (có thể gọi từ scheduler)
     */
    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupSessions() {
        try {
            scannerService.cleanupExpiredSessions();
            return ResponseEntity.ok(ApiResponse.success(null, "Đã dọn dẹp session hết hạn"));
        } catch (Exception e) {
            log.error("Error cleaning up sessions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponse.error(400, "Lỗi dọn dẹp: " + e.getMessage())
            );
        }
    }
    
    // Helper methods
    private String generateSessionId() {
        return "scan-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
