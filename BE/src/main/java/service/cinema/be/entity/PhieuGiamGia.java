package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.entity.base.PrimaryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_giam_gia")
@ExcelSheet(sheetName = "Phiếu Giảm Giá", headerRow = 0, dataStartRow = 1)
public class PhieuGiamGia extends PrimaryEntity {

    @ExcelColumn(headerName = "Mã Phiếu", order = 0, required = true, maxLength = 50)
    @Column(name = "ma_phieu_giam_gia", length = 50, unique = true)
    private String maPhieuGiamGia;

    @ExcelColumn(headerName = "Tên Phiếu", order = 1, required = true, maxLength = 100)
    @Column(name = "ten_phieu", length = 100)
    private String tenPhieu;

    @ExcelColumn(headerName = "Loại Phiếu (0: Giảm %, 1: Giảm VNĐ)", order = 2, required = true)
    @Column(name = "loai_phieu")
    private Integer loaiPhieu;

    @ExcelColumn(headerName = "Kiểu Phát Hành (0: Công khai, 1: Cá nhân)", order = 3, required = true)
    @Column(name = "kieu_phat_hanh")
    private Integer kieuPhatHanh;

    @ExcelColumn(headerName = "% Giảm", order = 4, required = false)
    @Column(name = "phan_tram_giam_gia", precision = 5, scale = 2)
    private BigDecimal phanTramGiamGia;

    @ExcelColumn(headerName = "Số Tiền Giảm", order = 5, required = false)
    @Column(name = "so_tien_giam", precision = 20, scale = 2)
    private BigDecimal soTienGiam;

    @ExcelColumn(headerName = "Giá Trị HĐ Tối Thiểu", order = 6, required = true)
    @Column(name = "gia_tri_hoa_don_toi_thieu", precision = 20, scale = 2)
    private BigDecimal giaTriHoaDonToiThieu;

    @ExcelColumn(headerName = "Cộng Dồn (0: Không, 1: Có)", order = 7, required = false)
    @Column(name = "co_cho_cong_don")
    private Integer coChoCongDon;

    @ExcelColumn(headerName = "Giảm Tối Đa", order = 8, required = false)
    @Column(name = "giam_toi_da", precision = 20, scale = 2)
    private BigDecimal giamToiDa;

    @ExcelColumn(headerName = "Ngày Bắt Đầu", order = 9, required = true, dateFormat = "dd/MM/yyyy HH:mm")
    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @ExcelColumn(headerName = "Ngày Kết Thúc", order = 10, required = true, dateFormat = "dd/MM/yyyy HH:mm")
    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @ExcelColumn(headerName = "Số Lượng", order = 11, required = true)
    @Column(name = "so_luong")
    private Integer soLuong;

    @ExcelColumn(headerName = "Điều Kiện Áp Dụng", order = 12, required = false)
    @Column(name = "dieu_kien_ap_dung", columnDefinition = "TEXT")
    private String dieuKienApDung;

    @ExcelColumn(headerName = "Ghi Chú", order = 13, required = false)
    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @ExcelColumn(headerName = "Trạng Thái", order = 14, required = true)
    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "phieuGiamGia")
    private List<PhieuGiamGiaChiTiet> phieuGiamGiaChiTiets = new ArrayList<>();

    @OneToMany(mappedBy = "phieuGiamGia")
    private List<HoaDon> hoaDons = new ArrayList<>();

}
