package service.cinema.be.core.admin.quanlyhoadon.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdLichSuThanhToanResponse {
    private String id;
    private String maGiaoDich;
    private Integer phuongThucThanhToan;
    private BigDecimal soTien;
    private LocalDateTime ngayThanhToan;
    private String noiDung;
    private Integer trangThai;
}
