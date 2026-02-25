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
@Table(name = "lich_su_thanh_toan")
public class LichSuThanhToan extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thanh_toan")
    private ThanhToan thanhToan;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "thoi_gian")
    private java.time.LocalDateTime thoiGian;

    @Column(name = "ma_phan_hoi", length = 255)
    private String maPhanHoi;

    @Column(name = "loai_thao_tac", length = 255)
    private String loaiThaoTac;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;
}
