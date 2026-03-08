package service.cinema.be.core.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.common.response.CccdDataResponse;
import service.cinema.be.core.common.service.CccdScanService;

@RestController
@RequestMapping("/api/v1/common/scan")
@RequiredArgsConstructor
public class ScanCccdController {

    private final CccdScanService cccdScanService;

    @PostMapping("/cccd")
    public ResponseEntity<ApiResponse<CccdDataResponse>> scanCccd(@RequestParam("file") MultipartFile file) {
        try {
            CccdDataResponse response = cccdScanService.scanCccdQr(file);
            return ResponseEntity.ok(ApiResponse.success(response, "Quét CCCD thành công"));
        } catch (Exception e) {
            // Nếu là lỗi không thấy QR thì trả về 200 với mã lỗi nội bộ để tránh spam console đỏ
            if (e.getMessage().contains("Không phát hiện mã QR")) {
                return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
            }
            return ResponseEntity.status(400).body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
