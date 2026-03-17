package com.outfy.outfy_backend.modules.draft.entity;

import com.outfy.outfy_backend.modules.draft.enums.DraftStatus;
import com.outfy.outfy_backend.modules.draft.enums.DraftType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drafts")
public class Draft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "draft_type", nullable = false)
    private DraftType draftType;

    @Column(name = "source_item_id")
    private Long sourceItemId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "input_data_json", columnDefinition = "TEXT")
    private String inputDataJson;

    @Column(name = "analysis_result_json", columnDefinition = "TEXT")
    private String analysisResultJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DraftStatus status;

    @Column(name = "garment_category")
    private String garmentCategory;

    @Column(name = "template_code")
    private String templateCode;

    @Column(name = "model_url")
    private String modelUrl;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "color")
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = DraftStatus.DRAFT;
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
    public DraftType getDraftType() { return draftType; }
    public void setDraftType(DraftType draftType) { this.draftType = draftType; }
    public Long getSourceItemId() { return sourceItemId; }
    public void setSourceItemId(Long sourceItemId) { this.sourceItemId = sourceItemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getInputDataJson() { return inputDataJson; }
    public void setInputDataJson(String inputDataJson) { this.inputDataJson = inputDataJson; }
    public String getAnalysisResultJson() { return analysisResultJson; }
    public void setAnalysisResultJson(String analysisResultJson) { this.analysisResultJson = analysisResultJson; }
    public DraftStatus getStatus() { return status; }
    public void setStatus(DraftStatus status) { this.status = status; }
    public String getGarmentCategory() { return garmentCategory; }
    public void setGarmentCategory(String garmentCategory) { this.garmentCategory = garmentCategory; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

