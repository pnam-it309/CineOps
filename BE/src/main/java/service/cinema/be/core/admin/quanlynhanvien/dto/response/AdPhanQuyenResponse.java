package service.cinema.be.core.admin.quanlynhanvien.dto.response;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AdPhanQuyenResponse {
    private String id;
    private String maPhanQuyen;
    private String tenVaiTro;
    private String quyenHan;
    private Integer trangThai;
}
