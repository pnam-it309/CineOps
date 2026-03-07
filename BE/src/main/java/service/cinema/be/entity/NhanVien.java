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
@Table(name = "nhan_vien")
@ExcelSheet(sheetName = "Nhân Viên", headerRow = 0, dataStartRow = 1)
public class NhanVien extends PrimaryEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;

    @ExcelColumn(headerName = "Mã Nhân Viên", order = 0, required = true, maxLength = 50)
    @Column(name = "ma_nhan_vien", length = 50, unique = true)
    private String maNhanVien;

    @ExcelColumn(headerName = "Tên Nhân Viên", order = 1, required = true, maxLength = 100)
    @Column(name = "ten_nhan_vien", length = 100, nullable = false)
    private String tenNhanVien;

    @ExcelColumn(headerName = "CCCD", order = 2, required = false, maxLength = 20)
    @Column(name = "cccd", length = 20)
    private String cccd;

    @ExcelColumn(headerName = "Ngày Sinh", order = 3, required = false, dateFormat = "dd/MM/yyyy")
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @ExcelColumn(headerName = "Quê Quán", order = 4, required = false, maxLength = 255)
    @Column(name = "que_quan", length = 255)
    private String queQuan;

    @ExcelColumn(headerName = "Giới Tính", order = 5, required = false)
    @Column(name = "gioi_tinh")
    private Integer gioiTinh;


    @ExcelColumn(headerName = "Số Điện Thoại", order = 7, required = false, maxLength = 20)
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @ExcelColumn(headerName = "Chức Vụ", order = 8, required = false, maxLength = 100)
    @Column(name = "chuc_vu", length = 100)
    private String chucVu;

    @ExcelColumn(headerName = "Ảnh Nhân Viên", order = 9, required = false, maxLength = 255)
    @Column(name = "anh_nhan_vien", length = 255)
    private String anhNhanVien;

    @ExcelColumn(headerName = "Trạng Thái", order = 10, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;


    @OneToMany(mappedBy = "nhanVien")
    private List<DiaChi> diaChis = new ArrayList<>();

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDon> hoaDons = new ArrayList<>();

}
