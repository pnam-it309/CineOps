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
@Table(name = "phan_quyen")
public class PhanQuyen extends PrimaryEntity {

    @Column(name = "ma_phan_quyen", length = 50, unique = true, nullable = false)
    private String maPhanQuyen;

    @Column(name = "ten_vai_tro", length = 100, nullable = false)
    private String tenVaiTro;

    @Column(name = "quyen_han", columnDefinition = "TEXT")
    private String quyenHan;


    @Column(name = "trang_thai")
    private Integer trangThai;


}
