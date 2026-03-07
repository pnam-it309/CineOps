package service.cinema.be.core.excel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.excel.service.PhimImportService;

@RestController
@RequestMapping("/api/excel/phim")
@Tag(name = "Phim Excel Management", description = "Excel import/export for Phim entities")
@Slf4j
public class PhimExcelController extends BaseExcelController<service.cinema.be.entity.Phim, String> {

    @Autowired
    public PhimExcelController(PhimImportService importService) {
        super(importService);
    }

    @Override
    protected String getEntityName() {
        return "Phim";
    }

    @GetMapping("/template")
    @Operation(summary = "Download Phim Excel template", description = "Generates and downloads an Excel template for Phim data import")
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadTemplate() {
        log.info("Downloading Phim Excel template");
        return super.downloadTemplate();
    }

    @PostMapping("/import")
    @Operation(summary = "Import Phim data from Excel", description = "Imports Phim data from an Excel file with validation and batch processing")
    @Override
    public ResponseEntity<ExcelImportResponse> importExcel(@RequestParam("file") MultipartFile file) {
        log.info("Starting Phim Excel import for file: {}", file.getOriginalFilename());
        try {
            ResponseEntity<ExcelImportResponse> response = super.importExcel(file);
            if (response.getBody() != null && response.getBody().isSuccess()) {
                log.info("Phim Excel import completed successfully. Total: {}, Success: {}, Failed: {}", 
                    response.getBody().getImportResult().getTotalRows(),
                    response.getBody().getImportResult().getSuccessCount(),
                    response.getBody().getImportResult().getFailureCount());
            } else {
                log.warn("Phim Excel import failed: {}", response.getBody() != null ? response.getBody().getMessage() : "Unknown error");
            }
            return response;
        } catch (Exception e) {
            log.error("Error during Phim Excel import", e);
            throw e;
        }
    }
}
