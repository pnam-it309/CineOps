package service.cinema.be.core.admin.quanlyphim.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.quanlyphim.dto.request.AdPhimRequest;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimPageResponse;
import service.cinema.be.core.admin.quanlyphim.service.AdPhimService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdPhimControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdPhimService adPhimService;

    @Test
    @DisplayName("GET /api/v1/admin/phim should return paginated movies")
    public void testGetPhim() throws Exception {
        AdPhimPageResponse response = new AdPhimPageResponse();
        response.setContent(Collections.emptyList());
        response.setTotalElements(0L);

        when(adPhimService.getPhim(any(), any(), any(), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/phim")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/phim should create new movie")
    public void testCreatePhim() throws Exception {
        AdPhimRequest request = new AdPhimRequest();
        request.setTenPhim("Avengers: Endgame");
        request.setThoiLuong(181);
        request.setGiaPhim(75000.0);
        request.setLoaiPhim("2D");
        request.setNhanDoTuoi("T13");
        request.setDoTuoi(13);
        request.setTrangThai(1);

        when(adPhimService.createPhim(any(AdPhimRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/phim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("PATCH /api/v1/admin/phim/{id}/trang-thai should update status")
    public void testUpdateTrangThai() throws Exception {
        when(adPhimService.updateTrangThai(anyString(), anyInt())).thenReturn(null);

        mockMvc.perform(patch("/api/v1/admin/phim/movie-id/trang-thai")
                        .param("trangThai", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
