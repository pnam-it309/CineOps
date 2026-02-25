package service.cinema.be.infrastructure.security.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.infrastructure.constant.EntityRole;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
     */
    public static UserPrincipal createFromKhachHang(KhachHang khachHang) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(EntityRole.CUSTOMER.getAuthority())
        );

        return new UserPrincipal(
                khachHang.getId(),
                khachHang.getEmail(),
                khachHang.getTenKhachHang(),
                khachHang.getMatKhau(),
                authorities
        );
    }

    /**
     * Create UserPrincipal from NhanVien entity
     */
    public static UserPrincipal createFromNhanVien(NhanVien nhanVien) {
        Collection<GrantedAuthority> authorities;
        if (nhanVien.getPhanQuyen() != null) {
            PhanQuyen phanQuyen = nhanVien.getPhanQuyen();
            EntityRole entityRole = convertToEntityRole(phanQuyen.getMaPhanQuyen());
            authorities = Collections.singletonList(
                new SimpleGrantedAuthority(entityRole.getAuthority())
            );
        } else {
            authorities = Collections.singletonList(
                new SimpleGrantedAuthority(EntityRole.STAFF.getAuthority())
            );
        }

        return new UserPrincipal(
                nhanVien.getId(),
                nhanVien.getEmail(),
                nhanVien.getTenNhanVien(),
                nhanVien.getMatKhau(),
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
            log.warn("Unknown role: {}, defaulting to STAFF", maPhanQuyen);
            return EntityRole.STAFF;
        }
    }

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

    public String getRole() {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }

}
