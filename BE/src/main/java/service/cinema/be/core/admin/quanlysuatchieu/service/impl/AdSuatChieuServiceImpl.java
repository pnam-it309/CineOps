package service.cinema.be.core.admin.quanlysuatchieu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdKhungGioResponse;
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
import service.cinema.be.core.admin.quanlysuatchieu.dto.request.AdBatchSuatChieuRequest;
import service.cinema.be.core.admin.quanlysuatchieu.dto.response.AdBatchSuatChieuResult;
import service.cinema.be.core.admin.quanlyve.repository.AdVeRepository;
import service.cinema.be.repository.GheRepository;
import service.cinema.be.entity.Ve;
import service.cinema.be.entity.Ghe;
import java.util.Map;
import java.util.HashMap;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdSuatChieuServiceImpl implements AdSuatChieuService {

    private final AdSuatChieuRepository adSuatChieuRepository;
    private final PhimRepository phimRepository;
    private final PhongChieuRepository phongChieuRepository;
    private final KhungGioRepository khungGioRepository;
    private final AdVeRepository adVeRepository;
    private final GheRepository gheRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdSuatChieuResponse> getSuatChieu(LocalDate date, String pcId) {
        return adSuatChieuRepository.findByNgayVaPhongNative(date, pcId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdSuatChieuResponse getById(String id) {
        SuatChieu sc = adSuatChieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu có id: " + id));
        return toResponse(sc);
    }

    @Override
    @Transactional
    public AdSuatChieuResponse create(AdSuatChieuRequest req) {
        // Tính toán thời gian kết thúc thực tế: giờ bắt đầu + thời lượng phim + thời gian dọn vệ sinh
        Phim phim = phimRepository.findById(req.getIdPhim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim có id: " + req.getIdPhim()));
        
        LocalTime gioKetThucThucTe = req.getGioBatDau()
                .plusMinutes(phim.getThoiLuong())
                .plusMinutes(req.getThoiGianDonVeSinh() != null ? req.getThoiGianDonVeSinh() : 15);
        
        // Kiểm tra xung đột với thời gian kết thúc thực tế
        if (adSuatChieuRepository.existsConflict(
                req.getIdPhongChieu(), 
                req.getNgayChieu(), 
                req.getGioBatDau(), 
                gioKetThucThucTe, 
                "NEW")) {
            throw new IllegalArgumentException(
                "Trùng lịch chiếu! Phòng chiếu đang bận trong khoảng thời gian này. " +
                "Thời gian kết thúc dự kiến: " + gioKetThucThucTe + " (bao gồm thời gian dọn vệ sinh)"
            );
        }
        
        PhongChieu pc = phongChieuRepository.findById(req.getIdPhongChieu())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu có id: " + req.getIdPhongChieu()));

        // Kiểm tra tương thích: Loại định dạng của Phim phải khớp với Loại màn hình của Phòng
        if (phim.getLoaiPhim() != null && pc.getLoaiManHinh() != null) {
            if (!phim.getLoaiPhim().equalsIgnoreCase(pc.getLoaiManHinh())) {
                throw new IllegalArgumentException(
                    "Loại phim (" + phim.getLoaiPhim() + ") không khớp với loại màn hình của phòng (" + pc.getLoaiManHinh() + ")"
                );
            }
        }
        
        // Tìm hoặc tạo KhungGio dựa trên giờ bắt đầu và kết thúc
        KhungGio kg = findOrCreateKhungGio(req.getGioBatDau(), gioKetThucThucTe);

        SuatChieu sc = new SuatChieu();
        sc.setPhim(phim);
        sc.setPhongChieu(pc);
        sc.setKhungGio(kg);
        sc.setNgayChieu(req.getNgayChieu());
        sc.setGioBatDau(req.getGioBatDau());
        sc.setGioKetThuc(gioKetThucThucTe);
        sc.setThoiGianDonVeSinh(req.getThoiGianDonVeSinh() != null ? req.getThoiGianDonVeSinh() : 15);
        sc.setTrangThai(req.getTrangThai());
        sc.setSoGheTrong(0); // Mặc định 0, sẽ được cập nhật sau nếu cần
        
        return toResponse(adSuatChieuRepository.save(sc));
    }

    /**
     * Tự động sinh hàng loạt suất chiếu.
     *
     * <p>Thuật toán:
     * <pre>
     * for ngay in [tuNgay .. denNgay]:
     *   for gioBatDau in danhSachGioBatDau:
     *     gioKetThuc = gioBatDau + thoiLuong + bufferPhut
     *     if not overlap(phong, ngay, gioBatDau, gioKetThuc):
     *       insert SuatChieu
     *     else:
     *       ghi vào danh sách bị trùng
     * </pre>
     */
    @Override
    @Transactional
    public AdBatchSuatChieuResult generateBatch(AdBatchSuatChieuRequest req) {
        // Validate date range
        if (req.getDenNgay().isBefore(req.getTuNgay())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");
        }

        Phim phim = phimRepository.findById(req.getIdPhim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + req.getIdPhim()));
        PhongChieu pc = phongChieuRepository.findById(req.getIdPhongChieu())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với id: " + req.getIdPhongChieu()));

        // Kiểm tra tương thích loại phim - loại màn hình
        if (phim.getLoaiPhim() != null && pc.getLoaiManHinh() != null) {
            if (!phim.getLoaiPhim().equalsIgnoreCase(pc.getLoaiManHinh())) {
                throw new IllegalArgumentException(
                    "Loại phim (" + phim.getLoaiPhim() + ") không khớp với loại màn hình của phòng (" + pc.getLoaiManHinh() + ")");
            }
        }

        int thoiLuongPhut = phim.getThoiLuong() != null ? phim.getThoiLuong() : 120;
        int bufferPhut = req.getBufferPhut() != null ? req.getBufferPhut() : 20;
        int trangThai = req.getTrangThai() != null ? req.getTrangThai() : 1;

        List<AdBatchSuatChieuResult.SkippedSlot> danhSachBiTrung = new ArrayList<>();
        int tongSlot = 0;
        int taoThanhCong = 0;

        // Vòng lặp qua từng ngày trong khoảng
        LocalDate current = req.getTuNgay();
        while (!current.isAfter(req.getDenNgay())) {
            for (LocalTime gioBatDau : req.getDanhSachGioBatDau()) {
                tongSlot++;
                LocalTime gioKetThuc = gioBatDau.plusMinutes(thoiLuongPhut + bufferPhut);

                // Kiểm tra overlap với phòng cùng ngày
                boolean isOverlap = adSuatChieuRepository.existsConflict(
                        req.getIdPhongChieu(), current, gioBatDau, gioKetThuc, "NEW");

                if (isOverlap) {
                    danhSachBiTrung.add(AdBatchSuatChieuResult.SkippedSlot.builder()
                            .ngay(current)
                            .gioBatDau(gioBatDau)
                            .gioKetThuc(gioKetThuc)
                            .lyDo("Trùng với suất chiếu khác trong cùng phòng")
                            .build());
                } else {
                    // Tìm hoặc tạo KhungGio
                    KhungGio kg = findOrCreateKhungGio(gioBatDau, gioKetThuc);

                    SuatChieu sc = new SuatChieu();
                    sc.setPhim(phim);
                    sc.setPhongChieu(pc);
                    sc.setKhungGio(kg);
                    sc.setNgayChieu(current);
                    sc.setTrangThai(trangThai);
                    sc.setSoGheTrong(0);
                    adSuatChieuRepository.save(sc);
                    taoThanhCong++;
                }
            }
            current = current.plusDays(1);
        }

        return AdBatchSuatChieuResult.builder()
                .tongSlot(tongSlot)
                .taoThanhCong(taoThanhCong)
                .boBoBiTrung(danhSachBiTrung.size())
                .danhSachBiTrung(danhSachBiTrung)
                .build();
    }

    @Override
    @Transactional
    public AdSuatChieuResponse update(String id, AdSuatChieuRequest req) {
        SuatChieu sc = adSuatChieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu có id: " + id));

        Phim phim = phimRepository.findById(req.getIdPhim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim có id: " + req.getIdPhim()));
        
        PhongChieu pc = phongChieuRepository.findById(req.getIdPhongChieu())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu có id: " + req.getIdPhongChieu()));

        // Tái tính toán giờ kết thúc thực tế
        LocalTime gioKetThucThucTe = req.getGioBatDau()
                .plusMinutes(phim.getThoiLuong())
                .plusMinutes(req.getThoiGianDonVeSinh() != null ? req.getThoiGianDonVeSinh() : 15);

        // Kiểm tra xung đột (trừ chính nó)
        if (adSuatChieuRepository.existsConflict(
                req.getIdPhongChieu(), 
                req.getNgayChieu(), 
                req.getGioBatDau(), 
                gioKetThucThucTe, 
                id)) {
            throw new IllegalArgumentException(
                "Trùng lịch chiếu! Phòng chiếu đang bận trong khoảng " + 
                req.getGioBatDau() + " - " + gioKetThucThucTe + " (đã tính thời gian dọn dẹp)."
            );
        }

        // Kiểm tra tương thích máy chiếu - màn hình
        if (phim.getLoaiPhim() != null && pc.getLoaiManHinh() != null) {
            if (!phim.getLoaiPhim().equalsIgnoreCase(pc.getLoaiManHinh())) {
                throw new IllegalArgumentException(
                    "Loại phim (" + phim.getLoaiPhim() + ") không khớp với loại màn hình của phòng (" + pc.getLoaiManHinh() + ")"
                );
            }
        }

        KhungGio kg = findOrCreateKhungGio(req.getGioBatDau(), gioKetThucThucTe);
        
        sc.setPhim(phim);
        sc.setPhongChieu(pc);
        sc.setKhungGio(kg);
        sc.setNgayChieu(req.getNgayChieu());
        sc.setGioBatDau(req.getGioBatDau());
        sc.setGioKetThuc(gioKetThucThucTe);
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
        SuatChieu sc = adSuatChieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu có id: " + id));
        sc.setTrangThai(0); // 0 = Đã hủy
        adSuatChieuRepository.save(sc);
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
                        .loaiPhim(p.getLoaiPhim())
                        .phuPhiLoaiPhim(p.getPhuPhiLoaiPhim() != null ? java.math.BigDecimal.valueOf(p.getPhuPhiLoaiPhim()) : null)
                        .lichChieu(p.getLichChieu())
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

    /**
     * Kiểm tra xem suất chiếu có nằm trong khung giờ vàng không
     * Trả về hệ số giá nếu có, null nếu không
     */
    @Transactional(readOnly = true)
    public Double getHeSoGiaVang(LocalDate ngayChieu, LocalTime gioBatDau) {
        DayOfWeek dayOfWeek = ngayChieu.getDayOfWeek();
        Integer thuTrongTuan = dayOfWeek.getValue() % 7; // Chủ nhật = 0 -> 7
        
        List<KhungGio> khungGioVangs = khungGioRepository.findKhungGioVang(thuTrongTuan, gioBatDau);
        if (!khungGioVangs.isEmpty()) {
            return null; // Không phải khung giờ vàng
        }
        return khungGioVangs.get(0).getHeSoGia();
    }

    private AdSuatChieuResponse toResponse(SuatChieu sc) {
        AdSuatChieuResponse res = AdSuatChieuResponse.builder()
                .id(sc.getId())
                .ngayChieu(sc.getNgayChieu())
                .trangThai(sc.getTrangThai())
                .idPhim(sc.getPhim().getId())
                .tenPhim(sc.getPhim().getTenPhim())
                .poster(sc.getPhim().getPoster())
                .thoiLuong(sc.getPhim().getThoiLuong())
                .idPhongChieu(sc.getPhongChieu().getId())
                .tenPhongChieu(sc.getPhongChieu().getTenPhong())
                .loaiManHinh(sc.getPhongChieu().getLoaiManHinh())
                .loaiPhim(sc.getPhim().getLoaiPhim())
                .soGheTrong(sc.getSoGheTrong())
                .tongGhe(sc.getPhongChieu() != null ? sc.getPhongChieu().getTongGhe() : 0)
                .thoiGianDonVeSinh(sc.getThoiGianDonVeSinh())
                .build();
        
        if (sc.getKhungGio() != null) {
            res.setIdKhungGio(sc.getKhungGio().getId());
            res.setTenKhungGio(sc.getKhungGio().getTenKhungGio());
            res.setGioBatDau(sc.getKhungGio().getGioBatDau());
            res.setGioKetThuc(sc.getKhungGio().getGioKetThuc());
        }
        
        return res;
    }
    @Override
    public List<AdKhungGioResponse> getKhungGioDropdown() {

        List<KhungGio> list = khungGioRepository.findAll();

        List<AdKhungGioResponse> responseList = new ArrayList<>();

        for (KhungGio k : list) {
            AdKhungGioResponse res = new AdKhungGioResponse();

            res.setId(k.getId());
            res.setTenKhungGio(k.getTenKhungGio());
            res.setGioBatDau(k.getGioBatDau());
            res.setGioKetThuc(k.getGioKetThuc());

            responseList.add(res);
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> getGheStatus(String idSuatChieu) {
        SuatChieu sc = adSuatChieuRepository.findById(idSuatChieu)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu"));
        
        // 1. Lấy toàn bộ ghế của phòng
        List<Ghe> tatCaGhe = gheRepository.findByPhongChieuIdOrderBySoGheAsc(sc.getPhongChieu().getId());
        
        // 2. Lấy danh sách vé đã bán (trạng thái = 1) cho suất này
        List<Ve> veDaBan = adVeRepository.findBySuatChieuIdAndTrangThai(idSuatChieu, 1);
        
        // Map idGhe -> status
        Map<String, Integer> statusMap = new HashMap<>();
        for (Ve v : veDaBan) {
            statusMap.put(v.getGhe().getId(), 1); // 1 = Đã bán/Hết
        }
        
        // 3. Chuyển đổi sang List DTO hoặc Map đơn giản
        return tatCaGhe.stream().map(g -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", g.getId());
            map.put("soGhe", g.getSoGhe());
            map.put("loaiGhe", g.getLoaiGhe() != null ? g.getLoaiGhe().getTenLoai() : "Thường");
            map.put("hang", g.getSoGhe().replaceAll("[0-9]", "")); // A1 -> A
            map.put("cot", Integer.parseInt(g.getSoGhe().replaceAll("[^0-9]", ""))); // A1 -> 1
            map.put("trangThai", statusMap.getOrDefault(g.getId(), 0)); // 0: Trống, 1: Hết
            return map;
        }).collect(Collectors.toList());
    }
}
