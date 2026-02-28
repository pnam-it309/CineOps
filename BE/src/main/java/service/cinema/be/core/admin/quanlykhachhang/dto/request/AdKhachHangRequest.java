package service.cinema.be.core.admin.quanlykhachhang.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class AdKhachHangRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 100, message = "Tên không được quá 100 ký tự")
    private String tenKhachHang;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải là 10 chữ số và bắt đầu bằng số 0")
    private String sdt;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Integer gioiTinh;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate ngaySinh;

    private String idLoaiKhachHang;

    private String hinhAnh;

    private String ghiChu;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;
}