package service.cinema.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "don_vi_tinh")
public class DonViTinh extends SimpleEntity {

    @Column(name = "ten_don_vi_tinh", length = 100)
    private String tenDonViTinh;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
}
