package service.cinema.be.core.admin.quanlyve.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;
import service.cinema.be.core.admin.quanlyve.dto.response.AdVeResponse;
import service.cinema.be.core.admin.quanlyve.dto.response.AdVeThongKeResponse;
import service.cinema.be.core.admin.quanlyve.repository.AdGiaVeChiTietRepository;
import service.cinema.be.core.admin.quanlyve.repository.AdVeRepository;
import service.cinema.be.core.admin.quanlyve.service.AdVeService;
import service.cinema.be.entity.Ghe;
import service.cinema.be.entity.SuatChieu;
import service.cinema.be.entity.Ve;
import service.cinema.be.repository.GheRepository;
import service.cinema.be.repository.SuatChieuRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdVeServiceImpl implements AdVeService {

    private final AdVeRepository adVeRepository;
    private final AdGiaVeChiTietRepository adGiaVeChiTietRepository;
    private final SuatChieuRepository suatChieuRepository;
    private final GheRepository gheRepository;

    /**
     * ==========================
     * ĐẶT VÉ MỚI
     * ==========================
     */
    @Override
    @Transactional
    public List<AdVeResponse> datVeMoi(AdDatVeRequest request) {

        SuatChieu suatChieu = suatChieuRepository.findById(request.getIdSuatChieu())
                .orElseThrow(() -> new RuntimeException("Suất chiếu không tồn tại"));

        BigDecimal giaGoc = suatChieu.getPhim().getGiaPhim() != null ? 
            java.math.BigDecimal.valueOf(suatChieu.getPhim().getGiaPhim()) : null;
        String idKhungGio = suatChieu.getKhungGio().getId();

        List<Ve> danhSachVe = new ArrayList<>();

        for (AdDatVeRequest.ChiTietGheDat item : request.getDanhSachGhe()) {

            // Kiểm tra ghế đã được đặt chưa
            if (adVeRepository.existsBySuatChieuIdAndGheIdAndTrangThai(
                    suatChieu.getId(),
                    item.getIdGhe(),
                    1
            )) {
                throw new RuntimeException("Ghế đã được đặt!");
            }

            Ghe ghe = gheRepository.findById(item.getIdGhe())
                    .orElseThrow(() -> new RuntimeException("Ghế không tồn tại"));

            Double phuThu = adGiaVeChiTietRepository
                    .timGiaPhuThuTheoTieuChi(
                            item.getIdLoaiKhachHang(),
                            request.getIdLoaiNgay(),
                            ghe.getLoaiGhe().getId(),
                            idKhungGio
                    )
                    .orElse(0.0);

            Ve ve = new Ve();
            ve.setId(UUID.randomUUID().toString());
            ve.setMaVe("VE-" + System.currentTimeMillis() + (int)(Math.random() * 100));
            ve.setSuatChieu(suatChieu);
            ve.setGhe(ghe);
            ve.setGiaThanhToan(giaGoc.add(BigDecimal.valueOf(phuThu)));
            ve.setTrangThai(1); // 1 = đã thanh toán
            ve.setLoaiVe(0);    // 0 = bán tại quầy

            danhSachVe.add(ve);
        }

        return adVeRepository.saveAll(danhSachVe)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * ==========================
     * TÌM KIẾM VÉ
     * ==========================
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdVeResponse> timKiemVe(String tuKhoa, Integer trangThai, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "ngayTao")
        );

        Specification<Ve> specification = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo mã vé
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("maVe")),
                                "%" + tuKhoa.trim().toLowerCase() + "%"
                        )
                );
            }

            // Lọc theo trạng thái
            if (trangThai != null) {
                predicates.add(
                        cb.equal(root.get("trangThai"), trangThai)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return adVeRepository.findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    /**
     * ==========================
     * HỦY VÉ
     * ==========================
     */
    @Override
    @Transactional
    public void huyVe(String idVe) {

        Ve ve = adVeRepository.findById(idVe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

        ve.setTrangThai(0); // 0 = đã hủy
        adVeRepository.save(ve);
    }

    @Override
    @Transactional(readOnly = true)
    public AdVeResponse getById(String id) {
        Ve ve = adVeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));
        return mapToResponse(ve);
    }

    /**
     * ==========================
     * THỐNG KÊ
     * ==========================
     */
    @Override
    public AdVeThongKeResponse getThongKe() {

        long tongVe = adVeRepository.count();
        long veHopLe = adVeRepository.countByTrangThai(1);
        long veDaHuy = adVeRepository.countByTrangThai(0);
        BigDecimal doanhThu = adVeRepository.sumTotalRevenue();

        return new AdVeThongKeResponse(
                tongVe,
                veHopLe,
                veDaHuy,
                doanhThu != null ? doanhThu : BigDecimal.ZERO
        );
    }

    /**
     * ==========================
     * ENTITY → DTO
     * ==========================
     */
    private AdVeResponse mapToResponse(Ve ve) {

        AdVeResponse dto = new AdVeResponse();

        dto.setId(ve.getId());
        dto.setMaVe(ve.getMaVe());

        if (ve.getSuatChieu() != null) {
            if (ve.getSuatChieu().getPhim() != null) {
                dto.setTenPhim(ve.getSuatChieu().getPhim().getTenPhim());
            }
            if (ve.getSuatChieu().getPhongChieu() != null) {
                dto.setTenPhongChieu(
                        ve.getSuatChieu().getPhongChieu().getTenPhong()
                );
            }
        }

        if (ve.getGhe() != null) {
            dto.setViTriGhe(
                    ve.getGhe().getSoHang() + ve.getGhe().getSoGhe()
            );
        }

        dto.setGiaThanhToan(ve.getGiaThanhToan());
        dto.setLoaiVe(ve.getLoaiVe());
        dto.setTrangThai(ve.getTrangThai());
        dto.setNgayTao(ve.getNgayTao());
        
        // Priority: Audited creator field -> fallback string
        dto.setNguoiTao(ve.getNguoiTao() != null ? ve.getNguoiTao() : "Hệ thống");

        if (ve.getLoaiKhachHang() != null) {
            dto.setTenLoaiKhachHang(ve.getLoaiKhachHang().getTenLoai());
        }

        return dto;
    }
}