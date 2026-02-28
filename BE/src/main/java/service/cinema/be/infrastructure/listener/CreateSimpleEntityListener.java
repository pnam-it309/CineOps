package service.cinema.be.infrastructure.listener;

import service.cinema.be.entity.base.SimpleEntity;
import jakarta.persistence.PrePersist;
import java.util.UUID;

public class CreateSimpleEntityListener {

    @PrePersist
    private void onCreate(SimpleEntity entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
    }

}
