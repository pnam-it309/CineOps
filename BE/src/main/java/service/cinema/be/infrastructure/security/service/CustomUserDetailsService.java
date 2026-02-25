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

import java.util.Optional;

@Service("customUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        // 1. Try to find in NhanVien (Staff/Admin)
        Optional<NhanVien> nhanVienOp = nhanVienRepository.findByEmail(email);
        if (nhanVienOp.isPresent()) {
            log.debug("Successfully loaded NhanVien: {}", email);
            return UserPrincipal.createFromNhanVien(nhanVienOp.get());
        }

        // 2. Try to find in KhachHang (Customer)
        Optional<KhachHang> khachHangOp = khachHangRepository.findByEmail(email);
        if (khachHangOp.isPresent()) {
            log.debug("Successfully loaded KhachHang: {}", email);
            return UserPrincipal.createFromKhachHang(khachHangOp.get());
        }

        log.error("User not found with email: {}", email);
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
