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
@Table(name = "ve", uniqueConstraints = {
    // Chống double booking: cùng ghế + cùng suất chiếu không thể có 2 vé active
    @UniqueConstraint(name = "uk_ve_ghe_suat", columnNames = {"id_ghe", "id_suat_chieu"})
})
public class Ve extends PrimaryEntity {

    // Optimistic Lock — chống race condition khi 2 request cùng lúc
    @Version
    @Column(name = "version")
    private Integer version;

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

    @Column(name = "gia_thanh_toan", precision = 20, scale = 2)
    private BigDecimal giaThanhToan;

    @Column(name = "loai_ve")
    private Integer loaiVe;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
