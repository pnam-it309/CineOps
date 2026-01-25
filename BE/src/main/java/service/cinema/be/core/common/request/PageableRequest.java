package service.cinema.be.core.common.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableRequest {
    private int page = 1;
    private int size = 10;
    private String orderBy = "DESC";
    private String sortBy = "createdDate";
}
