package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.excel.service.NhanVienImportService;

@RestController
@RequestMapping("/api/excel/nhan-vien")
@Tag(name = "Nhân Viên Excel Management", description = "Excel import/export for Nhân Viên entities")
@Slf4j
public class NhanVienExcelController extends BaseExcelController<service.cinema.be.entity.NhanVien, String> {

    @Autowired
    public NhanVienExcelController(NhanVienImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "NhanVien";
    }

    @GetMapping("/template")
    @Operation(summary = "Download Nhân Viên Excel template", description = "Generates and downloads an Excel template for Nhân Viên data import")
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadTemplate() {
        log.info("Downloading Nhân Viên Excel template");
        return super.downloadTemplate();
    }

    @PostMapping("/import")
    @Operation(summary = "Import Nhân Viên data from Excel", description = "Imports Nhân Viên data from an Excel file with validation and batch processing")
    @Override
    public ResponseEntity<ExcelImportResponse> importExcel(@RequestParam("file") MultipartFile file) {
        log.info("Starting Nhân Viên Excel import for file: {}", file.getOriginalFilename());
        try {
            ResponseEntity<ExcelImportResponse> response = super.importExcel(file);
            if (response.getBody() != null && response.getBody().isSuccess()) {
                log.info("Nhân Viên Excel import completed successfully. Total: {}, Success: {}, Failed: {}", 
                    response.getBody().getImportResult().getTotalRows(),
                    response.getBody().getImportResult().getSuccessCount(),
                    response.getBody().getImportResult().getFailureCount());
            } else {
                log.warn("Nhân Viên Excel import failed: {}", response.getBody() != null ? response.getBody().getMessage() : "Unknown error");
            }
            return response;
        } catch (Exception e) {
            log.error("Error during Nhân Viên Excel import", e);
            throw e;
        }
    }
}
