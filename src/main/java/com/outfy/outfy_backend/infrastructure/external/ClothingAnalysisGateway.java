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
}

