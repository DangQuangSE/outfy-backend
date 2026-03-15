package com.outfy.outfy_backend.modules.recommendation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class CreateRecommendationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long bodyProfileId;
    private String occasion;
    private Map<String, Object> preferences;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public Map<String, Object> getPreferences() { return preferences; }
    public void setPreferences(Map<String, Object> preferences) { this.preferences = preferences; }
}

