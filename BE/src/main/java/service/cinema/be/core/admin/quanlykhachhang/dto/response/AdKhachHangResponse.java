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
    private LocalDate ngaySinh; // Thêm dòng này để hết bị "đỏ" ở Service
    private Integer trangThai;
}