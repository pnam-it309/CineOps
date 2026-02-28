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
@Table(name = "loai_ngay")
public class LoaiNgay extends PrimaryEntity {

    @Column(name = "ten_loai_ngay", length = 100)
    private String tenLoaiNgay;

    @Column(name = "he_so_ngay", precision = 5, scale = 2)
    private BigDecimal heSoNgay;

    @OneToMany(mappedBy = "loaiNgay")
    private List<GiaVeChiTiet> giaVeChiTiets = new ArrayList<>();

}
