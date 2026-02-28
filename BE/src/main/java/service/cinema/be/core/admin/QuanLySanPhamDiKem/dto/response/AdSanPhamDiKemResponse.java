package service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdSanPhamDiKemResponse {
    private String id; // Đổi sang Long cho đồng bộ DB
    private String tenSanPham;
    private String tenLoaiSanPham;
    private String moTa;
    private String hinhAnh;
    private Integer trangThai;

    // Thêm danh sách biến thể để trả về cho Vue
    private List<VariantResponse> variants;

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VariantResponse {
        private String idChiTiet;
        private String tenKichCo;
        private String tenDonViTinh;
        private Double giaTriDinhLuong;
        private BigDecimal giaBan;
        private Integer soLuongTon;
    }
}