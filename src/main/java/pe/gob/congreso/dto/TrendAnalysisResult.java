package pe.gob.congreso.dto;

import java.util.List;

public class TrendAnalysisResult {

    private String analysis;
    private List<String> keyInsights;
    private String comparison;
    private String recommendation;
    private double confidenceScore;

    public TrendAnalysisResult() {
    }

    public TrendAnalysisResult(String analysis, List<String> keyInsights, String comparison, String recommendation, double confidenceScore) {
        this.analysis = analysis;
        this.keyInsights = keyInsights;
        this.comparison = comparison;
        this.recommendation = recommendation;
        this.confidenceScore = confidenceScore;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public List<String> getKeyInsights() {
        return keyInsights;
    }

    public void setKeyInsights(List<String> keyInsights) {
        this.keyInsights = keyInsights;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
