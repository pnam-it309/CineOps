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
@Table(name = "rap")
public class Rap extends PrimaryEntity {

    @Column(name = "ten_rap", length = 100, nullable = false)
    private String tenRap;

    @Column(name = "dia_chi", columnDefinition = "TEXT")
    private String diaChi;

    @Column(name = "thanh_pho", length = 100)
    private String thanhPho;

    @OneToMany(mappedBy = "rap")
    private List<PhongChieu> phongChieus = new ArrayList<>();

}
