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

    @Column(name = "ma_phim", length = 50)
    private String maPhim;

    @Column(name = "thoi_luong")
    private Integer thoiLuong;

    @Column(name = "ngay_khoi_chieu")
    private LocalDate ngayKhoiChieu;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "lich_chieu")
    private String lichChieu;

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

    @Column(name = "gia_phim", precision = 20, scale = 2)
    private BigDecimal giaPhim;

    /**
     * Fix #8 — Loại định dạng phim: "2D", "3D", "IMAX", "4DX"
     * Dùng để tính phụ phí format khi xuất vé.
     */
    @Column(name = "loai_phim", length = 20)
    private String loaiPhim;

    /**
     * Phụ phí theo định dạng (ví dụ: 3D +30k, IMAX +50k)
     * Giả trị 0 nếu không có phụ phí. Chuyn cho Pricing Engine.
     */
    @Column(name = "phu_phi_loai_phim", precision = 10, scale = 2)
    private BigDecimal phuPhiLoaiPhim = java.math.BigDecimal.ZERO;
    @OneToMany(mappedBy = "phim")
    private List<PhimTheLoai> phimTheLoais;


    @OneToMany(mappedBy = "phim")
    private List<SuatChieu> suatChieus = new ArrayList<>();

}
