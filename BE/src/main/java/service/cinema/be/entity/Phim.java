package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
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

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "lich_chieu")
    private String lichChieu;

    @Column(name = "dao_dien", length = 100)
    private String daoDien;

    @Column(name = "dien_vien", columnDefinition = "TEXT")
    private String dienVien;

    @Column(name = "trailer", length = 255)
    private String trailer;

    @Column(name = "poster", length = 255)
    private String poster;

    @Column(name = "ngon_ngu", length = 100)
    private String ngonNgu;

    @Column(name = "do_tuoi")
    private Integer doTuoi;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "danh_gia", precision = 3, scale = 1)
    private BigDecimal danhGia;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "gia_ve_goc", precision = 20, scale = 2)
    private BigDecimal giaVeGoc;

    @OneToMany(mappedBy = "phim", fetch = FetchType.EAGER)
    private List<PhimTheLoai> phimTheLoais;

    @OneToMany(mappedBy = "phim")
    private List<SuatChieu> suatChieus = new ArrayList<>();

}
