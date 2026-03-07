package service.cinema.be.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.infrastructure.constant.EntityRole;
import service.cinema.be.repository.KhachHangRepository;
import service.cinema.be.repository.NhanVienRepository;
import service.cinema.be.repository.PhanQuyenRepository;
import service.cinema.be.repository.TaiKhoanRepository;
import service.cinema.be.utils.AppConfig;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final PhanQuyenRepository phanQuyenRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final AppConfig appConfig;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!appConfig.isGenerateDb()) {
            log.info("[DataSeeder] GENERATE_DB=false → Bỏ qua seeding dữ liệu.");
            return;
        }

        log.info("[DataSeeder] ===== Bắt đầu seeding dữ liệu =====");

        PhanQuyen adminRole  = seedRole(EntityRole.ADMIN,    "Quản trị viên", "Toàn quyền hệ thống");
        PhanQuyen staffRole  = seedRole(EntityRole.STAFF,    "Nhân viên",     "Quản lý ca làm, bán vé");
                                seedRole(EntityRole.CUSTOMER, "Khách hàng",    "Đặt vé, xem lịch chiếu");

        String adminEmail    = appConfig.getUserEmail();
        String adminName     = appConfig.getUserName();
        String adminCode     = appConfig.getUserCode();
        String adminPhone    = appConfig.getUserPhone();

        seedAdminStaff(adminEmail, adminName, adminCode, adminPhone, adminRole);

        log.info("[DataSeeder] ===== Seeding hoàn tất =====");
    }

    private PhanQuyen seedRole(EntityRole entityRole, String tenVaiTro, String moTa) {
        String maPhanQuyen = entityRole.name().toLowerCase();
        return phanQuyenRepository.findByMaPhanQuyen(maPhanQuyen).orElseGet(() -> {
            PhanQuyen pq = new PhanQuyen();
            pq.setId(UUID.randomUUID().toString());
            pq.setMaPhanQuyen(maPhanQuyen);
            pq.setTenVaiTro(tenVaiTro);
            pq.setTrangThai(1);
            return phanQuyenRepository.save(pq);
        });
    }

    private void seedAdminStaff(String email, String name, String code, String phone, PhanQuyen phanQuyen) {
        String rawPassword = appConfig.getUserPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Find account first
        Optional<TaiKhoan> tkOptional = taiKhoanRepository.findByEmail(email);
        TaiKhoan tk;
        if (tkOptional.isPresent()) {
            tk = tkOptional.get();
            if (tk.getMat_khau() == null || tk.getMat_khau().isEmpty()) {
                tk.setMat_khau(encodedPassword);
                tk.setPhanQuyen(phanQuyen);
                taiKhoanRepository.save(tk);
                log.info("[DataSeeder] 🔄 Cập nhật mật khẩu tài khoản Admin: {}", email);
            }
        } else {
            tk = new TaiKhoan();
            tk.setId(UUID.randomUUID().toString());
            tk.setEmail(email);
            tk.setMat_khau(encodedPassword);
            tk.setPhanQuyen(phanQuyen);
            tk.setTrangThai(1);
            tk = taiKhoanRepository.save(tk);
            log.info("[DataSeeder] ✅ Tạo tài khoản Admin mới: {}", email);
        }

        // Check if NhanVien exists for this account
        Optional<NhanVien> nvOptional = nhanVienRepository.findByTaiKhoan(tk);
        if (nvOptional.isEmpty()) {
            NhanVien nv = new NhanVien();
            nv.setId(UUID.randomUUID().toString());
            nv.setMaNhanVien(code);
            nv.setTenNhanVien(name);
            nv.setSoDienThoai(phone);
            nv.setTaiKhoan(tk);
            nv.setTrangThai(1);
            nhanVienRepository.save(nv);
            log.info("[DataSeeder] ✅ Tạo hồ sơ nhân viên Admin cho: {}", email);
        } else {
            log.info("[DataSeeder] ⏭ Admin Staff hồ sơ đã tồn tại cho: {}", email);
        }
    }
}
