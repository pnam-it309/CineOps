package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khung_gio")
public class KhungGio extends PrimaryEntity {

    @Column(name = "ten_khung_gio", length = 100)
    private String tenKhungGio;

    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "khungGio")
    private List<GiaVeChiTiet> giaVeChiTiets = new ArrayList<>();

    @OneToMany(mappedBy = "khungGio")
    private List<SuatChieu> suatChieus = new ArrayList<>();

}
