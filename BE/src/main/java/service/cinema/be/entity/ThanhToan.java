package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thanh_toan")
public class ThanhToan extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;

    @Column(name = "phuong_thuc_thanh_toan", length = 50)
    private String phuongThucThanhToan;

    @Column(name = "so_tien", precision = 15, scale = 2)
    private BigDecimal soTien;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
