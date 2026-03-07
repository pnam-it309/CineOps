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
import service.cinema.be.entity.*;
import service.cinema.be.repository.GheRepository;
import service.cinema.be.repository.SuatChieuRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public Page<AdVeResponse> timKiemVe(
            String tuKhoa, Integer trangThai,
            BigDecimal minPrice, BigDecimal maxPrice,
            LocalDate tuNgay, LocalDate denNgay,
            String kyThoiGian, String sortDir,
            int page, int size) {

        // 1. XỬ LÝ LOGIC "KỲ THỜI GIAN" (Nội suy ra ngày bắt đầu/kết thúc) [cite: 2026-03-04]
        if (kyThoiGian != null && !kyThoiGian.trim().isEmpty()) {
            LocalDate now = LocalDate.now();
            switch (kyThoiGian.toUpperCase()) {
                case "TODAY": tuNgay = now; denNgay = now; break;
                case "THIS_WEEK":
                    tuNgay = now.with(java.time.DayOfWeek.MONDAY);
                    denNgay = now.with(java.time.DayOfWeek.SUNDAY); break;
                case "THIS_MONTH":
                    tuNgay = now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
                    denNgay = now.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()); break;
            }
        }

        // Biến final dùng cho Lambda Specification
        final LocalDate finalTuNgay = tuNgay;
        final LocalDate finalDenNgay = denNgay;

        // 2. THIẾT LẬP PHÂN TRANG & SẮP XẾP ĐỘNG
        Sort.Direction direction = (sortDir != null && sortDir.equalsIgnoreCase("ASC"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "ngayTao"));

        // 3. XÂY DỰNG CÂU TRUY VẤN ĐỘNG (SPECIFICATION)
        Specification<Ve> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // A. TÌM KIẾM ĐA NĂNG: Mã vé | Tên phim | Tên khách | SĐT khách
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String keyword = "%" + tuKhoa.trim().toLowerCase() + "%";

                // Join các bảng liên quan để tìm kiếm
                jakarta.persistence.criteria.Join<Ve, SuatChieu> suatChieuJoin = root.join("suatChieu", jakarta.persistence.criteria.JoinType.LEFT);
                jakarta.persistence.criteria.Join<SuatChieu, Phim> phimJoin = suatChieuJoin.join("phim", jakarta.persistence.criteria.JoinType.LEFT);
                jakarta.persistence.criteria.Join<Ve, HoaDon> hoaDonJoin = root.join("hoaDon", jakarta.persistence.criteria.JoinType.LEFT);
                jakarta.persistence.criteria.Join<HoaDon, KhachHang> khachHangJoin = hoaDonJoin.join("khachHang", jakarta.persistence.criteria.JoinType.LEFT);

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maVe")), keyword),
                        cb.like(cb.lower(phimJoin.get("tenPhim")), keyword),
                        cb.like(cb.lower(khachHangJoin.get("tenKhachHang")), keyword),
                        cb.like(khachHangJoin.get("sdt"), keyword)
                ));
            }

            // B. LỌC TRẠNG THÁI
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            // C. LỌC KHOẢNG GIÁ (Giá thanh toán thực tế của vé)
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("giaThanhToan"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("giaThanhToan"), maxPrice));

            // D. LỌC KHOẢNG THỜI GIAN (Ngày tạo vé) [cite: 2026-03-04]
            if (finalTuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayTao"), finalTuNgay.atStartOfDay()));
            }
            if (finalDenNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayTao"), finalDenNgay.atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 4. THỰC THI VÀ MAPPING SANG DTO
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

        // 1. Thông tin định danh & trạng thái [cite: 2026-03-08]
        dto.setId(ve.getId());
        dto.setMaVe(ve.getMaVe());
        dto.setGiaThanhToan(ve.getGiaThanhToan());
        dto.setTrangThai(ve.getTrangThai());
        dto.setNgayTao(ve.getNgayTao());
        dto.setLoaiVe(ve.getLoaiVe()); // 0: Tại quầy, 1: Online
        dto.setNguoiTao(ve.getNguoiTao() != null ? ve.getNguoiTao() : "Hệ thống");

        // 2. Thông tin Suất chiếu & Thời gian chiếu thực tế [cite: 2026-02-04]
        if (ve.getSuatChieu() != null) {
            dto.setNgayChieu(ve.getSuatChieu().getNgayChieu());
            dto.setGioBatDau(ve.getSuatChieu().getGioBatDau());

            if (ve.getSuatChieu().getPhim() != null) {
                dto.setTenPhim(ve.getSuatChieu().getPhim().getTenPhim());
            }
            if (ve.getSuatChieu().getPhongChieu() != null) {
                dto.setTenPhongChieu(ve.getSuatChieu().getPhongChieu().getTenPhong());
            }
        }

        // 3. Thông tin Ghế & Loại vé (Join Ghe -> LoaiGhe)
        if (ve.getGhe() != null) {
            dto.setViTriGhe(ve.getGhe().getSoGhe());
            if (ve.getGhe().getLoaiGhe() != null) {
                // Hiển thị chuyên nghiệp: "Vé VIP", "Vé Thường", "Vé Couple" [cite: 2026-03-04]
                dto.setTenLoaiVe("Vé " + ve.getGhe().getLoaiGhe().getTenLoai());
            }
        }

        // 4. Thông tin Khách hàng & Hóa đơn (Đã fix lỗi getHoaDon)
        if (ve.getHoaDon() != null) {
            dto.setMaHoaDon(ve.getHoaDon().getMaHoaDon());
            if (ve.getHoaDon().getKhachHang() != null) {
                dto.setTenKhachHang(ve.getHoaDon().getKhachHang().getTenKhachHang());
                dto.setSdtKhachHang(ve.getHoaDon().getKhachHang().getSdt());
            } else {
                dto.setTenKhachHang("Khách lẻ / vãng lai");
            }
        }

        if (ve.getLoaiKhachHang() != null) {
            dto.setTenLoaiKhachHang(ve.getLoaiKhachHang().getTenLoai());
        }

        return dto;
    }
}