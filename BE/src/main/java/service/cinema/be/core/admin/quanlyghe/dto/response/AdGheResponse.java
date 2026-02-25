package service.cinema.be.core.admin.quanlyghe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdGheResponse {
    private String id;
    private String soGhe;
    private String soHang;
    private Integer soCot;
    private Integer trangThai;
    private String idPhongChieu;
    private String tenPhongChieu;
    private String idLoaiGhe;
    private String tenLoaiGhe;
    private BigDecimal phuPhi;
}
