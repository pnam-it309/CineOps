package service.cinema.be.core.admin.quanlyghe.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AdGheBulkRequest {
    private List<String> ids;
    private String idLoaiGhe;
    private Integer trangThai;
}
