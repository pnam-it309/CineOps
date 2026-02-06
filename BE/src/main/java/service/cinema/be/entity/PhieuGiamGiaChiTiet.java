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
@Table(name = "phieu_giam_gia_chi_tiet")
public class PhieuGiamGiaChiTiet extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "so_luong_su_dung")
    private Integer soLuongSuDung;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
