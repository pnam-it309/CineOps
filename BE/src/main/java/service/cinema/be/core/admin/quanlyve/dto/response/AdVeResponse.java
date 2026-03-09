package service.cinema.be.core.admin.quanlyve.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdVeResponse {

    private String id;
    private String maVe;
    private String maHoaDon;      // BỔ SUNG: Để liên kết ngược lại hóa đơn
    private String tenPhim;
    private String tenPhongChieu;
    private String viTriGhe;

    // THỜI GIAN CHIẾU THỰC TẾ (Lấy từ SuatChieu) [cite: 2026-02-04]
    private LocalDate ngayChieu;
    private LocalTime gioBatDau;

    private BigDecimal giaThanhToan;
    private Integer loaiVe;       // 0: Tại quầy, 1: Online
    private Integer trangThai;    // 1: Đã bán, 0: Đã hủy
    private LocalDateTime ngayTao; // Ngày giờ xuất vé
    private String nguoiTao;      // Nhân viên thực hiện

    // THÔNG TIN KHÁCH HÀNG & LOẠI VÉ
    private String tenLoaiKhachHang;
    private String tenLoaiVe;     // Vé Thường, Vé VIP, Vé Couple
    private String tenKhachHang;
    private String sdtKhachHang;
    
    // BỔ SUNG CHO IN VÉ CHUYÊN NGHIỆP
    private Integer thoiLuong;
    private String nhanDoTuoi;
}