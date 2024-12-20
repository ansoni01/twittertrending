package pe.gob.congreso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.congreso.entity.Trend;

public interface TrendRepository extends JpaRepository<Trend, Long> {
}

