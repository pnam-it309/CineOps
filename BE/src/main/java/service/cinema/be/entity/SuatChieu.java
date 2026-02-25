package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suat_chieu")
public class SuatChieu extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phim")
    private Phim phim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_chieu")
    private PhongChieu phongChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khung_gio")
    private KhungGio khungGio;

    @Column(name = "ngay_chieu")
    private LocalDate ngayChieu;

    @Column(name = "so_ghe_trong")
    private Integer soGheTrong;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "suatChieu")
    private List<Ve> ves = new ArrayList<>();

}
