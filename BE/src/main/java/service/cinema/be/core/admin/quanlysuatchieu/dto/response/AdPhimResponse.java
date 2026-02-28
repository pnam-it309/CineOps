package service.cinema.be.core.admin.quanlysuatchieu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdPhimResponse {
    private String id;
    private String tenPhim;
    private Integer thoiLuong;
    private String poster;
}
