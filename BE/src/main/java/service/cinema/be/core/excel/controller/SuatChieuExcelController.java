package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.excel.service.SuatChieuImportService;
import service.cinema.be.entity.SuatChieu;

@RestController
@RequestMapping("/api/excel/suat-chieu")
@Tag(name = "Suất Chiếu Excel Management")
@Slf4j
public class SuatChieuExcelController extends BaseExcelController<SuatChieu, String> {

    @Autowired
    public SuatChieuExcelController(SuatChieuImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "SuatChieu";
    }
}
