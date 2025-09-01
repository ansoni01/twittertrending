package pe.gob.congreso.dto;

import java.time.LocalDate;
import java.util.Objects;

public class TrendDailyData {
    private String rawName;
    private LocalDate date;
    private long totalCount;
    private int peakHour;
    private long peakCount;
    private double growthRate;

    public TrendDailyData() {
    }

    public TrendDailyData(String rawName, LocalDate date, long totalCount, int peakHour, long peakCount, double growthRate) {
        this.rawName = rawName;
        this.date = date;
        this.totalCount = totalCount;
        this.peakHour = peakHour;
        this.peakCount = peakCount;
        this.growthRate = growthRate;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPeakHour() {
        return peakHour;
    }

    public void setPeakHour(int peakHour) {
        this.peakHour = peakHour;
    }

    public long getPeakCount() {
        return peakCount;
    }

    public void setPeakCount(long peakCount) {
        this.peakCount = peakCount;
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(double growthRate) {
        this.growthRate = growthRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrendDailyData that = (TrendDailyData) o;
        return totalCount == that.totalCount && peakHour == that.peakHour && peakCount == that.peakCount && Double.compare(that.growthRate, growthRate) == 0 && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, totalCount, peakHour, peakCount, growthRate);
    }

    @Override
    public String toString() {
        return "TrendDailyData{" +
                "date=" + date +
                ", totalCount=" + totalCount +
                ", peakHour=" + peakHour +
                ", peakCount=" + peakCount +
                ", growthRate=" + growthRate +
                '}';
    }
}
