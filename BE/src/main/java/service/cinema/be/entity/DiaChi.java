package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dia_chi")
public class DiaChi extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @Column(name = "mo_ta_dia_chi", columnDefinition = "TEXT")
    private String moTaDiaChi;

    @Column(name = "thanh_pho_tinh", length = 100)
    private String thanhPhoTinh;

    @Column(name = "phuong_xa", length = 100)
    private String phuongXa;

    @Column(name = "dia_chi_chi_tiet", columnDefinition = "TEXT")
    private String diaChiChiTiet;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
