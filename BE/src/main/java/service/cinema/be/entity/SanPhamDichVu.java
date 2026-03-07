package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham_di_kem")
@ExcelSheet(sheetName = "Đồ Ăn & Dịch Vụ", headerRow = 0, dataStartRow = 1)
public class SanPhamDichVu extends PrimaryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san_pham")
    private LoaiSanPham loaiSanPham;

    @ExcelColumn(headerName = "Tên Sản Phẩm", order = 0, required = true, maxLength = 150)
    @Column(name = "ten_san_pham", length = 150)
    private String tenSanPham;

    @ExcelColumn(headerName = "Trạng Thái", order = 1, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;

    @ExcelColumn(headerName = "Mô Tả", order = 2, required = false)
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @OneToMany(mappedBy = "sanPham")
    private List<ChiTietSanPhamDiKem> chiTietSanPhamDiKems = new ArrayList<>();

}
