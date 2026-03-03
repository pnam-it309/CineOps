package service.cinema.be.core.admin.quanlyve.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdVeResponse {

    // ĐỔI TỪ Long SANG String Ở ĐÂY NHÉ:
    private String id;

    private String maVe;
    private String tenPhim;
    private String tenPhongChieu;
    private String viTriGhe;
    private BigDecimal giaThanhToan;
    private Integer loaiVe;
    private Integer trangThai;
    private LocalDateTime ngayTao;
    private String nguoiTao;
}
