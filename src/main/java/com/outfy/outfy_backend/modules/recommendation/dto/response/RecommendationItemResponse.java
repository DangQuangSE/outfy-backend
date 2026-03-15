package com.outfy.outfy_backend.modules.recommendation.dto.response;

public class RecommendationItemResponse {
    private Long clothingItemId;
    private Double matchScore;
    private String reason;
    private Integer rank;
    private String imageUrl;

    // Getters and Setters
    public Long getClothingItemId() { return clothingItemId; }
    public void setClothingItemId(Long clothingItemId) { this.clothingItemId = clothingItemId; }
    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

