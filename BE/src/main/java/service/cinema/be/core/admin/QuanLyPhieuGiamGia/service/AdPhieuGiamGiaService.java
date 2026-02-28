//package service.cinema.be.core.admin.QuanLyPhieuGiamGia.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.request.AdPhieuGiamGiaRequest;
//import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.response.AdPhieuGiamGiaResponse;
//import service.cinema.be.core.admin.QuanLyPhieuGiamGia.repository.AdPhieuGiamGiaRepository;
//import service.cinema.be.entity.PhieuGiamGia;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AdPhieuGiamGiaService {
//
//    private final AdPhieuGiamGiaRepository repository;
//
//    @Transactional(readOnly = true)
//    public List<AdPhieuGiamGiaResponse> getAll() {
//        return repository.findAll().stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    @Transactional
//    public AdPhieuGiamGiaResponse create(AdPhieuGiamGiaRequest request) {
//
//        if (repository.existsByMaPhieuGiamGia(request.getMaPhieuGiamGia())) {
//            throw new IllegalArgumentException("Mã phiếu đã tồn tại");
//        }
//
//        PhieuGiamGia p = new PhieuGiamGia();
//        p.setMaPhieuGiamGia(request.getMaPhieuGiamGia());
//        p.setTenPhieu(request.getTenPhieu());
//        p.setLoaiPhieu(request.getLoaiPhieu());
//        p.setPhanTramGiamGia(request.getPhanTramGiamGia());
//        p.setSoTienGiam(request.getSoTienGiam());
//        p.setGiaTriHoaDonToiThieu(request.getGiaTriHoaDonToiThieu());
//        p.setSoLuong(request.getSoLuong());
//        p.setNgayBatDau(request.getNgayBatDau());
//        p.setNgayKetThuc(request.getNgayKetThuc());
//        p.setTrangThai(1);
//
//        return toResponse(repository.save(p));
//    }
//
//    @Transactional
//    public AdPhieuGiamGiaResponse update(String id, AdPhieuGiamGiaRequest request) {
//
//        PhieuGiamGia p = repository.findById(id).orElseThrow();
//
//        p.setTenPhieu(request.getTenPhieu());
//        p.setLoaiPhieu(request.getLoaiPhieu());
//        p.setPhanTramGiamGia(request.getPhanTramGiamGia());
//        p.setSoTienGiam(request.getSoTienGiam());
//        p.setGiaTriHoaDonToiThieu(request.getGiaTriHoaDonToiThieu());
//        p.setSoLuong(request.getSoLuong());
//        p.setNgayBatDau(request.getNgayBatDau());
//        p.setNgayKetThuc(request.getNgayKetThuc());
//
//        return toResponse(repository.save(p));
//    }
//
//    @Transactional
//    public void delete(String id) {
//        repository.deleteById(id);
//    }
//
//    private AdPhieuGiamGiaResponse toResponse(PhieuGiamGia p) {
//        return AdPhieuGiamGiaResponse.builder()
//                .id(p.getId())
//                .maPhieuGiamGia(p.getMaPhieuGiamGia())
//                .tenPhieu(p.getTenPhieu())
//                .loaiPhieu(p.getLoaiPhieu())
//                .phanTramGiamGia(p.getPhanTramGiamGia())
//                .soTienGiam(p.getSoTienGiam())
//                .giaTriHoaDonToiThieu(p.getGiaTriHoaDonToiThieu())
//                .soLuong(p.getSoLuong())
//                .ngayBatDau(p.getNgayBatDau())
//                .ngayKetThuc(p.getNgayKetThuc())
//                .trangThai(p.getTrangThai())
//                .build();
//    }
//
//    @Transactional(readOnly = true)
//    public Page<AdPhieuGiamGiaResponse> getAll(
//            String keyword,
//            Integer trangThai,
//            int page,
//            int size) {
//
//        Pageable pageable = PageRequest.of(
//                page,
//                size,
//                Sort.by(Sort.Direction.DESC, "createdDate") // mới nhất lên đầu
//        );
//
//        return repository.search(keyword, trangThai, pageable)
//                .map(this::toResponse);
//    }
//}
package service.cinema.be.core.admin.QuanLyPhieuGiamGia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.request.AdPhieuGiamGiaRequest;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.dto.response.AdPhieuGiamGiaResponse;
import service.cinema.be.core.admin.QuanLyPhieuGiamGia.repository.AdPhieuGiamGiaRepository;
import service.cinema.be.entity.PhieuGiamGia;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdPhieuGiamGiaService {

    private final AdPhieuGiamGiaRepository repository;

    @Transactional(readOnly = true)
    public Page<AdPhieuGiamGiaResponse> getAll(String keyword, Integer trangThai, int page, int size) {
        // Sắp xếp theo ngày tạo (ngayTao từ AuditEntity) giảm dần để bản ghi mới lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return repository.search(keyword, trangThai, pageable).map(this::toResponse);
    }

    @Transactional
    public AdPhieuGiamGiaResponse create(AdPhieuGiamGiaRequest request) {
        if (repository.existsByMaPhieuGiamGia(request.getMaPhieuGiamGia())) {
            throw new RuntimeException("Mã phiếu đã tồn tại!");
        }
        if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc!");
        }

        PhieuGiamGia p = new PhieuGiamGia();
        mapRequestToEntity(p, request);
        p.setTrangThai(determineStatus(request.getNgayBatDau(), request.getNgayKetThuc()));

        return toResponse(repository.save(p));
    }

    @Transactional
    public AdPhieuGiamGiaResponse update(String id, AdPhieuGiamGiaRequest request) {
        PhieuGiamGia p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));

        mapRequestToEntity(p, request);
        p.setTrangThai(determineStatus(request.getNgayBatDau(), request.getNgayKetThuc()));

        return toResponse(repository.save(p));
    }

    private void mapRequestToEntity(PhieuGiamGia p, AdPhieuGiamGiaRequest request) {
        p.setMaPhieuGiamGia(request.getMaPhieuGiamGia());//không sửa mã
        p.setTenPhieu(request.getTenPhieu());
        p.setLoaiPhieu(request.getLoaiPhieu());
        p.setPhanTramGiamGia(request.getPhanTramGiamGia());
        p.setSoTienGiam(request.getSoTienGiam());
        p.setGiamToiDa(request.getGiamToiDa());
        p.setGiaTriHoaDonToiThieu(request.getGiaTriHoaDonToiThieu());
        p.setSoLuong(request.getSoLuong());
        p.setNgayBatDau(request.getNgayBatDau());
        p.setNgayKetThuc(request.getNgayKetThuc());
    }

    private Integer determineStatus(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(start)) return 0; // Chưa bắt đầu
        if (now.isAfter(end)) return 2;    // Đã kết thúc
        return 1;                         // Đang diễn ra
    }

    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }

    private AdPhieuGiamGiaResponse toResponse(PhieuGiamGia p) {
        return AdPhieuGiamGiaResponse.builder()
                .id(p.getId())
                .maPhieuGiamGia(p.getMaPhieuGiamGia())
                .tenPhieu(p.getTenPhieu())
                .loaiPhieu(p.getLoaiPhieu())
                .phanTramGiamGia(p.getPhanTramGiamGia())
                .soTienGiam(p.getSoTienGiam())
                .giaTriHoaDonToiThieu(p.getGiaTriHoaDonToiThieu())
                .soLuong(p.getSoLuong())
                .ngayBatDau(p.getNgayBatDau())
                .ngayKetThuc(p.getNgayKetThuc())
                .trangThai(p.getTrangThai())
                .build();
    }
}