package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham_di_kem")
public class SanPhamDichVu extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san_pham")
    private LoaiSanPham loaiSanPham;

    @Column(name = "ten_san_pham", length = 150)
    private String tenSanPham;

    @Column(name = "gia_tien", precision = 15, scale = 2)
    private BigDecimal giaTien;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @OneToMany(mappedBy = "sanPham")
    private List<ChiTietSanPhamDiKem> chiTietSanPhamDiKems = new ArrayList<>();

}
