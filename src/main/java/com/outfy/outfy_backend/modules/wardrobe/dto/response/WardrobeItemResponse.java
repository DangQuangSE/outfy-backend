package com.outfy.outfy_backend.modules.wardrobe.dto.response;

import java.time.LocalDateTime;

public class WardrobeItemResponse {
    private Long id;
    private Long userId;
    private Long clothingItemId;
    private String category;
    private String season;
    private String color;
    private Boolean isFavorite;
    private String notes;
    private String imageUrl;

    // Model 3D fields - from ClothingItem
    private String templateCode;
    private String modelUrl;
    private String previewUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getClothingItemId() { return clothingItemId; }
    public void setClothingItemId(Long clothingItemId) { this.clothingItemId = clothingItemId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

