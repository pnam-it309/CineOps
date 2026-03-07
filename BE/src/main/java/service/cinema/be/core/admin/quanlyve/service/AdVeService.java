package service.cinema.be.core.admin.quanlyve.service;
import org.springframework.data.domain.Page;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;
import service.cinema.be.core.admin.quanlyve.dto.response.AdVeResponse;
import service.cinema.be.core.admin.quanlyve.dto.response.AdVeThongKeResponse;

import java.util.List;

public interface AdVeService {
    // Hàm lấy danh sách vé có phân trang để hiển thị lên UI
    Page<AdVeResponse> timKiemVe(String tuKhoa, Integer trangThai, int page, int size);
    // Hàm thực hiện nghiệp vụ đặt vé và tính tiền
    // Nghiệp vụ chốt đơn đặt vé
    List<AdVeResponse> datVeMoi(AdDatVeRequest request);
    void huyVe(String idVe);
    AdVeResponse getById(String id);
    AdVeThongKeResponse getThongKe();

}
