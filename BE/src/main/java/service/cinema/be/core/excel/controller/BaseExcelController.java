package service.cinema.be.core.excel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.excel.ExcelImportResult;
import service.cinema.be.core.excel.service.BaseImportService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public abstract class BaseExcelController<T, ID> {
    
    protected final BaseImportService<T, ID> importService;
    
    public BaseExcelController(BaseImportService<T, ID> importService) {
        this.importService = importService;
    }
    
    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() {
        try {
            byte[] templateData = importService.generateTemplate();
            ByteArrayResource resource = new ByteArrayResource(templateData);
            
            String fileName = getEntityName() + "_Template_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(templateData.length)
                .body(resource);
                
        } catch (IOException e) {
            log.error("Error generating template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/import")
    public ResponseEntity<ExcelImportResponse> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ExcelImportResponse(false, "File is empty", null));
            }
            
            if (!isValidExcelFile(file)) {
                return ResponseEntity.badRequest()
                    .body(new ExcelImportResponse(false, "Invalid file format. Please upload an Excel file (.xlsx)", null));
            }
            
            // Process import
            ExcelImportResult<T> result = importService.importFromExcel(file);
            
            ExcelImportResponse response = new ExcelImportResponse(
                true, 
                "Import completed successfully", 
                result
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error importing Excel file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExcelImportResponse(false, "Error during import: " + e.getMessage(), null));
        }
    }
    
    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        if (fileName == null) return false;
        
        return (contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) ||
               fileName.toLowerCase().endsWith(".xlsx");
    }
    
    protected abstract String getEntityName();
    
    public static class ExcelImportResponse {
        private boolean success;
        private String message;
        private ExcelImportResult<?> importResult;
        
        public ExcelImportResponse(boolean success, String message, ExcelImportResult<?> importResult) {
            this.success = success;
            this.message = message;
            this.importResult = importResult;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public ExcelImportResult<?> getImportResult() { return importResult; }
        public void setImportResult(ExcelImportResult<?> importResult) { this.importResult = importResult; }
    }
}
