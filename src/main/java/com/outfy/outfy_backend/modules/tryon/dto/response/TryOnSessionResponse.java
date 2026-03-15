package com.outfy.outfy_backend.modules.tryon.dto.response;

import java.time.LocalDateTime;

public class TryOnSessionResponse {
    private Long id;
    private Long userId;
    private Long bodyProfileId;
    private Long clothingItemId;
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRequestedSize() { return requestedSize; }
    public void setRequestedSize(String requestedSize) { this.requestedSize = requestedSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

