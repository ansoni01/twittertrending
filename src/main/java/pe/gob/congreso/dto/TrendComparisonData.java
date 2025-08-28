package pe.gob.congreso.dto;

import java.util.List;

public class TrendComparisonData {

    private String trendName1;
    private String trendName2;
    private List<TrendDailyData> trend1Data;
    private List<TrendDailyData> trend2Data;
    private TrendComparisonSummary summary;

    public TrendComparisonData() {
    }

    public TrendComparisonData(String trendName1, String trendName2, List<TrendDailyData> trend1Data, List<TrendDailyData> trend2Data, TrendComparisonSummary summary) {
        this.trendName1 = trendName1;
        this.trendName2 = trendName2;
        this.trend1Data = trend1Data;
        this.trend2Data = trend2Data;
        this.summary = summary;
    }

    public String getTrendName1() {
        return trendName1;
    }

    public void setTrendName1(String trendName1) {
        this.trendName1 = trendName1;
    }

    public String getTrendName2() {
        return trendName2;
    }

    public void setTrendName2(String trendName2) {
        this.trendName2 = trendName2;
    }

    public List<TrendDailyData> getTrend1Data() {
        return trend1Data;
    }

    public void setTrend1Data(List<TrendDailyData> trend1Data) {
        this.trend1Data = trend1Data;
    }

    public List<TrendDailyData> getTrend2Data() {
        return trend2Data;
    }

    public void setTrend2Data(List<TrendDailyData> trend2Data) {
        this.trend2Data = trend2Data;
    }

    public TrendComparisonSummary getSummary() {
        return summary;
    }

    public void setSummary(TrendComparisonSummary summary) {
        this.summary = summary;
    }
}
