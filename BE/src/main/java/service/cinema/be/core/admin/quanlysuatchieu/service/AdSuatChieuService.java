package service.cinema.be.core.admin.quanlysuatchieu.service;

import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdPhimResponse;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdSuatChieuResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;

import java.time.LocalDate;
import java.util.List;

public interface AdSuatChieuService {
    List<AdSuatChieuResponse> getSuatChieu(LocalDate date, String pcId);
    AdSuatChieuResponse create(AdSuatChieuRequest req);
    AdSuatChieuResponse update(String id, AdSuatChieuRequest req);
    void delete(String id);
    List<AdPhimResponse> getPhimDropdown();
    List<AdPhongChieuResponse> getPhongChieuDropdown();
}

