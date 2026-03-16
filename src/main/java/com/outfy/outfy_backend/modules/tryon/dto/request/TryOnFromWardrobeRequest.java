package com.outfy.outfy_backend.modules.tryon.dto.request;

import jakarta.validation.constraints.NotNull;

public class TryOnFromWardrobeRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Wardrobe item ID is required")
    private Long wardrobeItemId;

    @NotNull(message = "Avatar ID is required")
    private String avatarId;

    private String size;
    private String fitType;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getWardrobeItemId() { return wardrobeItemId; }
    public void setWardrobeItemId(Long wardrobeItemId) { this.wardrobeItemId = wardrobeItemId; }
    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }
}

