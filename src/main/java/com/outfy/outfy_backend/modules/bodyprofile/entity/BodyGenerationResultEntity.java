package com.outfy.outfy_backend.modules.bodyprofile.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_generation_results")
public class BodyGenerationResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "body_profile_id", nullable = false)
    private Long bodyProfileId;

    @Column(name = "body_type")
    private String bodyType;

    @Column(name = "avatar_preset_code")
    private String avatarPresetCode;

    @Column(name = "shape_params_json", columnDefinition = "TEXT")
    private String shapeParamsJson;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "model_url")
    private String modelUrl;

    @Column
    private Double confidence;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public String getAvatarPresetCode() { return avatarPresetCode; }
    public void setAvatarPresetCode(String avatarPresetCode) { this.avatarPresetCode = avatarPresetCode; }
    public String getShapeParamsJson() { return shapeParamsJson; }
    public void setShapeParamsJson(String shapeParamsJson) { this.shapeParamsJson = shapeParamsJson; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

