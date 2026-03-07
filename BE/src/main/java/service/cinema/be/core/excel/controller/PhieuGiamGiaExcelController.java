package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.excel.service.PhieuGiamGiaImportService;
import service.cinema.be.entity.PhieuGiamGia;

@RestController
@RequestMapping("/api/excel/phieu-giam-gia")
@Tag(name = "Phiếu Giảm Giá Excel Management")
@Slf4j
public class PhieuGiamGiaExcelController extends BaseExcelController<PhieuGiamGia, String> {

    @Autowired
    public PhieuGiamGiaExcelController(PhieuGiamGiaImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "PhieuGiamGia";
    }
}
