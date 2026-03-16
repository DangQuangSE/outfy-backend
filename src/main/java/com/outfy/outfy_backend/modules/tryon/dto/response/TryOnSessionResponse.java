package com.outfy.outfy_backend.modules.tryon.dto.response;

import java.time.LocalDateTime;

public class TryOnSessionResponse {
    private Long id;
    private Long userId;
    private Long bodyProfileId;
    private Long clothingItemId;
    private Long wardrobeItemId;
    private String avatarId;
    private String avatarUrl;
    private String garmentModelUrl;
    private String garmentCategory;
    private String fitType;
    private String sleeveLength;
    private String garmentColor;
    private String status;
    private String requestedSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public Long getClothingItemId() { return clothingItemId; }
    public void setClothingItemId(Long clothingItemId) { this.clothingItemId = clothingItemId; }
    public Long getWardrobeItemId() { return wardrobeItemId; }
    public void setWardrobeItemId(Long wardrobeItemId) { this.wardrobeItemId = wardrobeItemId; }
    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getGarmentModelUrl() { return garmentModelUrl; }
    public void setGarmentModelUrl(String garmentModelUrl) { this.garmentModelUrl = garmentModelUrl; }
    public String getGarmentCategory() { return garmentCategory; }
    public void setGarmentCategory(String garmentCategory) { this.garmentCategory = garmentCategory; }
    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }
    public String getSleeveLength() { return sleeveLength; }
    public void setSleeveLength(String sleeveLength) { this.sleeveLength = sleeveLength; }
    public String getGarmentColor() { return garmentColor; }
    public void setGarmentColor(String garmentColor) { this.garmentColor = garmentColor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRequestedSize() { return requestedSize; }
    public void setRequestedSize(String requestedSize) { this.requestedSize = requestedSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

