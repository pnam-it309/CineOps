package service.cinema.be.core.admin.quanlyphim.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdPhimResponse {
    private String id;
    private String tenPhim;
    private Integer thoiLuong;
    private LocalDate ngayKhoiChieu;
    private LocalDate ngayKetThuc;
    private String lichChieu;
    private String daoDien;
    private String dienVien;
    private String trailer;
    private String poster;
    private String ngonNgu;
    private Integer doTuoi;
    private String moTa;
    private BigDecimal danhGia;
    private BigDecimal giaVeGoc;
    private Integer trangThai;
    private List<AdTheLoaiResponse> theLoais;
}
