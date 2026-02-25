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
@Table(name = "ghe")
public class Ghe extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_chieu")
    private PhongChieu phongChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_ghe")
    private LoaiGhe loaiGhe;

    @Column(name = "so_ghe", length = 10)
    private String soGhe;

    @Column(name = "so_hang", length = 5)
    private String soHang;

    @Column(name = "so_cot")
    private Integer soCot;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "ghe")
    private List<Ve> ves = new ArrayList<>();

}
