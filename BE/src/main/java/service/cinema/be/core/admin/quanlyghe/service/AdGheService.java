package service.cinema.be.core.admin.quanlyghe.service;

import service.cinema.be.core.admin.quanlyghe.dto.request.AdGheGenerationRequest;
import service.cinema.be.core.admin.quanlyghe.dto.request.AdGheRequest;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdGheResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdLoaiGheResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;

import java.util.List;

public interface AdGheService {
    List<AdGheResponse> getGheByPhongChieu(String idPC);
    List<AdLoaiGheResponse> getLoaiGheDropdown();
    List<AdPhongChieuResponse> getPhongChieuDropdown();
    AdGheResponse createGhe(AdGheRequest request);
    void generateGhe(AdGheGenerationRequest request);
    AdGheResponse updateGhe(String id, AdGheRequest request);
    void deleteGhe(String id);
}

