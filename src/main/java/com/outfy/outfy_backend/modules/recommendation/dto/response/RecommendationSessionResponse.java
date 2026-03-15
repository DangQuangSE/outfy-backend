package com.outfy.outfy_backend.modules.recommendation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationSessionResponse {
    private Long id;
    private Long userId;
    private Long bodyProfileId;
    private String occasion;
    private String status;
    private LocalDateTime createdAt;
    private List<RecommendationItemResponse> items;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<RecommendationItemResponse> getItems() { return items; }
    public void setItems(List<RecommendationItemResponse> items) { this.items = items; }
}

