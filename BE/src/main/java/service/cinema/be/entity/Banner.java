package service.cinema.be.entity;

import jakarta.persistence.*;
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
@Table(name = "banner")
public class Banner extends PrimaryEntity {

    @Column(name = "hinh_anh_url", length = 255)
    private String hinhAnhUrl;

    @Column(name = "lien_ket", length = 255)
    private String lienKet;

    @Column(name = "tieu_de", length = 200)
    private String tieuDe;

    @Column(name = "thu_tu_hien_thi")
    private Integer thuTuHienThi;

    @Column(name = "trang_thai")
    private Integer trangThai;

}
