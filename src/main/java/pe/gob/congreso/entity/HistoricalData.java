package pe.gob.congreso.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class HistoricalData {

    private LocalDateTime timestamp;
    private Double avgCount;
    private Integer maxCount;

    public HistoricalData(LocalDateTime timestamp, Double avgCount, Integer maxCount) {
        this.timestamp = timestamp;
        this.avgCount = avgCount;
        this.maxCount = maxCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAvgCount() {
        return avgCount;
    }

    public void setAvgCount(Double avgCount) {
        this.avgCount = avgCount;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricalData that = (HistoricalData) o;
        return Objects.equals(timestamp, that.timestamp) && Objects.equals(avgCount, that.avgCount) && Objects.equals(maxCount, that.maxCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, avgCount, maxCount);
    }

    @Override
    public String toString() {
        return "HistoricalData{" +
                "timestamp=" + timestamp +
                ", avgCount=" + avgCount +
                ", maxCount=" + maxCount +
                '}';
    }
}
