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
    @JoinColumn(name = "id_loai_khach_hang")
    private LoaiKhachHang loaiKhachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ghe")
    private Ghe ghe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_suat_chieu")
    private SuatChieu suatChieu;

    @Column(name = "ma_ve", length = 50)
    private String maVe;

    @Column(name = "gia_ve_goc", precision = 20, scale = 2)
    private BigDecimal giaVeGoc;

    @Column(name = "loai_ve")
    private Integer loaiVe;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
