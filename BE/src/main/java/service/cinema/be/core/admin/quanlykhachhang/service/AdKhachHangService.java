package service.cinema.be.core.admin.quanlykhachhang.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlykhachhang.dto.request.AdKhachHangRequest;
import service.cinema.be.core.admin.quanlykhachhang.dto.response.AdKhachHangResponse;
import service.cinema.be.core.admin.quanlykhachhang.repository.AdKhachHangRepository;
import service.cinema.be.entity.KhachHang;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdKhachHangService {

    private final AdKhachHangRepository adKhachHangRepository;

    @Transactional(readOnly = true)
    public List<AdKhachHangResponse> getAllKhachHang(String search, Integer trangThai) {
        return adKhachHangRepository.findAllBySearchNative(search, trangThai).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdKhachHangResponse createKhachHang(AdKhachHangRequest request) {
        // 1. Kiểm tra trùng Email và SĐT
        if (adKhachHangRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email đã tồn tại trên hệ thống!");

        if (adKhachHangRepository.existsBySdt(request.getSdt()))
            throw new IllegalArgumentException("Số điện thoại đã tồn tại trên hệ thống!");

        KhachHang kh = new KhachHang();

        // 2. Sinh mã tự động KH001, KH002...
        kh.setMaKhachHang(generateMaKhachHang());

        kh.setNgayTao(LocalDateTime.now());
        transferData(request, kh);
        return toResponse(adKhachHangRepository.save(kh));
    }

    @Transactional
    public AdKhachHangResponse updateKhachHang(String id, AdKhachHangRequest request) {
        KhachHang kh = adKhachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng có ID: " + id));

        // 3. Kiểm tra trùng email/sđt nhưng loại trừ chính nó
        if (adKhachHangRepository.existsByEmailAndIdNot(request.getEmail(), id))
            throw new IllegalArgumentException("Email đã được sử dụng bởi khách hàng khác!");

        if (adKhachHangRepository.existsBySdtAndIdNot(request.getSdt(), id))
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi khách hàng khác!");

        transferData(request, kh);
        kh.setNgayCapNhat(LocalDateTime.now());
        return toResponse(adKhachHangRepository.save(kh));
    }

    @Transactional
    public void deleteKhachHang(String id) {
        if (!adKhachHangRepository.existsById(id)) {
            throw new IllegalArgumentException("Khách hàng không tồn tại để xóa!");
        }
        adKhachHangRepository.deleteById(id);
    }

    /**
     * Hàm sinh mã KH001, KH002...
     */
    private String generateMaKhachHang() {
        String maxMa = adKhachHangRepository.findMaxMaKhachHang();
        if (maxMa == null || maxMa.isEmpty()) {
            return "KH001";
        }
        try {
            // Tách phần số sau chữ "KH"
            int lastNumber = Integer.parseInt(maxMa.substring(2));
            return String.format("KH%03d", lastNumber + 1);
        } catch (Exception e) {
            // Backup trường hợp DB có mã lỗi
            return "KH" + System.currentTimeMillis() % 100000;
        }
    }

    private void transferData(AdKhachHangRequest request, KhachHang kh) {
        kh.setTenKhachHang(request.getTenKhachHang());
        kh.setEmail(request.getEmail());
        kh.setSdt(request.getSdt());
        kh.setGioiTinh(request.getGioiTinh());
        kh.setNgaySinh(request.getNgaySinh());
        kh.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);
    }

    private AdKhachHangResponse toResponse(KhachHang kh) {
        return AdKhachHangResponse.builder()
                .id(kh.getId())
                .maKhachHang(kh.getMaKhachHang())
                .tenKhachHang(kh.getTenKhachHang())
                .email(kh.getEmail())
                .sdt(kh.getSdt())
                .gioiTinh(kh.getGioiTinh())
                .ngaySinh(kh.getNgaySinh())
                .trangThai(kh.getTrangThai())
                .build();
    }
}