package service.cinema.be.core.excel.batch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.excel.service.PhimImportService;
import service.cinema.be.core.excel.service.NhanVienImportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/excel/batch")
@RequiredArgsConstructor
@Tag(name = "Excel Batch Processing", description = "Asynchronous Excel import using Spring Batch")
@Slf4j
public class ExcelJobController {

    private final JobLauncher jobLauncher;
    private final Job importPhimJob;
    private final Job importNhanVienJob;
    private final PhimImportService phimImportService;
    private final NhanVienImportService nhanVienImportService;

    // Temporary directory for uploaded files
    private static final String TEMP_DIR = "temp-uploads";

    @PostMapping("/phim/import")
    @Operation(summary = "Import Phim data asynchronously", description = "Uploads Excel file and starts background batch job for Phim import")
    public ResponseEntity<Map<String, Object>> importPhimAsync(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("File is empty"));
            }

            if (!isValidExcelFile(file)) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid file format. Please upload an Excel file (.xlsx)"));
            }

            // Save file to temporary location
            String tempFilePath = saveTempFile(file, "phim");
            
            // Start batch job
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", tempFilePath)
                    .addString("jobType", "Phim")
                    .addString("originalFileName", file.getOriginalFilename())
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("uniqueId", UUID.randomUUID().toString())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(importPhimJob, params);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Phim import job started successfully");
            response.put("jobId", jobExecution.getJobId());
            response.put("jobName", jobExecution.getJobInstance().getJobName());
            response.put("status", jobExecution.getStatus().name());
            response.put("startTime", jobExecution.getStartTime());

            log.info("Started Phim import job with ID: {} for file: {}", 
                jobExecution.getJobId(), file.getOriginalFilename());

            return ResponseEntity.accepted().body(response);

        } catch (JobExecutionAlreadyRunningException | 
                 JobRestartException | 
                 JobInstanceAlreadyCompleteException e) {
            log.error("Failed to start Phim import job", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse("Job is already running or completed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error starting Phim import job", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to start import job: " + e.getMessage()));
        }
    }

    @PostMapping("/nhan-vien/import")
    @Operation(summary = "Import NhanVien data asynchronously", description = "Uploads Excel file and starts background batch job for NhanVien import")
    public ResponseEntity<Map<String, Object>> importNhanVienAsync(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("File is empty"));
            }

            if (!isValidExcelFile(file)) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid file format. Please upload an Excel file (.xlsx)"));
            }

            // Save file to temporary location
            String tempFilePath = saveTempFile(file, "nhan-vien");
            
            // Start batch job
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", tempFilePath)
                    .addString("jobType", "NhanVien")
                    .addString("originalFileName", file.getOriginalFilename())
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("uniqueId", UUID.randomUUID().toString())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(importNhanVienJob, params);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "NhanVien import job started successfully");
            response.put("jobId", jobExecution.getJobId());
            response.put("jobName", jobExecution.getJobInstance().getJobName());
            response.put("status", jobExecution.getStatus().name());
            response.put("startTime", jobExecution.getStartTime());

            log.info("Started NhanVien import job with ID: {} for file: {}", 
                jobExecution.getJobId(), file.getOriginalFilename());

            return ResponseEntity.accepted().body(response);

        } catch (JobExecutionAlreadyRunningException | 
                 JobRestartException | 
                 JobInstanceAlreadyCompleteException e) {
            log.error("Failed to start NhanVien import job", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse("Job is already running or completed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error starting NhanVien import job", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to start import job: " + e.getMessage()));
        }
    }

    @GetMapping("/phim/template")
    @Operation(summary = "Download Phim Excel template", description = "Generates and downloads an Excel template for Phim data import")
    public ResponseEntity<Resource> downloadPhimTemplate() {
        try {
            byte[] templateData = phimImportService.generateTemplate();
            ByteArrayResource resource = new ByteArrayResource(templateData);
            
            String fileName = "Phim_Template_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(templateData.length)
                .body(resource);
                
        } catch (IOException e) {
            log.error("Error generating Phim template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nhan-vien/template")
    @Operation(summary = "Download NhanVien Excel template", description = "Generates and downloads an Excel template for NhanVien data import")
    public ResponseEntity<Resource> downloadNhanVienTemplate() {
        try {
            byte[] templateData = nhanVienImportService.generateTemplate();
            ByteArrayResource resource = new ByteArrayResource(templateData);
            
            String fileName = "NhanVien_Template_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(templateData.length)
                .body(resource);
                
        } catch (IOException e) {
            log.error("Error generating NhanVien template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        if (fileName == null) return false;
        
        return (contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) ||
               fileName.toLowerCase().endsWith(".xlsx");
    }

    private String saveTempFile(MultipartFile file, String prefix) throws IOException {
        // Create temp directory if it doesn't exist
        Path tempDir = Path.of(TEMP_DIR);
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".") 
            ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
            : ".xlsx";
        
        String tempFileName = prefix + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + fileExtension;
        Path tempFilePath = tempDir.resolve(tempFileName);

        // Save file
        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Saved uploaded file to temporary location: {}", tempFilePath);
        return tempFilePath.toString();
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
