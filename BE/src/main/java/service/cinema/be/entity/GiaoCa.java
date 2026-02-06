package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "giao_ca")
public class GiaoCa extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien_trong_ca")
    private NhanVien nhanVienTrongCa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien_ban_giao")
    private NhanVien nhanVienBanGiao;

    @Column(name = "thoi_gian_nhan_ca")
    private LocalDateTime thoiGianNhanCa;

    @Column(name = "thoi_gian_giao_ca")
    private LocalDateTime thoiGianGiaoCa;

    @Column(name = "tien_ban_dau", precision = 15, scale = 2)
    private BigDecimal tienBanDau;

    @Column(name = "tien_mat_trong_ca", precision = 15, scale = 2)
    private BigDecimal tienMatTrongCa;

    @Column(name = "tien_chuyen_khoan", precision = 15, scale = 2)
    private BigDecimal tienChuyenKhoan;

    @Column(name = "tong_tien_thuc_te", precision = 15, scale = 2)
    private BigDecimal tongTienThucTe;

    @Column(name = "chenh_lech", precision = 15, scale = 2)
    private BigDecimal chenhLech;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
