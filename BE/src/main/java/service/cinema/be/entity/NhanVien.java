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
@Table(name = "nhan_vien")
public class NhanVien extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phan_quyen")
    private PhanQuyen phanQuyen;

    @Column(name = "ma_nhan_vien", length = 50, unique = true)
    private String maNhanVien;

    @Column(name = "ten_nhan_vien", length = 100, nullable = false)
    private String tenNhanVien;

    @Column(name = "cccd", length = 20)
    private String cccd;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "que_quan", length = 255)
    private String queQuan;

    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "chuc_vu", length = 100)
    private String chucVu;

    @Column(name = "anh_nhan_vien", length = 255)
    private String anhNhanVien;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    @OneToMany(mappedBy = "nhanVien")
    private List<DiaChi> diaChis = new ArrayList<>();

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDon> hoaDons = new ArrayList<>();

}
