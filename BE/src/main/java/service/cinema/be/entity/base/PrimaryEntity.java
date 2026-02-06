package service.cinema.be.entity.base;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import service.cinema.be.infrastructure.constant.EntityProperties;
import service.cinema.be.infrastructure.constant.EntityStatus;
import service.cinema.be.infrastructure.listener.CreatePrimaryEntityListener;


@Getter
@Setter
@MappedSuperclass
@EntityListeners(CreatePrimaryEntityListener.class)
public abstract class PrimaryEntity extends AuditEntity implements IsIdentified {

    @Id
    @Column(length = EntityProperties.LENGTH_ID, updatable = false)
    private String id;

//    @Column(name = "status")
//    @Enumerated(EnumType.ORDINAL)
//    private EntityStatus status;

    // @Column(name = "ma")
    // private String ma;

    // @Column(name = "ten")
    // private String ten;

}
