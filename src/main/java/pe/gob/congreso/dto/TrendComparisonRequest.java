package pe.gob.congreso.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TrendComparisonRequest {

    private List<String> trendNames1;
    private List<String> trendNames2;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public TrendComparisonRequest() {
    }

    public TrendComparisonRequest(List<String> trendNames1, List<String> trendNames2, LocalDateTime startDate, LocalDateTime endDate) {
        this.trendNames1 = trendNames1;
        this.trendNames2 = trendNames2;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<String> getTrendNames1() {
        return trendNames1;
    }

    public void setTrendNames1(List<String> trendNames1) {
        this.trendNames1 = trendNames1;
    }

    public List<String> getTrendNames2() {
        return trendNames2;
    }

    public void setTrendNames2(List<String> trendNames2) {
        this.trendNames2 = trendNames2;
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
        return Objects.equals(trendNames1, that.trendNames1) && Objects.equals(trendNames2, that.trendNames2) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trendNames1, trendNames2, startDate, endDate);
    }

    @Override
    public String toString() {
        return "TrendComparisonRequest{" +
                "trendName1='" + trendNames1 + '\'' +
                ", trendName2='" + trendNames2 + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
