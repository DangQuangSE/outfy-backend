package com.outfy.outfy_backend.modules.recommendation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_results")
public class RecommendationResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recommendation_session_id", nullable = false)
    private Long recommendationSessionId;

    @Column(name = "clothing_item_id")
    private Long clothingItemId;

    @Column(name = "match_score")
    private Double matchScore;

    @Column(name = "reason")
    private String reason;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRecommendationSessionId() { return recommendationSessionId; }
    public void setRecommendationSessionId(Long recommendationSessionId) { this.recommendationSessionId = recommendationSessionId; }
    public Long getClothingItemId() { return clothingItemId; }
    public void setClothingItemId(Long clothingItemId) { this.clothingItemId = clothingItemId; }
    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

