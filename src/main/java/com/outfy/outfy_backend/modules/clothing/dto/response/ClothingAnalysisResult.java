package com.outfy.outfy_backend.modules.clothing.dto.response;

import java.util.Map;

public class ClothingAnalysisResult {
    private String garmentCategory;
    private String templateCode;
    private Map<String, Object> attributes;
    private Map<String, Object> garmentParameters;
    private String previewUrl;

    public ClothingAnalysisResult() {}

    public ClothingAnalysisResult(String garmentCategory, String templateCode, 
                                 Map<String, Object> attributes, 
                                 Map<String, Object> garmentParameters,
                                 String previewUrl) {
        this.garmentCategory = garmentCategory;
        this.templateCode = templateCode;
        this.attributes = attributes;
        this.garmentParameters = garmentParameters;
        this.previewUrl = previewUrl;
    }

    // Getters and Setters
    public String getGarmentCategory() { return garmentCategory; }
    public void setGarmentCategory(String garmentCategory) { this.garmentCategory = garmentCategory; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public Map<String, Object> getGarmentParameters() { return garmentParameters; }
    public void setGarmentParameters(Map<String, Object> garmentParameters) { this.garmentParameters = garmentParameters; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
}

