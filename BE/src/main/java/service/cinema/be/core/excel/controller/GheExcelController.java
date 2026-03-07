package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.excel.service.GheImportService;
import service.cinema.be.entity.Ghe;

@RestController
@RequestMapping("/api/excel/ghe")
@Tag(name = "Ghế Excel Management")
@Slf4j
public class GheExcelController extends BaseExcelController<Ghe, String> {

    @Autowired
    public GheExcelController(GheImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "Ghe";
    }
}
