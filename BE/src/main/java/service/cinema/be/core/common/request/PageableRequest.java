package service.cinema.be.core.common.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableRequest {
    private Integer page;
    private Integer size;
    private String orderBy;
    private String sortBy;
}
