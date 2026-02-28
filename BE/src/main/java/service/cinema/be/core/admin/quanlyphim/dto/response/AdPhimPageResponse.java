package service.cinema.be.core.admin.quanlyphim.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdPhimPageResponse {
    private List<AdPhimResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
