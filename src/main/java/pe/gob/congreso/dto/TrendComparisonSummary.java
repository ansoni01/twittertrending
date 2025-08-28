package pe.gob.congreso.dto;

import java.time.LocalDate;
import java.util.Objects;

public class TrendComparisonSummary {

    private long totalMentions1;
    private long totalMentions2;
    private LocalDate peakDate1;
    private LocalDate peakDate2;
    private long peakValue1;
    private long peakValue2;
    private double averageGrowth1;
    private double averageGrowth2;
    private int activeDays1;
    private int activeDays2;

    public TrendComparisonSummary() {
    }

    public TrendComparisonSummary(long totalMentions1, long totalMentions2, LocalDate peakDate1, LocalDate peakDate2, long peakValue1, long peakValue2, double averageGrowth1, double averageGrowth2, int activeDays1, int activeDays2) {
        this.totalMentions1 = totalMentions1;
        this.totalMentions2 = totalMentions2;
        this.peakDate1 = peakDate1;
        this.peakDate2 = peakDate2;
        this.peakValue1 = peakValue1;
        this.peakValue2 = peakValue2;
        this.averageGrowth1 = averageGrowth1;
        this.averageGrowth2 = averageGrowth2;
        this.activeDays1 = activeDays1;
        this.activeDays2 = activeDays2;
    }

    public long getTotalMentions1() {
        return totalMentions1;
    }

    public void setTotalMentions1(long totalMentions1) {
        this.totalMentions1 = totalMentions1;
    }

    public long getTotalMentions2() {
        return totalMentions2;
    }

    public void setTotalMentions2(long totalMentions2) {
        this.totalMentions2 = totalMentions2;
    }

    public LocalDate getPeakDate1() {
        return peakDate1;
    }

    public void setPeakDate1(LocalDate peakDate1) {
        this.peakDate1 = peakDate1;
    }

    public LocalDate getPeakDate2() {
        return peakDate2;
    }

    public void setPeakDate2(LocalDate peakDate2) {
        this.peakDate2 = peakDate2;
    }

    public long getPeakValue1() {
        return peakValue1;
    }

    public void setPeakValue1(long peakValue1) {
        this.peakValue1 = peakValue1;
    }

    public long getPeakValue2() {
        return peakValue2;
    }

    public void setPeakValue2(long peakValue2) {
        this.peakValue2 = peakValue2;
    }

    public double getAverageGrowth1() {
        return averageGrowth1;
    }

    public void setAverageGrowth1(double averageGrowth1) {
        this.averageGrowth1 = averageGrowth1;
    }

    public double getAverageGrowth2() {
        return averageGrowth2;
    }

    public void setAverageGrowth2(double averageGrowth2) {
        this.averageGrowth2 = averageGrowth2;
    }

    public int getActiveDays1() {
        return activeDays1;
    }

    public void setActiveDays1(int activeDays1) {
        this.activeDays1 = activeDays1;
    }

    public int getActiveDays2() {
        return activeDays2;
    }

    public void setActiveDays2(int activeDays2) {
        this.activeDays2 = activeDays2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrendComparisonSummary that = (TrendComparisonSummary) o;
        return totalMentions1 == that.totalMentions1 && totalMentions2 == that.totalMentions2 && peakValue1 == that.peakValue1 && peakValue2 == that.peakValue2 && Double.compare(that.averageGrowth1, averageGrowth1) == 0 && Double.compare(that.averageGrowth2, averageGrowth2) == 0 && activeDays1 == that.activeDays1 && activeDays2 == that.activeDays2 && Objects.equals(peakDate1, that.peakDate1) && Objects.equals(peakDate2, that.peakDate2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalMentions1, totalMentions2, peakDate1, peakDate2, peakValue1, peakValue2, averageGrowth1, averageGrowth2, activeDays1, activeDays2);
    }

    @Override
    public String toString() {
        return "TrendComparisonSummary{" +
                "totalMentions1=" + totalMentions1 +
                ", totalMentions2=" + totalMentions2 +
                ", peakDate1=" + peakDate1 +
                ", peakDate2=" + peakDate2 +
                ", peakValue1=" + peakValue1 +
                ", peakValue2=" + peakValue2 +
                ", averageGrowth1=" + averageGrowth1 +
                ", averageGrowth2=" + averageGrowth2 +
                ", activeDays1=" + activeDays1 +
                ", activeDays2=" + activeDays2 +
                '}';
    }

}
