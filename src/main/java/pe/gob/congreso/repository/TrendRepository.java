package pe.gob.congreso.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.congreso.dto.TrendSearchProjection;
import pe.gob.congreso.dto.TrendSearchResult;
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
            "LIMIT 5000" +
            ") AS top_trends " +
            "JOIN LATERAL ( " +
            "SELECT * FROM trends " +
            "WHERE raw_name = top_trends.raw_name AND count = top_trends.max_count AND timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY timestamp DESC " +
            "LIMIT 1" +
            ") t ON true " +
            "ORDER BY top_trends.max_count DESC",
            nativeQuery = true)
    List<Trend> findTopTrendsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT DISTINCT " +
            "t.raw_name as rawName, " +
            "t.name as displayName, " +
            "MAX(t.count) as totalMentions, " +
            "MIN(t.timestamp) as firstSeen, " +
            "MAX(t.timestamp) as lastSeen " +
            "FROM trends t " +
            "WHERE (LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(t.raw_name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND t.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY t.raw_name, t.name " +
            "ORDER BY totalMentions DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<TrendSearchProjection> searchTrendsByName(
            @Param("query") String query,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit);

    // Alternativa usando JPQL (más segura)
    @Query("SELECT new pe.gob.congreso.dto.TrendSearchResult(" +
            "t.rawName, " +
            "t.name, " +
            "SUM(t.count), " +
            "MIN(t.timestamp), " +
            "MAX(t.timestamp)) " +
            "FROM Trend t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(t.rawName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "GROUP BY t.rawName, t.name " +
            "ORDER BY SUM(t.count) DESC")
    List<TrendSearchResult> searchTrendsByNameJPQL(
            @Param("query") String query,
            Pageable pageable);

    @Query(value = "SELECT " +
            "DATE(timestamp) as date, " +
            "SUM(count) as total_count, " +
            "EXTRACT(HOUR FROM timestamp) as hour, " +
            "MAX(count) as max_count " +
            "FROM trends " +
            "WHERE raw_name = :rawName " +
            "  AND timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(timestamp), EXTRACT(HOUR FROM timestamp) " +
            "ORDER BY date, hour", nativeQuery = true)
    List<Object[]> getTrendHourlyDataNative(
            @Param("rawName") String rawName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "WITH daily_stats AS ( " +
            "  SELECT " +
            "    DATE(timestamp) AS date, " +
            "    SUM(count) AS total_count, " +
            "    MAX(count) AS peak_count, " +
            "    MODE() WITHIN GROUP (ORDER BY EXTRACT(HOUR FROM timestamp)) AS peak_hour " +
            "  FROM trends " +
            "  WHERE raw_name = :rawName " +
            "    AND timestamp BETWEEN :startDate AND :endDate " +
            "  GROUP BY DATE(timestamp) " +
            "), " +
            "daily_growth AS ( " +
            "  SELECT *, " +
            "    CASE " +
            "      WHEN LAG(total_count) OVER (ORDER BY date) IS NOT NULL " +
            "       AND LAG(total_count) OVER (ORDER BY date) > 0 " +
            "      THEN ((total_count - LAG(total_count) OVER (ORDER BY date))::DECIMAL / " +
            "            LAG(total_count) OVER (ORDER BY date)) * 100 " + // Fixed parenthesis
            "      ELSE 0 " +
            "    END AS growth_rate " +
            "  FROM daily_stats " +
            ") " +
            "SELECT date, total_count, peak_hour, peak_count, growth_rate " +
            "FROM daily_growth " +
            "ORDER BY date", nativeQuery = true)
    List<Object[]> getTrendDailyStatsNative(
            @Param("rawName") String rawName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "WITH daily_stats AS ( " +
            "  SELECT " +
            "    raw_name, " +  // Agregar raw_name
            "    DATE(timestamp) AS date, " +
            "    SUM(count) AS total_count, " +
            "    MAX(count) AS peak_count, " +
            "    MODE() WITHIN GROUP (ORDER BY EXTRACT(HOUR FROM timestamp)) AS peak_hour " +
            "  FROM trends " +
            "  WHERE raw_name IN :rawNames " +
            "    AND timestamp BETWEEN :startDate AND :endDate " +
            "  GROUP BY raw_name, DATE(timestamp) " +  // Agregar raw_name al GROUP BY
            "), " +
            "daily_growth AS ( " +
            "  SELECT *, " +
            "    CASE " +
            "      WHEN LAG(total_count) OVER (PARTITION BY raw_name ORDER BY date) IS NOT NULL " +
            "       AND LAG(total_count) OVER (PARTITION BY raw_name ORDER BY date) > 0 " +
            "      THEN ((total_count - LAG(total_count) OVER (PARTITION BY raw_name ORDER BY date))::DECIMAL / " +
            "            LAG(total_count) OVER (PARTITION BY raw_name ORDER BY date)) * 100 " +
            "      ELSE 0 " +
            "    END AS growth_rate " +
            "  FROM daily_stats " +
            ") " +
            "SELECT raw_name, date, total_count, peak_hour, peak_count, growth_rate " +  // Agregar raw_name
            "FROM daily_growth " +
            "ORDER BY raw_name, date", nativeQuery = true)
    List<Object[]> getGroupDailyStatsNative(
            @Param("rawNames") List<String> rawNames,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Trend t " +
            "WHERE t.rawName IN :rawNames " +
            "AND t.timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY t.timestamp, t.rawName")
    List<Trend> findTrendsByNamesAndDateRange(
            @Param("rawNames") List<String> rawNames,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

