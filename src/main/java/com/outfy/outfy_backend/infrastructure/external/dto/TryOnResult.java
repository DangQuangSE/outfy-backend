package com.outfy.outfy_backend.infrastructure.external.dto;

import java.util.Map;

public class TryOnResult {

    private String previewUrl;
    private Double fitScore;
    private String note;
    private Map<String, Object> appliedParams;

    // Getters and Setters
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public Double getFitScore() { return fitScore; }
    public void setFitScore(Double fitScore) { this.fitScore = fitScore; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Map<String, Object> getAppliedParams() { return appliedParams; }
    public void setAppliedParams(Map<String, Object> appliedParams) { this.appliedParams = appliedParams; }
}

