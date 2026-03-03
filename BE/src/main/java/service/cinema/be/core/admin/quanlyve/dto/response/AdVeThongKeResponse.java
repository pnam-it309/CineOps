package service.cinema.be.core.admin.quanlyve.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdVeThongKeResponse {
    private Long tongVe;
    private Long veHopLe;
    private Long veDaHuy;
    private BigDecimal doanhThu;
}