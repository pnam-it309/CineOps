package service.cinema.be.core.admin.quanlyve.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;
import service.cinema.be.core.admin.quanlyve.dto.response.AdVeResponse;
import service.cinema.be.core.admin.quanlyve.dto.response.AdVeThongKeResponse;
import service.cinema.be.core.admin.quanlyve.service.AdVeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/ve")
@RequiredArgsConstructor
public class AdVeController {

    private final AdVeService adVeService;

    /**
     * ==========================
     * TÌM KIẾM + PHÂN TRANG
     * ==========================
     */
    @GetMapping("/tim-kiem")
    public ResponseEntity<Page<AdVeResponse>> timKiemVe(
            @RequestParam(defaultValue = "") String tuKhoa,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdVeResponse> result =
                adVeService.timKiemVe(tuKhoa, trangThai, page, size);

        return ResponseEntity.ok(result);
    }

    /**
     * ==========================
     * ĐẶT VÉ
     * ==========================
     */
    @PostMapping("/dat-ve")
    public ResponseEntity<?> datVe(
            @RequestBody @Valid AdDatVeRequest request
    ) {
        try {
            List<AdVeResponse> result =
                    adVeService.datVeMoi(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /**
     * ==========================
     * HỦY VÉ
     * ==========================
     */
    @DeleteMapping("/{id}/huy")
    public ResponseEntity<?> huyVe(@PathVariable String id) {
        try {
            adVeService.huyVe(id);
            return ResponseEntity.ok("Đã hủy vé thành công");

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /**
     * ==========================
     * THỐNG KÊ TỔNG QUAN
     * ==========================
     */
    @GetMapping("/thong-ke-tong-quan")
    public ResponseEntity<AdVeThongKeResponse> getThongKe() {

        AdVeThongKeResponse response =
                adVeService.getThongKe();

        return ResponseEntity.ok(response);
    }
}