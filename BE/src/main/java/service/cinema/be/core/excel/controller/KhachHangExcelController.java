package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.excel.service.KhachHangImportService;
import service.cinema.be.entity.KhachHang;

@RestController
@RequestMapping("/api/excel/khach-hang")
@Tag(name = "Khách Hàng Excel Management")
@Slf4j
public class KhachHangExcelController extends BaseExcelController<KhachHang, String> {

    @Autowired
    public KhachHangExcelController(KhachHangImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "KhachHang";
    }
}
