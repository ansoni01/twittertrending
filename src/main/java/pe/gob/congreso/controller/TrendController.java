package pe.gob.congreso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.congreso.dto.*;
import pe.gob.congreso.entity.HistoricalData;
import pe.gob.congreso.entity.Trend;
import pe.gob.congreso.service.TwitterTrendingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trends")
public class TrendController {

    @Autowired
    private TwitterTrendingService trendService;

    @GetMapping("/top")
    public ResponseEntity<List<Trend>> getTopTrends(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(trendService.getTopTrends(start, end));
    }

    @PostMapping("/historical")
    public ResponseEntity<Map<String, List<HistoricalData>>> getHistoricalData(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS Z") LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS Z") LocalDateTime end,
            @RequestBody List<String> trendIds) {
        return ResponseEntity.ok(trendService.getHistoricalData(trendIds, start, end));
    }


    @GetMapping("/search")
    public ResponseEntity<List<TrendSearchResult>> searchTrends(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(trendService.searchTrendsByName(query, limit, startDate, endDate));
    }

    @PostMapping("/compare")
    public ResponseEntity<TrendComparisonResult> compareTrends(
            @RequestBody TrendComparisonRequest request) {
        return ResponseEntity.ok(trendService.compareTrends(request));
    }

    // Nuevo: Análisis con IA de comparación de trends
    @PostMapping("/analyze-comparison")
    public ResponseEntity<TrendAnalysisResult> analyzeTrendComparison(
            @RequestBody TrendComparisonData comparisonData) {
        return ResponseEntity.ok(trendService.analyzeTrendComparison(comparisonData));
    }

    @PostMapping("/compare-for-daily")
    public ResponseEntity<TrendDailyComparisonResult> compareTrendsForDaily(
            @RequestBody TrendComparisonRequest request) {
        return ResponseEntity.ok(trendService.compareTrendsForDaily(request));
    }
}