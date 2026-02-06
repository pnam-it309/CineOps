package service.cinema.be.infrastructure.security.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.infrastructure.constant.EntityRole;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class UserPrincipal implements OAuth2User, UserDetails {

    @Getter
    private final String id;
    @Getter
    private final String email;
    @Getter
    private final String fullName;
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    // Constructor
    public UserPrincipal(String id, String email, String fullName, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Create UserPrincipal from KhachHang entity
     * Extracts role from TaiKhoan -> PhanQuyen relationship
     */
    public static UserPrincipal createFromKhachHang(KhachHang khachHang) {
        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        
        Collection<GrantedAuthority> authorities;
        if (taiKhoan != null && taiKhoan.getPhanQuyen() != null) {
            PhanQuyen phanQuyen = taiKhoan.getPhanQuyen();
            // Convert ma_phan_quyen to EntityRole
            EntityRole entityRole = convertToEntityRole(phanQuyen.getMaPhanQuyen());
            authorities = Collections.singletonList(
                new SimpleGrantedAuthority(entityRole.getAuthority())
            );
        } else {
            // Default role if no role assigned
            authorities = Collections.singletonList(
                new SimpleGrantedAuthority(EntityRole.CUSTOMER.getAuthority())
            );
        }

        return new UserPrincipal(
                khachHang.getId(),
                khachHang.getEmail(),
                khachHang.getTenKhachHang(),
                taiKhoan != null ? taiKhoan.getMatKhau() : null,
                authorities
        );
    }

    /**
     * Convert ma_phan_quyen string to EntityRole enum
     */
    private static EntityRole convertToEntityRole(String maPhanQuyen) {
        try {
            return EntityRole.valueOf(maPhanQuyen.toUpperCase());
        } catch (Exception e) {
            log.warn("Unknown role: {}, defaulting to ROLE_USER", maPhanQuyen);
            return EntityRole.CUSTOMER;
        }
    }

    /**
     * Create UserPrincipal with single role (for simple authentication)
     */
    public static UserPrincipal createWithRole(String id, String email, String fullName, String password, String roleAuthority) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(roleAuthority)
        );

        return new UserPrincipal(id, email, fullName, password, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return id;
    }

    /**
     * Get the primary role of the user (first authority)
     */
    public String getRole() {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }

}
