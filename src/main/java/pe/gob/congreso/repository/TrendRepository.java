package pe.gob.congreso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.congreso.entity.Trend;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendRepository extends JpaRepository<Trend, Long> {

    @Query(value = "SELECT time_bucket_gapfill('1 hour', timestamp) as bucket, " +
            "raw_name, " +
            "LOCF(AVG(count)) as avg_count, " +
            "interpolate(MAX(count)) as max_count " +
            "FROM trends " +
            "WHERE raw_name IN :rawNames " +
            "AND timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY bucket, raw_name " +
            "ORDER BY bucket ASC",
            nativeQuery = true)
    List<Object[]> findHistoricalData(
            @Param("rawNames") List<String> rawNames,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT time_bucket_gapfill('1 day', timestamp) as bucket_day, " +
            "raw_name, " +
            "MAX(count) as pico_maximo_diario, " +
            "MAX(count) as promedio_diario " +
            "FROM trends " +
            "WHERE raw_name IN :rawNames " +
            "AND timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY bucket_day, raw_name " +
            "ORDER BY bucket_day ASC",
            nativeQuery = true)
    List<Object[]> findDailyTrends(
            @Param("rawNames") List<String> rawNames,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT t.* FROM ( " +
            "SELECT raw_name, MAX(count) as max_count " +
            "FROM trends " +
            "WHERE timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY raw_name " +
            "ORDER BY max_count DESC " +
            "LIMIT 50" +
            ") AS top_trends " +
            "JOIN LATERAL ( " +
            "SELECT * FROM trends " +
            "WHERE raw_name = top_trends.raw_name AND count = top_trends.max_count " +
            "ORDER BY timestamp DESC " +
            "LIMIT 1" +
            ") t ON true " +
            "ORDER BY top_trends.max_count DESC",
            nativeQuery = true)
    List<Trend> findTopTrendsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

