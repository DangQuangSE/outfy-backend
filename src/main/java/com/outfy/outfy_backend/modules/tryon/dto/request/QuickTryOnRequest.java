package com.outfy.outfy_backend.modules.tryon.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for quick try-on with 1-2 wardrobe items and body type
 * Can use either bodyProfileId OR bodyType + gender directly
 */
public class QuickTryOnRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    // Option 1: Use existing body profile
    private Long bodyProfileId;

    // Option 2: Pass body type directly (alternative to bodyProfileId)
    private String bodyType;  // e.g., "slim", "regular", "broad", "curvy"
    private String gender;     // e.g., "male", "female"

    @NotEmpty(message = "At least one wardrobe item ID is required")
    @Size(max = 2, message = "Maximum 2 wardrobe items allowed for try-on")
    private List<Long> wardrobeItemIds;

    // Optional parameters
    private String size;
    private String fitType;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public List<Long> getWardrobeItemIds() { return wardrobeItemIds; }
    public void setWardrobeItemIds(List<Long> wardrobeItemIds) { this.wardrobeItemIds = wardrobeItemIds; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }
}

