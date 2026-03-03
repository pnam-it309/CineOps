package service.cinema.be.core.admin.quanlyhoadon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
     * 1. THANH TOÁN TẠI QUẦY (POS)
     * Trả về thông tin hóa đơn vừa tạo để Frontend có thể thực hiện in ngay lập tức.
     */
    @PostMapping("/thanh-toan")
    public ResponseEntity<?> thanhToanHoaDon(@RequestBody @Valid AdHoaDonRequest request) {
        try {
            HoaDon hoaDonDaTao = adHoaDonService.thanhToanHoaDon(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(hoaDonDaTao); // Trả về object thay vì String để FE dễ xử lý

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống trong quá trình thanh toán!");
        }
    }

    /**
     * 2. TÌM KIẾM + LỌC HÓA ĐƠN ĐA NĂNG
     * Tách biệt rõ ràng các tiêu chí lọc. Tất cả tham số đều không bắt buộc để tránh lỗi 400.
     */
    @GetMapping("/tim-kiem")
    public ResponseEntity<Page<AdHoaDonResponse>> getDanhSachHoaDon(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer kenhBanHang,
            @RequestParam(required = false) String kyThoiGian,
            @RequestParam(required = false) Integer phuongThucThanhToan,
            @RequestParam(required = false) String idKhachHang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adHoaDonService.timKiemHoaDon(
                tuKhoa, trangThai, minPrice, maxPrice, tuNgay, denNgay,
                kyThoiGian, phuongThucThanhToan, kenhBanHang, idKhachHang, page, size
        ));
    }

    /**
     * 3. LẤY CHI TIẾT VÉ / ĐỒ ĂN CỦA HÓA ĐƠN
     */
    @GetMapping("/{id}/chi-tiet")
    public ResponseEntity<List<AdHoaDonChiTietResponse>> getChiTietHoaDon(@PathVariable String id) {
        return ResponseEntity.ok(adHoaDonService.layChiTietHoaDon(id));
    }

    /**
     * 4. XUẤT FILE BÁO CÁO EXCEL
     * Sử dụng MediaType.APPLICATION_OCTET_STREAM để trình duyệt hiểu đây là file tải về.
     */
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) Integer kenhBanHang,
            @RequestParam(required = false) String kyThoiGian
    ) {
        // Đảm bảo logic xử lý byte[] đã có trong Service
        byte[] data = adHoaDonService.xuatExcelHoaDon(tuKhoa, trangThai, kenhBanHang, kyThoiGian);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CineOps_HoaDon.xlsx")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}