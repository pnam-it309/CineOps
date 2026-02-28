//package service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.request;
//
//import jakarta.validation.constraints.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//public class AdPhieuGiamGiaRequest {
//    @NotBlank(message = "Mã phiếu không được để trống")
//    @Size(min = 3, max = 20, message = "Mã phiếu phải từ 3 - 20 ký tự")
//    @Pattern(regexp = "^[A-Z0-9-]+$",
//            message = "Chỉ cho phép chữ in hoa không dấu, số và dấu gạch ngang"
//    )
//    private String maPhieuGiamGia;
//
//    @NotBlank(message = "Tên phiếu không được để trống")
//    @Size(max = 100, message = "Tên không vượt quá 100 ký tự")
//    private String tenPhieu;
//
//    // 1 = %/2 = tiền mặt
//    @NotNull(message = "Loại phiếu không được để trống")
//    private Integer loaiPhieu;
//
//    @DecimalMin(value = "1.0", message = "Phần trăm phải >= 1")
//    @DecimalMax(value = "70.0", message = "Phần trăm không được vượt quá 70%")
//    private BigDecimal phanTramGiamGia;
//
//    @DecimalMin(value = "1000", message = "Số tiền giảm phải >= 1.000")
//    private BigDecimal soTienGiam;
//
////    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn tối thiểu phải > 0")
////    private BigDecimal giaTriHoaDonToiThieu;
//    @DecimalMin(value = "1000", message = "Đơn tối thiểu phải > 0")
//    private BigDecimal giaTriHoaDonToiThieu;
//
//    @Min(value = 1, message = "Số lượng phải > 0")
//    private Integer soLuong;
//
//    @NotNull(message = "Ngày bắt đầu không được để trống")
//    private LocalDateTime ngayBatDau;
//
//    @NotNull(message = "Ngày kết thúc không được để trống")
//    private LocalDateTime ngayKetThuc;
//
//}
package service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdPhieuGiamGiaRequest {
    @NotBlank(message = "Mã phiếu không được để trống")
    @Size(min = 3, max = 30, message = "Mã phiếu phải từ 3 - 30 ký tự")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Chỉ cho phép chữ in hoa, số và dấu gạch ngang")
    private String maPhieuGiamGia;

    @NotBlank(message = "Tên phiếu không được để trống")
    private String tenPhieu;

    @NotNull(message = "Loại phiếu không được để trống")
    private Integer loaiPhieu; // 1: %, 2: Tiền mặt

    private BigDecimal phanTramGiamGia;
    private BigDecimal soTienGiam;

    @DecimalMin(value = "0", message = "Giảm tối đa không được âm")
    private BigDecimal giamToiDa;

    @NotNull(message = "Đơn tối thiểu không được để trống")
    @DecimalMin(value = "0", message = "Đơn tối thiểu phải >= 0")
    private BigDecimal giaTriHoaDonToiThieu;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer soLuong;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;
}