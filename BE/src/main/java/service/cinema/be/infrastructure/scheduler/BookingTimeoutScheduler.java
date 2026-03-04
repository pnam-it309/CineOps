package service.cinema.be.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.HoaDon;
import service.cinema.be.entity.Ve;
import service.cinema.be.core.admin.quanlyhoadon.repository.AdHoaDonRepository;
import service.cinema.be.core.admin.quanlyhoadon.repository.AdHoaDonChiTietRepository;
import service.cinema.be.core.admin.quanlyve.repository.AdVeRepository;
import service.cinema.be.repository.SuatChieuRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fix #5 — Auto-release ghế sau 10 phút nếu chưa thanh toán.
 *
 * Flow:
 * 1. Mỗi 1 phút scan các HoaDon có trang_thai = 0 (PENDING) và thoi_gian_het_han < NOW
 * 2. Hủy hóa đơn (trang_thai = 2 = HET_HAN)
 * 3. Giải phóng tất cả vé liên quan (trang_thai Ve = 3 = DA_HUY / RELEASED)
 * 4. Cộng lại soGheTrong cho suất chiếu tương ứng
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingTimeoutScheduler {

    // Trạng thái hóa đơn
    private static final int TRANG_THAI_PENDING   = 0; // Chưa thanh toán
    private static final int TRANG_THAI_HET_HAN   = 2; // Hết hạn / bị hủy auto
    // Trạng thái vé
    private static final int VE_RELEASED          = 3; // Vé được giải phóng (ghế lại trống)

    private final AdHoaDonRepository hoaDonRepository;
    private final AdHoaDonChiTietRepository hoaDonChiTietRepository;
    private final AdVeRepository veRepository;
    private final SuatChieuRepository suatChieuRepository;

    /**
     * Chạy mỗi 60 giây.
     * fixedDelay đảm bảo lần chạy trước kết thúc xong mới chạy lần tiếp.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void releaseExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        // Tìm tất cả hóa đơn PENDING đã quá hạn
        List<HoaDon> expiredList = hoaDonRepository
                .findByTrangThaiAndThoiGianHetHanBefore(TRANG_THAI_PENDING, now);

        if (expiredList.isEmpty()) return;

        log.info("[BookingTimeout] Tìm thấy {} hóa đơn hết hạn, bắt đầu giải phóng ghế...", expiredList.size());

        for (HoaDon hoaDon : expiredList) {
            try {
                // 1. Lấy danh sách vé trong hóa đơn này
                List<Ve> veList = hoaDonChiTietRepository.findVeByHoaDonId(hoaDon.getId());

                for (Ve ve : veList) {
                    // 2. Giải phóng vé (trả ghế về trạng thái trống)
                    ve.setTrangThai(VE_RELEASED);
                    veRepository.save(ve);

                    // 3. Cộng lại soGheTrong cho suất chiếu
                    if (ve.getSuatChieu() != null) {
                        var suatChieu = ve.getSuatChieu();
                        int current = suatChieu.getSoGheTrong() != null ? suatChieu.getSoGheTrong() : 0;
                        suatChieu.setSoGheTrong(current + 1);
                        suatChieuRepository.save(suatChieu);
                    }
                }

                // 4. Đánh dấu hóa đơn đã hết hạn
                hoaDon.setTrangThai(TRANG_THAI_HET_HAN);
                hoaDonRepository.save(hoaDon);

                log.info("[BookingTimeout] Đã hủy HĐ {} — giải phóng {} ghế", hoaDon.getMaHoaDon(), veList.size());

            } catch (Exception e) {
                log.error("[BookingTimeout] Lỗi khi xử lý hóa đơn {}: {}", hoaDon.getMaHoaDon(), e.getMessage());
            }
        }
    }
}
