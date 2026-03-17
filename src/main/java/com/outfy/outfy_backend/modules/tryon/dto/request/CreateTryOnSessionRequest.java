package com.outfy.outfy_backend.modules.tryon.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateTryOnSessionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Body profile ID is required")
    private Long bodyProfileId;

    @NotEmpty(message = "At least one clothing item ID is required")
    private List<Long> clothingItemIds;

    private String size;
    private String fitType;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public List<Long> getClothingItemIds() { return clothingItemIds; }
    public void setClothingItemIds(List<Long> clothingItemIds) { this.clothingItemIds = clothingItemIds; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }
}

