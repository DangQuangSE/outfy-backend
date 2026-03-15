package com.outfy.outfy_backend.modules.tryon.dto.response;

import java.util.Map;

public class TryOnResult {
    private String sessionId;
    private String status;
    private String previewUrl;
    private Double fitScore;
    private String note;
    private Map<String, Object> appliedParams;

    public TryOnResult() {}

    public TryOnResult(String sessionId, String status, String previewUrl, 
                     Double fitScore, String note, Map<String, Object> appliedParams) {
        this.sessionId = sessionId;
        this.status = status;
        this.previewUrl = previewUrl;
        this.fitScore = fitScore;
        this.note = note;
        this.appliedParams = appliedParams;
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public Double getFitScore() { return fitScore; }
    public void setFitScore(Double fitScore) { this.fitScore = fitScore; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Map<String, Object> getAppliedParams() { return appliedParams; }
    public void setAppliedParams(Map<String, Object> appliedParams) { this.appliedParams = appliedParams; }
}

