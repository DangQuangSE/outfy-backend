package com.outfy.outfy_backend.modules.tryon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tryon_results")
public class TryOnResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tryon_session_id", nullable = false)
    private Long tryonSessionId;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "fit_score")
    private Double fitScore;

    @Column(name = "result_note")
    private String resultNote;

    @Column(name = "applied_params_json", columnDefinition = "TEXT")
    private String appliedParamsJson;

    @Column(name = "avatar_id")
    private String avatarId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "garment_category")
    private String garmentCategory;

    // Multiple garment categories (store as JSON)
    @Column(name = "garment_categories", columnDefinition = "TEXT")
    private String garmentCategoriesJson;

    @Column(name = "garment_color")
    private String garmentColor;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isFavorite == null) {
            isFavorite = false;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTryonSessionId() { return tryonSessionId; }
    public void setTryonSessionId(Long tryonSessionId) { this.tryonSessionId = tryonSessionId; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public Double getFitScore() { return fitScore; }
    public void setFitScore(Double fitScore) { this.fitScore = fitScore; }
    public String getResultNote() { return resultNote; }
    public void setResultNote(String resultNote) { this.resultNote = resultNote; }
    public String getAppliedParamsJson() { return appliedParamsJson; }
    public void setAppliedParamsJson(String appliedParamsJson) { this.appliedParamsJson = appliedParamsJson; }
    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getGarmentCategory() { return garmentCategory; }
    public void setGarmentCategory(String garmentCategory) { this.garmentCategory = garmentCategory; }
    public String getGarmentCategoriesJson() { return garmentCategoriesJson; }
    public void setGarmentCategoriesJson(String garmentCategoriesJson) { this.garmentCategoriesJson = garmentCategoriesJson; }
    public String getGarmentColor() { return garmentColor; }
    public void setGarmentColor(String garmentColor) { this.garmentColor = garmentColor; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

