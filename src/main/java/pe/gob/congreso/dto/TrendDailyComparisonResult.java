package pe.gob.congreso.dto;

import java.util.List;

public class TrendDailyComparisonResult {

    private String trendName1;
    private String trendName2;
    private List<TrendDailyDetail> trend1DailyDetails;
    private List<TrendDailyDetail> trend2DailyDetails;
    private TrendComparisonSummary summary;

    public TrendDailyComparisonResult() {
    }

    public TrendDailyComparisonResult(String trendName1, String trendName2, List<TrendDailyDetail> trend1DailyDetails, List<TrendDailyDetail> trend2DailyDetails, TrendComparisonSummary summary) {
        this.trendName1 = trendName1;
        this.trendName2 = trendName2;
        this.trend1DailyDetails = trend1DailyDetails;
        this.trend2DailyDetails = trend2DailyDetails;
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

    public List<TrendDailyDetail> getTrend1DailyDetails() {
        return trend1DailyDetails;
    }

    public void settrend1DailyDetails(List<TrendDailyDetail> trend1DailyDetails) {
        this.trend1DailyDetails = trend1DailyDetails;
    }

    public List<TrendDailyDetail> getTrend2DailyDetails() {
        return trend2DailyDetails;
    }

    public void setTrend2DailyDetails(List<TrendDailyDetail> trend2DailyDetails) {
        this.trend2DailyDetails = trend2DailyDetails;
    }

    public TrendComparisonSummary getSummary() {
        return summary;
    }

    public void setSummary(TrendComparisonSummary summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "TrendComparisonResult{" +
                "trendName1='" + trendName1 + '\'' +
                ", trendName2='" + trendName2 + '\'' +
                ", trend1DailyDetails=" + trend1DailyDetails +
                ", trend2DailyDetails=" + trend2DailyDetails +
                ", summary=" + summary +
                '}';
    }
}
