package pe.gob.congreso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.gob.congreso.client.GptService;
import pe.gob.congreso.client.TwitterTrendingClient;
import pe.gob.congreso.dto.*;
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
import java.util.stream.Collectors;

@Service
public class TwitterTrendingService {

    private final TwitterTrendingClient twitterTrendingClient;
    private final TableInfoRepository tableInfoRepository;
    private final TrendRepository trendRepository;
    private final WorldTrendsInfoRepository worldTrendsInfoRepository;
    private final WorldTrendRepository worldTrendRepository;
    private final ObjectMapper objectMapper;
    @PersistenceContext
    private EntityManager entityManager;

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




    public List<TrendSearchResult> searchTrendsByName(String query, int limit, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LocalDateTime actualStartDate = startDate != null ?
                    startDate : LocalDateTime.now().minusDays(30);
            LocalDateTime actualEndDate = endDate != null ?
                    endDate : LocalDateTime.now();
            // Opción 1: Usar la proyección (más eficiente)
            List<TrendSearchProjection> projections = trendRepository.searchTrendsByName(query, actualStartDate, actualEndDate, limit);

            return projections.stream()
                    .map(projection -> new TrendSearchResult(
                            projection.getRawName(),
                            projection.getDisplayName(),
                            projection.getTotalMentions(),
                            projection.getFirstSeen(),
                            projection.getLastSeen()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Opción 2: Fallback con JPQL si la proyección falla
            try {
                Pageable pageable = PageRequest.of(0, limit);
                return trendRepository.searchTrendsByNameJPQL(query, pageable);
            } catch (Exception ex) {
                // Opción 3: Buscar manualmente si todo falla
                return searchTrendsManually(query, limit);
            }
        }
    }

    // Método de fallback para búsqueda manual
    private List<TrendSearchResult> searchTrendsManually(String query, int limit) {
        try {
            // Usar EntityManager para query más controlada
            String jpql = "SELECT t FROM Trend t " +
                    "WHERE LOWER(t.name) LIKE LOWER(:query) " +
                    "   OR LOWER(t.rawName) LIKE LOWER(:query) " +
                    "ORDER BY t.count DESC";

            List<Trend> trends = entityManager.createQuery(jpql, Trend.class)
                    .setParameter("query", "%" + query.toLowerCase() + "%")
                    .setMaxResults(limit * 3) // Obtener más para agrupar
                    .getResultList();

            // Agrupar manualmente por rawName
            Map<String, TrendSearchResult> groupedResults = new HashMap<>();

            for (Trend trend : trends) {
                String key = trend.getRawName();
                TrendSearchResult existing = groupedResults.get(key);

                if (existing == null) {
                    groupedResults.put(key, new TrendSearchResult(
                            trend.getRawName(),
                            trend.getName(),
                            trend.getCount(),
                            trend.getTimestamp(),
                            trend.getTimestamp()
                    ));
                } else {
                    existing.setTotalMentions(existing.getTotalMentions() + trend.getCount());
                    if (trend.getTimestamp().isBefore(existing.getFirstSeen())) {
                        existing.setFirstSeen(trend.getTimestamp());
                    }
                    if (trend.getTimestamp().isAfter(existing.getLastSeen())) {
                        existing.setLastSeen(trend.getTimestamp());
                    }
                }
            }

            return groupedResults.values().stream()
                    .sorted((a, b) -> Long.compare(b.getTotalMentions(), a.getTotalMentions()))
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Nuevo: Comparar dos trends
    public TrendComparisonResult compareTrends(TrendComparisonRequest request) {
        LocalDateTime endDate = request.getEndDate() != null ?
                request.getEndDate() : LocalDateTime.now();
        LocalDateTime startDate = request.getStartDate() != null ?
                request.getStartDate() : endDate.minusDays(30);

        List<TrendDailyData> trend1Data = getGroupDailyData(request.getTrendNames1(), startDate, endDate);
        List<TrendDailyData> trend2Data = getGroupDailyData(request.getTrendNames2(), startDate, endDate);

        TrendComparisonSummary summary = calculateComparisonSummary(trend1Data, trend2Data);

        return new TrendComparisonResult(
                String.join(", ", request.getTrendNames1()),
                String.join(", ", request.getTrendNames2()),
                trend1Data,
                trend2Data,
                summary
        );
    }

    private List<TrendDailyData> getGroupDailyData(List<String> rawNames, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = trendRepository.getGroupDailyStatsNative(rawNames, startDate, endDate);

        return results.stream()
                .map(row -> new TrendDailyData(
                        (String) row[0],
                        ((java.sql.Date) row[1]).toLocalDate(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).longValue(),
                        ((Number) row[5]).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    // Obtener datos diarios de un trend
//    private List<TrendDailyData> getTrendDailyData(String rawName, LocalDateTime startDate, LocalDateTime endDate) {
//        List<Object[]> results = trendRepository.getTrendDailyStatsNative(rawName, startDate, endDate);
//
//        return results.stream()
//                .map(row -> new TrendDailyData(
//                        ((java.sql.Date) row[0]).toLocalDate(),
//                        ((Number) row[1]).longValue(), // total_count
//                        ((Number) row[2]).intValue(),  // peak_hour
//                        ((Number) row[3]).longValue(), // peak_count
//                        ((Number) row[4]).doubleValue() // growth_rate
//                ))
//                .collect(Collectors.toList());
//    }

    // Calcular resumen estadístico de la comparación
    private TrendComparisonSummary calculateComparisonSummary(List<TrendDailyData> trend1Data,
                                                              List<TrendDailyData> trend2Data) {
        TrendComparisonSummary summary = new TrendComparisonSummary();

        // Estadísticas del trend 1
        long totalMentions1 = trend1Data.stream().mapToLong(TrendDailyData::getTotalCount).sum();
        OptionalLong peakValue1Opt = trend1Data.stream().mapToLong(TrendDailyData::getTotalCount).max();
        Optional<TrendDailyData> peakDay1 = trend1Data.stream()
                .max(Comparator.comparingLong(TrendDailyData::getTotalCount));
        double avgGrowth1 = trend1Data.stream().mapToDouble(TrendDailyData::getGrowthRate).average().orElse(0);
        int activeDays1 = (int) trend1Data.stream().filter(d -> d.getTotalCount() > 0).count();

        // Estadísticas del trend 2
        long totalMentions2 = trend2Data.stream().mapToLong(TrendDailyData::getTotalCount).sum();
        OptionalLong peakValue2Opt = trend2Data.stream().mapToLong(TrendDailyData::getTotalCount).max();
        Optional<TrendDailyData> peakDay2 = trend2Data.stream()
                .max(Comparator.comparingLong(TrendDailyData::getTotalCount));
        double avgGrowth2 = trend2Data.stream().mapToDouble(TrendDailyData::getGrowthRate).average().orElse(0);
        int activeDays2 = (int) trend2Data.stream().filter(d -> d.getTotalCount() > 0).count();

        summary.setTotalMentions1(totalMentions1);
        summary.setTotalMentions2(totalMentions2);
        summary.setPeakDate1(peakDay1.map(TrendDailyData::getDate).orElse(null));
        summary.setPeakDate2(peakDay2.map(TrendDailyData::getDate).orElse(null));
        summary.setPeakValue1(peakValue1Opt.orElse(0));
        summary.setPeakValue2(peakValue2Opt.orElse(0));
        summary.setAverageGrowth1(avgGrowth1);
        summary.setAverageGrowth2(avgGrowth2);
        summary.setActiveDays1(activeDays1);
        summary.setActiveDays2(activeDays2);

        return summary;
    }

    // Nuevo: Análisis con IA
    public TrendAnalysisResult analyzeTrendComparison(TrendComparisonData comparisonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String jsonData = objectMapper.writeValueAsString(comparisonData);

            String prompt = buildAnalysisPrompt(comparisonData.getTrendName1(),
                    comparisonData.getTrendName2()) + jsonData;
            GptService gptService = new GptService();
            String gptResponse = gptService.sendDataToGPT(prompt);

            // Parsear la respuesta de la IA
            return parseGptAnalysisResponse(gptResponse);

        } catch (Exception e) {
            // Respuesta por defecto en caso de error
            return createDefaultAnalysisResult(comparisonData);
        }
    }

    private String buildAnalysisPrompt(String trendName1, String trendName2) {
        return "Analiza la siguiente comparación entre dos trends de Twitter: '" + trendName1 +
                "' vs '" + trendName2 + "'. " +

                "Basándote en los datos proporcionados, realiza un análisis detallado que incluya: " +

                "1. **Análisis de Impacto General**: Compara el alcance total, picos de actividad y duración de cada trend. " +

                "2. **Patrones Temporales**: Identifica cuándo cada trend tuvo mayor actividad, si hubo patrones de crecimiento similares o diferentes, y cómo evolucionaron día a día. " +

                "3. **Insights Clave**: Extrae 3-5 observaciones importantes sobre el comportamiento de cada trend, incluyendo: " +
                "   - Velocidad de crecimiento " +
                "   - Sostenibilidad en el tiempo " +
                "   - Momentos críticos " +

                "4. **Comparación Directa**: Explica cuál trend tuvo mayor impacto y por qué, considerando no solo números absolutos sino también patrones de engagement. " +

                "5. **Recomendaciones**: Proporciona insights sobre qué tipo de contenido o estrategia podría haber influido en el rendimiento de cada trend. " +

                "Responde ÚNICAMENTE con un JSON válido (sin markdown, sin caracteres de escape) con esta estructura: " +
                "{ " +
                "  \"analysis\": \"[análisis general detallado]\", " +
                "  \"keyInsights\": [\"insight1\", \"insight2\", \"insight3\"], " +
                "  \"comparison\": \"[comparación directa]\", " +
                "  \"recommendation\": \"[recomendaciones y conclusiones]\", " +
                "  \"confidenceScore\": 0.85 " +
                "} " +

                "\n\nDatos para analizar:\n";
    }

    private TrendAnalysisResult parseGptAnalysisResponse(String gptResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(gptResponse, TrendAnalysisResult.class);
        } catch (Exception e) {
            // Si no puede parsear, crear respuesta básica
            TrendAnalysisResult result = new TrendAnalysisResult();
            result.setAnalysis("Error al procesar el análisis automático. Datos disponibles para revisión manual.");
            result.setKeyInsights(Arrays.asList("Verificar datos manualmente", "Analizar patrones temporales", "Comparar métricas clave"));
            result.setComparison("Comparación no disponible automáticamente.");
            result.setRecommendation("Revisar los datos numéricos proporcionados para obtener insights.");
            result.setConfidenceScore(0.0);
            return result;
        }
    }

    private TrendAnalysisResult createDefaultAnalysisResult(TrendComparisonData comparisonData) {
        TrendAnalysisResult result = new TrendAnalysisResult();

        String trend1 = comparisonData.getTrendName1();
        String trend2 = comparisonData.getTrendName2();
        TrendComparisonSummary summary = comparisonData.getSummary();

        // Análisis básico basado en números
        String winner = summary.getTotalMentions1() > summary.getTotalMentions2() ? trend1 : trend2;
        result.setAnalysis(String.format("Comparación entre %s y %s. %s obtuvo mayor volumen total de menciones.",
                trend1, trend2, winner));

        result.setKeyInsights(Arrays.asList(
                String.format("%s: %,d menciones totales", trend1, summary.getTotalMentions1()),
                String.format("%s: %,d menciones totales", trend2, summary.getTotalMentions2()),
                String.format("Diferencia de impacto: %,.0f%%",
                        Math.abs(summary.getTotalMentions1() - summary.getTotalMentions2()) * 100.0 /
                                Math.max(summary.getTotalMentions1(), summary.getTotalMentions2()))
        ));

        result.setComparison(String.format("%s tuvo un impacto %s que %s en términos de volumen total.",
                winner,
                summary.getTotalMentions1() != summary.getTotalMentions2() ? "mayor" : "similar",
                summary.getTotalMentions1() > summary.getTotalMentions2() ? trend2 : trend1));

        result.setRecommendation("Análisis detallado recomendado para entender patrones temporales y contexto.");
        result.setConfidenceScore(0.6);

        return result;
    }

    public TrendDailyComparisonResult compareTrendsForDaily(TrendComparisonRequest request) {
        LocalDateTime endDate = request.getEndDate() != null ?
                request.getEndDate() : LocalDateTime.now();
        LocalDateTime startDate = request.getStartDate() != null ?
                request.getStartDate() : endDate.minusDays(30);

        // Obtener todos los registros para ambos grupos
        List<Trend> trends1 = getTrendsByNames(request.getTrendNames1(), startDate, endDate);
        List<Trend> trends2 = getTrendsByNames(request.getTrendNames2(), startDate, endDate);

        // Convertir a DTOs diarios
        List<TrendDailyDetail> trend1Details = convertToDailyDetails(trends1);
        List<TrendDailyDetail> trend2Details = convertToDailyDetails(trends2);

        // Calcular estadísticas de comparación
        TrendComparisonSummary summary = calculateDailyComparisonSummary(trend1Details, trend2Details);

        return new TrendDailyComparisonResult(
                String.join(", ", request.getTrendNames1()),
                String.join(", ", request.getTrendNames2()),
                trend1Details,
                trend2Details,
                summary
        );
    }

    private List<Trend> getTrendsByNames(List<String> rawNames, LocalDateTime startDate, LocalDateTime endDate) {
        if (rawNames == null || rawNames.isEmpty()) {
            return Collections.emptyList();
        }
        return trendRepository.findTrendsByNamesAndDateRange(rawNames, startDate, endDate);
    }

    private List<TrendDailyDetail> convertToDailyDetails(List<Trend> trends) {
        return trends.stream()
                .map(trend -> {
                    TrendDailyDetail detail = new TrendDailyDetail();
                    detail.setDate(trend.getTimestamp().toLocalDate());
                    detail.setRawName(trend.getRawName());
                    detail.setDisplayName(trend.getName());
                    detail.setCount(trend.getCount());
                    detail.setTimestamp(trend.getTimestamp());
                    detail.setTrendId(trend.getTrendId());
                    detail.setTableName(trend.getTableName());
                    return detail;
                })
                .collect(Collectors.toList());
    }

    private TrendComparisonSummary calculateDailyComparisonSummary(
            List<TrendDailyDetail> trend1Details,
            List<TrendDailyDetail> trend2Details) {

        TrendComparisonSummary summary = new TrendComparisonSummary();

        // Estadísticas del trend 1
        long totalMentions1 = trend1Details.stream().mapToInt(TrendDailyDetail::getCount).sum();
        Optional<Integer> peakValue1Opt = trend1Details.stream().map(TrendDailyDetail::getCount).max(Integer::compare);
        Optional<TrendDailyDetail> peakDay1 = trend1Details.stream()
                .max(Comparator.comparingInt(TrendDailyDetail::getCount));
        int activeDays1 = (int) trend1Details.stream()
                .collect(Collectors.groupingBy(TrendDailyDetail::getDate))
                .size();

        // Estadísticas del trend 2
        long totalMentions2 = trend2Details.stream().mapToInt(TrendDailyDetail::getCount).sum();
        Optional<Integer> peakValue2Opt = trend2Details.stream().map(TrendDailyDetail::getCount).max(Integer::compare);
        Optional<TrendDailyDetail> peakDay2 = trend2Details.stream()
                .max(Comparator.comparingInt(TrendDailyDetail::getCount));
        int activeDays2 = (int) trend2Details.stream()
                .collect(Collectors.groupingBy(TrendDailyDetail::getDate))
                .size();

        summary.setTotalMentions1(totalMentions1);
        summary.setTotalMentions2(totalMentions2);
        summary.setPeakDate1(peakDay1.map(TrendDailyDetail::getDate).orElse(null));
        summary.setPeakDate2(peakDay2.map(TrendDailyDetail::getDate).orElse(null));
        summary.setPeakValue1(peakValue1Opt.orElse(0));
        summary.setPeakValue2(peakValue2Opt.orElse(0));
        summary.setActiveDays1(activeDays1);
        summary.setActiveDays2(activeDays2);

        return summary;
    }

}
