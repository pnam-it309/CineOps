package service.cinema.be.core.admin.quanlyhoadon.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AdHoaDonChiTietResponse {
    private String id;
    private Integer loai; // 0: Vé, 1: Đồ ăn
    private String tenPhim;
    private String tenPhongChieu;
    private String viTriGhe;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String tenSanPham;
    private String maMuc; // Bổ sung mã vé hoặc mã SP
    private BigDecimal giamGia;
}