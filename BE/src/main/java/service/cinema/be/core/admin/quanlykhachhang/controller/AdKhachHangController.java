package service.cinema.be.core.admin.quanlykhachhang.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlykhachhang.dto.request.AdKhachHangRequest;
import service.cinema.be.core.admin.quanlykhachhang.dto.response.AdKhachHangResponse;
import service.cinema.be.core.admin.quanlykhachhang.service.AdKhachHangService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

import java.util.List;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_PREFIX + "/khach-hang")
@RequiredArgsConstructor
public class AdKhachHangController {
    private final AdKhachHangService adKhachHangService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String search,
                                    @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(ApiResponse.success(adKhachHangService.getAllKhachHang(search, trangThai)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AdKhachHangRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adKhachHangService.createKhachHang(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody AdKhachHangRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adKhachHangService.updateKhachHang(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        adKhachHangService.deleteKhachHang(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}