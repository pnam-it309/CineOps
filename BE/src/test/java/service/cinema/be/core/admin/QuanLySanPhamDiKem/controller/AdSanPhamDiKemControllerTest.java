package service.cinema.be.core.admin.QuanLySanPhamDiKem.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.request.AdSanPhamDiKemRequest;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.response.AdSanPhamDiKemResponse;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.service.AdSanPhamDiKemService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdSanPhamDiKemControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdSanPhamDiKemService adSanPhamDiKemService;

    @Test
    @DisplayName("GET /api/v1/admin/san-pham-di-kem should return products list")
    public void testGetAllProducts() throws Exception {
        Page<AdSanPhamDiKemResponse> page = new PageImpl<>(Collections.emptyList());
        when(adSanPhamDiKemService.getAll(any(), any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/san-pham-di-kem")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/san-pham-di-kem should create product with variants")
    public void testCreateProduct() throws Exception {
        AdSanPhamDiKemRequest request = new AdSanPhamDiKemRequest();
        request.setIdLoaiSanPham("category-id");
        request.setTenSanPham("Popcorn");
        request.setMoTa("Delicious popcorn");
        request.setTrangThai(1);

        List<AdSanPhamDiKemRequest.VariantRequest> variants = new ArrayList<>();
        AdSanPhamDiKemRequest.VariantRequest v = new AdSanPhamDiKemRequest.VariantRequest();
        v.setIdKichCo("size-id");
        v.setIdDonViTinh("unit-id");
        v.setGiaTriDinhLuong(300.0);
        v.setGiaBan(new BigDecimal("50000"));
        v.setSoLuongTon(100);
        variants.add(v);
        request.setVariants(variants);

        when(adSanPhamDiKemService.create(any(AdSanPhamDiKemRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/san-pham-di-kem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
