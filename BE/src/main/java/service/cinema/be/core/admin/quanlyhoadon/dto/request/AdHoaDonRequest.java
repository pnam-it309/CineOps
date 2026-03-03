package service.cinema.be.core.admin.quanlyhoadon.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;

import java.util.List;

@Getter
@Setter // Bắt buộc phải có @Getter, @Setter của Lombok thì mới hết báo đỏ method get/set
public class AdHoaDonRequest {

    @NotNull(message = "Thiếu thông tin suất chiếu")
    private String idSuatChieu;

    @NotNull(message = "Thiếu thông tin loại ngày")
    private String idLoaiNgay;

    @NotNull(message = "Thiếu phương thức thanh toán")
    private Integer phuongThucThanhToan; // 0: Tiền mặt, 1: Chuyển khoản, v.v.

    // Có thể thêm idPhieuGiamGia, idKhachHang nếu cần

    @NotEmpty(message = "Vui lòng chọn ít nhất 1 ghế")
    private List<AdDatVeRequest.ChiTietGheDat> danhSachGhe;
    // Thêm trường này vào class
    private String idKhachHang;
    private String idVoucher;
    private String ghiChu;
}