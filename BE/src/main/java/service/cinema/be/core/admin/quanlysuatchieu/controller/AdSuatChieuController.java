package service.cinema.be.core.admin.quanlysuatchieu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdBatchSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdBatchSuatChieuResult;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdKhungGioResponse;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdPhimResponse;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdSuatChieuResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;
import service.cinema.be.core.admin.quanlysuatchieu.service.AdSuatChieuService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_SUAT_CHIEU)
@RequiredArgsConstructor
public class AdSuatChieuController {

    private final AdSuatChieuService adSuatChieuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdSuatChieuResponse>>> get(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayChieu,
            @RequestParam(required = false) String idPhongChieu) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.getSuatChieu(ngayChieu, idPhongChieu)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdSuatChieuResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.getById(id)));
    }

    @GetMapping("/phim-dropdown")
    public ResponseEntity<ApiResponse<List<AdPhimResponse>>> getPhimDropdown() {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.getPhimDropdown()));
    }

    @GetMapping("/phong-chieu-dropdown")
    public ResponseEntity<ApiResponse<List<AdPhongChieuResponse>>> getPhongChieuDropdown() {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.getPhongChieuDropdown()));
    }

    @GetMapping("/khung-gio-dropdown")
    public ResponseEntity<ApiResponse<List<AdKhungGioResponse>>> getKhungGioDropdown() {

        return ResponseEntity.ok(
                ApiResponse.success(adSuatChieuService.getKhungGioDropdown())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdSuatChieuResponse>> create(@Valid @RequestBody AdSuatChieuRequest req) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdSuatChieuResponse>> update(@PathVariable String id, @Valid @RequestBody AdSuatChieuRequest req) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        adSuatChieuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Tự động sinh hàng loạt suất chiếu theo khoảng ngày + danh sách khung giờ.
     * Overlap check được thực hiện cho từng slot trước khi insert.
     */
    @PostMapping("/generate-batch")
    public ResponseEntity<ApiResponse<AdBatchSuatChieuResult>> generateBatch(
            @Valid @RequestBody AdBatchSuatChieuRequest req) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.generateBatch(req)));
    }

    @GetMapping("/{id}/ghe-status")
    public ResponseEntity<ApiResponse<List<?>>> getGheStatus(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.getGheStatus(id)));
    }
}

