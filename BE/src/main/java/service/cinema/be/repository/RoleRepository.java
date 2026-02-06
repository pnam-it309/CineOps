package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.infrastructure.constant.EntityRole;

import java.util.Optional;

/**
 * Repository for PhanQuyen (Role) entity
 */
@Repository
public interface RoleRepository extends JpaRepository<PhanQuyen, String> {

    /**
     * Find role by role code (ma_phan_quyen)
     */
    Optional<PhanQuyen> findByMaPhanQuyen(String maPhanQuyen);

    /**
     * Find role by role name
     */
    Optional<PhanQuyen> findByTenVaiTro(String tenVaiTro);

}
