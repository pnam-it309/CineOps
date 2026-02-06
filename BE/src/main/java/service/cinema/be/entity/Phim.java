package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phim")
public class Phim extends PrimaryEntity {

    @Column(name = "ten_phim", length = 255, nullable = false)
    private String tenPhim;

    @Column(name = "thoi_luong")
    private Integer thoiLuong;

    @Column(name = "ngay_khoi_chieu")
    private LocalDate ngayKhoiChieu;

    @Column(name = "dao_dien", length = 100)
    private String daoDien;

    @Column(name = "dien_vien", columnDefinition = "TEXT")
    private String dienVien;

    @Column(name = "trailer", length = 255)
    private String trailer;

    @Column(name = "poster", length = 255)
    private String poster;

    @Column(name = "do_tuoi_gioi_han")
    private Integer doTuoiGioiHan;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "phim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhimTheLoai> phimTheLoais = new ArrayList<>();

    @OneToMany(mappedBy = "phim")
    private List<DanhGiaPhim> danhGiaPhims = new ArrayList<>();

    @OneToMany(mappedBy = "phim")
    private List<SuatChieu> suatChieus = new ArrayList<>();

}
