package service.cinema.be.core.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.common.response.CccdDataResponse;
import service.cinema.be.core.common.service.CccdScanService;

@RestController
@RequestMapping("/api/v1/scan")
@RequiredArgsConstructor
public class ScanCccdController {

    private final CccdScanService cccdScanService;

    @PostMapping("/cccd")
    public ResponseEntity<ApiResponse<CccdDataResponse>> scanCccd(@RequestParam("file") MultipartFile file) {
        try {
            CccdDataResponse response = cccdScanService.scanCccdQr(file);
            return ResponseEntity.ok(ApiResponse.success(response, "Quét CCCD thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
