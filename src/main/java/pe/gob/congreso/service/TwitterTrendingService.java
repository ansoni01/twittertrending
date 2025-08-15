package pe.gob.congreso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pe.gob.congreso.client.TwitterTrendingClient;
import pe.gob.congreso.entity.*;
import pe.gob.congreso.repository.TableInfoRepository;
import pe.gob.congreso.repository.TrendRepository;
import pe.gob.congreso.repository.WorldTrendRepository;
import pe.gob.congreso.repository.WorldTrendsInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class TwitterTrendingService {

    private final TwitterTrendingClient twitterTrendingClient;
    private final TableInfoRepository tableInfoRepository;
    private final TrendRepository trendRepository;
    private final WorldTrendsInfoRepository worldTrendsInfoRepository;
    private final WorldTrendRepository worldTrendRepository;
    private final ObjectMapper objectMapper;

    public TwitterTrendingService(TwitterTrendingClient twitterTrendingClient, TableInfoRepository tableInfoRepository, TrendRepository trendRepository, WorldTrendsInfoRepository worldTrendsInfoRepository, WorldTrendRepository worldTrendRepository, ObjectMapper objectMapper) {
        this.twitterTrendingClient = twitterTrendingClient;
        this.tableInfoRepository = tableInfoRepository;
        this.trendRepository = trendRepository;
        this.worldTrendsInfoRepository = worldTrendsInfoRepository;
        this.worldTrendRepository = worldTrendRepository;
        this.objectMapper = objectMapper;
    }

    public String fetchCookies() {
        return twitterTrendingClient.getCookies();
    }

    public String fetchTrends(String country) {
        return twitterTrendingClient.getTrends(country);
    }

    @Transactional
    public void processAndSaveTrends(String country) {
        try {

            String json = fetchTrends(country);

            JsonNode rootNode = objectMapper.readTree(json);

            Iterator<Map.Entry<String, JsonNode>> tables = rootNode.fields();
            while (tables.hasNext()) {
                Map.Entry<String, JsonNode> tableEntry = tables.next();
                String tableName = tableEntry.getKey();

                if (tableName.equals("worldTT")) {

                    JsonNode worldInfo = tableEntry.getValue().get("info");
                    JsonNode worldTrends = tableEntry.getValue().get("trends");

                    WorldTrendsInfo worldTrendsInfo = new WorldTrendsInfo();
                    worldTrendsInfo.setTimestamp(worldInfo.get("timestamps").asLong());
                    worldTrendsInfo.setCountry(worldInfo.get("country").asText());
                    worldTrendsInfo = worldTrendsInfoRepository.save(worldTrendsInfo);

                    Iterator<Map.Entry<String, JsonNode>> worldTrendEntries = worldTrends.fields();
                    while (worldTrendEntries.hasNext()) {
                        Map.Entry<String, JsonNode> trendEntry = worldTrendEntries.next();
                        WorldTrend trend = new WorldTrend();
                        trend.setWorldTrendsInfo(worldTrendsInfo);
                        trend.setTrendId(trendEntry.getKey());
                        trend.setName(trendEntry.getValue().asText());
                        worldTrendRepository.save(trend);
                    }
                } else {

                    JsonNode info = tableEntry.getValue().get("info");
                    JsonNode trends = tableEntry.getValue().get("trends");

                    TableInfo tableInfo = new TableInfo();
                    tableInfo.setTableName(tableName);
                    Long timestampInSeconds = info.get("timestamps").asLong();
                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestampInSeconds, 0, ZoneOffset.of("-05:00"));
                    tableInfo.setTimestamp(dateTime);
                    tableInfo.setCountry(info.get("country").asText());
                    tableInfo = tableInfoRepository.save(tableInfo);

                    Iterator<Map.Entry<String, JsonNode>> trendEntries = trends.fields();
                    while (trendEntries.hasNext()) {
                        Map.Entry<String, JsonNode> trendEntry = trendEntries.next();
                        String trendId = trendEntry.getKey();
                        JsonNode trendValues = objectMapper.readTree(trendEntry.getValue().asText());

                        Trend trend = new Trend();
                        trend.setTableName(tableName);
                        trend.setTimestamp(dateTime);
                        trend.setTrendId(trendId);
                        trend.setName(trendValues.get(0).asText());
                        trend.setCount(trendValues.get(1).asInt());
                        trend.setRawName(trendValues.get(2).asText());
                        trendRepository.save(trend);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing and saving trends", e);
        }
    }

    public List<Trend> getTopTrends(LocalDateTime start, LocalDateTime end) {
        return trendRepository.findTopTrendsByDateRange(start, end);
    }

    public Map<String, List<HistoricalData>> getHistoricalData(List<String> rawNames, LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = trendRepository.findDailyTrends(rawNames, start, end);
        Map<String, List<HistoricalData>> historicalMap = new HashMap<>();

        for (Object[] row : results) {
            try {
                // 1. Manejar el timestamp (bucket)
                LocalDateTime bucket = convertToLocalDateTime(row[0]);

                // 2. Obtener el nombre
                String rawName = (String) row[1];

                // 3. Manejar avg_count (puede ser null, BigDecimal o Double)
                Double avgCount = convertToDouble(row[2]);

                // 4. Manejar max_count (puede ser null, BigDecimal o Integer)
                Integer maxCount = convertToInteger(row[3]);

                // 5. Crear y agregar el dato histórico
                HistoricalData data = new HistoricalData(bucket, avgCount, maxCount);
                historicalMap.computeIfAbsent(rawName, k -> new ArrayList<>()).add(data);
            } catch (Exception e) {
                // Loggear el error pero continuar procesando los demás registros
                System.err.println("Error procesando fila: " + Arrays.toString(row) + " - " + e.getMessage());
            }
        }

        return historicalMap;
    }

    // Métodos auxiliares para conversión segura
    private LocalDateTime convertToLocalDateTime(Object date) {
        if (date == null) return null;
        if (date instanceof Timestamp) return ((Timestamp) date).toLocalDateTime();
        if (date instanceof Instant) return LocalDateTime.ofInstant((Instant) date, ZoneId.systemDefault());
        if (date instanceof java.util.Date) return ((java.util.Date) date).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        throw new IllegalArgumentException("Tipo de fecha no soportado: " + date.getClass());
    }

    private Double convertToDouble(Object number) {
        if (number == null) return null;
        if (number instanceof BigDecimal) return ((BigDecimal) number).doubleValue();
        if (number instanceof Double) return (Double) number;
        if (number instanceof Integer) return ((Integer) number).doubleValue();
        throw new IllegalArgumentException("Tipo numérico no soportado para conversión a Double: " + number.getClass());
    }

    private Integer convertToInteger(Object number) {
        if (number == null) return null;
        if (number instanceof BigDecimal) return ((BigDecimal) number).intValue();
        if (number instanceof Integer) return (Integer) number;
        if (number instanceof Double) return ((Double) number).intValue();
        throw new IllegalArgumentException("Tipo numérico no soportado para conversión a Integer: " + number.getClass());
    }


}
