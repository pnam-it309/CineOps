package service.cinema.be.core.admin.quanlysuatchieu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class AdSuatChieuRequest {
    @NotBlank(message = "ID phim không được để trống")
    private String idPhim;
    @NotBlank(message = "ID phòng không được để trống")
    private String idPhongChieu;
    @NotBlank(message = "ID khung giờ không được để trống")
    private String idKhungGio;
    @NotNull(message = "Ngày chiếu không được để trống")
    private LocalDate ngayChieu;
    private Integer trangThai = 1;
}
