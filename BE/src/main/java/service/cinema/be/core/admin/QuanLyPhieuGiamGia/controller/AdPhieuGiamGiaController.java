package service.cinema.be.core.admin.QuanLyPhieuGiamGia.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.request.AdPhieuGiamGiaRequest;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.response.AdPhieuGiamGiaResponse;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.service.AdPhieuGiamGiaService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/phieu-giam-gia")
@RequiredArgsConstructor
public class AdPhieuGiamGiaController {

    private final AdPhieuGiamGiaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdPhieuGiamGiaResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) Integer loaiPhieu,
            @RequestParam(required = false) Integer kieuPhatHanh,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(service.getAll(keyword, trangThai, loaiPhieu, kieuPhatHanh, startDate, endDate, page, size))
        );
    }
    @PostMapping
    public ResponseEntity<ApiResponse<AdPhieuGiamGiaResponse>> create(
            @Valid @RequestBody AdPhieuGiamGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdPhieuGiamGiaResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdPhieuGiamGiaResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody AdPhieuGiamGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AdPhieuGiamGiaResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) Integer loaiPhieu,
            @RequestParam(required = false) Integer kieuPhatHanh,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(keyword, trangThai, loaiPhieu, kieuPhatHanh, startDate, endDate, page, size)));
    }
}