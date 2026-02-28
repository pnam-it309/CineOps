package service.cinema.be.core.admin.quanlysuatchieu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdPhimResponse;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdSuatChieuResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;
import service.cinema.be.core.admin.quanlysuatchieu.repository.AdSuatChieuRepository;
import service.cinema.be.core.admin.quanlysuatchieu.service.AdSuatChieuService;
import service.cinema.be.entity.KhungGio;
import service.cinema.be.entity.Phim;
import service.cinema.be.entity.PhongChieu;
import service.cinema.be.entity.SuatChieu;
import service.cinema.be.repository.KhungGioRepository;
import service.cinema.be.repository.PhimRepository;
import service.cinema.be.repository.PhongChieuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdSuatChieuServiceImpl implements AdSuatChieuService {

    private final AdSuatChieuRepository adSuatChieuRepository;
    private final PhimRepository phimRepository;
    private final PhongChieuRepository phongChieuRepository;
    private final KhungGioRepository khungGioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdSuatChieuResponse> getSuatChieu(LocalDate date, String pcId) {
        return adSuatChieuRepository.findByNgayVaPhongNative(date, pcId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdSuatChieuResponse create(AdSuatChieuRequest req) {
        if (adSuatChieuRepository.existsConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getGioBatDau(), req.getGioKetThuc(), "NEW")) {
            throw new IllegalArgumentException("Trùng lịch chiếu hoặc thời gian phim đè lên suất chiếu khác");
        }
        Phim phim = phimRepository.findById(req.getIdPhim()).orElseThrow();
        PhongChieu pc = phongChieuRepository.findById(req.getIdPhongChieu()).orElseThrow();
        
        // Tìm hoặc tạo KhungGio dựa trên giờ bắt đầu và kết thúc
        KhungGio kg = findOrCreateKhungGio(req.getGioBatDau(), req.getGioKetThuc());

        SuatChieu sc = new SuatChieu();
        sc.setPhim(phim);
        sc.setPhongChieu(pc);
        sc.setKhungGio(kg);
        sc.setNgayChieu(req.getNgayChieu());
        sc.setTrangThai(req.getTrangThai());
        sc.setSoGheTrong(0); // Mặc định 0, sẽ được cập nhật sau nếu cần
        return toResponse(adSuatChieuRepository.save(sc));
    }

    @Override
    @Transactional
    public AdSuatChieuResponse update(String id, AdSuatChieuRequest req) {
        SuatChieu sc = adSuatChieuRepository.findById(id).orElseThrow();
        if (adSuatChieuRepository.existsConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getGioBatDau(), req.getGioKetThuc(), id)) {
            throw new IllegalArgumentException("Trùng lịch chiếu hoặc thời gian phim đè lên suất chiếu khác");
        }
        
        Phim phim = phimRepository.findById(req.getIdPhim()).orElseThrow();
        KhungGio kg = findOrCreateKhungGio(req.getGioBatDau(), req.getGioKetThuc());
        
        sc.setPhim(phim);
        sc.setKhungGio(kg);
        sc.setNgayChieu(req.getNgayChieu());
        sc.setTrangThai(req.getTrangThai());
        return toResponse(adSuatChieuRepository.save(sc));
    }

    private KhungGio findOrCreateKhungGio(java.time.LocalTime start, java.time.LocalTime end) {
        return khungGioRepository.findAll().stream()
                .filter(kg -> kg.getGioBatDau().equals(start) && kg.getGioKetThuc().equals(end))
                .findFirst()
                .orElseGet(() -> {
                    KhungGio newKg = new KhungGio();
                    newKg.setGioBatDau(start);
                    newKg.setGioKetThuc(end);
                    newKg.setTenKhungGio(start.toString() + " - " + end.toString());
                    newKg.setTrangThai(1);
                    return khungGioRepository.save(newKg);
                });
    }

    @Override
    @Transactional
    public void delete(String id) {
        adSuatChieuRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdPhimResponse> getPhimDropdown() {
        return phimRepository.findAll().stream()
                .map(p -> AdPhimResponse.builder()
                        .id(p.getId())
                        .tenPhim(p.getTenPhim())
                        .thoiLuong(p.getThoiLuong())
                        .poster(p.getPoster())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdPhongChieuResponse> getPhongChieuDropdown() {
        return phongChieuRepository.findAll().stream()
                .map(pc -> AdPhongChieuResponse.builder()
                        .id(pc.getId())
                        .tenPhong(pc.getTenPhong())
                        .loaiManHinh(pc.getLoaiManHinh())
                        .build())
                .collect(Collectors.toList());
    }

    private AdSuatChieuResponse toResponse(SuatChieu sc) {
        AdSuatChieuResponse res = AdSuatChieuResponse.builder()
                .id(sc.getId())
                .ngayChieu(sc.getNgayChieu())
                .trangThai(sc.getTrangThai())
                .idPhim(sc.getPhim().getId())
                .tenPhim(sc.getPhim().getTenPhim())
                .poster(sc.getPhim().getPoster())
                .idPhongChieu(sc.getPhongChieu().getId())
                .tenPhongChieu(sc.getPhongChieu().getTenPhong())
                .build();
        
        if (sc.getKhungGio() != null) {
            res.setIdKhungGio(sc.getKhungGio().getId());
            res.setTenKhungGio(sc.getKhungGio().getTenKhungGio());
            res.setGioBatDau(sc.getKhungGio().getGioBatDau());
            res.setGioKetThuc(sc.getKhungGio().getGioKetThuc());
        }
        
        return res;
    }
}
