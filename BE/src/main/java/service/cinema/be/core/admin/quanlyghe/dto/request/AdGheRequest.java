package service.cinema.be.core.admin.quanlyghe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdGheRequest {
    @NotBlank(message = "ID phòng chiếu không được để trống")
    private String idPhongChieu;

    @NotBlank(message = "ID loại ghế không được để trống")
    private String idLoaiGhe;

    @NotBlank(message = "Số ghế không được để trống")
    private String soGhe;

    @NotBlank(message = "Hàng không được để trống")
    private String soHang;

    @NotNull(message = "Cột không được để trống")
    private Integer soCot;

    private Integer trangThai = 1;
}
