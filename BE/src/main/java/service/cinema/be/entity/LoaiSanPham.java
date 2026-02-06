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
@Table(name = "loai_san_pham")
public class LoaiSanPham extends PrimaryEntity {

    @Column(name = "ten_loai", length = 100)
    private String tenLoai;

    @OneToMany(mappedBy = "loaiSanPham")
    private List<SanPhamDichVu> sanPhamDichVus = new ArrayList<>();

}
