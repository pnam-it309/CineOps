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
@Table(name = "gia_ve_chi_tiet")
public class GiaVeChiTiet extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_ngay")
    private LoaiNgay loaiNgay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_khach_hang")
    private LoaiKhachHang loaiKhachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_ghe")
    private LoaiGhe loaiGhe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khung_gio")
    private KhungGio khungGio;

    @Column(name = "gia_tien", nullable = false)
    private Double giaTien;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
