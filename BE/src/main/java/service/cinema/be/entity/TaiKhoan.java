package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tai_khoan")
public class TaiKhoan extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phan_quyen")
    private PhanQuyen phanQuyen;

    @Column(name = "ten_tai_khoan", length = 100, unique = true, nullable = false)
    private String tenTaiKhoan;

    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToOne(mappedBy = "taiKhoan")
    private KhachHang khachHang;

    @OneToOne(mappedBy = "taiKhoan")
    private NhanVien nhanVien;

}
