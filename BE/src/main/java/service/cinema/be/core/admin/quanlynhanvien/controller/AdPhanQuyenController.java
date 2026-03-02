package service.cinema.be.core.admin.quanlynhanvien.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.admin.quanlynhanvien.service.AdPhanQuyenService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_ROLES)
@RequiredArgsConstructor
public class AdPhanQuyenController {

    private final AdPhanQuyenService adPhanQuyenService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ApiResponse.success(adPhanQuyenService.getAllActiveRoles()));
    }
}
