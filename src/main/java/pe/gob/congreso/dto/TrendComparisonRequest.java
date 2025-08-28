package pe.gob.congreso.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class TrendComparisonRequest {

    private String trendName1;
    private String trendName2;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public TrendComparisonRequest() {
    }

    public TrendComparisonRequest(String trendName1, String trendName2, LocalDateTime startDate, LocalDateTime endDate) {
        this.trendName1 = trendName1;
        this.trendName2 = trendName2;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrendComparisonRequest that = (TrendComparisonRequest) o;
        return Objects.equals(trendName1, that.trendName1) && Objects.equals(trendName2, that.trendName2) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trendName1, trendName2, startDate, endDate);
    }

    @Override
    public String toString() {
        return "TrendComparisonRequest{" +
                "trendName1='" + trendName1 + '\'' +
                ", trendName2='" + trendName2 + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
