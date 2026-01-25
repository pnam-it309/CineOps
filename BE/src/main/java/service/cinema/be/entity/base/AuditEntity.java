package service.cinema.be.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {

    @org.springframework.data.annotation.CreatedDate
    @Column(name = "created_date", updatable = false)
    private Long createdDate;

    @org.springframework.data.annotation.LastModifiedDate
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

}
