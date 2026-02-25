package service.cinema.be.core.admin.quanlyghe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdLoaiGheResponse {
    private String id;
    private String tenLoai;
}
