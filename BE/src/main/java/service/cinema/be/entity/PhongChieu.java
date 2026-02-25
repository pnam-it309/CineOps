package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phong_chieu")
public class PhongChieu extends PrimaryEntity {

    @Column(name = "id_rap", length = 36)
    private String idRap;

    @Column(name = "ten_phong", length = 50)
    private String tenPhong;

    @Column(name = "loai_man_hinh", length = 50)
    private String loaiManHinh;

    @Column(name = "tong_ghe")
    private Integer tongGhe;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "phongChieu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ghe> ghes = new ArrayList<>();

    @OneToMany(mappedBy = "phongChieu")
    private List<SuatChieu> suatChieus = new ArrayList<>();

}
