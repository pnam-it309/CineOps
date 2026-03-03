package service.cinema.be.core.admin.quanlyhoadon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlyhoadon.dto.request.AdHoaDonRequest;
import service.cinema.be.core.admin.quanlyhoadon.service.AdHoaDonService;
import service.cinema.be.core.admin.quanlyve.dto.request.AdDatVeRequest;
import service.cinema.be.core.admin.quanlyve.repository.AdGiaVeChiTietRepository;
import service.cinema.be.core.admin.quanlyve.repository.AdVeRepository;
import service.cinema.be.core.admin.quanlyhoadon.repository.AdHoaDonRepository;
import service.cinema.be.core.admin.quanlyhoadon.repository.AdHoaDonChiTietRepository;
import service.cinema.be.core.admin.quanlyhoadon.repository.AdLichSuHoaDonRepository;
import service.cinema.be.core.admin.quanlyhoadon.repository.AdThanhToanRepository;
import service.cinema.be.entity.*;
import service.cinema.be.repository.GheRepository;
import service.cinema.be.repository.SuatChieuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonResponse;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonChiTietResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdHoaDonServiceImpl implements AdHoaDonService {

    // 1. Inject toàn bộ các Repository chuẩn ADMIN
    private final AdHoaDonRepository adHoaDonRepository;
    private final AdHoaDonChiTietRepository adHoaDonChiTietRepository;
    private final AdVeRepository adVeRepository;
    private final AdGiaVeChiTietRepository adGiaVeChiTietRepository;
    private final AdThanhToanRepository adThanhToanRepository;
    private final AdLichSuHoaDonRepository adLichSuHoaDonRepository;

    // Repository dùng chung (không phân quyền phức tạp)
    private final SuatChieuRepository suatChieuRepository;
    private final GheRepository gheRepository;

    @Override
    @Transactional(rollbackFor = Exception.class) // Đảm bảo tính toàn vẹn dữ liệu (ACID)
    public HoaDon thanhToanHoaDon(AdHoaDonRequest request) {

        // 1. Kiểm tra Suất chiếu có tồn tại không
        SuatChieu suatChieu = suatChieuRepository.findById(request.getIdSuatChieu())
                .orElseThrow(() -> new RuntimeException("Suất chiếu không tồn tại"));

        BigDecimal giaGoc = suatChieu.getPhim().getGiaPhim();
        String idKhungGio = suatChieu.getKhungGio().getId();

        // 2. Khởi tạo Hóa Đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD-" + System.currentTimeMillis());
        hoaDon.setKemBanHang(0); // 0: Bán tại quầy (POS)
        hoaDon.setTrangThai(1);  // 1: Đã thanh toán
        hoaDon.setPhuongThucThanhToan(request.getPhuongThucThanhToan());

        // Lưu tạm để Database sinh ra ID thực tế
        HoaDon savedHoaDon = adHoaDonRepository.save(hoaDon);

        BigDecimal tongTien = BigDecimal.ZERO;
        List<HoaDonChiTiet> danhSachChiTiet = new ArrayList<>();

        // 3. Xử lý logic Đặt Vé & Chi Tiết Hóa Đơn
        for (AdDatVeRequest.ChiTietGheDat item : request.getDanhSachGhe()) {

            // a. Ràng buộc nghiệp vụ (Business Rule): Kiểm tra ghế đã có người đặt chưa
            if (adVeRepository.existsBySuatChieuIdAndGheIdAndTrangThai(suatChieu.getId(), item.getIdGhe(), 1)) {
                throw new RuntimeException("Ghế đã có người đặt, vui lòng chọn ghế khác!");
            }

            Ghe ghe = gheRepository.findById(item.getIdGhe())
                    .orElseThrow(() -> new RuntimeException("Ghế không tồn tại"));

            // b. Tính toán phụ thu (Lưu ý: request cần truyền thêm idLoaiNgay)
            BigDecimal phuThu = adGiaVeChiTietRepository
                    .timGiaPhuThuTheoTieuChi(
                            item.getIdLoaiKhachHang(),
                            request.getIdLoaiNgay(), // Lấy từ request
                            ghe.getLoaiGhe().getId(),
                            idKhungGio
                    )
                    .orElse(BigDecimal.ZERO);
            BigDecimal giaVeCuoiCung = giaGoc.add(phuThu);

            // c. Khởi tạo và lưu Vé mới
            Ve ve = new Ve();
            ve.setId(UUID.randomUUID().toString());
            ve.setMaVe("VE-" + System.currentTimeMillis() + (int)(Math.random() * 100));
            ve.setSuatChieu(suatChieu);
            ve.setGhe(ghe);
            ve.setGiaThanhToan(giaVeCuoiCung);
            ve.setTrangThai(1); // 1 = Đã bán
            ve.setLoaiVe(0);    // 0 = Bán tại quầy
            Ve savedVe = adVeRepository.save(ve);

            // d. Khởi tạo và lưu Chi Tiết Hóa Đơn
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(savedHoaDon);
            chiTiet.setVe(savedVe);
            chiTiet.setLoai(0); // 0 = Vé xem phim
            chiTiet.setSoLuong(1);
            chiTiet.setDonGia(giaVeCuoiCung);
            chiTiet.setThanhTien(giaVeCuoiCung);
            adHoaDonChiTietRepository.save(chiTiet);

            danhSachChiTiet.add(chiTiet);
            tongTien = tongTien.add(giaVeCuoiCung);
        }

        // 4. Cập nhật tổng tiền cho Hóa Đơn chính
        savedHoaDon.setTongTien(tongTien);
        savedHoaDon.setSoTienGiam(BigDecimal.ZERO); // Mặc định chưa áp dụng Voucher
        savedHoaDon.setTongTienThanhToan(tongTien);
        savedHoaDon.setHoaDonChiTiets(danhSachChiTiet);
        adHoaDonRepository.save(savedHoaDon);

        // 5. Ghi nhận Thanh Toán
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setHoaDon(savedHoaDon);
        thanhToan.setMaGiaoDich("GD-" + System.currentTimeMillis());
        thanhToan.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        thanhToan.setSoTien(tongTien);
        thanhToan.setNgayThanhToan(LocalDateTime.now());
        thanhToan.setTrangThai(1); // 1 = Thành công
        adThanhToanRepository.save(thanhToan);

        // 6. Ghi vết vào Lịch Sử Hóa Đơn (Audit Trail)
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(savedHoaDon);
        lichSu.setHanhDong("Tạo mới hóa đơn và thanh toán thành công tại quầy POS");
        lichSu.setThoiGianHanhDong(LocalDateTime.now());
        lichSu.setTrangThai(1);
        adLichSuHoaDonRepository.save(lichSu);

        return savedHoaDon;
    }
    @Override
    @Transactional(readOnly = true)
    public Page<AdHoaDonResponse> timKiemHoaDon(
            String tuKhoa, Integer trangThai,
            BigDecimal minPrice, BigDecimal maxPrice,
            LocalDate tuNgay, LocalDate denNgay,
            String kyThoiGian, // THÊM THAM SỐ NÀY
            Integer phuongThucThanhToan, // THÊM TRƯỜNG KHÁC VÍ DỤ
            int page, int size) {

        // 1. XỬ LÝ LOGIC "KỲ THỜI GIAN" (NẾU CÓ)
        if (kyThoiGian != null && !kyThoiGian.trim().isEmpty()) {
            LocalDate now = LocalDate.now(); // Lấy ngày hiện tại của Server
            switch (kyThoiGian.toUpperCase()) {
                case "TODAY": // Hôm nay
                    tuNgay = now;
                    denNgay = now;
                    break;
                case "THIS_WEEK": // Tuần này (Từ Thứ 2 đến Chủ nhật)
                    tuNgay = now.with(DayOfWeek.MONDAY);
                    denNgay = now.with(DayOfWeek.SUNDAY);
                    break;
                case "THIS_MONTH": // Tháng này (Từ ngày 1 đến ngày cuối tháng)
                    tuNgay = now.with(TemporalAdjusters.firstDayOfMonth());
                    denNgay = now.with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "THIS_QUARTER": // Quý này
                    int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                    int startMonth = (currentQuarter - 1) * 3 + 1;
                    tuNgay = LocalDate.of(now.getYear(), startMonth, 1);
                    denNgay = tuNgay.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "THIS_YEAR": // Năm nay
                    tuNgay = now.with(TemporalAdjusters.firstDayOfYear());
                    denNgay = now.with(TemporalAdjusters.lastDayOfYear());
                    break;
            }
        }

        // Cần copy các biến này ra final/effectively final để dùng trong Lambda (Specification)
        final LocalDate finalTuNgay = tuNgay;
        final LocalDate finalDenNgay = denNgay;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));

        // 2. BUILD SPECIFICATION (CÂU QUERY ĐỘNG)
        Specification<HoaDon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc từ khóa
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("maHoaDon")), "%" + tuKhoa.trim().toLowerCase() + "%"));
            }
            // Lọc trạng thái
            if (trangThai != null) predicates.add(cb.equal(root.get("trangThai"), trangThai));

            // LỌC CÁC TRƯỜNG KHÁC (Ví dụ: Phương thức thanh toán)
            if (phuongThucThanhToan != null) {
                predicates.add(cb.equal(root.get("phuongThucThanhToan"), phuongThucThanhToan));
            }
            // TODO: Nếu muốn lọc theo ID Khách hàng, ID Nhân viên, chỉ cần copy block if trên và đổi tên cột!

            // Lọc khoảng giá
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("tongTienThanhToan"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("tongTienThanhToan"), maxPrice));

            // Lọc khoảng ngày (Đã được nội suy từ kyThoiGian ở trên)
            if (finalTuNgay != null) predicates.add(cb.greaterThanOrEqualTo(root.get("ngayTao"), finalTuNgay.atStartOfDay()));
            if (finalDenNgay != null) predicates.add(cb.lessThanOrEqualTo(root.get("ngayTao"), finalDenNgay.atTime(23, 59, 59)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Trả về DTO như cũ...
        return adHoaDonRepository.findAll(spec, pageable).map(hoaDon -> {
            AdHoaDonResponse dto = new AdHoaDonResponse();

            // Map các trường cơ bản
            dto.setId(hoaDon.getId());
            dto.setMaHoaDon(hoaDon.getMaHoaDon());
            dto.setTongTienThanhToan(hoaDon.getTongTienThanhToan());
            dto.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan());
            dto.setTrangThai(hoaDon.getTrangThai());

            // Lấy ngayTao từ PrimaryEntity (Giả sử PrimaryEntity có hàm getNgayTao())
            dto.setNgayTao(hoaDon.getNgayTao());
            dto.setKemBanHang(hoaDon.getKemBanHang());

            // Map an toàn NhanVien và KhachHang (Tránh lỗi NullPointerException)
            if (hoaDon.getNhanVien() != null) {
                dto.setTenNhanVien(hoaDon.getNhanVien().getTenNhanVien());
            } else {
                dto.setTenNhanVien("Hệ thống");
            }

            if (hoaDon.getKhachHang() != null) {
                dto.setTenKhachHang(hoaDon.getKhachHang().getTenKhachHang());
            } else {
                dto.setTenKhachHang("Khách lẻ");
            }

            return dto;
        });
    }

    /**
     * LẤY CHI TIẾT 1 HÓA ĐƠN (KHI BẤM NÚT XEM CHI TIẾT)
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

            // Nếu là Vé (loại 0), ta móc data từ bảng Ve -> SuatChieu -> Phim
            if (chiTiet.getLoai() == 0 && chiTiet.getVe() != null) {
                Ve ve = chiTiet.getVe();
                if(ve.getSuatChieu() != null) {
                    dto.setTenPhim(ve.getSuatChieu().getPhim().getTenPhim());
                    dto.setTenPhongChieu(ve.getSuatChieu().getPhongChieu().getTenPhong());
                }
                if(ve.getGhe() != null) {
                    dto.setViTriGhe(ve.getGhe().getSoHang() + ve.getGhe().getSoGhe());
                }
            }
            return dto;
        }).toList();
    }
}