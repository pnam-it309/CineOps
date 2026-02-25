package service.cinema.be.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.infrastructure.constant.EntityRole;
import service.cinema.be.repository.KhachHangRepository;
import service.cinema.be.repository.NhanVienRepository;
import service.cinema.be.repository.PhanQuyenRepository;
import service.cinema.be.utils.AppConfig;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/**
 * DataSeeder: Tự động tạo roles và nhân viên admin khi ứng dụng khởi động.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final PhanQuyenRepository phanQuyenRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
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

        // 1. Seed tất cả roles
        PhanQuyen adminRole  = seedRole(EntityRole.ADMIN,    "Quản trị viên", "Toàn quyền hệ thống");
        PhanQuyen staffRole  = seedRole(EntityRole.STAFF,    "Nhân viên",     "Quản lý ca làm, bán vé");
                                seedRole(EntityRole.CUSTOMER, "Khách hàng",    "Đặt vé, xem lịch chiếu");

        // 2. Seed nhân viên admin
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
            pq.setMoTa(moTa);
            pq.setTrangThai(1);
            return phanQuyenRepository.save(pq);
        });
    }

    private void seedAdminStaff(String email, String name, String code, String phone, PhanQuyen phanQuyen) {
        String rawPassword = appConfig.getUserPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        var existing = nhanVienRepository.findByEmail(email);
        if (existing.isPresent()) {
            NhanVien nv = existing.get();
            if (nv.getMatKhau() == null || nv.getMatKhau().isEmpty()) {
                nv.setMatKhau(encodedPassword);
                nhanVienRepository.save(nv);
                log.info("[DataSeeder] 🔄 Đã cập nhật mật khẩu cho Admin Staff: {}", email);
            } else {
                log.info("[DataSeeder] ⏭  Admin Staff đã tồn tại và có mật khẩu: {}", email);
            }
            return;
        }

        NhanVien nv = new NhanVien();
        nv.setId(UUID.randomUUID().toString());
        nv.setMaNhanVien(code);
        nv.setTenNhanVien(name);
        nv.setEmail(email);
        nv.setSoDienThoai(phone);
        nv.setPhanQuyen(phanQuyen);
        nv.setMatKhau(encodedPassword);
        nv.setTrangThai(1);
        nhanVienRepository.save(nv);
        log.info("[DataSeeder] ✅ Đã tạo nhân viên admin: {}", email);
    }
}
