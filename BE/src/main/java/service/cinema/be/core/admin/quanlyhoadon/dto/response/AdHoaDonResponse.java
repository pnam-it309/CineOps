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
    private BigDecimal tongTien;
    private BigDecimal soTienGiam;
    private BigDecimal tongTienThanhToan;
    private Integer phuongThucThanhToan;
    private Integer trangThai;
    private LocalDateTime ngayTao;
    private String tenNhanVien;
    private String tenKhachHang;
    private Integer kemBanHang;
    private String ghiChu;
    private String email;    // Dùng để gửi mail/hiển thị liên hệ
    private String taiKhoan; // Dùng để xác thực/hiển thị tên người dùng       // Bổ sung trường này
    private String sdt;
//    pr
}