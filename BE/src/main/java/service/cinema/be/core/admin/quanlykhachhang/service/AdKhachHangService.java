package service.cinema.be.core.admin.quanlykhachhang.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlykhachhang.dto.request.AdKhachHangRequest;
import service.cinema.be.core.admin.quanlykhachhang.dto.response.AdKhachHangResponse;
import service.cinema.be.core.admin.quanlykhachhang.repository.AdKhachHangRepository;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.repository.PhanQuyenRepository;
import service.cinema.be.repository.TaiKhoanRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdKhachHangService {

    private final AdKhachHangRepository adKhachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PhanQuyenRepository phanQuyenRepository;
    private final PasswordEncoder passwordEncoder;
    private final service.cinema.be.core.email.service.IEmailService emailService;

    @Transactional(readOnly = true)
    public Page<AdKhachHangResponse> getAllKhachHang(String search, Integer trangThai, Pageable pageable) {
        return adKhachHangRepository.findAllBySearch(search, trangThai, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AdKhachHangResponse getById(String id) {
        KhachHang kh = adKhachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng có ID: " + id));
        return toResponse(kh);
    }

    @Transactional
    public AdKhachHangResponse createKhachHang(AdKhachHangRequest request) {
        // 1. Kiểm tra trùng Email và SĐT
        if (adKhachHangRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email đã tồn tại trên hệ thống!");

        if (adKhachHangRepository.existsBySdt(request.getSdt()))
            throw new IllegalArgumentException("Số điện thoại đã tồn tại trên hệ thống!");

        // 2. Tạo Tài Khoản mới cho khách hàng
        TaiKhoan tk = new TaiKhoan();
        tk.setId(UUID.randomUUID().toString());
        tk.setEmail(request.getEmail());
        // Mật khẩu mặc định là số điện thoại
        tk.setMat_khau(passwordEncoder.encode(request.getSdt())); 
        tk.setTrangThai(1);
        
        phanQuyenRepository.findByMaPhanQuyen("ROLE_CUSTOMER")
                .ifPresent(tk::setPhanQuyen);
        
        tk = taiKhoanRepository.save(tk);

        // 3. Tạo Khách Hàng
        KhachHang kh = new KhachHang();
        kh.setId(UUID.randomUUID().toString());
        kh.setMaKhachHang(generateMaKhachHang());
        kh.setTaiKhoan(tk);
        kh.setNgayTao(LocalDateTime.now());
        
        transferData(request, kh);
        return toResponse(adKhachHangRepository.save(kh));
    }

    @Transactional
    public AdKhachHangResponse updateKhachHang(String id, AdKhachHangRequest request) {
        KhachHang kh = adKhachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng có ID: " + id));

        if (adKhachHangRepository.existsByEmailAndIdNot(request.getEmail(), id))
            throw new IllegalArgumentException("Email đã được sử dụng bởi khách hàng khác!");

        if (adKhachHangRepository.existsBySdtAndIdNot(request.getSdt(), id))
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi khách hàng khác!");

        if (kh.getTaiKhoan() != null) {
            kh.getTaiKhoan().setEmail(request.getEmail());
            taiKhoanRepository.save(kh.getTaiKhoan());
        }

        transferData(request, kh);
        kh.setNgayCapNhat(LocalDateTime.now());
        return toResponse(adKhachHangRepository.save(kh));
    }

    @Transactional
    public void deleteKhachHang(String id) {
        KhachHang kh = adKhachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng có ID: " + id));
        kh.setTrangThai(0);
        
        if (kh.getTaiKhoan() != null) {
            kh.getTaiKhoan().setTrangThai(0);
            taiKhoanRepository.save(kh.getTaiKhoan());
        }
        
        adKhachHangRepository.save(kh);
    }

    private String generateMaKhachHang() {
        String maxMa = adKhachHangRepository.findMaxMaKhachHang();
        if (maxMa == null || maxMa.isEmpty()) {
            return "KH001";
        }
        try {
            int lastNumber = Integer.parseInt(maxMa.substring(2));
            return String.format("KH%03d", lastNumber + 1);
        } catch (Exception e) {
            return "KH" + System.currentTimeMillis() % 100000;
        }
    }

    @Transactional
    public void requestPasswordReset(String id) {
        KhachHang kh = adKhachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng!"));
        
        TaiKhoan tk = kh.getTaiKhoan();
        if (tk == null || tk.getEmail() == null) {
            throw new IllegalArgumentException("Khách hàng chưa có tài khoản hoặc email!");
        }

        // Generate a reset token
        String resetToken = UUID.randomUUID().toString();
        
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("staffName", kh.getTenKhachHang());
        variables.put("resetLink", "http://localhost:3456/reset-password?token=" + resetToken);
        variables.put("expiryHours", 24);

        service.cinema.be.core.email.dto.EmailRequest emailRequest = service.cinema.be.core.email.dto.EmailRequest.builder()
                .to(tk.getEmail())
                .subject("Yêu cầu đặt lại mật khẩu - CineOps")
                .templateName("email/password-reset")
                .variables(variables)
                .build();

        emailService.sendEmailAsync(emailRequest);
    }

    private void transferData(AdKhachHangRequest request, KhachHang kh) {
        kh.setTenKhachHang(request.getTenKhachHang());
        kh.setSdt(request.getSdt());
        kh.setGioiTinh(request.getGioiTinh());
        kh.setNgaySinh(request.getNgaySinh());
        kh.setHinhAnh(request.getHinhAnh());
        kh.setGhiChu(request.getGhiChu());
        kh.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);
    }

    private AdKhachHangResponse toResponse(KhachHang kh) {
        return AdKhachHangResponse.builder()
                .id(kh.getId())
                .maKhachHang(kh.getMaKhachHang())
                .tenKhachHang(kh.getTenKhachHang())
                .email(kh.getTaiKhoan() != null ? kh.getTaiKhoan().getEmail() : null)
                .sdt(kh.getSdt())
                .gioiTinh(kh.getGioiTinh())
                .ngaySinh(kh.getNgaySinh())
                .hinhAnh(kh.getHinhAnh())
                .ghiChu(kh.getGhiChu())
                .trangThai(kh.getTrangThai())
                .build();
    }
}