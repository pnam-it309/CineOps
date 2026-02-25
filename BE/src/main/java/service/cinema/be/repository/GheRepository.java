package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Ghe;

@Repository
public interface GheRepository extends JpaRepository<Ghe, String> {
}
