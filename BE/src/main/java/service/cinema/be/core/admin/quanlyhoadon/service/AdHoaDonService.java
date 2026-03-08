package service.cinema.be.core.admin.quanlyhoadon.service;

import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonChiTietResponse;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonResponse;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdLichSuThanhToanResponse;
import service.cinema.be.entity.HoaDon;
import service.cinema.be.core.admin.quanlyhoadon.dto.request.AdHoaDonRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AdHoaDonService {
    // 1. Chốt đơn thanh toán
    HoaDon thanhToanHoaDon(AdHoaDonRequest request);

    // 2. Tìm kiếm hóa đơn (ĐÃ CẬP NHẬT ĐỦ THAM SỐ)
    Page<AdHoaDonResponse> timKiemHoaDon(
            String tuKhoa,
            Integer trangThai,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDate tuNgay,
            LocalDate denNgay,
            String kyThoiGian,
            Integer phuongThucThanhToan,
            int page,
            int size
    );

    // 3. Lấy chi tiết hóa đơn
    List<AdHoaDonChiTietResponse> layChiTietHoaDon(String idHoaDon);

    AdHoaDonResponse getHoaDonById(String id);

    List<AdLichSuThanhToanResponse> getPaymentHistory(String id);
}