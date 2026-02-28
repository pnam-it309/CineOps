package service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdPhieuGiamGiaResponse {

    private String id;
    private String maPhieuGiamGia;
    private String tenPhieu;
    private Integer loaiPhieu;

    private BigDecimal phanTramGiamGia;
    private BigDecimal soTienGiam;
    private BigDecimal giaTriHoaDonToiThieu;

    private Integer soLuong;

    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;

    private Integer trangThai;
}