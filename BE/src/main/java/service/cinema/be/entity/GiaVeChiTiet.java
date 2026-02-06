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
@Table(name = "gia_ve_chi_tiet")
public class GiaVeChiTiet extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_ghe")
    private LoaiGhe loaiGhe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bang_gia")
    private BangGia bangGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khung_gio")
    private KhungGio khungGio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_ngay")
    private LoaiNgay loaiNgay;

    @Column(name = "gia_tien", precision = 15, scale = 2, nullable = false)
    private BigDecimal giaTien;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "giaVeChiTiet")
    private List<Ve> ves = new ArrayList<>();

}
