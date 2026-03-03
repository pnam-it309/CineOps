package service.cinema.be.core.admin.quanlyhoadon.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdHoaDonResponse {
    private String id;
    private String maHoaDon;
    private BigDecimal tongTienThanhToan;
    private Integer phuongThucThanhToan;
    private Integer trangThai;
    private LocalDateTime ngayTao;
    private String tenNhanVien; // Sau này nối bảng NhanVien
    private String tenKhachHang; // Sau này nối bảng KhachHang
    private Integer kenhBanHang;
    private String sdt;
}