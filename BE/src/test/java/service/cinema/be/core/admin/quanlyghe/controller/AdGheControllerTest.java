package service.cinema.be.core.admin.quanlyghe.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.quanlyghe.dto.request.AdGheRequest;
import service.cinema.be.core.admin.quanlyghe.service.AdGheService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdGheControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdGheService adGheService;

    @Test
    @DisplayName("GET /api/v1/admin/ghe should return seats for a room")
    public void testGetSeatsByRoom() throws Exception {
        when(adGheService.getGheByPhongChieu(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/admin/ghe?idPhongChieu=room-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("PUT /api/v1/admin/ghe/{id} should update seat")
    public void testUpdateSeat() throws Exception {
        AdGheRequest request = new AdGheRequest();
        request.setIdLoaiGhe("room-id");
        request.setTrangThai(1);
        request.setSoGhe("A1");
        request.setSoHang("A");
        request.setSoCot(1);
        request.setIdPhongChieu("room-id");

        when(adGheService.updateGhe(anyString(), any(AdGheRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/admin/ghe/seat-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
