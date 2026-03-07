package service.cinema.be.core.admin.quanlysuatchieu.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdBatchSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.service.AdSuatChieuService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdSuatChieuControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdSuatChieuService adSuatChieuService;

    @Test
    @DisplayName("GET /api/v1/admin/suat-chieu should return showtimes")
    public void testGetShowtimes() throws Exception {
        when(adSuatChieuService.getSuatChieu(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/admin/suat-chieu")
                        .param("ngayChieu", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/suat-chieu should create single showtime")
    public void testCreateShowtime() throws Exception {
        AdSuatChieuRequest request = new AdSuatChieuRequest();
        request.setIdPhim("movie-id");
        request.setIdPhongChieu("room-id");
        request.setNgayChieu(LocalDate.now());
        request.setGioBatDau(LocalTime.of(10, 0));
        request.setGioKetThuc(LocalTime.of(12, 0));
        request.setTrangThai(1);

        when(adSuatChieuService.create(any(AdSuatChieuRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/suat-chieu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/suat-chieu/generate-batch should launch batch job")
    public void testBatchGenerate() throws Exception {
        AdBatchSuatChieuRequest request = new AdBatchSuatChieuRequest();
        request.setIdPhim("movie-id");
        request.setIdPhongChieu("room-id");
        request.setTuNgay(LocalDate.now());
        request.setDenNgay(LocalDate.now().plusDays(1));
        request.setDanhSachGioBatDau(Collections.singletonList(LocalTime.of(10, 0)));
        request.setBufferPhut(20);
        request.setTrangThai(1);

        when(adSuatChieuService.generateBatch(any(AdBatchSuatChieuRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/suat-chieu/generate-batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
