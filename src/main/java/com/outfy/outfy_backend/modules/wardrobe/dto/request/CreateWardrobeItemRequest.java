package com.outfy.outfy_backend.modules.wardrobe.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateWardrobeItemRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long clothingItemId;
    private String category;
    private String season;
    private String color;
    private String notes;

    // Getters and Setters
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
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

