package service.cinema.be.core.admin.quanlykhachhang.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.quanlykhachhang.dto.request.AdKhachHangRequest;
import service.cinema.be.core.admin.quanlykhachhang.dto.response.AdKhachHangResponse;
import service.cinema.be.core.admin.quanlykhachhang.service.AdKhachHangService;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AdKhachHangControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdKhachHangService adKhachHangService;

    @Test
    @DisplayName("GET /api/admin/khach-hang should return paginated customers")
    public void testGetAllKhachHang() throws Exception {
        Page<AdKhachHangResponse> page = new PageImpl<>(Collections.emptyList());
        when(adKhachHangService.getAllKhachHang(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/khach-hang")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/khach-hang should create new customer")
    public void testCreateKhachHang() throws Exception {
        AdKhachHangRequest request = new AdKhachHangRequest();
        request.setTenKhachHang("Test Customer");
        request.setEmail("test@example.com");
        request.setSdt("0123456789");
        request.setGioiTinh(1);
        request.setNgaySinh(LocalDate.of(1990, 1, 1));
        request.setTrangThai(1);

        when(adKhachHangService.createKhachHang(any(AdKhachHangRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("PUT /api/v1/admin/khach-hang/{id} should update customer")
    public void testUpdateKhachHang() throws Exception {
        AdKhachHangRequest request = new AdKhachHangRequest();
        request.setTenKhachHang("Updated Customer");
        request.setEmail("test@example.com");
        request.setSdt("0123456789");
        request.setGioiTinh(1);
        request.setNgaySinh(LocalDate.of(1990, 1, 1));
        request.setTrangThai(1);

        when(adKhachHangService.updateKhachHang(anyString(), any(AdKhachHangRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/admin/khach-hang/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/khach-hang/{id}/reset-password should trigger reset password")
    public void testResetPassword() throws Exception {
        mockMvc.perform(post("/api/v1/admin/khach-hang/test-id/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Yêu cầu đặt lại mật khẩu đã được gửi đến email!"));
    }
}
