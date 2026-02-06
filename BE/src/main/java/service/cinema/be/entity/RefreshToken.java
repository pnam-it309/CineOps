package service.cinema.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.cinema.be.entity.base.PrimaryEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token_lam_moi")
public class RefreshToken extends PrimaryEntity {

    @Column(name = "token_lam_moi", length = 8000)
    private String refreshToken;

    @Column(name = "thoi_gian_het_han")
    private Long expiredAt;

    @Column(name = "id_tai_khoan")
    private String userId;

    @Column(name = "thoi_gian_thu_hoi")
    private Long revokedAt;

}
