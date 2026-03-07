package service.cinema.be.core.admin.quanlynhanvien.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlynhanvien.dto.request.AdNhanVienRequest;
import service.cinema.be.core.admin.quanlynhanvien.dto.response.AdNhanVienResponse;
import service.cinema.be.core.admin.quanlynhanvien.repository.AdNhanVienRepository;
import service.cinema.be.core.email.dto.EmailRequest;
import service.cinema.be.core.email.service.IEmailService;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.repository.PhanQuyenRepository;
import service.cinema.be.repository.TaiKhoanRepository;
import service.cinema.be.utils.RandomNumberGenerator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdNhanVienService {

    private final AdNhanVienRepository adNhanVienRepository;
    private final PhanQuyenRepository phanQuyenRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final service.cinema.be.repository.TokenRepository tokenRepository;
    private final RandomNumberGenerator randomGenerator = new RandomNumberGenerator();

    @Transactional(readOnly = true)
    public Page<AdNhanVienResponse> getAll(String search, String idPhanQuyen, Integer trangThai, Pageable pageable) {
        return adNhanVienRepository.searchNhanVien(search, idPhanQuyen, trangThai, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AdNhanVienResponse getById(String id) {
        NhanVien nv = adNhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên có ID: " + id));
        return toResponse(nv);
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctChucVu() {
        return adNhanVienRepository.findDistinctChucVu();
    }

    @Transactional
    public AdNhanVienResponse create(AdNhanVienRequest request) {
        if (adNhanVienRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email nhân viên đã tồn tại trên hệ thống!");
        }
        if (adNhanVienRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại trên hệ thống!");
        }
        if (adNhanVienRepository.existsByCccd(request.getCccd())) {
            throw new IllegalArgumentException("Số CCCD đã tồn tại trên hệ thống!");
        }

        // 1. Generate random password
        String rawPassword = randomGenerator.randomPassword();

        // 2. Create TaiKhoan
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setId(UUID.randomUUID().toString());
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setMat_khau(passwordEncoder.encode(rawPassword));
        taiKhoan.setTrangThai(1); 

        if (request.getIdPhanQuyen() != null) {
            phanQuyenRepository.findById(request.getIdPhanQuyen())
                    .ifPresent(taiKhoan::setPhanQuyen);
        }

        taiKhoan = taiKhoanRepository.save(taiKhoan);

        // 3. Create NhanVien
        NhanVien nv = new NhanVien();
        nv.setId(UUID.randomUUID().toString());
        nv.setMaNhanVien(generateMaNhanVien());
        nv.setNgayTao(LocalDateTime.now());
        nv.setTaiKhoan(taiKhoan); 

        transferData(request, nv);
        NhanVien savedNv = adNhanVienRepository.save(nv);

        // 4. Send email asynchronously
        sendWelcomeEmail(request.getEmail(), request.getTenNhanVien(), rawPassword);

        return toResponse(savedNv);
    }

    private void sendWelcomeEmail(String email, String name, String password) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("staffName", name);
        variables.put("email", email);
        variables.put("password", password);
        variables.put("loginUrl", "http://localhost:3000/login"); 

        EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .subject("Chào mừng bạn gia nhập đội ngũ CineOps")
                .templateName("email/new-staff-welcome")
                .variables(variables)
                .build();

        emailService.sendEmailAsync(emailRequest);
    }

    @Transactional
    public AdNhanVienResponse update(String id, AdNhanVienRequest request) {
        NhanVien nv = adNhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên có ID: " + id));

        if (adNhanVienRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi nhân viên khác!");
        }
        if (adNhanVienRepository.existsBySoDienThoaiAndIdNot(request.getSoDienThoai(), id)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi nhân viên khác!");
        }
        if (adNhanVienRepository.existsByCccdAndIdNot(request.getCccd(), id)) {
            throw new IllegalArgumentException("Số CCCD đã được sử dụng bởi nhân viên khác!");
        }

        // Update Account details
            if (nv.getTaiKhoan() != null) {
                nv.getTaiKhoan().setEmail(request.getEmail());
                if (request.getIdPhanQuyen() != null) {
                    phanQuyenRepository.findById(request.getIdPhanQuyen())
                            .ifPresent(nv.getTaiKhoan()::setPhanQuyen);
                }
                // Admin is no longer allowed to change passwords for others directly.
                // Removed nv.getTaiKhoan().setMat_khau(...)
                taiKhoanRepository.save(nv.getTaiKhoan());
            }
        transferData(request, nv);
        nv.setNgayCapNhat(LocalDateTime.now());
        return toResponse(adNhanVienRepository.save(nv));
    }

    @Transactional
    public void delete(String id) {
        NhanVien nv = adNhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên có ID: " + id));
        nv.setTrangThai(0);
        if (nv.getTaiKhoan() != null) {
            nv.getTaiKhoan().setTrangThai(0);
            taiKhoanRepository.save(nv.getTaiKhoan());
        }
        adNhanVienRepository.save(nv);
    }

    private String generateMaNhanVien() {
        String maxMa = adNhanVienRepository.findMaxMaNhanVien();
        if (maxMa == null || maxMa.isEmpty()) {
            return "NV001";
        }
        try {
            int lastNumber = Integer.parseInt(maxMa.substring(2));
            return String.format("NV%03d", lastNumber + 1);
        } catch (Exception e) {
            return "NV" + System.currentTimeMillis() % 100000;
        }
    }

    @Transactional
    public void requestPasswordReset(String id) {
        NhanVien nv = adNhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên!"));
        
        TaiKhoan tk = nv.getTaiKhoan();
        if (tk == null || tk.getEmail() == null) {
            throw new IllegalArgumentException("Nhân viên chưa có tài khoản hoặc email!");
        }

        // Generate a reset token (reusing Token entity)
        service.cinema.be.entity.Token resetToken = new service.cinema.be.entity.Token();
        resetToken.setId(UUID.randomUUID().toString());
        resetToken.setTaiKhoan(tk);
        resetToken.setMaToken(UUID.randomUUID().toString());
        resetToken.setNgayHetHan(LocalDateTime.now().plusHours(24));
        resetToken.setTrangThai(1);
        
        // Save token to DB
        tokenRepository.save(resetToken);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("staffName", nv.getTenNhanVien());
        variables.put("resetLink", "http://localhost:3456/reset-password?token=" + resetToken.getMaToken());
        variables.put("expiryHours", 24);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(tk.getEmail())
                .subject("Yêu cầu đặt lại mật khẩu - CineOps")
                .templateName("email/password-reset")
                .variables(variables)
                .build();

        emailService.sendEmailAsync(emailRequest);
    }

    private void transferData(AdNhanVienRequest request, NhanVien nv) {
        nv.setTenNhanVien(request.getTenNhanVien());
        nv.setSoDienThoai(request.getSoDienThoai());
        nv.setCccd(request.getCccd());
        nv.setNgaySinh(request.getNgaySinh());
        nv.setQueQuan(request.getQueQuan());
        nv.setGioiTinh(request.getGioiTinh());
        nv.setChucVu(request.getChucVu());
        nv.setAnhNhanVien(request.getAnhNhanVien());
        nv.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);
    }

    private AdNhanVienResponse toResponse(NhanVien nv) {
        return AdNhanVienResponse.builder()
                .id(nv.getId())
                .maNhanVien(nv.getMaNhanVien())
                .tenNhanVien(nv.getTenNhanVien())
                .email(nv.getTaiKhoan() != null ? nv.getTaiKhoan().getEmail() : null)
                .soDienThoai(nv.getSoDienThoai())
                .cccd(nv.getCccd())
                .ngaySinh(nv.getNgaySinh())
                .queQuan(nv.getQueQuan())
                .gioiTinh(nv.getGioiTinh())
                .chucVu(nv.getChucVu())
                .anhNhanVien(nv.getAnhNhanVien())
                .idPhanQuyen(nv.getTaiKhoan() != null && nv.getTaiKhoan().getPhanQuyen() != null ? nv.getTaiKhoan().getPhanQuyen().getId() : null)
                .tenPhanQuyen(nv.getTaiKhoan() != null && nv.getTaiKhoan().getPhanQuyen() != null ? nv.getTaiKhoan().getPhanQuyen().getTenVaiTro() : null)
                .trangThai(nv.getTrangThai())
                .ngayTao(nv.getNgayTao())
                .build();
    }
}