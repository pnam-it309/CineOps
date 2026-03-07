package service.cinema.be.core.admin.quanlysuatchieu.service;

import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdBatchSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdBatchSuatChieuResult;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdPhimResponse;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdSuatChieuResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AdSuatChieuService {
    List<AdSuatChieuResponse> getSuatChieu(LocalDate date, String pcId);
    AdSuatChieuResponse getById(String id);
    AdSuatChieuResponse create(AdSuatChieuRequest req);
    AdSuatChieuResponse update(String id, AdSuatChieuRequest req);
    void delete(String id);
    List<AdPhimResponse> getPhimDropdown();
    List<AdPhongChieuResponse> getPhongChieuDropdown();

    /**
     * Kiểm tra xem suất chiếu có nằm trong khung giờ vàng không
     * Trả về hệ số giá nếu có, null nếu không
     */
    Double getHeSoGiaVang(LocalDate ngayChieu, LocalTime gioBatDau);

    /**
     * Tự động sinh hàng loạt suất chiếu theo khoảng ngày và danh sách khung giờ.
     * Bỏ qua các slot bị trùng lịch phòng, trả về thống kê kết quả.
     */
    AdBatchSuatChieuResult generateBatch(AdBatchSuatChieuRequest req);
}

