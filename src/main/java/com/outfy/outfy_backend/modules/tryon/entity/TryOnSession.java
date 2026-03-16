package com.outfy.outfy_backend.modules.tryon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tryon_sessions")
public class TryOnSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "body_profile_id")
    private Long bodyProfileId;

    @Column(name = "clothing_item_id")
    private Long clothingItemId;

    @Column(name = "wardrobe_item_id")
    private Long wardrobeItemId;

    @Column(name = "avatar_id")
    private String avatarId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "avatar_model_url")
    private String avatarModelUrl;

    @Column(name = "garment_model_url")
    private String garmentModelUrl;

    @Column(name = "garment_category")
    private String garmentCategory;

    @Column(name = "fit_type")
    private String fitType;

    @Column(name = "sleeve_length")
    private String sleeveLength;

    @Column(name = "garment_color")
    private String garmentColor;

    @Column
    private String status;

    @Column(name = "requested_size")
    private String requestedSize;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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
    public String getAvatarModelUrl() { return avatarModelUrl; }
    public void setAvatarModelUrl(String avatarModelUrl) { this.avatarModelUrl = avatarModelUrl; }
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

