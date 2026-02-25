package service.cinema.be.core.admin.quanlyghe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdPhongChieuResponse {
    private String id;
    private String tenRap; // In DB it's id_rap string
    private String tenPhong;
    private String loaiManHinh;
}
