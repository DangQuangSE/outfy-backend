package com.outfy.outfy_backend.modules.clothing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for direct clothing analysis (without database)
 */
public class AnalyzeClothingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String fileName;
    private String name;

    /**
     * Garment category - if provided, used directly for template mapping.
     * If not provided, will be detected from filename.
     * Examples: TSHIRT, HOODIE, SHIRT, PANTS, DRESS, etc.
     */
    private String garmentCategory;

    // Constructors
    public AnalyzeClothingRequest() {}

    public AnalyzeClothingRequest(Long userId, String imageUrl, String fileName) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGarmentCategory() { return garmentCategory; }
    public void setGarmentCategory(String garmentCategory) { this.garmentCategory = garmentCategory; }
}

