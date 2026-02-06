package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "danh_gia_dich_vu")
public class DanhGiaDichVu extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "diem_so")
    private Integer diemSo;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "ngay_tao_danh_gia")
    private LocalDateTime ngayTaoDanhGia;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
