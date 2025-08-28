package pe.gob.congreso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.congreso.entity.TableInfo;

public interface TableInfoRepository extends JpaRepository<TableInfo, Long> {
}

