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
@Table(name = "chi_tiet_san_pham_di_kem")
public class ChiTietSanPhamDiKem extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPhamDichVu sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_co")
    private KichCo kichCo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_vi_tinh")
    private DonViTinh donViTinh;

    @Column(name = "gia_tri_dinh_luong")
    private Double giaTriDinhLuong;

    @Column(name = "huong_vi", length = 100)
    private String huongVi;

    @Column(name = "gia_ban", precision = 20, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;
}
