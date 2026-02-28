package service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AdSanPhamDiKemRequest {

    @NotNull(message = "Loại sản phẩm không được để trống")
    private String idLoaiSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 3, max = 50, message = "Tên sản phẩm phải từ 3-50 ký tự")
    private String tenSanPham;

    @Size(min = 3, max = 4000, message = "Mô tả phải từ 3-4000 ký tự")
    private String moTa;

    @Pattern(
            regexp = "^(https?:\\/\\/).+\\.(jpg|jpeg|png|webp)$",
            message = "URL hình ảnh không hợp lệ (phải là http/https và có đuôi jpg/png/webp)"
    )
    private String hinhAnh;

    private Integer trangThai;

    // Danh sách biến thể (Size, Giá, Tồn kho)
    @NotEmpty(message = "Sản phẩm phải có ít nhất một kích cỡ")
    @Valid
    private List<VariantRequest> variants;

    @Getter @Setter
    public static class VariantRequest {
        @NotNull(message = "Kích cỡ không được để trống")
        private String idKichCo;

        @NotNull(message = "Đơn vị tính không được để trống")
        private String idDonViTinh;

        @NotNull(message = "Định lượng không được để trống")
        private Double giaTriDinhLuong;

        @NotNull(message = "Giá bán không được để trống")
        @DecimalMin(value = "0.01", message = "Giá bán phải lớn hơn 0")
        private BigDecimal giaBan;

        @NotNull(message = "Tồn kho không được để trống")
        @Min(value = 0, message = "Tồn kho không được âm")
        private Integer soLuongTon;
    }
}