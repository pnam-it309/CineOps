package service.cinema.be.core.admin.quanlynhanvien.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class AdNhanVienRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(max = 100, message = "Tên nhân viên không được vượt quá 100 ký tự")
    private String tenNhanVien;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0[0-9]{9,10}", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số")
    private String soDienThoai;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "[0-9]{12}", message = "CCCD phải đúng 12 chữ số")
    private String cccd;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private LocalDate ngaySinh;

    @Size(max = 255, message = "Quê quán không được vượt quá 255 ký tự")
    private String queQuan;

    @NotNull(message = "Vui lòng chọn giới tính")
    @Min(value = 0, message = "Giới tính không hợp lệ")
    @Max(value = 1, message = "Giới tính không hợp lệ")
    private Integer gioiTinh; // 0: Nữ, 1: Nam

    @NotBlank(message = "Chức vụ không được để trống")
    @Size(max = 100, message = "Chức vụ không được vượt quá 100 ký tự")
    private String chucVu;

    @Size(max = 255, message = "Link ảnh không được vượt quá 255 ký tự")
    private String anhNhanVien;

    @NotBlank(message = "Vui lòng chọn vai trò cho nhân viên")
    private String idPhanQuyen;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 đến 255 ký tự")
    private String matKhau;
}