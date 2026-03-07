package service.cinema.be.core.admin.quanlysuatchieu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Kết quả sau khi sinh hàng loạt suất chiếu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdBatchSuatChieuResult {

    /** Tổng số slot được yêu cầu tạo (số ngày × số giờ). */
    private int tongSlot;

    /** Số suất chiếu đã tạo thành công. */
    private int taoThanhCong;

    /** Số slot bị bỏ qua do trùng lịch. */
    private int boBoBiTrung;

    /** Chi tiết các slot bị trùng (để FE hiển thị cho user biết). */
    private List<SkippedSlot> danhSachBiTrung;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkippedSlot {
        private LocalDate ngay;
        private LocalTime gioBatDau;
        private LocalTime gioKetThuc;
        private String lyDo;
    }
}
