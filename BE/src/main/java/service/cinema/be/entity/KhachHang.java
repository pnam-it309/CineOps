package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.entity.base.PrimaryEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khach_hang")
@ExcelSheet(sheetName = "Khách Hàng", headerRow = 0, dataStartRow = 1)
public class KhachHang extends PrimaryEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_khach_hang")
    private LoaiKhachHang loaiKhachHang;

    @ExcelColumn(headerName = "Mã Khách Hàng", order = 0, required = false, maxLength = 50)
    @Column(name = "ma_khach_hang", length = 50, unique = true)
    private String maKhachHang;

    @ExcelColumn(headerName = "Tên Khách Hàng", order = 1, required = true, maxLength = 100)
    @Column(name = "ten_khach_hang", length = 100, nullable = false)
    private String tenKhachHang;


    @ExcelColumn(headerName = "Số Điện Thoại", order = 2, required = false, maxLength = 20)
    @Column(name = "sdt", length = 20)
    private String sdt;

    @ExcelColumn(headerName = "Giới Tính (0: Nữ, 1: Nam)", order = 3, required = false)
    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @ExcelColumn(headerName = "Ngày Sinh", order = 4, required = false, dateFormat = "dd/MM/yyyy")
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;

    @ExcelColumn(headerName = "Ghi Chú", order = 5, required = false)
    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @ExcelColumn(headerName = "Trạng Thái", order = 6, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;


    @OneToMany(mappedBy = "khachHang")
    private List<DiaChi> diaChis = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    private List<PhieuGiamGiaChiTiet> phieuGiamGiaChiTiets = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    private List<HoaDon> hoaDons = new ArrayList<>();

}
