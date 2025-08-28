package pe.gob.congreso.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class TrendSearchResult {
    private String rawName;
    private String displayName;
    private long totalMentions;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;

    public TrendSearchResult() {}

    public TrendSearchResult(String rawName, String displayName, long totalMentions,
                             LocalDateTime firstSeen, LocalDateTime lastSeen) {
        this.rawName = rawName;
        this.displayName = displayName;
        this.totalMentions = totalMentions;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
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

    public long getTotalMentions() {
        return totalMentions;
    }

    public void setTotalMentions(long totalMentions) {
        this.totalMentions = totalMentions;
    }

    public LocalDateTime getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(LocalDateTime firstSeen) {
        this.firstSeen = firstSeen;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrendSearchResult that = (TrendSearchResult) o;
        return totalMentions == that.totalMentions && Objects.equals(rawName, that.rawName) && Objects.equals(displayName, that.displayName) && Objects.equals(firstSeen, that.firstSeen) && Objects.equals(lastSeen, that.lastSeen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawName, displayName, totalMentions, firstSeen, lastSeen);
    }

    @Override
    public String toString() {
        return "TrendSearchResult{" +
                "rawName='" + rawName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", totalMentions=" + totalMentions +
                ", firstSeen=" + firstSeen +
                ", lastSeen=" + lastSeen +
                '}';
    }
}