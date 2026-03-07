package service.cinema.be.core.admin.quanlysuatchieu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO để tự động sinh nhiều suất chiếu (batch generate).
 *
 * <p>Logic:
 * <ul>
 *   <li>Với mỗi ngày trong khoảng [tuNgay, denNgay]</li>
 *   <li>Với mỗi slot giờ trong danhSachGioBatDau</li>
 *   <li>Tính gioKetThuc = gioBatDau + thoiLuong (phút) + bufferPhut</li>
 *   <li>Kiểm tra overlap với phòng → nếu không trùng thì insert</li>
 * </ul>
 */
@Getter
@Setter
public class AdBatchSuatChieuRequest {

    @NotBlank(message = "ID phim không được để trống")
    private String idPhim;

    @NotBlank(message = "ID phòng không được để trống")
    private String idPhongChieu;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate tuNgay;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate denNgay;

    /**
     * Danh sách giờ bắt đầu cho từng suất trong ngày.
     * Ví dụ: [08:00, 11:00, 14:00, 18:00, 21:30]
     */
    @NotEmpty(message = "Phải có ít nhất 1 khung giờ")
    private List<LocalTime> danhSachGioBatDau;

    /**
     * Thời gian buffer (phút) giữa các suất chiếu (quảng cáo + dọn phòng).
     * Mặc định 20 phút nếu không truyền.
     */
    private Integer bufferPhut = 20;

    /** Trạng thái mặc định cho suất chiếu được tạo (1 = Sắp chiếu). */
    private Integer trangThai = 1;
}
