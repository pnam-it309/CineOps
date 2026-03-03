package service.cinema.be.core.admin.quanlyhoadon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlyhoadon.dto.request.AdHoaDonRequest;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonChiTietResponse;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonResponse;
import service.cinema.be.core.admin.quanlyhoadon.service.AdHoaDonService;
import service.cinema.be.entity.HoaDon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/admin/hoa-don")
@RequiredArgsConstructor
public class AdHoaDonController {

    private final AdHoaDonService adHoaDonService;

    /**
     * ==========================
     * THANH TOÁN TẠI QUẦY (POS)
     * ==========================
     */
    @PostMapping("/thanh-toan")
    public ResponseEntity<?> thanhToanHoaDon(@RequestBody @Valid AdHoaDonRequest request) {
        try {
            // Gọi tầng Service để xử lý toàn bộ logic giao dịch
            HoaDon hoaDonDaTao = adHoaDonService.thanhToanHoaDon(request);

            // Trả về HTTP Status 201 (Created) cùng mã hóa đơn để Frontend hiển thị
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Thanh toán thành công! Mã hóa đơn: " + hoaDonDaTao.getMaHoaDon());

        } catch (RuntimeException e) {
            // Bắt lỗi nghiệp vụ (VD: Ghế đã có người đặt, Suất chiếu không tồn tại...)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            // Bắt các lỗi hệ thống không lường trước được
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi hệ thống trong quá trình thanh toán!");
        }
    }
    @GetMapping("")
    public ResponseEntity<Page<AdHoaDonResponse>> getDanhSachHoaDon(
            @RequestParam(defaultValue = "") String tuKhoa,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String kyThoiGian, // Vd: THIS_WEEK, THIS_MONTH
            @RequestParam(required = false) Integer phuongThucThanhToan, // 0: Tiền mặt, 1: CK
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adHoaDonService.timKiemHoaDon(
                tuKhoa, trangThai, minPrice, maxPrice, tuNgay, denNgay,
                kyThoiGian, phuongThucThanhToan, page, size
        ));
    }

    /**
     * LẤY CHI TIẾT HÓA ĐƠN
     */
    @GetMapping("/{id}/chi-tiet")
    public ResponseEntity<List<AdHoaDonChiTietResponse>> getChiTietHoaDon(@PathVariable String id) {
        return ResponseEntity.ok(adHoaDonService.layChiTietHoaDon(id));
    }
}