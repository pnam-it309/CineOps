package service.cinema.be.core.admin.quanlyhoadon.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlyhoadon.dto.request.AdHoaDonRequest;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonChiTietResponse;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonResponse;
import service.cinema.be.core.admin.quanlyhoadon.repository.*;
import service.cinema.be.core.admin.quanlyhoadon.service.AdHoaDonService;
import service.cinema.be.core.admin.quanlykhachhang.repository.AdKhachHangRepository;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;
import service.cinema.be.core.admin.quanlyve.repository.AdGiaVeChiTietRepository;
import service.cinema.be.core.admin.quanlyve.repository.AdVeRepository;
import service.cinema.be.entity.*;
import service.cinema.be.repository.GheRepository;
import service.cinema.be.repository.SuatChieuRepository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdHoaDonServiceImpl implements AdHoaDonService {

    private final AdHoaDonRepository adHoaDonRepository;
    private final AdHoaDonChiTietRepository adHoaDonChiTietRepository;
    private final AdVeRepository adVeRepository;
    private final AdGiaVeChiTietRepository adGiaVeChiTietRepository;
    private final AdThanhToanRepository adThanhToanRepository;
    private final AdLichSuHoaDonRepository adLichSuHoaDonRepository;
    private final AdKhachHangRepository khachHangRepository;
    private final SuatChieuRepository suatChieuRepository;
    private final GheRepository gheRepository;

    /**
     * 1. THANH TOÁN TẠI QUẦY (POS)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HoaDon thanhToanHoaDon(AdHoaDonRequest request) {
        SuatChieu suatChieu = suatChieuRepository.findById(request.getIdSuatChieu())
                .orElseThrow(() -> new RuntimeException("Lỗi: Suất chiếu không tồn tại!"));

        BigDecimal giaGoc = suatChieu.getPhim().getGiaPhim();
        String idKhungGio = suatChieu.getKhungGio().getId();

        // Khởi tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD-" + System.currentTimeMillis());
        hoaDon.setKenhBanHang(0); // 0: POS
        hoaDon.setTrangThai(1);
        hoaDon.setPhuongThucThanhToan(request.getPhuongThucThanhToan());

        if (request.getIdKhachHang() != null && !request.getIdKhachHang().trim().isEmpty()) {
            KhachHang kh = khachHangRepository.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy khách hàng!"));
            hoaDon.setKhachHang(kh);
        }

        HoaDon savedHoaDon = adHoaDonRepository.save(hoaDon);
        BigDecimal tongTienHoaDon = BigDecimal.ZERO;
        List<HoaDonChiTiet> danhSachChiTiet = new ArrayList<>();

        // Xử lý từng ghế đặt
        for (AdDatVeRequest.ChiTietGheDat item : request.getDanhSachGhe()) {
            if (adVeRepository.existsBySuatChieuIdAndGheIdAndTrangThai(suatChieu.getId(), item.getIdGhe(), 1)) {
                throw new RuntimeException("Ghế " + item.getIdGhe() + " đã có người đặt!");
            }

            Ghe ghe = gheRepository.findById(item.getIdGhe())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ghế!"));

            BigDecimal phuThu = adGiaVeChiTietRepository
                    .timGiaPhuThuTheoTieuChi(item.getIdLoaiKhachHang(), request.getIdLoaiNgay(), ghe.getLoaiGhe().getId(), idKhungGio)
                    .orElse(BigDecimal.ZERO);

            BigDecimal giaVeCuoiCung = giaGoc.add(phuThu);

            // Tạo vé
            Ve ve = new Ve();
            ve.setId(UUID.randomUUID().toString());
            ve.setMaVe("VE-" + System.currentTimeMillis() + (int)(Math.random() * 100));
            ve.setSuatChieu(suatChieu);
            ve.setGhe(ghe);
            ve.setGiaThanhToan(giaVeCuoiCung);
            ve.setTrangThai(1);
            ve.setLoaiVe(0);
            Ve savedVe = adVeRepository.save(ve);

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(savedHoaDon);
            chiTiet.setVe(savedVe);
            chiTiet.setLoai(0);
            chiTiet.setSoLuong(1);
            chiTiet.setDonGia(giaVeCuoiCung);
            chiTiet.setThanhTien(giaVeCuoiCung);
            adHoaDonChiTietRepository.save(chiTiet);

            danhSachChiTiet.add(chiTiet);
            tongTienHoaDon = tongTienHoaDon.add(giaVeCuoiCung);
        }

        // Cập nhật tổng tiền hóa đơn
        savedHoaDon.setTongTien(tongTienHoaDon);
        savedHoaDon.setSoTienGiam(BigDecimal.ZERO);
        savedHoaDon.setTongTienThanhToan(tongTienHoaDon);
        savedHoaDon.setHoaDonChiTiets(danhSachChiTiet);
        adHoaDonRepository.save(savedHoaDon);

        // Lưu thông tin thanh toán
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setHoaDon(savedHoaDon);
        thanhToan.setMaGiaoDich("GD-" + System.currentTimeMillis());
        thanhToan.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        thanhToan.setSoTien(tongTienHoaDon);
        thanhToan.setNgayThanhToan(LocalDateTime.now());
        thanhToan.setTrangThai(1);
        adThanhToanRepository.save(thanhToan);

        // Lưu lịch sử
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(savedHoaDon);
        String khach = (savedHoaDon.getKhachHang() != null) ? savedHoaDon.getKhachHang().getTenKhachHang() : "Khách lẻ";
        lichSu.setHanhDong("Thanh toán thành công tại POS cho " + khach);
        lichSu.setThoiGianHanhDong(LocalDateTime.now());
        lichSu.setTrangThai(1);
        adLichSuHoaDonRepository.save(lichSu);

        return savedHoaDon;
    }

    /**
     * 2. TÌM KIẾM ĐA NĂNG + BỘ LỌC
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdHoaDonResponse> timKiemHoaDon(
            String tuKhoa, Integer trangThai,
            BigDecimal minPrice, BigDecimal maxPrice,
            LocalDate tuNgay, LocalDate denNgay,
            String kyThoiGian,
            Integer phuongThucThanhToan,
            Integer kenhBanHang,
            String idKhachHang,
            int page, int size) {

        // 1. XỬ LÝ KỲ THỜI GIAN ĐẦY ĐỦ (TODAY, WEEK, MONTH, QUARTER, YEAR)
        if (kyThoiGian != null && !kyThoiGian.trim().isEmpty()) {
            LocalDate now = LocalDate.now();
            switch (kyThoiGian.toUpperCase()) {
                case "TODAY":
                    tuNgay = now; denNgay = now; break;
                case "THIS_WEEK":
                    tuNgay = now.with(DayOfWeek.MONDAY); denNgay = now.with(DayOfWeek.SUNDAY); break;
                case "THIS_MONTH":
                    tuNgay = now.with(TemporalAdjusters.firstDayOfMonth()); denNgay = now.with(TemporalAdjusters.lastDayOfMonth()); break;
                case "THIS_QUARTER":
                    int startMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;
                    tuNgay = LocalDate.of(now.getYear(), startMonth, 1);
                    denNgay = tuNgay.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()); break;
                case "THIS_YEAR":
                    tuNgay = now.with(TemporalAdjusters.firstDayOfYear()); denNgay = now.with(TemporalAdjusters.lastDayOfYear()); break;
            }
        }

        final LocalDate finalTuNgay = tuNgay;
        final LocalDate finalDenNgay = denNgay;

        // Sắp xếp mặc định theo ngày tạo mới nhất
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));

        Specification<HoaDon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 2. TÌM KIẾM ĐA NĂNG (Mã HD, Tên KH, SĐT, Tên NV)
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String search = "%" + tuKhoa.trim().toLowerCase() + "%";
                // Sử dụng LEFT JOIN để không làm mất hóa đơn Khách lẻ hoặc bán Online
                Join<HoaDon, KhachHang> khJoin = root.join("khachHang", JoinType.LEFT);
                Join<HoaDon, NhanVien> nvJoin = root.join("nhanVien", JoinType.LEFT);

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maHoaDon")), search),
                        cb.like(cb.lower(khJoin.get("tenKhachHang")), search),
                        cb.like(cb.lower(khJoin.get("sdt")), search),
                        cb.like(cb.lower(nvJoin.get("tenNhanVien")), search)
                ));
            }

            // 3. CÁC BỘ LỌC CƠ BẢN (Sử dụng đúng tên trường trong Entity HoaDon)
            if (kenhBanHang != null) predicates.add(cb.equal(root.get("kenhBanHang"), kenhBanHang));
            if (trangThai != null) predicates.add(cb.equal(root.get("trangThai"), trangThai));
            if (phuongThucThanhToan != null) predicates.add(cb.equal(root.get("phuongThucThanhToan"), phuongThucThanhToan));

            if (idKhachHang != null && !idKhachHang.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("khachHang").get("id"), idKhachHang));
            }

            // 4. LỌC KHOẢNG GIÁ (Dựa trên tongTienThanhToan)
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("tongTienThanhToan"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("tongTienThanhToan"), maxPrice));

            // 5. LỌC THỜI GIAN AN TOÀN (Tránh NullPointerException)
            if (finalTuNgay != null) predicates.add(cb.greaterThanOrEqualTo(root.get("ngayTao"), finalTuNgay.atStartOfDay()));
            if (finalDenNgay != null) predicates.add(cb.lessThanOrEqualTo(root.get("ngayTao"), finalDenNgay.atTime(23, 59, 59)));

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return adHoaDonRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    /**
     * Mapping Entity -> Response DTO (Xử lý an toàn dữ liệu Null)
     */
    private AdHoaDonResponse mapToResponse(HoaDon hd) {
        AdHoaDonResponse dto = new AdHoaDonResponse();
        dto.setId(hd.getId());
        dto.setMaHoaDon(hd.getMaHoaDon());
        dto.setTongTienThanhToan(hd.getTongTienThanhToan()); //
        dto.setTrangThai(hd.getTrangThai());
        dto.setNgayTao(hd.getNgayTao());
        dto.setKenhBanHang(hd.getKenhBanHang()); //

        // Map Nhân viên bán hàng (Hỗ trợ hóa đơn bán qua Web - NhanVien null)
        if (hd.getNhanVien() != null) {
            dto.setTenNhanVien(hd.getNhanVien().getTenNhanVien());
        } else {
            dto.setTenNhanVien("Hệ thống (Web)");
        }

        // FIX LỖI 500: Kiểm tra null khách hàng (Hỗ trợ hóa đơn Khách lẻ)
        if (hd.getKhachHang() != null) {
            dto.setTenKhachHang(hd.getKhachHang().getTenKhachHang());
            dto.setSdt(hd.getKhachHang().getSdt());
        } else {
            dto.setTenKhachHang("Khách lẻ");
            dto.setSdt("---");
        }
        return dto;
    }

    /**
     * 3. LẤY CHI TIẾT HÓA ĐƠN
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdHoaDonChiTietResponse> layChiTietHoaDon(String idHoaDon) {
        List<HoaDonChiTiet> listChiTiet = adHoaDonChiTietRepository.findByHoaDonId(idHoaDon);

        return listChiTiet.stream().map(chiTiet -> {
            AdHoaDonChiTietResponse dto = new AdHoaDonChiTietResponse();
            dto.setId(chiTiet.getId());
            dto.setLoai(chiTiet.getLoai());
            dto.setSoLuong(chiTiet.getSoLuong());
            dto.setDonGia(chiTiet.getDonGia());
            dto.setThanhTien(chiTiet.getThanhTien());

            if (chiTiet.getLoai() == 0 && chiTiet.getVe() != null) {
                Ve ve = chiTiet.getVe();
                if (ve.getSuatChieu() != null) {
                    dto.setTenPhim(ve.getSuatChieu().getPhim().getTenPhim());
                    dto.setTenPhongChieu(ve.getSuatChieu().getPhongChieu().getTenPhong());
                }
                if (ve.getGhe() != null) {
                    dto.setViTriGhe(ve.getGhe().getSoHang() + ve.getGhe().getSoGhe());
                }
            }
            return dto;
        }).toList();
    }
}