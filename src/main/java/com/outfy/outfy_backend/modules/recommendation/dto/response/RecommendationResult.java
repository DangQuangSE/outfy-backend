package com.outfy.outfy_backend.modules.recommendation.dto.response;

import java.util.List;
import java.util.Map;

public class RecommendationResult {
    private String outfitId;
    private List<String> items;
    private Map<String, Double> scores;
    private String explanation;

    public RecommendationResult() {}

    public RecommendationResult(String outfitId, List<String> items, 
                              Map<String, Double> scores, String explanation) {
        this.outfitId = outfitId;
        this.items = items;
        this.scores = scores;
        this.explanation = explanation;
    }

    // Getters and Setters
    public String getOutfitId() { return outfitId; }
    public void setOutfitId(String outfitId) { this.outfitId = outfitId; }
    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; }
    public Map<String, Double> getScores() { return scores; }
    public void setScores(Map<String, Double> scores) { this.scores = scores; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}

