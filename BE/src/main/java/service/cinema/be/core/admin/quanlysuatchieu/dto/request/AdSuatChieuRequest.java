package service.cinema.be.core.admin.quanlysuatchieu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AdSuatChieuRequest {
    @NotBlank(message = "ID phim không được để trống")
    private String idPhim;

    @NotBlank(message = "ID phòng không được để trống")
    private String idPhongChieu;

    private String idKhungGio;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime gioBatDau;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime gioKetThuc;

    @NotNull(message = "Ngày chiếu không được để trống")
    private LocalDate ngayChieu;

    private Integer trangThai = 1;
}
