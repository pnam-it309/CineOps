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
@Table(name = "hoa_don")
public class HoaDon extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "ma_hoa_don", length = 50, unique = true)
    private String maHoaDon;

    @Column(name = "tong_tien", precision = 15, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "so_tien_giam", precision = 15, scale = 2)
    private BigDecimal soTienGiam;


    @Column(name = "tong_tien_thanh_toan", precision = 20, scale = 2)
    private BigDecimal tongTienThanhToan;

    @Column(name = "phuong_thuc_thanh_toan")
    private Integer phuongThucThanhToan;

    @Column(name = "kem_ban_hang")
    private Integer kemBanHang;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;


    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> hoaDonChiTiets = new ArrayList<>();

    @OneToMany(mappedBy = "hoaDon")
    private List<ThanhToan> thanhToans = new ArrayList<>();


}
