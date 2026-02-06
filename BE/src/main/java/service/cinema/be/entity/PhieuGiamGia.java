package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia extends PrimaryEntity {

    @Column(name = "ma_phieu_giam_gia", length = 50, unique = true)
    private String maPhieuGiamGia;

    @Column(name = "ten_phieu", length = 100)
    private String tenPhieu;

    @Column(name = "loai_phieu")
    private Integer loaiPhieu;

    @Column(name = "phan_tram_giam_gia", precision = 5, scale = 2)
    private BigDecimal phanTramGiamGia;

    @Column(name = "so_tien_giam", precision = 15, scale = 2)
    private BigDecimal soTienGiam;

    @Column(name = "giam_toi_da", precision = 15, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "dieu_kien_ap_dung", columnDefinition = "TEXT")
    private String dieuKienApDung;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "phieuGiamGia")
    private List<PhieuGiamGiaChiTiet> phieuGiamGiaChiTiets = new ArrayList<>();

    @OneToMany(mappedBy = "phieuGiamGia")
    private List<HoaDon> hoaDons = new ArrayList<>();

}
