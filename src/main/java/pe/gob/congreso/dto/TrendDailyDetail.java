package pe.gob.congreso.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TrendDailyDetail {
    private LocalDate date;
    private String rawName;
    private String displayName;
    private Integer count;
    private LocalDateTime timestamp;
    private String trendId;
    private String tableName;

    public TrendDailyDetail() {
    }

    public TrendDailyDetail(LocalDate date, String rawName, String displayName, Integer count,
                            LocalDateTime timestamp, String trendId, String tableName) {
        this.date = date;
        this.rawName = rawName;
        this.displayName = displayName;
        this.count = count;
        this.timestamp = timestamp;
        this.trendId = trendId;
        this.tableName = tableName;
    }

    // Getters y Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrendDailyDetail that = (TrendDailyDetail) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(rawName, that.rawName) &&
                Objects.equals(displayName, that.displayName) &&
                Objects.equals(count, that.count) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(trendId, that.trendId) &&
                Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, rawName, displayName, count, timestamp, trendId, tableName);
    }

    // toString
    @Override
    public String toString() {
        return "TrendDailyDetail{" +
                "date=" + date +
                ", rawName='" + rawName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", count=" + count +
                ", timestamp=" + timestamp +
                ", trendId='" + trendId + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}