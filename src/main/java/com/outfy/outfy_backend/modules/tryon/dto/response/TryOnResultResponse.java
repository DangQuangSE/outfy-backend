package com.outfy.outfy_backend.modules.tryon.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TryOnResultResponse {

    private Long id;
    private Long sessionId;
    private String previewUrl;
    private Double fitScore;
    private String note;
    private Map<String, Object> appliedParams;
    private String avatarId;
    private String avatarUrl;

    // Single item (legacy support)
    private String garmentCategory;
    private String garmentColor;

    // Multiple items (new)
    private List<String> garmentCategories;
    private List<String> garmentColors;

    private Boolean isFavorite;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public Double getFitScore() { return fitScore; }
    public void setFitScore(Double fitScore) { this.fitScore = fitScore; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Map<String, Object> getAppliedParams() { return appliedParams; }
    public void setAppliedParams(Map<String, Object> appliedParams) { this.appliedParams = appliedParams; }
    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getGarmentCategory() { return garmentCategory; }
    public void setGarmentCategory(String garmentCategory) { this.garmentCategory = garmentCategory; }
    public String getGarmentColor() { return garmentColor; }
    public void setGarmentColor(String garmentColor) { this.garmentColor = garmentColor; }
    public List<String> getGarmentCategories() { return garmentCategories; }
    public void setGarmentCategories(List<String> garmentCategories) { this.garmentCategories = garmentCategories; }
    public List<String> getGarmentColors() { return garmentColors; }
    public void setGarmentColors(List<String> garmentColors) { this.garmentColors = garmentColors; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

