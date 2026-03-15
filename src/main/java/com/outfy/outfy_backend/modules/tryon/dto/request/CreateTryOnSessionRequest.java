package com.outfy.outfy_backend.modules.tryon.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateTryOnSessionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Body profile ID is required")
    private Long bodyProfileId;

    @NotNull(message = "Clothing item ID is required")
    private Long clothingItemId;

    private String size;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public Long getClothingItemId() { return clothingItemId; }
    public void setClothingItemId(Long clothingItemId) { this.clothingItemId = clothingItemId; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}

