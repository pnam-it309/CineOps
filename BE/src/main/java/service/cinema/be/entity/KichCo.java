package service.cinema.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kich_co")
public class KichCo extends PrimaryEntity {

    @Column(name = "ten_kich_co", length = 100)
    private String tenKichCo;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
}
