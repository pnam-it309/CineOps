package service.cinema.be.core.admin.quanlynhanvien.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlynhanvien.dto.request.AdNhanVienRequest;
import service.cinema.be.core.admin.quanlynhanvien.service.AdNhanVienService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_PREFIX + "/nhan-vien")
@RequiredArgsConstructor
public class AdNhanVienController {

    private final AdNhanVienService adNhanVienService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String idPhanQuyen,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(ApiResponse.success(adNhanVienService.getAll(search, idPhanQuyen, trangThai)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AdNhanVienRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adNhanVienService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @Valid @RequestBody AdNhanVienRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adNhanVienService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        adNhanVienService.delete(id); // Giả định bạn đã thêm hàm delete vào Service
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}