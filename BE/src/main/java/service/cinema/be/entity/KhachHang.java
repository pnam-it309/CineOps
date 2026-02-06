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
@Table(name = "khach_hang")
public class KhachHang extends PrimaryEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan", unique = true)
    private TaiKhoan taiKhoan;

    @Column(name = "ma_khach_hang", length = 50, unique = true)
    private String maKhachHang;

    @Column(name = "ten_khach_hang", length = 100, nullable = false)
    private String tenKhachHang;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "khachHang")
    private List<DiaChi> diaChis = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    private List<DanhGiaPhim> danhGiaPhims = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    private List<PhieuGiamGiaChiTiet> phieuGiamGiaChiTiets = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    private List<HoaDon> hoaDons = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    private List<DanhGiaDichVu> danhGiaDichVus = new ArrayList<>();

}
