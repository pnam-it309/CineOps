package service.cinema.be.core.admin.QuanLyPhieuGiamGia.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.request.AdPhieuGiamGiaRequest;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.response.AdPhieuGiamGiaResponse;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.service.AdPhieuGiamGiaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdPhieuGiamGiaControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdPhieuGiamGiaService adPhieuGiamGiaService;

    @Test
    @DisplayName("GET /api/v1/admin/phieu-giam-gia should return paginated vouchers")
    public void testGetAllVouchers() throws Exception {
        Page<AdPhieuGiamGiaResponse> page = new PageImpl<>(Collections.emptyList());
        when(adPhieuGiamGiaService.getAll(any(), any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/phieu-giam-gia")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/phieu-giam-gia should create new voucher")
    public void testCreateVoucher() throws Exception {
        AdPhieuGiamGiaRequest request = new AdPhieuGiamGiaRequest();
        request.setMaPhieuGiamGia("VOUCHER2024");
        request.setTenPhieu("New Year Sale");
        request.setLoaiPhieu(1);
        request.setPhanTramGiamGia(new BigDecimal("10"));
        request.setGiaTriHoaDonToiThieu(new BigDecimal("100000"));
        request.setSoLuong(100);
        request.setNgayBatDau(LocalDateTime.now().plusHours(1));
        request.setNgayKetThuc(LocalDateTime.now().plusDays(7));
        request.setTrangThai(1);

        when(adPhieuGiamGiaService.create(any(AdPhieuGiamGiaRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/phieu-giam-gia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
