package service.cinema.be.core.admin.quanlykhachhang.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlykhachhang.dto.request.AdKhachHangRequest;
import service.cinema.be.core.admin.quanlykhachhang.service.AdKhachHangService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_PREFIX + "/khach-hang")
@RequiredArgsConstructor
public class AdKhachHangController {
    private final AdKhachHangService adKhachHangService;
    private final service.cinema.be.core.admin.quanlyve.repository.AdLoaiKhachHangRepository loaiKhachHangRepository;

    @GetMapping("/loai-khach-hang")
    public ResponseEntity<?> getLoaiKhachHang() {
        return ResponseEntity.ok(ApiResponse.success(loaiKhachHangRepository.findByTrangThai(1)));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) Integer gioiTinh,
            @RequestParam(required = false) String idLoaiKhachHang,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adKhachHangService.getAllKhachHang(search, trangThai, gioiTinh, idLoaiKhachHang, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(adKhachHangService.getById(id)));
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

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable String id) {
        adKhachHangService.requestPasswordReset(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Yêu cầu đặt lại mật khẩu đã được gửi đến email!"));
    }
}