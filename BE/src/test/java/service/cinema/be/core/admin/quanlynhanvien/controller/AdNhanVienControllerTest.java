package service.cinema.be.core.admin.quanlynhanvien.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import service.cinema.be.BaseIntegrationTest;
import service.cinema.be.core.admin.quanlynhanvien.dto.request.AdNhanVienRequest;
import service.cinema.be.core.admin.quanlynhanvien.dto.response.AdNhanVienResponse;
import service.cinema.be.core.admin.quanlynhanvien.service.AdNhanVienService;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdNhanVienControllerTest extends BaseIntegrationTest {

    @MockBean
    private AdNhanVienService adNhanVienService;

    @Test
    @DisplayName("GET /api/v1/admin/nhan-vien should return paginated staff")
    public void testGetAllStaff() throws Exception {
        Page<AdNhanVienResponse> page = new PageImpl<>(Collections.emptyList());
        when(adNhanVienService.getAll(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/nhan-vien")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/nhan-vien should create new employee")
    public void testCreateStaff() throws Exception {
        AdNhanVienRequest request = new AdNhanVienRequest();
        request.setTenNhanVien("John Doe");
        request.setEmail("john@example.com");
        request.setSoDienThoai("0987654321");
        request.setCccd("123456789012");
        request.setNgaySinh(LocalDate.of(1995, 5, 20));
        request.setGioiTinh(1);
        request.setChucVu("Staff");
        request.setIdPhanQuyen("role-id");
        request.setTrangThai(1);

        when(adNhanVienService.create(any(AdNhanVienRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/nhan-vien")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("POST /api/v1/admin/nhan-vien/{id}/reset-password should trigger reset password")
    public void testResetPassword() throws Exception {
        mockMvc.perform(post("/api/v1/admin/nhan-vien/staff-id/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Yêu cầu đặt lại mật khẩu đã được gửi đến email!"));
    }
}
