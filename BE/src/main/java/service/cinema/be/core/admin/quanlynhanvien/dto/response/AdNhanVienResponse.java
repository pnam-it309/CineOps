package service.cinema.be.core.admin.quanlynhanvien.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AdNhanVienResponse {
    private String id;
    private String maNhanVien;
    private String tenNhanVien;
    private String email;
    private String tenDangNhap; // Xử lý logic: @ + phần trước của email
    private String soDienThoai;
    private String cccd;
    private LocalDate ngaySinh;
    private String queQuan;
    private String diaChi; // Concatenated address
    private String tenPhanQuyen; // Lấy từ bảng PhanQuyen
    private String idPhanQuyen;
    private Integer gioiTinh;
    private String chucVu;
    private String anhNhanVien;
    private String thanhPhoTinh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private Integer trangThai;
    private LocalDateTime ngayTao; // Ngày tham gia
}