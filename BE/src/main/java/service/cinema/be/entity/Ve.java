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
@Table(name = "ve")
public class Ve extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ghe")
    private Ghe ghe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_suat_chieu")
    private SuatChieu suatChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gia_ve_chi_tiet")
    private GiaVeChiTiet giaVeChiTiet;

    @Column(name = "gia_ve_thuc_te", precision = 15, scale = 2)
    private BigDecimal giaVeThucTe;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
