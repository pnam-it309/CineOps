package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.SimpleEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phim_the_loai")
public class PhimTheLoai extends SimpleEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phim")
    private Phim phim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_the_loai")
    private TheLoai theLoai;

}
