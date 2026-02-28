package service.cinema.be.core.admin.QuanLySanPhamDiKem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.request.AdSanPhamDiKemRequest;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.response.AdSanPhamDiKemResponse;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.service.AdSanPhamDiKemService;

@RestController
@RequestMapping("/api/v1/admin/san-pham-di-kem")
@RequiredArgsConstructor
public class AdSanPhamDiKemController {

    private final AdSanPhamDiKemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdSanPhamDiKemResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(service.getAll(keyword, trangThai, page, size))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdSanPhamDiKemResponse>> create(
            @Valid @RequestBody AdSanPhamDiKemRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(service.create(request))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdSanPhamDiKemResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody AdSanPhamDiKemRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(service.update(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {

        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AdSanPhamDiKemResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(service.getAll(keyword, trangThai, page, size))
        );
    }
}