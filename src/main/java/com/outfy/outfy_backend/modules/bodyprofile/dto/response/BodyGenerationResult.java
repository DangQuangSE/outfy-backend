package com.outfy.outfy_backend.modules.bodyprofile.dto.response;

import java.util.Map;

public class BodyGenerationResult {
    private String bodyType;
    private String avatarPresetCode;
    private Map<String, Double> shapeParams;
    private String previewUrl;
    private String modelUrl;  // NEW: URL to GLB file
    private Double confidence;

    public BodyGenerationResult() {}

    public BodyGenerationResult(String bodyType, String avatarPresetCode,
                                Map<String, Double> shapeParams, String previewUrl,
                                String modelUrl, Double confidence) {
        this.bodyType = bodyType;
        this.avatarPresetCode = avatarPresetCode;
        this.shapeParams = shapeParams;
        this.previewUrl = previewUrl;
        this.modelUrl = modelUrl;
        this.confidence = confidence;
    }

    // Getters and Setters
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public String getAvatarPresetCode() { return avatarPresetCode; }
    public void setAvatarPresetCode(String avatarPresetCode) { this.avatarPresetCode = avatarPresetCode; }
    public Map<String, Double> getShapeParams() { return shapeParams; }
    public void setShapeParams(Map<String, Double> shapeParams) { this.shapeParams = shapeParams; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}

