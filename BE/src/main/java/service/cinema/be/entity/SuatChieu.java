package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suat_chieu")
@ExcelSheet(sheetName = "Suất Chiếu", headerRow = 0, dataStartRow = 1)
public class SuatChieu extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phim")
    private Phim phim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_chieu")
    private PhongChieu phongChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khung_gio")
    private KhungGio khungGio;

    @ExcelColumn(headerName = "Ngày Chiếu", order = 0, required = true, dateFormat = "dd/MM/yyyy")
    @Column(name = "ngay_chieu")
    private LocalDate ngayChieu;

    @ExcelColumn(headerName = "Giờ Bắt Đầu", order = 1, required = true, dateFormat = "HH:mm")
    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;

    @ExcelColumn(headerName = "Giờ Kết Thúc", order = 2, required = true, dateFormat = "HH:mm")
    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;

    /**
     * Thời gian dọn vệ sinh phòng chiếu (phút)
     * Mặc định = 15 phút
     */
    @ExcelColumn(headerName = "Dọn Vệ Sinh (phút)", order = 3, required = false)
    @Column(name = "thoi_gian_don_ve_sinh")
    private Integer thoiGianDonVeSinh = 15;

    @ExcelColumn(headerName = "Số Ghế Trống", order = 4, required = false)
    @Column(name = "so_ghe_trong")
    private Integer soGheTrong;

    @ExcelColumn(headerName = "Trạng Thái", order = 5, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "suatChieu")
    private List<Ve> ves = new ArrayList<>();

}
