package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.excel.service.SanPhamDichVuImportService;
import service.cinema.be.entity.SanPhamDichVu;

@RestController
@RequestMapping("/api/excel/san-pham-dich-vu")
@Tag(name = "Sản Phẩm & Dịch Vụ Excel Management")
@Slf4j
public class SanPhamDichVuExcelController extends BaseExcelController<SanPhamDichVu, String> {

    @Autowired
    public SanPhamDichVuExcelController(SanPhamDichVuImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "SanPhamDichVu";
    }
}
