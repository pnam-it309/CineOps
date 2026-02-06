package service.cinema.be.entity;

import jakarta.persistence.*;
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
@Table(name = "loai_ghe")
public class LoaiGhe extends PrimaryEntity {

    @Column(name = "ten_loai", length = 50, nullable = false)
    private String tenLoai;

    @Column(name = "phu_phi", precision = 10, scale = 2)
    private BigDecimal phuPhi;

    @OneToMany(mappedBy = "loaiGhe")
    private List<Ghe> ghes = new ArrayList<>();

    @OneToMany(mappedBy = "loaiGhe")
    private List<GiaVeChiTiet> giaVeChiTiets = new ArrayList<>();

}
