package service.cinema.be.core.admin.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler cho Continuous Scanner System
 * Tự động dọn dẹp session hết hạn và logging thống kê
 */
@Slf4j
@Component
public class ContinuousScannerScheduler {
    
    private final ContinuousScannerWrapperService scannerService;
    
    public ContinuousScannerScheduler(ContinuousScannerWrapperService scannerService) {
        this.scannerService = scannerService;
    }
    
    /**
     * Dọn dẹp session hết hạn mỗi 5 phút
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút
    public void cleanupExpiredSessions() {
        try {
            log.debug("Bắt đầu dọn dẹp session hết hạn...");
            scannerService.cleanupExpiredSessions();
            log.debug("Hoàn thành dọn dẹp session hết hạn");
        } catch (Exception e) {
            log.error("Lỗi trong quá trình dọn dẹp session: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Logging thống kê mỗi 10 phút
     */
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10 phút
    public void logStatistics() {
        try {
            var stats = scannerService.getScanStatistics();
            log.info("Thống kê quét - Tổng: {}, Thành công: {}, Thất bại: {}, Tỷ lệ: {:.2f}%, Session hoạt động: {}", 
                stats.get("totalAttempts"),
                stats.get("successfulScans"),
                stats.get("failedScans"),
                (Double) stats.get("successRate"),
                stats.get("activeSessions"));
        } catch (Exception e) {
            log.error("Lỗi logging thống kê: {}", e.getMessage(), e);
        }
    }
}
