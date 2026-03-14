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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phim")
@ExcelSheet(sheetName = "Phim", headerRow = 0, dataStartRow = 1)
public class Phim extends PrimaryEntity {

    @ExcelColumn(headerName = "Tên Phim", order = 1, required = true, maxLength = 255)
    @Column(name = "ten_phim", length = 255, nullable = false)
    private String tenPhim;

    @ExcelColumn(headerName = "Mã Phim", order = 0, required = false, maxLength = 50)
    @Column(name = "ma_phim", length = 50)
    private String maPhim;

    @ExcelColumn(headerName = "Thời Lượng (phút)", order = 2, required = true)
    @Column(name = "thoi_luong")
    private Integer thoiLuong;

    @ExcelColumn(headerName = "Ngày Khởi Chiếu", order = 3, required = true, dateFormat = "dd/MM/yyyy")
    @Column(name = "ngay_khoi_chieu")
    private LocalDate ngayKhoiChieu;

    @ExcelColumn(headerName = "Ngày Kết Thúc", order = 4, required = false, dateFormat = "dd/MM/yyyy")
    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @ExcelColumn(headerName = "Lịch Chiếu", order = 5, required = false, maxLength = 500)
    @Column(name = "lich_chieu")
    private String lichChieu;

    @ExcelColumn(headerName = "Đạo Diễn", order = 6, required = false, maxLength = 250)
    @Column(name = "dao_dien", length = 250)
    private String daoDien;

    @ExcelColumn(headerName = "Diễn Viên", order = 7, required = false, maxLength = 350)
    @Column(name = "dien_vien", length = 350)
    private String dienVien;

    @ExcelColumn(headerName = "Trailer", order = 8, required = false, maxLength = 255)
    @Column(name = "trailer", length = 255)
    private String trailer;

    @ExcelColumn(headerName = "Poster", order = 9, required = false, maxLength = 255)
    @Column(name = "poster", length = 255)
    private String poster;

    @ExcelColumn(headerName = "Ngôn Ngữ", order = 10, required = false, maxLength = 100)
    @Column(name = "ngon_ngu", length = 100)
    private String ngonNgu;

    @ExcelColumn(headerName = "Độ Tuổi", order = 11, required = false)
    @Column(name = "do_tuoi")
    private Integer doTuoi;

    /**
     * Gắn nhãn độ tuổi theo chuẩn: P (Tất cả), T13, T16, T18
     * null = Chưa phân loại
     */
    @Column(name = "nhan_do_tuoi", length = 10)
    private String nhanDoTuoi;

    @ExcelColumn(headerName = "Mô Tả", order = 12, required = false)
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @ExcelColumn(headerName = "Đánh Giá", order = 13, required = false)
    @Column(name = "danh_gia")
    private Double danhGia;

    @ExcelColumn(headerName = "Trạng Thái", order = 14, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;

    @ExcelColumn(headerName = "Giá Phim", order = 15, required = false)
    @Column(name = "gia_phim")
    private Double giaPhim;

    /**
     * Định dạng phim: "2D", "3D", "IMAX", "4DX"
     * Dùng để tính phụ phí format khi xuất vé.
     */
    @ExcelColumn(headerName = "Loại Phim", order = 16, required = false, maxLength = 20)
    @Column(name = "loai_phim", length = 20)
    private String loaiPhim;

    /**
     * Cờ hiển thị cảnh báo độ tuổi trên giao diện khách hàng
     * true = Bắt buộc hiển thị pop-up cảnh báo
     */
    @Column(name = "hien_thi_canh_bao_do_tuoi")
    private Boolean hienThiCanhBaoDoTuoi = true;

    /**
     * Phụ phí theo định dạng (ví dụ: 3D +30k, IMAX +50k)
     * Giả trị 0 nếu không có phụ phí. Chuyn cho Pricing Engine.
     */
    @ExcelColumn(headerName = "Phụ Phí Loại Phim", order = 17, required = false)
    @Column(name = "phu_phi_loai_phim")
    private Double phuPhiLoaiPhim = 0.0;
    @OneToMany(mappedBy = "phim")
    private List<PhimTheLoai> phimTheLoais = new ArrayList<>();


    @OneToMany(mappedBy = "phim")
    private List<SuatChieu> suatChieus = new ArrayList<>();

}
