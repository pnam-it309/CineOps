package service.cinema.be.core.admin.quanlyghe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdGheGenerationRequest {
    @NotBlank(message = "ID phòng chiếu không được để trống")
    private String idPhongChieu;

    @NotBlank(message = "ID loại ghế mặc định không được để trống")
    private String idLoaiGheDefault;

    @Min(value = 1, message = "Số hàng phải ít nhất là 1")
    private int rows;

    @Min(value = 1, message = "Số cột phải ít nhất là 1")
    private int cols;
}
