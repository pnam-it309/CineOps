package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bang_gia")
public class BangGia extends PrimaryEntity {

    @Column(name = "ten_bang_gia", length = 100)
    private String tenBangGia;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "is_default")
    private Boolean isDefault;

    @OneToMany(mappedBy = "bangGia")
    private List<GiaVeChiTiet> giaVeChiTiets = new ArrayList<>();

}
