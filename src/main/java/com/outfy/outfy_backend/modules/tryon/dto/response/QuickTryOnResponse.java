package com.outfy.outfy_backend.modules.tryon.dto.response;

import java.util.List;

/**
 * Response DTO for quick try-on result - returns 3D model URL for 360 display
 */
public class QuickTryOnResponse {

    private String modelUrl;           // URL to GLB file for 3D viewer
    private String modelFileName;      // File name for reference
    private String bodyType;           // User's body type (e.g., "slim", "regular", "broad")
    private String gender;             // User's gender
    private List<String> clothingCategories;  // Categories of selected clothing
    private Double fitScore;          // Fit score based on body type match
    private String message;

    // Getters and Setters
    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public String getModelFileName() { return modelFileName; }
    public void setModelFileName(String modelFileName) { this.modelFileName = modelFileName; }
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public List<String> getClothingCategories() { return clothingCategories; }
    public void setClothingCategories(List<String> clothingCategories) { this.clothingCategories = clothingCategories; }
    public Double getFitScore() { return fitScore; }
    public void setFitScore(Double fitScore) { this.fitScore = fitScore; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

