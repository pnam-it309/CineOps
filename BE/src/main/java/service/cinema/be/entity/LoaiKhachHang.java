package service.cinema.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "loai_khach_hang")
public class LoaiKhachHang extends PrimaryEntity {

    @Column(name = "ten_loai", length = 100)
    private String tenLoai;

    @Column(name = "he_so_giam_gia", precision = 5, scale = 2)
    private BigDecimal heSoGiamGia;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "loaiKhachHang")
    private List<KhachHang> khachHangs = new ArrayList<>();

    @OneToMany(mappedBy = "loaiKhachHang")
    private List<Ve> ves = new ArrayList<>();
}
