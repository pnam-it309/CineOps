package service.cinema.be.core.admin.quanlyphim.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AdPhimRequest {

    @NotBlank(message = "Tên phim không được để trống!")
    @Size(max = 255, message = "Tên phim tối đa 255 kí tự")
    private String tenPhim;

    private String maPhim;

    @NotNull(message = "Thời lượng không được để trống!")
    @Min(value = 1, message = "thời lượng phải lớn hơn 0.")
    private Integer thoiLuong;

    private LocalDate ngayKhoiChieu;

    private LocalDate ngayKetThuc;

    // Lịch chiếu: chuỗi các thứ cách nhau dấu phẩy
    // VD: "1,3,5" = T2, T4, T6 | tối đa 5 thứ
    @Pattern(
            regexp = "^([0-6],){0,4}[0-6]$|^$",
            message = "Lịch chiếu không hợp lệ! VD: 1,3,5"
    )
    private String lichChieu;

    @Size(max = 255, message = "Link trailer tối đa 255 ký tự.")
    private String trailer;

    @Size(max = 255, message = "Link poster tối đa 255 ký tự.")
    private String poster;

    @Size(max = 100, message = "Ngôn ngữ tối đa 100 ký tự.")
    private String ngonNgu;

    @Min(value = 0, message = "Độ tuổi không hợp lệ!")
    private Integer doTuoi;

    /**
     * Gắn nhãn độ tuổi theo chuẩn: P (Tất cả), T13, T16, T18
     */
    @Pattern(regexp = "^(P|T13|T16|T18)?$", message = "Nhãn độ tuổi không hợp lệ! Chỉ chấp nhận: P, T13, T16, T18")
    private String nhanDoTuoi;

    /**
     * Cờ hiển thị cảnh báo độ tuổi trên giao diện khách hàng
     */
    private Boolean hienThiCanhBaoDoTuoi = true;

    private String moTa;

    @DecimalMin(value = "0.0", message = "Đánh giá không hợp lệ!")
    @DecimalMax(value = "10.0", message = "Đánh giá tối đa 10.0.")
    private Double danhGia;

    @NotNull(message = "Giá vé gốc không được để trống!")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé gốc phải lớn hơn 0.")
    private Double giaPhim;

    // Định dạng phim: "2D", "3D", "IMAX", "4DX"
    @Pattern(regexp = "^(2D|3D|IMAX|4DX)?$", message = "Định dạng phim không hợp lệ! Chỉ chấp nhận: 2D, 3D, IMAX, 4DX")
    private String loaiPhim = "2D";

    // Phụ phí theo định dạng (3D = +30000đ, IMAX = +50000đ, 2D = 0)
    private Double phuPhiLoaiPhim = 0.0;

    private Integer trangThai = 1;

    private List<String> idTheLoais;

}
