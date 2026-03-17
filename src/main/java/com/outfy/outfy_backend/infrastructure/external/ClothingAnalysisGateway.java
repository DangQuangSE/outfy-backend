package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;

public interface ClothingAnalysisGateway {

    /**
     * Analyze clothing item from database
     */
    ClothingAnalysisResult analyze(Long clothingItemId);

    /**
     * Analyze clothing directly from image URL (for demo without database)
     */
    ClothingAnalysisResult analyzeFromImage(String imageUrl, String fileName);

    /**
     * Analyze clothing directly from image URL with explicit garment category
     * @param imageUrl URL of the clothing image
     * @param fileName Original filename (for fallback detection)
     * @param garmentCategory Explicit garment category (e.g., TSHIRT, HOODIE, PANTS)
     * @return ClothingAnalysisResult with category-specific template
     */
    ClothingAnalysisResult analyzeFromImageWithCategory(String imageUrl, String fileName, String garmentCategory);
}

