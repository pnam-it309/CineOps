package service.cinema.be.core.admin.quanlyphim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlyphim.dto.request.AdPhimRequest;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimPageResponse;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimResponse;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdTheLoaiResponse;
import service.cinema.be.core.admin.quanlyphim.service.AdPhimService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

import java.util.List;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_PREFIX + "/phim")
@RequiredArgsConstructor
public class AdPhimController {
    private final AdPhimService adPhimService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdPhimPageResponse>> getPhim(
            @RequestParam(required = false) String tenPhim,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) String idTheLoai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                adPhimService.getPhim(tenPhim, trangThai, idTheLoai, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdPhimResponse>> getPhimById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(adPhimService.getPhimById(id)));
    }

    @GetMapping("/the-loai")
    public ResponseEntity<ApiResponse<List<AdTheLoaiResponse>>> getTheLoaiDropdown() {
        return ResponseEntity.ok(ApiResponse.success(adPhimService.getTheLoaiDropdown()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdPhimResponse>> create(@Valid @RequestBody AdPhimRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adPhimService.createPhim(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdPhimResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody AdPhimRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adPhimService.updatePhim(id, request)));
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<AdPhimResponse>> updateTrangThai(
            @PathVariable String id,
            @RequestParam Integer trangThai) {
        return ResponseEntity.ok(ApiResponse.success(adPhimService.updateTrangThai(id, trangThai)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        adPhimService.deletePhim(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

