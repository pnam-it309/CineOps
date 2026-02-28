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

    @Size(max = 100, message = "Đạo diễn tối đa 100 ký tự.")
    private String daoDien;

    private String dienVien;

    @Size(max = 255, message = "Link trailer tối đa 255 ký tự.")
    private String trailer;

    @Size(max = 255, message = "Link poster tối đa 255 ký tự.")
    private String poster;

    @Size(max = 100, message = "Ngôn ngữ tối đa 100 ký tự.")
    private String ngonNgu;

    @Min(value = 0, message = "Độ tuổi không hợp lệ!")
    private Integer doTuoi;

    private String moTa;

    @DecimalMin(value = "0.0", message = "Đánh giá không hợp lệ!")
    @DecimalMax(value = "10.0", message = "Đánh giá tối đa 10.0.")
    private BigDecimal danhGia;

    @NotNull(message = "Giá vé gốc không được để trống!")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé gốc phải lớn hơn 0.")
    private BigDecimal giaVeGoc;

    private Integer trangThai = 1;

    private List<String> idTheLoais;

}
