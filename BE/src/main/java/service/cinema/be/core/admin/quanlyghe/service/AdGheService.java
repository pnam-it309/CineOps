package service.cinema.be.core.admin.quanlyghe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlyghe.dto.request.AdGheRequest;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdGheResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdLoaiGheResponse;
import service.cinema.be.core.admin.quanlyghe.dto.response.AdPhongChieuResponse;
import service.cinema.be.core.admin.quanlyghe.repository.AdGheRepository;
import service.cinema.be.entity.Ghe;
import service.cinema.be.entity.LoaiGhe;
import service.cinema.be.entity.PhongChieu;
import service.cinema.be.repository.LoaiGheRepository;
import service.cinema.be.repository.PhongChieuRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdGheService {

    private final AdGheRepository adGheRepository;
    private final LoaiGheRepository loaiGheRepository;
    private final PhongChieuRepository phongChieuRepository;

    @Transactional(readOnly = true)
    public List<AdGheResponse> getGheByPhongChieu(String idPC) {
        return adGheRepository.findByPhongChieuNative(idPC).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdLoaiGheResponse> getLoaiGheDropdown() {
        return loaiGheRepository.findAll().stream()
                .map(lg -> AdLoaiGheResponse.builder()
                        .id(lg.getId())
                        .tenLoai(lg.getTenLoai())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdPhongChieuResponse> getPhongChieuDropdown() {
        return phongChieuRepository.findAll().stream()
                .map(pc -> AdPhongChieuResponse.builder()
                        .id(pc.getId())
                        .tenRap(pc.getIdRap())
                        .tenPhong(pc.getTenPhong())
                        .loaiManHinh(pc.getLoaiManHinh())
                        .build())
                .toList();
    }

    @Transactional
    public AdGheResponse createGhe(AdGheRequest request) {
        if (adGheRepository.existsBySoGheAndPhongChieuId(request.getSoGhe(), request.getIdPhongChieu())) {
            throw new IllegalArgumentException("Ghế đã tồn tại");
        }
        PhongChieu pc = phongChieuRepository.findById(request.getIdPhongChieu()).orElseThrow();
        LoaiGhe lg = loaiGheRepository.findById(request.getIdLoaiGhe()).orElseThrow();

        Ghe g = new Ghe();
        g.setPhongChieu(pc);
        g.setLoaiGhe(lg);
        g.setSoGhe(request.getSoGhe());
        g.setSoHang(request.getSoHang());
        g.setSoCot(request.getSoCot());
        g.setTrangThai(request.getTrangThai());
        return toResponse(adGheRepository.save(g));
    }

    @Transactional
    public AdGheResponse updateGhe(String id, AdGheRequest request) {
        Ghe g = adGheRepository.findById(id).orElseThrow();
        LoaiGhe lg = loaiGheRepository.findById(request.getIdLoaiGhe()).orElseThrow();
        g.setLoaiGhe(lg);
        g.setSoGhe(request.getSoGhe());
        g.setSoHang(request.getSoHang());
        g.setSoCot(request.getSoCot());
        g.setTrangThai(request.getTrangThai());
        return toResponse(adGheRepository.save(g));
    }

    @Transactional
    public void deleteGhe(String id) {
        adGheRepository.deleteById(id);
    }

    private AdGheResponse toResponse(Ghe g) {
        return AdGheResponse.builder()
                .id(g.getId())
                .soGhe(g.getSoGhe())
                .soHang(g.getSoHang())
                .soCot(g.getSoCot())
                .trangThai(g.getTrangThai())
                .idPhongChieu(g.getPhongChieu().getId())
                .tenPhongChieu(g.getPhongChieu().getTenPhong())
                .idLoaiGhe(g.getLoaiGhe().getId())
                .tenLoaiGhe(g.getLoaiGhe().getTenLoai())
                .phuPhi(g.getLoaiGhe().getPhuPhi())
                .build();
    }
}
