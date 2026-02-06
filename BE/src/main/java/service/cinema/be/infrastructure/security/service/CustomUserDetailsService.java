package service.cinema.be.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.repository.UserRepository;

import java.util.Optional;

@Service("customUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading customer by email: {}", email);

        // Find customer with account and role eagerly loaded
        Optional<KhachHang> khachHangOptional = userRepository.findByEmailWithRoles(email);

        if (khachHangOptional.isEmpty()) {
            log.error("Customer not found with email: {}", email);
            throw new UsernameNotFoundException("Customer not found with email: " + email);
        }

        KhachHang khachHang = khachHangOptional.get();
        
        // Check if customer has account
        if (khachHang.getTaiKhoan() == null) {
            log.error("Customer {} has no account", email);
            throw new UsernameNotFoundException("Customer has no account: " + email);
        }

        // Check if account has role
        if (khachHang.getTaiKhoan().getPhanQuyen() == null) {
            log.error("Customer {} has no role assigned", email);
            throw new UsernameNotFoundException("Customer has no role assigned: " + email);
        }

        log.debug("Successfully loaded customer: {} with role: {}", 
                email, khachHang.getTaiKhoan().getPhanQuyen().getTenVaiTro());

        return UserPrincipal.createFromKhachHang(khachHang);
    }

}
