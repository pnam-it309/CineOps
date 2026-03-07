package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.entity.base.PrimaryEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ghe")
@ExcelSheet(sheetName = "Ghế", headerRow = 0, dataStartRow = 1)
public class Ghe extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_chieu")
    private PhongChieu phongChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_ghe")
    private LoaiGhe loaiGhe;

    @ExcelColumn(headerName = "Số Ghế", order = 0, required = true, maxLength = 10)
    @Column(name = "so_ghe", length = 10)
    private String soGhe;

    @ExcelColumn(headerName = "Hàng", order = 1, required = true, maxLength = 5)
    @Column(name = "so_hang", length = 5)
    private String soHang;

    @ExcelColumn(headerName = "Cột", order = 2, required = true)
    @Column(name = "so_cot")
    private Integer soCot;

    @ExcelColumn(headerName = "Trạng Thái", order = 3, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "ghe")
    private List<Ve> ves = new ArrayList<>();

}
