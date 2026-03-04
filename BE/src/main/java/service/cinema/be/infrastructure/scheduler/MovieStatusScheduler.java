package service.cinema.be.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.repository.PhimRepository;

import java.time.LocalDate;

/**
 * Fix #9 — Tự động cập nhật trạng thái phim theo ngày thực tế.
 *
 * Quy tắc nghiệp vụ:
 *   trangThai = 0 → Sắp chiếu (ngayKhoiChieu > hôm nay)
 *   trangThai = 1 → Đang chiếu (ngayKhoiChieu <= hôm nay <= ngayKetThuc)
 *   trangThai = 2 → Đã kết thúc (ngayKetThuc < hôm nay)
 *   trangThai = 3 → Ẩn / Ngừng chiếu (quản trị viên set thủ công — KHÔNG tự động thay đổi)
 *
 * Chạy mỗi ngày lúc 00:05 (5 phút sau nửa đêm).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovieStatusScheduler {

    private final PhimRepository phimRepository;

    @Scheduled(cron = "0 5 0 * * *") // Mỗi ngày lúc 00:05:00
    @Transactional
    public void autoUpdateMovieStatus() {
        LocalDate today = LocalDate.now();
        log.info("[MovieStatus] Bắt đầu cập nhật trạng thái phim — Ngày: {}", today);

        // 1. Phim bắt đầu chiếu hôm nay hoặc trước đó và chưa kết thúc → trang_thai = 1
        int updatedToShowing = phimRepository.updateTrangThaiDangChieu(today);
        log.info("[MovieStatus] {} phim chuyển sang 'Đang chiếu'", updatedToShowing);

        // 2. Phim đã qua ngày kết thúc → trang_thai = 2
        int updatedToEnded = phimRepository.updateTrangThaiKetThuc(today);
        log.info("[MovieStatus] {} phim chuyển sang 'Kết thúc'", updatedToEnded);

        // 3. Phim chưa đến ngày khởi chiếu → trang_thai = 0
        int updatedToUpcoming = phimRepository.updateTrangThaiSapChieu(today);
        log.info("[MovieStatus] {} phim chuyển sang 'Sắp chiếu'", updatedToUpcoming);
    }
}
