package service.cinema.be.core.admin.quanlyve.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AdDatVeRequest {
    @NotNull(message = "Thiếu thông tin suất chiếu")
    private String idSuatChieu;

    @NotNull(message = "Thiếu thông tin loại ngày")
    private String idLoaiNgay;

    @NotEmpty(message = "Vui lòng chọn ít nhất 1 ghế")
    @Valid
    private List<ChiTietGheDat> danhSachGhe;

    @Getter
    @Setter
    public static class ChiTietGheDat {
        @NotNull(message = "Thiếu ID ghế")
        private String idGhe;

        @NotNull(message = "Thiếu loại khách hàng")
        private String idLoaiKhachHang;
    }
}