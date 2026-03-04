package service.cinema.be.core.admin.quanlyhoadon.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdHoaDonRequest {

    @NotNull(message = "Thiếu thông tin suất chiếu")
    private String idSuatChieu;

    @NotNull(message = "Thiếu thông tin loại ngày")
    private String idLoaiNgay;

    @NotNull(message = "Thiếu phương thức thanh toán")
    private Integer phuongThucThanhToan; // 0: Tiền mặt, 1: Chuyển khoản, v.v.

    // Danh sách ghế (vé xem phim)
    @NotEmpty(message = "Vui lòng chọn ít nhất 1 ghế")
    private List<AdDatVeRequest.ChiTietGheDat> danhSachGhe;

    // Danh sách đồ ăn/concessions (optional, có thể để trống)
    @Valid
    private List<ChiTietDoAn> danhSachDoAn = new ArrayList<>();

    @Getter
    @Setter
    public static class ChiTietDoAn {
        @NotNull(message = "Thiếu ID chi tiết sản phẩm")
        private String idChiTietSanPhamDiKem; // FK vào bảng chi_tiet_san_pham_di_kem

        @Min(value = 1, message = "Số lượng tối thiểu là 1")
        private int soLuong;
    }
}
