package service.cinema.be.core.admin.quanlysuatchieu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdSuatChieuResponse {
    private String id;
    private LocalDate ngayChieu;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private Integer trangThai;
    private String idPhim;
    private String tenPhim;
    private String poster;
    private String idPhongChieu;
    private String tenPhongChieu;
    private String idKhungGio;
    private String tenKhungGio;
}
