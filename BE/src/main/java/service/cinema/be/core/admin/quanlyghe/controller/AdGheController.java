package service.cinema.be.core.admin.quanlyghe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlyghe.dto.request.AdGheRequest;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdGheResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdLoaiGheResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;
import service.cinema.be.core.admin.quanlyghe.service.AdGheService;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;

import java.util.List;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_PREFIX + "/ghe")
@RequiredArgsConstructor
public class AdGheController {

    private final AdGheService adGheService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdGheResponse>>> getGhe(
            @RequestParam(required = false) String idPhongChieu) {
        return ResponseEntity.ok(ApiResponse.success(adGheService.getGheByPhongChieu(idPhongChieu)));
    }

    @GetMapping("/loai-ghe")
    public ResponseEntity<ApiResponse<List<AdLoaiGheResponse>>> getLoaiGhe() {
        return ResponseEntity.ok(ApiResponse.success(adGheService.getLoaiGheDropdown()));
    }

    @GetMapping("/phong-chieu")
    public ResponseEntity<ApiResponse<List<AdPhongChieuResponse>>> getPhongChieu() {
        return ResponseEntity.ok(ApiResponse.success(adGheService.getPhongChieuDropdown()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdGheResponse>> create(@Valid @RequestBody AdGheRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adGheService.createGhe(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdGheResponse>> update(@PathVariable String id, @Valid @RequestBody AdGheRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adGheService.updateGhe(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        adGheService.deleteGhe(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
