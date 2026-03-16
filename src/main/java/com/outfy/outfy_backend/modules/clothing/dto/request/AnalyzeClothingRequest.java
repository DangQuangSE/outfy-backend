package com.outfy.outfy_backend.modules.clothing.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for direct clothing analysis (without database)
 */
public class AnalyzeClothingRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String fileName;

    // Constructors
    public AnalyzeClothingRequest() {}

    public AnalyzeClothingRequest(String imageUrl, String fileName) {
        this.imageUrl = imageUrl;
        this.fileName = fileName;
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

