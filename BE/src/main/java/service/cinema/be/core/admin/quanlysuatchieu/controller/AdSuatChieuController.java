package service.cinema.be.core.admin.quanlysuatchieu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdSuatChieuResponse;
import service.cinema.be.core.admin.quanlysuatchieu.service.AdSuatChieuService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_PREFIX + "/suat-chieu")
@RequiredArgsConstructor
public class AdSuatChieuController {

    private final AdSuatChieuService adSuatChieuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdSuatChieuResponse>>> get(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayChieu,
            @RequestParam(required = false) String idPhongChieu) {
        return ResponseEntity.ok(ApiResponse.success(adSuatChieuService.getSuatChieu(ngayChieu, idPhongChieu)));
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
}
