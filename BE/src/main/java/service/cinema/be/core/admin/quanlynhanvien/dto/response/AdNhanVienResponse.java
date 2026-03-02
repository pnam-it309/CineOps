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
    private String tenPhanQuyen; // Lấy từ bảng PhanQuyen
    private String idPhanQuyen;
    private String chucVu;
    private String anhNhanVien;
    private Integer trangThai;
    private LocalDateTime ngayTao; // Ngày tham gia
}