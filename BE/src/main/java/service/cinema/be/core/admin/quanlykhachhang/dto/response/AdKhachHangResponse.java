package service.cinema.be.core.admin.quanlykhachhang.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate; // Đừng quên import cái này

@Getter
@Setter
@Builder
public class AdKhachHangResponse {
    private String id;
    private String maKhachHang;
    private String tenKhachHang;
    private String email;
    private String sdt;
    private Integer gioiTinh;
    private LocalDate ngaySinh;
    private String hinhAnh;
    private String ghiChu;
    private String thanhPhoTinh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private String diaChi; // Concatenated address
    private Integer trangThai;

    // Bổ sung các trường thống kê cho bảng PGG cá nhân
    private String buyLast;      // Ngày mua gần nhất
    private Integer orders;       // Số lượng đơn hàng
    private Double spent;        // Tổng chi tiêu
}