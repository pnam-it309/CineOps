package service.cinema.be.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import service.cinema.be.infrastructure.constant.EntityProperties;
import service.cinema.be.infrastructure.listener.CreateSimpleEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(CreateSimpleEntityListener.class)
public abstract class SimpleEntity implements IsIdentified {

    @Id
    @Column(length = EntityProperties.LENGTH_ID, updatable = false)
    private String id;

}
