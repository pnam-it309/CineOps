package service.cinema.be.core.admin.quanlyphim.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlyphim.dto.request.AdPhimRequest;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimPageResponse;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimResponse;
import service.cinema.be.core.admin.quanlyphim.dto.response.AdTheLoaiResponse;
import service.cinema.be.core.admin.quanlyphim.repository.AdPhimRepository;
import service.cinema.be.core.admin.quanlyphim.repository.AdPhimTheLoaiRepository;
import service.cinema.be.core.admin.quanlyphim.repository.AdTheLoaiRepository;
import service.cinema.be.entity.Phim;
import service.cinema.be.entity.PhimTheLoai;
import service.cinema.be.entity.TheLoai;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdPhimService {

    private final AdPhimRepository adPhimRepository;
    private final AdTheLoaiRepository adTheLoaiRepository;
    private final AdPhimTheLoaiRepository adPhimTheLoaiRepository;

    // ==================== DANH SÁCH + TÌM KIẾM ====================

    @Transactional(readOnly = true)
    public AdPhimPageResponse getPhim(String tenPhim, Integer trangThai, String idTheLoai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Phim> pageResult = adPhimRepository.searchPhim(
                (tenPhim != null && tenPhim.isBlank()) ? null : tenPhim,
                trangThai,
                (idTheLoai != null && idTheLoai.isBlank()) ? null : idTheLoai,
                pageable
        );
        List<AdPhimResponse> content = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
        return AdPhimPageResponse.builder()
                .content(content)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public AdPhimResponse getPhimById(String id) {
        Phim phim = adPhimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
        return toResponse(phim);
    }

    // ==================== DROPDOWN THỂ LOẠI ====================

    @Transactional(readOnly = true)
    public List<AdTheLoaiResponse> getTheLoaiDropdown() {
        return adTheLoaiRepository.findAll().stream()
                .map(tl -> AdTheLoaiResponse.builder()
                        .id(tl.getId())
                        .tenTheLoai(tl.getTenTheLoai())
                        .build())
                .toList();
    }

    // ==================== THÊM PHIM ====================

    @Transactional
    public AdPhimResponse createPhim(AdPhimRequest request) {
        if (adPhimRepository.existsByTenPhimIgnoreCase(request.getTenPhim())) {
            throw new IllegalArgumentException("Tên phim đã tồn tại");
        }
        Phim phim = new Phim();
        mapRequestToEntity(request, phim);
        Phim saved = adPhimRepository.save(phim);
        savePhimTheLoai(saved, request.getIdTheLoais());
        return toResponse(adPhimRepository.findById(saved.getId()).orElseThrow());
    }

    // ==================== SỬA PHIM ====================

    @Transactional
    public AdPhimResponse updatePhim(String id, AdPhimRequest request) {
        Phim phim = adPhimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
        if (adPhimRepository.existsByTenPhimIgnoreCaseAndIdNot(request.getTenPhim(), id)) {
            throw new IllegalArgumentException("Tên phim đã tồn tại");
        }
        mapRequestToEntity(request, phim);
        adPhimRepository.save(phim);
        adPhimTheLoaiRepository.deleteByPhimId(id);
        savePhimTheLoai(phim, request.getIdTheLoais());
        return toResponse(adPhimRepository.findById(id).orElseThrow());
    }

    // ==================== ĐỔI TRẠNG THÁI ====================

    @Transactional
    public AdPhimResponse updateTrangThai(String id, Integer trangThai) {
        Phim phim = adPhimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
        phim.setTrangThai(trangThai);
        return toResponse(adPhimRepository.save(phim));
    }

    // ==================== XÓA PHIM ====================

    @Transactional
    public void deletePhim(String id) {
        if (!adPhimRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy phim với id: " + id);
        }
        adPhimTheLoaiRepository.deleteByPhimId(id);
        adPhimRepository.deleteById(id);
    }

    // ==================== HELPER ====================

    private void mapRequestToEntity(AdPhimRequest request, Phim phim) {
        phim.setTenPhim(request.getTenPhim());
        phim.setThoiLuong(request.getThoiLuong());
        phim.setNgayKhoiChieu(request.getNgayKhoiChieu());
        phim.setNgayKetThuc(request.getNgayKetThuc());
        phim.setLichChieu(request.getLichChieu());
        phim.setDaoDien(request.getDaoDien());
        phim.setDienVien(request.getDienVien());
        phim.setTrailer(request.getTrailer());
        phim.setPoster(request.getPoster());
        phim.setNgonNgu(request.getNgonNgu());
        phim.setDoTuoi(request.getDoTuoi());
        phim.setMoTa(request.getMoTa());
        phim.setDanhGia(request.getDanhGia());
        phim.setGiaVeGoc(request.getGiaVeGoc()); // giaVeGoc
        phim.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);
    }

    private void savePhimTheLoai(Phim phim, List<String> idTheLoais) {
        if (idTheLoais == null || idTheLoais.isEmpty()) return;
        for (String idTL : idTheLoais) {
            TheLoai theLoai = adTheLoaiRepository.findById(idTL)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại: " + idTL));
            PhimTheLoai ptl = new PhimTheLoai();
            ptl.setPhim(phim);
            ptl.setTheLoai(theLoai);
            adPhimTheLoaiRepository.save(ptl);
        }
    }

    private AdPhimResponse toResponse(Phim phim) {
        List<AdTheLoaiResponse> theLoais = phim.getPhimTheLoais().stream()
                .map(ptl -> AdTheLoaiResponse.builder()
                        .id(ptl.getTheLoai().getId())
                        .tenTheLoai(ptl.getTheLoai().getTenTheLoai())
                        .build())
                .toList();
        return AdPhimResponse.builder()
                .id(phim.getId())
                .tenPhim(phim.getTenPhim())
                .thoiLuong(phim.getThoiLuong())
                .ngayKhoiChieu(phim.getNgayKhoiChieu())
                .ngayKetThuc(phim.getNgayKetThuc())  // ngay kett thuc
                .lichChieu(phim.getLichChieu()) // lich chieu
                .daoDien(phim.getDaoDien())
                .dienVien(phim.getDienVien())
                .trailer(phim.getTrailer())
                .poster(phim.getPoster())
                .ngonNgu(phim.getNgonNgu())
                .doTuoi(phim.getDoTuoi())
                .moTa(phim.getMoTa())
                .danhGia(phim.getDanhGia())
                .giaVeGoc(phim.getGiaVeGoc()) // giaVeGoc
                .trangThai(phim.getTrangThai())
                .theLoais(theLoais)
                .build();
    }
}