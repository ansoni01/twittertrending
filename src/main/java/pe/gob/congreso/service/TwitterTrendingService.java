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

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        List<Object[]> results = trendRepository.findHistoricalData(rawNames, start, end);
        Map<String, List<HistoricalData>> historicalMap = new HashMap<>();

        for (Object[] row : results) {
            LocalDateTime bucket = ((Timestamp) row[0]).toLocalDateTime();
            String rawName = (String) row[1];
            Double avgCount = (Double) row[2];
            Integer maxCount = (Integer) row[3];

            HistoricalData data = new HistoricalData(bucket, avgCount, maxCount);
            historicalMap.computeIfAbsent(rawName, k -> new ArrayList<>()).add(data);
        }

        return historicalMap;
    }


}
