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
@Table(name = "the_loai")
public class TheLoai extends PrimaryEntity {

    @Column(name = "ten_the_loai", length = 100, nullable = false)
    private String tenTheLoai;

    @OneToMany(mappedBy = "theLoai")
    private List<PhimTheLoai> phimTheLoais = new ArrayList<>();

}
