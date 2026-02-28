package service.cinema.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import service.cinema.be.entity.base.PrimaryEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loai_san_pham_di_kem")
public class LoaiSanPham extends PrimaryEntity {

    @Column(name = "ten_loai", nullable = false, unique = true, length = 100)
    private String tenLoai;

    @JsonIgnore
    @OneToMany(mappedBy = "loaiSanPham")
    private List<SanPhamDichVu> sanPhamDichVus = new ArrayList<>();
}