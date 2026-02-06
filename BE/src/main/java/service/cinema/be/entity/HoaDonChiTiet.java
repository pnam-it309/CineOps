package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTiet extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_dich_vu")
    private SanPhamDichVu sanPhamDichVu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "don_gia", precision = 15, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", precision = 15, scale = 2)
    private BigDecimal thanhTien;

}
