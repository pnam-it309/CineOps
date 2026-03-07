package service.cinema.be.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.repository.KhachHangRepository;
import service.cinema.be.repository.NhanVienRepository;

import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.repository.TaiKhoanRepository;

import java.util.Optional;

@Service("customUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 1. Try to find if it's a NhanVien
        Optional<NhanVien> nhanVienOp = nhanVienRepository.findByTaiKhoan(taiKhoan);
        if (nhanVienOp.isPresent()) {
            return UserPrincipal.createFromNhanVien(nhanVienOp.get());
        }

        // 2. Try to find if it's a KhachHang
        Optional<KhachHang> khachHangOp = khachHangRepository.findByTaiKhoan(taiKhoan);
        if (khachHangOp.isPresent()) {
            return UserPrincipal.createFromKhachHang(khachHangOp.get());
        }

        throw new UsernameNotFoundException("Profile not found for account: " + email);
    }

}
