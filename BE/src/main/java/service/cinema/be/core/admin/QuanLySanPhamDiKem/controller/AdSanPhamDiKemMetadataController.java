package service.cinema.be.core.admin.QuanLySanPhamDiKem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.entity.DonViTinh;
import service.cinema.be.entity.KichCo;
import service.cinema.be.entity.LoaiSanPham;
import service.cinema.be.repository.DonViTinhRepository;
import service.cinema.be.repository.KichCoRepository;
import service.cinema.be.repository.LoaiSanPhamRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/san-pham-di-kem-metadata")
@RequiredArgsConstructor
public class AdSanPhamDiKemMetadataController {

    private final LoaiSanPhamRepository loaiSanPhamRepository;
    private final KichCoRepository kichCoRepository;
    private final DonViTinhRepository dvtRepository;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<LoaiSanPham>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(loaiSanPhamRepository.findAll()));
    }

    @GetMapping("/sizes")
    public ResponseEntity<ApiResponse<List<KichCo>>> getSizes() {
        return ResponseEntity.ok(ApiResponse.success(kichCoRepository.findAll()));
    }

    @GetMapping("/units")
    public ResponseEntity<ApiResponse<List<DonViTinh>>> getUnits() {
        return ResponseEntity.ok(ApiResponse.success(dvtRepository.findAll()));
    }
}
