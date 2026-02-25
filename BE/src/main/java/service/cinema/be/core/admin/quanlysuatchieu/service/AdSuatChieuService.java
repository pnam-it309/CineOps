package service.cinema.be.core.admin.quanlysuatchieu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdSuatChieuResponse;
import service.cinema.be.core.admin.quanlysuatchieu.repository.AdSuatChieuRepository;
import service.cinema.be.entity.KhungGio;
import service.cinema.be.entity.Phim;
import service.cinema.be.entity.PhongChieu;
import service.cinema.be.entity.SuatChieu;
import service.cinema.be.repository.KhungGioRepository;
import service.cinema.be.repository.PhimRepository;
import service.cinema.be.repository.PhongChieuRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdSuatChieuService {

    private final AdSuatChieuRepository adSuatChieuRepository;
    private final PhimRepository phimRepository;
    private final PhongChieuRepository phongChieuRepository;
    private final KhungGioRepository khungGioRepository;

    @Transactional(readOnly = true)
    public List<AdSuatChieuResponse> getSuatChieu(LocalDate date, String pcId) {
        return adSuatChieuRepository.findByNgayVaPhongNative(date, pcId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdSuatChieuResponse create(AdSuatChieuRequest req) {
        if (adSuatChieuRepository.existsConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getIdKhungGio(), "NEW")) {
            throw new IllegalArgumentException("Trùng lịch chiếu");
        }
        Phim phim = phimRepository.findById(req.getIdPhim()).orElseThrow();
        PhongChieu pc = phongChieuRepository.findById(req.getIdPhongChieu()).orElseThrow();
        KhungGio kg = khungGioRepository.findById(req.getIdKhungGio()).orElseThrow();

        SuatChieu sc = new SuatChieu();
        sc.setPhim(phim);
        sc.setPhongChieu(pc);
        sc.setKhungGio(kg);
        sc.setNgayChieu(req.getNgayChieu());
        sc.setTrangThai(req.getTrangThai());
        return toResponse(adSuatChieuRepository.save(sc));
    }

    @Transactional
    public AdSuatChieuResponse update(String id, AdSuatChieuRequest req) {
        SuatChieu sc = adSuatChieuRepository.findById(id).orElseThrow();
        if (adSuatChieuRepository.existsConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getIdKhungGio(), id)) {
            throw new IllegalArgumentException("Trùng lịch chiếu");
        }
        Phim phim = phimRepository.findById(req.getIdPhim()).orElseThrow();
        KhungGio kg = khungGioRepository.findById(req.getIdKhungGio()).orElseThrow();
        
        sc.setPhim(phim);
        sc.setKhungGio(kg);
        sc.setNgayChieu(req.getNgayChieu());
        sc.setTrangThai(req.getTrangThai());
        return toResponse(adSuatChieuRepository.save(sc));
    }

    @Transactional
    public void delete(String id) {
        adSuatChieuRepository.deleteById(id);
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
