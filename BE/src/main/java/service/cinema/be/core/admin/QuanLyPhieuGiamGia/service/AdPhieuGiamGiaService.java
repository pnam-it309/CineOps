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
    private final service.cinema.be.repository.KhachHangRepository khachHangRepository;
    private final service.cinema.be.repository.PhieuGiamGiaChiTietRepository chiTietRepository;
    private final service.cinema.be.core.email.service.IEmailService emailService;

    @Transactional(readOnly = true)
    public Page<AdPhieuGiamGiaResponse> getAll(String keyword, Integer trangThai, int page, int size) {
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

        service.cinema.be.entity.PhieuGiamGia p = new service.cinema.be.entity.PhieuGiamGia();
        mapRequestToEntity(p, request);
        p.setKieuPhatHanh(request.getKieuPhatHanh());
        
        // Luôn tính toán trạng thái dựa trên thời gian nếu không truyền trạng thái cụ thể
        if (request.getTrangThai() == null) {
            p.setTrangThai(determineStatus(request.getNgayBatDau(), request.getNgayKetThuc()));
        } else {
            p.setTrangThai(request.getTrangThai());
        }

        service.cinema.be.entity.PhieuGiamGia saved = repository.save(p);

        // Lưu chi tiết khách hàng nếu là phiếu cá nhân
        if (Integer.valueOf(1).equals(request.getKieuPhatHanh()) && request.getIdKhachHangs() != null) {
            saveAssociations(saved, request.getIdKhachHangs());
            sendVoucherNotifications(saved, "voucher_created");
        }

        return toResponse(saved);
    }

    @Transactional
    public AdPhieuGiamGiaResponse update(String id, AdPhieuGiamGiaRequest request) {
        service.cinema.be.entity.PhieuGiamGia p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));

        Integer oldStatus = p.getTrangThai();
        java.time.LocalDateTime oldStart = p.getNgayBatDau();
        java.time.LocalDateTime oldEnd = p.getNgayKetThuc();

        mapRequestToEntity(p, request);
        
        // Xử lý tự động thay đổi thời gian dựa trên yêu cầu đổi trạng thái
        if (request.getTrangThai() != null && !request.getTrangThai().equals(oldStatus)) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (request.getTrangThai() == 1) { // Kích hoạt (Hoạt động)
                p.setNgayBatDau(now); // Đặt lại ngày bắt đầu là hiện tại
                if (p.getNgayKetThuc().isBefore(now)) {
                    p.setNgayKetThuc(now.plusDays(7)); // Mặc định gia hạn 7 ngày nếu đã hết hạn
                }
            } else if (request.getTrangThai() == 2) { // Kết thúc
                p.setNgayKetThuc(now);
            }
            p.setTrangThai(request.getTrangThai());
        } else {
            // Nếu không đổi trạng thái trực tiếp, tính toán lại dựa trên ngày mới
            p.setTrangThai(determineStatus(p.getNgayBatDau(), p.getNgayKetThuc()));
        }

        service.cinema.be.entity.PhieuGiamGia saved = repository.save(p);

        // Cập nhật lại danh sách cá nhân nếu cần
        if (Integer.valueOf(1).equals(saved.getKieuPhatHanh())) {
            if (request.getIdKhachHangs() != null) {
                chiTietRepository.deleteByPhieuGiamGia(saved);
                saveAssociations(saved, request.getIdKhachHangs());
                // Luôn gửi email cho khách hàng mới trong danh sách khi cập nhật
                sendVoucherNotifications(saved, "voucher_updated");
            } else if (!oldStatus.equals(saved.getTrangThai()) || !oldEnd.equals(saved.getNgayKetThuc())) {
                // Nếu không gửi đến danh sách KH cụ thể, nhưng có đổi trạng thái/hạn dùng
                sendVoucherNotifications(saved, "voucher_updated");
            }
        }

        return toResponse(saved);
    }

    private void saveAssociations(service.cinema.be.entity.PhieuGiamGia p, java.util.List<String> customerIds) {
        for (String customerId : customerIds) {
            khachHangRepository.findById(customerId).ifPresent(kh -> {
                service.cinema.be.entity.PhieuGiamGiaChiTiet ct = new service.cinema.be.entity.PhieuGiamGiaChiTiet();
                ct.setPhieuGiamGia(p);
                ct.setKhachHang(kh);
                ct.setMaPhieuGiamGiaChiTiet(p.getMaPhieuGiamGia() + "-" + kh.getMaKhachHang());
                ct.setSoLuongDung(0);
                ct.setTrangThai(1);
                chiTietRepository.save(ct);
            });
        }
    }

    private void sendVoucherNotifications(service.cinema.be.entity.PhieuGiamGia p, String type) {
        java.util.List<service.cinema.be.entity.PhieuGiamGiaChiTiet> chiTiets = chiTietRepository.findByPhieuGiamGia(p);
        System.out.println("Sending voucher notifications for: " + p.getMaPhieuGiamGia() + ", count: " + chiTiets.size());
        for (service.cinema.be.entity.PhieuGiamGiaChiTiet ct : chiTiets) {
            if (ct.getKhachHang() != null && ct.getKhachHang().getTaiKhoan() != null) {
                String email = ct.getKhachHang().getTaiKhoan().getEmail();
                if (email != null && !email.isBlank()) {
                    String subject = type.equals("voucher_created") ? "[CineOps] Bạn nhận được ưu đãi mới!" : "[CineOps] Cập nhật ưu đãi của bạn";
                    String message = String.format(
                        "Xin chào %s,\n\nBạn có một %s: %s (%s).\nThời gian sử dụng: %s đến %s.\n%s\n\nChúc bạn có những giây phút xem phim tuyệt vời tại CineOps!",
                        ct.getKhachHang().getTenKhachHang(),
                        type.equals("voucher_created") ? "phiếu giảm giá mới" : "cập nhật về phiếu giảm giá",
                        p.getTenPhieu(),
                        p.getMaPhieuGiamGia(),
                        p.getNgayBatDau().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        p.getNgayKetThuc().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        p.getDieuKienApDung() != null ? "Điều kiện: " + p.getDieuKienApDung() : ""
                    );
                    
                    service.cinema.be.core.email.dto.EmailRequest emailRequest = 
                        service.cinema.be.core.email.dto.EmailRequest.builder()
                        .to(email)
                        .subject(subject)
                        .variables(java.util.Map.of("content", message))
                        .templateName(null) // Sử dụng email đơn giản
                        .isHtml(false)
                        .build();

                    System.out.println("Queued email for: " + email);
                    emailService.sendEmailAsync(emailRequest);
                } else {
                    System.out.println("Voucher notification skipped: No email for customer " + ct.getKhachHang().getTenKhachHang());
                }
            } else {
                System.out.println("Voucher notification skipped: No account linked to customer in " + ct.getMaPhieuGiamGiaChiTiet());
            }
        }
    }

    private void mapRequestToEntity(service.cinema.be.entity.PhieuGiamGia p, AdPhieuGiamGiaRequest request) {
        p.setMaPhieuGiamGia(request.getMaPhieuGiamGia());
        p.setTenPhieu(request.getTenPhieu());
        p.setLoaiPhieu(request.getLoaiPhieu());
        // p.setKieuPhatHanh(request.getKieuPhatHanh()); -> Chỉ set lúc create
        p.setPhanTramGiamGia(request.getPhanTramGiamGia());
        p.setSoTienGiam(request.getSoTienGiam());
        p.setGiamToiDa(request.getGiamToiDa());
        p.setGiaTriHoaDonToiThieu(request.getGiaTriHoaDonToiThieu());
        p.setSoLuong(request.getSoLuong());
        p.setNgayBatDau(request.getNgayBatDau());
        p.setNgayKetThuc(request.getNgayKetThuc());
        p.setCoChoCongDon(request.getCoChoCongDon());
        p.setDieuKienApDung(request.getDieuKienApDung());
        p.setGhiChu(request.getGhiChu());
    }

    private Integer determineStatus(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.isBefore(start)) return 0; // Tạm ngưng/Chưa tới
        if (now.isAfter(end)) return 2;    // Đã kết thúc
        return 1;                         // Đang diễn ra
    }

    @Transactional(readOnly = true)
    public AdPhieuGiamGiaResponse getById(String id) {
        service.cinema.be.entity.PhieuGiamGia p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));
        return toResponse(p);
    }

    @Transactional
    public void delete(String id) {
        service.cinema.be.entity.PhieuGiamGia p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));
        p.setTrangThai(2); // 2 = Đã kết thúc / Ngừng
        repository.save(p);
    }

    private AdPhieuGiamGiaResponse toResponse(service.cinema.be.entity.PhieuGiamGia p) {
        return AdPhieuGiamGiaResponse.builder()
                .id(p.getId())
                .maPhieuGiamGia(p.getMaPhieuGiamGia())
                .tenPhieu(p.getTenPhieu())
                .loaiPhieu(p.getLoaiPhieu())
                .kieuPhatHanh(p.getKieuPhatHanh())
                .phanTramGiamGia(p.getPhanTramGiamGia())
                .soTienGiam(p.getSoTienGiam())
                .giaTriHoaDonToiThieu(p.getGiaTriHoaDonToiThieu())
                .giamToiDa(p.getGiamToiDa())
                .soLuong(p.getSoLuong())
                .ngayBatDau(p.getNgayBatDau())
                .ngayKetThuc(p.getNgayKetThuc())
                .coChoCongDon(p.getCoChoCongDon())
                .dieuKienApDung(p.getDieuKienApDung())
                .ghiChu(p.getGhiChu())
                .trangThai(p.getTrangThai())
                .build();
    }
}