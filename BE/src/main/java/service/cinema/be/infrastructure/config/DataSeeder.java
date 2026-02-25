package service.cinema.be.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.infrastructure.constant.EntityRole;
import service.cinema.be.repository.RoleRepository;
import service.cinema.be.repository.UserRepository;
import service.cinema.be.utils.AppConfig;

import java.util.UUID;

/**
 * DataSeeder: Tự động tạo roles và tài khoản admin khi ứng dụng khởi động.
 *
 * Tài khoản admin được tạo với:
 *  - Email:    USER_EMAIL (từ application.properties)
 *  - Mật khẩu: USER_PASSWORD (từ application.properties, default: Admin@123456)
 *  - Gmail:    ADMIN_GMAIL (nếu khác email chính, tạo thêm entry để login Google)
 *
 * Nếu tài khoản / role đã tồn tại → bỏ qua (idempotent).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppConfig appConfig;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!appConfig.isGenerateDb()) {
            log.info("[DataSeeder] GENERATE_DB=false → Bỏ qua seeding dữ liệu.");
            return;
        }

        log.info("[DataSeeder] ===== Bắt đầu seeding dữ liệu =====");

        // 1. Seed tất cả roles
        PhanQuyen adminRole  = seedRole(EntityRole.ADMIN,    "Quản trị viên", "Toàn quyền hệ thống");
        PhanQuyen staffRole  = seedRole(EntityRole.STAFF,    "Nhân viên",     "Quản lý ca làm, bán vé");
                               seedRole(EntityRole.CUSTOMER, "Khách hàng",    "Đặt vé, xem lịch chiếu");

        // 2. Seed tài khoản admin - 1 email USER_EMAIL dùng cho cả đăng nhập
        //    username/password lẫn Google OAuth2 (đăng nhập Google với đúng email này)
        String adminEmail    = appConfig.getUserEmail();
        String adminPassword = appConfig.getUserPassword();
        String adminName     = appConfig.getUserName();
        String adminCode     = appConfig.getUserCode();
        String adminPhone    = appConfig.getUserPhone();

        PhanQuyen targetRole = resolveRole(appConfig.getRole(), adminRole, staffRole);
        seedKhachHangAccount(adminEmail, adminPassword, adminName, adminCode, adminPhone, targetRole);

        log.info("[DataSeeder] ===== Seeding hoàn tất =====");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    /**
     * Seed một role nếu chưa tồn tại.
     * @return PhanQuyen entity (mới tạo hoặc đã có sẵn)
     */
    private PhanQuyen seedRole(EntityRole entityRole, String tenVaiTro, String moTa) {
        String maPhanQuyen = entityRole.name().toLowerCase(); // "admin" / "staff" / "customer"
        return roleRepository.findByMaPhanQuyen(maPhanQuyen).orElseGet(() -> {
            PhanQuyen pq = new PhanQuyen();
            pq.setId(UUID.randomUUID().toString());
            pq.setMaPhanQuyen(maPhanQuyen);
            pq.setTenVaiTro(tenVaiTro);
            pq.setMoTa(moTa);
            pq.setTrangThai(1);
            PhanQuyen saved = roleRepository.save(pq);
            log.info("[DataSeeder] ✅ Đã tạo role: {} ({})", tenVaiTro, maPhanQuyen);
            return saved;
        });
    }

    /**
     * Seed tài khoản KhachHang.
     * - Nếu chưa tồn tại → tạo mới.
     * - Nếu đã tồn tại nhưng password rỗng/null (OAuth2 user hoặc chưa set) → cập nhật password + role.
     */
    private void seedKhachHangAccount(String email, String rawPassword,
                                       String tenKhachHang, String maKhachHang,
                                       String phone, PhanQuyen phanQuyen) {
        // Kiểm tra đã tồn tại chưa
        var existing = userRepository.findByEmailWithRoles(email);
        if (existing.isPresent()) {
            KhachHang kh = existing.get();
            TaiKhoan tk = kh.getTaiKhoan();
            boolean needUpdate = false;

            // Cập nhật password nếu rỗng/null và config có password
            if (rawPassword != null && !rawPassword.isBlank() && tk != null) {
                String storedPw = tk.getMatKhau();
                if (storedPw == null || storedPw.isBlank() || !storedPw.startsWith("$2")) {
                    tk.setMatKhau(passwordEncoder.encode(rawPassword));
                    log.info("[DataSeeder] 🔑 Cập nhật password cho: {}", email);
                    needUpdate = true;
                }
            }

            // Cập nhật role nếu chưa đúng
            if (tk != null && (tk.getPhanQuyen() == null ||
                    !phanQuyen.getId().equals(tk.getPhanQuyen().getId()))) {
                tk.setPhanQuyen(phanQuyen);
                log.info("[DataSeeder] 🔄 Cập nhật role cho: {} → {}", email, phanQuyen.getMaPhanQuyen());
                needUpdate = true;
            }

            if (needUpdate) {
                userRepository.save(kh);
            } else {
                log.info("[DataSeeder] ⏭  Tài khoản đã đầy đủ, bỏ qua: {}", email);
            }
            return;
        }

        // TaiKhoan
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setId(UUID.randomUUID().toString());
        taiKhoan.setTenTaiKhoan(email);
        taiKhoan.setMatKhau(rawPassword != null ? passwordEncoder.encode(rawPassword) : "");
        taiKhoan.setPhanQuyen(phanQuyen);
        taiKhoan.setTrangThai(1);

        // KhachHang
        KhachHang khachHang = new KhachHang();
        khachHang.setId(UUID.randomUUID().toString());
        khachHang.setTaiKhoan(taiKhoan);
        String emailHash = String.valueOf(Math.abs(email.hashCode() % 100000));
        String code = maKhachHang + "_" + emailHash;
        khachHang.setMaKhachHang(code.substring(0, Math.min(50, code.length())));
        khachHang.setTenKhachHang(tenKhachHang);
        khachHang.setEmail(email);
        khachHang.setSdt(phone);
        khachHang.setTrangThai(1);

        userRepository.save(khachHang);
        log.info("[DataSeeder] ✅ Đã tạo tài khoản: {} | role: {}", email, phanQuyen.getMaPhanQuyen());
    }

    /** Resolve PhanQuyen dựa theo chuỗi config ROLE */
    private PhanQuyen resolveRole(String roleStr, PhanQuyen adminRole, PhanQuyen staffRole) {
        if (roleStr == null) return adminRole;
        return switch (roleStr.toUpperCase()) {
            case "STAFF" -> staffRole;
            default      -> adminRole;  // mặc định ADMIN
        };
    }
}
